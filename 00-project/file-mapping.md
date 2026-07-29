# File Mapping — Repository Initialization

Produced 2026-07-29 when this directory was first turned into a Git repository. Every mapped file was **copied**, never moved; sources remain untouched at their original location. Each copy below was verified byte-identical to its source via SHA-256, computed at initialization time — see the checksums.

## Normalization rules applied

| Rule | Detail |
|---|---|
| Lowercase | `Knowledge-Base-Audit` → `knowledge-base-audit` |
| Kebab-case | Spaces and underscores → hyphens |
| Numeric prefix dropped | `00-`, `01-` removed per the target repo spec's naming |
| Preserve version tokens | `v1.1` retained in `blueprint-v1.1-corrections.md` |

## Project documents

Source: `~/Downloads/Java-Interview-Handbook/` (four Markdown files generated during a prior session's Notion audit / roadmap work — this is the only location on disk where they were found).

| Original | New path | SHA-256 (source = destination) |
|---|---|---|
| `00-Knowledge-Base-Audit-Report.md` | `00-project/knowledge-base-audit.md` | `8ed489254369d610b2af53654e2c82148ddac9c6c5f0d8e15e86881a6fa6ce28` |
| `01-Knowledge-Architecture-Blueprint.md` | `00-project/knowledge-architecture-blueprint.md` | `2e5cf4e4cf2aacf881e2026bfd5c67df6be340569565b2042ced33d3312bd7b0` |
| `Blueprint-v1.1-Corrections.md` | `00-project/blueprint-v1.1-corrections.md` | `90f7446d6ccde8b91e7702b4a4f6869f2ff6573a954ad02a75747d9a76753faa` |
| `00-Roadmap.md` | `00-project/learning-roadmap.md` | `8ed9662d752b7034fd109d0dc919773c0758c77ac0c86c3824079369a6dd452b` |

Checksums verified by direct `shasum -a 256` comparison at copy time, in this session — not carried forward from any prior claim.

## Everything else moved during initialization

| Original | New path | Reason |
|---|---|---|
| `validate.py` (repo root) | `scripts/validate.py` | Script already assumed a `scripts/` location internally (`ROOT = dirname(dirname(__file__))`); no logic changes needed |

## Explicitly NOT migrated — see `archive/pre-initialization-scaffolding/`

Before this initialization, the repository root already contained a `MANIFEST.md`, `CHANGELOG.md`, `file-mapping.md`, and `repository-tree.md`, plus `study-packs/week-02/MANIFEST.md`. These documents asserted a fully executed migration of **30 files** (Week 1 + Week 2 study packs, 13 files each) with individual SHA-256 checksums and a claim that their Java code was "executed on OpenJDK 21: 42/42 and 37/37 assertions pass."

**None of those 30 study-pack files exist anywhere on this machine.** This was checked exhaustively: Spotlight name search, Spotlight content search on distinctive phrases from the manifests (e.g. "errata drill", "LRU cache eviction"), a `find` sweep of the home directory for filename fragments (`hexagonal`, `star-story`, etc.), and a check of `~/.Trash`. Nothing matched except the meta-documents themselves and unrelated content (an unconnected generic FAANG study-pack PDF from a different task).

The four project-level documents' checksums in the old `file-mapping.md` do check out against the real files in `~/Downloads/` — so that part of the record is accurate. The 26 study-pack file claims, and the corresponding "copied + checksum-verified + executed" language in `CHANGELOG.md` and both `MANIFEST.md` files, are not backed by anything on disk. Whatever produced those documents did not persist the work it described.

**Action taken:** the four stale documents were moved (not deleted) to `archive/pre-initialization-scaffolding/` for provenance, alongside a note (`archive/pre-initialization-scaffolding/NOTES.md`) explaining why. New `README.md` and `CHANGELOG.md` were written reflecting only what is actually verifiable: the four Phase 1–3 documents exist and are real; Phase 4 (study packs) has not started.
