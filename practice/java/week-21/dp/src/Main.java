final class Main {
    public static void main(String[] args) {
        // LC 72
        Check.eq(3, Problems.minDistance("horse", "ros"), "LC72 minDistance(horse, ros) = 3");
        Check.eq(5, Problems.minDistance("intention", "execution"), "LC72 minDistance(intention, execution) = 5");

        // LC 213
        Check.eq(3, Problems.robCircular(new int[]{2,3,2}), "LC213 robCircular([2,3,2]) = 3");
        Check.eq(4, Problems.robCircular(new int[]{1,2,3,1}), "LC213 robCircular([1,2,3,1]) = 4");
        Check.eq(1, Problems.robCircular(new int[]{1}), "LC213 robCircular([1]) single house = 1");

        // LC 518
        Check.eq(4, Problems.change(5, new int[]{1,2,5}), "LC518 change(5, [1,2,5]) = 4 combinations");
        Check.eq(0, Problems.change(3, new int[]{2}), "LC518 change(3, [2]) = 0 (unreachable)");

        // LC 494
        Check.eq(5, Problems.findTargetSumWays(new int[]{1,1,1,1,1}, 3), "LC494 findTargetSumWays([1,1,1,1,1], 3) = 5");
        Check.eq(1, Problems.findTargetSumWays(new int[]{1}, 1), "LC494 findTargetSumWays([1], 1) = 1");

        // LC 64
        Check.eq(7, Problems.minPathSum(new int[][]{{1,3,1},{1,5,1},{4,2,1}}), "LC64 minPathSum(3x3 grid) = 7");
        Check.eq(12, Problems.minPathSum(new int[][]{{1,2,3},{4,5,6}}), "LC64 minPathSum(2x3 grid) = 12");

        // LC 516
        Check.eq(4, Problems.longestPalindromeSubseq("bbbab"), "LC516 longestPalindromeSubseq(bbbab) = 4");
        Check.eq(2, Problems.longestPalindromeSubseq("cbbd"), "LC516 longestPalindromeSubseq(cbbd) = 2");

        // LC 309
        Check.eq(3, Problems.maxProfitCooldown(new int[]{1,2,3,0,2}), "LC309 maxProfitCooldown([1,2,3,0,2]) = 3");
        Check.eq(0, Problems.maxProfitCooldown(new int[]{1}), "LC309 maxProfitCooldown([1]) single day = 0");

        Check.summary("Week 21 — Dynamic Programming (LC 72, 213, 518, 494, 64, 516, 309)");
    }
}
