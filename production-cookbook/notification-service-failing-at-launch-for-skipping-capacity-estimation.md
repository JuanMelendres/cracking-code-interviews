---
title: "Notification Service Failing at Launch for Skipping Capacity Estimation"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/11-system-design/system-design-method-and-estimation.md
source: handbook/system-design/system-design-method-and-estimation.md#production-scenarios
---

# Notification Service Failing at Launch for Skipping Capacity Estimation

## Context

A design document for a new notification service is approved and shipped without an explicit capacity estimation section — the document jumps directly from a one-paragraph problem statement to an architecture diagram with a queue, three consumer services, and a database.

## Symptoms

Within a month of launch, the service falls over during a marketing campaign that triggers notifications to a large fraction of the user base simultaneously. The queue backs up for hours, and notifications arrive so late they're no longer relevant.

## Impact

A user-facing feature effectively failed during the exact event — a promotional campaign — it existed to support.

## Initial Hypotheses

- A bug in the consumer service — checked and ruled out; no error logs, just backlog.
- Insufficient consumer instance count — partially true, but the deeper question is why the count chosen was insufficient.
- The original design never estimated peak load at all — correct.

## Evidence

The design document contains no QPS or volume estimate anywhere; the consumer service's instance count and queue partition count were set to "reasonable-sounding" round numbers with no traceable justification.

## Investigation Timeline

1. **Service fell over during a marketing campaign**, with the queue backing up for hours.
2. **Consumer-bug hypothesis ruled out**, since logs showed backlog accumulation, not errors.
3. **Instance count reviewed**, found insufficient, but the review pushed further to ask why that specific count was chosen.
4. **Original design document reviewed**, finding no capacity estimation section anywhere between the problem statement and the architecture diagram.

## Root Cause

The design skipped the Estimate phase entirely, going straight from a vague problem statement to the architecture. Every subsequent capacity decision — consumer count, queue partitions — was therefore a guess, not a number-justified choice, and the guess happened to be wrong for the actual peak load a campaign produces.

## Immediate Mitigation

Scale consumer instances and queue partitions reactively during the incident, restoring throughput after the fact.

## Permanent Fix

Redo the capacity estimation retroactively — daily active users, expected notification-triggering events, and specifically the peak-to-average ratio for a promotional-campaign event, which can be far higher than typical daily peak — and re-size the architecture against those numbers, documenting the assumptions so future reviewers can challenge and revise them.

## Alternatives Considered

Simply over-provisioning capacity broadly as a hedge. Rejected as expensive and still not actually traceable to a specific, defensible number — the point of estimation is not just "have more capacity" but "know precisely how much capacity this specific event requires."

## Trade-offs

Retroactive estimation costs engineering time that could have been spent on new features. Accepted, since the alternative is repeating the same guess-based sizing on the next capacity decision.

## Prevention

Make an explicit capacity-estimation section, with stated assumptions, a required part of every design document template — not an optional appendix, but a gate before the architecture section can be reviewed.

## Monitoring and Alerts

- A design-document template enforcing the estimation phase as a required, non-skippable section before an architecture diagram can be added, converting this prevention rule into a structural gate rather than a reviewer's discretion.
- Queue depth and consumer lag tracked and alerted well below the point of user-visible delay, giving the team a chance to scale reactively before notifications become stale, even for future events whose peak wasn't perfectly estimated in advance.

## Interview Story

This maps directly to the six-phase method's own Estimate phase, arriving as a real production incident when it's skipped. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a notification service, approved without a capacity-estimation section, failed during the exact marketing campaign event it was built to support.
- **Task:** explain why "reasonable-sounding" capacity numbers weren't reasonable in practice.
- **Action:** rule out a consumer-service bug directly; trace insufficient capacity back to the original design document; find that no estimation phase preceded the architecture decisions at all.
- **Result:** redid the capacity estimation with real, traceable numbers — including the campaign-specific peak-to-average ratio — and made an estimation section a mandatory, gating part of the design-document template going forward.

## Staff-Level Discussion

This incident is the cleanest possible demonstration of why the six-phase method sequences Estimate before Architecture: every capacity-shaped decision downstream of a skipped estimation phase — instance counts, partition counts, timeout values — is necessarily a guess dressed up as a decision, because there's no traceable number to check it against. The failure wasn't really a technical one; the architecture diagram itself (queue, consumers, database) was a reasonable shape for the problem. The actual defect was procedural: the design review process allowed an architecture to be approved with no number anyone could point to and defend. A Staff engineer reviewing any design document should treat the absence of an estimation section as a blocking finding on its own, independent of whether the proposed architecture looks reasonable, because "looks reasonable" and "is sized correctly for actual peak load" are different claims, and only the latter is defensible under real production conditions.

## Related Handbook Chapters

- [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md) — canonical six-phase method and estimation-phase discipline used here.
