import java.util.*;

/** T-1407 -- heaps. LC 215, 347, 23, 295. */
public class HeapProblems {

    /** LC 215: Kth Largest Element in an Array. Min-heap of size k. */
    static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int n : nums) {
            minHeap.offer(n);
            if (minHeap.size() > k) minHeap.poll();
        }
        return minHeap.peek();
    }

    /**
     * LC 347: Top K Frequent Elements. Count, then min-heap of size k by frequency.
     *
     * ERRATA (Phase 1 audit): the source material presented this kind of result as
     * if the heap's own output order were a specific, reliable sequence. It isn't --
     * {@code PriorityQueue} guarantees only that {@code poll()} returns the smallest
     * remaining element by the given comparator; it does NOT guarantee any particular
     * order among elements that compare equal (here, elements tied on frequency), and
     * its {@code iterator()} isn't sorted at all -- it walks the underlying binary-heap
     * array, not heap order. Extracting via repeated {@code poll()} below IS
     * deterministic for a given JDK and input, but callers should never assert on that
     * incidental order; Main.java's own test sorts the result before comparing, and
     * separately, directly demonstrates that {@code PriorityQueue.iterator()} does NOT
     * yield sorted order, with a real, executed counter-example.
     */
    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            minHeap.offer(new int[]{e.getKey(), e.getValue()});
            if (minHeap.size() > k) minHeap.poll();
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = minHeap.poll()[0];
        return result;
    }

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** LC 23: Merge K Sorted Lists. Min-heap of the current head of each list. */
    static ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
        for (ListNode node : lists) if (node != null) minHeap.offer(node);

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!minHeap.isEmpty()) {
            ListNode smallest = minHeap.poll();
            tail.next = smallest;
            tail = smallest;
            if (smallest.next != null) minHeap.offer(smallest.next);
        }
        return dummy.next;
    }

    static ListNode buildList(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static List<Integer> toList(ListNode head) {
        List<Integer> result = new ArrayList<>();
        while (head != null) { result.add(head.val); head = head.next; }
        return result;
    }

    /** LC 295: Find Median from Data Stream. Two heaps: max-heap for the lower
     * half, min-heap for the upper half, kept balanced within size 1 of each other. */
    static class MedianFinder {
        private final PriorityQueue<Integer> lowerHalf = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
        private final PriorityQueue<Integer> upperHalf = new PriorityQueue<>(); // min-heap

        void addNum(int num) {
            lowerHalf.offer(num);
            upperHalf.offer(lowerHalf.poll()); // always route through lowerHalf first to keep the invariant simple
            if (upperHalf.size() > lowerHalf.size()) {
                lowerHalf.offer(upperHalf.poll());
            }
        }

        double findMedian() {
            if (lowerHalf.size() > upperHalf.size()) return lowerHalf.peek();
            return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
        }
    }
}
