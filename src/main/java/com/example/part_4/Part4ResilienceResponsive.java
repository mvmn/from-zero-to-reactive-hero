package com.example.part_4;

import static com.example.annotations.Complexity.Level.EASY;
import static com.example.annotations.Complexity.Level.HARD;
import static com.example.annotations.Complexity.Level.MEDIUM;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.reactivestreams.Publisher;

import com.example.annotations.Complexity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Part4ResilienceResponsive {

    @Complexity(EASY)
    public static Publisher<String> fallbackHelloOnEmpty(Flux<String> emptyPublisher) {
        // TODO: return fallback on empty source
        // TODO: in case of no value emitted return fallback with "Hello"
        // HINT: Flux#switchIfEmpty() or Flux#defaultIfEmpty

        return emptyPublisher.defaultIfEmpty("Hello");
    }

    @Complexity(EASY)
    public static Publisher<String> fallbackHelloOnError(Flux<String> failurePublisher) {
        // TODO: return fallback on error
        // TODO: in case of error return fallback with "Hello"
        // HINT: Flux#onErrorResume or Flux#onErrorReturn

        return failurePublisher.onErrorReturn("Hello");
    }

    @Complexity(EASY)
    public static Publisher<String> retryOnError(Mono<String> failurePublisher) {
        // TODO: retry operation if error
        // HINT: Flux#retry()

        return failurePublisher.retry();
    }

    @Complexity(MEDIUM)
    public static Publisher<String> timeoutLongOperation(CompletableFuture<String> longRunningCall) {
        // TODO: limit the overall operation execution to one second
        // TODO: in case of timeout return fallback with "Hello"
        // HINT: Mono.fromFuture() + Mono#timeout(Duration, Mono)

        return Mono.fromFuture(longRunningCall).timeout(Duration.ofSeconds(1), Mono.just("Hello"));
    }

    @Complexity(HARD)
    public static Publisher<String> timeoutLongOperation(Callable<String> longRunningCall) {
        // TODO: limit the overall operation execution to one second
        // TODO: in case of timeout return fallback with "Hello"
        // HINT: bear in mind that execution should occur on different thread
        // HINT: Mono.fromCallable + .subscribeOn + Mono#timeout(Duration, Mono)

        return Mono.fromCallable(longRunningCall).timeout(Duration.ofSeconds(1), Mono.just("Hello")).subscribeOn(Schedulers.parallel());
    }
}
