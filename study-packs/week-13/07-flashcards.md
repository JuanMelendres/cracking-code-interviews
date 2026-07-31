---
title: "Week 13 Flashcards — Java Core"
week: 13
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-07-30
---

# Week 13 Flashcards — Java Core

15 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** When does a stream pipeline actually execute?
**Answer:** Only when a terminal operation is called — intermediate operations build a lazy pipeline that does nothing on its own.
**Why it matters:** Explains why `peek()`-based debugging can look confusing if you expect output immediately.
**Common trap:** Assuming intermediate operations run as soon as they're called.
**Related:** `01-streams-and-collectors.md`

## Card 2

**Prompt:** Why does `Collectors.toMap()` throw on duplicate keys by default?
**Answer:** The two-argument overload has no way to resolve a collision; the three-argument overload requires an explicit merge function.
**Why it matters:** A common production `IllegalStateException` waiting to happen on real-world data.
**Common trap:** Using the two-argument `toMap()` on data that could plausibly contain duplicate keys.
**Related:** `01-streams-and-collectors.md`

## Card 3

**Prompt:** Does `parallel()` make a stream's writes to shared state thread-safe?
**Answer:** No — a plain `ArrayList` measurably loses updates under `parallel().forEach()`. Use a proper collector instead.
**Why it matters:** A silent, no-exception data-loss bug, not a crash.
**Common trap:** Assuming `parallel()` handles thread-safety of the stream's own side effects.
**Related:** `01-streams-and-collectors.md`

## Card 4

**Prompt:** What can't a plain `equals()`-only override guarantee about a `HashSet`?
**Answer:** That equal objects will be found in the same bucket — without a matching `hashCode()`, they can be routed to different buckets and treated as distinct.
**Why it matters:** The single most common Java equality bug.
**Common trap:** Assuming `equals()` alone controls hash-based collection behavior.
**Related:** `02-equals-hashcode-and-comparable-contracts.md`

## Card 5

**Prompt:** What does `TreeSet` use to decide two elements are "the same"?
**Answer:** `compareTo() == 0` exclusively — it never consults `equals()` at all.
**Why it matters:** A `Comparable` inconsistent with `equals()` can silently drop a genuinely distinct element.
**Common trap:** Assuming `TreeSet` falls back to `equals()` the way `HashSet` does within a bucket.
**Related:** `02-equals-hashcode-and-comparable-contracts.md`

## Card 6

**Prompt:** Why should `equals()`, `hashCode()`, and `compareTo()` all derive from the same fields?
**Answer:** So that "are these the same value" gives the identical answer whether a hash-based or sorted collection is asking.
**Why it matters:** Prevents the exact silent-drop and wrong-bucket bugs this week measures directly.
**Common trap:** Deriving `compareTo()` from only one field "for convenience" on a class also used as a hash/sorted key.
**Related:** `02-equals-hashcode-and-comparable-contracts.md`

## Card 7

**Prompt:** What does type erasure actually remove, and when?
**Answer:** Generic type parameter information, removed after compile time — `List<String>` and `List<Integer>` are the identical class at runtime.
**Why it matters:** Explains why you can't do `instanceof List<String>` or `new T[]`.
**Common trap:** Assuming some generic type information survives to runtime.
**Related:** `03-generics-erasure-and-pecs.md`

## Card 8

**Prompt:** When does a defeated generic (via unchecked cast) actually fail?
**Answer:** At read time — when code relies on the declared element type — not at the point the incompatible value was inserted.
**Why it matters:** Explains why such bugs are often hard to trace back to their real cause.
**Common trap:** Assuming the cast operation itself is where the failure would occur.
**Related:** `03-generics-erasure-and-pecs.md`

## Card 9

**Prompt:** State PECS.
**Answer:** Producer Extends, Consumer Super — a parameter you only read from should be `? extends T`; one you only write to should be `? super T`.
**Why it matters:** Maximizes what callers can pass while keeping the compiler's safety guarantees.
**Common trap:** Reversing extends/super.
**Related:** `03-generics-erasure-and-pecs.md`

## Card 10

**Prompt:** What does chaining the cause when wrapping an exception actually preserve?
**Answer:** The original exception and its full stack trace, retrievable via `getCause()` and shown in `printStackTrace()`'s `Caused by:` section.
**Why it matters:** Without it, `getCause()` returns `null` and the real root cause is gone permanently.
**Common trap:** Constructing a message-only wrapped exception inside a `catch` block.
**Related:** `04-exception-design-and-hierarchy-strategy.md`

## Card 11

**Prompt:** What happens when both a try-with-resources body and `close()` throw?
**Answer:** The body's exception propagates as primary; the `close()` exception is attached via `addSuppressed()` — neither is lost.
**Why it matters:** The specific guarantee that motivated try-with-resources over manual cleanup.
**Common trap:** Assuming a manual `finally` block behaves the same way.
**Related:** `04-exception-design-and-hierarchy-strategy.md`

## Card 12

**Prompt:** Why is a manual `finally`-block `close()` that also throws strictly worse than try-with-resources?
**Answer:** It silently replaces the original exception entirely, with no suppressed-exception mechanism to recover it.
**Why it matters:** The concrete reason try-with-resources exists as a language feature.
**Common trap:** Assuming both approaches are equivalent as long as `close()` is called.
**Related:** `04-exception-design-and-hierarchy-strategy.md`

## Card 13

**Prompt:** Do `final` fields alone make a class immutable?
**Answer:** No — `final` prevents reassigning the field, but the object it references can still be mutated if a live reference leaks through the constructor or a getter.
**Why it matters:** The single most common misconception about Java immutability.
**Common trap:** Treating "all fields final, no setters" as sufficient proof of immutability.
**Related:** `05-immutability-and-defensive-copying.md`

## Card 14

**Prompt:** What are the two places a supposedly-immutable class can leak mutability?
**Answer:** The constructor (storing a caller's mutable reference directly) and a getter (returning a live reference to internal mutable state).
**Why it matters:** Both must be defensively copied for genuine immutability.
**Common trap:** Fixing only one of the two boundaries.
**Related:** `05-immutability-and-defensive-copying.md`

## Card 15

**Prompt:** Why is `List.copyOf()` stronger than copying into a new `ArrayList`?
**Answer:** It rejects any mutation attempt outright (`UnsupportedOperationException`), not just providing independence from the original list.
**Why it matters:** A structural guarantee is stronger than a convention nobody's supposed to violate.
**Common trap:** Treating a plain defensive copy as equivalent to an immutable view.
**Related:** `05-immutability-and-defensive-copying.md`
