---
title: "Diagnostic Re-run — Day 0 vs. Week 6 vs. Week 12"
document_type: mock-interview
week: 12
status: draft
---

# Diagnostic Re-run — Day 0 vs. Week 6 vs. Week 12

**The exact D1-D4 instrument from `00-project/learning-roadmap.md` §1, run a third time.** Week 6 already repeated it once (per that week's own README/MANIFEST); this is the final measurement point, giving a true three-point trend rather than a single before/after.

## Table of Contents

1. [Why verbatim repetition, not new questions](#why-verbatim-repetition-not-new-questions)
2. [The instrument](#the-instrument)
3. [Three-point comparison template](#three-point-comparison-template)
4. [Exit check](#exit-check)

---

## Why verbatim repetition, not new questions

The entire diagnostic value depends on holding the QUESTIONS constant so the ANSWER is the only variable — swapping in "harder" or "different" questions at Week 12 would measure difficulty drift, not actual progress. This is the same discipline as any controlled before/after measurement: change one thing at a time.

## The instrument

Reproduced verbatim from §1 — do not modify:

| # | Exercise | Time | What it measures |
|---|---|---|---|
| D1 | **Record yourself** answering 6 questions cold, 3 min each. No notes.<br>① How does HashMap work internally?<br>② What does `@Transactional` do?<br>③ When does an index not help?<br>④ Difference between hexagonal and layered architecture?<br>⑤ How do you model many-to-many?<br>⑥ Why did you choose that design? | 30m | Technical depth + **communication** |
| D2 | Solve **LC 146 (LRU Cache)** in Java, narrating aloud, 35-min limit | 40m | Java fluency, design coding, narration |
| D3 | 30-minute design: **URL shortener**, on paper, timed | 35m | Design method presence |
| D4 | Write one STAR story cold, 400 words: *a technical decision you made and why* | 25m | Story structure, quantification |

**Total time: ~2h10m.** Retain all three artifact sets (Day 0, Week 6, Week 12) — the delta across all three, not just the final score, is the actual outcome measure §1 names.

## Three-point comparison template

Fill in from your own retained artifacts — nothing here is pre-filled, per this repository's own convention against fabricating personal results:

| Exercise | Day 0 score (§8, 1-5) | Week 6 score | Week 12 score | Delta (Day 0 → Week 12) | What specifically changed |
|---|---|---|---|---|---|
| D1① HashMap internals | | | | | |
| D1② `@Transactional` | | | | | |
| D1③ Index doesn't help | | | | | |
| D1④ Hexagonal vs. layered | | | | | |
| D1⑤ Many-to-many modeling | | | | | |
| D1⑥ Design justification | | | | | |
| D2 (LC 146, coding) | | | | | |
| D3 (URL shortener design) | | | | | |
| D4 (STAR story) | | | | | |

**Watch all three D1 recordings back-to-back if you have them.** The communication-delivery change (pace, filler words, structure) is usually more visible on video than the content change — this is exactly the point §7.3 makes about recording being the fastest correction mechanism.

## Exit check

- [ ] All four exercises re-run under the original time limits, not extended for "fairness"
- [ ] Scored against §8's rubrics, not against how it felt in the moment
- [ ] Three-point comparison table filled in honestly, including any dimension that got WORSE (a real possibility if a specific weak area wasn't practiced recently — this is useful signal, not something to hide from the table)
- [ ] At least one concrete, specific "what changed" note per row — not a generic "got better"
