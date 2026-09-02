---
title: "Cheat Sheet: Atomics, CAS, and the ABA Problem"
slug: atomics-cas-and-the-aba-problem
document_type: cheat-sheet
domain: concurrency
topic_id: T-405
canonical: ../handbook/concurrency/atomics-cas-and-the-aba-problem.md
last_updated: 2026-09-02
---

# Atomics, CAS, and the ABA Problem

**Canonical chapter:** [`handbook/concurrency/atomics-cas-and-the-aba-problem.md`](../handbook/concurrency/atomics-cas-and-the-aba-problem.md)

## Core Mental Model

Compare-and-swap answers exactly one question — "is the value still what I last saw?" — and nothing about what may have happened in between. A CAS that succeeds tells you current equality, not "unchanged since I looked."

## Essential Definitions

- **CAS (compare-and-swap)** — a hardware-atomic instruction (`cmpxchg`/`ldrex`+`strex`) that reads, compares to expected, and writes only if they match, all indivisibly.
- **ABA problem** — a value changes A → B → A between a thread's read and its CAS; the CAS succeeds because A really does equal A, even though the world changed in between.
- **`AtomicStampedReference<T>`** — pairs the reference with an integer stamp incremented on every mutation; CAS checks both, so a round-trip back to an identity-equal state still fails the stamp check.

## Decision Table

| Question | Answer |
|---|---|
| Value's "history" doesn't matter beyond its current value (a plain counter)? | Plain CAS (`AtomicInteger`/`AtomicLong`) is sufficient |
| Can the exact same object reference be removed and later reintroduced (pools, free-lists)? | Plain `AtomicReference` is ABA-vulnerable — use `AtomicStampedReference` |
| High-contention, short critical sections? | CAS retry loops tend to win on throughput — but measure |
| Would `synchronized` make correctness simpler with acceptable performance? | Prefer it unless measured throughput actually requires lock-free code |

## Key Numbers

- 8 threads × 500,000 increments each: `AtomicInteger` CAS loop 70ms vs `synchronized` 154ms — measured ~2x faster, both correct (zero lost updates).
- ABA reproduced deterministically: a stale CAS succeeds (`top == A` after a pop-pop-push-back cycle) and corrupts the stack; the identical interleaving with `AtomicStampedReference` correctly fails because the stamp moved from 3 to 6.

## Common Pitfalls

- Assuming `compareAndSet` succeeding means "nothing changed" rather than "currently equal to expected."
- Building a lock-free structure with object/node reuse (pools, free-lists) on plain `AtomicReference` without considering ABA.
- Assuming CAS is always faster than `synchronized` without measuring — correct only under the right contention profile.
- Forgetting to bump the stamp on every mutation path when using `AtomicStampedReference`.

## Interview Answer Skeleton

**30-sec:** CAS atomically checks "is the value still what I expected?" — the basis of every lock-free structure. ABA: a value changes A → B → A between read and CAS; the CAS succeeds incorrectly. Fixed by `AtomicStampedReference` — pairing the reference with a monotonic stamp so a stale CAS is correctly rejected even when the reference matches.

**2-min:** Add the real, deterministic Treiber-stack reproduction: Thread 1 reads `top=A, next=B`, is preempted; Thread 2 pops A, pops B, pushes A back; Thread 1's stale CAS succeeds and resurrects the already-removed B — real corruption, fixed identically by `AtomicStampedReference` rejecting the same CAS via the stamp.

**Whiteboard:** Thread 1 reads top=A, is preempted; Thread 2 pops A, pops B, pushes A back; Thread 1's CAS runs against the mutated stack. Annotate: plain `AtomicReference` succeeds (corrupted) vs `AtomicStampedReference` fails (correct).

**Staff-level framing:** ABA is one instance of a broader principle: any optimistic-concurrency scheme that checks "does current state match what I last observed?" is vulnerable unless "match" rules out a legitimate round-trip — the identical pattern shows up in database version columns, distributed conditional writes (etcd/ZooKeeper/DynamoDB), and cache invalidation.

## Production Warning Signs

- A lock-free object pool intermittently hands the same pooled object to two callers — a real, rare ABA vulnerability in the free-list's pop/push CAS. Fix: `AtomicStampedReference` with the stamp bumped on every acquire/release.

## Related

- `handbook/concurrency/completablefuture-and-async-composition.md`
- `handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md`
