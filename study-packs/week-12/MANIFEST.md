---
title: "Week 12 Study Pack — Manifest"
week: 12
plan: B
last_reviewed: 2026-07-31
---

# Week 12 Study Pack — Manifest

**Topics:** none (capstone — full loop simulation) · **Plan:** B, final week
**Files:** 9 (+ this manifest) · **Total words:** 8,510 (real count, `wc -w` over all 9 files; updated 2026-07-31 — trimmed redundant prose in the loop files' Round 3 justification bullets and pass/fail prose plus `07`'s invariants; no canonical-chapter link applicable, these files are the primary mock-interview deliverable content itself, not duplicated topic explanation, so cuts stayed shallow)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Study Pack Standard front matter + full weekly structure | 859 |
| 2 | `01-loop-1-technical-coding-design.md` | Loop 1 — 60 min, 3 rounds | 1,050 |
| 3 | `02-loop-2-technical-coding-design-behavioral.md` | Loop 2 — 75 min, 4 rounds | 1,129 |
| 4 | `03-loop-3-java-fluency-coding-production-judgment.md` | Loop 3 — 60 min, 3 rounds | 1,098 |
| 5 | `04-loop-4-final-full-loop.md` | Loop 4 — 90 min, 4 rounds, the §8.7-scored loop | 1,410 |
| 6 | `05-diagnostic-rerun.md` | Verbatim D1-D4 re-run, three-point comparison template | 602 |
| 7 | `06-final-readiness-assessment.md` | The capstone go/no-go artifact | 742 |
| 8 | `07-java-coding-practice.md` | 8 problems, all compiled and run | 1,354 |
| 9 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 266 |

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

**Second finding, caught in review before merge:** the first draft's Loop 3 and Loop 4 system-design prompts ("news feed" and "payment processing system") were claimed as fresh but actually duplicated `study-packs/week-04/08-design-exercise-news-feed.md` and `study-packs/week-05/09-design-exercise-payment-processing.md` respectively — an oversight from not cross-checking the full design-exercise inventory before writing. Fixed by replacing them with genuinely new prompts (real-time chat/messaging for Loop 3, a hotel booking system for Loop 4) that preserve the same teaching content (delivery guarantees, Saga compensating actions) without repeating a prior week's exact problem. All cross-references (README, resources.md, the design-problem tally in `06-final-readiness-assessment.md`) updated accordingly.

**Third finding, caught in review before merge:** all four loop files were missing two fields `CLAUDE.md`'s own Mock Interview Standard requires for every mock — full pass/borderline/fail signal triads and a remediation recommendations section (only partial "pass signal for a 5" snippets existed per round, no borderline/fail criteria, no remediation guidance). This mattered because the pack explicitly adopts CLAUDE.md's standards (per the user's decision, see `CLAUDE.md`'s Session Rules addendum), not just the lighter template used in Weeks 7-11. Fixed by adding a "Pass / borderline / fail signals" and a "Remediation recommendations" section to each loop file, scoped per-loop rather than per-round to keep the addition proportionate to a 3-4-round mock document.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
