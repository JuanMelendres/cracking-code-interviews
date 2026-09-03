---
title: "Cheat Sheet: Optimistic vs. Pessimistic Locking"
slug: optimistic-vs-pessimistic-locking
document_type: cheat-sheet
domain: databases
topic_id: T-604
canonical: ../handbook/databases/optimistic-vs-pessimistic-locking.md
last_updated: 2026-09-02
---

# Optimistic vs. Pessimistic Locking

**Canonical chapter:** [`syllabus/06-databases/optimistic-vs-pessimistic-locking.md`](../syllabus/06-databases/optimistic-vs-pessimistic-locking.md)

## Core Mental Model

Both strategies answer "what happens when two transactions try to modify the same row at once?" at completely different moments. Optimistic locking lets both proceed as if there were no conflict and only checks at write time — cheap while uncontended, fails loudly exactly when a real conflict occurred. Pessimistic locking prevents the second transaction from even reading the row for update until the first is done — no exception, no lost update, but the second transaction pays in real wall-clock wait time, contended or not.

## Essential Definitions

- **Lost update** — two transactions read the same row, each computes from its own identical read, and whichever commits second silently overwrites the first with no error.
- **Optimistic locking (`@Version`)** — detects a lost-update conflict at write time by comparing a version marker against the version originally read; rejects the write with an exception on mismatch instead of silently overwriting.
- **Pessimistic locking (`LockModeType.PESSIMISTIC_WRITE`/`PESSIMISTIC_READ`)** — acquires a real database row lock (`SELECT ... FOR UPDATE`) at read time, forcing other transactions wanting the same lock to wait.
- **Detects, not prevents** — the register's own named misconception: optimistic locking allows the same stale-read sequence to happen; it only intervenes at commit.
- **`@Version` applies unconditionally** — once present, Hibernate enforces the version check on every UPDATE against that entity; there is no way to write to it "without locking."

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Contention on the row/entity is rare in practice | Optimistic locking |
| Write involves a slow external call that shouldn't hold a DB lock | Optimistic locking (or move the call outside any lock) |
| Caller's retry logic is well-tested and bounded | Optimistic locking |
| Guaranteed no-conflict required, no caller retry acceptable | Pessimistic locking |
| Workload is bursty with occasional extreme contention on a few rows | Optimistic locking (per this chapter's production scenario) |

**Comparison:**

| Dimension | Optimistic | Pessimistic |
|---|---|---|
| When conflict is caught | At commit/flush | Never occurs — prevented at read time |
| Cost, low contention | Near zero | Real lock-acquisition overhead |
| Cost, high contention | Bounded retries | Potentially unbounded wait time |
| Deadlock risk | None | Real, same as any row lock |
| Caller obligation | Must implement retry logic | Must not hold the lock across slow work |

## Key Numbers (real, executed against Hibernate 6.6)

- No locking: Session A and B both compute from a stale $100 read; final balance $150 (expected $200) — a real lost update.
- With `@Version`: Session A commits (balance $150, version 1); Session B's commit throws a real `jakarta.persistence.OptimisticLockException`; final balance $150 intact, B's update rejected not lost.
- Retry loop: attempt 1 fails with a real `OptimisticLockException` against a stale version=0; attempt 2 reloads (version=1) and commits successfully to $200.
- `PESSIMISTIC_WRITE` block: Thread B's real measured wait was ~1520ms against an intentional 1500ms hold by Thread A.

## Common Pitfalls

- Believing optimistic locking prevents conflicts — it detects them after both sides have already proceeded.
- Writing an optimistic retry loop with no bound, risking unbounded retries (effectively a livelock) under sustained contention.
- Holding a pessimistic lock across an external call (payment, email, downstream API) instead of committing quickly and doing slow work outside the transaction.
- Assuming pessimistic locking is the "safer" default without considering its real cost under bursty, high-contention traffic.
- Retrofitting `@Version` onto a heavily-used entity without auditing every existing write path.

## Interview Answer Skeleton

**30-sec:** Optimistic locking detects a lost-update conflict at write time via a version column and fails loudly rather than silently overwriting — it doesn't prevent the conflict. Pessimistic locking prevents it entirely by holding a real row lock, at the cost of making the second transaction wait for however long the first holds it.

**2-min:** Add the real lost-update baseline ($150 instead of $200), the real `OptimisticLockException` and successful retry to $200, and the real measured pessimistic block (~1520ms against a 1500ms hold). Close on contention profile as the deciding factor.

**Whiteboard:** Draw two parallel timelines for A and B. Optimistic: both proceed unchanged until B's commit arrow hits a red X labeled "conflict discovered here, not before." Pessimistic: B's timeline is flat and waiting until A's ends, labeled "B blocked, full duration of A's lock."

**Staff-level framing:** Reason about contention profile as the deciding factor, not habit or "safety" framing. Connect pessimistic locking's real cost to deadlock risk, and discuss the organizational discipline needed to keep locking-strategy decisions explicit and reviewed rather than defaulted per-developer per-entity.

## Production Warning Signs

- A flash-sale or promotional-traffic spike causes p99 latency on an inventory-decrement endpoint to jump from 40ms to seconds, with normal database CPU/I/O — check for `PESSIMISTIC_WRITE` locks held across a slow external call (e.g., payment authorization) under thousands of concurrent requests on the same hot rows.
- Fix: move slow external calls outside the locked transaction; switch hot-row operations to optimistic locking with a bounded retry loop.
- Mixing optimistic and pessimistic writers on the same table inconsistently — a pessimistic lock does not protect against an optimistic writer using a plain `merge()` with no explicit lock mode.

## Related

- `syllabus/06-databases/locks-deadlocks-and-lock-escalation.md`
- `syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md`
- `syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md`
- `syllabus/06-databases/mvcc-vacuum-and-bloat.md`
