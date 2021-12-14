package by.epam.jwdmultithreading.entity;

import by.epam.jwdmultithreading.util.IdGenerator;

public class Truck implements Runnable {

    private final int id;
    private int arrivalNumber;
    private boolean isPriorityPermission;
    private int cargoWeight;

    public Truck(boolean isPriorityPermission, int cargoWeight) {
        this.id = IdGenerator.getTruckId();
        this.isPriorityPermission = isPriorityPermission;
        this.cargoWeight = cargoWeight;
    }

    public int getId() {
        return id;
    }

    public int getArrivalNumber() {
        return arrivalNumber;
    }

    public void setArrivalNumber(int number) {
        this.arrivalNumber = number;
    }

    public boolean isPriorityPermission() {
        return isPriorityPermission;
    }

    public void setPriorityPermission(boolean priorityPermission) {
        isPriorityPermission = priorityPermission;
    }

    public int getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(int cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    @Override
    public void run() {
        LogisticHub hub = LogisticHub.getInstance();
        hub.takeArrivedTruck(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Truck truck = (Truck) o;

        if (id != truck.id) return false;
        if (isPriorityPermission != truck.isPriorityPermission) return false;
        return cargoWeight == truck.cargoWeight;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (isPriorityPermission ? 1 : 0);
        result = 31 * result + cargoWeight;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Truck{");
        sb.append("id=").append(id);
        sb.append(", order=").append(arrivalNumber);
        sb.append(", isPriorityPermission=").append(isPriorityPermission);
        sb.append(", cargoWeight=").append(cargoWeight);
        sb.append('}');
        return sb.toString();
    }
}
