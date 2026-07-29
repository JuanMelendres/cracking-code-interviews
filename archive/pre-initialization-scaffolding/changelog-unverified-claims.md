# Changelog

All notable changes to this repository are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project does not use semantic versioning; entries are grouped by programme milestone.

---

## [Unreleased]

### Planned
- Week 3 study pack — transactions, isolation levels, system design method (T-504, T-505, T-611, T-801)
- Week 3 checkpoint — first go/no-go gate
- Extraction of durable reference material from study packs into `handbook/`
- `scripts/validate.sh` — automated pre-commit validation

---

## [2026-07-29] — Repository initialization

### Added
- Repository structure: `00-project/`, `study-packs/`, `handbook/`, `interview-playbook/`,
  `practice/`, `flashcards/`, `cheat-sheets/`, `templates/`, `resources/`, `scripts/`, `archive/`
- `README.md` — purpose, structure, three-plan summary, Week 1 start instructions
- `CONTRIBUTING.md` — privacy safeguards, branching, commit conventions, validation
- `.gitignore`, `.editorconfig`, `.markdownlint.json`
- `00-project/file-mapping.md` — original-to-normalized filename mapping with checksums
- `MANIFEST.md` for Week 1 and Week 2 study packs

### Migrated (content unchanged, filenames normalized)
- 4 project documents into `00-project/`
- 13 Week 1 documents into `study-packs/week-01/`
- 13 Week 2 documents into `study-packs/week-02/`

All 30 files verified by SHA-256 checksum against their originals. No technical content
was rewritten during initialization.

### Known issues
- **92 prose cross-references are now stale.** Documents refer to each other by their
  original filenames (e.g. `` `03-Technical-Answer-Framework.md` ``) inside backticks.
  These are prose, not Markdown links, so no link resolution is broken — but the names
  no longer match. Deferred to a dedicated `fix/normalize-cross-references` branch,
  because repository initialization must not rewrite approved technical content.

---

## Programme history (pre-repository)

These milestones predate version control. Documents are in `00-project/`.

### Phase 1 — Knowledge base audit
- Audited 8 Notion assets, ~333 rows, against Senior/Staff interview requirements
- Overall quality 4.1/10; approximately 22% coverage of the target surface
- Identified 7 defective code implementations and 6 incorrect technical claims
- Flagged a privacy issue in the source material: a URL containing an expired OAuth
  token with personal data. **Reported, not modified — Notion is read-only.**

### Phase 2 — Knowledge architecture blueprint
- Defined the Interview Weight Index (IWI) across 5 weighted factors
- Scored a topic register across 16 domains
- Found all 25 highest-IWI topics to be gapped

### Phase 3 — Corrections and roadmap
- **v1.1 corrections:** 7 defects in the Phase 2 model, 3 of them self-identified
  - Topic count corrected 124 → **198**
  - Effort corrected 1,338h → **1,371h**
  - Single Return-on-Study metric split into **Knowledge RoS** and **Readiness RoS**
  - A geometric-mean composite was tested and **rejected**, with the negative result documented
  - All frequency estimates relabelled `[H]` heuristic
- **Roadmap:** three plans (A/B/C), three parallel tracks, 10/20/30h variants,
  with weeks 1–2 pre-committed to real interview feedback

### Phase 4 — Week 1 study pack
- 13 documents, ~44,000 words
- T-901 architecture · T-609 indexing · T-1601, T-1501, T-1419 communication
- Java verified on OpenJDK 21: **42/42 assertions pass**
- Includes errata drill 1 of 7 — LRU cache eviction defect, reproduced under test

### Phase 5 — Week 2 study pack
- 13 documents, ~39,000 words
- T-610 query planning · T-605/T-608 modelling · T-903 aggregates · T-617/T-811 storage
  · T-1505/T-916 trade-off narration and ADRs
- Java verified on OpenJDK 21: **37/37 assertions pass**
- Includes errata drill 6 of 7 — monotonic stack index-vs-value defect

---

## Errata tracking

Defects found in the original knowledge base, and where each is corrected.

| # | Defect | Status |
|---|---|---|
| 1 | LRU cache `put()` evicts a valid entry on key update | ✅ Week 1 |
| 6 | Monotonic stack stores values where indices are required | ✅ Week 2 |
| 7 | Topological sort diagram depicts a cycle | Week 4 |
| 2 | Top-K relies on unspecified `PriorityQueue` iteration order | Week 10 |
| 3 | Backtracking `permute` uses `contains()`, wrong on duplicates | Plan B Week 7 |
| 4 | Greedy comparator `a[1]-b[1]` overflows | Later |
| 5 | Suffix array presented as efficient without caveat | Deferred (Expert tier) |

Incorrect claims: thread lifecycle states, `volatile` semantics, CMS removal in JDK 14,
`Set` hierarchy inversion, `NavigableSet` misclassification, Bloom filter correlated hashes.
Scheduled for Plan B Week 9.
