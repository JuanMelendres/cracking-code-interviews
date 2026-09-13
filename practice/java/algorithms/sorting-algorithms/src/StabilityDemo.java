import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StabilityDemo {
    record Entry(int key, int originalIndex) {
        @Override public String toString() { return key + "@" + originalIndex; }
    }

    static void quickSortUnstable(Entry[] a, int lo, int hi, Comparator<Entry> cmp) {
        if (lo >= hi) return;
        Entry pivot = a[lo];
        int i = lo, j = hi;
        while (i <= j) {
            while (cmp.compare(a[i], pivot) < 0) i++;
            while (cmp.compare(a[j], pivot) > 0) j--;
            if (i <= j) {
                Entry t = a[i]; a[i] = a[j]; a[j] = t;
                i++; j--;
            }
        }
        quickSortUnstable(a, lo, j, cmp);
        quickSortUnstable(a, i, hi, cmp);
    }

    public static void main(String[] args) {
        int n = 12;
        Random r = new Random(3);
        Entry[] entries = new Entry[n];
        for (int idx = 0; idx < n; idx++) {
            entries[idx] = new Entry(r.nextInt(4), idx); // small key range -> guaranteed duplicates
        }
        System.out.println("original: " + Arrays.toString(entries));

        Entry[] forStable = Arrays.copyOf(entries, entries.length);
        Arrays.sort(forStable, Comparator.comparingInt(Entry::key)); // TimSort: real, documented stable sort
        System.out.println("Arrays.sort (TimSort, stable): " + Arrays.toString(forStable));

        Entry[] forUnstable = Arrays.copyOf(entries, entries.length);
        quickSortUnstable(forUnstable, 0, forUnstable.length - 1, Comparator.comparingInt(Entry::key));
        System.out.println("naive in-place quicksort (unstable): " + Arrays.toString(forUnstable));

        boolean stableOrderPreserved = isStableRelativeToOriginal(entries, forStable);
        boolean unstableOrderPreserved = isStableRelativeToOriginal(entries, forUnstable);
        System.out.println("TimSort preserved original tie order? " + stableOrderPreserved);
        System.out.println("naive quicksort preserved original tie order? " + unstableOrderPreserved);
    }

    // For each key value, checks whether the relative order of originalIndex values
    // among entries sharing that key matches their order in the original array.
    static boolean isStableRelativeToOriginal(Entry[] original, Entry[] sorted) {
        for (int key = 0; key < 10; key++) {
            List<Integer> fromOriginal = new ArrayList<>();
            for (Entry e : original) if (e.key() == key) fromOriginal.add(e.originalIndex());
            List<Integer> fromSorted = new ArrayList<>();
            for (Entry e : sorted) if (e.key() == key) fromSorted.add(e.originalIndex());
            if (!fromOriginal.equals(fromSorted)) return false;
        }
        return true;
    }
}
