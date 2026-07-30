# Week 12 Java — Final Loop Coding Set (8 problems) — runnable verification

LC 3, 207, 56, 139, 128, 973, 55, 127 — 2 problems per loop, no external dependencies.

## Setup and run

```bash
cd practice/java/week-12/final-loop-coding
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Real observed output (last run, abbreviated — full output in `study-packs/week-12/07-java-coding-practice.md`):**

```
== Loop 1 -- LC 3: Longest Substring Without Repeating Characters ==
  PASS  "abcabcbb" -> 3 ("abc")
...
== Loop 4 -- LC 127: Word Ladder ==
  PASS  ladderLength(hit->cog) = 5 (hit->hot->dot->dog->cog)
  PASS  ladderLength = 0 when endWord not in the dictionary
Week 12 final-loop coding suite (8 problems): 15/15 assertions passed
```

The first draft of the `Main.java` test for LC 56 asserted the wrong expected merge result — a real bug caught by running the suite, not by re-reading the code. See the study-pack chapter's "Verification" section for the specifics.
