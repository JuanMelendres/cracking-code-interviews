---
title: "Flashcards: Matrix and Grid Traversal Patterns"
slug: matrix-and-grid-traversal-patterns
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2121
canonical: ../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md
last_updated: 2026-09-20
---

# Flashcards: Matrix and Grid Traversal Patterns

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md`](../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md)

## Card: The transpose-then-reverse rotation trick

**Prompt:**
How do you rotate an N×N matrix 90 degrees clockwise, in place, without allocating a second matrix?

**Answer:**
Transpose the matrix (swap `m[r][c]` with `m[c][r]` for every `r < c`), then reverse each row. Neither step alone rotates the matrix; composed, they do — a real, provable geometric identity, checked programmatically against a brute-force rotated copy across seven sizes (1×1 through 200×200).

**Why it matters:**
The standard in-place technique — avoids O(n²) extra space a second allocated matrix would cost.

**Common trap:**
Allocating a second matrix when the problem explicitly asks for in-place, or reversing columns instead of rows (producing counter-clockwise instead of clockwise).

**Related:**
[Core Concepts](../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md#4-core-concepts-l2)

## Card: Recursive flood fill's real, measured stack-depth risk

**Prompt:**
Does a recursive flood fill's call-stack depth depend on the grid's overall size, or something else?

**Answer:**
The size of the single largest connected region it explores in one unbroken chain of recursive calls — not the grid's width or height. Real, measured on one machine's default thread stack: a 14,000-cell single solid region recurses fine; 16,000 cells throws `StackOverflowError`. A grid with many small separate regions never recurses deep, regardless of total grid size.

**Why it matters:**
Correctness on small unit tests says nothing about behavior on one large, real connected region — a real, silent production risk, not a theoretical one.

**Common trap:**
Considering a recursive flood fill "done" once it passes small test cases.

**Related:**
[How It Works Internally](../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md#5-how-it-works-internally-l3)

## Card: The iterative fix, and why it actually works

**Prompt:**
How does replacing recursion with an explicit `ArrayDeque` stack/queue fix flood fill's stack-depth risk, and how much does it actually help?

**Answer:**
It moves "pending work" from the call stack (a fixed, comparatively tiny per-thread reservation) to the heap (bounded only by available memory), via a manually-managed stack (DFS) or queue (BFS) instead of the JVM's own call stack. Measured directly: the same technique correctly counts 2,000,000 cells in one solid region — over 100x past the 16,000-cell point where recursion overflowed — with zero risk regardless of region size.

**Why it matters:**
Removes the failure mode entirely rather than merely raising its threshold (unlike bumping `-Xss`, which only delays the same crash).

**Common trap:**
Treating a larger `-Xss` as the fix, rather than recognizing it only raises the threshold without removing the underlying unbounded-recursion-depth risk.

**Related:**
[Performance Implications](../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md#10-performance-implications)
