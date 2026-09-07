---
title: "Cheat Sheet: Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find"
slug: graphs-bfs-dfs-and-shortest-paths
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2108
canonical: ../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md
last_updated: 2026-09-06
---

# Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md`](../syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md)

## Core Mental Model

The interview skill is rarely "can you implement Dijkstra's algorithm" (a memorizable template) — it's correctly recognizing which of five structurally different questions (BFS, DFS, topological sort, shortest-path, union-find) a new, unfamiliar problem is actually asking, since choosing the wrong one produces a plausible-looking but wrong answer, not an obvious crash.

## Essential Definitions

- **BFS** — explores level by level; the natural choice for shortest path in an *unweighted* graph, since it guarantees the first time a node is reached is via the fewest possible edges.
- **DFS** — explores as deep as possible before backtracking; natural for reachability, cycle detection, and topological ordering.
- **Dijkstra's algorithm** — finds shortest paths from one source with non-negative edge weights, using a min-heap to always expand the closest unvisited node; once popped, a node's distance is final — a guarantee that requires non-negative weights.
- **Union-Find (disjoint-set)** — answers "are these two nodes already connected, and if not, connect them," efficiently, without a full traversal per query; standard for incremental cycle detection and counting components.
- **Bellman-Ford-style bounded relaxation** — required whenever a shortest-path problem has an extra constraint (e.g., max stops) that Dijkstra's greedy finality cannot represent.

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Unweighted graph, need shortest path | BFS |
| Weighted, non-negative edges, no extra constraints | Dijkstra |
| Weighted, but an extra constraint beyond raw cost (e.g., max stops) | Bellman-Ford-style bounded relaxation, not Dijkstra |
| Incremental "does adding this edge create a cycle" / connected components | Union-Find |
| Cheapest set of edges connecting every node (MST) | Prim's (dense/implicit graph) or Kruskal's + Union-Find (sparse, explicit edges) |
| Something spreads from multiple origins simultaneously | Multi-source BFS (seed all sources into the queue at once) |

**Complexity:** Network Delay Time (Dijkstra) O(E log V); Redundant Connection (Union-Find) O(n·α(n)), effectively linear; Min Cost to Connect All Points (Prim's array-based) O(n²); Rotting Oranges (multi-source BFS) O(rows×cols); Cheapest Flights Within K Stops (bounded relaxation) O(k·E).

## Common Pitfalls

- Applying Dijkstra to a shortest-path problem with an additional non-cost constraint (e.g., max stops) — produces a plausible-looking but wrong answer, not a crash, which is exactly why it's a favored interview trap.
- Applying Dijkstra to a graph with negative edge weights at all — a negative edge can retroactively improve an already-finalized distance, which Dijkstra cannot detect or correct.
- Choosing Kruskal's for a dense, implicitly-defined graph — technically correct, but pays an unnecessary O(n² log n) sort cost when Prim's array-based form achieves O(n²).
- Running single-source BFS repeatedly instead of seeding all sources at once for a "spreads simultaneously" problem — produces an incorrect layer/time count.

## Interview Answer Skeleton

**30-sec:** Graph problems are solved by first identifying which of BFS, DFS, Dijkstra, MST, or Union-Find the question is actually asking — edge weights present or not, negative weights possible, extra constraints beyond cost, and graph density are the checkable signals that decide it.

**2-min:** Cheapest Flights Within K Stops is the canonical trap: Dijkstra greedily commits to the cheapest path to a node as final the moment it's popped, with no mechanism to track how many edges that path used — a costlier-but-fewer-stops path can be permanently pruned in favor of a cheaper-but-too-many-stops path. Bellman-Ford-style bounded relaxation fixes this by running exactly `k+1` rounds, cloning the distance array each round so a round's updates don't leak into its own other relaxations.

**Whiteboard:** Draw a small weighted graph, run through Dijkstra's min-heap expansion showing "once popped, final," then add a stop-count constraint and show why a cheaper-but-too-many-stops path gets wrongly finalized — motivating the bounded-relaxation fix.

**Staff-level framing:** These five algorithm families map directly onto infrastructure problems: multi-source BFS is the technique behind flood-fill-style cache invalidation; Union-Find underlies real-time connectivity tracking in network topology; Dijkstra's bounded-relaxation variant underlies routing systems that must respect a max-hop-count business rule alongside raw distance — recognizing "this is graph problem X, with constraint Y that rules out the naive algorithm" is the direct, load-bearing transfer.

## Production Warning Signs

- A routing or shortest-path feature using Dijkstra's algorithm returns a path that satisfies a specific business rule (e.g., a maximum number of transfers/hops) in some cases but silently violates it in others, without ever throwing an error.
- Diagnose: Dijkstra's core greedy property has no mechanism to represent an edge-count or hop-count constraint, so it happily returns the globally cheapest path even when it uses more hops than allowed, with no error signal. Confirm by checking whether failing cases correlate with the globally cheapest path exceeding the hop limit while a valid, slightly costlier path exists. The fix is replacing Dijkstra with bounded Bellman-Ford-style relaxation, not patching Dijkstra's output after the fact.

## Related

- syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
- syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md
