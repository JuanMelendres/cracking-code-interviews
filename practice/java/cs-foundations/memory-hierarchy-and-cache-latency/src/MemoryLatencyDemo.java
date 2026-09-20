import java.util.Random;

/**
 * Pointer-chasing memory-latency benchmark (the same technique lat_mem_rd and
 * Drepper's "What Every Programmer Should Know About Memory" use).
 *
 * A pointer-chase over a random single-cycle permutation defeats the CPU's
 * hardware prefetcher (the next index to visit is not predictable from the
 * current one) and defeats out-of-order execution's ability to overlap
 * accesses (each access depends on the previous one's result), so the
 * measured time per step is close to the real, honest latency of one memory
 * access at whatever level of the cache hierarchy currently holds the
 * working set -- not an artifact of sequential-access prefetching.
 */
public class MemoryLatencyDemo {

    // Element count per working-set size, powers of two from 4 KiB to 256 MiB
    // (int[], 4 bytes/element). Chosen to sweep from "fits in the smallest
    // cache" to "far larger than any on-chip cache, backed by RAM."
    private static final int[] ELEMENT_COUNTS = {
        1_024,            //   4 KiB
        2_048,            //   8 KiB
        4_096,            //  16 KiB
        8_192,            //  32 KiB
        16_384,           //  64 KiB
        32_768,           // 128 KiB
        65_536,           // 256 KiB
        131_072,          // 512 KiB
        262_144,          //   1 MiB
        524_288,          //   2 MiB
        1_048_576,        //   4 MiB
        2_097_152,        //   8 MiB
        4_194_304,        //  16 MiB
        8_388_608,        //  32 MiB
        16_777_216,       //  64 MiB
        33_554_432,       // 128 MiB
        67_108_864,       // 256 MiB
    };

    private static final long TIMED_STEPS = 20_000_000L;
    private static final long WARMUP_STEPS = 3_000_000L;

    public static void main(String[] args) {
        System.out.println("size_bytes,size_elements,ns_per_access");

        // JIT warmup on a small, separate array before any measured size --
        // otherwise the first few rows would be contaminated by the
        // interpreter/tiered-compilation ramp-up this same repository's
        // "How a Computer Executes a Program" chapter documents.
        int[] warmupCycle = buildSingleCyclePermutation(65_536, 1L);
        chase(warmupCycle, WARMUP_STEPS);

        for (int n : ELEMENT_COUNTS) {
            int[] cycle = buildSingleCyclePermutation(n, 42L);
            // Per-size warmup: touches every page of this specific array so
            // the timed pass measures steady-state cache/RAM latency, not
            // first-touch page faults (a separate, real cost this chapter's
            // text attributes correctly to the OS, not the cache hierarchy).
            chase(cycle, Math.max(WARMUP_STEPS, n));

            long start = System.nanoTime();
            long finalIndex = chase(cycle, TIMED_STEPS);
            long elapsedNanos = System.nanoTime() - start;

            double nsPerAccess = (double) elapsedNanos / TIMED_STEPS;
            long sizeBytes = (long) n * Integer.BYTES;
            System.out.printf("%d,%d,%.3f%n", sizeBytes, n, nsPerAccess);

            // Use finalIndex so the JIT cannot prove the loop's result is
            // unobserved and eliminate it.
            if (finalIndex == Integer.MIN_VALUE) {
                throw new AssertionError("unreachable, defeats dead-code elimination");
            }
        }
    }

    /** Sattolo's algorithm: produces a permutation that is a single cycle covering all n indices. */
    private static int[] buildSingleCyclePermutation(int n, long seed) {
        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            next[i] = i;
        }
        Random random = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i); // strictly less than i -- Sattolo's, not Fisher-Yates
            int tmp = next[i];
            next[i] = next[j];
            next[j] = tmp;
        }
        return next;
    }

    /** Follows the pointer chain for exactly {@code steps} hops, returning the final index reached. */
    private static long chase(int[] next, long steps) {
        int idx = 0;
        for (long s = 0; s < steps; s++) {
            idx = next[idx];
        }
        return idx;
    }
}
