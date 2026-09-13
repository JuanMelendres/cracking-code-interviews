import java.util.*;

final class Problems {

    // ---- LC 785: Is Graph Bipartite? (two-coloring via BFS) ----
    static boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // 0 = uncolored, 1/-1 = the two colors
        for (int start = 0; start < n; start++) {
            if (color[start] != 0) continue;
            color[start] = 1;
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(start);
            while (!queue.isEmpty()) {
                int node = queue.poll();
                for (int neighbor : graph[node]) {
                    if (color[neighbor] == 0) {
                        color[neighbor] = -color[node];
                        queue.offer(neighbor);
                    } else if (color[neighbor] == color[node]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // ---- LC 332: Reconstruct Itinerary (Eulerian path via Hierholzer's algorithm) ----
    static List<String> findItinerary(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<>();
        for (List<String> ticket : tickets) {
            graph.computeIfAbsent(ticket.get(0), k -> new PriorityQueue<>()).offer(ticket.get(1));
        }
        LinkedList<String> route = new LinkedList<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push("JFK");
        while (!stack.isEmpty()) {
            String airport = stack.peek();
            PriorityQueue<String> destinations = graph.get(airport);
            if (destinations == null || destinations.isEmpty()) {
                route.addFirst(stack.pop());
            } else {
                stack.push(destinations.poll());
            }
        }
        return route;
    }

    // ---- LC 1319: Number of Operations to Make Network Connected (Union-Find) ----
    static int makeConnected(int n, int[][] connections) {
        if (connections.length < n - 1) return -1;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int components = n;
        for (int[] c : connections) {
            int ra = find1319(parent, c[0]);
            int rb = find1319(parent, c[1]);
            if (ra != rb) {
                parent[ra] = rb;
                components--;
            }
        }
        return components - 1;
    }

    private static int find1319(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find1319(parent, parent[x]);
        return parent[x];
    }

    // ---- LC 399: Evaluate Division (weighted graph + DFS) ----
    static double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0), b = equations.get(i).get(1);
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, values[i]);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / values[i]);
        }
        double[] result = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String src = queries.get(i).get(0), dst = queries.get(i).get(1);
            if (!graph.containsKey(src) || !graph.containsKey(dst)) {
                result[i] = -1.0;
            } else {
                result[i] = dfs399(graph, src, dst, new HashSet<>());
            }
        }
        return result;
    }

    private static double dfs399(Map<String, Map<String, Double>> graph, String cur, String target, Set<String> visited) {
        if (cur.equals(target)) return 1.0;
        visited.add(cur);
        for (Map.Entry<String, Double> entry : graph.get(cur).entrySet()) {
            if (!visited.contains(entry.getKey())) {
                double sub = dfs399(graph, entry.getKey(), target, visited);
                if (sub != -1.0) return entry.getValue() * sub;
            }
        }
        return -1.0;
    }

    // ---- LC 802: Find Eventual Safe States (DFS three-coloring for cycle detection) ----
    static List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] state = new int[n]; // 0 = unvisited, 1 = visiting, 2 = safe
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (dfs802(graph, i, state)) result.add(i);
        }
        return result;
    }

    private static boolean dfs802(int[][] graph, int node, int[] state) {
        if (state[node] != 0) return state[node] == 2;
        state[node] = 1; // mark as currently-being-visited (part of the current path)
        for (int next : graph[node]) {
            if (!dfs802(graph, next, state)) return false;
        }
        state[node] = 2;
        return true;
    }

    // ---- LC 1466: Reorder Routes to Make All Paths Lead to City Zero ----
    static int minReorder(int n, int[][] connections) {
        Map<Integer, List<int[]>> adjacency = new HashMap<>(); // neighbor -> [neighbor, needsReversalIfTraversedThisWay]
        for (int[] c : connections) {
            adjacency.computeIfAbsent(c[0], k -> new ArrayList<>()).add(new int[]{c[1], 1}); // original direction: away from c[0]
            adjacency.computeIfAbsent(c[1], k -> new ArrayList<>()).add(new int[]{c[0], 0}); // reverse traversal: no reorder needed
        }
        boolean[] visited = new boolean[n];
        return dfs1466(0, adjacency, visited);
    }

    private static int dfs1466(int node, Map<Integer, List<int[]>> adjacency, boolean[] visited) {
        visited[node] = true;
        int reorders = 0;
        for (int[] edge : adjacency.getOrDefault(node, List.of())) {
            if (!visited[edge[0]]) {
                reorders += edge[1];
                reorders += dfs1466(edge[0], adjacency, visited);
            }
        }
        return reorders;
    }
}
