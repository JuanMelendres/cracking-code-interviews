import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 785
        Check.isTrue(!Problems.isBipartite(new int[][]{{1,2,3},{0,2},{0,1,3},{0,2}}),
            "LC785 isBipartite(odd-cycle-containing graph) -> false");
        Check.isTrue(Problems.isBipartite(new int[][]{{1,3},{0,2},{1,3},{0,2}}),
            "LC785 isBipartite(bipartite 4-cycle) -> true");

        // LC 332
        List<List<String>> tickets1 = List.of(
            List.of("MUC","LHR"), List.of("JFK","MUC"), List.of("SFO","SJC"), List.of("LHR","SFO"));
        Check.eq(List.of("JFK","MUC","LHR","SFO","SJC"), Problems.findItinerary(tickets1),
            "LC332 findItinerary(4 tickets) = [JFK,MUC,LHR,SFO,SJC]");
        List<List<String>> tickets2 = List.of(
            List.of("JFK","SFO"), List.of("JFK","ATL"), List.of("SFO","ATL"), List.of("ATL","JFK"), List.of("ATL","SFO"));
        Check.eq(List.of("JFK","ATL","JFK","SFO","ATL","SFO"), Problems.findItinerary(tickets2),
            "LC332 findItinerary(5 tickets, lexical tie-break) = [JFK,ATL,JFK,SFO,ATL,SFO]");

        // LC 1319
        Check.eq(1, Problems.makeConnected(4, new int[][]{{0,1},{0,2},{1,2}}),
            "LC1319 makeConnected(n=4, triangle+isolated) = 1");
        Check.eq(2, Problems.makeConnected(6, new int[][]{{0,1},{0,2},{0,3},{1,2},{1,3}}),
            "LC1319 makeConnected(n=6, 2 components need merging) = 2");
        Check.eq(-1, Problems.makeConnected(6, new int[][]{{0,1},{0,2},{0,3},{1,2}}),
            "LC1319 makeConnected(n=6, too few edges) = -1");

        // LC 399
        List<List<String>> eq1 = List.of(List.of("a","b"), List.of("b","c"));
        double[] vals1 = {2.0, 3.0};
        List<List<String>> queries1 = List.of(
            List.of("a","c"), List.of("b","a"), List.of("a","e"), List.of("a","a"), List.of("x","x"));
        double[] expected1 = {6.0, 0.5, -1.0, 1.0, -1.0};
        double[] actual1 = Problems.calcEquation(eq1, vals1, queries1);
        boolean allClose = true;
        for (int i = 0; i < expected1.length; i++) {
            if (Math.abs(expected1[i] - actual1[i]) > 1e-5) allClose = false;
        }
        Check.isTrue(allClose, "LC399 calcEquation(5 queries) matches [6.0, 0.5, -1.0, 1.0, -1.0]");

        // LC 802
        Check.eq(List.of(2,4,5,6), Problems.eventualSafeNodes(
            new int[][]{{1,2},{2,3},{5},{0},{5},{},{}}), "LC802 eventualSafeNodes(7-node graph) = [2,4,5,6]");
        Check.eq(List.of(4), Problems.eventualSafeNodes(
            new int[][]{{1,2,3,4},{1,2},{3,4},{0,4},{}}), "LC802 eventualSafeNodes(5-node graph) = [4]");

        // LC 1466
        Check.eq(3, Problems.minReorder(6, new int[][]{{0,1},{1,3},{2,3},{4,0},{4,5}}),
            "LC1466 minReorder(6 cities, example 1) = 3");
        Check.eq(2, Problems.minReorder(5, new int[][]{{1,0},{1,2},{3,2},{3,4}}),
            "LC1466 minReorder(5 cities, example 2) = 2");
        Check.eq(0, Problems.minReorder(3, new int[][]{{1,0},{2,0}}),
            "LC1466 minReorder(3 cities, already all point to 0) = 0");

        Check.summary("Week 24 — Graphs (LC 785, 332, 1319, 399, 802, 1466)");
    }
}
