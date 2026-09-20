import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/** Real, measured false sharing: four threads, each hammering its own
 * counter with no shared logical state at all -- yet when the four
 * counters sit on the same 64-byte CPU cache line, every atomic update
 * from one core invalidates the other cores' cached copy of that line,
 * forcing real cross-core cache-coherence traffic for data the threads
 * never actually share. A primitive {@code long[]} array is used (not
 * an array of {@code AtomicLong} objects -- object *references* in an
 * array don't control where the referenced objects themselves land in
 * memory, only a primitive array's own elements are laid out inline
 * and contiguous, which is what this demo needs to control). Spacing
 * each real counter 64 bytes (8 longs) apart provably puts them on
 * different cache lines: if counter[i] is in a line starting at address
 * A (A &lt;= addr &lt; A+64), counter[i+8] at addr+64 is always &gt;= A+64,
 * hence in a later line -- true regardless of the array's own absolute
 * alignment. */
public class FalseSharingDemo {

    static final VarHandle LONG_ARRAY = MethodHandles.arrayElementVarHandle(long[].class);

    static final int THREADS = 4;
    static final long ITERATIONS = 200_000_000L;

    // Unpadded: 4 real counters in 4 consecutive array slots -- all
    // inside a single 64-byte cache line (4 longs = 32 bytes).
    static final long[] unpadded = new long[THREADS];

    // Padded: each real counter is 8 longs (64 bytes) apart.
    static final int STRIDE = 8;
    static final long[] padded = new long[THREADS * STRIDE];

    public static void main(String[] args) throws Exception {
        System.out.println("Warmup...");
        run(unpadded, 1, 20_000_000L);
        run(padded, STRIDE, 20_000_000L);

        System.out.println("\n=== Real measurement, " + THREADS + " threads, "
                + ITERATIONS + " atomic increments each ===");
        long unpaddedMs = run(unpadded, 1, ITERATIONS);
        long paddedMs = run(padded, STRIDE, ITERATIONS);

        System.out.println("\nUnpadded (all 4 counters share a cache line): " + unpaddedMs + "ms");
        System.out.println("Padded (each counter its own cache line):      " + paddedMs + "ms");
        System.out.printf("Slowdown from false sharing:                   %.2fx%n",
                (double) unpaddedMs / paddedMs);
    }

    static long run(long[] counters, int stride, long iterations) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            int index = t * stride;
            threads[t] = new Thread(() -> {
                for (long i = 0; i < iterations; i++) {
                    LONG_ARRAY.getAndAdd(counters, index, 1L);
                }
            });
        }
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
