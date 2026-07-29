# Blueprint v1.1 — Corrections
### Java-Interview-Handbook · Amendments to Phase 2
**Supersedes:** relevant sections of `01-Knowledge-Architecture-Blueprint.md`
**Date:** 28 July 2026
**Method:** all figures recomputed programmatically; no hand-derived rankings

---

## 0. Summary of Corrections

| # | Defect in v1.0 | Severity | Status |
|---|---|---|---|
| C1 | RoS table not sorted by its own computed values | **High** | Fixed — all rankings machine-generated |
| C2 | RoS ignored Practice Hours, making practice-heavy topics look free | **High** | Fixed — split into two metrics |
| C3 | **Topic count stated as 124; actual count is 198** | **High** | Fixed — tier counts recomputed |
| C4 | Effort totals understated (1,338h stated vs 1,371h actual) | Medium | Fixed |
| C5 | Frequency estimates presented with unwarranted precision | Medium | Fixed — epistemic labelling added |
| C6 | Gap severity scale lacked a "studied-and-wrong" band | Medium | Fixed — 5-band scale |
| C7 | Ratio metric proposed as roadmap driver without testing it | **High** | Fixed — tested, found unsuitable alone, two-list scheduler adopted |

Corrections C3 and C7 were surfaced during recomputation and were not in the original correction request.

---

## 1. C1 + C2 — The Return-on-Study Model

### 1.1 What was wrong

The v1.0 table was internally inconsistent. Ranked positions did not follow the printed values:

| v1.0 rank | Topic | Printed RoS | Should have ranked |
|---|---|---|---|
| 1 | T-1504 Incident stories | 5.73 | 6th |
| 2 | T-1501 STAR | 5.33 | 9th |
| 3 | T-1505 Arch. narration | 5.40 | 7th |
| 7 | T-1509 Cross-team influence | 7.50 | **2nd** |
| 12 | T-1603 Mock interviews | 7.90 | **1st** |

The values were computed correctly; the ordering was assigned by intuition and never reconciled against them. Every ranking in v1.1 is generated from the register data by script.

The second defect was structural. `RoS = (IWI × Gap) / StudyHours` ignores practice entirely. T-1603 Mock Interviews carries **2 study hours and 20 practice hours**. Dividing by study alone reported it as the cheapest high-value topic in the register — when it is in fact one of the most expensive commitments in the entire plan.

### 1.2 The corrected two-metric model

```
Knowledge RoS   = (IWI × GapSeverity) / max(StudyHours, 2.0)
Readiness RoS   = (IWI × GapSeverity) / max(StudyHours + PracticeHours, 3.0)
```

| Metric | Question it answers | Used for |
|---|---|---|
| **Knowledge RoS** | Cheapest route to *understanding* a topic | Concept acquisition order; deciding what to read |
| **Readiness RoS** | Cheapest route to *performing* a topic under interview conditions | Roadmap construction; calendar allocation |

### 1.3 Normalization decisions — and why

**Denominator floors (Study ≥ 2.0h, Study+Practice ≥ 3.0h).**
Without floors, a 1-hour topic divides by 1 and inflates arbitrarily. T-1514 "Questions to ask your interviewer" (IWI 5.40, 1h) would score 10.80 — ranking above every genuinely important topic in the register purely because it is short. The floors are a **regularization term** that prevents small denominators from dominating, at the cost of slightly under-rewarding genuinely cheap topics. That trade is correct: the failure mode being prevented is severe, the cost is marginal.

**Gap severity expanded to five bands.** v1.0 used a 3-band scale. Phase 1 found eight topics where the existing material is not merely absent but **actively wrong and already studied** — the buggy LRU, the invented "Running" thread state, the `volatile`-as-caching model. Unlearning costs more than learning, so these need their own band.

| Band | Symbol | Multiplier | Count | Meaning |
|---|---|---|---|---|
| Adequate | 🟢 | 1.0 | 1 | Covered acceptably |
| Partial | 🟡 | 1.3 | 8 | Exists, materially incomplete |
| Shallow | 🟠 | 1.6 | 28 | Definition-level only |
| Absent | 🔴 | 2.0 | 153 | No coverage |
| **Absent-and-wrong** | ⛔ | **2.5** | **8** | Studied incorrectly; requires unlearning |

**No normalization of IWI itself.** IWI is already bounded 1–10 and the register spans 3.20–8.65. Rescaling would add no discrimination and would obscure the raw scores.

### 1.4 Where the two metrics diverge — the practical payoff

This table is the direct answer to the critique. Rank shifts are computed within the IWI ≥ 6.0 eligible set.

| ID | Topic | K-RoS rank | R-RoS rank | Shift | S | P |
|---|---|---|---|---|---|---|
| T-1603 | Mock interviews & self-evaluation | 1 | **105** | **−104** | 2 | 20 |
| T-1602 | System design narration | 2 | **90** | **−88** | 2 | 8 |
| T-1416 | Design-style coding problems (LRU) | 24 | 102 | −78 | 3 | 10 |
| T-1419 | Coding communication protocol | 12 | 88 | −76 | 2 | 6 |
| T-1502 | Story portfolio design | 47 | 101 | −54 | 4 | 10 |
| T-1403 | Hashing patterns | 52 | 106 | −54 | 2 | 12 |
| T-1601 | Technical communication protocol | 4 | 54 | −50 | 2 | 5 |
| T-1503 | Scope & influence narratives | 20 | 61 | −41 | 3 | 5 |
| T-301 | JVM memory layout | 48 | **8** | **+40** | 3 | 1 |
| T-1604 | Company loop structures | 44 | 6 | +38 | 3 | 1 |
| T-304 | G1 internals | 94 | 65 | +29 | 5 | 2 |
| T-503 | Spring AOP & proxy mechanics | 78 | 52 | +26 | 4 | 2 |
| T-1301 | OWASP Top 10 | 75 | 50 | +25 | 4 | 2 |

**The correction changes the recommendation materially.** Under v1.0's single metric, mock interviewing appeared to be the single highest-return activity available. Under Readiness RoS it falls to 105th, because 20 hours of practice is a real and substantial cost.

That does **not** mean mocks are unimportant — their IWI is 7.90 and they remain mandatory. It means the v1.0 claim that they were *cheap* was an artifact of a broken denominator. They are expensive and necessary, which is a different scheduling instruction: **book them deliberately, don't assume they fit in the gaps.**

### 1.5 Corrected rankings

#### Knowledge RoS — top 20 (IWI ≥ 6.0)

| # | ID | Topic | IWI | Gap | S | **K-RoS** |
|---|---|---|---|---|---|---|
| 1 | T-1603 | Mock interviews & self-evaluation rubrics | 7.90 | 🔴 | 2 | **7.90** |
| 2 | T-1602 | System design narration & whiteboard | 7.60 | 🔴 | 2 | **7.60** |
| 3 | T-1509 | Cross-team influence without authority | 7.50 | 🔴 | 2 | **7.50** |
| 4 | T-1601 | Technical communication protocol | 7.30 | 🔴 | 2 | **7.30** |
| 5 | T-1506 | Conflict & technical disagreement | 7.10 | 🔴 | 2 | **7.10** |
| 6 | T-1508 | Failure, mistakes, and learning | 7.00 | 🔴 | 2 | **7.00** |
| 7 | T-1507 | Mentoring & growing engineers | 6.90 | 🔴 | 2 | **6.90** |
| 8 | T-1206 | SLI, SLO, error budgets | 6.80 | 🔴 | 2 | **6.80** |
| 9 | T-1204 | Latency, percentiles, coordinated omission | 6.70 | 🔴 | 2 | **6.70** |
| 10 | T-1511 | Technical debt advocacy | 6.60 | 🔴 | 2 | **6.60** |
| 11 | T-910 | Modular monolith as deliberate choice | 6.40 | 🔴 | 2 | **6.40** |
| 12 | T-1419 | Coding interview communication protocol | 6.40 | 🔴 | 2 | **6.40** |
| 13 | T-916 | Architecture Decision Records | 6.20 | 🔴 | 2 | **6.20** |
| 14 | T-1208 | Capacity planning & headroom | 6.10 | 🔴 | 2 | **6.10** |
| 15 | T-1504 | Production incident stories | 8.60 | 🔴 | 3 | **5.73** |
| 16 | T-409 | Deadlock, livelock, races | 6.70 | ⛔ | 3 | **5.58** |
| 17 | T-402 | volatile & final field semantics | 6.60 | ⛔ | 3 | **5.50** |
| 18 | T-1505 | Architecture decision narration | 8.10 | 🔴 | 3 | **5.40** |
| 19 | T-1501 | STAR structure | 8.00 | 🔴 | 3 | **5.33** |
| 20 | T-1503 | Scope, impact & influence narratives | 7.95 | 🔴 | 3 | **5.30** |

*Note the two ⛔ entries at 16–17. Both are concurrency topics where Phase 1 found the existing material factually wrong. They rank high because unlearning-plus-relearning carries a 2.5× multiplier on a modest study cost.*

#### Readiness RoS — top 20 (IWI ≥ 6.0)

| # | ID | Topic | IWI | Gap | S | P | **R-RoS** |
|---|---|---|---|---|---|---|---|
| 1 | T-1206 | SLI, SLO, error budgets | 6.80 | 🔴 | 2 | 1 | **4.53** |
| 2 | T-910 | Modular monolith | 6.40 | 🔴 | 2 | 1 | **4.27** |
| 3 | T-1204 | Latency & percentiles | 6.70 | 🔴 | 2 | 2 | **3.35** |
| 4 | T-402 | volatile & final semantics | 6.60 | ⛔ | 3 | 2 | **3.30** |
| 5 | T-1511 | Technical debt advocacy | 6.60 | 🔴 | 2 | 2 | **3.30** |
| 6 | T-1604 | Company loop structures | 6.50 | 🔴 | 3 | 1 | **3.25** |
| 7 | T-908 | Monolith vs microservices | 7.90 | 🔴 | 3 | 2 | **3.16** |
| 8 | T-301 | JVM memory layout | 6.30 | 🔴 | 3 | 1 | **3.15** |
| 9 | T-916 | Architecture Decision Records | 6.20 | 🔴 | 2 | 2 | **3.10** |
| 10 | T-1208 | Capacity planning | 6.10 | 🔴 | 2 | 2 | **3.05** |
| 11 | T-705 | Partition key design & ordering | 7.55 | 🔴 | 3 | 2 | **3.02** |
| 12 | T-1509 | Cross-team influence | 7.50 | 🔴 | 2 | 3 | **3.00** |
| 13 | T-702 | Producer semantics: acks, idempotence | 7.40 | 🔴 | 3 | 2 | **2.96** |
| 14 | T-913 | Tech debt & evolutionary architecture | 7.25 | 🔴 | 3 | 2 | **2.90** |
| 15 | T-1207 | Incident response & postmortems | 7.10 | 🔴 | 3 | 2 | **2.84** |
| 16 | T-1506 | Conflict & disagreement | 7.10 | 🔴 | 2 | 3 | **2.84** |
| 17 | T-513 | JWT design & revocation | 7.00 | 🔴 | 3 | 2 | **2.80** |
| 18 | T-1101 | Test strategy | 7.00 | 🔴 | 3 | 2 | **2.80** |
| 19 | T-1508 | Failure & learning | 7.00 | 🔴 | 2 | 3 | **2.80** |
| 20 | T-409 | Deadlock, livelock, races | 6.70 | ⛔ | 3 | 3 | **2.79** |

---

## 2. C7 — Why Ratio Metrics Cannot Drive the Roadmap Alone

This correction was not requested but is necessary, and it emerged from actually running the numbers rather than reasoning about them.

### 2.1 The artifact

Both ratio metrics systematically promote **cheap topics of moderate importance** over **expensive topics of decisive importance**. Unfiltered, the Readiness RoS top 10 is:

> SLI/SLO · Modular monolith · API gateway/BFF · Cloud cost · Spring Cache · Secrets management · Immutability · Bean scopes · 12-factor · Questions to ask

Every one is legitimate. Not one is a topic that decides a Senior Java interview. Meanwhile:

| Topic | IWI | IWI rank | R-RoS rank |
|---|---|---|---|
| T-801 Design method | 8.65 | **1** | 132 |
| T-813 Canonical design problems | 8.20 | 8 | 178 |
| T-609 Index structures | 8.30 | 7 | 111 |
| T-401 Java Memory Model | 7.75 | 22 | 121 |

A roadmap built from R-RoS alone would schedule SLI/SLO ahead of database indexing. That is obviously wrong, and the interview feedback in the Phase 3 brief — which names index topics explicitly — confirms it independently.

### 2.2 The composite that was tried and rejected

A geometric-mean composite was tested:

```
SPS = sqrt( (IWI × Gap) × ReadinessRoS )
```

The geometric mean was chosen because it balances magnitude against efficiency without letting either dominate. **It failed.** The resulting top 3 was SLI/SLO, Modular monolith, and `volatile` — with T-609 Index structures still at rank 111. The efficiency term varies across a much wider range (0.51 → 4.53, a 9× spread) than the magnitude term (7.7 → 17.3, a 2.2× spread), so efficiency dominates any product-based composite regardless of weighting.

This is documented rather than silently discarded because the negative result is informative: **no single scalar built from a cost-divided ratio will produce a sane interview roadmap.**

### 2.3 The adopted design — a two-list scheduler

| List | Selection rule | Purpose |
|---|---|---|
| **0 · Feedback Override** | Named in real interview feedback | Scheduled first, unconditionally |
| **1 · Mandatory Core** | Ranked by **Weighted IWI** (`IWI × Gap`), cost-blind | Must appear in the plan regardless of expense |
| **2 · Efficiency Fill** | Ranked by **Readiness RoS** | Fills remaining capacity; maximises value per residual hour |

Magnitude decides *inclusion*. Efficiency decides *ordering within available time*. This mirrors standard knapsack practice — take the mandatory items, then fill by density — and it is the structure `00-Roadmap.md` implements.

### 2.4 Mandatory Core — top 20 by Weighted IWI

| # | ID | Topic | IWI | Gap | **W** | S+P |
|---|---|---|---|---|---|---|
| 1 | T-801 | Design method | 8.65 | 🔴 | **17.3** | 16 |
| 2 | T-1504 | Production incident stories | 8.60 | 🔴 | **17.2** | 7 |
| 3 | T-804 | Caching strategies & invalidation | 8.45 | 🔴 | **16.9** | 11 |
| 4 | T-909 | Distributed systems failure modes | 8.45 | 🔴 | **16.9** | 10 |
| 5 | T-1502 | Story portfolio design | 8.45 | 🔴 | **16.9** | 14 |
| 6 | T-907 | Microservice decomposition | 8.40 | 🔴 | **16.8** | 9 |
| 7 | T-409 | Deadlock, livelock, races | 6.70 | ⛔ | **16.75** | 6 |
| 8 | T-609 | Index structures | 8.30 | 🔴 | **16.6** | 11 |
| 9 | T-402 | volatile & final semantics | 6.60 | ⛔ | **16.5** | 5 |
| 10 | T-813 | Canonical design problems | 8.20 | 🔴 | **16.4** | 32 |
| 11 | T-504 | @Transactional & self-invocation | 8.15 | 🔴 | **16.3** | 9 |
| 12 | T-1505 | Architecture decision narration | 8.10 | 🔴 | **16.2** | 7 |
| 13 | T-704 | Delivery semantics & exactly-once | 8.00 | 🔴 | **16.0** | 7 |
| 14 | T-1501 | STAR structure | 8.00 | 🔴 | **16.0** | 7 |
| 15 | T-611 | Isolation levels & anomalies | 7.95 | 🔴 | **15.9** | 8 |
| 16 | T-1503 | Scope & influence narratives | 7.95 | 🔴 | **15.9** | 8 |
| 17 | T-610 | Query planning & EXPLAIN | 7.90 | 🔴 | **15.8** | 12 |
| 18 | T-803 | API design | 7.90 | 🔴 | **15.8** | 9 |
| 19 | T-807 | CAP, PACELC, consistency | 7.90 | 🔴 | **15.8** | 8 |
| 20 | T-908 | Monolith vs microservices | 7.90 | 🔴 | **15.8** | 5 |

Two ⛔ concurrency topics (T-409, T-402) enter the Mandatory Core at ranks 7 and 9 despite modest IWI, purely on the unlearning multiplier. That is the intended behaviour: material already memorized incorrectly is a liability, not a gap.

---

## 3. C3 + C4 — Arithmetic Corrections

### 3.1 Topic count

v1.0 stated **124 topics** and gave tier counts summing to 124. Both were wrong. The register as written contains **198 topics**.

| Tier | v1.0 (stated) | v1.1 (actual) | Δ |
|---|---|---|---|
| Foundation | 24 | **20** | −4 |
| Core | 41 | **58** | +17 |
| Advanced | 28 | **60** | +32 |
| Staff-Level | 22 | **54** | +32 |
| Expert | 9 | **6** | −3 |
| **Total** | **124** | **198** | **+74** |

The v1.0 count was asserted rather than computed. Every downstream statement referencing "124 topics" should read 198.

**One v1.0 claim survives and is strengthened:** the Expert tier is the smallest and lowest-value in the register — 6 topics, 37 total hours, **maximum IWI 4.60**. No Expert topic appears anywhere near the top of any ranking. The recommendation to defer it stands.

### 3.2 Effort model

| Metric | v1.0 | v1.1 | Δ |
|---|---|---|---|
| Study hours | 641 | **663** | +22 |
| Practice hours | 697 | **708** | +11 |
| **Total** | **1,338** | **1,371** | **+33** |

Per-domain totals (corrected):

| Domain | n | Study | Practice | Total | Avg IWI |
|---|---|---|---|---|---|
| D1 Java Core | 16 | 50 | 25 | 75 | 4.93 |
| D2 Collections | 9 | 22 | 15 | 37 | 5.68 |
| D3 JVM | 12 | 42 | 24 | 66 | 5.84 |
| D4 Concurrency | 16 | 56 | 34 | 90 | 5.67 |
| D5 Spring | 17 | 62 | 39 | 101 | 6.26 |
| D6 Database | 18 | 74 | 55 | 129 | 6.86 |
| D7 Kafka | 10 | 33 | 23 | 56 | 6.79 |
| **D8 System Design** | 14 | 60 | 69 | 129 | **7.40** |
| D9 Architecture | 16 | 58 | 42 | 100 | 6.96 |
| D10 Cloud | 9 | 31 | 19 | 50 | 5.71 |
| D11 Testing | 8 | 22 | 21 | 43 | 5.82 |
| D12 Performance | 8 | 23 | 20 | 43 | 6.54 |
| D13 Security | 7 | 20 | 11 | 31 | 5.76 |
| D14 Coding | 19 | 62 | 223 | 285 | 5.47 |
| D15 Behavioral | 15 | 39 | 54 | 93 | 7.24 |
| **D16 Interview Craft** | 4 | 9 | 34 | 43 | **7.32** |
| **Total** | **198** | **663** | **708** | **1,371** | 6.22 |

### 3.3 Corrected scope budgets

| Scope | Definition | Topics | Total h |
|---|---|---|---|
| Plan A — Sprint | Feedback override + selected Mandatory Core, partial depth | 48 | **~120 allocated** |
| Plan B — Interview-Ready | IWI ≥ 6.5 + all behavioral/craft + coding volume | 104 | ~240 allocated |
| Plan C — Senior-ready | IWI ≥ 6.0 + all behavioral/craft | 111 | 848 |
| Complete register | Everything | 198 | 1,371 |

*Plans A and B use **allocated** hours, not full topic hours: a sprint takes topics to interview-usable depth (typically 50–70% of full study time plus targeted practice), not to completion. Plan C uses full hours.*

---

## 4. C5 — Epistemic Labelling of Frequency Estimates

### 4.1 The problem

v1.0 stated frequency bands as percentages — "Near-Certain: ≥80% of loops" — with a parenthetical about composite sourcing. That phrasing implies measurement. **No such measurement exists.** These are structured judgments, and presenting them as data was overclaiming.

### 4.2 The labelling scheme

Every claim in the handbook now carries one of four markers:

| Marker | Meaning | Verification |
|---|---|---|
| **[E] Evidence** | Directly observed or computable | Reproducible from a stated source |
| **[H] Heuristic** | Informed estimate, no measurement | Not verifiable; treat as a prior |
| **[C] Company-specific** | Varies materially by employer | Confirm against the target company |
| **[R] Role-specific** | Varies by stack, domain, or seniority | Adjust to the actual role |

### 4.3 Applied to this project

**[E] Evidence-based — trust these:**
- All Phase 1 audit figures. 262 rows, ~110-char mean answer length, 3 answers over 300 chars, 0 reviewed, 4 tracker problems, 0 in Java — computed by direct SQL aggregation over the live workspace.
- All seven code defects and six incorrect technical claims — verified by reading the stored implementations.
- Every arithmetic figure in this document — machine-computed from the register.
- Java/Spring/PostgreSQL/Kafka **behaviour** claims — verifiable against documentation and source.

**[H] Heuristic — informed judgment, no measurement:**
- **Every `F` (frequency) score in the register.** These reflect pattern recognition across public interview reports, published loop structures, and engineering-hiring convention. They are priors, not data.
- Every `Q`, `P`, `A`, `D` factor score. Judgment calls.
- The IWI weights (0.30/0.20/0.20/0.15/0.15). Defensible, not derived.
- All study and practice hour estimates. Individual variation is large.

**[C] Company-specific — must be confirmed per target:**
- Amazon: behavioral share is materially higher than the register's average weighting implies, and LP mapping (T-1513) becomes near-mandatory rather than optional.
- Stripe: API design (T-803) and idempotency (T-809) weight far above the mean; the loop includes practical/integration rounds unlike most.
- Netflix: distributed failure modes (T-909) and operational judgment weight heavily; the process is unusual in structure.
- Consultancy-model employers (EPAM and similar): client-facing communication and breadth-across-stack often outweigh algorithmic depth.
- Startups: system design frequently replaced by practical build exercises.

**[R] Role-specific — adjust to the actual posting:**
- Reactive stacks: T-509 WebFlux rises sharply; otherwise low priority.
- Data-intensive roles: D6 Database and D7 Kafka rise; D14 algorithms fall.
- Platform/infra roles: D10 Cloud rises materially.
- Staff vs Senior: D9 Architecture and D15 Behavioral weights rise; D14 algorithm weight falls.

### 4.4 Revised frequency bands

Bands are now expressed as **ordinal likelihood**, not percentages:

| Band | Interpretation | Planning implication |
|---|---|---|
| **Near-Certain** | Expect it in essentially every loop | Must be interview-ready |
| **Very High** | Expect it in most loops | Must be interview-ready |
| **High** | Appears often; likely at least once | Should be solid |
| **Moderate** | Appears sometimes, often stack-dependent | Recognition-plus |
| **Occasional** | Appears in a minority of loops | Recognition-level |
| **Rare** | Unlikely unless the role is specialised | Awareness only |

**The single most reliable calibration source is not this document.** It is the user's own interview history. The Phase 3 brief contains real feedback from a real interview naming Clean Architecture, hexagonal boundaries, and index types. That is **[E] evidence** for this specific candidate and target market, and it outranks every **[H]** estimate in the register. The roadmap treats it accordingly.

---

## 5. Amendments to Carry Forward

| v1.0 statement | v1.1 replacement |
|---|---|
| "124 topic units" | **198 topic units** |
| Tier counts 24/41/28/22/9 | **20/58/60/54/6** (FDN/COR/ADV/STF/EXP) |
| "1,338 hours" | **1,371 hours** |
| "§8 RoS ranking" | **Two rankings — Knowledge RoS and Readiness RoS** |
| "RoS drives the roadmap" | **Two-list scheduler: Weighted IWI for inclusion, Readiness RoS for ordering** |
| "Frequency ≥80% of loops" | **Ordinal bands, labelled [H] heuristic** |
| Gap severity 3 bands | **5 bands, incl. ⛔ absent-and-wrong (×2.5)** |

**Unchanged and reconfirmed:**
- The IWI formula and factor definitions, including *differentiating* difficulty.
- All 198 IWI scores.
- The Top-25-by-IWI list (independently re-sorted; v1.0 ordering was correct here).
- The finding that **all 25 highest-IWI topics are gapped** — 24 🔴 absent, and under the expanded scale two additional ⛔ entries join the Mandatory Core.
- The three-track parallel model.
- The dependency graph and critical-path chains.
- The 19-chapter Table of Contents, including `ERRATA.md` and `INDEX.md`.
- The recommendation to defer the Expert tier — now more strongly supported: 6 topics, max IWI 4.60.

---

**Blueprint v1.1 complete.** Proceed to `00-Roadmap.md`.
