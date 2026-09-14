import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 130
        char[][] board1 = {
            {'X','X','X','X'},
            {'X','O','O','X'},
            {'X','X','O','X'},
            {'X','O','X','X'}
        };
        Problems.solve(board1);
        char[][] expected1 = {
            {'X','X','X','X'},
            {'X','X','X','X'},
            {'X','X','X','X'},
            {'X','O','X','X'}
        };
        Check.isTrue(Arrays.deepEquals(expected1, board1), "LC130 solve(4x4 board) captures interior O's, keeps border-connected O");

        // LC 417
        int[][] heights = {
            {1,2,2,3,5},
            {3,2,3,4,4},
            {2,4,5,3,1},
            {6,7,1,4,5},
            {5,1,1,2,4}
        };
        List<List<Integer>> flow = Problems.pacificAtlantic(heights);
        Set<List<Integer>> flowSet = new HashSet<>(flow);
        Set<List<Integer>> expectedFlow = Set.of(
            List.of(0,4), List.of(1,3), List.of(1,4), List.of(2,2),
            List.of(3,0), List.of(3,1), List.of(4,0));
        Check.eq(7, flow.size(), "LC417 pacificAtlantic(5x5 example) = 7 cells");
        Check.isTrue(flowSet.equals(expectedFlow), "LC417 pacificAtlantic(5x5 example) exact set matches known LeetCode answer");

        // LC 863 — standard LC 863 example tree: root 3, left 5(children 6, 2(children 7,4)), right 1(children 0, 8)
        Problems.TreeNode lc863Root = new Problems.TreeNode(3,
            new Problems.TreeNode(5,
                new Problems.TreeNode(6),
                new Problems.TreeNode(2, new Problems.TreeNode(7), new Problems.TreeNode(4))),
            new Problems.TreeNode(1,
                new Problems.TreeNode(0),
                new Problems.TreeNode(8)));
        Problems.TreeNode target5 = lc863Root.left; // node with value 5
        List<Integer> distK = Problems.distanceK(lc863Root, target5, 2);
        Set<Integer> distKSet = new HashSet<>(distK);
        Check.eq(3, distK.size(), "LC863 distanceK(target=5, k=2) has 3 nodes");
        Check.isTrue(distKSet.containsAll(List.of(7,4,1)), "LC863 distanceK(target=5, k=2) = {7,4,1}");

        // LC 1129
        Check.isTrue(Arrays.equals(new int[]{0,1,-1},
            Problems.shortestAlternatingPaths(3, new int[][]{{0,1}}, new int[][]{})),
            "LC1129 shortestAlternatingPaths(n=3, only red 0->1) = [0,1,-1]");
        Check.isTrue(Arrays.equals(new int[]{0,1,-1},
            Problems.shortestAlternatingPaths(3, new int[][]{{0,1}}, new int[][]{{2,1}})),
            "LC1129 shortestAlternatingPaths(n=3, red 0->1, blue 2->1) = [0,1,-1]");

        // LC 815
        Check.eq(2, Problems.numBusesToDestination(
            new int[][]{{1,2,7},{3,6,7}}, 1, 6), "LC815 numBusesToDestination(2 routes) = 2");
        Check.eq(-1, Problems.numBusesToDestination(
            new int[][]{{1,2,7},{3,6,7}}, 1, 5), "LC815 numBusesToDestination(unreachable target) = -1");
        Check.eq(0, Problems.numBusesToDestination(
            new int[][]{{1,2,7}}, 1, 1), "LC815 numBusesToDestination(source==target) = 0");

        Check.summary("Week 25 — Graphs, final closure (LC 130, 417, 863, 1129, 815)");
    }
}
