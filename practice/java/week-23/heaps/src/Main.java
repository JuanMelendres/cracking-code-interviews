import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 1046
        Check.eq(1, Problems.lastStoneWeight(new int[]{2,7,4,1,8,1}), "LC1046 lastStoneWeight([2,7,4,1,8,1]) = 1");
        Check.eq(1, Problems.lastStoneWeight(new int[]{1}), "LC1046 lastStoneWeight([1]) = 1");

        // LC 692
        Check.eq(List.of("i","love"),
            Problems.topKFrequent(new String[]{"i","love","leetcode","i","love","coding"}, 2),
            "LC692 topKFrequent(k=2) = [i, love]");
        Check.eq(List.of("the","is","sunny","day"),
            Problems.topKFrequent(new String[]{"the","day","is","sunny","the","the","the","sunny","is","is"}, 4),
            "LC692 topKFrequent(k=4) = [the, is, sunny, day]");

        // LC 373
        Check.eq(List.of(List.of(1,2), List.of(1,4), List.of(1,6)),
            Problems.kSmallestPairs(new int[]{1,7,11}, new int[]{2,4,6}, 3),
            "LC373 kSmallestPairs(k=3) = [[1,2],[1,4],[1,6]]");
        Check.eq(List.of(List.of(1,1), List.of(1,1)),
            Problems.kSmallestPairs(new int[]{1,1,2}, new int[]{1,1,3}, 2),
            "LC373 kSmallestPairs(duplicates, k=2) = [[1,1],[1,1]]");

        // LC 767
        Check.eq("aba", Problems.reorganizeString("aab"), "LC767 reorganizeString(aab) = aba");
        Check.eq("", Problems.reorganizeString("aaab"), "LC767 reorganizeString(aaab) = \"\" (impossible)");

        // LC 1642
        Check.eq(4, Problems.furthestBuilding(new int[]{4,2,7,6,9,14,12}, 5, 1),
            "LC1642 furthestBuilding(bricks=5, ladders=1) = 4");
        Check.eq(7, Problems.furthestBuilding(new int[]{4,12,2,7,3,18,20,3,19}, 10, 2),
            "LC1642 furthestBuilding(bricks=10, ladders=2) = 7");

        Check.summary("Week 23 — Heaps (LC 1046, 692, 373, 767, 1642)");
    }
}
