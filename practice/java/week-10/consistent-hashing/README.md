# Week 10 Java — Consistent Hashing — runnable verification

One real demo. No external dependencies.

## Setup and run

```bash
cd practice/java/week-10/consistent-hashing
mkdir -p out
javac -d out src/ConsistentHashingDemo.java
java -cp out ConsistentHashingDemo
```

**Real observed output (last run):**

```
== naive hash % N ==
removed 1 of 10 nodes: 9247 of 10000 keys (92.5%) remapped to a different node
(theoretical worst case for hash%N on ANY node-count change: nearly ALL keys remap, because N itself changed, and every key's slot is k.hashCode() % N)

== consistent hashing with 150 virtual nodes per physical node ==
removed 1 of 10 nodes: 920 of 10000 keys (9.2%) remapped to a different node
(theoretical ideal for removing 1 of 10 nodes: ~10.0% -- only that node's own keys should move, to neighbors on the ring)
```

**What this proves:** 10,000 real keys, 10 real nodes, one real removal — naive `hash % N` remaps 92.5% of all keys; consistent hashing with 150 virtual nodes per physical node remaps only 9.2%, close to the theoretical `1/10 = 10%` ideal.
