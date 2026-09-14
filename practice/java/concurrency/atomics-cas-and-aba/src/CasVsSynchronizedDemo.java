import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real, measured comparison of a CAS retry-loop counter (AtomicInteger)
 * against a synchronized counter, under real contention from multiple
 * threads. Both must produce the exact correct final count; the timing
 * difference is the real, measured cost of lock acquisition versus a
 * lock-free retry loop under this contention level.
 */
public class CasVsSynchronizedDemo {

    static final int THREADS = 8;
    static final int INCREMENTS_PER_THREAD = 500_000;
    static final int EXPECTED_TOTAL = THREADS * INCREMENTS_PER_THREAD;

    public static void main(String[] args) throws InterruptedException {
        long casElapsed = runCasCounter();
        long syncElapsed = runSynchronizedCounter();

        System.out.println("\n== Real measured wall-clock time, " + THREADS + " threads x " + INCREMENTS_PER_THREAD + " increments ==");
        System.out.println("AtomicInteger (CAS retry loop): " + casElapsed + "ms");
        System.out.println("synchronized counter:           " + syncElapsed + "ms");
    }

    static long runCasCounter() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        Thread[] threads = new Thread[THREADS];
        long start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    // incrementAndGet() is itself a CAS retry loop internally
                    // (compareAndSet, retry on contention) -- no lock taken.
                    counter.incrementAndGet();
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long elapsed = System.currentTimeMillis() - start;

        int actual = counter.get();
        System.out.println("CAS counter: expected=" + EXPECTED_TOTAL + " actual=" + actual
                + (actual == EXPECTED_TOTAL ? " (correct, no lost updates)" : " (MISMATCH)"));
        return elapsed;
    }

    static long runSynchronizedCounter() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        Thread[] threads = new Thread[THREADS];
        long start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment();
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long elapsed = System.currentTimeMillis() - start;

        int actual = counter.get();
        System.out.println("synchronized counter: expected=" + EXPECTED_TOTAL + " actual=" + actual
                + (actual == EXPECTED_TOTAL ? " (correct, no lost updates)" : " (MISMATCH)"));
        return elapsed;
    }

    static class SynchronizedCounter {
        private int value = 0;

        synchronized void increment() {
            value++;
        }

        synchronized int get() {
            return value;
        }
    }
}
