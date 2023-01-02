package com.matheusmv;

import java.util.logging.Logger;

public class VolatileAndYield {
    private static final Logger log = Logger.getLogger(VolatileAndYield.class.getSimpleName());

    public static void main(String[] args) {
        Volatile.runExample();
    }

    public static class Volatile {
        private static volatile int number = 0;
        private static volatile boolean done = false;

        private static class MyRunnable implements Runnable {
            @Override
            public void run() {
                while (!done) {
                    Thread.yield();
                }

                if (number != 42) {
                    throw new IllegalStateException("number != 42");
                }
            }
        }

        public static void runExample() {
            while (true) {
                Thread threadZero = new Thread(new MyRunnable());
                Thread threadOne = new Thread(new MyRunnable());
                Thread threadTwo = new Thread(new MyRunnable());

                threadZero.start();
                threadOne.start();
                threadTwo.start();

                number = 42;
                done = true;

                while (
                    threadZero.getState() != Thread.State.TERMINATED
                    || threadOne.getState() != Thread.State.TERMINATED
                    || threadTwo.getState() != Thread.State.TERMINATED
                ) {}

                number = 0;
                done = false;
            }
        }
    }
}
