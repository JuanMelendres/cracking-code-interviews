import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Real, measured proof of StructuredTaskScope's core value proposition:
 * when one subtask fails, ShutdownOnFailure automatically interrupts every
 * OTHER subtask still running in the scope -- real cancellation, not a
 * documentation claim. Measured by giving the sibling task a real 5-second
 * sleep loop that cooperatively checks for interruption, then confirming
 * the whole scope.join() returns in a small fraction of that 5 seconds.
 */
public class FailFastCancellationDemo {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        boolean[] longTaskWasInterrupted = {false};

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                Thread.sleep(100); // fails fast
                throw new IllegalStateException("simulated fast failure");
            });
            scope.fork(() -> {
                // A cooperative long task: checks for interruption every
                // 50ms across a real 5-second budget instead of one long
                // uninterruptible sleep, so cancellation is actually observable.
                try {
                    for (int i = 0; i < 100; i++) {
                        if (Thread.currentThread().isInterrupted()) {
                            longTaskWasInterrupted[0] = true;
                            return "cancelled-early-via-flag-check";
                        }
                        Thread.sleep(50);
                    }
                    return "completed-full-5-seconds";
                } catch (InterruptedException e) {
                    // The MORE common real outcome: shutdown() interrupts the
                    // blocking Thread.sleep() itself, which throws instead of
                    // letting the loop reach its own isInterrupted() check.
                    longTaskWasInterrupted[0] = true;
                    throw e;
                }
            });

            scope.join();

            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Real elapsed: " + elapsed + "ms (the long task's FULL budget was 5000ms)");
            System.out.println("Long-running sibling was really interrupted (flag check or Thread.sleep() throwing): "
                    + longTaskWasInterrupted[0]);

            try {
                scope.throwIfFailed();
            } catch (ExecutionException e) {
                System.out.println("scope.throwIfFailed() correctly surfaced the real failure: " + e.getCause());
            }
        }
    }
}
