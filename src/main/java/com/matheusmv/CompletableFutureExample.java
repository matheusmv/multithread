package com.matheusmv;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class CompletableFutureExample {
    private static final Logger log = Logger.getLogger(CompletableFutureExample.class.getSimpleName());

    public static void main(String[] args) {
        Instant start = Instant.now();

        final String resource = "https://localhost/content/";

        ExecutorService pool = Executors.newFixedThreadPool(5);

        CompletableFuture<List<URI>> urisFutureOne =
                CompletableFuture.supplyAsync(() -> {
                    log.info("future running in pool: " + Thread.currentThread().getName());
                    longProcess();
                    return IntStream.rangeClosed(1, 50)
                            .mapToObj(id -> URI.create(resource + id))
                            .toList();
                }, pool);

        CompletableFuture<List<URI>> urisFutureTwo =
                CompletableFuture.supplyAsync(() -> {
                    longProcess();
                    log.info("future running in pool: " + Thread.currentThread().getName());
                    return IntStream.rangeClosed(51, 100)
                            .mapToObj(id -> URI.create(resource + id))
                            .toList();
                }, pool);

        CompletableFuture<List<URI>> urisFutureThee =
                CompletableFuture.supplyAsync(() -> {
                    longProcess();
                    log.info("future running in pool: " + Thread.currentThread().getName());
                    return IntStream.rangeClosed(101, 150)
                            .mapToObj(id -> URI.create(resource + id))
                            .toList();
                }, pool);

        CompletableFuture<List<URI>> urisFutureFour =
                CompletableFuture.supplyAsync(() -> {
                    longProcess();
//                    throw new RuntimeException("custom error");
                    log.info("future running in pool: " + Thread.currentThread().getName());
                    return IntStream.rangeClosed(151, 200)
                            .mapToObj(id -> URI.create(resource + id))
                            .toList();
                }, pool);

//        List<URI> one = urisFutureOne.join();
//        List<URI> two = urisFutureTwo.join();
//        List<URI> three = urisFutureThee.join();
//        List<URI> four = urisFutureFour.join();
//        var result = Stream.of(one, two, three, four).flatMap(Collection::stream).toList();

//        CompletableFuture<List<URI>> allUrisCombined = urisFutureOne.thenCombineAsync(
//                        urisFutureTwo,
//                        (urisOne, urisTwo) -> {
//                            log.info("futures one and two done, combining them in: " + Thread.currentThread().getName());
//                            return Stream.of(urisOne, urisTwo)
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                        }, pool)
//                .thenCombineAsync(
//                        urisFutureThee,
//                        (urisOneAndTwo, urisThree) -> {
//                            log.info("future three done, combining with one and two in: " + Thread.currentThread().getName());
//                            return Stream.of(urisOneAndTwo, urisThree)
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                        }, pool)
//                .thenCombineAsync(
//                        urisFutureFour,
//                        (urisOneTwoAndThree, urisFour) -> {
//                            log.info("future four done, combining with one, two and three in: " + Thread.currentThread().getName());
//                            return Stream.of(urisOneTwoAndThree, urisFour)
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                        }, pool);
//
//        List<URI> result = allUrisCombined.join();

//        List<URI> result = allOfExceptionally(List.of(urisFutureOne, urisFutureTwo, urisFutureThee, urisFutureFour), pool)
//                .thenApplyAsync(lists -> lists.stream().flatMap(Collection::stream).toList(), pool)
//                .exceptionallyAsync(throwable -> {
//                    if (Objects.nonNull(throwable)) {
//                        log.log(Level.SEVERE, throwable, throwable::getMessage);
//                        pool.shutdownNow();
//                    }
//                    return Collections.emptyList();
//                }, pool)
//                .join();

        List<URI> result = anyOf(List.of(urisFutureOne, urisFutureTwo, urisFutureThee, urisFutureFour), pool)
                .thenApplyAsync(lists -> lists.stream().flatMap(Collection::stream).toList(), pool)
                .join();

        Instant end = Instant.now();

        result.forEach(System.out::println);
        log.info("time: " + Duration.between(start, end).toMillis());

        pool.shutdown();
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<List<T>> anyOf(List<CompletableFuture<T>> futures, ExecutorService pool) {
        CompletableFuture<Object> any = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]));
        return any.thenApplyAsync(o -> Optional.ofNullable(o).stream().map(val -> (T) val).toList(), pool);
    }

    private static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futures, ExecutorService pool) {
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return all.thenApplyAsync(__ -> futures.stream().map(CompletableFuture::join).toList(), pool);
    }

    private static <T> CompletableFuture<List<T>> allOfExceptionally(List<CompletableFuture<T>> futures, ExecutorService pool) {
        CompletableFuture<List<T>> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApplyAsync(__ -> futures.stream().map(CompletableFuture::join).toList(), pool);

        futures.forEach(future -> future.whenComplete((__, e) -> {
            if (Objects.nonNull(e)) {
                all.completeExceptionally(e);
            }
        }));

        return all;
    }

    private static void longProcess() {
        try {
            Thread.sleep(new Random().nextInt(10) * 1000);
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, e, e::getMessage);
        }
    }
}
