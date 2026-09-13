---
title: "LinkedList Refactor Regressing a Hot Indexed-Read Path"
document_type: production-cookbook-entry
domain: collections
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md
source: syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#production-scenarios
---

# LinkedList Refactor Regressing a Hot Indexed-Read Path

## Context

A service refactors a frequently-read, rarely-written internal list from `ArrayList` to `LinkedList`, reasoning that "LinkedList is more flexible for a list that changes." A request-handling path repeatedly reads elements from this list by index.

## Symptoms

After deployment, the request-handling path that reads by index shows a measurable latency regression.

## Impact

A refactor made for a plausible-sounding but ultimately irrelevant reason (flexibility for a list whose actual write pattern didn't need it) regresses a hot, frequently-executed code path.

## Initial Hypotheses

- An unrelated change in the same release caused the regression — checked, the list-type change is the only relevant diff.
- Increased load coincided with the deploy — checked, request volume is unchanged.
- The switch from `ArrayList` to `LinkedList` regressed the list's indexed-read performance — correct.

## Evidence

Profiling shows the majority of added CPU time is spent inside `LinkedList.get(int)`'s internal node-traversal loop, and the access pattern confirms the list is read by index far more often than it's structurally modified — exactly the profile favoring `ArrayList`.

## Investigation Timeline

1. Latency regression noticed post-deploy on the indexed-read path.
2. Unrelated-change and load-increase hypotheses ruled out via deploy diff and request-volume metrics.
3. CPU profile taken, showing time concentrated in `LinkedList.get(int)`'s traversal loop.
4. Access-pattern audit confirms indexed reads dominate over structural modifications.

## Root Cause

The refactor optimized for a property (ease of insertion/removal) that the actual access pattern barely exercised, while regressing the property (indexed read speed) the hot path actually depended on — the same O(1)-vs-O(n) gap this chapter measures directly at a 320x factor for random access.

## Immediate Mitigation

Revert to `ArrayList`.

## Permanent Fix

Establish a review norm: any `List` implementation choice must be justified against the code's actual dominant access pattern (measured or at least reasoned about explicitly), not a general intuition about which structure "sounds more flexible."

## Alternatives Considered

Keeping `LinkedList` but adding a separate index/cache for fast lookups — rejected as needless complexity solving a problem `ArrayList` already solves natively for this exact access pattern.

## Trade-offs

None — reverting to the structure that matches the actual access pattern has no downside here.

## Prevention

Treat `ArrayList` as the default choice for `List`, and require an explicit, access-pattern-based justification (frequent front/middle insertion at an already-known position, not just "might need to insert sometimes") before choosing `LinkedList` instead.

## Monitoring and Alerts

- Per-endpoint latency dashboards split by deploy version, so a list-implementation-driven regression surfaces on the next deploy's comparison rather than being absorbed into a noisy aggregate.
- A lightweight CPU profile taken automatically on any latency-regression alert, rather than only on manual investigation, to catch traversal-cost regressions this early.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent regression.

- **Situation:** a hot indexed-read path regressed in latency after an internal-list refactor.
- **Task:** confirm the regression's cause without assuming it from the diff alone.
- **Action:** ruled out unrelated-change and load-increase hypotheses via metrics; profiled the hot path and found time concentrated in `LinkedList.get(int)`'s traversal loop; confirmed the access pattern was read-by-index-dominant.
- **Result:** reverted to `ArrayList`, closing the regression, and added a review norm for future `List` choices.

## Staff-Level Discussion

The deeper issue isn't the specific `ArrayList`/`LinkedList` choice — it's that a plausible-sounding non-functional justification ("more flexible") substituted for measuring the actual access pattern. A Staff engineer's contribution is establishing the review norm this incident produced: any collection-type choice on a hot path should cite the dominant operation it's optimizing for, explicitly, rather than a general intuition. That norm generalizes past collections — it's the same discipline this program applies to any structural choice made without first checking which property the code actually exercises most.

## Related Handbook Chapters

- [ArrayList and LinkedList Internals](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md) — the canonical O(1)-vs-O(n) indexed-access measurement behind this incident.
