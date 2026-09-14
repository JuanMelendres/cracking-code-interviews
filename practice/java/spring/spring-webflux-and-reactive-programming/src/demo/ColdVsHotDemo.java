package demo;

import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdVsHotDemo {

    public static void main(String[] args) {
        System.out.println("=== COLD Flux: the source's side effect re-runs for EACH subscriber ===");
        AtomicInteger coldSideEffects = new AtomicInteger(0);
        Flux<Integer> cold = Flux.defer(() -> {
            coldSideEffects.incrementAndGet();
            return Flux.range(1, 3);
        });
        cold.subscribe(v -> {});
        cold.subscribe(v -> {});
        System.out.println("Real side-effect executions: " + coldSideEffects.get()
                + " (expect 2 -- once per subscriber, independently)");

        System.out.println();
        System.out.println("=== HOT Flux (ConnectableFlux): the source's side effect runs ONCE ===");
        AtomicInteger hotSideEffects = new AtomicInteger(0);
        Flux<Integer> source = Flux.range(1, 3).doOnSubscribe(s -> hotSideEffects.incrementAndGet());
        ConnectableFlux<Integer> hot = source.publish();
        hot.subscribe(v -> {});
        hot.subscribe(v -> {});
        hot.connect(); // only NOW does the underlying source actually get subscribed to, once
        System.out.println("Real side-effect executions: " + hotSideEffects.get()
                + " (expect 1 -- both subscribers share the one real upstream subscription)");
    }
}
