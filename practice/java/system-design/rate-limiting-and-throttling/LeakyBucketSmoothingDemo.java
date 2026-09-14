import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Real demonstration of leaky-bucket smoothing. 30 requests arrive in a single burst
 * (near-simultaneous submission), capacity is 30 (so none are rejected), and the
 * leak rate is 10/s. The measured processing timestamps should show a real, roughly
 * constant 100ms spacing between completions -- the burst absorbed and smoothed into
 * steady output -- not a burst of near-simultaneous completions.
 */
public final class LeakyBucketSmoothingDemo {

    public static void main(String[] args) throws InterruptedException {
        int burstSize = 30;
        double leakPerSecond = 10.0;
        LeakyBucket bucket = new LeakyBucket(burstSize, leakPerSecond);

        long startNanos = System.nanoTime();
        AtomicLong[] completedAtMillis = new AtomicLong[burstSize];
        CountDownLatch done = new CountDownLatch(burstSize);

        System.out.println("=== Leaky-bucket smoothing: burst=" + burstSize
                + " requests, capacity=" + burstSize + ", leak=" + leakPerSecond + "/s ===");
        System.out.println();

        for (int i = 0; i < burstSize; i++) {
            final int index = i;
            completedAtMillis[i] = new AtomicLong(-1);
            boolean accepted = bucket.offer(() -> {
                completedAtMillis[index].set((System.nanoTime() - startNanos) / 1_000_000);
                done.countDown();
            });
            if (!accepted) {
                System.out.println("Request " + i + " REJECTED at submit time (bucket full)");
                done.countDown();
            }
        }

        done.await();
        bucket.shutdown();

        long previous = 0;
        long totalGap = 0;
        int gapCount = 0;
        for (int i = 0; i < burstSize; i++) {
            long t = completedAtMillis[i].get();
            long gap = i == 0 ? 0 : t - previous;
            if (i > 0) {
                totalGap += gap;
                gapCount++;
            }
            System.out.printf("request[%2d] completed at t+%4dms (gap from previous: %4dms)%n", i, t, gap);
            previous = t;
        }

        double averageGap = gapCount == 0 ? 0 : (double) totalGap / gapCount;
        System.out.println();
        System.out.printf("Average gap between completions: %.1fms (expected ~%.1fms at %.1f/s)%n",
                averageGap, 1000.0 / leakPerSecond, leakPerSecond);
    }
}
