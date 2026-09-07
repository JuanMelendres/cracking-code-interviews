---
title: "Flashcards: Trees, BSTs, and Traversal Patterns"
slug: trees-bst-and-traversal-patterns
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2107"
canonical: ../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md
last_updated: 2026-09-07
---

# Flashcards: Trees, BSTs, and Traversal Patterns

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md`](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)

## Card: Return value vs. side-channel in Diameter of Binary Tree

**Prompt:**
Diameter of Binary Tree (LC 543) needs both a value the parent needs and a value that accumulates globally. How does the solution structure this?

**Answer:**
The return value at each node carries its depth (what the parent needs to compute its own depth); a mutable side-channel (an `int[1]`, since Java has no output parameters) tracks the running maximum diameter across every node visited, since the diameter's highest point isn't necessarily the root and can't be computed by a single top-down formula alone.

**Why it matters:**
This "return value carries what the parent needs; side-channel carries what accumulates globally" split is the central structural decision behind most tree recursion problems.

**Common trap:**
Trying to compute a globally-accumulating value purely through return values, or assuming the diameter must pass through the root.

**Related:**
[syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)

## Card: Why in-order traversal of a BST is sorted

**Prompt:**
Why does an in-order traversal of a BST always visit nodes in ascending sorted order?

**Answer:**
The BST invariant — every node's left subtree holds only smaller values, its right subtree only larger — means in-order traversal (left, node, right) necessarily visits every smaller value before the current node and every larger value after it, at every level of the recursion simultaneously. This directly makes the k-th visited node the k-th smallest value (Kth Smallest Element in a BST, LC 230), with no sorting needed.

**Why it matters:**
A direct, powerful structural consequence, not a separate fact to memorize — enables O(h + k) rank queries instead of a full O(n) traversal plus sort.

**Common trap:**
Confusing in-order with pre-order or post-order and attaching the "sorted" claim to the wrong traversal.

**Related:**
[syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)

## Card: Why tree serialization needs null markers

**Prompt:**
Why does serializing a binary tree (LC 297) require explicit null markers rather than just recording node values in pre-order?

**Answer:**
A pre-order sequence without explicit null markers can't distinguish a left-skewed chain from a small balanced tree with the same values. Recording every null child as a sentinel token removes that ambiguity, letting the deserializer reconstruct the exact recursive shape by consuming tokens in the same recursive order they were written.

**Why it matters:**
Transfers directly to any system persisting a hierarchical structure (config trees, org charts) — an encoding that can't distinguish "no data here" from "this branch just ends here" silently produces a different structure on deserialization.

**Common trap:**
Serializing a tree without null markers, producing an ambiguous encoding that can't reliably round-trip.

**Related:**
[syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)

## Card: Why a hash map prevents O(n²) tree reconstruction

**Prompt:**
In reconstructing a tree from preorder and inorder traversals (LC 105), why does precomputing a value-to-index hash map matter for complexity?

**Answer:**
The root's position in the in-order sequence must be found to split left/right subtrees. A linear scan per recursive call degrades the algorithm from O(n) to O(n²); a precomputed hash map turns each boundary lookup into O(1) — exactly the difference between the two complexity classes.

**Why it matters:**
A direct application of hashing's O(1)-lookup principle to a tree-construction problem; the bug is invisible on small test trees but becomes the dominant cost at real scale.

**Common trap:**
Reconstructing a tree with a linear scan for each root's in-order position instead of a precomputed map.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: The one-child leaf-check bug

**Prompt:**
Why is checking only `root.left == null` (instead of both children) wrong when identifying a leaf node in Path Sum (LC 112)?

**Answer:**
A node with exactly one child is not a leaf; checking only one side silently accepts paths that don't actually reach the bottom of the tree. The leaf check requires both `root.left == null && root.right == null`.

**Why it matters:**
A subtle correctness bug that produces a plausible-looking but wrong answer for any tree containing single-child nodes.

**Common trap:**
Checking only one child's nullness to identify a leaf.

**Related:**
[syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)
