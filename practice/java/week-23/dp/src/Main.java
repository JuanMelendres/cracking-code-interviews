final class Main {
    public static void main(String[] args) {
        // LC 91
        Check.eq(2, Problems.numDecodings("12"), "LC91 numDecodings(12) = 2 (AB, L)");
        Check.eq(3, Problems.numDecodings("226"), "LC91 numDecodings(226) = 3 (BZ, VF, BBF)");
        Check.eq(0, Problems.numDecodings("06"), "LC91 numDecodings(06) = 0 (leading zero invalid)");
        Check.eq(1, Problems.numDecodings("10"), "LC91 numDecodings(10) = 1 (J only)");

        // LC 63
        Check.eq(2, Problems.uniquePathsWithObstacles(new int[][]{{0,0,0},{0,1,0},{0,0,0}}),
            "LC63 uniquePathsWithObstacles(3x3, center blocked) = 2");
        Check.eq(1, Problems.uniquePathsWithObstacles(new int[][]{{0,1},{0,0}}),
            "LC63 uniquePathsWithObstacles(2x2, one blocked) = 1");
        Check.eq(0, Problems.uniquePathsWithObstacles(new int[][]{{1,0}}),
            "LC63 uniquePathsWithObstacles(start blocked) = 0");

        // LC 337
        Problems.TreeNode t1 = new Problems.TreeNode(3,
            new Problems.TreeNode(2, null, new Problems.TreeNode(3)),
            new Problems.TreeNode(3, null, new Problems.TreeNode(1)));
        Check.eq(7, Problems.robTree(t1), "LC337 robTree(3,(2,_,3),(3,_,1)) = 7 (rob 3+3+1)");
        Problems.TreeNode t2 = new Problems.TreeNode(3,
            new Problems.TreeNode(4, new Problems.TreeNode(1), new Problems.TreeNode(3)),
            new Problems.TreeNode(5, null, new Problems.TreeNode(1)));
        Check.eq(9, Problems.robTree(t2), "LC337 robTree(3,(4,1,3),(5,_,1)) = 9 (rob 4+5)");

        // LC 188
        Check.eq(2, Problems.maxProfitK(2, new int[]{2,4,1}), "LC188 maxProfitK(k=2, [2,4,1]) = 2");
        Check.eq(7, Problems.maxProfitK(2, new int[]{3,2,6,5,0,3}), "LC188 maxProfitK(k=2, [3,2,6,5,0,3]) = 7");
        Check.eq(0, Problems.maxProfitK(0, new int[]{1,2,3}), "LC188 maxProfitK(k=0) = 0");

        // LC 132
        Check.eq(1, Problems.minCut("aab"), "LC132 minCut(aab) = 1 (aa|b)");
        Check.eq(0, Problems.minCut("a"), "LC132 minCut(a) = 0 (already palindrome)");
        Check.eq(0, Problems.minCut("racecar"), "LC132 minCut(racecar) = 0 (whole string is a palindrome)");

        Check.summary("Week 23 — Dynamic Programming (LC 91, 63, 337, 188, 132)");
    }
}
