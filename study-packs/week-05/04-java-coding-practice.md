---
title: "Java Coding Practice — Week 5"
week: 5
last_reviewed: 2026-07-29
---

# Java Coding Practice — Week 5

**3 design-coding problems, all compiled and run.** See `practice/java/week-05/design-coding/README.md` for the note on why LC 155 (already verified in Week 2) isn't repeated here.

## Table of Contents

1. [LC 380 — Insert Delete GetRandom O(1)](#lc-380--insert-delete-getrandom-o1)
2. [LC 706 — Design HashMap](#lc-706--design-hashmap)
3. [LC 622 — Design Circular Queue (errata fix)](#lc-622--design-circular-queue-errata-fix)
4. [Verification](#verification--real-not-asserted)

---

## LC 380 — Insert Delete GetRandom O(1)

```java
boolean remove(int val) {
    if (!valueToIndex.containsKey(val)) return false;
    int indexToRemove = valueToIndex.get(val);
    int lastIndex = values.size() - 1;
    int lastValue = values.get(lastIndex);
    values.set(indexToRemove, lastValue);
    valueToIndex.put(lastValue, indexToRemove);
    values.remove(lastIndex);
    valueToIndex.remove(val);
    return true;
}
```

**Invariant:** removal never shifts elements — it swaps the target with the *last* element (O(1)), then removes from the end (also O(1) for an `ArrayList`). The `valueToIndex` map is what makes the swap target findable in O(1) instead of requiring a linear scan. **Complexity:** O(1) for all three operations.

## LC 706 — Design HashMap

```java
private int bucketIndex(int key) {
    int h = Integer.hashCode(key);
    h ^= (h >>> 16);
    return Math.floorMod(h, BUCKETS);
}
```

**Invariant:** separate chaining — each bucket holds a linked list of entries hashing to it; `get`/`put`/`remove` all walk the chain at the target bucket. The `h ^ (h >>> 16)` spread mirrors `java.util.HashMap`'s actual internal technique: without it, a modulo-based bucket index only ever looks at the hash code's low bits, wasting the entropy in the high bits. **Verified with a deliberately forced collision** (keys 1 and 1025, which collide at a 1024-bucket table) to confirm chaining actually works, not just the no-collision happy path. **Complexity:** O(1) average, O(n) worst case for a bucket with many collisions (this implementation does not treeify, unlike the real `java.util.HashMap` past 8 entries in one bucket).

## LC 622 — Design Circular Queue (errata fix)

```java
boolean enQueue(int value) {
    if (isFull()) return false;
    int rearIndex = (front + count) % data.length;
    data[rearIndex] = value;
    count++;
    return true;
}
boolean isEmpty() { return count == 0; }
boolean isFull() { return count == data.length; }
```

**This is the exact defect the Phase 1 audit found** in the source material: a `size`-like field declared but never read or written, and `Front()`, `Rear()`, `isEmpty()`, `isFull()` all missing despite being required by this exact LeetCode problem. This implementation adds all four, using a `count` field that *is* actually read and written, and computes `Rear()` as `(front + count - 1) % data.length` to correctly account for wraparound. **Complexity:** O(1) for every operation.

## Verification — real, not asserted

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

Full output and reproduce instructions: `practice/java/week-05/design-coding/README.md`.

## Exit check

- [ ] All 3 problems solved with a written retrospective
- [ ] Can explain the `h ^ (h >>> 16)` spread technique and why a plain modulo alone under-uses the hash code
- [ ] Can state the exact Circular Queue defect from the audit without looking it up
