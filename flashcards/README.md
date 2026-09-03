---
title: "Flashcards — Index"
document_type: flashcard-index
status: draft
last_updated: 2026-09-02
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

## How this relates to other deliverables

- `handbook/` — the canonical chapters these cards are extracted from; each card's `## Flashcards` section in its source chapter remains in place (unchanged) as the chapter's own embedded review aid.
- `cheat-sheets/` — a different grain and moment: a cheat sheet is a one-page whole-chapter refresh for the day before an interview; a flashcard is one atomic Q/A pair for ongoing spaced-repetition drilling. Neither restates the other's content verbatim.
- `00-project/learning-roadmap.md` — the source of the `Rev` (revision interval) scheduling concept this deliverable exists to make mechanically possible.
