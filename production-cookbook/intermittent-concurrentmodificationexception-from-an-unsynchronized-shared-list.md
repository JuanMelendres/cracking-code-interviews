---
title: "Intermittent ConcurrentModificationException from an Unsynchronized Shared List"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md
source: handbook/collections/fail-fast-vs-weakly-consistent-iterators.md#production-scenarios
---

# Intermittent ConcurrentModificationException from an Unsynchronized Shared List

## Context

A request handler builds a response by iterating a shared, request-scoped `ArrayList` while a background thread occasionally trims stale entries from the same list.

## Symptoms

Under low traffic this never fails; under real production load, requests intermittently fail with `ConcurrentModificationException`, and — separately, less frequently noticed — some responses are silently missing an entry with no exception at all.

## Impact

Intermittent 500 errors on some requests, and a rarer, unnoticed data-completeness bug on others, both traced to the same underlying misuse.

## Initial Hypotheses

- A bug in the trimming logic itself — checked, and ruled out: the trim logic correctly removes only genuinely stale entries.
- A race in request-handling code unrelated to the list — checked, and ruled out: the handler's own logic is otherwise correct.
- The list is genuinely shared and mutated across threads without any synchronization or concurrent-safe collection — correct.

## Evidence

The stack trace matches the textbook `ConcurrentModificationException` shape exactly — thrown from `ArrayList$Itr.checkForComodification`. Log correlation shows the missing-entry cases (no exception at all) line up with the timing of the second-to-last-element removal quirk, where the exception is not guaranteed to fire.

## Investigation Timeline

1. **Intermittent 500 errors reported** under production load, all showing `ConcurrentModificationException` from the same request-handling code path.
2. **A separate, quieter data-completeness issue noticed**, where some responses were missing an entry with no exception logged at all — initially treated as unrelated.
3. **Trimming logic reviewed** and confirmed correct — it removes only genuinely stale entries, ruling out a logic bug in what gets removed.
4. **Request-handling logic reviewed** and confirmed otherwise correct — the handler's own logic around the list is not the source of the bug.
5. **Shared-state access pattern confirmed as the root cause** — a plain `ArrayList` is read (iterated) by request-handling threads while a background thread concurrently removes entries from the same instance, with no synchronization coordinating the two; both observed symptoms (the thrown exception and the silently missing entry) are two different possible outcomes of that same underlying race, depending on exactly which element is removed and when.

## Root Cause

A plain `ArrayList` is genuinely shared and mutated across threads with no synchronization at all — fail-fast detection is not a substitute for thread safety, and under real concurrent access, both outcomes (a thrown exception, or a silently missed element) are possible depending on timing.

## Immediate Mitigation

Add a coarse `synchronized` block around both the iteration and the trim operation to eliminate the race immediately.

## Permanent Fix

Replace the shared list with a `CopyOnWriteArrayList` (read-heavy, infrequent trims) so concurrent iteration is genuinely safe by design rather than merely detected-when-lucky, or restructure so the background trimmer publishes a fresh, request-scoped copy rather than mutating shared state directly.

## Alternatives Considered

Wrapping the list with `Collections.synchronizedList()` — rejected as still requiring manual external synchronization around iteration specifically (its own Javadoc states this), which is easy to forget at a new call site; `CopyOnWriteArrayList` makes the safety structural rather than a discipline to remember.

## Trade-offs

`CopyOnWriteArrayList` copies the entire backing array on every write — accepted here specifically because trims are rare and reads (iteration) are frequent, exactly its intended profile; would be the wrong choice for a write-heavy list.

## Prevention

Any collection genuinely shared and mutated across threads should be flagged in review — fail-fast's `ConcurrentModificationException` is a best-effort bug detector for single-threaded misuse, not a concurrency-safety mechanism, and it is explicitly, provably capable of missing the exact scenario it's often mistaken for guarding against.

## Monitoring and Alerts

- Alert on `ConcurrentModificationException` occurrences in production at any non-zero rate for request-handling code — this exception should never occur in correctly-synchronized or correctly-chosen-collection code, so any occurrence is a real signal, not noise to be filtered.
- Because the "silent" half of this bug (a missing entry with no exception) produces no direct signal, add a data-completeness check downstream of any request path known to iterate shared mutable state — for example, asserting an expected minimum entry count where one is knowable — so the quieter symptom is caught by an assertion rather than requiring a user-facing report.
- Add a static-analysis rule or code-review checklist item flagging any field-level collection (`ArrayList`, `HashMap`, `HashSet`) that is both stored as shared/request-scoped-but-cross-thread state and mutated from more than one code path, since this is the general shape of the bug independent of the specific trimming-thread scenario.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a request handler intermittently failed with `ConcurrentModificationException` under production load, and — separately — some responses were silently missing data with no exception at all.
- **Task:** connect two seemingly unrelated symptoms (a thrown exception on some requests, a silent gap on others) to a single root cause.
- **Action:** ruled out a trimming-logic bug and a request-handling logic bug, then identified that a plain `ArrayList` was genuinely shared and concurrently mutated by a background trimming thread with no synchronization, and recognized that fail-fast detection is best-effort — capable of both throwing and silently missing the identical class of concurrent modification depending on timing.
- **Result:** applied a coarse `synchronized` block as an immediate fix, then permanently replaced the shared list with a `CopyOnWriteArrayList` matched to its genuinely read-heavy, write-rare access pattern, closing both symptoms structurally.

## Staff-Level Discussion

The most consequential fact in this incident is not the exception itself but what its *absence* means: `ConcurrentModificationException` is explicitly documented as best-effort, and this codebase's own quieter symptom — a silently missing entry, discovered separately and initially treated as unrelated — is direct proof that the tripwire has real, exploitable gaps rather than being a reliable safety net. This is a common and dangerous misreading of fail-fast iteration: many engineers treat "my loop didn't throw" as evidence the code is thread-safe, when the JDK's own documentation states the opposite. A Staff engineer investigating any concurrency bug touching a fail-fast collection should treat both symptoms — the thrown exception and any adjacent, unexplained data-correctness anomaly — as candidates for the same root cause, rather than debugging them independently. The broader lesson for architecture and code review is that "no exception was thrown" must never be accepted as evidence of correctness for concurrent collection access; the only real fix is either a collection whose concurrency contract is a genuine guarantee (`CopyOnWriteArrayList`, `ConcurrentHashMap`) or externally-enforced synchronization covering every access path, not a hope that the fail-fast check will reliably catch a bug that its own specification says it might not.

## Related Handbook Chapters

- [Fail-Fast vs. Weakly-Consistent Iterators](../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md) — canonical `modCount` mechanism, the best-effort second-to-last-element quirk, and weakly-consistent alternatives this incident's fix relies on.
- [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md) — the permanent-fix collection's own cost model and intended read-heavy, write-rare workload profile.
