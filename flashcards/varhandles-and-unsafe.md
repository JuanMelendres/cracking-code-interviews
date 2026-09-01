---
title: "Flashcards: VarHandles, Unsafe, and Their Replacement"
slug: varhandles-and-unsafe
document_type: flashcard-deck
domain: concurrency
topic_id: T-415
canonical: ../handbook/concurrency/varhandles-and-unsafe.md
last_updated: 2026-09-01
---

# Flashcards: VarHandles, Unsafe, and Their Replacement

**Canonical chapter:** [`handbook/concurrency/varhandles-and-unsafe.md`](../handbook/concurrency/varhandles-and-unsafe.md)

## Card: What's the real difference between VarHandle and AtomicInteger?

**Prompt:**
What does `VarHandle` provide that `AtomicInteger` doesn't, for the
identical atomic-increment use case?

**Answer:**
The same real atomicity guarantee, but over a plain field with no dedicated
wrapper object — measured directly: a `VarHandle`-backed plain `int` field
and a real `AtomicInteger` both produced the exact correct count (800,000)
under identical real concurrent contention.

**Why it matters:**
At real scale (millions of instances), the wrapper-object overhead
`AtomicInteger` requires becomes a measurable memory cost `VarHandle`
avoids.

**Common trap:**
Assuming `VarHandle` is simply "a faster `AtomicInteger`" rather than the
same guarantee via a different, allocation-free mechanism.

**Related:**
[handbook/concurrency/varhandles-and-unsafe.md](../handbook/concurrency/varhandles-and-unsafe.md), [Atomics, CAS, and the ABA Problem](../handbook/concurrency/atomics-cas-and-the-aba-problem.md)

## Card: Where does a VarHandle's ordering strength actually come from?

**Prompt:**
A field is declared plain (not `volatile`). Can a `VarHandle` still perform
a volatile-strength read on it?

**Answer:**
Yes — measured directly: this chapter's own demo exercises `get`,
`getOpaque`, `getAcquire`, and `getVolatile` all on the same, genuinely
plain field. The ordering strength comes entirely from which access-mode
method is called, not from the field's own declared modifier.

**Why it matters:**
It's the real, defining feature separating `VarHandle` from both `Unsafe`
(no sanctioned ordering choice at all) and `AtomicXxx` (always full
volatile strength).

**Common trap:**
Assuming a field must be declared `volatile` for any VarHandle access mode
to provide ordering guarantees.

**Related:**
[handbook/concurrency/varhandles-and-unsafe.md](../handbook/concurrency/varhandles-and-unsafe.md), [Java Memory Model and volatile](../handbook/concurrency/java-memory-model-and-volatile.md)

## Card: Why doesn't this topic demonstrate a live reordering bug?

**Prompt:**
Why does this chapter prove `setRelease`/`getAcquire`'s guarantee rather
than trying to show a bug from using a weaker access mode incorrectly?

**Answer:**
Proving the guarantee's presence is a real, repeatable test (200,000 real
rounds, zero failures). Reliably provoking a visible bug from its *absence*
on typical hardware (x86/ARM with a modern JIT) is notoriously unreliable —
it can require enormous iteration counts or simply not surface in a short
demo, despite being a real, specification-level correctness gap.

**Why it matters:**
Claiming to have "proven" a reordering bug without a robust, sustained
reproduction would overstate what a short demo can honestly show.

**Common trap:**
Treating a short demo's failure to show a bug as proof the weaker access
mode was actually safe.

**Related:**
[handbook/concurrency/varhandles-and-unsafe.md](../handbook/concurrency/varhandles-and-unsafe.md)
