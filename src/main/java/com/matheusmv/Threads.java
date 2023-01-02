package com.matheusmv;

import java.util.logging.Logger;

public class Threads {
    private static final Logger log = Logger.getLogger(Threads.class.getSimpleName());

    public static void main(String[] args) {
        getCurrentThreadInfo();
        createNewThreadOne();
        createNewThreadTwo();
    }

    private static void getCurrentThreadInfo() {
        final String info = """
                    Name: %s, Priority: %d, Group: %s
                    """.formatted(
                Thread.currentThread().getName(),
                Thread.currentThread().getPriority(),
                Thread.currentThread().getThreadGroup().getName());

        log.info(info);
    }

    private static void createNewThreadOne() {
        class MyRunnable implements Runnable {
            @Override
            public void run() {
                log.info("MyRunnable class in " + Thread.currentThread());
            }
        }

        Thread threadOne = new Thread(new MyRunnable());
        threadOne.start();
    }

    private static void createNewThreadTwo() {
        Thread threadOne = new Thread(() -> {
            log.info("Runnable lambda in " + Thread.currentThread());
        });

        threadOne.start();
    }
}
