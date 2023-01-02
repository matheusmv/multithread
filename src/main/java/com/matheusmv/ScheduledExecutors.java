package com.matheusmv;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ScheduledExecutors {
    private static final Logger log = Logger.getLogger(ScheduledExecutors.class.getSimpleName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        exampleSchedule();
        exampleFixedRate();
        exampleFixedDelay();
    }

    private static class Work implements Callable<String> {
        private static Random random = new Random();

        @Override
        public String call() throws Exception {
            int taskId = random.nextInt(100);
            return "executing task %d on thread: %s".formatted(
                    taskId, Thread.currentThread().getName());
        }
    }

    private static void exampleSchedule() throws ExecutionException, InterruptedException {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Instant start = Instant.now();
        ScheduledFuture<String> future = executor.schedule(new Work(), 3, TimeUnit.SECONDS);
        log.info(future.get());
        Instant end = Instant.now();

        log.info("operation take: %d seconds".formatted(Duration.between(start, end)));

        executor.shutdown();
    }

    private record Task(CountDownLatch done) implements Runnable {
        @Override
        public void run() {
            log.info("executing task on thread: " + Thread.currentThread().getName());
            done.countDown();
        }
    }

    private static void exampleFixedRate() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        CountDownLatch count = new CountDownLatch(10);

        executor.scheduleAtFixedRate(new Task(count), 0, 1, TimeUnit.SECONDS);

        count.await();
        executor.shutdown();
    }

    private static void exampleFixedDelay() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        CountDownLatch count = new CountDownLatch(10);

        executor.scheduleWithFixedDelay(new Task(count), 0, 2, TimeUnit.SECONDS);

        count.await();
        executor.shutdown();
    }
}
