package com.matheusmv;

import java.util.logging.Logger;

public class ConcurrentThreads {
    private static final Logger log = Logger.getLogger(ConcurrentThreads.class.getSimpleName());

    public static void main(String[] args) {
        synchronizationExampleOne();
    }

    private static int sharedCounter = -1;

    public static class MyRunnableOne implements Runnable {
        @Override
        public synchronized void run() {
            sharedCounter += 1;
            log.info("sharedCounter: %d thread: %s".formatted(
                    sharedCounter, Thread.currentThread().getName()));
        }
    }

    public static class MyRunnableTwo implements Runnable {
        @Override
        public void run() {
            synchronized (this) {
                sharedCounter += 1;
                log.info("sharedCounter: %d thread: %s".formatted(
                        sharedCounter, Thread.currentThread().getName()));
            }
        }
    }

    public static class MyRunnableThree implements Runnable {
        private static final Object lockeOne = new Object();
        private static final Object lockeTwo = new Object();

        @Override
        public void run() {
            synchronized (lockeOne) {
                sharedCounter += 1;
                log.info("sharedCounter: %d thread: %s".formatted(
                        sharedCounter, Thread.currentThread().getName()));
            }

            synchronized (lockeTwo) {
                sharedCounter += 1;
                log.info("sharedCounter: %d thread: %s".formatted(
                        sharedCounter, Thread.currentThread().getName()));
            }
        }
    }

    private static void incrementCounterAndLog() {
        synchronized (ConcurrentThreads.class) {
            sharedCounter += 1;
            log.info("sharedCounter: %d thread: %s".formatted(
                    sharedCounter, Thread.currentThread().getName()));
        }
    }

    public static class MyRunnableFour implements Runnable {
        @Override
        public void run() {
            incrementCounterAndLog();
        }
    }

    public static class MyRunnableFive implements Runnable {
        @Override
        public void run() {
            int result = 0;

            synchronized (this) {
                sharedCounter += 1;
                result = sharedCounter * 2;
            }

            double resultRaisedToTen = Math.pow(result, 10);
            double rootOfResultRaisedToTen = Math.sqrt(resultRaisedToTen);
            log.info("rootOfResultRaisedToTen: " + rootOfResultRaisedToTen);
        }
    }

    private static void synchronizationExampleOne() {
        Runnable runnable = new MyRunnableFive();

        Thread threadOne = new Thread(runnable);
        Thread threadTwo = new Thread(runnable);
        Thread threadThree = new Thread(runnable);
        Thread threadFour = new Thread(runnable);
        Thread threadFive = new Thread(runnable);

        threadOne.start();
        threadTwo.start();
        threadThree.start();
        threadFour.start();
        threadFive.start();
    }
}
