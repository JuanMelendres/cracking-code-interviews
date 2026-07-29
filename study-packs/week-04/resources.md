---
title: "Week 4 Resources"
week: 4
last_reviewed: 2026-07-29
---

# Week 4 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-804 — Caching

| Source | Type | Note |
|---|---|---|
| AWS Builders' Library — ["Caching challenges and strategies"](https://aws.amazon.com/builders-library/caching-challenges-and-strategies/) | PRIMARY | |
| Java (pure JDK, `ConcurrentHashMap`, `CompletableFuture`) | TOOL | Produced the real cache-stampede reproduction; see `practice/java/week-04/failure-modes/` |

## T-909 — Distributed Failure Modes

| Source | Type | Note |
|---|---|---|
| Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 8 "The Trouble with Distributed Systems" | BOOK | The fencing-token example follows Kleppmann's original |
| AWS Builders' Library — ["Timeouts, retries, and backoff with jitter"](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/) | PRIMARY | |
| Java (pure JDK, `ExecutorService`, `Future`) | TOOL | Produced the real retry-storm and fencing-token reproductions |

## T-803 — API Design

| Source | Type | Note |
|---|---|---|
| [Google API Design Guide](https://cloud.google.com/apis/design) | PRIMARY | Resource naming, standard methods, errors |
| [RFC 9457 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457) | PRIMARY | |
| PostgreSQL 16 via Docker | TOOL | Produced the real pagination measurement; see `practice/sql/week-04/` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-base-audit.md` | PRIMARY | Confirmed zero coverage of caching, distributed failure modes, and API design in the original knowledge base |
| `00-project/learning-roadmap.md` §3 (Week 4) | PRIMARY | Full Week 4 spec this pack implements |
