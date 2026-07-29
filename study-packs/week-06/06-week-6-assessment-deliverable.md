---
title: "week-6-assessment.md Deliverable"
week: 6
last_reviewed: 2026-07-29
---

# `week-6-assessment.md` Deliverable

**Watch the Day 0 and Week 6 diagnostic recordings back-to-back. Write the comparison down.** This is the programme's single output artifact — everything else in six weeks was building toward being able to write this honestly.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# Week 6 Assessment

## D1–D4 comparison
[Pull directly from 05-diagnostic-rerun.md's comparison table. For each
artifact, one or two sentences on what specifically changed -- not
"got better," but what mechanism or structure is now present that
wasn't before.]

## Weak-list outcome
[How many cards failed Monday's retrieval pass, how many were fully
repaired by Tuesday, and whether any repaired card failed AGAIN in a
mock later in the week -- that specific recurrence is the most
important signal in this whole document.]

## Mock scores
[Both full mocks, scored against 07-interview-readiness-rubric.md.]

## Honest weakest area
[Not the area you feel best about -- the one the numbers say is
weakest. Name it plainly.]

## Plan for the weakest area before real interviews
[Specific, not "study more."]
```

## 2. Worked example

```markdown
# Week 6 Assessment (illustrative)

## D1-D4 comparison
D1.2 (@Transactional): Day 0 answer stopped at "manages transactions."
Week 6 answer includes the proxy mechanism, the self-invocation failure
mode, and the checked-exception rollback rule unprompted -- a
structural improvement, not just a longer answer.
D3 (URL shortener design): Day 0 jumped straight to "we need a hash
function and a database" with no estimation. Week 6 ran all six phases
unprompted, including stating the read:write ratio before proposing
caching -- this is the single clearest delta in the whole diagnostic.
D2 (LRU Cache): Day 0 reproduced the exact buggy put() from the
source material, unprompted, without noticing. Week 6: correct from
scratch in 14 minutes, and named the specific bug before being asked
"have you seen this fail before?"

## Weak-list outcome
9 of 72 cards failed Monday. All 9 repaired by Tuesday. One card
(Week 3, write skew vs lost update) failed AGAIN during Wednesday's
mock despite Tuesday's repair -- this is flagged below as the honest
weakest area, since a single repair session clearly didn't make it
durable.

## Mock scores
Technical+Coding mock: Technical Depth 4/5, Coding 4/5, Java Fluency 4/5.
Design+Behavioral mock: System Design 4/5, Behavioral 4/5.

## Honest weakest area
Write skew vs. lost update conflation, specifically. It failed the
initial retrieval pass, was "repaired," and failed again under mock
pressure two days later -- this pattern (fails twice) is a stronger
signal than any single-pass score and means the underlying mental
model, not just the recall, is still shaky.

## Plan for the weakest area before real interviews
Before any scheduled interview, re-derive the write-skew example from
scratch (not from memory of the flashcard answer) and re-run the real
reproduction in practice/sql/week-03/ personally, hands-on, rather than
reading the chapter a third time -- the failure pattern suggests passive
re-reading isn't the fix that worked for the other 8 repaired cards.
```

**Why this is a complete assessment:** it names a specific recurring failure (not just a list of scores), traces it to a concrete root-cause hypothesis (passive repair didn't work, hands-on reproduction might), and proposes a plan that's different from what already failed twice — not "study it again the same way."

## 3. Exit check

Your own `week-6-assessment.md` must name at least one genuinely weak area — if every dimension scored well and nothing recurred on the weak list, that itself is worth double-checking against the raw mock recordings before accepting it, since a completely clean result after six weeks of real material is the less common outcome.
