---
title: "failure-modes.md Deliverable"
week: 4
last_reviewed: 2026-07-29
---

# `failure-modes.md` Deliverable

**For one system you have worked on, enumerate every dependency, its timeout, its retry policy, and what happens when it fails.** Most engineers discover during this exercise that several dependencies have no timeout configured at all — that discovery is itself worth writing down, and is often the seed of a strong incident story.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# Failure Modes — [System Name]

| Dependency | Timeout configured? | Retry policy | What happens when it fails |
|---|---|---|---|
| [Database] | [value or "none found"] | [policy or "none"] | [specific, concrete consequence] |
| [Cache] | | | |
| [External API #1] | | | |
| [Message queue] | | | |
| ... | | | |

## Findings
[What surprised you. Which dependency had no timeout. Which retry policy,
on inspection, has no backoff and could amplify an outage per
02-distributed-failure-modes.md §3.]

## Recommended fixes, prioritized
1. [Highest-risk gap first]
2. ...
```

## 2. Worked example

```markdown
# Failure Modes — Order Processing Service (illustrative)

| Dependency | Timeout configured? | Retry policy | What happens when it fails |
|---|---|---|---|
| Primary database (PostgreSQL) | 5s statement timeout | None (fails fast) | Request fails immediately; order not created; client sees a 503 |
| Payment provider API | **None found** | 3 retries, no backoff, no idempotency key | A slow payment provider holds a connection-pool slot for however long the call takes (no client-side timeout to bound it); a genuine outage triggers all 3 retries immediately, tripling load on an already-degraded provider, and a retried charge with no idempotency key risks a **double charge** |
| Inventory service (internal HTTP) | 2s | 2 retries, exponential backoff (100ms base) | Well-configured; matches the fix pattern from `02-distributed-failure-modes.md` §3 |
| Notification queue (async) | N/A (fire-and-forget) | At-least-once delivery, consumer-side dedup | Notification may be delayed under queue backpressure; consumer-side dedup means a delayed retry is safe |

## Findings

The payment provider dependency is the highest-risk finding in this
exercise: no client-side timeout means a slow provider can hold
resources indefinitely, and the retry policy (no backoff, no
idempotency key) is exactly the amplification pattern reproduced in
`02-distributed-failure-modes.md` §3 -- except here the retried
operation is a **financial charge**, making a spurious retry far more
consequential than a redundant read.

## Recommended fixes, prioritized

1. Add a client-side timeout to the payment provider call (bounding the
   worst case even if the provider itself doesn't degrade gracefully).
2. Add an idempotency key to every payment request, keyed on the order
   ID, so a retry -- automatic or manual -- cannot double-charge.
3. Add exponential backoff to the payment retry policy, matching the
   inventory service's already-correct pattern.
```

**Why this is a complete deliverable:** it names a specific, plausible highest-risk gap (not a generic "we should have more timeouts") and connects it explicitly back to this week's technical content (the amplification mechanism from §3, the idempotency-key fix from the same chapter) rather than treating the deliverable and the chapter as unrelated.

## 3. Exit check

Your own `failure-modes.md`, produced against a real system, must name at least one dependency with **no** timeout or **no** backoff — if every dependency in the real system you pick turns out to already be perfectly configured, pick a different system, since finding at least one gap is close to universal in practice and its absence would be worth independently verifying rather than assuming.
