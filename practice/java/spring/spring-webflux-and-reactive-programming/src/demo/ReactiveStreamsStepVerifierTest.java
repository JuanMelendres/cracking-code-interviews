package demo;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ReactiveStreamsStepVerifierTest {

    @Test
    void fluxEmitsExpectedSequence() {
        Flux<Integer> flux = Flux.just(1, 2, 3);

        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void virtualTimeProvesADelayWithoutActuallyWaitingForIt() {
        // Really simulates two real hours of elapsed time in a few milliseconds --
        // no test ever actually sleeps for this.
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(1)).take(2))
                .expectSubscription()
                .thenAwait(Duration.ofHours(2))
                .expectNext(0L, 1L)
                .verifyComplete();
    }
}
