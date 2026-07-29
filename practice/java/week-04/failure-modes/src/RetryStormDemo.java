import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real, measured retry-amplification simulation.
 *
 * A downstream "service" is a fixed-size thread pool (capacity = how many
 * requests it can process concurrently). Crucially -- and realistically --
 * a client-side timeout does NOT cancel the work already submitted to the
 * downstream service; the work keeps running and occupying a pool slot
 * even after the client has given up and moved on. This is the actual
 * mechanism by which retries amplify load: a retry doesn't replace the
 * original request's load, it ADDS to it.
 */
public class RetryStormDemo {

    static final int CAPACITY = 4;
    static final long WORK_MILLIS = 400;      // how long the downstream takes per unit of work (degraded)
    static final long CLIENT_TIMEOUT_MILLIS = 700; // client gives up if queueing + work exceeds this
    static final int LOGICAL_REQUESTS = 12;
    static final int MAX_ATTEMPTS = 3;
    static final long OVERALL_SLA_MILLIS = 6000; // how long we're willing to wait for a final answer

    public static void main(String[] args) throws Exception {
        System.out.println("Downstream capacity=" + CAPACITY + ", work=" + WORK_MILLIS + "ms, client timeout="
                + CLIENT_TIMEOUT_MILLIS + "ms, " + LOGICAL_REQUESTS + " logical requests in a burst.\n");

        Result noRetry = run("NO RETRY", 1, 0);
        Result retryNoBackoff = run("RETRY, NO BACKOFF (immediate resubmit)", MAX_ATTEMPTS, 0);
        Result retryBackoff = run("RETRY, EXPONENTIAL BACKOFF + JITTER", MAX_ATTEMPTS, 200);

        System.out.println("\n=== Summary ===");
        System.out.printf("%-42s %10s %14s %16s%n", "Strategy", "Succeeded", "Work units submitted", "Amplification");
        printRow("No retry", noRetry);
        printRow("Retry, no backoff", retryNoBackoff);
        printRow("Retry, exponential backoff + jitter", retryBackoff);
    }

    private static void printRow(String label, Result r) {
        double amplification = (double) r.workUnitsSubmitted / LOGICAL_REQUESTS;
        System.out.printf("%-42s %10d %14d %15.1fx%n", label, r.succeeded, r.workUnitsSubmitted, amplification);
    }

    static final class Result {
        int succeeded;
        int workUnitsSubmitted;
    }

    private static Result run(String label, int maxAttempts, long baseBackoffMillis) throws InterruptedException {
        System.out.println("--- " + label + " ---");
        ExecutorService downstream = Executors.newFixedThreadPool(CAPACITY);
        AtomicInteger workUnitsSubmitted = new AtomicInteger();
        ExecutorService clientPool = Executors.newFixedThreadPool(LOGICAL_REQUESTS);

        long start = System.currentTimeMillis();
        java.util.List<Future<Boolean>> clientFutures = new java.util.ArrayList<>();
        for (int i = 0; i < LOGICAL_REQUESTS; i++) {
            final int requestId = i;
            clientFutures.add(clientPool.submit(() ->
                    handleLogicalRequest(requestId, downstream, workUnitsSubmitted, maxAttempts, baseBackoffMillis)));
        }

        int succeeded = 0;
        for (Future<Boolean> f : clientFutures) {
            try {
                boolean ok = f.get(OVERALL_SLA_MILLIS, TimeUnit.MILLISECONDS);
                if (ok) succeeded++;
            } catch (Exception e) {
                // treated as a final failure for this logical request within the SLA window
            }
        }
        long elapsed = System.currentTimeMillis() - start;

        clientPool.shutdownNow();
        downstream.shutdown();
        downstream.awaitTermination(3, TimeUnit.SECONDS);
        downstream.shutdownNow();

        System.out.println("Elapsed: " + elapsed + "ms, succeeded within SLA: " + succeeded + "/" + LOGICAL_REQUESTS
                + ", total work units submitted to downstream: " + workUnitsSubmitted.get() + "\n");

        Result r = new Result();
        r.succeeded = succeeded;
        r.workUnitsSubmitted = workUnitsSubmitted.get();
        return r;
    }

    private static boolean handleLogicalRequest(int requestId, ExecutorService downstream,
                                                 AtomicInteger workUnitsSubmitted,
                                                 int maxAttempts, long baseBackoffMillis) {
        java.util.Random random = new java.util.Random(requestId);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            workUnitsSubmitted.incrementAndGet();
            Future<Boolean> work = downstream.submit(() -> {
                Thread.sleep(WORK_MILLIS); // the work keeps running even if the client below times out
                return true;
            });
            try {
                work.get(CLIENT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                return true; // client saw a successful response within its timeout
            } catch (TimeoutException e) {
                // client gives up on THIS attempt, but the submitted work above is NOT cancelled --
                // it still occupies a downstream thread until it finishes.
                if (attempt < maxAttempts && baseBackoffMillis > 0) {
                    long backoff = baseBackoffMillis * (1L << (attempt - 1)) + random.nextInt(100);
                    try { Thread.sleep(backoff); } catch (InterruptedException ignored) { }
                }
                // no-backoff case: loop immediately resubmits with no delay
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
