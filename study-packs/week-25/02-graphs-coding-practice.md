---
title: "Coding Practice — Graphs, Full Closure (T-1409)"
week: 25
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Graphs, Full Closure (T-1409)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 17/22 to **22/22 — full register closure**, the third D14 coding-pattern closed to 100% (after Tries/T-1415 in Week 21 and Dynamic Programming/T-1411 earlier this same week). Weeks 4, 12, 20, and 24 progressively covered basic traversal, Dijkstra, Union-Find, Eulerian paths, and directed-graph edge-direction tracking — all via adjacency-list representations. This final batch specifically closes a representation gap the pre-work audit found: **none of the prior 17 solutions used a 2D-grid graph**, plus adds a tree-to-graph conversion technique and two transformed-graph BFS problems.

---

## Problem 1 — LC 130 Surrounded Regions

**Pattern:** flood-fill *inward from the border*, marking everything reachable from an edge as safe — the inverse of the more intuitive "flood-fill outward from an interior region" approach, which would require tracking every region separately.

```java
static void solve(char[][] board) {
    for (border cells) markSafe130(board, r, c); // DFS from every border 'O', marking '#'
    for (every cell) {
        if (board[r][c] == 'O') board[r][c] = 'X'; // never reached from border: truly surrounded
        else if (board[r][c] == '#') board[r][c] = 'O'; // reached from border: restore
    }
}
```

**Retrospective:** the key inversion is recognizing that a region of `'O'`s is *safe from capture* if and only if it touches the board's border somewhere — rather than finding every enclosed region and checking whether it's surrounded (which requires enumerating regions first), starting the flood-fill from every border cell and marking everything reachable directly identifies exactly the safe cells in one pass; anything left as plain `'O'` afterward is, by construction, unreachable from any border and therefore truly surrounded. Using a temporary sentinel character (`'#'`) rather than a separate `visited` boolean array lets the algorithm distinguish three states (safe-and-marked, still-unprocessed-`O`, and `X`) using the board itself as the state store, avoiding extra memory. This is the first 2D-grid-as-a-graph problem in this pattern's coverage — every prior graph solution used an adjacency list, but a grid's implicit 4-directional adjacency is just as much a graph, worth stating explicitly when recognizing the pattern. **Complexity:** O(rows·cols) time and space.

## Problem 2 — LC 417 Pacific Atlantic Water Flow

**Pattern:** flood-fill from *both* oceans' borders inward, reversing the water-flow direction (water flows to lower-or-equal neighbors, so flowing *backward* from an ocean means only expanding to neighbors with height ≥ the current cell) — the answer is the intersection of what's reachable from each ocean's border.

```java
static void dfs417(int[][] heights, int r, int c, boolean[][] reachable) {
    reachable[r][c] = true;
    for (each neighbor) {
        if (!reachable[nr][nc] && heights[nr][nc] >= heights[r][c]) {
            dfs417(heights, nr, nc, reachable);
        }
    }
}
// called once per border cell, separately for pacific[][] (top+left borders) and atlantic[][] (bottom+right borders)
```

**Retrospective:** computing "can this cell's water reach the Pacific" directly, for every cell, would require tracing a downhill path from each cell independently — potentially re-walking large portions of the grid for every starting cell. Reversing the question to "starting from the ocean, which cells could water have flowed *from*" turns it into two single flood-fills (one per ocean), each cell visited once per ocean, and the final answer is simply the set of cells marked reachable in *both* flood-fills. This "reverse the direction of flow and fill from the destination(s) rather than from every possible source" technique directly parallels LC 130's border-inward inversion (Problem 1) — both problems get meaningfully simpler by flood-filling from a small, fixed set of starting points rather than testing reachability from every cell individually. **Complexity:** O(rows·cols) time — each cell visited at most once per ocean, so twice total, still linear in grid size.

## Problem 3 — LC 863 All Nodes Distance K in Binary Tree (tree-to-graph conversion)

**Pattern:** convert the tree into an undirected graph by recording each node's parent via a DFS pre-pass, then run a standard BFS layer-by-layer from the target — a distinct technique from every prior graph problem in this register, since the input isn't natively a graph at all.

```java
Map<TreeNode, TreeNode> parents = new HashMap<>();
buildParentMap(root, null, parents); // one DFS pass recording every node's parent

// then BFS from target, treating left, right, AND parent as neighbors:
if (node.left != null && visited.add(node.left)) queue.offer(node.left);
if (node.right != null && visited.add(node.right)) queue.offer(node.right);
TreeNode parent = parents.get(node);
if (parent != null && visited.add(parent)) queue.offer(parent);
```

**Retrospective:** a binary tree's `left`/`right` pointers only support downward movement, but "distance K from a target" requires moving in every direction, including *up* toward ancestors and back down other branches — the parent-map pre-pass is what makes upward movement possible at all, effectively turning the directed tree into an undirected graph before the actual search begins. Once that conversion is done, the BFS itself is completely standard (identical in shape to any other unweighted-shortest-path BFS in this register) — the interesting part of this problem is entirely in recognizing *that* the conversion is necessary, not in the search algorithm that follows it. This is a Meta-favorite interview question specifically because it tests whether a candidate defaults to "trees only support parent-to-child traversal" or recognizes the reframing opportunity. **Complexity:** O(n) time (one DFS to build the parent map, one BFS bounded by tree size), O(n) space.

## Problem 4 — LC 1129 Shortest Path with Alternating Colors

**Pattern:** BFS with an augmented state — each queue entry tracks not just *which node* but also *which edge color was just used to arrive there*, since the next edge must be the opposite color.

```java
int[][] dist = new int[n][2]; // dist[node][0] = shortest via red-last, dist[node][1] = via blue-last
queue.offer(new int[]{0, 0}); // start, pretending we arrived via "red" so the first move can be blue... 
queue.offer(new int[]{0, 1}); // ...and vice-versa, covering both possible first moves
```

**Retrospective:** a plain BFS over the raw graph would be wrong here, because it has no way to enforce the alternating-color constraint — two different paths to the same node via different "last color used" are genuinely different states for the purposes of *what can happen next*, even though they land on the same physical node. Augmenting the BFS state from just `node` to `(node, lastColorUsed)` is the standard technique whenever a shortest-path problem has a constraint that depends on the history of the path, not just the current position — the same underlying idea as Week 20's LC 787 (Cheapest Flights Within K Stops), which augmented state with "stops used so far" instead of "last color." Seeding the queue with *both* starting colors for node 0 correctly allows the very first edge taken to be either color. **Complexity:** O(V + E) time — each `(node, color)` state visited at most once, so at most 2V states total.

## Problem 5 — LC 815 Bus Routes (transformed-graph BFS: vertices are buses, not stops)

**Pattern:** reframe the graph so its vertices are *bus routes* rather than physical stops — BFS then counts buses taken, which is exactly the quantity the problem asks to minimize.

```java
Map<Integer, List<Integer>> stopToRoutes = new HashMap<>(); // which routes serve each stop
// BFS layer-by-layer: each layer = "one more bus ride"
for (int route : stopToRoutes.getOrDefault(stop, List.of())) {
    if (visitedRoute[route]) continue;
    visitedRoute[route] = true;
    for (int nextStop : routes[route]) { /* every stop this route serves becomes reachable this layer */ }
}
```

**Retrospective:** a BFS over physical stops (treating each stop as a graph node, with edges between consecutive stops on the same route) would count *stops traveled*, not *buses boarded* — the wrong metric entirely, since the problem only cares about minimizing transfers, not distance. Reframing the search so each BFS "layer" corresponds to boarding one additional bus (marking an entire route as visited the moment any of its stops is reached, then making every stop on that route reachable in the same layer) directly counts buses instead. This is the same "identify what the BFS layer boundary should represent" insight that distinguishes LC 994 (Rotting Oranges, Week 20 — layers = minutes) from a plain reachability BFS — recognizing which quantity a BFS naturally counts, and reshaping the graph so that quantity is the one the problem actually asks for, is a reusable transferable skill across many BFS problems, not just this one. **Complexity:** O(sum of all route lengths) time — every stop across every route visited at most once via the route-level visited marking.

## Verification

```
$ cd practice/java/week-25/graphs/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC130 solve(4x4 board) captures interior O's, keeps border-connected O
  PASS  LC417 pacificAtlantic(5x5 example) = 7 cells
  PASS  LC417 pacificAtlantic(5x5 example) exact set matches known LeetCode answer
  PASS  LC863 distanceK(target=5, k=2) has 3 nodes
  PASS  LC863 distanceK(target=5, k=2) = {7,4,1}
  PASS  LC1129 shortestAlternatingPaths(n=3, only red 0->1) = [0,1,-1]
  PASS  LC1129 shortestAlternatingPaths(n=3, red 0->1, blue 2->1) = [0,1,-1]
  PASS  LC815 numBusesToDestination(2 routes) = 2
  PASS  LC815 numBusesToDestination(unreachable target) = -1
  PASS  LC815 numBusesToDestination(source==target) = 0
Week 25 — Graphs, final closure (LC 130, 417, 863, 1129, 815): 10/10 assertions passed
```
