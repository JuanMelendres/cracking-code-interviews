---
title: "Duration-Based Billing Cycle Double-Charging Across a DST Transition"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/language-core/java-time-api.md
source: syllabus/02-java/language-core/java-time-api.md#production-scenarios
---

# Duration-Based Billing Cycle Double-Charging Across a DST Transition

## Context

A billing job computes each customer's next renewal timestamp as `lastRenewal.plus(Duration.ofDays(30))`.

## Symptoms

On the one day of the year affected by a Daylight Saving Time transition, a small number of customers are charged, then — due to an unrelated retry mechanism reading a renewal window computed slightly differently — charged again.

## Impact

Real customer-facing billing errors, refund requests, and support escalations, affecting only the specific subset of customers whose renewal window happens to span a DST boundary.

## Initial Hypotheses

- A retry-mechanism idempotency bug — checked, the retry logic itself is correct given its inputs.
- A database timestamp precision issue — checked, timestamps are stored with sufficient precision.
- The renewal-window calculation itself produces a boundary condition specifically around DST — correct.

## Evidence

The affected renewal timestamps cluster exactly on the calendar dates of DST transitions, and the actual gap between the computed renewal window's start and end is either 30 days minus one hour or 30 days plus one hour, depending on which transition.

## Investigation Timeline

1. Double-charge complaints cluster on a specific calendar date matching a DST transition.
2. Retry-idempotency and timestamp-precision hypotheses ruled out.
3. Renewal-window arithmetic inspected, revealing the fixed-seconds `Duration.ofDays(30)` calculation.

## Root Cause

`Duration.ofDays(30)` computes a fixed number of real seconds; on a DST transition, that fixed-seconds window lands one hour earlier or later, on the wall clock, than the "30 calendar days later" a billing cycle actually means.

## Immediate Mitigation

Manually reconcile the small number of double-charged customers for the affected billing run.

## Permanent Fix

Replace `lastRenewal.plus(Duration.ofDays(30))` with `lastRenewal.plus(Period.ofDays(30))` (or, more precisely for a monthly billing cycle, `Period.ofMonths(1)`) — a calendar-based renewal cycle should use a calendar-based amount, not a fixed-seconds one.

## Alternatives Considered

Special-casing DST transition dates in the billing logic — rejected, since it's solving the symptom rather than the actual type-category mismatch, and would need to be independently re-derived for every DST rule change across every supported time zone.

## Trade-offs

None meaningful — `Period` is the semantically correct type for a calendar-based billing cycle regardless of DST; there's no scenario where `Duration` was actually the intended semantics here.

## Prevention

Treat "is this a calendar concept (billing cycles, renewal dates, recurring reminders) or a fixed-time concept (cache expiry, rate-limit windows, session timeouts)" as a standing design question whenever a date/time arithmetic operation is introduced.

## Monitoring and Alerts

- A billing-run reconciliation check that flags any renewal window whose actual elapsed time deviates from its nominal calendar length, catching this class of bug even for time zones/transitions not yet anticipated.
- A code-review lint or convention flagging any `Duration.ofDays(N)` used in calendar-adjacent (billing, renewal, scheduling) code.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent billing bug.

- **Situation:** a small subset of customers were double-charged, clustered on a single calendar date each year.
- **Task:** find the root cause of a bug that only reproduces on DST transition dates.
- **Action:** ruled out retry-idempotency and timestamp-precision bugs; traced the renewal-window arithmetic to a fixed-seconds `Duration` calculation instead of a calendar-based `Period`.
- **Result:** fixed by switching to `Period.ofDays(30)`, and reconciled the affected billing run.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "what's the difference between `Period` and `Duration`" — arriving as a real, customer-facing billing bug rather than a definitional question. The organizational risk is that this exact type-category mismatch (calendar concept modeled with a fixed-time type) can recur anywhere date arithmetic touches a business concept, not just billing — a standing review question, not a one-time fix.

## Related Handbook Chapters

- [java.time API](../syllabus/02-java/language-core/java-time-api.md) — the canonical `Period` vs. `Duration` distinction behind this incident.
