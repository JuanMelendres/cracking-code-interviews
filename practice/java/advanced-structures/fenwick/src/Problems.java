import java.util.*;

final class Problems {

    // Minimal Fenwick tree (Binary Indexed Tree) over a fixed 1-indexed rank space.
    // update(i, +1) marks one occurrence of rank i; query(i) returns the count of
    // occurrences at ranks 1..i (inclusive). Both O(log m) for m ranks.
    static final class Fenwick {
        final int[] tree;
        Fenwick(int m) { tree = new int[m + 1]; }
        void update(int i) {
            for (; i < tree.length; i += i & (-i)) tree[i]++;
        }
        int query(int i) {
            int sum = 0;
            for (; i > 0; i -= i & (-i)) sum += tree[i];
            return sum;
        }
    }

    // ---- LC 315: Count of Smaller Numbers After Self ----
    // For each i, count how many j > i have nums[j] < nums[i]. Process right to
    // left: a Fenwick tree over coordinate-compressed values tracks how many
    // values less than nums[i] have already been seen (i.e. lie to its right).
    static List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
        int m = uniq.length;
        Fenwick bit = new Fenwick(m);
        Integer[] result = new Integer[n];
        for (int i = n - 1; i >= 0; i--) {
            int rank = lowerBound(uniq, nums[i]) + 1; // 1-indexed rank of nums[i] itself
            result[i] = bit.query(rank - 1);           // count of already-seen values strictly less than nums[i]
            bit.update(rank);
        }
        return Arrays.asList(result);
    }

    // ---- LC 493: Reverse Pairs ----
    // Count pairs i < j with nums[i] > 2 * nums[j]. Process left to right, playing
    // the role of j at each step: before inserting nums[j], every already-inserted
    // value v came from an earlier index i < j, so the pair (i, j) qualifies
    // exactly when v > 2 * nums[j]. That count is (how many values are inserted
    // so far) minus (how many of them are <= 2 * nums[j]) -- the second term read
    // straight off the Fenwick tree via a coordinate-compressed rank boundary.
    static int reversePairs(int[] nums) {
        int n = nums.length;
        long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
        int m = uniq.length;
        Fenwick bit = new Fenwick(m);
        int count = 0;
        int inserted = 0;
        for (int i = 0; i < n; i++) {
            long threshold = 2L * nums[i];
            int leRank = countLessOrEqual(uniq, threshold); // # of uniq values <= threshold
            int alreadyLE = bit.query(leRank);               // # of already-inserted values <= threshold
            count += inserted - alreadyLE;                   // the rest are strictly greater -> qualify
            int rank = lowerBound(uniq, nums[i]) + 1;
            bit.update(rank);
            inserted++;
        }
        return count;
    }

    // number of entries in sorted uniq that are <= threshold (uses long math to avoid overflow)
    private static int countLessOrEqual(long[] uniq, long threshold) {
        int lo = 0, hi = uniq.length; // first index where uniq[idx] > threshold
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (uniq[mid] <= threshold) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    // ---- LC 327: Count of Range Sum ----
    // Count contiguous range sums nums[i..j] falling in [lower, upper]. Using
    // prefix sums P[0..n] (P[0]=0), a range sum P[j]-P[i] is in range iff
    // P[j]-upper <= P[i] <= P[j]-lower. Insert prefix values into a Fenwick tree
    // as we scan j left to right, and for each j query the count of already-
    // inserted P[i] (i < j) falling in that window via two rank-boundary lookups.
    static int countRangeSum(int[] nums, int lower, int upper) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];

        long[] uniq = prefix.clone();
        Arrays.sort(uniq);
        uniq = Arrays.stream(uniq).distinct().toArray();
        int m = uniq.length;
        Fenwick bit = new Fenwick(m);

        int count = 0;
        insert(bit, uniq, prefix[0]);
        for (int j = 1; j <= n; j++) {
            long lo = prefix[j] - upper;
            long hi = prefix[j] - lower;
            int loIdx = lowerBoundInclusive(uniq, lo); // first index with uniq[idx] >= lo
            int hiIdx = upperBoundInclusive(uniq, hi); // first index with uniq[idx] > hi
            count += bit.query(hiIdx) - bit.query(loIdx);
            insert(bit, uniq, prefix[j]);
        }
        return count;
    }

    private static void insert(Fenwick bit, long[] uniq, long value) {
        bit.update(lowerBound(uniq, value) + 1);
    }

    // first index in sorted uniq where uniq[idx] >= value (== index, 0-indexed)
    private static int lowerBound(long[] uniq, long value) {
        int lo = 0, hi = uniq.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (uniq[mid] < value) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    private static int lowerBoundInclusive(long[] uniq, long value) {
        return lowerBound(uniq, value);
    }

    // first index in sorted uniq where uniq[idx] > value
    private static int upperBoundInclusive(long[] uniq, long value) {
        int lo = 0, hi = uniq.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (uniq[mid] <= value) lo = mid + 1; else hi = mid;
        }
        return lo;
    }
}
