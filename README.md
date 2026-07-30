# cracking-code-interviews

Structured interview preparation for **Senior Java Backend Engineer** and **Staff Engineer** roles.

> ⚠️ **Public repository.** See [Privacy and confidentiality](#privacy-and-confidentiality) before committing anything derived from real work — STAR stories, production examples, and interview feedback are the highest-risk content this repo will eventually hold.

---

## Purpose

This repository is the working system for a targeted interview-preparation programme. It exists because a Notion knowledge-base audit found the existing study material broad but shallow — a mean answer length of roughly 110 characters, 76% of rows with no code example, several technically incorrect implementations already memorized, and zero coverage of System Design, Behavioral prep, or any Java feature after Java 8.

The programme is built around one organizing idea:

> **Knowledge you cannot articulate under pressure scores zero.**

Every chapter pairs technical depth with a spoken-answer instrument: layered answers, trade-off structures, recorded drills, and scored mock interviews.

**Target roles**
- Senior Java Backend Engineer *(primary)*
- Staff Engineer *(secondary — Plan C)*

---

## Current status

| | |
|---|---|
| **Phase** | **Plan A complete (Weeks 1–6). Plan B underway, Week 9 done (Checkpoint pack built, scorecard pending self-assessment).** |
| **Completed** | Phase 1 Audit · Phase 2 Blueprint · Phase 3 Corrections · Phase 3 Roadmap (`00-project/`) · **`study-packs/week-01` through `week-09`, in full** |
| **In progress** | Plan B, Week 10 (Distributed Data + Resilience) |
| **Not started** | Plan B Weeks 11–12 · handbook chapters · interview playbook (beyond what's embedded in study packs) |
| **Topic register** | 198 topics scored across 16 domains |
| **Estimated total effort** | 1,371 hours (663 study + 708 practice) for the full register |

Weeks 1–6 total **~60,400 words** across 74 chapter files, with every technical claim backed by real, executed code rather than description alone:

| Week | Real Java | Real SQL / other |
|---|---|---|
| 1 | 18/18 assertions (LRU errata reproduced + fixed) | PostgreSQL index lab (seq scan → index scan → index-only scan) |
| 2 | 21/21 assertions (monotonic-stack errata) | Query-plan lab (3 diagnoses) + many-to-many price-history bug |
| 3 | 11/11 tree assertions + 6 real Spring transaction demos | Write-skew reproduced (REPEATABLE READ) and prevented (SERIALIZABLE) |
| 4 | 14/14 graph assertions + retry-storm, cache-stampede, fencing-token demos | Pagination lab (OFFSET ~3,000x slower than keyset at depth) |
| 5 | 23/23 assertions (Circular Queue errata fix) + idempotency-key mechanism | — |
| 6 | Consolidation — no new technical claims (by design) | — |

`handbook/` and `interview-playbook/` remain scaffolded but empty — the durable-reference extraction from study packs into `handbook/` is planned work, not yet done. See `CHANGELOG.md` for the full history and the archived-vs-real content distinction from initialization.

---

## Repository structure

```
cracking-code-interviews/
├── 00-project/             Audit, blueprint, corrections, roadmap  (the "why")
├── study-packs/            Week-by-week execution material         (the "do this week")
│   ├── week-01..06/        Plan A — complete
│   ├── week-07..09/        Plan B — complete
│   └── week-10../          Plan B — in progress
├── handbook/               Durable technical reference by domain   (the "look it up")
├── interview-playbook/     How to communicate answers              (the "say it well")
├── practice/               Runnable Java, SQL, design exercises    (the "do it")
├── flashcards/             Spaced-repetition cards
├── cheat-sheets/           Condensed pre-interview reference
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
| **`handbook/`** | *How does this actually work?* | Long-lived reference, revised as understanding deepens | Chapter on `@Transactional` semantics |
| **`interview-playbook/`** | *How do I say this out loud?* | Long-lived | The nine-layer answer stack |
| **`practice/`** | *Can I actually do it?* | Append-only | Runnable LRU cache with JUnit tests; PostgreSQL index lab |

The separation is deliberate. **Study packs are time-boxed and disposable in the sense that they are executed once**; the handbook is what remains afterward.

> **Notion is a read-only historical source.** The original knowledge base remains in Notion and is never modified by this project. It was audited, not migrated. Anything of value is rewritten here from primary sources rather than copied — see `00-project/knowledge-base-audit.md` §6 for exactly what does and doesn't survive into the handbook.

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

None. Personal study material, not for distribution or reuse.
