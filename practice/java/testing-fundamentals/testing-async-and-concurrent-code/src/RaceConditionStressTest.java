import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** A single run with a handful of iterations would very likely pass by
 * luck even with the real race present -- the whole point of a stress
 * test is enough concurrent iterations that a real, intermittent bug
 * becomes a reliably observable one instead of a coin flip. */
public class RaceConditionStressTest {

    private static final int THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 100_000;
    private static final int EXPECTED = THREADS * INCREMENTS_PER_THREAD;

    @Test
    void unsafeCounterLosesUpdatesUnderConcurrency() throws InterruptedException {
        UnsafeCounter counter = new UnsafeCounter();
        runConcurrently(counter::increment);
        assertEquals(EXPECTED, counter.get(), "unsafe, non-atomic increment loses real updates under concurrency");
    }

    static void runConcurrently(Runnable incrementOnce) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS);

        for (int t = 0; t < THREADS; t++) {
            new Thread(() -> {
                ready.countDown();
                try {
                    start.await(); // all threads start incrementing at once -- maximizes real contention
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int i = 0; i < INCREMENTS_PER_THREAD; i++) incrementOnce.run();
                done.countDown();
            }).start();
        }

        ready.await();
        start.countDown();
        done.await();
    }
}
