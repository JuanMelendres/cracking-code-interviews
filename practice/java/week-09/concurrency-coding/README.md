# Week 9 Java — Concurrency Coding Problems (T-1417) — runnable verification

LC 1114, 1115, 1116 — thread coordination via `Semaphore`. No external dependencies.

## Setup and run

```bash
cd practice/java/week-09/concurrency-coding
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

**Real observed output (last run):**

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

`Main.java` deliberately starts LC 1114's three threads in the WRONG order (3, 1, 2) on every one of 100 trials, to verify the semaphore-based ordering is enforced by the coordination logic itself, not by accidentally-favorable OS thread scheduling.
