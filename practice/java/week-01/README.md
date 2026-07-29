# Week 1 Java — runnable verification

Compiled and run on OpenJDK 21.0.12 (Homebrew build), this repository's initialization session. No build tool (Maven/Gradle) or JUnit jar was available in that environment, so verification uses a small hand-rolled assertion harness (`Check.java`) instead of `@Test` annotations. Converting this to a real JUnit 5 project (`pom.xml` or `build.gradle`) is future work, not required for what this pack claims.

## Reproduce

```bash
cd practice/java/week-01
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `Check.java` | Minimal assertion harness (`eq`, `isTrue`, `summary`) |
| `Problems.java` | The 6 warm-up problems from `study-packs/week-01/07-java-coding-practice.md` Days 1–4 |
| `LRUCacheBuggy.java` | ⛔ The defective LRU implementation, reproducing the Phase 1 audit's errata finding — do not use as a reference |
| `LRUCacheFixed.java` | The corrected LRU implementation |
| `Main.java` | Runs all 18 assertions and prints a summary |

## Real output (last run)

```
== Week 1 problem set ==
  PASS  twoSum index0
  PASS  twoSum index1
  PASS  twoSumSorted 1-indexed lo
  PASS  twoSumSorted 1-indexed hi
  PASS  maxProfit typical case
  PASS  maxProfit monotonic decreasing -> 0
  PASS  isAnagram true case
  PASS  isAnagram false case
  PASS  groupAnagrams produces 3 groups
  PASS  longestSubstring abcabcbb -> 3
  PASS  longestSubstring bbbbb -> 1
  PASS  longestSubstring pwwkew -> 3

== LC 146 LRU Cache — fixed implementation ==
  PASS  fixed: get(1) after put(1,1) put(2,2)
  PASS  fixed: get(2) evicted by capacity, correct eviction target
  PASS  fixed: get(3) present

== Errata drill — reproducing the buggy version's failure ==
  PASS  buggy: get(2) incorrectly evicted (this IS the bug, reproduced on purpose)

== Fixed version does not reproduce the failure on the same sequence ==
  PASS  fixed: get(2) survives the update-at-capacity sequence
  PASS  fixed: get(1) reflects the update
Week 1 suite: 18/18 assertions passed
```
