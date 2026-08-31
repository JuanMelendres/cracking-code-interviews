import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Empirically verifies Little's Law (L = lambda * W) against a real, running
 * bounded worker pool under steady-state (well-below-capacity) load.
 *
 * <p>L  = time-average number of requests in the system (queued + being served)
 * <p>lambda = real measured throughput (completions per second)
 * <p>W  = real measured average time-in-system per request (seconds)
 */
public final class LittlesLawDemo {

    private static final int POOL_SIZE = 6;
    private static final long SERVICE_TIME_MILLIS = 30;
    private static final double OFFERED_RATE_PER_SEC = 100.0;
    private static final long WARMUP_MILLIS = 1_000;
    private static final long MEASURE_MILLIS = 8_000;
    private static final long SAMPLE_INTERVAL_MILLIS = 2;

    public static void main(String[] args) throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(POOL_SIZE);
        ScheduledExecutorService arrivalGenerator = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService sampler = Executors.newSingleThreadScheduledExecutor();

        AtomicInteger inFlight = new AtomicInteger(0);
        AtomicLong completedInWindow = new AtomicLong(0);
        List<Long> latenciesNanosInWindow = new CopyOnWriteArrayList<>();
        List<Integer> inFlightSamples = new CopyOnWriteArrayList<>();

        long startNanos = System.nanoTime();
        long measureStartNanos = startNanos + TimeUnit.MILLISECONDS.toNanos(WARMUP_MILLIS);
        long measureEndNanos = measureStartNanos + TimeUnit.MILLISECONDS.toNanos(MEASURE_MILLIS);

        long periodNanos = (long) (1_000_000_000.0 / OFFERED_RATE_PER_SEC);
        arrivalGenerator.scheduleAtFixedRate(() -> {
            long submitNanos = System.nanoTime();
            inFlight.incrementAndGet();
            workers.submit(() -> {
                try {
                    Thread.sleep(SERVICE_TIME_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    inFlight.decrementAndGet();
                    long completeNanos = System.nanoTime();
                    if (submitNanos >= measureStartNanos && completeNanos <= measureEndNanos) {
                        completedInWindow.incrementAndGet();
                        latenciesNanosInWindow.add(completeNanos - submitNanos);
                    }
                }
            });
        }, 0, periodNanos, TimeUnit.NANOSECONDS);

        sampler.scheduleAtFixedRate(() -> {
            long now = System.nanoTime();
            if (now >= measureStartNanos && now <= measureEndNanos) {
                inFlightSamples.add(inFlight.get());
            }
        }, 0, SAMPLE_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);

        Thread.sleep(WARMUP_MILLIS + MEASURE_MILLIS + 500);

        arrivalGenerator.shutdownNow();
        sampler.shutdownNow();
        workers.shutdown();
        workers.awaitTermination(5, TimeUnit.SECONDS);

        double measuredWindowSeconds = MEASURE_MILLIS / 1000.0;
        double lambdaMeasured = completedInWindow.get() / measuredWindowSeconds;

        double wSecondsMeasured = latenciesNanosInWindow.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0) / 1_000_000_000.0;

        double lMeasured = inFlightSamples.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        double lPredictedByLittlesLaw = lambdaMeasured * wSecondsMeasured;

        System.out.println("=== Real, measured steady-state numbers ===");
        System.out.printf("Pool size (max concurrent servers):     %d%n", POOL_SIZE);
        System.out.printf("Real fixed service time per request:    %d ms%n", SERVICE_TIME_MILLIS);
        System.out.printf("Offered arrival rate (target):           %.1f req/s%n", OFFERED_RATE_PER_SEC);
        System.out.printf("Completed requests in measured window:   %d%n", completedInWindow.get());
        System.out.printf("lambda (measured throughput):            %.2f req/s%n", lambdaMeasured);
        System.out.printf("W (measured avg time-in-system):         %.4f s (%.1f ms)%n",
                wSecondsMeasured, wSecondsMeasured * 1000);
        System.out.printf("L (measured avg number in system, sampled every %dms): %.3f%n",
                SAMPLE_INTERVAL_MILLIS, lMeasured);
        System.out.println();
        System.out.println("=== Little's Law check: L =?= lambda * W ===");
        System.out.printf("lambda * W (predicted L):                %.3f%n", lPredictedByLittlesLaw);
        System.out.printf("L (directly measured):                   %.3f%n", lMeasured);
        double percentError = Math.abs(lMeasured - lPredictedByLittlesLaw) / lPredictedByLittlesLaw * 100;
        System.out.printf("Relative error:                          %.1f%%%n", percentError);
        System.out.println(percentError < 15
                ? "Little's Law holds: two independently measured quantities (L via sampling, "
                        + "lambda*W via throughput and latency) agree within measurement noise."
                : "Relative error exceeds the demo's tolerance band -- see README for interpretation.");
    }
}
