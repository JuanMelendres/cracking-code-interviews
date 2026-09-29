package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

/**
 * Why identical code is fast in development and slow in production.
 *
 * <p>No Spring, no database — a deliberately simple stand-in whose only cost is
 * a real per-call round-trip latency and a real bounded pool, because the point
 * is the SHAPE of the two curves, not any particular database's speed. Nothing
 * about the code under test changes between the dev and production runs; only
 * the data volume and the concurrency do.
 */
public final class LoadProfileDemo {

    /** Stands in for a database: a bounded pool plus a real per-query latency. */
    static final class FakeDatabase {
        private final Semaphore pool;

        FakeDatabase(int poolSize) {
            this.pool = new Semaphore(poolSize);
        }

        /** One round trip. Acquires a connection, waits, releases it. */
        void query() {
            try {
                pool.acquire();
                try {
                    LockSupport.parkNanos(200_000);  // ~0.2 ms of "network + server time"
                } finally {
                    pool.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
    }

    /** The N+1 shape: one query for the list, then one per row. */
    static void loadOrdersNaive(FakeDatabase db, int rows) {
        db.query();
        for (int i = 0; i < rows; i++) {
            db.query();
        }
    }

    /** The fixed shape: one query for the list, one for every row's children. */
    static void loadOrdersBatched(FakeDatabase db, int rows) {
        db.query();
        db.query();
    }

    static long microsOf(Runnable work) {
        long start = System.nanoTime();
        work.run();
        return (System.nanoTime() - start) / 1_000;
    }

    public static void main(String[] args) throws Exception {
        FakeDatabase db = new FakeDatabase(10);

        // Warm up the JIT and the parking machinery so the first timed run is
        // not measuring class loading.
        for (int i = 0; i < 200; i++) {
            db.query();
        }

        long singleQueryMicros = 0;
        for (int i = 0; i < 100; i++) {
            long start = System.nanoTime();
            db.query();
            singleQueryMicros += (System.nanoTime() - start) / 1000;
        }
        singleQueryMicros /= 100;

        System.out.println("== Measured baseline ==");
        System.out.printf("One uncontended round trip: %d microseconds (real, measured on this machine)%n",
                singleQueryMicros);
        System.out.println();

        System.out.println("== Scenario A: identical code, different data volume ==");
        System.out.printf("%-10s %16s %16s %10s%n", "rows", "naive (N+1)", "batched", "ratio");
        for (int rows : new int[] {10, 100, 1000, 5000}) {
            long naive = microsOf(() -> loadOrdersNaive(db, rows));
            long batched = microsOf(() -> loadOrdersBatched(db, rows));
            System.out.printf("%-10d %13d us %13d us %9.0fx%n",
                    rows, naive, batched, (double) naive / batched);
        }
        System.out.println();
        System.out.println("The naive version is linear in row count; the batched version is flat.");
        System.out.println("A dev database seeded with 10 rows cannot distinguish them.");
        System.out.println();

        System.out.println("== Scenario B: identical code and data, different concurrency ==");
        System.out.println("Connection pool size: 10. Each request makes 5 round trips.");
        System.out.printf("%-12s %12s %12s %12s %14s%n",
                "concurrency", "p50", "p95", "p99", "throughput");
        for (int concurrency : new int[] {1, 5, 10, 50, 200}) {
            long wallStart = System.nanoTime();
            long[] samples = runConcurrent(db, concurrency, 20);
            double wallSeconds = (System.nanoTime() - wallStart) / 1_000_000_000.0;
            java.util.Arrays.sort(samples);
            long p50 = samples[(int) (samples.length * 0.50)];
            long p95 = samples[Math.min(samples.length - 1, (int) (samples.length * 0.95))];
            long p99 = samples[Math.min(samples.length - 1, (int) (samples.length * 0.99))];
            System.out.printf("%-12d %9d ms %9d ms %9d ms %8.0f req/s%n",
                    concurrency, p50, p95, p99, samples.length / wallSeconds);
        }
        System.out.println();
        System.out.println("Throughput stops climbing once concurrency passes the pool size: the pool,");
        System.out.println("not the code, is the ceiling. Note WHERE the pain shows up -- the median");
        System.out.println("barely moves while the tail explodes, because an unfair semaphore lets some");
        System.out.println("waiters be overtaken repeatedly. A dashboard showing average latency would");
        System.out.println("report this system as healthy.");
    }

    static long[] runConcurrent(FakeDatabase db, int concurrency, int requestsPerThread) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(concurrency);
        CountDownLatch startGate = new CountDownLatch(1);
        List<java.util.concurrent.Future<long[]>> futures = new ArrayList<>();

        for (int t = 0; t < concurrency; t++) {
            futures.add(pool.submit(() -> {
                startGate.await();
                long[] mine = new long[requestsPerThread];
                for (int i = 0; i < requestsPerThread; i++) {
                    long start = System.nanoTime();
                    for (int q = 0; q < 5; q++) {
                        db.query();
                    }
                    mine[i] = (System.nanoTime() - start) / 1_000_000;
                }
                return mine;
            }));
        }

        startGate.countDown();
        List<Long> all = new ArrayList<>();
        for (var f : futures) {
            for (long v : f.get()) {
                all.add(v);
            }
        }
        pool.shutdown();
        return all.stream().mapToLong(Long::longValue).toArray();
    }
}
