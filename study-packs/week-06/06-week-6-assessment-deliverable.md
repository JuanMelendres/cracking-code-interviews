---
title: "week-6-assessment.md Deliverable"
week: 6
last_reviewed: 2026-07-31
---

# `week-6-assessment.md` Deliverable

**Watch the Day 0 and Week 6 diagnostic recordings back-to-back. Write the comparison down.** The programme's single output artifact — everything else in six weeks was building toward writing this honestly.

## Table of Contents

1. [Template](#1-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Template

```markdown
# Week 6 Assessment

## D1–D4 comparison
[Pull from 05-diagnostic-rerun.md's comparison table. For each artifact,
one or two sentences on what specifically changed -- not "got better,"
but what mechanism or structure is now present that wasn't before.]

## Weak-list outcome
[How many cards failed Monday, how many were repaired by Tuesday, and
whether any repaired card failed AGAIN in a later mock -- that
recurrence is the most important signal in this document.]

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
D1.2 (@Transactional): Day 0 stopped at "manages transactions." Week 6
includes the proxy mechanism, self-invocation failure mode, and
checked-exception rollback rule unprompted -- structural, not longer.
D3 (URL shortener): Day 0 jumped to "hash function and a database,"
no estimation. Week 6 ran all six phases unprompted, stating the
read:write ratio before proposing caching -- the clearest delta here.
D2 (LRU Cache): Day 0 reproduced the exact buggy put() unnoticed.
Week 6: correct from scratch in 14 minutes, named the specific bug
before being asked "have you seen this fail before?"

## Weak-list outcome
9 of 72 cards failed Monday, all repaired by Tuesday. One (Week 3,
write skew vs lost update) failed AGAIN during Wednesday's mock --
flagged below since a single repair session didn't make it durable.

## Mock scores
Technical+Coding: Technical Depth 4/5, Coding 4/5, Java Fluency 4/5.
Design+Behavioral: System Design 4/5, Behavioral 4/5.

## Honest weakest area
Write skew vs. lost update conflation. Failed the retrieval pass, was
"repaired," failed again under mock pressure two days later -- failing
twice is a stronger signal than any single-pass score: the mental
model, not just recall, is still shaky.

## Plan for the weakest area before real interviews
Before any scheduled interview, re-derive the write-skew example from
scratch and re-run the reproduction in practice/sql/week-03/ hands-on,
rather than reading the chapter again -- passive re-reading isn't what
worked for the other 8 repaired cards.
```

**Why this is complete:** names a specific recurring failure (not a score list), traces it to a root-cause hypothesis (passive repair didn't work, hands-on might), proposes a plan different from what already failed twice.

## 3. Exit check

Your own `week-6-assessment.md` must name at least one genuinely weak area — a completely clean result after six weeks of real material is the less common outcome, worth double-checking against raw recordings before accepting.
