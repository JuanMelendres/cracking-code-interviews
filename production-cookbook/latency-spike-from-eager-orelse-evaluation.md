---
title: "Latency Spike from Eager orElse() Evaluation"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/language-core/optional-and-null-strategy.md
source: handbook/java-core/optional-and-null-strategy.md#production-scenarios
---

# Latency Spike from Eager orElse() Evaluation

## Context

A request handler is refactored from a null-check to an `Optional`-based chain: `cache.get(key).orElse(expensiveDatabaseFallback(key))`.

## Symptoms

After deployment, average request latency increases measurably, even on cache-hit requests where the fallback's result is never used.

## Impact

A measurable latency regression on the majority (cache-hit) code path, introduced by a refactor intended to make the code cleaner, not slower.

## Initial Hypotheses

- Cache hit-rate regression — checked, and ruled out: hit rate is unchanged and high.
- A database connection-pool issue — checked, and ruled out: connection metrics are normal.
- The fallback database call is being made on every request, including cache hits — correct.

## Evidence

Tracing shows `expensiveDatabaseFallback(key)` — a real database query — executing on every single request, including ones where `cache.get(key)` returned a present value that was ultimately used instead.

## Investigation Timeline

1. **Latency regression observed** after the `Optional`-based refactor deployed, with the increase present even on requests known to be cache hits.
2. **Cache hit-rate metrics checked** and confirmed unchanged and high — the regression is not explained by more requests missing the cache.
3. **Connection-pool metrics checked** and confirmed normal — no evidence of pool exhaustion or connection-acquisition delay.
4. **Request tracing added around the fallback call**, revealing `expensiveDatabaseFallback(key)` executing on every request regardless of whether the cached value was present.
5. **`orElse()`'s evaluation semantics confirmed as the mechanism** — its argument is evaluated unconditionally, before `orElse()` is even invoked, regardless of whether the cached value is present, exactly matching the observed unconditional fallback execution.

## Root Cause

`orElse(expensiveDatabaseFallback(key))` evaluates its argument — the real database call — unconditionally, before `orElse()` is even invoked, regardless of whether the cached value is present. This is an ordinary method call, not a conditional branch: `orElse`'s argument is computed first, every time, and simply discarded if the `Optional` was already present.

## Immediate Mitigation

Revert to `orElseGet(() -> expensiveDatabaseFallback(key))`, immediately eliminating the unconditional database call on cache hits.

## Permanent Fix

Add a code-review checklist item (or a static-analysis rule, where available) flagging `orElse()` calls whose argument is a non-trivial method call or computation rather than a constant or already-computed value.

## Alternatives Considered

Reverting the `Optional` refactor entirely back to manual null-checks — unnecessary; the bug is specifically the choice of `orElse` over `orElseGet`, not the use of `Optional` itself.

## Trade-offs

None — `orElseGet()` has no real downside versus `orElse()` for a non-trivial fallback; the only reason to prefer `orElse()` is a genuinely trivial, already-computed fallback value where the distinction is moot.

## Prevention

Treat any `orElse()` call whose argument is a method call (not a constant or a variable already holding a computed value) as a default red flag in code review.

## Monitoring and Alerts

- Add a metric (or a trace span) specifically around the fallback computation, tagged with whether the primary (cache) value was ultimately present or absent; an alert threshold on "fallback executed while primary value was present" at non-zero volume flags this exact bug pattern directly, rather than requiring latency correlation to rediscover it.
- Track database query volume attributable to the fallback path separately from genuine cache-miss-driven queries; a query volume that tracks total request volume rather than cache-miss volume is a strong, fast signal of this exact mistake.
- Add a static-analysis rule (where tooling supports custom checks) flagging `Optional.orElse(...)` calls whose argument is a method invocation, since a constant or already-computed value is the only case where `orElse()` and `orElseGet()` are actually equivalent.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a request handler refactored from manual null-checks to an `Optional`-based chain showed a latency regression that persisted even on cache-hit requests, where the added fallback logic should never have run.
- **Task:** find why a refactor intended purely as a readability improvement was making the majority code path measurably slower.
- **Action:** ruled out a cache hit-rate regression and a connection-pool issue, then traced the actual database calls and found the fallback executing unconditionally on every request due to `orElse()`'s eager argument evaluation.
- **Result:** swapped `orElse()` for `orElseGet()`, immediately eliminating the unnecessary database calls, and added a review/static-analysis rule to catch the same mistake before it ships again.

## Staff-Level Discussion

This is a case where an API's naming similarity actively works against correct usage: `orElse()` and `orElseGet()` look like interchangeable stylistic variants, and the difference between them (eager versus lazy argument evaluation) is invisible at the call site unless the reader already knows to look for it — a refactor that "reads cleaner" can silently regress performance with no compiler warning and no obviously wrong output, since the fallback's result is simply discarded on the hit path rather than causing any incorrect behavior. The organizational lesson is that API ergonomics that hide a meaningful cost difference behind near-identical names are a recurring source of exactly this class of regression, and a Staff engineer should treat "this refactor touches an Optional fallback chain" as a specific, named review trigger rather than trusting that the change is obviously safe because it's "just a readability cleanup." At larger scale, this also argues for measuring the actual before/after cost of any refactor touted as a pure readability improvement — a refactor with zero intended behavior change should be verified, not assumed, to have zero performance change, especially anywhere a fallback or default-value computation is involved.

## Related Handbook Chapters

- [Optional and Null Strategy](../syllabus/02-java/language-core/optional-and-null-strategy.md) — canonical `orElse()` vs. `orElseGet()` mechanics and the measured ~1200x evaluation-cost gap.
- [Connection Pooling and Sizing (HikariCP)](../syllabus/06-databases/connection-pooling-and-sizing.md) — related considerations when a fallback path makes unnecessary database calls under load.
