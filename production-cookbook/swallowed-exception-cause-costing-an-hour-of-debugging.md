---
title: "Swallowed Exception Cause Costing an Hour of Debugging"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/java-core/exception-design-and-hierarchy-strategy.md
source: handbook/java-core/exception-design-and-hierarchy-strategy.md#production-scenarios
---

# Swallowed Exception Cause Costing an Hour of Debugging

## Context

The code that throws `OrderProcessingException` uses the single-argument constructor — `new OrderProcessingException("could not process order")` — inside a catch block for the actual low-level `IOException`.

## Symptoms

A production alert fires for `OrderProcessingException: could not process order` with no further detail. The on-call engineer has no `getCause()` to inspect, no chained stack trace pointing at a specific downstream failure, and has to reproduce the issue manually by re-running the failing order through a staging environment to discover the actual root cause — a disk-full condition on a specific volume.

## Impact

A production incident that could have been diagnosed in seconds from a chained stack trace instead takes over an hour of manual reproduction, extending the incident's resolution time and the affected customers' downtime.

## Initial Hypotheses

- The alerting system itself is stripping detail — checked and ruled out; the alert payload includes the full exception message and stack trace as captured, there's simply nothing more captured to include.
- The failure is non-deterministic and hard to reproduce — checked and ruled out; it reproduces reliably once the actual cause is identified.
- The exception-wrapping code discards the original cause — correct.

## Evidence

The code that throws `OrderProcessingException` uses the single-argument message-only constructor inside a catch block for the actual low-level `IOException`, matching the `getCause() == null` failure signature directly.

## Investigation Timeline

1. **Alert fired with a generic message** and no chained cause available to inspect.
2. **Alerting-system and non-determinism hypotheses ruled out**, since the full captured payload was already present in the alert and the issue reproduces reliably once found.
3. **Manual reproduction begun** in staging, testing candidate causes one at a time.
4. **Root cause found empirically**: a disk-full condition on a specific volume, only after the fact traced back to the exact `catch` block that discarded it originally.

## Root Cause

The wrapping exception was designed with only a message constructor, discarding the caught `IOException` — and its stack trace pointing at the specific disk volume — at the exact moment it was wrapped.

## Immediate Mitigation

Manually reproduce the failure in staging by testing likely causes one at a time until the disk-full condition is found.

## Permanent Fix

Add a `Throwable`-accepting constructor to every custom exception type in the codebase, and update every catch-and-wrap site to pass the caught exception as the cause. Add a static analysis rule flagging any exception construction inside a `catch` block that doesn't reference the caught variable.

## Alternatives Considered

Logging the original exception separately before throwing the wrapped one, instead of chaining it. Rejected as strictly worse than chaining — it requires correlating two separate log entries by timestamp or thread instead of getting the full chain from one exception object.

## Trade-offs

None — chaining the cause has no real cost; it was simply omitted in the original exception class design.

## Prevention

Require every custom exception class to expose a cause-accepting constructor as a matter of code-review policy, and treat any `catch` block that constructs a new exception without referencing the caught variable as a review flag.

## Monitoring and Alerts

- A static analysis rule (the Permanent Fix above) run in CI, catching any `catch`-and-wrap site that doesn't pass the caught exception forward — this converts an entire class of future debugging-time cost into a build-time fix, before the exception type ever ships.
- Alert payload completeness as its own tracked property: an alert firing with `getCause() == null` on a wrapped exception type is itself a signal worth flagging, independent of the specific incident it's attached to.

## Interview Story

This maps directly to a "why did a simple production error take an hour to diagnose" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a production alert with a generic message and no diagnostic detail took over an hour to resolve via manual reproduction.
- **Task:** explain why standard diagnostic tooling (stack trace, cause chain) provided nothing useful.
- **Action:** rule out alerting-system truncation and non-determinism; inspect the exception-wrapping code directly; identify the single-argument constructor discarding the original `IOException` at the exact point of wrapping.
- **Result:** added cause-accepting constructors to every custom exception type and a static analysis rule preventing the same omission in any future exception class.

## Staff-Level Discussion

This is a small, mechanical defect with an outsized incident-response cost, which is exactly the pattern worth naming explicitly: the fix (pass the caught exception to the wrapping constructor) costs nothing and has no trade-off, yet its absence multiplies the cost of every future incident that happens to route through that exception type. The organizational lesson isn't "remember to chain exceptions" as a personal discipline — it's that exception-design defects are latent until an incident occurs, so the review and static-analysis investment should happen once, at exception-class design time, rather than being rediscovered incident by incident. A Staff engineer reviewing a new custom exception type should treat "does this have a cause-accepting constructor, and is it always used" as a non-negotiable checklist item, not a style preference.

## Related Handbook Chapters

- [Exception Design and Hierarchy Strategy](../handbook/java-core/exception-design-and-hierarchy-strategy.md) — canonical exception-chaining mechanics used here.
