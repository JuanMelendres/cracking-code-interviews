import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-515 -- exponential backoff with jitter, real delay sequence printed,
 * plus the specific failure mode it exists to prevent: a "retry storm"
 * where every client backs off on the exact same schedule and all retry
 * in lockstep, re-overwhelming the recovering downstream at the same
 * instant instead of spreading load out.
 */
public class RetryBackoffJitterDemo {
    public static void main(String[] args) {
        System.out.println("== exponential backoff WITHOUT jitter: every failing client retries at the identical instant ==");
        simulateClients(5, false);

        System.out.println();
        System.out.println("== exponential backoff WITH full jitter: retries spread out ==");
        simulateClients(5, true);
    }

    static void simulateClients(int clientCount, boolean useJitter) {
        Random random = new Random(42);
        long baseMs = 100;
        int maxAttempts = 4;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            long capMs = baseMs * (1L << (attempt - 1)); // exponential: 100, 200, 400, 800
            StringBuilder delays = new StringBuilder();
            for (int client = 0; client < clientCount; client++) {
                long delay = useJitter ? (long) (random.nextDouble() * capMs) : capMs;
                delays.append(delay).append("ms ");
            }
            System.out.printf("attempt %d (exponential cap=%dms): client delays = %s%n", attempt, capMs, delays);
        }
    }
}
