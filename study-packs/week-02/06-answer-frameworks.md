---
title: "Week 2 Answer Frameworks — Nine-Layer Treatment"
week: 2
last_reviewed: 2026-07-29
---

# Week 2 Answer Frameworks

Applying the nine-layer stack from `study-packs/week-01/03-technical-answer-framework.md` §2 to this week's Deep topics: T-610, T-605, T-1505, per `00-project/learning-roadmap.md` §7.4's coverage schedule.

## Table of Contents

1. [T-610 — Query Planning](#1-t-610--query-planning)
2. [T-605 — Data Modelling](#2-t-605--data-modelling)
3. [T-1505 — Trade-off Narration](#3-t-1505--trade-off-narration)
4. [Practice Exercises](#4-practice-exercises)

---

## 1. T-610 — Query Planning

- **L1 (30s):** "`EXPLAIN ANALYZE` shows both the planner's cost estimate and the actual measured execution — the gap between estimated and actual rows is usually the fastest route to a diagnosis."
- **L2 (2 min):** add the seq-scan-vs-index-scan distinction and one real number from `01-query-planning-and-explain.md` §3 (e.g. the 21.6ms → 5.8ms function-index fix).
- **L3 (10 min):** all three scenarios from §3, the join-algorithm decision table (§4), and `Memoize`'s effect on nested-loop cost.
- **L5 (production example):** the Scenario 1 result, stated honestly — "the fix I expected to help a lot only moved the needle modestly, because the real bottleneck was elsewhere in the plan."
- **L6 (trade-offs):** forcing a join algorithm is a diagnostic tool, never a production setting (§5).
- **L7 (traps):** assuming any added index helps without checking which side of the plan actually dominates cost.
- **L8 (follow-up chain):** the `01-…` §6 questions, especially Q1 (row-mismatch diagnosis) and Q2 (`Memoize`).
- **L9 (Staff):** predicting, from plan shape alone, whether a proposed fix will actually move the needle before spending engineering time on it.

## 2. T-605 — Data Modelling

- **L1:** "A plain many-to-many join table can only record that two things are related, not any fact about the relationship — the moment a fact needs to survive independently of when the relationship formed, it needs to become an explicit entity."
- **L2:** add the quantity example and the price-history trigger, stated precisely ("as of formation time" vs. "as of read time").
- **L5 (production example):** the real executed price-history bug from `02-data-modelling-join-tables.md` §3 — a genuine, reproducible defect, not hypothetical.
- **L6:** the explicit entity costs an extra class, mapper, and join on every query touching the relationship.
- **L7 (traps):** testing only for "does the relationship have an attribute" and missing the price-history case, which has no obvious attribute until you ask "what if the referenced data changes later."
- **L8:** `02-…` §6 questions.
- **L9 (Staff):** generalizing the "as of formation time" trigger beyond order lines — permission grants, pricing agreements, versioned configuration.

## 3. T-1505 — Trade-off Narration

- **L1:** "Every technical decision I explain, I try to hit four beats: the actual constraint, the alternatives I seriously considered, the specific factor that decided it, and what it cost."
- **L2:** the worked example from `05-trade-off-narration-and-adrs.md` §3 (relational vs. document store for the catalog service).
- **L6:** this *is* the trade-off layer applied reflexively — beat 4 of the four-beat structure is L6 of the nine-layer stack, made explicit as its own named thing.
- **L7 (traps):** presenting a strawman alternative instead of a genuinely-considered one; stopping at beat 3 without beat 4.
- **L9 (Staff):** an example where cost, not benefit, was the actual deciding factor — the harder, more honest version of this answer.

## 4. Practice Exercises

Build L1, L2, and L6 in writing for T-610 and T-605 before Friday's recording (per `README.md`'s schedule) — this is the same construction discipline from Week 1, applied to this week's two Deep topics.
