---
title: "Mock Interview: Collections Technical Round (45 min)"
slug: collections-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - HashMap internals and resize mechanics
  - Hash-distribution diagnosis under load
  - ConcurrentHashMap atomicity and compound operations
  - Backpressure and unbounded-queue failure modes
  - ArrayList vs LinkedList complexity trade-offs
related:
  - ../../syllabus/02-java/collections/hashmap-internals.md
  - ../../syllabus/02-java/collections/concurrenthashmap-internals.md
  - ../../syllabus/02-java/collections/blockingqueue-family.md
  - ../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md
  - ../../syllabus/02-java/collections/collection-selection-decision-matrix.md
source: ../../study-packs/week-14/08-week-14-mock-interview.md
official_references: []
---

# Mock Interview: Collections Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-14/08-week-14-mock-interview.md`.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| HashMap resize mechanics | Q1 | [HashMap Internals](../../syllabus/02-java/collections/hashmap-internals.md) |
| Hash-distribution diagnosis | Q2 | [HashMap Internals](../../syllabus/02-java/collections/hashmap-internals.md) |
| ConcurrentHashMap atomicity | Q3 | [ConcurrentHashMap Internals](../../syllabus/02-java/collections/concurrenthashmap-internals.md) |
| Backpressure / unbounded queues | Q4 | [BlockingQueue Family and Producer-Consumer](../../syllabus/02-java/collections/blockingqueue-family.md) |
| ArrayList vs LinkedList complexity | Q5, Q6 | [ArrayList and LinkedList Internals](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md) |
| Cross-topic synthesis and selection reasoning | Q6, Q7 | [Collection Selection Decision Matrix](../../syllabus/02-java/collections/collection-selection-decision-matrix.md) |

## Interviewer Opening Script

*"This is a 45-minute Collections technical round. I'll ask seven questions covering HashMap internals, concurrent collections, and the ArrayList/LinkedList trade-off — most are diagnostic, one is a whiteboard design, and the last is free-form synthesis. Think aloud throughout — I'm evaluating your reasoning process as much as the final answer. Let's start."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(7 min)** Walk through exactly what happens, mechanically, when a `HashMap` resizes — when it triggers, what the new capacity is, and what work it costs.
2. **(6 min)** Your HashMap-based cache's lookup latency is climbing even though entry count is stable. What do you check, in order?
3. **(6 min)** Your metrics dashboard undercounts under peak load but matches at low load. Diagnose it.
4. **(6 min)** Your ingestion service crashed with `OutOfMemoryError` during a downstream slowdown. What's your first suspect, and how do you fix it without just adding memory?
5. **(6 min)** Your team switched a hot-path list from `ArrayList` to `LinkedList` "for flexibility" and it got slower. Why, precisely?
6. **(6 min, whiteboard)** Design the internal collection choices for an LRU cache — sketch it, narrating the access pattern that justifies each choice.
7. **(8 min)** Free-form: pick two of this week's five topics and explain a real system where they interact (e.g., a ConcurrentHashMap whose keys are a poorly-designed custom class from last week's equals/hashCode chapter).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — HashMap resize mechanics

**Ideal answer outline:** default capacity 16, load factor 0.75, threshold 12; resize triggers once size exceeds threshold; new capacity is double the old; every entry is rehashed into the new table.
**Pass signal:** states the numbers correctly and the rehash cost.
**Fail signal:** vague ("it gets bigger when it's full") with no specific numbers or mechanism.

### Question 2 — Climbing HashMap lookup latency, stable entry count

**Ideal answer outline:** suspects the key type's hashCode() distribution; proposes inspecting bucket contents (or profiling) for evidence of overloaded/treeified buckets before assuming a sizing fix.
**Pass signal:** correctly identifies hash distribution as the likely cause, not table size.
**Fail signal:** proposes increasing capacity without diagnosing the actual cause first.

### Question 3 — Undercounting metrics dashboard

**Ideal answer outline:** identifies a likely `get()`-then-`put()` non-atomic increment pattern on a ConcurrentHashMap; proposes `merge()`/`compute()` as the fix.
**Pass signal:** correctly diagnoses the lost-update mechanism and its load-dependence.
**Fail signal:** assumes the metrics pipeline itself is at fault without examining the counting code.

### Question 4 — OOM crash during downstream slowdown

**Ideal answer outline:** suspects an unbounded internal queue absorbing backlog with no backpressure; fixes by bounding the queue and adding a timed offer/rejection policy at the boundary.
**Pass signal:** connects the crash to a specific unbounded buffer, not a vague "memory leak."
**Fail signal:** proposes only "add more memory" or "scale up" without addressing the missing backpressure.

### Question 5 — LinkedList refactor regression

**Ideal answer outline:** LinkedList.get(index) is O(n); if the hot path does indexed reads, the refactor traded away O(1) access for insertion flexibility the workload didn't actually need.
**Pass signal:** correctly states the complexity classes and connects them to the specific regression.
**Fail signal:** vague "LinkedList and ArrayList are just different" without complexity-based reasoning.

### Question 6 — Whiteboard: LRU cache design

**Ideal answer outline:** a HashMap (or ConcurrentHashMap if thread-safe) for O(1) key lookup, paired with a doubly-linked list (or LinkedHashMap's access-order mode) for O(1) move-to-front/remove-from-back — no indexed access needed anywhere.
**Pass signal:** correctly identifies both structures and the specific operations each is chosen for.
**Fail signal:** proposes a single structure that can't actually deliver both O(1) lookup and O(1) recency ordering.

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., a ConcurrentHashMap whose key type has an inconsistent hashCode(), causing silent lookup failures under concurrent load) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold as the [Java Core Technical Round](java-core-technical-round.md):

| Score | Meaning |
|---|---|
| 1 | No coherent answer, or a factually wrong one |
| 2 | Names the right topic but no working mechanism |
| 3 | Correct mechanism, Senior-level bar met |
| 4 | Correct mechanism plus one Staff-level extension |
| 5 | Correct mechanism, Staff-level extension, and a real/plausible production connection |

**Pass threshold for this mock:** average score ≥ 3.5 across all seven questions, with no individual score below 2.

## Debrief Guide

Walk the candidate through their own scores question by question, starting with the lowest. Questions 1–2 and 3 are both HashMap-family questions with genuinely different root causes (sizing mechanics vs. hash distribution vs. concurrent atomicity) — if the candidate scored low on more than one, check whether they're conflating all three into one generic "HashMap problem" instinct rather than diagnosing each independently. For Question 6, confirm the candidate explicitly named *why* neither structure alone suffices, not just that the combination "works."

## Remediation Recommendations

- Any score ≤ 2 on Q1 or Q2 → re-read [HashMap Internals](../../syllabus/02-java/collections/hashmap-internals.md) in full, including the treeification-threshold material.
- Any score ≤ 2 on Q3 → re-read [ConcurrentHashMap Internals](../../syllabus/02-java/collections/concurrenthashmap-internals.md), specifically the get-then-put non-atomicity demo.
- Any score ≤ 2 on Q4 → re-read [BlockingQueue Family and Producer-Consumer](../../syllabus/02-java/collections/blockingqueue-family.md)'s unbounded-queue production scenario.
- Any score ≤ 2 on Q5 or Q6 → re-read [ArrayList and LinkedList Internals](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md), focusing on the measured indexed-access complexity contrast.
- Weak Q7 synthesis despite individually-passing questions → re-read [Collection Selection Decision Matrix](../../syllabus/02-java/collections/collection-selection-decision-matrix.md), whose entire purpose is connecting these topics into one selection framework rather than five isolated facts.
- Below the 3.5 pass threshold overall → retake this mock in full after remediation.
