---
title: "Week 3 Checkpoint Rubric"
week: 3
checkpoint: true
last_reviewed: 2026-07-29
---

# Week 3 Checkpoint Rubric

Six dimensions, scored 1–5, per `00-project/learning-roadmap.md` §8. **This is a pass/fail gate, not just a diagnostic** — see the pass bar at the bottom.

## Table of Contents

1. [Technical Depth](#technical-depth)
2. [Coding](#coding)
3. [System Design](#system-design)
4. [Behavioral](#behavioral)
5. [Java Fluency](#java-fluency)
6. [Production Judgment](#production-judgment)
7. [Pass bar](#pass-bar)

---

## Technical Depth

| Score | Week 3 evidence anchor |
|---|---|
| 1 | Cannot explain why self-invocation breaks `@Transactional` |
| 2 | Explains self-invocation correctly; write-skew example conflates it with a lost update |
| 3 | Correct write-skew example; cannot explain why REPEATABLE READ specifically misses it |
| **4** | **Correct write-skew example, explains why REPEATABLE READ misses it and SERIALIZABLE catches it; names at least one of the three self-invocation fixes unprompted** |
| 5 | All of the above, plus generalizes the `readOnly` driver-dependence finding to predict behavior on an unseen database/driver combination |

## Coding

| Score | Week 3 evidence anchor |
|---|---|
| 1 | Cannot produce a working BST validation |
| 2 | Working LC 98 after the local-check trap is pointed out |
| 3 | States the local-check trap unprompted, but takes >20 min to implement correctly |
| **4** | **States the trap unprompted, implements the bounds-carrying recursion correctly in ≤ 15 min, states complexity unprompted** |
| 5 | ≤ 10 min; proactively constructs their own locally-consistent-but-globally-invalid test case before being asked to verify |

## System Design

| Score | Week 3 evidence anchor |
|---|---|
| 1 | Jumps to components on the unseen problem; no six-phase structure at all |
| 2 | Some structure; estimation is decorative, not connected to later decisions |
| 3 | Follows all six phases, but architecture decisions aren't explicitly traced back to Phase 2's numbers |
| **4** | **All six phases, unprompted; at least one architectural decision explicitly justified by a Phase 2 number; names ≥ 3 bottlenecks** |
| 5 | Revises an estimation assumption live when challenged and shows the resulting architecture change |

## Behavioral

| Score | Week 3 evidence anchor |
|---|---|
| 1 | Story 5 or 6 rambles past 2:30 with no clear structure |
| 2 | Clear STAR, no quantified result |
| 3 | Clear STAR, quantified, delivered in ≤ 2 min |
| **4** | **≤ 2 min; states the specific decision criterion (four-beat structure beat 3) without being asked** |
| 5 | States what it cost (beat 4) unprompted as well — full four-beat delivery without prompting |

## Java Fluency

| Score | Week 3 evidence anchor |
|---|---|
| 1 | Cannot write a correct recursive tree traversal without significant hints |
| 2 | Correct but slow; doesn't state the recursion invariant before coding |
| 3 | States the invariant before coding on at least half of this week's problems |
| **4** | **States the invariant before coding on every problem; recognizes LC 199 as "LC 102 plus one condition" without being told** |
| 5 | Discusses the O(h) vs O(n) space trade-off between the BST-specific LCA (LC 235) and a general tree-search LCA unprompted |

## Production Judgment

| Score | Week 3 evidence anchor |
|---|---|
| 1 | No mention of connection-pool cost when discussing a long-running transaction |
| 2 | Acknowledges a cost when prompted |
| 3 | Names the pool-exhaustion mechanism when asked directly |
| **4** | **Names pool exhaustion unprompted when discussing any long-running transaction or external call inside a transaction boundary** |
| 5 | Extends this to the design round — proactively names connection-pool sizing as a bottleneck in Phase 6 of the unseen design problem, connecting Week 3's two chapters explicitly |

---

## Pass bar

**≥ 3/5 on four of six dimensions** to proceed to Week 4 as planned.

**If 4 of 6 dimensions fail:** per `README.md`, stop adding new topics. Spend Week 4 consolidating Weeks 1–3 instead, and repeat this checkpoint at the end of that consolidation week before resuming the Week 4 schedule. Adding breadth (Week 4's caching and failure-mode topics) on a foundation that hasn't cleared this checkpoint is the specific failure mode this gate exists to catch — see the roadmap's own warning on this point.
