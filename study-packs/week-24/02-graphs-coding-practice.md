---
title: "Coding Practice — Graphs, Final Batch (T-1409)"
week: 24
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Graphs, Final Batch (T-1409)

**6 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 11/22 to 17/22 (77%). Week 4 established basic traversal (Number of Islands, Clone Graph, Course Schedule I/II, Number of Provinces) and Week 12 added Word Ladder; Week 20 added Dijkstra, Union-Find, Prim's MST, multi-source BFS, and a Bellman-Ford-style shortest path. This batch adds graph two-coloring, Eulerian-path reconstruction, a second (application-focused) Union-Find problem, weighted-graph DFS, cycle-detection via three-state DFS, and directed-graph traversal with edge-direction tracking.

---

## Problem 1 — LC 785 Is Graph Bipartite?

**Pattern:** two-coloring via BFS — a graph is bipartite exactly when it can be colored with two colors such that no edge connects same-colored nodes, which is equivalent to having no odd-length cycle.

```java
static boolean isBipartite(int[][] graph) {
    int[] color = new int[n]; // 0 = uncolored, 1/-1 = the two colors
    for (int start = 0; start < n; start++) {
        if (color[start] != 0) continue;
        color[start] = 1;
        // BFS: color every neighbor the OPPOSITE color; if a neighbor is already
        // the SAME color as the current node, an odd cycle exists -> not bipartite
    }
}
```

**Retrospective:** the outer loop over every node (not just node 0) is necessary because the graph may be disconnected — each connected component must be independently checked and colored, since a component with no edges at all is trivially bipartite regardless of how other components look. Assigning `-color[node]` to every uncolored neighbor enforces the alternating-color constraint directly; the moment a neighbor is found to already share the current node's color, that's proof of an odd-length cycle (the two paths reaching that neighbor took a different number of steps of different parity), which is the standard equivalence this algorithm relies on. **Complexity:** O(V + E) time — each node and edge visited once.

## Problem 2 — LC 332 Reconstruct Itinerary (Eulerian path via Hierholzer's algorithm)

**Pattern:** Hierholzer's algorithm — the standard technique for finding an Eulerian path (a path using every edge exactly once), applied here with a priority queue to satisfy the "lexicographically smallest" tie-break.

```java
Deque<String> stack = new ArrayDeque<>();
stack.push("JFK");
while (!stack.isEmpty()) {
    String airport = stack.peek();
    PriorityQueue<String> destinations = graph.get(airport);
    if (destinations == null || destinations.isEmpty()) {
        route.addFirst(stack.pop()); // dead end reached: this airport is final in the route
    } else {
        stack.push(destinations.poll()); // greedily take the lexicographically smallest unused ticket
    }
}
```

**Retrospective:** this is a genuinely different graph-traversal shape than anything else in the register — a plain DFS that greedily always takes the smallest available destination can get "stuck" at a dead end before all tickets are used, because a greedy choice can consume an edge that was actually needed to complete the full Eulerian circuit later. Hierholzer's algorithm resolves this by *not* backtracking on failure — instead, when an airport truly runs out of outgoing tickets, that airport is prepended to the front of the final route (`addFirst`), which correctly produces the itinerary in reverse-completion order; airports that get "used up" earliest in this backtracking sense end up latest in the final path. This is a fundamentally different graph-search primitive from BFS/DFS-for-reachability or Dijkstra/Union-Find, worth explicitly naming as its own category. **Complexity:** O(E log E) time (E = number of tickets), the log factor from the priority queue.

## Problem 3 — LC 1319 Number of Operations to Make Network Connected (Union-Find)

**Pattern:** count connected components via Union-Find, then answer with `components - 1` — the register's first *application* of Union-Find beyond the mechanical cycle-detection use in Week 20's LC 684.

```java
static int makeConnected(int n, int[][] connections) {
    if (connections.length < n - 1) return -1; // not enough edges to possibly connect everything
    // union-find over connections, count remaining distinct roots
    return components - 1;
}
```

**Retrospective:** the early feasibility check (`connections.length < n - 1`) is a real, necessary optimization derived from graph theory, not a shortcut — connecting `n` nodes into a single component requires at least `n - 1` edges (a spanning tree's edge count), so fewer edges than that make full connectivity mathematically impossible regardless of which edges they are. Once that check passes, the answer is simply `components - 1`, because each redundant (cycle-forming) edge — the ones Union-Find's `union()` call returns `false` for — can be conceptually "unplugged" and reused to join exactly one pair of currently-disconnected components; this is the same insight as LC 684 (Redundant Connection, Week 20) applied in the opposite direction: there, a redundant edge was the answer itself, while here, the *count* of redundant edges bounds how many components can be merged. **Complexity:** O(n · α(n)) time — effectively linear.

## Problem 4 — LC 399 Evaluate Division (weighted graph + DFS)

**Pattern:** build a weighted, bidirectional graph from equations (each edge's weight is the ratio, with the reverse edge storing the reciprocal), then DFS multiplying edge weights along the path.

```java
graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, values[i]);
graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / values[i]); // reciprocal edge

private static double dfs399(... ) {
    if (cur.equals(target)) return 1.0;
    for (edge : graph.get(cur)) {
        double sub = dfs399(graph, edge.getKey(), target, visited);
        if (sub != -1.0) return edge.getValue() * sub;
    }
    return -1.0;
}
```

**Retrospective:** the key modeling insight is recognizing `a / b = k` implies both a forward edge (`a -> b` weighted `k`) and a reverse edge (`b -> a` weighted `1/k`) — without storing both directions, a query like `b / a` (asking to traverse the equation "backwards") would be unanswerable even though it's mathematically well-defined. The DFS multiplies edge weights *along the path* as it recurses, so by the time it reaches the target, the accumulated product is exactly the compounded ratio — this is a distinct DFS usage from every other traversal problem in the register, since most graph DFS here tracks reachability or ordering, not an accumulated numeric product along the path. Variables never seen in any equation (checked via `containsKey` before the DFS even starts) correctly return `-1.0` without needing a special case inside the recursion itself. **Complexity:** O(V·E) worst case per query (V = variables, E = equations) — acceptable given the typically small variable counts in this problem.

## Problem 5 — LC 802 Find Eventual Safe States (three-state DFS cycle detection)

**Pattern:** DFS with three states per node (unvisited, currently-on-the-recursion-stack, confirmed-safe) — the standard technique for detecting whether a node lies on or leads into any cycle, distinct from simple visited/unvisited reachability tracking.

```java
private static boolean dfs802(int[][] graph, int node, int[] state) {
    if (state[node] != 0) return state[node] == 2; // already resolved: return cached answer
    state[node] = 1; // mark as ON THE CURRENT PATH (not just "visited")
    for (int next : graph[node]) {
        if (!dfs802(graph, next, state)) return false; // any unsafe neighbor makes this node unsafe
    }
    state[node] = 2; // every neighbor resolved safely; this node is safe too
    return true;
}
```

**Retrospective:** a plain boolean `visited[]` array (as used in most other graph traversals in this register) can't distinguish "currently being explored on this DFS path" from "fully explored and known-safe" — but that distinction is exactly what's needed here: if the DFS ever revisits a node that's still marked state `1` (on the current path), that's a real cycle, which makes every node on that path unsafe. The three-state pattern (0/1/2, sometimes called white/gray/black coloring) is the standard, reusable way to detect cycles during a single DFS pass without a second traversal — a technique worth having ready any time a problem's "safety" or "validity" depends on cycle absence, not just plain reachability. **Complexity:** O(V + E) time — each node's state is resolved exactly once thanks to memoization via `state[]`.

## Problem 6 — LC 1466 Reorder Routes to Make All Paths Lead to City Zero

**Pattern:** DFS over a directed graph reinterpreted as undirected for traversal purposes, while separately tracking each edge's original direction to count how many need reversing.

```java
adjacency.computeIfAbsent(c[0], k -> new ArrayList<>()).add(new int[]{c[1], 1}); // original direction: needs reversal if traversed this way
adjacency.computeIfAbsent(c[1], k -> new ArrayList<>()).add(new int[]{c[0], 0}); // traversing "backwards": no reversal needed

private static int dfs1466(int node, ..., boolean[] visited) {
    for (int[] edge : adjacency.get(node)) {
        if (!visited[edge[0]]) {
            reorders += edge[1]; // 1 if this edge originally pointed AWAY from node zero's direction
            reorders += dfs1466(edge[0], ..., visited);
        }
    }
    return reorders;
}
```

**Retrospective:** the input graph is directed (roads only go one way, city-to-city), but the traversal needs to visit every city regardless of edge direction — so each connection is inserted into the adjacency structure *twice*, once per direction, with a flag distinguishing "this is the original direction" (1, meaning reversing it costs one operation) from "this is the reverse of the original direction" (0, meaning traversing it this way is already correct and costs nothing). Starting the DFS from city 0 and summing the flag values along every edge actually traversed directly counts how many original-direction edges point *away* from city 0 — each of those needs reversing so that every city ends up able to reach city 0. This "duplicate every directed edge with a direction-tracking flag, then traverse as if undirected" technique is a reusable pattern whenever a directed graph's traversal needs to ignore direction but its logic still needs to know what the original direction was. **Complexity:** O(V + E) time.

## Verification

```
$ cd practice/java/week-24/graphs/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC785 isBipartite(odd-cycle-containing graph) -> false
  PASS  LC785 isBipartite(bipartite 4-cycle) -> true
  PASS  LC332 findItinerary(4 tickets) = [JFK,MUC,LHR,SFO,SJC]
  PASS  LC332 findItinerary(5 tickets, lexical tie-break) = [JFK,ATL,JFK,SFO,ATL,SFO]
  PASS  LC1319 makeConnected(n=4, triangle+isolated) = 1
  PASS  LC1319 makeConnected(n=6, 2 components need merging) = 2
  PASS  LC1319 makeConnected(n=6, too few edges) = -1
  PASS  LC399 calcEquation(5 queries) matches [6.0, 0.5, -1.0, 1.0, -1.0]
  PASS  LC802 eventualSafeNodes(7-node graph) = [2,4,5,6]
  PASS  LC802 eventualSafeNodes(5-node graph) = [4]
  PASS  LC1466 minReorder(6 cities, example 1) = 3
  PASS  LC1466 minReorder(5 cities, example 2) = 2
  PASS  LC1466 minReorder(3 cities, already all point to 0) = 0
Week 24 — Graphs (LC 785, 332, 1319, 399, 802, 1466): 13/13 assertions passed
```
