---
title: "Flashcards — Week 3"
week: 3
last_reviewed: 2026-07-29
---

# Flashcards — Week 3

16 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: Why does self-invocation break `@Transactional`?**
A: The call never passes through the Spring-managed proxy that starts the transaction.

**2. Q: Does a checked exception roll back a `@Transactional` method by default?**
A: No — only `RuntimeException`/`Error`, unless `rollbackFor` says otherwise.

**3. Q: Real use case for `REQUIRES_NEW`?**
A: An audit log entry that must survive even if the calling operation later rolls back.

**4. Q: `REQUIRES_NEW`'s specific deadlock risk?**
A: It suspends the outer transaction's connection and starts a new one; if the inner transaction needs a row the suspended outer transaction already locked, it blocks indefinitely.

**5. Q: Is `readOnly = true` guaranteed to prevent writes?**
A: No — enforcement is driver-dependent (confirmed: not enforced on H2, enforced on PostgreSQL).

**6. Q: Production cost of an HTTP call inside a transaction?**
A: It holds a pooled connection for the call's duration, reducing pool availability for every other concurrent request.

**7. Q: Difference between a lost update and write skew?**
A: Lost update is a same-row conflict; write skew is a cross-row invariant violation where each transaction's own single-row write looks fine in isolation.

**8. Q: Does REPEATABLE READ prevent write skew?**
A: No — confirmed via real reproduction; it prevents same-row lost updates but not cross-row invariant violations.

**9. Q: What does SERIALIZABLE do differently?**
A: Tracks read-write dependencies across transactions (SSI) and aborts one transaction at commit time if a dangerous cycle is detected.

**10. Q: What must application code do to safely use SERIALIZABLE?**
A: Implement retry-on-serialization-failure.

**11. Q: Name the six design-method phases, in order.**
A: Clarify, Estimate, API, Data, Architecture, Bottlenecks.

**12. Q: Why estimate before architecture?**
A: So every architectural decision is justified by a specific number, not reflex.

**13. Q: Most important assumption to state explicitly in a QPS estimate?**
A: The peak-to-average ratio — architecture is sized to peak, not average.

**14. Q: Most commonly skipped design-method phase, and why it matters?**
A: Phase 6, bottlenecks — it's the phase most directly testing production judgment.

**15. Q: LC 98's classic trap?**
A: Checking only the immediate parent-child relationship isn't sufficient — bounds must be carried down through the whole recursion, tightened at each level.

**16. Q: Why is LC 235's BST-specific LCA faster than the general binary-tree LCA?**
A: BST ordering lets you decide direction in O(h) using value comparisons alone, instead of O(n) general tree search.
