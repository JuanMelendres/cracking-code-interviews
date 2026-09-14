import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Finds a real bounded worker pool's saturation point by offering
 * increasing real load and measuring real throughput and real tail
 * latency at each level -- the empirical basis for a capacity-planning
 * headroom recommendation.
 *
 * <p>Theoretical max sustainable throughput = POOL_SIZE / SERVICE_TIME_SECONDS.
 * This demo measures whether reality matches that number, and what
 * happens to latency as offered load approaches and exceeds it.
 */
public final class SaturationPointDemo {

    private static final int POOL_SIZE = 8;
    private static final long SERVICE_TIME_MILLIS = 50;
    private static final long RUN_DURATION_MILLIS = 4_000;
    private static final double[] OFFERED_RATES = {60, 100, 140, 155, 165, 200};

    public static void main(String[] args) throws Exception {
        double theoreticalMaxThroughput = POOL_SIZE / (SERVICE_TIME_MILLIS / 1000.0);
        System.out.printf("Pool size: %d, service time: %dms -> theoretical max throughput: %.1f req/s%n%n",
                POOL_SIZE, SERVICE_TIME_MILLIS, theoreticalMaxThroughput);
        System.out.printf("%-14s %-16s %-10s %-10s %-10s%n",
                "Offered(/s)", "Completed(/s)", "p50(ms)", "p99(ms)", "Max(ms)");

        for (double rate : OFFERED_RATES) {
            Result r = runLoadLevel(rate);
            System.out.printf("%-14.0f %-16.1f %-10.1f %-10.1f %-10.1f%n",
                    rate, r.throughput, r.p50Millis, r.p99Millis, r.maxMillis);
        }

        System.out.println();
        System.out.println("Interpretation: throughput tracks offered load up to roughly the "
                + theoreticalMaxThroughput + " req/s theoretical ceiling; beyond it, completed "
                + "throughput flattens (the pool cannot go faster) while p99 and max latency grow "
                + "sharply as excess requests queue up faster than the pool can drain them.");
    }

    private static Result runLoadLevel(double offeredRatePerSec) throws InterruptedException {
        ExecutorService workers = Executors.newFixedThreadPool(POOL_SIZE);
        ScheduledExecutorService arrivalGenerator = Executors.newSingleThreadScheduledExecutor();
        AtomicLong completedInWindow = new AtomicLong(0);
        // Latency of every request *submitted* during the offered-load window, no matter
        // when it actually finishes -- this is what makes a real backlog's blowup visible
        // instead of silently dropping the very requests that prove the point.
        List<Long> latenciesNanosForSubmittedInWindow = new CopyOnWriteArrayList<>();

        long runStartNanos = System.nanoTime();
        long windowEndNanos = runStartNanos + TimeUnit.MILLISECONDS.toNanos(RUN_DURATION_MILLIS);

        long periodNanos = (long) (1_000_000_000.0 / offeredRatePerSec);
        arrivalGenerator.scheduleAtFixedRate(() -> {
            long submitNanos = System.nanoTime();
            boolean submittedInWindow = submitNanos <= windowEndNanos;
            workers.submit(() -> {
                try {
                    Thread.sleep(SERVICE_TIME_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    long completeNanos = System.nanoTime();
                    if (submittedInWindow) {
                        latenciesNanosForSubmittedInWindow.add(completeNanos - submitNanos);
                    }
                    if (completeNanos <= windowEndNanos) {
                        completedInWindow.incrementAndGet();
                    }
                }
            });
        }, 0, periodNanos, TimeUnit.NANOSECONDS);

        Thread.sleep(RUN_DURATION_MILLIS);
        arrivalGenerator.shutdownNow();
        workers.shutdown();
        // Drain whatever backlog built up so every in-window-submitted request's real
        // completion latency is captured, even if it finishes after the window closed.
        workers.awaitTermination(30, TimeUnit.SECONDS);

        List<Long> sorted = new ArrayList<>(latenciesNanosForSubmittedInWindow);
        Collections.sort(sorted);
        double throughput = completedInWindow.get() / (RUN_DURATION_MILLIS / 1000.0);

        return new Result(
                throughput,
                percentileMillis(sorted, 0.50),
                percentileMillis(sorted, 0.99),
                sorted.isEmpty() ? 0 : sorted.get(sorted.size() - 1) / 1_000_000.0);
    }

    private static double percentileMillis(List<Long> sortedNanos, double percentile) {
        if (sortedNanos.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil(percentile * sortedNanos.size()) - 1;
        index = Math.max(0, Math.min(index, sortedNanos.size() - 1));
        return sortedNanos.get(index) / 1_000_000.0;
    }

    private record Result(double throughput, double p50Millis, double p99Millis, double maxMillis) {
    }
}
