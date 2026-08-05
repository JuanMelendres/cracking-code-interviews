---
title: "Cheat Sheets — Index"
document_type: cheat-sheet-index
status: draft
last_updated: 2026-08-03
---

# Cheat Sheets

One-page-equivalent rapid-review documents, one per canonical `handbook/` chapter, per `CLAUDE.md`'s Cheat Sheet Standard: core mental model, essential definitions, decision table, key numbers, common pitfalls, interview answer skeleton, production warning signs, related links. These are **not** full-length chapters — read the linked canonical chapter for the actual teaching material, internals, and full interview-question set; use these for the day-before-the-interview pass.

## A note on scope

Sixty-eight cheat sheets exist so far, ranked by each canonical chapter's own stated IWI (interview-weight-index) — built across thirteen bounded batches, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. 7 chapters remain uncovered.

Every fact in every cheat sheet below (definitions, decision tables, measured numbers, production incidents) was extracted directly from its canonical chapter — nothing here was written from memory or general knowledge. Extraction was done via a dedicated read-and-report pass per chapter before any cheat sheet was drafted, consistent with this repository's no-fabrication discipline.

## Cheat Sheets

| # | Cheat Sheet | Topic ID | IWI | Domain | Canonical Chapter |
|---|---|---|---|---|---|
| 1 | [System Design Method and Estimation](system-design-method-and-estimation.md) | T-801 | 8.65 | system-design | `handbook/system-design/system-design-method-and-estimation.md` |
| 2 | [Distributed Systems Failure Modes](distributed-systems-failure-modes.md) | T-909 | 8.45 | system-design | `handbook/system-design/distributed-systems-failure-modes.md` |
| 3 | [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md) | T-804 | 8.45 | system-design | `handbook/system-design/caching-strategies-and-invalidation.md` |
| 4 | [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md) | T-907 | 8.40 | architecture | `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md` |
| 5 | [Database Index Structures](index-structures-btree-composite-covering.md) | T-609 | 8.30 | databases | `handbook/databases/index-structures-btree-composite-covering.md` |
| 6 | [Spring Transactional Proxy Mechanics and Propagation](transactional-proxy-mechanics-and-propagation.md) | T-504 | 8.15 | spring | `handbook/spring/transactional-proxy-mechanics-and-propagation.md` |
| 7 | [Kafka Delivery Semantics and Exactly-Once](delivery-semantics-and-exactly-once.md) | T-704 | 8.00 | kafka | `handbook/kafka/delivery-semantics-and-exactly-once.md` |
| 8 | [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md) | T-611 | 7.95 | databases | `handbook/databases/isolation-levels-and-concurrency-anomalies.md` |
| 9 | [CAP Theorem and Consistency Models](cap-theorem-and-consistency-models.md) | T-807 | 7.90 | system-design | `handbook/system-design/cap-theorem-and-consistency-models.md` |
| 10 | [API Design](api-design.md) | T-803 | 7.90 | system-design | `handbook/system-design/api-design.md` |
| 11 | [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) | T-610 | 7.90 | databases | `handbook/databases/query-planning-and-explain-analyze.md` |
| 12 | [Idempotency at System Edges](idempotency.md) | T-809 | 7.85 | system-design | `handbook/system-design/idempotency.md` |
| 13 | [Java Memory Model and volatile](java-memory-model-and-volatile.md) | T-401 | 7.75 | concurrency | `handbook/concurrency/java-memory-model-and-volatile.md` |
| 14 | [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md) | T-806 | 7.70 | system-design | `handbook/system-design/data-partitioning-and-consistent-hashing.md` |
| 15 | [Distributed Transactions: Saga and Outbox](distributed-transactions-saga-and-outbox.md) | T-618 | 7.65 | system-design | `handbook/system-design/distributed-transactions-saga-and-outbox.md` |
| 16 | [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md) | T-614 | 7.60 | databases | `handbook/databases/table-partitioning-and-sharding-strategies.md` |
| 17 | [Resilience Patterns](resilience-patterns.md) | T-515 | 7.60 | system-design | `handbook/system-design/resilience-patterns.md` |
| 18 | [Kafka Producer Semantics: acks, Idempotence, Partition Keys](producer-semantics-and-partition-keys.md) | T-702/T-705 | 7.55 | kafka | `handbook/kafka/producer-semantics-and-partition-keys.md` |
| 19 | [Consumer Groups and Rebalancing](consumer-groups-and-rebalancing.md) | T-703 | 7.50 | kafka | `handbook/kafka/consumer-groups-and-rebalancing.md` |
| 20 | [HashMap Internals](hashmap-internals.md) | T-201 | 7.4 | collections | `handbook/collections/hashmap-internals.md` |
| 21 | [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md) | T-306 | 7.35 | jvm | `handbook/jvm/gc-fundamentals-and-log-analysis.md` |
| 22 | [Zero-Downtime Schema Migration](zero-downtime-schema-migration.md) | T-616 | 7.30 | databases | `handbook/databases/zero-downtime-schema-migration.md` |
| 23 | [Auto-Configuration and Bean Lifecycle](auto-configuration-and-bean-lifecycle.md) | T-501 | 7.30 | spring | `handbook/spring/auto-configuration-and-bean-lifecycle.md` |
| 24 | [Clean and Hexagonal Architecture](clean-hexagonal-architecture.md) | T-901 | 7.25 | architecture | `handbook/architecture/clean-hexagonal-architecture.md` |
| 25 | [DDD Tactical Design: Aggregates](ddd-tactical-design-aggregates.md) | T-903 | 7.25 | architecture | `handbook/architecture/ddd-tactical-design-aggregates.md` |
| 26 | [Spring Security Filter Chain](security-filter-chain.md) | T-511 | 7.20 | spring | `handbook/spring/security-filter-chain.md` |
| 27 | [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md) | T-406 | 7.15 | concurrency | `handbook/concurrency/executors-and-thread-pool-sizing.md` |
| 28 | [OAuth2, OIDC, and JWT](oauth2-oidc-and-jwt.md) | T-512/T-513 | 7.15 | security | `handbook/security/oauth2-oidc-and-jwt.md` |
| 29 | [Test Strategy and Test Doubles](test-strategy-and-test-doubles.md) | T-1103 | 7.00 | testing | `handbook/testing/test-strategy-and-test-doubles.md` |
| 30 | [Storage Selection Trade-offs](storage-selection-tradeoffs.md) | T-811 | 6.90 | system-design | `handbook/system-design/storage-selection-tradeoffs.md` |
| 31 | [Performance Methodology and SLO Error Budgets](performance-methodology-and-slo-error-budgets.md) | T-1206 | 6.90 | performance | `handbook/performance/performance-methodology-and-slo-error-budgets.md` |
| 32 | [Logging, Metrics, Tracing, and OpenTelemetry](logging-metrics-tracing-and-opentelemetry.md) | T-1205 | 6.90 | performance | `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` |
| 33 | [GC Roots, Reachability, and Reference Strength](gc-roots-reachability-and-reference-strength.md) | T-303 | 6.9 | jvm | `handbook/jvm/gc-roots-reachability-and-reference-strength.md` |
| 34 | [Kubernetes Resource Limits, Probes, and JVM Sizing](kubernetes-resource-limits-probes-and-jvm-sizing.md) | T-1003 | 6.8 | cloud | `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| 35 | [Virtual Threads](virtual-threads.md) | T-410 | 6.75 | concurrency | `handbook/concurrency/virtual-threads.md` |
| 36 | [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md) | T-1204 | 6.70 | performance | `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` |
| 37 | [Deadlock, Race Conditions, and Thread Diagnostics](deadlock-race-conditions-and-thread-diagnostics.md) | T-409 | 6.70 | concurrency | `handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` |
| 38 | [ConcurrentHashMap Internals](concurrenthashmap-internals.md) | T-205 | 6.65 | collections | `handbook/collections/concurrenthashmap-internals.md` |
| 39 | [Integration Testing Against Real Dependencies](integration-testing-against-real-dependencies.md) | T-1104 | 6.50 | testing | `handbook/testing/integration-testing-against-real-dependencies.md` |
| 40 | [Kubernetes Objects, Scheduling, and Networking](kubernetes-objects-scheduling-and-networking.md) | T-1002 | 6.5 | cloud | `handbook/cloud/kubernetes-objects-scheduling-and-networking.md` |
| 41 | [Kafka Architecture Fundamentals](kafka-architecture-fundamentals.md) | T-701 | 6.40 | kafka | `handbook/kafka/kafka-architecture-fundamentals.md` |
| 42 | [OWASP Top 10 for Backend Services](owasp-top-10-for-backend-services.md) | T-1301 | 6.35 | security | `handbook/security/owasp-top-10-for-backend-services.md` |
| 43 | [Applied Cryptography: Hashing, Signing, TLS](applied-cryptography-hashing-signing-tls.md) | T-1303 | 6.2 | security | `handbook/security/applied-cryptography-hashing-signing-tls.md` |
| 44 | [Streams and Collectors](streams-and-collectors.md) | T-107 | 6.2 | java-core | `handbook/java-core/streams-and-collectors.md` |
| 45 | [AuthN vs AuthZ, RBAC vs ABAC](authn-authz-rbac-vs-abac.md) | T-1302 | 6.0 | security | `handbook/security/authn-authz-rbac-vs-abac.md` |
| 46 | [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) | T-101 | 5.9 | java-core | `handbook/java-core/equals-hashcode-and-comparable-contracts.md` |
| 47 | [Performance and Load Testing Methodology](performance-and-load-testing-methodology.md) | T-1106 | 5.9 | testing | `handbook/testing/performance-and-load-testing-methodology.md` |
| 48 | [Cloud Cost and Scaling Economics](cloud-cost-and-scaling-economics.md) | T-1007 | 5.9 | cloud | `handbook/cloud/cloud-cost-and-scaling-economics.md` |
| 49 | [Generics: Erasure, Variance, and PECS](generics-erasure-and-pecs.md) | T-104 | 5.85 | java-core | `handbook/java-core/generics-erasure-and-pecs.md` |
| 50 | [BlockingQueue Family and Producer-Consumer](blockingqueue-family.md) | T-207 | 5.8 | collections | `handbook/collections/blockingqueue-family.md` |
| 51 | [CI/CD Pipeline Design and Deployment Strategies](cicd-pipeline-design-and-deployment-strategies.md) | T-1009 | 5.8 | cloud | `handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md` |
| 52 | [Writing Tests Live in an Interview](writing-tests-live-in-an-interview.md) | T-1108 | 5.8 | testing | `handbook/testing/writing-tests-live-in-an-interview.md` |
| 53 | [Injection, Input Validation, and Output Encoding](injection-input-validation-output-encoding.md) | T-1305 | 5.7 | security | `handbook/security/injection-input-validation-output-encoding.md` |
| 54 | [Collection Selection Decision Matrix](collection-selection-decision-matrix.md) | T-209 | 5.7 | collections | `handbook/collections/collection-selection-decision-matrix.md` |
| 55 | [Contract Testing for Services](contract-testing-for-services.md) | T-1105 | 5.7 | testing | `handbook/testing/contract-testing-for-services.md` |
| 56 | [ArrayList and LinkedList Internals](arraylist-and-linkedlist-internals.md) | T-202 | 5.6 | collections | `handbook/collections/arraylist-and-linkedlist-internals.md` |
| 57 | [AWS Core Services for Backend Engineers](aws-core-services-for-backend-engineers.md) | T-1006 | 5.6 | cloud | `handbook/cloud/aws-core-services-for-backend-engineers.md` |
| 58 | [Multi-Tenancy Isolation Models](multi-tenancy-isolation-models.md) | T-1307 | 5.6 | security | `handbook/security/multi-tenancy-isolation-models.md` |
| 59 | [Exception Design and Hierarchy Strategy](exception-design-and-hierarchy-strategy.md) | T-105 | 5.5 | java-core | `handbook/java-core/exception-design-and-hierarchy-strategy.md` |
| 60 | [Secrets Management and Key Rotation](secrets-management-and-key-rotation.md) | T-1304 | 5.5 | security | `handbook/security/secrets-management-and-key-rotation.md` |
| 61 | [Immutability and Defensive Copying](immutability-and-defensive-copying.md) | T-103 | 5.4 | java-core | `handbook/java-core/immutability-and-defensive-copying.md` |
| 62 | [ZGC and Shenandoah: Concurrent Collection](zgc-and-shenandoah-concurrent-collection.md) | T-305 | 5.4 | jvm | `handbook/jvm/zgc-and-shenandoah-concurrent-collection.md` |
| 63 | [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) | T-605/T-608 | 5.20 | databases | `handbook/databases/data-modelling-and-explicit-join-tables.md` |
| 64 | [JUnit 5 Architecture and Advanced Features](junit5-architecture-and-advanced-features.md) | T-1102 | 5.0 | testing | `handbook/testing/junit5-architecture-and-advanced-features.md` |
| 65 | [Safepoints and Stop-the-World Mechanics](safepoints-and-stop-the-world-mechanics.md) | T-310 | 5.0 | jvm | `handbook/jvm/safepoints-and-stop-the-world-mechanics.md` |
| 66 | [Supply Chain Security, SBOM, and Dependency Risk](supply-chain-security-sbom-and-dependency-risk.md) | T-1306 | 5.0 | security | `handbook/security/supply-chain-security-sbom-and-dependency-risk.md` |
| 67 | [Object Layout, Headers, and Compressed Oops](object-layout-headers-and-compressed-oops.md) | T-302 | 4.9 | jvm | `handbook/jvm/object-layout-headers-and-compressed-oops.md` |
| 68 | [Native Memory, Direct Buffers, and Off-Heap](native-memory-direct-buffers-and-off-heap.md) | T-311 | 4.7 | jvm | `handbook/jvm/native-memory-direct-buffers-and-off-heap.md` |

## How this relates to `flashcards/`

Cheat sheets and flashcards serve different grain sizes and different moments, and are meant to coexist without duplicating each other: a flashcard is one atomic Q/A pair for spaced-repetition drilling; a cheat sheet is a one-page whole-chapter refresh for the day before an interview. `flashcards/` is currently empty (not yet started as its own Phase 6 deliverable) — when it is built, individual cards should draw from the same canonical chapters these cheat sheets do, not restate a cheat sheet's content verbatim.

## Selection method

Chapters are ranked by the IWI each canonical chapter states in its own "Topic register" line — this is more reliable than `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register table, which predates several newer system-design/T-8xx/T-9xx chapters and does not list them at all (a known staleness gap; see the flagged coverage-audit refresh task). Built across thirteen batches (top 8, then 5 x 12), spanning 13 domains so far (system-design, architecture, databases, spring, kafka, concurrency, collections, jvm, testing, cloud, security, java-core) rather than front-loading one. Tie-break history: Clean/Hexagonal Architecture over DDD Tactical Design in batch 4 (DDD Tactical closed batch 5); Storage Selection Trade-offs over Performance Methodology/SLO Error Budgets in batch 5 (Performance Methodology closed batch 6); Percentiles/Tail Latency over Deadlock/Race-Conditions/Thread-Diagnostics in batch 6 (Deadlock/Race-Conditions closed batch 7, no tie); Applied Cryptography over Streams and Collectors in batch 8, both IWI 6.2 — resolved via cross-repository reference count (12 refs vs. 10; Streams and Collectors carried over and closed in batch 9). Batch 9's three-way tie at IWI 5.9 (Performance and Load Testing Methodology, equals/hashCode/Comparable Contracts, Cloud Cost and Scaling Economics) needed no elimination — all three fit within the batch's 5 slots; ordered by cross-repository reference count (equals/hashCode and Performance and Load Testing both at 12 refs, Cloud Cost at 10) with equals/hashCode's own stated "Very High" interview frequency breaking its tie with Performance and Load Testing's "Moderate" frequency. Batch 10's three-way tie at IWI 5.8 (Writing Tests Live in an Interview, CI/CD Pipeline Design and Deployment Strategies, BlockingQueue Family) also needed no elimination for the same reason; batch 10's final slot required an actual elimination among three IWI-5.7 candidates (Injection/Input Validation/Output Encoding, Collection Selection Decision Matrix, Contract Testing for Services) — resolved via cross-repository reference count (11 refs vs. 10 vs. 7), with Injection winning and the other two carried over uncovered. Batch 11 closed both carried-over IWI-5.7 chapters (Collection Selection Decision Matrix, Contract Testing for Services) at their established rank, then filled its final 3 slots with a genuine three-way tie at IWI 5.6 (Multi-Tenancy Isolation Models, AWS Core Services for Backend Engineers, ArrayList and LinkedList Internals) that needed no elimination — the tier's exact size matched the batch's remaining capacity. Batch 12 closed two clean two-way ties in sequence (IWI 5.5: Secrets Management and Key Rotation, Exception Design and Hierarchy Strategy; IWI 5.4: ZGC and Shenandoah, Immutability and Defensive Copying) — both needed no elimination — then took the next unambiguous single chapter, Data Modelling and Explicit Join Tables (IWI 5.20), as the batch's fifth slot. Batch 13 closed a clean three-way tie at IWI 5.0 (JUnit 5 Architecture and Advanced Features, Safepoints and Stop-the-World Mechanics, Supply Chain Security/SBOM/Dependency Risk) with no elimination needed, then took the next two unambiguous chapters, Object Layout/Headers/Compressed Oops (IWI 4.9) and Native Memory/Direct Buffers/Off-Heap (IWI 4.7), as the batch's fourth and fifth slots. Only 7 chapters remain uncovered: 2 with a stated IWI (Escape Analysis and Scalar Replacement, IWI 4.6; Mutation and Property-Based Testing, IWI 4.3) plus the 5 jvm chapters lacking any stated IWI. Not every chapter carries an individually stated IWI line in a matching format — 5 jvm chapters (`g1-remembered-sets-and-write-barriers.md`, `jit-tiered-compilation-and-deoptimization.md`, `jvm-flags-and-container-ergonomics.md`, `jvm-memory-layout-and-runtime-regions.md`, `memory-leak-diagnosis-and-heap-dump-analysis.md`) were excluded from the ranking scan for this reason; no IWI value was invented for them. Next candidate for a future batch: re-run the scripted IWI scan across all 75 chapters' own Topic register lines before selecting the batch, since the ranking shifts as chapters get covered — do not trust a hardcoded list here.
