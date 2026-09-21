import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** The reliable way to test the identical async work: wait on a real
 * completion signal (a CountDownLatch the async callback itself counts
 * down), bounded by a generous timeout -- not a fixed guess at how long
 * the work might take. Same AsyncWorker, same real 5-60ms delay, zero
 * flakiness across repeated runs. */
public class ReliableAsyncTest {

    @RepeatedTest(30)
    void completesBeforeLatchTimeout() throws InterruptedException {
        AtomicBoolean completed = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        new AsyncWorker().runAsync(() -> {
            completed.set(true);
            latch.countDown();
        });

        boolean signaled = latch.await(2, TimeUnit.SECONDS); // waits for the REAL signal, not a guess

        assertTrue(signaled, "latch was never counted down within the timeout");
        assertTrue(completed.get(), "expected async work to have completed");
    }
}
