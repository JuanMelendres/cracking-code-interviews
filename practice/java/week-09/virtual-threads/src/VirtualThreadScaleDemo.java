import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-410 -- what actually changes for IO-bound workloads under virtual
 * threads: the same 5,000 blocking-sleep "requests" run through a
 * virtual-thread-per-task executor versus a 200-platform-thread pool,
 * wall-clock timed.
 */
public class VirtualThreadScaleDemo {
    static final int TASKS = 5000;
    static final int BLOCK_MS = 50;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== 200 platform threads, 5000 blocking 50ms tasks ==");
        runWith(Executors.newFixedThreadPool(200), "platform pool (200 threads)");

        System.out.println();
        System.out.println("== virtual-thread-per-task executor, same 5000 blocking 50ms tasks ==");
        runWith(Executors.newVirtualThreadPerTaskExecutor(), "virtual threads (one per task)");
    }

    static void runWith(ExecutorService pool, String label) throws InterruptedException {
        AtomicInteger completed = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(TASKS);
        long start = System.nanoTime();
        for (int i = 0; i < TASKS; i++) {
            pool.execute(() -> {
                try { Thread.sleep(BLOCK_MS); } catch (InterruptedException ignored) { }
                completed.incrementAndGet();
                done.countDown();
            });
        }
        done.await();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        pool.shutdown();
        System.out.printf("%s: %d tasks completed in %dms (theoretical minimum if fully parallel: %dms)%n",
                label, completed.get(), elapsedMs, BLOCK_MS);
    }
}
