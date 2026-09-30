import java.util.concurrent.atomic.AtomicInteger;

/**
 * Part 4: the three correct options are not equally cheap. Measured on the same
 * workload so the numbers are comparable, after a warmup so the JIT is not what
 * is being measured.
 */
final class CostDemo {

    static final int THREADS = 8;
    static final int INCREMENTS = 200_000;

    static int guarded;
    static final Object LOCK = new Object();
    static final AtomicInteger ATOMIC = new AtomicInteger();

    static long timeOf(Runnable increment) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int n = 0; n < INCREMENTS; n++) {
                    increment.run();
                }
            });
        }
        long start = System.nanoTime();
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    static void run() throws InterruptedException {
        // Warm up so the JIT has compiled both paths before anything is timed.
        for (int i = 0; i < 3; i++) {
            guarded = 0;
            timeOf(() -> { synchronized (LOCK) { guarded++; } });
            ATOMIC.set(0);
            timeOf(ATOMIC::incrementAndGet);
        }

        System.out.println("== 6. The correct options are not equally cheap ==");
        System.out.printf("  %d threads x %,d increments, contended%n", THREADS, INCREMENTS);

        guarded = 0;
        long syncMs = timeOf(() -> { synchronized (LOCK) { guarded++; } });
        ATOMIC.set(0);
        long atomicMs = timeOf(ATOMIC::incrementAndGet);

        System.out.printf("  synchronized block            : %,5d ms  (result %,d)%n", syncMs, guarded);
        System.out.printf("  AtomicInteger                 : %,5d ms  (result %,d)%n", atomicMs, ATOMIC.get());
        System.out.println();
        System.out.println("  Both are correct. AtomicInteger is a single compare-and-swap on one");
        System.out.println("  variable; synchronized takes a lock and can protect several fields at");
        System.out.println("  once. Pick by what needs to be atomic TOGETHER, not by the timing --");
        System.out.println("  a lock protecting two related fields is not replaceable by two atomics.");
    }
}
