package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LogisticHub {

    private static final Logger log = LogManager.getLogger();

    private final int DEFAULT_HUB_CAPACITY = 1_500_000_000;
    private final int DEFAULT_TERMINALS_QUANTITY = 4;
    private final int DEFAULT_CURRENT_HUB_LOAD = 500_000_000;
    private final String RESOURCES_FILE_NAME = "hub";
    private final String CAPACITY_PROPERTY_NAME = "hub.capacity";
    private final String TERMINALS_QUANTITY_PROPERTY_NAME = "hub.terminalsQuantity";
    private final String CURRENT_HUB_LOAD_PROPERTY_NAME = "hub.currentHubLoad";

    private static LogisticHub instance;
    public static ReentrantLock lock = new ReentrantLock(true);
    private Condition turnQueueCondition;

    private int hubWeightCapacity;
    private int currentHubLoad;
    private int terminalsQuantity;
    private Queue<Terminal> terminals;
    private Queue<Truck> trucksQueue;
    private Truck nextTruck;

    private LogisticHub() {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCES_FILE_NAME);
        this.hubWeightCapacity = bundle.containsKey(CAPACITY_PROPERTY_NAME)
                ? Integer.parseInt(bundle.getString(CAPACITY_PROPERTY_NAME))
                : DEFAULT_HUB_CAPACITY;
        this.currentHubLoad = bundle.containsKey(CURRENT_HUB_LOAD_PROPERTY_NAME)
                ? Integer.parseInt(bundle.getString(CURRENT_HUB_LOAD_PROPERTY_NAME))
                : DEFAULT_CURRENT_HUB_LOAD;
        this.terminalsQuantity = bundle.containsKey(TERMINALS_QUANTITY_PROPERTY_NAME)
                ? Integer.parseInt(bundle.getString(TERMINALS_QUANTITY_PROPERTY_NAME))
                : DEFAULT_TERMINALS_QUANTITY;
        this.trucksQueue = new PriorityQueue<Truck>(Comparator.comparing(Truck::isPriorityPermission)
                .reversed()
                .thenComparing(Truck::getArrivalNumber));
        this.terminals = Stream.generate(Terminal::new)
                .limit(terminalsQuantity)
                .collect(Collectors.toCollection(() -> new LinkedList<>()));
        this.turnQueueCondition = lock.newCondition();
    }

    public static LogisticHub getInstance() {
        if (instance == null) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new LogisticHub();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Condition getTurnQueueCondition() {
        return turnQueueCondition;
    }

    public int getHubWeightCapacity() {
        return hubWeightCapacity;
    }

    public int getCurrentHubLoad() {
        return currentHubLoad;
    }

    public void setCurrentHubLoad(int currentHubLoad) {
        this.currentHubLoad = currentHubLoad;
    }

    public Queue<Terminal> getTerminals() {
        return terminals;
    }

    public void setTerminals(Queue<Terminal> terminals) {
        this.terminals = terminals;
    }

    public boolean addTerminal(Terminal terminal) {
        return terminals.add(terminal);
    }

    public Terminal getAvailableTerminal(Truck truck) {
        truck.setArrivalNumber(IdGenerator.getArrivalNumber());
        log.info("Truck #{} get arrival number #{}", truck.getId(), truck.getArrivalNumber());
        Terminal terminal = null;
        try {
            lock.lock();
            trucksQueue.add(truck);
            log.info("Truck #{} (arrival #{}) is queuing up to the terminal", truck.getId(), truck.getArrivalNumber());
            if (nextTruck == null) {
                nextTruck = trucksQueue.poll();
            }
            while (terminals.isEmpty() || nextTruck.getId() != truck.getId()) {
                turnQueueCondition.await();
            }
            nextTruck = trucksQueue.poll();
            terminal = terminals.poll();
            turnQueueCondition.signalAll();

        } catch (InterruptedException e) {
            log.error("Interrupted Exception in {}", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return terminal;
    }
}
