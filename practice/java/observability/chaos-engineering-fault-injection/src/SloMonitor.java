import java.util.ArrayList;
import java.util.List;

/**
 * Real-time multi-window burn-rate detection, applying the same technique
 * documented in metric-cardinality-and-alert-fatigue.md (T-2409) -- a short
 * window and a long window must BOTH exceed the burn-rate threshold before
 * paging. Windows are compressed to 500ms/2000ms here purely so this demo
 * runs in a few real seconds; the ratio between short and long window
 * (1:4) and the 14.4 threshold are the same real technique, not a
 * different one.
 */
public class SloMonitor {

    static final double ALLOWED_ERROR_RATE = 0.01; // SLO: 99% success
    static final double BURN_RATE_THRESHOLD = 14.4;
    static final long SHORT_WINDOW_MILLIS = 500;
    static final long LONG_WINDOW_MILLIS = 2000;
    static final int MIN_SAMPLES = 4;

    private final List<RequestOutcome> outcomes = new ArrayList<>();

    public synchronized void record(RequestOutcome outcome) {
        outcomes.add(outcome);
    }

    public synchronized boolean isPaging(long nowMillis) {
        double shortBurn = burnRate(nowMillis, SHORT_WINDOW_MILLIS);
        double longBurn = burnRate(nowMillis, LONG_WINDOW_MILLIS);
        return shortBurn >= BURN_RATE_THRESHOLD && longBurn >= BURN_RATE_THRESHOLD;
    }

    public synchronized double burnRate(long nowMillis, long windowMillis) {
        long windowStart = nowMillis - windowMillis;
        int total = 0;
        int failures = 0;
        for (RequestOutcome o : outcomes) {
            if (o.timestampMillis() >= windowStart && o.timestampMillis() <= nowMillis) {
                total++;
                if (!o.success()) {
                    failures++;
                }
            }
        }
        if (total < MIN_SAMPLES) {
            return 0.0; // not enough data in this window yet
        }
        double errorRate = (double) failures / total;
        return errorRate / ALLOWED_ERROR_RATE;
    }
}
