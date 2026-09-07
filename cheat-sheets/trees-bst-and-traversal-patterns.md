---
title: "Cheat Sheet: Trees, BSTs, and Traversal Patterns"
slug: trees-bst-and-traversal-patterns
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2107
canonical: ../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md
last_updated: 2026-09-06
---

# Trees, BSTs, and Traversal Patterns

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md`](../syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md)

## Core Mental Model

Tree problems are less about the traversal order (pre/in/post-order is a means to an end) and more about correctly structuring a recursive traversal to compute the right thing at the right point: a value the parent needs (the return value) versus a value that needs to accumulate across the whole traversal (a side-channel).

## Essential Definitions

- **Binary Search Tree (BST) invariant** — every node's left subtree contains only smaller values, its right subtree only larger; a direct consequence is that in-order traversal always visits nodes in ascending sorted order.
- **Return value vs. side-channel** — the return value carries what a parent needs (e.g., subtree depth); a mutable side-channel (an `int[1]` array in Java, since there are no output parameters) tracks something that accumulates globally across every node visited (e.g., a running maximum diameter).
- **Serialization ambiguity** — a traversal sequence without explicit null markers can't distinguish a left-skewed chain from a small balanced tree with the same values; recording every `null` child as a sentinel token removes the ambiguity.
- **Reconstruction from two traversal orders** — pre-order reveals which node is the root at each level; in-order reveals the left/right subtree split; neither order alone carries both pieces of information.

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Computation needs a value the parent needs, a value that accumulates globally, or both | Decide return-value vs. side-channel *before* writing recursive code |
| BST problem framed in terms of rank or sorted position ("k-th smallest," "closest value") | In-order traversal — the invariant does the sorting for free |
| Reconstruction recursion would otherwise search a traversal array repeatedly | Precompute a value-to-index hash map first |
| Tree must round-trip through serialization exactly | Explicit null-marker sentinel tokens |

**Complexity:** Diameter of Binary Tree O(n); Serialize/Deserialize O(n) time and space both directions; Kth Smallest in BST O(h + k); Build Tree from Preorder/Inorder O(n) with a hash map (O(n²) without it); Path Sum O(n) worst case, O(h) space.

## Common Pitfalls

- Trying to compute a globally-accumulating value (like diameter) purely through return values, without a side-channel — leads to convoluted code or an incorrect algorithm that only checks the root as a candidate.
- Serializing a tree without null markers, producing an ambiguous encoding that can't reliably round-trip.
- Reconstructing a tree with a linear scan for each root's in-order position instead of a precomputed hash map — functionally correct but silently degrades from O(n) to O(n²).
- Checking only one child for `null` to identify a leaf — a node with exactly one child is not a leaf.

## Interview Answer Skeleton

**30-sec:** Most tree recursion problems come down to one structural decision made before writing any code: does this computation need a value the parent needs (return value), a value that accumulates globally (side-channel), or state passed down from parent to child?

**2-min:** Diameter of Binary Tree needs both — the return value carries each subtree's depth, while a side-channel tracks the running maximum of `leftDepth + rightDepth` at every node, since the diameter's highest point isn't necessarily the root and can't be computed by a single top-down formula. A single post-order pass computes each node's depth exactly once while also checking it as a diameter candidate — no redundant O(n²) work.

**Whiteboard:** Draw a small tree, and at each node write two things: what flows up to the parent (return value) and what's tracked in a side box off to the side (the accumulator). Circle where the two interact.

**Staff-level framing:** The tree-serialization null-marker technique transfers directly to any system that must persist or transmit a hierarchical structure and reconstruct it exactly — a configuration tree, an org chart, a nested permission structure — where an encoding that can't distinguish "no data here" from "this branch just ends here" silently produces a different structure on deserialization, a real, hard-to-detect data-integrity bug.

## Production Warning Signs

- A tree-reconstruction function (from preorder and inorder traversals) works correctly on small test trees but times out or runs unacceptably slowly on a tree with a few thousand nodes.
- Diagnose: check whether the in-order root-position lookup uses a linear scan inside the recursive call rather than a precomputed hash map built once before recursion starts — a linear scan per call silently degrades the algorithm from O(n) to O(n²), invisible on small trees but dominant at real scale.

## Related

- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
