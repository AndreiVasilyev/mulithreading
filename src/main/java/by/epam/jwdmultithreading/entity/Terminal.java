package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


public class Terminal {

    private static final Logger log = LogManager.getLogger();
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
        try {
            //load and unload truck
            //and change cargo characteristics

            TimeUnit.SECONDS.sleep(random.nextInt(11));
        } catch (InterruptedException e) {
            log.error("Interrupted Exception in {}", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
        log.info("Truck #{}(arrivalNumber {}) finished handle in terminal #{}", truck.getId(), truck.getArrivalNumber(), id);
        LogisticHub hub=LogisticHub.getInstance();
        Lock lock = LogisticHub.getLock();
        try {
            lock.lock();
            hub.addTerminal(this);
            hub.getTurnQueueCondition().signalAll();
        } finally {
            lock.unlock();
        }
    }
}
