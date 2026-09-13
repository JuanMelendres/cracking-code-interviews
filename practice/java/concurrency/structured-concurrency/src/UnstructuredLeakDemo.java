import java.util.concurrent.CompletableFuture;

/**
 * Real, measured proof of the exact problem structured concurrency solves:
 * with plain CompletableFuture (or a raw ExecutorService), when one async
 * task fails, NOTHING automatically cancels its sibling -- the sibling
 * keeps running as a real, orphaned background thread, consuming resources
 * for its FULL duration even though the caller has already "moved on" past
 * the failure.
 */
public class UnstructuredLeakDemo {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        CompletableFuture<String> failFast = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            throw new IllegalStateException("simulated fast failure");
        });

        CompletableFuture<String> longRunning = CompletableFuture.supplyAsync(() -> {
            sleep(2000); // nothing will cancel this when failFast fails
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("  [background] orphaned task FINALLY finished at +" + elapsed
                    + "ms -- it ran its full real 2000ms even though the caller already moved on");
            return "completed-full-2-seconds";
        });

        try {
            failFast.join();
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Caller observes the failure at +" + elapsed + "ms and \"moves on\" --"
                    + " but the sibling task is STILL RUNNING in the background right now, uncancelled.");
        }

        System.out.println("isDone() on the sibling immediately after 'moving on': " + longRunning.isDone()
                + "  <-- still running, a real orphaned/leaked task");

        // Only to let this demo's own process observe the eventual real completion for the README capture --
        // in real production code, nothing would ever wait for this, which is exactly the leak.
        longRunning.join();
        long totalElapsed = System.currentTimeMillis() - start;
        System.out.println("Total real wall time until the orphaned task actually finished: " + totalElapsed
                + "ms -- versus StructuredTaskScope's real ~100ms in FailFastCancellationDemo");
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
