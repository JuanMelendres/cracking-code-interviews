# Week 5 Java — Design Coding — runnable verification

Compiled and run on OpenJDK 21.0.12. Same hand-rolled `Check` harness as prior weeks.

## Reproduce

```bash
cd practice/java/week-05/design-coding
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `RandomizedSet.java` | LC 380 — Insert Delete GetRandom O(1) |
| `MyHashMap.java` | LC 706 — Design HashMap, from scratch |
| `MyCircularQueue.java` | LC 622 — Design Circular Queue (the exact audited errata, fixed) |
| `Main.java` | Runs all 23 assertions, including a deliberately forced hash collision |

## Real output (last run)

```
== LC 380: Insert Delete GetRandom O(1) ==
  PASS  insert 1 succeeds (new value)
  PASS  insert 1 again fails (already present)
  PASS  insert 2 succeeds
  PASS  remove 1 succeeds
  PASS  remove 1 again fails (already gone)
  PASS  getRandom returns a value currently in the set

== LC 706: Design HashMap ==
  PASS  get key 1
  PASS  get missing key returns -1
  PASS  put on existing key updates value
  PASS  get after remove returns -1
  PASS  collision: key 1 still correct after inserting key 1025
  PASS  collision: key 1025 correct, chained in the same bucket as key 1

== LC 622: Design Circular Queue (errata fix) ==
  PASS  enqueue 1
  PASS  enqueue 2
  PASS  enqueue 3
  PASS  enqueue 4 fails, queue is full (isFull correctly implemented)
  PASS  Rear() returns 3
  PASS  dequeue succeeds
  PASS  enqueue 4 now succeeds after a dequeue freed a slot
  PASS  Front() returns 2 after the first dequeue
  PASS  Rear() returns 4 (wrapped around the circular buffer)
  PASS  isEmpty() true after dequeuing everything
  PASS  Front() on empty queue returns -1
Week 5 design-coding suite: 23/23 assertions passed
```

## Note on scope

The roadmap's Track B for this week lists LC 155, 380, 706, 622. LC 155 (Min Stack) was already implemented and verified in Week 2 (`practice/java/week-02/`) — it is not repeated here to avoid duplicate, redundant content; this pack covers the three problems genuinely new to Week 5.
