---
title: "Flashcards — Week 8"
week: 8
last_reviewed: 2026-07-29
---

# Flashcards — Week 8

14 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What does Kafka guarantee about record ordering?**
A: Total order within a single partition only — no guarantee across partitions or across a topic as a whole.

**2. Q: Why is changing partition count on a keyed topic dangerous?**
A: It changes every key's `hash(key) % partitionCount` mapping, silently remapping and breaking existing per-key ordering.

**3. Q: What is the ISR, and what does `acks=all` actually wait for?**
A: The In-Sync Replica set — replicas caught up with the leader. `acks=all` waits for the CURRENT ISR, not `replication.factor` replicas, so it needs `min.insync.replicas` to mean anything durable.

**4. Q: What does an idempotent producer deduplicate, and what does it NOT cover?**
A: Deduplicates its own retried sends to Kafka (by producer ID + partition + sequence number). Does not prevent consumer-side duplicate processing.

**5. Q: What does the sticky partitioner do with a null key?**
A: Batches records onto one partition per in-flight batch to maximize batch size, rather than strict per-record round robin.

**6. Q: Can two consumers in the same group read the same partition at the same time?**
A: No — exactly one consumer per partition per group at a time.

**7. Q: What's the most common cause of a consumer group rebalancing repeatedly?**
A: `max.poll.interval.ms` violations from slow per-batch processing, causing the coordinator to evict a live-but-slow consumer.

**8. Q: What caps consumer parallelism for a single topic?**
A: The partition count — consumers beyond it sit idle.

**9. Q: What causes at-least-once duplicate processing?**
A: Committing the offset AFTER processing; a crash between processing and commit causes redelivery on restart.

**10. Q: What causes at-most-once silent loss?**
A: Committing the offset BEFORE processing; a crash after commit but before processing means that record is never retried.

**11. Q: Is Kafka exactly-once real, precisely?**
A: Yes for the Kafka-to-Kafka transactional read-process-write loop; no, it does not extend to writes on external systems without an outbox or idempotent consumer.

**12. Q: Given `dp[i] = dp[i-1] + dp[i-2]`, what LeetCode problem and what does it mean?**
A: LC 70 Climbing Stairs — reaching step `i` comes from a 1-step (from `i-1`) or a 2-step (from `i-2`); it's the Fibonacci recurrence.

**13. Q: In LC 322 Coin Change, why does the amount loop go on the outside and the coin loop on the inside?**
A: That ordering allows unlimited reuse of each coin denomination (unbounded knapsack); the reverse ordering would cap each coin to one use.

**14. Q: In the O(n log n) LIS solution, what does `tails[k]` actually represent?**
A: The smallest possible tail value among all increasing subsequences of length `k+1` found so far — not a real subsequence itself, which is what makes binary search over the array valid.
