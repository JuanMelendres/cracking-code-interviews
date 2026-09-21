---
title: "Cheat Sheet: Matrix and Grid Traversal Patterns"
slug: matrix-and-grid-traversal-patterns
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2121
canonical: ../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md
last_updated: 2026-09-20
---

# Matrix and Grid Traversal Patterns

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md`](../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md)

## Core Mental Model

A 2D grid is an implicit graph — each cell a node, up to four orthogonal neighbors its edges. Three distinct sub-patterns recur: in-place geometric transformation (rotate), boundary-following traversal (spiral), and connected-component discovery (flood fill) — recognize which one a new problem actually is before writing code.

## Essential Definitions

- **Direction-vector idiom** — `dr = {-1,1,0,0}, dc = {0,0,-1,1}` looped together, the standard way to enumerate a cell's 4 orthogonal neighbors.
- **Transpose-then-reverse** — the geometric decomposition of 90-degree clockwise matrix rotation into two simpler, well-known in-place operations.
- **Shrinking-boundary spiral** — track `top`/`bottom`/`left`/`right`, traverse one edge, shrink that boundary inward, repeat — no visited-set needed.
- **Connected-region-bounded recursion depth** — a recursive flood fill's call-stack depth scales with the size of the largest single connected region, not the grid's overall dimensions.

## Decision Table

| Sub-pattern | Technique | Extra space |
|---|---|---|
| Rotate 90° clockwise, in place | Transpose, then reverse each row | O(1) |
| Spiral traversal | Four shrinking boundaries | O(1) beyond output |
| Flood fill / connected regions (recursive) | DFS via recursion | O(region size) call stack — real, measured risk |
| Flood fill / connected regions (production-safe) | DFS/BFS via explicit `ArrayDeque` | O(region size) heap, no call-stack risk |

## Common Pitfalls

- Treating a recursive flood fill as "done" once it passes small tests — real, measured: 14,000-cell single region OK, 16,000 cells `StackOverflowError`, on this machine's default `-Xss`.
- Omitting the two mid-loop boundary guards in spiral traversal — the most common bug, double-counts cells on non-square/single-row/single-column input.
- Allocating a second matrix when the problem asks for in-place rotation.

## Interview Answer Skeleton

**30-sec:** Grid problems split into three shapes: in-place rotation (transpose + reverse), spiral traversal (shrinking boundaries), and flood fill (grid-as-implicit-graph DFS/BFS). The one genuinely grid-specific gotcha: recursive flood fill's stack depth scales with connected-region size, not grid size — real, measured `StackOverflowError` risk on one sufficiently large region.

**2-min:** Add the transpose-then-reverse geometric proof for rotation, the shrinking-boundary termination argument for spiral, and the real numbers: 14,000-cell region recurses fine, 16,000 overflows; an explicit `ArrayDeque` stack/queue handles 2,000,000 cells with zero risk, because pending work moves from the call stack (fixed, small) to the heap (bounded only by available memory).

**Staff-level framing:** Any recursive traversal over externally-supplied, unbounded-shape data has an implicit depth assumption — state and defend it explicitly (or convert to iteration) rather than discovering the limit for the first time in production.

## Related

- syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md
- syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md
- syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md
