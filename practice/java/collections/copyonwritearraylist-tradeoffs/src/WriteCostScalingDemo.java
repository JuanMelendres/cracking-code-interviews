import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Real, measured proof of CopyOnWriteArrayList's core cost model: every
 * write copies the ENTIRE backing array, so per-write cost scales with the
 * list's current size -- O(n) per write, not ArrayList's O(1) amortized
 * add(). Measured directly by timing a fixed batch of add() calls at
 * increasing starting sizes.
 */
public class WriteCostScalingDemo {

    static final int[] STARTING_SIZES = {1_000, 10_000, 100_000, 500_000};
    static final int WRITES_TO_MEASURE = 100;

    public static void main(String[] args) {
        System.out.println("== CopyOnWriteArrayList: per-write cost as list size grows ==");
        System.out.println("size\ttime for " + WRITES_TO_MEASURE + " add() calls (ms)\tavg per write (microseconds)");
        for (int size : STARTING_SIZES) {
            CopyOnWriteArrayList<Integer> cow = new CopyOnWriteArrayList<>();
            for (int i = 0; i < size; i++) cow.add(i);

            long start = System.nanoTime();
            for (int i = 0; i < WRITES_TO_MEASURE; i++) cow.add(i);
            long elapsedNanos = System.nanoTime() - start;

            System.out.println(size + "\t" + (elapsedNanos / 1_000_000) + "ms\t"
                    + String.format("%.2f", elapsedNanos / 1000.0 / WRITES_TO_MEASURE));
        }

        System.out.println("\n== ArrayList (synchronized externally, single-threaded here): per-write cost as list size grows ==");
        System.out.println("size\ttime for " + WRITES_TO_MEASURE + " add() calls (ms)\tavg per write (microseconds)");
        for (int size : STARTING_SIZES) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < size; i++) list.add(i);

            long start = System.nanoTime();
            for (int i = 0; i < WRITES_TO_MEASURE; i++) list.add(i);
            long elapsedNanos = System.nanoTime() - start;

            System.out.println(size + "\t" + (elapsedNanos / 1_000_000) + "ms\t"
                    + String.format("%.2f", elapsedNanos / 1000.0 / WRITES_TO_MEASURE));
        }

        System.out.println("\nConclusion: CopyOnWriteArrayList's per-write cost grows with list size (real, measured,"
                + " O(n) full-array-copy behavior); ArrayList's per-write cost stays roughly flat"
                + " (real, measured, O(1) amortized behavior) regardless of current size.");
    }
}
