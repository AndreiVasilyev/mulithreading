package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Terminal {

    private static final Logger log = LogManager.getLogger();
    private final double HUB_OPTIMIZATION_FACTOR = 0.5;
    private final int HUB_OPTIMIZATION_TIME = 80;
    private final int EMPTY_TRUCK = 0;
    private final int HANDLE_TIME_RANDOM_BOUND = 100;
    private final int LOADING_CARGO_WEIGHT_RANDOM_BOUND = 100;
    private final int id;
    private final Random random;

    public Terminal() {
        this.id = IdGenerator.getTerminalId();
        this.random = new Random();
    }

    public int getId() {
        return id;
    }

    public void handleTruck(Truck truck) {
        log.info("Truck #{}(arrivalNumber {}) start handle in terminal #{}", truck.getId(), truck.getArrivalNumber(), id);
        LogisticHub hub = LogisticHub.getInstance();
        if (truck.getCargoWeight() != 0) {
            unloadTruck(truck, hub);
        }
        loadTruck(truck, hub);
        try {
            LogisticHub.lock.lock();
            hub.addTerminal(this);
            hub.getTurnQueueCondition().signalAll();
            log.info("Truck #{}(arrivalNumber {}) finished handle in terminal #{}", truck.getId(), truck.getArrivalNumber(), id);
        } finally {
            LogisticHub.lock.unlock();
        }
    }

    private void loadTruck(Truck truck, LogisticHub hub) {
        int loadingCargoWeight = random.nextInt(LOADING_CARGO_WEIGHT_RANDOM_BOUND);
        int loadTime = random.nextInt(HANDLE_TIME_RANDOM_BOUND) * loadingCargoWeight;
        log.info("Truck #{}(arrivalNumber {}) started loading...", truck.getId(), truck.getArrivalNumber());
        try {
            LogisticHub.lock.lock();
            int newCurrentHubLoad = hub.getCurrentHubLoad() - loadingCargoWeight;
            if (newCurrentHubLoad < 0) {
                hub.setCurrentHubLoad((int) (hub.getHubWeightCapacity() * HUB_OPTIMIZATION_FACTOR));
                loadTime *= HUB_OPTIMIZATION_TIME;
            }
            hub.setCurrentHubLoad(hub.getCurrentHubLoad() - loadingCargoWeight);
            truck.setCargoWeight(loadingCargoWeight);
        } finally {
            LogisticHub.lock.unlock();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(loadTime);
            log.info("Truck #{}(arrivalNumber {}) loaded", truck.getId(), truck.getArrivalNumber());
        } catch (InterruptedException e) {
            log.error("Interrupted Exception in {}", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }

    private void unloadTruck(Truck truck, LogisticHub hub) {
        int unloadTime = random.nextInt(HANDLE_TIME_RANDOM_BOUND);
        try {
            log.info("Truck #{}(arrivalNumber {}) started unloading...", truck.getId(), truck.getArrivalNumber());
            LogisticHub.lock.lock();
            int newCurrentHubLoad = truck.getCargoWeight() + hub.getCurrentHubLoad();
            if (newCurrentHubLoad > hub.getHubWeightCapacity()) {
                hub.setCurrentHubLoad((int) (hub.getCurrentHubLoad() * HUB_OPTIMIZATION_FACTOR));
                unloadTime *= HUB_OPTIMIZATION_TIME;
            }
            hub.setCurrentHubLoad(truck.getCargoWeight() + hub.getCurrentHubLoad());
            truck.setCargoWeight(EMPTY_TRUCK);
        } finally {
            LogisticHub.lock.unlock();
        }
        try {
            unloadTime *= truck.getCargoWeight();
            TimeUnit.MILLISECONDS.sleep(unloadTime);
            log.info("Truck #{}(arrivalNumber {}) unloaded", truck.getId(), truck.getArrivalNumber());
        } catch (InterruptedException e) {
            log.error("Interrupted Exception in {}", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }
}
