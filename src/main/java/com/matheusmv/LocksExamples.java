package com.matheusmv;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class LocksExamples {
    private static final Logger log = Logger.getLogger(LocksExamples.class.getSimpleName());

    private static int i = -1;

    public static void main(String[] args) {
        exampleOne();
        exampleTwo();
    }

    private static void exampleOne() {
        Lock lock = new ReentrantLock();
        ExecutorService executor = Executors.newCachedThreadPool();

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            lock.lock();
            i += 1;
            log.info("thread: %s increment i: %d".formatted(name, i));
            lock.unlock();
        };

        for (int i = 0; i < 6; i++) {
            executor.submit(task);
        }

        executor.shutdown();
    }

    private static void exampleTwo() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Runnable writeTask = () -> {
            String name = Thread.currentThread().getName();
            lock.writeLock().lock();
            i += 1;
            log.info("thread: %s write i".formatted(name));
            lock.writeLock().unlock();
        };

        Runnable readTask = () -> {
            String name = Thread.currentThread().getName();
            lock.readLock().lock();
            log.info("thread: %s read i: %d".formatted(name, i));
            lock.readLock().unlock();
        };

        for (int i = 0; i < 6; i++) {
            executor.submit(writeTask);
            executor.submit(readTask);
        }

        executor.shutdown();
    }
}
