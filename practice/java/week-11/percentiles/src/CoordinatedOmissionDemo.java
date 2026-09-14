import java.util.*;

/**
 * T-1204 -- percentiles, tail latency, and coordinated omission, measured
 * against a simulated service with a realistic latency profile: mostly
 * fast (~10ms), occasionally very slow (a 500ms stall on 2% of requests,
 * simulating a GC pause or a lock contention spike).
 *
 * A CLOSED-LOOP load generator (send the next request only after the
 * previous one completes) systematically UNDER-counts how bad the tail
 * really is, because it sends fewer requests exactly when the service is
 * slow -- the slow requests it does measure are correctly recorded, but
 * all the requests that WOULD have been sent and WOULD have queued up
 * behind a stall are never sent at all, and never measured.
 *
 * An OPEN-LOOP load generator (send at a fixed rate regardless of
 * response time) reveals the true tail, because a stall causes a real
 * backlog of un-serviced requests whose wait time is real and must be
 * counted.
 */
public class CoordinatedOmissionDemo {
    static final int TOTAL_REQUESTS = 100_000;
    static final double STALL_PROBABILITY = 0.02;
    static final long NORMAL_LATENCY_MS = 10;
    static final long STALL_LATENCY_MS = 500;
    static final long TARGET_INTERVAL_MS = 50; // open-loop: intended 20 req/s -- well under the
    // ~50 req/s the service can sustain (avg demand = 0.98*10ms + 0.02*500ms = 19.8ms/req),
    // so the queue is stable and drains between stalls instead of growing without bound

    public static void main(String[] args) {
        Random random = new Random(11);

        System.out.println("== closed-loop (naive): send next request only after the previous completes ==");
        List<Long> closedLoopLatencies = new ArrayList<>();
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            long latency = random.nextDouble() < STALL_PROBABILITY ? STALL_LATENCY_MS : NORMAL_LATENCY_MS;
            closedLoopLatencies.add(latency); // this IS the request's real service time -- correctly measured
        }
        printPercentiles("closed-loop", closedLoopLatencies);

        System.out.println();
        System.out.println("== open-loop (correct): requests are scheduled every " + TARGET_INTERVAL_MS
                + "ms regardless of how long the previous one took ==");
        random = new Random(11); // same seed -- same sequence of stalls, for a fair comparison
        List<Long> openLoopLatencies = new ArrayList<>();
        long nextScheduledAt = 0;
        long serviceFreeAt = 0;
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            long serviceTime = random.nextDouble() < STALL_PROBABILITY ? STALL_LATENCY_MS : NORMAL_LATENCY_MS;
            long actualStartTime = Math.max(nextScheduledAt, serviceFreeAt); // waits behind any backlog
            long responseTime = (actualStartTime - nextScheduledAt) + serviceTime; // queueing wait + real service time
            openLoopLatencies.add(responseTime);
            serviceFreeAt = actualStartTime + serviceTime;
            nextScheduledAt += TARGET_INTERVAL_MS;
        }
        printPercentiles("open-loop", openLoopLatencies);

        System.out.println();
        System.out.println("The closed-loop run measured the SAME per-request service times as the open-loop run's "
                + "service component -- but never measured the QUEUEING delay a real stall causes for every request "
                + "stuck behind it, because it never sends those requests in the first place. That queueing delay is "
                + "coordinated omission, and it's why closed-loop load generators systematically understate p99+.");
    }

    static void printPercentiles(String label, List<Long> latencies) {
        List<Long> sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        System.out.printf("%s: p50=%dms  p90=%dms  p99=%dms  p99.9=%dms  max=%dms%n",
                label,
                percentile(sorted, 50), percentile(sorted, 90),
                percentile(sorted, 99), percentile(sorted, 99.9),
                sorted.get(sorted.size() - 1));
    }

    static long percentile(List<Long> sorted, double p) {
        int index = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }
}
