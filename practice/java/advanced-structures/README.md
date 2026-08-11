# Advanced Structures (T-1418) — Supplemental, Outside Plan C's Core Roadmap

**Status:** optional supplement, not part of the numbered `study-packs/week-01`–`week-25` sequence. `00-project/learning-roadmap.md` §5 states Plan C **deliberately excludes** the entire Expert tier — T-1418 (Advanced Structures: segment tree, Fenwick/BIT, rolling hash) among it — as "the most common misallocation in senior interview prep." This directory exists because the user explicitly asked to close the gap anyway, not because the roadmap changed its recommendation. Treat this material as recognition-level polish on top of an already-complete core program, not a prerequisite for it.

**8 problems total, split across the structure the register names: segment tree, Fenwick/BIT, rolling hash.** Brings T-1418 from 0/8 to 8/8 across 3 bounded batches, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. Batches 1–2 (Fenwick/BIT, segment tree) are complete; rolling hash is the remaining batch.

---

## Batch 1 — Fenwick Tree / Binary Indexed Tree (3 problems)

**All code below was compiled and executed — see `fenwick/` for the real source, and the Verification section below for the exact commands and real pass counts.**

A **Fenwick tree** (Binary Indexed Tree, BIT) supports two operations over a fixed-size array in O(log n): point update (add 1 at an index) and prefix-sum query (sum of everything up to an index). All three problems below reduce to "count things up to a boundary, over an index space that grows one element at a time" — exactly what a BIT is for — after a coordinate-compression step maps arbitrary integer values onto a dense 1..m rank space the tree can index into.

### Problem 1 — LC 315 Count of Smaller Numbers After Self

**Pattern:** for each index, count how many elements to its right are strictly smaller. Process the array **right to left**, coordinate-compress the values into ranks, and query the BIT — which only holds elements already seen, i.e. everything to the current index's right — for the count of ranks strictly below the current element's rank, before inserting it.

```java
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
```

**Retrospective:** the right-to-left scan is what makes "already in the BIT" mean "to the right" — the entire problem collapses to one query-then-insert per element once that direction is fixed. A brute-force O(n²) comparison is the obvious first answer; the BIT's value is turning "count smaller to the right" into a running, incrementally-maintained structure instead of re-scanning. **Complexity:** O(n log n) total (n inserts/queries, each O(log m), m ≤ n).

### Problem 2 — LC 493 Reverse Pairs

**Pattern:** count pairs `i < j` with `nums[i] > 2 * nums[j]` — a variant of Problem 1's shape, but with an asymmetric doubling condition and the scan direction reversed (left to right, `j` plays the "current" role and already-inserted values are the earlier `i`s). The BIT tracks how many already-inserted values are `<= 2 * nums[j]`; the rest — `inserted so far` minus that count — are `> 2 * nums[j]` and each forms a qualifying pair with the current `j`.

```java
static int reversePairs(int[] nums) {
    int n = nums.length;
    long[] uniq = Arrays.stream(nums).asLongStream().distinct().sorted().toArray();
    int m = uniq.length;
    Fenwick bit = new Fenwick(m);
    int count = 0, inserted = 0;
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
```

**Retrospective:** the first (wrong) implementation of this problem queried "already-inserted values `v` with `2*v < nums[i]`" — a plausible-looking mirror of the doubling condition that is actually backwards: the LC493 definition requires the *earlier* value to be the large one (`nums[i] > 2*nums[j]` for `i < j`), not the later one. That version passed 2 of 4 test cases (including, misleadingly, the first) and silently produced wrong counts on an ascending array and a mixed one — caught by the test suite, not by inspection, which is exactly the kind of subtle off-by-direction bug this problem's known reputation for tripping up candidates comes from. The fix: reframe the query as "how many already-inserted values are `> 2*nums[j]`," computed as `total inserted - count(<= threshold)`, which correctly places the doubling on the *later* index's value while counting the *earlier* index's values against it. **Complexity:** O(n log n).

### Problem 3 — LC 327 Count of Range Sum

**Pattern:** count contiguous subarray sums falling within `[lower, upper]`. Convert to prefix sums `P[0..n]` (`P[0] = 0`); a range sum `nums[i..j-1] = P[j] - P[i]` is in range iff `P[j] - upper <= P[i] <= P[j] - lower`. Insert prefix values into a BIT as the scan proceeds left to right, and for each `j` query the count of already-inserted `P[i]` (`i < j`) whose *value* falls in that window — via two rank-boundary binary searches into the coordinate-compressed prefix-value space, then a BIT range query (the difference of two prefix-sum queries).

```java
static int countRangeSum(int[] nums, int lower, int upper) {
    int n = nums.length;
    long[] prefix = new long[n + 1];
    for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
    long[] uniq = Arrays.stream(prefix).distinct().sorted().toArray();
    Fenwick bit = new Fenwick(uniq.length);
    int count = 0;
    insert(bit, uniq, prefix[0]);
    for (int j = 1; j <= n; j++) {
        long lo = prefix[j] - upper, hi = prefix[j] - lower;
        int loIdx = lowerBoundInclusive(uniq, lo); // first index with uniq[idx] >= lo
        int hiIdx = upperBoundInclusive(uniq, hi); // first index with uniq[idx] > hi
        count += bit.query(hiIdx) - bit.query(loIdx);
        insert(bit, uniq, prefix[j]);
    }
    return count;
}
```

**Retrospective:** this is the same BIT-over-coordinate-compressed-values shape as Problems 1 and 2, but querying a *window* (two boundaries) instead of a single one-sided threshold — the natural generalization once the prefix-sum reduction is in place. The reduction itself (range sum → difference of two prefix sums → range query over prefix values) is the part that's easy to state and easy to get backwards under pressure; getting the window direction right (`P[j]-upper <= P[i] <= P[j]-lower`, not the reverse) is the specific trap this problem is known for. **Complexity:** O(n log n).

---

## Batch 2 — Segment Tree (3 problems)

**All code below was compiled and executed — see `segment-tree/` for the real source, and the Verification section below for the exact commands and real pass counts.**

A **segment tree** covers a range with a binary tree of sub-ranges, each internal node summarizing its two children (sum, max, etc.) so that both point/range updates and range queries run in O(log n) instead of O(n). The three problems below use three different shapes of the same idea: a static bottom-up tree over a fixed array, a dynamic tree built lazily over a domain too large to materialize upfront, and a static tree over coordinate-compressed indices queried with a single top-down pass instead of true pushdown.

### Problem 4 — LC 307 Range Sum Query - Mutable

**Pattern:** the canonical segment tree exercise — point update, range sum query — implemented as a flat, iterative, bottom-up array (leaves at indices `[n, 2n)`, each internal node the sum of its two children), avoiding recursion entirely.

```java
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

    int sumRange(int left, int right) {
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
```

**Retrospective:** this iterative form trades the recursive tree's readability for a single flat array and no recursion overhead — the update loop walks straight up from a leaf to the root recomputing sums, and the query loop walks two pointers inward from the leaf boundaries, both O(log n) with small constants. It's the same underlying idea as Problem 1's Fenwick tree (prefix aggregation over a fixed index space) but supports point *updates* as a first-class operation rather than only append-style inserts. **Complexity:** O(log n) per update/query, O(n) build.

### Problem 5 — LC 732 My Calendar III

**Pattern:** track the maximum number of simultaneously-overlapping bookings ("k-booking") as bookings arrive online. The domain (`[0, 1e9]`) is too large to materialize upfront and bookings aren't known ahead of time, so coordinate compression isn't available — instead, a **dynamic segment tree** creates child nodes lazily, only where a booking's range actually touches, with real lazy propagation (`node.lazy`) so a range-add doesn't need to visit every leaf.

```java
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
```

**Retrospective:** the key move is that `node.val` always means "the current max overlap count anywhere under this node, including this node's own pending `lazy` contribution" — so the root's `val` is always readable directly as the global max, with no separate query pass needed. Nodes are only created along the O(log(range)) path a given booking's range actually touches, so the tree stays sparse regardless of how large the coordinate domain is — this is the general technique for "range updates over a domain too big or too dynamic to coordinate-compress." **Complexity:** O(log(range)) per booking, here O(30) since the domain is bounded at `1e9`.

### Problem 6 — LC 218 The Skyline Problem

**Pattern:** given a set of rectangular buildings, output the key points of the combined silhouette. Coordinate-compress every building edge into a dense index space, then use a segment tree that stores each building's height at the single ancestor node exactly covering its range — no pushdown during updates — and reconstruct every leaf's true height in one final top-down pass that accumulates the running max from root to leaf.

```java
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
    if (start == end) { result[start] = cur; return; }
    int mid = (start + end) / 2;
    collect(2 * node, start, mid, cur, result);
    collect(2 * node + 1, mid + 1, end, cur, result);
}
```

**Retrospective:** skipping real lazy propagation is only valid here because every leaf is read exactly once, at the very end, in a single traversal — `collect` reconstructs the correct answer by carrying the max height seen along the root-to-leaf path as `inherited`, which is equivalent to pushing every node's stored value down to its descendants, just done implicitly during the one read pass instead of eagerly during each write. Getting this shortcut's precondition wrong (any interleaved point query before the final collect) would silently return stale values — worth stating explicitly since it's the kind of assumption that's easy to carry into a problem where it no longer holds. **Complexity:** O(n log n) for `n` buildings (coordinate compression plus `n` range updates).

---

## Verification

| Problem | Status | Real assertions |
|---|---|---|
| LC 315 Count of Smaller Numbers After Self | **Executed.** All passed on first run. | 4/4 pass |
| LC 493 Reverse Pairs | **Executed.** First implementation had the doubling condition backwards — 2/4 assertions failed on run, including a plausible-looking initial pass on the readme example alone; corrected before citing as passing (see Problem 2's retrospective). | 4/4 pass |
| LC 327 Count of Range Sum | **Executed.** All passed on first run. | 4/4 pass |
| LC 307 Range Sum Query - Mutable | **Executed.** All passed on first run. | 4/4 pass |
| LC 732 My Calendar III | **Executed.** All passed on first run. | 6/6 pass |
| LC 218 The Skyline Problem | **Executed.** All passed on first run, including a hand-verified 2-building overlap case. | 3/3 pass |
| **Running total (batches 1–2)** | | **25/25 pass** |

```
cd practice/java/advanced-structures/fenwick
javac -d out src/*.java && java -cp out Main

cd practice/java/advanced-structures/segment-tree
javac -d out src/*.java && java -cp out Main
```

Source: [`practice/java/advanced-structures/fenwick/`](fenwick/), [`practice/java/advanced-structures/segment-tree/`](segment-tree/)

## Coverage impact

| Register item | Before batch 1 | After batch 1 | After batch 2 |
|---|---|---|---|
| T-1418 Advanced Structures | 0/8 | 3/8 (Fenwick/BIT) | 6/8 (+ segment tree) |

Remaining: 2 rolling-hash problems, in a later bounded batch.

## Errata addressed

**Batch 1 — LC 493 Reverse Pairs:** the doubling condition's direction was initially implemented backwards (see Problem 2's retrospective above); caught by the test suite before being cited as passing, not by inspection.

**Batch 2 — LC 218 The Skyline Problem:** the first hand-derived expected value for the "taller building masks a shorter overlapping one" test case (buildings `[[0,5,7],[1,6,4]]`) omitted the intermediate `[5,4]` transition point, incorrectly expecting only `[[0,7],[6,0]]`. Caught by re-deriving the expected output by hand before running the suite, not by the test failing — recorded here since it's a real correction to this batch's own test data, not just its implementation.

Documented here per this project's standing rule to record real errata rather than silently fix and move on.
