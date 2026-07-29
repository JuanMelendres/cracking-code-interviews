# Why these files are archived here

These five documents were already present in this directory before it became a Git repository (2026-07-29), untracked, zero commits. They describe a fully executed, checksum-verified migration of 30 files and Java test runs ("42/42" / "37/37 assertions pass") for Week 1 and Week 2 study packs.

An exhaustive search of this machine (Spotlight by filename, Spotlight by distinctive content phrase, a `find` sweep of the home directory, and `~/.Trash`) found **no trace of any of the 26 study-pack files** these documents describe. See `../../00-project/file-mapping.md` for the full account.

They are kept here, unmodified, for provenance — not as a source of truth about repository state. Do not treat their claims (file counts, checksums, test results) as current. The real, verifiable state of the project is in `00-project/` and `CHANGELOG.md` at the repository root.

| File here | Original location | Original claim |
|---|---|---|
| `root-manifest-misplaced-week-1-content.md` | `MANIFEST.md` (repo root) | Week 1 study-pack manifest — 13 files, misfiled at root instead of `study-packs/week-01/` |
| `week-02-manifest-misplaced-content.md` | `study-packs/week-02/MANIFEST.md` | Week 2 study-pack manifest — 13 files |
| `changelog-unverified-claims.md` | `CHANGELOG.md` (repo root) | Full initialization + Phase 4/5 history |
| `file-mapping-unverified-claims.md` | `file-mapping.md` (repo root) | 30-file checksum table |
| `repository-tree-stale.md` | `repository-tree.md` (repo root) | Full tree including the nonexistent files |

Note: the four *project-level* documents (`knowledge-base-audit.md` and its three siblings) are real and their checksums in the old file-mapping table do check out against the copies now in `00-project/`. It is specifically the study-pack claims that don't correspond to anything on disk.
