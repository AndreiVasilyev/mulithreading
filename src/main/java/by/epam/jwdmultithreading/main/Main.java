package by.epam.jwdmultithreading.main;

import by.epam.jwdmultithreading.entity.LogisticHub;
import by.epam.jwdmultithreading.entity.Truck;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        LogisticHub hub = LogisticHub.getInstance();
        Truck truck1 = new Truck(false, 10);
        truck1.run();

        Truck truck2 = new Truck(false, 20);
        truck2.run();

        Truck truck3 = new Truck(true, 30);
        truck3.run();

        Truck truck4 = new Truck(false, 40);
        truck4.run();

        Truck truck5 = new Truck(true, 50);
        truck5.run();

        Truck truck6 = new Truck(false, 60);
        truck6.run();
        hub.getTrucksQueue().add(null);



        Queue queue=hub.getTrucksQueue();
        System.out.println("\n"+queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
    }
}
