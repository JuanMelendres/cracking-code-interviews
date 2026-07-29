# Week 2 Java — runnable verification

Compiled and run on OpenJDK 21.0.12. Same hand-rolled `Check` harness as Week 1 (see `practice/java/week-01/README.md` for why no JUnit).

## Reproduce

```bash
cd practice/java/week-02
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `BinarySearchProblems.java` | LC 704, LC 35, LC 33, LC 875 — `study-packs/week-02/07-java-coding-practice.md` Days 1–3 |
| `StackProblems.java` | LC 20, LC 739 (corrected, index-based) — Days 4–5 |
| `MinStack` (in `StackProblems.java`) | LC 155 |
| `Trie.java` | LC 208 — Day 6 |
| `Main.java` | Runs all 21 assertions |

## Real output (last run)

```
== Week 2 problem set — binary search family ==
  PASS  LC704 search found
  PASS  LC704 search not found
  PASS  LC35 exact match
  PASS  LC35 insert between
  PASS  LC35 insert at end
  PASS  LC33 target in right half
  PASS  LC33 target absent
  PASS  LC875 koko example 1
  PASS  LC875 koko example 2
  PASS  LC875 koko example 3

== Stack family ==
  PASS  LC20 valid nested
  PASS  LC20 mismatched
  PASS  LC20 unclosed
  PASS  LC155 min after 3 pushes
  PASS  LC155 top after pop
  PASS  LC155 min after pop
  PASS  LC739 daily temperatures, index-based monotonic stack (corrected — see errata)

== Trie ==
  PASS  LC208 search exact word after insert
  PASS  LC208 prefix alone is not a word
  PASS  LC208 startsWith matches prefix
  PASS  LC208 search matches after inserting the prefix as its own word
Week 2 suite: 21/21 assertions passed
```
