---
title: "Weak-List Repair"
week: 6
last_reviewed: 2026-07-31
---

# Weak-List Repair

**Only the items that failed Monday's retrieval pass.** For each: re-read the source chapter section (not just the flashcard answer), re-answer aloud without reading, then record yourself delivering it once. A card fails at retrieval, not at recognition — the fix has to be practiced the same way, not just re-read.

## Table of Contents

1. [Repair template](#1-repair-template)
2. [Worked example](#2-worked-example)
3. [Exit check](#3-exit-check)

---

## 1. Repair template

For each failed card:

```markdown
### [Week X, Card #N]: [question]

**Why it failed:** [be specific — "forgot the mechanism," "remembered the
term but not the example," "confused it with a different week's concept"]

**Source section to re-read:** [study-packs/week-0X/0Y-chapter.md §Z]

**Re-answered aloud, from memory:** [write out what you actually said]

**Recorded:** [yes/no]
```

## 2. Worked example

```markdown
### [Week 3, Card #7]: Difference between a lost update and write skew?

**Why it failed:** Defined write skew but called it "kind of like a lost
update" — the exact conflation the card exists to catch. Hadn't
internalized they're different anomaly classes (same-row vs cross-row).

**Source section to re-read:** study-packs/week-03/02-isolation-levels-and-write-skew.md §4, §7 Q2

**Re-answered aloud, from memory:** "A lost update is two transactions
writing to the SAME row, one overwriting the other's change. Write skew
is two transactions writing to DIFFERENT rows, based on a shared read,
where the combined writes break a cross-row invariant — REPEATABLE READ
prevents lost updates but not write skew, which is why it's the more
dangerous, less obvious anomaly."

**Recorded:** yes
```

## 3. Exit check

Every failed card has an entry, and every entry names a genuine failure mode — not just "didn't remember," which gives Friday's re-run nothing actionable. No specific failure mode named means the repair isn't complete.
