# Changelog

All notable changes to this repository are documented here. Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/). This project does not use semantic versioning; entries are grouped by programme milestone.

---

## [Unreleased]

### Planned
- Phase 4 — generate the Week 1 study pack (does not exist yet; see "Known gap" below)
- Extraction of durable reference material into `handbook/` as study packs are produced
- `fix/normalize-cross-references` — the four Phase 1–3 documents cross-reference each other by their pre-normalization filenames in prose (inside backticks, not Markdown links, so nothing is broken, but the names no longer match)

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
| 1 | LRU cache `put()` evicts a valid entry on key update | Open — scheduled Week 1 |
| 2 | Top-K relies on unspecified `PriorityQueue` iteration order | Open — scheduled Week 10 (Plan B) |
| 3 | Backtracking `permute` uses `contains()`, wrong on duplicate inputs | Open — scheduled Plan B Week 7 |
| 4 | Greedy comparator `a[1]-b[1]` integer-subtraction overflow | Open |
| 5 | Suffix array presented as efficient without an O(n² log n) caveat | Open — deferred |
| 6 | Monotonic stack diagram (indices) contradicts code (values) | Open — scheduled Week 2 |
| 7 | Topological-sort diagram depicts a cycle while asserting a valid order exists | Open — scheduled Week 4 |

Also open: incorrect thread-lifecycle states (missing `TIMED_WAITING`), `volatile` reduced to "prevents caching" instead of happens-before, CMS listed without noting removal in JDK 14, inverted `Set` hierarchy diagram, `NavigableSet` miscategorized as a peer implementation, correlated-hash Bloom filter.

None of these are fixed yet — they are documented so the wrong versions get explicitly unlearned when each topic is written, per the audit's recommendation.
