---
title: "Coding Practice — Trees / BST (T-1408)"
week: 23
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Trees / BST (T-1408)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 7/16 to 12/16. Previous coverage (LC 104 Max Depth, LC 226 Invert Binary Tree, LC 98 Validate BST, LC 235/236 Lowest Common Ancestor, LC 102 Level Order Traversal, LC 199 Right Side View, across Weeks 3 and 11) established basic traversal, BST validation, and LCA. This batch adds a "combine subtree results at every node" DFS pattern, tree serialization, BST in-order traversal for rank queries, tree reconstruction from two traversal orders, and root-to-leaf path accumulation.

---

## Problem 1 — LC 543 Diameter of Binary Tree

**Pattern:** a single post-order DFS that computes both depth (the return value, needed by the parent) and diameter (a side-channel maximum, updated but never returned) in one pass.

```java
private static int depthForDiameter(TreeNode node, int[] diameter) {
    if (node == null) return 0;
    int leftDepth = depthForDiameter(node.left, diameter);
    int rightDepth = depthForDiameter(node.right, diameter);
    diameter[0] = Math.max(diameter[0], leftDepth + rightDepth);
    return 1 + Math.max(leftDepth, rightDepth);
}
```

**Retrospective:** the diameter (longest path between any two nodes, measured in edges) doesn't have to pass through the root, so it can't be computed by a single top-down formula — instead, every node is a *candidate* for being the path's highest point, and `leftDepth + rightDepth` at that node is exactly the length of the longest path passing through it. Using an `int[1]` array (or an instance field) as a mutable side-channel is the standard way to accumulate a running maximum across a recursive traversal in Java, where a return value alone can only carry the depth needed by the caller, not this separate diameter tracking. **Complexity:** O(n) time — a single pass, not the O(n²) that would result from a naive "compute depth from every node" approach.

## Problem 2 — LC 297 Serialize and Deserialize Binary Tree

**Pattern:** pre-order traversal with explicit null markers — the encoding is unambiguous enough to reconstruct the exact original tree shape without needing any additional size or structure information.

```java
private static void serializeHelper(TreeNode node, StringBuilder sb) {
    if (node == null) { sb.append("#,"); return; }
    sb.append(node.val).append(",");
    serializeHelper(node.left, sb);
    serializeHelper(node.right, sb);
}

private static TreeNode deserializeHelper(Deque<String> tokens) {
    String token = tokens.poll();
    if (token.equals("#")) return null;
    TreeNode node = new TreeNode(Integer.parseInt(token));
    node.left = deserializeHelper(tokens);
    node.right = deserializeHelper(tokens);
    return node;
}
```

**Retrospective:** a pre-order traversal *without* null markers is ambiguous — `[1,2,3]` could be a left-skewed chain or a balanced tree with 2 and 3 as children of 1 — but explicitly recording every `null` child as a `"#"` token removes that ambiguity entirely, since `deserializeHelper` can then reconstruct the exact recursive shape by consuming tokens in the same order they were written, recursing left-then-right just like the serializer did. This null-marker technique is what makes pre-order serialization viable at all; the alternative (level-order/BFS serialization, as LeetCode's own official examples use) needs a different marker discipline but solves the same fundamental ambiguity problem. **Complexity:** O(n) time and space for both directions.

## Problem 3 — LC 230 Kth Smallest Element in a BST

**Pattern:** iterative in-order traversal using an explicit stack, stopping as soon as the k-th node is visited — exploits the BST invariant that in-order traversal visits nodes in ascending sorted order.

```java
Deque<TreeNode> stack = new ArrayDeque<>();
TreeNode cur = root;
int count = 0;
while (cur != null || !stack.isEmpty()) {
    while (cur != null) { stack.push(cur); cur = cur.left; }
    cur = stack.pop();
    if (++count == k) return cur.val;
    cur = cur.right;
}
```

**Retrospective:** an in-order traversal of a BST always visits nodes in ascending value order — this is the BST invariant itself, restated as a traversal fact — so the k-th node visited *is* the k-th smallest value, with no sorting or auxiliary array needed. Doing this iteratively with an explicit stack (rather than recursively) allows early termination the instant the k-th node is found, which matters if `k` is small relative to the tree's size — a recursive in-order traversal would need an early-exit flag threaded through every call to get the same benefit, which is more error-prone than simply `return`-ing directly from an iterative loop. **Complexity:** O(h + k) time, where h is tree height — far better than an O(n) full traversal when k is small.

## Problem 4 — LC 105 Construct Binary Tree from Preorder and Inorder Traversal

**Pattern:** the pre-order sequence gives the root at each recursive level; the in-order sequence (indexed via a pre-built hash map) tells you exactly how many nodes belong in the left vs. right subtree.

```java
private static TreeNode buildHelper(int[] preorder, int[] preorderPos, int inLo, int inHi, Map<Integer, Integer> inorderIndex) {
    if (inLo > inHi) return null;
    int rootVal = preorder[preorderPos[0]++];
    TreeNode root = new TreeNode(rootVal);
    int mid = inorderIndex.get(rootVal);
    root.left = buildHelper(preorder, preorderPos, inLo, mid - 1, inorderIndex);
    root.right = buildHelper(preorder, preorderPos, mid + 1, inHi, inorderIndex);
    return root;
}
```

**Retrospective:** pre-order always visits the root before either subtree, so the *next* unconsumed pre-order value is always the current subtree's root — but pre-order alone can't tell you where the left subtree ends and the right begins; that boundary comes from finding the root's position in the in-order sequence, since everything to its left in-order is the entire left subtree (regardless of internal structure) and everything to its right is the entire right subtree. Precomputing a value-to-index hash map for the in-order array turns each boundary lookup into O(1) rather than an O(n) linear scan per recursive call, which is what keeps the whole reconstruction at O(n) instead of O(n²). The shared, mutable `preorderPos[0]++` (an `int[1]` used the same way as Problem 1's diameter side-channel) is necessary because pre-order values must be consumed strictly left-to-right across the *entire* recursion, not independently reset per subtree. **Complexity:** O(n) time with the hash map (O(n²) worst case without it), O(n) space.

## Problem 5 — LC 112 Path Sum

**Pattern:** the target sum is decremented on the way down rather than accumulated on the way up — turning a "does any root-to-leaf path sum to X" check into a simple recursive remaining-balance check.

```java
static boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    if (root.left == null && root.right == null) return root.val == targetSum;
    int remaining = targetSum - root.val;
    return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
}
```

**Retrospective:** subtracting the current node's value from the target *before* recursing (rather than passing an accumulating running sum and comparing against the original target only at the leaf) means the leaf-level check is always the same simple `root.val == targetSum` comparison — the "how much has been spent so far" bookkeeping is folded entirely into what the target value itself represents at each recursive depth, which is a small but real simplification worth having ready in an interview rather than reaching for an accumulator parameter. The leaf check specifically requires *both* children to be null, not just one — a node with only one child isn't a leaf, and treating it as one would silently accept paths that don't actually reach the tree's bottom. **Complexity:** O(n) time worst case (every node visited once), O(h) space for recursion depth.

## Verification

```
$ cd practice/java/week-23/trees/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC543 diameterOfBinaryTree(5-node tree) = 3 (longest path 4-2-1-3, in edges)
  PASS  LC543 diameterOfBinaryTree(1,(2)) = 1
  PASS  LC297 serialize(deserialize(serialize(tree))) round-trips identically
  PASS  LC297 round-tripped root val = 1
  PASS  LC297 round-tripped tree structure preserved (root.right.right = 5)
  PASS  LC297 serialize/deserialize(null) round-trips to null
  PASS  LC230 kthSmallest(k=3) = 3
  PASS  LC230 kthSmallest(k=1) = 1 (leftmost)
  PASS  LC230 kthSmallest(k=6) = 6 (rightmost, largest)
  PASS  LC105 buildTree root = 3
  PASS  LC105 buildTree root.left = 9
  PASS  LC105 buildTree root.right = 20
  PASS  LC105 buildTree root.right.left = 15
  PASS  LC105 buildTree root.right.right = 7
  PASS  LC112 hasPathSum(target=22) -> true (5-4-11-2)
  PASS  LC112 hasPathSum(target=26) -> true (5-8-13)
  PASS  LC112 hasPathSum(target=100) -> false (no root-to-leaf path sums that high)
  PASS  LC112 hasPathSum(null tree) -> false
Week 23 — Trees (LC 543, 297, 230, 105, 112): 18/18 assertions passed
```
