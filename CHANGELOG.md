# Changelog

All notable changes to this repository are documented here. Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). This project does not use semantic versioning; entries are grouped by programme milestone.

---

## [Unreleased]

### Planned
- Plan B, Weeks 8–12 (Kafka semantics, concurrency/JVM, distributed data/resilience, testing/observability, full loop simulation)
- Extraction of durable reference material into `handbook/` from the now-complete study packs
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

Also open: incorrect thread-lifecycle states (missing `TIMED_WAITING`), `volatile` reduced to "prevents caching" instead of happens-before, CMS listed without noting removal in JDK 14, inverted `Set` hierarchy diagram, `NavigableSet` miscategorized as a peer implementation, correlated-hash Bloom filter.

None of these are fixed yet — they are documented so the wrong versions get explicitly unlearned when each topic is written, per the audit's recommendation.
