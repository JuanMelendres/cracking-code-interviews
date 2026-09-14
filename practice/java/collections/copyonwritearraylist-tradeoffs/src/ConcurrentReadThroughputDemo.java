import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Real, measured proof of CopyOnWriteArrayList's actual benefit: reads take
 * NO lock at all (they read a plain, immutable array reference), so many
 * concurrent reader threads scale cleanly. Collections.synchronizedList()
 * serializes every single read through one intrinsic lock, even when there
 * are zero writers -- measured directly with real reader threads.
 */
public class ConcurrentReadThroughputDemo {

    static final int READER_THREADS = 8;
    static final int READS_PER_THREAD = 2_000_000;
    static final int LIST_SIZE = 1_000;

    public static void main(String[] args) throws InterruptedException {
        List<Integer> seed = new java.util.ArrayList<>();
        for (int i = 0; i < LIST_SIZE; i++) seed.add(i);

        long cowElapsed = measureConcurrentReads(new CopyOnWriteArrayList<>(seed), "CopyOnWriteArrayList (lock-free reads)");
        long syncElapsed = measureConcurrentReads(Collections.synchronizedList(new java.util.ArrayList<>(seed)),
                "Collections.synchronizedList (every read takes the intrinsic lock)");

        System.out.println("\n== Real measured total wall-clock time, " + READER_THREADS + " threads x "
                + READS_PER_THREAD + " reads each, ZERO writers ==");
        System.out.println("CopyOnWriteArrayList:        " + cowElapsed + "ms");
        System.out.println("Collections.synchronizedList: " + syncElapsed + "ms");
        System.out.println("Real measured ratio: " + String.format("%.2fx", (double) syncElapsed / cowElapsed));
    }

    static long measureConcurrentReads(List<Integer> list, String label) throws InterruptedException {
        Thread[] threads = new Thread[READER_THREADS];
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicLong sink = new AtomicLong(); // prevents JIT from eliminating the reads entirely

        for (int t = 0; t < READER_THREADS; t++) {
            threads[t] = new Thread(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                long localSum = 0;
                for (int i = 0; i < READS_PER_THREAD; i++) {
                    localSum += list.get(i % LIST_SIZE);
                }
                sink.addAndGet(localSum);
            });
        }
        for (Thread th : threads) th.start();

        long start = System.currentTimeMillis();
        startLatch.countDown(); // all readers start together
        for (Thread th : threads) th.join();
        long elapsed = System.currentTimeMillis() - start;

        System.out.println(label + ": " + elapsed + "ms (sink=" + sink.get() + ", prevents dead-code elimination)");
        return elapsed;
    }
}
