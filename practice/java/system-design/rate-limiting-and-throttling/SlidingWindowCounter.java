/**
 * Real sliding-window-counter limiter (the "weighted count" approximation used by
 * Cloudflare and Kong in production). Keeps only two integer counters -- current and
 * previous fixed window -- and estimates the count in the trailing sliding window as
 * {@code previousCount * overlapFraction + currentCount}, where overlapFraction is
 * how much of the previous window still falls inside the trailing window. O(1) memory
 * and O(1) work per call, unlike {@link SlidingWindowLog}, at the cost of being an
 * estimate rather than an exact count.
 */
final class SlidingWindowCounter {

    private final int limit;
    private final long windowMillis;
    private long currentWindowStart;
    private int currentCount;
    private int previousCount;

    SlidingWindowCounter(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
        this.currentWindowStart = alignedWindowStart(System.currentTimeMillis());
        this.currentCount = 0;
        this.previousCount = 0;
    }

    private long alignedWindowStart(long nowMillis) {
        return (nowMillis / windowMillis) * windowMillis;
    }

    synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long window = alignedWindowStart(now);
        if (window != currentWindowStart) {
            long windowsElapsed = (window - currentWindowStart) / windowMillis;
            previousCount = (windowsElapsed == 1) ? currentCount : 0;
            currentCount = 0;
            currentWindowStart = window;
        }
        double elapsedInCurrent = now - currentWindowStart;
        double overlapFraction = Math.max(0.0, (windowMillis - elapsedInCurrent) / windowMillis);
        double estimate = previousCount * overlapFraction + currentCount;
        if (estimate < limit) {
            currentCount++;
            return true;
        }
        return false;
    }
}
