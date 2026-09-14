final class Main {
    public static void main(String[] args) {
        // LC 329
        Check.eq(4, Problems.longestIncreasingPath(new int[][]{{9,9,4},{6,6,8},{2,1,1}}),
            "LC329 longestIncreasingPath(3x3 example) = 4 (path 1-2-6-9)");
        Check.eq(4, Problems.longestIncreasingPath(new int[][]{{3,4,5},{3,2,6},{2,2,1}}),
            "LC329 longestIncreasingPath(3x3 example 2) = 4 (path 3-4-5-6)");
        Check.eq(1, Problems.longestIncreasingPath(new int[][]{{1}}), "LC329 longestIncreasingPath(single cell) = 1");

        // LC 312
        Check.eq(167, Problems.maxCoins(new int[]{3,1,5,8}), "LC312 maxCoins([3,1,5,8]) = 167");
        Check.eq(10, Problems.maxCoins(new int[]{1,5}), "LC312 maxCoins([1,5]) = 10");

        // LC 10
        Check.isTrue(!Problems.isMatch("aa", "a"), "LC10 isMatch(aa, a) -> false");
        Check.isTrue(Problems.isMatch("aa", "a*"), "LC10 isMatch(aa, a*) -> true (zero-or-more)");
        Check.isTrue(Problems.isMatch("ab", ".*"), "LC10 isMatch(ab, .*) -> true");
        Check.isTrue(Problems.isMatch("aab", "c*a*b"), "LC10 isMatch(aab, c*a*b) -> true");
        Check.isTrue(!Problems.isMatch("mississippi", "mis*is*p*."), "LC10 isMatch(mississippi, mis*is*p*.) -> false");

        // LC 44
        Check.isTrue(!Problems.isMatchWildcard("aa", "a"), "LC44 isMatchWildcard(aa, a) -> false");
        Check.isTrue(Problems.isMatchWildcard("aa", "*"), "LC44 isMatchWildcard(aa, *) -> true");
        Check.isTrue(!Problems.isMatchWildcard("cb", "?a"), "LC44 isMatchWildcard(cb, ?a) -> false");
        Check.isTrue(Problems.isMatchWildcard("adceb", "*a*b"), "LC44 isMatchWildcard(adceb, *a*b) -> true");
        Check.isTrue(!Problems.isMatchWildcard("acdcb", "a*c?b"), "LC44 isMatchWildcard(acdcb, a*c?b) -> false");

        // LC 96
        Check.eq(5, Problems.numTrees(3), "LC96 numTrees(3) = 5");
        Check.eq(1, Problems.numTrees(1), "LC96 numTrees(1) = 1");
        Check.eq(14, Problems.numTrees(4), "LC96 numTrees(4) = 14");

        // LC 32
        Check.eq(2, Problems.longestValidParentheses("(()"), "LC32 longestValidParentheses(\"(()\") = 2");
        Check.eq(4, Problems.longestValidParentheses(")()())"), "LC32 longestValidParentheses(\")()())\") = 4");
        Check.eq(0, Problems.longestValidParentheses(""), "LC32 longestValidParentheses(empty) = 0");

        Check.summary("Week 24 — Dynamic Programming (LC 329, 312, 10, 44, 96, 32)");
    }
}
