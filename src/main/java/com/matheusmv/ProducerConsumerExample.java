package com.matheusmv;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProducerConsumerExample {
    private static final Logger log = Logger.getLogger(ProducerConsumerExample.class.getSimpleName());

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        Runnable producer = () -> {
            try {
                int value = new Random().nextInt(1000);
                queue.put(value);
                log.info("producer add: %d in queue".formatted(value));
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, e, e::getMessage);
            }
        };

        Runnable consumer = () -> {
            longProcess();
            try {
                String name = Thread.currentThread().getName();
                int value = queue.take();
                log.info("consumer (%s) take: %d from queue".formatted(name, value));
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, e, e::getMessage);
            }
        };

        executor.scheduleWithFixedDelay(producer, 0, 1, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(consumer, 0, 1, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(consumer, 0, 1, TimeUnit.SECONDS);
    }

    private static void longProcess() {
        try {
            Thread.sleep(new Random().nextInt(6));
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        }
    }
}
