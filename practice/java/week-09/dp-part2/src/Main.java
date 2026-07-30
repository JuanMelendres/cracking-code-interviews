public class Main {
    public static void main(String[] args) {
        System.out.println("== LC 62: Unique Paths ==");
        Check.eq(28, DpPart2Problems.uniquePaths(3, 7), "uniquePaths(3,7) = 28");
        Check.eq(6, DpPart2Problems.uniquePaths(3, 3), "uniquePaths(3,3) = 6");
        Check.eq(1, DpPart2Problems.uniquePaths(1, 1), "uniquePaths(1,1) = 1 (already at the destination)");

        System.out.println("\n== LC 1143: Longest Common Subsequence ==");
        Check.eq(3, DpPart2Problems.longestCommonSubsequence("abcde", "ace"), "LCS(\"abcde\",\"ace\") = 3 (\"ace\")");
        Check.eq(3, DpPart2Problems.longestCommonSubsequence("abc", "abc"), "LCS(\"abc\",\"abc\") = 3 (identical)");
        Check.eq(0, DpPart2Problems.longestCommonSubsequence("abc", "def"), "LCS(\"abc\",\"def\") = 0 (no common chars)");

        System.out.println("\n== LC 416: Partition Equal Subset Sum ==");
        Check.isTrue(DpPart2Problems.canPartition(new int[]{1, 5, 11, 5}), "canPartition([1,5,11,5]) = true ([1,5,5] and [11])");
        Check.isTrue(!DpPart2Problems.canPartition(new int[]{1, 2, 3, 5}), "canPartition([1,2,3,5]) = false (sum=11, odd)");
        Check.isTrue(DpPart2Problems.canPartition(new int[]{1, 2, 3, 4, 5, 6, 7}), "canPartition([1..7]) = true (sum=28, half=14)");

        System.out.println("\n== LC 5: Longest Palindromic Substring ==");
        checkPalindromeResult("babad", 3, "bab or aba, either is a correct longest palindrome");
        checkPalindromeResult("cbbd", 2, "bb");
        checkPalindromeResult("a", 1, "single character is trivially a palindrome");
        checkPalindromeResult("racecar", 7, "the whole string is a palindrome");

        Check.summary("Week 9 DP part 2 suite");
        if (Check.fail > 0) System.exit(1);
    }

    static void checkPalindromeResult(String input, int expectedLen, String note) {
        String result = DpPart2Problems.longestPalindrome(input);
        Check.eq(expectedLen, result.length(), "longestPalindrome(\"" + input + "\") has length " + expectedLen + " (" + note + "), got \"" + result + "\"");
        Check.isTrue(isPalindrome(result), "\"" + result + "\" returned for \"" + input + "\" is actually a palindrome");
        Check.isTrue(input.contains(result), "\"" + result + "\" is actually a substring of \"" + input + "\"");
    }

    static boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i++) != s.charAt(j--)) return false;
        }
        return true;
    }
}
