import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // LC 743
        Check.eq(2, Problems.networkDelayTime(new int[][]{{2,1,1},{2,3,1},{3,4,1}}, 4, 2),
                "LC743 networkDelayTime(4 nodes, start=2) = 2");
        Check.eq(-1, Problems.networkDelayTime(new int[][]{{1,2,1}}, 2, 2),
                "LC743 networkDelayTime unreachable node -> -1");

        // LC 684
        Check.eq("[2, 3]", Arrays.toString(Problems.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}})),
                "LC684 findRedundantConnection triangle -> [2,3]");
        Check.eq("[1, 4]", Arrays.toString(Problems.findRedundantConnection(new int[][]{{1,2},{2,3},{3,4},{1,4},{1,5}})),
                "LC684 findRedundantConnection 5-edge case -> [1,4]");

        // LC 1584
        Check.eq(20, Problems.minCostConnectPoints(new int[][]{{0,0},{2,2},{3,10},{5,2},{7,0}}),
                "LC1584 minCostConnectPoints(5 points) = 20");
        Check.eq(18, Problems.minCostConnectPoints(new int[][]{{3,12},{-2,5},{-4,1}}),
                "LC1584 minCostConnectPoints(3 points) = 18");

        // LC 994
        Check.eq(4, Problems.orangesRotting(new int[][]{{2,1,1},{1,1,0},{0,1,1}}), "LC994 orangesRotting -> 4 minutes");
        Check.eq(-1, Problems.orangesRotting(new int[][]{{2,1,1},{0,1,1},{1,0,1}}), "LC994 orangesRotting unreachable fresh -> -1");
        Check.eq(0, Problems.orangesRotting(new int[][]{{0,2}}), "LC994 orangesRotting no fresh oranges -> 0");

        // LC 787
        Check.eq(600, Problems.findCheapestPrice(4,
                new int[][]{{0,1,100},{1,2,100},{2,3,100},{0,2,500}}, 0, 3, 1),
                "LC787 findCheapestPrice(k=1 stop) = 600 (the 3-edge 0->1->2->3 path needs 2 stops, exceeding k=1; only 0->2->3 qualifies)");
        Check.eq(200, Problems.findCheapestPrice(3,
                new int[][]{{0,1,100},{1,2,100},{0,2,500}}, 0, 2, 1),
                "LC787 findCheapestPrice(k=1 stop, cheaper 2-hop path exists) = 200");

        Check.summary("Week 20 — Graphs Advanced (LC 743, 684, 1584, 994, 787)");
    }
}
