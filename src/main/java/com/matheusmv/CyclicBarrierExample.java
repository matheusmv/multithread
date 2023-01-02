package com.matheusmv;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

// Batch process, Task control
public class CyclicBarrierExample {
    private static final Logger log = Logger.getLogger(CyclicBarrierExample.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
//        cyclicBarrierExampleOne();
        cyclicBarrierExampleTwo();
    }

    private static void await(CyclicBarrier cyclicBarrier) {
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        }
    }

    private static void cyclicBarrierExampleOne() {
        // (423 * 3) + (3 ^ 14) + (45 * 127 / 12)

        BlockingQueue<Integer> results = new ArrayBlockingQueue<>(3);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            int sum = 0;
            while (!results.isEmpty()) {
                sum += results.poll();
            }
            log.info("result: " + sum);
        });

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable callableOne = () -> {
            int result = 423 * 3;
            results.add(result);
            await(cyclicBarrier);
        };

        Runnable callableTwo = () -> {
            int result = (int) Math.pow(3, 14);
            results.add(result);
            await(cyclicBarrier);
        };

        Runnable callableThree = () -> {
            int result = 45 * 127 / 12;
            results.add(result);
            await(cyclicBarrier);
        };

        executor.submit(callableOne);
        executor.submit(callableTwo);
        executor.submit(callableThree);

        executor.shutdown();
    }

    private static void cyclicBarrierExampleTwo() throws InterruptedException {
        // (423 * 3) + (3 ^ 14) + (45 * 127 / 12)

        AtomicLong sum = new AtomicLong();
        BlockingQueue<Integer> results = new ArrayBlockingQueue<>(3);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            while (!results.isEmpty()) {
                sum.addAndGet(results.poll());
            }
            log.info("result: " + sum);
        });

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable callableOne = () -> {
            while (true) {
                int result = 423 * 3;
                results.add(result);
                await(cyclicBarrier);
                sleep();
            }
        };

        Runnable callableTwo = () -> {
            while (true) {
                int result = (int) Math.pow(3, 14);
                results.add(result);
                await(cyclicBarrier);
                sleep();
            }
        };

        Runnable callableThree = () -> {
            while (true) {
                int result = 45 * 127 / 12;
                results.add(result);
                await(cyclicBarrier);
                sleep();
            }
        };

        executor.submit(callableOne);
        executor.submit(callableTwo);
        executor.submit(callableThree);

        executor.shutdown();
    }

    private static void sleep() {
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {}
    }
}
