package demo;

import java.time.Duration;
import java.time.Instant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class BlockingPitfallDemo {

    public static void main(String[] args) {
        // Simulates a small, fixed-size reactive event-loop-style scheduler --
        // WebFlux's real Netty event loop is exactly this shape: a small, fixed
        // number of threads meant to never block.
        Scheduler eventLoop = Schedulers.newParallel("event-loop", 1);

        System.out.println("=== BUGGY: blocking calls run directly on the tiny event-loop scheduler ===");
        Instant start1 = Instant.now();
        Flux.range(1, 3)
                .flatMap(i -> Mono.fromCallable(() -> {
                            Thread.sleep(300); // simulates blocking I/O (e.g. a JDBC call)
                            return i;
                        })
                        .subscribeOn(eventLoop))
                .doOnNext(i -> System.out.println("finished task " + i + " at +"
                        + Duration.between(start1, Instant.now()).toMillis() + "ms"))
                .blockLast();
        long buggyTotal = Duration.between(start1, Instant.now()).toMillis();
        System.out.println("Total: " + buggyTotal + "ms (expect ~900ms -- serialized on the single event-loop thread)");

        System.out.println();
        System.out.println("=== FIXED: blocking calls offloaded to a real bounded elastic pool ===");
        Instant start2 = Instant.now();
        Flux.range(1, 3)
                .flatMap(i -> Mono.fromCallable(() -> {
                            Thread.sleep(300);
                            return i;
                        })
                        .subscribeOn(Schedulers.boundedElastic()))
                .doOnNext(i -> System.out.println("finished task " + i + " at +"
                        + Duration.between(start2, Instant.now()).toMillis() + "ms"))
                .blockLast();
        long fixedTotal = Duration.between(start2, Instant.now()).toMillis();
        System.out.println("Total: " + fixedTotal + "ms (expect ~300ms -- ran concurrently on separate threads)");

        eventLoop.dispose();
    }
}
