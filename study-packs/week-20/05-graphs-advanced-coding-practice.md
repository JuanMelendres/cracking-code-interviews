---
title: "Coding Practice — Graphs, Advanced (T-1409)"
week: 20
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Graphs, Advanced (T-1409)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** This is the register's single highest-weight D14 topic (⭐, IWI 6.25); this batch brings its coverage from 6/22 to 11/22, adding the shortest-path, MST, and multi-source-BFS sub-patterns not yet represented by the six problems from earlier weeks (Number of Islands, Clone Graph, Course Schedule I/II, Number of Provinces, Word Ladder).

---

## Problem 1 — LC 743 Network Delay Time

**Pattern:** Dijkstra's shortest path with a min-heap.

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

**Retrospective:** the min-heap always expands the currently-closest unvisited node next, which is exactly the greedy property that makes Dijkstra correct for non-negative edge weights — once a node is popped from the heap, its distance is final and can never be improved by a later relaxation. **Complexity:** O(E log V) time with a binary heap.

## Problem 2 — LC 684 Redundant Connection

**Pattern:** explicit Union-Find (disjoint set) with path compression.

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

static int[] findRedundantConnection(int[][] edges) {
    UnionFind uf = new UnionFind(edges.length);
    for (int[] edge : edges) if (!uf.union(edge[0], edge[1])) return edge;
    return new int[0];
}
```

**Retrospective:** a tree with `n` nodes has exactly `n-1` edges; the input here has `n` edges, meaning exactly one edge closes a cycle — `union()` returning `false` (both endpoints already share a root) identifies that exact edge, and because edges are processed in input order, the *first* such edge found is the one LeetCode expects. **Complexity:** O(n · α(n)) time — effectively linear, since `α` (inverse Ackermann) is smaller than 5 for any realistic input size.

## Problem 3 — LC 1584 Min Cost to Connect All Points

**Pattern:** Prim's Minimum Spanning Tree on an implicit, dense (all-pairs) graph.

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

**Retrospective:** Prim's (array-based, not heap-based) is the right choice specifically because this graph is *dense* — every pair of points has an implicit edge (Manhattan distance) — so building and sorting an explicit edge list for Kruskal's would cost O(n² log n) just to sort n² edges, while Prim's array-based form stays O(n²) by never materializing the edge list at all. **Complexity:** O(n²) time, O(n) space.

## Problem 4 — LC 994 Rotting Oranges

**Pattern:** multi-source BFS — seed the queue with every initially-rotten cell simultaneously.

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

**Retrospective:** seeding *every* rotten orange into the queue before the first BFS layer, rather than picking one and running single-source BFS repeatedly, is what makes the layer count equal the actual elapsed minutes — rot spreads from all sources simultaneously in real life, and the BFS must model that concurrency, not process one source's full spread before starting the next. **Complexity:** O(rows × cols) time — every cell enqueued at most once.

## Problem 5 — LC 787 Cheapest Flights Within K Stops

**Pattern:** Bellman-Ford-style bounded relaxation — plain Dijkstra is the wrong tool here.

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

**Retrospective:** this is a deliberate, common interview trap — a plain Dijkstra greedily commits to the cheapest path found so far and would incorrectly prune a costlier-but-within-the-stop-limit path in favor of a cheaper one that uses too many stops, since Dijkstra has no notion of "stop count" as a constraint. Bounding the relaxation to exactly `k+1` rounds (cloning `dist` each round so a round's own updates don't leak into the same round) correctly models "at most k+1 edges used." **Complexity:** O(k · E) time.

## Verification

```
$ cd practice/java/week-20/graphs-advanced/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
