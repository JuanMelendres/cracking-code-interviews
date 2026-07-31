---
title: "Week 14 Mock — Collections Technical Round (45 min)"
week: 14
document_type: study-pack-mock
status: draft
last_reviewed: 2026-07-30
---

# Week 14 Mock — Collections Technical Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

## Candidate Section

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

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Week 13's mock — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
