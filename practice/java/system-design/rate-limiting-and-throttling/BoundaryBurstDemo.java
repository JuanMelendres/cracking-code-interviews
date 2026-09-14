/**
 * Real demonstration of the fixed-window boundary-burst flaw and how the other three
 * algorithms handle the same attack. All four limiters are configured identically:
 * limit = 10 requests per 200ms window. The attack: sleep until just before a fixed
 * window boundary, fire 10 requests, then fire 10 more immediately after the boundary
 * -- 20 requests inside a real elapsed span far smaller than the 200ms window.
 */
public final class BoundaryBurstDemo {

    private static final int LIMIT = 10;
    private static final long WINDOW_MILLIS = 200;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Boundary-burst attack: limit=" + LIMIT + " per " + WINDOW_MILLIS + "ms window ===");
        System.out.println();

        runAttack("FixedWindowCounter", new FixedWindowCounter(LIMIT, WINDOW_MILLIS)::tryAcquire);
        runAttack("SlidingWindowLog", new SlidingWindowLog(LIMIT, WINDOW_MILLIS)::tryAcquire);
        runAttack("SlidingWindowCounter (approx)", new SlidingWindowCounter(LIMIT, WINDOW_MILLIS)::tryAcquire);

        System.out.println();
        System.out.println("TokenBucket under the same attack (capacity=10, refill=50/s -> steady-state 10 per 200ms):");
        TokenBucket bucket = new TokenBucket(LIMIT, LIMIT / (WINDOW_MILLIS / 1000.0));
        runAttack("TokenBucket", bucket::tryAcquire);
    }

    private interface Acquirer {
        boolean tryAcquire();
    }

    private static void runAttack(String name, Acquirer limiter) throws InterruptedException {
        long windowBoundary = ((System.currentTimeMillis() / WINDOW_MILLIS) + 1) * WINDOW_MILLIS;
        long sleepUntil = windowBoundary - 20;
        long now = System.currentTimeMillis();
        if (sleepUntil > now) {
            Thread.sleep(sleepUntil - now);
        }

        int admittedBeforeBoundary = 0;
        for (int i = 0; i < LIMIT; i++) {
            if (limiter.tryAcquire()) admittedBeforeBoundary++;
        }

        while (System.currentTimeMillis() < windowBoundary) {
            // busy-wait the last few ms to land just after the real window boundary
        }

        int admittedAfterBoundary = 0;
        for (int i = 0; i < LIMIT; i++) {
            if (limiter.tryAcquire()) admittedAfterBoundary++;
        }

        int total = admittedBeforeBoundary + admittedAfterBoundary;
        System.out.printf(
                "%-32s before-boundary=%d after-boundary=%d total-admitted-in-burst=%d (nominal limit=%d)%n",
                name, admittedBeforeBoundary, admittedAfterBoundary, total, LIMIT);
    }
}
