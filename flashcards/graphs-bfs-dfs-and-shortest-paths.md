---
title: "Flashcards: Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find"
slug: graphs-bfs-dfs-and-shortest-paths
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2108"
canonical: ../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md
last_updated: 2026-09-07
---

# Flashcards: Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md`](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)

## Card: Why Dijkstra fails under an extra path constraint

**Prompt:**
Why does plain Dijkstra give a wrong answer on Cheapest Flights Within K Stops (LC 787), a shortest-path problem with an extra stop-count constraint?

**Answer:**
Dijkstra greedily commits to the cheapest path to a node as final the moment it's popped from the heap, with no mechanism to track how many edges that path used. A costlier path using fewer stops (valid under the k-stops constraint) can be permanently pruned in favor of a cheaper-but-too-long path — Dijkstra has no way to represent "prefer cheap, but only among paths using at most k+1 edges."

**Why it matters:**
A deliberate, common interview trap — it produces a plausible-looking but wrong answer, not an obvious crash, exactly why "shortest path → Dijkstra" pattern-matching without checking assumptions fails here.

**Common trap:**
Applying Dijkstra to any shortest-path problem with an additional non-cost constraint.

**Related:**
[syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)

## Card: Dijkstra's non-negative-weight assumption

**Prompt:**
What assumption does Dijkstra's algorithm rely on, and why does a negative edge weight break it?

**Answer:**
Dijkstra relies on all edge weights being non-negative — once a node is popped from the heap, its distance is treated as final, which is only safe if no later edge could produce a shorter path to it. A negative edge can retroactively improve an already-finalized distance, which Dijkstra has no mechanism to detect or correct.

**Why it matters:**
Distinguishes genuine mechanism-level understanding from template-level Dijkstra implementation.

**Common trap:**
Producing a correct Dijkstra implementation while being unable to state what assumption makes it correct.

**Related:**
[syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md](../syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md)

## Card: Union-Find's cycle-detection correctness

**Prompt:**
In Redundant Connection (LC 684), why does the first edge whose two endpoints already share a root (via Union-Find) correctly identify the redundant edge?

**Answer:**
A tree with `n` nodes has exactly `n-1` edges, so being handed `n` edges for `n` nodes means exactly one edge closes a cycle. Processing edges in input order and calling `union()` on each, the first edge where `union()` returns `false` (the endpoints are already connected through some other path) is provably that redundant edge — and because edges are processed in the input's own order, this identifies the specific edge the problem expects.

**Why it matters:**
Union-Find with path compression achieves amortized O(α(n)) per operation — effectively O(1) for any realistic input size.

**Common trap:**
Claiming Union-Find is literally O(1) per operation rather than the more precise O(α(n)) amortized bound.

**Related:**
[syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)

## Card: Why Prim's beats Kruskal's on a dense implicit graph

**Prompt:**
In Min Cost to Connect All Points (LC 1584), why is Prim's array-based form the right choice over Kruskal's, given the graph is dense and implicit (every pair of points has an edge)?

**Answer:**
An explicit edge list here has O(n²) edges, so Kruskal's approach would need to sort all of them first, costing O(n² log n) just for the sort. Prim's array-based form never materializes an edge list at all, staying at O(n²) total — the correct choice specifically because the graph's density makes an explicit edge list itself the bottleneck.

**Why it matters:**
A genuine, non-obvious algorithm-selection criterion driven by graph density, not a stylistic preference between MST algorithms.

**Common trap:**
Choosing Kruskal's for a dense, implicitly-defined graph and paying an unnecessary O(n² log n) sort cost.

**Related:**
[syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)

## Card: Why Rotting Oranges seeds all sources at once

**Prompt:**
In Rotting Oranges (LC 994), why must the BFS queue be seeded with every initially-rotten cell at once, rather than running single-source BFS repeatedly?

**Answer:**
Rot spreads from every source simultaneously in the real scenario the problem describes; seeding all sources before the first BFS layer runs is what makes each BFS layer correspond to exactly one elapsed minute. Processing sources sequentially would produce a completely different, wrong layer count.

**Why it matters:**
The correct model whenever a problem describes something spreading from multiple origins at once, in parallel, rather than sequentially.

**Common trap:**
Running single-source BFS repeatedly for a "spreads simultaneously from multiple origins" problem.

**Related:**
[syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)
