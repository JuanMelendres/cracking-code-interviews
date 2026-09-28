import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Concurrency and parallelism are different properties, and the difference is
 * measurable: the same thread count helps enormously for one workload shape and
 * not at all for the other.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/ConcurrencyVsParallelismDemo.java
 *   java -cp out ConcurrencyVsParallelismDemo
 */
public class ConcurrencyVsParallelismDemo {

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        System.out.println("availableProcessors() = " + CORES);

        section("1. CPU-BOUND work: adding threads helps until you run out of cores");
        cpuBound();

        section("2. I/O-BOUND work: adding threads helps far beyond the core count");
        ioBound();

        section("3. Amdahl's law: the serial fraction sets the ceiling");
        amdahl();

        section("4. Concurrency without parallelism: one thread, interleaved progress");
        singleThreadedConcurrency();
    }

    // ------------------------------------------------ 1

    /** Deliberately CPU-only: no allocation, no I/O, no sleeping. */
    private static long burn(long iterations) {
        long acc = 0;
        for (long i = 0; i < iterations; i++) {
            acc += (i * 2654435761L) ^ (acc >>> 7);
        }
        return acc;
    }

    private static void cpuBound() throws Exception {
        int tasks = 40;
        long perTask = 20_000_000L;

        System.out.println("  40 CPU-bound tasks, no I/O whatsoever:");
        for (int threads : new int[]{1, 2, 4, CORES, CORES * 4}) {
            long millis = runTasks(threads, tasks, () -> burn(perTask));
            System.out.printf("    %3d threads -> %6d ms%s%n", threads, millis,
                    threads == CORES ? "   <- one per core" : threads > CORES ? "   <- MORE threads, no better" : "");
        }
        System.out.println("  Speedup tracks core count and then flattens. Extra threads beyond the");
        System.out.println("  core count add context switching, not throughput -- there is no idle CPU");
        System.out.println("  left for them to use. This is PARALLELISM: doing work simultaneously,");
        System.out.println("  and it is bounded by hardware.");
    }

    // ------------------------------------------------ 2

    private static void ioBound() throws Exception {
        int tasks = 200;
        Duration latency = Duration.ofMillis(50);

        System.out.println("  200 tasks that each wait 50 ms (a stand-in for a network call):");
        for (int threads : new int[]{1, CORES, 50, 200}) {
            long millis = runTasks(threads, tasks, () -> {
                Thread.sleep(latency.toMillis());
                return 1L;
            });
            System.out.printf("    %3d threads -> %6d ms%s%n", threads, millis,
                    threads == CORES ? "   <- one per core" : threads == tasks ? "   <- one per task" : "");
        }
        System.out.println("  Here threads help far past the core count, because a waiting thread uses");
        System.out.println("  no CPU at all. This is CONCURRENCY: many tasks in flight, overlapping their");
        System.out.println("  waiting. Total time approaches the latency of ONE call, not their sum.");
        System.out.println("  The same 200 tasks with virtual threads:");
        long virtualMillis = runVirtual(tasks, latency);
        System.out.printf("    virtual threads -> %6d ms   (one per task, no pool sizing decision)%n",
                virtualMillis);
    }

    private static long runVirtual(int tasks, Duration latency) throws Exception {
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Long>> futures = new ArrayList<>();
            for (int i = 0; i < tasks; i++) {
                futures.add(executor.submit(() -> {
                    Thread.sleep(latency.toMillis());
                    return 1L;
                }));
            }
            for (Future<Long> f : futures) {
                f.get();
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    // ------------------------------------------------ 3

    private static void amdahl() throws Exception {
        long serialWork = 30_000_000L;
        long parallelWorkPerTask = 20_000_000L;
        int tasks = 40;

        long oneThread = time(() -> {
            burn(serialWork);
            for (int i = 0; i < tasks; i++) {
                burn(parallelWorkPerTask);
            }
        });

        long manyThreads = time(() -> {
            burn(serialWork);                                    // this part CANNOT be shared out
            try {
                runTasks(CORES, tasks, () -> burn(parallelWorkPerTask));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.printf("  serial phase + 40 parallel tasks%n");
        System.out.printf("    1 thread       -> %6d ms%n", oneThread);
        System.out.printf("    %d threads     -> %6d ms%n", CORES, manyThreads);
        System.out.printf("    speedup: %.2fx with %d cores%n", (double) oneThread / manyThreads, CORES);
        System.out.println("  The speedup is well below the core count because the serial phase is");
        System.out.println("  unchanged no matter how many cores you add. That ceiling is Amdahl's law,");
        System.out.println("  and it is why 'just add threads' stops working long before it should.");
    }

    // ------------------------------------------------ 4

    private static void singleThreadedConcurrency() {
        System.out.println("  Two tasks interleaved by ONE thread, cooperatively:");
        List<String> log = new ArrayList<>();
        int aRemaining = 3;
        int bRemaining = 3;
        int step = 0;
        while (aRemaining > 0 || bRemaining > 0) {
            if (aRemaining > 0) {
                log.add("step " + (step++) + ": task A slice (" + (--aRemaining) + " left)");
            }
            if (bRemaining > 0) {
                log.add("step " + (step++) + ": task B slice (" + (--bRemaining) + " left)");
            }
        }
        log.forEach(line -> System.out.println("    " + line));
        System.out.println("  Both tasks were in progress at the same time; neither ran simultaneously.");
        System.out.println("  That is concurrency with zero parallelism -- exactly what a single-threaded");
        System.out.println("  event loop does, and why Node.js handles many connections on one thread.");
    }

    // ------------------------------------------------ helpers

    private static long runTasks(int threads, int taskCount, Callable<Long> task) throws Exception {
        long start = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Long>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(pool.submit(task));
            }
            for (Future<Long> f : futures) {
                f.get();
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static long time(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
