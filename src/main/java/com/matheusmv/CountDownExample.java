package com.matheusmv;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CountDownExample {
    private static final Logger log = Logger.getLogger(CountDownExample.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        exampleOne();
    }

    private static volatile int i = 0;
    private static CountDownLatch latch = new CountDownLatch(3);

    private static void exampleOne() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        executor.scheduleAtFixedRate(() -> {
            int j = new Random().nextInt(1000);
            int x = i * j;
            log.info("%d x %d = %d".formatted(i, j, x));
            latch.countDown();
        }, 1, 1, TimeUnit.SECONDS);

        latch.await();
        executor.shutdown();
    }
}
