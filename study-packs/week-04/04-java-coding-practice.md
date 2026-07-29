---
title: "Java Coding Practice — Week 4"
week: 4
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 4

**5 graph problems. Union-Find and topological sort both implemented from scratch. All code compiled and executed — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [LC 200 — Number of Islands](#lc-200--number-of-islands)
2. [LC 133 — Clone Graph](#lc-133--clone-graph)
3. [LC 207 — Course Schedule](#lc-207--course-schedule)
4. [LC 210 — Course Schedule II](#lc-210--course-schedule-ii)
5. [LC 547 — Number of Provinces](#lc-547--number-of-provinces)
6. [Verification](#verification--real-not-asserted)

---

## LC 200 — Number of Islands

```java
static int numIslands(char[][] grid) {
    int count = 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[0].length; c++) {
            if (grid[r][c] == '1') { count++; sink(grid, r, c); }
        }
    }
    return count;
}
private static void sink(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
    grid[r][c] = '0';
    sink(grid, r + 1, c); sink(grid, r - 1, c); sink(grid, r, c + 1); sink(grid, r, c - 1);
}
```

**Invariant:** DFS flood-fill from any unvisited land cell marks the entire connected island as visited (by "sinking" it to water), so each island is counted exactly once regardless of its shape. **Complexity:** O(rows × cols) time and space (worst case, recursion stack for an all-land grid).

## LC 133 — Clone Graph

```java
static Node cloneGraph(Node node) {
    if (node == null) return null;
    return clone(node, new HashMap<>());
}
private static Node clone(Node node, Map<Node, Node> visited) {
    if (visited.containsKey(node)) return visited.get(node);
    Node copy = new Node(node.val);
    visited.put(node, copy);
    for (Node neighbor : node.neighbors) copy.neighbors.add(clone(neighbor, visited));
    return copy;
}
```

**Invariant:** the `visited` map from original node → clone must be populated *before* recursing into neighbors, or a cycle in the graph (common — this problem's canonical example is a cycle) causes infinite recursion. **Complexity:** O(V + E) time and space.

## LC 207 — Course Schedule

```java
static boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
    int[] state = new int[numCourses]; // 0=unvisited, 1=in-progress, 2=done
    for (int i = 0; i < numCourses; i++) {
        if (state[i] == 0 && hasCycle(graph, i, state)) return false;
    }
    return true;
}
private static boolean hasCycle(List<List<Integer>> graph, int node, int[] state) {
    state[node] = 1;
    for (int next : graph.get(node)) {
        if (state[next] == 1) return true; // back edge -> cycle
        if (state[next] == 0 && hasCycle(graph, next, state)) return true;
    }
    state[node] = 2;
    return false;
}
```

**Invariant:** a course sequence is possible if and only if the prerequisite graph has no cycle. Three-color DFS (unvisited / in-progress / done) detects a cycle precisely as an edge back to an in-progress node — a two-color (visited/unvisited) scheme cannot distinguish "already fully explored, no cycle" from "currently on the call stack, this IS a cycle." **Complexity:** O(V + E).

## LC 210 — Course Schedule II

```java
static int[] findOrder(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
    int[] inDegree = new int[numCourses];
    for (List<Integer> neighbors : graph) for (int next : neighbors) inDegree[next]++;
    Deque<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) if (inDegree[i] == 0) queue.add(i);
    int[] order = new int[numCourses];
    int idx = 0;
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order[idx++] = node;
        for (int next : graph.get(node)) if (--inDegree[next] == 0) queue.add(next);
    }
    return idx == numCourses ? order : new int[0];
}
```

**Invariant (Kahn's algorithm):** repeatedly removing a node with in-degree 0 and decrementing its neighbors' in-degrees produces a valid topological order if and only if every node is eventually removed; if the queue empties before all nodes are processed, a cycle exists among the remaining nodes. **Complexity:** O(V + E).

## LC 547 — Number of Provinces

```java
// UnionFind.java -- path compression + union by rank, from scratch
int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]);
    return parent[x];
}
void union(int a, int b) {
    int ra = find(a), rb = find(b);
    if (ra == rb) return;
    if (rank[ra] < rank[rb]) { int t = ra; ra = rb; rb = t; }
    parent[rb] = ra;
    if (rank[ra] == rank[rb]) rank[ra]++;
    components--;
}
```

**Invariant:** two cities are in the same province exactly when their Union-Find roots match; unioning every directly-connected pair and counting distinct remaining roots gives the province count, without needing an explicit graph traversal. **Complexity:** O(n² α(n)) for this problem's dense adjacency-matrix input (α = inverse Ackermann, effectively constant) — the n² term comes from scanning the input matrix, not from Union-Find itself.

## Verification — real, not asserted

```
  PASS  LC200 one connected island
  PASS  LC200 three islands
  PASS  LC133 clone is a distinct object from the original
  PASS  LC133 clone preserves root value
  PASS  LC133 clone preserves neighbor count
  PASS  LC133 cloned neighbors are also distinct objects
  PASS  LC207 no cycle -> can finish
  PASS  LC207 cycle -> cannot finish
  PASS  LC210 valid order found for all 4 courses
  PASS  LC210 course 0 before course 1 (prerequisite respected)
  PASS  LC210 course 1 before course 3
  PASS  LC210 cycle -> empty order
  PASS  LC547 two provinces
  PASS  LC547 three isolated provinces
Week 4 graph suite: 14/14 assertions passed
```

Full output and reproduce instructions: `practice/java/week-04/graphs/README.md`.

## Exit check

- [ ] All 5 problems solved with a written retrospective
- [ ] Union-Find and topological sort (Kahn's) both implemented from scratch, without reference
- [ ] Can explain why three-color DFS is needed for cycle detection instead of a simple visited/unvisited boolean
