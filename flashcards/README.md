---
title: "Flashcards — Index"
document_type: flashcard-index
status: draft
last_updated: 2026-09-08
---

# Flashcards

Atomic, spaced-repetition-ready Q/A decks, one deck per canonical `handbook/` chapter, per `CLAUDE.md`'s Flashcard Standard. Each card is one concept: a prompt, a concise answer, why it matters for an interview, and the common trap around it. These are **not** rapid-review pages — see `cheat-sheets/` for the one-page-per-chapter refresh; a flashcard is the smallest reviewable unit, meant for daily/weekly drilling on the topic register's `Rev` intervals (`00-project/learning-roadmap.md`), not a pre-interview cram pass.

## A note on scope

Every card in every deck below already existed, written into its canonical chapter's own `## Flashcards` section, as part of that chapter's original closure. Building this deliverable is an extraction and reorganization pass, not new content generation: each deck below is copied from its chapter's existing cards, promoted to standalone files (topic-ID-keyed, per the roadmap's own spec — `flashcards/` "by topic ID, reviewed on the register's `Rev` intervals"), with in-chapter anchor links rewritten to resolve correctly from `flashcards/`'s own directory.

Handbook chapters currently use one of two existing card formats: an explicit `### Card:` structure (Prompt/Answer/Why it matters/Common trap/Related — the format this deliverable's template follows directly), and a plainer `**Q:**`/`**A:**` format used in the newer `jvm/`, `security/`, and `testing/` chapters. Decks built from the second format require light, non-fabricated synthesis to add the "Why it matters" and "Common trap" fields the template requires — grounded in that chapter's own stated interview framing and common-mistakes material, never invented.

**91/91 decks, 283/283 cards.** This deliverable was originally closed at 75/75 decks, 238/238 cards (2026-08-06). Between that closure date and 2026-09-01, sixteen new handbook chapters were added — five in `handbook/spring/`, four in `handbook/system-design/`, three in `handbook/concurrency/`, one each in `handbook/databases/`, `handbook/cloud/`, `handbook/performance/`, and `handbook/jvm/` — every one of them with its own `## Flashcards` section, none of them ever getting a matching deck. This batch (2026-09-01) closed that backlog in one pass: 13 of the 16 new chapters actually use the `### Card:` structured format (Prompt/Answer/Why it matters/Common trap/Related, extracted directly — the same format 169 of the original 238 cards came from), and only 3 (`container-image-internals.md`, `capacity-planning-and-headroom.md`, `benchmarking-and-jmh-pitfalls.md`) use the plainer `**Q:**`/`**A:**` format requiring light, non-fabricated synthesis of "Why it matters"/"Common trap," grounded directly in each Q/A pair's own content. The 13 structured-format chapters use cross-chapter `[[wikilink]]`-style `Related` references (a first for this repository — no prior chapter used this exact notation); these were mechanically translated to relative Markdown links resolving from `flashcards/`'s own directory, using the same convention already established for cross-chapter references (chapter title as link text, no anchor) and self-references (the canonical-chapter link, matching the plainer-format convention). Every card across every domain was extracted from a chapter's own existing `## Flashcards` section — 205 cards from the `### Card:` format and 78 cards from the plainer `**Q:**`/`**A:**` format. Built across 18 bounded batches plus this one backlog-closing batch, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. 5 chapters (`memory-leak-diagnosis-and-heap-dump-analysis.md`, `jvm-memory-layout-and-runtime-regions.md`, `g1-remembered-sets-and-write-barriers.md`, `jit-tiered-compilation-and-deoptimization.md`, `jvm-flags-and-container-ergonomics.md`) have no `cheat-sheets/` companion — the same 5 jvm chapters left open in that deliverable per the user's explicit 2026-08-05 decision — so their topic IDs were sourced from `00-project/knowledge-architecture-blueprint.md` instead of the usual cheat-sheet front matter. Given how quickly this sixteen-chapter backlog accumulated once, any future handbook chapter that adds its own `## Flashcards` section should get a matching deck in the same batch it lands, rather than allowing another backlog to build.

**137/137 decks, 419/419 cards (2026-09-02).** The "91/91" claim above was found stale while closing an identical, larger backlog in `cheat-sheets/` (see that deliverable's own README for the full audit method): the same diff-against-full-handbook-list check, run against this directory, found the same 46 chapters — java-core, collections, concurrency, spring, databases, kafka, system-design, architecture, cloud, performance — each carrying their own `## Flashcards` section with no deck here.

Built via two parallel, independently-scoped 23-file background-agent batches, mirroring `flashcards/hashmap-internals.md`'s exact template. This remained an extraction pass, not new content generation, for 45 of the 46: every card's Prompt/Answer/Why it matters/Common trap text was copied verbatim from its chapter's own existing `## Flashcards` section — 22 chapters used the `### Card:` heading level, 9 used `## Card:` (converted to the deck template's `## Card:` level as a formatting-only change, not a content change). The sole exception, `hibernate-flush-modes-and-batch-writes.md`, uses the plainer `**Q:**`/`**A:**` format (like 3 of batch 15's chapters) and required light, non-fabricated synthesis of "Why it matters"/"Common trap" fields, grounded strictly in that chapter's own Q/A content and surrounding text — the same allowed pattern used for `container-image-internals.md`, `capacity-planning-and-headroom.md`, and `benchmarking-and-jmh-pitfalls.md` in batch 15.

16 of the 46 chapters used `[[wikilink]]`-style self-references in their `Related` fields (a few also cross-referencing another chapter by wikilink); all were mechanically translated to relative Markdown links resolving from `flashcards/`'s own directory, using the same convention established in batch 15 (see `flashcards/spring-webflux-and-reactive-programming.md` for the reference pattern). `git-internals-and-collaboration-workflows.md` was included with `topic_id: —`, since its own chapter explicitly states it carries no blueprint T-code — matching the same convention already used for it in `cheat-sheets/`.

Verified before merging: all 46 files' YAML parses; every deck has a matching Prompt/Answer/Why-it-matters/Common-trap/Related quintet per card (no missing fields); zero unresolved `[[wikilink]]` markers remain; two decks (`optimistic-vs-pessimistic-locking.md` and `hibernate-flush-modes-and-batch-writes.md`) were spot-checked line-by-line against their source chapters — both matched exactly, including the same "~1520ms against a 1500ms hold" figure already verified in the `cheat-sheets/` batch.

This is the same 46-chapter list as the `cheat-sheets/` batch, but the two deliverables draw from different sections of each chapter (`## Flashcards` here vs. whole-chapter extraction there) and were built and verified independently — this closure does not imply the `production-cookbook/` gap (the third, still-open leg of the same discovery) is closed by association.

## Decks

| # | Deck | Topic ID | Cards | Canonical Chapter |
|---|---|---|---|---|
| 1 | [Database Index Structures](index-structures-btree-composite-covering.md) | T-609 | 5 | `syllabus/06-databases/index-structures-btree-composite-covering.md` |
| 2 | [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md) | T-611 | 4 | `syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md` |
| 3 | [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) | T-610 | 4 | `syllabus/06-databases/query-planning-and-explain-analyze.md` |
| 4 | [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) | T-605/T-608 | 3 | `syllabus/06-databases/data-modelling-and-explicit-join-tables.md` |
| 5 | [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md) | T-614 | 3 | `syllabus/06-databases/table-partitioning-and-sharding-strategies.md` |
| 6 | [Zero-Downtime Schema Migration](zero-downtime-schema-migration.md) | T-616 | 3 | `syllabus/06-databases/zero-downtime-schema-migration.md` |
| 7 | [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) | T-101 | 3 | `syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md` |
| 8 | [Exception Design and Hierarchy Strategy](exception-design-and-hierarchy-strategy.md) | T-105 | 3 | `syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md` |
| 9 | [Generics: Erasure, Variance, and PECS](generics-erasure-and-pecs.md) | T-104 | 3 | `syllabus/02-java/language-core/generics-erasure-and-pecs.md` |
| 10 | [Immutability and Defensive Copying](immutability-and-defensive-copying.md) | T-103 | 3 | `syllabus/02-java/language-core/immutability-and-defensive-copying.md` |
| 11 | [Streams and Collectors](streams-and-collectors.md) | T-107 | 3 | `syllabus/02-java/language-core/streams-and-collectors.md` |
| 12 | [HashMap Internals](hashmap-internals.md) | T-201 | 3 | `syllabus/02-java/collections/hashmap-internals.md` |
| 13 | [ArrayList and LinkedList Internals](arraylist-and-linkedlist-internals.md) | T-202 | 3 | `syllabus/02-java/collections/arraylist-and-linkedlist-internals.md` |
| 14 | [ConcurrentHashMap Internals](concurrenthashmap-internals.md) | T-205 | 3 | `syllabus/02-java/collections/concurrenthashmap-internals.md` |
| 15 | [BlockingQueue Family and Producer-Consumer](blockingqueue-family.md) | T-207 | 3 | `syllabus/02-java/collections/blockingqueue-family.md` |
| 16 | [Collection Selection Decision Matrix](collection-selection-decision-matrix.md) | T-209 | 3 | `syllabus/02-java/collections/collection-selection-decision-matrix.md` |
| 17 | [Java Memory Model and volatile](java-memory-model-and-volatile.md) | T-401 | 3 | `syllabus/02-java/concurrency/java-memory-model-and-volatile.md` |
| 18 | [Executors and Thread Pool Sizing](executors-and-thread-pool-sizing.md) | T-406 | 3 | `syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md` |
| 19 | [Deadlock, Race Conditions, and Thread Diagnostics](deadlock-race-conditions-and-thread-diagnostics.md) | T-409 | 3 | `syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` |
| 20 | [Virtual Threads (Project Loom)](virtual-threads.md) | T-410 | 3 | `syllabus/02-java/concurrency/virtual-threads.md` |
| 21 | [Spring Auto-Configuration and Bean Lifecycle](auto-configuration-and-bean-lifecycle.md) | T-501 | 3 | `syllabus/05-spring/auto-configuration-and-bean-lifecycle.md` |
| 22 | [Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation](transactional-proxy-mechanics-and-propagation.md) | T-504 | 4 | `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md` |
| 23 | [Spring Security Filter Chain](security-filter-chain.md) | T-511 | 3 | `syllabus/05-spring/security-filter-chain.md` |
| 24 | [Kafka Architecture Fundamentals — Topics, Partitions, Replication](kafka-architecture-fundamentals.md) | T-701 | 3 | `syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md` |
| 25 | [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](producer-semantics-and-partition-keys.md) | T-702/T-705 | 3 | `syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md` |
| 26 | [Kafka Consumer Groups, Rebalancing, and Offset Management](consumer-groups-and-rebalancing.md) | T-703 | 3 | `syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md` |
| 27 | [Kafka Delivery Semantics and Exactly-Once Processing](delivery-semantics-and-exactly-once.md) | T-704 | 3 | `syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md` |
| 28 | [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md) | T-1204 | 3 | `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md` |
| 29 | [Logging, Metrics, Tracing, and OpenTelemetry](logging-metrics-tracing-and-opentelemetry.md) | T-1205 | 3 | `syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md` |
| 30 | [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](performance-methodology-and-slo-error-budgets.md) | T-1206 | 3 | `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md` |
| 31 | [Clean and Hexagonal Architecture](clean-hexagonal-architecture.md) | T-901 | 3 | `syllabus/17-architecture/clean-hexagonal-architecture.md` |
| 32 | [DDD Tactical Design — Aggregates](ddd-tactical-design-aggregates.md) | T-903 | 3 | `syllabus/17-architecture/ddd-tactical-design-aggregates.md` |
| 33 | [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md) | T-907 | 4 | `syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md` |
| 34 | [Kubernetes Objects, Scheduling, and Networking](kubernetes-objects-scheduling-and-networking.md) | T-1002 | 3 | `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md` |
| 35 | [Kubernetes Resource Limits, Probes, and JVM Sizing](kubernetes-resource-limits-probes-and-jvm-sizing.md) | T-1003 | 3 | `syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| 36 | [AWS Core Services for Backend Engineers](aws-core-services-for-backend-engineers.md) | T-1006 | 3 | `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` |
| 37 | [Cloud Cost and Scaling Economics](cloud-cost-and-scaling-economics.md) | T-1007 | 3 | `syllabus/15-cloud/cloud-cost-and-scaling-economics.md` |
| 38 | [CI/CD Pipeline Design and Deployment Strategies](cicd-pipeline-design-and-deployment-strategies.md) | T-1009 | 3 | `syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md` |
| 39 | [API Design](api-design.md) | T-803 | 3 | `syllabus/07-api-design/api-design.md` |
| 40 | [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md) | T-804 | 4 | `syllabus/11-system-design/caching-strategies-and-invalidation.md` |
| 41 | [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md) | T-806 | 3 | `syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md` |
| 42 | [CAP Theorem and Consistency Models](cap-theorem-and-consistency-models.md) | T-807 | 4 | `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md` |
| 43 | [Distributed Systems Failure Modes](distributed-systems-failure-modes.md) | T-909 | 4 | `syllabus/10-distributed-systems/distributed-systems-failure-modes.md` |
| 44 | [System Design Method and Estimation](system-design-method-and-estimation.md) | T-801 | 4 | `syllabus/11-system-design/system-design-method-and-estimation.md` |
| 45 | [Idempotency at System Edges](idempotency.md) | T-809 | 4 | `syllabus/11-system-design/idempotency.md` |
| 46 | [Distributed Transactions: Saga, Outbox, and 2PC](distributed-transactions-saga-and-outbox.md) | T-618 | 4 | `syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md` |
| 47 | [Storage Selection Trade-offs](storage-selection-tradeoffs.md) | T-811 | 3 | `syllabus/11-system-design/storage-selection-tradeoffs.md` |
| 48 | [Resilience Patterns: Circuit Breaker, Retry Jitter, Timeouts, and Bulkheads](resilience-patterns.md) | T-515 | 3 | `syllabus/11-system-design/resilience-patterns.md` |
| 49 | [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md) | T-306 | 3 | `syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md` |
| 50 | [OAuth2, OIDC, and JWT](oauth2-oidc-and-jwt.md) | T-512/T-513 | 4 | `syllabus/12-security/oauth2-oidc-and-jwt.md` |
| 51 | [Test Strategy, the Pyramid, and Test Doubles](test-strategy-and-test-doubles.md) | T-1103 | 3 | `syllabus/08-testing/test-strategy-and-test-doubles.md` |
| 52 | [Integration Testing Against Real Dependencies](integration-testing-against-real-dependencies.md) | T-1104 | 3 | `syllabus/08-testing/integration-testing-against-real-dependencies.md` |
| 53 | [JUnit 5 Architecture and Advanced Features](junit5-architecture-and-advanced-features.md) | T-1102 | 3 | `syllabus/08-testing/junit5-architecture-and-advanced-features.md` |
| 54 | [Contract Testing for Services](contract-testing-for-services.md) | T-1105 | 3 | `syllabus/08-testing/contract-testing-for-services.md` |
| 55 | [Performance and Load Testing Methodology](performance-and-load-testing-methodology.md) | T-1106 | 3 | `syllabus/08-testing/performance-and-load-testing-methodology.md` |
| 56 | [Mutation and Property-Based Testing](mutation-and-property-based-testing.md) | T-1107 | 3 | `syllabus/08-testing/mutation-and-property-based-testing.md` |
| 57 | [Writing Tests Live in an Interview](writing-tests-live-in-an-interview.md) | T-1108 | 3 | `syllabus/08-testing/writing-tests-live-in-an-interview.md` |
| 58 | [OWASP Top 10 for Backend Services](owasp-top-10-for-backend-services.md) | T-1301 | 3 | `syllabus/12-security/owasp-top-10-for-backend-services.md` |
| 59 | [AuthN vs AuthZ, RBAC vs ABAC](authn-authz-rbac-vs-abac.md) | T-1302 | 3 | `syllabus/12-security/authn-authz-rbac-vs-abac.md` |
| 60 | [Applied Cryptography: Hashing, Signing, and TLS](applied-cryptography-hashing-signing-tls.md) | T-1303 | 3 | `syllabus/12-security/applied-cryptography-hashing-signing-tls.md` |
| 61 | [Injection, Input Validation, and Output Encoding](injection-input-validation-output-encoding.md) | T-1305 | 3 | `syllabus/12-security/injection-input-validation-output-encoding.md` |
| 62 | [Secrets Management and Key Rotation](secrets-management-and-key-rotation.md) | T-1304 | 3 | `syllabus/12-security/secrets-management-and-key-rotation.md` |
| 63 | [Supply Chain Security, SBOM, and Dependency Risk](supply-chain-security-sbom-and-dependency-risk.md) | T-1306 | 3 | `syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md` |
| 64 | [Multi-Tenancy Isolation Models](multi-tenancy-isolation-models.md) | T-1307 | 3 | `syllabus/12-security/multi-tenancy-isolation-models.md` |
| 65 | [Object Layout, Headers, and Compressed Oops](object-layout-headers-and-compressed-oops.md) | T-302 | 3 | `syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md` |
| 66 | [GC Roots, Reachability, and Reference Strength](gc-roots-reachability-and-reference-strength.md) | T-303 | 3 | `syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md` |
| 67 | [Memory Leak Diagnosis and Heap Dump Analysis](memory-leak-diagnosis-and-heap-dump-analysis.md) | T-307 | 3 | `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md` |
| 68 | [Escape Analysis and Scalar Replacement](escape-analysis-and-scalar-replacement.md) | T-309 | 3 | `syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md` |
| 69 | [JVM Memory Layout and Runtime Regions](jvm-memory-layout-and-runtime-regions.md) | T-301 | 3 | `syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md` |
| 70 | [G1 Internals: Remembered Sets and Write Barriers](g1-remembered-sets-and-write-barriers.md) | T-304 | 3 | `syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md` |
| 71 | [JIT: Tiered Compilation, Inlining, and Deoptimization](jit-tiered-compilation-and-deoptimization.md) | T-308 | 3 | `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md` |
| 72 | [Safepoints and Stop-the-World Mechanics](safepoints-and-stop-the-world-mechanics.md) | T-310 | 3 | `syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md` |
| 73 | [ZGC and Shenandoah: Concurrent Collection](zgc-and-shenandoah-concurrent-collection.md) | T-305 | 3 | `syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md` |
| 74 | [Native Memory, Direct Buffers, and Off-Heap](native-memory-direct-buffers-and-off-heap.md) | T-311 | 3 | `syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md` |
| 75 | [JVM Flags and Container Ergonomics](jvm-flags-and-container-ergonomics.md) | T-312 | 3 | `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md` |
| 76 | [Spring Cache Abstraction and Pitfalls](spring-cache-abstraction-and-pitfalls.md) | T-514 | 3 | `syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md` |
| 77 | [Spring Bean Scopes and Proxy Modes](spring-bean-scopes-and-proxy-modes.md) | T-502 | 3 | `syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md` |
| 78 | [Spring Testing: Slices and Context Caching](spring-testing-slices-and-context-caching.md) | T-517 | 3 | `syllabus/05-spring/spring-testing-slices-and-context-caching.md` |
| 79 | [Spring WebFlux and Reactive Programming](spring-webflux-and-reactive-programming.md) | T-509 | 3 | `syllabus/05-spring/spring-webflux-and-reactive-programming.md` |
| 80 | [Spring Boot Actuator, Health, and Observability Hooks](spring-actuator-health-and-observability-hooks.md) | T-516 | 3 | `syllabus/05-spring/spring-actuator-health-and-observability-hooks.md` |
| 81 | [API Gateway, BFF, and Edge Concerns](api-gateway-bff-and-edge-concerns.md) | T-911 | 3 | `syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md` |
| 82 | [Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push](realtime-delivery-websocket-sse-and-long-polling.md) | T-812 | 3 | `syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md` |
| 83 | [Hibernate Second-Level and Query Cache](hibernate-second-level-and-query-cache.md) | T-603 | 3 | `syllabus/06-databases/hibernate-second-level-and-query-cache.md` |
| 84 | [Search and Indexing Systems](search-and-indexing-systems.md) | T-810 | 3 | `syllabus/11-system-design/search-and-indexing-systems.md` |
| 85 | [The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation](twelve-factor-config.md) | T-1008 | 3 | `syllabus/15-cloud/twelve-factor-config.md` |
| 86 | [ThreadLocal-Mediated Classloader Leaks](threadlocal-mediated-classloader-leaks.md) | T-413 | 2 | `syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md` |
| 87 | [VarHandles, Unsafe, and Their Replacement](varhandles-and-unsafe.md) | T-415 | 3 | `syllabus/02-java/concurrency/varhandles-and-unsafe.md` |
| 88 | [Foreign Function & Memory API](foreign-function-and-memory-api.md) | T-416 | 1 | `syllabus/02-java/concurrency/foreign-function-and-memory-api.md` |
| 89 | [Containers & Image Internals](container-image-internals.md) | T-1001 | 3 | `syllabus/14-devops-containers/container-image-internals.md` |
| 90 | [Capacity Planning & Headroom](capacity-planning-and-headroom.md) | T-1208 | 3 | `syllabus/16-performance-jvm/capacity-planning-and-headroom.md` |
| 91 | [Benchmarking & JMH Pitfalls](benchmarking-and-jmh-pitfalls.md) | T-1203 | 3 | `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md` |
| 92 | [Rate Limiting and Throttling Algorithms](rate-limiting-and-throttling-algorithms.md) | T-808 | 3 | `syllabus/11-system-design/rate-limiting-and-throttling-algorithms.md` |
| 93 | [Event-Driven Architecture Integration Styles](event-driven-architecture-integration-styles.md) | T-906 | 3 | `syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md` |
| 94 | [DDD Strategic — Bounded Contexts and Context Mapping](ddd-strategic-bounded-contexts-and-context-mapping.md) | T-902 | 3 | `syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md` |
| 95 | [Strangler Fig and Migration Patterns](strangler-fig-and-migration-patterns.md) | T-912 | 3 | `syllabus/17-architecture/strangler-fig-and-migration-patterns.md` |
| 96 | [Technical Debt and Evolutionary Architecture](technical-debt-and-evolutionary-architecture.md) | T-913 | 3 | `syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md` |
| 97 | [Consumer Lag, Backpressure, and DLQ Strategy](consumer-lag-backpressure-and-dlq-strategy.md) | T-707 | 3 | `syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md` |
| 98 | [JPA Entity Lifecycle and the N+1 Problem](jpa-entity-lifecycle-and-the-n1-problem.md) | T-601/T-602 | 3 | `syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md` |
| 99 | [Optimistic vs. Pessimistic Locking](optimistic-vs-pessimistic-locking.md) | T-604 | 3 | `syllabus/06-databases/optimistic-vs-pessimistic-locking.md` |
| 100 | [Incident Response and Blameless Postmortems](incident-response-and-blameless-postmortems.md) | T-1207 | 3 | `syllabus/13-observability/incident-response-and-blameless-postmortems.md` |
| 101 | [Spring Framework vs. Spring Boot](spring-framework-vs-spring-boot.md) | T-506/T-501 | 3 | `syllabus/05-spring/spring-framework-vs-spring-boot.md` |
| 102 | [MVCC, Vacuum, and Bloat](mvcc-vacuum-and-bloat.md) | T-612 | 3 | `syllabus/06-databases/mvcc-vacuum-and-bloat.md` |
| 103 | [Replication, Read Replicas, and Replica Lag](replication-read-replicas-and-replica-lag.md) | T-615 | 3 | `syllabus/06-databases/replication-read-replicas-and-replica-lag.md` |
| 104 | [CQRS Read/Write Separation](cqrs-read-write-separation.md) | T-904 | 3 | `syllabus/17-architecture/cqrs-read-write-separation.md` |
| 105 | [Multi-Region Failover and Disaster Recovery](multi-region-failover-and-disaster-recovery.md) | T-814 | 3 | `syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md` |
| 106 | [Profiling, JFR, and Flame Graphs](profiling-jfr-and-flame-graphs.md) | T-1202 | 3 | `syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md` |
| 107 | [Load Balancing, Service Discovery, and Health Checking](load-balancing-service-discovery-and-health-checking.md) | T-805 | 3 | `syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md` |
| 108 | [Locks, Deadlocks, and Lock Escalation](locks-deadlocks-and-lock-escalation.md) | T-613 | 3 | `syllabus/06-databases/locks-deadlocks-and-lock-escalation.md` |
| 109 | [Schema Registry and Compatibility Evolution](schema-registry-and-compatibility-evolution.md) | T-708 | 3 | `syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md` |
| 110 | [CompletableFuture and Async Composition](completablefuture-and-async-composition.md) | T-407 | 3 | `syllabus/02-java/concurrency/completablefuture-and-async-composition.md` |
| 111 | [Connection Pooling and Sizing (HikariCP)](connection-pooling-and-sizing.md) | T-607 | 3 | `syllabus/06-databases/connection-pooling-and-sizing.md` |
| 112 | [Modular Monolith as a Deliberate Choice](modular-monolith-as-a-deliberate-choice.md) | T-910 | 3 | `syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md` |
| 113 | [Messaging Patterns and Change Data Capture](messaging-patterns-and-change-data-capture.md) | T-710 | 3 | `syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md` |
| 114 | [Architecture Decision Records](architecture-decision-records.md) | T-916 | 3 | `syllabus/17-architecture/architecture-decision-records.md` |
| 115 | [Event Sourcing and Its Real Costs](event-sourcing-and-its-real-costs.md) | T-905 | 3 | `syllabus/09-messaging-event-driven/event-sourcing-and-its-real-costs.md` |
| 116 | [Atomics, CAS, and the ABA Problem](atomics-cas-and-the-aba-problem.md) | T-405 | 3 | `syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md` |
| 117 | [Design Patterns Applied](design-patterns-applied.md) | T-914 | 4 | `syllabus/04-software-design/design-patterns-applied.md` |
| 118 | [ReentrantLock, ReadWriteLock, and StampedLock](reentrantlock-readwritelock-and-stampedlock.md) | T-404 | 3 | `syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md` |
| 119 | [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md) | T-102 | 3 | `syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md` |
| 120 | [Hibernate Flush Modes and Batch Writes](hibernate-flush-modes-and-batch-writes.md) | T-606 | 3 | `syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md` |
| 121 | [Lambdas and Functional Interfaces](lambdas-and-functional-interfaces.md) | T-108 | 3 | `syllabus/02-java/language-core/lambdas-and-functional-interfaces.md` |
| 122 | [Structured Concurrency](structured-concurrency.md) | T-411 | 3 | `syllabus/02-java/concurrency/structured-concurrency.md` |
| 123 | [TreeMap, TreeSet, and the Navigable Hierarchy](treemap-treeset-and-navigable-hierarchy.md) | T-203 | 2 | `syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md` |
| 124 | [Fail-Fast vs. Weakly-Consistent Iterators](fail-fast-vs-weakly-consistent-iterators.md) | T-208 | 3 | `syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md` |
| 125 | [CopyOnWriteArrayList and Copy-on-Write Trade-offs](copyonwritearraylist-and-copy-on-write-tradeoffs.md) | T-206 | 3 | `syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md` |
| 126 | [ForkJoinPool and Work-Stealing](forkjoinpool-and-work-stealing.md) | T-408 | 3 | `syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md` |
| 127 | [Strings: Interning, Compact Strings, and Builders](strings-interning-compact-strings-and-builders.md) | T-106 | 3 | `syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md` |
| 128 | [ArrayDeque Internals and the Legacy Stack/Vector Problem](arraydeque-internals-and-the-legacy-stack-problem.md) | T-204 | 3 | `syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md` |
| 129 | [Reflection and Dynamic Proxies](reflection-and-dynamic-proxies.md) | T-113 | 3 | `syllabus/02-java/language-core/reflection-and-dynamic-proxies.md` |
| 130 | [Optional and Null Strategy](optional-and-null-strategy.md) | T-109 | 3 | `syllabus/02-java/language-core/optional-and-null-strategy.md` |
| 131 | [ClassLoaders and Class Initialization](classloaders-and-class-initialization.md) | T-114 | 3 | `syllabus/02-java/language-core/classloaders-and-class-initialization.md` |
| 132 | [Scoped Values and ThreadLocal Migration](scoped-values-and-threadlocal-migration.md) | T-412 | 3 | `syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md` |
| 133 | [Records, Sealed Types, and Pattern Matching](records-sealed-types-and-pattern-matching.md) | T-110 | 2 | `syllabus/02-java/language-core/records-sealed-types-and-pattern-matching.md` |
| 134 | [Annotations and Annotation Processing](annotations-and-annotation-processing.md) | T-112 | 3 | `syllabus/02-java/language-core/annotations-and-annotation-processing.md` |
| 135 | [Enums, EnumMap, and EnumSet](enums-enummap-and-enumset.md) | T-111 | 3 | `syllabus/02-java/language-core/enums-enummap-and-enumset.md` |
| 136 | [Serialization Hazards and Alternatives](serialization-hazards-and-alternatives.md) | T-115 | 3 | `syllabus/02-java/language-core/serialization-hazards-and-alternatives.md` |
| 137 | [Git Internals and Collaboration Workflows](git-internals-and-collaboration-workflows.md) | — (no blueprint T-code) | 2 | `syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md` |

## New-Writing Domain Decks (T-1800s/T-1900s/T-2000s, no embedded `## Flashcards` section)

**Added 2026-09-07.** `01-computer-science-foundations`, `03-data-structures-algorithms`, `18-engineering-practices`, and `19-leadership-staff` are the four "new-writing" syllabus domains from Phase 5 of `00-project/syllabus-transformation-plan.md` — written directly to L1–L4 depth from the start, using the newer 20-section syllabus topic template, which carries no embedded `## Flashcards` section to extract from. This is the flashcards third of the same "cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters" backlog whose cheat-sheets third closed 2026-09-06 (see `cheat-sheets/README.md`'s "New-Writing Domain Cheat Sheets" section) — every deck below is authored directly from its chapter's full text (definitions, decision points, and its own concrete measured numbers, named bugs, or named historical incidents/frameworks), not copied from a pre-existing section, and carries the same no-fabrication discipline as every other deck in this file. `git-internals-and-collaboration-workflows.md` (18-engineering-practices) already had a deck from an earlier batch (row 137 above) and is not repeated here.

Built as five parallel, bounded batches (one per domain, `03-data-structures-algorithms` split into two sub-batches of 9 and 8), each reading its assigned chapter fully before writing. Verified after writing: front-matter fields and YAML validity, exactly one H1 per file, each card's full Prompt/Answer/Why-it-matters/Common-trap/Related quintet present, and every canonical/Related link resolved against the real filesystem — 31 decks, 174 cards, zero broken links.

| # | Deck | Topic ID | Cards | Canonical Chapter |
|---|---|---|---|---|
| N1 | [Algorithmic Complexity and Big-O, From First Principles](algorithmic-complexity-and-big-o-from-first-principles.md) | T-2001 | 6 | `syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md` |
| N2 | [How a Computer Executes a Program](how-a-computer-executes-a-program.md) | T-2002 | 6 | `syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md` |
| N3 | [Number Representation](number-representation.md) | T-2003 | 6 | `syllabus/01-computer-science-foundations/number-representation.md` |
| N4 | [The OS Process/Thread Model](os-process-thread-model.md) | T-2004 | 6 | `syllabus/01-computer-science-foundations/os-process-thread-model.md` |
| N5 | [Networking Basics](networking-basics.md) | T-2005 | 6 | `syllabus/01-computer-science-foundations/networking-basics.md` |
| N6 | [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md) | T-2101 | 5 | `syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md` |
| N7 | [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) | T-2102 | 5 | `syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md` |
| N8 | [Binary Search and Search on Answer](binary-search-and-search-on-answer.md) | T-2103 | 5 | `syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md` |
| N9 | [Linked Lists and In-Place Manipulation](linked-lists-and-in-place-manipulation.md) | T-2104 | 5 | `syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md` |
| N10 | [Stacks and the Monotonic Stack](stacks-and-monotonic-stack.md) | T-2105 | 5 | `syllabus/03-data-structures-algorithms/stacks-and-monotonic-stack.md` |
| N11 | [Heaps, Top-K, and K-Way Merge](heaps-top-k-and-k-way-merge.md) | T-2106 | 5 | `syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md` |
| N12 | [Trees, BST, and Traversal Patterns](trees-bst-and-traversal-patterns.md) | T-2107 | 5 | `syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md` |
| N13 | [Graphs: BFS, DFS, and Shortest Paths](graphs-bfs-dfs-and-shortest-paths.md) | T-2108 | 5 | `syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md` |
| N14 | [Backtracking and Pruning](backtracking-and-pruning.md) | T-2109 | 6 | `syllabus/03-data-structures-algorithms/backtracking-and-pruning.md` |
| N15 | [Dynamic Programming: 1D, 2D, Knapsack, and Intervals](dynamic-programming.md) | T-2110 | 6 | `syllabus/03-data-structures-algorithms/dynamic-programming.md` |
| N16 | [Intervals: Merging and Sweep Line](intervals-merging-and-sweep-line.md) | T-2111 | 6 | `syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md` |
| N17 | [Greedy and the Exchange Argument](greedy-and-the-exchange-argument.md) | T-2112 | 5 | `syllabus/03-data-structures-algorithms/greedy-and-the-exchange-argument.md` |
| N18 | [Bit Manipulation](bit-manipulation.md) | T-2113 | 6 | `syllabus/03-data-structures-algorithms/bit-manipulation.md` |
| N19 | [Tries and Prefix Structures](tries-and-prefix-structures.md) | T-2114 | 5 | `syllabus/03-data-structures-algorithms/tries-and-prefix-structures.md` |
| N20 | [Design-Style Coding Problems](design-style-coding-problems.md) | T-2115 | 5 | `syllabus/03-data-structures-algorithms/design-style-coding-problems.md` |
| N21 | [Concurrency Coding Problems](concurrency-coding-problems.md) | T-2116 | 6 | `syllabus/03-data-structures-algorithms/concurrency-coding-problems.md` |
| N22 | [Advanced Structures: Segment Tree, Fenwick, Rolling Hash](advanced-structures-segment-tree-fenwick-rolling-hash.md) | T-2117 | 5 | `syllabus/03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md` |
| N23 | [Code Review: Standards and Practice](code-review-standards-and-practice.md) | T-1801 | 6 | `syllabus/18-engineering-practices/code-review-standards-and-practice.md` |
| N24 | [Architecture Decision Records and Technical Writing for Engineers](architecture-decision-records-and-technical-writing.md) | T-1802 | 6 | `syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md` |
| N25 | [Working with Legacy Code](working-with-legacy-code.md) | T-1803 | 6 | `syllabus/18-engineering-practices/working-with-legacy-code.md` |
| N26 | [Refactoring Discipline](refactoring-discipline.md) | T-1804 | 6 | `syllabus/18-engineering-practices/refactoring-discipline.md` |
| N27 | [Mentoring and Developing Others](mentoring-and-developing-others.md) | T-1901 | 6 | `syllabus/19-leadership-staff/mentoring-and-developing-others.md` |
| N28 | [Cross-Team Influence Without Authority](cross-team-influence-without-authority.md) | T-1902 | 6 | `syllabus/19-leadership-staff/cross-team-influence-without-authority.md` |
| N29 | [Leading Migrations and Large Technical Change](leading-migrations-and-large-technical-change.md) | T-1903 | 6 | `syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md` |
| N30 | [Technical Debt Prioritization and Advocacy](technical-debt-prioritization-and-advocacy.md) | T-1904 | 6 | `syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md` |
| N31 | [Design Reviews and RFCs as Organizational Practice](design-reviews-and-rfcs-as-organizational-practice.md) | T-1905 | 6 | `syllabus/19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md` |

**Added 2026-09-07 (batch 2): the 5 Junior Fundamentals chapters (T-2200s reserved range).** These postdate the batch above — they didn't exist when it closed — and were found and closed the same day they were discovered missing, matching `cheat-sheets/README.md`'s own second batch for the same 5 chapters.

| N32 | [Java OOP Fundamentals: Classes, Objects, and Interfaces](java-oop-fundamentals-classes-objects-and-interfaces.md) | T-2201 | 5 | `syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md` |
| N33 | [SQL and Relational Database Fundamentals](sql-and-relational-database-fundamentals.md) | T-2202 | 5 | `syllabus/06-databases/sql-and-relational-database-fundamentals.md` |
| N34 | [Spring MVC Fundamentals](spring-mvc-fundamentals.md) | T-2203 | 5 | `syllabus/05-spring/spring-mvc-fundamentals.md` |
| N35 | [Unit Testing Fundamentals with JUnit](unit-testing-fundamentals-with-junit.md) | T-2204 | 5 | `syllabus/08-testing/unit-testing-fundamentals-with-junit.md` |
| N36 | [REST API Fundamentals](rest-api-fundamentals.md) | T-2205 | 9 (5 + 4 added 2026-09-11, closing a real status-code gap — see note below) | `syllabus/07-api-design/rest-api-fundamentals.md` |

**Added 2026-09-08 (batch 3): 2 more Junior Fundamentals decks**, matching `cheat-sheets/README.md`'s own third batch for the same 2 chapters, found after the user directly asked whether this repository genuinely served a low-to-high-seniority reader yet.

| N37 | [Java Syntax Fundamentals: Variables, Control Flow, and Methods](java-syntax-fundamentals-variables-control-flow-and-methods.md) | T-2206 | 5 | `syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md` |
| N38 | [Collections Usage Fundamentals: List, Map, and Set](java-collections-usage-fundamentals-list-map-and-set.md) | T-2207 | 5 | `syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md` |

**Added 2026-09-08 (batch 4): Docker and Containers Fundamentals**, matching `cheat-sheets/README.md`'s own fourth batch, found the same way.

| N39 | [Docker and Containers Fundamentals](docker-and-containers-fundamentals.md) | T-2208 | 5 | `syllabus/14-devops-containers/docker-and-containers-fundamentals.md` |

176 decks across the four new-writing-domain batches combined (137 pre-existing + 39 new-writing-domain), 633 cards — see the Frontend Decks section below for the deliverable's actual, final total including the frontend batch.

**Added 2026-09-08 (batch 5): 3 more backend Junior Fundamentals decks**, matching `cheat-sheets/README.md`'s own fifth batch for the same 3 chapters, found after the user asked directly whether `02-java` covered primitive types, JVM/JDK/JRE, access modifiers, `static`/`final`, and abstract-vs-concrete method signatures, plus a Java version-features timeline, and to cross-check a connected Notion knowledge base (read-only).

| N40 | [Java Platform Basics: JVM, JDK, JRE, and Primitive Types](java-platform-basics-jvm-jdk-jre-and-primitive-types.md) | T-2209 | 5 | `syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md` |
| N41 | [Java Modifiers and Method Signatures](java-modifiers-and-method-signatures.md) | T-2210 | 5 | `syllabus/02-java/language-core/java-modifiers-and-method-signatures.md` |
| N42 | [Java Version Features Timeline](java-version-features-timeline.md) | T-2211 | 5 | `syllabus/02-java/language-core/java-version-features-timeline.md` |

**Added 2026-09-08 (batch 6): SDLC and Agile Methodology Fundamentals**, found via a review of 6 additional Notion databases/views — see `cheat-sheets/README.md`'s own sixth batch.

| N43 | [SDLC and Agile Methodology Fundamentals](sdlc-and-agile-methodology-fundamentals.md) | T-2212 | 5 | `syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md` |

## Frontend Decks (F-codes, no IWI)

**Added 2026-09-07.** The frontend domain (`syllabus/21-frontend-web/`) uses the Master Topic Register's F-codes and a Beginner/Intermediate/Advanced/Expert tier instead of the backend's numeric IWI score, same as `cheat-sheets/`'s own Frontend Cheat Sheets section (which closed this same gap for cheat sheets on 2026-09-03). Unlike the new-writing-domain decks above, every one of these 31 chapters already has its own embedded `## Flashcards` section, written when the chapter itself was authored — this batch is a pure extraction, not new content, matching the same discipline used for the 137 pre-existing backend decks. `frontend-live-coding-and-debugging-protocol.md` (a `playbook-technical-answer`-typed chapter with no F-code) is deliberately excluded, matching the identical, already-established exclusion in `cheat-sheets/README.md`'s own Frontend Cheat Sheets table (31, not 32).

Built as two parallel batches (14 React, 17 Next.js — the same split cheat-sheets used for its own frontend batch). Every card's Prompt/Answer/Why-it-matters/Common-trap text was copied verbatim from its chapter's own `## Flashcards` section; `[[wikilink]]`-style `Related` references (self- and cross-chapter) were mechanically translated to relative Markdown links resolving from `flashcards/`'s own directory, using each target chapter's own front-matter `title:` — the same convention already established for the backend batches above. Verified: all 31 files' YAML parses, one H1 each, zero unresolved `[[wikilink]]` markers, every link resolves — 62 cards total, zero broken.

**Added 2026-09-08**: 3 new "Web & Language Fundamentals" (D-F0) decks, closing a real gap found in a repository-wide audit — this domain's own Beginner-tier chapters assumed plain JavaScript, HTML/CSS/HTTP, and TypeScript literacy that nothing here ever taught. See `00-project/frontend-topic-register.md`'s new D-F0 section.

| # | Deck | Topic ID | Tier | Canonical Chapter |
|---|---|---|---|---|
| F0.1 | [How the Web Works (HTML, CSS, the DOM, and HTTP)](how-the-web-works-html-css-dom-and-http.md) | F-001 | Beginner | `syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md` |
| F0.2 | [JavaScript Fundamentals (Variables, Functions, Closures, Asynchrony)](javascript-fundamentals-variables-functions-and-asynchrony.md) | F-002 | Beginner | `syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md` |
| F0.3 | [TypeScript Fundamentals (Types, Interfaces, Generics)](typescript-fundamentals-types-interfaces-and-generics.md) | F-003 | Beginner/Intermediate | `syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md` |
| F1 | [React Fundamentals (JSX, Components, Props, and State)](react-fundamentals-jsx-components-props-and-state.md) | F-101-F-104 | Beginner | `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md` |
| F2 | [React Hooks (useEffect and useRef)](react-hooks-useeffect-and-useref.md) | F-105/F-106 | Intermediate | `syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md` |
| F3 | [React Memoization and Context (useMemo, useCallback, useContext)](react-usememo-usecallback-and-usecontext.md) | F-107/F-108 | Intermediate | `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md` |
| F4 | [React useReducer and Custom Hooks](react-usereducer-and-custom-hooks.md) | F-109/F-110 | Intermediate | `syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md` |
| F5 | [React Component Patterns](react-component-patterns.md) | F-111 | Advanced | `syllabus/21-frontend-web/react-component-patterns.md` |
| F6 | [React Reconciliation and the Fiber Architecture](react-reconciliation-and-fiber.md) | F-112 | Advanced | `syllabus/21-frontend-web/react-reconciliation-and-fiber.md` |
| F7 | [Concurrent React (Transitions, Deferred Values, Suspense)](react-concurrent-rendering.md) | F-113 | Advanced | `syllabus/21-frontend-web/react-concurrent-rendering.md` |
| F8 | [React Forms (Controlled vs. Uncontrolled, Validation, RHF/Zod)](react-forms.md) | F-114 | Intermediate | `syllabus/21-frontend-web/react-forms.md` |
| F9 | [React Error Boundaries and Error Handling Strategy](react-error-boundaries.md) | F-115 | Intermediate | `syllabus/21-frontend-web/react-error-boundaries.md` |
| F10 | [React Accessibility (Semantic HTML, ARIA, Keyboard, Focus)](react-accessibility.md) | F-116 | Intermediate | `syllabus/21-frontend-web/react-accessibility.md` |
| F11 | [React Performance (Profiling, Memoization, Virtualization, Code-Splitting)](react-performance.md) | F-117 | Advanced | `syllabus/21-frontend-web/react-performance.md` |
| F12 | [React Testing (RTL Philosophy, Mocking, E2E with Playwright)](react-testing.md) | F-118 | Advanced | `syllabus/21-frontend-web/react-testing.md` |
| F13 | [TypeScript with React (Generics, Discriminated Unions, Exhaustiveness)](react-typescript.md) | F-119 | Advanced | `syllabus/21-frontend-web/react-typescript.md` |
| F14 | [React State Management Landscape](react-state-management.md) | F-120 | Advanced | `syllabus/21-frontend-web/react-state-management.md` |
| F15 | [Next.js Fundamentals](nextjs-fundamentals.md) | F-201 | Beginner | `syllabus/21-frontend-web/nextjs-fundamentals.md` |
| F16 | [Next.js App Router Fundamentals](nextjs-app-router-fundamentals.md) | F-202 | Beginner | `syllabus/21-frontend-web/nextjs-app-router-fundamentals.md` |
| F17 | [Next.js Server vs. Client Components](nextjs-server-vs-client-components.md) | F-203 | Intermediate | `syllabus/21-frontend-web/nextjs-server-vs-client-components.md` |
| F18 | [Next.js Data Fetching and Caching](nextjs-data-fetching-and-caching.md) | F-204 | Intermediate | `syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md` |
| F19 | [Next.js Rendering Strategies](nextjs-rendering-strategies.md) | F-205 | Intermediate | `syllabus/21-frontend-web/nextjs-rendering-strategies.md` |
| F20 | [Next.js Streaming and Suspense](nextjs-streaming-and-suspense.md) | F-206 | Advanced | `syllabus/21-frontend-web/nextjs-streaming-and-suspense.md` |
| F21 | [Next.js Route Handlers](nextjs-route-handlers.md) | F-207 | Intermediate | `syllabus/21-frontend-web/nextjs-route-handlers.md` |
| F22 | [Next.js Proxy and Edge Runtime](nextjs-proxy-and-edge-runtime.md) | F-208 | Advanced | `syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md` |
| F23 | [Next.js Metadata API and SEO](nextjs-metadata-api-and-seo.md) | F-209 | Intermediate | `syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md` |
| F24 | [Next.js Image, Font Optimization and Web Vitals](nextjs-image-font-optimization-and-web-vitals.md) | F-210 | Intermediate | `syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md` |
| F25 | [Next.js Authentication Patterns](nextjs-authentication-patterns.md) | F-211 | Advanced | `syllabus/21-frontend-web/nextjs-authentication-patterns.md` |
| F26 | [Next.js Server Actions and Mutations](nextjs-server-actions-and-mutations.md) | F-212 | Advanced | `syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md` |
| F27 | [Next.js Deployment Models](nextjs-deployment-models.md) | F-213 | Advanced | `syllabus/21-frontend-web/nextjs-deployment-models.md` |
| F28 | [Next.js Full-Stack Integration](nextjs-fullstack-integration.md) | F-214 | Expert | `syllabus/21-frontend-web/nextjs-fullstack-integration.md` |
| F29 | [Vite vs. Turbopack Build Tooling](nextjs-build-tooling-vite-vs-turbopack.md) | F-301 | Intermediate | `syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md` |
| F30 | [Next.js Styling Approaches](nextjs-styling-approaches.md) | F-302 | Intermediate | `syllabus/21-frontend-web/nextjs-styling-approaches.md` |
| F31 | [Next.js Monorepo Layout](nextjs-monorepo-layout.md) | F-303 | Advanced | `syllabus/21-frontend-web/nextjs-monorepo-layout.md` |

**Total across all groups: 207 decks** (137 backend + 39 new-writing-domain, including the 8 Junior Fundamentals decks added 2026-09-07/2026-09-08 — see above — + 31 frontend), 695 cards. With this batch, `flashcards/` reaches the same domain coverage as `cheat-sheets/` — every syllabus chapter that has ever been flagged as missing a deck now has one.

**2026-09-08 — 3 new frontend Junior Fundamentals decks.** Closing the same "assumes the basics" gap found and fixed for the Java backend domain, 3 new floor-level frontend topics (F-001 How the Web Works, F-002 JavaScript Fundamentals, F-003 TypeScript Fundamentals) each got a 5-card deck — see the new rows at the top of the Frontend Decks table above. **New total, verified directly against the file system: 210 decks (137 backend + 39 new-writing-domain + 34 frontend), 710 cards.**

**2026-09-08 — 3 more backend Junior Fundamentals decks** (T-2209, T-2210, T-2211 — see the new-writing-domain batch 5 rows above), each with 5 cards. **New total, verified directly against the file system: 213 decks (137 backend + 42 new-writing-domain + 34 frontend), 725 cards.**

**2026-09-08 — 1 more Junior Fundamentals deck** (T-2212, SDLC and Agile Methodology Fundamentals — see the new-writing-domain batch 6 row above), 5 cards. **Final total, verified directly against the file system: 214 decks (137 backend + 43 new-writing-domain + 34 frontend), 730 cards.**

**2026-09-11 — 26-deck backlog from the full 22-domain gap audit.** The same re-audit that found `cheat-sheets/`'s 26-chapter gap (diffing every `syllabus/` chapter's filename against this directory's own file list) found the identical 26 chapters, added by the full 22-domain gap audit across 2026-09-10/09-11, had zero flashcard deck. Closed in this single batch, same precedent as prior backlog batches (these chapters were never subject to a prioritization decision, they simply postdated this deliverable's last audit): Azure and GCP for Backend Engineers (T-2404, 3 cards), Bean Validation and Global Exception Handling (T-518, 3 cards), Spring Data JPA Repository Abstraction (T-510, 3 cards), Bytecode and Class File Fundamentals (T-2406, 3 cards), Java Platform Module System (T-116, 3 cards), java.time API (T-2400, 3 cards), MethodHandle and java.lang.invoke (T-2405, 3 cards), PriorityQueue Internals (T-210, 3 cards), java.util.concurrent Synchronizers (T-417, 3 cards), Window Functions and CTEs (T-2401, 3 cards), JSONB and Advanced Index Types (T-2402, 3 cards), Kafka Connect (T-2408, 3 cards), Kafka Streams and Stateful Processing (T-709, 3 cards), Retention/Log Compaction/Tiered Storage (T-706, 3 cards), Consensus Algorithms: Raft and Paxos (T-2403, 3 cards), Vector Clocks and Quorum-Based Replication (T-2407, 3 cards), CSRF/CORS/Session Security (T-1308, 3 cards), Metric Cardinality and Alert Fatigue (T-2409, 3 cards), Hiring and Team Building (T-1907, 3 cards), Incident Command (T-1906, 3 cards), Frontend Security: XSS/CSRF/CSP (F-401, 2 cards — extracted from the chapter's own existing inline deck), WebSocket and SSE for Real-Time UI (F-402, 2 cards — same), Micro-Frontends and Module Federation (F-403, 2 cards — same), SOLID Principles (T-1701, 3 cards), OOD Interview Problems (T-1702, 3 cards), Sorting Algorithms (T-2119, 3 cards). Most decks' cards were extracted directly from each chapter's own already-written inline `## Flashcards`/`## Key Takeaways` content rather than derived fresh. **New total, verified directly against the file system: 240 decks (214 prior + 26 gap-audit batch), 805 cards.**

**2026-09-11 (same day) — REST API Fundamentals (N36, T-2205) gains 4 more cards, closing a real status-code gap.** A direct user question ("do we have API status codes?") found this deck (and its cheat sheet) only ever covered `200`/`201`/`204`/`404`/`500` — the chapter itself was expanded the same day with real, executed evidence for `400`, `405`, `409`, `422`, and `304` (see `syllabus/07-api-design/rest-api-fundamentals.md`'s own updated changelog note and `practice/java/rest-api-fundamentals/README.md`). Added 4 new cards (400 vs. 422, 401 vs. 403, a real 409 business-key conflict, and conditional GET/304) rather than 5, since `401`/`403`/`429`/`502`-`504` are covered only conceptually in the chapter (real depth already exists in `12-security` and `11-system-design`'s rate-limiting chapter, cross-linked instead of duplicated) — no new card was added for a concept with no chapter-local real evidence behind it. **New total: 240 decks, 809 cards.**

## How this relates to other deliverables

- `syllabus/` — the canonical chapters these cards are drawn from. For the 137 pre-existing decks, each source chapter's own `## Flashcards` section remains in place (unchanged) as the chapter's own embedded review aid; the 31 new-writing-domain decks above have no such embedded section to leave in place, since their source template doesn't carry one.
- `cheat-sheets/` — a different grain and moment: a cheat sheet is a one-page whole-chapter refresh for the day before an interview; a flashcard is one atomic Q/A pair for ongoing spaced-repetition drilling. Neither restates the other's content verbatim.
- `00-project/learning-roadmap.md` — the source of the `Rev` (revision interval) scheduling concept this deliverable exists to make mechanically possible.
