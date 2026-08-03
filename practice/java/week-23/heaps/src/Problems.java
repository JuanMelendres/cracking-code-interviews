import java.util.*;

final class Problems {

    // ---- LC 1046: Last Stone Weight ----
    static int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int s : stones) maxHeap.offer(s);
        while (maxHeap.size() > 1) {
            int a = maxHeap.poll();
            int b = maxHeap.poll();
            if (a != b) maxHeap.offer(a - b);
        }
        return maxHeap.isEmpty() ? 0 : maxHeap.peek();
    }

    // ---- LC 692: Top K Frequent Words ----
    static List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) freq.merge(w, 1, Integer::sum);
        // min-heap of size k: higher freq wins; on tie, lexicographically LARGER is "worse" (evicted first)
        PriorityQueue<String> heap = new PriorityQueue<>((a, b) -> {
            int freqCompare = freq.get(a) - freq.get(b);
            if (freqCompare != 0) return freqCompare; // lower freq = smaller = evicted first
            return b.compareTo(a); // lexicographically larger = smaller priority = evicted first
        });
        for (String w : freq.keySet()) {
            heap.offer(w);
            if (heap.size() > k) heap.poll();
        }
        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) result.add(heap.poll());
        Collections.reverse(result); // heap pops least-favored first; reverse for highest-freq-first order
        return result;
    }

    // ---- LC 373: Find K Pairs with Smallest Sums ----
    static List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0 || k == 0) return result;
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> nums1[a[0]] + nums2[a[1]]));
        for (int i = 0; i < Math.min(nums1.length, k); i++) {
            minHeap.offer(new int[]{i, 0});
        }
        while (k-- > 0 && !minHeap.isEmpty()) {
            int[] idx = minHeap.poll();
            int i = idx[0], j = idx[1];
            result.add(List.of(nums1[i], nums2[j]));
            if (j + 1 < nums2.length) {
                minHeap.offer(new int[]{i, j + 1});
            }
        }
        return result;
    }

    // ---- LC 767: Reorganize String ----
    static String reorganizeString(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
        PriorityQueue<Map.Entry<Character, Integer>> maxHeap =
            new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        maxHeap.addAll(freq.entrySet());

        StringBuilder result = new StringBuilder();
        Map.Entry<Character, Integer> prev = null;
        while (!maxHeap.isEmpty()) {
            Map.Entry<Character, Integer> cur = maxHeap.poll();
            result.append(cur.getKey());
            cur.setValue(cur.getValue() - 1);
            if (prev != null && prev.getValue() > 0) maxHeap.offer(prev);
            prev = cur;
        }
        return result.length() == s.length() ? result.toString() : "";
    }

    // ---- LC 1642: Furthest Building You Can Reach ----
    static int furthestBuilding(int[] heights, int bricks, int ladders) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // climbs currently "paid" with a ladder
        for (int i = 0; i < heights.length - 1; i++) {
            int climb = heights[i + 1] - heights[i];
            if (climb <= 0) continue;
            minHeap.offer(climb);
            if (minHeap.size() > ladders) {
                bricks -= minHeap.poll(); // pay for the smallest climb with bricks instead
                if (bricks < 0) return i;
            }
        }
        return heights.length - 1;
    }
}
