package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class LogisticHub {

    private final int DEFAULT_HUB_CAPACITY = 1_500_000_000;
    private final int DEFAULT_TERMINALS_QUANTITY = 4;
    private final int DEFAULT_CURRENT_HUB_LOAD = 500_000_000;

    private static LogisticHub instance;
    private static AtomicBoolean isInstance = new AtomicBoolean(false);
    private static ReentrantLock lock = new ReentrantLock(true);

    private int hubWeightCapacity;
    private int currentHubLoad;
    private List<Terminal> terminals;
    private Queue<Truck> trucksQueue;

    private LogisticHub() {
        this.hubWeightCapacity = DEFAULT_HUB_CAPACITY;
        this.currentHubLoad = DEFAULT_CURRENT_HUB_LOAD;
        this.trucksQueue = new PriorityQueue<Truck>(Comparator.comparing(Truck::isPriorityPermission)
                .reversed()
                .thenComparing(Truck::getArrivalNumber));

        this.terminals = Stream.generate(Terminal::new)
                .limit(DEFAULT_TERMINALS_QUANTITY)
                .toList();
        startWork();
    }

    public static LogisticHub getInstance() {
        if (!isInstance.get()) {
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

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<Terminal> terminals) {
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

    public boolean takeArrivedTruck(Truck truck) {
        truck.setArrivalNumber(IdGenerator.getArrivalNumber());
        return trucksQueue.add(truck);
    }

    private void startWork(){
        boolean isWorkingHours=true;
        while (isWorkingHours){
            //TimeUnit.MINUTES.
        }
    }
}
