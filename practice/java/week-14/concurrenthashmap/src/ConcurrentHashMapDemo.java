import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHashMapDemo {

    static final int THREADS = 8;
    static final int PUTS_PER_THREAD = 20_000;

    public static void main(String[] args) throws Exception {
        System.out.println("== A plain HashMap under real concurrent writes: corrupted, not just \"unsafe in theory\" ==");
        Map<Integer, Integer> plainMap = new HashMap<>();
        runConcurrentPuts(plainMap);
        System.out.println("Expected size: " + (THREADS * PUTS_PER_THREAD) + ", actual size: " + plainMap.size()
                + (plainMap.size() != THREADS * PUTS_PER_THREAD
                    ? "  <-- CORRUPTED (lost entries from concurrent structural modification, no exception thrown)"
                    : ""));

        System.out.println();
        System.out.println("== The correct, thread-safe replacement: ConcurrentHashMap ==");
        Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();
        runConcurrentPuts(concurrentMap);
        System.out.println("Expected size: " + (THREADS * PUTS_PER_THREAD) + ", actual size: " + concurrentMap.size()
                + "  (always correct -- ConcurrentHashMap's per-bucket locking makes individual put() calls safe)");

        System.out.println();
        System.out.println("== But ConcurrentHashMap's per-operation safety does NOT make get-then-put atomic ==");
        ConcurrentHashMap<String, Integer> counterMap = new ConcurrentHashMap<>();
        counterMap.put("hits", 0);
        runNaiveIncrement(counterMap);
        int expected = THREADS * PUTS_PER_THREAD;
        System.out.println("Expected \"hits\" count: " + expected + ", actual: " + counterMap.get("hits")
                + "  (LOST UPDATES -- get() then put() is two separate operations; the map is thread-safe"
                + " per-call, but the read-modify-write sequence across two calls is not atomic)");

        System.out.println();
        System.out.println("== The correct fix: merge()/compute() perform the read-modify-write atomically ==");
        ConcurrentHashMap<String, Integer> correctCounterMap = new ConcurrentHashMap<>();
        correctCounterMap.put("hits", 0);
        runAtomicIncrement(correctCounterMap);
        System.out.println("Expected \"hits\" count: " + expected + ", actual: " + correctCounterMap.get("hits")
                + "  (correct -- merge() performs the whole read-modify-write under one atomic per-key operation)");
    }

    static void runConcurrentPuts(Map<Integer, Integer> map) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        for (int t = 0; t < THREADS; t++) {
            int threadId = t;
            executor.submit(() -> {
                for (int i = 0; i < PUTS_PER_THREAD; i++) {
                    map.put(threadId * PUTS_PER_THREAD + i, i);
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }

    static void runNaiveIncrement(ConcurrentHashMap<String, Integer> map) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < PUTS_PER_THREAD; i++) {
                    int current = map.get("hits");   // read
                    map.put("hits", current + 1);     // modify-write -- NOT atomic with the read above
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }

    static void runAtomicIncrement(ConcurrentHashMap<String, Integer> map) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < PUTS_PER_THREAD; i++) {
                    map.merge("hits", 1, Integer::sum); // atomic read-modify-write for this key
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();
    }
}
