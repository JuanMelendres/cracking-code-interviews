import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Real, measured proof that the DEFAULT (unfair) ReentrantLock allows
 * "barging" -- a thread that's already running can re-acquire the lock
 * ahead of threads that have been queued and waiting longer -- while a
 * fair ReentrantLock(true) enforces a much more even, roughly-FIFO
 * distribution of acquisitions, at a real measured throughput cost.
 */
public class FairnessAndBargingDemo {

    static final int THREAD_COUNT = 4;
    static final int TOTAL_ACQUISITIONS = 40_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Unfair (default) ReentrantLock: acquisitions per thread ==");
        long unfairElapsed = runAndMeasure(new ReentrantLock());

        System.out.println("\n== Fair ReentrantLock(true): acquisitions per thread ==");
        long fairElapsed = runAndMeasure(new ReentrantLock(true));

        System.out.println("\n== Real measured wall-clock time ==");
        System.out.println("Unfair: " + unfairElapsed + "ms");
        System.out.println("Fair:   " + fairElapsed + "ms");
    }

    static long runAndMeasure(ReentrantLock lock) throws InterruptedException {
        AtomicIntegerArray perThreadCounts = new AtomicIntegerArray(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                while (true) {
                    lock.lock();
                    try {
                        int total = 0;
                        for (int t = 0; t < THREAD_COUNT; t++) total += perThreadCounts.get(t);
                        if (total >= TOTAL_ACQUISITIONS) return;
                        perThreadCounts.incrementAndGet(threadIndex);
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        long start = System.currentTimeMillis();
        for (Thread t : threads) t.start();
        startLatch.countDown();
        for (Thread t : threads) t.join();
        long elapsed = System.currentTimeMillis() - start;

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int i = 0; i < THREAD_COUNT; i++) {
            int count = perThreadCounts.get(i);
            System.out.println("  thread-" + i + ": " + count + " acquisitions");
            min = Math.min(min, count);
            max = Math.max(max, count);
        }
        System.out.println("  spread (max - min): " + (max - min) + " out of " + (TOTAL_ACQUISITIONS / THREAD_COUNT) + " expected-even share");
        return elapsed;
    }
}
