---
title: "Flashcards: Optimistic vs. Pessimistic Locking"
slug: optimistic-vs-pessimistic-locking
document_type: flashcard-deck
domain: databases
topic_id: T-604
canonical: ../handbook/databases/optimistic-vs-pessimistic-locking.md
last_updated: 2026-09-02
---

# Flashcards: Optimistic vs. Pessimistic Locking

**Canonical chapter:** [`syllabus/06-databases/optimistic-vs-pessimistic-locking.md`](../syllabus/06-databases/optimistic-vs-pessimistic-locking.md)

## Card: Detects vs. prevents

**Prompt:**
Does optimistic locking prevent a conflict, or detect one?

**Answer:**
Detects. Both transactions are allowed to read and compute concurrently; only the
second commit is rejected, via a real version mismatch, once the conflict has already
occurred.

**Why it matters:**
This is the register's own named misconception — the fastest way to lose credibility
on this topic in an interview.

**Common trap:**
Describing optimistic locking as if it blocks the second reader, which is
pessimistic locking's behavior, not optimistic's.

**Related:**
[handbook/databases/optimistic-vs-pessimistic-locking.md](../syllabus/06-databases/optimistic-vs-pessimistic-locking.md)

## Card: The real cost of pessimistic locking

**Prompt:**
What determines how expensive a pessimistic lock actually is in production?

**Answer:**
The lock's held duration multiplied by how many concurrent requests are waiting for
it — not whether a conflict is "likely." This chapter measured a single waiter's real
cost at ~1520ms against a 1500ms hold; holding a lock across a slow external call
multiplies that cost by every waiter during a real traffic spike.

**Why it matters:**
It reframes "pessimistic is safer" into "pessimistic has a real, measurable,
sometimes catastrophic cost" — the exact lesson of this chapter's production
scenario.

**Common trap:**
Choosing pessimistic locking by default as the "safe" option without considering
held-duration cost under real contention.

**Related:**
[handbook/databases/optimistic-vs-pessimistic-locking.md](../syllabus/06-databases/optimistic-vs-pessimistic-locking.md), [handbook/databases/locks-deadlocks-and-lock-escalation.md](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md)

## Card: Why can't a @Version entity skip locking?

**Prompt:**
Can you write to a `@Version`-annotated entity without triggering the optimistic
check?

**Answer:**
No — Hibernate enforces the version check unconditionally on every UPDATE against a
versioned entity, a real finding from building this chapter's own demos. There's no
way to temporarily disable it; a genuinely unversioned entity is required to
reproduce a true "no locking at all" baseline.

**Why it matters:**
Retrofitting `@Version` onto an existing, heavily-used entity means every existing
write path is now subject to the check, which can surface previously-invisible
concurrent-write bugs.

**Common trap:**
Assuming `@Version` only applies where you explicitly check for conflicts.

**Related:**
[handbook/databases/optimistic-vs-pessimistic-locking.md](../syllabus/06-databases/optimistic-vs-pessimistic-locking.md)
