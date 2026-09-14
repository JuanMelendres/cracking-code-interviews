import java.util.*;

public class Main {
    public static void main(String[] args) {
        char[][] grid1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        Check.eq(1, GraphProblems.numIslands(grid1), "LC200 one connected island");

        char[][] grid2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        Check.eq(3, GraphProblems.numIslands(grid2), "LC200 three islands");

        // Graph: 1-2-3, triangle (1-2, 2-3, 3-1 per LC133's canonical adjacency list)
        GraphProblems.Node n1 = new GraphProblems.Node(1);
        GraphProblems.Node n2 = new GraphProblems.Node(2);
        GraphProblems.Node n3 = new GraphProblems.Node(3);
        n1.neighbors.add(n2); n1.neighbors.add(n3);
        n2.neighbors.add(n1); n2.neighbors.add(n3);
        n3.neighbors.add(n1); n3.neighbors.add(n2);
        GraphProblems.Node cloned = GraphProblems.cloneGraph(n1);
        Check.isTrue(cloned != n1, "LC133 clone is a distinct object from the original");
        Check.eq(1, cloned.val, "LC133 clone preserves root value");
        Check.eq(2, cloned.neighbors.size(), "LC133 clone preserves neighbor count");
        Check.isTrue(cloned.neighbors.get(0) != n2, "LC133 cloned neighbors are also distinct objects");

        Check.isTrue(GraphProblems.canFinish(2, new int[][]{{1,0}}), "LC207 no cycle -> can finish");
        Check.isTrue(!GraphProblems.canFinish(2, new int[][]{{1,0},{0,1}}), "LC207 cycle -> cannot finish");

        int[] order = GraphProblems.findOrder(4, new int[][]{{1,0},{2,0},{3,1},{3,2}});
        Check.eq(4, order.length, "LC210 valid order found for all 4 courses");
        Check.isTrue(indexOf(order, 0) < indexOf(order, 1), "LC210 course 0 before course 1 (prerequisite respected)");
        Check.isTrue(indexOf(order, 1) < indexOf(order, 3), "LC210 course 1 before course 3");
        Check.eq(0, GraphProblems.findOrder(2, new int[][]{{1,0},{0,1}}).length, "LC210 cycle -> empty order");

        int[][] connected1 = {{1,1,0},{1,1,0},{0,0,1}};
        Check.eq(2, GraphProblems.findCircleNum(connected1), "LC547 two provinces");
        int[][] connected2 = {{1,0,0},{0,1,0},{0,0,1}};
        Check.eq(3, GraphProblems.findCircleNum(connected2), "LC547 three isolated provinces");

        Check.summary("Week 4 graph suite");
        if (Check.fail > 0) System.exit(1);
    }

    private static int indexOf(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) if (arr[i] == val) return i;
        return -1;
    }
}
