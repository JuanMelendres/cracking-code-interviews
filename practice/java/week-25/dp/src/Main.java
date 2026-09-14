final class Main {
    public static void main(String[] args) {
        // LC 152
        Check.eq(6, Problems.maxProduct(new int[]{2,3,-2,4}), "LC152 maxProduct([2,3,-2,4]) = 6");
        Check.eq(0, Problems.maxProduct(new int[]{-2,0,-1}), "LC152 maxProduct([-2,0,-1]) = 0");
        Check.eq(24, Problems.maxProduct(new int[]{-2,3,-4}), "LC152 maxProduct([-2,3,-4]) = 24 (two negatives cancel)");

        // LC 279
        Check.eq(3, Problems.numSquares(12), "LC279 numSquares(12) = 3 (4+4+4)");
        Check.eq(2, Problems.numSquares(13), "LC279 numSquares(13) = 2 (4+9)");
        Check.eq(1, Problems.numSquares(1), "LC279 numSquares(1) = 1");

        // LC 174
        Check.eq(7, Problems.calculateMinimumHP(new int[][]{{-2,-3,3},{-5,-10,1},{10,30,-5}}),
            "LC174 calculateMinimumHP(3x3 example) = 7");
        Check.eq(1, Problems.calculateMinimumHP(new int[][]{{0}}), "LC174 calculateMinimumHP(single positive cell) = 1");

        // LC 673
        Check.eq(2, Problems.findNumberOfLIS(new int[]{1,3,5,4,7}), "LC673 findNumberOfLIS([1,3,5,4,7]) = 2");
        Check.eq(5, Problems.findNumberOfLIS(new int[]{2,2,2,2,2}), "LC673 findNumberOfLIS([2,2,2,2,2]) = 5 (each length-1 subsequence)");

        // LC 1220
        Check.eq(5, Problems.countVowelPermutation(1), "LC1220 countVowelPermutation(1) = 5");
        Check.eq(10, Problems.countVowelPermutation(2), "LC1220 countVowelPermutation(2) = 10");
        Check.eq(68, Problems.countVowelPermutation(5), "LC1220 countVowelPermutation(5) = 68");

        Check.summary("Week 25 — Dynamic Programming, final closure (LC 152, 279, 174, 673, 1220)");
    }
}
