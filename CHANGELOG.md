# Changelog

All notable changes to this repository are documented here. Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). This project does not use semantic versioning; entries are grouped by programme milestone.

---

## [Unreleased]

### Added
- `handbook/databases/` — the "Database Triad" canonical chapter group complete (T-609/T-610/T-611), the first Phase 5 deliverable under `CLAUDE.md`'s canonical-handbook-chapter template:
  - `index-structures-btree-composite-covering.md` (T-609, IWI 8.30) — elevates the verified `EXPLAIN ANALYZE` evidence from `study-packs/week-01/02-database-index-fundamentals.md`
  - `query-planning-and-explain-analyze.md` (T-610, IWI 7.90) — elevates the three verified scenarios from `study-packs/week-02/01-query-planning-and-explain.md`
  - `isolation-levels-and-concurrency-anomalies.md` (T-611, IWI 7.95) — elevates the real two-session write-skew reproduction from `study-packs/week-03/02-isolation-levels-and-write-skew.md`
  - All three cross-linked to each other; all `EXPLAIN`/session evidence traced to reproducible scripts in `practice/sql/week-01..03/`
- `handbook/spring/transactional-proxy-mechanics-and-propagation.md` — second Phase 5 chapter group (T-503/T-504/T-505, IWI 8.15 combined, highest-IWI Spring-specific topic in the register). Elevates the six real, executed Spring Framework 6.1.14 demos from `study-packs/week-03/01-transactions-and-propagation.md` (proxy self-invocation, default rollback rule, `REQUIRES_NEW` deadlock risk, driver-dependent `readOnly`, connection-pool exhaustion). Cross-linked to the Database Triad's isolation-levels chapter.
- `handbook/kafka/` — the "Kafka Semantics Cluster" canonical chapter group complete (T-701/T-702/T-703/T-704/T-705), the third Phase 5 chapter group:
  - `kafka-architecture-fundamentals.md` (T-701, IWI 6.40) — prerequisite mechanics: partitions, ordering scope, ISR, unclean leader election
  - `producer-semantics-and-partition-keys.md` (T-702/T-705, IWI 7.40/7.55) — `acks`, idempotent producers, partition key design
  - `consumer-groups-and-rebalancing.md` (T-703, IWI 7.50) — rebalance triggers, eager vs. cooperative-incremental, offset-driven resumption
  - `delivery-semantics-and-exactly-once.md` (T-704, IWI 8.00, highest-weighted in the cluster) — at-least/at-most/exactly-once, the outbox/idempotent-consumer boundary
  - All four cross-linked; all traces verified against `practice/java/week-08/kafka/src/*.java` real executed output; all relative links checked against the filesystem

### Changed
- `study-packs/week-01/02-database-index-fundamentals.md`, `week-02/01-query-planning-and-explain.md`, `week-03/01-transactions-and-propagation.md`, `week-03/02-isolation-levels-and-write-skew.md` — slimmed to a per-section summary + link to their new canonical `handbook/` chapter, per `CLAUDE.md`'s canonical-ownership rule. Numbered section headings were deliberately kept stable (same count, same order) because `week-01/09-week-1-mock-interview.md`, `week-02/06-answer-frameworks.md`, `week-03/06-week-3-checkpoint-mock.md`, `week-03/08-design-exercise-ride-hailing.md`, and `week-06/02-weak-list-repair.md` all cite specific section numbers (§3, §4, §6, §7, §9) inside these files directly.
- `study-packs/week-01/MANIFEST.md`, `week-02/MANIFEST.md`, `week-03/MANIFEST.md` — word counts re-run and corrected for the four slimmed files above. Week-01's re-run also surfaced (but did not silently fix) a pre-existing stale count on `01-clean-hexagonal-architecture.md`, flagged separately.
- `study-packs/week-08/01-kafka-architecture-fundamentals.md`, `02-producer-semantics-and-partition-keys.md`, `03-consumer-groups-and-rebalancing.md`, `04-delivery-semantics-and-exactly-once.md` — slimmed to a per-section summary + link to their new canonical `handbook/kafka/` chapters, same numbered-heading-stable pattern (no other file cites specific §N sections of these four, only whole-file references, so risk here was lower than Weeks 1–3/6).
- `study-packs/week-08/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match the slimmed files.

- `handbook/system-design/` — fourth Phase 5 chapter group, two of the four topics tied for 3rd-highest IWI (8.45) in the entire 198-topic register:
  - `caching-strategies-and-invalidation.md` (T-804, IWI 8.45) — invalidation strategies, the cache/database disagreement race, cache stampede (measured: 50 DB calls uncoordinated vs. 1 with single-flight), hot-key mitigation
  - `distributed-systems-failure-modes.md` (T-909, IWI 8.45) — retry amplification (measured: 2.3× load, same 4/12 success rate without backoff vs. 12/12 with it), idempotency keys, split-brain and fencing tokens
  - Cross-linked to each other and to the Database Triad's isolation-levels chapter; all traces verified against `practice/java/week-04/failure-modes/src/*.java` real executed output

### Changed (continued)
- `study-packs/week-04/01-caching-strategies.md`, `02-distributed-failure-modes.md` — slimmed to a per-section summary + link to their new canonical `handbook/system-design/` chapters, numbered headings kept stable because `03-api-design.md`, `06-failure-modes-deliverable.md`, `08-design-exercise-news-feed.md`, `09-week-4-checklist.md`, and Week 5's `02-idempotency.md`/`09-design-exercise-payment-processing.md` all cite specific §3/§4 sections directly.
- `study-packs/week-04/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match.

- Fifth Phase 5 chapter group, from Week 5's Mandatory Core topics:
  - `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md` (T-907/T-908, IWI 8.40/7.90) — the boundary-consistency test, the "two services need one transaction" signal, merge-back criteria, the four-engineer judgment trap
  - `handbook/system-design/idempotency.md` (T-809, IWI 8.09) — the full key/storage/TTL mechanism, real concurrent-duplicate and crash-recovery traces, cross-linked to Distributed Systems Failure Modes and Kafka's exactly-once chapter
  - `handbook/system-design/cap-theorem-and-consistency-models.md` (T-807, IWI 7.90) — CP/AP classification, eventual vs. strong consistency in user-facing terms, per-data-type consistency partitioning
  - All cross-linked; idempotency's traces verified against `practice/java/week-05/idempotency/src/IdempotencyDemo.java`

### Changed (continued)
- `study-packs/week-05/01-microservice-decomposition.md`, `02-idempotency.md`, `03-cap-and-consistency.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `06-decomposition-analysis-deliverable.md` and `09-design-exercise-payment-processing.md` cite §3/§3/§5 respectively.
- `study-packs/week-05/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (9,853 → 6,584 total words, verified).

- `handbook/concurrency/` — sixth Phase 5 chapter group, both flagged ⛔ absent-and-wrong by Phase 1's audit (prior source material was factually incorrect, not just shallow — carries the highest gap-severity multiplier in the register):
  - `java-memory-model-and-volatile.md` (T-401/T-402, IWI 7.75/6.60) — corrects the "volatile prevents caching" error; measured JIT-hoisting visibility failure (5+ second hang, reproduced 3/3 runs), happens-before rules, double-checked locking
  - `deadlock-race-conditions-and-thread-diagnostics.md` (T-409, IWI 6.70) — corrects the invented "Running" thread state and missing `TIMED_WAITING`; real `ThreadMXBean` deadlock detection, measured race condition (83.8% lost updates, 0 with `AtomicInteger`)
  - Cross-linked to each other; all traces verified against `practice/java/week-09/concurrency-fundamentals/`, `deadlock-diagnostics/` real executed output

### Changed (continued)
- `study-packs/week-09/01-java-memory-model-and-volatile.md`, `03-deadlock-races-and-thread-diagnostics.md` — slimmed to a per-section summary + link to their new canonical `handbook/concurrency/` chapters, numbered headings kept stable because `08-week-9-checkpoint.md` and `MANIFEST.md`'s own errata table cite §3 of each directly.
- `study-packs/week-09/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (13,923 → 11,816 total words, verified); the errata table's fix-location references updated to point at the canonical chapters.

- Seventh Phase 5 chapter group — the System Design spine, closing the largest remaining gap in the register:
  - `handbook/system-design/system-design-method-and-estimation.md` (T-801/T-802, IWI 8.65 — **#1 of 198**, the single highest-IWI topic in the entire register, previously uncovered) — the six-phase method, worked QPS/storage estimation
  - `handbook/system-design/api-design.md` (T-803, IWI 7.90) — the measured `OFFSET` vs. keyset pagination comparison (~3,000×), resource naming, error design, idempotency
  - Cross-linked to caching, distributed failure modes, idempotency, and the Database Triad's index-structures chapter; pagination trace verified against `practice/sql/week-04/pagination-lab.sql`

### Changed (continued)
- `study-packs/week-03/03-system-design-method.md`, `week-04/03-api-design.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `week-04/08-design-exercise-news-feed.md`/`09-week-4-checklist.md` cite §3 of the latter, and several Week 3/4/6 mock-interview and checklist files reference the former by name.
- `study-packs/week-03/MANIFEST.md`, `week-04/MANIFEST.md`, both `README.md` files — word counts and purpose descriptions updated to match (week-03: 8,447 → 7,203; week-04: 7,133 → 6,233, both verified).

- Eighth Phase 5 chapter group, from Week 10's distributed-data topics:
  - `handbook/system-design/distributed-transactions-saga-and-outbox.md` (T-618, IWI 7.65) — the convergence point of Spring transactions, idempotency, and Kafka delivery semantics; real dual-write-loss reproduction and a working transactional outbox with measured crash recovery (3 orders → 4 messages, zero losses, one real duplicate)
  - `handbook/system-design/data-partitioning-and-consistent-hashing.md` (T-806, IWI 7.70) — naive `hash % N` vs. consistent hashing, measured (92.5% vs. 9.2% remapped keys removing 1 of 10 nodes), virtual nodes, hot-key vs. rebalancing-cost distinction
  - Cross-linked to idempotency, Spring transactions, Kafka delivery semantics, and Kafka partition-key design; all traces verified against `practice/java/week-10/outbox-publisher/` and `consistent-hashing/` real executed output

### Changed (continued)
- `study-packs/week-10/01-saga-outbox-and-distributed-transactions.md`, `03-consistent-hashing.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `09-week-10-mock-architecture-round.md` cites §3/§4 of the former and `10-design-exercise-distributed-cache.md` cites §6 Q2 of the latter directly.
- `study-packs/week-10/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (15,110 → 12,453 total words, verified).

- Ninth Phase 5 chapter group, from Week 7's Spring Security cluster:
  - `handbook/spring/security-filter-chain.md` (T-511, IWI 7.20) — the filter chain-of-responsibility model, real request traces showing the 401 vs. 403 distinction, filter-ordering-by-cost principle
  - `handbook/security/oauth2-oidc-and-jwt.md` (T-512/T-513, IWI 7.15/7.00) — OAuth2 vs. OIDC, Authorization Code + PKCE, real HMAC-SHA256 JWT sign/tamper/expiry traces, the honest JWT-revocation answer
  - Cross-linked to each other, to Spring transactions, and to caching/CAP for the stateless-vs-stateful trade-off pattern; JWT traces verified against `practice/java/week-07/security/src/JwtDemo.java`

### Changed (continued)
- `study-packs/week-07/02-spring-security-filter-chain.md`, `03-oauth2-oidc-and-jwt.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `06-security-chain-trace-deliverable.md` and `08-design-exercise-authentication-service.md` reference the former's scenarios/principle and the latter's §4 directly.
- `study-packs/week-07/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (8,292 → 6,626 total words, verified).

- `handbook/spring/auto-configuration-and-bean-lifecycle.md` (T-506/T-501, IWI 7.30) — completes Week 7's Spring cluster: the real observed bean lifecycle order (constructor → `BeanPostProcessor.before` → `@PostConstruct` → `InitializingBean` → custom init → `BeanPostProcessor.after`), the exact hook that creates a `@Transactional` proxy, `@ConditionalOnMissingBean`'s ordering guarantee, and the measured `@Async`+`@Transactional` gotcha (12ms return, no visible exception, correct silent rollback). Cross-linked to Transactional Proxy Mechanics and the Spring Security filter chain; traces verified against `practice/java/week-07/spring-internals/` real executed output.

### Changed (continued)
- `study-packs/week-07/01-spring-auto-configuration-and-lifecycle.md` — slimmed to a per-section summary + link to its new canonical chapter, numbered headings kept stable because `09-week-7-checklist.md` cites §1–4 directly.
- `study-packs/week-07/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (6,626 → 5,644 total words, verified; all three Week 7 T-topics now summary + canonical-link).

- Tenth Phase 5 chapter group — completes the `handbook/concurrency/` domain (4 of 4 Week 9 concurrency topics now covered):
  - `handbook/concurrency/executors-and-thread-pool-sizing.md` (T-406, IWI 7.15) — the unbounded-default-queue trap, measured (496/500 tasks silently queued); bounded queue + rejection policy backpressure; Little's Law sizing
  - `handbook/concurrency/virtual-threads.md` (T-410, IWI 6.75) — measured 18× IO-bound scale improvement, measured ~10× pinning regression from `synchronized`, why pooling virtual threads is an anti-pattern
  - Cross-linked to each other and to the JMM/volatile chapter; all traces verified against `practice/java/week-09/executors/` and `virtual-threads/` real executed output

- Eleventh Phase 5 chapter group — opens the `handbook/jvm/` domain:
  - `handbook/jvm/gc-fundamentals-and-log-analysis.md` (T-303/T-306, IWI 7.35/6.90) — reads a real captured G1 GC log field-by-field (four sub-millisecond young pauses, rising post-collection occupancy trend); humongous allocations; the "tuning means more heap" misconception; a production scenario tracing a gradually-degrading p99 to an unbounded cache via the occupancy-trend signal
  - Cross-linked to the JMM/volatile chapter; traces verified against `practice/java/week-09/gc/AllocationStormDemo.java` real captured `-Xlog:gc*` output

### Changed (continued)
- `study-packs/week-09/02-executors-and-thread-pool-sizing.md`, `04-virtual-threads.md`, `05-gc-fundamentals-and-log-analysis.md` — slimmed to a per-section summary + link to their new canonical `handbook/concurrency/` and `handbook/jvm/` chapters, numbered headings kept stable (no other Week 9 file cites specific §N sections of these three, only whole-file references).
- `study-packs/week-09/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (11,816 → 8,526 total words, verified; all five Week 9 T-topics now summary + canonical-link).

- Twelfth Phase 5 chapter group — completes Week 10's remaining topics:
  - `handbook/databases/table-partitioning-and-sharding-strategies.md` (T-614, IWI 7.60) — real partition-pruning `EXPLAIN` measurement (0.727ms pruned vs. 2.667ms unpruned), shard-key-as-one-way-door, Postgres HASH partitioning's identical repartitioning cost to naive `hash % N`
  - `handbook/system-design/resilience-patterns.md` (T-515, IWI 7.60) — real three-state circuit breaker cycle with measured latency savings, real retry-jitter-vs-no-jitter synchronized-storm demonstration, timeout-from-percentile and bulkhead framing; explicitly cross-linked to (and distinguished from) Distributed Systems Failure Modes' retry-amplification measurement
  - `handbook/databases/zero-downtime-schema-migration.md` (T-616, IWI 7.30) — real 1943ms-vs-84ms blocking-vs-`CONCURRENTLY` index creation measurement, expand-contract for column/type changes, the dual-write phase's inherited atomicity hazard
  - All three cross-linked to each other and to prior chapters (consistent hashing, executor sizing, distributed transactions/outbox); all traces verified against `practice/sql/week-10/sharding/`, `practice/java/week-10/resilience/`, and `practice/sql/week-10/zero-downtime-migration/` real executed output

### Changed (continued)
- `study-packs/week-10/02-sharding-and-partitioning-strategies.md`, `04-resilience-patterns.md`, `05-zero-downtime-migration.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `10-design-exercise-distributed-cache.md` cites `04-resilience-patterns.md` §5's bulkhead discussion directly.
- `study-packs/week-10/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (12,453 → 8,807 total words, verified; all five Week 10 T-topics now summary + canonical-link).

- Thirteenth Phase 5 chapter group — completes Week 2's remaining topics:
  - `handbook/databases/data-modelling-and-explicit-join-tables.md` (T-605/T-608, IWI 5.20) — the naive-vs-explicit-join-entity trap, the precise "as of formation time" trigger, and a real measured price-history data-integrity bug
  - `handbook/architecture/ddd-tactical-design-aggregates.md` (T-903, IWI 7.25) — aggregate root/boundary, the sizing rule, repository-per-aggregate, aggregate boundaries as future service boundaries
  - `handbook/system-design/storage-selection-tradeoffs.md` (T-617/T-811, IWI 6.90) — the access-pattern method, relational/document/key-value/wide-column trade-offs, polyglot persistence as a cost/benefit call
  - `interview-playbook/technical-answers/trade-off-narration-and-adrs.md` (T-1505/T-916, IWI 8.10, the highest-IWI single item in the interview-feedback register) — the four-beat trade-off structure and ADRs as its written form; the first entry in `interview-playbook/technical-answers/`
  - All four cross-linked to each other and to prior chapters (data modelling ↔ aggregates, storage selection ↔ consistent hashing)

### Changed (continued)
- `study-packs/week-02/02-data-modelling-join-tables.md`, `03-ddd-tactical-aggregates.md`, `04-storage-selection-tradeoffs.md`, `05-trade-off-narration-and-adrs.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `06-answer-frameworks.md` cites §3 of the first and last directly, and `week-03/08-design-exercise-ride-hailing.md` cites §3 of the third.
- `study-packs/week-02/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (11,303 → 7,908 total words, verified; all six Week 2 T-topics now summary + canonical-link).

- `handbook/architecture/clean-hexagonal-architecture.md` (T-901, IWI 7.25) — completes Week 1's remaining topic and closes the prerequisite chain for the DDD aggregates chapter and T-912. The one-rule definition (dependencies point inward), primary/secondary ports, the three JPA-entity-placement options, and all 10 of the original chapter's interview questions carried forward in full (this is the deepest, most Staff-differentiating single topic in Week 1 — "would you use this on every project?"). A representative (explicitly labeled, non-personal) production scenario replaces the original template placeholder for a migration-estimate blowup caused by an unenforced dependency rule. Cross-linked to DDD Tactical Design — Aggregates and Microservice Decomposition.

### Changed (continued)
- `study-packs/week-01/01-clean-hexagonal-architecture.md` — slimmed to a per-section summary + link to its new canonical chapter, numbered headings kept stable (17 sections, same order) because `03-technical-answer-framework.md`, `06-domain-purity-exercise.md`, `09-week-1-mock-interview.md`, `11-week-1-checklist.md`, and `README.md` all cite §1, §3, §4, and §7 directly.
- `study-packs/week-01/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (11,811 → 9,428 total words, verified). This also resolves the previously-flagged stale word count on this file (was 1,972, silently out of date against the file's actual 3,317 pre-slim word count) — the row now reflects a freshly counted, correct total for the slimmed file. Both of Week 1's technical-domain T-topics (T-901, T-609) are now summary + canonical-link; the remaining three (T-1601, T-1501, T-1419) are interview-craft/behavioral framework content with no separate deeper duplicate elsewhere, so they are left as their own primary source rather than hollowed out.

- Fourteenth Phase 5 chapter group — opens the `handbook/testing/` and `handbook/performance/` domains from Week 11:
  - `handbook/testing/test-strategy-and-test-doubles.md` (T-1101/T-1103, IWI 7.00/6.40) — a mock verifying interaction not just outcome, the pyramid vs. the ice-cream-cone anti-pattern, coverage as diagnostic not target
  - `handbook/testing/integration-testing-against-real-dependencies.md` (T-1104, IWI 6.50) — a real Postgres-backed integration test, the Testcontainers-vs-manual-orchestration scoping note, flaky-test diagnosis via shared state
  - `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` (T-1204, IWI 6.70) — real closed-loop-vs-open-loop measurement (p99 500ms → 830ms from correcting methodology alone), why average latency is close to useless
  - `handbook/performance/logging-metrics-tracing-and-opentelemetry.md` (T-1205, IWI 6.90) — a real 4-span OpenTelemetry trace, the shared-`traceId` reconstruction mechanism, why logs/metrics/traces are complementary not redundant
  - `handbook/performance/performance-methodology-and-slo-error-budgets.md` (T-1201/T-1206, IWI 6.90/6.80) — USE/RED applied retroactively to Week 9's GC log and Week 8's Kafka consumer group, a real 30-day error-budget simulation showing one incident consuming ~14% of a month's budget
  - All five cross-linked to each other and to prior chapters (Clean/Hexagonal Architecture, GC log analysis, Kafka consumer groups, resilience patterns, executor sizing); all traces verified against `practice/java/week-11/` real executed output

### Changed (continued)
- `study-packs/week-11/01-test-strategy-and-test-doubles.md` through `05-performance-methodology-and-slo-error-budgets.md` — slimmed to a per-section summary + link to their new canonical chapters, numbered headings kept stable because `MANIFEST.md` cites `02-integration-testing-against-real-dependencies.md` §4 directly.
- `study-packs/week-11/MANIFEST.md`, `README.md` — word counts and purpose descriptions updated to match (13,357 → 7,635 total words, verified; all five Week 11 T-topics now summary + canonical-link).

### Fixed
- `00-project/knowledge-architecture-blueprint.md`, `blueprint-v1.1-corrections.md`, `learning-roadmap.md` — the six navigational self-references among the four Phase 1–3 documents (`Foundation document:`, `Supersedes:`, `Built on:`, `Deliverables:`, and two "structure it implements"/"proceed to" mentions) updated from their pre-normalization filenames (`00-Knowledge-Base-Audit-Report.md`, `01-Knowledge-Architecture-Blueprint.md`, `Blueprint-v1.1-Corrections.md`, `00-Roadmap.md`) to the real, current kebab-case filenames (`knowledge-base-audit.md`, `knowledge-architecture-blueprint.md`, `blueprint-v1.1-corrections.md`, `learning-roadmap.md`). `knowledge-architecture-blueprint.md` §9's proposed-TOC narrative (its old `00-Roadmap.md`/`01-Java-Core/`-style structure diagram, explicitly retained in full as historical record) and `learning-roadmap.md`'s old-folder-scheme chapter-path preview (`07-Architecture/01-clean-hexagonal.md`) were deliberately left untouched, per `CLAUDE.md`'s own instruction to interpret pre-normalization folder names conceptually rather than rewrite historical planning artifacts.

### Added (Phase 4 + Phase 5 combined — three new-domain weeks)
- Java Core, Collections, and Cloud & Infrastructure had zero study-pack coverage anywhere in this program prior to this entry (confirmed by auditing every existing study-pack file's `domain:` front-matter field — none matched these three domains). This entry closes that gap at the top-5-by-IWI depth per domain, producing both new study-pack weeks (Phase 4) and their canonical handbook chapters (Phase 5) together, rather than the usual two-step "study pack first, elevate later" sequence used elsewhere in this programme.
- `study-packs/week-13/` — Java Core (T-107 Streams & Collectors, T-101 equals/hashCode/Comparable, T-104 Generics erasure & PECS, T-105 Exception design, T-103 Immutability & defensive copying), with matching canonical chapters in `handbook/java-core/` (opening that domain). Real, executed Java evidence throughout: measured stream laziness/short-circuiting, a real ~6.6x parallel-stream slowdown after JIT warmup, a real HashSet failing to deduplicate on a broken hashCode(), a real TreeSet silently dropping a distinct element on a Comparable/equals mismatch, real type-erasure and heap-pollution traces via reflection, real suppressed-exception vs. lost-exception try-with-resources behavior, and a real mutable-state leak through both a constructor and a getter. 11 supporting files (coding practice, flashcards, mock, code-review exercise, checklist, resources) plus MANIFEST/README, all with real `wc -w` counts.
- `study-packs/week-14/` — Collections (T-201 HashMap internals — the single most-asked Java data structure question per the blueprint, T-205 ConcurrentHashMap internals, T-207 BlockingQueue family, T-202 ArrayList/LinkedList internals, T-209 Collection selection decision matrix), with matching canonical chapters in `handbook/collections/` (opening that domain). Real evidence via `--add-opens java.base/java.util=ALL-UNNAMED` reflection: HashMap's lazy init and resize-at-threshold-13 behavior, a real ~3,076x lookup slowdown from a constant hashCode with reflection-confirmed `TreeNode` treeification, a real 8-thread HashMap corruption (160,000 expected, ~68,683 actual) vs. ConcurrentHashMap correctness, a real get()-then-put() lost-update rate (~26,212 of 160,000) fixed by `merge()`, real `ArrayBlockingQueue`/`SynchronousQueue` blocking confirmed via thread state, ArrayList's ~1.5x growth factor confirmed via reflection, and a real ~320x/~117x random-access/front-insertion gap between ArrayList and LinkedList. Same supporting-file set as Week 13.
- `study-packs/week-15/` — Cloud & Infrastructure (T-1003 Kubernetes resource limits/probes/JVM sizing — the blueprint's named highest-value entry in this domain, T-1002 Kubernetes objects/scheduling/networking, T-1007 Cloud cost & scaling economics, T-1009 CI/CD pipeline design & deployment strategies, T-1006 AWS core services), with matching canonical chapters in `handbook/cloud/` (opening that domain, and this programme's first infrastructure-shaped rather than algorithm-shaped domain). Evidence is scoped differently and stated explicitly per chapter: real Docker containers with real cgroup memory limits (not a live Kubernetes cluster) measuring container-aware JVM heap sizing across three memory limits — including a real, initially-surprising small-container floor explained by `MinRAMPercentage`, and a real, deliberately-reproduced contrast between a clean `java.lang.OutOfMemoryError` (exit 1) and a genuine OOMKilled process (exit 137, `docker inspect` confirms `OOMKilled=true`, zero application-level signal); real, syntax-validated (via `ruby -ryaml`, not a live API server) Kubernetes and GitHub Actions manifests; real, independently-rechecked worked arithmetic for cloud cost decisions using clearly-labeled illustrative unit prices. Supporting-file set adapted to the domain: a hands-on lab (`06-hands-on-lab.md`) replaces the usual coding-practice file, and a full infrastructure-design exercise replaces the usual system-design exercise.
- All 15 new canonical chapters cross-linked to each other within their week and to prior weeks' relevant chapters (e.g., HashMap internals ↔ the Java Core equals/hashCode chapter; the Kubernetes JVM-sizing chapter ↔ Week 9's GC log analysis chapter).

### Added (JVM depth — closing the thinnest handbook domain)
- `00-project/coverage-audit-2026-07-31.md` — a repository-wide coverage audit cross-referencing the blueprint's Master Topic Register (198 unique topics across 16 domains — the register table's real count, distinct from the blueprint's own stale "124 topics" prose figure) against actual `handbook/` chapters, study-pack front matter, `practice/` problem counts, story/mock counts, and the Phase 6 complementary-deliverable directories. Found JVM at 1/12 register topics covered (only Week 9's GC-fundamentals chapter), the thinnest domain with any coverage at all; Security at 0/7; coding-problem volume at 60 of a stated 150–170 target; and `interview-playbook/`/`cheat-sheets/`/`architecture-atlas/`/`production-cookbook/`/`behavioral-handbook/` almost entirely unbuilt (three of six directories don't exist).
- `study-packs/week-16/` — JVM Internals Depth (T-304 G1 remembered sets & write barriers, T-307 memory leak diagnosis & heap dump analysis, T-301 JVM memory layout & runtime regions, T-312 JVM flags & container ergonomics, T-308 JIT tiered compilation & deoptimization), with matching canonical chapters in `handbook/jvm/` (bringing that domain from 1/12 to 6/12 register topics covered). Written full-depth from the start (no separate slimming pass needed, following Weeks 13–15's authoring pattern). Real, executed evidence throughout: a real ~1,841x dirty-card difference between a low- and a high-cross-region-write G1 workload (far exceeding the ~8x difference in pause count between the two, isolating the cost driver to write pattern specifically); a real listener-registration leak showing live-instance counts growing 32,701 → 67,167 via `jmap -histo:live` versus zero in a fixed version, plus a real 201MB `.hprof` heap dump (verified by magic header, not committed); a real `OutOfMemoryError: Metaspace` after 5,275 dynamically-generated classes with heap at only 18MB of a 512MB max; real `StackOverflowError` depth scaling from 1,479 to 413,005 across `-Xss` values with heap held constant; real Docker-container evidence that JVM CPU detection reflects `--cpus` quota (not host core count) and that `-XX:MaxRAMPercentage` scales the heap cap proportionally on an unchanged container memory limit; and a real, measured JIT deoptimization (a monomorphic call site's C2 compilation marked "made not entrant" the instant a second type was introduced, costing ~2x on the first affected call versus the same call re-run after recompilation).
- `.gitignore` — added `*.hprof` (heap dumps are large, environment-specific binary artifacts, matching the existing `*.log`/`*.class` exclusions).
- `handbook/jvm/gc-fundamentals-and-log-analysis.md` — cross-linked to the three new chapters that build directly on its region/collection vocabulary (G1 remembered sets, memory leak diagnosis, memory layout).

### Added (Security domain closure — 0/7 to 7/7, first domain fully closed)
- `study-packs/week-17/` — Security, all 7 register topics (T-1301 OWASP Top 10 for backend services, T-1303 applied cryptography, T-1302 AuthN/AuthZ RBAC vs. ABAC, T-1305 injection/input validation/output encoding, T-1307 multi-tenancy isolation models, T-1304 secrets management & key rotation, T-1306 supply chain security/SBOM), with matching canonical chapters in `handbook/security/` (bringing that domain from 0/7 to 7/7 — the first domain in the entire 198-topic register closed to full coverage). Written full-depth from the start, same authoring pattern as Weeks 13–16. Real, executed evidence throughout: a real IDOR (missing ownership check leaks another user's invoice) and a real SSRF (a "URL preview" service leaking a fake internal metadata endpoint's credentials, blocked by a resolved-destination allowlist); real adaptive-cost PBKDF2 password hashing, a real EC signature failing verification the instant one byte changes, and a real self-signed TLS 1.3 handshake negotiating `TLS_AES_256_GCM_SHA384` and a hybrid post-quantum key-exchange group; a real RBAC-vs-ABAC comparison where three users sharing one role get two wrong answers under RBAC and correct, differentiated answers under ABAC; a real live SQL-injection authentication bypass against PostgreSQL 16 (`admin' --` truncating the password check) closed by a `PreparedStatement`, and a real stored-XSS payload neutralized by output encoding; a real PostgreSQL Row-Level Security demo proving per-tenant isolation and its fail-closed default, plus the critical caveat that a superuser role bypasses RLS entirely; a real AES-256-GCM envelope-encryption key rotation with zero downtime, and real proof that retiring a key before its re-encryption sweep completes permanently breaks decryption; and a real Docker Scout SBOM (213 packages) and CVE scan (13 vulnerabilities, including one CRITICAL in a transitive package) against `eclipse-temurin:21-jre`, the same base image used in Week 16's container-ergonomics chapter.
- `.gitignore` — added `*.p12` and `*.jks` alongside the pre-existing `*.pem`/`*.key` exclusions (this week's TLS demo was the first in this repository to generate a PKCS12 keystore).
- `handbook/jvm/jvm-flags-and-container-ergonomics.md` — cross-linked to the new supply-chain chapter, since both discuss the same `eclipse-temurin:21-jre` base image from different angles (ergonomics vs. vulnerability surface).

### Added (Testing domain closure — 3/8 to 8/8, second domain fully closed)
- Coverage correction: the 2026-07-31 audit reported Testing at 2/8; `handbook/testing/test-strategy-and-test-doubles.md`'s own "Topic register: T-1101/T-1103" line shows T-1103 (Mockito, test doubles, mocking boundaries) was already covered, bundled into that chapter but never tagged in study-pack front matter — an undercounting the audit's own methodology section already flagged as a known limitation of bundled-chapter detection. Real count going into this week was 3/8, leaving exactly 5 genuinely uncovered topics.
- `study-packs/week-18/` — Testing, the 5 remaining register topics (T-1106 performance & load testing methodology, T-1108 writing tests live in an interview, T-1105 contract testing for services, T-1102 JUnit 5 architecture & advanced features, T-1107 mutation & property-based testing), with matching canonical chapters in `handbook/testing/` (bringing that domain from 3/8 to 8/8 — the second domain in the entire register closed to full coverage, after Security in Week 17). Written full-depth from the start. Real, executed evidence throughout: a real load test where mean (12.45ms) and p50 (4.17ms) both looked healthy while p95 (150.54ms) revealed a real, injected 1-in-20 slow path; a real four-step red-green-refactor TDD cycle on a run-length-encoding kata with captured JUnit 5 console output at every RED/GREEN transition; a real consumer-driven-contract-style verification test passing against a compliant provider and failing with a precise, named-field message against a real breaking change; a real 10-test JUnit 5 class covering `@ParameterizedTest`/`@TestFactory`/a custom extension/`@Nested`, plus real tag-filtering evidence (exactly 1/10 vs. 9/10 selected); a real property-based test finding a genuine merge-sorted-arrays bug on trial 2 that two hand-picked, unconsciously-biased example tests both missed, and a real single-token mutant (`>=`→`>`) surviving a weak test suite and killed only after adding the exact boundary-value test the weak suite lacked.
- Deliberately rescoped `handbook/testing/performance-and-load-testing-methodology.md` mid-authoring after discovering real overlap with the pre-existing `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` (T-1204) — the new chapter owns the testing-*practice* half (load/stress/soak design, release-process placement) and explicitly links to, rather than duplicates, T-1204's percentile-mathematics and coordinated-omission depth. Added a reciprocal cross-link from T-1204 back to the new chapter.
- Mutation and property-based testing demonstrated without PIT or jqwik (neither available in the local Maven cache; verified before deciding to hand-roll rather than fabricate) — both hand-rolled versions produce real, executed, directly comparable evidence; chapters link to PIT/jqwik as recommended production tooling.

### Added (JVM domain full closure — 6/12 to 12/12, third domain fully closed)
- `study-packs/week-19/` — JVM, the 6 remaining register topics (T-303 GC roots/reachability/reference strength, T-305 ZGC & Shenandoah, T-310 safepoints & stop-the-world mechanics, T-302 object layout/headers/compressed oops, T-311 native memory/direct buffers/off-heap, T-309 escape analysis & scalar replacement), with matching canonical chapters in `handbook/jvm/` (bringing that domain from 6/12 to 12/12 — the third domain in the entire register closed to full coverage, after Security in Week 17 and Testing in Week 18). Written full-depth from the start. Real, executed evidence throughout: all four `java.lang.ref` reference-strength behaviors demonstrated directly (strong survives GC, weak clears immediately, soft survives under no memory pressure, phantom never returns the referent and only enqueues post-collection); a real G1-vs-ZGC pause-time comparison on an identical workload (G1 max 0.748ms vs. ZGC's real 1-40 microsecond safepoints — but 218 real ZGC allocation-stall events and ~22% less throughput, an honest trade-off rather than a clean win); three distinct real safepoint operations from one run spanning a ~1,500x cost range (`FindDeadlocks` ~1μs, `PrintThreads` ~84μs, `G1CollectFull` ~1.59ms); a real ~42% memory-footprint difference (134MB vs. 191MB for 5M nodes) from the compressed-oops flag alone; a real direct-buffer `OutOfMemoryError` at 256MB on a 32MB heap plus exact-match NMT evidence that direct memory is tracked entirely separately from Java Heap; and a dramatic real 0-vs-362 GC-pause-count contrast for an identical 600-million-iteration hot loop with escape analysis on vs. off.
- Coverage correction, continuing the pattern from Weeks 18/19: `gc-fundamentals-and-log-analysis.md`'s own topic-register line credits T-303, but its actual content is G1-implementation-centric (young/mixed/full mechanics, GC-log reading), not a treatment of GC roots, formal reachability, or the reference-strength hierarchy — unlike Testing's T-1103, T-303 was NOT credited as already-covered; instead its new chapter was scoped explicitly to the genuinely missing ground and cross-linked to, not duplicating, the existing chapter.
- 5 cross-links added between new and existing `handbook/jvm/` chapters (gc-fundamentals ↔ gc-roots/zgc-shenandoah, jit-deopt ↔ safepoints/escape-analysis, jvm-memory-layout ↔ object-layout/native-memory, container-ergonomics ↔ native-memory).

### Added (Coding-problem volume gap — Phase 1: 60 → 84 problems)
- `study-packs/week-20/` — a bounded, targeted coding-volume sprint (not an attempt at the full ~90-problem gap in one pass, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation). A per-pattern audit (not previously run) found the 60-problem shortfall wasn't evenly spread: Bit Manipulation had zero coverage; Linked Lists, Greedy, and Intervals each had exactly one problem (10-12% of register target); Graphs — the register's single highest-weight D14 topic (⭐, IWI 6.25) — sat at only 27% (6/22) despite already having more raw problems than all but one other pattern. Added 24 new, real, compiled-and-executed LeetCode solutions targeting exactly these five gaps: Linked Lists (LC 21, 141, 19, 143, 138 — 1/10 → 6/10), Greedy (LC 45, 134, 621, 763, 402 — 1/10 → 6/10), Intervals (LC 57, 253, 452, 986 — 1/8 → 5/8), Bit Manipulation (LC 136, 191, 268, 371, 338 — 0/6 → 5/6), Graphs (LC 743 Dijkstra, 684 Union-Find, 1584 Prim's MST, 994 multi-source BFS, 787 Bellman-Ford-style — 6/22 → 11/22). All 24 problems verified via a shared `Check.java` assertion helper (matching Week 1's existing convention): 48/48 real assertions pass. Two hand-computed expected test values (LC 1584 and LC 787) were caught wrong on first run and corrected by hand-tracing the algorithm against the problem's real constraints — both were test-authoring errors, not implementation bugs.
- Deliberately did **not** touch T-1418 (Advanced Structures: segment tree/Fenwick/rolling hash), the other zero-coverage pattern found in the audit — it's Expert tier, which the blueprint's own tier system explicitly flags as "the lowest-priority tier in the entire blueprint... the single most common misallocation in senior interview prep." Closing a Core-tier zero (bit manipulation) took priority over an Expert-tier one, per the blueprint's own stated guidance rather than closing every zero indiscriminately.

### Added (Coding-problem volume gap — Phase 2: 84 → 106 problems)
- `study-packs/week-21/` — a second bounded coding-volume sprint continuing Week 20's Phase 1. A dedicated research audit (Explore subagent, not a guess) confirmed exact existing LC coverage per pattern before writing anything, avoiding duplication with problems already solved elsewhere (e.g. LC 402 and LC 42 are monotonic-stack-shaped but correctly already categorized as Greedy and Two-pointers respectively — not re-added here). Added 22 new, real, compiled-and-executed LeetCode solutions across four patterns: Tries (LC 211 wildcard search, 212 Word Search II, 421 binary-trie max XOR, 648 Replace Words, 677 Map Sum Pairs — 1/6 → 6/6, full register closure), Backtracking (LC 17 phone letters, 79 Word Search, 47 Permutations II, 40 Combination Sum II, 51 N-Queens — 4/14 → 9/14), Stacks/monotonic-stack (LC 496 Next Greater Element I, 84 Largest Rectangle in Histogram, 150 Evaluate RPN, 232 Queue via Stacks, 503 Next Greater Element II circular — 3/10 → 8/10), Dynamic Programming (LC 72 Edit Distance, 213 House Robber II, 518 Coin Change II, 494 Target Sum, 64 Min Path Sum, 516 Longest Palindromic Subsequence, 309 Buy/Sell Stock with Cooldown — 9/32 → 16/32). All 22 problems verified via the same `Check.java` assertion helper as Week 20: 54/54 real assertions pass. Two expected test values (LC 648 in Tries, LC 503 in Stacks) were caught wrong on first run and corrected by hand-tracing the algorithm against the problem's real constraints — both were test-authoring errors, not implementation bugs.
- Deliberately left DP at 50% (16/32) despite it being the largest absolute remaining gap — closing all 23 remaining DP problems in one file would violate `CLAUDE.md`'s instruction against generating an entire deliverable in one operation, and DP's breadth (knapsack variants, interval DP, tree DP, digit DP, bitmask DP, DP-on-graphs) justifies a dedicated future batch rather than rushing the remainder here.

### Added (Coding-problem volume gap — Phase 3: 106 → 125 problems)
- `study-packs/week-22/` — a third bounded coding-volume sprint continuing Weeks 20–21. A dedicated research audit (Explore subagent) confirmed exact existing LC coverage per pattern and resolved a genuine ambiguity: "rate limiter" and "bounded queue" style problems could plausibly belong to either Concurrency Coding or Design-Style — each was assigned to exactly one pattern based on whether the canonical LeetCode problem actually requires thread coordination (LC 1188 Design Bounded Blocking Queue → Concurrency, since it needs real `wait`/`notify`; LC 359 Logger Rate Limiter → Design, since it's a plain single-threaded frequency check). Added 19 new, real, compiled-and-executed LeetCode solutions across four patterns: Hashing (LC 217 Contains Duplicate, 560 Subarray Sum Equals K, 349 Intersection of Two Arrays, 202 Happy Number, 454 4Sum II — 4/12 → 9/12), Binary Search (LC 34 Find First/Last Position, 74 Search a 2D Matrix, 153 Find Minimum in Rotated Sorted Array, 1011 Capacity to Ship Packages, 4 Median of Two Sorted Arrays — 4/12 → 9/12), Concurrency Coding (LC 1117 Building H2O, 1195 Fizz Buzz Multithreaded, 1226 Dining Philosophers, 1188 Design Bounded Blocking Queue — 3/8 → 7/8), Design-Style (LC 460 LFU Cache, 981 Time Based Key-Value Store, 355 Design Twitter, 1472 Design Browser History, 359 Logger Rate Limiter — 4/10 → 9/10). All 19 problems verified via the same `Check.java` assertion helper as Weeks 20–21: 56/56 real assertions pass. The concurrency suite (real `Thread`/`Semaphore`/`ReentrantLock`/intrinsic-lock code, not simulated) was re-run 5 consecutive times to check for scheduling-dependent flakiness — identical 10/10 result every run; Dining Philosophers' deadlock-freedom was verified via a bounded `join(10_000)` timeout that a real deadlock would have tripped. Three expected test values (LC 4 in Binary Search, two in LC 1472 in Design-Style) were caught wrong on first run and corrected by hand-tracing the algorithm against the problem's real constraints — all were test-authoring errors, not implementation bugs.
- Deliberately stopped Concurrency Coding at 7/8, not 8/8 — the plausible 5th candidate, LC 1242 (Web Crawler Multithreaded), is LeetCode Premium-only and its exact spec could not be verified against the free problem statement; reconstructing it from an unverified description risked citing a solution to a problem that doesn't match LeetCode's real grader, which `CLAUDE.md` prohibits.

### Planned
- Next: continue the coding-problem volume gap (125 of 150–170 target now; DP remainder at 50% despite highest raw count is the largest absolute gap, arrays/two-pointers at 39% is the largest gap among patterns still under 40%, heaps at 42%, trees at 44%, graphs at 50% are the remaining next-thinnest patterns) or start Phase 6 — undecided.
- Phase 6 complementary deliverables (interview-playbook, cheat-sheets, architecture-atlas, production-cookbook, behavioral-handbook) — not yet started; behavioral stories (13 built) and mock-interview content (21 artifacts) flagged as the cheapest wins since they already exist and just need consolidating.

---

## [2026-07-29] — Plan A complete (Weeks 1–6), rolling into Plan B

### Added
- `study-packs/week-01/` through `week-06/` — full Plan A sprint, 74 chapter files, ~60,400 words total
- `practice/java/week-01/` through `week-05/`, `practice/sql/week-01/` through `week-04/` — every technical claim in Weeks 1–5 backed by real, executed Java (OpenJDK 21.0.12) or PostgreSQL 16 (via Docker), not description alone. Highlights: a live-reproduced LRU cache eviction bug (Week 1), a real write-skew anomaly reproduced at `REPEATABLE READ` and prevented at `SERIALIZABLE` (Week 3), a real cache-stampede/retry-storm/fencing-token trio (Week 4), a real idempotency-key mechanism under genuine concurrency (Week 5)
- Week 6: consolidation only, per the roadmap's own "no new topics" instruction — full 72-card flashcard retrieval pass, weak-list repair, two cross-week mocks, verbatim D1–D4 diagnostic re-run, and the final Interview-Readiness rubric (Delta-vs-Day-0)

### Corrected (see Errata register below for full status)
- Errata #1 (LRU cache eviction bug) — fixed and verified, Week 1
- Errata #6 (monotonic-stack diagram/code contradiction) — corrected and explained as a structural impossibility (a values-only stack cannot recover the index the answer requires), Week 2

### Known gap
- Errata #7 (topological-sort diagram depicting a cycle) was scheduled for Week 4 in the original register, but Week 4's actual deliverable (LC 210, Kahn's algorithm) implemented topological sort correctly without explicitly calling out or correcting the specific diagram defect from the audit. Left open below rather than marked fixed, since the specific documentation error was not directly addressed.

---

## [2026-07-29] — Repository initialization

### Added
- Repository structure: `00-project/`, `study-packs/week-01..06/`, `handbook/<13 domains>/`, `interview-playbook/<4 areas>/`, `practice/<5 areas>/`, `flashcards/`, `cheat-sheets/`, `templates/`, `resources/`, `scripts/`, `archive/`
- `.gitignore`, `.editorconfig`, `.markdownlint.json`
- `00-project/file-mapping.md` — mapping and SHA-256 verification for the four documents actually migrated
- `scripts/validate.py` — moved from repo root, unchanged

### Migrated (content unchanged, filenames normalized, byte-verified)
- `00-Knowledge-Base-Audit-Report.md` → `00-project/knowledge-base-audit.md`
- `01-Knowledge-Architecture-Blueprint.md` → `00-project/knowledge-architecture-blueprint.md`
- `Blueprint-v1.1-Corrections.md` → `00-project/blueprint-v1.1-corrections.md`
- `00-Roadmap.md` → `00-project/learning-roadmap.md`

Source: `~/Downloads/Java-Interview-Handbook/` — the only location these were found. All four verified byte-identical to source by SHA-256.

### Archived, not migrated
- A pre-existing `MANIFEST.md`, `CHANGELOG.md`, `file-mapping.md`, `repository-tree.md`, and `study-packs/week-02/MANIFEST.md` were found already sitting in this directory (untracked, zero commits) before initialization. They asserted a completed, checksum-verified migration of 30 files — the four project documents above, plus 13 Week 1 and 13 Week 2 study-pack files — and claimed the Week 1/2 Java code had been executed on OpenJDK 21 (42/42 and 37/37 assertions passing).
- **None of the 26 study-pack files exist anywhere on this machine.** Confirmed by exhaustive search (Spotlight by name and by distinctive content phrase, a `find` sweep of the home directory, `~/.Trash`). Whatever produced those documents did not persist the work they describe.
- The five stale documents are preserved for provenance in `archive/pre-initialization-scaffolding/`, with an explanatory note. They are not part of this repository's factual record and should not be cited as evidence that Phase 4 happened.

### Known gap
- **Phase 4 (Week 1 study pack) and Phase 5 (Week 2) have not been produced.** `study-packs/week-01/` through `week-06/` are empty scaffolding. This corrects the record left by the archived documents, which claimed otherwise.

---

## Programme history (pre-repository, verifiable from `00-project/`)

### Phase 1 — Knowledge base audit (`00-project/knowledge-base-audit.md`)
- Audited 8 Notion assets, ~333 rows, against Senior/Staff interview requirements, read-only
- Overall quality 4.1/10; ~22% coverage of the target interview surface
- 7 defective code implementations and 6 incorrect technical claims identified by reading the actual stored implementations
- Flagged (not fixed — Notion is read-only) an expired OAuth token embedded in a source-material link

### Phase 2 — Knowledge architecture blueprint (`00-project/knowledge-architecture-blueprint.md`)
- Interview Weight Index (IWI) defined across 5 weighted factors
- Topic register scored across 16 domains
- All 25 highest-IWI topics found to be gapped

### Phase 3 — Corrections and roadmap (`00-project/blueprint-v1.1-corrections.md`, `00-project/learning-roadmap.md`)
- v1.1 corrections: topic count 124 → 198; effort 1,338h → 1,371h; single RoS metric split into Knowledge RoS and Readiness RoS; a tested composite metric rejected and documented as such; all frequency estimates relabelled `[H]` heuristic
- Roadmap: three plans (A 6wk / B 12wk / C 9–14mo), three parallel tracks every week, 10/20/30h variants, Weeks 1–2 pre-committed to named real-interview feedback

---

## Errata register (from Phase 1, to be corrected as each topic is written)

| # | Defect | Status |
|---|---|---|
| 1 | LRU cache `put()` evicts a valid entry on key update | ✅ Fixed and verified — Week 1 (`study-packs/week-01/07-java-coding-practice.md`) |
| 2 | Top-K relies on unspecified `PriorityQueue` iteration order | Open — scheduled Week 10 (Plan B) |
| 3 | Backtracking `permute` uses `contains()`, wrong on duplicate inputs | In progress — Plan B Week 7 |
| 4 | Greedy comparator `a[1]-b[1]` integer-subtraction overflow | Open |
| 5 | Suffix array presented as efficient without an O(n² log n) caveat | Open — deferred |
| 6 | Monotonic stack diagram (indices) contradicts code (values) | ✅ Fixed and explained — Week 2 (`study-packs/week-02/07-java-coding-practice.md`) |
| 7 | Topological-sort diagram depicts a cycle while asserting a valid order exists | **Still open** — Week 4 implemented topological sort correctly (LC 210) but did not explicitly correct this specific documentation defect; corrected here rather than silently marked done |

Also open: CMS listed without noting removal in JDK 14, inverted `Set` hierarchy diagram, `NavigableSet` miscategorized as a peer implementation, correlated-hash Bloom filter.

✅ Fixed — Week 9 (`study-packs/week-09/01-java-memory-model-and-volatile.md`, `03-deadlock-races-and-thread-diagnostics.md`): incorrect thread-lifecycle states (missing `TIMED_WAITING`) and `volatile` reduced to "prevents caching" instead of happens-before. Both corrected with real, executed evidence — the real six-value `Thread.State` enum printed from a running JVM, and a reliably-reproducing (3/3 runs) visibility-failure demonstration showing what "prevents caching" gets wrong about the actual mechanism (compiler/JIT reordering, not CPU cache coherence).

The remaining items above are not fixed yet — they are documented so the wrong versions get explicitly unlearned when each topic is written, per the audit's recommendation.
