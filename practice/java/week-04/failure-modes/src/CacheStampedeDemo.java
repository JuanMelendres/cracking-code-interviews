import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real, measured cache-stampede (thundering herd) reproduction and fix.
 *
 * Scenario: a cache entry just expired. 50 concurrent requests for the same
 * key arrive at (almost) the same instant. The naive cache-aside pattern
 * checks the cache, misses, and falls through to the database independently
 * for every single request -- because there is no coordination between the
 * concurrent misses. The fix (single-flight / request coalescing) makes only
 * the FIRST request actually load from the database; every other concurrent
 * request for the same key waits on that first request's in-flight result
 * instead of issuing its own database call.
 */
public class CacheStampedeDemo {

    static final int CONCURRENT_REQUESTS = 50;
    static final long DB_LOAD_MILLIS = 300;

    public static void main(String[] args) throws Exception {
        System.out.println(CONCURRENT_REQUESTS + " concurrent requests for the same just-expired cache key.\n");

        System.out.println("--- Naive cache-aside (no coordination) ---");
        runNaive();

        System.out.println("\n--- Single-flight (request coalescing) fix ---");
        runSingleFlight();
    }

    private static void runNaive() throws InterruptedException {
        ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
        AtomicInteger dbLoadCount = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(CONCURRENT_REQUESTS);

        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    String value = cache.get("hot-key");
                    if (value == null) {
                        // MISS -- every thread that misses independently loads from the DB.
                        // There is no coordination, so all 50 threads can miss simultaneously.
                        value = loadFromDatabase(dbLoadCount);
                        cache.put("hot-key", value);
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        long start = System.currentTimeMillis();
        startGate.countDown(); // release all 50 threads at (almost) the same instant
        doneLatch.await();
        long elapsed = System.currentTimeMillis() - start;
        pool.shutdown();

        System.out.println("Database load calls made: " + dbLoadCount.get() + " (should be 1 with coordination)");
        System.out.println("Elapsed: " + elapsed + "ms");
    }

    private static void runSingleFlight() throws InterruptedException {
        ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, CompletableFuture<String>> inFlight = new ConcurrentHashMap<>();
        AtomicInteger dbLoadCount = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(CONCURRENT_REQUESTS);

        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    String value = cache.get("hot-key");
                    if (value == null) {
                        // Single-flight: only the thread that successfully creates the
                        // CompletableFuture actually loads from the database. Every other
                        // concurrent thread gets the SAME in-flight future and just waits
                        // on it -- no extra database calls.
                        CompletableFuture<String> future = new CompletableFuture<>();
                        CompletableFuture<String> existing = inFlight.putIfAbsent("hot-key", future);
                        if (existing == null) {
                            String loaded = loadFromDatabase(dbLoadCount);
                            cache.put("hot-key", loaded);
                            future.complete(loaded);
                            inFlight.remove("hot-key");
                        } else {
                            existing.join(); // wait for the in-flight load, no DB call of our own
                        }
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        long start = System.currentTimeMillis();
        startGate.countDown();
        doneLatch.await();
        long elapsed = System.currentTimeMillis() - start;
        pool.shutdown();

        System.out.println("Database load calls made: " + dbLoadCount.get() + " (coordination working as intended)");
        System.out.println("Elapsed: " + elapsed + "ms");
    }

    private static String loadFromDatabase(AtomicInteger counter) {
        counter.incrementAndGet();
        try {
            Thread.sleep(DB_LOAD_MILLIS);
        } catch (InterruptedException ignored) {
        }
        return "value-loaded-from-db";
    }
}
