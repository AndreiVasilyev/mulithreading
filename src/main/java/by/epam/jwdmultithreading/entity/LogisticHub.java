package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LogisticHub {

    private static final Logger log = LogManager.getLogger();

    private final int DEFAULT_HUB_CAPACITY = 1_500_000_000;
    private final int DEFAULT_TERMINALS_QUANTITY = 4;
    private final int DEFAULT_CURRENT_HUB_LOAD = 500_000_000;

    private static LogisticHub instance;
    private static ReentrantLock lock = new ReentrantLock(true);
    private Condition turnQueueCondition;

    private int hubWeightCapacity;
    private int currentHubLoad;
    private Queue<Terminal> terminals;
    private Queue<Truck> trucksQueue;
    private Truck nextTruck;

    private LogisticHub() {
        this.hubWeightCapacity = DEFAULT_HUB_CAPACITY;
        this.currentHubLoad = DEFAULT_CURRENT_HUB_LOAD;
        this.trucksQueue = new PriorityQueue<Truck>(Comparator.comparing(Truck::isPriorityPermission)
                .reversed()
                .thenComparing(Truck::getArrivalNumber));
        this.terminals = Stream.generate(Terminal::new)
                .limit(DEFAULT_TERMINALS_QUANTITY)
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

    public static ReentrantLock getLock() {
        return lock;
    }

    public Condition getTurnQueueCondition() {
        return turnQueueCondition;
    }

    public int getHubWeightCapacity() {
        return hubWeightCapacity;
    }

    public void setHubWeightCapacity(int hubWeightCapacity) {
        this.hubWeightCapacity = hubWeightCapacity;
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

    public Queue<Truck> getTrucksQueue() {
        return trucksQueue;
    }

    public void setTrucksQueue(Queue<Truck> trucksQueue) {
        this.trucksQueue = trucksQueue;
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
