---
title: "T-503/504/505 · Spring Transactions, Proxies, and Propagation"
topic_id: T-503/T-504/T-505
domain: Spring
tier: Advanced
iwi: 8.15
prerequisites: [T-901]
unlocks: [T-611]
week: 3
last_reviewed: 2026-07-30
canonical: ../../handbook/spring/transactional-proxy-mechanics-and-propagation.md
---

# T-503 / T-504 / T-505 · Spring Transactions, Proxies, and Propagation

**IWI 8.15 · Advanced tier · Highest-IWI single Spring topic in the register**

**Canonical chapter:** [Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md). This file is the Week 3 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because other deliverables (the Week 3 checkpoint mock, the checklist) cite them directly — notably §6, cited by the checkpoint mock for the `readOnly` demo.

**Verification note:** every demo behind this summary is backed by a real, executed Spring Framework 6.1.14 demo — no Spring Boot, no Maven, plain jars from Maven Central on a hand-built classpath. Source and full output: `practice/java/week-03/spring-demos/`.

## Table of Contents

1. [The concept — AOP proxies](#1-the-concept--aop-proxies)
2. [Why it exists](#2-why-it-exists)
3. [Demo 1 — self-invocation bypasses the proxy](#3-demo-1--self-invocation-bypasses-the-proxy)
4. [Demo 2 — checked exceptions do not roll back by default](#4-demo-2--checked-exceptions-do-not-roll-back-by-default)
5. [Demo 3 — REQUIRES_NEW commits independently](#5-demo-3--requires_new-commits-independently)
6. [Demo 4 & 5 — readOnly is a hint, enforcement is driver-dependent](#6-demo-4--5--readonly-is-a-hint-enforcement-is-driver-dependent)
7. [Demo 6 — connection-pool exhaustion from a long transaction](#7-demo-6--connection-pool-exhaustion-from-a-long-transaction)
8. [Propagation reference](#8-propagation-reference)
9. [Trade-offs](#9-trade-offs)
10. [Interview questions](#10-interview-questions)
11. [Common mistakes](#11-common-mistakes)
12. [Staff-level discussion](#12-staff-level-discussion)
13. [Summary](#13-summary)
14. [Key Takeaways](#14-key-takeaways)
15. [Cheat Sheet](#15-cheat-sheet)
16. [Flashcards](#16-flashcards)
17. [Practice Exercises](#17-practice-exercises)
18. [Additional Reading](#18-additional-reading)
19. [Official References](#19-official-references)

---

## 1. The concept — AOP proxies

`@Transactional` is implemented as a proxy wrapping your bean — Spring creates a JDK dynamic proxy or a CGLIB subclass proxy, and that proxy intercepts each call to begin/commit/rollback a transaction. → [Definition and Purpose](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#definition-and-purpose), [Core Concepts](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#core-concepts).

## 2. Why it exists

Declarative transaction management exists so demarcation doesn't have to be hand-written per method — the cost is that it only works through the proxy; any call that bypasses it (self-invocation) never gets a transaction. → [Definition and Purpose](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#definition-and-purpose).

## 3. Demo 1 — self-invocation bypasses the proxy

Measured: called through the proxy, `isActualTransactionActive() = true`; called via `this.method()`, `= false`. Three real fixes: split into a separate bean; self-inject via `ApplicationContext.getBean(getClass())`/`@Lookup`; `AopContext.currentProxy()`. → [Internal Implementation, Demo 1](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation).

## 4. Demo 2 — checked exceptions do not roll back by default

Measured: a checked exception thrown after a successful `INSERT` leaves that row committed under the default rule; `rollbackFor = Exception.class` fixes it. The default mirrors EJB's original convention. → [Internal Implementation, Demo 2](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation), [Historical Context](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#historical-context).

## 5. Demo 3 — REQUIRES_NEW commits independently

Measured: an audit-log `INSERT` under `REQUIRES_NEW` survives the outer transaction's rollback. Real use case: audit logging. Real deadlock risk: the suspended outer transaction can hold a lock the inner transaction needs. → [Internal Implementation, Demo 3](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation).

## 6. Demo 4 & 5 — readOnly is a hint, enforcement is driver-dependent

Measured on two databases with identical code: H2 silently allows a write inside `readOnly = true`; PostgreSQL rejects it at the database level. `readOnly` is a portable hint, not a portable constraint. → [Internal Implementation, Demo 4 & 5](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation).

## 7. Demo 6 — connection-pool exhaustion from a long transaction

Measured: two threads holding a connection for 6 seconds each exhaust a pool of size 2, causing a third, unrelated, fast request to time out after ~2 seconds waiting for a connection. → [Internal Implementation, Demo 6](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#internal-implementation), [Production Scenarios](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#production-scenarios).

## 8. Propagation reference

`REQUIRED` (default, join-or-start), `REQUIRES_NEW` (always independent), `SUPPORTS`, `MANDATORY`, `NOT_SUPPORTED`, `NEVER`, `NESTED`. → [Core Concepts, propagation modes](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#core-concepts) has the full behavior/use-case table.

## 9. Trade-offs

Declarative `@Transactional` removes manual demarcation code but only works through the proxy; `REQUIRES_NEW` survives caller rollback but risks a real deadlock; `readOnly` enforcement is driver-dependent; long-lived transactions reduce pool availability for every other request. → [Trade-offs](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#trade-offs).

## 10. Interview questions

1. Method A calls `@Transactional` method B in the same class. What happens, and why? Three fixes.
2. Your method threw a checked exception. Did it roll back? Why is that the default?
3. `REQUIRES_NEW` — give a real use case and name the deadlock risk.
4. Where does the transaction boundary belong, and defend it.
5. There's an HTTP call inside a transaction. What breaks, and at what load?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#interview-questions).

## 11. Common mistakes

Assuming `@Transactional` "just works" regardless of call path; assuming any exception rolls back; treating `readOnly` as a guaranteed cross-database constraint; making a network call inside a transaction boundary. → [Common Mistakes](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#common-mistakes).

## 12. Staff-level discussion

Every failure mode in this topic has the same root cause: treating a declarative, proxy-based mechanism as if it were a language-level guarantee. → [Staff-Level Discussion](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#interview-answer-framework).

## 13. Summary

`@Transactional` is a proxy-based mechanism, not a language feature — every surprising behavior in this chapter (self-invocation, checked-exception non-rollback, driver-dependent `readOnly`, pool exhaustion) follows directly from that fact. → [Summary](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#summary).

## 14. Key Takeaways

→ [Key Takeaways](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#key-takeaways).

## 15. Cheat Sheet

→ [Cheat Sheet](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#cheat-sheet).

## 16. Flashcards

→ [Flashcards](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 17. Practice Exercises

→ [Practice Exercises](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#practice-exercises) and [Solutions](../../handbook/spring/transactional-proxy-mechanics-and-propagation.md#solutions). Reproducible demos: `practice/java/week-03/spring-demos/`.

## 18. Additional Reading

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 7 (also referenced in `02-isolation-levels-and-write-skew.md`)

## 19. Official References

- [Spring Framework documentation — Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction.html)
- [Spring Framework documentation — AOP Proxies](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html)
