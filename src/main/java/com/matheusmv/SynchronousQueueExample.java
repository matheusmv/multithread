package com.matheusmv;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynchronousQueueExample {
    private static final Logger log = Logger.getLogger(SynchronousQueueExample.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        exampleSynchronousQueue();
        exampleExchanger();
    }

    private static void exampleSynchronousQueue() throws InterruptedException {
        SynchronousQueue<String> messages = new SynchronousQueue<>();

        final int numberOfMessages = 50;
        CountDownLatch tasksProduced = new CountDownLatch(numberOfMessages);
        CountDownLatch tasksConsumed = new CountDownLatch(numberOfMessages);

        final int numberOfProducers = 2;
        ExecutorService producers = Executors.newFixedThreadPool(numberOfProducers);

        final int numberOfConsumers = 2;
        ExecutorService consumers = Executors.newFixedThreadPool(numberOfConsumers);

        for (int i = 0; i < numberOfProducers; i++) {
            producers.submit(() -> {
                String message = "message of %s";
                while (tasksProduced.getCount() > 0) {
                    try {
                        messages.put(message.formatted(Thread.currentThread().getName()));
                        tasksProduced.countDown();
                    } catch (InterruptedException e) {
                        log.log(Level.SEVERE, e, e::getMessage);
                    }
                }
                log.info("producers done");
            });
        }

        for (int i = 0; i < numberOfConsumers; i++) {
            consumers.submit(() -> {
                while (tasksConsumed.getCount() > 0) {
                    try {
                        log.info("thread " + Thread.currentThread().getName() + " read: " + messages.take());
                        tasksConsumed.countDown();
                    } catch (InterruptedException e) {
                        log.log(Level.SEVERE, e, e::getMessage);
                    }
                }
                log.info("consumers done");
            });
        }

        tasksProduced.await();
        tasksConsumed.await();

        producers.shutdown();
        consumers.shutdown();
    }

    private static void exampleExchanger() {
        Exchanger<String> messages = new Exchanger<>();

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(() -> {
            try {
                String message = "thread %s say hello";
                String messageReceived = messages.exchange(message.formatted(Thread.currentThread().getName()));
                log.info("message received: " + messageReceived);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, e, e::getMessage);
            }
        });

        executor.submit(() -> {
            try {
                String message = "thread %s say hi";
                String messageReceived = messages.exchange(message.formatted(Thread.currentThread().getName()));
                log.info("message received: " + messageReceived);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, e, e::getMessage);
            }
        });

        executor.shutdown();
    }
}
