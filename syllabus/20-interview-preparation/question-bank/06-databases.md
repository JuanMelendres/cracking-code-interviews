---
title: "Interview Question Bank — 06-databases"
document_type: interview-question-bank
domain: 20-interview-preparation
status: pilot
version: 1.0
last_updated: 2026-09-27
related:
  - ../../06-databases/INDEX.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Databases

Pilot compendium for `06-databases`, per `00-project/interview-question-bank-plan.md`.
Every question here is mined from this domain's own already-written, already-verified
canonical chapters — their `## Interview Questions` sections (deep treatment) and
`## Flashcards` sections (lighter, quick-fire) — not invented independently. Each
canonical chapter stays the authoritative source; this file re-organizes its real
questions by seniority tier and links back rather than duplicating the full answer.

## How to read this

Each main question shows **what a correct answer actually looks like at each
seniority tier**, for that specific question — not a generic, repeated paragraph.
Where a tier genuinely doesn't apply (many Staff-caliber production questions have no
honest Junior-level version), that's stated directly rather than padded with a
strained answer. This follows `CLAUDE.md`'s own Depth-by-Interview-Level definitions:

| Tier | What a correct answer demonstrates |
|---|---|
| **Junior** | Basic definition, correct terminology, a simple example |
| **Mid** | Implementation awareness, the common trade-off, basic production use |
| **Senior** | Internals, failure modes, alternatives, decision criteria |
| **Staff** | Cross-system consequences, migration/operational cost, organizational judgment |

**Honest count.** This domain's 16 chapters yielded **48 deep questions** (each with
full Interview Question Standard treatment already in the canonical chapter) and
**57 quick-fire questions** (from Flashcards), plus **5 already-leveled Junior/Mid
questions** from the one Junior Fundamentals chapter — **110 real questions total**,
short of the requested ~150. (Updated 2026-09-27: `database-normalization-1nf-through-bcnf.md`
and `views-and-materialized-views.md` had complete Interview Questions sections
never indexed — a stale-index gap, not a content gap. Added 8 questions + 9
quick-fire cards.) Padding to an exact quota with invented, low-value
questions was rejected per the plan's own sourcing discipline. Reaching closer to 150
for this domain would mean either (a) writing new Junior/Mid questions for the 13
chapters that currently target `senior`/`staff` only, genuinely grounded in their
existing content, or (b) pulling in real cross-domain questions that touch databases
from `05-spring`, `09-messaging-event-driven`, and `10-distributed-systems` — both
listed as open follow-up work, not done in this pass.

---

## SQL and Relational Database Fundamentals (T-2202)

This is the domain's one Junior Fundamentals chapter — its own `## Interview
Questions` section already tags each question by seniority, so it's reproduced
directly rather than re-derived.

### Q1 — What's the difference between a primary key and a foreign key?

**Canonical treatment:** [SQL and Relational Database Fundamentals §15](../../06-databases/sql-and-relational-database-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** A primary key uniquely identifies each row in its own table; a foreign key references another table's primary key, expressing a relationship and letting the database enforce that the referenced row actually exists. This is the target tier for this exact question.
- **Mid/Senior/Staff:** Not typically asked at this level in this exact form — a Mid+ interviewer would instead probe *why* the constraint matters operationally (see Q4 below).

### Q2 — What's the difference between `INNER JOIN` and `LEFT JOIN`?

**Canonical treatment:** [SQL and Relational Database Fundamentals §15](../../06-databases/sql-and-relational-database-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** States the syntax difference.
- **Mid:** Illustrates with a concrete example — an unmatched row disappears under `INNER JOIN`, survives with `NULL`s under `LEFT JOIN`. This is the target tier: a weak answer describes syntax without describing which rows end up missing.
- **Senior/Staff:** Not typically asked in this exact form at these tiers.

### Q3 — A report must show every customer's order count, including customers with zero orders. What goes wrong with `INNER JOIN`?

**Canonical treatment:** [SQL and Relational Database Fundamentals §9, §15](../../06-databases/sql-and-relational-database-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Recognizes something is wrong with the join choice, even if imprecisely.
- **Mid:** Correctly identifies that customers with zero orders are silently dropped from the report entirely, rather than showing `0` — the `GROUP BY`-plus-join edge case this is designed to test. Target tier.
- **Senior/Staff:** Not typically asked in this exact form; a Senior+ version would ask about a live incident this pattern caused (see the domain's Production Scenarios instead).

### Q4 — Why enforce a foreign key at the database level instead of checking it in application code?

**Canonical treatment:** [SQL and Relational Database Fundamentals §13, §15](../../06-databases/sql-and-relational-database-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes having worked in a system with multiple writers.
- **Mid:** Names "the database checks it automatically" without the multi-writer framing.
- **Senior:** A database constraint is the one enforcement point every writer is forced through, regardless of how many services or code paths write to the table; application-level checks must be independently re-implemented correctly everywhere data enters the table. Target tier.
- **Staff:** Extends this to a real audit/compliance framing — see Q5.

### Q5 — A table with a dozen writers over several years just failed a data-integrity audit with thousands of orphaned rows. What's your first question?

**Canonical treatment:** [SQL and Relational Database Fundamentals §13, §15](../../06-databases/sql-and-relational-database-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Not typically asked — this is an organizational-diagnosis question, not a mechanics question.
- **Senior:** Asks whether the referencing column ever had a real foreign key constraint at all.
- **Staff:** States the diagnosis directly and unprompted: the relationship was almost certainly only ever enforced by convention, not by the schema — framed as a structural failure across every writer, not a one-off bug in any single one. Target tier.

---

## Database Index Structures — B+Tree, Composite, Covering (T-609)

### Q1 — Walk the B+Tree lookup path, root to heap.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** States that an index is sorted and lookups are faster than scanning every row, without precise mechanism.
- **Mid:** Names root → internal nodes → leaf node, even without the heap-fetch step.
- **Senior:** Full path: root → internal routing nodes (keys only, no row data) → leaf node → heap tuple ID → heap page fetch for the actual row (unless the index is covering) — correctly distinguishing "index has the value" from "index points to the row."
- **Staff:** Explains *why* high fan-out specifically matters — minimizing disk page reads, the exact reason B-Trees were invented — connecting tree shape to physical I/O cost, ideally citing a real measured number from having actually run `EXPLAIN ANALYZE` in practice.

### Q2 — Composite index `(customer_id, created_at)`: which queries does it serve?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes that column order in a multi-column index matters at all.
- **Mid:** States the rule but can't yet identify a failing example unprompted.
- **Senior:** States the rule precisely — serves `customer_id` alone and `customer_id`+`created_at` together, never `created_at` alone — and correctly identifies the failing query shape when given one.
- **Staff:** States the rule, identifies the failing shape, and proposes a cost-aware remedy (a second index, or reordering) while discussing the write-cost trade-off of adding it.

### Q3 — What proves an index-only scan happened, and what has to be true for PostgreSQL to choose one?

**Canonical treatment:** [§ Interview Questions, Q3](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes already knowing what a covering index is.
- **Mid:** Names the covering-index requirement (query columns present in the index) without the `EXPLAIN` markers.
- **Senior:** Names the correct `EXPLAIN` markers (`Index Only Scan`, `Heap Fetches: 0`) rather than just asserting "it's faster."
- **Staff:** Names the visibility-map/`VACUUM` requirement and the planner-cost-model caveat together, and can explain why a covering index over an infrequently-vacuumed table might still show heap fetches.

### Q4 — When is a sequential scan genuinely faster than an index scan?

**Canonical treatment:** [§ Interview Questions, Q4](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes that indexes aren't always faster.
- **Mid:** States that low selectivity makes an index less useful, without the random-vs-sequential I/O framing.
- **Senior:** Gives the correct qualitative reasoning (random I/O cost vs. sequential I/O cost) and rejects a fixed-percentage framing when pressed.
- **Staff:** Explicitly rejects the "fixed percentage" framing unprompted and explains why physical clustering and storage medium change the crossover point.

### Q5 — You added an index and the query got slower. Two distinct mechanisms?

**Canonical treatment:** [§ Interview Questions, Q5](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names write amplification alone (the index itself now costs an update on every write).
- **Senior:** Names both mechanisms when prompted for a second: write amplification, and the new index shifting planner statistics/candidate plans toward a worse choice.
- **Staff:** Names both unprompted and proposes a detection method (`EXPLAIN ANALYZE` before/after, write-latency monitoring).

### Q6 — Why did the planner ignore your index? Give three reasons.

**Canonical treatment:** [§ Interview Questions, Q6](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** Names one reason (usually "the query doesn't match" or similar).
- **Mid:** Names at least two of the three unprompted (low selectivity, stale statistics, function/cast wrapping).
- **Senior:** Names all three.
- **Staff:** Names all three and sketches a concrete expression-index fix on the spot, tying the answer to a measured example rather than a rule recited from memory.

### Q7 — "Clustered vs non-clustered" — what changes when the engine is PostgreSQL?

**Canonical treatment:** [§ Interview Questions, Q7](../../06-databases/index-structures-btree-composite-covering.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes cross-engine experience.
- **Mid:** Recognizes the terms don't map identically across engines, even if fuzzy on the PostgreSQL-specific detail.
- **Senior:** Correctly states PostgreSQL has no maintained clustered index and explains the heap-plus-secondary-index model.
- **Staff:** Names the engine explicitly *before being asked*, and explains PostgreSQL's `CLUSTER` command as a one-off, unmaintained physical reordering, not an ongoing structural guarantee.

---

## Query Planning and EXPLAIN ANALYZE (T-610)

### Q1 — `rows=1000` vs `actual rows=48000`: what does the gap tell you?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/query-planning-and-explain-analyze.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes knowing `EXPLAIN ANALYZE` output.
- **Mid:** Recognizes the gap as a problem, without a specific cause.
- **Senior:** Names stale statistics (needs `ANALYZE`) as the likely cause.
- **Staff:** Names stale statistics *and* correlated-column limitations (per-column stats assume independence), and proposes `CREATE STATISTICS` for the latter.

### Q2 — Nested loop vs. hash join vs. merge join: when does the planner pick each?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/query-planning-and-explain-analyze.md#interview-questions)

**What's expected:**
- **Junior:** Names one join algorithm.
- **Mid:** Describes when nested loop wins (small side, indexed inner lookup).
- **Senior:** Correctly states the cost shape and win condition for all three algorithms.
- **Staff:** Connects the choice to real production consequences of a plan flipping between algorithms as data grows (e.g., a nested loop that was fine at 10K rows becoming ruinous at 10M).

### Q3 — You added an index and the query got slower. Two distinct mechanisms?

**Canonical treatment:** [§ Interview Questions, Q3](../../06-databases/query-planning-and-explain-analyze.md#interview-questions) — same question as T-609 Q5 above; consolidated here rather than duplicated in full, since both chapters ask it about the identical mechanism.

### Q4 — Why did the planner ignore your index? Three reasons.

**Canonical treatment:** [§ Interview Questions, Q4](../../06-databases/query-planning-and-explain-analyze.md#interview-questions) — same question as T-609 Q6 above; consolidated, not duplicated.

---

## Isolation Levels and Concurrency Anomalies (T-611)

### Q1 — Two transactions read a balance and both write. Walk it at all three isolation levels.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/isolation-levels-and-concurrency-anomalies.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes that concurrent writes to the same row need "some kind of protection."
- **Mid:** States that concurrent writes to the same row are handled with locking of some kind, even without precisely separating atomic-`UPDATE` from read-then-write.
- **Senior:** Correctly distinguishes atomic `UPDATE` (safe at READ COMMITTED) from application-level read-then-write (unsafe without an explicit lock).
- **Staff:** Names `SELECT ... FOR UPDATE` unprompted as the READ-COMMITTED-compatible fix, without needing to escalate the whole transaction to SERIALIZABLE.

### Q2 — Explain write skew with a concrete example. *(the discriminating question)*

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/isolation-levels-and-concurrency-anomalies.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — write skew presupposes understanding isolation levels first.
- **Mid:** Attempts a concrete example, even if the invariant or mechanism is stated imprecisely.
- **Senior:** Gives a correct, concrete write-skew example (e.g., the on-call-doctors scenario) and correctly distinguishes it from a lost update when asked.
- **Staff:** Explains precisely *why* REPEATABLE READ misses it — each transaction's own single-row write has no conflict; the violated invariant spans rows neither transaction locked — while correctly stating REPEATABLE READ *does* prevent same-row lost updates.

### Q3 — What must application code do differently to safely use SERIALIZABLE?

**Canonical treatment:** [§ Interview Questions, Q3](../../06-databases/isolation-levels-and-concurrency-anomalies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that SERIALIZABLE transactions can fail and need some form of handling.
- **Senior:** Names retry-on-serialization-failure specifically as the required behavior (SQLSTATE `40001`).
- **Staff:** Extends this to the architectural point: the guarantee only holds if *every* code path touching the protected data both uses SERIALIZABLE and retries — a single unprotected path reintroduces the anomaly.

---

## MVCC in PostgreSQL, Vacuum, and Bloat (T-612)

### Q1 — Why doesn't a plain VACUUM shrink a bloated table?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/mvcc-vacuum-and-bloat.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes that VACUUM and VACUUM FULL are different, even without stating why.
- **Mid:** States VACUUM and VACUUM FULL behave differently, even without precisely naming the mechanism.
- **Senior:** Explains the mechanism precisely (marks space reusable within the file, doesn't compact it) and names VACUUM FULL's real cost (an exclusive lock for the duration).
- **Staff:** Names an online alternative (`pg_repack`) for shrinking a bloated table without VACUUM FULL's full outage-risk lock.

### Q2 — How can a transaction that never touches table X cause table X to bloat?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/mvcc-vacuum-and-bloat.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes understanding MVCC snapshots first.
- **Mid:** States that long transactions "affect vacuum somehow" without the precise mechanism.
- **Senior:** States the snapshot-based mechanism precisely — VACUUM can't remove a dead tuple older than the oldest open snapshot database-wide, regardless of which tables that snapshot's transaction queries.
- **Staff:** Proposes a concrete monitoring and prevention strategy (transaction-age alerting, bounding BI-tool transaction duration).

---

## Locks, Deadlocks, and Lock Escalation (T-613)

### Q1 — Does PostgreSQL escalate row locks to a table lock under high contention?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/locks-deadlocks-and-lock-escalation.md#interview-questions)

**What's expected:**
- **Junior:** Guesses based on general database intuition, likely incorrectly (this is exactly the cross-engine-assumption trap the question is designed to catch).
- **Mid:** Says "no" without being able to explain why.
- **Senior:** Says "no" and correctly names the tuple-header mechanism (PostgreSQL represents row locks on the tuple itself, not as lock-manager entries) as the reason.
- **Staff:** Names the real, different PostgreSQL limit that exists instead (`max_locks_per_transaction`, tied to distinct lockable objects, not row count) and describes a concrete scenario that would actually exhaust it.

### Q2 — Walk me through exactly how PostgreSQL detects and resolves a deadlock.

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/locks-deadlocks-and-lock-escalation.md#interview-questions)

**What's expected:**
- **Junior:** Knows "the database catches deadlocks somehow."
- **Mid:** Knows the database detects and resolves deadlocks automatically by killing one side.
- **Senior:** Names the wait-for graph and the timeout-then-check sequence correctly (`deadlock_timeout`, default 1s, then cycle detection).
- **Staff:** Can read a real `pg_locks` snapshot and identify the actual wait-for relationship it represents, and connects this to a concrete, permanent fix (consistent lock ordering) rather than stopping at "catch the error and retry."

---

## Optimistic vs. Pessimistic Locking (T-604)

### Q1 — Two users edit the same record. Walk both strategies.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/optimistic-vs-pessimistic-locking.md#interview-questions)

**What's expected:**
- **Junior:** Names both terms, even without narrating the mechanism.
- **Mid:** Describes one strategy correctly, gestures vaguely at the other.
- **Senior:** Correctly narrates both, including that optimistic locking allows the stale read to happen and only fails at commit.
- **Staff:** Adds the contention-profile decision criterion and a concrete real cost for each (retry cost vs. block-duration cost).

### Q2 — Does optimistic locking prevent lost updates?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/optimistic-vs-pessimistic-locking.md#interview-questions)

**What's expected:**
- **Junior:** Guesses "yes" (the common, incorrect intuition this question is designed to catch).
- **Mid:** Says "sort of" or hedges without a clear detect-vs-prevent distinction.
- **Senior:** States the distinction precisely and unprompted: it detects them at commit time via a version mismatch, converting a silent overwrite into a loud, recoverable exception.
- **Staff:** Explains why this distinction matters operationally — the caller must implement retry logic, since detection alone doesn't complete the operation.

---

## Data Modelling and Explicit Join Tables (T-605/T-608)

### Q1 — Model many-to-many between `Order` and `Product`. Now the relationship needs `quantity` — what changes, and why was the original `@ManyToMany` a trap?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/data-modelling-and-explicit-join-tables.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes a plain join table can't hold extra columns.
- **Mid:** Proposes an explicit entity for the quantity, even without naming the underlying "trap" framing.
- **Senior:** Correctly proposes the explicit entity (`OrderLine` with its own primary key).
- **Staff:** Identifies the price-history trigger unprompted, not just the quantity case — the less obvious, more consequential version of the same defect.

### Q2 — When is an explicit join entity mandatory rather than optional?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/data-modelling-and-explicit-join-tables.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States a trigger condition, even if imprecisely worded (e.g., "when there's extra data").
- **Senior:** States a reasonable trigger condition.
- **Staff:** States the "as of formation time" framing precisely, and can produce the price-history example unprompted.

---

## Hibernate Flush Modes and Batch Writes (T-606)

### Q1 — `hibernate.jdbc.batch_size=50` is set and a bulk insert job shows no measurable speedup. Walk through your diagnosis.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/hibernate-flush-modes-and-batch-writes.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes JDBC batching knowledge.
- **Mid:** Recognizes that the setting might not be taking effect and suggests checking configuration loading, without naming the identifier-strategy cause.
- **Senior:** Names `IDENTITY` generation as the specific, likely cause and explains the underlying mechanism (Hibernate must know the row's id before batching several inserts together, which `IDENTITY` cannot provide ahead of time).
- **Staff:** Discusses the real migration cost of switching `IDENTITY` to `SEQUENCE` on an already-populated, high-traffic table, not just recommending the switch as if it were free.

### Q2 — Explain a scenario where `FlushMode.AUTO`'s default behavior could surprise a developer, and one where `FlushMode.COMMIT` could.

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/hibernate-flush-modes-and-batch-writes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names one of the two directions correctly without the other.
- **Senior:** Explains both directions with the specific mechanism behind each.
- **Staff:** Connects this to a broader principle — any ORM convenience feature operating on an implicit, non-obvious trigger condition is a common source of subtle bugs precisely because the trigger condition is easy to forget once a team is used to the convenience.

---

## Hibernate Second-Level and Query Cache (T-603)

### Q1 — Does running a native SQL update through Hibernate bypass its second-level cache consistency?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/hibernate-second-level-and-query-cache.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Guesses that native SQL "probably" bypasses the cache without stating Hibernate's actual, real self-protective behavior.
- **Senior:** States the real distinction (Hibernate's own API vs. external writes) precisely — Hibernate conservatively invalidates the affected L2 cache region for DML it executes itself, including native queries run through its own session.
- **Staff:** Connects this directly to multi-writer, multi-service architectures as the real-world shape where this actually becomes a problem, and proposes explicit cross-process cache eviction as the fix.

### Q2 — Why might enabling the query cache provide little real benefit on its own?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/hibernate-second-level-and-query-cache.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes knowing the query cache exists.
- **Mid:** States that the query cache and entity cache are "related" without explaining the ID-only storage model.
- **Senior:** Explains the ID-only storage model precisely and states that both caches should be enabled together for full benefit.
- **Staff:** Discusses how to verify this in practice (via Hibernate's `Statistics` API) rather than assuming caching configuration is working as intended.

---

## JPA Entity Lifecycle and the N+1 Problem (T-601/T-602)

### Q1 — Your endpoint fires 400 queries for a page of 20 records. Fix it without breaking lazy loading elsewhere.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes this as "too many queries" without naming N+1.
- **Mid:** Identifies the pattern as N+1, even without a precise fix.
- **Senior:** Correctly proposes `JOIN FETCH` (or an equivalent targeted mechanism) scoped to the specific query.
- **Staff:** Explains explicitly why `EAGER` is the wrong fix (unconditional cost everywhere) and names an alternative (DTO projection, `@BatchSize`) for a case where a targeted `JOIN FETCH` doesn't fit.

### Q2 — Why does a `LazyInitializationException` happen, and what's the actual fix?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#interview-questions)

**What's expected:**
- **Junior:** Knows "you need the session open," without the precise mechanism.
- **Mid:** States that the session is closed, even without a specific fix.
- **Senior:** Correctly explains the detached-entity mechanism and proposes at least one real fix (`JOIN FETCH` within the original transaction, or DTO mapping before the session closes).
- **Staff:** Explicitly rejects "just widen the transaction to cover the whole request" as usually the wrong fix, in favor of DTO mapping at the actual persistence boundary.

---

## Table Partitioning and Sharding Strategies (T-614)

### Q1 — Chose the wrong shard key. Recovery plan?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/table-partitioning-and-sharding-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that changing the shard key requires moving data, even without naming the specific migration technique.
- **Senior:** Correctly identifies this requires a real migration, not a setting change.
- **Staff:** Connects explicitly to zero-downtime migration technique (dual writes, backfill, verify, cutover) and names the operational cost as the reason shard-key selection deserves real design-time investment.

### Q2 — Add a node to your shard cluster — how much data moves?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/table-partitioning-and-sharding-strategies.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes distributed-systems background.
- **Mid:** States that the answer depends on the hashing scheme, even without precise numbers.
- **Senior:** Names consistent hashing as the fix for this specific problem.
- **Staff:** Notes that PostgreSQL's own declarative HASH partitioning has the same naive-remap problem as `hash % N` if partition count changes — a different layer, but the identical mathematical issue.

---

## Replication, Read Replicas, and Replica Lag (T-615)

### Q1 — Can a read replica ever return incorrect data, and how would you handle that?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/replication-read-replicas-and-replica-lag.md#interview-questions)

**What's expected:**
- **Junior:** States that replicas can be "a bit behind," without the read-your-own-writes framing or a concrete fix.
- **Mid:** Same as Junior, plus recognizes this could cause user-visible bugs.
- **Senior:** Explains the asynchronous-replication mechanism precisely and proposes routing sensitive reads to the primary.
- **Staff:** Generalizes to the broader "eventually-consistent read-scaling layer" pattern (caches, search indexes, CDC-fed stores) and treats read classification as a standing architectural discipline.

### Q2 — What happens to auto-incrementing IDs across a replica promotion?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/replication-read-replicas-and-replica-lag.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that some ID discontinuity is possible, even without the precise sequence-caching mechanism.
- **Senior:** Explains the sequence-caching mechanism and its interaction with promotion, and names the real, downstream implication (gaps are possible, not necessarily a bug to "fix").
- **Staff:** Connects this to the broader principle that failover mechanics have real operational side effects that only surface by actually exercising a real failover, not by reading documentation alone.

---

## Zero-Downtime Schema Migration (T-616)

### Q1 — Rename a column on a live 200M-row table.

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/zero-downtime-schema-migration.md#interview-questions)

**What's expected:**
- **Junior:** Proposes a direct `RENAME COLUMN`, unaware of the rolling-deploy hazard (the common mistake this question targets).
- **Mid:** States that a direct rename is risky and some staged approach is needed, even without naming all three phases precisely.
- **Senior:** Correctly proposes expand-contract with the three phases named.
- **Staff:** Names the dual-write phase's own atomicity hazard explicitly and connects it to outbox-pattern-style thinking, not a naive assumption that "just write to both columns" is itself safe under a crash.

### Q2 — How do you add an index to a 500M-row table in production without downtime?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/zero-downtime-schema-migration.md#interview-questions)

**What's expected:**
- **Junior:** Proposes a plain `CREATE INDEX`, unaware of the blocking behavior.
- **Mid:** Names `CONCURRENTLY`, even without the failure-mode detail.
- **Senior:** Names `CONCURRENTLY` and its non-blocking property.
- **Staff:** Knows the failure mode (an `INVALID` index left behind, requiring explicit cleanup) and that `CONCURRENTLY` cannot run inside an explicit transaction block, which affects how migration tooling must invoke it.

---

## JSONB and Advanced PostgreSQL Index Types (T-2402)

### Q1 — Does adding a GIN index always speed up a JSONB containment query?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/jsonb-and-advanced-index-types.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States "GIN is for JSONB, so yes" — the common, incorrect intuition this question is designed to catch.
- **Senior:** Correctly states that GIN's benefit is conditional, even without precise numbers.
- **Staff:** Cites or reconstructs a concrete selectivity-dependent example (a real measured regression at ~4% selectivity vs. a ~3,780× win at much higher selectivity) and proposes a measurement-based decision process rather than a rule of thumb.

### Q2 — How would you prevent two overlapping bookings for the same resource at the database level, without relying on application code?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/jsonb-and-advanced-index-types.md#interview-questions)

**What's expected:**
- **Junior:** Proposes an application-level check (query for overlaps, then insert if none found), unaware of the race condition — the common mistake this question targets.
- **Mid:** Same as Junior, but recognizes a race is possible without naming the fix.
- **Senior:** Correctly names the `EXCLUDE USING GIST` mechanism and explains why it's race-condition-safe where application-level checking isn't.
- **Staff:** Proactively names the `btree_gist` extension requirement and explains why a scalar equality column needs it to participate in a GiST index.

---

## Window Functions and Common Table Expressions (T-2401)

### Q1 — How would you find the top N rows per group, and why does the specific approach matter?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/window-functions-and-ctes.md#interview-questions)

**What's expected:**
- **Junior:** Writes a syntactically correct window-function query, even without being able to quantify the performance difference against alternatives.
- **Mid:** Same, plus recognizes a correlated subquery is a slower alternative in general terms.
- **Senior:** Correctly distinguishes `ROW_NUMBER()` from `RANK()` for the tie-handling decision, and can explain why the correlated subquery alternative is slower (repeated re-scanning).
- **Staff:** Cites or reasons through a concrete, quantified performance gap (this chapter measured ~1,500×+ at 200,000 rows) and connects it to a real production risk — correct-and-fast at QA scale, broken at production scale.

### Q2 — How would you list every employee under a given manager, at any depth, without knowing the org chart's depth in advance?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/window-functions-and-ctes.md#interview-questions)

**What's expected:**
- **Junior:** Proposes a fixed number of self-joins ("just join the table to itself 3 or 4 times") — the common mistake this question targets, correct only up to a hardcoded depth.
- **Mid:** Recognizes the fixed-join approach has a limit, without knowing the correct alternative.
- **Senior:** Correctly writes a recursive CTE and explains why a fixed-depth self-join is the wrong tool.
- **Staff:** Proactively raises cycle-safety for real, potentially-imperfect production data (an employee somehow listed as their own indirect manager), not just the happy-path traversal.

---

## Connection Pooling and Sizing (HikariCP) (T-607)

### Q1 — Your service is timing out on the database with pool exhaustion errors. Do you increase the pool size?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/connection-pooling-and-sizing.md#interview-questions)

**What's expected:**
- **Junior:** Says "yes, increase it" as a default — the common mistake this question targets.
- **Mid:** Hesitates before saying "just increase it," even without a full diagnostic plan.
- **Senior:** States the database-utilization check explicitly as the deciding factor.
- **Staff:** Cites a concrete mechanism for why oversizing can actively hurt (contention for finite CPU capacity), ideally with a rough number or a real measured example.

### Q2 — How would you detect a connection leak in production?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/connection-pooling-and-sizing.md#interview-questions)

**What's expected:**
- **Junior:** Names monitoring active-connection count as a general signal without the specific pool feature.
- **Mid:** Same as Junior.
- **Senior:** Names `leakDetectionThreshold` specifically and describes what its output looks like (a stack trace at the acquisition point).
- **Staff:** Connects a slow leak to its eventual failure mode (full pool exhaustion) and to the real discovery that HikariCP silently disables detection below its enforced 2000ms minimum.

### Q3 — Does PgBouncer replace the need for HikariCP, or the other way around?

**Canonical treatment:** [§ Core Concepts, PgBouncer subsection](../../06-databases/connection-pooling-and-sizing.md#pgbouncer-pooling-at-the-database-tier-not-just-the-jvm)

**What's expected:**
- **Junior:** Not typically asked — presupposes knowing both tools exist.
- **Mid:** Recognizes they're different tools without a precise "why."
- **Senior:** States that HikariCP pools within one JVM and PgBouncer pools at the database tier across many app instances, and that production topologies typically run both.
- **Staff:** Names the real PgBouncer `pool_mode = transaction` session-state-leak risk (advisory locks, `SET` variables silently surviving across logically unrelated requests reusing the same backend) as the reason `transaction` mode isn't a drop-in, zero-caveat upgrade over `session` mode.

---

## Database Normalization — 1NF Through BCNF (T-2411)

### Q1 — What's the difference between 2NF and 3NF, concretely?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/database-normalization-1nf-through-bcnf.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Attempts to state the two rules but often treats them as unrelated definitions to memorize separately, without recognizing the shared underlying pattern — the common mistake this question targets.
- **Senior:** Names the shared pattern unprompted — 2NF removes a *partial* dependency (a column depending on only part of a composite key), 3NF removes a *transitive* dependency (a column depending on a non-key column) — and states both produce the identical anomaly shape, a fact duplicated across rows able to disagree with itself.
- **Staff:** Connects this to schema-review practice — recognizing the pattern quickly when reviewing a new table design, before it ships.

### Q2 — Give a table that satisfies 3NF but not BCNF.

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/database-normalization-1nf-through-bcnf.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Cannot produce a concrete counterexample at all, or confuses BCNF with a stricter form of 1NF or 2NF rather than a narrower closure of a specific 3NF gap.
- **Senior:** Produces `enrollments(student_id, course_id, instructor)`, correctly identifying `instructor → course_id` as a real functional dependency from a non-candidate-key column, and can state precisely which part of 3NF's own definition the counterexample exploits — 3NF only constrains dependencies *on* non-key columns, not dependencies *from* one.
- **Staff:** Discusses when this gap actually matters in practice versus when 3NF is a perfectly sufficient stopping point.

### Q3 — When would you deliberately denormalize a schema, and what would you measure first?

**Canonical treatment:** [§ Interview Questions, Q3](../../06-databases/database-normalization-1nf-through-bcnf.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats denormalization as a default performance lever rather than a measured, justified exception, and doesn't know to check the write-side cost before committing.
- **Senior:** Denormalizes only after confirming via `EXPLAIN ANALYZE` (not assumption) that a specific read path is genuinely slow and the read:write ratio favors it, measures the real write-amplification cost first, and names both sides of the trade with real reasoning rather than just "it's faster" — preferring a materialized view over denormalizing the source schema when possible.
- **Staff:** Frames it as an organizational-risk question when applied repeatedly across a system, not just a single-table decision.

### Q4 — Explain the anomaly-first way to think about all four normal forms in under a minute.

**Canonical treatment:** [§ Interview Questions, Q4](../../06-databases/database-normalization-1nf-through-bcnf.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Falls back to reciting the formal definitions instead of the anomaly framing when asked to explain quickly.
- **Senior:** Delivers the anomaly-first framing fluently, unprompted — every normal form exists to make one specific way a schema can represent two contradictory facts about the same real-world thing structurally impossible (1NF: an unqueryable repeating cell; 2NF/3NF: a fact duplicated via a partial or transitive dependency; BCNF: a non-key column's own real-world rule going unenforced).
- **Staff:** Extends it to why this framing matters for schema *review*, not just schema *design* — reviewers who think in anomalies catch real bugs faster than reviewers checking definitions off a list.

---

## Views and Materialized Views (T-2410)

### Q1 — What's the actual difference between a view and a materialized view?

**Canonical treatment:** [§ Interview Questions, Q1](../../06-databases/views-and-materialized-views.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Calls a plain view "cached" (it isn't — nothing is stored) or assumes a materialized view "updates automatically" without an explicit or scheduled `REFRESH` — the common mistakes this question targets.
- **Senior:** States it precisely — a view is a stored query, re-executed on every read, with no storage of its own; a materialized view is a stored result set, fast to read but stale between refreshes — and cites the real trade-off (staleness for speed) with a concrete scenario where it's worth it.
- **Staff:** Frames it as an operational/ownership question — who monitors the refresh, what's the documented staleness contract — not just a technical mechanism.

### Q2 — Why can you `UPDATE` through some views but not others?

**Canonical treatment:** [§ Interview Questions, Q2](../../06-databases/views-and-materialized-views.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as an arbitrary PostgreSQL limitation rather than a real logical impossibility for aggregated/joined views, and doesn't know `INSTEAD OF` triggers exist as the escape hatch.
- **Senior:** States the exact rule — PostgreSQL can only unambiguously map a write back to one base-table row when the view involves exactly one base table and has no aggregation, `DISTINCT`, `UNION`, or `LIMIT`/`OFFSET` — rather than "it depends on the view."
- **Staff:** Discusses when reaching for an `INSTEAD OF` trigger is the right call versus a sign the schema itself should change.

### Q3 — A materialized view refresh job silently stopped running three weeks ago. What's the actual failure here, and how would you have caught it sooner?

**Canonical treatment:** [§ Interview Questions, Q3](../../06-databases/views-and-materialized-views.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes PostgreSQL surfaces staleness somehow on its own, or proposes "just refresh more often" as a fix for what's actually a monitoring gap.
- **Senior:** Correctly identifies this as a monitoring gap, not a database bug — a materialized view produces no error when stale, so reads keep succeeding against old data indefinitely — and proposes an explicit monitored signal (a job-success/failure metric, or a queryable `last_refreshed_at` marker).
- **Staff:** Generalizes to the org-wide risk of many independently-scheduled materialized views with no shared monitoring convention.

### Q4 — What's the difference between what a primary key, a foreign key, an index, and a view each guarantee?

**Canonical treatment:** [§ Interview Questions, Q4](../../06-databases/views-and-materialized-views.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates "index" with "primary key" (a primary key creates an index automatically but is a distinct constraint), or believes a view enforces something on its own.
- **Senior:** States all four distinctly without conflating any pair — a primary key guarantees row uniqueness/identity, a foreign key guarantees a reference points at something real, an index makes lookups fast without changing what's logically true, and a view provides a reusable, possibly access-controlled or precomputed way to read the result of combining the other three.
- **Staff:** Connects the distinction to schema-design judgment — e.g., recognizing that a well-keyed, well-indexed schema can still need a view layer for safe, controlled external access.

---

## Quick-fire questions (from this domain's Flashcards)

Lighter-weight, single-answer questions mined directly from each chapter's own
`## Flashcards` section — genuinely real, not invented, but not given the full
4-tier treatment above since flashcards are single-depth by design. Each links to
its canonical chapter for the complete context.

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Does UPDATE modify a row in place? | [MVCC in PostgreSQL, Vacuum, and Bloat](../../06-databases/mvcc-vacuum-and-bloat.md#flashcards) |
| 2 | Why doesn't VACUUM shrink the table? | [MVCC in PostgreSQL, Vacuum, and Bloat](../../06-databases/mvcc-vacuum-and-bloat.md#flashcards) |
| 3 | How can a transaction that never queries table X still cause table X to bloat? | [MVCC in PostgreSQL, Vacuum, and Bloat](../../06-databases/mvcc-vacuum-and-bloat.md#flashcards) |
| 4 | What's the difference between a lost update and write skew? | [Isolation Levels](../../06-databases/isolation-levels-and-concurrency-anomalies.md#flashcards) |
| 5 | Does REPEATABLE READ prevent write skew? | [Isolation Levels](../../06-databases/isolation-levels-and-concurrency-anomalies.md#flashcards) |
| 6 | What must application code do to safely use SERIALIZABLE? | [Isolation Levels](../../06-databases/isolation-levels-and-concurrency-anomalies.md#flashcards) |
| 7 | What's the READ-COMMITTED-compatible fix for a read-then-write race, without escalating isolation level? | [Isolation Levels](../../06-databases/isolation-levels-and-concurrency-anomalies.md#flashcards) |
| 8 | Describe the B+Tree lookup path in one sentence. | [Index Structures](../../06-databases/index-structures-btree-composite-covering.md#flashcards) |
| 9 | What queries does a composite index on `(customer_id, created_at)` actually serve? | [Index Structures](../../06-databases/index-structures-btree-composite-covering.md#flashcards) |
| 10 | What does `EXPLAIN` show when an index-only scan actually happens, versus merely being possible? | [Index Structures](../../06-databases/index-structures-btree-composite-covering.md#flashcards) |
| 11 | Name two distinct reasons adding an index can make a query slower. | [Index Structures](../../06-databases/index-structures-btree-composite-covering.md#flashcards) |
| 12 | Does PostgreSQL have a clustered index the way InnoDB does? | [Index Structures](../../06-databases/index-structures-btree-composite-covering.md#flashcards) |
| 13 | Most useful `EXPLAIN` flag combination, and why? | [Query Planning](../../06-databases/query-planning-and-explain-analyze.md#flashcards) |
| 14 | What does a large estimate-vs-actual row-count mismatch tell you? | [Query Planning](../../06-databases/query-planning-and-explain-analyze.md#flashcards) |
| 15 | What SQL pattern silently defeats a plain index regardless of selectivity? | [Query Planning](../../06-databases/query-planning-and-explain-analyze.md#flashcards) |
| 16 | Can a nested loop beat a hash join? When? | [Query Planning](../../06-databases/query-planning-and-explain-analyze.md#flashcards) |
| 17 | Async replication's real risk — name it precisely. | [Replication](../../06-databases/replication-read-replicas-and-replica-lag.md#flashcards) |
| 18 | Why are there "two different lag numbers" in this chapter, and which one usually matters? | [Replication](../../06-databases/replication-read-replicas-and-replica-lag.md#flashcards) |
| 19 | What's the real gotcha with sequence-backed IDs after a promotion? | [Replication](../../06-databases/replication-read-replicas-and-replica-lag.md#flashcards) |
| 20 | What does partition pruning actually require to work? | [Table Partitioning and Sharding Strategies](../../06-databases/table-partitioning-and-sharding-strategies.md#flashcards) |
| 21 | Why is shard-key selection called "a one-way door"? | [Table Partitioning and Sharding Strategies](../../06-databases/table-partitioning-and-sharding-strategies.md#flashcards) |
| 22 | What's the hidden cost of PostgreSQL's native HASH partitioning? | [Table Partitioning and Sharding Strategies](../../06-databases/table-partitioning-and-sharding-strategies.md#flashcards) |
| 23 | What does a plain `CREATE INDEX` actually block, and for how long? | [Zero-Downtime Schema Migration](../../06-databases/zero-downtime-schema-migration.md#flashcards) |
| 24 | What does `CONCURRENTLY` trade away to avoid blocking writes? | [Zero-Downtime Schema Migration](../../06-databases/zero-downtime-schema-migration.md#flashcards) |
| 25 | Why is a direct column rename unsafe during a rolling deploy? | [Zero-Downtime Schema Migration](../../06-databases/zero-downtime-schema-migration.md#flashcards) |
| 26 | GIN's benefit is conditional on what, specifically? | [JSONB and Advanced PostgreSQL Index Types](../../06-databases/jsonb-and-advanced-index-types.md#flashcards) |
| 27 | What does a GiST `EXCLUDE` constraint actually guarantee? | [JSONB and Advanced PostgreSQL Index Types](../../06-databases/jsonb-and-advanced-index-types.md#flashcards) |
| 28 | Why might the planner decline to use a BRIN index even when it's the table's only index? | [JSONB and Advanced PostgreSQL Index Types](../../06-databases/jsonb-and-advanced-index-types.md#flashcards) |
| 29 | `ROW_NUMBER()` vs `RANK()` on ties — what actually changes in the result set? | [Window Functions and CTEs](../../06-databases/window-functions-and-ctes.md#flashcards) |
| 30 | How much faster is a window function than a logically equivalent correlated subquery, roughly? | [Window Functions and CTEs](../../06-databases/window-functions-and-ctes.md#flashcards) |
| 31 | What's the actual recursion mechanism behind `WITH RECURSIVE`? | [Window Functions and CTEs](../../06-databases/window-functions-and-ctes.md#flashcards) |
| 32 | Does a bigger connection pool always help throughput? | [Connection Pooling](../../06-databases/connection-pooling-and-sizing.md#flashcards) |
| 33 | What's HikariCP's real, enforced minimum for `leakDetectionThreshold`? | [Connection Pooling](../../06-databases/connection-pooling-and-sizing.md#flashcards) |
| 34 | What real exception does pool exhaustion throw, and what does it embed? | [Connection Pooling](../../06-databases/connection-pooling-and-sizing.md#flashcards) |
| 35 | Under PgBouncer `pool_mode = transaction`, what happens to an advisory lock a session never released? | [Connection Pooling](../../06-databases/connection-pooling-and-sizing.md#flashcards) |
| 36 | What can't a plain join table store, structurally? | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md#flashcards) |
| 37 | What's the real trigger for needing an explicit join entity? | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md#flashcards) |
| 38 | What's the canonical example of a silent bug from skipping an explicit join entity? | [Data Modelling and Explicit Join Tables](../../06-databases/data-modelling-and-explicit-join-tables.md#flashcards) |
| 39 | Does Hibernate protect its own second-level cache from its own native SQL updates? | [Hibernate Second-Level and Query Cache](../../06-databases/hibernate-second-level-and-query-cache.md#flashcards) |
| 40 | What kind of write actually produces a stale second-level-cache read? | [Hibernate Second-Level and Query Cache](../../06-databases/hibernate-second-level-and-query-cache.md#flashcards) |
| 41 | Why doesn't the query cache alone eliminate database hits? | [Hibernate Second-Level and Query Cache](../../06-databases/hibernate-second-level-and-query-cache.md#flashcards) |
| 42 | What does the JPA persistence context actually guarantee? | [JPA Entity Lifecycle and N+1](../../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#flashcards) |
| 43 | Why does `LazyInitializationException` happen, mechanically? | [JPA Entity Lifecycle and N+1](../../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#flashcards) |
| 44 | Why doesn't switching an association to `EAGER` actually fix N+1? | [JPA Entity Lifecycle and N+1](../../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#flashcards) |
| 45 | "Detects" vs. "prevents" — which word describes optimistic locking correctly? | [Optimistic vs. Pessimistic Locking](../../06-databases/optimistic-vs-pessimistic-locking.md#flashcards) |
| 46 | What's the real, ongoing cost of pessimistic locking under contention? | [Optimistic vs. Pessimistic Locking](../../06-databases/optimistic-vs-pessimistic-locking.md#flashcards) |
| 47 | Why can't a `@Version`-annotated entity skip locking entirely? | [Optimistic vs. Pessimistic Locking](../../06-databases/optimistic-vs-pessimistic-locking.md#flashcards) |
| 48 | What single question does every normal form (1NF–BCNF) answer, underneath its formal definition? | [Database Normalization](../../06-databases/database-normalization-1nf-through-bcnf.md#flashcards) |
| 49 | What's the real structural difference between a 2NF violation and a 3NF violation? | [Database Normalization](../../06-databases/database-normalization-1nf-through-bcnf.md#flashcards) |
| 50 | Give a real example of a table that satisfies 3NF but still has an anomaly BCNF would prevent. | [Database Normalization](../../06-databases/database-normalization-1nf-through-bcnf.md#flashcards) |
| 51 | This chapter measured a specific denormalization trade-off — what was the real read win, and what was the real write cost? | [Database Normalization](../../06-databases/database-normalization-1nf-through-bcnf.md#flashcards) |
| 52 | In one sentence each, what's the real difference between a view and a materialized view? | [Views and Materialized Views](../../06-databases/views-and-materialized-views.md#flashcards) |
| 53 | What exact conditions make a view automatically updatable in PostgreSQL? | [Views and Materialized Views](../../06-databases/views-and-materialized-views.md#flashcards) |
| 54 | This chapter's lab measured a specific live-query-versus-materialized-view speedup — what was it, and what did it cost? | [Views and Materialized Views](../../06-databases/views-and-materialized-views.md#flashcards) |
| 55 | Why does `REFRESH MATERIALIZED VIEW CONCURRENTLY` require a unique index on the materialized view? | [Views and Materialized Views](../../06-databases/views-and-materialized-views.md#flashcards) |
| 56 | State, in one line each, what a primary key, a foreign key, an index, and a view each guarantee. | [Views and Materialized Views](../../06-databases/views-and-materialized-views.md#flashcards) |

---

## Related

- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md) — the full 22-domain plan this pilot belongs to.
- [`syllabus/06-databases/INDEX.md`](../../06-databases/INDEX.md) — the canonical domain index every question above links back into.
