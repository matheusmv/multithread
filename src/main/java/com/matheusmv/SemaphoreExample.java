package com.matheusmv;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SemaphoreExample {
    private static final Logger log = Logger.getLogger(SemaphoreExample.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
//        exampleOne();
        exampleTwo();
    }

    private static final Semaphore SEMAPHORE = new Semaphore(3);

    private static void exampleOne() {
        ExecutorService executor = Executors.newCachedThreadPool();

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            int id = new Random().nextInt(1000);

            acquire();

            log.info("Thread: %s get id -> %d".formatted(name, id));

            sleep();

            SEMAPHORE.release();
        };

        for (int i = 0; i < 50; i++) {
            executor.execute(task);
        }

        executor.shutdown();
    }

    private static void exampleTwo() throws InterruptedException {
        final int tasks = 10;
        CountDownLatch remainingTasks = new CountDownLatch(tasks);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(tasks + 1);

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            int id = new Random().nextInt(1000);

            boolean ok  = false;
            while (!ok) {
                ok = tryAcquire();
            }

            log.info("Thread: %s get id -> %d".formatted(name, id));

            sleep();
            remainingTasks.countDown();

            SEMAPHORE.release();
        };

        Runnable displayRemaining = () -> {
            log.info("remaining tasks: " + remainingTasks.getCount());
        };

        for (int i = 0; i < tasks; i++) {
            executor.execute(task);
        }

        executor.scheduleWithFixedDelay(displayRemaining, 0, 100, TimeUnit.MILLISECONDS);
        remainingTasks.await();
        executor.shutdown();
    }

    private static boolean tryAcquire() {
        try {
            return SEMAPHORE.tryAcquire(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
            return false;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(new Random().nextInt(6) * 1000);
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        }
    }

    private static void acquire() {
        try {
            SEMAPHORE.acquire();
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        }
    }
}
