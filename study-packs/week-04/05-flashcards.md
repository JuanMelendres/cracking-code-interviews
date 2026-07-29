---
title: "Flashcards — Week 4"
week: 4
last_reviewed: 2026-07-29
---

# Flashcards — Week 4

16 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: How does cache/database disagreement typically happen?**
A: A cache-aside read populates a stale value after a concurrent write already invalidated the cache — a race, not a generic sync bug.

**2. Q: What happens to the database when the entire cache dies at peak?**
A: A full-working-set stampede — every previously cached read now falls through simultaneously, against a database sized assuming cache assistance.

**3. Q: Name three cache-stampede fixes.**
A: Single-flight/request coalescing, probabilistic early expiration with jitter, stale-while-revalidate.

**4. Q: Three hot-key mitigations?**
A: Local in-process caching, key sharding across suffixed keys, edge/CDN caching.

**5. Q: Why is a network timeout ambiguous?**
A: It can't distinguish "request lost," "still processing," and "succeeded but the response was lost."

**6. Q: Precisely how do retries amplify an outage?**
A: They add new work on top of the still-running original attempt rather than replacing it (confirmed: 2.3x load, 3x elapsed time, zero success-rate improvement without backoff).

**7. Q: What structurally fixes the retry-safety problem?**
A: Idempotency keys — the server recognizes a retried operation and returns the original result instead of re-executing it.

**8. Q: What structurally prevents split-brain corruption?**
A: A fencing token, enforced at the storage layer, rejecting any write carrying an older token than one already accepted.

**9. Q: Why does OFFSET pagination get slower with depth?**
A: The database must walk and discard every skipped row before returning the requested page (confirmed: ~3,000x slower at depth 1M vs depth 100).

**10. Q: What does keyset pagination give up for its flat cost at any depth?**
A: Arbitrary page-number jumping — only forward/backward from a known cursor.

**11. Q: Is PUT idempotent? Is POST?**
A: PUT yes, by definition. POST only with a client-supplied idempotency key.

**12. Q: Three-color DFS states, and why not just visited/unvisited?**
A: Unvisited / in-progress / done — two colors can't distinguish "fully explored, safe" from "currently on the call stack, this is a cycle."

**13. Q: Kahn's algorithm's cycle-detection signal?**
A: If the queue empties before every node has been processed, a cycle exists among the remaining unprocessed nodes.

**14. Q: Union-Find's two optimizations, named?**
A: Path compression (flattens the tree on find) and union by rank (attaches the smaller tree under the larger's root).

**15. Q: What's the trap in LC 133 (Clone Graph) that a cycle exposes?**
A: The visited map must be populated before recursing into neighbors, or a cycle causes infinite recursion.

**16. Q: Why does single-flight caching not just relocate the latency problem?**
A: Every coalesced request was already going to wait for a database load — coalescing removes the 49 *redundant* calls, not the one unavoidable one.
