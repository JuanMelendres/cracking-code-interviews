---
title: "Cheat Sheet: Code Review: Standards and Practice"
slug: code-review-standards-and-practice
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1801
canonical: ../syllabus/18-engineering-practices/code-review-standards-and-practice.md
last_updated: 2026-09-06
---

# Code Review: Standards and Practice

**Canonical chapter:** [`syllabus/18-engineering-practices/code-review-standards-and-practice.md`](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Core Mental Model

Feedback should be prioritized by actual impact, not by how easy it is to spot — a reviewer who nitpicks style while missing a real correctness or security issue has inverted the review's actual priority.

## Essential Definitions

- **Code review** — having at least one other engineer read a proposed change before it merges, to catch problems before production and to spread codebase knowledge across more than one person.
- **Blocking vs. optional feedback** — reviews should distinguish must-fix issues from take-it-or-leave-it suggestions explicitly, often via a labeling convention (e.g., "blocking:", "nit:", "question:").
- **Explaining why, not just what** — "this will race under concurrent access because both threads read-then-write the same field without synchronization" is actionable; "this looks wrong" forces a costly clarifying round-trip.
- **Self-review** — reading your own diff as if you were the reviewer (ideally after stepping away briefly) before requesting review, catching a real fraction of issues before another person's time is spent.
- **PR/commit description as review artifact** — a description of *why* a change is made, not just what it does, gives a reviewer the context to evaluate whether the approach is right, not only whether the code is well-written.

## Decision Table

| Situation | Response |
|---|---|
| Comment is correctness/security/convention-violation | Label blocking |
| Comment is taste/style preference | Label nit/optional |
| Comment is ambiguous priority | Add an explicit prefix convention rather than leave it implied |
| Reviewer disagrees with overall approach, not implementation details | Escalate to synchronous conversation, not continued async threads |
| Change is large and hard to review carefully | Ask the author to split it, don't approve on a time-pressure trade |
| Time-sensitive fix can't wait for full review | Use an explicit, narrower hotfix review process, don't skip review entirely |

## Common Pitfalls

- Spending review attention on the easiest-to-spot issues (formatting, naming) while missing a genuine correctness or security concern.
- Leaving ambiguous feedback that doesn't distinguish "you must fix this" from "consider this, your call," forcing a costly clarifying round-trip.
- Approving a large, hard-to-review change because reviewing it carefully would take too long, rather than asking the author to split it.
- Treating every review comment on your own code as a personal criticism rather than feedback on the artifact.
- A reviewer's ability to hold a change's full context degrades non-linearly with size — a 500-line diff does not get half the scrutiny of two 250-line diffs; it gets far less.

## Interview Answer Skeleton

**30-sec:** Code review is having another engineer read a change before it merges, to catch problems and spread codebase knowledge. Feedback should be prioritized by impact, not ease of spotting, and blocking issues should be labeled explicitly separate from optional nits.

**2-min:** Reviews serve two purposes: correctness checking and knowledge transfer. The key discipline is explicit labeling — distinguishing blocking feedback (correctness, security, convention violations) from optional suggestions (taste, style) — so the author isn't left guessing which of ten comments must be fixed before merge. Comments should explain *why*, not just *what*, since "this will race under concurrent access because both threads read-then-write without synchronization" teaches something while "this looks wrong" wastes a round-trip. Change size has a non-linear effect on review quality: a reviewer's attention and working memory are the actual bottleneck, so a 500-line diff gets far less than half the scrutiny of two 250-line diffs reviewed separately — this is the concrete mechanism behind "keep changes small," not a stylistic preference.

**Staff-level framing:** At Staff scope, code review is a lever for organizational knowledge distribution, not just correctness — a Staff engineer's review comments across a team's changes are one of the highest-leverage ways to spread architectural knowledge without formal training. Staff engineers are often the ones establishing a team's review norms (blocking vs. optional conventions, expected turnaround time, when synchronous discussion is warranted) rather than only participating within norms someone else set, and a review culture that silently rewards fast, low-scrutiny approvals accumulates correctness and security risk invisibly until an incident surfaces it.

## Production Warning Signs

- A team's average PR review turnaround time creeping from a few hours to several days over months, without anyone deciding this was acceptable — check whether review requests are distributed evenly (a small number of "designated" reviewers becoming a bottleneck is a common cause) and whether change sizes have grown over the same period. The fix is addressing the underlying cause (redistributing load, encouraging smaller changes), not just directing people to "review faster."

## Related

- syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md
- syllabus/18-engineering-practices/refactoring-discipline.md
