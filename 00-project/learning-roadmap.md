# 00 · Learning Roadmap
### Java-Interview-Handbook · Phase 3
**Built on:** `knowledge-base-audit.md` · `knowledge-architecture-blueprint.md` · `blueprint-v1.1-corrections.md`
**Optimized for:** an engineer **actively interviewing now** for Senior Java Backend roles
**Date:** 28 July 2026

---

## 0. How This Roadmap Is Built

### 0.1 Scheduling authority

Four inputs decide what goes where, in strict precedence order:

| Rank | Input | Source | Overrides |
|---|---|---|---|
| **1** | **Real interview feedback** | Your actual interview | Everything below |
| 2 | Mandatory Core (Weighted IWI) | Blueprint v1.1 §2.4 | Efficiency ordering |
| 3 | Prerequisite chains | Blueprint §6.3 | Efficiency ordering |
| 4 | Readiness RoS | Blueprint v1.1 §1.5 | — |

Feedback outranks the model because it is **[E] evidence about you**, while every frequency score in the register is **[H] heuristic about the market**. A named weakness from a real interviewer is worth more than any estimate this project can produce.

### 0.2 The three tracks — non-negotiable

Every week in every plan contains all three. Sequential execution (read for a month, then practice) is the dominant preparation failure and this roadmap structurally prevents it.

| Track | Content | Why it can't wait |
|---|---|---|
| **A · Technical Depth** | Java, Spring, DB, Kafka, architecture, design, performance, security | Depth requires focused blocks |
| **B · Coding Practice** | Java-only problems, patterns, complexity, verbal narration | Skill acquisition needs *distributed* repetition; cramming does not transfer |
| **C · Interview Performance** | Stories, answer drills, design narration, mocks, self-scoring | Story portfolios need **elapsed time** — retrieval, artifact hunting, rewriting |

### 0.3 Which plan

| | **Plan A** | **Plan B** | **Plan C** |
|---|---|---|---|
| **Duration** | 6 weeks | 12 weeks | 9–14 months |
| **Use when** | Interviews booked or expected within ~8 weeks | Actively looking, no urgent deadline | Deliberate Senior→Staff progression |
| **Scope** | 48 topics, partial depth | 104 topics, working depth | 111 topics, full depth |
| **Coding target** | 60–75 problems | 150–170 problems | 220–250 problems |
| **Design problems** | 6 | 12 | 12 + variants |
| **Stories** | 8 | 12–14 | 14 + Staff-scope rewrites |
| **Mocks** | 6 | 14 | 30+ |
| **Outcome** | Interview-survivable in known weak areas | Balanced Senior readiness | Staff-credible |

**If interviews are already scheduled: Plan A. Do not start Plan B or C.** Plan A rolls directly into Plan B if the search extends — weeks 1–6 are identical.

### 0.4 Notation

`[E]` evidence · `[H]` heuristic estimate · `[C]` company-specific · `[R]` role-specific
`T-xxx` topic ID · `S` study hours · `P` practice hours

---

## 1. Day 0 — Initial Diagnostic

**Time: 3 hours. Do not skip.** Without a baseline, week-6 progress is unmeasurable.

| # | Exercise | Time | What it measures |
|---|---|---|---|
| D1 | **Record yourself** answering 6 questions cold, 3 min each. No notes.<br>① How does HashMap work internally?<br>② What does `@Transactional` do?<br>③ When does an index not help?<br>④ Difference between hexagonal and layered architecture?<br>⑤ How do you model many-to-many?<br>⑥ Why did you choose that design? | 30m | Technical depth + **communication** |
| D2 | Solve **LC 146 (LRU Cache)** in Java, narrating aloud, 35-min limit | 40m | Java fluency, design coding, narration |
| D3 | 30-minute design: **URL shortener**, on paper, timed | 35m | Design method presence |
| D4 | Write one STAR story cold, 400 words: *a technical decision you made and why* | 25m | Story structure, quantification |
| D5 | Score all four against the §8 rubrics. Be harsh. | 20m | Calibration |
| D6 | List every interview booked or expected in the next 8 weeks, with company `[C]` | 10m | Plan selection |

**Retain all artifacts.** Week 6 repeats D1–D4 verbatim; the delta is the primary outcome metric.

**Expected baseline given the Phase 1 audit `[H]`:** D1 answers around 45–90 seconds with no follow-up depth; D2 likely reproduces the buggy LRU `put()`; D3 likely jumps to components without capacity estimation; D4 likely lacks quantified impact.

---

## 2. The Feedback Override Block

Weeks 1–2 are **pre-committed** to weaknesses named in your real interview. These are scheduled first regardless of where the scoring model places them.

| Feedback item | Topic ID | IWI | Model rank | Scheduled |
|---|---|---|---|---|
| Clean Architecture, Hexagonal, Ports & Adapters | **T-901** | 7.25 | 72 | **W1** |
| Repository interfaces, persistence-agnostic domain | **T-901 + T-903** | 7.25 | 118 | **W1–W2** |
| Technology replacement boundaries | **T-901 + T-912** | 7.35 | — | **W2** |
| Clustered vs non-clustered indexes | **T-609** | 8.30 | 111 | **W1** |
| Composite & covering indexes | **T-609 + T-610** | 8.30 | 111 | **W1–W2** |
| Explicit join tables, many-to-many | **T-605 + T-608** | 5.20 | 169 | **W2** |
| Database trade-offs | **T-617 + T-811** | 6.90 | 33 | **W2** |
| Explaining concepts with greater depth | **T-1601** | 7.30 | 71 | **W1–W6** |
| Communicating *why* a decision was selected | **T-1505** | 8.10 | 36 | **W1–W6** |
| Explaining alternatives and trade-offs | **T-1505 + T-916** | 8.10 | 31 | **W2–W6** |

**Block total: 95 hours at full depth.** Weeks 1–2 allocate ~40h at 20h/wk, so these topics are taken to **interview-usable depth** now and revisited in W5–W6 and in Plan B.

### 2.1 What the feedback actually signals

Three of the ten items are pure communication (`depth`, `why`, `alternatives`). That is not a coincidence, and it changes the diagnosis.

An interviewer who says *"explain with greater depth"* and *"explain why you chose it"* is usually **not** reporting missing knowledge. They are reporting that knowledge was present but arrived as a definition and stopped. This matches the Phase 1 finding precisely: a knowledge base of ~110-character answers trains exactly that failure — the first sentence is available and nothing follows it.

**Consequence for this roadmap:** the fix is not only "learn hexagonal architecture." It is "learn hexagonal architecture *and* build the four-layer answer for it" — 30-second, 2-minute, 10-minute, whiteboard. That is why the Interview Answer Practice System (§7) starts in Week 1 rather than being appended at the end.

### 2.2 A correction worth having ready

The feedback mentions **clustered vs non-clustered indexes**. This phrasing comes from SQL Server / MySQL-InnoDB. **PostgreSQL has no clustered indexes** in that sense — all tables are heaps, all indexes are secondary, and `CLUSTER` is a one-off physical reordering that is not maintained. In InnoDB the primary key *is* the clustered index and every secondary index stores the PK as its row pointer, which is why wide PKs are expensive there and not in Postgres.

If the interviewer used the term generically, knowing this distinction — and naming which engine you are describing — is itself a depth signal. Build it into the T-609 answer.

---

## 3. Plan A — Interview Emergency Sprint (6 weeks)

**Baseline: 20 h/week = 120 hours.** Track A ≈ 10h · Track B ≈ 5h · Track C ≈ 5h.
Variants for 10h and 30h in §6.

### Week 1 — Architecture Boundaries + Index Fundamentals

> **Objective:** Convert the two hardest-named feedback weaknesses into answers you can deliver out loud at three lengths. Establish the daily coding and story habits that run for six weeks.

| | |
|---|---|
| **Topics** | T-901 (Clean/Hexagonal/Ports & Adapters) · T-609 (Index structures) · T-1601 (Communication protocol) · T-1501 (STAR) · T-1419 (Coding narration) |
| **Why this week** | T-901 and T-609 are the two most concrete feedback items and both are prerequisites for W2's deeper material (T-903 needs T-901; T-610 needs T-609). T-1601 is scheduled Week 1 because *every* subsequent week's output depends on being able to explain things at depth — it is the multiplier on all other work. |
| **Prerequisites** | None. This is the entry point. |
| **Study** | 10h — T-901 (4h) · T-609 (4h) · T-1601 (2h) |
| **Practice** | 10h — coding 5h · stories 3h · answer drills 2h |

**Track A — Technical Depth (10h)**

*Required reading*
- Hexagonal Architecture — Alistair Cockburn's original article
- *Clean Architecture* (Martin) — Ch. 22 "The Clean Architecture", Ch. 23 "Presenters and Humble Objects"
- PostgreSQL docs — Ch. 11 "Indexes" (all sections)
- Use The Index, Luke — Ch. 1–3 (B-Tree, concatenated indexes, clustering)

*Must be able to answer aloud*
1. What problem does hexagonal architecture solve that layered architecture does not?
2. What exactly is a port, and what is an adapter? Give one of each from your own system.
3. Where does the repository *interface* live, and why not next to its implementation?
4. Your domain model must not depend on JPA. What does that cost you, concretely?
5. You are replacing PostgreSQL with DynamoDB. Which files change, and which must not?
6. How does a B+Tree index actually find a row? Walk it from root to heap.
7. Index on `(customer_id, created_at)` — which queries does it serve, which does it not, and why?
8. What is a covering index and how do you know from `EXPLAIN` that you got one?
9. When is a sequential scan *faster* than an index scan?
10. Clustered vs non-clustered — and what changes when the engine is PostgreSQL rather than InnoDB?

*Deliverable* — **`domain-purity.md`**: take one real aggregate from your current system and document (a) its current framework coupling, (b) the port/adapter refactor, (c) what breaks, (d) whether you would actually do it. **Point (d) is the Senior signal** — a candidate who applies hexagonal architecture unconditionally has not understood the trade-off.

**Track B — Coding (5h)** — Java only, always narrated aloud

| Day | Problem | Focus |
|---|---|---|
| 1 | LC 1 Two Sum, LC 167 Two Sum II | Warm-up; narration protocol |
| 2 | LC 121 Best Time to Buy/Sell | State the invariant before coding |
| 3 | LC 242 Valid Anagram, LC 49 Group Anagrams | T-1403 hashing |
| 4 | LC 3 Longest Substring | T-1402 sliding window |
| 5 | **LC 146 LRU Cache** | **⛔ ERRATA — see below** |
| 6 | Re-solve LC 146 from scratch, 25 min | Consolidation |

> ⛔ **Errata drill.** The LRU implementation in your Notion DSA guide is **wrong**. In `put()`, when the key already exists it unlinks the node from the list but never removes it from the map — so at capacity, updating an existing key evicts a different, valid entry. Write the buggy version, construct the input sequence that exposes it, then write the correct version. **Unlearning requires seeing the failure, not just reading the fix.**

*Retrospective* — for every problem: pattern name, why that pattern, complexity with justification, one thing that was slow.

**Track C — Interview Performance (5h)**

- **STAR framework (T-1501), 1h.** Structure only.
- **Story inventory, 2h.** Brain-dump 20+ candidate situations from the last 3 years. Don't write them yet — just list them. This is the elapsed-time work that cannot be compressed.
- **Stories to complete this week (2):**
  - **Story 1 — Architecture decision.** A design choice you made, alternatives considered, why you chose it, what it cost. *Directly targets the feedback.*
  - **Story 2 — Technical disagreement.** Include the other person's strongest argument.
- **Design exercise:** *Design a URL shortener.* 45 min, timed, on paper. Don't research first — this measures method, not knowledge.
- **Mock:** self-recorded 20-min technical round on T-901 and T-609. Score with §8.1. Watch it back — this is uncomfortable and it is the point.
- **Flashcards to create (12):** B+Tree lookup path · leftmost-prefix rule · covering index · index selectivity · seq-scan-wins conditions · clustered vs non-clustered by engine · port · adapter · dependency rule · repository interface placement · hexagonal cost · ACL definition

**Exit criteria — all must pass**
- [ ] Explain hexagonal architecture in 30s, 2min, and 10min without notes
- [ ] Answer all 10 questions above aloud, unprompted, with a concrete example each
- [ ] `domain-purity.md` complete including the "would I actually do it" verdict
- [ ] 8+ problems solved in Java with written retrospectives
- [ ] LRU written correctly from scratch, twice, and you can state the bug in the old version
- [ ] 2 STAR stories written with quantified impact
- [ ] Baseline design exercise recorded (quality irrelevant — it's a baseline)

---

### Week 2 — Query Plans, Data Modelling, Trade-off Narration

> **Objective:** Complete the feedback block. Move from "I know what an index is" to "I can read a plan and defend a modelling decision." Begin the trade-off vocabulary that the rest of the plan depends on.

| | |
|---|---|
| **Topics** | T-610 (Query planning/EXPLAIN) · T-605 (Entity mapping, join tables) · T-608 (SQL fundamentals) · T-903 (DDD tactical — aggregates) · T-617/T-811 (Storage selection) · T-1505 (Trade-off narration) · T-916 (ADRs) |
| **Why this week** | T-610 is unusable without W1's T-609 — you cannot interpret a plan without knowing what the planner is choosing between. T-903 completes the persistence-agnostic domain thread from T-901. T-1505 is scheduled now, not later, because every remaining week produces trade-off answers and the structure must exist first. |
| **Prerequisites** | T-609 ✔ · T-901 ✔ (both W1) |
| **Study** | 10h — T-610 (4h) · T-605+T-608 (3h) · T-903 (2h) · T-916 (1h) |
| **Practice** | 10h — coding 5h · stories 2h · design 2h · mock 1h |

**Track A**

*Required reading*
- PostgreSQL docs — Ch. 14.1 "Using EXPLAIN", Ch. 14.2 "Statistics Used by the Planner"
- Use The Index, Luke — Ch. 4 "The Join Operation"
- *Domain-Driven Design Distilled* (Vernon) — Ch. 5 "Tactical Design with Aggregates"

*Must be able to answer aloud*
1. Read `EXPLAIN ANALYZE` line by line. What is `rows=1000` vs `actual rows=48000` telling you?
2. Nested loop vs hash join vs merge join — when does the planner pick each?
3. You added an index and the query got slower. Give two distinct mechanisms.
4. Why did the planner ignore your index? Three reasons.
5. Model many-to-many between `Order` and `Product`. Now the relationship needs `quantity` — what changes, and why was the original `@ManyToMany` a trap?
6. When is an explicit join entity mandatory rather than optional?
7. What is an aggregate boundary, and why is it a *transaction* boundary?
8. Choose between PostgreSQL and DynamoDB for a given workload. Defend it, then argue the opposite.

*Deliverable* — **`query-plan-analysis.md`**: take three real slow queries (or construct them). For each: the plan before, the diagnosis, the fix, the plan after, and measured improvement. **Actual `EXPLAIN` output required, not description.**

*Deliverable* — **`ADR-001.md`**: a real Architecture Decision Record for a decision you have made. Context / Options considered / Decision / Consequences. This is the written form of the exact skill the feedback flagged.

**Track B — Coding (5h)**

| Day | Problem | Focus |
|---|---|---|
| 1 | LC 704, LC 35 | T-1404 binary search — exact boundary conditions |
| 2 | LC 33 Search in Rotated | Binary search variant |
| 3 | LC 875 Koko Eating Bananas | **Search on answer space** — the pattern most candidates miss |
| 4 | LC 20 Valid Parentheses, LC 155 Min Stack | T-1406 stacks |
| 5 | LC 739 Daily Temperatures | ⛔ Monotonic stack — your guide's version has a diagram/code mismatch (indices vs values). Write it index-based. |
| 6 | LC 208 Implement Trie | Design coding |

**Track C**

- **Trade-off narration (T-1505), 2h.** Drill the four-beat structure: *Context → Options → Decision criterion → What it cost.* The fourth beat is the one candidates skip and the one interviewers score.
- **Stories to complete (2):**
  - **Story 3 — Production incident.** Detection, mitigation, diagnosis, prevention. Include the timeline.
  - **Story 4 — Technical debt / refactor you argued for.** Include the business framing.
- **Design exercise:** *Design a rate limiter.* 45 min. Compare token bucket vs sliding window explicitly; state the distributed-enforcement problem.
- **Mock:** 30-min database round with a partner or self-recorded. Must include one `EXPLAIN` walkthrough aloud.
- **Flashcards (14):** estimate-vs-actual divergence · nested loop / hash / merge join criteria · three reasons a plan skips an index · `ANALYZE` and statistics · `@ManyToMany` failure mode · join entity trigger · aggregate = transaction boundary · aggregate sizing rule · ADR sections · storage selection criteria (×2) · four-beat trade-off structure · write amplification · index-only scan requirement

**Exit criteria**
- [ ] Read an unfamiliar `EXPLAIN ANALYZE` aloud and identify the bottleneck node
- [ ] `query-plan-analysis.md` with three real before/after plans
- [ ] `ADR-001.md` complete
- [ ] Model many-to-many both ways and state the trigger for the explicit entity
- [ ] 4 STAR stories total
- [ ] 8+ problems this week (16+ cumulative)
- [ ] Deliver any technical answer using the four-beat trade-off structure without prompting

---

### Week 3 — Transactions, Isolation, Design Method · **CHECKPOINT**

> **Objective:** Close the highest-IWI Spring gap and the highest-IWI database gap. Acquire a repeatable system design procedure. **Week 3 checkpoint at week end.**

| | |
|---|---|
| **Topics** | T-504 (@Transactional) · T-505 (Propagation) · T-611 (Isolation levels) · T-503 (Spring AOP) · T-801 (Design method) · T-802 (Estimation) |
| **Why this week** | T-504 is the highest-IWI framework topic in the register (8.15) and W2's database work is its prerequisite — propagation is meaningless without isolation. T-503 must precede T-504 because self-invocation is unexplainable without proxy mechanics. T-801 is scheduled now so weeks 4–6 have a method to hang design content on. |
| **Prerequisites** | T-609 ✔ T-610 ✔ (W1–W2) |
| **Study** | 10h — T-503 (2h) · T-504 (3h) · T-505+T-611 (3h) · T-801+T-802 (2h) |
| **Practice** | 10h — coding 5h · design 3h · mock 2h |

**Track A**

*Required reading*
- Spring Framework docs — "Transaction Management" (declarative section in full)
- PostgreSQL docs — Ch. 13 "Concurrency Control" (13.2 isolation levels, 13.3 explicit locking)
- Martin Kleppmann, *DDIA* — Ch. 7 "Transactions", pp. 233–251 (weak isolation)

*Must be able to answer aloud*
1. Method A calls `@Transactional` method B in the same class. What happens, and why? Three fixes.
2. Your method threw a checked exception. Did it roll back? Why is that the default?
3. `REQUIRES_NEW` — give a real use case and name the deadlock risk.
4. Where does the transaction boundary belong, and defend it.
5. There's an HTTP call inside a transaction. What breaks, and at what load?
6. Two transactions read a balance and both write. Walk it at READ COMMITTED, REPEATABLE READ, SERIALIZABLE.
7. **Explain write skew with a concrete example.** *(the discriminating question)*
8. Estimate QPS and storage for a system with 10M DAU. Show every assumption.

*Deliverable* — **`transaction-traps.md`**: five runnable Spring examples demonstrating self-invocation failure, checked-exception non-rollback, `REQUIRES_NEW` isolation, read-only propagation, and connection-pool exhaustion from a long transaction. **Runnable, with output.**

**Track B (5h)** — T-1408 trees: LC 104, 226, 98, 235, 102, 199. Narrate the recursion invariant before writing.

**Track C**

- **Design method (T-801) drill, 3h.** Run the six-phase procedure three times against three problems, 25 min each, focusing only on phase discipline — clarify → estimate → API → data → architecture → bottlenecks. Correctness is not the goal this week; *procedure* is.
- **Stories (2):** **Story 5 — Mentoring.** **Story 6 — A failure you owned.**
- **Design exercise:** *Design a ride-hailing dispatch system.* 45 min, full method.
- **Mock:** ⚑ **Week 3 Checkpoint** — 60-min combined round (30 technical / 30 design) with a partner if possible. Score against §8.

**⚑ Week 3 Checkpoint — pass criteria**

| Dimension | Pass |
|---|---|
| Technical depth | Sustain a 4-follow-up chain on T-504 or T-609 without deflecting |
| Coding | 24+ problems cumulative; Medium solved in ≤ 30 min with narration |
| System design | All six phases executed unprompted; estimation drives ≥ 1 decision |
| Behavioral | 6 stories written; 2 delivered in 2 min without notes |
| Java fluency | Write correct LRU in ≤ 20 min from scratch |
| Production judgment | Name a real failure mode for every design component proposed |

**If 4 of 6 fail:** stop adding topics. Spend Week 4 consolidating W1–W3 and repeat the checkpoint. Adding breadth on a weak base is the failure mode this checkpoint exists to catch.

---

### Week 4 — Caching, Failure Modes, API Design

> **Objective:** Acquire the three components that appear in nearly every design round. Shift the behavioral track from writing to *delivering*.

| | |
|---|---|
| **Topics** | T-804 (Caching) · T-909 (Distributed failure modes) · T-803 (API design) · T-1504 (Incident stories) · T-1207 (Incident response) |
| **Why this week** | T-804 and T-909 are ranked 3rd and 4th in the Mandatory Core. Both require T-801 (W3) to be useful — components without method produce unstructured answers. T-1504 is the single highest-IWI behavioral topic and pairs naturally with T-909: the same incident supplies both the technical failure analysis and the story. |
| **Prerequisites** | T-801 ✔ T-802 ✔ (W3) |
| **Study** | 10h — T-804 (4h) · T-909 (4h) · T-803 (2h) |
| **Practice** | 10h — coding 5h · design 3h · mock 2h |

**Track A**

*Required reading*
- *DDIA* Ch. 8 "The Trouble with Distributed Systems"
- AWS Builders' Library — "Timeouts, retries and backoff with jitter"
- Google API Design Guide — resource naming, standard methods, errors

*Must be able to answer aloud*
1. Cache and database disagree. How did it happen, how do you detect it, how do you fix it?
2. Your cache dies at peak. Walk through what happens to the database.
3. One key takes 40% of traffic. Three mitigations.
4. Cache stampede — what is it and give three distinct fixes.
5. You added retries and made the outage worse. Explain the mechanism precisely.
6. How do you distinguish "the request failed" from "the request succeeded slowly," and why does it matter?
7. Two nodes both believe they are leader. How, and what breaks?
8. Design pagination for a 500M-row endpoint. Why not `OFFSET`?

*Deliverable* — **`failure-modes.md`**: for one system you have worked on, enumerate every dependency, its timeout, its retry policy, and what happens when it fails. Most engineers discover during this exercise that several have no timeout at all. That discovery is itself a story.

**Track B (5h)** — T-1409 graphs: LC 200, 133, 207, 210, 547. Union-Find and topological sort both implemented from scratch.

**Track C**

- **Stories (2):** **Story 7 — Cross-team influence.** **Story 8 — Migration you led.**
- **Delivery drill, 2h.** Take all 8 stories to 2-minute spoken versions. Record each. Any story over 2:30 gets cut, not compressed.
- **Design exercise:** *Design a news feed.* Caching and fan-out are mandatory discussion points.
- **Mock:** 45-min full system design round with a partner. **Partner mock strongly preferred** — self-mocks cannot generate unexpected follow-ups.

**Exit criteria**
- [ ] Explain cache invalidation strategies with the failure mode of each
- [ ] Explain retry amplification without notes
- [ ] `failure-modes.md` complete for a real system
- [ ] 8 stories, all delivered in ≤ 2 min recorded
- [ ] 32+ problems cumulative
- [ ] Design round completed with all six phases and ≥ 3 named failure modes

---

### Week 5 — Decomposition, Idempotency, Consistency

> **Objective:** Acquire the Staff-signal architecture judgment topics. These are where "should we" replaces "how would we."

| | |
|---|---|
| **Topics** | T-907 (Decomposition) · T-908 (Monolith trade-off) · T-809 (Idempotency) · T-807 (CAP/consistency) · T-1503 (Scope & influence) |
| **Why this week** | These are the topics where the expected answer is frequently *don't do it* — judgment traps that need W1–W4's foundation to answer credibly. T-809 (R-RoS 2.62) is high-efficiency and connects W4's failure modes to W6's consolidation. T-1503 is scheduled after 8 stories exist, because scope framing is a *rewrite* of existing stories, not new ones. |
| **Prerequisites** | T-901 ✔ T-903 ✔ T-909 ✔ |
| **Study** | 10h — T-907+T-908 (4h) · T-809 (3h) · T-807 (3h) |
| **Practice** | 10h — coding 4h · design 4h · mock 2h |

**Track A**

*Required reading*
- *Building Microservices* 2e (Newman) — Ch. 1–3
- Stripe API docs — idempotent requests
- *DDIA* Ch. 9 "Consistency and Consensus", pp. 321–345

*Must be able to answer aloud*
1. Where exactly do you draw a service boundary, and why *there* rather than one table over?
2. Two services need one transaction. Now what?
3. When would you merge two services back together?
4. You have four engineers. Does microservices still make sense? Defend it.
5. Make a payment endpoint idempotent. Full mechanism — key, storage, TTL, concurrent-duplicate behaviour.
6. What does the client do when it never receives the response?
7. CAP — what does a system actually give up during a partition? Be specific about your own system.
8. What is the difference between eventual and strong consistency *for the user*?

*Deliverable* — **`decomposition-analysis.md`**: take a real monolith you know. Propose boundaries. Then argue **against your own proposal** — operational cost, latency inflation, transaction fragmentation, on-call burden. Conclude with a genuine recommendation. **The counter-argument is the deliverable.**

**Track B (4h)** — T-1416 design coding: LC 155, 380, 706, 622 (Circular Queue — implement `Front`/`Rear`/`isEmpty`/`isFull`, which the Notion version omits).

**Track C**

- **Scope reframing (T-1503), 2h.** Rewrite Stories 1, 4, 7, 8 to foreground *scope and influence* rather than technical detail. Same events, Staff-level framing: who else was affected, who you convinced, what changed beyond your team.
- **Design exercise:** *Design a payment processing system.* Idempotency and exactly-once are mandatory.
- **Mock:** 45-min behavioral round, 6 questions, partner preferred. Score with §8.4.

**Exit criteria**
- [ ] Argue both sides of a decomposition and reach a defended recommendation
- [ ] Explain idempotency end-to-end including the concurrent-duplicate case
- [ ] `decomposition-analysis.md` with a genuine counter-argument
- [ ] 4 stories reframed for scope
- [ ] 40+ problems cumulative
- [ ] Behavioral mock ≥ 3.5/5 average

---

### Week 6 — Consolidation + Full Loop Simulation

> **Objective:** No new topics. Convert knowledge into performance. Measure against the Day 0 baseline.

| | |
|---|---|
| **Topics** | Revision only — all W1–W5 · T-1603 (Mock protocol) · T-1514 (Questions to ask) |
| **Why this week** | Sprint plans fail when they add material until the last day. Retrieval practice under simulated pressure produces more interview-day improvement than any new topic could `[H]`. Week 6 also re-runs the Day 0 diagnostic — without it there is no measurement. |
| **Prerequisites** | All prior weeks |
| **Study** | 6h — revision only |
| **Practice** | 14h — mocks 8h · drills 4h · retrospective 2h |

**Monday — Full retrieval pass (3h).** All flashcards from W1–W5, spaced. Everything failed goes on a short list.
**Tuesday — Weak-list repair (3h).** Only the failed items. Re-read, re-answer aloud, re-record.
**Wednesday — Technical + coding mock (3h).** 60-min technical, 45-min coding, partner preferred.
**Thursday — Design + behavioral mock (3h).** 60-min design on an unseen problem, 45-min behavioral.
**Friday — Diagnostic re-run (3h).** Repeat D1–D4 from Day 0 **verbatim**.
**Saturday — Comparison & retrospective (2h).** Watch Day 0 and Week 6 recordings back-to-back. Write `week-6-assessment.md`.
**Sunday — Rest.** Non-negotiable before an interview week.

**⚑ Week 6 Interview-Readiness Assessment** — see §8.7 for full rubric.

| Dimension | Target |
|---|---|
| Technical depth | 4+ follow-ups sustained on 3 of 4 sampled topics |
| Coding | Medium in ≤ 25 min, narrated, correct first submission ≥ 60% |
| System design | Six phases unprompted; ≥ 3 failure modes; ≥ 2 explicit trade-offs |
| Behavioral | 8 stories at 2 min; every one quantified; every one first-person |
| Java fluency | No syntax hesitation; correct collection choice justified aloud |
| Production judgment | Every design component paired with a real failure mode |
| **Delta vs Day 0** | **Measurable improvement on all six D1–D4 artifacts** |

---

## 4. Plan B — Interview-Ready Program (12 weeks)

**Weeks 1–6: identical to Plan A.** Weeks 7–12 broaden from sprint coverage to balanced Senior readiness.

### Week 7 — Spring Depth + Security
**Topics:** T-506 (Auto-configuration) · T-501 (Bean lifecycle) · T-511 (Security filter chain) · T-512 (OAuth2/OIDC) · T-513 (JWT)
**Why now:** W3's T-503 proxy mechanics is the prerequisite for auto-configuration internals; the security chain is a filter pipeline whose ordering only makes sense after the container lifecycle.
**S 10 / P 10** · Coding: T-1410 backtracking (LC 46, 78, 39, 22) — ⛔ errata: the Notion `permute` uses `temp.contains()` which breaks on duplicate inputs; implement index-based.
**Track C:** Stories 9–10 (design review, technical debt advocacy) · Design: *Design an authentication service* · Mock: 45-min Spring technical.
**Deliverable:** `security-chain-trace.md` — trace one authenticated request through every filter.
**Exit:** Explain why `@Transactional` on a `@Async` method behaves unexpectedly · Explain JWT revocation honestly (you cannot; here are the mitigations).

### Week 8 — Kafka Semantics
**Topics:** T-701 · T-702 (Producer) · T-703 (Consumer groups) · T-704 (Delivery semantics) · T-705 (Partition keys)
**Why now:** T-704 is 13th in the Mandatory Core and needs W5's T-809 idempotency to explain end-to-end exactly-once honestly.
**S 10 / P 10** · Coding: T-1411 DP part 1 (LC 70, 198, 322, 300).
**Track C:** Story 11 (scaling/performance) · Design: *Design a notification system* · Mock: 45-min messaging deep-dive.
**Deliverable:** `kafka-guarantees.md` — a table of every guarantee, its configuration, and its precise failure boundary.
**Exit:** Explain why `acks=all` alone doesn't prevent loss · Explain why exactly-once doesn't extend to an external DB write.

### Week 9 — Concurrency + JVM · **CHECKPOINT**
**Topics:** T-401 (JMM) · T-402 (volatile ⛔) · T-409 (Deadlock/races ⛔) · T-406 (Thread pools) · T-410 (Virtual threads) · T-303/T-306 (GC + tuning)
**Why now:** Two ⛔ errata topics — material already memorized wrong. T-401 is the deepest single technical topic (S=8) and needs an established study rhythm. Virtual threads are now standard in Senior Java loops `[H]`.
**S 12 / P 8** · Coding: T-1417 concurrency problems (LC 1114, 1115, 1116) + T-1411 DP part 2.
**Track C:** Story 12 (ambiguity/incomplete information) · Design: *Design a distributed job scheduler* · Mock: ⚑ **Week 9 Checkpoint** — full 3-round loop.

**⚑ Week 9 Checkpoint**

| Dimension | Pass |
|---|---|
| Technical depth | 5+ follow-ups on any W1–W9 topic |
| Coding | 110+ cumulative; Medium ≤ 25 min; 2 Hards solved |
| System design | 9 problems completed; unseen problem handled cleanly |
| Behavioral | 12 stories; any answerable in 30s / 2min / 5min |
| Java fluency | Explain `volatile` via happens-before, not caching |
| Production judgment | Diagnose from an artifact (GC log, EXPLAIN, flame graph) |

### Week 10 — Distributed Data + Resilience
**Topics:** T-618 (Saga/Outbox) · T-614 (Sharding) · T-806 (Consistent hashing) · T-515 (Resilience patterns) · T-616 (Zero-downtime migration)
**Why now:** T-618 needs W3 transactions, W5 idempotency, and W8 Kafka. It is the convergence point of three earlier threads.
**S 10 / P 10** · Coding: T-1407 heaps (LC 215, 347, 23, 295).
**Track C:** Design: *Design a distributed cache* · Mock: 60-min architecture round.
**Deliverable:** `outbox-implementation.md` — working transactional outbox with a CDC or polling publisher.

### Week 11 — Testing, Observability, Performance
**Topics:** T-1101 (Test strategy) · T-1103 (Mockito) · T-1104 (Testcontainers) · T-1205 (Tracing/OTel) · T-1206 (SLI/SLO) · T-1201 (USE/RED) · T-1204 (Percentiles)
**Why now:** These supply the *vocabulary* that makes incident and scaling stories credible. Scheduled late deliberately — they are most valuable as a retrofit onto stories that already exist.
**S 8 / P 12** · Coding: mixed review, timed, 15 problems.
**Track C:** Retrofit observability language into Stories 3, 7, 11 · Design: *Design a metrics/monitoring system* · Mock: 45-min behavioral, full 6-question set.

### Week 12 — Full Loop Simulation
**No new topics.** Four complete simulated loops across the week, each 3–4 rounds, different problems, partners where possible. Full diagnostic re-run. `final-readiness-assessment.md`.
**Exit:** All §8.7 criteria at target, plus 12 design problems, 150+ coding problems, 12–14 stories, 14+ mocks.

---

## 5. Plan C — Complete Senior-to-Staff Program

**Scope:** 111 topics · 415 study + 433 practice = **848 hours** · 9–14 months at 15–20 h/week.

Plan C is **not** twelve more weeks of the same. It is a different objective: Plan B produces a candidate who passes Senior loops; Plan C produces one who is credible at Staff. The difference is not more topics — it is depth, production evidence, and demonstrated scope.

| Phase | Weeks | Focus | Gate |
|---|---|---|---|
| **I · Sprint Foundation** | 1–6 | Plan A verbatim | Week 6 assessment |
| **II · Breadth** | 7–12 | Plan B weeks 7–12 | Week 9 + 12 checkpoints |
| **III · JVM & Performance Depth** | 13–20 | T-304, T-306, T-307, T-308, T-1202, T-1203, T-312, T-205, T-407 | Diagnose a real production issue from artifacts |
| **IV · Architecture Depth** | 21–30 | T-902, T-903, T-904, T-905, T-906, T-910, T-912, T-913, T-916, T-1512 | Write and defend 3 ADRs against a real reviewer |
| **V · Staff Signal** | 31–40 | T-1503, T-1507, T-1509, T-1510, T-1511, T-1513, T-1604 + all Staff-tier architecture | 14 stories with cross-org scope; 2 mocks with a real Staff+ engineer |
| **VI · Continuous** | 41+ | Maintenance cadence, company-specific prep `[C]`, active loops | Offer |

**What Plan C adds that B does not:**
- **Production evidence.** Phases III–IV require *doing* — profiling a real service, writing real ADRs, leading a real migration. Staff signal cannot be studied into existence.
- **Depth beyond the interview.** T-401, T-304, T-612 to a level that survives an expert interviewer, not just a checklist.
- **Written communication.** T-1512 RFCs and design docs — heavily weighted at Staff, absent from shorter plans.
- **Real-reviewer feedback loops.** Self-scoring plateaus; Phase V requires external calibration.

**Deliberately excluded from Plan C:** the entire **Expert tier** (6 topics, 37h, max IWI 4.60) and the long tail below IWI 6.0. Over-investment in Expert-tier material is the most common misallocation in senior interview prep, and the corrected register makes the case stronger than v1.0 did.

---

## 6. Workload Variants

The variants are **not** the same plan with topics deleted. They differ in **depth of treatment**, which is the honest lever. Every variant keeps all three tracks and the full week structure.

### Four depth levels

| Level | What it means | Test |
|---|---|---|
| **Deep** | Internals, trade-offs, production examples, 4+ follow-ups, whiteboard-ready | Can teach it |
| **Working** | Correct 2-min answer, one production example, 2 follow-ups | Can use it |
| **Recognition** | Know what it is, when it applies, that a trade-off exists; can say "I'd need to look at the details" credibly | Can navigate it |
| **Deferred** | Not this plan | — |

Recognition level is a legitimate interview outcome, not a failure. *"I know REPEATABLE READ doesn't prevent write skew — that's why SERIALIZABLE exists — but I'd need to check the exact SSI semantics"* scores far better than a confident wrong answer or silence.

### 10 h/week — Plan A ≈ 60 hours

**Allocation:** Track A 5h · Track B 2.5h · Track C 2.5h

| | |
|---|---|
| **Deep (7)** | T-901, T-609, T-610, T-504, T-801, T-1601, T-1505 — the entire feedback block plus design method |
| **Working (8)** | T-611, T-804, T-909, T-907, T-908, T-605, T-1501, T-1504 |
| **Recognition (12)** | T-503, T-505, T-608, T-802, T-803, T-807, T-809, T-903, T-617, T-811, T-916, T-1207 |
| **Deferred** | Everything else, including all of Kafka, JVM, and concurrency |
| **Practiced repeatedly** | Coding narration (every session) · 2-min story delivery (weekly) · 30s/2min answers for the 7 Deep topics |
| **Coding target** | 30 problems |
| **Design problems** | 3 (URL shortener, rate limiter, news feed) |
| **Stories** | 5 |
| **Mocks** | 3 (weeks 3, 5, 6) |

> **Honest expectation at 10 h/week `[H]`:** this closes the named feedback gaps and installs a design method. It will not produce broad Senior readiness. If interviews are imminent and time is genuinely capped at 10h, prioritize *depth on the feedback block over breadth everywhere else* — a candidate strong on named weaknesses and honest elsewhere outperforms one uniformly shallow.

### 20 h/week — Plan A = 120 hours (baseline)

**Allocation:** Track A 10h · Track B 5h · Track C 5h
**Deep (15):** all feedback-block topics plus T-504, T-505, T-611, T-801, T-804, T-909, T-907, T-1501, T-1504
**Working (20):** the remainder of the Plan A scope
**Recognition (13):** T-503, T-807, T-806, T-618, T-1207, T-613, T-607, T-602, T-604, T-201, T-406, T-402, T-409
**Coding:** 60–75 · **Design:** 6 · **Stories:** 8 · **Mocks:** 6

### 30 h/week — Plan A ≈ 180 hours

**Allocation:** Track A 14h · Track B 8h · Track C 8h
Adds to the 20h baseline:
- **Deep** on T-201 (HashMap), T-406 (thread pools), T-402/T-409 (⛔ errata), T-602 (N+1), T-604 (locking) — pulling Plan B weeks 7 and 9 material forward
- **Working** on T-701–T-705 Kafka semantics
- **Coding:** 100–110 problems, including 5 Hards
- **Design:** 9 problems
- **Mocks:** 12 — **doubling mock volume is the highest-value use of the extra 10 hours `[H]`**, not extra reading
- **Stories:** 10, all with 30s/2min/5min variants

> **Warning at 30 h/week:** sustained 30h weeks alongside a full-time job produce diminishing returns after roughly 4 weeks `[H]`. If the search is longer than 6 weeks, 20 h/week over 12 weeks (Plan B) outperforms 30 h/week over 6 — spacing beats intensity for retention. Choose 30h only when the deadline is genuinely fixed.

---

## 7. Interview Answer Practice System

The feedback said *"explain with greater depth"* and *"explain why."* This section is the direct instrument for that. It runs every week from Week 1.

### 7.1 The nine-layer stack

Every **Deep** topic gets all nine layers built and rehearsed:

| # | Layer | Length | Purpose | Common failure |
|---|---|---|---|---|
| 1 | **Opening** | 30s | Signal you know it; invite the follow-up | Rambling past 45s |
| 2 | **Senior answer** | 2 min | Mechanism + one production example | Definition with no mechanism |
| 3 | **Deep dive** | 10 min | Internals, edge cases, evolution | Repeating layer 2 slower |
| 4 | **Whiteboard** | 3–5 min | Draw it while explaining | Silence while drawing |
| 5 | **Production example** | 90s | A real system, real numbers | Hypothetical, unquantified |
| 6 | **Trade-offs** | 2 min | What it costs; when *not* to use it | Only listing benefits |
| 7 | **Traps** | 60s | The misconception; why it's wrong | Not knowing the trap exists |
| 8 | **Follow-up chain** | — | 5 pre-anticipated follow-ups, answered | Chain collapses at Q3 |
| 9 | **Staff extension** | 2 min | Org, cost, migration, long-horizon framing | Staying purely technical |

### 7.2 Worked example — T-901 Hexagonal Architecture

**L1 (30s):** *"Hexagonal architecture inverts the dependency between your domain and your infrastructure. The domain defines interfaces — ports — for what it needs, and infrastructure provides adapters implementing them. The practical effect is that your business logic has no compile-time dependency on the database, the web framework, or the message broker."*

**L2 (2 min):** Add the dependency rule, the port/adapter distinction with a concrete repository example, where interfaces live, and one real system where it paid off.

**L3 (10 min):** Relationship to Clean Architecture and onion architecture; primary vs secondary ports; where DTOs and mappers live; the testing payoff; how it interacts with JPA entities specifically.

**L4 (whiteboard):** Hexagon, ports on the edge, adapters outside, dependency arrows pointing inward. **Narrate every arrow as you draw it.**

**L5:** *"On [system], swapping [X] for [Y] touched 3 adapter classes and zero domain classes — about 2 days instead of the 3 weeks the estimate assumed."* Real numbers.

**L6:** Indirection cost; mapping overhead; the temptation to leak framework types through ports; when a CRUD service does not justify it. **This layer is the one the feedback was asking for.**

**L7:** The trap — believing hexagonal means a folder layout. It is a *dependency direction* rule; you can have the folders and still violate it by importing `javax.persistence` in a domain class.

**L8 — the chain:**
1. "Where do JPA entities live?" → separate persistence models + mappers, or annotated domain objects as a pragmatic compromise; state which you chose and why
2. "Isn't that a lot of mapping code?" → yes; here is when it is worth it and when it is not
3. "How do you handle transactions across the boundary?" → application-service level; the domain does not know transactions exist
4. "What about queries that don't fit the repository abstraction?" → CQRS-lite read models bypassing the domain; the honest answer
5. "Would you use this on every project?" → **no**, and here is the specific criterion

**L9 (Staff):** Boundaries as *team* boundaries; how architecture constrains parallel work; the migration cost of retrofitting it; how you would introduce it incrementally into a legacy codebase without a rewrite.

### 7.3 Weekly cadence

| Day | Activity | Time |
|---|---|---|
| Mon | Build L1–L2 for the week's Deep topics; **write them out** | 45m |
| Tue | Build L5–L6 — production example and trade-offs | 45m |
| Wed | Build L3 deep dive; rehearse aloud | 45m |
| Thu | Build L7–L8 — traps and the 5-follow-up chain | 45m |
| Fri | **Record L1, L2, L6 for one topic.** Watch it back. | 30m |
| Sat | L4 whiteboard + L9 Staff extension for one topic | 30m |

**The recording is mandatory.** Reading an answer silently and delivering it aloud are different skills, and only one of them is tested. Watching yourself back is the fastest available correction mechanism `[H]`.

### 7.4 Coverage schedule — Plan A

| Week | Full 9-layer treatment |
|---|---|
| 1 | T-901, T-609 |
| 2 | T-610, T-605, T-1505 |
| 3 | T-504, T-611, T-801 |
| 4 | T-804, T-909, T-803 |
| 5 | T-907, T-809, T-807 |
| 6 | Rehearse all 14 — no new builds |

---

## 8. Checkpoints & Rubrics

All rubrics score **1–5**. 3 = Mid. **4 = Senior.** 5 = Staff.

### 8.1 Technical Depth

| Score | Behaviour |
|---|---|
| 1 | Definition only; no mechanism |
| 2 | Mechanism, but the chain collapses at follow-up 1 |
| 3 | Sustains 2 follow-ups; no production example |
| **4** | **Sustains 4 follow-ups; one real production example; names a trade-off unprompted** |
| 5 | Sustains 5+; volunteers the trap before being asked; extends to org/cost implications |

### 8.2 Coding

| Score | Behaviour |
|---|---|
| 1 | Cannot reach a working solution |
| 2 | Works after hints; silent while coding |
| 3 | Medium in 35 min; narrates partially; complexity correct when asked |
| **4** | **Medium in ≤ 25 min; narrates throughout; states complexity unprompted; tests own code** |
| 5 | Medium in ≤ 18 min; discusses alternatives; handles the follow-up variant |

### 8.3 System Design

| Score | Behaviour |
|---|---|
| 1 | Jumps to components; no requirements or scale |
| 2 | Some structure; estimation absent or decorative |
| 3 | Follows method; estimation present but doesn't drive decisions |
| **4** | **All six phases unprompted; estimation drives ≥ 1 decision; ≥ 3 failure modes; ≥ 2 explicit trade-offs** |
| 5 | Volunteers what they would *not* build; discusses migration from an existing system; names monitoring |

### 8.4 Behavioral Communication

| Score | Behaviour |
|---|---|
| 1 | Rambling; no structure; "we" throughout |
| 2 | Loose STAR; no quantified result |
| 3 | Clear STAR; quantified; first-person |
| **4** | **≤ 2 min; quantified; first-person; names alternatives considered; states what it cost** |
| 5 | Scope beyond own team; names who disagreed and their best argument; states what they'd do differently |

### 8.5 Java Fluency

| Score | Behaviour |
|---|---|
| 1 | Syntax hesitation; wrong collection choices |
| 2 | Writes correct Java slowly; choices unjustified |
| 3 | Fluent; correct collections; justifies when asked |
| **4** | **Fluent; justifies collection and concurrency choices unprompted; idiomatic modern Java** |
| 5 | Discusses JVM-level implications of choices; knows when the idiom is wrong |

### 8.6 Production Judgment

| Score | Behaviour |
|---|---|
| 1 | Happy path only |
| 2 | Acknowledges failure when prompted |
| 3 | Names failure modes when asked |
| **4** | **Names a failure mode for every component unprompted; discusses monitoring; cites a real incident** |
| 5 | Distinguishes likely from catastrophic; discusses blast radius, degradation, and recovery cost |

### 8.7 Checkpoint schedule

| Checkpoint | When | Pass bar |
|---|---|---|
| **Initial diagnostic** | Day 0 | None — baseline only |
| **Weekly review** | Every Sunday, 30 min | Exit criteria met; deliverables produced |
| **End-of-week mock** | Every week | Scored; delta tracked |
| **⚑ Week 3** | End W3 | ≥ 3/5 on four of six dimensions |
| **⚑ Week 6 readiness** | End W6 | **≥ 4/5 on technical depth, behavioral, Java fluency; ≥ 3.5/5 on design, coding, production judgment; measurable delta on all Day 0 artifacts** |
| **⚑ Week 9** (Plan B) | End W9 | ≥ 4/5 on five of six |
| **Final loop** | End W6 / W12 | ≥ 4/5 on all six across a full multi-round loop |

**Week 6 is a go/no-go, not a formality.** Below bar on technical depth or behavioral: keep interviewing (interviews are practice) but treat the next 2–3 as calibration rather than targets, and prioritize your highest-preference companies later in the sequence.

---

## 9. Evidence Ledger

Every week produces artifacts. This is how progress is measured rather than felt.

| Week | Written | Code | Recorded | Scored |
|---|---|---|---|---|
| 1 | `domain-purity.md`, Stories 1–2 | 8 problems + LRU errata drill | 1 answer set | Self-mock |
| 2 | `query-plan-analysis.md`, `ADR-001.md`, Stories 3–4 | 8 problems | 1 answer set | DB mock |
| 3 | `transaction-traps.md` (runnable), Stories 5–6 | 8 problems | 3 design runs | ⚑ Checkpoint |
| 4 | `failure-modes.md`, Stories 7–8 | 8 problems | 8 story deliveries | Design mock |
| 5 | `decomposition-analysis.md`, 4 reframed stories | 8 problems | 1 answer set | Behavioral mock |
| 6 | `week-6-assessment.md` | Timed review set | Full diagnostic re-run | ⚑ Readiness |

**Running trackers (maintain all six weeks):**
`problem-log.md` — problem, pattern, time, first-submission correctness, retrospective
`answer-bank.md` — every L1/L2/L6 answer written out, by topic ID
`story-bank.md` — every story, 3 length variants, competency tags
`mock-scores.md` — every mock, all six dimensions, date
`flashcards/` — by topic ID, reviewed on the register's `Rev` intervals
`errata-log.md` — each wrong thing unlearned, with the failing case that proved it

---

## 10. Week 1 Preview — Start Here

**Sunday before Week 1 (3h):** Run the Day 0 diagnostic. Do not read anything first.

| Day | Track A (Technical) | Track B (Coding) | Track C (Performance) |
|---|---|---|---|
| **Mon** | Cockburn's hexagonal article + *Clean Architecture* Ch. 22. Write the port/adapter definitions in your own words. (2h) | LC 1, LC 167 — narrate aloud (45m) | Build L1+L2 for T-901. Write them out. (45m) |
| **Tue** | PostgreSQL Ch. 11.1–11.5. Draw a B+Tree and trace a lookup by hand. (2h) | LC 121 — state the invariant first (45m) | Build L5+L6 for T-901 — production example + trade-offs (45m) |
| **Wed** | *Clean Architecture* Ch. 23. Start `domain-purity.md`. (2h) | LC 242, LC 49 (45m) | Build L3 for T-901; rehearse aloud (45m) |
| **Thu** | Use The Index, Luke Ch. 2–3. Answer questions 6–10 aloud. (2h) | LC 3 — sliding window (45m) | Build L7+L8 for T-609 — traps + 5-follow-up chain (45m) |
| **Fri** | Finish `domain-purity.md` incl. the "would I do it" verdict (2h) | **LC 146 — write the buggy version, expose it, then fix it** (1h) | **Record L1/L2/L6 for T-901. Watch it back.** (30m) |
| **Sat** | — | LC 146 again from scratch, 25 min (45m) | Story inventory: 20+ situations. Write Stories 1–2. L4 whiteboard for T-901. (2.5h) |
| **Sun** | Weekly review: exit criteria, flashcards, 20-min self-mock (1h) | — | — |

**Week 1 total: 20h.** *(10h @ 10h/wk: keep Mon/Tue/Fri Track A, LC 1/121/146, and L1/L2/L6 + Story 1.)*

**By Sunday you will have:** a written architecture analysis with a defended verdict, 8 solved problems in Java, a corrected LRU plus proof of the original bug, 2 STAR stories, 12 flashcards, one recorded answer set, a baseline design exercise, and a first mock score.

**The single most important thing in Week 1** is not the reading. It is Friday's recording. The feedback said *"explain with greater depth."* You cannot fix that by reading more — only by speaking, listening back, and noticing where you stopped.

---

## Phase 3 Complete — Awaiting Approval

**Deliverables:** `blueprint-v1.1-corrections.md` · `learning-roadmap.md`

**Proposed Phase 4:** begin handbook chapters, in Week 1 dependency order — `07-Architecture/01-clean-hexagonal.md` (T-901) and `08-Database/03-index-structures.md` (T-609) first, since those are what Week 1 requires.

**Confirm to proceed to Phase 4 — Chapter Generation.**
