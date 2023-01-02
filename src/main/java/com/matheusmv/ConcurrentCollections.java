package com.matheusmv;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ConcurrentCollections {
    private static final Logger log = Logger.getLogger(ConcurrentCollections.class.getSimpleName());

    public static void main(String[] args) throws InterruptedException {
        exampleWithSynchronizedCollections();
        exampleWithCopyOnWriteArrayList();
        exampleWithAtomicClasses();
    }

    private static final List<String> messagesOne = Collections.synchronizedList(new ArrayList<>());

    private static class MyRunnableOne implements Runnable {
        @Override
        public void run() {
            messagesOne.add("message from " + Thread.currentThread().getName());
        }
    }

    private static void exampleWithSynchronizedCollections() throws InterruptedException {
        Runnable runnable = new MyRunnableOne();

        Thread threadOne = new Thread(runnable);
        Thread threadTwo = new Thread(runnable);
        Thread threadThree = new Thread(runnable);

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        Thread.sleep(Duration.ofSeconds(1).toMillis());

        messagesOne.forEach(log::info);
    }

    /*
     * Thread safe collections:
     *
     * - private static final List<String> messagesTwo = new CopyOnWriteArrayList<>();
     *      - do not use in cases where 'writing' (.add, .remove) is very frequent.
     *      - good in 'read' cases (.get, .indexOf).
     *
     * - private static final Map<String, String> messagesThree = new ConcurrentHashMap<>();
     *
     * - private static final BlockingQueue<String> messagesThree = new LinkedBlockingQueue<>();
     *
     * - private static final BlockingDeque<String> messagesThree = new LinkedBlockingDeque<>();
     */
    private static final List<String> messagesTwo = new CopyOnWriteArrayList<>();

    private static class MyRunnableTwo implements Runnable {
        @Override
        public void run() {
            messagesTwo.add("message from " + Thread.currentThread().getName());
        }
    }

    private static void exampleWithCopyOnWriteArrayList() throws InterruptedException {
        Runnable runnable = new MyRunnableTwo();

        Thread threadOne = new Thread(runnable);
        Thread threadTwo = new Thread(runnable);
        Thread threadThree = new Thread(runnable);

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        Thread.sleep(Duration.ofSeconds(1).toMillis());

        messagesTwo.forEach(log::info);
    }

    private static final AtomicInteger sharedCounter = new AtomicInteger(-1);
    private static final AtomicBoolean sharedBoolean = new AtomicBoolean(false);
    private static final AtomicReference<Integer> sharedReference = new AtomicReference<>(-10);

    public static class MyRunnableThree implements Runnable {
        @Override
        public synchronized void run() {
            boolean isFalse = sharedBoolean.compareAndExchange(false, true);
            int value = sharedCounter.incrementAndGet();
            int result = sharedReference.updateAndGet(sharedInt -> sharedInt + 1);
            log.info("sharedCounter: %d, sharedBoolean: %b, sharedReference: %d thread: %s".formatted(
                    value, isFalse, result, Thread.currentThread().getName()));
        }
    }

    private static void exampleWithAtomicClasses() {
        Runnable runnable = new MyRunnableThree();

        Thread threadOne = new Thread(runnable);
        Thread threadTwo = new Thread(runnable);
        Thread threadThree = new Thread(runnable);

        threadOne.start();
        threadTwo.start();
        threadThree.start();
    }
}
