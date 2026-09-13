import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real demonstration of the check-then-increment race that a naive, non-synchronized
 * rate limiter has under concurrent access -- the actual bug behind "our rate limiter
 * let through way more than the configured limit under load" incidents. A single
 * shared window is hammered by many real threads simultaneously; the naive limiter's
 * plain, unsynchronized `if (count < limit) count++` is compared against the
 * production {@link FixedWindowCounter}, which guards the same sequence with
 * `synchronized`.
 */
public final class ConcurrencyRaceDemo {

    private static final int LIMIT = 100;
    private static final int THREADS = 64;
    private static final int ATTEMPTS_PER_THREAD = 50;

    static final class NaiveCounter {
        private final int limit;
        private int count;

        NaiveCounter(int limit) {
            this.limit = limit;
        }

        boolean tryAcquire() {
            if (count < limit) {
                count++;
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Concurrency race: limit=" + LIMIT + ", " + THREADS
                + " threads x " + ATTEMPTS_PER_THREAD + " attempts each (" + (THREADS * ATTEMPTS_PER_THREAD)
                + " total attempts) ===");
        System.out.println();

        int naiveAdmitted = runTrial("NaiveCounter (unsynchronized)", () -> {
            NaiveCounter counter = new NaiveCounter(LIMIT);
            return counter::tryAcquire;
        });

        int correctAdmitted = runTrial("FixedWindowCounter (synchronized)", () -> {
            FixedWindowCounter counter = new FixedWindowCounter(LIMIT, 60_000);
            return counter::tryAcquire;
        });

        System.out.println();
        System.out.println("Nominal limit: " + LIMIT);
        System.out.println("NaiveCounter admitted:       " + naiveAdmitted
                + (naiveAdmitted > LIMIT ? "  <-- OVERSHOOT, real race" : ""));
        System.out.println("FixedWindowCounter admitted: " + correctAdmitted
                + (correctAdmitted == LIMIT ? "  <-- exact, no overshoot" : ""));
    }

    private interface Acquirer {
        boolean tryAcquire();
    }

    private static int runTrial(String name, java.util.function.Supplier<Acquirer> factory) throws InterruptedException {
        Acquirer limiter = factory.get();
        AtomicInteger admitted = new AtomicInteger(0);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(THREADS);

        for (int t = 0; t < THREADS; t++) {
            Thread thread = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int i = 0; i < ATTEMPTS_PER_THREAD; i++) {
                    if (limiter.tryAcquire()) {
                        admitted.incrementAndGet();
                    }
                }
                doneGate.countDown();
            });
            thread.start();
        }

        startGate.countDown();
        doneGate.await();

        System.out.println(name + ": admitted=" + admitted.get());
        return admitted.get();
    }
}
