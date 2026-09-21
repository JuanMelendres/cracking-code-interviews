import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** The tempting, wrong way to test async code: a fixed sleep shorter than
 * the work can genuinely take. AsyncWorker's real delay is 5-60ms; this
 * test's fixed 20ms sleep loses the race whenever the real delay exceeds
 * it -- a real, reproducible flaky test, not a contrived one. */
public class FlakyAsyncTest {

    @RepeatedTest(30)
    void completesWithinAFixedSleep() throws InterruptedException {
        AtomicBoolean completed = new AtomicBoolean(false);
        new AsyncWorker().runAsync(() -> completed.set(true));

        Thread.sleep(20); // WRONG: shorter than the real worst-case delay (60ms)

        assertTrue(completed.get(), "expected async work to complete within the fixed sleep");
    }
}
