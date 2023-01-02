package com.matheusmv;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ExecutorsExamples {
    private static final Logger log = Logger.getLogger(ExecutorsExamples.class.getSimpleName());

    public static void main(String[] args) {
        exampleSingleThreadExecutor();
        exampleSingleThreadExecutorWithReturn();
        exampleMultiThreadExecutor();
        exampleThreadExecutorInvokeAll();
        exampleThreadExecutorInvokeAny();
    }

    private static class Task implements Runnable {
        @Override
        public void run() {
            log.info("executing task on thread: " + Thread.currentThread().getName());
        }
    }

    private static class Work implements Callable<String> {
        @Override
        public String call() throws Exception {
            int taskId = new Random().nextInt(100);
            return "executing task %d on thread: %s".formatted(
                    taskId, Thread.currentThread().getName());
        }
    }

    private static void exampleSingleThreadExecutor() {
        ExecutorService executor = null;

        try {
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new Task());
            executor.execute(new Task());
            executor.execute(new Task());
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        } finally {
            if (Objects.nonNull(executor)) {
                executor.shutdown();
            }
        }
    }

    private static void exampleSingleThreadExecutorWithReturn() {
        ExecutorService executor = null;

        try {
            executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> "done by " + Thread.currentThread().getName());
            Future<String> future = executor.submit(new Work());

            log.info("done: " + future.isDone());

            log.info("result: " + future.get(1, TimeUnit.SECONDS));

            log.info("done: " + future.isDone());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        } finally {
            if (Objects.nonNull(executor)) {
                executor.shutdown();
            }
        }
    }

    private static void exampleMultiThreadExecutor() {
        ExecutorService executor = null;

        try {
            executor = Executors.newFixedThreadPool(4);
            // executor = Executors.newCachedThreadPool(); // on demand

            Future<String> futureOne = executor.submit(new Work());
            Future<String> futureTwo = executor.submit(new Work());
            Future<String> futureThree = executor.submit(new Work());

            log.info("result: " + futureOne.get());
            log.info("result: " + futureTwo.get());
            log.info("result: " + futureThree.get());

        } catch (InterruptedException | ExecutionException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        } finally {
            if (Objects.nonNull(executor)) {
                executor.shutdown();
            }
        }
    }

    private static void exampleThreadExecutorInvokeAll() {
        ExecutorService executor = null;

        try {
            executor = Executors.newCachedThreadPool();

            final int maxTasks = 10;
            List<Work> tasks = IntStream.range(0, maxTasks)
                    .mapToObj(__ -> new Work())
                    .toList();

            List<Future<String>> futures = executor.invokeAll(tasks);

            for (Future<String> future : futures) {
                log.info("result: " + future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        } finally {
            if (Objects.nonNull(executor)) {
                executor.shutdown();
            }
        }
    }

    private static void exampleThreadExecutorInvokeAny() {
        ExecutorService executor = null;

        try {
            executor = Executors.newCachedThreadPool();

            final int maxTasks = 10;
            List<Work> tasks = IntStream.range(0, maxTasks)
                    .mapToObj(__ -> new Work())
                    .toList();

            String firstResult = executor.invokeAny(tasks);

            log.info("result: " + firstResult);
        } catch (InterruptedException | ExecutionException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        } finally {
            if (Objects.nonNull(executor)) {
                executor.shutdown();
            }
        }
    }
}
