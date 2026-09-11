---
title: "Flashcards: java.time API"
slug: java-time-api
document_type: flashcard-deck
domain: 02-java/language-core
topic_id: T-2400
canonical: ../syllabus/02-java/language-core/java-time-api.md
last_updated: 2026-09-11
---

# Flashcards: java.time API

**Canonical chapter:** [`syllabus/02-java/language-core/java-time-api.md`](../syllabus/02-java/language-core/java-time-api.md)

## Card: Immutability silent no-op

**Prompt:**
Why does `date.plusDays(7);` (statement alone, no assignment) appear to do nothing?

**Answer:**
Every `java.time` type is immutable — `plusDays()` returns a new object rather than mutating `date`. Discarding the return value silently no-ops.

**Why it matters:**
A real, common first-encounter bug with immutable value types generally, not just `java.time`.

**Common trap:**
Assuming a "modifying"-sounding method name mutates the receiver.

**Related:**
[java.time API](../syllabus/02-java/language-core/java-time-api.md)

## Card: Period vs. Duration

**Prompt:**
Why can a `Duration.ofDays(1)`-based billing cycle be off by an hour on some days?

**Answer:**
`Duration` is time-based (a fixed number of seconds); `Period` is calendar-based (a calendar day, which can be 23 or 25 hours on a DST transition). Using `Duration` for a calendar concept produces a real, measured hour-long divergence on transition days.

**Why it matters:**
A concrete, measured distinction — not a theoretical footnote.

**Common trap:**
Treating `Period` and `Duration` as interchangeable "elapsed time" types.

**Related:**
[java.time API](../syllabus/02-java/language-core/java-time-api.md)

## Card: SimpleDateFormat's real thread-safety bug

**Prompt:**
Is a shared `SimpleDateFormat` instance safe for concurrent parsing?

**Answer:**
No — a real, reproduced demo shows roughly 70% corruption under concurrent load against a shared instance. `DateTimeFormatter` (the `java.time` replacement) is immutable and safe to share the same way.

**Why it matters:**
A measured, not theoretical, legacy-API danger — directly relevant to any code still using `SimpleDateFormat`.

**Common trap:**
Assuming a formatter, being "read-only" in intent, is automatically thread-safe.

**Related:**
[java.time API](../syllabus/02-java/language-core/java-time-api.md)
