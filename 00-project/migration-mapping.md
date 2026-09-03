---
title: "Migration Mapping — Phase 0"
document_type: project-planning-document
status: generated — for user review before any Phase 1 execution
version: 1.0
last_updated: 2026-09-03
depends_on:
  - 00-project/syllabus-transformation-plan.md
  - 00-project/knowledge-architecture-blueprint.md
  - 00-project/frontend-topic-register.md
---

# Migration Mapping — Phase 0

Generated per `00-project/syllabus-transformation-plan.md` Section 7.1/10 (Phase 0 — "Provenance and tooling"): a topic-ID-joined, file-by-file mapping from every git-tracked Markdown file today to its proposed home in the new `syllabus/` taxonomy (Section 3.2), produced mechanically by script rather than hand-authored, as the plan itself specifies. **This is read-only analysis. No file has been moved, renamed, or rewritten to produce this document.**

## 0. Methodology and independent verification

Per this repository's own working discipline (verify before building, never trust a prior claim at face value), the plan's own audit numbers (Section 2.1) were independently re-derived from `git ls-files` rather than reused. Three discrepancies were found and are corrected here, not in the plan document itself (which the user has already approved and this task does not edit):

1. **Total tracked Markdown files: 1088, not 1,086 as Section 2.1 states.** (Off by 2 — immaterial to any decision in the plan, but corrected for an exhaustive mapping.)
2. **`handbook/` totals 168 files (137 backend + 31 frontend), not 181 as Section 2.1 and Section 2.5 state.** The plan's 181 figure appears to predate the frontend domain's current size, or to have been a rough estimate never re-checked against `git ls-files` — either way, 137 is the real backend chapter count this mapping is built against.
3. **`interview-playbook/` totals 9 files, not 12 as Section 2.1 states.**

Two specific file-path citations in Section 7.3 do not match the actual repository layout (both are corrected in the per-file tables below, with a note on each affected row):

- `distributed-systems-failure-modes.md` is cited as `handbook/architecture/distributed-systems-failure-modes.md`; it actually lives at `handbook/system-design/distributed-systems-failure-modes.md`.
- `twelve-factor-config.md` is cited as `handbook/cloud/twelve-factor-config.md`; it actually lives at `handbook/system-design/twelve-factor-config.md`.

One additional file, `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md`, is not explicitly assigned to a new domain anywhere in Section 7.3's `13-observability` vs. `16-performance-jvm` split. It is placed in `13-observability` here, inferred from the split's own stated criterion ("how do I know something is wrong in production") — flagged for explicit confirmation rather than silently decided.

None of these six items change the plan's conclusions or require re-approval of anything in Section 3–13 — they are corrections to citation accuracy and one inferred placement, surfaced because Phase 0's stated purpose is exactly to catch this kind of thing before any file moves.

## 1. Executive summary

| Directory | Real file count (git-tracked .md) | Treatment |
|---|---|---|
| `study-packs/` | 278 | Stays in place; referenced wholesale from Interview Emergency Sprint / Backend Java Specialization paths — see Section 11 |
| `handbook/` | 168 | Physically relocated (`git mv`) into the new domain taxonomy — see Section 3 |
| `cheat-sheets/` | 164 | Stays in place; referenced from its topic's new canonical domain — see Section 6 |
| `flashcards/` | 138 | Stays in place; referenced from its topic's new canonical domain — see Section 7 |
| `practice/` | 138 | Stays in place; referenced from topics/learning paths — see Section 10 |
| `production-cookbook/` | 137 | Stays in place; referenced from its topic's new canonical domain — see Section 8 |
| `architecture-atlas/` | 18 | Stays in place; referenced from `11-system-design/case-studies/` — see Section 9 |
| `behavioral-handbook/` | 16 | Physically relocated into `20-interview-preparation/behavioral/` — see Section 4 |
| `interview-playbook/` | 9 | Split: most subdirs relocate into `20-interview-preparation/*` or `21-frontend-web`; `company-prep/` stays put and private — see Section 5 |
| `00-project/` | 8 | Stays in place; referenced as provenance from `00-overview/` — see Section 12 |
| `archive/` | 6 | No migration — stays archived as-is — see Section 13 |
| `(root)/` | 5 | Content preserved; framing rewritten in Phase 1 (not Phase 0 — see note below) — see Section 13 |
| `templates/` | 2 | Stays in place; referenced from Topic Spec and `18-engineering-practices` — see Section 13 |
| `resources/` | 1 | Stays in place; folded into `00-overview/` as provenance — see Section 13 |
| **Total** | **1088** | |

**Note on root files:** Section 2.1's inventory table states root `.md` files are "rewritten in Phase 0", but Section 10's own Phase 0 definition ("Provenance and tooling") does not include any root-file rewrite — that work is actually scoped under Phase 1 ("Scaffolding... write the new root `README.md`/`CLAUDE.md` framing"). This mapping follows Section 10's phase definitions, not Section 2.1's summary line, since Section 10 is the more specific and more recently-reasoned part of the plan. Flagged here as a minor internal inconsistency in the approved plan, not acted on.

## 2. Domain-level mapping (corrected against real file counts)

| New domain | Handbook chapters mapped (real count) | Status |
|---|---|---|
| `00-overview/` | — | Provenance only — see Section 12 (`00-project/`) |
| `01-computer-science-foundations/` | — | **New domain — zero existing handbook chapters** (per plan Section 7.6) |
| `02-java/` | — | 0 chapter(s) relocated via `git mv` (Phase 3) |
| `03-data-structures-algorithms/` | — | **New domain — zero existing handbook chapters** (per plan Section 7.6) |
| `04-software-design/` | 1 | 1 chapter(s) relocated via `git mv` (Phase 3) |
| `05-spring/` | 9 | 9 chapter(s) relocated via `git mv` (Phase 3) |
| `06-databases/` | 14 | 14 chapter(s) relocated via `git mv` (Phase 3) |
| `07-api-design/` | 2 | 2 chapter(s) relocated via `git mv` (Phase 3) |
| `08-testing/` | 7 | 7 chapter(s) relocated via `git mv` (Phase 3) |
| `09-messaging-event-driven/` | 9 | 9 chapter(s) relocated via `git mv` (Phase 3) |
| `10-distributed-systems/` | 5 | 5 chapter(s) relocated via `git mv` (Phase 3) |
| `11-system-design/` | 9 | 9 chapter(s) relocated via `git mv` (Phase 3) |
| `12-security/` | 8 | 8 chapter(s) relocated via `git mv` (Phase 3) |
| `13-observability/` | 4 | 4 chapter(s) relocated via `git mv` (Phase 3) |
| `14-devops-containers/` | 4 | 4 chapter(s) relocated via `git mv` (Phase 3) |
| `15-cloud/` | 3 | 3 chapter(s) relocated via `git mv` (Phase 3) |
| `16-performance-jvm/` | 3 | 3 chapter(s) relocated via `git mv` (Phase 3) |
| `17-architecture/` | 9 | 9 chapter(s) relocated via `git mv` (Phase 3) |
| `18-engineering-practices/` | 1 | 1 chapter(s) relocated via `git mv` (Phase 3) |
| `19-leadership-staff/` | — | **New domain — zero existing handbook chapters** (per plan Section 7.6) |
| `20-interview-preparation/` | — | Sourced from `behavioral-handbook/`, `interview-playbook/`, `practice/mock-interviews/` — see Sections 4–5 |
| `21-frontend-web/` | 31 | 31 chapter(s) relocated via `git mv` (Phase 3) |

Handbook chapters whose new-domain placement required a correction or an inference beyond what Section 7.3 states verbatim:

- `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` → `13-observability/`: Not explicitly named in plan Section 7.3 — inferred from the stated 13-observability split criterion ("how do I know something is wrong in production")
- `handbook/system-design/distributed-systems-failure-modes.md` → `10-distributed-systems/`: Plan Section 7.3 cites this as handbook/architecture/distributed-systems-failure-modes.md — it is actually in handbook/system-design/. Path citation corrected here; domain placement (10-distributed-systems) matches stated intent.
- `handbook/system-design/twelve-factor-config.md` → `15-cloud/`: Plan Section 7.3 cites this as handbook/cloud/twelve-factor-config.md — it is actually in handbook/system-design/. Path citation corrected here; domain placement (15-cloud) matches stated intent.

## 3. `handbook/` — full mapping (168 files, physically relocated)

Every chapter below moves via `git mv` in Phase 3, one domain at a time, per the plan's Section 10. Ordered by new domain, then old path.

| Old path | Topic ID | New path | Note |
|---|---|---|---|
| `handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md` | T-204 | `02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md` |  |
| `handbook/collections/arraylist-and-linkedlist-internals.md` | T-202 | `02-java/collections/arraylist-and-linkedlist-internals.md` |  |
| `handbook/collections/blockingqueue-family.md` | T-207 | `02-java/collections/blockingqueue-family.md` |  |
| `handbook/collections/collection-selection-decision-matrix.md` | T-209 | `02-java/collections/collection-selection-decision-matrix.md` |  |
| `handbook/collections/concurrenthashmap-internals.md` | T-205 | `02-java/collections/concurrenthashmap-internals.md` |  |
| `handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md` | T-206 | `02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md` |  |
| `handbook/collections/fail-fast-vs-weakly-consistent-iterators.md` | T-208 | `02-java/collections/fail-fast-vs-weakly-consistent-iterators.md` |  |
| `handbook/collections/hashmap-internals.md` | T-201 | `02-java/collections/hashmap-internals.md` |  |
| `handbook/collections/treemap-treeset-and-navigable-hierarchy.md` | T-203 | `02-java/collections/treemap-treeset-and-navigable-hierarchy.md` |  |
| `handbook/concurrency/atomics-cas-and-the-aba-problem.md` | T-405 | `02-java/concurrency/atomics-cas-and-the-aba-problem.md` |  |
| `handbook/concurrency/completablefuture-and-async-composition.md` | T-407 | `02-java/concurrency/completablefuture-and-async-composition.md` |  |
| `handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` | T-409 | `02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` |  |
| `handbook/concurrency/executors-and-thread-pool-sizing.md` | T-406 | `02-java/concurrency/executors-and-thread-pool-sizing.md` |  |
| `handbook/concurrency/foreign-function-and-memory-api.md` | T-416/T-414 | `02-java/concurrency/foreign-function-and-memory-api.md` |  |
| `handbook/concurrency/forkjoinpool-and-work-stealing.md` | T-408 | `02-java/concurrency/forkjoinpool-and-work-stealing.md` |  |
| `handbook/concurrency/java-memory-model-and-volatile.md` | T-401/T-402 | `02-java/concurrency/java-memory-model-and-volatile.md` |  |
| `handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md` | T-404 | `02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md` |  |
| `handbook/concurrency/scoped-values-and-threadlocal-migration.md` | T-412 | `02-java/concurrency/scoped-values-and-threadlocal-migration.md` |  |
| `handbook/concurrency/structured-concurrency.md` | T-411 | `02-java/concurrency/structured-concurrency.md` |  |
| `handbook/concurrency/threadlocal-mediated-classloader-leaks.md` | T-413 | `02-java/concurrency/threadlocal-mediated-classloader-leaks.md` |  |
| `handbook/concurrency/varhandles-and-unsafe.md` | T-415 | `02-java/concurrency/varhandles-and-unsafe.md` |  |
| `handbook/concurrency/virtual-threads.md` | T-410 | `02-java/concurrency/virtual-threads.md` |  |
| `handbook/jvm/escape-analysis-and-scalar-replacement.md` | T-309 | `02-java/jvm-internals/escape-analysis-and-scalar-replacement.md` |  |
| `handbook/jvm/g1-remembered-sets-and-write-barriers.md` | — | `02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md` |  |
| `handbook/jvm/gc-fundamentals-and-log-analysis.md` | T-303/T-306/T-306/T-303 | `02-java/jvm-internals/gc-fundamentals-and-log-analysis.md` |  |
| `handbook/jvm/gc-roots-reachability-and-reference-strength.md` | T-303 | `02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md` |  |
| `handbook/jvm/jit-tiered-compilation-and-deoptimization.md` | — | `02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md` |  |
| `handbook/jvm/jvm-flags-and-container-ergonomics.md` | — | `02-java/jvm-internals/jvm-flags-and-container-ergonomics.md` |  |
| `handbook/jvm/jvm-memory-layout-and-runtime-regions.md` | — | `02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md` |  |
| `handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md` | — | `02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md` |  |
| `handbook/jvm/native-memory-direct-buffers-and-off-heap.md` | T-311 | `02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md` |  |
| `handbook/jvm/object-layout-headers-and-compressed-oops.md` | T-302 | `02-java/jvm-internals/object-layout-headers-and-compressed-oops.md` |  |
| `handbook/jvm/safepoints-and-stop-the-world-mechanics.md` | T-310 | `02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md` |  |
| `handbook/jvm/zgc-and-shenandoah-concurrent-collection.md` | T-305 | `02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md` |  |
| `handbook/java-core/annotations-and-annotation-processing.md` | T-112 | `02-java/language-core/annotations-and-annotation-processing.md` |  |
| `handbook/java-core/classloaders-and-class-initialization.md` | T-114 | `02-java/language-core/classloaders-and-class-initialization.md` |  |
| `handbook/java-core/enums-enummap-and-enumset.md` | T-111 | `02-java/language-core/enums-enummap-and-enumset.md` |  |
| `handbook/java-core/equals-hashcode-and-comparable-contracts.md` | T-101 | `02-java/language-core/equals-hashcode-and-comparable-contracts.md` |  |
| `handbook/java-core/exception-design-and-hierarchy-strategy.md` | T-105 | `02-java/language-core/exception-design-and-hierarchy-strategy.md` |  |
| `handbook/java-core/generics-erasure-and-pecs.md` | T-104 | `02-java/language-core/generics-erasure-and-pecs.md` |  |
| `handbook/java-core/immutability-and-defensive-copying.md` | T-103 | `02-java/language-core/immutability-and-defensive-copying.md` |  |
| `handbook/java-core/lambdas-and-functional-interfaces.md` | T-108 | `02-java/language-core/lambdas-and-functional-interfaces.md` |  |
| `handbook/java-core/optional-and-null-strategy.md` | T-109 | `02-java/language-core/optional-and-null-strategy.md` |  |
| `handbook/java-core/polymorphism-and-dynamic-dispatch.md` | T-102 | `02-java/language-core/polymorphism-and-dynamic-dispatch.md` |  |
| `handbook/java-core/records-sealed-types-and-pattern-matching.md` | T-110 | `02-java/language-core/records-sealed-types-and-pattern-matching.md` |  |
| `handbook/java-core/reflection-and-dynamic-proxies.md` | T-113 | `02-java/language-core/reflection-and-dynamic-proxies.md` |  |
| `handbook/java-core/serialization-hazards-and-alternatives.md` | T-115 | `02-java/language-core/serialization-hazards-and-alternatives.md` |  |
| `handbook/java-core/streams-and-collectors.md` | T-107 | `02-java/language-core/streams-and-collectors.md` |  |
| `handbook/java-core/strings-interning-compact-strings-and-builders.md` | T-106 | `02-java/language-core/strings-interning-compact-strings-and-builders.md` |  |
| `handbook/architecture/design-patterns-applied.md` | T-914 | `04-software-design/design-patterns-applied.md` |  |
| `handbook/spring/auto-configuration-and-bean-lifecycle.md` | T-506/T-501 | `05-spring/auto-configuration-and-bean-lifecycle.md` |  |
| `handbook/spring/security-filter-chain.md` | T-511 | `05-spring/security-filter-chain.md` |  |
| `handbook/spring/spring-actuator-health-and-observability-hooks.md` | T-516 | `05-spring/spring-actuator-health-and-observability-hooks.md` |  |
| `handbook/spring/spring-bean-scopes-and-proxy-modes.md` | T-502 | `05-spring/spring-bean-scopes-and-proxy-modes.md` |  |
| `handbook/spring/spring-cache-abstraction-and-pitfalls.md` | T-514 | `05-spring/spring-cache-abstraction-and-pitfalls.md` |  |
| `handbook/spring/spring-framework-vs-spring-boot.md` | T-506/T-501 | `05-spring/spring-framework-vs-spring-boot.md` |  |
| `handbook/spring/spring-testing-slices-and-context-caching.md` | T-517 | `05-spring/spring-testing-slices-and-context-caching.md` |  |
| `handbook/spring/spring-webflux-and-reactive-programming.md` | T-509 | `05-spring/spring-webflux-and-reactive-programming.md` |  |
| `handbook/spring/transactional-proxy-mechanics-and-propagation.md` | T-503/T-504/T-505 | `05-spring/transactional-proxy-mechanics-and-propagation.md` |  |
| `handbook/databases/connection-pooling-and-sizing.md` | T-607 | `06-databases/connection-pooling-and-sizing.md` |  |
| `handbook/databases/data-modelling-and-explicit-join-tables.md` | T-605/T-608 | `06-databases/data-modelling-and-explicit-join-tables.md` |  |
| `handbook/databases/hibernate-flush-modes-and-batch-writes.md` | T-606 | `06-databases/hibernate-flush-modes-and-batch-writes.md` |  |
| `handbook/databases/hibernate-second-level-and-query-cache.md` | T-603 | `06-databases/hibernate-second-level-and-query-cache.md` |  |
| `handbook/databases/index-structures-btree-composite-covering.md` | T-609 | `06-databases/index-structures-btree-composite-covering.md` |  |
| `handbook/databases/isolation-levels-and-concurrency-anomalies.md` | T-611 | `06-databases/isolation-levels-and-concurrency-anomalies.md` |  |
| `handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md` | T-601/T-602 | `06-databases/jpa-entity-lifecycle-and-the-n1-problem.md` |  |
| `handbook/databases/locks-deadlocks-and-lock-escalation.md` | T-613 | `06-databases/locks-deadlocks-and-lock-escalation.md` |  |
| `handbook/databases/mvcc-vacuum-and-bloat.md` | T-612 | `06-databases/mvcc-vacuum-and-bloat.md` |  |
| `handbook/databases/optimistic-vs-pessimistic-locking.md` | T-604 | `06-databases/optimistic-vs-pessimistic-locking.md` |  |
| `handbook/databases/query-planning-and-explain-analyze.md` | T-610 | `06-databases/query-planning-and-explain-analyze.md` |  |
| `handbook/databases/replication-read-replicas-and-replica-lag.md` | T-615 | `06-databases/replication-read-replicas-and-replica-lag.md` |  |
| `handbook/databases/table-partitioning-and-sharding-strategies.md` | T-614 | `06-databases/table-partitioning-and-sharding-strategies.md` |  |
| `handbook/databases/zero-downtime-schema-migration.md` | T-616 | `06-databases/zero-downtime-schema-migration.md` |  |
| `handbook/system-design/api-design.md` | T-803 | `07-api-design/api-design.md` |  |
| `handbook/system-design/api-gateway-bff-and-edge-concerns.md` | T-911 | `07-api-design/api-gateway-bff-and-edge-concerns.md` |  |
| `handbook/testing/contract-testing-for-services.md` | T-1105 | `08-testing/contract-testing-for-services.md` |  |
| `handbook/testing/integration-testing-against-real-dependencies.md` | T-1104 | `08-testing/integration-testing-against-real-dependencies.md` |  |
| `handbook/testing/junit5-architecture-and-advanced-features.md` | T-1102 | `08-testing/junit5-architecture-and-advanced-features.md` |  |
| `handbook/testing/mutation-and-property-based-testing.md` | T-1107 | `08-testing/mutation-and-property-based-testing.md` |  |
| `handbook/testing/performance-and-load-testing-methodology.md` | T-1106 | `08-testing/performance-and-load-testing-methodology.md` |  |
| `handbook/testing/test-strategy-and-test-doubles.md` | T-1101/T-1103 | `08-testing/test-strategy-and-test-doubles.md` |  |
| `handbook/testing/writing-tests-live-in-an-interview.md` | T-1108 | `08-testing/writing-tests-live-in-an-interview.md` |  |
| `handbook/architecture/event-driven-architecture-integration-styles.md` | T-906 | `09-messaging-event-driven/event-driven-architecture-integration-styles.md` |  |
| `handbook/architecture/event-sourcing-and-its-real-costs.md` | T-905 | `09-messaging-event-driven/event-sourcing-and-its-real-costs.md` |  |
| `handbook/kafka/consumer-groups-and-rebalancing.md` | T-703 | `09-messaging-event-driven/consumer-groups-and-rebalancing.md` |  |
| `handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md` | T-707 | `09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md` |  |
| `handbook/kafka/delivery-semantics-and-exactly-once.md` | T-704 | `09-messaging-event-driven/delivery-semantics-and-exactly-once.md` |  |
| `handbook/kafka/kafka-architecture-fundamentals.md` | T-701/T-702/T-703/T-704/T-705 | `09-messaging-event-driven/kafka-architecture-fundamentals.md` |  |
| `handbook/kafka/producer-semantics-and-partition-keys.md` | T-702/T-705 | `09-messaging-event-driven/producer-semantics-and-partition-keys.md` |  |
| `handbook/kafka/schema-registry-and-compatibility-evolution.md` | T-708 | `09-messaging-event-driven/schema-registry-and-compatibility-evolution.md` |  |
| `handbook/system-design/messaging-patterns-and-change-data-capture.md` | T-710 | `09-messaging-event-driven/messaging-patterns-and-change-data-capture.md` |  |
| `handbook/system-design/cap-theorem-and-consistency-models.md` | T-807 | `10-distributed-systems/cap-theorem-and-consistency-models.md` |  |
| `handbook/system-design/data-partitioning-and-consistent-hashing.md` | T-806 | `10-distributed-systems/data-partitioning-and-consistent-hashing.md` |  |
| `handbook/system-design/distributed-systems-failure-modes.md` | T-909 | `10-distributed-systems/distributed-systems-failure-modes.md` | Plan Section 7.3 cites this as handbook/architecture/distributed-systems-failure-modes.md — it is actually in handbook/system-design/. Path citation corrected here; domain placement (10-distributed-systems) matches stated intent. |
| `handbook/system-design/distributed-transactions-saga-and-outbox.md` | T-618 | `10-distributed-systems/distributed-transactions-saga-and-outbox.md` |  |
| `handbook/system-design/multi-region-failover-and-disaster-recovery.md` | T-814 | `10-distributed-systems/multi-region-failover-and-disaster-recovery.md` |  |
| `handbook/system-design/caching-strategies-and-invalidation.md` | T-804 | `11-system-design/caching-strategies-and-invalidation.md` |  |
| `handbook/system-design/idempotency.md` | T-809 | `11-system-design/idempotency.md` |  |
| `handbook/system-design/load-balancing-service-discovery-and-health-checking.md` | T-805 | `11-system-design/load-balancing-service-discovery-and-health-checking.md` |  |
| `handbook/system-design/rate-limiting-and-throttling-algorithms.md` | T-808 | `11-system-design/rate-limiting-and-throttling-algorithms.md` |  |
| `handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md` | T-812 | `11-system-design/realtime-delivery-websocket-sse-and-long-polling.md` |  |
| `handbook/system-design/resilience-patterns.md` | T-515 | `11-system-design/resilience-patterns.md` |  |
| `handbook/system-design/search-and-indexing-systems.md` | T-810 | `11-system-design/search-and-indexing-systems.md` |  |
| `handbook/system-design/storage-selection-tradeoffs.md` | T-617/T-811 | `11-system-design/storage-selection-tradeoffs.md` |  |
| `handbook/system-design/system-design-method-and-estimation.md` | T-801/T-802 | `11-system-design/system-design-method-and-estimation.md` |  |
| `handbook/security/applied-cryptography-hashing-signing-tls.md` | T-1303 | `12-security/applied-cryptography-hashing-signing-tls.md` |  |
| `handbook/security/authn-authz-rbac-vs-abac.md` | T-1302 | `12-security/authn-authz-rbac-vs-abac.md` |  |
| `handbook/security/injection-input-validation-output-encoding.md` | T-1305 | `12-security/injection-input-validation-output-encoding.md` |  |
| `handbook/security/multi-tenancy-isolation-models.md` | T-1307 | `12-security/multi-tenancy-isolation-models.md` |  |
| `handbook/security/oauth2-oidc-and-jwt.md` | T-512/T-513 | `12-security/oauth2-oidc-and-jwt.md` |  |
| `handbook/security/owasp-top-10-for-backend-services.md` | T-1301 | `12-security/owasp-top-10-for-backend-services.md` |  |
| `handbook/security/secrets-management-and-key-rotation.md` | T-1304 | `12-security/secrets-management-and-key-rotation.md` |  |
| `handbook/security/supply-chain-security-sbom-and-dependency-risk.md` | T-1306 | `12-security/supply-chain-security-sbom-and-dependency-risk.md` |  |
| `handbook/performance/incident-response-and-blameless-postmortems.md` | T-1207 | `13-observability/incident-response-and-blameless-postmortems.md` |  |
| `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` | T-1205 | `13-observability/logging-metrics-tracing-and-opentelemetry.md` |  |
| `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` | T-1204 | `13-observability/percentiles-tail-latency-and-coordinated-omission.md` | Not explicitly named in plan Section 7.3 — inferred from the stated 13-observability split criterion ("how do I know something is wrong in production") |
| `handbook/performance/performance-methodology-and-slo-error-budgets.md` | T-1201/T-1206 | `13-observability/performance-methodology-and-slo-error-budgets.md` |  |
| `handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md` | T-1009 | `14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md` |  |
| `handbook/cloud/container-image-internals.md` | T-1001 | `14-devops-containers/container-image-internals.md` |  |
| `handbook/cloud/kubernetes-objects-scheduling-and-networking.md` | T-1002 | `14-devops-containers/kubernetes-objects-scheduling-and-networking.md` |  |
| `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md` | T-1003 | `14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md` |  |
| `handbook/cloud/aws-core-services-for-backend-engineers.md` | T-1006 | `15-cloud/aws-core-services-for-backend-engineers.md` |  |
| `handbook/cloud/cloud-cost-and-scaling-economics.md` | T-1007 | `15-cloud/cloud-cost-and-scaling-economics.md` |  |
| `handbook/system-design/twelve-factor-config.md` | T-1008 | `15-cloud/twelve-factor-config.md` | Plan Section 7.3 cites this as handbook/cloud/twelve-factor-config.md — it is actually in handbook/system-design/. Path citation corrected here; domain placement (15-cloud) matches stated intent. |
| `handbook/jvm/benchmarking-and-jmh-pitfalls.md` | T-1203 | `16-performance-jvm/benchmarking-and-jmh-pitfalls.md` |  |
| `handbook/performance/capacity-planning-and-headroom.md` | T-1208 | `16-performance-jvm/capacity-planning-and-headroom.md` |  |
| `handbook/performance/profiling-jfr-and-flame-graphs.md` | T-1202 | `16-performance-jvm/profiling-jfr-and-flame-graphs.md` |  |
| `handbook/architecture/architecture-decision-records.md` | T-916 | `17-architecture/architecture-decision-records.md` |  |
| `handbook/architecture/clean-hexagonal-architecture.md` | T-901/T-903/T-912 | `17-architecture/clean-hexagonal-architecture.md` |  |
| `handbook/architecture/cqrs-read-write-separation.md` | T-904 | `17-architecture/cqrs-read-write-separation.md` |  |
| `handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md` | T-902 | `17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md` |  |
| `handbook/architecture/ddd-tactical-design-aggregates.md` | T-903/T-901 | `17-architecture/ddd-tactical-design-aggregates.md` |  |
| `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md` | T-907/T-908 | `17-architecture/microservice-decomposition-and-monolith-tradeoff.md` |  |
| `handbook/architecture/modular-monolith-as-a-deliberate-choice.md` | T-910 | `17-architecture/modular-monolith-as-a-deliberate-choice.md` |  |
| `handbook/architecture/strangler-fig-and-migration-patterns.md` | T-912 | `17-architecture/strangler-fig-and-migration-patterns.md` |  |
| `handbook/architecture/technical-debt-and-evolutionary-architecture.md` | T-913 | `17-architecture/technical-debt-and-evolutionary-architecture.md` |  |
| `handbook/cloud/git-internals-and-collaboration-workflows.md` | — | `18-engineering-practices/git-internals-and-collaboration-workflows.md` |  |
| `handbook/frontend/nextjs-app-router-fundamentals.md` | F-202 | `21-frontend-web/nextjs-app-router-fundamentals.md` |  |
| `handbook/frontend/nextjs-authentication-patterns.md` | F-211 | `21-frontend-web/nextjs-authentication-patterns.md` |  |
| `handbook/frontend/nextjs-build-tooling-vite-vs-turbopack.md` | F-301 | `21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md` |  |
| `handbook/frontend/nextjs-data-fetching-and-caching.md` | F-204 | `21-frontend-web/nextjs-data-fetching-and-caching.md` |  |
| `handbook/frontend/nextjs-deployment-models.md` | F-213 | `21-frontend-web/nextjs-deployment-models.md` |  |
| `handbook/frontend/nextjs-fullstack-integration.md` | F-214 | `21-frontend-web/nextjs-fullstack-integration.md` |  |
| `handbook/frontend/nextjs-fundamentals.md` | F-201 | `21-frontend-web/nextjs-fundamentals.md` |  |
| `handbook/frontend/nextjs-image-font-optimization-and-web-vitals.md` | F-210 | `21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md` |  |
| `handbook/frontend/nextjs-metadata-api-and-seo.md` | F-209 | `21-frontend-web/nextjs-metadata-api-and-seo.md` |  |
| `handbook/frontend/nextjs-monorepo-layout.md` | F-303 | `21-frontend-web/nextjs-monorepo-layout.md` |  |
| `handbook/frontend/nextjs-proxy-and-edge-runtime.md` | F-208 | `21-frontend-web/nextjs-proxy-and-edge-runtime.md` |  |
| `handbook/frontend/nextjs-rendering-strategies.md` | F-205 | `21-frontend-web/nextjs-rendering-strategies.md` |  |
| `handbook/frontend/nextjs-route-handlers.md` | F-207 | `21-frontend-web/nextjs-route-handlers.md` |  |
| `handbook/frontend/nextjs-server-actions-and-mutations.md` | F-212 | `21-frontend-web/nextjs-server-actions-and-mutations.md` |  |
| `handbook/frontend/nextjs-server-vs-client-components.md` | F-203 | `21-frontend-web/nextjs-server-vs-client-components.md` |  |
| `handbook/frontend/nextjs-streaming-and-suspense.md` | F-206 | `21-frontend-web/nextjs-streaming-and-suspense.md` |  |
| `handbook/frontend/nextjs-styling-approaches.md` | F-302 | `21-frontend-web/nextjs-styling-approaches.md` |  |
| `handbook/frontend/react-accessibility.md` | F-116 | `21-frontend-web/react-accessibility.md` |  |
| `handbook/frontend/react-component-patterns.md` | F-111 | `21-frontend-web/react-component-patterns.md` |  |
| `handbook/frontend/react-concurrent-rendering.md` | F-113 | `21-frontend-web/react-concurrent-rendering.md` |  |
| `handbook/frontend/react-error-boundaries.md` | F-115 | `21-frontend-web/react-error-boundaries.md` |  |
| `handbook/frontend/react-forms.md` | F-114 | `21-frontend-web/react-forms.md` |  |
| `handbook/frontend/react-fundamentals-jsx-components-props-and-state.md` | F-101/F-104 | `21-frontend-web/react-fundamentals-jsx-components-props-and-state.md` |  |
| `handbook/frontend/react-hooks-useeffect-and-useref.md` | F-105/F-106 | `21-frontend-web/react-hooks-useeffect-and-useref.md` |  |
| `handbook/frontend/react-performance.md` | F-117 | `21-frontend-web/react-performance.md` |  |
| `handbook/frontend/react-reconciliation-and-fiber.md` | F-112 | `21-frontend-web/react-reconciliation-and-fiber.md` |  |
| `handbook/frontend/react-state-management.md` | F-120 | `21-frontend-web/react-state-management.md` |  |
| `handbook/frontend/react-testing.md` | F-118 | `21-frontend-web/react-testing.md` |  |
| `handbook/frontend/react-typescript.md` | F-119 | `21-frontend-web/react-typescript.md` |  |
| `handbook/frontend/react-usememo-usecallback-and-usecontext.md` | F-107/F-108 | `21-frontend-web/react-usememo-usecallback-and-usecontext.md` |  |
| `handbook/frontend/react-usereducer-and-custom-hooks.md` | F-109/F-110 | `21-frontend-web/react-usereducer-and-custom-hooks.md` |  |

## 4. `behavioral-handbook/` — full mapping (16 files, physically relocated)

Per Section 3.2's taxonomy tree (`20-interview-preparation/behavioral/ = behavioral-handbook/ (as-is, canonical)`), this directory relocates as a block, unchanged in content, per the Section 2.7 recommendation (open question #2, Section 14).

| Old path | Topic ID | New path |
|---|---|---|
| `behavioral-handbook/01-star-framework-and-delivery.md` | — | `20-interview-preparation/behavioral/01-star-framework-and-delivery.md` |
| `behavioral-handbook/02-story-portfolio-design.md` | — | `20-interview-preparation/behavioral/02-story-portfolio-design.md` |
| `behavioral-handbook/03-scope-impact-and-influence-framing.md` | — | `20-interview-preparation/behavioral/03-scope-impact-and-influence-framing.md` |
| `behavioral-handbook/04-production-incident-narratives.md` | — | `20-interview-preparation/behavioral/04-production-incident-narratives.md` |
| `behavioral-handbook/05-architecture-trade-off-narration.md` | — | `20-interview-preparation/behavioral/05-architecture-trade-off-narration.md` |
| `behavioral-handbook/06-conflict-and-technical-disagreement.md` | — | `20-interview-preparation/behavioral/06-conflict-and-technical-disagreement.md` |
| `behavioral-handbook/07-mentoring-and-developing-others.md` | — | `20-interview-preparation/behavioral/07-mentoring-and-developing-others.md` |
| `behavioral-handbook/08-failure-and-learning-narratives.md` | — | `20-interview-preparation/behavioral/08-failure-and-learning-narratives.md` |
| `behavioral-handbook/09-cross-team-influence-without-authority.md` | — | `20-interview-preparation/behavioral/09-cross-team-influence-without-authority.md` |
| `behavioral-handbook/10-migrations-and-large-technical-change.md` | — | `20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md` |
| `behavioral-handbook/11-technical-debt-advocacy.md` | — | `20-interview-preparation/behavioral/11-technical-debt-advocacy.md` |
| `behavioral-handbook/12-design-reviews-and-rfcs.md` | — | `20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md` |
| `behavioral-handbook/13-company-specific-frameworks.md` | — | `20-interview-preparation/behavioral/13-company-specific-frameworks.md` |
| `behavioral-handbook/14-questions-to-ask-your-interviewer.md` | — | `20-interview-preparation/behavioral/14-questions-to-ask-your-interviewer.md` |
| `behavioral-handbook/15-offer-evaluation-and-negotiation.md` | — | `20-interview-preparation/behavioral/15-offer-evaluation-and-negotiation.md` |
| `behavioral-handbook/README.md` | — | `20-interview-preparation/behavioral/INDEX.md` (content merged, not a 1:1 rename) |

## 5. `interview-playbook/` — full mapping (9 files, mixed treatment)

| Old path | New path | Note |
|---|---|---|
| `interview-playbook/README.md` | `20-interview-preparation/INDEX.md` | Content merged, not a 1:1 rename |
| `interview-playbook/behavioral/company-loop-structures-and-question-pattern-recognition.md` | `20-interview-preparation/behavioral/` | Per plan Section 7.5 item 2 — overlaps behavioral-handbook/'s scope; plan recommends consolidating under 20-interview-preparation/behavioral/ in Phase 1, not a plain relocation |
| `interview-playbook/coding/coding-interview-communication-protocol.md` | `20-interview-preparation/coding/` |  |
| `interview-playbook/company-prep/nordstrom-senior-backend-remote.md` | `20-interview-preparation/company-prep/ [PRIVATE — excluded from public/commercial build, Section 2.8]` | Names a real employer; plan proposes this stays a permanently private category, not sanitized (open question #3, Section 14) |
| `interview-playbook/frontend/frontend-live-coding-and-debugging-protocol.md` | `21-frontend-web/` |  |
| `interview-playbook/system-design/system-design-narration-and-whiteboard-discipline.md` | `20-interview-preparation/system-design/` |  |
| `interview-playbook/system-design/time-boxing-and-mid-round-changes.md` | `20-interview-preparation/system-design/` |  |
| `interview-playbook/technical-answers/technical-answer-framework.md` | `20-interview-preparation/technical-answers/` |  |
| `interview-playbook/technical-answers/trade-off-narration-and-adrs.md` | `20-interview-preparation/technical-answers/` | Also directly relevant to 18-engineering-practices (ADR discipline) — plan proposes reference, not duplication |

## 6. `cheat-sheets/` — full mapping (163 files + README, stays in place)

All 163 files stay at their current path per Section 7.4 (moving them into 21 nested domain folders would multiply broken-link risk for no organizational benefit). Each is referenced from its topic's new canonical domain via the `related:`/`canonical:`/`source:` front-matter link, updated in Phase 4.

| Path | Topic ID | New domain (referenced from) |
|---|---|---|
| `cheat-sheets/annotations-and-annotation-processing.md` | T-112 | `02-java/language-core/` |
| `cheat-sheets/api-design.md` | T-803 | `07-api-design/` |
| `cheat-sheets/api-gateway-bff-and-edge-concerns.md` | T-911 | `07-api-design/` |
| `cheat-sheets/applied-cryptography-hashing-signing-tls.md` | T-1303 | `12-security/` |
| `cheat-sheets/architecture-decision-records.md` | T-916 | `17-architecture/` |
| `cheat-sheets/arraydeque-internals-and-the-legacy-stack-problem.md` | T-204 | `02-java/collections/` |
| `cheat-sheets/arraylist-and-linkedlist-internals.md` | T-202 | `02-java/collections/` |
| `cheat-sheets/atomics-cas-and-the-aba-problem.md` | T-405 | `02-java/concurrency/` |
| `cheat-sheets/authn-authz-rbac-vs-abac.md` | T-1302 | `12-security/` |
| `cheat-sheets/auto-configuration-and-bean-lifecycle.md` | T-501 | `05-spring/` |
| `cheat-sheets/aws-core-services-for-backend-engineers.md` | T-1006 | `15-cloud/` |
| `cheat-sheets/benchmarking-and-jmh-pitfalls.md` | T-1203 | `16-performance-jvm/` |
| `cheat-sheets/blockingqueue-family.md` | T-207 | `02-java/collections/` |
| `cheat-sheets/caching-strategies-and-invalidation.md` | T-804 | `11-system-design/` |
| `cheat-sheets/cap-theorem-and-consistency-models.md` | T-807 | `10-distributed-systems/` |
| `cheat-sheets/capacity-planning-and-headroom.md` | T-1208 | `16-performance-jvm/` |
| `cheat-sheets/cicd-pipeline-design-and-deployment-strategies.md` | T-1009 | `14-devops-containers/` |
| `cheat-sheets/classloaders-and-class-initialization.md` | T-114 | `02-java/language-core/` |
| `cheat-sheets/clean-hexagonal-architecture.md` | T-901 | `17-architecture/` |
| `cheat-sheets/cloud-cost-and-scaling-economics.md` | T-1007 | `15-cloud/` |
| `cheat-sheets/collection-selection-decision-matrix.md` | T-209 | `02-java/collections/` |
| `cheat-sheets/completablefuture-and-async-composition.md` | T-407 | `02-java/concurrency/` |
| `cheat-sheets/concurrenthashmap-internals.md` | T-205 | `02-java/collections/` |
| `cheat-sheets/connection-pooling-and-sizing.md` | T-607 | `06-databases/` |
| `cheat-sheets/consumer-groups-and-rebalancing.md` | T-703 | `09-messaging-event-driven/` |
| `cheat-sheets/consumer-lag-backpressure-and-dlq-strategy.md` | T-707 | `09-messaging-event-driven/` |
| `cheat-sheets/container-image-internals.md` | T-1001 | `14-devops-containers/` |
| `cheat-sheets/contract-testing-for-services.md` | T-1105 | `08-testing/` |
| `cheat-sheets/copyonwritearraylist-and-copy-on-write-tradeoffs.md` | T-206 | `02-java/collections/` |
| `cheat-sheets/cqrs-read-write-separation.md` | T-904 | `17-architecture/` |
| `cheat-sheets/data-modelling-and-explicit-join-tables.md` | T-605/T-608 | `06-databases/` |
| `cheat-sheets/data-partitioning-and-consistent-hashing.md` | T-806 | `10-distributed-systems/` |
| `cheat-sheets/ddd-strategic-bounded-contexts-and-context-mapping.md` | T-902 | `17-architecture/` |
| `cheat-sheets/ddd-tactical-design-aggregates.md` | T-903 | `17-architecture/` |
| `cheat-sheets/deadlock-race-conditions-and-thread-diagnostics.md` | T-409 | `02-java/concurrency/` |
| `cheat-sheets/delivery-semantics-and-exactly-once.md` | T-704 | `09-messaging-event-driven/` |
| `cheat-sheets/design-patterns-applied.md` | T-914 | `04-software-design/` |
| `cheat-sheets/distributed-systems-failure-modes.md` | T-909 | `10-distributed-systems/` |
| `cheat-sheets/distributed-transactions-saga-and-outbox.md` | T-618 | `10-distributed-systems/` |
| `cheat-sheets/enums-enummap-and-enumset.md` | T-111 | `02-java/language-core/` |
| `cheat-sheets/equals-hashcode-and-comparable-contracts.md` | T-101 | `02-java/language-core/` |
| `cheat-sheets/escape-analysis-and-scalar-replacement.md` | T-309 | `02-java/jvm-internals/` |
| `cheat-sheets/event-driven-architecture-integration-styles.md` | T-906 | `09-messaging-event-driven/` |
| `cheat-sheets/event-sourcing-and-its-real-costs.md` | T-905 | `09-messaging-event-driven/` |
| `cheat-sheets/exception-design-and-hierarchy-strategy.md` | T-105 | `02-java/language-core/` |
| `cheat-sheets/executors-and-thread-pool-sizing.md` | T-406 | `02-java/concurrency/` |
| `cheat-sheets/fail-fast-vs-weakly-consistent-iterators.md` | T-208 | `02-java/collections/` |
| `cheat-sheets/foreign-function-and-memory-api.md` | T-416 | `02-java/concurrency/` |
| `cheat-sheets/forkjoinpool-and-work-stealing.md` | T-408 | `02-java/concurrency/` |
| `cheat-sheets/gc-fundamentals-and-log-analysis.md` | T-306 | `02-java/jvm-internals/` |
| `cheat-sheets/gc-roots-reachability-and-reference-strength.md` | T-303 | `02-java/jvm-internals/` |
| `cheat-sheets/generics-erasure-and-pecs.md` | T-104 | `02-java/language-core/` |
| `cheat-sheets/git-internals-and-collaboration-workflows.md` | N/A (no blueprint topic ID — see chapter's Topic register note) | `18-engineering-practices/` |
| `cheat-sheets/hashmap-internals.md` | T-201 | `02-java/collections/` |
| `cheat-sheets/hibernate-flush-modes-and-batch-writes.md` | T-606 | `06-databases/` |
| `cheat-sheets/hibernate-second-level-and-query-cache.md` | T-603 | `06-databases/` |
| `cheat-sheets/idempotency.md` | T-809 | `11-system-design/` |
| `cheat-sheets/immutability-and-defensive-copying.md` | T-103 | `02-java/language-core/` |
| `cheat-sheets/incident-response-and-blameless-postmortems.md` | T-1207 | `13-observability/` |
| `cheat-sheets/index-structures-btree-composite-covering.md` | T-609 | `06-databases/` |
| `cheat-sheets/injection-input-validation-output-encoding.md` | T-1305 | `12-security/` |
| `cheat-sheets/integration-testing-against-real-dependencies.md` | T-1104 | `08-testing/` |
| `cheat-sheets/isolation-levels-and-concurrency-anomalies.md` | T-611 | `06-databases/` |
| `cheat-sheets/java-memory-model-and-volatile.md` | T-401 | `02-java/concurrency/` |
| `cheat-sheets/jpa-entity-lifecycle-and-the-n1-problem.md` | T-601 / T-602 | `06-databases/` |
| `cheat-sheets/junit5-architecture-and-advanced-features.md` | T-1102 | `08-testing/` |
| `cheat-sheets/kafka-architecture-fundamentals.md` | T-701 | `09-messaging-event-driven/` |
| `cheat-sheets/kubernetes-objects-scheduling-and-networking.md` | T-1002 | `14-devops-containers/` |
| `cheat-sheets/kubernetes-resource-limits-probes-and-jvm-sizing.md` | T-1003 | `14-devops-containers/` |
| `cheat-sheets/lambdas-and-functional-interfaces.md` | T-108 | `02-java/language-core/` |
| `cheat-sheets/load-balancing-service-discovery-and-health-checking.md` | T-805 | `11-system-design/` |
| `cheat-sheets/locks-deadlocks-and-lock-escalation.md` | T-613 | `06-databases/` |
| `cheat-sheets/logging-metrics-tracing-and-opentelemetry.md` | T-1205 | `13-observability/` |
| `cheat-sheets/messaging-patterns-and-change-data-capture.md` | T-710 | `09-messaging-event-driven/` |
| `cheat-sheets/microservice-decomposition-and-monolith-tradeoff.md` | T-907 | `17-architecture/` |
| `cheat-sheets/modular-monolith-as-a-deliberate-choice.md` | T-910 | `17-architecture/` |
| `cheat-sheets/multi-region-failover-and-disaster-recovery.md` | T-814 | `10-distributed-systems/` |
| `cheat-sheets/multi-tenancy-isolation-models.md` | T-1307 | `12-security/` |
| `cheat-sheets/mutation-and-property-based-testing.md` | T-1107 | `08-testing/` |
| `cheat-sheets/mvcc-vacuum-and-bloat.md` | T-612 | `06-databases/` |
| `cheat-sheets/native-memory-direct-buffers-and-off-heap.md` | T-311 | `02-java/jvm-internals/` |
| `cheat-sheets/nextjs-app-router-fundamentals.md` | F-202 | `21-frontend-web/` |
| `cheat-sheets/nextjs-authentication-patterns.md` | F-211 | `21-frontend-web/` |
| `cheat-sheets/nextjs-build-tooling-vite-vs-turbopack.md` | F-301 | `21-frontend-web/` |
| `cheat-sheets/nextjs-data-fetching-and-caching.md` | F-204 | `21-frontend-web/` |
| `cheat-sheets/nextjs-deployment-models.md` | F-213 | `21-frontend-web/` |
| `cheat-sheets/nextjs-fullstack-integration.md` | F-214 | `21-frontend-web/` |
| `cheat-sheets/nextjs-fundamentals.md` | F-201 | `21-frontend-web/` |
| `cheat-sheets/nextjs-image-font-optimization-and-web-vitals.md` | F-210 | `21-frontend-web/` |
| `cheat-sheets/nextjs-metadata-api-and-seo.md` | F-209 | `21-frontend-web/` |
| `cheat-sheets/nextjs-monorepo-layout.md` | F-303 | `21-frontend-web/` |
| `cheat-sheets/nextjs-proxy-and-edge-runtime.md` | F-208 | `21-frontend-web/` |
| `cheat-sheets/nextjs-rendering-strategies.md` | F-205 | `21-frontend-web/` |
| `cheat-sheets/nextjs-route-handlers.md` | F-207 | `21-frontend-web/` |
| `cheat-sheets/nextjs-server-actions-and-mutations.md` | F-212 | `21-frontend-web/` |
| `cheat-sheets/nextjs-server-vs-client-components.md` | F-203 | `21-frontend-web/` |
| `cheat-sheets/nextjs-streaming-and-suspense.md` | F-206 | `21-frontend-web/` |
| `cheat-sheets/nextjs-styling-approaches.md` | F-302 | `21-frontend-web/` |
| `cheat-sheets/oauth2-oidc-and-jwt.md` | T-512/T-513 | `12-security/` |
| `cheat-sheets/object-layout-headers-and-compressed-oops.md` | T-302 | `02-java/jvm-internals/` |
| `cheat-sheets/optimistic-vs-pessimistic-locking.md` | T-604 | `06-databases/` |
| `cheat-sheets/optional-and-null-strategy.md` | T-109 | `02-java/language-core/` |
| `cheat-sheets/owasp-top-10-for-backend-services.md` | T-1301 | `12-security/` |
| `cheat-sheets/percentiles-tail-latency-and-coordinated-omission.md` | T-1204 | `13-observability/` |
| `cheat-sheets/performance-and-load-testing-methodology.md` | T-1106 | `08-testing/` |
| `cheat-sheets/performance-methodology-and-slo-error-budgets.md` | T-1206 | `13-observability/` |
| `cheat-sheets/polymorphism-and-dynamic-dispatch.md` | T-102 | `02-java/language-core/` |
| `cheat-sheets/producer-semantics-and-partition-keys.md` | T-702/T-705 | `09-messaging-event-driven/` |
| `cheat-sheets/profiling-jfr-and-flame-graphs.md` | T-1202 | `16-performance-jvm/` |
| `cheat-sheets/query-planning-and-explain-analyze.md` | T-610 | `06-databases/` |
| `cheat-sheets/rate-limiting-and-throttling-algorithms.md` | T-808 | `11-system-design/` |
| `cheat-sheets/react-accessibility.md` | F-116 | `21-frontend-web/` |
| `cheat-sheets/react-component-patterns.md` | F-111 | `21-frontend-web/` |
| `cheat-sheets/react-concurrent-rendering.md` | F-113 | `21-frontend-web/` |
| `cheat-sheets/react-error-boundaries.md` | F-115 | `21-frontend-web/` |
| `cheat-sheets/react-forms.md` | F-114 | `21-frontend-web/` |
| `cheat-sheets/react-fundamentals-jsx-components-props-and-state.md` | F-101-F-104 | `21-frontend-web/` |
| `cheat-sheets/react-hooks-useeffect-and-useref.md` | F-105/F-106 | `21-frontend-web/` |
| `cheat-sheets/react-performance.md` | F-117 | `21-frontend-web/` |
| `cheat-sheets/react-reconciliation-and-fiber.md` | F-112 | `21-frontend-web/` |
| `cheat-sheets/react-state-management.md` | F-120 | `21-frontend-web/` |
| `cheat-sheets/react-testing.md` | F-118 | `21-frontend-web/` |
| `cheat-sheets/react-typescript.md` | F-119 | `21-frontend-web/` |
| `cheat-sheets/react-usememo-usecallback-and-usecontext.md` | F-107/F-108 | `21-frontend-web/` |
| `cheat-sheets/react-usereducer-and-custom-hooks.md` | F-109/F-110 | `21-frontend-web/` |
| `cheat-sheets/realtime-delivery-websocket-sse-and-long-polling.md` | T-812 | `11-system-design/` |
| `cheat-sheets/records-sealed-types-and-pattern-matching.md` | T-110 | `02-java/language-core/` |
| `cheat-sheets/reentrantlock-readwritelock-and-stampedlock.md` | T-404 | `02-java/concurrency/` |
| `cheat-sheets/reflection-and-dynamic-proxies.md` | T-113 | `02-java/language-core/` |
| `cheat-sheets/replication-read-replicas-and-replica-lag.md` | T-615 | `06-databases/` |
| `cheat-sheets/resilience-patterns.md` | T-515 | `11-system-design/` |
| `cheat-sheets/safepoints-and-stop-the-world-mechanics.md` | T-310 | `02-java/jvm-internals/` |
| `cheat-sheets/schema-registry-and-compatibility-evolution.md` | T-708 | `09-messaging-event-driven/` |
| `cheat-sheets/scoped-values-and-threadlocal-migration.md` | T-412 | `02-java/concurrency/` |
| `cheat-sheets/search-and-indexing-systems.md` | T-810 | `11-system-design/` |
| `cheat-sheets/secrets-management-and-key-rotation.md` | T-1304 | `12-security/` |
| `cheat-sheets/security-filter-chain.md` | T-511 | `05-spring/` |
| `cheat-sheets/serialization-hazards-and-alternatives.md` | T-115 | `02-java/language-core/` |
| `cheat-sheets/spring-actuator-health-and-observability-hooks.md` | T-516 | `05-spring/` |
| `cheat-sheets/spring-bean-scopes-and-proxy-modes.md` | T-502 | `05-spring/` |
| `cheat-sheets/spring-cache-abstraction-and-pitfalls.md` | T-514 | `05-spring/` |
| `cheat-sheets/spring-framework-vs-spring-boot.md` | T-506 / T-501 | `05-spring/` |
| `cheat-sheets/spring-testing-slices-and-context-caching.md` | T-517 | `05-spring/` |
| `cheat-sheets/spring-webflux-and-reactive-programming.md` | T-509 | `05-spring/` |
| `cheat-sheets/storage-selection-tradeoffs.md` | T-811 | `11-system-design/` |
| `cheat-sheets/strangler-fig-and-migration-patterns.md` | T-912 | `17-architecture/` |
| `cheat-sheets/streams-and-collectors.md` | T-107 | `02-java/language-core/` |
| `cheat-sheets/strings-interning-compact-strings-and-builders.md` | T-106 | `02-java/language-core/` |
| `cheat-sheets/structured-concurrency.md` | T-411 | `02-java/concurrency/` |
| `cheat-sheets/supply-chain-security-sbom-and-dependency-risk.md` | T-1306 | `12-security/` |
| `cheat-sheets/system-design-method-and-estimation.md` | T-801 | `11-system-design/` |
| `cheat-sheets/table-partitioning-and-sharding-strategies.md` | T-614 | `06-databases/` |
| `cheat-sheets/technical-debt-and-evolutionary-architecture.md` | T-913 | `17-architecture/` |
| `cheat-sheets/test-strategy-and-test-doubles.md` | T-1103 | `08-testing/` |
| `cheat-sheets/threadlocal-mediated-classloader-leaks.md` | T-413 | `02-java/concurrency/` |
| `cheat-sheets/transactional-proxy-mechanics-and-propagation.md` | T-504 | `05-spring/` |
| `cheat-sheets/treemap-treeset-and-navigable-hierarchy.md` | T-203 | `02-java/collections/` |
| `cheat-sheets/twelve-factor-config.md` | T-1008 | `15-cloud/` |
| `cheat-sheets/varhandles-and-unsafe.md` | T-415 | `02-java/concurrency/` |
| `cheat-sheets/virtual-threads.md` | T-410 | `02-java/concurrency/` |
| `cheat-sheets/writing-tests-live-in-an-interview.md` | T-1108 | `08-testing/` |
| `cheat-sheets/zero-downtime-schema-migration.md` | T-616 | `06-databases/` |
| `cheat-sheets/zgc-and-shenandoah-concurrent-collection.md` | T-305 | `02-java/jvm-internals/` |

## 7. `flashcards/` — full mapping (137 files + README, stays in place)

All 137 files stay at their current path per Section 7.4 (moving them into 21 nested domain folders would multiply broken-link risk for no organizational benefit). Each is referenced from its topic's new canonical domain via the `related:`/`canonical:`/`source:` front-matter link, updated in Phase 4.

| Path | Topic ID | New domain (referenced from) |
|---|---|---|
| `flashcards/annotations-and-annotation-processing.md` | T-112 | `02-java/language-core/` |
| `flashcards/api-design.md` | T-803 | `07-api-design/` |
| `flashcards/api-gateway-bff-and-edge-concerns.md` | T-911 | `07-api-design/` |
| `flashcards/applied-cryptography-hashing-signing-tls.md` | T-1303 | `12-security/` |
| `flashcards/architecture-decision-records.md` | T-916 | `17-architecture/` |
| `flashcards/arraydeque-internals-and-the-legacy-stack-problem.md` | T-204 | `02-java/collections/` |
| `flashcards/arraylist-and-linkedlist-internals.md` | T-202 | `02-java/collections/` |
| `flashcards/atomics-cas-and-the-aba-problem.md` | T-405 | `02-java/concurrency/` |
| `flashcards/authn-authz-rbac-vs-abac.md` | T-1302 | `12-security/` |
| `flashcards/auto-configuration-and-bean-lifecycle.md` | T-501 | `05-spring/` |
| `flashcards/aws-core-services-for-backend-engineers.md` | T-1006 | `15-cloud/` |
| `flashcards/benchmarking-and-jmh-pitfalls.md` | T-1203 | `16-performance-jvm/` |
| `flashcards/blockingqueue-family.md` | T-207 | `02-java/collections/` |
| `flashcards/caching-strategies-and-invalidation.md` | T-804 | `11-system-design/` |
| `flashcards/cap-theorem-and-consistency-models.md` | T-807 | `10-distributed-systems/` |
| `flashcards/capacity-planning-and-headroom.md` | T-1208 | `16-performance-jvm/` |
| `flashcards/cicd-pipeline-design-and-deployment-strategies.md` | T-1009 | `14-devops-containers/` |
| `flashcards/classloaders-and-class-initialization.md` | T-114 | `02-java/language-core/` |
| `flashcards/clean-hexagonal-architecture.md` | T-901 | `17-architecture/` |
| `flashcards/cloud-cost-and-scaling-economics.md` | T-1007 | `15-cloud/` |
| `flashcards/collection-selection-decision-matrix.md` | T-209 | `02-java/collections/` |
| `flashcards/completablefuture-and-async-composition.md` | T-407 | `02-java/concurrency/` |
| `flashcards/concurrenthashmap-internals.md` | T-205 | `02-java/collections/` |
| `flashcards/connection-pooling-and-sizing.md` | T-607 | `06-databases/` |
| `flashcards/consumer-groups-and-rebalancing.md` | T-703 | `09-messaging-event-driven/` |
| `flashcards/consumer-lag-backpressure-and-dlq-strategy.md` | T-707 | `09-messaging-event-driven/` |
| `flashcards/container-image-internals.md` | T-1001 | `14-devops-containers/` |
| `flashcards/contract-testing-for-services.md` | T-1105 | `08-testing/` |
| `flashcards/copyonwritearraylist-and-copy-on-write-tradeoffs.md` | T-206 | `02-java/collections/` |
| `flashcards/cqrs-read-write-separation.md` | T-904 | `17-architecture/` |
| `flashcards/data-modelling-and-explicit-join-tables.md` | T-605/T-608 | `06-databases/` |
| `flashcards/data-partitioning-and-consistent-hashing.md` | T-806 | `10-distributed-systems/` |
| `flashcards/ddd-strategic-bounded-contexts-and-context-mapping.md` | T-902 | `17-architecture/` |
| `flashcards/ddd-tactical-design-aggregates.md` | T-903 | `17-architecture/` |
| `flashcards/deadlock-race-conditions-and-thread-diagnostics.md` | T-409 | `02-java/concurrency/` |
| `flashcards/delivery-semantics-and-exactly-once.md` | T-704 | `09-messaging-event-driven/` |
| `flashcards/design-patterns-applied.md` | T-914 | `04-software-design/` |
| `flashcards/distributed-systems-failure-modes.md` | T-909 | `10-distributed-systems/` |
| `flashcards/distributed-transactions-saga-and-outbox.md` | T-618 | `10-distributed-systems/` |
| `flashcards/enums-enummap-and-enumset.md` | T-111 | `02-java/language-core/` |
| `flashcards/equals-hashcode-and-comparable-contracts.md` | T-101 | `02-java/language-core/` |
| `flashcards/escape-analysis-and-scalar-replacement.md` | T-309 | `02-java/jvm-internals/` |
| `flashcards/event-driven-architecture-integration-styles.md` | T-906 | `09-messaging-event-driven/` |
| `flashcards/event-sourcing-and-its-real-costs.md` | T-905 | `09-messaging-event-driven/` |
| `flashcards/exception-design-and-hierarchy-strategy.md` | T-105 | `02-java/language-core/` |
| `flashcards/executors-and-thread-pool-sizing.md` | T-406 | `02-java/concurrency/` |
| `flashcards/fail-fast-vs-weakly-consistent-iterators.md` | T-208 | `02-java/collections/` |
| `flashcards/foreign-function-and-memory-api.md` | T-416 | `02-java/concurrency/` |
| `flashcards/forkjoinpool-and-work-stealing.md` | T-408 | `02-java/concurrency/` |
| `flashcards/g1-remembered-sets-and-write-barriers.md` | T-304 | `02-java/jvm-internals/` |
| `flashcards/gc-fundamentals-and-log-analysis.md` | T-306 | `02-java/jvm-internals/` |
| `flashcards/gc-roots-reachability-and-reference-strength.md` | T-303 | `02-java/jvm-internals/` |
| `flashcards/generics-erasure-and-pecs.md` | T-104 | `02-java/language-core/` |
| `flashcards/git-internals-and-collaboration-workflows.md` | — | `18-engineering-practices/` |
| `flashcards/hashmap-internals.md` | T-201 | `02-java/collections/` |
| `flashcards/hibernate-flush-modes-and-batch-writes.md` | T-606 | `06-databases/` |
| `flashcards/hibernate-second-level-and-query-cache.md` | T-603 | `06-databases/` |
| `flashcards/idempotency.md` | T-809 | `11-system-design/` |
| `flashcards/immutability-and-defensive-copying.md` | T-103 | `02-java/language-core/` |
| `flashcards/incident-response-and-blameless-postmortems.md` | T-1207 | `13-observability/` |
| `flashcards/index-structures-btree-composite-covering.md` | T-609 | `06-databases/` |
| `flashcards/injection-input-validation-output-encoding.md` | T-1305 | `12-security/` |
| `flashcards/integration-testing-against-real-dependencies.md` | T-1104 | `08-testing/` |
| `flashcards/isolation-levels-and-concurrency-anomalies.md` | T-611 | `06-databases/` |
| `flashcards/java-memory-model-and-volatile.md` | T-401 | `02-java/concurrency/` |
| `flashcards/jit-tiered-compilation-and-deoptimization.md` | T-308 | `02-java/jvm-internals/` |
| `flashcards/jpa-entity-lifecycle-and-the-n1-problem.md` | T-601 / T-602 | `06-databases/` |
| `flashcards/junit5-architecture-and-advanced-features.md` | T-1102 | `08-testing/` |
| `flashcards/jvm-flags-and-container-ergonomics.md` | T-312 | `02-java/jvm-internals/` |
| `flashcards/jvm-memory-layout-and-runtime-regions.md` | T-301 | `02-java/jvm-internals/` |
| `flashcards/kafka-architecture-fundamentals.md` | T-701 | `09-messaging-event-driven/` |
| `flashcards/kubernetes-objects-scheduling-and-networking.md` | T-1002 | `14-devops-containers/` |
| `flashcards/kubernetes-resource-limits-probes-and-jvm-sizing.md` | T-1003 | `14-devops-containers/` |
| `flashcards/lambdas-and-functional-interfaces.md` | T-108 | `02-java/language-core/` |
| `flashcards/load-balancing-service-discovery-and-health-checking.md` | T-805 | `11-system-design/` |
| `flashcards/locks-deadlocks-and-lock-escalation.md` | T-613 | `06-databases/` |
| `flashcards/logging-metrics-tracing-and-opentelemetry.md` | T-1205 | `13-observability/` |
| `flashcards/memory-leak-diagnosis-and-heap-dump-analysis.md` | T-307 | `02-java/jvm-internals/` |
| `flashcards/messaging-patterns-and-change-data-capture.md` | T-710 | `09-messaging-event-driven/` |
| `flashcards/microservice-decomposition-and-monolith-tradeoff.md` | T-907 | `17-architecture/` |
| `flashcards/modular-monolith-as-a-deliberate-choice.md` | T-910 | `17-architecture/` |
| `flashcards/multi-region-failover-and-disaster-recovery.md` | T-814 | `10-distributed-systems/` |
| `flashcards/multi-tenancy-isolation-models.md` | T-1307 | `12-security/` |
| `flashcards/mutation-and-property-based-testing.md` | T-1107 | `08-testing/` |
| `flashcards/mvcc-vacuum-and-bloat.md` | T-612 | `06-databases/` |
| `flashcards/native-memory-direct-buffers-and-off-heap.md` | T-311 | `02-java/jvm-internals/` |
| `flashcards/oauth2-oidc-and-jwt.md` | T-512/T-513 | `12-security/` |
| `flashcards/object-layout-headers-and-compressed-oops.md` | T-302 | `02-java/jvm-internals/` |
| `flashcards/optimistic-vs-pessimistic-locking.md` | T-604 | `06-databases/` |
| `flashcards/optional-and-null-strategy.md` | T-109 | `02-java/language-core/` |
| `flashcards/owasp-top-10-for-backend-services.md` | T-1301 | `12-security/` |
| `flashcards/percentiles-tail-latency-and-coordinated-omission.md` | T-1204 | `13-observability/` |
| `flashcards/performance-and-load-testing-methodology.md` | T-1106 | `08-testing/` |
| `flashcards/performance-methodology-and-slo-error-budgets.md` | T-1206 | `13-observability/` |
| `flashcards/polymorphism-and-dynamic-dispatch.md` | T-102 | `02-java/language-core/` |
| `flashcards/producer-semantics-and-partition-keys.md` | T-702/T-705 | `09-messaging-event-driven/` |
| `flashcards/profiling-jfr-and-flame-graphs.md` | T-1202 | `16-performance-jvm/` |
| `flashcards/query-planning-and-explain-analyze.md` | T-610 | `06-databases/` |
| `flashcards/rate-limiting-and-throttling-algorithms.md` | T-808 | `11-system-design/` |
| `flashcards/realtime-delivery-websocket-sse-and-long-polling.md` | T-812 | `11-system-design/` |
| `flashcards/records-sealed-types-and-pattern-matching.md` | T-110 | `02-java/language-core/` |
| `flashcards/reentrantlock-readwritelock-and-stampedlock.md` | T-404 | `02-java/concurrency/` |
| `flashcards/reflection-and-dynamic-proxies.md` | T-113 | `02-java/language-core/` |
| `flashcards/replication-read-replicas-and-replica-lag.md` | T-615 | `06-databases/` |
| `flashcards/resilience-patterns.md` | T-515 | `11-system-design/` |
| `flashcards/safepoints-and-stop-the-world-mechanics.md` | T-310 | `02-java/jvm-internals/` |
| `flashcards/schema-registry-and-compatibility-evolution.md` | T-708 | `09-messaging-event-driven/` |
| `flashcards/scoped-values-and-threadlocal-migration.md` | T-412 | `02-java/concurrency/` |
| `flashcards/search-and-indexing-systems.md` | T-810 | `11-system-design/` |
| `flashcards/secrets-management-and-key-rotation.md` | T-1304 | `12-security/` |
| `flashcards/security-filter-chain.md` | T-511 | `05-spring/` |
| `flashcards/serialization-hazards-and-alternatives.md` | T-115 | `02-java/language-core/` |
| `flashcards/spring-actuator-health-and-observability-hooks.md` | T-516 | `05-spring/` |
| `flashcards/spring-bean-scopes-and-proxy-modes.md` | T-502 | `05-spring/` |
| `flashcards/spring-cache-abstraction-and-pitfalls.md` | T-514 | `05-spring/` |
| `flashcards/spring-framework-vs-spring-boot.md` | T-506 / T-501 | `05-spring/` |
| `flashcards/spring-testing-slices-and-context-caching.md` | T-517 | `05-spring/` |
| `flashcards/spring-webflux-and-reactive-programming.md` | T-509 | `05-spring/` |
| `flashcards/storage-selection-tradeoffs.md` | T-811 | `11-system-design/` |
| `flashcards/strangler-fig-and-migration-patterns.md` | T-912 | `17-architecture/` |
| `flashcards/streams-and-collectors.md` | T-107 | `02-java/language-core/` |
| `flashcards/strings-interning-compact-strings-and-builders.md` | T-106 | `02-java/language-core/` |
| `flashcards/structured-concurrency.md` | T-411 | `02-java/concurrency/` |
| `flashcards/supply-chain-security-sbom-and-dependency-risk.md` | T-1306 | `12-security/` |
| `flashcards/system-design-method-and-estimation.md` | T-801 | `11-system-design/` |
| `flashcards/table-partitioning-and-sharding-strategies.md` | T-614 | `06-databases/` |
| `flashcards/technical-debt-and-evolutionary-architecture.md` | T-913 | `17-architecture/` |
| `flashcards/test-strategy-and-test-doubles.md` | T-1103 | `08-testing/` |
| `flashcards/threadlocal-mediated-classloader-leaks.md` | T-413 | `02-java/concurrency/` |
| `flashcards/transactional-proxy-mechanics-and-propagation.md` | T-504 | `05-spring/` |
| `flashcards/treemap-treeset-and-navigable-hierarchy.md` | T-203 | `02-java/collections/` |
| `flashcards/twelve-factor-config.md` | T-1008 | `15-cloud/` |
| `flashcards/varhandles-and-unsafe.md` | T-415 | `02-java/concurrency/` |
| `flashcards/virtual-threads.md` | T-410 | `02-java/concurrency/` |
| `flashcards/writing-tests-live-in-an-interview.md` | T-1108 | `08-testing/` |
| `flashcards/zero-downtime-schema-migration.md` | T-616 | `06-databases/` |
| `flashcards/zgc-and-shenandoah-concurrent-collection.md` | T-305 | `02-java/jvm-internals/` |

## 8. `production-cookbook/` — full mapping (136 files + README, stays in place)

All 136 files stay at their current path per Section 7.4 (moving them into 21 nested domain folders would multiply broken-link risk for no organizational benefit). Each is referenced from its topic's new canonical domain via the `related:`/`canonical:`/`source:` front-matter link, updated in Phase 4.

| Path | Topic ID | New domain (referenced from) |
|---|---|---|
| `production-cookbook/actuator-env-exposed-to-the-public-internet-via-a-wildcard-override.md` | — | `05-spring/` |
| `production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md` | — | `17-architecture/` |
| `production-cookbook/assumed-mysql-lock-escalation-risk-that-doesnt-apply-to-postgresql.md` | — | `06-databases/` |
| `production-cookbook/blame-coded-postmortem-language-truncating-an-incident-investigation.md` | — | `13-observability/` |
| `production-cookbook/blocking-jdbc-call-starving-the-webflux-event-loop.md` | — | `05-spring/` |
| `production-cookbook/boundary-condition-bug-behind-a-95-percent-coverage-figure.md` | — | `08-testing/` |
| `production-cookbook/broken-equals-hashcode-contract-letting-duplicates-through.md` | — | `02-java/language-core/` |
| `production-cookbook/broken-trace-propagation-at-a-library-migration-boundary.md` | — | `13-observability/` |
| `production-cookbook/cache-cluster-failover-triggering-a-full-database-outage.md` | — | `11-system-design/` |
| `production-cookbook/cache-layer-becomes-rce-vector-from-unrestricted-deserialization.md` | — | `02-java/language-core/` |
| `production-cookbook/canary-promotion-shipping-a-regression-with-no-human-gate.md` | — | `14-devops-containers/` |
| `production-cookbook/classcastexception-after-plugin-reload-from-classloader-identity.md` | — | `02-java/language-core/` |
| `production-cookbook/column-rename-outage-during-a-rolling-deploy.md` | — | `06-databases/` |
| `production-cookbook/compressed-oops-ceiling-causing-a-memory-regression-past-32gb.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/config-cache-throughput-ceiling-from-a-single-exclusive-lock.md` | — | `02-java/concurrency/` |
| `production-cookbook/connection-pool-exhaustion-from-an-http-call-in-a-transaction.md` | — | `05-spring/` |
| `production-cookbook/coordinated-omission-masking-real-tail-latency-in-a-load-test.md` | — | `13-observability/` |
| `production-cookbook/cp-configuration-store-causing-a-regional-outage-during-a-network-blip.md` | — | `10-distributed-systems/` |
| `production-cookbook/credential-stuffing-undetected-from-missing-security-event-logging.md` | — | `12-security/` |
| `production-cookbook/cross-request-threadlocal-leak-from-pooled-thread-reuse.md` | — | `02-java/concurrency/` |
| `production-cookbook/cross-tenant-data-leak-via-a-superuser-analytics-role.md` | — | `12-security/` |
| `production-cookbook/custom-validation-annotation-silently-inert-from-default-retention.md` | — | `02-java/language-core/` |
| `production-cookbook/dashboard-query-re-deriving-an-aggregate-the-write-model-already-knew.md` | — | `17-architecture/` |
| `production-cookbook/dead-code-elimination-inflating-a-serialization-benchmark-claim.md` | — | `16-performance-jvm/` |
| `production-cookbook/direct-buffer-oom-invisible-to-heap-monitoring.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/dirtiescontext-tripling-ci-runtime-across-a-shared-base-test-class.md` | — | `05-spring/` |
| `production-cookbook/docker-build-time-regression-from-a-collapsed-copy-layer.md` | — | `14-devops-containers/` |
| `production-cookbook/document-store-blocking-a-later-cross-order-transaction-need.md` | — | `11-system-design/` |
| `production-cookbook/double-refund-from-a-same-row-read-then-write-race.md` | — | `06-databases/` |
| `production-cookbook/doubling-the-connection-pool-made-latency-worse-under-cpu-saturation.md` | — | `06-databases/` |
| `production-cookbook/duplicate-payment-charge-from-kafka-redelivery.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/dynamodb-migration-blocking-a-later-ad-hoc-query-need.md` | — | `15-cloud/` |
| `production-cookbook/eroded-hexagonal-boundary-blowing-up-a-migration-estimate.md` | — | `17-architecture/` |
| `production-cookbook/error-budget-aggregate-hiding-a-climbing-daily-trend.md` | — | `13-observability/` |
| `production-cookbook/expensive-authorization-check-ahead-of-cheap-filter-chain-validation.md` | — | `05-spring/` |
| `production-cookbook/feature-flag-rollout-triggering-a-deoptimization-latency-spike.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/fire-and-forget-completablefuture-silently-dropping-audit-log-errors.md` | — | `02-java/concurrency/` |
| `production-cookbook/flaky-ci-integration-tests-from-shared-container-state.md` | — | `08-testing/` |
| `production-cookbook/flash-sale-latency-collapse-from-pessimistic-locks-held-across-payment-auth.md` | — | `06-databases/` |
| `production-cookbook/flash-sale-latency-spike-from-an-unmeasured-saturation-point.md` | — | `16-performance-jvm/` |
| `production-cookbook/fleet-wide-cve-traced-to-a-shared-base-image.md` | — | `12-security/` |
| `production-cookbook/force-push-silently-discarding-a-teammates-pushed-commits.md` | — | `18-engineering-practices/` |
| `production-cookbook/full-suite-blocking-every-commit-fixed-via-junit5-tag-filtering.md` | — | `08-testing/` |
| `production-cookbook/gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md` | — | `17-architecture/` |
| `production-cookbook/gradual-latency-degradation-from-an-unbounded-cache-and-growing-old-generation.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/h2-datasource-silently-activated-from-a-test-scope-classpath-leak.md` | — | `05-spring/` |
| `production-cookbook/hashmap-bucket-overload-from-a-poor-hashcode-distribution.md` | — | `02-java/collections/` |
| `production-cookbook/heap-overhead-from-millions-of-atomiclong-counters.md` | — | `02-java/concurrency/` |
| `production-cookbook/hot-mutable-cache-driving-g1-pause-growth-via-rset-pressure.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/identical-field-removal-diff-safe-under-backward-unsafe-under-forward.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/in-memory-idempotency-map-breaking-under-horizontal-scaling.md` | — | `11-system-design/` |
| `production-cookbook/index-regression-from-an-orm-generated-cast-expression.md` | — | `06-databases/` |
| `production-cookbook/intermittent-concurrentmodificationexception-from-an-unsynchronized-shared-list.md` | — | `02-java/collections/` |
| `production-cookbook/jstack-triggered-safepoint-pause-misdiagnosed-via-gc-logs.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/jwt-revocation-gap-after-account-suspension.md` | — | `12-security/` |
| `production-cookbook/kafka-consumer-group-rebalance-storm.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/kms-outage-exposing-a-missing-key-version-fallback.md` | — | `12-security/` |
| `production-cookbook/kubernetes-oomkill-with-no-application-logs.md` | — | `14-devops-containers/` |
| `production-cookbook/lambda-capture-compile-error-blocking-an-incident-hotfix.md` | — | `02-java/language-core/` |
| `production-cookbook/latency-spike-from-eager-orelse-evaluation.md` | — | `02-java/language-core/` |
| `production-cookbook/launch-day-shard-key-becoming-an-18-month-scaling-bottleneck.md` | — | `06-databases/` |
| `production-cookbook/leaderboard-latency-from-full-sort-instead-of-a-navigablemap.md` | — | `02-java/collections/` |
| `production-cookbook/leaked-correlation-id-from-a-singleton-injected-request-scoped-bean.md` | — | `05-spring/` |
| `production-cookbook/legacy-stack-synchronization-overhead-in-a-single-threaded-undo-buffer.md` | — | `02-java/collections/` |
| `production-cookbook/like-clause-sql-injection-surviving-an-automated-scan.md` | — | `12-security/` |
| `production-cookbook/like-query-degradation-as-a-product-catalog-grows.md` | — | `11-system-design/` |
| `production-cookbook/listener-registry-bottleneck-from-swapping-copyonwritearraylist-for-synchronizedlist.md` | — | `02-java/collections/` |
| `production-cookbook/lock-free-object-pool-aba-corruption-handing-out-duplicate-objects.md` | — | `02-java/concurrency/` |
| `production-cookbook/lock-ordering-deadlock-under-peak-load.md` | — | `02-java/concurrency/` |
| `production-cookbook/log-formatting-throughput-regression-from-string-concatenation-in-a-loop.md` | — | `02-java/language-core/` |
| `production-cookbook/log-shipping-dr-silently-missing-its-configured-rpo-target.md` | — | `10-distributed-systems/` |
| `production-cookbook/long-held-bi-transaction-blocking-autovacuum-and-bloating-a-hot-table.md` | — | `06-databases/` |
| `production-cookbook/lost-update-in-a-get-then-put-counter-increment.md` | — | `02-java/collections/` |
| `production-cookbook/manual-allocation-avoidance-as-a-wasted-premature-optimization.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/metaspace-growth-from-a-threadlocal-leaked-classloader.md` | — | `02-java/concurrency/` |
| `production-cookbook/missing-environment-variable-passing-health-checks-then-failing-every-request.md` | — | `15-cloud/` |
| `production-cookbook/mocked-repository-tests-masking-a-real-schema-migration-break.md` | — | `08-testing/` |
| `production-cookbook/module-level-dependency-cycle-from-a-plausible-direct-call-shortcut.md` | — | `17-architecture/` |
| `production-cookbook/n1-query-regression-from-a-lazy-collection-count-in-a-dto-loop.md` | — | `06-databases/` |
| `production-cookbook/naive-hash-mod-n-cache-scaling-causing-a-database-overload.md` | — | `10-distributed-systems/` |
| `production-cookbook/naive-lazy-singleton-double-constructing-a-connection-pool-at-cold-start.md` | — | `04-software-design/` |
| `production-cookbook/notification-service-failing-at-launch-for-skipping-capacity-estimation.md` | — | `11-system-design/` |
| `production-cookbook/offset-pagination-degrading-an-admin-tool-as-a-table-grows.md` | — | `07-api-design/` |
| `production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md` | — | `06-databases/` |
| `production-cookbook/orphaned-downstream-requests-from-an-uncancelled-completablefuture-fan-out.md` | — | `02-java/concurrency/` |
| `production-cookbook/over-sized-aggregate-causing-system-wide-lock-contention.md` | — | `17-architecture/` |
| `production-cookbook/overselling-inventory-from-a-missing-cache-eviction.md` | — | `05-spring/` |
| `production-cookbook/package-naming-convention-alone-failing-to-stop-a-module-boundary-violation.md` | — | `17-architecture/` |
| `production-cookbook/parallelstream-starving-an-unrelated-feature-via-the-shared-forkjoinpool.md` | — | `02-java/concurrency/` |
| `production-cookbook/partition-count-increase-silently-breaking-per-customer-ordering.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/per-tenant-rate-limit-effectively-tripled-by-unshared-per-instance-state.md` | — | `11-system-design/` |
| `production-cookbook/premature-microservice-decomposition-doubling-on-call-burden.md` | — | `17-architecture/` |
| `production-cookbook/price-drift-from-a-join-table-without-a-locked-historical-price.md` | — | `06-databases/` |
| `production-cookbook/query-plan-regression-from-an-unindexed-filter.md` | — | `06-databases/` |
| `production-cookbook/rbac-separation-of-duties-violation-in-a-financial-approval-workflow.md` | — | `12-security/` |
| `production-cookbook/read-your-own-writes-gap-hiding-a-just-created-order-on-confirmation.md` | — | `06-databases/` |
| `production-cookbook/reflexive-parallel-stream-regressing-a-hot-request-path.md` | — | `02-java/language-core/` |
| `production-cookbook/registry-blocked-required-field-with-no-default-on-an-orders-topic.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/reserved-capacity-purchase-increasing-cloud-spend.md` | — | `15-cloud/` |
| `production-cookbook/retry-amplification-cascading-into-a-multi-service-outage.md` | — | `10-distributed-systems/` |
| `production-cookbook/rollback-runbook-no-longer-viable-by-the-time-it-was-needed.md` | — | `17-architecture/` |
| `production-cookbook/rolling-update-latency-spike-despite-maxunavailable-zero.md` | — | `14-devops-containers/` |
| `production-cookbook/round-robin-load-balancing-overloading-a-slower-backend-instance.md` | — | `11-system-design/` |
| `production-cookbook/salted-fast-hash-passwords-surviving-rainbow-tables-not-gpu-cracking.md` | — | `12-security/` |
| `production-cookbook/scaling-consumers-past-partition-count-fails-to-reduce-kafka-lag.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/sealed-hierarchy-catching-a-missed-payment-event-case-at-build-time.md` | — | `02-java/language-core/` |
| `production-cookbook/sequential-client-side-fan-out-inflating-mobile-dashboard-latency.md` | — | `07-api-design/` |
| `production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md` | — | `17-architecture/` |
| `production-cookbook/shared-mutable-config-corrupted-by-a-live-reference-getter.md` | — | `02-java/language-core/` |
| `production-cookbook/short-polling-price-ticker-triggering-unrelated-rate-limit-alerts.md` | — | `11-system-design/` |
| `production-cookbook/silent-data-loss-from-a-shrunk-isr-without-min-insync-replicas.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/silent-notification-loss-from-an-uncoordinated-dual-write.md` | — | `10-distributed-systems/` |
| `production-cookbook/silently-stale-search-index-from-missed-outbox-writes-on-new-write-paths.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/silently-swallowed-async-exception-hiding-weeks-of-failures.md` | — | `05-spring/` |
| `production-cookbook/silently-unbatched-inserts-from-identity-generated-keys.md` | — | `06-databases/` |
| `production-cookbook/split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md` | — | `10-distributed-systems/` |
| `production-cookbook/stackoverflowerror-misdiagnosed-as-a-heap-sizing-problem.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/stale-flag-read-exposed-by-a-jvm-upgrades-jit-timing.md` | — | `02-java/concurrency/` |
| `production-cookbook/stale-hibernate-l2-cache-from-a-bypassing-batch-job-write.md` | — | `06-databases/` |
| `production-cookbook/status-field-meaning-shift-from-persisted-enum-ordinal.md` | — | `02-java/language-core/` |
| `production-cookbook/swallowed-exception-cause-costing-an-hour-of-debugging.md` | — | `02-java/language-core/` |
| `production-cookbook/synchronized-retry-storm-without-jitter.md` | — | `11-system-design/` |
| `production-cookbook/three-known-bad-collection-patterns-caught-in-one-review.md` | — | `02-java/collections/` |
| `production-cookbook/transactional-annotation-silently-skipped-on-self-invocation.md` | — | `02-java/language-core/` |
| `production-cookbook/unbounded-blockingqueue-losing-backpressure-into-an-oom.md` | — | `02-java/collections/` |
| `production-cookbook/unbounded-event-replay-making-an-event-sourced-cart-unusable.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/unbounded-executor-queue-causing-an-oom-crash.md` | — | `02-java/concurrency/` |
| `production-cookbook/unchecked-cast-heap-pollution-surfacing-far-from-its-cause.md` | — | `02-java/language-core/` |
| `production-cookbook/unconditional-heap-growth-and-memory-leak-diagnosis.md` | — | `02-java/jvm-internals/` |
| `production-cookbook/unguarded-debug-log-concatenation-as-the-real-checkout-bottleneck.md` | — | `16-performance-jvm/` |
| `production-cookbook/unmeasured-health-check-detection-latency-in-a-failover-design-review.md` | — | `11-system-design/` |
| `production-cookbook/unowned-load-testing-script-letting-a-regression-ship.md` | — | `08-testing/` |
| `production-cookbook/untraceable-choreographed-order-failure-with-no-correlation-id.md` | — | `09-messaging-event-driven/` |
| `production-cookbook/validation-framework-silently-skips-rules-from-constructor-dispatch.md` | — | `02-java/language-core/` |
| `production-cookbook/virtual-thread-migration-regression-from-synchronized-block-pinning.md` | — | `02-java/concurrency/` |
| `production-cookbook/zgc-migration-p99-regression-from-undersized-heap-headroom.md` | — | `02-java/jvm-internals/` |

## 9. `architecture-atlas/` — full mapping (17 files, stays in place)

All 17 case studies stay at their current path; a thin index in `11-system-design/case-studies/` links to each (Section 7.4).

| Path | New reference location |
|---|---|
| `architecture-atlas/authentication-service.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/deployment-infrastructure-for-a-payments-notification-service.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/distributed-cache.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/distributed-job-scheduler.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/distributed-key-value-store.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/jvm-sizing-and-diagnostics-playbook-for-a-real-time-pricing-service.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/jvm-tuning-playbook-for-a-market-data-service.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/metrics-monitoring-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/news-feed-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/notification-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/payment-processing-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/real-time-chat-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/ride-hailing-dispatch-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/security-review-of-a-multi-tenant-expense-approval-platform.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/test-strategy-for-a-checkout-service.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/ticket-and-event-booking-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |
| `architecture-atlas/url-shortener-system.md` | `11-system-design/case-studies/` (indexed, file unmoved) |

## 10. `practice/` — full mapping (138 Markdown files, stays in place)

`practice/` contains 1,121 total git-tracked files (485 `.java`, plus SQL/YAML/config); only the 138 Markdown files (READMEs and ADR examples) are in scope for this document, per Section 2.1's own "1,086 tracked Markdown files" framing. Non-Markdown practice code is unaffected by this migration entirely (Section 7.4) and is not enumerated here.

### `practice/architecture/` (5 files) — referenced from `18-engineering-practices (ADR examples) — see note`

- `practice/architecture/adr-examples/README.md`
- `practice/architecture/adr-examples/adr-001-cqrs-for-order-reporting.md`
- `practice/architecture/adr-examples/adr-002-streaming-replication-for-dr.md`
- `practice/architecture/adr-examples/adr-003-backward-compatibility-for-orders-topic.md`
- `practice/architecture/adr-examples/bad-example-missing-consequences.md`

### `practice/frontend/` (20 files) — referenced from `21-frontend-web`

- `practice/frontend/build-tooling-comparison/README.md`
- `practice/frontend/monorepo-layout-demo/README.md`
- `practice/frontend/react-accessibility/README.md`
- `practice/frontend/react-component-patterns/README.md`
- `practice/frontend/react-concurrent/README.md`
- `practice/frontend/react-error-handling/README.md`
- `practice/frontend/react-forms/README.md`
- `practice/frontend/react-fundamentals/README.md`
- `practice/frontend/react-hooks/README.md`
- `practice/frontend/react-memo-and-context/README.md`
- `practice/frontend/react-nextjs-fundamentals/AGENTS.md`
- `practice/frontend/react-nextjs-fundamentals/CLAUDE.md`
- `practice/frontend/react-nextjs-fundamentals/README.md`
- `practice/frontend/react-performance/README.md`
- `practice/frontend/react-reconciliation-and-fiber/README.md`
- `practice/frontend/react-reducer-and-custom-hooks/README.md`
- `practice/frontend/react-state-management/README.md`
- `practice/frontend/react-testing/README.md`
- `practice/frontend/react-typescript/README.md`
- `practice/frontend/styling-approaches-comparison/README.md`

### `practice/java/` (83 files) — referenced from `various — see Section 7.1 methodology note below`

`practice/java/` is not one domain — it mirrors `handbook/java-core/`, `collections/`, `jvm/`, `concurrency/`, plus `week-XX/` chronological folders, `advanced-structures/`, `hibernate-jpa/`, `oop-fundamentals/`, `design-patterns/`, `performance/`, `cloud/`, and `databases/` subfolders. Each README below is referenced from whichever new domain owns the matching topic (mechanical join deferred to Phase 4, since it requires per-subfolder topic matching, not a single fixed mapping). Full path list for exhaustiveness:

- `practice/java/advanced-structures/README.md`
- `practice/java/architecture/cqrs-read-write-separation/README.md`
- `practice/java/architecture/ddd-bounded-contexts-and-context-mapping/README.md`
- `practice/java/architecture/event-driven-integration-styles/README.md`
- `practice/java/architecture/event-sourcing-and-its-real-costs/README.md`
- `practice/java/architecture/modular-monolith-boundary-enforcement/README.md`
- `practice/java/architecture/strangler-fig-and-migration-patterns/README.md`
- `practice/java/architecture/technical-debt-and-evolutionary-architecture/README.md`
- `practice/java/cloud/container-image-internals/README.md`
- `practice/java/collections/arraydeque-internals/README.md`
- `practice/java/collections/copyonwritearraylist-tradeoffs/README.md`
- `practice/java/collections/fail-fast-vs-weakly-consistent/README.md`
- `practice/java/collections/treemap-treeset-internals/README.md`
- `practice/java/concurrency/atomics-cas-and-aba/README.md`
- `practice/java/concurrency/completablefuture-internals/README.md`
- `practice/java/concurrency/foreign-function-and-memory-api/README.md`
- `practice/java/concurrency/forkjoinpool-and-work-stealing/README.md`
- `practice/java/concurrency/locks-reentrant-rw-stamped/README.md`
- `practice/java/concurrency/scoped-values-and-threadlocal/README.md`
- `practice/java/concurrency/structured-concurrency/README.md`
- `practice/java/concurrency/threadlocal-classloader-leak/README.md`
- `practice/java/concurrency/varhandles-and-unsafe/README.md`
- `practice/java/databases/connection-pooling-and-sizing/README.md`
- `practice/java/full-stack-integration-backend/README.md`
- `practice/java/hibernate-jpa/flush-modes-and-batch-writes/README.md`
- `practice/java/hibernate-jpa/optimistic-vs-pessimistic-locking/README.md`
- `practice/java/hibernate-jpa/second-level-and-query-cache/README.md`
- `practice/java/java-core/annotations-and-processing/README.md`
- `practice/java/java-core/classloaders-and-class-initialization/README.md`
- `practice/java/java-core/enums-enummap-enumset/README.md`
- `practice/java/java-core/optional-and-null-strategy/README.md`
- `practice/java/java-core/reflection-and-dynamic-proxies/README.md`
- `practice/java/java-core/serialization-hazards/README.md`
- `practice/java/java-core/strings-interning-compact-builders/README.md`
- `practice/java/jvm/benchmarking-and-jmh-pitfalls/README.md`
- `practice/java/jvm/profiling-jfr-and-flame-graphs/README.md`
- `practice/java/kafka/consumer-lag-backpressure-and-dlq-strategy/README.md`
- `practice/java/kafka/messaging-patterns-point-to-point-vs-pubsub/README.md`
- `practice/java/kafka/schema-registry-and-compatibility-evolution/README.md`
- `practice/java/lambdas-and-functional-interfaces/README.md`
- `practice/java/performance/capacity-planning-and-headroom/README.md`
- `practice/java/spring/spring-actuator-health-and-observability-hooks/README.md`
- `practice/java/spring/spring-bean-scopes-and-proxy-modes/README.md`
- `practice/java/spring/spring-cache-abstraction-and-pitfalls/README.md`
- `practice/java/spring/spring-testing-slices-and-context-caching/README.md`
- `practice/java/spring/spring-webflux-and-reactive-programming/README.md`
- `practice/java/system-design/api-gateway-bff-and-edge-concerns/README.md`
- `practice/java/system-design/load-balancing-and-health-checking/README.md`
- `practice/java/system-design/rate-limiting-and-throttling/README.md`
- `practice/java/system-design/realtime-delivery-websocket-sse-long-poll/README.md`
- `practice/java/system-design/search-and-indexing-systems/README.md`
- `practice/java/system-design/twelve-factor-config/README.md`
- `practice/java/week-01/README.md`
- `practice/java/week-02/README.md`
- `practice/java/week-03/spring-demos/README.md`
- `practice/java/week-03/trees/README.md`
- `practice/java/week-04/failure-modes/README.md`
- `practice/java/week-04/graphs/README.md`
- `practice/java/week-05/design-coding/README.md`
- `practice/java/week-05/idempotency/README.md`
- `practice/java/week-07/backtracking/README.md`
- `practice/java/week-07/security/README.md`
- `practice/java/week-07/spring-internals/README.md`
- `practice/java/week-08/dp/README.md`
- `practice/java/week-08/kafka/README.md`
- `practice/java/week-09/concurrency-coding/README.md`
- `practice/java/week-09/concurrency-fundamentals/README.md`
- `practice/java/week-09/deadlock-diagnostics/README.md`
- `practice/java/week-09/dp-part2/README.md`
- `practice/java/week-09/executors/README.md`
- `practice/java/week-09/gc/README.md`
- `practice/java/week-09/virtual-threads/README.md`
- `practice/java/week-10/consistent-hashing/README.md`
- `practice/java/week-10/heaps/README.md`
- `practice/java/week-10/outbox-publisher/README.md`
- `practice/java/week-10/resilience/README.md`
- `practice/java/week-11/error-budget/README.md`
- `practice/java/week-11/mixed-review/README.md`
- `practice/java/week-11/percentiles/README.md`
- `practice/java/week-11/testing/README.md`
- `practice/java/week-11/tracing/README.md`
- `practice/java/week-12/final-loop-coding/README.md`
- `practice/java/week-17/supply-chain/README.md`

### `practice/mock-interviews/` (13 files) — referenced from `20-interview-preparation/mock-interviews`

- `practice/mock-interviews/README.md`
- `practice/mock-interviews/architecture-and-database-indexing-round.md`
- `practice/mock-interviews/cloud-infrastructure-round.md`
- `practice/mock-interviews/collections-technical-round.md`
- `practice/mock-interviews/data-modelling-and-storage-tradeoffs-round.md`
- `practice/mock-interviews/java-core-technical-round.md`
- `practice/mock-interviews/jvm-internals-concurrent-gc-native-memory-round.md`
- `practice/mock-interviews/jvm-internals-gc-diagnostics-round.md`
- `practice/mock-interviews/kafka-messaging-technical-round.md`
- `practice/mock-interviews/security-technical-round.md`
- `practice/mock-interviews/spring-technical-round.md`
- `practice/mock-interviews/system-design-live-round.md`
- `practice/mock-interviews/testing-technical-round.md`

### `practice/production/` (4 files) — referenced from `?`

- `practice/production/postmortem-examples/README.md`
- `practice/production/postmortem-examples/bad-example-blaming-language.md`
- `practice/production/postmortem-examples/bad-example-single-root-cause.md`
- `practice/production/postmortem-examples/postmortem-001-checkout-latency-regression.md`

### `practice/sql/` (13 files) — referenced from `06-databases`

- `practice/sql/cdc-via-logical-replication/README.md`
- `practice/sql/locks-deadlocks-and-lock-escalation/README.md`
- `practice/sql/multi-region-failover-and-dr/README.md`
- `practice/sql/mvcc-vacuum-and-bloat/README.md`
- `practice/sql/replication-and-replica-lag/README.md`
- `practice/sql/search-and-indexing-systems/README.md`
- `practice/sql/week-01/README.md`
- `practice/sql/week-02/README.md`
- `practice/sql/week-03/README.md`
- `practice/sql/week-04/README.md`
- `practice/sql/week-10/outbox/README.md`
- `practice/sql/week-10/sharding/README.md`
- `practice/sql/week-10/zero-downtime-migration/README.md`

## 11. `study-packs/` — full mapping (278 files, stays in place)

All 25 weeks stay at their current path, referenced wholesale from the **Interview Emergency Sprint** and **Backend Java Specialization** learning paths (Section 6) — zero content migration required for this directory, per the plan's own framing.

### `study-packs/week-01/` (14 files)

- `study-packs/week-01/01-clean-hexagonal-architecture.md`
- `study-packs/week-01/02-database-index-fundamentals.md`
- `study-packs/week-01/03-technical-answer-framework.md`
- `study-packs/week-01/04-coding-interview-communication.md`
- `study-packs/week-01/05-star-story-workbook.md`
- `study-packs/week-01/06-domain-purity-exercise.md`
- `study-packs/week-01/07-java-coding-practice.md`
- `study-packs/week-01/08-flashcards.md`
- `study-packs/week-01/09-week-1-mock-interview.md`
- `study-packs/week-01/10-week-1-evaluation-rubric.md`
- `study-packs/week-01/11-week-1-checklist.md`
- `study-packs/week-01/MANIFEST.md`
- `study-packs/week-01/README.md`
- `study-packs/week-01/resources.md`

### `study-packs/week-02/` (14 files)

- `study-packs/week-02/01-query-planning-and-explain.md`
- `study-packs/week-02/02-data-modelling-join-tables.md`
- `study-packs/week-02/03-ddd-tactical-aggregates.md`
- `study-packs/week-02/04-storage-selection-tradeoffs.md`
- `study-packs/week-02/05-trade-off-narration-and-adrs.md`
- `study-packs/week-02/06-answer-frameworks.md`
- `study-packs/week-02/07-java-coding-practice.md`
- `study-packs/week-02/08-flashcards.md`
- `study-packs/week-02/09-week-2-mock-interview.md`
- `study-packs/week-02/10-adr-exercise.md`
- `study-packs/week-02/11-week-2-checklist.md`
- `study-packs/week-02/MANIFEST.md`
- `study-packs/week-02/README.md`
- `study-packs/week-02/resources.md`

### `study-packs/week-03/` (12 files)

- `study-packs/week-03/01-transactions-and-propagation.md`
- `study-packs/week-03/02-isolation-levels-and-write-skew.md`
- `study-packs/week-03/03-system-design-method.md`
- `study-packs/week-03/04-java-coding-practice.md`
- `study-packs/week-03/05-flashcards.md`
- `study-packs/week-03/06-week-3-checkpoint-mock.md`
- `study-packs/week-03/07-week-3-checkpoint-rubric.md`
- `study-packs/week-03/08-design-exercise-ride-hailing.md`
- `study-packs/week-03/09-week-3-checklist.md`
- `study-packs/week-03/MANIFEST.md`
- `study-packs/week-03/README.md`
- `study-packs/week-03/resources.md`

### `study-packs/week-04/` (12 files)

- `study-packs/week-04/01-caching-strategies.md`
- `study-packs/week-04/02-distributed-failure-modes.md`
- `study-packs/week-04/03-api-design.md`
- `study-packs/week-04/04-java-coding-practice.md`
- `study-packs/week-04/05-flashcards.md`
- `study-packs/week-04/06-failure-modes-deliverable.md`
- `study-packs/week-04/07-week-4-mock-interview.md`
- `study-packs/week-04/08-design-exercise-news-feed.md`
- `study-packs/week-04/09-week-4-checklist.md`
- `study-packs/week-04/MANIFEST.md`
- `study-packs/week-04/README.md`
- `study-packs/week-04/resources.md`

### `study-packs/week-05/` (13 files)

- `study-packs/week-05/01-microservice-decomposition.md`
- `study-packs/week-05/02-idempotency.md`
- `study-packs/week-05/03-cap-and-consistency.md`
- `study-packs/week-05/04-java-coding-practice.md`
- `study-packs/week-05/05-flashcards.md`
- `study-packs/week-05/06-decomposition-analysis-deliverable.md`
- `study-packs/week-05/07-story-scope-reframing.md`
- `study-packs/week-05/08-week-5-behavioral-mock.md`
- `study-packs/week-05/09-design-exercise-payment-processing.md`
- `study-packs/week-05/10-week-5-checklist.md`
- `study-packs/week-05/MANIFEST.md`
- `study-packs/week-05/README.md`
- `study-packs/week-05/resources.md`

### `study-packs/week-06/` (11 files)

- `study-packs/week-06/01-full-flashcard-review.md`
- `study-packs/week-06/02-weak-list-repair.md`
- `study-packs/week-06/03-week-6-mock-technical-coding.md`
- `study-packs/week-06/04-week-6-mock-design-behavioral.md`
- `study-packs/week-06/05-diagnostic-rerun.md`
- `study-packs/week-06/06-week-6-assessment-deliverable.md`
- `study-packs/week-06/07-interview-readiness-rubric.md`
- `study-packs/week-06/08-week-6-checklist.md`
- `study-packs/week-06/MANIFEST.md`
- `study-packs/week-06/README.md`
- `study-packs/week-06/resources.md`

### `study-packs/week-07/` (12 files)

- `study-packs/week-07/01-spring-auto-configuration-and-lifecycle.md`
- `study-packs/week-07/02-spring-security-filter-chain.md`
- `study-packs/week-07/03-oauth2-oidc-and-jwt.md`
- `study-packs/week-07/04-java-coding-practice.md`
- `study-packs/week-07/05-flashcards.md`
- `study-packs/week-07/06-security-chain-trace-deliverable.md`
- `study-packs/week-07/07-week-7-mock-interview.md`
- `study-packs/week-07/08-design-exercise-authentication-service.md`
- `study-packs/week-07/09-week-7-checklist.md`
- `study-packs/week-07/MANIFEST.md`
- `study-packs/week-07/README.md`
- `study-packs/week-07/resources.md`

### `study-packs/week-08/` (13 files)

- `study-packs/week-08/01-kafka-architecture-fundamentals.md`
- `study-packs/week-08/02-producer-semantics-and-partition-keys.md`
- `study-packs/week-08/03-consumer-groups-and-rebalancing.md`
- `study-packs/week-08/04-delivery-semantics-and-exactly-once.md`
- `study-packs/week-08/05-java-coding-practice.md`
- `study-packs/week-08/06-flashcards.md`
- `study-packs/week-08/07-kafka-guarantees-deliverable.md`
- `study-packs/week-08/08-week-8-mock-interview.md`
- `study-packs/week-08/09-design-exercise-notification-system.md`
- `study-packs/week-08/10-week-8-checklist.md`
- `study-packs/week-08/MANIFEST.md`
- `study-packs/week-08/README.md`
- `study-packs/week-08/resources.md`

### `study-packs/week-09/` (13 files)

- `study-packs/week-09/01-java-memory-model-and-volatile.md`
- `study-packs/week-09/02-executors-and-thread-pool-sizing.md`
- `study-packs/week-09/03-deadlock-races-and-thread-diagnostics.md`
- `study-packs/week-09/04-virtual-threads.md`
- `study-packs/week-09/05-gc-fundamentals-and-log-analysis.md`
- `study-packs/week-09/06-java-coding-practice.md`
- `study-packs/week-09/07-flashcards.md`
- `study-packs/week-09/08-week-9-checkpoint.md`
- `study-packs/week-09/09-design-exercise-distributed-job-scheduler.md`
- `study-packs/week-09/10-week-9-checklist.md`
- `study-packs/week-09/MANIFEST.md`
- `study-packs/week-09/README.md`
- `study-packs/week-09/resources.md`

### `study-packs/week-10/` (14 files)

- `study-packs/week-10/01-saga-outbox-and-distributed-transactions.md`
- `study-packs/week-10/02-sharding-and-partitioning-strategies.md`
- `study-packs/week-10/03-consistent-hashing.md`
- `study-packs/week-10/04-resilience-patterns.md`
- `study-packs/week-10/05-zero-downtime-migration.md`
- `study-packs/week-10/06-java-coding-practice.md`
- `study-packs/week-10/07-flashcards.md`
- `study-packs/week-10/08-outbox-implementation-deliverable.md`
- `study-packs/week-10/09-week-10-mock-architecture-round.md`
- `study-packs/week-10/10-design-exercise-distributed-cache.md`
- `study-packs/week-10/11-week-10-checklist.md`
- `study-packs/week-10/MANIFEST.md`
- `study-packs/week-10/README.md`
- `study-packs/week-10/resources.md`

### `study-packs/week-11/` (13 files)

- `study-packs/week-11/01-test-strategy-and-test-doubles.md`
- `study-packs/week-11/02-integration-testing-against-real-dependencies.md`
- `study-packs/week-11/03-percentiles-tail-latency-and-coordinated-omission.md`
- `study-packs/week-11/04-logging-metrics-tracing-and-opentelemetry.md`
- `study-packs/week-11/05-performance-methodology-and-slo-error-budgets.md`
- `study-packs/week-11/06-java-coding-practice.md`
- `study-packs/week-11/07-flashcards.md`
- `study-packs/week-11/08-week-11-mock-behavioral.md`
- `study-packs/week-11/09-design-exercise-metrics-monitoring-system.md`
- `study-packs/week-11/10-week-11-checklist.md`
- `study-packs/week-11/MANIFEST.md`
- `study-packs/week-11/README.md`
- `study-packs/week-11/resources.md`

### `study-packs/week-12/` (10 files)

- `study-packs/week-12/01-loop-1-technical-coding-design.md`
- `study-packs/week-12/02-loop-2-technical-coding-design-behavioral.md`
- `study-packs/week-12/03-loop-3-java-fluency-coding-production-judgment.md`
- `study-packs/week-12/04-loop-4-final-full-loop.md`
- `study-packs/week-12/05-diagnostic-rerun.md`
- `study-packs/week-12/06-final-readiness-assessment.md`
- `study-packs/week-12/07-java-coding-practice.md`
- `study-packs/week-12/MANIFEST.md`
- `study-packs/week-12/README.md`
- `study-packs/week-12/resources.md`

### `study-packs/week-13/` (13 files)

- `study-packs/week-13/01-streams-and-collectors.md`
- `study-packs/week-13/02-equals-hashcode-and-comparable-contracts.md`
- `study-packs/week-13/03-generics-erasure-and-pecs.md`
- `study-packs/week-13/04-exception-design-and-hierarchy-strategy.md`
- `study-packs/week-13/05-immutability-and-defensive-copying.md`
- `study-packs/week-13/06-java-coding-practice.md`
- `study-packs/week-13/07-flashcards.md`
- `study-packs/week-13/08-week-13-mock-interview.md`
- `study-packs/week-13/09-code-review-exercise.md`
- `study-packs/week-13/10-week-13-checklist.md`
- `study-packs/week-13/MANIFEST.md`
- `study-packs/week-13/README.md`
- `study-packs/week-13/resources.md`

### `study-packs/week-14/` (13 files)

- `study-packs/week-14/01-hashmap-internals.md`
- `study-packs/week-14/02-concurrenthashmap-internals.md`
- `study-packs/week-14/03-blockingqueue-family.md`
- `study-packs/week-14/04-arraylist-and-linkedlist-internals.md`
- `study-packs/week-14/05-collection-selection-decision-matrix.md`
- `study-packs/week-14/06-java-coding-practice.md`
- `study-packs/week-14/07-flashcards.md`
- `study-packs/week-14/08-week-14-mock-interview.md`
- `study-packs/week-14/09-code-review-exercise.md`
- `study-packs/week-14/10-week-14-checklist.md`
- `study-packs/week-14/MANIFEST.md`
- `study-packs/week-14/README.md`
- `study-packs/week-14/resources.md`

### `study-packs/week-15/` (13 files)

- `study-packs/week-15/01-kubernetes-resource-limits-probes-and-jvm-sizing.md`
- `study-packs/week-15/02-kubernetes-objects-scheduling-and-networking.md`
- `study-packs/week-15/03-cloud-cost-and-scaling-economics.md`
- `study-packs/week-15/04-cicd-pipeline-design-and-deployment-strategies.md`
- `study-packs/week-15/05-aws-core-services-for-backend-engineers.md`
- `study-packs/week-15/06-hands-on-lab.md`
- `study-packs/week-15/07-flashcards.md`
- `study-packs/week-15/08-week-15-mock-interview.md`
- `study-packs/week-15/09-design-exercise-deployment-infrastructure.md`
- `study-packs/week-15/10-week-15-checklist.md`
- `study-packs/week-15/MANIFEST.md`
- `study-packs/week-15/README.md`
- `study-packs/week-15/resources.md`

### `study-packs/week-16/` (13 files)

- `study-packs/week-16/01-g1-remembered-sets-and-write-barriers.md`
- `study-packs/week-16/02-memory-leak-diagnosis-and-heap-dump-analysis.md`
- `study-packs/week-16/03-jvm-memory-layout-and-runtime-regions.md`
- `study-packs/week-16/04-jvm-flags-and-container-ergonomics.md`
- `study-packs/week-16/05-jit-tiered-compilation-and-deoptimization.md`
- `study-packs/week-16/06-hands-on-lab.md`
- `study-packs/week-16/07-flashcards.md`
- `study-packs/week-16/08-week-16-mock-interview.md`
- `study-packs/week-16/09-design-exercise-jvm-sizing-and-diagnostics-playbook.md`
- `study-packs/week-16/10-week-16-checklist.md`
- `study-packs/week-16/MANIFEST.md`
- `study-packs/week-16/README.md`
- `study-packs/week-16/resources.md`

### `study-packs/week-17/` (15 files)

- `study-packs/week-17/01-owasp-top-10-for-backend-services.md`
- `study-packs/week-17/02-applied-cryptography-hashing-signing-tls.md`
- `study-packs/week-17/03-authn-authz-rbac-vs-abac.md`
- `study-packs/week-17/04-injection-input-validation-output-encoding.md`
- `study-packs/week-17/05-multi-tenancy-isolation-models.md`
- `study-packs/week-17/06-secrets-management-and-key-rotation.md`
- `study-packs/week-17/07-supply-chain-security-sbom-and-dependency-risk.md`
- `study-packs/week-17/08-hands-on-lab.md`
- `study-packs/week-17/09-flashcards.md`
- `study-packs/week-17/10-week-17-mock-interview.md`
- `study-packs/week-17/11-design-exercise-multi-tenant-expense-platform-security-review.md`
- `study-packs/week-17/12-week-17-checklist.md`
- `study-packs/week-17/MANIFEST.md`
- `study-packs/week-17/README.md`
- `study-packs/week-17/resources.md`

### `study-packs/week-18/` (13 files)

- `study-packs/week-18/01-performance-and-load-testing-methodology.md`
- `study-packs/week-18/02-writing-tests-live-in-an-interview.md`
- `study-packs/week-18/03-contract-testing-for-services.md`
- `study-packs/week-18/04-junit5-architecture-and-advanced-features.md`
- `study-packs/week-18/05-mutation-and-property-based-testing.md`
- `study-packs/week-18/06-hands-on-lab.md`
- `study-packs/week-18/07-flashcards.md`
- `study-packs/week-18/08-week-18-mock-interview.md`
- `study-packs/week-18/09-design-exercise-test-strategy-for-a-checkout-service.md`
- `study-packs/week-18/10-week-18-checklist.md`
- `study-packs/week-18/MANIFEST.md`
- `study-packs/week-18/README.md`
- `study-packs/week-18/resources.md`

### `study-packs/week-19/` (14 files)

- `study-packs/week-19/01-gc-roots-reachability-and-reference-strength.md`
- `study-packs/week-19/02-zgc-and-shenandoah-concurrent-collection.md`
- `study-packs/week-19/03-safepoints-and-stop-the-world-mechanics.md`
- `study-packs/week-19/04-object-layout-headers-and-compressed-oops.md`
- `study-packs/week-19/05-native-memory-direct-buffers-and-off-heap.md`
- `study-packs/week-19/06-escape-analysis-and-scalar-replacement.md`
- `study-packs/week-19/07-hands-on-lab.md`
- `study-packs/week-19/08-flashcards.md`
- `study-packs/week-19/09-week-19-mock-interview.md`
- `study-packs/week-19/10-design-exercise-jvm-tuning-for-a-market-data-service.md`
- `study-packs/week-19/11-week-19-checklist.md`
- `study-packs/week-19/MANIFEST.md`
- `study-packs/week-19/README.md`
- `study-packs/week-19/resources.md`

### `study-packs/week-20/` (7 files)

- `study-packs/week-20/01-linked-lists-coding-practice.md`
- `study-packs/week-20/02-greedy-coding-practice.md`
- `study-packs/week-20/03-intervals-coding-practice.md`
- `study-packs/week-20/04-bit-manipulation-coding-practice.md`
- `study-packs/week-20/05-graphs-advanced-coding-practice.md`
- `study-packs/week-20/MANIFEST.md`
- `study-packs/week-20/README.md`

### `study-packs/week-21/` (6 files)

- `study-packs/week-21/01-tries-coding-practice.md`
- `study-packs/week-21/02-backtracking-coding-practice.md`
- `study-packs/week-21/03-stacks-coding-practice.md`
- `study-packs/week-21/04-dynamic-programming-coding-practice.md`
- `study-packs/week-21/MANIFEST.md`
- `study-packs/week-21/README.md`

### `study-packs/week-22/` (6 files)

- `study-packs/week-22/01-hashing-coding-practice.md`
- `study-packs/week-22/02-binary-search-coding-practice.md`
- `study-packs/week-22/03-concurrency-coding-practice.md`
- `study-packs/week-22/04-design-coding-practice.md`
- `study-packs/week-22/MANIFEST.md`
- `study-packs/week-22/README.md`

### `study-packs/week-23/` (6 files)

- `study-packs/week-23/01-dp-coding-practice.md`
- `study-packs/week-23/02-arrays-two-pointers-coding-practice.md`
- `study-packs/week-23/03-heaps-coding-practice.md`
- `study-packs/week-23/04-trees-coding-practice.md`
- `study-packs/week-23/MANIFEST.md`
- `study-packs/week-23/README.md`

### `study-packs/week-24/` (4 files)

- `study-packs/week-24/01-dp-coding-practice.md`
- `study-packs/week-24/02-graphs-coding-practice.md`
- `study-packs/week-24/MANIFEST.md`
- `study-packs/week-24/README.md`

### `study-packs/week-25/` (4 files)

- `study-packs/week-25/01-dp-coding-practice.md`
- `study-packs/week-25/02-graphs-coding-practice.md`
- `study-packs/week-25/MANIFEST.md`
- `study-packs/week-25/README.md`

## 12. `00-project/` — full mapping (8 files, stays in place)

Per Section 7.3's `00-overview` row, these stay at `00-project/` — they are provenance/source material referenced by the new `syllabus/00-overview/`, not migrated as content themselves.

| Path | Role |
|---|---|
| `00-project/blueprint-v1.1-corrections.md` | Historical corrections log — provenance |
| `00-project/coverage-audit-2026-07-31.md` | Historical coverage snapshot — provenance |
| `00-project/file-mapping.md` | A prior, narrower file-mapping exercise — superseded by this document for migration purposes; not deleted |
| `00-project/frontend-topic-register.md` | The frontend Master Topic Register — primary input alongside the above |
| `00-project/knowledge-architecture-blueprint.md` | The Master Topic Register itself — primary input to `00-overview/topic-register.md` |
| `00-project/knowledge-base-audit.md` | Phase 1 audit (original interview-prep scoping) — provenance |
| `00-project/learning-roadmap.md` | Superseded in spirit by the new Learning Paths (Section 6), kept for provenance |
| `00-project/syllabus-transformation-plan.md` | This very plan — stays at `00-project/`, referenced from `00-overview/` once built |

## 13. Archive, templates, resources, root files — full mapping (16 files)

### `archive/` (6 files) — no migration

- `archive/pre-initialization-scaffolding/NOTES.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`
- `archive/pre-initialization-scaffolding/changelog-unverified-claims.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`
- `archive/pre-initialization-scaffolding/file-mapping-unverified-claims.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`
- `archive/pre-initialization-scaffolding/repository-tree-stale.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`
- `archive/pre-initialization-scaffolding/root-manifest-misplaced-week-1-content.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`
- `archive/pre-initialization-scaffolding/week-02-manifest-misplaced-content.md` — stays archived, explicitly marked non-authoritative in `CHANGELOG.md`

### `templates/` (2 files, excl. `.gitkeep`) — stays in place

- `templates/adr-template.md` — referenced from the Topic Specification (Section 4) and `18-engineering-practices`
- `templates/postmortem-template.md` — referenced from the Topic Specification (Section 4) and `18-engineering-practices`

### `resources/` (1 file, excl. `.gitkeep`) — stays in place

- `resources/repository-tree.md` — folded into `00-overview/` as provenance

### Root files (5 files) — content preserved, framing rewritten in Phase 1

- `AGENTS.md`
- `CHANGELOG.md`
- `CLAUDE.md`
- `CONTRIBUTING.md`
- `README.md`

## 14. Master Topic Register cross-check

Beyond mapping files to domains, Phase 0's exhaustiveness goal implies checking the reverse direction too: does every register topic have a corresponding file? This section does that check — it is additional to what Section 7.1 explicitly asks for, but directly serves the same "surface incompleteness before Phase 1" goal, and directly informs Section 11's first Risk row ("the topic_id join is less complete than sampling suggested").

**Backend register: 198 T-codes.** 144 have at least one matching `handbook/` chapter reference; **54 do not appear in any `handbook/` chapter's own Topic register line or front matter.**

T-codes with zero matching handbook chapter (these are register-only — either genuinely unwritten topics, or written under a different/compound ID this script's regex didn't catch, e.g. a chapter covering T-605/T-608 together only exposes both codes if both appear literally in its Topic register line):

- `T-1004` — Service mesh & sidecar trade-offs
- `T-1005` — Infrastructure as Code fundamentals
- `T-116` — Java Platform Module System
- `T-1401` — Complexity analysis & amortization
- `T-1402` — Arrays, two pointers, sliding window
- `T-1403` — Hashing patterns & frequency maps
- `T-1404` — Binary search incl. search-on-answer
- `T-1405` — Linked lists & in-place manipulation
- `T-1406` — Stacks & monotonic stack
- `T-1407` — Heaps, Top-K, k-way merge
- `T-1408` — Trees, BST, traversal patterns
- `T-1409` — Graphs: BFS, DFS, topological sort, Dijkstra, Union-Find** ⭐
- `T-1410` — Backtracking & pruning
- `T-1411` — Dynamic programming: 1D, 2D, knapsack, intervals** ⭐
- `T-1412` — Intervals, merging, sweep line
- `T-1413` — Greedy & the exchange argument
- `T-1414` — Bit manipulation
- `T-1415` — Tries & prefix structures
- `T-1416` — Design-style coding problems (LRU, LFU, iterators)** ⭐
- `T-1417` — Concurrency coding problems** ⭐
- `T-1418` — Advanced structures: Segment/Fenwick/rolling hash
- `T-1419` — Coding interview communication protocol
- `T-1501` — STAR structure & narrative construction** ⭐
- `T-1502` — Story portfolio design (12-story matrix)** ⭐
- `T-1503` — Scope, impact & influence narratives (Staff signal)** ⭐
- `T-1504` — Production incident stories** ⭐
- `T-1505` — Architecture decision & trade-off narration** ⭐
- `T-1506` — Conflict & technical disagreement** ⭐
- `T-1507` — Mentoring & growing engineers** ⭐
- `T-1508` — Failure, mistakes, and learning** ⭐
- `T-1509` — Cross-team influence without authority** ⭐
- `T-1510` — Large migrations & long-horizon projects** ⭐
- `T-1511` — Technical debt advocacy & prioritization
- `T-1512` — Design reviews, RFCs, written communication
- `T-1513` — Company frameworks (Amazon LPs, etc.)** ⭐
- `T-1514` — Questions to ask your interviewer
- `T-1515` — Offer evaluation & negotiation
- `T-1601` — Technical communication protocol** ⭐
- `T-1602` — System design narration & whiteboard discipline** ⭐
- `T-1603` — Mock interviews & self-evaluation rubrics** ⭐
- `T-1604` — Company loop structures & calibration
- `T-301` — JVM memory layout & regions
- `T-304` — G1 internals: regions, RSets, write barriers
- `T-307` — Memory leak diagnosis & heap dump analysis** ⭐
- `T-308` — JIT: tiered compilation, inlining, deoptimization
- `T-312` — JVM flags & ergonomics for containers
- `T-403` — synchronized, monitors, lock optimizations
- `T-507` — Externalized config, profiles, property binding
- `T-508` — Spring MVC request lifecycle
- `T-510` — Spring Data repositories & query derivation
- `T-706` — Retention, log compaction, tiered storage
- `T-709` — Kafka Streams & stateful stream processing
- `T-813` — Canonical design problems (12-problem set)** ⭐
- `T-915` — SOLID at architectural scale

**Frontend register: 37 F-codes.** 35 have at least one matching `handbook/frontend/` chapter reference; **2 do not.**

- `F-102`
- `F-103`

**Caveat on this check's own limits:** this only confirms a T-code/F-code appears *somewhere* in a chapter's Topic register line — it does not verify the chapter actually teaches that topic's full stated scope, and several chapters intentionally cover a compound range (e.g. `T-101–T-104`) or two codes at once (e.g. `T-107/T-108`) which this script's regex does correctly catch. A register T-code showing as "missing" here should be read as "not obviously covered," not "definitely absent" — the Master Topic Register's own `Gap` column (🔴/🟠/🟡/🟢) remains the authoritative, human-verified source for that judgment; this is a cheap mechanical cross-check, not a replacement for it.

## 15. Duplication re-check (plan Section 7.5)

Section 7.5 flagged two areas as needing a migration-phase decision. Both were checked directly during Phase 0 rather than left open, since the check was cheap and removes one open question from Section 14 below.

### 7.5 item 1 — `practice/java/{domain}/` vs. `practice/java/week-XX/`

**Resolved: genuinely complementary, not a duplicate.** Spot-checked `practice/java/collections/` (4 subfolders: `copyonwritearraylist-tradeoffs/`, `arraydeque-internals/`, `fail-fast-vs-weakly-consistent/`, `treemap-treeset-internals/` — all four built this session as companion demos for the 2026-09-02 cheat-sheets/flashcards/production-cookbook backlog closures) against every `practice/java/week-*/` folder's content. The domain folder holds topic-organized demos backing a specific `handbook/` chapter's claims (e.g., `ConcurrentReadThroughputDemo.java` proving `CopyOnWriteArrayList`'s read-cost claim); the week folders hold unrelated, chronological coding-interview exercises (`LRUCacheFixed.java`, `Trie.java`, a hand-rolled `MyHashMap.java` as a design-coding exercise). Zero file-name or topic overlap found. **No action needed — this is not a duplication risk requiring resolution before Phase 4.**

### 7.5 item 2 — `behavioral-handbook/` vs. `interview-playbook/behavioral/`

Confirmed as stated in the plan: `interview-playbook/behavioral/` contains exactly one file (`company-loop-structures-and-question-pattern-recognition.md`), and its own scope (interview-day logistics, loop-structure recognition) is genuinely distinct from `behavioral-handbook/`'s STAR/story-portfolio methodology — not a content duplicate, but a directory-boundary question. The plan's proposed resolution (consolidate both under `20-interview-preparation/behavioral/` in Phase 1) is reflected in Section 5's mapping above and requires no further Phase 0 investigation.

## 16. Coverage accounting — proof this mapping is exhaustive

Per Section 13's Phase 0 definition of done ("accounts for all 1,086 tracked Markdown files" — corrected here to the real count of 1,088, see Section 0) — every file counted in Section 1's executive summary is enumerated by name in exactly one of Sections 3–13 above:

- Section 3 (`handbook/`): 168
- Section 4 (`behavioral-handbook/`): 16 (incl. README)
- Section 5 (`interview-playbook/`): 9
- Section 6 (`cheat-sheets/`): 164 (incl. README)
- Section 7 (`flashcards/`): 138 (incl. README)
- Section 8 (`production-cookbook/`): 137 (incl. README)
- Section 9 (`architecture-atlas/`): 18 (incl. README)
- Section 10 (`practice/`): 138
- Section 11 (`study-packs/`): 278
- Section 12 (`00-project/`): 8
- Section 13 (archive/templates/resources/root): 14
- **Sum: 1088**
- **Independently verified total from `git ls-files`: 1088**

**These match exactly — every tracked Markdown file is accounted for in this document.**

---

## Section 14 (plan) cross-reference — open questions carried forward

This document does not answer the six open questions from the plan's own Section 14 — they are presented separately for the user's review, one at a time, per the user's explicit request in this turn, not folded into this mapping artifact.
