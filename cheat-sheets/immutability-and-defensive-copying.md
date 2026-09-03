---
title: "Cheat Sheet: Immutability and Defensive Copying"
slug: immutability-and-defensive-copying
document_type: cheat-sheet
domain: java-core
topic_id: T-103
canonical: ../handbook/java-core/immutability-and-defensive-copying.md
last_updated: 2026-08-05
---

# Immutability and Defensive Copying

**Canonical chapter:** [`syllabus/02-java/language-core/immutability-and-defensive-copying.md`](../syllabus/02-java/language-core/immutability-and-defensive-copying.md)

## Core Mental Model

A class is only as immutable as its most permissive point of entry or exit for mutable state. `final` fields prevent *reassignment* of the field itself, but say nothing about whether the object the field points to can still be mutated by someone else who holds a reference to it — either handed to the constructor and kept, or handed back out by a getter. True immutability requires defensive copying at both boundaries.

## Essential Definitions

- **Immutable object** — its observable state cannot change after construction.
- **Defensive copying** — copying a mutable object rather than storing/returning a direct reference, at the constructor (way in) and getter (way out) boundaries.
- **Leak on the way in** — a constructor storing the caller's mutable reference directly (`this.when = when;`).
- **Leak on the way out** — a getter returning a live internal reference (`return attendees;`).
- **`List.copyOf()`** — stronger than a plain defensive copy: produces an unmodifiable view that throws `UnsupportedOperationException` on mutation, rather than merely being independent of the original.

## Decision Table

| Question | Answer |
|---|---|
| Constructor accepts a mutable type? | Copy defensively on the way in, unless the type is itself genuinely immutable |
| Getter returns a field of a mutable type? | Return a defensive copy or immutable view, never the live internal reference |
| Object shared across multiple callers/subsystems? | Prefer an immutable view over a plain copy — mutation attempts fail loudly |
| Field's type can be changed to something genuinely immutable? | Prefer that (`java.time` over `Date`) — eliminates the need for copying entirely |

**Trade-offs:** storing a caller's reference directly costs nothing but isn't actually immutable; a plain defensive copy is independent but the copy itself can still be mutated if handed out; `List.copyOf()` is independent AND rejects mutation outright, at a small one-time copy cost.

## Key Numbers (real, executed — `MutableLeakDemo.java`)

```
Leak #1 (constructor stores live reference):
  Event date right after construction: Tue Nov 14 16:13:20 CST 2023
  After caller mutates the ORIGINAL Date: Wed Dec 31 18:00:00 CST 1969  <- CHANGED

Leak #2 (getter returns live reference):
  attendees before: [carol]
  after getAttendees().add("mallory") from OUTSIDE: [carol, mallory]  <- internal state mutated externally
```

```
Fixed version (defensive copy + List.copyOf()):
  Event date after caller mutates original: Tue Nov 14 16:13:20 CST 2023  <- UNCHANGED
  getAttendees().add("mallory") threw UnsupportedOperationException  <- rejected outright
```

## Common Pitfalls

- Believing `final` fields alone make a class immutable, without checking whether the referenced objects are themselves mutable.
- Copying on construction but not on the getter (or vice versa) — both boundaries need protection.
- Treating a plain mutable-copy getter as equivalent to an immutable view — it's safer than a live reference but still allows unnoticed local mutation.

## Interview Answer Skeleton

**30-sec:** `final` fields prevent reassignment, not mutation of the referenced object — a class can leak mutability through its constructor (storing a caller's reference) or its getters (returning a live reference), measured directly in both cases. Defensive copying on both boundaries, or an immutable view like `List.copyOf()`, closes both leaks.

**2-min:** Add why immutability matters (shared mutable state is a major bug source; immutable objects need zero synchronization) + the real measured evidence (mutating the original `Date` after construction changed the "immutable" object's state; `List.copyOf()` throwing on mutation) + the trade-off (defensive copying is a real, bounded O(n) cost per call, not free).

**Whiteboard:** A constructor receiving a mutable argument, branching to "stores reference directly → LEAK" vs. "copies defensively → SAFE"; a getter returning a field, branching to "live reference → LEAK" vs. "defensive/immutable copy → SAFE." Circle both LEAK branches: "final fields alone protect against neither of these."

**Staff-level framing:** immutability pays for itself directly in concurrency terms — zero synchronization needed to share safely across threads, because there's no mutation to race on, connecting directly to the Java Memory Model's safe-publication guarantee for final fields. Audit every mutable-typed field as a potential leak until both its constructor and getters are verified.

## Production Warning Signs

- A shared `Config` object behaves as if a feature flag changed mid-request, with no intentional toggle — check whether a getter returns a live `Map`/`List` reference and some subsystem is mutating it locally, silently corrupting state for every other holder of the same instance.
- Intermittent, hard-to-reproduce behavior differences across subsystems all reading "the same" shared object — a classic signature of Leak #2 (getter returning a live reference) rather than a race condition.
- **Prevention:** expose collection-typed fields on any shared, read-only object via immutable views (`List.copyOf()`, `Map.copyOf()`) by default, not by "don't mutate this" convention — a convention is silently violated exactly when someone doesn't realize the object is shared.

## Related

- `syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md`
- `syllabus/02-java/concurrency/java-memory-model-and-volatile.md`
