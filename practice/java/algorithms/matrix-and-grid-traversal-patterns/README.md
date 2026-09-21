# Matrix and Grid Traversal Patterns — Real Demo

Backs [`syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md`](../../../../syllabus/03-data-structures-algorithms/matrix-and-grid-traversal-patterns.md) (T-2121).

Pure JDK, no dependencies. Tested on OpenJDK 21.0.12.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **In-place matrix rotation (transpose + row-reverse) really does match a brute-force,
  separately-allocated rotated copy**, checked programmatically across seven sizes
  (1×1 through 200×200), not just the textbook 3×3 example.
- **Spiral traversal's shrinking-boundary technique is really correct across square,
  non-square, single-row, single-column, and single-cell shapes** — the degenerate
  shapes are exactly where an off-by-one in the boundary conditions tends to surface.
- **The real, common gotcha specific to grid traversal: a naive recursive flood fill's
  call-stack depth grows with the connected component's size, not the grid's overall
  dimensions.** One large solid region (a "lake"), not a maze of small islands, forces
  one recursive call per cell. Measured directly, one fresh JVM process per size (see
  the note below on why): on this machine's default thread stack, recursive DFS
  handles a 14,000-cell single region fine and overflows at 16,000 cells — a real,
  narrow, reproducible breaking point, not a theoretical concern.
- **The standard fix — an explicit, heap-allocated `ArrayDeque` as a stack (DFS) or
  queue (BFS) — really does remove the call-stack bound entirely.** Both correctly
  count all 2,000,000 cells of a solid region an order of magnitude past where
  recursion broke, with zero risk of `StackOverflowError` regardless of connected
  component size, because the traversal's "pending work" now lives on the heap
  (bounded by available memory, not by a fixed per-thread stack reservation) instead
  of the call stack.

## A methodological note: why the sweep uses separate JVM processes

An earlier, same-process version of this sweep produced a genuinely misleading result:
recursive DFS overflowed at 20,000 cells, then **succeeded** at 40,000, then overflowed
again at 80,000 and 160,000 — non-monotonic, and wrong to present as a clean "breaking
point." The real cause: repeated same-process calls to the same hot recursive method
eventually trigger JIT compilation partway through the sweep, and the compiled version's
stack-frame cost differs from the interpreted version's — an artifact of *when* JIT
compilation happened to kick in during the sweep, not a real property of the algorithm
or the grid size. `run.sh` now invokes `GridFloodFillStackDemo` once per size as a
completely fresh `java` process (each starting cold, interpreted, every time), which
produced the clean, monotonic, reproducible breaking point reported above.
