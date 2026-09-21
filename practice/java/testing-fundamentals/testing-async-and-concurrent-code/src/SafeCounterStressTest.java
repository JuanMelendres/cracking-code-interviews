import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Identical stress harness, identical thread/iteration counts, the only
 * difference is AtomicInteger instead of a plain int -- reliably correct
 * across repeated runs, proving the fix addresses the real mechanism, not
 * just this specific run's luck. */
public class SafeCounterStressTest {

    private static final int THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 100_000;
    private static final int EXPECTED = THREADS * INCREMENTS_PER_THREAD;

    @RepeatedTest(5)
    void safeCounterNeverLosesUpdatesUnderConcurrency() throws InterruptedException {
        SafeCounter counter = new SafeCounter();
        RaceConditionStressTest.runConcurrently(counter::increment);
        assertEquals(EXPECTED, counter.get(), "AtomicInteger must never lose an update under concurrency");
    }
}
