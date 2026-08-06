---
title: "Flashcards — Index"
document_type: flashcard-index
status: draft
last_updated: 2026-08-06
---

# Flashcards

Atomic, spaced-repetition-ready Q/A decks, one deck per canonical `handbook/` chapter, per `CLAUDE.md`'s Flashcard Standard. Each card is one concept: a prompt, a concise answer, why it matters for an interview, and the common trap around it. These are **not** rapid-review pages — see `cheat-sheets/` for the one-page-per-chapter refresh; a flashcard is the smallest reviewable unit, meant for daily/weekly drilling on the topic register's `Rev` intervals (`00-project/learning-roadmap.md`), not a pre-interview cram pass.

## A note on scope

Every card in every deck below already existed, written into its canonical chapter's own `## Flashcards` section, as part of that chapter's original closure. Building this deliverable is an extraction and reorganization pass, not new content generation: each deck below is copied from its chapter's existing cards, promoted to standalone files (topic-ID-keyed, per the roadmap's own spec — `flashcards/` "by topic ID, reviewed on the register's `Rev` intervals"), with in-chapter anchor links rewritten to resolve correctly from `flashcards/`'s own directory.

Handbook chapters currently use one of two existing card formats: an explicit `### Card:` structure (Prompt/Answer/Why it matters/Common trap/Related — the format this deliverable's template follows directly), and a plainer `**Q:**`/`**A:**` format used in the newer `jvm/`, `security/`, and `testing/` chapters. Decks built from the second format require light, non-fabricated synthesis to add the "Why it matters" and "Common trap" fields the template requires — grounded in that chapter's own stated interview framing and common-mistakes material, never invented.

**Batches 1–16 cover every chapter using the `### Card:` source format (52 decks, 169 cards) plus `testing/`, `security/`, and the first 4 `jvm/` chapters in the `**Q:**`/`**A:**` format (16 more decks, 48 cards): 68 decks, 217 cards total.** Sixteen bounded batches of 75 total chapters (238 real cards: 169 in `### Card:` format, 69 in `**Q:**` format) per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. Cards drawn from the `**Q:**`/`**A:**` format required light, non-fabricated synthesis of the "Why it matters" and "Common trap" fields — grounded directly in each Q/A pair's own content, not invented — and their "Related" links point to the canonical chapter as a whole rather than a specific anchor, since the source format has no anchor-labeled subsections to link to. **`testing/` and `security/` are both fully closed.** `jvm/` (11 chapters, the deliverable's last domain) is split across three batches given its size; 7 remain. One chapter in this batch, `memory-leak-diagnosis-and-heap-dump-analysis.md`, has no `cheat-sheets/` companion (it's one of the 5 jvm chapters left open in that deliverable per an earlier explicit user decision) — its topic ID (T-307) was sourced from `00-project/knowledge-architecture-blueprint.md` instead.

## Decks

| # | Deck | Topic ID | Cards | Canonical Chapter |
|---|---|---|---|---|
| 1 | [Database Index Structures](index-structures-btree-composite-covering.md) | T-609 | 5 | `handbook/databases/index-structures-btree-composite-covering.md` |
| 2 | [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md) | T-611 | 4 | `handbook/databases/isolation-levels-and-concurrency-anomalies.md` |
| 3 | [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) | T-610 | 4 | `handbook/databases/query-planning-and-explain-analyze.md` |
| 4 | [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) | T-605/T-608 | 3 | `handbook/databases/data-modelling-and-explicit-join-tables.md` |
| 5 | [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md) | T-614 | 3 | `handbook/databases/table-partitioning-and-sharding-strategies.md` |
| 6 | [Zero-Downtime Schema Migration](zero-downtime-schema-migration.md) | T-616 | 3 | `handbook/databases/zero-downtime-schema-migration.md` |
| 7 | [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) | T-101 | 3 | `handbook/java-core/equals-hashcode-and-comparable-contracts.md` |
| 8 | [Exception Design and Hierarchy Strategy](exception-design-and-hierarchy-strategy.md) | T-105 | 3 | `handbook/java-core/exception-design-and-hierarchy-strategy.md` |
| 9 | [Generics: Erasure, Variance, and PECS](generics-erasure-and-pecs.md) | T-104 | 3 | `handbook/java-core/generics-erasure-and-pecs.md` |
| 10 | [Immutability and Defensive Copying](immutability-and-defensive-copying.md) | T-103 | 3 | `handbook/java-core/immutability-and-defensive-copying.md` |
| 11 | [Streams and Collectors](streams-and-collectors.md) | T-107 | 3 | `handbook/java-core/streams-and-collectors.md` |
| 12 | [HashMap Internals](hashmap-internals.md) | T-201 | 3 | `handbook/collections/hashmap-internals.md` |
| 13 | [ArrayList and LinkedList Internals](arraylist-and-linkedlist-internals.md) | T-202 | 3 | `handbook/collections/arraylist-and-linkedlist-internals.md` |
| 14 | [ConcurrentHashMap Internals](concurrenthashmap-internals.md) | T-205 | 3 | `handbook/collections/concurrenthashmap-internals.md` |
| 15 | [BlockingQueue Family and Producer-Consumer](blockingqueue-family.md) | T-207 | 3 | `handbook/collections/blockingqueue-family.md` |
| 16 | [Collection Selection Decision Matrix](collection-selection-decision-matrix.md) | T-209 | 3 | `handbook/collections/collection-selection-decision-matrix.md` |
| 17 | [Java Memory Model and volatile](java-memory-model-and-volatile.md) | T-401 | 3 | `handbook/concurrency/java-memory-model-and-volatile.md` |
| 18 | [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md) | T-406 | 3 | `handbook/concurrency/executors-and-thread-pool-sizing.md` |
| 19 | [Deadlock, Race Conditions, and Thread Diagnostics](deadlock-race-conditions-and-thread-diagnostics.md) | T-409 | 3 | `handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` |
| 20 | [Virtual Threads (Project Loom)](virtual-threads.md) | T-410 | 3 | `handbook/concurrency/virtual-threads.md` |
| 21 | [Spring Auto-Configuration and Bean Lifecycle](auto-configuration-and-bean-lifecycle.md) | T-501 | 3 | `handbook/spring/auto-configuration-and-bean-lifecycle.md` |
| 22 | [Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation](transactional-proxy-mechanics-and-propagation.md) | T-504 | 4 | `handbook/spring/transactional-proxy-mechanics-and-propagation.md` |
| 23 | [Spring Security Filter Chain](security-filter-chain.md) | T-511 | 3 | `handbook/spring/security-filter-chain.md` |
| 24 | [Kafka Architecture Fundamentals — Topics, Partitions, Replication](kafka-architecture-fundamentals.md) | T-701 | 3 | `handbook/kafka/kafka-architecture-fundamentals.md` |
| 25 | [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](producer-semantics-and-partition-keys.md) | T-702/T-705 | 3 | `handbook/kafka/producer-semantics-and-partition-keys.md` |
| 26 | [Kafka Consumer Groups, Rebalancing, and Offset Management](consumer-groups-and-rebalancing.md) | T-703 | 3 | `handbook/kafka/consumer-groups-and-rebalancing.md` |
| 27 | [Kafka Delivery Semantics and Exactly-Once Processing](delivery-semantics-and-exactly-once.md) | T-704 | 3 | `handbook/kafka/delivery-semantics-and-exactly-once.md` |
| 28 | [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md) | T-1204 | 3 | `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` |
| 29 | [Logging, Metrics, Tracing, and OpenTelemetry](logging-metrics-tracing-and-opentelemetry.md) | T-1205 | 3 | `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` |
| 30 | [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](performance-methodology-and-slo-error-budgets.md) | T-1206 | 3 | `handbook/performance/performance-methodology-and-slo-error-budgets.md` |
| 31 | [Clean and Hexagonal Architecture](clean-hexagonal-architecture.md) | T-901 | 3 | `handbook/architecture/clean-hexagonal-architecture.md` |
| 32 | [DDD Tactical Design — Aggregates](ddd-tactical-design-aggregates.md) | T-903 | 3 | `handbook/architecture/ddd-tactical-design-aggregates.md` |
| 33 | [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md) | T-907 | 4 | `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md` |
| 34 | [Kubernetes Objects, Scheduling, and Networking](kubernetes-objects-scheduling-and-networking.md) | T-1002 | 3 | `handbook/cloud/kubernetes-objects-scheduling-and-networking.md` |
| 35 | [Kubernetes Resource Limits, Probes, and JVM Sizing](kubernetes-resource-limits-probes-and-jvm-sizing.md) | T-1003 | 3 | `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| 36 | [AWS Core Services for Backend Engineers](aws-core-services-for-backend-engineers.md) | T-1006 | 3 | `handbook/cloud/aws-core-services-for-backend-engineers.md` |
| 37 | [Cloud Cost and Scaling Economics](cloud-cost-and-scaling-economics.md) | T-1007 | 3 | `handbook/cloud/cloud-cost-and-scaling-economics.md` |
| 38 | [CI/CD Pipeline Design and Deployment Strategies](cicd-pipeline-design-and-deployment-strategies.md) | T-1009 | 3 | `handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md` |
| 39 | [API Design](api-design.md) | T-803 | 3 | `handbook/system-design/api-design.md` |
| 40 | [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md) | T-804 | 4 | `handbook/system-design/caching-strategies-and-invalidation.md` |
| 41 | [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md) | T-806 | 3 | `handbook/system-design/data-partitioning-and-consistent-hashing.md` |
| 42 | [CAP Theorem and Consistency Models](cap-theorem-and-consistency-models.md) | T-807 | 4 | `handbook/system-design/cap-theorem-and-consistency-models.md` |
| 43 | [Distributed Systems Failure Modes](distributed-systems-failure-modes.md) | T-909 | 4 | `handbook/system-design/distributed-systems-failure-modes.md` |
| 44 | [System Design Method and Estimation](system-design-method-and-estimation.md) | T-801 | 4 | `handbook/system-design/system-design-method-and-estimation.md` |
| 45 | [Idempotency at System Edges](idempotency.md) | T-809 | 4 | `handbook/system-design/idempotency.md` |
| 46 | [Distributed Transactions: Saga, Outbox, and 2PC](distributed-transactions-saga-and-outbox.md) | T-618 | 4 | `handbook/system-design/distributed-transactions-saga-and-outbox.md` |
| 47 | [Storage Selection Trade-offs](storage-selection-tradeoffs.md) | T-811 | 3 | `handbook/system-design/storage-selection-tradeoffs.md` |
| 48 | [Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads](resilience-patterns.md) | T-515 | 3 | `handbook/system-design/resilience-patterns.md` |
| 49 | [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md) | T-306 | 3 | `handbook/jvm/gc-fundamentals-and-log-analysis.md` |
| 50 | [OAuth2, OIDC, and JWT](oauth2-oidc-and-jwt.md) | T-512/T-513 | 4 | `handbook/security/oauth2-oidc-and-jwt.md` |
| 51 | [Test Strategy, the Pyramid, and Test Doubles](test-strategy-and-test-doubles.md) | T-1103 | 3 | `handbook/testing/test-strategy-and-test-doubles.md` |
| 52 | [Integration Testing Against Real Dependencies](integration-testing-against-real-dependencies.md) | T-1104 | 3 | `handbook/testing/integration-testing-against-real-dependencies.md` |
| 53 | [JUnit 5 Architecture and Advanced Features](junit5-architecture-and-advanced-features.md) | T-1102 | 3 | `handbook/testing/junit5-architecture-and-advanced-features.md` |
| 54 | [Contract Testing for Services](contract-testing-for-services.md) | T-1105 | 3 | `handbook/testing/contract-testing-for-services.md` |
| 55 | [Performance and Load Testing Methodology](performance-and-load-testing-methodology.md) | T-1106 | 3 | `handbook/testing/performance-and-load-testing-methodology.md` |
| 56 | [Mutation and Property-Based Testing](mutation-and-property-based-testing.md) | T-1107 | 3 | `handbook/testing/mutation-and-property-based-testing.md` |
| 57 | [Writing Tests Live in an Interview](writing-tests-live-in-an-interview.md) | T-1108 | 3 | `handbook/testing/writing-tests-live-in-an-interview.md` |
| 58 | [OWASP Top 10 for Backend Services](owasp-top-10-for-backend-services.md) | T-1301 | 3 | `handbook/security/owasp-top-10-for-backend-services.md` |
| 59 | [AuthN vs AuthZ, RBAC vs ABAC](authn-authz-rbac-vs-abac.md) | T-1302 | 3 | `handbook/security/authn-authz-rbac-vs-abac.md` |
| 60 | [Applied Cryptography: Hashing, Signing, and TLS](applied-cryptography-hashing-signing-tls.md) | T-1303 | 3 | `handbook/security/applied-cryptography-hashing-signing-tls.md` |
| 61 | [Injection, Input Validation, and Output Encoding](injection-input-validation-output-encoding.md) | T-1305 | 3 | `handbook/security/injection-input-validation-output-encoding.md` |
| 62 | [Secrets Management and Key Rotation](secrets-management-and-key-rotation.md) | T-1304 | 3 | `handbook/security/secrets-management-and-key-rotation.md` |
| 63 | [Supply Chain Security, SBOM, and Dependency Risk](supply-chain-security-sbom-and-dependency-risk.md) | T-1306 | 3 | `handbook/security/supply-chain-security-sbom-and-dependency-risk.md` |
| 64 | [Multi-Tenancy Isolation Models](multi-tenancy-isolation-models.md) | T-1307 | 3 | `handbook/security/multi-tenancy-isolation-models.md` |
| 65 | [Object Layout, Headers, and Compressed Oops](object-layout-headers-and-compressed-oops.md) | T-302 | 3 | `handbook/jvm/object-layout-headers-and-compressed-oops.md` |
| 66 | [GC Roots, Reachability, and Reference Strength](gc-roots-reachability-and-reference-strength.md) | T-303 | 3 | `handbook/jvm/gc-roots-reachability-and-reference-strength.md` |
| 67 | [Memory Leak Diagnosis and Heap Dump Analysis](memory-leak-diagnosis-and-heap-dump-analysis.md) | T-307 | 3 | `handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md` |
| 68 | [Escape Analysis and Scalar Replacement](escape-analysis-and-scalar-replacement.md) | T-309 | 3 | `handbook/jvm/escape-analysis-and-scalar-replacement.md` |

## How this relates to other deliverables

- `handbook/` — the canonical chapters these cards are extracted from; each card's `## Flashcards` section in its source chapter remains in place (unchanged) as the chapter's own embedded review aid.
- `cheat-sheets/` — a different grain and moment: a cheat sheet is a one-page whole-chapter refresh for the day before an interview; a flashcard is one atomic Q/A pair for ongoing spaced-repetition drilling. Neither restates the other's content verbatim.
- `00-project/learning-roadmap.md` — the source of the `Rev` (revision interval) scheduling concept this deliverable exists to make mechanically possible.
