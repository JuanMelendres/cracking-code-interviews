---
title: "Lock-Free Object Pool ABA Corruption Handing Out Duplicate Objects"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md
source: handbook/concurrency/atomics-cas-and-the-aba-problem.md#production-scenarios
---

# Lock-Free Object Pool ABA Corruption Handing Out Duplicate Objects

## Context

A custom lock-free object pool, built on `AtomicReference` over a free-list, models the Treiber-stack pattern for acquiring and releasing pooled objects.

## Symptoms

The pool occasionally hands the same pooled object to two different callers simultaneously, causing intermittent, hard-to-reproduce data corruption in whichever downstream code uses the object — visible maybe once per few million pool operations, never reproducible under a debugger.

## Impact

Two unrelated request-handling threads silently share mutable state through the "same" pooled object, corrupting both callers' results unpredictably, with no exception, no stack trace, no clear repro.

## Initial Hypotheses

- A bug in the object's own reset/reuse logic — checked, and ruled out: the reset logic is correct and idempotent.
- A race in the calling code around the pool's public API — checked, and ruled out: callers use the pool correctly, single acquire/release each.
- The pool's own free-list pop/push has an ABA vulnerability — correct, once reproduced deterministically using a fixed, hand-interleaved sequence rather than relying on rare, timing-dependent thread races.

## Evidence

The pool's `acquire()` follows the identical shape as a plain Treiber-stack `pop()`: read the free-list head, capture its successor, CAS. Under sustained load, a pooled object can legitimately be released (pushed back onto the free-list) and re-acquired by a different thread in the narrow window between another thread reading the head and that thread's CAS — reproducing the classic ABA corruption on a real, if rare, production timing.

## Investigation Timeline

1. **Intermittent data-corruption reports arrive**, occurring roughly once per few million pool operations with no consistent repro and no exception anywhere.
2. **Object reset/reuse logic reviewed and tested in isolation**, confirmed correct and idempotent — the bug is not in what happens to an object between uses.
3. **Calling code around the pool's public API reviewed**, confirmed each caller performs exactly one acquire and one release with no double-acquire or missing-release pattern.
4. **Pool's free-list pop/push mechanism examined directly**, revealing it follows the standard Treiber-stack CAS-loop shape: read `head`, capture `head`'s successor, then CAS `head` from the captured value to the successor.
5. **ABA vulnerability reproduced deterministically** (not relying on rare, timing-dependent thread races) using a fixed, hand-interleaved sequence: a thread reads the head and is paused before its CAS; a second thread pops that same object, pops another, and pushes the first object back — restoring the exact same reference the paused thread is still holding — at which point the paused thread's CAS succeeds against a free-list whose actual structure has changed underneath it.

## Root Cause

A textbook ABA problem: the free-list's head CAS only checks reference identity, and a pooled object being released and immediately re-acquired (a completely normal, frequent event in a busy pool) reintroduces the exact "A changed to something else and back to A" pattern that plain `AtomicReference` CAS cannot detect, corrupting the free-list's actual structure while the CAS itself reports success.

## Immediate Mitigation

Temporarily reduce pool concurrency (fewer worker threads sharing the pool) to shrink the race window while a permanent fix is prepared.

## Permanent Fix

Replace the free-list's `AtomicReference<Node>` with an `AtomicStampedReference<Node>`, incrementing the stamp on every acquire and release, so the CAS checks both the reference and a monotonically-advancing stamp rather than reference identity alone.

## Alternatives Considered

Switching to a `synchronized`-guarded free-list — rejected as unnecessarily giving up the pool's lock-free throughput advantage for a problem that has a real, targeted, lock-free fix.

## Trade-offs

`AtomicStampedReference` costs a small amount of extra memory (the boxed `[reference, stamp]` pair on every read) and a marginally more complex CAS call — accepted, since the alternative (an intermittent, production data-corruption bug) is categorically worse.

## Prevention

Any lock-free data structure built on `AtomicReference` where nodes can be legitimately reused (freed and reallocated, pooled, or otherwise re-inserted) should be reviewed specifically for ABA vulnerability — the question to ask is "can the exact same object reference reappear after being removed?", and if yes, plain `AtomicReference` CAS is not sufficient.

## Monitoring and Alerts

- Add an internal consistency check (even a periodic, low-frequency one) that scans the free-list for structural anomalies — for example, a node reachable from more than one traversal path — since ABA corruption manifests as a structurally invalid list even though every individual CAS reports success.
- Instrument the object pool to log or metric-tag every case where a caller observes an already-in-use object being handed out (detectable via an in-use flag checked immediately after acquire), since this is the most direct, unambiguous symptom of exactly this bug class and is otherwise invisible until downstream corruption is noticed.
- Given how rare and non-reproducible this class of bug is under normal testing, add a dedicated, deliberately adversarial concurrency test (using `CountDownLatch`-forced interleaving rather than relying on natural thread timing) for any custom lock-free structure before it ships, specifically targeting ABA scenarios on any node that can be freed and reused.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a custom lock-free object pool intermittently handed the same pooled object to two different callers, corrupting downstream results with no exception and no reliable reproduction — occurring roughly once per few million operations.
- **Task:** diagnose a bug that couldn't be reproduced under a debugger and had already ruled out the two most obvious suspects (object reuse logic and caller misuse).
- **Action:** examined the free-list's CAS-based pop/push mechanism directly, recognized its Treiber-stack shape, and reproduced the corruption deterministically using a fixed, hand-interleaved sequence rather than waiting for a rare natural race — confirming a classic ABA vulnerability.
- **Result:** replaced the plain `AtomicReference` free-list with an `AtomicStampedReference`, closing the vulnerability with a targeted, still-lock-free fix, and added a deliberately adversarial concurrency test to catch the same class of bug in any future lock-free structure before it ships.

## Staff-Level Discussion

ABA bugs are a strong argument for why "no thread ever reported an exception, and stress testing didn't find anything" is not sufficient evidence that a lock-free data structure is correct — the ABA window here required a specific, narrow interleaving that natural thread scheduling might not hit for millions of operations, yet the bug was present the entire time. This is a case where a Staff engineer's contribution isn't primarily writing the fix (`AtomicStampedReference` is a well-known, standard remedy) but recognizing which class of bug this is quickly enough to reproduce it deterministically rather than continuing to chase an elusive, timing-dependent repro — the deterministic, hand-interleaved reproduction technique used here is the actual skill worth generalizing to any future lock-free debugging effort. At the architectural level, this incident is also a reasonable data point for a broader team-level decision: hand-rolled lock-free data structures carry a real, non-obvious correctness tax (ABA is only one of several subtle hazards in this space), and a team should weigh that tax against simply using a well-tested, already-hardened concurrent structure from `java.util.concurrent` wherever the workload doesn't specifically demand a custom lock-free design.

## Related Handbook Chapters

- [Atomics, CAS, and the ABA Problem](../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md) — canonical CAS mechanics, the deterministic ABA reproduction, and the `AtomicStampedReference` fix this incident applies directly.
- [ReentrantLock, ReadWriteLock, and StampedLock](../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md) — related lock-based alternative considered and rejected in favor of preserving lock-free throughput.
