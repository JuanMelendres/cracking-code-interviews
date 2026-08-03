---
title: "Coding Practice — Greedy (T-1413)"
week: 20
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Greedy (T-1413)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 1/10 to 6/10 register problems.

---

## Problem 1 — LC 45 Jump Game II

**Pattern:** implicit BFS-layer expansion — track the farthest index reachable within the current "layer," incrementing jumps only when the layer is exhausted.

```java
static int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest;
        }
    }
    return jumps;
}
```

**Retrospective:** this is greedy, not DP, because at each layer boundary the algorithm never needs to reconsider an earlier decision — committing to "jump as far as the current layer allows" is always at least as good as any less-greedy choice, since farther reach can only help future jumps, never hurt them. **Complexity:** O(n) time, O(1) space — notably better than an O(n²) DP formulation of the same problem.

## Problem 2 — LC 134 Gas Station

**Pattern:** running-total reset — if the tank ever goes negative, no station before the failure point can be a valid start either.

```java
static int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0, currentTank = 0, startIndex = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        totalTank += diff;
        currentTank += diff;
        if (currentTank < 0) {
            startIndex = i + 1;
            currentTank = 0;
        }
    }
    return totalTank >= 0 ? startIndex : -1;
}
```

**Retrospective:** the key insight most candidates miss without prompting: if starting at station `i` and the tank goes negative at station `j`, then *no* station between `i` and `j` can be a valid start either (each would arrive at `j` with an equal-or-smaller tank) — this is exactly what justifies jumping the candidate start straight to `j+1` rather than trying every index individually. **Complexity:** O(n) time, O(1) space, versus an O(n²) brute-force check of every starting index.

## Problem 3 — LC 621 Task Scheduler

**Pattern:** closed-form greedy formula from the most-frequent task's required spacing.

```java
static int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    int maxFreq = 0;
    for (int f : freq) maxFreq = Math.max(maxFreq, f);
    int maxCount = 0;
    for (int f : freq) if (f == maxFreq) maxCount++;
    int formula = (maxFreq - 1) * (n + 1) + maxCount;
    return Math.max(formula, tasks.length);
}
```

**Retrospective:** the most-frequent task forces `(maxFreq - 1)` full cooldown windows of size `(n + 1)`, plus however many *other* tasks are tied at that same max frequency (each needs its own slot in the final window). The `Math.max(formula, tasks.length)` guard handles the case where there are enough distinct tasks that idle slots are never actually needed. **Complexity:** O(tasks.length + 26) time — effectively O(n).

## Problem 4 — LC 763 Partition Labels

**Pattern:** last-occurrence tracking — extend the current partition's boundary to cover every character's last appearance seen so far.

```java
static List<Integer> partitionLabels(String s) {
    int[] lastIndex = new int[26];
    for (int i = 0; i < s.length(); i++) lastIndex[s.charAt(i) - 'a'] = i;
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    return result;
}
```

**Retrospective:** a partition can only be "closed" once every character seen inside it has had its last occurrence accounted for — otherwise that character would reappear in a later partition, violating the "each letter in at most one part" requirement. **Complexity:** O(n) time (one pass to index last occurrences, one pass to partition), O(1) space for the fixed 26-letter alphabet.

## Problem 5 — LC 402 Remove K Digits

**Pattern:** monotonic-stack greedy — pop any digit larger than the incoming one while removals remain, to keep leading digits as small as possible.

```java
static String removeKdigits(String num, int k) {
    StringBuilder stack = new StringBuilder();
    for (char c : num.toCharArray()) {
        while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > c) {
            stack.deleteCharAt(stack.length() - 1);
            k--;
        }
        stack.append(c);
    }
    while (k > 0 && stack.length() > 0) {
        stack.deleteCharAt(stack.length() - 1);
        k--;
    }
    int i = 0;
    while (i < stack.length() - 1 && stack.charAt(i) == '0') i++;
    String result = stack.substring(i);
    return result.isEmpty() ? "0" : result;
}
```

**Retrospective:** a larger digit followed by a smaller one is always worth removing the larger one (it makes the number smaller from a more-significant position), which is exactly what a monotonic-increasing stack enforces; leftover removals after the scan come off the end (least significant digits), and leading zeros in the result must be stripped since a numeric result can't have them. **Complexity:** O(n) time, O(n) space — each digit is pushed and popped at most once.

## Verification

```
$ cd practice/java/week-20/greedy/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC45 jump([2,3,1,1,4]) = 2
  PASS  LC45 jump([2,3,0,1,4]) = 2
  PASS  LC134 canCompleteCircuit start index = 3
  PASS  LC134 canCompleteCircuit no solution = -1
  PASS  LC621 leastInterval(AAABBB, n=2) = 8
  PASS  LC621 leastInterval(AAABBB, n=0) = 6 (no cooldown needed)
  PASS  LC763 partitionLabels("ababcbacadefegdehijhklij")
  PASS  LC402 removeKdigits("1432219", 3)
  PASS  LC402 removeKdigits("10200", 1) -- leading zeros stripped
  PASS  LC402 removeKdigits("10", 2) -- empty result becomes "0"
Week 20 — Greedy (LC 45, 134, 621, 763, 402): 10/10 assertions passed
```
