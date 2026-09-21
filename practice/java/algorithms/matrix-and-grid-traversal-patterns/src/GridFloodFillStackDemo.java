import java.util.ArrayDeque;

/**
 * LC 200 -- Number of Islands, and the real, common gotcha specific to grids:
 * a naive recursive flood fill's call-stack depth grows with the CONNECTED
 * COMPONENT's size, not the grid's dimensions -- a single large connected
 * region (a "lake," not a maze of small islands) can recurse one call per
 * cell, exactly the call-stack-exhaustion mechanism
 * ../../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md
 * measures directly for plain recursion. This demo measures the real
 * breaking point for THIS pattern specifically, then shows the standard fix:
 * an explicit, heap-allocated stack/queue converts the same traversal from
 * call-stack-bounded to heap-bounded -- the exact concrete design decision
 * ../../../../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md's
 * own Design Exercise describes in the abstract.
 */
public class GridFloodFillStackDemo {

    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    /** Recursive DFS -- one stack frame per visited cell in the worst case (a single solid region). */
    static int recursiveDFSCount(char[][] grid, boolean[][] visited, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) return 0;
        if (visited[r][c] || grid[r][c] != '1') return 0;
        visited[r][c] = true;
        int count = 1;
        for (int d = 0; d < 4; d++) {
            count += recursiveDFSCount(grid, visited, r + DR[d], c + DC[d]);
        }
        return count;
    }

    /** Iterative DFS -- an explicit, heap-allocated ArrayDeque stands in for the call stack. */
    static int iterativeDFSCount(char[][] grid, boolean[][] visited, int startR, int startC) {
        int rows = grid.length, cols = grid[0].length;
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{startR, startC});
        visited[startR][startC] = true;
        int count = 0;
        while (!stack.isEmpty()) {
            int[] cell = stack.pop();
            count++;
            for (int d = 0; d < 4; d++) {
                int nr = cell[0] + DR[d], nc = cell[1] + DC[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (visited[nr][nc] || grid[nr][nc] != '1') continue;
                visited[nr][nc] = true;
                stack.push(new int[]{nr, nc});
            }
        }
        return count;
    }

    /** Iterative BFS -- the other standard fix, an explicit queue instead of a stack. */
    static int iterativeBFSCount(char[][] grid, boolean[][] visited, int startR, int startC) {
        int rows = grid.length, cols = grid[0].length;
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{startR, startC});
        visited[startR][startC] = true;
        int count = 0;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            count++;
            for (int d = 0; d < 4; d++) {
                int nr = cell[0] + DR[d], nc = cell[1] + DC[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (visited[nr][nc] || grid[nr][nc] != '1') continue;
                visited[nr][nc] = true;
                queue.offer(new int[]{nr, nc});
            }
        }
        return count;
    }

    static char[][] solidGrid(int rows, int cols) {
        char[][] g = new char[rows][cols];
        for (char[] row : g) java.util.Arrays.fill(row, '1');
        return g;
    }

    /**
     * A single recursive-DFS measurement at one size, run in its own fresh JVM process
     * (invoked by run.sh once per size) -- deliberately NOT a same-process sweep, since
     * JIT compilation of the hot recursive method partway through a sweep would shrink
     * its stack-frame cost mid-run and produce a non-monotonic, misleading result
     * (confirmed directly: an in-process sweep really did overflow at 20,000 cells,
     * survive at 40,000, then overflow again at 80,000 -- a JIT-warmup artifact, not a
     * real property of the algorithm). One clean process per size avoids this entirely.
     */
    static void runSingleRecursiveMeasurement(int rows, int cols) {
        char[][] grid = solidGrid(rows, cols);
        boolean[][] visited = new boolean[rows][cols];
        try {
            int counted = recursiveDFSCount(grid, visited, 0, 0);
            System.out.printf("  cells=%-8d rows=%-6d cols=%-4d recursive DFS: OK, counted=%d%n",
                    rows * cols, rows, cols, counted);
        } catch (StackOverflowError e) {
            System.out.printf("  cells=%-8d rows=%-6d cols=%-4d recursive DFS: StackOverflowError%n",
                    rows * cols, rows, cols);
        }
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            // Single-size mode: one fresh process per measurement, see run.sh.
            runSingleRecursiveMeasurement(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            return;
        }

        // The fix, at a size an order of magnitude past where recursion broke above:
        // an explicit stack (heap-allocated, per the memory-hierarchy chapter's own
        // design-exercise reasoning) removes the call-stack depth bound entirely.
        int bigRows = 1000, bigCols = 2000; // 2,000,000 cells, one solid connected region
        char[][] bigGrid = solidGrid(bigRows, bigCols);

        boolean[][] visitedDFS = new boolean[bigRows][bigCols];
        int iterativeDFSResult = iterativeDFSCount(bigGrid, visitedDFS, 0, 0);

        boolean[][] visitedBFS = new boolean[bigRows][bigCols];
        int iterativeBFSResult = iterativeBFSCount(bigGrid, visitedBFS, 0, 0);

        int expected = bigRows * bigCols;
        System.out.println();
        System.out.printf("cells=%d (far beyond the recursive breaking point above):%n", expected);
        System.out.printf("  iterative DFS (explicit stack): counted=%d, matches expected=%b%n",
                iterativeDFSResult, iterativeDFSResult == expected);
        System.out.printf("  iterative BFS (explicit queue): counted=%d, matches expected=%b%n",
                iterativeBFSResult, iterativeBFSResult == expected);

        if (iterativeDFSResult != expected) throw new IllegalStateException("iterative DFS undercount");
        if (iterativeBFSResult != expected) throw new IllegalStateException("iterative BFS undercount");

        // LC 200-shaped correctness check: multiple separate islands, real expected count.
        char[][] islands = {
            {'1', '1', '0', '0', '0'},
            {'1', '1', '0', '0', '0'},
            {'0', '0', '1', '0', '0'},
            {'0', '0', '0', '1', '1'},
        };
        boolean[][] visitedIslands = new boolean[islands.length][islands[0].length];
        int islandCount = 0;
        for (int r = 0; r < islands.length; r++) {
            for (int c = 0; c < islands[0].length; c++) {
                if (islands[r][c] == '1' && !visitedIslands[r][c]) {
                    iterativeDFSCount(islands, visitedIslands, r, c);
                    islandCount++;
                }
            }
        }
        System.out.println();
        System.out.println("LC200 number-of-islands example: expected=3, actual=" + islandCount);
        if (islandCount != 3) throw new IllegalStateException("island count mismatch");
    }
}
