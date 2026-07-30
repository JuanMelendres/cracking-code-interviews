---
title: "Flashcards — Week 10"
week: 10
last_reviewed: 2026-07-29
---

# Flashcards — Week 10

16 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What specifically does a dual write (DB write + separate message publish) fail to guarantee?**
A: That the message gets published if the DB write succeeds and a crash follows before the publish call — no shared transaction records that a publish is owed.

**2. Q: What makes the transactional outbox's atomicity possible?**
A: Writing the business row and the outbox row in the SAME database transaction — Postgres's own guarantee, not application coordination.

**3. Q: Is the transactional outbox exactly-once or at-least-once?**
A: At-least-once — measured: a crash between "Kafka confirms" and "DB marks published" produced one real duplicate and zero losses across 3 events.

**4. Q: Why is 2PC avoided despite offering true atomicity?**
A: Locks held across the coordinator's round-trip; a coordinator crash leaves "prepared" participants stuck indefinitely — poor availability under partial failure.

**5. Q: What does partition pruning require to work?**
A: The query must filter by the partition/shard key — anything else fans out to every partition (measured 0.727ms pruned vs 2.667ms unpruned).

**6. Q: Why is shard-key selection called a "one-way door"?**
A: Changing it after data exists requires physically migrating the data, not a configuration change.

**7. Q: Why does naive `hash(key) % N` remap nearly all keys when N changes?**
A: The divisor itself changed — nearly every key's modulus result differs. Measured: 92.5% of keys remapped removing 1 of 10 nodes.

**8. Q: What fraction of keys should move under consistent hashing when removing 1 of N nodes?**
A: Roughly `1/N` — measured 9.2% for N=10, close to the 10% ideal.

**9. Q: Why use many virtual nodes per physical node in consistent hashing?**
A: One point per node gives uneven load by chance; many virtual nodes converge each node's share closer to an even `1/N`.

**10. Q: What does a circuit breaker's OPEN state actually save, measured?**
A: Converts a call costing the full downstream timeout (200ms) into one failing in ~0ms — real, quantified savings during an outage.

**11. Q: What does retry jitter fix, precisely?**
A: Without it, every client retries at the exact same instant on every attempt (measured, not theoretical) — a retry-storm risk.

**12. Q: What is a bulkhead, and what does it prevent?**
A: A per-dependency resource pool; prevents one slow/failing dependency from exhausting a shared pool and starving callers of unrelated dependencies.

**13. Q: What lock does a plain `CREATE INDEX` hold, and what does it block?**
A: A `SHARE` lock for the entire build duration — blocks writes. Measured: a concurrent INSERT waited 1943ms.

**14. Q: What does `CREATE INDEX CONCURRENTLY` trade for not blocking writes?**
A: A slower, multi-pass build and risk of a leftover `INVALID` index on failure. Measured: the same INSERT took 84ms instead.

**15. Q: Why can't you directly rename a column during a rolling deploy?**
A: Old and new application versions run concurrently during rollout; expand-contract (add, dual-write+backfill, drop) keeps both working.

**16. Q: LC 23's heap never exceeds size k (number of lists) — why, without an explicit eviction check?**
A: It holds at most one node per source list at a time; polling the min and immediately offering its successor preserves that invariant naturally.
