import java.util.Arrays;
import java.util.Random;

public class InsertionVsMergeDemo {
    static long comparisons;

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
        }
    }

    static void mergeSort(int[] a, int[] tmp, int lo, int hi) {
        if (hi - lo <= 1) return;
        int mid = (lo + hi) / 2;
        mergeSort(a, tmp, lo, mid);
        mergeSort(a, tmp, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            comparisons++;
            if (a[i] <= a[j]) tmp[k++] = a[i++];
            else tmp[k++] = a[j++];
        }
        while (i < mid) tmp[k++] = a[i++];
        while (j < hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo);
    }

    static int[] nearlySorted(int n, int swaps, long seed) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        Random r = new Random(seed);
        for (int s = 0; s < swaps; s++) {
            int x = r.nextInt(n), y = r.nextInt(n);
            int t = a[x]; a[x] = a[y]; a[y] = t;
        }
        return a;
    }

    static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(n * 10);
        return a;
    }

    public static void main(String[] args) {
        int smallN = 12;
        int largeN = 5000;

        int[] smallNearlySorted = nearlySorted(smallN, 1, 11);
        int[] largeRandom = randomArray(largeN, 11);

        for (int[] base : new int[][] { smallNearlySorted, largeRandom }) {
            String label = base.length == smallN
                    ? "small, nearly-sorted (n=" + smallN + ")"
                    : "large, random (n=" + largeN + ")";

            int[] a1 = Arrays.copyOf(base, base.length);
            comparisons = 0;
            insertionSort(a1);
            long insertionComparisons = comparisons;

            int[] a2 = Arrays.copyOf(base, base.length);
            comparisons = 0;
            mergeSort(a2, new int[a2.length], 0, a2.length);
            long mergeComparisons = comparisons;

            System.out.printf("%-32s insertionSort comparisons=%-8d mergeSort comparisons=%d%n",
                    label, insertionComparisons, mergeComparisons);
        }
    }
}
