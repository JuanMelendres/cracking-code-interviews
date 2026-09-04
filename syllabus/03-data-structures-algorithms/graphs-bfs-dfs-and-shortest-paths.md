---
title: "Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find"
slug: graphs-bfs-dfs-and-shortest-paths
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2108
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
  - heaps-top-k-and-k-way-merge.md
related:
  - heaps-top-k-and-k-way-merge.md
  - trees-bst-and-traversal-patterns.md
practice: ../../practice/java/week-20/graphs-advanced/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-20/05-graphs-advanced-coding-practice.md
---

# Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-20/05-graphs-advanced-coding-practice.md` — real, compiled, executed code (`practice/java/week-20/graphs-advanced/`), re-verified on OpenJDK 21.0.12 while writing this chapter (11/11 assertions passing).

This is Master Topic Register **T-1409** — the single highest-weighted pattern in the entire coding-interview register (IWI 6.25, ⭐, very-high frequency). A tree ([Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md)) is a special case of a graph — one with no cycles and exactly one path between any two nodes; this chapter covers what changes once cycles, multiple paths, and edge weights are allowed.

## 1. Why This Matters

Graphs model an enormous range of real problems — networks, dependencies, maps, social connections — and this pattern's five core algorithm families (BFS, DFS, topological sort, shortest-path, and union-find) each answer a structurally different question. The actual interview skill is rarely "can you implement Dijkstra's algorithm" (a memorizable template); it's correctly recognizing *which* of the five questions a new, unfamiliar problem is actually asking, since choosing the wrong one (as this chapter's Problem 5 demonstrates directly) produces a plausible-looking but wrong answer, not an obvious crash.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — graph algorithm complexities are typically expressed in terms of both vertices (V) and edges (E), a slightly richer vocabulary than single-variable array complexity. [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) — Dijkstra's algorithm (Section 4) is a direct application of a heap's repeated-extreme-extraction pattern.

## 3. Foundation (L1)

**A graph is a set of nodes (vertices) connected by edges**, which can be directed (one-way) or undirected (two-way), and weighted (each edge has a cost) or unweighted. Unlike a tree, a graph can have cycles (a path that returns to where it started) and multiple distinct paths between the same two nodes — both possibilities that every graph algorithm in this chapter has to account for explicitly, usually via a "visited" tracking structure to avoid infinite loops or redundant work.

**BFS (breadth-first search) explores a graph level by level, all nodes at distance 1 before any node at distance 2** — the natural choice whenever a problem asks for the shortest path in an *unweighted* graph, since BFS guarantees the first time a node is reached is via the fewest possible edges. **DFS (depth-first search) explores as deep as possible along one path before backtracking** — the natural choice for reachability questions, cycle detection, and topological ordering.

## 4. Core Concepts (L2)

**Dijkstra's algorithm** (Section 7, Problem 1, Network Delay Time) finds shortest paths from one source in a graph with non-negative edge weights, using a min-heap to always expand the currently-closest unvisited node next — a direct application of [Heaps'](heaps-top-k-and-k-way-merge.md#3-foundation-l1) repeated-extreme-extraction pattern to graph distances. Once a node is popped from the heap, its shortest distance is final and can never be improved by a later relaxation — this greedy finality is exactly what non-negative edge weights guarantee, and exactly what breaks the moment negative weights are allowed (Section 5).

**Union-Find (disjoint-set)** (Section 7, Problem 2, Redundant Connection) answers a different question entirely: not "what's the shortest path," but "are these two nodes already connected, and if not, connect them" — efficiently, without traversing the whole graph on every query. It's the standard tool for cycle detection while building a graph incrementally, and for counting connected components.

**Minimum Spanning Tree (MST) algorithms** (Section 7, Problem 3, Min Cost to Connect All Points) find the cheapest set of edges that connects every node with no cycles. Two standard approaches exist — Prim's (grow one connected tree, always adding the cheapest edge leaving it) and Kruskal's (consider all edges globally, cheapest first, using Union-Find to skip any that would create a cycle) — and choosing between them depends on graph density (Section 5).

**Multi-source BFS** (Section 7, Problem 4, Rotting Oranges) seeds a BFS queue with *every* starting point simultaneously, rather than running single-source BFS repeatedly — the correct model whenever a problem describes something spreading from multiple origins at once, in parallel, rather than sequentially.

**Bellman-Ford-style bounded relaxation** (Section 7, Problem 5, Cheapest Flights Within K Stops) is required whenever a shortest-path problem has an *additional constraint* (like a maximum number of edges/stops) that plain Dijkstra has no way to represent — Dijkstra's greedy "once popped, final" property has no notion of "but only if it took few enough stops," making it structurally the wrong tool the moment that constraint exists.

## 5. How It Works Internally (L3)

**Why plain Dijkstra fails on Cheapest Flights Within K Stops, precisely**: Dijkstra greedily commits to the cheapest path found to each node as final the moment that node is popped from the heap, with no mechanism to track *how many edges* that cheapest path used. A costlier path that happens to use fewer stops (and is therefore valid under the `k`-stops constraint) can be permanently pruned in favor of a cheaper path that uses too many stops and is therefore invalid — Dijkstra has no way to represent "prefer cheap, but only among paths using at most k+1 edges" in its core greedy step. Bellman-Ford-style bounded relaxation solves this by running exactly `k+1` rounds of relaxation, where each round only allows paths using one additional edge, and *cloning* the distance array each round so a round's own updates don't leak into and corrupt the same round's other relaxations — directly modeling "at most k+1 edges used" as a hard structural limit on the algorithm itself, not a property to check after the fact.

**Union-Find's cycle-detection correctness** (Redundant Connection): a tree with `n` nodes has exactly `n-1` edges; being handed `n` edges for `n` nodes means exactly one edge closes a cycle. Processing edges in order and calling `union()` on each, the first edge whose two endpoints already share a root (meaning `union()` returns `false`, since they're already connected through some other path) is provably the redundant one — and because edges are processed in the input's own order, this correctly identifies the *specific* edge the problem expects, not merely *some* cycle-closing edge. With path compression (`find()` recursively flattening the tree toward the root as a side effect of each lookup), the amortized cost per operation is O(α(n)) — the inverse Ackermann function, smaller than 5 for any input size realistic enough to ever be tested, making Union-Find effectively O(1) per operation in practice.

**Why Prim's array-based form, not Kruskal's, is the right choice for Min Cost to Connect All Points**: the graph here is *dense* — every pair of points has an implicit edge (their Manhattan distance) — so an explicit edge list has O(n²) edges, and Kruskal's approach would need to sort all of them first, costing O(n² log n) just for the sort. Prim's array-based form (repeatedly scanning for the minimum-distance unvisited node, rather than using a heap) never materializes an edge list at all, staying at O(n²) total — the correct choice specifically because the graph's *density* makes an explicit edge list itself the bottleneck, a genuine, non-obvious algorithm-selection criterion worth being able to state precisely.

**Multi-source BFS's layer-count-equals-elapsed-time correctness**: seeding *every* initially-rotten cell into the queue before the first BFS layer runs, rather than picking one and running single-source BFS to completion before starting the next, is what makes each BFS layer correspond to exactly one elapsed minute in the real simulation — rot spreads from every source simultaneously in the real scenario the problem describes, and the algorithm must model that concurrency structurally, not approximate it by processing sources sequentially (which would produce a completely different, wrong layer count).

## 6. Practical Usage

- **Ask "unweighted or weighted, and are there any extra constraints beyond raw cost" before choosing an algorithm** — unweighted-shortest-path is plain BFS; weighted with non-negative edges and no extra constraints is Dijkstra; weighted with an extra constraint (stop count, as here) needs Bellman-Ford-style bounded relaxation instead.
- **Reach for Union-Find specifically for incremental connectivity questions** — "does adding this edge create a cycle," "how many connected components exist after these unions" — rather than a full graph traversal on every query.
- **Check graph density before choosing Prim's vs. Kruskal's for an MST problem** — a dense, implicit graph (Section 5) favors Prim's array-based form; a sparse, explicitly-listed-edges graph favors Kruskal's with Union-Find.

## 7. Examples

**Problem 1 — LC 743, Network Delay Time.**

```java
static int networkDelayTime(int[][] times, int n, int k) {
    Map<Integer, List<int[]>> graph = new HashMap<>();
    for (int[] t : times) graph.computeIfAbsent(t[0], x -> new ArrayList<>()).add(new int[]{t[1], t[2]});
    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{k, 0});
    boolean[] visited = new boolean[n + 1];
    while (!pq.isEmpty()) {
        int[] cur = pq.poll();
        int node = cur[0];
        if (visited[node]) continue;
        visited[node] = true;
        for (int[] edge : graph.getOrDefault(node, List.of())) {
            int next = edge[0], weight = edge[1];
            if (dist[node] + weight < dist[next]) { dist[next] = dist[node] + weight; pq.offer(new int[]{next, dist[next]}); }
        }
    }
    int maxDist = 0;
    for (int i = 1; i <= n; i++) { if (dist[i] == Integer.MAX_VALUE) return -1; maxDist = Math.max(maxDist, dist[i]); }
    return maxDist;
}
```

**Retrospective:** the min-heap always expands the closest unvisited node next — the greedy property making Dijkstra correct for non-negative weights. **Complexity:** O(E log V).

**Problem 2 — LC 684, Redundant Connection.**

```java
static class UnionFind {
    int[] parent;
    UnionFind(int n) { parent = new int[n + 1]; for (int i = 0; i <= n; i++) parent[i] = i; }
    int find(int x) { if (parent[x] != x) parent[x] = find(parent[x]); return parent[x]; }
    boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        parent[ra] = rb;
        return true;
    }
}
```

**Retrospective:** see Section 5's cycle-detection argument. **Complexity:** O(n · α(n)) — effectively linear.

**Problem 3 — LC 1584, Min Cost to Connect All Points.**

```java
static int minCostConnectPoints(int[][] points) {
    int n = points.length;
    boolean[] inMst = new boolean[n];
    int[] minDist = new int[n];
    Arrays.fill(minDist, Integer.MAX_VALUE);
    minDist[0] = 0;
    int totalCost = 0;
    for (int i = 0; i < n; i++) {
        int u = -1;
        for (int j = 0; j < n; j++) if (!inMst[j] && (u == -1 || minDist[j] < minDist[u])) u = j;
        inMst[u] = true;
        totalCost += minDist[u];
        for (int v = 0; v < n; v++) {
            if (!inMst[v]) {
                int dist = Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]);
                minDist[v] = Math.min(minDist[v], dist);
            }
        }
    }
    return totalCost;
}
```

**Retrospective:** see Section 5's density-driven algorithm-choice argument. **Complexity:** O(n²) time, O(n) space.

**Problem 4 — LC 994, Rotting Oranges.**

```java
static int orangesRotting(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    Queue<int[]> queue = new LinkedList<>();
    int freshCount = 0;
    for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) {
        if (grid[r][c] == 2) queue.offer(new int[]{r, c});
        else if (grid[r][c] == 1) freshCount++;
    }
    if (freshCount == 0) return 0;
    int minutes = -1;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!queue.isEmpty()) {
        int size = queue.size();
        minutes++;
        for (int i = 0; i < size; i++) {
            int[] cur = queue.poll();
            for (int[] d : dirs) {
                int nr = cur[0]+d[0], nc = cur[1]+d[1];
                if (nr>=0 && nr<rows && nc>=0 && nc<cols && grid[nr][nc]==1) {
                    grid[nr][nc] = 2; freshCount--; queue.offer(new int[]{nr, nc});
                }
            }
        }
    }
    return freshCount == 0 ? minutes : -1;
}
```

**Retrospective:** see Section 5's multi-source-BFS argument. **Complexity:** O(rows × cols).

**Problem 5 — LC 787, Cheapest Flights Within K Stops.**

```java
static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int round = 0; round <= k; round++) {
        int[] next = dist.clone();
        for (int[] flight : flights) {
            int u = flight[0], v = flight[1], w = flight[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < next[v]) next[v] = dist[u] + w;
        }
        dist = next;
    }
    return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
}
```

**Retrospective:** see Section 5's Dijkstra-fails-here argument. This is a deliberate, common interview trap. **Complexity:** O(k · E).

## 8. Common Mistakes

- **Applying Dijkstra to a shortest-path problem with an additional non-cost constraint** (Section 5, Section 7 Problem 5) — a plausible-looking but wrong answer, not an obvious crash, which is exactly what makes it a favored interview trap.
- **Applying Dijkstra to a graph with negative edge weights at all** — Dijkstra's greedy "once popped, final" guarantee requires non-negative weights; a negative edge can retroactively improve an already-finalized distance, which Dijkstra's algorithm has no mechanism to detect or correct.
- **Choosing Kruskal's for a dense, implicitly-defined graph** (Section 5) — technically correct, but paying an unnecessary O(n² log n) sort cost when Prim's array-based form achieves the same result in O(n²).
- **Running single-source BFS repeatedly instead of seeding all sources at once** for a "spreads simultaneously from multiple origins" problem (Section 5) — produces an incorrect layer/time count, not a crash.

## 9. Edge Cases

- **An unreachable node** (Network Delay Time's own verified unreachable-node case, correctly returning `-1`) — every shortest-path algorithm in this chapter must distinguish "genuinely unreachable" from "just far away."
- **No fresh oranges to begin with** (Rotting Oranges' verified zero-fresh case, correctly returning `0` immediately) — the algorithm must not enter its main loop at all in this case.
- **A stop-count constraint that excludes the globally cheapest path** (Cheapest Flights' own verified case, where a 3-edge path is cheaper but exceeds the stop limit, and a costlier 2-edge path is the correct answer) — exactly the scenario Section 5/8 names as the reason plain Dijkstra fails here.

## 10. Performance Implications

Real, executed verification from `practice/java/week-20/graphs-advanced/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC743 networkDelayTime(4 nodes, start=2) = 2
  PASS  LC743 networkDelayTime unreachable node -> -1
  PASS  LC684 findRedundantConnection triangle -> [2,3]
  PASS  LC684 findRedundantConnection 5-edge case -> [1,4]
  PASS  LC1584 minCostConnectPoints(5 points) = 20
  PASS  LC1584 minCostConnectPoints(3 points) = 18
  PASS  LC994 orangesRotting -> 4 minutes
  PASS  LC994 orangesRotting unreachable fresh -> -1
  PASS  LC994 orangesRotting no fresh oranges -> 0
  PASS  LC787 findCheapestPrice(k=1 stop) = 600 (the 3-edge 0->1->2->3 path needs 2 stops, exceeding k=1; only 0->2->3 qualifies)
  PASS  LC787 findCheapestPrice(k=1 stop, cheaper 2-hop path exists) = 200
Week 20 — Graphs Advanced (LC 743, 684, 1584, 994, 787): 11/11 assertions passed
```

The performance lesson specific to this chapter isn't just Big-O — it's that the *correct* algorithm choice given a graph's specific properties (density, edge-weight sign, extra constraints) often changes the achievable complexity class entirely, not just its constant factor: Prim's vs. Kruskal's (Section 5) is a density-driven choice with a real O(n² log n)-vs-O(n²) consequence, and Dijkstra-vs-Bellman-Ford (Section 5, Section 8) is a correctness-driven choice, not merely a performance one.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| BFS (unweighted shortest path) | Simple, guarantees fewest-edges path | Doesn't account for edge weights at all |
| Dijkstra (weighted, non-negative) | Correct, efficient shortest path with weights | Fails silently-wrong on negative edges or extra path constraints |
| Bellman-Ford-style bounded relaxation | Correctly handles an edge-count constraint Dijkstra cannot represent | O(k·E), strictly worse than Dijkstra's O(E log V) when the constraint isn't actually needed |
| Union-Find | Near-O(1) amortized connectivity queries | Doesn't answer "what's the path," only "are these connected" |
| Prim's (array-based) | O(n²), no edge-list materialization — ideal for dense graphs | Worse than Kruskal's for genuinely sparse graphs |
| Kruskal's (with Union-Find) | Efficient for sparse, explicitly-listed-edge graphs | Requires sorting all edges upfront — costly for dense, implicit graphs |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is treating "which graph algorithm applies here" as a question with a precise, checkable answer — edge weights present or not, negative weights possible or not, extra constraints beyond cost, graph density — rather than reaching for whichever algorithm was most recently practiced. Cheapest Flights Within K Stops (Section 5, Section 8) is specifically designed to catch candidates who pattern-match to "shortest path → Dijkstra" without checking whether Dijkstra's actual guarantees still hold under this problem's specific extra constraint.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, this chapter's five algorithm families map directly onto real infrastructure problems: multi-source BFS (Section 5) is the same technique behind flood-fill-style cache invalidation or dependency-propagation systems where multiple changes originate simultaneously; Union-Find is the standard technique behind real-time connectivity tracking in network topology or social-graph systems; Dijkstra and its bounded-relaxation variant underlie real routing and pathfinding systems, where the "extra constraint" that breaks plain Dijkstra (Section 5, Section 8) is exactly analogous to a real routing system needing to respect a maximum-hop-count or maximum-cost-per-segment business rule alongside raw distance. Recognizing a production requirement as "this is graph problem X, with constraint Y that rules out the naive algorithm" is the direct, load-bearing transfer from this entire chapter.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a graph-algorithm-specific root cause.

> Planned reference: a future `production-cookbook/` entry covering a real routing or dependency-resolution system that applied plain Dijkstra (or an equivalent greedy shortest-path approach) to a problem with an unstated extra constraint — silently producing a subtly wrong, not obviously broken, result — would be a natural, non-duplicative addition connecting this chapter's Section 5/8 trap directly to a genuine incident.

## 15. Interview Questions

### Question 1 — Given a weighted graph and a source node, find the shortest path to every other node. What algorithm would you use, and what assumption does it rely on?

**Why interviewers ask it.** It's the canonical Dijkstra-recognition question, but the second half — naming the non-negative-weight assumption — is what actually distinguishes genuine understanding from template memorization.

**Expected answer.** Dijkstra's algorithm, using a min-heap to always expand the currently-closest unvisited node next. It relies on all edge weights being non-negative — once a node is popped from the heap, its distance is treated as final, which is only safe if no later edge could ever produce a shorter path to it, a guarantee that negative edges would break.

**Minimum acceptable answer.** Names Dijkstra and produces a broadly correct implementation, even without stating the non-negative-weight assumption unprompted.

**Strong Senior answer.** States the non-negative-weight assumption unprompted, and can explain concretely why a negative edge breaks the "once popped, final" guarantee — a node's distance could later be improved by a path through a negative edge discovered after that node was already finalized.

**Staff-level extension.** Names Bellman-Ford as the correct alternative for graphs with negative edges (though not negative *cycles*, which make "shortest path" undefined), and states the trade-off precisely: Bellman-Ford's O(V·E) is strictly worse than Dijkstra's O(E log V), a real cost paid only when negative edges are actually possible.

**Common mistakes.** Producing a correct Dijkstra implementation while being unable to state what assumption makes it correct, revealing template-level rather than mechanism-level understanding.

**Follow-up questions.** "What if the graph had an additional constraint — say, at most k edges used?" (Exactly this chapter's Section 5/8 trap — Dijkstra's greedy finality can't represent that constraint; bounded Bellman-Ford-style relaxation is needed instead.)

### Question 2 — Why does Union-Find with path compression have such a fast (near-constant) amortized time complexity?

**Why interviewers ask it.** It tests whether "inverse Ackermann function" is understood as a real, near-constant bound with practical significance, or recited as an impressive-sounding term without grasping why it applies.

**Expected answer.** Path compression makes every `find()` call flatten the path from the queried node directly to its root as a side effect, so future `find()` calls on any node along that path become O(1) directly. Combined across many operations, the amortized cost per operation converges to O(α(n)), the inverse Ackermann function — a function that grows so slowly it's smaller than 5 for any input size that could ever realistically occur, making Union-Find operations effectively constant-time in practice, even though the true bound isn't literally O(1).

**Minimum acceptable answer.** States that path compression makes `find()` fast over repeated calls, even without naming the inverse Ackermann function specifically.

**Strong Senior answer.** Names the inverse Ackermann function and can state, at least approximately, why it's considered "effectively constant" for practical purposes (its value is bounded by a small constant for any conceivable real input size).

**Staff-level extension.** Connects this to a broader principle about amortized data structures: the *first* few operations on a Union-Find structure can be relatively more expensive (the tree hasn't been flattened yet), but the cost amortizes favorably specifically because path compression is a permanent, cumulative optimization each `find()` call contributes to, benefiting every subsequent call — a genuinely different amortization shape than, say, `ArrayList`'s periodic resize cost.

**Common mistakes.** Claiming Union-Find is literally O(1) per operation, rather than the more precise (and still, for practical purposes, equivalent) O(α(n)) amortized bound.

**Follow-up questions.** "What if you used union by rank/size in addition to path compression?" (Combining both techniques achieves the tightest known bound, though path compression alone is already sufficient for the "effectively constant" practical claim.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-20/graphs-advanced/) yourself and confirm the same 11/11 assertions pass.
- This pattern has additional real, already-solved problems: Number of Islands, Clone Graph, Course Schedule I/II (topological sort), Number of Provinces, and Word Ladder across earlier weeks' practice code — study Course Schedule specifically as this repository's topological-sort representative, a graph technique not directly exercised by this chapter's own five problems.
- Implement Kruskal's algorithm (using the Union-Find implementation from Section 7, Problem 2) for Min Cost to Connect All Points, and compare its real, measured wall-clock performance against the existing Prim's-based solution at a few different point-count sizes — confirm whether Section 5's density-driven prediction (Prim's should win here) actually holds on real data.

## 17. Debugging Exercises

**Symptom:** a routing or shortest-path feature that uses Dijkstra's algorithm returns a path that satisfies a specific business rule (e.g., a maximum number of transfers/hops) in some cases but silently violates it in others, without ever throwing an error.

**Diagnose:** this is exactly Section 5/8's trap made real — Dijkstra's core greedy property ("once a node is popped, its distance is final") has no mechanism to represent an edge-count or hop-count constraint at all, so it will happily return the globally cheapest path even when that path uses more hops than the business rule allows, with no error signal, since Dijkstra doesn't even know the constraint exists. Confirm by checking whether the implementation ever tracks hop count as part of a node's state (it shouldn't, in a naive Dijkstra implementation) and whether failing cases specifically correlate with the globally cheapest path exceeding the hop limit while a valid, slightly costlier path exists — exactly the verified test case in Section 7/9. The fix is replacing Dijkstra with the bounded Bellman-Ford-style relaxation from Section 5/7, not patching Dijkstra's output after the fact.

## 18. Design Exercises

**Design constraint:** design a service-dependency deployment orderer that must determine a valid order to deploy a set of services, given a list of "service A must deploy before service B" constraints, and must detect and reject any circular dependency before attempting deployment.

Design this using topological sort (referenced in Section 16 as this repository's separate worked example) directly: model each "A before B" constraint as a directed edge, and state explicitly why a cycle in this graph means no valid deployment order exists at all — connecting this to Union-Find's cycle-detection role (Section 4/5) as an alternative check specifically for whether *any* cycle exists (though topological sort's own algorithm, via Kahn's method or DFS-based cycle detection, is the more standard and more informative choice here, since it also produces the actual valid order, not just a yes/no cycle answer). Name the real production framing: this is precisely the problem a CI/CD pipeline's own dependency-ordering logic must solve correctly before any deployment sequence is safe to execute.

## 19. Further Reading

- [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) — the heap mechanics Dijkstra's algorithm (Section 4) is built directly on top of.
- [Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md) — a tree is a special-case graph (acyclic, single-path-between-any-two-nodes); several traversal ideas there generalize directly to this chapter's BFS/DFS.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, the difference between BFS and DFS, and why a graph can have cycles a tree cannot | [Section 3](#3-foundation-l1) |
| L2 | Choose the correct algorithm family (BFS, Dijkstra, Union-Find, MST, bounded relaxation) for a new, unfamiliar graph problem based on its specific properties | [Interview Question 1](#question-1--given-a-weighted-graph-and-a-source-node-find-the-shortest-path-to-every-other-node-what-algorithm-would-you-use-and-what-assumption-does-it-rely-on) |
| L3 | Derive why plain Dijkstra fails under an extra path constraint, and explain Union-Find's amortized near-constant complexity precisely | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real routing/shortest-path bug as a Dijkstra-vs-constrained-relaxation mismatch (Section 17), and design a real dependency-ordering system using topological sort deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
