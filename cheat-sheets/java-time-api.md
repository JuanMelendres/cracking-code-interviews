---
title: "Cheat Sheet: java.time API"
slug: java-time-api
document_type: cheat-sheet
domain: 02-java/language-core
topic_id: T-2400
canonical: ../syllabus/02-java/language-core/java-time-api.md
last_updated: 2026-09-11
---

# java.time API

**Canonical chapter:** [`syllabus/02-java/language-core/java-time-api.md`](../syllabus/02-java/language-core/java-time-api.md)

## Core Mental Model

Ask one question per value: does it need a time zone to be unambiguous, and does it need to survive being shared across threads? `LocalDate`/`LocalDateTime` are zone-naive but immutable/thread-safe. `ZonedDateTime`/`Instant` are both zone-aware and thread-safe. Legacy `Date`/`Calendar` are mutable — the root of nearly every legacy date bug.

## Essential Definitions

- **`Instant`** — a machine timestamp, a point on the UTC timeline.
- **`ZonedDateTime`** — a human-facing scheduled event, zone-aware.
- **`Period`** — calendar-based elapsed time (days/months/years).
- **`Duration`** — time-based elapsed time (seconds/nanos) — not interchangeable with `Period`.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| `date.plusDays(7)` appears to do nothing | Return value discarded — types are immutable | Reassign: `date = date.plusDays(7)` |
| Recurring/billing date off by exactly one hour on certain days | `Duration.ofDays(n)` used for a calendar concept | Use `Period.ofDays(n)`/`Period.ofMonths(n)` |
| Intermittent wrong parsed dates under load | Shared `SimpleDateFormat` accessed concurrently | Replace with a shared `DateTimeFormatter` (thread-safe) |
| Date comparison wrong across time zones | `LocalDateTime` used where zone-awareness was needed | Use `ZonedDateTime` or `Instant` |

## Common Pitfalls

- Discarding the return value of an "modifying" method — every `java.time` type is immutable.
- Using `Duration` for calendar concepts (billing cycles, recurring dates) instead of `Period`.
- Any new use of legacy `Date`/`Calendar`/`SimpleDateFormat` — not just style, a real, measured thread-safety bug source.

## Interview Answer Skeleton

**30-sec:** `java.time` (Java 8+) types are immutable and thread-safe; choose `Instant` for machine timestamps, `ZonedDateTime` for scheduled human events, `LocalDate`/`LocalDateTime` only for genuinely zone-independent values.

**2-min:** Add: `Period` (calendar-based) and `Duration` (time-based) diverge by a full hour on a DST transition day — real, measured evidence, not a theoretical distinction. `SimpleDateFormat` shows ~70% corruption under concurrent load in a real reproduced demo; `DateTimeFormatter` is the safe, immutable replacement.

**Staff-level framing:** Legacy date/time types shouldn't appear in new code at all — this isn't a style preference, it's measured, reproducible thread-safety risk.

## Related

- syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md
