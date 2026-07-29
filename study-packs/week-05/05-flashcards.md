---
title: "Flashcards — Week 5"
week: 5
last_reviewed: 2026-07-29
---

# Flashcards — Week 5

14 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What's the actual test for where to draw a service boundary?**
A: Where strong single-transaction consistency is NOT required across the line — the same test as an aggregate boundary.

**2. Q: Two services need one transaction — what does that signal?**
A: The boundary may be wrong, or the operation needs to become an explicitly eventually-consistent saga, not a distributed transaction.

**3. Q: Name a concrete signal that two services should be merged back.**
A: They are always co-deployed together (detectable via deployment-history correlation).

**4. Q: Should a 4-engineer team default to microservices?**
A: Generally no — the organizational benefit requires multiple independently-scheduled teams.

**5. Q: What does an idempotency key actually protect against?**
A: A duplicate side effect (e.g., a double charge) from a client retrying a request it can't confirm succeeded.

**6. Q: What coordinates concurrent duplicate idempotent requests correctly?**
A: The storage layer's own unique constraint — not application-level locking.

**7. Q: Why is a TTL necessary on an idempotency-key mechanism?**
A: Without it, a crashed in-progress attempt permanently blocks every future retry of that key.

**8. Q: Correct client behavior when a response never arrives?**
A: Retry, unconditionally, with the same idempotency key.

**9. Q: Does CAP apply outside of an actual network partition?**
A: No — it's specifically a statement about partition behavior.

**10. Q: What does a CP system do during a partition?**
A: Refuses requests it can't guarantee are current on the partitioned-off side.

**11. Q: What does an AP system do during a partition?**
A: Continues serving requests on both sides, accepting they may disagree until reconciliation.

**12. Q: Should one consistency model apply uniformly across a whole system?**
A: No — different data types warrant different models.

**13. Q: What's the `h ^ (h >>> 16)` technique for?**
A: Spreads a hash code's high bits into the low bits before a modulo, so a bucket-index calculation doesn't waste the entropy in the high bits.

**14. Q: What was the exact audited Circular Queue defect?**
A: A `size`-like field declared but never used, and missing `Front()`/`Rear()`/`isEmpty()`/`isFull()` — all required by LC 622.
