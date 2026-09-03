---
title: "@Transactional Silently Skipped on Self-Invocation"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/language-core/reflection-and-dynamic-proxies.md
  - ../syllabus/05-spring/spring-framework-vs-spring-boot.md
source: handbook/java-core/reflection-and-dynamic-proxies.md#production-scenarios
---

# @Transactional Silently Skipped on Self-Invocation

## Context

A Spring service method annotated `@Transactional` calls another `@Transactional` method on `this` directly (`this.otherMethod()`).

## Symptoms

The second method's transaction boundary is silently never applied — no error, just unexpectedly missing transactional behavior, discovered only when a partial-write bug surfaces in production.

## Impact

Real data-integrity risk from a transaction boundary the developer believed was active but genuinely wasn't.

## Initial Hypotheses

- A Spring configuration error disabling `@Transactional` entirely — checked, and ruled out: other, externally-called methods on the same bean are correctly transactional.
- A bug in the transaction manager itself — checked, and ruled out: no errors, no unusual logs.
- The self-invocation bypasses the proxy entirely — correct.

## Evidence

Spring's default `@Transactional` support is implemented via the `java.lang.reflect.Proxy`/`InvocationHandler` pattern (or CGLIB for class-based proxying) — the proxy object is what callers from *outside* the bean interact with, but `this` inside the bean's own method refers to the real, unproxied object, never routing through the `InvocationHandler` at all.

## Investigation Timeline

1. **Partial-write data-integrity bug surfaces in production**, traced to a method the developer believed was covered by its own `@Transactional` annotation.
2. **Spring configuration reviewed** and confirmed correct — other methods on the same bean, called from outside the bean, are correctly transactional, ruling out a global configuration issue.
3. **Transaction manager logs and behavior reviewed** and found unremarkable — no errors, no rollback anomalies reported, ruling out a transaction-manager defect.
4. **Call path to the affected method traced**, revealing it is invoked via `this.otherMethod()` from within another method on the same bean, rather than from an external caller.
5. **Proxy mechanism confirmed as the cause** — Spring's `@Transactional` support relies on a proxy object interposed between external callers and the bean; a call made via `this` inside the bean's own code never passes through that proxy, so the `InvocationHandler` that would start the transaction is never invoked.

## Root Cause

A self-invocation (`this.otherMethod()`) calls the real object directly, completely bypassing the proxy layer that Spring's `@Transactional` support depends on — the transactional behavior genuinely never runs, because the code path that would trigger it (calling through the proxy) was never taken.

## Immediate Mitigation

Manually wrap the self-invoked logic in an explicit `TransactionTemplate` call as a stopgap.

## Permanent Fix

Refactor so the second method is called through a proxy-aware path — either by injecting the bean's own proxy reference (via `AopContext.currentProxy()`, with self-injection enabled) or, more commonly recommended, by extracting the two methods into separate beans so the call genuinely crosses a proxy boundary.

## Alternatives Considered

Removing `@Transactional` from the inner method and manually managing transactions — rejected in favor of the proxy-boundary-respecting refactor, which preserves the declarative annotation-based approach used elsewhere in the codebase.

## Trade-offs

Splitting into separate beans adds a small amount of structural overhead — accepted, since it makes the proxy boundary explicit and visible in the codebase's own class structure, rather than an invisible runtime gotcha.

## Prevention

Any code review of `@Transactional` (or `@Async`, or any other proxy-based Spring annotation) usage should flag self-invocation (`this.someAnnotatedMethod()`) specifically, since it is a silent and common way to defeat proxy-based behavior.

## Monitoring and Alerts

- Add a static-analysis rule (several are available for Spring codebases) that flags any call of the form `this.someMethod()` where `someMethod` carries `@Transactional`, `@Async`, `@Cacheable`, or another proxy-dependent annotation, catching the defect at review time rather than in production.
- Add an integration test asserting that any method intended to run in its own transaction actually does so when invoked via the code path used in production (i.e., testing through the real bean reference, not a direct unproxied instance), so a self-invocation regression fails a test rather than shipping silently.
- Track a metric or log assertion for "expected transaction boundary count" versus "actual transaction boundary count" during integration testing of critical multi-step write paths, so a silently-skipped transaction boundary produces a measurable discrepancy before reaching production.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a Spring service exhibited a partial-write data-integrity bug traced to a method the developer believed was covered by its own `@Transactional` annotation.
- **Task:** find why a correctly-annotated, correctly-configured transactional method wasn't actually running in a transaction, with no error anywhere in the transaction manager's own logs.
- **Action:** ruled out a Spring configuration issue and a transaction-manager bug, then traced the call path and found the method was invoked via `this` from within the same bean — bypassing the proxy that Spring's declarative transaction support depends on entirely.
- **Result:** applied an explicit `TransactionTemplate` wrap as an immediate fix, then permanently resolved it by splitting the two methods into separate beans so the call genuinely crosses the proxy boundary, and added a static-analysis rule to catch future self-invocations of proxy-dependent methods.

## Staff-Level Discussion

Self-invocation defeating `@Transactional` is one of the clearest illustrations of a framework "magic" mechanism having a hard, structural boundary that isn't visible from the annotation's own declaration site — nothing about `@Transactional`'s syntax hints that its effect depends entirely on which object reference the call goes through. This is a direct consequence of how JDK dynamic proxies (and CGLIB subclass proxies) work: they can only intercept calls that arrive from outside the proxied object, because the proxy is a separate object wrapping the real one, and `this` inside the real object's own methods always refers to the real, unwrapped instance. A Staff engineer's response to encountering this once should be to treat every proxy-based Spring annotation (`@Transactional`, `@Async`, `@Cacheable`, `@Retryable`) as carrying the identical structural risk, and to push for the static-analysis rule as a standing safeguard rather than relying on every future engineer independently rediscovering the proxy boundary. This also has a broader architectural implication worth raising at the review stage: a class with several methods that call each other internally while relying on different proxy-based cross-cutting annotations on each is a signal that those responsibilities may belong in separate beans in the first place, since the proxy-boundary constraint effectively forces that separation to make the annotations work correctly at all.

## Related Handbook Chapters

- [Reflection and Dynamic Proxies](../syllabus/02-java/language-core/reflection-and-dynamic-proxies.md) — canonical dynamic-proxy mechanics and the reproduced `Proxy`/`InvocationHandler` interception model this incident traces back to.
- [Spring Framework vs. Spring Boot](../syllabus/05-spring/spring-framework-vs-spring-boot.md) — related Spring mechanism context for how proxy-based cross-cutting behavior is wired into a Spring application.
