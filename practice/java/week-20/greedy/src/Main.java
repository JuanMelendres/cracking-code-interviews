public class Main {
    public static void main(String[] args) {
        // LC 45
        Check.eq(2, Problems.jump(new int[]{2, 3, 1, 1, 4}), "LC45 jump([2,3,1,1,4]) = 2");
        Check.eq(2, Problems.jump(new int[]{2, 3, 0, 1, 4}), "LC45 jump([2,3,0,1,4]) = 2");

        // LC 134
        Check.eq(3, Problems.canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}),
                "LC134 canCompleteCircuit start index = 3");
        Check.eq(-1, Problems.canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3}),
                "LC134 canCompleteCircuit no solution = -1");

        // LC 621
        Check.eq(8, Problems.leastInterval("AAABBB".toCharArray(), 2), "LC621 leastInterval(AAABBB, n=2) = 8");
        Check.eq(6, Problems.leastInterval("AAABBB".toCharArray(), 0), "LC621 leastInterval(AAABBB, n=0) = 6 (no cooldown needed)");

        // LC 763
        Check.eq("[9, 7, 8]", Problems.partitionLabels("ababcbacadefegdehijhklij").toString(),
                "LC763 partitionLabels(\"ababcbacadefegdehijhklij\")");

        // LC 402
        Check.eq("1219", Problems.removeKdigits("1432219", 3), "LC402 removeKdigits(\"1432219\", 3)");
        Check.eq("200", Problems.removeKdigits("10200", 1), "LC402 removeKdigits(\"10200\", 1) -- leading zeros stripped");
        Check.eq("0", Problems.removeKdigits("10", 2), "LC402 removeKdigits(\"10\", 2) -- empty result becomes \"0\"");

        Check.summary("Week 20 — Greedy (LC 45, 134, 621, 763, 402)");
    }
}
