import java.util.*;

final class Problems {

    // ---- LC 307: Range Sum Query - Mutable ----
    // Iterative (bottom-up) segment tree: leaves at indices [n, 2n), internal
    // nodes hold the sum of their two children. Both update and sumRange are
    // O(log n); the leaf array is stored flat, no recursion needed.
    static final class NumArray {
        final int n;
        final int[] tree;

        NumArray(int[] nums) {
            n = nums.length;
            tree = new int[2 * n];
            for (int i = 0; i < n; i++) tree[n + i] = nums[i];
            for (int i = n - 1; i > 0; i--) tree[i] = tree[2 * i] + tree[2 * i + 1];
        }

        void update(int index, int val) {
            int pos = index + n;
            tree[pos] = val;
            for (pos /= 2; pos >= 1; pos /= 2) tree[pos] = tree[2 * pos] + tree[2 * pos + 1];
        }

        int sumRange(int left, int right) { // inclusive
            int l = left + n, r = right + n + 1;
            int sum = 0;
            while (l < r) {
                if ((l & 1) == 1) sum += tree[l++];
                if ((r & 1) == 1) sum += tree[--r];
                l /= 2;
                r /= 2;
            }
            return sum;
        }
    }

    // ---- LC 732: My Calendar III ----
    // A dynamic (implicitly-built) segment tree with lazy propagation over the
    // full [0, 1e9] domain -- coordinate compression isn't possible here since
    // bookings arrive online, one at a time, with no upfront knowledge of future
    // ranges. Each book() does a range-add of +1 over [start, end-1] and the
    // tree's root value is always the current maximum overlap count anywhere.
    static final class MyCalendarThree {
        static final class Node {
            Node left, right;
            int val = 0, lazy = 0;
        }

        private final Node root = new Node();
        private int k = 0;

        int book(int start, int end) {
            update(root, 0, 1_000_000_000, start, end - 1, 1);
            k = Math.max(k, root.val);
            return k;
        }

        private void update(Node node, int lo, int hi, int l, int r, int delta) {
            if (r < lo || hi < l) return;
            if (l <= lo && hi <= r) {
                node.val += delta;
                node.lazy += delta;
                return;
            }
            int mid = lo + (hi - lo) / 2;
            if (node.left == null) node.left = new Node();
            if (node.right == null) node.right = new Node();
            update(node.left, lo, mid, l, r, delta);
            update(node.right, mid + 1, hi, l, r, delta);
            node.val = node.lazy + Math.max(node.left.val, node.right.val);
        }
    }

    // ---- LC 218: The Skyline Problem ----
    // Coordinate-compress every building edge into a dense index space, then use
    // a segment tree that supports "range max-assign" without pushdown: each
    // update stores its height at the single ancestor node that exactly covers
    // the query range, and a single final top-down pass accumulates the running
    // max down to every leaf. This works specifically because the read pattern
    // is "collect every leaf exactly once at the end" rather than interleaved
    // point queries, which sidesteps needing real lazy propagation.
    private static final class SegTreeMax {
        final int[] tree;

        SegTreeMax(int leafCount) {
            tree = new int[4 * Math.max(1, leafCount)];
        }

        void update(int node, int start, int end, int l, int r, int h) {
            if (r < start || end < l) return;
            if (l <= start && end <= r) {
                tree[node] = Math.max(tree[node], h);
                return;
            }
            int mid = (start + end) / 2;
            update(2 * node, start, mid, l, r, h);
            update(2 * node + 1, mid + 1, end, l, r, h);
        }

        void collect(int node, int start, int end, int inherited, int[] result) {
            int cur = Math.max(inherited, tree[node]);
            if (start == end) {
                result[start] = cur;
                return;
            }
            int mid = (start + end) / 2;
            collect(2 * node, start, mid, cur, result);
            collect(2 * node + 1, mid + 1, end, cur, result);
        }
    }

    static List<List<Integer>> getSkyline(int[][] buildings) {
        List<List<Integer>> result = new ArrayList<>();
        if (buildings.length == 0) return result;

        TreeSet<Integer> xsSet = new TreeSet<>();
        for (int[] b : buildings) {
            xsSet.add(b[0]);
            xsSet.add(b[1]);
        }
        Integer[] xs = xsSet.toArray(new Integer[0]);
        int m = xs.length; // m >= 2 always, since every building contributes 2 distinct edges
        Map<Integer, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < m; i++) indexOf.put(xs[i], i);

        int leafCount = m - 1; // segment i represents [xs[i], xs[i+1])
        SegTreeMax seg = new SegTreeMax(leafCount);
        for (int[] b : buildings) {
            int li = indexOf.get(b[0]);
            int ri = indexOf.get(b[1]) - 1; // last segment index fully inside [b[0], b[1])
            if (li <= ri) seg.update(1, 0, leafCount - 1, li, ri, b[2]);
        }

        int[] heights = new int[leafCount];
        seg.collect(1, 0, leafCount - 1, 0, heights);

        int prevHeight = 0;
        for (int i = 0; i < heights.length; i++) {
            if (heights[i] != prevHeight) {
                result.add(List.of(xs[i], heights[i]));
                prevHeight = heights[i];
            }
        }
        if (prevHeight != 0) result.add(List.of(xs[m - 1], 0));
        return result;
    }
}
