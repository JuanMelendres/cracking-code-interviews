---
title: "Cheat Sheet: VarHandles, Unsafe, and Their Replacement"
slug: varhandles-and-unsafe
document_type: cheat-sheet
domain: concurrency
topic_id: T-415
canonical: ../handbook/concurrency/varhandles-and-unsafe.md
last_updated: 2026-09-01
---

# VarHandles, Unsafe, and Their Replacement

**Canonical chapter:** [`handbook/concurrency/varhandles-and-unsafe.md`](../handbook/concurrency/varhandles-and-unsafe.md)

*Expert tier, rare interview frequency — this cheat sheet is scoped shorter to match the register's own recognition-level depth target.*

## Core Mental Model

Before `VarHandle`, Java's atomic-access story was binary: a plain field (no cross-thread visibility) or `volatile`/`AtomicXxx` (full, bidirectional happens-before, paid on every access). Real high-performance code often needs something in between — "visible eventually, but not a full memory fence on every access" — and before `VarHandle`, the only way to get that finer control was `sun.misc.Unsafe`, an internal, unsupported API. `VarHandle` gives the same fine-grained control as named, sanctioned access-mode methods instead of raw memory offsets.

## Essential Definitions

- **`sun.misc.Unsafe`** — internal HotSpot class for raw memory access, CAS, off-heap allocation; never part of the public API but became a de facto standard through necessity.
- **`VarHandle`** (JEP 193, Java 9) — the public, safe, checked replacement, exposing four access-mode families with an explicit, named ordering guarantee each.
- **Four access-mode families** (weakest to strongest): plain (no guarantee) → opaque (no self-reorder, no happens-before with anything else) → acquire/release (one-directional happens-before) → volatile (full, bidirectional happens-before).
- **Ordering comes from the method called**, not the field's declared modifier — proven directly on a genuinely plain field.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Atomic access to millions of small values at real scale? | `VarHandle` over a plain field — real, measured memory savings over `AtomicXxx` |
| Ordering requirement unclear or not performance-critical? | `AtomicXxx` or `volatile` — the safer, stronger default |
| Existing code calling `sun.misc.Unsafe` directly? | Migrate to `VarHandle` — the sanctioned, public replacement |
| Team not deeply familiar with the JMM's access-mode distinctions? | Default to `volatile`-strength access |

**Trade-offs:**

| Mechanism | Public/supported? | Per-call ordering control? | Wrapper object needed? |
|---|---|---|---|
| `sun.misc.Unsafe` | No | Yes (unchecked) | No |
| `AtomicXxx` | Yes | No — always full volatile strength | Yes |
| `VarHandle` | Yes | Yes (four real access-mode families) | No |

## Key Numbers (real, executed JDK 21 output)

`VarHandle` matching `AtomicInteger`'s exact guarantee, no wrapper object, under real contention:

```
8 real threads, 100000 increments each -- expected final count: 800000
Real AtomicInteger result: 800000 (correct)
Real VarHandle result:     800000 (correct)
```

Safe-publication proof (200,000 real rounds, zero failures):

```
Real failures across 200000 real publish/observe rounds: 0
```

## Common Pitfalls

- Reaching for `sun.misc.Unsafe` in new code on a modern JDK, when `VarHandle` is the sanctioned, public alternative.
- Choosing a weaker access mode (`plain`/`opaque`) than a use case's real visibility requirement — a genuine correctness bug that may not manifest reliably in casual testing.
- Assuming `VarHandle`'s ordering guarantee comes from a field's `volatile` declaration rather than the specific access-mode method called.
- Claiming to have "proven" a reordering bug from a short demo — reliably provoking the *absence* of ordering is notoriously unreliable to reproduce; prove the guarantee's presence instead.

## Interview Answer Skeleton

**30-sec:** `sun.misc.Unsafe` was internal and unsupported but filled a real capability gap. `VarHandle` (Java 9) is the sanctioned, safe replacement, exposing four real access-mode families — plain, opaque, acquire/release, volatile — letting code choose exactly the ordering strength it needs.

**2-min:** Add the measured `VarHandle`-vs-`AtomicInteger` equivalence (800,000 both) with no wrapper object, and the honest scoping: proving `setRelease`/`getAcquire`'s guarantee (200,000 rounds, zero failures) is the demonstrable side; provoking a reordering bug's absence is not something a short demo can honestly claim.

**Whiteboard:** Four boxes labeled plain, opaque, acquire/release, volatile with an arrow "increasing ordering strength." Below, a writer thread's plain writes flow into a "release" arrow crossing into a reader's "acquire" arrow, with a checkmark on every observed field — label it "guaranteed by specification, not by luck."

**Staff-level framing:** Discuss `VarHandle` adoption as a scale-driven engineering decision (real memory savings from avoiding wrapper objects at millions of instances), not a default replacement for `AtomicXxx` — and demonstrate calibrated honesty about what can and can't be reliably shown live.

## Production Warning Signs

- A metrics library spending measurable heap on `AtomicLong` wrapper overhead at millions of instances — replace with a plain `long` field plus a shared `VarHandle`, same atomicity guarantee, no wrapper object.
- `InaccessibleObjectException` attempting `sun.misc.Unsafe` on a modern JDK without `--add-opens`/`--add-exports` — a deliberate JDK-team decision pushing code onto `VarHandle`.

## Related

- `handbook/concurrency/atomics-cas-and-the-aba-problem.md`
- `handbook/concurrency/java-memory-model-and-volatile.md`
- `handbook/concurrency/foreign-function-and-memory-api.md`
