# Week 9 Java — Dynamic Programming Part 2 (T-1411) — runnable verification

LC 62, 1143, 416, 5 — 2D DP, 0/1 knapsack, interval DP. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/dp-part2
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Real observed output (last run):**

```
== LC 62: Unique Paths ==
  PASS  uniquePaths(3,7) = 28
  PASS  uniquePaths(3,3) = 6
  PASS  uniquePaths(1,1) = 1 (already at the destination)

== LC 1143: Longest Common Subsequence ==
  PASS  LCS("abcde","ace") = 3 ("ace")
  PASS  LCS("abc","abc") = 3 (identical)
  PASS  LCS("abc","def") = 0 (no common chars)

== LC 416: Partition Equal Subset Sum ==
  PASS  canPartition([1,5,11,5]) = true ([1,5,5] and [11])
  PASS  canPartition([1,2,3,5]) = false (sum=11, odd)
  PASS  canPartition([1..7]) = true (sum=28, half=14)

== LC 5: Longest Palindromic Substring ==
  PASS  longestPalindrome("babad") has length 3 (bab or aba, either is a correct longest palindrome), got "bab"
  PASS  "bab" returned for "babad" is actually a palindrome
  PASS  "bab" is actually a substring of "babad"
  PASS  longestPalindrome("cbbd") has length 2 (bb), got "bb"
  PASS  "bb" returned for "cbbd" is actually a palindrome
  PASS  "bb" is actually a substring of "cbbd"
  PASS  longestPalindrome("a") has length 1 (single character is trivially a palindrome), got "a"
  PASS  "a" returned for "a" is actually a palindrome
  PASS  "a" is actually a substring of "a"
  PASS  longestPalindrome("racecar") has length 7 (the whole string is a palindrome), got "racecar"
  PASS  "racecar" returned for "racecar" is actually a palindrome
  PASS  "racecar" is actually a substring of "racecar"
Week 9 DP part 2 suite: 21/21 assertions passed
```

`Main.java`'s palindrome checks verify length + actual palindrome property + actual substring membership rather than an exact expected string, since `babad` has two equally-valid longest palindromes (`bab` and `aba`) and either is correct.
