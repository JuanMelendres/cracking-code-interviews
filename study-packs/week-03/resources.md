---
title: "Week 3 Resources"
week: 3
last_reviewed: 2026-07-29
---

# Week 3 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-503/T-504/T-505 — Spring Transactions and Propagation

| Source | Type | Note |
|---|---|---|
| [Spring Framework documentation — Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction.html) | PRIMARY | Declarative section in full |
| [Spring Framework documentation — AOP Proxies](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html) | PRIMARY | |
| Spring Framework 6.1.14 + H2 + HikariCP + PostgreSQL JDBC, via Maven Central | TOOL | Produced all 6 real demos in `01-transactions-and-propagation.md`; see `practice/java/week-03/spring-demos/` |

## T-611 — Isolation Levels

| Source | Type | Note |
|---|---|---|
| [PostgreSQL documentation, Ch. 13 "Concurrency Control"](https://www.postgresql.org/docs/current/mvcc.html) | PRIMARY | §13.2 isolation levels, §13.3 explicit locking |
| Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 7 | BOOK | pp. 233–251, weak isolation |
| PostgreSQL 16 via Docker | TOOL | Produced the real write-skew reproduction; see `practice/sql/week-03/` |

## T-801/T-802 — Design Method and Estimation

| Source | Type | Note |
|---|---|---|
| The System Design Primer (github.com/donnemartin/system-design-primer) | SECONDARY | Broad component reference |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-base-audit.md` | PRIMARY | Confirmed zero System Design coverage and 1-row JVM/concurrency coverage in the original knowledge base |
| `00-project/learning-roadmap.md` §3 (Week 3) | PRIMARY | Full Week 3 spec, including the checkpoint pass criteria |
