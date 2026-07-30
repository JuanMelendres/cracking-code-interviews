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

### Planned
- Next Phase 5 chapter group: TBD
- Next Phase 5 chapter group: TBD
- `fix/normalize-cross-references` — the four Phase 1–3 documents cross-reference each other by their pre-normalization filenames in prose (inside backticks, not Markdown links, so nothing is broken, but the names no longer match)

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
