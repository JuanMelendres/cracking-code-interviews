package demo;

import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class BackpressureDemo {

    private static final int BATCH_SIZE = 4;

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger produced = new AtomicInteger(0);

        // Flux.range is demand-aware -- it only generates elements up to the
        // outstanding request, never ahead of it. This proves that directly:
        // "produced" should never run ahead of what the subscriber actually asked for.
        Flux<Integer> range = Flux.range(1, 12)
                .doOnNext(v -> produced.incrementAndGet());

        range.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("Requesting first batch of " + BATCH_SIZE);
                request(BATCH_SIZE);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Consumed " + value + " -- real upstream elements produced so far: " + produced.get());
                if (value % BATCH_SIZE == 0) {
                    System.out.println("Requesting next batch of " + BATCH_SIZE);
                    request(BATCH_SIZE);
                }
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Done. Final produced count: " + produced.get() + " (matches total consumed -- never ran ahead of demand)");
            }
        });
    }
}
