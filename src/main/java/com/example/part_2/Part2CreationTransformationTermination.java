package com.example.part_2;

import com.example.annotations.Complexity;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.function.Function;

import static com.example.annotations.Complexity.Level.EASY;
import static com.example.annotations.Complexity.Level.HARD;
import static com.example.annotations.Complexity.Level.MEDIUM;

public class Part2CreationTransformationTermination {

    @Complexity(EASY)
    public static Mono<List<String>> collectAllItemsToList(Flux<String> source) {
        // TODO: collect to list
        // Flux#collectList or Flux#collect(+Collectors.toList)

        return source.collectList();
    }

    @Complexity(EASY)
    public static String lastElementFromSource(Flux<String> source) {
        // TODO: block until all emitted

        return source.last().block();
    }

    @Complexity(EASY)
    public static Publisher<String> mergeSeveralSources(Publisher<String>... sources) {
        // TODO: merge all sources in one stream

        return Flux.merge(sources);
    }

    @Complexity(EASY)
    public static Publisher<String> fromFirstEmitted(Publisher<String>... sources) {
        // TODO: return events from the first emitted
        // HINT: Flux.first()

        return Flux.first(sources);
    }

    @Complexity(EASY)
    public static Publisher<GroupedFlux<Character, String>> groupWordsByFirstLatter(Flux<String> words) {
        // TODO: group elements by first latter
        // HINT: flux.groupBy(java.util.function.Function<? super T,? extends K>)
        // HINT: String#chartAt(0) to extract first character

        return words.groupBy(s->s.charAt(0));
    }

    @Complexity(MEDIUM)
    public static Mono<String> executeLazyTerminationOperationAndSendHello(Flux<String> source) {
        // TODO: wait completion and .THEN() execute another source
        // HINT: source.then( + Flux#just('Hello') )

		return source.then(Mono.just("Hello"));
    }

    @Complexity(MEDIUM)
    public static Publisher<String> zipSeveralSources(Publisher<String> prefix,
                                                      Publisher<String> word,
                                                      Publisher<String> suffix) {
        // TODO: zip sources and concat elements in string
        // HINT: Flux#zip produce as the result of zipping 3 streams elements of type Tuple3
        // HINT: use Tuple3.getT1 ... getT2 ... getT3

        return Flux.zip(prefix, word, suffix).map(t->t.getT1()+t.getT2()+t.getT3());
    }

    @Complexity(HARD)
    public static Publisher<String> combineSeveralSources(Publisher<String> prefix,
                                                          Publisher<String> word,
                                                          Publisher<String> suffix) {
        // TODO: combine latest element emitted from the sources in string
        // HINT: use Tuples::fromArray as a combinator function for
        //       reactor.core.publisher.Flux.combineLatest(
        //                 java.util.function.Function<java.lang.Object[],V>,  <--- Tuples::fromArray
        //                 org.reactivestreams.Publisher<? extends T>...
        //       )
        // HINT: Use Flux#cast to cast Tuple2 --> Tuple3
        // HINT: Use "" + Tuple3.getT1 ... getT2 ... getT3

        //
        //
        //
        //        return Flux.combineLatest(
        //                args -> "" + args[0] + args[1] + args[2],
        //                prefix, word, suffix
        //        );

		return Flux.combineLatest(prefix, word, suffix, Tuples::fromArray)
				.map(t -> "" + t.getT1() + t.getT2() + t.get(2));
    }

    @Complexity(HARD)
    public static Flux<IceCreamBall> fillIceCreamWaffleBowl(
            Flux<IceCreamType> clientPreferences,
            Flux<IceCreamBall> vanillaIceCreamStream,
            Flux<IceCreamBall> chocolateIceCreamStream
    ) {
        // TODO: switch between clientPreferences regarding to the ice cream type emitted from clientPreferences stream
        // HINT: use clientPreferences.switchMap to switch between clientPreferences
        // HINT: use plain if else statement to check if emitted element from clientPreferences Flux is IceCreamType.VANILLA
        //       or IceCreamType.CHOCOLATE
        //       In case if event is IceCreamType.VANILLA - return vanillaIceCreamStream Flux
        //       otherwise return chocolateIceCreamStream Flux
		return clientPreferences
				.switchMap(t -> IceCreamType.VANILLA.equals(t) ? vanillaIceCreamStream : chocolateIceCreamStream);
    }
}
