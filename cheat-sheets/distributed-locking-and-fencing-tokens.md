---
title: "Cheat Sheet: Distributed Locking and Fencing Tokens"
slug: distributed-locking-and-fencing-tokens
document_type: cheat-sheet
domain: 10-distributed-systems
topic_id: T-2422
canonical: ../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md
last_updated: 2026-09-21
---

# Distributed Locking and Fencing Tokens

**Canonical chapter:** [`syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md`](../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md)

## Core Mental Model

A lease is a claim ticket with an expiry, not a physical key — if the holder pauses longer than the TTL, the lock is reassigned whether or not the holder still believes it holds it. A paused process cannot observe its own pause, so no lock-service improvement closes this gap — only a fencing token, checked by the resource being written to, does.

## Essential Definitions

- **Lease** — a lock grant with a real TTL; expires and becomes reassignable regardless of the holder's state.
- **Fencing token** — a real, monotonically increasing number issued with each lease; the protected resource rejects any write with a token lower than the highest already accepted.
- **The bug** — a lock holder pauses (GC, slow I/O) longer than its lease, resumes unaware it expired, and writes anyway.

## Decision Table

| Question | Answer |
|---|---|
| Does a shorter lease TTL fix the stale-write bug? | No — narrows the window, never closes it |
| Where must the fencing check live? | At the resource being written to, never the lock service |
| Does the lock service correctly refuse concurrent acquires on a valid lease? | Yes — ordinary mutual exclusion works normally; this chapter's focus is the separate expiry case |
| Is "we use a distributed lock" a complete answer? | No — necessary but not sufficient without fencing tokens |

## Common Pitfalls

- Stopping at "use a distributed lock" without the fencing-token follow-up.
- Trying to fix the bug by shortening the lease TTL — narrows, never closes, the window.
- Adding the fencing check to the lock service instead of the actual resource being written to.
- Assuming a "smarter" lock service (faster heartbeats) can solve a problem that's actually about a paused process's inability to observe elapsed time.

## Interview Answer Skeleton

**30-sec:** A lease has a real TTL; if the holder pauses longer than it, the lock is reassigned, but the paused holder can't know that and may write anyway after waking. A fencing token — checked by the resource itself — rejects that stale write regardless of cause.

**2-min:** Add the real evidence: a 300ms lease against a real 500ms pause reproducibly corrupts data without fencing tokens (`Final value: A-value`, the stale write won); with fencing tokens, the identical stale write is explicitly rejected (`token 1 <= last accepted token 2`) and the legitimate write survives.

**Staff-level framing:** "We use a distributed lock" is necessary but not sufficient for any operation with a real, costly duplication risk — fencing-token support belongs in the resource's write path from day one, not as reactive rework after an incident.

## Production Warning Signs

- A job scheduler with a real, correct distributed lock still occasionally runs a job twice — check whether the job's own writes are fencing-token-aware, not whether the lock itself is correct.
- A promoted database replica accepts a write from a client that hadn't yet learned about failover — a real split-brain write, resolved only by fencing at the storage layer.

## Real Measured Numbers

- `LockContentionDemo`: a second acquire while a lease is still valid is refused with a real `IllegalStateException`.
- `UnsafeLockDemo`: 300ms lease, real 500ms pause — `Final UnsafeStorage value: A-value` (real, reproducible corruption).
- `FencedLockDemo`: identical timeline — `Client-A REJECTED: token 1 <= last accepted token 2`; `Final FencedStorage value: B-value` (real fix verified).

## Related

- syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md
- syllabus/10-distributed-systems/distributed-systems-failure-modes.md
