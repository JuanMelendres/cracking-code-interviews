import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

// Real, compiling demonstrations of PriorityQueue's actual behavior: the
// binary-heap ordering guarantee (poll() only, never iteration order), a
// counting Comparator used as real, measured evidence of O(log n) offer/poll
// (not asserted from the Javadoc), the classic iteration-order trap, and the
// real ConcurrentModificationException a structural mutation during iteration
// throws (PriorityQueue is not thread-safe and is also fail-fast, like the
// other core collections this domain already covers).
public class PriorityQueueInternalsDemo {

    static void demoOrderingGuarantee() {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        int[] insertOrder = {50, 10, 40, 20, 30, 5, 45};
        for (int x : insertOrder) minHeap.offer(x);

        System.out.println("  Inserted in this order: " + java.util.Arrays.toString(insertOrder));
        System.out.print("  poll() repeatedly: ");
        List<Integer> polled = new ArrayList<>();
        while (!minHeap.isEmpty()) polled.add(minHeap.poll());
        System.out.println(polled + "  <-- always ascending, regardless of insert order");
    }

    static void demoMaxHeapViaComparator() {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int x : new int[]{50, 10, 40, 20, 30}) maxHeap.offer(x);
        List<Integer> polled = new ArrayList<>();
        while (!maxHeap.isEmpty()) polled.add(maxHeap.poll());
        System.out.println("  Comparator.reverseOrder() -> poll() order: " + polled + "  <-- descending, same class, one constructor arg");
    }

    static void demoIterationOrderTrap() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int x : new int[]{50, 10, 40, 20, 30, 5, 45}) pq.offer(x);

        StringBuilder iterationOrder = new StringBuilder();
        for (int x : pq) iterationOrder.append(x).append(" ");
        System.out.println("  for-each iteration order:  " + iterationOrder.toString().trim()
                + "  <-- this is the internal array's storage order, NOT sorted");

        List<Integer> pollOrder = new ArrayList<>();
        while (!pq.isEmpty()) pollOrder.add(pq.poll());
        System.out.println("  repeated poll() order:     " + pollOrder + "  <-- THIS is the sorted order");
        System.out.println("  The ordering guarantee applies ONLY to poll()/peek() -- never to iterator()/toString()/stream().");
    }

    // A Comparator that counts every comparison it's asked to make -- real,
    // direct evidence of how many comparisons ONE poll() actually performs,
    // rather than trusting the Javadoc's O(log n) claim on faith.
    static class CountingComparator implements Comparator<Integer> {
        final AtomicInteger count = new AtomicInteger();
        public int compare(Integer a, Integer b) {
            count.incrementAndGet();
            return Integer.compare(a, b);
        }
    }

    static void demoLogNComparisonCount() {
        System.out.println("  size N -> comparisons made by exactly ONE poll() call (sift-down), and log2(N) for reference:");
        for (int n : new int[]{100, 1_000, 10_000, 100_000, 1_000_000}) {
            CountingComparator counter = new CountingComparator();
            PriorityQueue<Integer> pq = new PriorityQueue<>(n, counter);
            java.util.Random rnd = new java.util.Random(42);
            for (int i = 0; i < n; i++) pq.offer(rnd.nextInt());

            counter.count.set(0); // measure exactly one poll(), not the n offers that built the heap
            pq.poll();
            int comparisons = counter.count.get();
            double log2n = Math.log(n) / Math.log(2);
            System.out.printf("  N=%,9d  poll() comparisons=%3d   log2(N)=%.1f%n", n, comparisons, log2n);
        }
        System.out.println("  Comparisons per poll() track log2(N), not N -- real, measured O(log n), not asserted.");
    }

    static void demoConcurrentModificationException() {
        PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(30, 10, 20, 5, 25));
        System.out.println("  Iterating and structurally modifying the SAME PriorityQueue mid-iteration...");
        try {
            Iterator<Integer> it = pq.iterator();
            while (it.hasNext()) {
                int x = it.next();
                if (x == 10) pq.offer(999); // structural modification NOT via the iterator itself
            }
            System.out.println("  Completed without exception (BUG -- expected a fail-fast CME)");
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("  Threw " + e.getClass().getSimpleName()
                    + " -- PriorityQueue's iterator is fail-fast, exactly like ArrayList's and HashMap's.");
        }
    }

    public static void main(String[] args) {
        System.out.println("############ Ordering guarantee: poll() only ############");
        demoOrderingGuarantee();
        System.out.println();
        System.out.println("############ Max-heap via a Comparator -- same class ############");
        demoMaxHeapViaComparator();
        System.out.println();
        System.out.println("############ The classic trap: iteration order != sorted order ############");
        demoIterationOrderTrap();
        System.out.println();
        System.out.println("############ Real, measured O(log n): comparisons per poll() ############");
        demoLogNComparisonCount();
        System.out.println();
        System.out.println("############ Not thread-safe, and fail-fast like the rest of this domain ############");
        demoConcurrentModificationException();
    }
}
