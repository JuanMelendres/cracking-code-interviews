---
title: "PriorityQueue Direct Iteration Displaying the Wrong Order"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/collections/priorityqueue-internals.md
source: syllabus/02-java/collections/priorityqueue-internals.md#production-scenarios
---

# PriorityQueue Direct Iteration Displaying the Wrong Order

## Context

An internal support-ticket dashboard backs its "priority queue" with `java.util.PriorityQueue<Ticket>`, and renders the queue's current contents by iterating it directly (`for (Ticket t : queue)`) to display "what's coming up next."

## Symptoms

Support staff report the displayed order doesn't match what actually gets handled first.

## Impact

Staff are misled about processing order, planning their work around a display that doesn't reflect reality — a real, user-facing correctness bug, not a cosmetic one.

## Initial Hypotheses

- A bug in ticket priority assignment — checked, priorities are assigned correctly.
- A caching/refresh-timing issue — checked, the dashboard re-queries on every load.
- The display code iterates the heap directly rather than draining it in priority order — correct.

## Evidence

The dashboard's displayed order matches the internal heap array's storage-order shape — a valid heap layout, not sorted, not random garbage either.

## Investigation Timeline

1. Staff report the "top of queue" display disagrees with actual handling order.
2. Priority-assignment and caching hypotheses ruled out.
3. Displayed order compared against a known heap-array layout, confirming it's the internal storage order, not a sorted order.

## Root Cause

`PriorityQueue`'s iteration order is the internal heap array's storage order, which only guarantees parent-before-child, not global sorted order — exactly the trap this chapter's Core Concepts section names directly.

## Immediate Mitigation

None needed beyond the fix — no data was lost or mishandled; only the display was wrong, since actual ticket processing correctly used `poll()`.

## Permanent Fix

For display purposes, copy the queue's contents into a `List` and sort the copy (or maintain a parallel sorted view), rather than iterating the live `PriorityQueue` directly — never drain the actual processing queue just to produce a display.

## Alternatives Considered

Iterating a `PriorityQueue` "carefully" to approximate sorted order — rejected, since there's no reliable way to do this without either fully sorting a copy or fully draining the original queue.

## Trade-offs

Maintaining a sorted display copy costs a real O(n log n) sort on each refresh — acceptable for a dashboard refreshed occasionally, not for a hot path.

## Prevention

Treat any code that iterates a `PriorityQueue` directly (rather than calling `poll()`/`peek()`) as a review flag by default — it's very rarely what the author actually intended.

## Monitoring and Alerts

No dedicated metric applies here — this is a correctness bug caught by user report, not a performance or availability signal. The applicable prevention is a code-review checklist item (direct `PriorityQueue` iteration), not a runtime alert.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent bug.

- **Situation:** an internal dashboard displayed ticket priority order incorrectly.
- **Task:** find why the displayed order disagreed with actual processing order.
- **Action:** ruled out priority-assignment and caching bugs; compared the displayed order against the known shape of a heap array's storage order.
- **Result:** confirmed the display iterated the live heap directly instead of draining in priority order; fixed by sorting a copy for display only.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "does iterating a `PriorityQueue` give sorted order?" — arriving as a real, user-facing production bug rather than a trivia question. The organizational lesson is that a data structure's documented iteration-order contract (or lack of one) is not optional trivia — any code that displays or otherwise surfaces a `PriorityQueue`'s contents needs an explicit review for whether it's draining (`poll()`) or merely observing (iterating) the structure, since the two produce genuinely different, easily-confused results.

## Related Handbook Chapters

- [PriorityQueue Internals](../syllabus/02-java/collections/priorityqueue-internals.md) — the canonical heap-array iteration-order mechanics behind this incident.
