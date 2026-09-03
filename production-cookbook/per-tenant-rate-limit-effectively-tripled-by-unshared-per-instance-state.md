---
title: "Per-Tenant Rate Limit Effectively Tripled by Unshared Per-Instance Counter State"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/system-design/rate-limiting-and-throttling-algorithms.md
  - ../handbook/system-design/idempotency.md
source: handbook/system-design/rate-limiting-and-throttling-algorithms.md#production-scenarios
---

# Per-Tenant Rate Limit Effectively Tripled by Unshared Per-Instance Counter State

## Context

A service enforces a documented "1000 requests/minute" per-tenant API limit, running three instances behind a load balancer. Each instance runs its own in-memory `FixedWindowCounter`, with no shared state between instances.

## Symptoms

A customer complains they were rate limited far less aggressively than the documented limit — closer to 2800 requests/minute during a burst.

## Impact

The documented per-tenant rate limit was not actually being enforced as stated, undermining both the guarantee given to the customer and any capacity planning that assumed the limit was a hard ceiling.

## Initial Hypotheses

A bug in the limiter's math.

## Evidence

Each of the three service instances ran its own in-memory `FixedWindowCounter`, with no shared state — every instance independently enforced 1000/minute, so the *effective* limit for any tenant whose requests were load-balanced across all three was up to 3000/minute, and the customer's real, bursty traffic pattern happened to land closer to 2800 than the theoretical 3000 ceiling.

## Investigation Timeline

1. Customer reports being rate limited less aggressively than the documented 1000 requests/minute limit, observing behavior closer to 2800/minute during a burst.
2. Limiter's algorithm (fixed-window counting) reviewed for a mathematical bug — initial hypothesis.
3. Each of the three service instances' counter state inspected, revealing each runs its own independent in-memory `FixedWindowCounter` with no shared state across instances.
4. Effective limit recalculated given three independently-enforcing instances: up to 3000/minute for a tenant whose traffic is load-balanced across all three.
5. Customer's real traffic pattern (bursty, closer to 2800 than 3000) confirmed as consistent with hitting close to, but not exactly, the theoretical per-instance-multiplied ceiling.

## Root Cause

The limiter's algorithm was correct; its *placement* was wrong — per-instance local state cannot enforce a global limit once there is more than one instance.

## Immediate Mitigation

Temporarily route each tenant's traffic to a single sticky instance via the load balancer's session affinity.

## Permanent Fix

Move the counter to a shared, single source of truth — Redis, using `INCR` with a `PEXPIRE` set only on the first increment of each window (an atomic `EVAL` Lua script to avoid a race between the `INCR` and the conditional `PEXPIRE`), so all three instances check the same count.

## Alternatives Considered

Sticky sessions via load-balancer affinity — used only as an immediate, temporary mitigation, not adopted permanently, since it reintroduces a different problem (uneven load distribution, and a single point of enforcement per tenant that breaks if that instance is unhealthy).

## Trade-offs

Every rate-limit check now costs a network round-trip to Redis instead of an in-memory comparison — accepted because the check is on the request's fast path already talking to other shared infrastructure, and because Redis's own latency (sub-millisecond, same availability zone) is negligible next to the request's total latency budget.

## Prevention

Any rate limiter design review now explicitly asks "is this limiter's state local to one process, and does that process run as more than one instance?" as a first question.

## Monitoring and Alerts

- Alert when a tenant's aggregate request rate across all instances exceeds the documented limit by any meaningful margin — this incident's own symptom (2800 against a documented 1000) would have paged immediately on such an alert instead of surfacing only via a customer complaint.
- Track the shared Redis-based counter's own latency and error rate as a first-class dependency metric, since the permanent fix moved a fast-path check onto network infrastructure — a Redis slowdown or outage now directly affects every rate-limit check across all instances, which is a new failure mode the in-memory version didn't have.
- Add an automated architecture check (or a design-review checklist item, per the Prevention step) flagging any newly introduced in-memory counter, cache, or rate-limiting state in a horizontally-scaled service, catching the "local state, multi-instance deployment" pattern before it reaches production rather than after a customer notices.

## Interview Story

This maps directly to the single most common gap in an otherwise-correct rate-limiting interview answer — describing an algorithm perfectly while never mentioning that multi-instance deployment needs shared state. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a documented "1000 requests/minute" per-tenant limit was actually being enforced closer to 2800/minute for tenants whose traffic was load-balanced across three instances.
- **Task:** find the cause, given the limiter's algorithm itself appeared correct.
- **Action:** confirmed each of the three instances ran its own independent in-memory counter with no shared state, meaning the effective limit multiplied by instance count rather than staying fixed.
- **Result:** moved the counter to a shared Redis-based source of truth using an atomic `INCR`/`PEXPIRE` script, accepting a small added network round-trip per check in exchange for the limit actually meaning what it says.

## Staff-Level Discussion

This is the interview version of a mistake that is genuinely easy to make in production: the algorithm (fixed window, token bucket, sliding window — any of them) is usually explained and implemented correctly, and the placement decision — where does the counter's state actually live — is treated as an implementation detail rather than a design decision with its own correctness implications. That gap matters more, not less, as a service scales horizontally, since the effective limit multiplies by instance count silently, with no error or exception marking the failure — exactly the profile (correct-looking code, wrong emergent behavior) that a Staff engineer should learn to suspect whenever a system's observed behavior diverges from its stated guarantee. The Redis-based fix's trade-off — a network round-trip on every rate-limit check, and a new dependency whose own availability now gates every request — is a cost worth stating explicitly in any design review, since it converts a previously-local concern into a shared-infrastructure one, with the corresponding monitoring and failure-mode obligations that shift entails.

## Related Handbook Chapters

- [Rate Limiting and Throttling Algorithms](../handbook/system-design/rate-limiting-and-throttling-algorithms.md) — canonical algorithm comparison and the shared-state requirement this incident reproduces.
- [Idempotency](../handbook/system-design/idempotency.md) — the related atomic-operation discipline (`INCR`/`PEXPIRE` via Lua script) this fix relies on to avoid a race condition.
