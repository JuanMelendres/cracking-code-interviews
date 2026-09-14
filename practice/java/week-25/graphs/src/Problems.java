import java.util.*;

final class Problems {

    // ---- LC 130: Surrounded Regions ----
    static void solve(char[][] board) {
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            markSafe130(board, r, 0);
            markSafe130(board, r, cols - 1);
        }
        for (int c = 0; c < cols; c++) {
            markSafe130(board, 0, c);
            markSafe130(board, rows - 1, c);
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') board[r][c] = 'X';
                else if (board[r][c] == '#') board[r][c] = 'O';
            }
        }
    }

    private static void markSafe130(char[][] board, int r, int c) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != 'O') return;
        board[r][c] = '#'; // temporarily mark as border-connected (safe from capture)
        markSafe130(board, r + 1, c);
        markSafe130(board, r - 1, c);
        markSafe130(board, r, c + 1);
        markSafe130(board, r, c - 1);
    }

    // ---- LC 417: Pacific Atlantic Water Flow ----
    static List<List<Integer>> pacificAtlantic(int[][] heights) {
        int rows = heights.length, cols = heights[0].length;
        boolean[][] pacific = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            dfs417(heights, r, 0, pacific);
            dfs417(heights, r, cols - 1, atlantic);
        }
        for (int c = 0; c < cols; c++) {
            dfs417(heights, 0, c, pacific);
            dfs417(heights, rows - 1, c, atlantic);
        }
        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pacific[r][c] && atlantic[r][c]) result.add(List.of(r, c));
            }
        }
        return result;
    }

    private static void dfs417(int[][] heights, int r, int c, boolean[][] reachable) {
        reachable[r][c] = true;
        int rows = heights.length, cols = heights[0].length;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !reachable[nr][nc]
                && heights[nr][nc] >= heights[r][c]) {
                dfs417(heights, nr, nc, reachable);
            }
        }
    }

    // ---- LC 863: All Nodes Distance K in Binary Tree ----
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, TreeNode> parents = new HashMap<>();
        buildParentMap(root, null, parents);

        Queue<TreeNode> queue = new LinkedList<>();
        Set<TreeNode> visited = new HashSet<>();
        queue.offer(target);
        visited.add(target);
        int distance = 0;
        while (!queue.isEmpty() && distance < k) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null && visited.add(node.left)) queue.offer(node.left);
                if (node.right != null && visited.add(node.right)) queue.offer(node.right);
                TreeNode parent = parents.get(node);
                if (parent != null && visited.add(parent)) queue.offer(parent);
            }
            distance++;
        }
        List<Integer> result = new ArrayList<>();
        for (TreeNode node : queue) result.add(node.val);
        return result;
    }

    private static void buildParentMap(TreeNode node, TreeNode parent, Map<TreeNode, TreeNode> parents) {
        if (node == null) return;
        parents.put(node, parent);
        buildParentMap(node.left, node, parents);
        buildParentMap(node.right, node, parents);
    }

    // ---- LC 1129: Shortest Path with Alternating Colors ----
    static int[] shortestAlternatingPaths(int n, int[][] redEdges, int[][] blueEdges) {
        List<List<Integer>> redGraph = new ArrayList<>();
        List<List<Integer>> blueGraph = new ArrayList<>();
        for (int i = 0; i < n; i++) { redGraph.add(new ArrayList<>()); blueGraph.add(new ArrayList<>()); }
        for (int[] e : redEdges) redGraph.get(e[0]).add(e[1]);
        for (int[] e : blueEdges) blueGraph.get(e[0]).add(e[1]);

        int[][] dist = new int[n][2]; // dist[node][0] = via red-last, dist[node][1] = via blue-last
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[0][0] = 0;
        dist[0][1] = 0;

        Queue<int[]> queue = new LinkedList<>(); // {node, colorUsedToArrive} 0=red, 1=blue, -1=start
        queue.offer(new int[]{0, 0});
        queue.offer(new int[]{0, 1});
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int node = cur[0], colorUsed = cur[1];
            int nextColor = 1 - colorUsed;
            List<Integer> neighbors = (nextColor == 0) ? redGraph.get(node) : blueGraph.get(node);
            for (int next : neighbors) {
                if (dist[next][nextColor] == Integer.MAX_VALUE) {
                    dist[next][nextColor] = dist[node][colorUsed] + 1;
                    queue.offer(new int[]{next, nextColor});
                }
            }
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            int best = Math.min(dist[i][0], dist[i][1]);
            result[i] = (best == Integer.MAX_VALUE) ? -1 : best;
        }
        return result;
    }

    // ---- LC 815: Bus Routes (BFS over a transformed graph: vertices are BUSES, not stops) ----
    static int numBusesToDestination(int[][] routes, int source, int target) {
        if (source == target) return 0;
        Map<Integer, List<Integer>> stopToRoutes = new HashMap<>();
        for (int r = 0; r < routes.length; r++) {
            for (int stop : routes[r]) {
                stopToRoutes.computeIfAbsent(stop, k -> new ArrayList<>()).add(r);
            }
        }
        boolean[] visitedRoute = new boolean[routes.length];
        Set<Integer> visitedStop = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visitedStop.add(source);
        int buses = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            buses++;
            for (int i = 0; i < size; i++) {
                int stop = queue.poll();
                for (int route : stopToRoutes.getOrDefault(stop, List.of())) {
                    if (visitedRoute[route]) continue;
                    visitedRoute[route] = true;
                    for (int nextStop : routes[route]) {
                        if (nextStop == target) return buses;
                        if (visitedStop.add(nextStop)) queue.offer(nextStop);
                    }
                }
            }
        }
        return -1;
    }
}
