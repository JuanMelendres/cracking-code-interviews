import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("== LC 215: Kth Largest Element in an Array ==");
        Check.eq(5, HeapProblems.findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2), "kthLargest([3,2,1,5,6,4], k=2) = 5");
        Check.eq(4, HeapProblems.findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), "kthLargest([3,2,3,1,2,4,5,5,6], k=4) = 4");

        System.out.println("\n== LC 347: Top K Frequent Elements ==");
        int[] top2 = HeapProblems.topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
        Arrays.sort(top2);
        Check.eq("[1, 2]", Arrays.toString(top2), "topKFrequent([1,1,1,2,2,3], k=2) = [1,2] (sorted for comparison)");
        Check.eq("[1]", Arrays.toString(HeapProblems.topKFrequent(new int[]{1}, 1)), "topKFrequent([1], k=1) = [1]");

        System.out.println("\n== LC 23: Merge K Sorted Lists ==");
        HeapProblems.ListNode[] lists = {
                HeapProblems.buildList(1, 4, 5),
                HeapProblems.buildList(1, 3, 4),
                HeapProblems.buildList(2, 6)
        };
        List<Integer> merged = HeapProblems.toList(HeapProblems.mergeKLists(lists));
        Check.eq(List.of(1, 1, 2, 3, 4, 4, 5, 6), merged, "mergeKLists([[1,4,5],[1,3,4],[2,6]]) = [1,1,2,3,4,4,5,6]");
        Check.eq(List.of(), HeapProblems.toList(HeapProblems.mergeKLists(new HeapProblems.ListNode[]{})), "mergeKLists([]) = []");

        System.out.println("\n== LC 295: Find Median from Data Stream ==");
        HeapProblems.MedianFinder mf = new HeapProblems.MedianFinder();
        mf.addNum(1);
        mf.addNum(2);
        Check.eq(1.5, mf.findMedian(), "after adding 1,2: median = 1.5");
        mf.addNum(3);
        Check.eq(2.0, mf.findMedian(), "after adding 3: median = 2.0");

        System.out.println("\n== LC 295 cross-check: MedianFinder vs a sorted-list reference over 500 random insertions ==");
        Random rnd = new Random(7);
        HeapProblems.MedianFinder streamed = new HeapProblems.MedianFinder();
        java.util.List<Integer> reference = new java.util.ArrayList<>();
        boolean allMatch = true;
        for (int i = 0; i < 500; i++) {
            int v = rnd.nextInt(10000);
            streamed.addNum(v);
            int insertAt = java.util.Collections.binarySearch(reference, v);
            if (insertAt < 0) insertAt = -(insertAt + 1);
            reference.add(insertAt, v);
            double expected = reference.size() % 2 == 1
                    ? reference.get(reference.size() / 2)
                    : (reference.get(reference.size() / 2 - 1) + reference.get(reference.size() / 2)) / 2.0;
            if (Math.abs(expected - streamed.findMedian()) > 1e-9) {
                allMatch = false;
                System.out.println("  MISMATCH at insertion " + i + ": expected=" + expected + " actual=" + streamed.findMedian());
            }
        }
        Check.isTrue(allMatch, "MedianFinder matches a sorted-list reference after every one of 500 random insertions");

        Check.summary("Week 10 heaps suite");
        if (Check.fail > 0) System.exit(1);
    }
}
