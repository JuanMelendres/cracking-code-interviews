---
title: "Cheat Sheet: Auto-Configuration and Bean Lifecycle"
slug: auto-configuration-and-bean-lifecycle
document_type: cheat-sheet
domain: spring
topic_id: T-501
canonical: ../handbook/spring/auto-configuration-and-bean-lifecycle.md
last_updated: 2026-08-04
---

# Auto-Configuration and Bean Lifecycle

**Canonical chapter:** [`handbook/spring/auto-configuration-and-bean-lifecycle.md`](../handbook/spring/auto-configuration-and-bean-lifecycle.md)

## Core Mental Model

Every Spring bean goes through the same fixed assembly line, and every framework feature you rely on (transactions, validation, security) is implemented by hooking into a specific station on that line. Auto-configuration is a separate, later concern: a set of conditionally-applied `@Configuration` classes that only take effect if the application hasn't already supplied its own answer.

## Essential Definitions

- **Auto-configuration** — `@Configuration` classes guarded by conditions (`@ConditionalOnClass`, `@ConditionalOnMissingBean`, etc.), applied automatically based on classpath contents and what the application has already defined itself.
- **Bean lifecycle order** — Constructor → `BeanPostProcessor.postProcessBeforeInitialization` → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom init-method → `BeanPostProcessor.postProcessAfterInitialization` → ready for use. Shutdown mirror: `@PreDestroy` → `DisposableBean.destroy()` → custom destroy-method.

## Decision Table

| Approach | Benefit | Cost |
|---|---|---|
| `@PostConstruct`/`@PreDestroy` | Simple, annotation-based | Only runs within a Spring-managed bean |
| `InitializingBean`/`DisposableBean` | Explicit, typed interface | Couples the class directly to Spring's API |
| `@Async` returning `void` | Simplest fire-and-forget syntax | Caller cannot observe success, failure, or completion at all |
| `@Async` returning `CompletableFuture` | Caller can observe success/failure | Easy to forget to call `.get()`/attach a callback, silently reverting to the same blind spot |

| Need | Mechanism |
|---|---|
| Hook before dependency injection completes | Not possible via standard callbacks — use a `BeanPostProcessor` |
| Hook after injection, using injected fields | `@PostConstruct` |
| Wrap/modify a bean before its own init logic runs | `BeanPostProcessor.postProcessBeforeInitialization` (how `@Transactional` proxies are made) |
| Override exactly one auto-configured bean | Define that bean directly — `@ConditionalOnMissingBean` skips the default |
| Observe an `@Async` method's success/failure | Return `CompletableFuture<T>`, or configure `AsyncUncaughtExceptionHandler` |

## Key Numbers (real, executed — Spring Framework 6.1.14)

```
Call returned after 12ms. Exception visible to caller: false
[test observer] async work actually completed: true
[test observer] row count after the exception: 0 (transaction correctly rolled back)
```
The transaction rolls back correctly — the surprise is that the calling thread has already moved on and never sees the failure.

## Common Pitfalls

- Believing `@Transactional` "doesn't work" on `@Async` methods — it works; the surprise is about visibility, not correctness
- Using `@PostConstruct` for logic depending on another bean's own `@PostConstruct` having already run — cross-bean initialization order isn't guaranteed
- Assuming auto-configuration can't be overridden without disabling it entirely — `@ConditionalOnMissingBean` exists specifically so a single bean can be overridden

## Interview Answer Skeleton

**30-sec:** Beans go through a fixed lifecycle: constructor, `BeanPostProcessor.before`, `@PostConstruct`, `InitializingBean`, custom init, `BeanPostProcessor.after` — `@Transactional`'s proxy is created at the `BeanPostProcessor.before` step, before `@PostConstruct` even runs. `@Async` + `@Transactional` produces a correct transaction with an invisible failure, since a void async method returns before the work executes.

**2-min:** Add why auto-configuration runs after application config (so `@ConditionalOnMissingBean` can detect an override) + the lifecycle sequence + the measured 12ms-return/invisible-rollback trace.

**Whiteboard:** Draw the bean-lifecycle sequence diagram. Circle the `BeanPostProcessor.before` step and annotate: "this is where `@Transactional`'s proxy gets created — before the bean has even run its own init logic."

**Staff-level framing:** the `@Async`+`@Transactional` gotcha is an instance of "any mechanism that changes which thread executes code changes what 'the caller' can observe" — the same class of problem as distributed-systems failure ambiguity. `CompletableFuture` is the in-process analogue of an idempotency-key mechanism: it moves the ambiguity resolution back to a party that can actually check.

## Production Warning Signs

- **Real incident pattern:** a background job's silent failures go unnoticed for weeks — no alerts ever fire, yet a data audit later reveals a small, steady percentage of reconciliation records were never processed. Root cause: a `void`-returning `@Async` method whose exceptions have nowhere to surface.
- Mitigation: add alerting on `AsyncUncaughtExceptionHandler`. Permanent fix: change the method signature to `CompletableFuture<Void>` so the caller can actually observe failure.

## Related

- [Spring Transactional Proxy Mechanics and Propagation](transactional-proxy-mechanics-and-propagation.md)
- `handbook/spring/security-filter-chain.md`
