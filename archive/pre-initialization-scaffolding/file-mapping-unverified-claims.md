# File Mapping — Repository Initialization

Original-to-normalized filename mapping produced during repository initialization
on 2026-07-29. **Every file was copied, never moved**; originals remain untouched
at their source location. Each copy was verified by SHA-256 checksum.

**Content was not modified.** Only filenames and locations changed.

---

## Normalization rules applied

| Rule | Detail |
|---|---|
| Lowercase | `Clean-Hexagonal` → `clean-hexagonal` |
| Kebab-case | Spaces and underscores → hyphens |
| Preserve ordering prefixes | `01-`, `02-` retained inside study packs |
| Preserve version tokens | `v1.1` retained in `blueprint-v1.1-corrections.md` |
| `README.md` exception | Kept uppercase — GitHub auto-renders it in directory listings |

---

## Project documents

| Original | New path | Bytes | SHA-256 (src → dst) | Reason |
|---|---|---|---|---|
| `00-Knowledge-Base-Audit-Report.md` | `00-project/knowledge-base-audit.md` | 29,317 | ✅ `8ed489254369d610` | Project document; kebab-case; '-Report' suffix redundant with repo-spec name |
| `01-Knowledge-Architecture-Blueprint.md` | `00-project/knowledge-architecture-blueprint.md` | 93,629 | ✅ `2e5cf4e4cf2aacf8` | Project document; kebab-case; numeric prefix dropped per repo spec |
| `Blueprint-v1.1-Corrections.md` | `00-project/blueprint-v1.1-corrections.md` | 21,797 | ✅ `90f7446d6ccde8b9` | Project document; kebab-case; version token 'v1.1' preserved |
| `00-Roadmap.md` | `00-project/learning-roadmap.md` | 51,112 | ✅ `8ed9662d752b7034` | Project document; renamed to repo-spec name 'learning-roadmap.md' |

## Week 1 study pack → `study-packs/week-01/`

| Original | New filename | Bytes | Verified | Reason |
|---|---|---|---|---|
| `01-Clean-Hexagonal-Architecture.md` | `01-clean-hexagonal-architecture.md` | 35,476 | ✅ `3c2b79cf5ada74ec` | Lowercase kebab-case |
| `02-Database-Index-Fundamentals.md` | `02-database-index-fundamentals.md` | 41,084 | ✅ `43b2084b191996b8` | Lowercase kebab-case |
| `03-Technical-Answer-Framework.md` | `03-technical-answer-framework.md` | 50,931 | ✅ `90b20275b11c4643` | Lowercase kebab-case |
| `04-Coding-Interview-Communication.md` | `04-coding-interview-communication.md` | 12,347 | ✅ `a75c8cfb8e171d9c` | Lowercase kebab-case |
| `05-STAR-Story-Workbook.md` | `05-star-story-workbook.md` | 18,783 | ✅ `2ad77e81276b82d5` | Lowercase kebab-case |
| `06-Domain-Purity-Exercise.md` | `06-domain-purity-exercise.md` | 24,744 | ✅ `35463a2a5ec23abf` | Lowercase kebab-case |
| `07-Java-Coding-Practice.md` | `07-java-coding-practice.md` | 28,207 | ✅ `299020a9b6630e84` | Lowercase kebab-case |
| `08-Flashcards.md` | `08-flashcards.md` | 22,175 | ✅ `0eb122e447c5235b` | Lowercase kebab-case |
| `09-Week-1-Mock-Interview.md` | `09-week-1-mock-interview.md` | 20,972 | ✅ `91480a847efb1c60` | Lowercase kebab-case |
| `10-Week-1-Evaluation-Rubric.md` | `10-week-1-evaluation-rubric.md` | 7,485 | ✅ `44b41204af3c48ec` | Lowercase kebab-case |
| `11-Week-1-Checklist.md` | `11-week-1-checklist.md` | 9,735 | ✅ `c521cb0d75a77a43` | Lowercase kebab-case |
| `README.md` | `README.md` | 12,396 | ✅ `1626e222210a3721` | **Exception** — GitHub convention |
| `Resources.md` | `resources.md` | 10,731 | ✅ `6dce3dbdb21fd2b9` | Lowercase kebab-case |

## Week 2 study pack → `study-packs/week-02/`

| Original | New filename | Bytes | Verified | Reason |
|---|---|---|---|---|
| `01-Query-Planning-And-EXPLAIN.md` | `01-query-planning-and-explain.md` | 27,476 | ✅ `10f418ff9b8a8e59` | Lowercase kebab-case |
| `02-Data-Modelling-Join-Tables.md` | `02-data-modelling-join-tables.md` | 24,812 | ✅ `5b8cc1767c96569f` | Lowercase kebab-case |
| `03-DDD-Tactical-Aggregates.md` | `03-ddd-tactical-aggregates.md` | 24,131 | ✅ `940a0d440b9065b1` | Lowercase kebab-case |
| `04-Storage-Selection-Tradeoffs.md` | `04-storage-selection-tradeoffs.md` | 20,695 | ✅ `51fbb4d1b0a9b786` | Lowercase kebab-case |
| `05-Trade-off-Narration-And-ADRs.md` | `05-trade-off-narration-and-adrs.md` | 15,534 | ✅ `bbbf5199695d3b53` | Lowercase kebab-case |
| `06-Answer-Frameworks.md` | `06-answer-frameworks.md` | 36,423 | ✅ `5e71b139a194d7ac` | Lowercase kebab-case |
| `07-Java-Coding-Practice.md` | `07-java-coding-practice.md` | 28,320 | ✅ `f31945461f288046` | Lowercase kebab-case |
| `08-Flashcards.md` | `08-flashcards.md` | 19,482 | ✅ `0143f84775734130` | Lowercase kebab-case |
| `09-Week-2-Mock-Interview.md` | `09-week-2-mock-interview.md` | 20,220 | ✅ `7526975b6d3be346` | Lowercase kebab-case |
| `10-ADR-Exercise.md` | `10-adr-exercise.md` | 16,405 | ✅ `5fe029fc723ba417` | Lowercase kebab-case |
| `11-Week-2-Checklist.md` | `11-week-2-checklist.md` | 10,455 | ✅ `aec8f8196dffbd9c` | Lowercase kebab-case |
| `README.md` | `README.md` | 9,051 | ✅ `bdbd8620a22ee0b5` | **Exception** — GitHub convention |
| `Resources.md` | `resources.md` | 9,642 | ✅ `4b71571b7fa6e94a` | Lowercase kebab-case |

---

## Summary

- Files mapped: **30**
- Checksum-verified: **30/30**
- Total bytes: **753,567**
- Originals deleted: **none** — source directory left intact
- Content modified: **none**

## Known consequence of renaming

Documents cross-reference each other in prose using their original filenames,
inside backticks — for example ``` `03-Technical-Answer-Framework.md` ```.
These are **not** Markdown links, so no link resolution is broken and no
navigation fails. However the referenced names no longer match the files on disk.

**92 such references exist.** They were deliberately left unchanged: repository
initialization must not rewrite approved technical content. Correcting them is
scheduled for a dedicated `fix/normalize-cross-references` branch.
