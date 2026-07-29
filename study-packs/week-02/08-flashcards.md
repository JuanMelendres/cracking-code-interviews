---
title: "Flashcards — Week 2"
week: 2
last_reviewed: 2026-07-29
---

# Flashcards — Week 2

14 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4.

---

**1. Q: What does an estimate-vs-actual row mismatch in `EXPLAIN ANALYZE` usually mean?**
A: Stale statistics, or column correlation the planner's per-column statistics can't model.

**2. Q: Why can't a plain B-Tree index serve `WHERE UPPER(col) = ?`?**
A: The index is built from raw values; it has no entry for the function's output. Needs an expression index.

**3. Q: Nested loop vs hash join — the deciding factor?**
A: Nested loop wins when the inner side has cheap, selective lookups (indexed, or repeated values cached via `Memoize`); hash join wins when neither side is small.

**4. Q: What can't a plain many-to-many join table store?**
A: Any fact about the relationship itself — no columns beyond the two foreign keys.

**5. Q: The real trigger for an explicit join entity, precisely?**
A: Any fact that must be true "as of" relationship-formation time, not "as of" read time.

**6. Q: What is an aggregate root?**
A: The only object in an aggregate external code is allowed to reference directly.

**7. Q: What decides aggregate boundaries?**
A: The true consistency invariant, not object composition or conceptual relatedness.

**8. Q: How many repositories does an aggregate get?**
A: One, for the root only.

**9. Q: First question in storage selection?**
A: What are the actual read/write access patterns — not "which technology is trendy."

**10. Q: Hidden cost of polyglot persistence?**
A: Ongoing operational burden (backup, monitoring, on-call expertise) for every additional storage technology.

**11. Q: Name the four beats of trade-off narration, in order.**
A: Context, Options, Decision criterion, What it cost.

**12. Q: Which beat does the named interview feedback specifically target?**
A: Beat 4 — what it cost.

**13. Q: What's an ADR?**
A: The four-beat structure, written down and dated — Context / Options Considered / Decision / Consequences.

**14. Q: Why can't a values-only monotonic stack solve LC 739 (Daily Temperatures)?**
A: The required output is a distance between indices; a stack holding only values can't recover which index a popped value came from.
