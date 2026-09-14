import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Real sliding-window-log limiter. Stores an exact timestamp per admitted request and
 * evicts everything older than `windowMillis` on each call, then admits only if the
 * remaining count is under the limit. Exact (no boundary-burst flaw, unlike
 * {@link FixedWindowCounter}) but O(n) memory in the limit and O(n) eviction work
 * per call -- the real cost this precision buys.
 */
final class SlidingWindowLog {

    private final int limit;
    private final long windowMillis;
    private final Deque<Long> timestamps = new ArrayDeque<>();

    SlidingWindowLog(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
    }

    synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long cutoff = now - windowMillis;
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.pollFirst();
        }
        if (timestamps.size() < limit) {
            timestamps.addLast(now);
            return true;
        }
        return false;
    }

    synchronized int size() {
        return timestamps.size();
    }
}
