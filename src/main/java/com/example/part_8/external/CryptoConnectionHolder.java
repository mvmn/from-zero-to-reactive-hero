package com.example.part_8.external;

import com.example.part_8.external.utils.PriceMessageUnpacker;
import com.example.part_8.external.utils.TradeMessageUnpacker;
import com.google.common.base.Functions;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

//TODO turn to multi-subscriber with processor or another similar operator
//TODO add small history for each subscriber
//TODO add resilience

public class CryptoConnectionHolder {
    public static final int CACHE_SIZE = 3;

    private final Flux<Map<String, Object>> reactiveCryptoListener;

    public CryptoConnectionHolder() {
        reactiveCryptoListener = ReactiveCryptoListener
                .connect(
                        Flux.just("5~CCCAGG~BTC~USD", "0~Poloniex~BTC~USD"),
                        Arrays.asList(new PriceMessageUnpacker(), new TradeMessageUnpacker())
                )
                .transform(CryptoConnectionHolder::provideResilience)
                .transform(CryptoConnectionHolder::provideCaching);
    }

    public Flux<Map<String, Object>> listenForExternalEvents() {
        return reactiveCryptoListener;
    }

    // XTODO: implement resilience such as retry with delay
    public static <T> Flux<T> provideResilience(Flux<T> input) {
    	    return input.retryWhen(err -> Flux.interval(Duration.ofSeconds(10L)).onBackpressureDrop());
    }


    // XTODO: implement caching of 3 last elements & multi subscribers support
    public static <T> Flux<T> provideCaching(Flux<T> input) {
        return input.cache(3);
    }
}
