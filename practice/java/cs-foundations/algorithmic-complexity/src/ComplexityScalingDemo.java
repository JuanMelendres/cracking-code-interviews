import java.util.Arrays;
import java.util.Random;

/**
 * Real, measured wall-clock scaling for five complexity classes, run at
 * increasing input sizes, to make the abstract "growth rate" claim concrete.
 *
 * Each operation is run multiple times per size and the minimum time is kept
 * (reduces JIT warmup / GC noise dominating a single-shot measurement, without
 * needing a full JMH harness for what is meant to be a legible, from-scratch
 * demonstration of the underlying idea, not a rigorous microbenchmark).
 */
public class ComplexityScalingDemo {

    static final int[] SIZES = {1_000, 10_000, 100_000, 1_000_000, 10_000_000};
    static final int REPEATS = 5;
    static final int WARMUP_ROUNDS = 3;

    public static void main(String[] args) {
        System.out.println("=== O(1): array index access, repeated 10,000 times regardless of n ===");
        for (int n : SIZES) {
            int[] data = buildSortedArray(n);
            long nanos = timeMinNanos(() -> constantTimeAccess(data), WARMUP_ROUNDS, REPEATS);
            report(n, nanos);
        }

        System.out.println();
        System.out.println("=== O(log n): binary search for a present key ===");
        for (int n : SIZES) {
            int[] data = buildSortedArray(n);
            int target = data[n / 3]; // a real, present value, not the trivial first/last element
            long nanos = timeMinNanos(() -> Arrays.binarySearch(data, target), WARMUP_ROUNDS, REPEATS);
            report(n, nanos);
        }

        System.out.println();
        System.out.println("=== O(n): linear sum over the array ===");
        for (int n : SIZES) {
            int[] data = buildSortedArray(n);
            long nanos = timeMinNanos(() -> linearSum(data), WARMUP_ROUNDS, REPEATS);
            report(n, nanos);
        }

        System.out.println();
        System.out.println("=== O(n log n): Arrays.sort (Dual-Pivot Quicksort for int[]) ===");
        for (int n : SIZES) {
            long nanos = timeMinNanos(() -> {
                int[] copy = buildRandomArray(n); // fresh unsorted copy every timed call
                Arrays.sort(copy);
                return copy;
            }, WARMUP_ROUNDS, REPEATS);
            report(n, nanos);
        }

        System.out.println();
        System.out.println("=== O(n^2): all-pairs comparison (bubble-sort-shaped nested loop) ===");
        // Deliberately smaller sizes -- O(n^2) at n=10,000,000 would not finish
        // in a reasonable demo run; the whole point is that this growth curve
        // makes even moderate n impractical, which is itself the finding.
        int[] quadraticSizes = {1_000, 2_000, 4_000, 8_000, 16_000};
        for (int n : quadraticSizes) {
            long nanos = timeMinNanos(() -> {
                int[] copy = buildRandomArray(n);
                return allPairsComparisons(copy);
            }, WARMUP_ROUNDS, REPEATS);
            report(n, nanos);
        }
    }

    static int[] buildSortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    static int[] buildRandomArray(int n) {
        Random r = new Random(42);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }

    static long constantTimeAccess(int[] data) {
        long sum = 0;
        int mid = data.length / 2;
        for (int i = 0; i < 10_000; i++) {
            sum += data[mid]; // same index every time -- the "n" never enters this operation
        }
        return sum;
    }

    static long linearSum(int[] data) {
        long sum = 0;
        for (int value : data) sum += value;
        return sum;
    }

    static long allPairsComparisons(int[] data) {
        long comparisons = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (data[i] > data[j]) comparisons++;
            }
        }
        return comparisons;
    }

    interface TimedOp {
        Object run();
    }

    static long timeMinNanos(TimedOp op, int warmup, int repeats) {
        for (int i = 0; i < warmup; i++) op.run();
        long best = Long.MAX_VALUE;
        for (int i = 0; i < repeats; i++) {
            long start = System.nanoTime();
            Object result = op.run();
            long elapsed = System.nanoTime() - start;
            if (result == null) throw new AssertionError("unreachable, prevents dead-code elimination concerns");
            best = Math.min(best, elapsed);
        }
        return best;
    }

    static void report(int n, long nanos) {
        double millis = nanos / 1_000_000.0;
        System.out.printf("  n=%,10d  ->  %10.4f ms%n", n, millis);
    }
}
