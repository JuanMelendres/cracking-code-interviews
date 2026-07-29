# Week 8 Java — Dynamic Programming (T-1411 part 1) — runnable verification

LC 70, 198, 322, 300. No external dependencies.

## Setup and run

```bash
cd practice/java/week-08/dp
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Real observed output (last run):**

```
== LC 70: Climbing Stairs ==
  PASS  climbStairs(1) = 1
  PASS  climbStairs(2) = 2
  PASS  climbStairs(3) = 3 (1+1+1, 1+2, 2+1)
  PASS  climbStairs(5) = 8 (Fibonacci shape)

== LC 198: House Robber ==
  PASS  rob([1,2,3,1]) = 4 (rob house 0 and 2)
  PASS  rob([2,7,9,3,1]) = 12 (rob houses 0,2,4)
  PASS  rob([]) = 0

== LC 322: Coin Change ==
  PASS  coinChange([1,2,5], 11) = 3 (5+5+1)
  PASS  coinChange([2], 3) = -1 (unreachable)
  PASS  coinChange([1], 0) = 0

== LC 300: Longest Increasing Subsequence ==
  PASS  lengthOfLIS([10,9,2,5,3,7,101,18]) = 4 (2,3,7,101 or 2,3,7,18)
  PASS  lengthOfLIS([7,7,7,7]) = 1 (strictly increasing)
  PASS  lengthOfLIS([0,1,0,3,2,3]) = 4

== LC 300 cross-check: O(n log n) vs O(n^2) reference on random inputs ==
  PASS  O(n log n) LIS matches O(n^2) reference on 200 random trials
Week 8 DP suite: 14/14 assertions passed
```

`DpProblems.java` holds the four solutions plus an O(n²) reference LIS used only to cross-check the O(n log n) solution in `Main.java` — 200 randomized trials, seeded (`new Random(42)`) for reproducibility.
