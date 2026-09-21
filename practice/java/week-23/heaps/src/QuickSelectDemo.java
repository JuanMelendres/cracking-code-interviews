import java.util.*;

/**
 * Extends this practice pack for T-2106's Quickselect addition (2026-09-20):
 * the classic Senior follow-up to "find the Kth largest element" -- can you
 * beat the heap's O(n log k) with an O(n)-average approach? Quickselect
 * (Hoare/Lomuto-style partitioning, reused from QuickSort) answers yes, at
 * the cost of worst-case O(n^2) with a bad pivot -- the same pivot lesson
 * sorting-algorithms.md (T-2119) already teaches, applied here to selection
 * instead of sorting.
 */
final class QuickSelectDemo {

    private static long comparisons;

    /** LC 215 -- Kth Largest Element, via quickselect with a random pivot. */
    static int findKthLargestRandomPivot(int[] nums, int k) {
        int[] a = nums.clone();
        int targetIndex = a.length - k; // Kth largest == (n-k)th smallest, 0-indexed
        int lo = 0, hi = a.length - 1;
        Random random = new Random(7);
        while (true) {
            int pivotIndex = lo + random.nextInt(hi - lo + 1);
            int p = partition(a, lo, hi, pivotIndex);
            if (p == targetIndex) return a[p];
            if (p < targetIndex) lo = p + 1; else hi = p - 1;
        }
    }

    /** Same algorithm, but always pivots on the first element -- deliberately adversarial. */
    static int findKthLargestFirstElementPivot(int[] nums, int k) {
        int[] a = nums.clone();
        int targetIndex = a.length - k;
        int lo = 0, hi = a.length - 1;
        while (true) {
            int p = partition(a, lo, hi, lo); // always pivot on the first element
            if (p == targetIndex) return a[p];
            if (p < targetIndex) lo = p + 1; else hi = p - 1;
        }
    }

    /** Lomuto partition -- moves the pivot to its final sorted position, returns that index. */
    private static int partition(int[] a, int lo, int hi, int pivotIndex) {
        int pivot = a[pivotIndex];
        swap(a, pivotIndex, hi);
        int store = lo;
        for (int i = lo; i < hi; i++) {
            comparisons++;
            if (a[i] < pivot) {
                swap(a, i, store);
                store++;
            }
        }
        swap(a, store, hi);
        return store;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    /** Heap-based Kth largest (the pattern this chapter otherwise teaches), for a real comparison. */
    static int findKthLargestHeap(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int n : nums) {
            minHeap.offer(n);
            if (minHeap.size() > k) minHeap.poll();
        }
        return minHeap.peek();
    }

    public static void main(String[] args) {
        // Correctness: quickselect (random pivot) vs Arrays.sort ground truth, several sizes/k.
        Random random = new Random(42);
        for (int trial = 0; trial < 20; trial++) {
            int n = 50 + random.nextInt(500);
            int[] nums = random.ints(n, -1000, 1000).toArray();
            int k = 1 + random.nextInt(n);
            int[] sorted = nums.clone();
            Arrays.sort(sorted);
            int expected = sorted[n - k];
            int actual = findKthLargestRandomPivot(nums, k);
            Check.eq(expected, actual, "quickselect trial " + trial + " (n=" + n + ", k=" + k + ")");
        }

        // Adversarial pivot comparison: already-sorted input, n=2000, k=1 (worst case for
        // a first-element pivot on ascending input -- every partition is maximally unbalanced).
        int n = 2000;
        int[] sortedAscending = new int[n];
        for (int i = 0; i < n; i++) sortedAscending[i] = i;

        comparisons = 0;
        int firstPivotResult = findKthLargestFirstElementPivot(sortedAscending.clone(), 1);
        long firstPivotComparisons = comparisons;

        comparisons = 0;
        int randomPivotResult = findKthLargestRandomPivot(sortedAscending.clone(), 1);
        long randomPivotComparisons = comparisons;

        Check.eq(n - 1, firstPivotResult, "first-element-pivot quickselect, kthLargest(k=1) on ascending input");
        Check.eq(n - 1, randomPivotResult, "random-pivot quickselect, kthLargest(k=1) on ascending input");

        System.out.println("  n=" + n + " ascending input, k=1:");
        System.out.println("    first-element pivot: " + firstPivotComparisons + " comparisons");
        System.out.println("    random pivot:         " + randomPivotComparisons + " comparisons");
        Check.isTrue(firstPivotComparisons > randomPivotComparisons * 10,
            "first-element pivot is at least 10x worse than random pivot on adversarial input");

        // Timing comparison at scale: quickselect (O(n) average) vs heap (O(n log k)).
        int large = 5_000_000;
        int[] bigRandom = new Random(99).ints(large, Integer.MIN_VALUE, Integer.MAX_VALUE).toArray();
        int bigK = 100;

        long t0 = System.nanoTime();
        int quickSelectResult = findKthLargestRandomPivot(bigRandom, bigK);
        long quickSelectNanos = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        int heapResult = findKthLargestHeap(bigRandom, bigK);
        long heapNanos = System.nanoTime() - t1;

        Check.eq(quickSelectResult, heapResult, "quickselect and heap agree on Kth largest (n=" + large + ", k=" + bigK + ")");
        System.out.println("  n=" + large + ", k=" + bigK + ":");
        System.out.println("    quickselect: " + (quickSelectNanos / 1_000_000) + "ms");
        System.out.println("    heap:        " + (heapNanos / 1_000_000) + "ms");

        Check.summary("QuickSelect extension (T-2106)");
    }
}
