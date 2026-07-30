---
title: "Week 12 Study Pack — Manifest"
week: 12
plan: B
last_reviewed: 2026-07-30
---

# Week 12 Study Pack — Manifest

**Topics:** none (capstone — full loop simulation) · **Plan:** B, final week
**Files:** 9 (+ this manifest) · **Total words:** 7,514 (real count, `wc -w` over all 9 files)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Study Pack Standard front matter + full weekly structure | 820 |
| 2 | `01-loop-1-technical-coding-design.md` | Loop 1 — 60 min, 3 rounds | 860 |
| 3 | `02-loop-2-technical-coding-design-behavioral.md` | Loop 2 — 75 min, 4 rounds | 903 |
| 4 | `03-loop-3-java-fluency-coding-production-judgment.md` | Loop 3 — 60 min, 3 rounds | 873 |
| 5 | `04-loop-4-final-full-loop.md` | Loop 4 — 90 min, 4 rounds, the §8.7-scored loop | 1,098 |
| 6 | `05-diagnostic-rerun.md` | Verbatim D1-D4 re-run, three-point comparison template | 602 |
| 7 | `06-final-readiness-assessment.md` | The capstone go/no-go artifact | 714 |
| 8 | `07-java-coding-practice.md` | 8 problems, all compiled and run | 1,380 |
| 9 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 264 |

---

## Verification

| Item | Status |
|---|---|
| Java — final-loop coding | **Executed.** OpenJDK 21.0.12. `15/15` assertions pass across all 8 problems (LC 3, 207, 56, 139, 128, 973, 55, 127), none repeating a problem number solved in Weeks 1-11. Source: `practice/java/week-12/final-loop-coding/` |
| Loop content | **Real, cross-referenced.** Every "ideal answer outline" cell cites the specific prior-week chapter and section its content is drawn from — verified against those chapters' actual real-executed numbers (e.g., Loop 1's Kafka question cites Week 8's real ISR/`acks=all` measurement; Loop 2's consistent-hashing question cites Week 10's real 92.5%-vs-9.2% figures) rather than restating the topic from memory |
| Diagnostic re-run instrument | **Verbatim reproduction** of `00-project/learning-roadmap.md` §1's D1-D4, unmodified — the entire point is holding the instrument constant |
| Final readiness assessment | **Deliberately left blank for the user to fill in** — per this repository's own convention against fabricating personal results or behavioral stories (see `CLAUDE.md`'s Prohibited Behavior section) |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

None. `CHANGELOG.md`'s errata register has no open items scoped to this week (no new topics).

## A real bug this pack's own review caught

The first draft of `practice/java/week-12/final-loop-coding/src/Main.java`'s LC 56 (Merge Intervals) test asserted `[[1,3],[6,10],[15,18]]` as the expected merged result for input `[[1,3],[2,6],[8,10],[15,18]]`. Running it surfaced the real correct answer, `[[1,6],[8,10],[15,18]]` — intervals `[1,3]` and `[2,6]` genuinely overlap and merge. The ALGORITHM was correct throughout; the TEST's hand-computed expected value was wrong. Fixed by correcting the test, re-verified. Documented in `07-java-coding-practice.md`'s Verification section as a live example of why running code beats re-reading it.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
