import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-406 -- the unbounded-queue trap in Executors.newFixedThreadPool, and
 * what a bounded queue + rejection policy actually does about it, both
 * measured against a producer that outpaces the workers.
 */
public class ExecutorSizingDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== newFixedThreadPool(2): backed by an UNBOUNDED LinkedBlockingQueue ==");
        unboundedTrap();

        System.out.println();
        System.out.println("== ThreadPoolExecutor with a BOUNDED queue + AbortPolicy: backpressure, not silent growth ==");
        boundedWithRejection();
    }

    static void unboundedTrap() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) pool;
        AtomicInteger completed = new AtomicInteger();

        // 2 slow workers (100ms/task), producer submits 500 tasks essentially instantly
        for (int i = 0; i < 500; i++) {
            pool.execute(() -> {
                sleep(100);
                completed.incrementAndGet();
            });
        }
        Thread.sleep(200); // snapshot shortly after submission -- workers have barely made a dent
        System.out.printf("200ms after submitting 500 tasks to a 2-thread pool: queue size=%d, completed=%d, "
                        + "active=%d (every unstarted task sits in memory in the unbounded queue)%n",
                tpe.getQueue().size(), completed.get(), tpe.getActiveCount());

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.printf("after full drain: completed=%d (all 500 eventually ran -- unbounded means no rejection, "
                + "just unbounded memory growth under sustained overload)%n", completed.get());
    }

    static void boundedWithRejection() throws InterruptedException {
        int queueCapacity = 5;
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(
                2, 2, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy());

        int accepted = 0, rejected = 0;
        for (int i = 0; i < 20; i++) {
            try {
                tpe.execute(() -> sleep(200));
                accepted++;
            } catch (RejectedExecutionException e) {
                rejected++;
            }
        }
        System.out.printf("submitted 20 tasks to a 2-thread pool with a %d-slot bounded queue: accepted=%d rejected=%d%n",
                queueCapacity, accepted, rejected);
        System.out.println("(2 running + 5 queued = 7 can be accepted immediately; the rest are rejected loudly "
                + "instead of silently piling up)");

        tpe.shutdown();
        tpe.awaitTermination(10, TimeUnit.SECONDS);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
    }
}
