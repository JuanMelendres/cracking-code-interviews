# Knowledge Architecture Blueprint
### Java-Interview-Handbook — Phase 2
**Foundation document:** `knowledge-base-audit.md`
**Date:** 28 July 2026
**Target calibration:** Senior Backend Engineer (L5) / Staff Engineer (L6)
**Status:** Blueprint only — no chapters generated

---

## 0. How to Read This Blueprint

### 0.1 The governing design principle

Phase 1 found that the existing knowledge base allocated **15 rows to Git and 1 row to the JVM**. Effort was distributed by *category convenience* rather than by *interview consequence*.

This blueprint inverts that. Every allocation decision — depth of treatment here, chapter length later, study hours assigned — is driven by a single computed quantity, the **Interview Weight Index (IWI)**.

A direct consequence: **this document is deliberately asymmetric.** Topics scoring IWI ≥ 7.0 receive full eight-dimension dossiers. Topics below that threshold receive registry entries and grouped domain notes. Writing 900 words on `ClassLoaders` (IWI 4.6) and 900 words on *Production Incident Narratives* (IWI 8.6) would reproduce exactly the failure mode the audit identified.

### 0.2 What this document is not

It is not a list of missing topics — that was §4 of the audit. It is the **structural design of the handbook**: what exists, why, at what depth, in what order, and how each part loads the next.

### 0.3 The three organizing structures

| Structure | Question it answers | Section |
|---|---|---|
| **Topic Register** | What must be known, and how much does it matter? | §3 |
| **Dependency Graph** | In what order can it be learned? | §6 |
| **Table of Contents** | How is it packaged for a reader? | §9 |

These are deliberately **three different orderings of the same material**. The handbook's chapter order is *pedagogical*; the study roadmap (Phase 3) will be *priority-ordered*. Conflating them is the classic mistake in interview prep — people read a Java book cover-to-cover and arrive at the loop having never practiced a design problem.

---

## 1. The Interview Weight Index

### 1.1 Formula

```
IWI = 0.30·F  +  0.20·Q  +  0.20·P  +  0.15·A  +  0.15·D
```

| Symbol | Factor | Scale | Rationale for weight |
|---|---|---|---|
| **F** | Interview Frequency | 1–10 | **0.30** — highest weight. You cannot score on what is never asked. Frequency is the single best predictor of expected value. |
| **Q** | Follow-up Depth | 1–10 | **0.20** — how many layers of follow-up the topic sustains. Phase 1's core finding: Senior signal is generated in follow-ups 2–5, not in answer 1. A topic that sustains a 6-question chain is worth far more than one that closes after a definition. |
| **P** | Production Importance | 1–10 | **0.20** — how much the topic governs real system behaviour. Staff interviews probe production judgment; this factor separates "read about it" from "operated it." |
| **A** | Architecture Relevance | 1–10 | **0.15** — how load-bearing the topic is in design and trade-off discussion. Dominant at L6. |
| **D** | Differentiating Difficulty | 1–10 | **0.15** — see below. |

### 1.2 A necessary clarification on Difficulty

Raw difficulty is a poor priority signal — a topic is not valuable *because* it is hard. Used naively it would rank `Unsafe`/`VarHandles` above `@Transactional`, which is backwards.

**D is therefore defined as *differentiating* difficulty:** how reliably the topic separates a strong Senior candidate from a competent Mid one. High-D topics are those where a shallow answer is *visibly* shallow to a trained interviewer.

- `equals`/`hashCode` → D = 4. Everyone answers it; it rarely separates.
- Java Memory Model → D = 9. The gap between recited and understood is instantly audible.
- `VarHandles` → D = 9 on raw difficulty, but F = 2, so its IWI lands at 3.85. Correctly deprioritized.

### 1.3 Frequency bands

| Band | Appears in | F value |
|---|---|---|
| **Near-Certain** | ≥ 80% of loops | 9–10 |
| **Very High** | 60–80% | 7–8 |
| **High** | 40–60% | 6 |
| **Moderate** | 20–40% | 4–5 |
| **Occasional** | 5–20% | 2–3 |
| **Rare** | < 5% | 1 |

*Bands reflect a composite of Big-Tech and senior-market European/US backend loops (Amazon, Microsoft, Oracle, Capital One, Stripe, Uber, Netflix, EPAM-class consultancies). Individual companies deviate — Stripe weights API/idempotency far above the mean; Amazon weights behavioral to ~40% of the loop; Netflix weights distributed-systems failure modes heavily. Company-specific overrides appear in Ch. 16.*

### 1.4 Interpreting the score

| IWI | Meaning | Blueprint treatment |
|---|---|---|
| **8.0 – 10.0** | Outcome-deciding. Weakness here fails the loop. | Full dossier + flagship chapter section |
| **7.0 – 7.9** | High leverage. Expected at Senior; assumed at Staff. | Full dossier |
| **6.0 – 6.9** | Solid contributor. Asked often enough to matter. | Registry + grouped notes |
| **5.0 – 5.9** | Situational. Depth only if role-adjacent. | Registry + brief note |
| **< 5.0** | Long tail. Recognition-level only. | Registry entry |

### 1.5 The second index — Return on Study

IWI measures *importance*. It says nothing about **what to do first given the current state of the knowledge base**. For that, Phase 3 will use:

```
RoS = (IWI × GapSeverity) / StudyHours
```

Where `GapSeverity` ∈ {1.0 covered, 1.5 shallow, 2.0 absent, 2.5 absent-and-wrong}.

This surfaces a genuinely different ordering. *Idempotency & Exactly-Once* (IWI 7.85, absent, 6h) scores **RoS 2.62**. *Dynamic Programming* (IWI 5.85, partially covered, 40h) scores **RoS 0.22** — an order of magnitude lower. Both matter; only one is worth touching in week one. Full RoS ranking is a Phase 3 deliverable; §8 previews the top 20.

---

## 2. Tier Definitions

Tiers describe **the nature of the knowledge**, not its priority. A Foundation topic can outrank a Staff-Level topic on IWI — and several do.

| Tier | Definition | Failure signature when weak | Count |
|---|---|---|---|
| **Foundation** | Prerequisite mechanics. Assumed, rarely credited, fatal when absent. | "They didn't know how a HashMap resizes." | 24 |
| **Core** | The working competence of a Senior engineer. The bulk of a Senior loop. | "Solid but shallow — Mid, not Senior." | 41 |
| **Advanced** | Depth that distinguishes strong Senior candidates. Internals, tuning, failure analysis. | "Knew the API, not the system." | 28 |
| **Staff-Level** | System-scale judgment, trade-offs, organizational impact. | "Great engineer, no evidence of Staff scope." | 22 |
| **Expert** | Specialist depth. Bonus signal; never the reason for a hire or a reject. | *(no failure mode — absence is not penalized)* | 9 |

**Total: 124 topic units.**

> **Read this carefully:** *Expert* tier is the lowest-priority tier in the entire blueprint. Nine topics, none exceeding IWI 5.5. Engineers systematically over-invest here — `Unsafe`, suffix automata, custom classloaders — because it feels like mastery. It is the single most common misallocation in senior interview prep, and the handbook is structured to resist it.

---

## 3. The Master Topic Register

**Legend**
`Tier`: FDN Foundation · COR Core · ADV Advanced · STF Staff-Level · EXP Expert
`IP` Interview Priority 1–10 · `LP` Learning Priority 1–10 · `S`/`P` Study/Practice hours · `Rev` revision interval
`Gap`: 🔴 absent · 🟠 shallow · 🟡 partial · 🟢 adequate *(from Phase 1)*

### D1 · Java Language Core

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-101 | equals / hashCode / Comparable contracts | FDN | 5.9 | 7 | 8 | Very High | 3 | 2 | 6w | 🟠 |
| T-102 | Polymorphism & dynamic dispatch mechanics | FDN | 5.6 | 6 | 7 | Very High | 3 | 1 | 8w | 🟠 |
| T-103 | Immutability & defensive copying | FDN | 5.4 | 6 | 7 | High | 2 | 1 | 8w | 🔴 |
| T-104 | Generics: erasure, variance, PECS | COR | 5.85 | 6 | 8 | High | 6 | 3 | 6w | 🔴 |
| T-105 | Exception design & hierarchy strategy | COR | 5.5 | 6 | 6 | High | 3 | 1 | 8w | 🟠 |
| T-106 | Strings: interning, compact strings, builders | FDN | 4.9 | 5 | 5 | Moderate | 2 | 1 | 10w | 🟠 |
| T-107 | Streams & Collectors (incl. custom) | COR | 6.2 | 7 | 7 | Very High | 6 | 4 | 4w | 🔴 |
| T-108 | Lambdas & functional interface design | COR | 5.3 | 6 | 6 | High | 3 | 2 | 8w | 🟠 |
| T-109 | Optional & null strategy | FDN | 4.7 | 5 | 5 | Moderate | 1 | 1 | 12w | 🔴 |
| T-110 | Records, sealed types, pattern matching | COR | 4.4 | 5 | 6 | Moderate | 4 | 2 | 8w | 🔴 |
| T-111 | Enums, EnumMap, EnumSet | FDN | 4.2 | 4 | 4 | Moderate | 1 | 1 | 12w | 🟠 |
| T-112 | Annotations & annotation processing | ADV | 4.3 | 4 | 4 | Occasional | 3 | 1 | 12w | 🔴 |
| T-113 | Reflection & dynamic proxies | ADV | 4.75 | 5 | 6 | Moderate | 4 | 2 | 10w | 🔴 |
| T-114 | ClassLoaders & class initialization | ADV | 4.6 | 4 | 5 | Occasional | 4 | 1 | 12w | 🔴 |
| T-115 | Serialization hazards & alternatives | ADV | 4.1 | 4 | 4 | Occasional | 2 | 1 | 12w | 🔴 |
| T-116 | Java Platform Module System | EXP | 3.2 | 3 | 3 | Rare | 3 | 1 | — | 🔴 |

### D2 · Collections

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-201** | **HashMap internals** ⭐ | FDN | **7.4** | **10** | **10** | Near-Certain | 5 | 3 | 3w | 🔴 |
| T-202 | ArrayList / LinkedList internals & growth | FDN | 5.6 | 7 | 7 | Very High | 2 | 1 | 8w | 🟠 |
| T-203 | TreeMap / TreeSet & Red-Black mechanics | COR | 5.2 | 5 | 6 | High | 3 | 2 | 8w | 🟠 |
| T-204 | ArrayDeque & the legacy Stack/Vector problem | FDN | 4.8 | 5 | 6 | Moderate | 1 | 1 | 10w | 🔴 |
| **T-205** | **ConcurrentHashMap internals** | ADV | **6.65** | 7 | 8 | High | 4 | 2 | 5w | 🔴 |
| T-206 | CopyOnWriteArrayList & copy-on-write trade-offs | ADV | 4.9 | 5 | 5 | Moderate | 1 | 1 | 10w | 🔴 |
| T-207 | BlockingQueue family & producer-consumer | COR | 5.8 | 6 | 7 | High | 3 | 2 | 6w | 🔴 |
| T-208 | Fail-fast vs weakly-consistent iterators | COR | 5.1 | 5 | 5 | High | 1 | 1 | 8w | 🟠 |
| T-209 | Collection selection decision matrix | COR | 5.7 | 6 | 7 | High | 2 | 2 | 6w | 🟡 |

### D3 · JVM

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-301 | JVM memory layout & regions | FDN | 6.3 | 7 | 8 | Very High | 3 | 1 | 5w | 🔴 |
| T-302 | Object layout, headers, compressed oops | ADV | 4.9 | 4 | 5 | Moderate | 2 | 1 | 10w | 🔴 |
| T-303 | GC fundamentals: roots, reachability, generational | COR | 6.9 | 8 | 9 | Very High | 4 | 2 | 4w | 🟡 |
| **T-304** | **G1 internals: regions, RSets, write barriers** | ADV | **6.8** | 7 | 8 | High | 5 | 2 | 5w | 🔴 |
| T-305 | ZGC & Shenandoah: concurrent collection | ADV | 5.4 | 5 | 6 | Moderate | 3 | 1 | 8w | 🔴 |
| **T-306** | **GC tuning & log analysis** ⭐ | ADV | **7.35** | 8 | 9 | High | 6 | 5 | 4w | 🔴 |
| **T-307** | **Memory leak diagnosis & heap dump analysis** ⭐ | ADV | **6.75** | 7 | 9 | Moderate | 5 | 5 | 5w | 🔴 |
| T-308 | JIT: tiered compilation, inlining, deoptimization | ADV | 5.45 | 5 | 6 | Moderate | 4 | 2 | 8w | 🔴 |
| T-309 | Escape analysis & scalar replacement | ADV | 4.6 | 4 | 5 | Occasional | 2 | 1 | 10w | 🔴 |
| T-310 | Safepoints & stop-the-world mechanics | ADV | 5.0 | 5 | 5 | Moderate | 2 | 1 | 8w | 🔴 |
| T-311 | Native memory, direct buffers, off-heap | ADV | 4.7 | 4 | 5 | Occasional | 3 | 1 | 10w | 🔴 |
| T-312 | JVM flags & ergonomics for containers | COR | 5.9 | 6 | 7 | Moderate | 3 | 2 | 6w | 🔴 |

### D4 · Concurrency

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-401** | **Java Memory Model & happens-before** ⭐ | ADV | **7.75** | 9 | **10** | Very High | 8 | 4 | 3w | 🔴 |
| T-402 | volatile & final field semantics | COR | 6.6 | 8 | 9 | Very High | 3 | 2 | 4w | 🟠 |
| T-403 | synchronized, monitors, lock optimizations | COR | 6.1 | 7 | 7 | Very High | 3 | 2 | 5w | 🟠 |
| T-404 | ReentrantLock, ReadWriteLock, StampedLock | COR | 5.7 | 6 | 6 | High | 3 | 2 | 6w | 🔴 |
| T-405 | Atomics, CAS, and the ABA problem | ADV | 5.9 | 6 | 7 | High | 3 | 2 | 6w | 🔴 |
| **T-406** | **Executors & thread pool sizing** ⭐ | COR | **7.15** | 8 | 9 | Very High | 4 | 3 | 4w | 🔴 |
| T-407 | CompletableFuture & async composition | COR | 6.4 | 7 | 7 | High | 4 | 3 | 5w | 🔴 |
| T-408 | ForkJoinPool & work stealing | ADV | 4.9 | 4 | 5 | Moderate | 2 | 1 | 10w | 🔴 |
| T-409 | Deadlock, livelock, starvation, race conditions | COR | 6.7 | 8 | 8 | Very High | 3 | 3 | 5w | 🟠 |
| **T-410** | **Virtual Threads (Loom)** ⭐ | ADV | **6.75** | 7 | 8 | High | 5 | 3 | 4w | 🔴 |
| T-411 | Structured Concurrency | ADV | 5.2 | 5 | 6 | Moderate | 3 | 2 | 8w | 🔴 |
| T-412 | Scoped Values & ThreadLocal migration | ADV | 4.5 | 4 | 5 | Occasional | 2 | 1 | 10w | 🔴 |
| T-413 | ThreadLocal semantics & classloader leaks | ADV | 5.3 | 5 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| T-414 | Lock-free structures & memory ordering | EXP | 4.6 | 4 | 4 | Occasional | 5 | 3 | 12w | 🔴 |
| T-415 | VarHandles, Unsafe, and their replacement | EXP | 3.85 | 3 | 3 | Rare | 3 | 1 | — | 🔴 |
| T-416 | Foreign Function & Memory API | EXP | 3.4 | 2 | 3 | Rare | 3 | 1 | — | 🔴 |

### D5 · Spring

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-501 | IoC container & bean lifecycle | COR | 6.05 | 7 | 8 | Very High | 4 | 2 | 5w | 🟠 |
| T-502 | Bean scopes & proxy modes | COR | 5.4 | 5 | 6 | High | 2 | 1 | 8w | 🔴 |
| **T-503** | **Spring AOP & proxy mechanics** | ADV | **6.3** | 7 | 8 | High | 4 | 2 | 5w | 🔴 |
| **T-504** | **@Transactional semantics & self-invocation** ⭐ | ADV | **8.15** | **9** | **10** | Very High | 5 | 4 | 3w | 🔴 |
| **T-505** | **Transaction propagation & isolation in Spring** ⭐ | ADV | **7.7** | 8 | 9 | Very High | 4 | 3 | 4w | 🔴 |
| **T-506** | **Spring Boot auto-configuration internals** | COR | **7.05** | 8 | 8 | Very High | 4 | 2 | 5w | 🟠 |
| T-507 | Externalized config, profiles, property binding | FDN | 5.0 | 5 | 5 | High | 2 | 1 | 10w | 🟠 |
| T-508 | Spring MVC request lifecycle | COR | 5.6 | 6 | 6 | High | 3 | 2 | 8w | 🟠 |
| T-509 | WebFlux & reactive programming | ADV | 5.1 | 5 | 5 | Moderate | 6 | 3 | 8w | 🔴 |
| T-510 | Spring Data repositories & query derivation | COR | 5.3 | 5 | 6 | High | 3 | 2 | 8w | 🟠 |
| **T-511** | **Spring Security filter chain** ⭐ | ADV | **7.0** | 8 | 8 | High | 5 | 3 | 4w | 🔴 |
| **T-512** | **OAuth2 / OIDC flows** ⭐ | ADV | **7.15** | 8 | 8 | High | 5 | 3 | 4w | 🔴 |
| **T-513** | **JWT design, validation, revocation** ⭐ | ADV | **7.0** | 8 | 8 | High | 3 | 2 | 4w | 🔴 |
| T-514 | Spring Cache abstraction & pitfalls | COR | 5.5 | 5 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| **T-515** | **Resilience: retry, circuit breaker, bulkhead, timeout** ⭐ | STF | **7.6** | 8 | 9 | High | 5 | 4 | 4w | 🔴 |
| T-516 | Actuator, health, and observability hooks | COR | 5.2 | 5 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| T-517 | Testing Spring: slices & context caching | COR | 5.4 | 5 | 6 | Moderate | 3 | 3 | 8w | 🔴 |

### D6 · Persistence & Databases

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-601 | JPA entity lifecycle & persistence context | COR | 6.6 | 7 | 8 | Very High | 4 | 3 | 5w | 🟠 |
| **T-602** | **Fetch strategies & the N+1 problem** ⭐ | ADV | **7.2** | 8 | 9 | Very High | 4 | 3 | 4w | 🔴 |
| T-603 | Hibernate caching: L1, L2, query cache | ADV | 5.8 | 6 | 6 | Moderate | 3 | 2 | 6w | 🔴 |
| **T-604** | **Optimistic vs pessimistic locking** ⭐ | ADV | **7.1** | 8 | 8 | High | 3 | 3 | 4w | 🔴 |
| T-605 | Entity mapping & inheritance strategies | COR | 5.2 | 5 | 5 | High | 3 | 2 | 8w | 🟠 |
| T-606 | Flush modes, dirty checking, batch writes | ADV | 5.6 | 6 | 6 | Moderate | 3 | 2 | 6w | 🔴 |
| **T-607** | **Connection pooling & sizing (HikariCP)** ⭐ | ADV | **6.4** | 7 | 8 | Moderate | 3 | 2 | 5w | 🔴 |
| T-608 | SQL fundamentals: joins, grouping, windows | FDN | 6.0 | 7 | 7 | Very High | 5 | 5 | 5w | 🟠 |
| **T-609** | **Index structures: B+Tree, composite, covering** ⭐ | ADV | **8.3** | **9** | **10** | Very High | 6 | 5 | 3w | 🔴 |
| **T-610** | **Query planning & EXPLAIN ANALYZE** ⭐ | ADV | **7.9** | 9 | 9 | High | 6 | 6 | 3w | 🔴 |
| **T-611** | **Isolation levels & concurrency anomalies** ⭐ | ADV | **7.95** | 9 | 10 | Very High | 5 | 3 | 3w | 🔴 |
| **T-612** | **MVCC in PostgreSQL, vacuum, bloat** ⭐ | ADV | **6.9** | 7 | 8 | Moderate | 4 | 2 | 5w | 🔴 |
| T-613 | Locks, deadlocks, and lock escalation in RDBMS | ADV | 6.5 | 7 | 7 | Moderate | 3 | 2 | 5w | 🔴 |
| **T-614** | **Partitioning & sharding strategies** ⭐ | STF | **7.6** | 8 | 8 | High | 5 | 3 | 4w | 🔴 |
| T-615 | Replication, read replicas, replica lag | STF | 6.9 | 7 | 8 | Moderate | 3 | 2 | 5w | 🔴 |
| **T-616** | **Zero-downtime schema migration** ⭐ | STF | **7.3** | 8 | 9 | Moderate | 4 | 3 | 4w | 🔴 |
| T-617 | NoSQL selection & data modelling | STF | 6.6 | 7 | 7 | High | 5 | 3 | 5w | 🔴 |
| **T-618** | **Distributed transactions: Saga, Outbox, 2PC** ⭐ | STF | **7.65** | 8 | 9 | High | 5 | 4 | 4w | 🔴 |

### D7 · Messaging & Kafka

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-701 | Kafka architecture: topics, partitions, brokers, ISR | COR | 6.4 | 7 | 8 | High | 3 | 2 | 5w | 🟠 |
| **T-702** | **Producer semantics: acks, idempotence, batching** ⭐ | ADV | **7.4** | 8 | 9 | High | 3 | 2 | 4w | 🔴 |
| **T-703** | **Consumer groups, rebalancing, offset management** ⭐ | ADV | **7.5** | 8 | 9 | High | 4 | 3 | 4w | 🟠 |
| **T-704** | **Delivery semantics & exactly-once processing** ⭐ | ADV | **8.0** | 9 | 9 | High | 4 | 3 | 3w | 🔴 |
| **T-705** | **Partition key design & ordering guarantees** ⭐ | STF | **7.55** | 8 | 9 | High | 3 | 2 | 4w | 🔴 |
| T-706 | Retention, log compaction, tiered storage | ADV | 5.7 | 6 | 6 | Moderate | 2 | 1 | 6w | 🟠 |
| **T-707** | **Consumer lag, backpressure, DLQ strategy** ⭐ | STF | **7.2** | 8 | 8 | Moderate | 3 | 3 | 4w | 🔴 |
| T-708 | Schema Registry & compatibility evolution | STF | 6.5 | 7 | 7 | Moderate | 3 | 2 | 5w | 🔴 |
| T-709 | Kafka Streams & stateful stream processing | ADV | 5.3 | 5 | 5 | Moderate | 5 | 3 | 8w | 🟠 |
| T-710 | Messaging patterns & CDC | STF | 6.3 | 6 | 7 | Moderate | 3 | 2 | 6w | 🔴 |

### D8 · System Design

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-801** | **Design method: requirements → capacity → API → data → scale** ⭐ | STF | **8.65** | **10** | **10** | Near-Certain | 6 | 10 | 2w | 🔴 |
| **T-802** | **Back-of-envelope estimation & latency numbers** ⭐ | FDN | **7.4** | 9 | 9 | Very High | 3 | 4 | 3w | 🔴 |
| **T-803** | **API design: REST, gRPC, GraphQL, versioning** ⭐ | COR | **7.9** | 9 | 9 | Very High | 5 | 4 | 3w | 🔴 |
| **T-804** | **Caching strategies & invalidation** ⭐ | STF | **8.45** | **10** | **10** | Near-Certain | 6 | 5 | 2w | 🔴 |
| T-805 | Load balancing, service discovery, health checking | COR | 6.6 | 7 | 7 | High | 3 | 2 | 5w | 🔴 |
| **T-806** | **Data partitioning & consistent hashing** ⭐ | STF | **7.7** | 8 | 9 | High | 4 | 3 | 4w | 🔴 |
| **T-807** | **CAP, PACELC, and consistency models** ⭐ | STF | **7.9** | 9 | 9 | High | 5 | 3 | 3w | 🔴 |
| **T-808** | **Rate limiting & throttling algorithms** ⭐ | STF | **7.6** | 8 | 9 | High | 3 | 3 | 4w | 🔴 |
| **T-809** | **Idempotency & exactly-once at system edges** ⭐ | STF | **7.85** | 9 | 10 | High | 3 | 3 | 3w | 🔴 |
| T-810 | Search & indexing systems | ADV | 5.8 | 6 | 6 | Moderate | 4 | 2 | 8w | 🔴 |
| T-811 | Storage selection & polyglot persistence | STF | 6.9 | 7 | 8 | High | 3 | 2 | 5w | 🔴 |
| T-812 | Real-time delivery: WebSocket, SSE, long-poll, push | ADV | 5.9 | 6 | 6 | Moderate | 3 | 2 | 6w | 🔴 |
| **T-813** | **Canonical design problems (12-problem set)** ⭐ | STF | **8.2** | **10** | **10** | Near-Certain | 8 | 24 | 2w | 🟢 |
| T-814 | Multi-region, failover, disaster recovery | STF | 6.7 | 7 | 7 | Moderate | 4 | 2 | 6w | 🔴 |

### D9 · Architecture

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-901** | **Clean / Hexagonal / Ports-and-Adapters** ⭐ | STF | **7.25** | 8 | 8 | High | 4 | 3 | 5w | 🔴 |
| **T-902** | **DDD strategic: bounded contexts, context mapping** ⭐ | STF | **7.4** | 8 | 8 | Moderate | 5 | 3 | 5w | 🔴 |
| T-903 | DDD tactical: aggregates, entities, value objects | STF | 7.25 | 7 | 8 | Moderate | 5 | 4 | 5w | 🔴 |
| T-904 | CQRS: read/write separation | STF | 6.75 | 7 | 7 | Moderate | 3 | 2 | 6w | 🔴 |
| T-905 | Event sourcing & its real costs | ADV | 5.95 | 5 | 6 | Occasional | 4 | 2 | 8w | 🔴 |
| **T-906** | **Event-driven architecture: choreography vs orchestration** ⭐ | STF | **7.5** | 8 | 8 | High | 4 | 3 | 4w | 🔴 |
| **T-907** | **Microservice decomposition & boundary design** ⭐ | STF | **8.4** | **9** | **10** | Very High | 5 | 4 | 3w | 🔴 |
| **T-908** | **Monolith vs microservices: the honest trade-off** ⭐ | STF | **7.9** | 9 | 9 | Very High | 3 | 2 | 4w | 🔴 |
| **T-909** | **Distributed systems failure modes** ⭐ | STF | **8.45** | **9** | **10** | High | 6 | 4 | 3w | 🔴 |
| T-910 | Modular monolith as a deliberate choice | STF | 6.4 | 6 | 7 | Moderate | 2 | 1 | 6w | 🔴 |
| T-911 | API gateway, BFF, edge concerns | COR | 5.9 | 6 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| **T-912** | **Strangler fig, anti-corruption layer, migration patterns** ⭐ | STF | **7.35** | 8 | 8 | Moderate | 3 | 3 | 5w | 🔴 |
| **T-913** | **Technical debt & evolutionary architecture** ⭐ | STF | **7.25** | 8 | 8 | High | 3 | 2 | 5w | 🔴 |
| T-914 | Design patterns applied (GoF in production) | COR | 5.8 | 6 | 6 | Very High | 5 | 4 | 6w | 🟠 |
| T-915 | SOLID at architectural scale | COR | 5.6 | 6 | 6 | Very High | 2 | 2 | 8w | 🟠 |
| T-916 | Architecture Decision Records & documentation | STF | 6.2 | 6 | 7 | Moderate | 2 | 2 | 8w | 🔴 |

### D10 · Cloud & Infrastructure

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-1001 | Containers & image internals | COR | 5.7 | 6 | 6 | Moderate | 3 | 2 | 8w | 🔴 |
| **T-1002** | **Kubernetes objects, scheduling, networking** | ADV | **6.5** | 7 | 7 | Moderate | 6 | 4 | 6w | 🔴 |
| **T-1003** | **K8s resource limits, probes, autoscaling, JVM sizing** ⭐ | ADV | **6.8** | 7 | 8 | Moderate | 4 | 3 | 5w | 🔴 |
| T-1004 | Service mesh & sidecar trade-offs | ADV | 4.9 | 4 | 4 | Occasional | 3 | 1 | 10w | 🔴 |
| T-1005 | Infrastructure as Code fundamentals | COR | 4.8 | 4 | 4 | Occasional | 3 | 2 | 10w | 🔴 |
| T-1006 | AWS core services for backend engineers | COR | 5.6 | 6 | 6 | Moderate | 5 | 3 | 8w | 🟠 |
| T-1007 | Cloud cost & scaling economics | STF | 5.9 | 6 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| T-1008 | 12-factor, config & secrets management | COR | 5.4 | 5 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| T-1009 | CI/CD pipeline design & deployment strategies | COR | 5.8 | 6 | 6 | High | 3 | 2 | 8w | 🟠 |

### D11 · Testing

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-1101** | **Test strategy, pyramid, and what to test** ⭐ | COR | **7.0** | 8 | 8 | High | 3 | 2 | 5w | 🔴 |
| T-1102 | JUnit 5 architecture & advanced features | FDN | 5.0 | 5 | 6 | Moderate | 3 | 3 | 8w | 🔴 |
| **T-1103** | **Mockito, test doubles, and mocking boundaries** ⭐ | COR | **6.4** | 7 | 7 | High | 3 | 3 | 6w | 🔴 |
| **T-1104** | **Integration testing with Testcontainers** ⭐ | ADV | **6.5** | 7 | 8 | Moderate | 4 | 4 | 5w | 🔴 |
| T-1105 | Contract testing for services | STF | 5.7 | 6 | 6 | Occasional | 3 | 2 | 8w | 🔴 |
| T-1106 | Performance & load testing methodology | ADV | 5.9 | 6 | 6 | Moderate | 3 | 2 | 8w | 🔴 |
| T-1107 | Mutation & property-based testing | EXP | 4.3 | 4 | 4 | Rare | 2 | 2 | 12w | 🔴 |
| T-1108 | Writing tests live in an interview | COR | 5.8 | 7 | 6 | Moderate | 1 | 3 | 6w | 🔴 |

### D12 · Performance & Observability

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-1201** | **Performance methodology (USE / RED)** ⭐ | STF | **6.9** | 7 | 8 | Moderate | 3 | 2 | 5w | 🔴 |
| **T-1202** | **Profiling: async-profiler, JFR, flame graphs** ⭐ | ADV | **6.6** | 7 | 8 | Moderate | 4 | 5 | 5w | 🔴 |
| T-1203 | Benchmarking & JMH pitfalls | ADV | 5.2 | 5 | 5 | Occasional | 3 | 3 | 8w | 🔴 |
| **T-1204** | **Latency: percentiles, tail latency, coordinated omission** ⭐ | STF | **6.7** | 7 | 8 | Moderate | 2 | 2 | 5w | 🔴 |
| **T-1205** | **Logging, metrics, tracing & OpenTelemetry** ⭐ | STF | **6.9** | 7 | 8 | High | 4 | 3 | 5w | 🔴 |
| **T-1206** | **SLI, SLO, error budgets** ⭐ | STF | **6.8** | 7 | 8 | Moderate | 2 | 1 | 5w | 🔴 |
| **T-1207** | **Incident response & blameless postmortems** ⭐ | STF | **7.1** | 8 | 8 | High | 3 | 2 | 4w | 🔴 |
| T-1208 | Capacity planning & headroom | STF | 6.1 | 6 | 7 | Moderate | 2 | 2 | 6w | 🔴 |

### D13 · Security

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-1301** | **OWASP Top 10 for backend services** ⭐ | COR | **6.35** | 7 | 7 | High | 4 | 2 | 5w | 🔴 |
| T-1302 | AuthN vs AuthZ, RBAC vs ABAC | COR | 6.0 | 6 | 7 | High | 3 | 2 | 6w | 🔴 |
| **T-1303** | **Applied cryptography: hashing, TLS, signing** ⭐ | ADV | **6.2** | 6 | 7 | Moderate | 4 | 2 | 6w | 🔴 |
| T-1304 | Secrets management & key rotation | ADV | 5.5 | 5 | 6 | Moderate | 2 | 1 | 8w | 🔴 |
| T-1305 | Injection, input validation, output encoding | COR | 5.7 | 6 | 6 | Moderate | 2 | 2 | 8w | 🟠 |
| T-1306 | Supply chain security, SBOM, dependency risk | STF | 5.0 | 5 | 5 | Occasional | 2 | 1 | 10w | 🔴 |
| T-1307 | Multi-tenancy isolation models | STF | 5.6 | 5 | 6 | Occasional | 3 | 1 | 8w | 🔴 |

### D14 · Algorithms & Coding

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| T-1401 | Complexity analysis & amortization | FDN | 6.1 | 7 | 8 | Near-Certain | 3 | 3 | 6w | 🟠 |
| T-1402 | Arrays, two pointers, sliding window | FDN | 6.3 | 8 | 8 | Near-Certain | 4 | 18 | 3w | 🟢 |
| T-1403 | Hashing patterns & frequency maps | FDN | 6.0 | 7 | 7 | Near-Certain | 2 | 12 | 4w | 🟡 |
| T-1404 | Binary search incl. search-on-answer | COR | 5.4 | 7 | 8 | Very High | 3 | 12 | 4w | 🟡 |
| T-1405 | Linked lists & in-place manipulation | COR | 5.0 | 6 | 6 | Very High | 2 | 10 | 5w | 🟡 |
| T-1406 | Stacks & monotonic stack | COR | 5.2 | 6 | 6 | High | 2 | 10 | 5w | 🟡 |
| T-1407 | Heaps, Top-K, k-way merge | COR | 5.5 | 6 | 7 | Very High | 3 | 12 | 4w | 🟠 |
| T-1408 | Trees, BST, traversal patterns | COR | 5.8 | 7 | 7 | Very High | 4 | 16 | 4w | 🟡 |
| **T-1409** | **Graphs: BFS, DFS, topological sort, Dijkstra, Union-Find** ⭐ | COR | **6.25** | 8 | 8 | Very High | 6 | 22 | 3w | 🟡 |
| T-1410 | Backtracking & pruning | COR | 5.1 | 6 | 6 | High | 3 | 14 | 5w | 🟠 |
| **T-1411** | **Dynamic programming: 1D, 2D, knapsack, intervals** ⭐ | ADV | **5.85** | 8 | 8 | Very High | 8 | 32 | 3w | 🟠 |
| T-1412 | Intervals, merging, sweep line | COR | 5.0 | 6 | 6 | High | 2 | 8 | 5w | 🔴 |
| T-1413 | Greedy & the exchange argument | COR | 4.8 | 5 | 6 | High | 3 | 10 | 6w | 🟠 |
| T-1414 | Bit manipulation | COR | 4.4 | 5 | 5 | Moderate | 2 | 6 | 8w | 🟡 |
| T-1415 | Tries & prefix structures | ADV | 4.7 | 5 | 5 | Moderate | 2 | 6 | 8w | 🟡 |
| **T-1416** | **Design-style coding problems (LRU, LFU, iterators)** ⭐ | ADV | **6.2** | 8 | 8 | Very High | 3 | 10 | 4w | 🟠 |
| **T-1417** | **Concurrency coding problems** ⭐ | ADV | **5.75** | 7 | 7 | Moderate | 3 | 8 | 5w | 🔴 |
| T-1418 | Advanced structures: Segment/Fenwick/rolling hash | EXP | 4.2 | 4 | 4 | Occasional | 5 | 8 | 12w | 🟡 |
| T-1419 | Coding interview communication protocol | COR | 6.4 | 8 | 8 | Near-Certain | 2 | 6 | 4w | 🔴 |

### D15 · Behavioral & Leadership

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-1501** | **STAR structure & narrative construction** ⭐ | FDN | **8.0** | 9 | 10 | Near-Certain | 3 | 4 | 3w | 🔴 |
| **T-1502** | **Story portfolio design (12-story matrix)** ⭐ | STF | **8.45** | **10** | **10** | Near-Certain | 4 | 10 | 2w | 🔴 |
| **T-1503** | **Scope, impact & influence narratives (Staff signal)** ⭐ | STF | **7.95** | 9 | 10 | Near-Certain | 3 | 5 | 3w | 🔴 |
| **T-1504** | **Production incident stories** ⭐ | STF | **8.6** | **10** | **10** | Near-Certain | 3 | 4 | 2w | 🔴 |
| **T-1505** | **Architecture decision & trade-off narration** ⭐ | STF | **8.1** | 9 | 10 | Near-Certain | 3 | 4 | 3w | 🔴 |
| **T-1506** | **Conflict & technical disagreement** ⭐ | COR | **7.1** | 9 | 9 | Near-Certain | 2 | 3 | 3w | 🔴 |
| **T-1507** | **Mentoring & growing engineers** ⭐ | STF | **6.9** | 8 | 8 | Very High | 2 | 3 | 4w | 🔴 |
| **T-1508** | **Failure, mistakes, and learning** ⭐ | COR | **7.0** | 8 | 9 | Near-Certain | 2 | 3 | 3w | 🔴 |
| **T-1509** | **Cross-team influence without authority** ⭐ | STF | **7.5** | 8 | 9 | Very High | 2 | 3 | 3w | 🔴 |
| **T-1510** | **Large migrations & long-horizon projects** ⭐ | STF | **7.8** | 8 | 9 | Very High | 3 | 3 | 3w | 🔴 |
| T-1511 | Technical debt advocacy & prioritization | STF | 6.6 | 7 | 7 | High | 2 | 2 | 5w | 🔴 |
| T-1512 | Design reviews, RFCs, written communication | STF | 6.8 | 7 | 8 | High | 3 | 3 | 5w | 🔴 |
| **T-1513** | **Company frameworks (Amazon LPs, etc.)** ⭐ | STF | **7.4** | 9 | 8 | Near-Certain* | 4 | 4 | 3w | 🔴 |
| T-1514 | Questions to ask your interviewer | FDN | 5.4 | 6 | 5 | Near-Certain | 1 | 1 | 8w | 🔴 |
| T-1515 | Offer evaluation & negotiation | FDN | 5.0 | 5 | 5 | Near-Certain | 2 | 2 | — | 🔴 |

*\*Near-Certain at Amazon and Amazon-derived processes; Moderate elsewhere.*

### D16 · Interview Craft

| ID | Topic | Tier | IWI | IP | LP | Freq | S | P | Rev | Gap |
|---|---|---|---|---|---|---|---|---|---|---|
| **T-1601** | **Technical communication protocol** ⭐ | COR | **7.3** | 9 | 9 | Near-Certain | 2 | 5 | 3w | 🔴 |
| **T-1602** | **System design narration & whiteboard discipline** ⭐ | STF | **7.6** | 9 | 9 | Near-Certain | 2 | 8 | 3w | 🔴 |
| **T-1603** | **Mock interviews & self-evaluation rubrics** ⭐ | STF | **7.9** | 9 | 10 | — | 2 | 20 | 2w | 🔴 |
| T-1604 | Company loop structures & calibration | STF | 6.5 | 7 | 7 | — | 3 | 1 | 6w | 🔴 |

---

## 4. Top 25 by Interview Weight Index

These 25 topics — **20% of the register** — account for the large majority of outcome variance in a Senior/Staff loop.

| # | ID | Topic | IWI | Tier | Gap |
|---|---|---|---|---|---|
| 1 | T-801 | Design method: requirements → capacity → API → data → scale | **8.65** | STF | 🔴 |
| 2 | T-1504 | Production incident stories | **8.60** | STF | 🔴 |
| 3 | T-1502 | Story portfolio design | **8.45** | STF | 🔴 |
| 3= | T-804 | Caching strategies & invalidation | **8.45** | STF | 🔴 |
| 3= | T-909 | Distributed systems failure modes | **8.45** | STF | 🔴 |
| 6 | T-907 | Microservice decomposition & boundaries | **8.40** | STF | 🔴 |
| 7 | T-609 | Index structures: B+Tree, composite, covering | **8.30** | ADV | 🔴 |
| 8 | T-813 | Canonical design problems | **8.20** | STF | 🟢 |
| 9 | T-504 | @Transactional semantics & self-invocation | **8.15** | ADV | 🔴 |
| 10 | T-1505 | Architecture decision & trade-off narration | **8.10** | STF | 🔴 |
| 11 | T-1501 | STAR structure & narrative construction | **8.00** | FDN | 🔴 |
| 11= | T-704 | Delivery semantics & exactly-once | **8.00** | ADV | 🔴 |
| 13 | T-611 | Isolation levels & anomalies | **7.95** | ADV | 🔴 |
| 13= | T-1503 | Scope, impact & influence narratives | **7.95** | STF | 🔴 |
| 15 | T-610 | Query planning & EXPLAIN ANALYZE | **7.90** | ADV | 🔴 |
| 15= | T-803 | API design | **7.90** | COR | 🔴 |
| 15= | T-807 | CAP, PACELC, consistency models | **7.90** | STF | 🔴 |
| 15= | T-908 | Monolith vs microservices | **7.90** | STF | 🔴 |
| 15= | T-1603 | Mock interviews & self-evaluation | **7.90** | STF | 🔴 |
| 20 | T-809 | Idempotency & exactly-once at edges | **7.85** | STF | 🔴 |
| 21 | T-1510 | Large migrations & long-horizon projects | **7.80** | STF | 🔴 |
| 22 | T-401 | Java Memory Model & happens-before | **7.75** | ADV | 🔴 |
| 23 | T-505 | Transaction propagation & isolation | **7.70** | ADV | 🔴 |
| 23= | T-806 | Data partitioning & consistent hashing | **7.70** | STF | 🔴 |
| 25 | T-618 | Distributed transactions: Saga, Outbox | **7.65** | STF | 🔴 |

### The uncomfortable observation

**All 25 are marked 🔴 absent.** Not shallow — *absent*. The existing knowledge base has zero coverage of every single one of the twenty-five highest-weight topics in a Senior/Staff Java backend interview.

Meanwhile the topics it *does* cover well — DSA patterns (T-1402, T-1409) and data structure APIs — cluster in the IWI 5.0–6.3 band.

This is the audit's finding restated with numbers attached: **the knowledge base is strongest precisely where interviews are least differentiating, and empty precisely where they are decided.**

Note also the tier composition of the top 25: **15 Staff-Level, 7 Advanced, 2 Core, 1 Foundation.** And note what is *not* there — no Expert-tier topic appears anywhere in the top 25. The highest-scoring Expert topic is T-905 Event Sourcing at 5.95, ranked ~60th.

---

## 5. Topic Dossiers

> Dossiers follow the eight-dimension schema requested. Depth is IWI-proportional: topics ≥ 7.0 receive full treatment; 6.0–6.9 receive condensed treatment in §5.9; below 6.0 are covered by the register and grouped notes.

---

### 5.1 T-801 · Design Method · IWI 8.65 · Staff-Level

**Why it matters.** Every other system design topic is a *component*; this is the *procedure* that assembles them. Candidates fail design rounds far more often from having no method than from missing a component — they jump to a database choice in minute three, never establish scale, and spend forty minutes elaborating an architecture that solves an unstated problem. Method is also what makes a candidate *coachable* under interviewer pressure, which is itself a Staff signal.

**Frequency.** Near-Certain. Two to three dedicated rounds at Senior+; one at minimum.

**Expected depth.** Must drive a 45-minute open-ended problem unprompted: clarify functional and non-functional requirements → establish scale (QPS, storage, read:write ratio) → define the API → design the data model → draw the high-level architecture → identify bottlenecks → deep-dive one or two components at the interviewer's direction → state trade-offs and what you would monitor. Staff candidates additionally surface what they would *not* build, and name the failure modes before being asked.

**Typical follow-ups.**
1. "What breaks first at 10× traffic?"
2. "You have half the budget — what goes?"
3. "How do you migrate to this from an existing system with live users?"
4. "Where is the single point of failure?"
5. "What would you monitor, and what alert would page someone at 3am?"
6. "What's the first thing you'd cut if this had to ship in six weeks?"

**Common misconceptions.**
- That the goal is a *correct* diagram. There is no correct answer; the interviewer is scoring reasoning, prioritization, and trade-off articulation.
- Rushing to microservices. Unprompted decomposition of a problem that doesn't need it reads as pattern-matching, not judgment.
- Treating estimation as ceremony. Numbers must actually *drive* subsequent decisions — otherwise the interviewer sees a memorized ritual.
- Silence while thinking. Unnarrated reasoning is unscored reasoning.

**Production knowledge required.** Realistic latency/throughput figures for common components; awareness that migration from an existing system, not greenfield build, is the normal case; understanding that operability and cost are design constraints, not afterthoughts.

**Dependencies.** Requires T-802 (estimation), T-803 (API design). Substantially strengthened by T-804, T-807, T-909.
**Unlocks.** T-813 (canonical problems), T-1602 (design narration).

**Study order.** Immediately after T-802. This is the spine of Chapter 06 and one of the first three things to build.

---

### 5.2 T-1504 / T-1502 / T-1503 · The Behavioral Core · IWI 8.60 / 8.45 / 7.95 · Staff-Level

*Treated together because they form one system: a story portfolio (T-1502) whose highest-value entries are incident stories (T-1504), scored on scope and influence (T-1503).*

**Why it matters.** This is the most under-weighted cluster in engineering interview prep, and the most common cause of down-levelling at Staff. The technical bar between L5 and L6 is narrower than most candidates assume; the *evidence bar* is not. A Staff offer requires demonstrable scope beyond one team, influence without authority, and judgment under ambiguity — and that evidence arrives exclusively through stories. Phase 1 found **zero behavioral material** in the workspace. For an Amazon-style loop, where behavioral can be ~40% of the decision, this is the largest single risk in the entire register.

**Frequency.** Near-Certain. Every loop. Typically one dedicated round plus 10–15 minutes appended to every technical round.

**Expected depth.** A prepared portfolio of **10–14 stories** mapped to a competency matrix, each rehearsed to a 2-minute core with 4-minute and 30-second variants. Each story must carry: quantified impact, the candidate's *specific* contribution (not the team's), the alternatives considered and rejected, and what they would do differently. Staff-level stories must additionally demonstrate influence across organizational boundaries and decisions made under genuine ambiguity.

**Typical follow-ups.** Behavioral follow-ups are relentless and specifically designed to find the seam between a real story and a constructed one:
1. "What was *your* specific contribution, as opposed to the team's?"
2. "Who disagreed with you, and what was their strongest argument?"
3. "What would you do differently?"
4. "How did you measure that it worked?"
5. "What did it cost — what didn't get done because of this?"
6. "Tell me about a time this approach *failed*."

**Common misconceptions.**
- That behavioral rounds are a formality that technical strength offsets. They are not, and it does not.
- That stories can be improvised. Under time pressure, unrehearsed recall produces meandering answers with no quantified impact — the single most common failure.
- Answering "we" instead of "I." Interviewers are scoring *your* scope; collective phrasing reads as absent ownership.
- Selecting stories by how impressive the *project* was rather than by which competency they *demonstrate*.
- Preparing conflict and failure stories last. They are asked most often and are hardest to construct well.

**Production knowledge required.** Genuine operational history: incidents owned, migrations led, decisions made with incomplete information, engineers mentored. Stories cannot be manufactured — but real experience is routinely under-told, and the handbook's job is extraction and structuring, not invention.

**Dependencies.** T-1501 (STAR) is the prerequisite mechanic. T-1207 (incident response) supplies vocabulary for T-1504. T-1505 draws on the architecture chapters.
**Unlocks.** T-1513 (company frameworks), T-1603 (mock interviews).

**Study order.** **Begin in week one.** This is counter-intuitive and it is the most important sequencing recommendation in the blueprint. Story portfolios need *elapsed time* — memory retrieval, artifact hunting, metric reconstruction, and iterative rewriting cannot be compressed into a final week. Start early, refine continuously.

---

### 5.3 T-804 · Caching Strategies & Invalidation · IWI 8.45 · Staff-Level

**Why it matters.** Caching appears in essentially every system design discussion and most production performance conversations. It also carries unusual *diagnostic* value for interviewers: a candidate who adds "a Redis cache" without discussing invalidation, coherence, or failure behaviour reveals a great deal in one sentence.

**Frequency.** Near-Certain in design rounds; High in backend technical rounds.

**Expected depth.** Cache-aside vs read-through vs write-through vs write-behind, with the failure mode of each. TTL vs explicit invalidation vs versioned keys. Eviction policies and when LRU is wrong. Cache stampede/dogpile and its three mitigations (lock, probabilistic early expiry, stale-while-revalidate). Hot-key problems. Local vs distributed vs multi-tier. What happens when the cache goes down — does the system degrade or collapse?

**Typical follow-ups.**
1. "Cache and database disagree. How did that happen, and how do you detect it?"
2. "Your cache dies at peak. What happens to the database?"
3. "One key gets 40% of traffic. Now what?"
4. "Would you cache this at all? What's the hit-rate threshold that makes it worth the complexity?"
5. "How do you invalidate across regions?"
6. "Write-through or write-behind here — and what do you lose on a crash?"

**Common misconceptions.**
- That caching is an optimization rather than a *consistency decision*. Introducing a cache means accepting staleness; the design question is how much and for how long.
- That TTLs solve invalidation. They bound staleness; they don't provide correctness.
- Ignoring the cold-start/stampede problem entirely — the most common gap.
- Believing a cache improves availability. Naively added, it usually *reduces* it by creating a dependency whose failure produces a thundering herd.

**Production knowledge required.** Redis/Memcached operational behaviour; cache hit ratios that justify the complexity; the observation that a cache which must never be stale is usually the wrong solution; awareness of cache warming during deploys.

**Dependencies.** Needs T-802, T-807 (consistency models). Pairs with T-514 (Spring Cache), T-1204 (tail latency).
**Unlocks.** T-813 — caching appears in at least eight of the twelve canonical problems.

---

### 5.4 T-909 · Distributed Systems Failure Modes · IWI 8.45 · Staff-Level

**Why it matters.** This is the clearest single dividing line between Senior and Staff in a design round. Senior candidates design the happy path competently. Staff candidates design the *failure* path, and do so unprompted. Every distributed system question is ultimately a partial-failure question.

**Frequency.** High as a dedicated topic; Near-Certain as follow-up pressure inside design rounds.

**Expected depth.** The eight fallacies and why each bites. Partial failure and why it's harder than total failure. Network partitions, split-brain, and fencing tokens. Timeouts, retries, and the **retry storm** — including why naive retries convert a slow dependency into an outage. Exponential backoff with jitter. Circuit breakers and bulkheads. Cascading failure and load shedding. Thundering herd. Gray failure — the node that's alive but useless, and why health checks miss it. Clock skew and why timestamps are not ordering.

**Typical follow-ups.**
1. "This service times out. Walk me through what happens to everything upstream."
2. "You added retries and made it worse. Explain why."
3. "How do you know the request failed versus succeeded-slowly? What do you do differently?"
4. "Two nodes both think they're leader. How did that happen and what breaks?"
5. "How do you shed load, and what do you shed first?"
6. "Your health check passes but the node serves errors. How do you detect that?"

**Common misconceptions.**
- That retries improve reliability. Without backoff, jitter, and a budget, they are an amplification mechanism — the single most valuable correction in this topic.
- That timeouts are a tuning detail rather than a core design parameter that must be derived from downstream latency distributions.
- Confusing availability with correctness under partition.
- Assuming exactly-once delivery is achievable at the network layer. It is not; it is achieved at the application layer via idempotency (T-809).

**Production knowledge required.** Having watched a cascading failure; understanding that most outages are triggered by *recovery* behaviour rather than the original fault; familiarity with the retry-budget concept.

**Dependencies.** Requires T-807 (CAP). Deeply coupled to T-515 (resilience patterns), T-809 (idempotency), T-1207 (incident response).
**Unlocks.** The failure-analysis segment of every design problem in T-813; supplies raw material for T-1504.

---

### 5.5 T-907 / T-908 · Microservice Decomposition & the Monolith Trade-off · IWI 8.40 / 7.90 · Staff-Level

**Why it matters.** "How would you split this system?" is the canonical Staff architecture question, and it is a **judgment trap**. The candidate who decomposes enthusiastically fails it. The expected answer weighs organizational structure, transaction boundaries, data ownership, and operational cost — and frequently concludes that decomposition is not warranted. Recognizing when *not* to split is the signal.

**Frequency.** Very High. Near-universal at Staff.

**Expected depth.** Boundaries derived from bounded contexts (T-902), not from technical layers or table structure. Conway's Law and the inverse manoeuvre. Data ownership per service and why shared databases void the entire premise. Distributed transaction avoidance via T-618. Service granularity and the distributed-monolith anti-pattern. Honest accounting of the operational tax: observability, deployment, versioning, on-call burden, and latency inflation from network hops.

**Typical follow-ups.**
1. "Where exactly do you draw the boundary, and what argument supports that line rather than one table over?"
2. "These two services need one transaction. Now what?"
3. "How do you extract this incrementally without a rewrite?" *(→ T-912)*
4. "What did this cost you in latency? In on-call load?"
5. "When would you merge two services back together?"
6. "You have four engineers. Does this architecture still make sense?"

**Common misconceptions.**
- That microservices are a scaling technology. They are primarily an **organizational** technology — independent deployability for independent teams.
- Splitting by technical layer (an "auth service," a "database service") rather than business capability.
- Believing a shared database is an acceptable intermediate step. It reproduces every coupling cost while adding network latency.
- Underestimating operational cost by an order of magnitude.
- Treating the monolith as an embarrassing legacy state rather than a valid, often correct, architecture. **The modular monolith (T-910) is frequently the right Staff answer.**

**Production knowledge required.** Having lived with a decomposition after the migration excitement faded; experience with the distributed-monolith failure state; awareness that most successful decompositions are incremental extractions, never rewrites.

**Dependencies.** Requires T-902, T-903 (DDD). Coupled to T-618, T-909, T-912.
**Unlocks.** T-1510 (migration narratives) — often the same story told from the leadership angle.

---

### 5.6 T-609 / T-610 / T-611 · The Database Triad · IWI 8.30 / 7.90 / 7.95 · Advanced

*Indexing, query planning, and isolation form one competence: the ability to reason about what the database is actually doing.*

**Why it matters.** Phase 1 found 15 generic SQL rows and **zero PostgreSQL-specific content** — while the brief targets PostgreSQL explicitly. This triad is where backend performance problems actually live. It is also unusually *verifiable*: an interviewer can hand you a slow query and a plan, and the answer is either right or it isn't. Few topics discriminate this cleanly.

**Frequency.** Very High. Near-universal for backend roles.

**Expected depth.**
- **T-609:** B+Tree structure and why it's the default; why index order matters and the leftmost-prefix rule; covering indexes and index-only scans; selectivity and why an index on a low-cardinality column is often useless; partial and expression indexes; write amplification as the cost of every index; when a sequential scan is genuinely faster.
- **T-610:** Reading `EXPLAIN ANALYZE`; estimated vs actual rows and what a large divergence means; scan types (seq/index/index-only/bitmap); join algorithms (nested loop/hash/merge) and when the planner picks each; statistics and `ANALYZE`; identifying the actual bottleneck node rather than the largest number.
- **T-611:** The four standard levels and the three anomalies; what PostgreSQL actually implements (no dirty reads even at READ UNCOMMITTED); **Read Committed as the default and why it permits non-repeatable reads and lost updates**; SERIALIZABLE via SSI and its abort behaviour; write skew — the anomaly that surprises nearly everyone; and choosing a level deliberately rather than inheriting a default.

**Typical follow-ups.**
1. "This query takes 8 seconds. Here's the plan. Go."
2. "You added an index and it got slower. Why?"
3. "Why did the planner ignore your index?"
4. "Two transactions read a balance and both write. What happens at each isolation level?"
5. "Explain write skew with a concrete example." *(the discriminating question)*
6. "Composite index on (a, b) — does it help a query filtering only on b? Why not?"
7. "How do you add an index to a 500M-row table in production without downtime?"

**Common misconceptions.**
- That adding indexes is free. Every index is a write-path tax and a planning-cost increase.
- That `(a, b)` and `(b, a)` are interchangeable.
- That REPEATABLE READ prevents all anomalies — write skew survives it, which is precisely why SERIALIZABLE exists.
- That an index guarantees usage; the planner may correctly reject it on selectivity grounds.
- Reading `EXPLAIN` for the biggest cost number rather than for the estimate-vs-actual divergence that indicates stale statistics.

**Production knowledge required.** `CREATE INDEX CONCURRENTLY`; the impact of long transactions on vacuum (→ T-612); connection pool exhaustion under slow queries (→ T-607); ORM-generated query shapes (→ T-602).

**Dependencies.** Requires T-608. Tightly coupled to T-602, T-604, T-607, T-612.
**Unlocks.** T-614 (partitioning), T-616 (migrations), and the data-layer of every T-813 problem.

---

### 5.7 T-504 / T-505 · Spring Transaction Semantics · IWI 8.15 / 7.70 · Advanced

**Why it matters.** The highest-IWI *Spring-specific* topic, and the one most reliably used to separate framework users from framework understanders. `@Transactional` looks like a simple annotation and is in fact a proxy-mediated behaviour with several failure modes that are silent in development and destructive in production. The self-invocation question in particular is near-universal at Senior level.

**Frequency.** Very High for any Spring-based role.

**Expected depth.** How the annotation works mechanically — proxy creation, `TransactionInterceptor`, `PlatformTransactionManager`. **Why calling an `@Transactional` method from within the same class silently does nothing**, and the three ways to fix it. Why the annotation is ineffective on private/final methods under JDK proxies. Default rollback on unchecked exceptions only, and why `rollbackFor` is so often required. All seven propagation modes with a real use case for `REQUIRES_NEW` and `NESTED`. Isolation-level mapping to the database (→ T-611). Transaction boundaries versus HTTP request boundaries. Read-only optimization semantics.

**Typical follow-ups.**
1. "Method A calls method B in the same class; B is `@Transactional`. What happens?" *(the classic)*
2. "Your method throws a checked exception. Did it roll back?"
3. "When would you use `REQUIRES_NEW`, and what's the deadlock risk?"
4. "Where should the transaction boundary sit — controller, service, or repository? Defend it."
5. "You have an HTTP call inside a transaction. What's wrong with that?"
6. "How does `@Transactional` interact with `@Async`? With a virtual thread?"

**Common misconceptions.**
- That `@Transactional` works on self-invocation. **It does not** — the proxy is bypassed entirely.
- That all exceptions trigger rollback. Only unchecked ones do, by default.
- That `readOnly = true` is merely a hint — it has real effects on flush mode and can be propagated as a database hint.
- Holding transactions open across network calls, converting a slow dependency into connection-pool exhaustion (→ T-607).
- Believing propagation is an obscure feature. `REQUIRES_NEW` for audit logging that must survive rollback is a routine production requirement.

**Production knowledge required.** Connection-pool starvation from long transactions; long-running transactions blocking vacuum (→ T-612); why transaction scope should be the narrowest unit of business atomicity.

**Dependencies.** Requires T-503 (AOP/proxies) — the self-invocation behaviour is *unexplainable* without it. Coupled to T-611, T-607, T-601.
**Unlocks.** T-618 (Saga/Outbox — motivated by the limits of local transactions).

---

### 5.8 T-704 / T-702 / T-703 / T-705 · Kafka Semantics Cluster · IWI 8.00 / 7.40 / 7.50 / 7.55 · Advanced

**Why it matters.** Phase 1 found 15 Kafka rows averaging ~117 characters — pure API vocabulary. Kafka interview value is almost entirely in *semantics under failure*, and none of it was present. These four topics are effectively one competence: what guarantees exist, what they cost, and where they break.

**Frequency.** High for event-driven roles; a standard deep-dive at Senior+.

**Expected depth.** `acks=0/1/all` and why `acks=all` **alone** does not prevent loss without `min.insync.replicas ≥ 2`. ISR mechanics and shrinkage. Unclean leader election as an availability-for-durability trade. Idempotent producers (sequence numbers, producer epoch) and transactional producers. Consumer group protocol, rebalance triggers, and the stop-the-world cost of eager rebalancing versus cooperative-incremental. Offset commit strategies and the precise at-least-once vs at-most-once boundary. **Why end-to-end exactly-once requires either the transactional read-process-write loop within Kafka, or idempotent consumers at the boundary (→ T-809).** Partition key selection: ordering is per-partition only; key skew creates hot partitions; changing partition count breaks key-to-partition mapping permanently.

**Typical follow-ups.**
1. "`acks=all` and you still lost a message. How?"
2. "Guarantee ordering for a given customer. Now what happens when you scale partitions?"
3. "Consumer crashes after processing but before committing. What happens, and how do you make that safe?"
4. "Your consumer group rebalances every 30 seconds. Diagnose it."
5. "Is exactly-once real? Explain precisely what Kafka provides and what it doesn't."
6. "One partition holds 60% of the traffic. Fix it."
7. "Consumer lag is growing linearly. Walk me through your response."

**Common misconceptions.**
- That Kafka guarantees global ordering. **Ordering is per-partition only** — the most consequential misunderstanding in the topic.
- That `acks=all` alone guarantees durability; it does not without `min.insync.replicas`.
- That Kafka's exactly-once is end-to-end. It covers Kafka-to-Kafka processing; it does not extend to an external database write without additional work.
- Believing more partitions is always better — they cost file handles, memory, rebalance time, and end-to-end latency.
- Treating consumer lag as a metric to watch rather than as an SLO to design against.

**Production knowledge required.** Rebalance storms from `max.poll.interval.ms` violations; DLQ design and poison-message handling; consumer-lag alerting; the operational reality that partition count is effectively immutable for keyed topics.

**Dependencies.** Requires T-701. Deeply coupled to T-809, T-618, T-909.
**Unlocks.** T-708, T-710, and the async backbone of several T-813 problems.

---

### 5.9 Condensed Dossiers · IWI 7.0 – 7.6

| ID · Topic · IWI | Depth expected · Key follow-ups · Dominant misconception |
|---|---|
| **T-401 · JMM & happens-before · 7.75** | Program order vs synchronization order; the six happens-before rules; safe publication; why DCL needs `volatile`; final-field freeze. **FU:** "Why does DCL break without volatile?" · "Is this code data-race free?" **Misconception:** volatile is about caching. It is about *ordering*; cache coherence is a hardware detail below the model. |
| **T-806 · Partitioning & consistent hashing · 7.70** | Range vs hash vs directory; hotspots; consistent hashing with virtual nodes; rebalancing cost; why a naive `hash % N` is catastrophic on resize. **FU:** "Add a node — how much data moves?" · "Your shard key is the timestamp. What breaks?" **Misconception:** that resharding is a routine operation. |
| **T-618 · Saga, Outbox, 2PC · 7.65** | Why 2PC is avoided; orchestration vs choreography sagas; compensating actions; the transactional outbox and why dual-write is unsafe; CDC-based outbox. **FU:** "Compensate a charged payment." · "You wrote to the DB and published to Kafka. Prove no message is lost." **Misconception:** that a DB write plus a message publish can be made atomic without an outbox. |
| **T-515 · Resilience patterns · 7.60** | Timeout selection from latency percentiles; retry budgets, backoff, jitter; circuit-breaker states; bulkhead isolation; graceful degradation. **FU:** "Set the timeout — from what data?" · "Circuit opens. What does the user see?" **Misconception:** retry-until-success as a reliability strategy. |
| **T-808 · Rate limiting · 7.60** | Token bucket, leaky bucket, fixed and sliding window; distributed enforcement and its coordination cost; per-user vs per-tenant vs global; 429 semantics and `Retry-After`. **FU:** "Enforce this across 50 instances." · "Fixed windows let 2× through. Where?" **Misconception:** that local per-instance limiting approximates a global limit. |
| **T-614 · Partitioning & sharding (DB) · 7.60** | Declarative partitioning; partition pruning; shard-key selection and its irreversibility; cross-shard queries and joins; rebalancing. **FU:** "Chose the wrong shard key. Recovery plan?" **Misconception:** that sharding is a performance tweak rather than a permanent architectural commitment. |
| **T-1602 · Design narration · 7.60** | Structured verbal walkthrough; whiteboard/diagram discipline; signposting; explicit trade-off statements; handling redirection without defensiveness. **FU:** *(implicit — the interviewer's redirections are the test)* **Misconception:** that thinking silently and presenting a finished answer is stronger. It is unscorable. |
| **T-1509 · Cross-team influence · 7.50** | Evidence of change driven outside your reporting line; building consensus; navigating disagreement with peers and seniors. **FU:** "Who resisted, and how did you handle it?" **Misconception:** that influence means winning arguments rather than changing outcomes. |
| **T-906 · EDA: choreography vs orchestration · 7.50** | Event notification vs event-carried state transfer vs event sourcing; coupling analysis; debuggability trade-off. **FU:** "Trace a request across seven services." **Misconception:** that events automatically decouple — they relocate coupling into schema and ordering. |
| **T-902 · DDD strategic · 7.40** | Bounded contexts; ubiquitous language; context maps; conformist/ACL/shared-kernel relationships. **FU:** "Two teams disagree on what 'Order' means. Resolve it." **Misconception:** that DDD is tactical patterns. The strategic half is what interviews reward. |
| **T-802 · Estimation & latency numbers · 7.40** | QPS from DAU; storage growth; bandwidth; the latency table; powers of two. **FU:** "Justify that number." **Misconception:** that precision matters. Order of magnitude and stated assumptions are what count. |
| **T-1513 · Company frameworks · 7.40** | Amazon's 16 LPs and the two-story-per-principle expectation; bar-raiser calibration; equivalents elsewhere. **FU:** *(LP-tagged probes throughout the loop)* **Misconception:** that generic strong stories map cleanly onto LPs without deliberate re-framing. |
| **T-306 · GC tuning & log analysis · 7.35** | Reading unified GC logs; allocation rate; promotion failure; humongous allocations; pause-time goals; throughput vs latency; sizing under containers. **FU:** "Pauses hit 4s. Diagnose from this log." **Misconception:** that tuning means increasing heap size. |
| **T-912 · Strangler fig & ACL · 7.35** | Incremental extraction; facade routing; anti-corruption layers; dual-write and backfill; cutover and rollback. **FU:** "How do you roll back mid-migration?" **Misconception:** that a rewrite is faster. |
| **T-1601 · Communication protocol · 7.30** | Restating the problem; stating assumptions; thinking aloud; asking before assuming; taking hints gracefully. **Misconception:** that speed signals competence. Clarity does. |
| **T-616 · Zero-downtime migration · 7.30** | Expand-contract; backward-compatible schema changes; dual writes; backfill; `CONCURRENTLY`; feature-flagged cutover. **FU:** "Rename a column on a live 200M-row table." **Misconception:** that a maintenance window is available. |
| **T-901 · Clean/Hexagonal · 7.25** | Dependency inversion at architectural scale; ports and adapters; domain isolation from framework and persistence; testability payoff. **FU:** "Where do JPA entities live, and why?" **Misconception:** that it means a specific folder layout. |
| **T-903 · DDD tactical · 7.25** | Aggregate boundaries as *transaction* boundaries; invariant enforcement; value objects; repositories per aggregate root. **FU:** "Why is this aggregate too large?" **Misconception:** that aggregates are just entity clusters. |
| **T-913 · Technical debt & evolutionary architecture · 7.25** | Framing debt in delivery-risk terms; fitness functions; incremental modernization; making the business case. **FU:** "Sell this refactor to a sceptical PM." **Misconception:** that debt is a code-quality argument rather than an economic one. |
| **T-602 · N+1 & fetch strategies · 7.20** | LAZY vs EAGER; join fetch; entity graphs; batch fetching; the DTO projection escape hatch; detecting N+1 in tests. **FU:** "Your endpoint fires 400 queries. Fix it without breaking lazy loading elsewhere." **Misconception:** that EAGER solves N+1 — it relocates it and usually worsens it. |
| **T-707 · Consumer lag & DLQ · 7.20** | Lag as an SLO; scaling consumers vs partitions; poison messages; DLQ with replay; ordering loss on retry. **FU:** "One bad message blocks the partition. Options?" **Misconception:** that adding consumers beyond partition count helps. |
| **T-406 · Executors & pool sizing · 7.15** | Pool types and their queue behaviours; the unbounded-queue trap in `newFixedThreadPool`; sizing from Little's Law; rejection policies; separating CPU-bound from IO-bound pools; shutdown semantics. **FU:** "Size this pool. Show the arithmetic." · "Queue is unbounded and memory is climbing. Why?" **Misconception:** that bigger pools are faster. |
| **T-512 · OAuth2 / OIDC · 7.15** | Authorization Code + PKCE as the default; why implicit is dead; client credentials for service-to-service; token introspection vs local validation; refresh rotation; OIDC vs OAuth2 distinction. **FU:** "Why PKCE if you already have a client secret?" **Misconception:** conflating authentication with authorization. |
| **T-604 · Optimistic vs pessimistic locking · 7.10** | `@Version` mechanics; `OptimisticLockException` handling and retry; `PESSIMISTIC_READ/WRITE`; lost-update prevention; choosing by contention profile. **FU:** "Two users edit the same record. Walk both strategies." **Misconception:** that optimistic locking prevents conflicts — it *detects* them. |
| **T-1207 · Incident response & postmortems · 7.10** | Detection, triage, mitigation-before-diagnosis, comms; blameless analysis; contributing factors over root cause; action-item follow-through. **FU:** "Mitigate or diagnose first? Defend it." **Misconception:** that postmortems identify a single root cause. |
| **T-1506 · Conflict & disagreement · 7.10** | Disagreement with peers, seniors, and non-engineers; disagree-and-commit; escalation judgment; changing your own mind. **FU:** "A time you were wrong." **Misconception:** that the story should end in your being right. Stories where the candidate updated their view often score higher. |
| **T-1101 · Test strategy · 7.00** | What to test at each level; the ice-cream-cone anti-pattern; testing behaviour not implementation; flakiness as a design signal; coverage as a diagnostic not a target. **FU:** "Where do you draw the unit/integration line?" **Misconception:** that coverage percentage measures quality. |
| **T-1508 · Failure & learning · 7.00** | A real failure with real consequences, owned without deflection, with concrete behavioural change afterwards. **FU:** "What would you do differently?" **Misconception:** choosing a trivially safe failure. It reads as evasion. |
| **T-511 · Spring Security filter chain · 7.00** | Filter ordering; `SecurityFilterChain` lambda DSL; authentication vs authorization phases; `SecurityContext` propagation and its interaction with async and virtual threads; method security. **FU:** "Request 403s and you don't know why. Debug it." **Misconception:** treating the chain as configuration rather than an ordered pipeline. |
| **T-513 · JWT design · 7.00** | Signature algorithms and the `alg: none` / algorithm-confusion attacks; claims design; **why JWTs cannot be revoked** and the mitigations (short TTL, refresh rotation, denylist); why symmetric keys fail across services; where to store tokens. **FU:** "Log a user out immediately. Go." **Misconception:** that JWTs are a session mechanism. |

---

### 5.10 Grouped Notes · IWI 5.0 – 6.9

Registry entries above carry the quantitative fields. Qualitative notes are grouped by domain.

**Java Core & Collections (T-101…T-209).** Foundation mechanics: assumed, rarely credited, occasionally fatal. **T-201 HashMap internals is the exception and belongs in the top tier of study despite its Foundation classification** — hash spreading, load factor, resize, treeification at 8/untreeification at 6, and the `equals`/`hashCode` contract. Phase 1 confirmed this is absent while being the most-asked Java data structure question. Streams (T-107) attract more follow-ups than expected: laziness, short-circuiting, when parallel streams *hurt*, custom collectors. Generics (T-104) is where erasure, PECS, and variance separate depth from familiarity.

**JVM (T-301…T-312).** Reframe from trivia to diagnosis. The valuable framing is not "name the GC algorithms" but "here is a latency graph and a GC log — what happened?" T-306 and T-307 are the highest-value entries because they are *demonstrable skills*. T-312 (container ergonomics) is quietly important and widely missed: JVMs sized against host rather than cgroup limits remain a common production failure.

**Concurrency below the top tier (T-402…T-416).** T-402/403/409 are the frequently-asked trio. T-410 Virtual Threads has risen sharply and now appears in most 2026-era Senior Java loops — pinning, `synchronized` interaction, why pooling virtual threads is an anti-pattern, and what actually changes for IO-bound workloads. T-414/415/416 are Expert-tier: recognition-level only.

**Spring below the top tier (T-501…T-517).** T-501/503/506 form the "do you understand the framework or use it" cluster. T-509 WebFlux is role-dependent — deep only if the target stack is reactive; note that Virtual Threads have reduced its strategic relevance for most workloads.

**Cloud & Infrastructure (T-1001…T-1009).** Rarely a primary evaluation axis for Java backend roles; commonly a supporting axis in design rounds. T-1003 is the highest-value entry: heap sizing against container limits, probe semantics, and why an OOMKill differs from an `OutOfMemoryError`.

**Security (T-1301…T-1307).** T-1301 and T-1303 carry the weight. Depth expectation is "can reason about it correctly," not specialist. Security questions usually arrive embedded in design rounds rather than standalone.

**Algorithms (T-1401…T-1419).** The best-covered domain in the existing workspace (Phase 1: 🟢). Priority here is **practice volume, not new study** — hence the high P values and modest S values throughout. T-1409 Graphs and T-1411 DP carry the most weight; T-1416 design problems and T-1417 concurrency problems are under-practiced relative to their frequency. T-1419 (communication during coding) is absent and disproportionately valuable: solving with poor narration scores below solving less with clear narration.

---

## 6. Dependency Graph & Critical Path

### 6.1 Domain-level dependency structure

```
                      ┌─────────────────────────────┐
                      │  D15 BEHAVIORAL  (parallel) │  ← start week 1, runs throughout
                      └─────────────────────────────┘

  D1 Java Core ──┬──► D2 Collections ──► D4 Concurrency ──┐
                 │                                          │
                 └──► D3 JVM ─────────────────────────────┤
                                                            ▼
  D6 SQL/Index ──┬──► D6 Transactions ──► D5 Spring TX ──► D8 SYSTEM DESIGN
                 │                                            ▲   ▲
                 └──► D6 Distributed Data ───────────────────┘   │
                                                                 │
  D5 Spring Core ──► D5 Security ──────────────────────────────┤
                                                                 │
  D7 Kafka ────────► D7 Semantics ─────────────────────────────┤
                                                                 │
  D9 Architecture ─────────────────────────────────────────────┘
         │
         └──► D9 Trade-offs ──► D16 Mock Interviews ◄── everything

  D14 Algorithms ──────────────────────────────────────► (independent track)
  D11 Testing · D12 Observability · D13 Security ──────► (supporting, weave in)
```

### 6.2 The three parallel tracks

The most important structural insight in this blueprint: **these are three independent tracks that must run concurrently, not sequentially.**

| Track | Content | Cadence | Why parallel |
|---|---|---|---|
| **A · Knowledge** | D1–D13 | Blocks of focused study | Sequential dependencies within; benefits from depth sessions |
| **B · Coding** | D14 | Daily, small doses | Skill acquisition requires distributed practice; cramming does not work |
| **C · Behavioral** | D15 | Weekly, continuous | Requires *elapsed time* for memory retrieval and iterative refinement |

Running these sequentially is the single most common preparation failure. Track C in particular cannot be compressed — a story portfolio built in the final week is visibly a story portfolio built in the final week.

### 6.3 Critical-path chains

Chains where order is genuinely forced:

```
T-503 (AOP/proxies) → T-504 (@Transactional) → T-505 (propagation) → T-618 (Saga/Outbox)
    The self-invocation behaviour is unexplainable without proxy mechanics.

T-608 (SQL) → T-609 (indexes) → T-610 (query plans) → T-614 (partitioning) → T-806 (sharding)
    Each step is meaningless without the prior.

T-401 (JMM) → T-402 (volatile) → T-405 (CAS) → T-414 (lock-free)
    Memory model before any lock-free reasoning.

T-802 (estimation) → T-801 (design method) → T-813 (canonical problems) → T-1602 (narration)
    Method before problems; problems before narration practice.

T-1501 (STAR) → T-1502 (portfolio) → T-1504/1505 (incident & architecture stories) → T-1513 (frameworks)
    Structure before content before company-specific mapping.

T-701 (Kafka basics) → T-702/703 (producer/consumer) → T-704 (delivery semantics) → T-809 (idempotency)
```

### 6.4 Hub topics

Topics with the highest downstream connectivity — weakness here propagates:

| Topic | Feeds into | Effect if weak |
|---|---|---|
| **T-909 Distributed failure modes** | 11 topics | Every design answer stays on the happy path |
| **T-807 CAP / consistency** | 9 topics | Data-layer trade-offs cannot be articulated |
| **T-801 Design method** | 8 topics | Design rounds lack structure regardless of knowledge |
| **T-503 Spring AOP/proxies** | 6 topics | The entire Spring transaction and security model is opaque |
| **T-609 Index structures** | 6 topics | All database performance reasoning is blocked |
| **T-1502 Story portfolio** | 8 behavioral topics | Every behavioral answer is improvised |

---

## 7. Effort Model

### 7.1 Totals by domain

| Domain | Study (h) | Practice (h) | Total | Share |
|---|---|---|---|---|
| D1 Java Core | 47 | 23 | 70 | 6.4% |
| D2 Collections | 22 | 15 | 37 | 3.4% |
| D3 JVM | 41 | 24 | 65 | 5.9% |
| D4 Concurrency | 51 | 33 | 84 | 7.7% |
| D5 Spring | 62 | 38 | 100 | 9.1% |
| D6 Persistence & DB | 74 | 53 | 127 | 11.6% |
| D7 Kafka | 33 | 23 | 56 | 5.1% |
| D8 System Design | 55 | 69 | 124 | 11.3% |
| D9 Architecture | 55 | 39 | 94 | 8.6% |
| D10 Cloud & Infra | 30 | 19 | 49 | 4.5% |
| D11 Testing | 22 | 22 | 44 | 4.0% |
| D12 Performance & Obs | 22 | 18 | 40 | 3.6% |
| D13 Security | 20 | 11 | 31 | 2.8% |
| D14 Algorithms | 62 | 224 | 286 | 26.1%¹ |
| D15 Behavioral | 36 | 52 | 88 | 8.0% |
| D16 Interview Craft | 9 | 34 | 43 | 3.9% |
| **Total** | **641** | **697** | **1,338** | |

*¹ D14's share is inflated by practice hours — 224 of its 286 hours are problem-solving repetitions, which run as a daily background track rather than competing with study blocks.*

### 7.2 Realistic scenarios

The 1,338h figure is the **complete register including Expert-tier and long-tail topics**. Nobody should study all of it. Realistic paths:

| Path | Scope | Hours | At 12h/wk | At 20h/wk |
|---|---|---|---|---|
| **Sprint** | Top 25 IWI + coding refresh | ~260 | 22 wks | 13 wks |
| **Senior-ready** | IWI ≥ 6.5, full behavioral, 150 problems | ~610 | 51 wks | 31 wks |
| **Staff-ready** | IWI ≥ 6.0, all Staff-Level, 200 problems | ~880 | 73 wks | 44 wks |
| **Complete** | Entire register | 1,338 | — | — |

**Recommended target: Senior-ready (~610h).** It captures every top-25 topic, all Staff-Level architecture and behavioral content, and a defensible coding volume, while omitting the Expert tier and low-IWI long tail. Phase 3 will convert this into a dated weekly roadmap.

---

## 8. Return-on-Study Preview — Top 20

Ranked by `RoS = (IWI × GapSeverity) / StudyHours`. **This ordering differs sharply from the IWI ranking** and is what Phase 3's roadmap will follow. High-RoS topics are high-value *and* cheap — the correct starting set.

| # | ID | Topic | IWI | Gap | S | **RoS** |
|---|---|---|---|---|---|---|
| 1 | T-1504 | Production incident stories | 8.60 | 2.0 | 3 | **5.73** |
| 2 | T-1501 | STAR structure | 8.00 | 2.0 | 3 | **5.33** |
| 3 | T-1505 | Architecture decision narration | 8.10 | 2.0 | 3 | **5.40** |
| 4 | T-1503 | Scope & influence narratives | 7.95 | 2.0 | 3 | **5.30** |
| 5 | T-809 | Idempotency at edges | 7.85 | 2.0 | 3 | **5.23** |
| 6 | T-908 | Monolith vs microservices | 7.90 | 2.0 | 3 | **5.27** |
| 7 | T-1509 | Cross-team influence | 7.50 | 2.0 | 2 | **7.50** |
| 8 | T-1506 | Conflict & disagreement | 7.10 | 2.0 | 2 | **7.10** |
| 9 | T-1508 | Failure & learning | 7.00 | 2.0 | 2 | **7.00** |
| 10 | T-1601 | Communication protocol | 7.30 | 2.0 | 2 | **7.30** |
| 11 | T-1602 | Design narration | 7.60 | 2.0 | 2 | **7.60** |
| 12 | T-1603 | Mock interviews | 7.90 | 2.0 | 2 | **7.90** |
| 13 | T-1204 | Latency & percentiles | 6.70 | 2.0 | 2 | **6.70** |
| 14 | T-1206 | SLI/SLO/error budgets | 6.80 | 2.0 | 2 | **6.80** |
| 15 | T-808 | Rate limiting | 7.60 | 2.0 | 3 | **5.07** |
| 16 | T-705 | Partition key design | 7.55 | 2.0 | 3 | **5.03** |
| 17 | T-702 | Producer semantics | 7.40 | 2.0 | 3 | **4.93** |
| 18 | T-604 | Optimistic vs pessimistic locking | 7.10 | 2.0 | 3 | **4.73** |
| 19 | T-1207 | Incident response | 7.10 | 2.0 | 3 | **4.73** |
| 20 | T-802 | Estimation & latency numbers | 7.40 | 2.0 | 3 | **4.93** |

**The pattern is striking and actionable.** Twelve of the top twenty are behavioral or interview-craft topics — high weight, total absence, and *low study cost*. They are cheap because they require structuring existing experience rather than acquiring new knowledge.

This means the **highest-return opening move is not technical study at all.** It is building the story portfolio and the communication protocol. That is a genuinely counter-intuitive conclusion, it falls directly out of the data, and it inverts how almost every engineer sequences interview preparation.

The heavyweight technical topics — T-609 indexes (6h), T-401 JMM (8h), T-813 design problems (8h) — carry enormous IWI but lower RoS purely because they are expensive. They are unskippable; they are simply not where week one should go.

---

## 9. Complete Table of Contents

### 9.1 Structure

```
Java-Interview-Handbook/
│
├── README.md
├── 00-Roadmap.md
│
├── 01-Java-Core/
├── 02-Collections/
├── 03-JVM/
├── 04-Concurrency/
├── 05-Spring/
├── 06-SystemDesign/
├── 07-Architecture/
├── 08-Database/
├── 09-Kafka/
├── 10-Cloud/
├── 11-Testing/
├── 12-Performance/
├── 13-Security/
├── 14-Behavioral/
├── 15-LeetCode/
├── 16-MockInterviews/
├── 17-CheatSheets/
└── Resources/
```

*The brief's structure is retained in full. Two additions are proposed at the end of §9.2 — an errata file and a cross-reference index — both of which follow directly from Phase 1 findings.*

### 9.2 Chapter rationale and connections

---

**`README.md` — Orientation**
*Why it exists:* A handbook of this size fails if the reader starts at page one and reads forward. This file supplies three entry points — by role target (Senior vs Staff), by timeline (6 weeks vs 6 months), and by weakness (diagnostic self-assessment) — plus the IWI methodology so the reader understands why chapters are weighted unevenly.
*Connects to:* Every chapter. Routes readers into `00-Roadmap.md`.

---

**`00-Roadmap.md` — Sequencing**
*Why it exists:* The register is not a study order. This file converts the dependency graph (§6) and RoS ranking (§8) into a dated, three-track weekly plan. It carries the blueprint's most important instruction: **the three tracks run in parallel.**
*Connects to:* Ordering authority for all chapters. Produced in Phase 3.

---

**`01-Java-Core/` — Language Mechanics** · 16 topics · 70h · avg IWI 4.9
*Why it exists:* Foundation tier. Rarely wins an interview; reliably loses one. Its real function is enabling later chapters — generics and functional interfaces underpin Ch. 04; reflection and proxies underpin Ch. 05.
*Rewrite note:* Absorbs and replaces the OOP/Java Core/Exceptions rows from the Notion base, rewritten from ~110-character answers to full concept dossiers.
*Connects to:* → 02 (collections build on `equals`/`hashCode`), → 04 (functional interfaces), → 05 (reflection → proxies → AOP).

---

**`02-Collections/`** · 9 topics · 37h · avg IWI 5.7
*Why it exists:* Contains **T-201 HashMap internals (IWI 7.4, IP 10)** — the highest-frequency single Java question and entirely absent from the current base. Also corrects the Notion guide's inverted Set hierarchy and the `NavigableSet`-as-implementation error.
*Salvage:* Inherits and corrects the Map/Set variant tables — the strongest existing material.
*Connects to:* ← 01 (contracts), → 03 (collection memory behaviour), → 04 (concurrent collections), → 15 (structure selection under time pressure).

---

**`03-JVM/`** · 12 topics · 65h · avg IWI 5.7
*Why it exists:* The Notion base has **one JVM row**. This chapter reframes the JVM from trivia to diagnosis: reading GC logs, analyzing heap dumps, sizing under container limits. Its centre of gravity is T-306 and T-307 — demonstrable skills, not recitable facts.
*Connects to:* ← 02 (object layout), → 04 (memory model shares foundations), → 12 (profiling), → 10 (container ergonomics).

---

**`04-Concurrency/`** · 16 topics · 84h · avg IWI 5.5
*Why it exists:* Contains **T-401 JMM (IWI 7.75)**, the deepest single technical topic in the handbook, and corrects two verified Phase 1 errors — the invented "Running" thread state with missing `TIMED_WAITING`, and the caching-based `volatile` model. Adds Virtual Threads, now standard in 2026-era Java loops.
*Connects to:* ← 03 (memory model), → 05 (async, `SecurityContext` propagation, transactions on virtual threads), → 15 (concurrency coding problems), → 12 (contention profiling).

---

**`05-Spring/`** · 17 topics · 100h · avg IWI 6.1
*Why it exists:* Highest-volume framework chapter, anchored by **T-504 `@Transactional` (IWI 8.15)** — the single highest-IWI framework topic. Deliberately ordered so proxy mechanics (T-503) precede transactions (T-504), because the self-invocation behaviour is unexplainable otherwise.
*Rewrite note:* Replaces 32 shallow Spring rows entirely.
*Connects to:* ← 01 (reflection/proxies), → 08 (transactions bridge into isolation), → 13 (security chain), → 11 (test slices), → 07 (Spring as the implementation of hexagonal boundaries).

---

**`06-SystemDesign/`** · 14 topics · 124h · avg IWI 7.4 — **highest-weight chapter**
*Why it exists:* Zero coverage at time of writing; two to three loop rounds. Structured as method (T-801) → components (T-802…T-812) → **twelve worked canonical problems (T-813)**. The method precedes the components deliberately: candidates fail from absent procedure more often than from absent components. *Status update, 2026-09-01:* T-813's Gap marker above is now 🟢 — the Architecture Atlas reached exactly 12 classic full-system-design entries, matching this row's stated count. This single cell was verified and updated directly; the rest of this register's Gap column predates this session's closure work across all 16 domains and was never swept for accuracy — treat `CHANGELOG.md`'s `### Planned` section as the current source of truth for what is actually closed, not this table.
*Connects to:* ← 08 (data layer), ← 09 (async backbone), ← 07 (decomposition), → 16 (mock design rounds), → 14 (design decisions become architecture stories).

---

**`07-Architecture/`** · 16 topics · 94h · avg IWI 7.0
*Why it exists:* The Staff-differentiating chapter. Where Ch. 06 asks "how would you build it," Ch. 07 asks "should you, and what does it cost." Contains the register's judgment traps — T-907/T-908, where the expected answer is frequently *don't decompose*.
*Connects to:* ← 06 (design vocabulary), ← 08 (transaction boundaries constrain service boundaries), → 14 (architecture decisions are the strongest Staff stories), → 16.

---

**`08-Database/`** · 18 topics · 127h · avg IWI 6.8 — **largest technical chapter**
*Why it exists:* Highest concentration of top-25 topics of any technical chapter (T-609, T-610, T-611 all ≥ 7.9). Phase 1 found 15 generic SQL rows and **no PostgreSQL-specific content** against a PostgreSQL-targeted brief. Spans ORM behaviour → SQL → indexing → planning → isolation → MVCC → distribution.
*Connects to:* ← 05 (`@Transactional` → isolation), → 06 (storage selection, sharding), → 07 (data ownership per service), → 12 (query performance).

---

**`09-Kafka/`** · 10 topics · 56h · avg IWI 6.7
*Why it exists:* Existing coverage is 15 rows of API vocabulary averaging 117 characters. Rebuilt around **semantics under failure** — the four-topic delivery cluster (T-702…T-705) carries the chapter.
*Connects to:* ← 08 (outbox pattern), → 06 (async design), → 07 (event-driven architecture), → 12 (lag as an SLO).

---

**`10-Cloud/`** · 9 topics · 49h · avg IWI 5.5
*Why it exists:* Deliberately **de-scoped**. Existing AWS content is certification trivia; this chapter targets only what backend engineers are actually asked. T-1003 (JVM sizing under cgroup limits) is the highest-value entry.
*Connects to:* ← 03 (container ergonomics), → 12 (deployment observability), → 06 (infrastructure constraints on design).

---

**`11-Testing/`** · 8 topics · 44h · avg IWI 5.8
*Why it exists:* Entirely absent, yet testing philosophy is a routine Senior discussion and live test-writing appears in some loops. Emphasis on *strategy* (T-1101) over framework syntax.
*Connects to:* ← 05 (Spring test slices), ← 08 (Testcontainers), → 16 (live TDD segments), → 14 (quality-advocacy stories).

---

**`12-Performance/`** · 8 topics · 40h · avg IWI 6.4
*Why it exists:* Absent. Supplies the *methodology* that turns Ch. 03 and Ch. 08 knowledge into diagnostic capability, and the vocabulary — percentiles, SLOs, error budgets — that makes Ch. 14 incident stories credible.
*Connects to:* ← 03 (GC/heap analysis), ← 08 (query performance), → 14 (T-1207 incident response directly feeds T-1504 incident stories), → 06 (latency budgets in design).

---

**`13-Security/`** · 7 topics · 31h · avg IWI 5.8
*Why it exists:* Scoped to "can reason correctly," not specialist depth. Security typically arrives embedded in design rounds. Also the home of the Phase 1 hygiene finding — the embedded JWT in a workspace link becomes a worked teaching example.
*Connects to:* ← 05 (Spring Security, OAuth2, JWT implementation), → 06 (security in design), → 07 (multi-tenancy isolation).

---

**`14-Behavioral/`** · 15 topics · 88h · avg IWI 7.2 — **highest-RoS chapter**
*Why it exists:* Zero existing coverage against a topic cluster holding three of the top ten IWI scores and **twelve of the top twenty RoS scores**. This is the largest single risk in the register and simultaneously the cheapest to close.
*Structural note:* Built as a **portfolio system**, not a question list — a competency matrix, 10–14 stories, three length variants each, mapped to company frameworks.
*Connects to:* ← 12 (incident vocabulary), ← 07 (architecture decisions), ← everything (technical work supplies story content), → 16 (behavioral mocks).
*Scheduling note:* **Begins week one and runs continuously.** Cannot be compressed.

---

**`15-LeetCode/`** · 19 topics · 286h · avg IWI 5.5
*Why it exists:* Highest *practice* hours, modest study hours — this is a repetition track, not a reading track. Phase 1 found **4 problems, none in Java**; target is 150–250 solved in Java with written retrospectives.
*Salvage:* Inherits the 23-pattern taxonomy from the DSA guide — the strongest existing asset — **with its seven verified code defects corrected**.
*Structural note:* Includes the practice-log specification (harvested from the Notion tracker schema) and T-1419 communication-during-coding, which is absent and disproportionately valuable.
*Connects to:* ← 02 (structure selection), ← 04 (concurrency problems), → 16 (coding mocks).

---

**`16-MockInterviews/`** · 4 topics · 43h · avg IWI 7.4
*Why it exists:* The integration layer. Every other chapter builds a component; this one tests the assembled system under time pressure. Contains **T-1603 (RoS 7.90)** — one of the highest-return topics in the register, because rehearsal converts latent knowledge into performed knowledge.
*Contents:* Full simulated rounds — technical, design, coding, behavioral — each with follow-up trees, hints, model answers, *annotated weak answers*, and evaluation rubrics.
*Connects to:* ← all chapters. This is the terminal node of the dependency graph.

---

**`17-CheatSheets/`** · Derived · ~15h
*Why it exists:* Phase 1 found **zero of 262 rows marked reviewed** — spaced repetition had never begun. This chapter exists specifically to make revision mechanically possible: one-page condensations, complexity tables, latency numbers, GC flags, decision matrices, and flashcard decks keyed to the `Rev` intervals in §3.
*Connects to:* Derived from every chapter; consumed continuously.

---

**`Resources/`** · Reference
*Why it exists:* Curated primary sources — JEPs, PostgreSQL and Kafka documentation, the papers worth reading, book recommendations by topic. Also the repair site for Phase 1's link-quality findings: canonical LeetCode URLs replacing the Bing redirect corpus, and the scrubbed Medium link.
*Connects to:* Referenced from all chapters.

---

**Two proposed additions**

**`ERRATA.md`** — *Why it exists:* Phase 1 verified **seven defective algorithm implementations and six incorrect technical claims** in the existing base. Silently publishing correct versions is insufficient: the wrong versions have been studied and must be *actively unlearned*. Each entry states the incorrect version, why it fails, the correct version, and the interview question that exposes the error. The buggy LRU `put()` and the "Running" thread state are the flagship entries.

**`INDEX.md`** — *Why it exists:* The register contains 124 topics with dense cross-dependencies; §6 identifies six hub topics feeding 6–11 downstream topics each. Without a cross-reference index, the handbook's modularity becomes fragmentation. This file maps topic ID → chapter → dependencies → related mock interviews, enforcing the brief's "cross-reference, never duplicate" rule. It also prevents the specific defect Phase 1 found in the existing base: Suffix Array, AVL, Fenwick, and LRU duplicated across two pages with **contradictory implementations**.

### 9.3 Chapter weight summary

| Ch | Topics | Hours | Avg IWI | Top-25 topics | Role |
|---|---|---|---|---|---|
| 01 Java Core | 16 | 70 | 4.9 | 0 | Foundation |
| 02 Collections | 9 | 37 | 5.7 | 0 | Foundation |
| 03 JVM | 12 | 65 | 5.7 | 0 | Depth |
| 04 Concurrency | 16 | 84 | 5.5 | 1 | Depth |
| 05 Spring | 17 | 100 | 6.1 | 2 | Core competence |
| **06 System Design** | 14 | 124 | **7.4** | **7** | **Outcome-deciding** |
| **07 Architecture** | 16 | 94 | 7.0 | **4** | **Staff signal** |
| **08 Database** | 18 | 127 | 6.8 | **4** | **Outcome-deciding** |
| 09 Kafka | 10 | 56 | 6.7 | 1 | Domain depth |
| 10 Cloud | 9 | 49 | 5.5 | 0 | Supporting |
| 11 Testing | 8 | 44 | 5.8 | 0 | Supporting |
| 12 Performance | 8 | 40 | 6.4 | 0 | Enabling |
| 13 Security | 7 | 31 | 5.8 | 0 | Supporting |
| **14 Behavioral** | 15 | 88 | **7.2** | **4** | **Highest RoS** |
| 15 LeetCode | 19 | 286 | 5.5 | 1 | Practice track |
| **16 Mocks** | 4 | 43 | **7.4** | 1 | **Integration** |
| 17 Cheat Sheets | — | 15 | — | — | Retention |
| **Total** | **124** | **1,338** | **6.2** | **25** | |

**The blueprint's shape in one line:** four chapters — 06, 07, 08, 14 — hold **19 of the 25 outcome-deciding topics** in 433 hours (32% of total effort). All four are currently at or near zero coverage.

---

## 10. Phase 3 Handoff

This blueprint supplies the inputs Phase 3 requires:

| Phase 3 need | Source |
|---|---|
| Priority ordering | §8 RoS ranking (full computation for all 124) |
| Prerequisite constraints | §6 dependency graph & critical-path chains |
| Time budget | §7 effort model + scenario paths |
| Track parallelism | §6.2 — the three-track model |
| Revision scheduling | `Rev` column in §3 |
| Bucket assignment | Immediate / High / Medium / Advanced / Expert, derived from RoS × tier |

**Three recommendations Phase 3 should carry forward:**

1. **Open with Chapter 14, not Chapter 01.** Twelve of the top twenty RoS topics are behavioral. Highest return, lowest cost, and it needs elapsed time.
2. **Run three parallel tracks from week one.** Sequential execution is the dominant preparation failure mode.
3. **Target the Senior-ready path (~610h)**, not the complete register. The Expert tier — nine topics, none above IWI 5.5 — should be explicitly deferred, since over-investment there is the most common misallocation in senior interview prep.

---

## Phase 2 Complete — Awaiting Approval

**Proposed Phase 3 deliverable:** `00-Roadmap.md` — the full learning roadmap with Immediate / High / Medium / Advanced / Expert buckets, per-topic study time, prerequisites, difficulty, interview frequency, recommended order, common mistakes, and expected interview depth, rendered as a three-track dated weekly plan.

**Confirm to proceed to Phase 3 — Learning Roadmap.**
