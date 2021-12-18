package by.epam.jwdmultithreading.main;

import by.epam.jwdmultithreading.entity.Truck;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new Thread(new Truck(false, 60)));
        executorService.execute(new Thread(new Truck(false, 20)));
        executorService.execute(new Thread(new Truck(true, 50)));
        executorService.execute(new Thread(new Truck(false, 40)));
        executorService.execute(new Thread(new Truck(true, 30)));
        executorService.execute(new Thread(new Truck(false, 70)));
        executorService.execute(new Thread(new Truck(true, 10)));
        executorService.execute(new Thread(new Truck(false, 30)));
        executorService.execute(new Thread(new Truck(true, 80)));
        executorService.execute(new Thread(new Truck(false, 20)));
        executorService.shutdown();

    }
}
