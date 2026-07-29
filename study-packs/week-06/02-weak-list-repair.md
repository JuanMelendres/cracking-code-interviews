---
title: "Weak-List Repair"
week: 6
last_reviewed: 2026-07-29
---

# Weak-List Repair

**Only the items that failed Monday's retrieval pass.** For each: re-read the source chapter section (not just the flashcard answer), re-answer aloud without reading, then record yourself delivering it once. This is the one day this week where "just re-reading" is explicitly not enough — a card fails at retrieval, not at recognition, and the fix has to be practiced the same way.

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

**Why it failed:** I could define write skew but described it as "kind of
like a lost update," which is exactly the conflation the card exists to
catch — I hadn't actually internalized that they're different anomaly
classes (same-row vs cross-row).

**Source section to re-read:** study-packs/week-03/02-isolation-levels-and-write-skew.md §4, §7 Q2

**Re-answered aloud, from memory:** "A lost update is two transactions
writing to the SAME row, where one overwrites the other's change. Write
skew is two transactions each writing to a DIFFERENT row, based on a
shared read, where the combination of their two writes breaks an
invariant that spans both rows — REPEATABLE READ actually prevents lost
updates, but does NOT prevent write skew, which is exactly why it's the
more dangerous, less obvious anomaly."

**Recorded:** yes
```

## 3. Exit check

Every failed card from Monday has an entry here, and every entry has a genuine "why it failed" diagnosis — not just "didn't remember," which doesn't tell you anything actionable for Friday's diagnostic re-run. A repair with no specific failure mode identified is not a complete repair.
