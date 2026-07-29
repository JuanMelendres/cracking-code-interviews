---
title: "Java Coding Practice — Week 3"
week: 3
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 3

**6 tree problems. All code on this page was compiled and executed — see the verification block and `MANIFEST.md`.**

Narrate the recursion invariant *before* writing code on every problem this week — this is the specific narration discipline this week's problems are chosen to drill.

## Table of Contents

1. [LC 104 — Maximum Depth of Binary Tree](#lc-104--maximum-depth-of-binary-tree)
2. [LC 226 — Invert Binary Tree](#lc-226--invert-binary-tree)
3. [LC 98 — Validate Binary Search Tree](#lc-98--validate-binary-search-tree)
4. [LC 235 — Lowest Common Ancestor of a BST](#lc-235--lowest-common-ancestor-of-a-bst)
5. [LC 102 — Binary Tree Level Order Traversal](#lc-102--binary-tree-level-order-traversal)
6. [LC 199 — Binary Tree Right Side View](#lc-199--binary-tree-right-side-view)
7. [Verification](#verification--real-not-asserted)

---

## LC 104 — Maximum Depth of Binary Tree

```java
static int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

**Invariant, stated before coding:** the depth of a node is one more than the deeper of its two children's depths; an empty tree has depth 0. **Complexity:** O(n) time (visits every node once), O(h) space for the recursion stack, h = tree height.

## LC 226 — Invert Binary Tree

```java
static TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);
    root.left = right;
    root.right = left;
    return root;
}
```

**Invariant:** each subtree is fully inverted *before* being reattached as the swapped child — the recursive calls must happen before the swap, not after, or the swap operates on not-yet-inverted subtrees (which still produces a correct final tree either way for this particular problem, but stating the order deliberately is the narration discipline being drilled, not just arriving at a correct answer). **Complexity:** O(n) time, O(h) space.

## LC 98 — Validate Binary Search Tree

```java
static boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}
private static boolean validate(TreeNode node, Integer lowerExclusive, Integer upperExclusive) {
    if (node == null) return true;
    if (lowerExclusive != null && node.val <= lowerExclusive) return false;
    if (upperExclusive != null && node.val >= upperExclusive) return false;
    return validate(node.left, lowerExclusive, node.val) && validate(node.right, node.val, upperExclusive);
}
```

**The trap this problem is chosen to drill:** checking only that a node's value is greater than its immediate left child and less than its immediate right child is **not sufficient** — a node can locally satisfy that check while still violating a bound established further up the tree. The correct invariant carries an exclusive lower and upper bound *down* through the recursion, tightened at each step, not just compared against the immediate parent. Verified against a tree specifically constructed to be locally-consistent but globally invalid (see `Main.java`). **Complexity:** O(n) time, O(h) space.

## LC 235 — Lowest Common Ancestor of a BST

```java
static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    TreeNode cur = root;
    while (cur != null) {
        if (p.val < cur.val && q.val < cur.val) cur = cur.left;
        else if (p.val > cur.val && q.val > cur.val) cur = cur.right;
        else return cur;
    }
    throw new IllegalArgumentException("p and q must both exist in the tree");
}
```

**Invariant:** BST ordering means the split point where `p` and `q` diverge (one goes left, one goes right, or one of them *is* the current node) is exactly the LCA — no need for a generic tree-search LCA algorithm. **Complexity:** O(h) time, O(1) space — the BST-specific version is both faster and simpler than the general binary-tree LCA algorithm, worth naming as a follow-up-proofing detail.

## LC 102 — Binary Tree Level Order Traversal

```java
static List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Deque<TreeNode> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        result.add(level);
    }
    return result;
}
```

**Invariant:** capturing `queue.size()` *before* the inner loop starts is what separates "one level" from "everything currently in the queue" — the inner loop enqueues the *next* level's nodes while iterating exactly `levelSize` times over the *current* level, so the two levels never mix. **Complexity:** O(n) time, O(n) space (worst case, the widest level).

## LC 199 — Binary Tree Right Side View

```java
static List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    Deque<TreeNode> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (i == levelSize - 1) result.add(node.val);
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
    }
    return result;
}
```

**Invariant:** this is LC 102's exact BFS skeleton, with one added condition — the *last* node dequeued at each level (`i == levelSize - 1`) is precisely the one visible from the right side. Recognizing this as "LC 102 plus one condition" rather than a new problem from scratch is itself the pattern-recognition skill this problem is chosen to drill. **Complexity:** O(n) time, O(n) space.

## Verification — real, not asserted

```
  PASS  LC104 maxDepth on the canonical example
  PASS  LC104 maxDepth of empty tree
  PASS  LC226 root.left is now the old root.right
  PASS  LC226 root.right is now the old root.left
  PASS  LC226 grandchild swapped correctly
  PASS  LC98 simple valid BST
  PASS  LC98 catches the ancestor-bound trap (local check alone would miss this)
  PASS  LC235 LCA of 2 and 8 is the root
  PASS  LC235 LCA of 2 and 4 is 2 itself (ancestor case)
  PASS  LC102 level order on the canonical example
  PASS  LC199 right side view on the canonical example
Week 3 tree suite: 11/11 assertions passed
```

Full output and reproduce instructions: `practice/java/week-03/trees/README.md`. Compiled and run with `javac`/`java` on OpenJDK 21.0.12.

## Exit check

- [ ] All 6 problems solved with a written retrospective, invariant stated *before* code was written
- [ ] Can explain, unprompted, why the LC 98 local-check approach is insufficient
- [ ] Can explain LC 199 as "LC 102 plus one condition" rather than as an unrelated problem
