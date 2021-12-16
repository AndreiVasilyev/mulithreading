package by.epam.jwdmultithreading.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdGenerator {

    private static final Logger log = LogManager.getLogger();
    private static int truckId;
    private static int terminalId;
    private static int arrivalNumber;

    private IdGenerator() {
    }

    public static int getTruckId() {
        return ++truckId;
    }

    public static int getTerminalId() {
        return ++terminalId;
    }

    public static int getArrivalNumber() {
        return ++arrivalNumber;
    }
}
