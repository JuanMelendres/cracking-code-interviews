---
title: "Week 19 — JVM Domain Full Closure"
document_type: study-pack
week: 19
status: draft
estimated_hours: 16
---

# Week 19 — JVM Domain Full Closure

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real code the six remaining JVM register topics: GC roots/reachability/reference strength, ZGC and Shenandoah concurrent collection, safepoints and stop-the-world mechanics, object layout and compressed oops, native memory and direct buffers, and escape analysis and scalar replacement — closing JVM to 12/12 register topics, the third domain in the entire register closed to full coverage, after Security (Week 17) and Testing (Week 18).

## Why This Week Matters

JVM was the first domain this program deliberately deepened (Week 16, 1/12 → 6/12) and this week finishes it. A pre-work check found T-303 (GC roots, reachability, generational fundamentals) was already bundled, untagged, into Week 9's `gc-fundamentals-and-log-analysis.md` chapter — the same undercounting pattern found in Security's T-1103 and Testing's earlier corrections — but its actual content is G1-implementation-centric, not a real treatment of GC roots or the reference-strength hierarchy, so it remained genuinely in scope this week, scoped specifically to avoid duplicating the existing chapter. These six topics span from Very High interview frequency (T-303) to Occasional (T-305, T-311, T-309) — a mix of near-guaranteed and specialist-depth questions, all real, all measured.

## Prerequisites

`syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md` (Week 9) and `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md` (Week 16) — this week's chapters explicitly build on and cross-link to both rather than re-deriving their content.

## Schedule

See `11-week-19-checklist.md` for the day-by-day breakdown.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-gc-roots-reachability-and-reference-strength.md` | T-303 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md` |
| 3 | `02-zgc-and-shenandoah-concurrent-collection.md` | T-305 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md` |
| 4 | `03-safepoints-and-stop-the-world-mechanics.md` | T-310 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md` |
| 5 | `04-object-layout-headers-and-compressed-oops.md` | T-302 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md` |
| 6 | `05-native-memory-direct-buffers-and-off-heap.md` | T-311 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md` |
| 7 | `06-escape-analysis-and-scalar-replacement.md` | T-309 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md` |
| 8 | `07-hands-on-lab.md` | 6 labs reproducing this week's real demonstrations |
| 9 | `08-flashcards.md` | 18 cards |
| 10 | `09-week-19-mock-interview.md` | 45-min JVM internals technical round |
| 11 | `10-design-exercise-jvm-tuning-for-a-market-data-service.md` | Full JVM tuning playbook for a market-data service |
| 12 | `11-week-19-checklist.md` | Day-by-day checklist |
| 13 | `resources.md` | Sources classified PRIMARY/INTERNAL/TOOL |

## Hands-On Exercises

Complete all 6 labs in `07-hands-on-lab.md` — real reference-strength clearing behavior, a real G1-vs-ZGC pause and allocation-stall comparison, real distinct safepoint-operation evidence, a real compressed-oops memory-footprint measurement, a real direct-buffer OOM plus NMT evidence, and a real 0-vs-362 GC-pause escape-analysis contrast.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

None this week in the usual LeetCode sense — JVM internals topics are runtime-behavior-shaped, not algorithm-shaped. See `07-hands-on-lab.md` for this week's hands-on equivalent.

## System Design Exercise

`10-design-exercise-jvm-tuning-for-a-market-data-service.md` — produce a full JVM tuning playbook for a latency-sensitive market-data service, applying all six of this week's topics.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`09-week-19-mock-interview.md` — 45-minute JVM internals technical round, candidate/evaluator sections hard-separated.

## Review Checklist

See `11-week-19-checklist.md`.

## Completion Criteria

- [ ] All six canonical chapters read in full
- [ ] All six labs in `07-hands-on-lab.md` reproduced with matching results
- [ ] Design exercise completed independently before checking the reference solution
- [ ] Mock interview average score ≥ 3.5

## Retrospective

Note which of the six topics needs a second pass, and whether the design exercise revealed a gap not caught by the individual chapter labs.

## Next Week

JVM coverage is now at 12/12 register topics (was 6/12 before this week) — the third domain in the entire register closed to full coverage, after Security (Week 17) and Testing (Week 18). Per `00-project/coverage-audit-2026-07-31.md`'s next-actions list, the largest remaining gaps are: the coding-problem volume shortfall (60 of a 150–170 target, still the single largest numeric gap in the program), and Phase 6 complementary deliverables (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook — none started yet, with behavioral stories and mock-interview content flagged as the cheapest wins since they already exist and just need consolidating).
