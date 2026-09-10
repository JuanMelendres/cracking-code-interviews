import java.util.Arrays;
import java.util.Random;

public class QuickSortPivotDemo {
    static long comparisons;

    static int partitionLomuto(int[] a, int lo, int hi, int pivotIndex) {
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

    static void quickSort(int[] a, int lo, int hi, boolean randomPivot, Random rnd) {
        if (lo >= hi) return;
        int pivotIndex = randomPivot ? lo + rnd.nextInt(hi - lo + 1) : lo; // naive: first element
        int p = partitionLomuto(a, lo, hi, pivotIndex);
        quickSort(a, lo, p - 1, randomPivot, rnd);
        quickSort(a, p + 1, hi, randomPivot, rnd);
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    static int[] sortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    static int[] reverseArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(n * 10);
        return a;
    }

    static void run(String label, int[] template, boolean randomPivot) {
        int[] a = Arrays.copyOf(template, template.length);
        comparisons = 0;
        quickSort(a, 0, a.length - 1, randomPivot, new Random(42));
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) throw new IllegalStateException("not sorted!");
        }
        System.out.printf("%-22s n=%-6d %-24s comparisons=%d%n",
                label, template.length, randomPivot ? "(random pivot)" : "(first-element pivot)", comparisons);
    }

    public static void main(String[] args) {
        int n = 2000;
        int[] sorted = sortedArray(n);
        int[] reverse = reverseArray(n);
        int[] random = randomArray(n, 7);

        run("already-sorted input", sorted, false);
        run("already-sorted input", sorted, true);
        run("reverse-sorted input", reverse, false);
        run("reverse-sorted input", reverse, true);
        run("random input", random, false);
        run("random input", random, true);
    }
}
