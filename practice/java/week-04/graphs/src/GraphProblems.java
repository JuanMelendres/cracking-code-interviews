import java.util.*;

final class GraphProblems {

    // LC 200 — Number of Islands. DFS flood-fill, marking visited land in place.
    static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sink(grid, r, c);
                }
            }
        }
        return count;
    }

    private static void sink(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
        grid[r][c] = '0'; // mark visited by sinking the land
        sink(grid, r + 1, c);
        sink(grid, r - 1, c);
        sink(grid, r, c + 1);
        sink(grid, r, c - 1);
    }

    // LC 133 — Clone Graph. DFS with a visited map from original node -> clone,
    // so cycles in the graph don't cause infinite recursion or duplicate clones.
    static final class Node {
        int val;
        List<Node> neighbors = new ArrayList<>();
        Node(int val) { this.val = val; }
    }

    static Node cloneGraph(Node node) {
        if (node == null) return null;
        return clone(node, new HashMap<>());
    }

    private static Node clone(Node node, Map<Node, Node> visited) {
        if (visited.containsKey(node)) return visited.get(node);
        Node copy = new Node(node.val);
        visited.put(node, copy);
        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(clone(neighbor, visited));
        }
        return copy;
    }

    // LC 207 — Course Schedule. Cycle detection in a directed graph via
    // three-color DFS (unvisited / in-progress / done). A back edge to an
    // in-progress node means a cycle -- courses can't be finished.
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
        int[] state = new int[numCourses]; // 0 = unvisited, 1 = in-progress, 2 = done
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

    // LC 210 — Course Schedule II. Kahn's algorithm: repeatedly remove nodes
    // with in-degree 0. If all nodes are removed, the removal order IS a
    // valid topological order; if not, a cycle exists and no order is possible.
    static int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = buildGraph(numCourses, prerequisites);
        int[] inDegree = new int[numCourses];
        for (List<Integer> neighbors : graph) {
            for (int next : neighbors) inDegree[next]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) if (inDegree[i] == 0) queue.add(i);

        int[] order = new int[numCourses];
        int idx = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            order[idx++] = node;
            for (int next : graph.get(node)) {
                if (--inDegree[next] == 0) queue.add(next);
            }
        }
        return idx == numCourses ? order : new int[0];
    }

    private static List<List<Integer>> buildGraph(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
        for (int[] p : prerequisites) graph.get(p[1]).add(p[0]); // p[1] -> p[0]
        return graph;
    }

    // LC 547 — Number of Provinces. Union-Find from scratch (see UnionFind.java) --
    // union every pair of directly-connected cities, then count distinct roots.
    static int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) uf.union(i, j);
            }
        }
        return uf.componentCount();
    }
}
