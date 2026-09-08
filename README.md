# cracking-code-interviews

Structured interview preparation for Java backend engineers across the full **Junior-through-Staff** ladder — start at true fundamentals with no prior experience assumed, or start wherever you already are and go deeper toward Senior or Staff.

> ⚠️ **Public repository.** See [Privacy and confidentiality](#privacy-and-confidentiality) before committing anything derived from real work — STAR stories, production examples, and interview feedback are the highest-risk content this repo will eventually hold.

---

## Purpose

This repository is the working system for a targeted interview-preparation programme. It exists because a Notion knowledge-base audit found the existing study material broad but shallow — a mean answer length of roughly 110 characters, 76% of rows with no code example, several technically incorrect implementations already memorized, and zero coverage of System Design, Behavioral prep, or any Java feature after Java 8.

The programme is built around one organizing idea:

> **Knowledge you cannot articulate under pressure scores zero.**

Every chapter pairs technical depth with a spoken-answer instrument: layered answers, trade-off structures, recorded drills, and scored mock interviews.

**Target roles** — the full ladder, not one fixed point on it:
- Junior/Mid Backend Engineer *(true fundamentals — `syllabus/01-computer-science-foundations/`, `syllabus/03-data-structures-algorithms/`, and the Junior Fundamentals chapters across `02-java`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`)*
- Senior Java Backend Engineer *(the original primary focus, still the deepest-covered level)*
- Staff Engineer *(secondary — Plan C)*

---

## Current status

*(Note: the phase model below — Audit → Blueprint → Roadmap → Study Packs → Handbook → Complementary Deliverables — is this project's original bootstrap sequence, described in full in `CLAUDE.md`. That bootstrap is complete; canonical content now lives under `syllabus/`, not `handbook/` — see Repository structure below.)*

| | |
|---|---|
| **Phase** | Original 7-phase bootstrap complete. Canonical content fully migrated to and organized under `syllabus/`, per `00-project/syllabus-transformation-plan.md` (all phases complete 2026-09-07). |
| **Completed** | Phase 1 Audit · Phase 2 Blueprint · Phase 3 Corrections · Phase 3 Roadmap (`00-project/`) · **`study-packs/week-01` through `week-25`** (Interview Emergency Sprint) · **`study-packs/junior-to-mid/`** (7 weeks) and **`study-packs/mid-to-senior/`** (10 weeks) — see [`study-packs/README.md`](study-packs/README.md) for which one to use · `syllabus/` — 21 domains, 222 canonical topic files, each L1 (Foundation) through L4 (Staff) · Interview Playbook, Architecture Atlas, Production Cookbook, and Behavioral content (now under `syllabus/20-interview-preparation/behavioral/`) all built out — see the counts below |
| **Complementary deliverables** | 202 cheat sheets · 207 flashcard decks (695 cards) · 136 production-cookbook entries · 17 Architecture Atlas system-design write-ups (counts verified against the file system 2026-09-08) |
| **Topic register** | 198 original backend topics (16 domains) plus 4 new-writing domains (Computer Science Foundations, Data Structures & Algorithms, Engineering Practices, Leadership & Staff) and a separate React/Next.js register (`00-project/frontend-topic-register.md`) — 222 topic files total across all 21 `syllabus/` domains |
| **Estimated total effort** | 1,371 hours (663 study + 708 practice) for the original 198-topic backend register; does not include the four domains added after that estimate was made |

Weeks 1–6 total **~60,400 words** across 74 chapter files, with every technical claim backed by real, executed code rather than description alone:

| Week | Real Java | Real SQL / other |
|---|---|---|
| 1 | 18/18 assertions (LRU errata reproduced + fixed) | PostgreSQL index lab (seq scan → index scan → index-only scan) |
| 2 | 21/21 assertions (monotonic-stack errata) | Query-plan lab (3 diagnoses) + many-to-many price-history bug |
| 3 | 11/11 tree assertions + 6 real Spring transaction demos | Write-skew reproduced (REPEATABLE READ) and prevented (SERIALIZABLE) |
| 4 | 14/14 graph assertions + retry-storm, cache-stampede, fencing-token demos | Pagination lab (OFFSET ~3,000x slower than keyset at depth) |
| 5 | 23/23 assertions (Circular Queue errata fix) + idempotency-key mechanism | — |
| 6 | Consolidation — no new technical claims (by design) | — |

The Weeks 1–6 table above reflects this project's earliest, most heavily-verified work; later weeks (through week-25) extend the same real-executed-code discipline across the full topic register and a dedicated coding-problem closure sprint. See `CHANGELOG.md` for the full history and the archived-vs-real content distinction from initialization, and `syllabus/00-overview/changelog.md` for the canonical-content migration's own history.

---

## Repository structure

```
cracking-code-interviews/
├── 00-project/             Audit, blueprint, corrections, roadmap, transformation plan  (the "why")
├── study-packs/            Week-by-week execution material                (the "do this week")
│   └── week-01..25/        Plan A/B/C study weeks — see Current status above
├── syllabus/               Canonical technical reference, 21 domains     (the "look it up")
│   ├── 00-overview/        Vision, taxonomy, topic spec, mastery model, learning-paths/
│   └── 01-*/ .. 21-*/      One directory per domain (Java, DBs, Kafka, System Design,
│                           Frontend/Next.js, Interview Prep, Leadership, etc.)
├── interview-playbook/     README + private, real interview-loop notes (`company-prep/`)
├── practice/               Runnable Java, SQL, design exercises          (the "do it")
├── flashcards/             Spaced-repetition cards, one deck per topic
├── cheat-sheets/           Condensed pre-interview reference, one per topic
├── architecture-atlas/     System-design case studies with full diagrams and trade-offs
├── production-cookbook/    Incident-postmortem-style troubleshooting entries
├── templates/              Reusable templates (ADR, STAR, retrospective)
├── resources/              Source bibliography
├── scripts/                Validation and maintenance tooling
└── archive/                Superseded material, retained for provenance
```

### What goes where — and why the distinction matters

| Directory | Answers the question | Lifetime | Example |
|---|---|---|---|
| **`00-project/`** | *Why is the plan shaped this way?* | Stable; amended by explicit correction documents | The roadmap, the scoring model |
| **`study-packs/`** | *What do I do this week?* | Frozen once approved | Week 1: hexagonal architecture + B+Tree indexing |
| **`syllabus/`** | *How does this actually work?* | Long-lived reference, revised as understanding deepens | `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md` |
| **`interview-playbook/`** | *How do I say this out loud?* | Long-lived | `company-prep/large-ecommerce-retailer-senior-backend-remote.md` |
| **`practice/`** | *Can I actually do it?* | Append-only | Runnable LRU cache with JUnit tests; PostgreSQL index lab |

The separation is deliberate. **Study packs are time-boxed and disposable in the sense that they are executed once**; `syllabus/` is what remains afterward. Every complementary deliverable (cheat sheets, flashcards, Architecture Atlas, Production Cookbook) references its `syllabus/` canonical chapter rather than restating it — see `00-project/syllabus-transformation-plan.md` for the full canonical-ownership model and `syllabus/00-overview/INDEX.md` for the domain-by-domain index.

> **Notion is a read-only historical source.** The original knowledge base remains in Notion and is never modified by this project. It was audited, not migrated. Anything of value is rewritten here from primary sources rather than copied — see `00-project/knowledge-base-audit.md` §6 for exactly what does and doesn't survive into the canonical `syllabus/` chapters.

---

## Choosing your starting point

Eight separate programs exist under `study-packs/` — pick the one that matches your situation, not the first one you find:

| If you are... | Use... |
|---|---|
| New to backend development entirely (0–2 years, no prior Java/SQL/Spring) | [`study-packs/junior-to-mid/`](study-packs/junior-to-mid/README.md) — 7 weeks |
| A working engineer (2–5 years) who ships features but needs Senior-level internals depth | [`study-packs/mid-to-senior/`](study-packs/mid-to-senior/README.md) — 10 weeks |
| A Senior engineer building the systemic/organizational judgment a Staff loop tests | [`study-packs/senior-to-staff/`](study-packs/senior-to-staff/README.md) — 8 weeks |
| Focused specifically on deep Java-stack mastery rather than broader domain breadth | [`study-packs/backend-java-specialization/`](study-packs/backend-java-specialization/README.md) — 9 weeks |
| Already Senior/Staff-level with an interview loop coming up soon — recall, not new learning | [`study-packs/senior-interview-refresh/`](study-packs/senior-interview-refresh/README.md) — 3–5 days |
| New to frontend/web development entirely (0–2 years, no prior JS/TS/React) | [`study-packs/frontend-junior-to-mid/`](study-packs/frontend-junior-to-mid/README.md) — 6 weeks |
| A working frontend/full-stack engineer who needs Senior-level Next.js/React internals depth | [`study-packs/frontend-mid-to-senior/`](study-packs/frontend-mid-to-senior/README.md) — 10 weeks |
| On an urgent interview timeline and need the highest-impact material fast, regardless of level | Plan A/B/C below (`study-packs/week-01` through `week-25`) |

See [`study-packs/README.md`](study-packs/README.md) for the full breakdown. The rest of this section (**How to use the roadmap** through **Starting the programme**) describes the last option — the original Interview Emergency Sprint program this repository started with.

---

## How to use the roadmap

Read [`00-project/learning-roadmap.md`](00-project/learning-roadmap.md) in full once. It contains three plans; pick one based on how close your interviews are.

The roadmap is driven by two metrics, both defined in [`00-project/blueprint-v1.1-corrections.md`](00-project/blueprint-v1.1-corrections.md):

- **Weighted IWI** (`Interview Weight Index × gap severity`) decides **what must be included**, cost-blind.
- **Readiness RoS** (`weighted IWI ÷ (study + practice hours)`) decides **ordering within available time**.

**Real interview feedback overrides both.** Weeks 1–2 of every plan are pre-committed to weaknesses named in an actual interview, regardless of where the model ranks them.

### The three plans

| | **Plan A** | **Plan B** | **Plan C** |
|---|---|---|---|
| **Duration** | 6 weeks | 12 weeks | 9–14 months |
| **Use when** | Interviews booked or expected within ~8 weeks | Actively looking, no urgent deadline | Deliberate Senior → Staff progression |
| **Scope** | 48 topics, partial depth | 104 topics, working depth | 111 topics, full depth |
| **Coding target** | 60–75 problems | 150–170 | 220–250 |
| **Design problems** | 6 | 12 | 12 + variants |
| **STAR stories** | 8 | 12–14 | 14 + Staff-scope rewrites |
| **Mock interviews** | 6 | 14 | 30+ |
| **Outcome** | Survivable in known weak areas | Balanced Senior readiness | Staff-credible |

**Plan A — Interview Emergency Sprint (6 weeks, ~120h at 20h/week).** Weeks 1–2 address named interview feedback; weeks 3–5 cover the highest-weight gaps; week 6 is consolidation and a full mock loop. Three tracks run every week — technical depth, coding practice, interview performance — never sequenced.

**Plan B — Interview-Ready Programme (12 weeks).** Weeks 1–6 identical to Plan A. Weeks 7–12 broaden into Spring depth/security, Kafka semantics, concurrency/JVM, distributed data/resilience, testing/observability, then a full simulated loop.

**Plan C — Senior-to-Staff Programme (9–14 months, 848h).** Six phases; I–II are Plans A/B. III–V add JVM/performance depth, architecture depth, and Staff signal, the last requiring production evidence and external calibration rather than more reading.

All three ship in 10h/20h/30h weekly variants that change **depth of treatment**, not the topic list.

---

## Starting the programme

1. Read [`00-project/learning-roadmap.md`](00-project/learning-roadmap.md) §1 — the Day 0 diagnostic. Three hours, before reading anything else. Without a baseline the Week 6 delta is unmeasurable.
2. Start `study-packs/week-01/README.md` and proceed week by week. Each week's `MANIFEST.md` states exactly what's verified and how to reproduce it.
3. Week 6 (`study-packs/week-06/07-interview-readiness-rubric.md`) is Plan A's gate — it tells you whether to treat upcoming interviews as the target or as calibration, and how to roll into Plan B if you're not on an urgent timeline.
4. Plan B picks up at `study-packs/week-07/` — identical scope through Week 6, then broadens into Spring depth/security, Kafka, concurrency/JVM, distributed data/resilience, and testing/observability.

Prerequisites: JDK 17+ (JDK 21 used throughout so far), Docker (for every PostgreSQL lab), and a voice recorder. No Maven/Gradle install is required — every Java pack that needs a library beyond the JDK ships its own `fetch-deps.sh` pulling plain jars from Maven Central.

---

## Contribution and review workflow

Single-author repository, but the discipline is deliberate — approved weeks must stay stable so week-over-week scores remain comparable.

| Rule | Detail |
|---|---|
| **One branch per week** | `study/week-XX` |
| **Fix branches are separate** | `fix/week-01-lru-tests` — never amend an approved week on its study branch |
| **Manifest per pack** | `MANIFEST.md` listing every file and its purpose — written only once the files actually exist |
| **Validate before committing** | `scripts/validate.py` |
| **Conventional commits** | `docs:` `feat:` `fix:` `chore:` |
| **No auto-merge to `main`** | Every branch reviewed before merge |

Full detail in [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## Privacy and confidentiality

**This repository is public.** It will eventually contain drafts of professional experience, self-assessment scores, and notes derived from real interview feedback. Nothing in it today identifies an employer, client, or colleague — the four documents in `00-project/` are Notion-audit and planning artifacts about a personal study programme, not accounts of specific work. Keep it that way as content is added.

Before committing anything derived from real work, read [`CONTRIBUTING.md` § Privacy](CONTRIBUTING.md#privacy-and-sanitisation). Short version: **never commit** employer secrets · confidential client names · production credentials · internal URLs or hostnames · real customer data · proprietary source code · personal information about colleagues · interviewer-identifying detail · tokens embedded in URLs. **Anonymize instead** — `financial-services client`, `logistics platform`, `internal migration project`, `production service`, `a senior colleague`.

STAR stories will be the highest-risk content in this repository once written. Sanitize before committing, not after.

---

## Verification status

| Item | Status |
|---|---|
| `00-project/` documents | Exist, read in full, checked for secrets/PII before commit — see `00-project/file-mapping.md` |
| Notion audit methodology | Read-only; all counts and category distributions are direct SQL aggregation over the live workspace, not estimates (stated in the audit doc's own integrity section) |
| Study-pack code (Java/SQL), Weeks 1–5 | **Executed.** Every assertion count and every `EXPLAIN` block quoted in a chapter is real output from that week's `practice/` directory — see the per-week table above and each week's `MANIFEST.md` for exact reproduce commands |
| Week 6 | Deliberately has no executed code — consolidation only, stated explicitly in its `MANIFEST.md` rather than fabricating a demo that doesn't belong |
| Interview statistics | None invented anywhere in this repository — frequency estimates are explicitly labelled `[H]` heuristic, not measured |

---

## License

Copyright © 2026 Juan Melendres. All rights reserved.

No part of this repository may be reproduced, distributed, or used to create derivative works without explicit written permission from the copyright holder. This supersedes the repository's original "personal study material, not for distribution" framing — a deliberate change made once the repository's purpose shifted toward a possible future commercial release, per the 2026-09-08 decision recorded in `CHANGELOG.md`.
