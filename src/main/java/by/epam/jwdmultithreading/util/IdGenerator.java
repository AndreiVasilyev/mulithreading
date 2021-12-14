package by.epam.jwdmultithreading.util;

public class IdGenerator {

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
