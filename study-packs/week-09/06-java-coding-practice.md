---
title: "Java Coding Practice — Week 9"
week: 9
last_reviewed: 2026-07-31
---

# Java Coding Practice — Week 9

**T-1417 concurrency problems (LC 1114, 1115, 1116) + T-1411 DP part 2 (LC 62, 1143, 416, 5). All code compiled and executed, including 100-trial randomized-scheduling verification on the concurrency problems — see the verification blocks and `MANIFEST.md`.**

## Table of Contents

1. [LC 1114 — Print in Order](#lc-1114--print-in-order)
2. [LC 1115 — Print FooBar Alternately](#lc-1115--print-foobar-alternately)
3. [LC 1116 — Print Zero Even Odd](#lc-1116--print-zero-even-odd)
4. [LC 62 — Unique Paths](#lc-62--unique-paths)
5. [LC 1143 — Longest Common Subsequence](#lc-1143--longest-common-subsequence)
6. [LC 416 — Partition Equal Subset Sum](#lc-416--partition-equal-subset-sum)
7. [LC 5 — Longest Palindromic Substring](#lc-5--longest-palindromic-substring)
8. [Verification](#verification--real-not-asserted)

---

## LC 1114 — Print in Order

```java
public class PrintInOrder {
    private final Semaphore secondReady = new Semaphore(0);
    private final Semaphore thirdReady = new Semaphore(0);

    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        secondReady.release();
    }
    public void second(Runnable printSecond) throws InterruptedException {
        secondReady.acquire();
        printSecond.run();
        thirdReady.release();
    }
    public void third(Runnable printThird) throws InterruptedException {
        thirdReady.acquire();
        printThird.run();
    }
}
```

**Invariant:** two semaphores, each starting at 0 permits (locked), gate `second` and `third`. `first()` runs immediately (nothing gates it) and releases `secondReady`; `second()` blocks on `secondReady.acquire()` until that release happens, regardless of what order the three threads were *started* in. General pattern for "enforce order on unpredictably-scheduled threads": one semaphore per downstream step, released only by the step that must precede it.

## LC 1115 — Print FooBar Alternately

```java
public class FooBar {
    private final Semaphore fooTurn = new Semaphore(1); // foo goes first
    private final Semaphore barTurn = new Semaphore(0);

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooTurn.acquire(); printFoo.run(); barTurn.release();
        }
    }
    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barTurn.acquire(); printBar.run(); fooTurn.release();
        }
    }
}
```

**Invariant:** two semaphores acting as a strict ping-pong baton — `fooTurn` starts with 1 permit (so `foo` goes first), `barTurn` starts with 0. Each side releases the other's semaphore after printing, so both threads can never hold "the turn" simultaneously, guaranteeing strict alternation regardless of OS thread scheduling.

## LC 1116 — Print Zero Even Odd

```java
public class ZeroEvenOdd {
    private final Semaphore zeroTurn = new Semaphore(1);
    private final Semaphore evenTurn = new Semaphore(0);
    private final Semaphore oddTurn = new Semaphore(0);

    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            zeroTurn.acquire();
            printNumber.accept(0);
            if (i % 2 == 1) oddTurn.release(); else evenTurn.release();
        }
    }
    // even()/odd() each acquire their own turn, print, then release zeroTurn
}
```

**Invariant:** a three-way baton where `zero` alone decides which of `even`/`odd` goes next, based on the parity of the number about to print — coordination logic lives entirely in the `zero` thread; `even`/`odd` are symmetric, simple consumers of their own semaphore.

## LC 62 — Unique Paths

```java
static int uniquePaths(int rows, int cols) {
    int[] dp = new int[cols];
    Arrays.fill(dp, 1);
    for (int r = 1; r < rows; r++) {
        for (int c = 1; c < cols; c++) {
            dp[c] += dp[c - 1];
        }
    }
    return dp[cols - 1];
}
```

**Invariant:** `dp[r][c] = dp[r-1][c] + dp[r][c-1]` (only two ways into any cell: above or left), collapsed to a 1D rolling array since row `r` only needs row `r-1`'s values — `dp[c]` on the right of `+=` still holds the previous row's value at the moment it's read, before being overwritten. **Complexity:** O(rows × cols) time, O(cols) space.

## LC 1143 — Longest Common Subsequence

```java
static int longestCommonSubsequence(String a, String b) {
    int[][] dp = new int[a.length() + 1][b.length() + 1];
    for (int i = 1; i <= a.length(); i++)
        for (int j = 1; j <= b.length(); j++)
            dp[i][j] = a.charAt(i-1) == b.charAt(j-1)
                ? dp[i-1][j-1] + 1
                : Math.max(dp[i-1][j], dp[i][j-1]);
    return dp[a.length()][b.length()];
}
```

**Invariant:** `dp[i][j]` is the LCS length of the first `i` characters of `a` and first `j` characters of `b`. A matching character extends the diagonal predecessor's LCS by one; a mismatch takes the best of dropping one character from either string. **Complexity:** O(m × n) time and space.

## LC 416 — Partition Equal Subset Sum

```java
static boolean canPartition(int[] nums) {
    int sum = Arrays.stream(nums).sum();
    if (sum % 2 != 0) return false;
    int target = sum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;
    for (int n : nums) {
        for (int t = target; t >= n; t--) {   // DOWNWARD -- each num used at most once
            dp[t] = dp[t] || dp[t - n];
        }
    }
    return dp[target];
}
```

**Invariant:** 0/1 knapsack — can some subset of `nums` sum exactly to `target`? Iterating the target **downward** for each number is what makes this 0/1 (each number usable once) rather than unbounded — the mirror image of Week 8's Coin Change (`05-java-coding-practice.md`), which iterates upward to allow reuse. Same recurrence shape, opposite iteration direction, opposite reuse semantics — worth internalizing as one fact, not two. **Complexity:** O(n × target) time, O(target) space.

## LC 5 — Longest Palindromic Substring

```java
static String longestPalindrome(String s) {
    boolean[][] dp = new boolean[n][n];
    for (int i = 0; i < n; i++) dp[i][i] = true;
    for (int len = 2; len <= n; len++) {          // shortest intervals FIRST
        for (int i = 0; i + len - 1 < n; i++) {
            int j = i + len - 1;
            if (s.charAt(i) != s.charAt(j)) continue;
            dp[i][j] = (len == 2) || dp[i + 1][j - 1];
            // track the longest dp[i][j] == true seen
        }
    }
}
```

**Invariant:** interval DP — `dp[i][j]` depends on `dp[i+1][j-1]`, a strictly shorter interval, so the loop must fill by increasing interval length, not by row or column index like LC 1143. Key structural difference between "2D DP over two independent sequences" (LC 1143) and "interval DP over one sequence" (LC 5) — fill order is dictated by dependency direction; getting it backwards reads uninitialized state. **Complexity:** O(n²) time, O(n²) space.

## Verification — real, not asserted

**Concurrency (100 randomized-scheduling trials on LC 1114; n=1000 on LC 1115/1116):**

```
== LC 1114: Print in Order (100 randomized-scheduling trials) ==
  PASS  all 100 trials printed "123" regardless of thread start order (3,1,2)

== LC 1115: Print FooBar Alternately (n=1000, verify no foo-foo or bar-bar) ==
  PASS  foobar output has exactly 2000 entries
  PASS  output strictly alternates foo,bar,foo,bar,... for all 2000 entries

== LC 1116: Print Zero Even Odd (n=1000, verify 0,1,0,2,0,3,... pattern) ==
  PASS  zero-even-odd output has exactly 2000 entries
  PASS  output is exactly 0,1,0,2,0,3,...,0,1000
Week 9 concurrency coding suite: 5/5 assertions passed
```

**DP part 2:**

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
  PASS  longestPalindrome("cbbd") has length 2 (bb), got "bb"
  PASS  longestPalindrome("a") has length 1 (single character is trivially a palindrome), got "a"
  PASS  longestPalindrome("racecar") has length 7 (the whole string is a palindrome), got "racecar"
Week 9 DP part 2 suite: 21/21 assertions passed
```

Full source: `practice/java/week-09/concurrency-coding/src/`, `practice/java/week-09/dp-part2/src/`. Reproduce: `cd <dir> && javac -d out src/*.java && java -cp out Main`.

## Exit check

- [ ] All 7 problems solved with a written retrospective
- [ ] Can explain why LC 416 iterates its target downward while Week 8's Coin Change iterates upward, unprompted
- [ ] Can explain why LC 5's fill order (by interval length) differs from LC 1143's fill order (by row/column) and why that difference is structural, not stylistic
- [ ] Can design a semaphore-based coordination scheme for a NEW ordering constraint not in this pack (e.g., four threads, two independent pairs that must each be ordered but the pairs can interleave)
