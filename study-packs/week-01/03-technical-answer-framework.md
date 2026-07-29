# T-1601 · Technical Answer Framework

**IWI 7.30 · Runs every week (W1–W6) · This chapter builds the method; §2 applies it in full to T-901**

---

## 1. Why this exists

The named interview feedback included *"explain with greater depth"* and *"explain why you chose it."* Read literally, that sounds like a knowledge gap. It almost never is. An interviewer who says this is usually reporting that the knowledge was present — the first sentence arrived correctly — and then **stopped**. The Notion audit's own finding was a mean answer length of ~110 characters: a single sentence. That's not a knowledge problem, it's a *structure* problem — there was no next layer to go to.

The fix is not "know more facts." It's "build the next four layers before the interview, so they're already there when the follow-up comes," and separately, "narrate them out loud until delivering them is a skill, not a hope."

## 2. The nine-layer stack

Every topic marked **Deep** in the roadmap gets all nine layers built and rehearsed before the interview, not improvised during it.

| # | Layer | Length | Purpose | Common failure |
|---|---|---|---|---|
| 1 | **Opening** | 30s | Signal you know it; invite the follow-up | Rambling past 45s |
| 2 | **Senior answer** | 2 min | Mechanism + one production example | Definition with no mechanism |
| 3 | **Deep dive** | 10 min | Internals, edge cases, evolution | Repeating layer 2, slower |
| 4 | **Whiteboard** | 3–5 min | Draw it while explaining | Silence while drawing |
| 5 | **Production example** | 90s | A real system, real numbers | Hypothetical, unquantified |
| 6 | **Trade-offs** | 2 min | What it costs; when *not* to use it | Only listing benefits |
| 7 | **Traps** | 60s | The misconception; why it's wrong | Not knowing the trap exists |
| 8 | **Follow-up chain** | — | 5 pre-anticipated follow-ups, answered | Chain collapses at question 3 |
| 9 | **Staff extension** | 2 min | Org, cost, migration, long-horizon framing | Staying purely technical |

The layers are cumulative, not alternative — an interviewer who stops you after layer 2 got a complete, well-formed answer. One who keeps probing gets layers 3–9 because you already built them, not because you're inventing them under pressure.

## 3. Worked in full — T-901 Hexagonal Architecture

This is the actual worked answer for this week's first Deep topic. Read `01-clean-hexagonal-architecture.md` first — every claim below is sourced from that chapter, not restated from memory.

**L1 — Opening (30s):**
> "Hexagonal architecture inverts the dependency between your domain and your infrastructure. The domain defines interfaces — ports — for what it needs, and infrastructure provides adapters implementing them. The practical effect is that your business logic has no compile-time dependency on the database, the web framework, or the message broker."

**L2 — Senior answer (2 min):** add the dependency rule, the port/adapter distinction with a concrete repository example (`OrderRepository` port, `PostgresOrderRepository` adapter), where the interface lives (domain package, not infrastructure), and one real system where it paid off.

**L3 — Deep dive (10 min):** relationship to Clean/Onion Architecture (§1 of the chapter); primary vs secondary ports; where JPA entities live (three options, §3); the testing payoff (plain unit tests, no framework bootstrap); how transactions interact (application-service level, §3).

**L4 — Whiteboard:** draw the hexagon from §1's diagram — driving adapters on the left, domain + ports in the center, driven adapters on the right. **Narrate every arrow as you draw it**; silence while drawing is the single most common point-loss in this layer.

**L5 — Production example (90s):** the §6 template, filled from real experience: *"[N] adapter classes changed, zero domain classes, [X days] instead of [Y weeks]."*

**L6 — Trade-offs (2 min):** §4's table — mapping-code cost, extra indirection, when it's not worth it (a thin CRUD service). **This is the layer the feedback was specifically asking for.**

**L7 — Traps (60s):** believing the pattern is a folder layout rather than an enforced dependency direction (§8) — you can have `domain/`, `application/`, `infrastructure/` folders and still violate the rule inside them.

**L8 — Follow-up chain:** the 10 questions in `01-clean-hexagonal-architecture.md` §7, especially Q5 (would you use this on every project — **no**, with a stated criterion) and Q9 (queries that don't fit the repository abstraction — CQRS-lite read models, named as a deliberate exception).

**L9 — Staff extension (2 min):** §9 — boundaries as team boundaries, incremental introduction via Strangler Fig starting at the highest-change-rate module, the org-cost side of the indirection trade-off.

## 4. Weekly build cadence

| Day | Activity | Time |
|---|---|---|
| Mon | Build L1–L2 for the week's Deep topics; **write them out**, don't just think them | 45m |
| Tue | Build L5–L6 — production example and trade-offs | 45m |
| Wed | Build L3 deep dive; rehearse aloud | 45m |
| Thu | Build L7–L8 — traps and the 5-follow-up chain | 45m |
| Fri | **Record L1, L2, L6 for one topic. Watch it back.** | 30m |
| Sat | L4 whiteboard + L9 Staff extension | 30m |

The recording on Friday is not optional. Reading a written answer silently and delivering it aloud under mild pressure are different skills, and an interview only tests the second one.

## 5. Applying this to T-609 (this week's second Deep topic)

Repeat the same nine-layer construction against `02-database-index-fundamentals.md`. As a starting scaffold:

- **L1:** the §1 B+Tree definition, 30 seconds, no more.
- **L2:** the §3 before/after `EXPLAIN` numbers (5.754ms → 0.111ms) — a real measured example beats a hypothetical one every time.
- **L6 (the feedback-targeted layer):** §8's trade-off table, plus the §7 engine-specific correction (PostgreSQL vs InnoDB clustering) — naming which engine you mean, unprompted, is exactly the kind of depth signal layer 6 exists to produce.
- **L7:** the §5 note that a covering index makes an index-only scan *possible*, not *automatic* — a real trap, not a hypothetical one, since it required `SET enable_bitmapscan = off` to actually observe in this chapter's own lab.

## 6. Exercise

Before Friday's recording, write out L1 and L2 for T-609 from memory, without re-reading `02-database-index-fundamentals.md`. Then check against it. Any factual gap found this way is worth more than reading the chapter twice — it locates exactly what didn't stick.
