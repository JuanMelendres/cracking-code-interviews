import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * T-410 -- pinning: a virtual thread blocking on I/O INSIDE a
 * `synchronized` block pins its carrier platform thread instead of
 * unmounting, so a small carrier pool (here forced to 2 via
 * -Djdk.virtualThreadScheduler.parallelism=2) gets starved. The same
 * blocking call under a ReentrantLock does NOT pin -- measured, not
 * asserted. Run with:
 *   java -Djdk.virtualThreadScheduler.parallelism=2 -cp out VirtualThreadPinningDemo
 */
public class VirtualThreadPinningDemo {
    static final int TASKS = 20;
    static final int BLOCK_MS = 200;
    // each task locks its OWN object -- the only shared constraint is the
    // carrier pool, not artificial contention from one shared mutex

    public static void main(String[] args) throws InterruptedException {
        System.out.println("carrier parallelism = " + System.getProperty("jdk.virtualThreadScheduler.parallelism", "(default, not overridden)"));

        System.out.println();
        System.out.println("== blocking INSIDE synchronized -- pins the carrier thread ==");
        run(true);

        System.out.println();
        System.out.println("== blocking INSIDE a ReentrantLock -- does NOT pin ==");
        run(false);
    }

    static void run(boolean useSynchronized) throws InterruptedException {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch done = new CountDownLatch(TASKS);
        long start = System.nanoTime();
        for (int i = 0; i < TASKS; i++) {
            Object ownMonitor = new Object();
            ReentrantLock ownLock = new ReentrantLock();
            pool.execute(() -> {
                if (useSynchronized) {
                    synchronized (ownMonitor) {
                        sleep(BLOCK_MS);
                    }
                } else {
                    ownLock.lock();
                    try {
                        sleep(BLOCK_MS);
                    } finally {
                        ownLock.unlock();
                    }
                }
                done.countDown();
            });
        }
        done.await();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        pool.shutdown();
        System.out.printf("%d tasks x %dms blocking each, %s: %dms wall time (unpinned lower bound with a "
                        + "small carrier pool is roughly (tasks/carriers)*blockMs)%n",
                TASKS, BLOCK_MS, useSynchronized ? "synchronized (pins)" : "ReentrantLock (no pin)", elapsedMs);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
    }
}
