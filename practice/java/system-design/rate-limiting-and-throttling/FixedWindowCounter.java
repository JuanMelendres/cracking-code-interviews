/**
 * Real fixed-window counter. A single counter is reset every `windowMillis`, aligned
 * to absolute wall-clock boundaries (not relative to first request). This is the
 * algorithm with the well-known boundary-burst flaw: a client can send `limit`
 * requests in the last millisecond of one window and another `limit` in the first
 * millisecond of the next, admitting up to 2x the intended rate in a short span
 * straddling the boundary.
 */
final class FixedWindowCounter {

    private final int limit;
    private final long windowMillis;
    private long currentWindowStart;
    private int count;

    FixedWindowCounter(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
        this.currentWindowStart = alignedWindowStart(System.currentTimeMillis());
        this.count = 0;
    }

    private long alignedWindowStart(long nowMillis) {
        return (nowMillis / windowMillis) * windowMillis;
    }

    synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long window = alignedWindowStart(now);
        if (window != currentWindowStart) {
            currentWindowStart = window;
            count = 0;
        }
        if (count < limit) {
            count++;
            return true;
        }
        return false;
    }
}
