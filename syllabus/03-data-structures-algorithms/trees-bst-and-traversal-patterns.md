---
title: "Trees, BSTs, and Traversal Patterns"
slug: trees-bst-and-traversal-patterns
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2107
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
  - hashing-patterns-and-frequency-maps.md
related:
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-23/trees/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-23/04-trees-coding-practice.md
---

# Trees, BSTs, and Traversal Patterns

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-23/04-trees-coding-practice.md` — real, compiled, executed code (`practice/java/week-23/trees/`), re-verified on OpenJDK 21.0.12 while writing this chapter (18/18 assertions passing).

This is Master Topic Register **T-1408** (IWI 5.8, very-high frequency). This chapter covers pattern-level tree techniques beyond basic traversal (max depth, invert, level order — already covered in earlier practice weeks): combining subtree results, serialization, BST-specific rank queries, and reconstruction from traversal orders.

## 1. Why This Matters

Tree problems are less about a single algorithm and more about correctly structuring a recursive traversal to compute the right thing at the right point in the recursion — a value needed by the parent (the return value) versus a value that needs to accumulate across the whole traversal (a side-channel). Getting this structural decision right, consistently, across genuinely different-looking tree problems, is the actual interview skill; the traversal order itself (pre/in/post-order) is a means to that end, not the end itself.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — recursion-depth complexity counting, applied here to tree height rather than array length. [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — several techniques in this chapter (Section 4) use a hash map to convert an O(n) lookup into O(1).

## 3. Foundation (L1)

**A binary tree is a hierarchy of nodes, each with at most two children** — unlike a linked list's single chain, a tree branches, and most tree algorithms are naturally expressed recursively: solve the problem for a node's left and right subtrees, then combine those results at the current node.

**A Binary Search Tree (BST) adds one invariant on top of a plain binary tree: every node's left subtree contains only smaller values, and its right subtree contains only larger values.** This single invariant is what makes an in-order traversal of a BST always visit nodes in ascending sorted order — a direct, powerful consequence, not a separate fact to memorize.

## 4. Core Concepts (L2)

**A "return value carries what the parent needs; a side-channel carries what accumulates globally" is the central structural decision in most tree recursion problems.** Diameter of Binary Tree (Section 7, Problem 1) needs both: the return value carries each subtree's depth (what a parent needs to compute its own depth), while a mutable side-channel (an `int[1]` array, since Java has no output parameters) tracks the running maximum diameter across every node visited, since the diameter's highest point isn't necessarily the root and can't be computed by a single top-down formula alone.

**Tree serialization requires resolving a structural ambiguity**, not just recording values: a pre-order sequence without explicit null markers can't distinguish a left-skewed chain from a small balanced tree with the same values. Explicitly recording every `null` child as a sentinel token (Section 7, Problem 2) removes that ambiguity, since the deserializer can then reconstruct the exact recursive shape by consuming tokens in the same recursive order they were written.

**A BST's in-order-equals-sorted-order invariant directly enables O(h + k) rank queries** (Kth Smallest Element, Section 7, Problem 3) — since in-order traversal visits values in ascending order, the k-th visited node *is* the k-th smallest value, with no sorting or auxiliary array needed at all.

**Reconstructing a tree from two traversal orders** (Section 7, Problem 4) works because different traversal orders carry different, complementary information: pre-order reveals which node is the root at each recursive level (it's always visited first), while in-order reveals the split between left and right subtrees (everything before the root's position belongs to the left subtree, everything after to the right) — neither order alone carries both pieces of information.

## 5. How It Works Internally (L3)

**Diameter of Binary Tree's single-pass efficiency**: computing "depth from every node" independently, from scratch, at every node would cost O(n) per node, O(n²) overall — but a single post-order pass computes each node's depth exactly once (returned to its parent) while simultaneously checking, at that same node, whether `leftDepth + rightDepth` beats the running maximum, since that sum is exactly the length of the longest path passing through this specific node. Every node gets to be a "candidate highest point" for the diameter exactly once, during the single pass that also computes its own depth — no redundant work.

**BST reconstruction from preorder and inorder, precisely**: the *next* unconsumed value in the pre-order sequence is always the current subtree's root, since pre-order visits a node before either of its subtrees. But pre-order alone can't say where the left subtree ends and the right begins — that boundary comes from finding the root's value in the in-order sequence: everything to its left in-order is entirely the left subtree (regardless of that subtree's own internal structure), and everything to its right is entirely the right subtree. Precomputing a value-to-index hash map for the in-order sequence turns each boundary lookup into O(1) rather than an O(n) linear scan per recursive call — the exact difference between an O(n) and an O(n²) reconstruction, a direct, concrete application of [Hashing Patterns'](hashing-patterns-and-frequency-maps.md#3-foundation-l1) O(1)-lookup principle to a tree-construction problem. The shared, mutable pre-order index (an `int[1]`, the same side-channel technique as Diameter's `int[1]`) is necessary because pre-order values must be consumed strictly left-to-right across the *entire* recursion tree, not independently reset per subtree.

**Path Sum's "decrement on the way down" restructuring** (Section 7, Problem 5): rather than accumulating a running sum on the way down and comparing it against the target only at a leaf, subtracting the current node's value from the target *before* recursing means the leaf-level check is always the same simple comparison (`root.val == targetSum`) — the "how much has been spent so far" bookkeeping is folded entirely into what the target itself represents at each recursive depth, a small restructuring that removes the need for a separate accumulator parameter.

## 6. Practical Usage

- **Ask "does this computation need a value the parent needs, a value that accumulates globally, or both" before writing a single line of recursive code** — Section 4's central structural decision, and the fastest way to avoid a half-finished, awkwardly-shaped recursive solution.
- **Reach for in-order traversal specifically whenever a BST problem's answer is framed in terms of rank or sorted position** ("k-th smallest," "closest value to X") — the invariant does the sorting for free.
- **Precompute a value-to-index map before any tree-reconstruction recursion that would otherwise need to search a traversal array repeatedly** — the direct fix for the O(n²)-without-a-map pitfall in Section 5.

## 7. Examples

**Problem 1 — LC 543, Diameter of Binary Tree.**

```java
private static int depthForDiameter(TreeNode node, int[] diameter) {
    if (node == null) return 0;
    int leftDepth = depthForDiameter(node.left, diameter);
    int rightDepth = depthForDiameter(node.right, diameter);
    diameter[0] = Math.max(diameter[0], leftDepth + rightDepth);
    return 1 + Math.max(leftDepth, rightDepth);
}
```

**Retrospective:** see Section 5's single-pass argument. **Complexity:** O(n) time — a single pass.

**Problem 2 — LC 297, Serialize and Deserialize Binary Tree.**

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

**Retrospective:** see Section 4's ambiguity-resolution argument. **Complexity:** O(n) time and space, both directions.

**Problem 3 — LC 230, Kth Smallest Element in a BST.**

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

**Retrospective:** an in-order traversal of a BST always visits nodes in ascending order; doing this iteratively allows early termination the instant the k-th node is found. **Complexity:** O(h + k), h = tree height — far better than a full O(n) traversal when k is small.

**Problem 4 — LC 105, Construct Binary Tree from Preorder and Inorder Traversal.**

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

**Retrospective:** see Section 5's boundary-lookup argument. **Complexity:** O(n) with the hash map (O(n²) without it), O(n) space.

**Problem 5 — LC 112, Path Sum.**

```java
static boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    if (root.left == null && root.right == null) return root.val == targetSum;
    int remaining = targetSum - root.val;
    return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
}
```

**Retrospective:** see Section 5's decrement-on-the-way-down argument. The leaf check requires *both* children null — a one-child node isn't a leaf. **Complexity:** O(n) worst case, O(h) space for recursion.

## 8. Common Mistakes

- **Trying to compute a globally-accumulating value (like a diameter) purely through return values**, without a side-channel — leads to convoluted code that tries to smuggle two different pieces of information through one return type, or an incorrect algorithm that only checks the root as a candidate.
- **Serializing a tree without null markers**, producing an ambiguous encoding that can't be reliably deserialized back to the exact original shape (Section 4).
- **Reconstructing a tree from traversal orders with a linear scan for each root's in-order position** instead of a precomputed hash map — functionally correct but silently degrades from O(n) to O(n²) (Section 5).
- **Checking only `root.left == null` (or only `root.right == null`) to identify a leaf** — a node with exactly one child is not a leaf, and treating it as one silently accepts paths that don't actually reach the bottom of the tree (Section 7, Problem 5).

## 9. Edge Cases

- **A single-node tree** (Diameter's verified `(1,(2))` case, correctly returning `1`, not `0`) — the diameter formula must handle the case where one of the two subtrees is entirely absent.
- **Serializing and deserializing `null`** (the verified empty-tree round-trip case) — the encoding must handle "there is no tree at all," not just "there are no more children at some node."
- **`k` equal to the total number of nodes** (Kth Smallest's verified rightmost/largest case) — the traversal must correctly terminate exactly at the last node, not overrun or underrun by one.

## 10. Performance Implications

Real, executed verification from `practice/java/week-23/trees/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
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

Every solution here is O(n) or better (Kth Smallest's O(h + k) is strictly better than O(n) when k is small) — the practical performance lesson is that the hash-map precomputation in Problem 4 is the difference between O(n) and O(n²) for tree reconstruction, exactly mirroring [Hashing Patterns'](hashing-patterns-and-frequency-maps.md#10-performance-implications) own point about trading O(n) memory for a complexity-class improvement.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Recursive tree traversal | Simple, direct mapping from the tree's own recursive structure | Recursion depth equals tree height — a very unbalanced tree risks stack overflow ([How a Computer Executes a Program](../01-computer-science-foundations/how-a-computer-executes-a-program.md) covers exactly this limit) |
| Iterative traversal with an explicit stack | No call-stack depth limit; supports early termination mid-traversal (Kth Smallest) | More verbose, more error-prone bookkeeping than the equivalent recursive version |
| Pre-order + null markers for serialization | Simple, unambiguous, round-trips exactly | Larger encoded size than a scheme that can infer some structure without explicit markers |
| Hash-map-assisted tree reconstruction | O(n) instead of O(n²) | O(n) extra memory for the map |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is correctly identifying, before writing any code, whether a tree problem's needed computation flows *up* the recursion (a value the parent needs, like depth), *sideways* across it (a global accumulator, like diameter), or *down* it (state passed from parent to child, like Path Sum's remaining balance) — most candidate struggles on tree problems trace back to conflating these three flows rather than to the recursion itself being hard to write.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the serialization technique (Section 4/5) transfers directly to any system that needs to persist or transmit a hierarchical structure and reconstruct it exactly — a configuration tree, an org chart, a nested permission structure — where the same null-marker-or-equivalent unambiguity requirement applies: an encoding that can't distinguish "no data here" from "this branch just happens to end here" will silently produce a different structure on deserialization than what was serialized, a real, hard-to-detect data-integrity bug in any production serialization format design.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a tree-serialization-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real hierarchical-data serialization ambiguity bug (e.g., a nested configuration format that couldn't distinguish an empty child from a missing one) would be a natural, non-duplicative addition connecting this chapter's serialization-ambiguity lesson to a genuine production incident.

## 15. Interview Questions

### Question 1 — How would you find the diameter of a binary tree — the longest path between any two nodes?

**Why interviewers ask it.** It's the canonical test of the "return value vs. side-channel" structural decision (Section 4) — a candidate who tries to compute diameter with only a return value, or who assumes the diameter must pass through the root, reveals a real gap the problem is specifically designed to surface.

**Expected answer.** A single post-order DFS: the return value at each node is that node's depth (needed by its parent to compute its own depth), while a separate, mutable running maximum tracks `leftDepth + rightDepth` at every node visited, since that sum is the longest path passing through that specific node, and the true diameter doesn't necessarily pass through the root.

**Minimum acceptable answer.** Correctly identifies that diameter isn't necessarily root-to-leaf, even if the first attempt at an algorithm is O(n²) (computing depth independently from every node).

**Strong Senior answer.** Produces the O(n) single-pass solution directly, explicitly naming the return-value-vs-side-channel split.

**Staff-level extension.** Generalizes the return-value-vs-side-channel distinction to other tree problems unprompted, and can name at least one other problem (e.g., "maximum path sum," a well-known sibling problem) with the identical structural shape.

**Common mistakes.** Assuming the longest path must pass through the root, or attempting to return two different pieces of information (depth and diameter) awkwardly packed into one return value instead of using a side-channel.

**Follow-up questions.** "What if the tree could have up to a million nodes — any concerns?" (Recursion depth for a heavily unbalanced tree could risk `StackOverflowError` — a real, direct connection to [How a Computer Executes a Program's](../01-computer-science-foundations/how-a-computer-executes-a-program.md) own call-stack-depth measurement.)

### Question 2 — Why does an in-order traversal of a BST visit nodes in sorted order, and how does that help find the k-th smallest element efficiently?

**Why interviewers ask it.** It checks whether the BST invariant is understood as a structural fact with direct algorithmic consequences, rather than a memorized "in-order is sorted" rule applied without understanding why.

**Expected answer.** The BST invariant — every node's left subtree holds only smaller values, its right subtree only larger — means an in-order traversal (left, then node, then right) necessarily visits every value smaller than the current node before it, and every value larger after it, at every level of the recursion simultaneously. This directly means the k-th node visited during in-order traversal is the k-th smallest value overall, with no sorting needed. Doing this iteratively with an explicit stack (rather than recursively) allows returning immediately once the k-th node is found, achieving O(h + k) rather than a full O(n) traversal.

**Minimum acceptable answer.** States that in-order traversal of a BST is sorted, and uses that fact correctly, even without a full structural proof.

**Strong Senior answer.** Explains why the iterative version specifically enables early termination that a naive recursive version wouldn't get without extra plumbing (an early-exit flag threaded through every recursive call).

**Staff-level extension.** Connects this to a broader principle: choosing an iterative traversal specifically when early termination has real value (small k relative to a large tree) versus a recursive traversal when the full traversal is needed anyway — a genuine, reusable trade-off, not just a stylistic preference.

**Common mistakes.** Confusing in-order with pre-order or post-order and getting the "sorted" claim attached to the wrong traversal.

**Follow-up questions.** "What if you needed to find the k-th smallest element multiple times, with the tree changing between queries?" (A real, harder follow-up — augmenting each BST node with a subtree-size count enables O(h) rank queries even as the tree mutates, a genuinely different and more sophisticated technique worth knowing exists.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-23/trees/) yourself and confirm the same 18/18 assertions pass.
- This pattern has additional real, already-solved problems: LC 104 (Max Depth), LC 226 (Invert Binary Tree), LC 98 (Validate BST), LC 235/236 (Lowest Common Ancestor), LC 102 (Level Order Traversal), and LC 199 (Right Side View) across earlier weeks' practice code — study Validate BST specifically as a case where the "obvious" local check (each node's immediate children satisfy the BST property) is actually insufficient, requiring a range-bound check propagated down the recursion instead.
- Implement finding the diameter of a binary tree (Section 7, Problem 1) from scratch without looking at the existing solution, then compare your side-channel technique against the one shown.

## 17. Debugging Exercises

**Symptom:** a tree-reconstruction function (from preorder and inorder traversals) works correctly on small test trees but times out or runs unacceptably slowly on a tree with a few thousand nodes.

**Diagnose:** check whether the in-order root-position lookup uses a linear scan (`for` loop searching the in-order array for the root's value) inside the recursive call, rather than a precomputed hash map built once before recursion starts — Section 5/8 names this exact bug: a linear scan per recursive call silently degrades the algorithm from O(n) to O(n²), which is invisible on small test trees (where O(n) and O(n²) are both fast) but becomes the dominant cost at real scale. Confirm by checking whether the same lookup value is searched for repeatedly across different recursive calls, and by measuring wall-clock time at a few increasing tree sizes to check whether growth looks linear or quadratic — the same diagnostic technique [Algorithmic Complexity's own debugging exercise](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#17-debugging-exercises) recommends generally.

## 18. Design Exercises

**Design constraint:** design a file-system-like hierarchical permission structure that must be serialized to disk and reconstructed exactly, including correctly distinguishing a folder with no children from a folder whose children simply weren't included in a partial export.

Design the serialization format using this chapter's null-marker technique (Section 4/5) as the direct model: every node's encoding must explicitly and unambiguously represent "no child here" versus "child present," exactly the distinction Tree Serialization's `"#"` token makes for a simple binary tree. State explicitly what would go wrong without this — the same ambiguity Section 4 names for pre-order-without-markers — a partial export missing genuine children could deserialize into something indistinguishable from an intentionally childless folder, a real, silent data-loss risk if the encoding doesn't resolve this explicitly.

## 19. Further Reading

- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — the O(1)-lookup technique this chapter's tree-reconstruction problem (Section 5) applies directly to avoid an O(n²) degradation.
- [How a Computer Executes a Program](../01-computer-science-foundations/how-a-computer-executes-a-program.md) — the call-stack mechanics behind recursion depth, directly relevant to why a heavily unbalanced tree risks `StackOverflowError` during recursive traversal (Interview Question 1's follow-up).

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what a BST invariant guarantees and why in-order traversal visits nodes in sorted order | [Section 3](#3-foundation-l1) |
| L2 | Decide whether a tree problem needs a return-value flow, a side-channel accumulator, or a downward-passed state, before writing code | [Interview Question 1](#question-1--how-would-you-find-the-diameter-of-a-binary-tree--the-longest-path-between-any-two-nodes) |
| L3 | Derive the single-pass diameter argument and the hash-map-assisted tree-reconstruction argument precisely | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real O(n) vs. O(n²) tree-reconstruction regression (Section 17), and design a real hierarchical serialization format that avoids a genuine data-integrity ambiguity (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
