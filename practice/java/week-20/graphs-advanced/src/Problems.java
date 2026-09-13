import java.util.*;

public class Problems {

    // LC 743 — Network Delay Time. Classic Dijkstra with a min-heap:
    // relax each node's shortest known distance, always expanding the
    // closest unvisited node next. O(E log V) time.
    static int networkDelayTime(int[][] times, int n, int k) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] t : times) {
            graph.computeIfAbsent(t[0], x -> new ArrayList<>()).add(new int[]{t[1], t[2]});
        }
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
                if (dist[node] + weight < dist[next]) {
                    dist[next] = dist[node] + weight;
                    pq.offer(new int[]{next, dist[next]});
                }
            }
        }
        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }

    // LC 684 — Redundant Connection. Explicit Union-Find with path
    // compression: the first edge that tries to union two nodes already
    // in the same set is the redundant one. O(n α(n)) time.
    static class UnionFind {
        int[] parent;
        UnionFind(int n) {
            parent = new int[n + 1];
            for (int i = 0; i <= n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;
            parent[ra] = rb;
            return true;
        }
    }

    static int[] findRedundantConnection(int[][] edges) {
        UnionFind uf = new UnionFind(edges.length);
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) return edge;
        }
        return new int[0];
    }

    // LC 1584 — Min Cost to Connect All Points. Prim's MST from an
    // implicit graph (all pairs, Manhattan distance): grow the tree by
    // always adding the cheapest edge to an unvisited point. O(n^2) time,
    // appropriate for a dense implicit graph (better than Kruskal's
    // O(n^2 log n) here since there's no need to sort n^2 edges explicitly).
    static int minCostConnectPoints(int[][] points) {
        int n = points.length;
        boolean[] inMst = new boolean[n];
        int[] minDist = new int[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[0] = 0;
        int totalCost = 0;
        for (int i = 0; i < n; i++) {
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!inMst[j] && (u == -1 || minDist[j] < minDist[u])) u = j;
            }
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

    // LC 994 — Rotting Oranges. Multi-source BFS: seed the queue with
    // EVERY initially-rotten orange simultaneously, then expand layer by
    // layer -- the number of layers is the answer. O(rows*cols) time.
    static int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int freshCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) queue.offer(new int[]{r, c});
                else if (grid[r][c] == 1) freshCount++;
            }
        }
        if (freshCount == 0) return 0;
        int minutes = -1;
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        while (!queue.isEmpty()) {
            int size = queue.size();
            minutes++;
            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                for (int[] d : dirs) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        freshCount--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }
        return freshCount == 0 ? minutes : -1;
    }

    // LC 787 — Cheapest Flights Within K Stops. Bellman-Ford-style relaxation
    // bounded to k+1 rounds (each round allows one more edge/stop) -- a plain
    // Dijkstra would incorrectly prune a costlier-but-fewer-stops-remaining
    // path, since Dijkstra's greedy "closest first" doesn't track stop count.
    // O(k * E) time.
    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        for (int round = 0; round <= k; round++) {
            int[] next = dist.clone();
            for (int[] flight : flights) {
                int u = flight[0], v = flight[1], w = flight[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < next[v]) {
                    next[v] = dist[u] + w;
                }
            }
            dist = next;
        }
        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}
