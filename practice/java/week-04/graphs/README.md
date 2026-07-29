# Week 4 Java — Graph Problems — runnable verification

Compiled and run on OpenJDK 21.0.12. Same hand-rolled `Check` harness as Weeks 1–3.

## Reproduce

```bash
cd practice/java/week-04/graphs
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `UnionFind.java` | Disjoint-set with path compression + union by rank, from scratch |
| `GraphProblems.java` | LC 200, 133, 207, 210, 547 |
| `Main.java` | Runs all 14 assertions |

## Real output (last run)

```
  PASS  LC200 one connected island
  PASS  LC200 three islands
  PASS  LC133 clone is a distinct object from the original
  PASS  LC133 clone preserves root value
  PASS  LC133 clone preserves neighbor count
  PASS  LC133 cloned neighbors are also distinct objects
  PASS  LC207 no cycle -> can finish
  PASS  LC207 cycle -> cannot finish
  PASS  LC210 valid order found for all 4 courses
  PASS  LC210 course 0 before course 1 (prerequisite respected)
  PASS  LC210 course 1 before course 3
  PASS  LC210 cycle -> empty order
  PASS  LC547 two provinces
  PASS  LC547 three isolated provinces
Week 4 graph suite: 14/14 assertions passed
```
