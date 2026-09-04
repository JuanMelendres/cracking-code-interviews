---
title: "Code Review: Standards and Practice"
slug: code-review-standards-and-practice
document_type: syllabus-topic
domain: 18-engineering-practices
topic_id: T-1801
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites: []
related:
  - architecture-decision-records-and-technical-writing.md
  - refactoring-discipline.md
practice: ../../practice/git/
production_scenarios:
  - ../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md
interview_paths: [senior-to-staff]
official_references:
  - https://google.github.io/eng-practices/review/
source_history: []
---

# Code Review: Standards and Practice

This is the first canonical chapter in `18-engineering-practices` beyond its git-internals seed, assigned **T-1801** in the plan's reserved `T-1800`–`T-1899` range for this domain. Code review is the highest-frequency engineering practice in this entire domain — nearly every line of production code an engineer writes passes through it — yet it's rarely taught explicitly, and its actual skill (giving feedback that improves the code without stalling the team, and receiving it without becoming defensive) is learned ad hoc, on the job, if at all.

## 1. Why This Matters

Code review is simultaneously a correctness check, a knowledge-transfer mechanism, and a team-culture signal — and doing it well or poorly compounds daily across every single change a team ships. A reviewer who nitpicks style while missing a real correctness or security issue has inverted the review's actual priority; an author who treats every comment as a personal attack, or who ignores substantive feedback to ship faster, erodes the practice's entire value over time. This is also a near-certain interview topic at Senior and Staff levels, since "how do you review code" and "tell me about giving hard feedback in a review" are both standard behavioral and technical-judgment questions.

## 2. Prerequisites

None — this is a foundational engineering-practice topic assumed by everything else in this domain.

## 3. Foundation (L1)

**Code review is the practice of having at least one other engineer read a proposed change before it merges**, checking it for correctness, clarity, and fit with the codebase's existing conventions before it becomes part of the shared, shipped system. Its two primary purposes are catching problems before they reach production and spreading knowledge of the codebase across more than one person's head.

**Feedback in a review should be prioritized by actual impact, not by how easy it is to spot.** A typo in a comment and a genuine race condition are not equally important, even though the typo is far easier to notice — a reviewer who spends their attention on the easiest-to-spot issues rather than the most consequential ones is optimizing for the wrong thing.

## 4. Core Concepts (L2)

**Reviews should distinguish blocking issues from optional suggestions explicitly**, rather than leaving an author to guess which of ten comments must be addressed before merge and which are take-it-or-leave-it style preferences. Many teams adopt an explicit prefix convention (e.g., "blocking:", "nit:", "question:") specifically to remove this ambiguity.

**A review comment should explain *why*, not just *what*.** "This will race under concurrent access because both threads read-then-write the same field without synchronization" is actionable and teaches something; "this looks wrong" forces the author to guess at the actual concern, wasting a round-trip.

**Reviewing your own diff before requesting review** (reading it as if you were the reviewer, ideally after stepping away from it briefly) catches a real, non-trivial fraction of issues before another person's time is spent on them — a cheap, disciplined habit with an outsized return.

**A PR/commit description is part of the review artifact, not an afterthought.** A description explaining *why* a change is being made (not just restating what the diff already shows) gives a reviewer the context needed to evaluate whether the approach is right, not just whether the code is well-written — this repository's own commit history (Section 7) applies this convention consistently.

## 5. How It Works Internally (L3)

**The size of a change under review has an outsized, non-linear effect on review quality.** A reviewer's ability to hold a change's full context in mind and spot subtle issues degrades faster than the change's size grows — a 500-line diff doesn't get half the scrutiny of two 250-line diffs reviewed separately; it typically gets far less than half, because the reviewer's attention and working memory are the actual bottleneck, not their available time. This is the concrete mechanism behind the standard advice to keep changes small: it's not a stylistic preference, it's a direct response to how human attention degrades with change size.

**Review latency compounds across a team in a way that's easy to underestimate.** If every review takes a day to receive feedback on, and a typical change needs two review rounds before merging, that's two days of calendar time added to every single change — multiplied across every engineer on a team, this becomes a substantial, if invisible, tax on overall throughput. Fast review turnaround (same-day, ideally within a few hours) is one of the highest-leverage, least-technical interventions a team can make to its own velocity.

**Nitpick-heavy reviews create a specific, measurable cost beyond the immediate friction**: an author who receives ten style nitpicks and one substantive concern in the same review round is likely to spend disproportionate attention on the nitpicks (since they're easy to resolve and feel like "wins") and under-attend to the substantive one — the review's own structure can accidentally bury its most important feedback under its least important feedback, unless the reviewer deliberately signals which is which (Section 4).

## 6. Practical Usage

- **Read the PR/commit description and understand the *intent* of a change before reading the diff line by line** — reviewing a diff without understanding why it exists risks approving code that's well-written but solves the wrong problem, or rejecting a reasonable approach because its rationale wasn't visible.
- **Explicitly label blocking feedback versus optional suggestions** (Section 4) in every review, removing ambiguity about what must change before merge.
- **Keep changes under review small enough to actually review carefully** — split a large change into a sequence of smaller, independently reviewable ones wherever the codebase and change shape allow it (Section 5).

## 7. Examples

Real commit messages from this repository's own history, demonstrating the "explain why, not just what" convention (Section 4) applied consistently:

```
docs: complete 03-data-structures-algorithms domain (T-2110-T-2117)
docs: continue 03-data-structures-algorithms domain (T-2106-T-2109)
docs: complete 01-computer-science-foundations domain (T-2003, T-2004, T-2005)
```

Each of this repository's own commit messages (this project's `CLAUDE.md` Change Management standard) states not just *what* changed, but *why it's grouped this way and what phase it belongs to* — exactly the context a reviewer (or, for a solo-authored repository, a future reader of the history) needs to evaluate the change's intent, not just its diff.

## 8. Common Mistakes

- **Spending review attention on the easiest-to-spot issues (formatting, naming) while missing a genuine correctness or security concern** — Section 3's priority-by-impact principle inverted.
- **Leaving ambiguous feedback that doesn't distinguish "you must fix this" from "consider this, your call"** — forces a costly clarifying round-trip.
- **Approving a large, hard-to-review change because reviewing it carefully would take too long**, rather than asking the author to split it — trades review quality for review speed in a way that defeats the review's own purpose.
- **Treating every review comment on your own code as a personal criticism** rather than as feedback on the artifact — a real, common cause of defensive, unproductive review threads.

## 9. Edge Cases

- **A review comment that's genuinely a matter of taste, not correctness** — worth explicitly labeling as such (a "nit," Section 4) so the author can make their own call without feeling obligated to match the reviewer's exact preference.
- **A reviewer who disagrees with the overall approach, not just implementation details** — this is a design-review-level disagreement, not a code-review-level one, and often needs a synchronous conversation rather than continued asynchronous comment threads, which tend to escalate rather than resolve genuine disagreements about approach.
- **A time-sensitive fix that can't wait for a full review cycle** — most teams have an explicit, narrower "hotfix" review process for exactly this case, rather than skipping review entirely under pressure.

## 10. Performance Implications

This chapter's core "performance" claim is about team throughput, not runtime — Section 5's two derivations (review-quality degradation with change size, and review-latency compounding across a team) are the two concrete, measurable levers: keeping changes small preserves review *quality*; keeping review turnaround fast preserves team *velocity*. Both are real, well-documented findings in the software engineering literature (Google's own internal engineering practices, linked in Section 19, are built directly around both principles) rather than this chapter's own claims.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Small, frequent changes under review | Higher review quality, faster turnaround per change | More total review requests, more context-switching for reviewers |
| Large, infrequent changes under review | Fewer review requests to manage | Lower review quality per Section 5's own degradation argument; harder to revert if wrong |
| Explicit blocking/nit labeling | Removes ambiguity, respects author's time | Slightly more effort per comment to categorize |
| Synchronous review discussion (pairing, a call) | Resolves genuine disagreements faster than async threads | Harder to schedule, no permanent written record unless separately captured |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is calibrating review depth to a change's actual risk — a one-line typo fix and a change to a payment-processing code path don't warrant the same review rigor, and treating every change identically either wastes reviewer time on low-risk changes or under-scrutinizes high-risk ones. A Senior reviewer also recognizes when a review comment reveals a gap in the codebase's own conventions or documentation, not just a gap in this specific author's knowledge — and addresses the systemic gap, not only the immediate instance.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, code review is a lever for organizational knowledge distribution, not just correctness: a Staff engineer's review comments on a broad range of a team's changes are one of the highest-leverage ways to spread architectural and codebase-specific knowledge without formal training sessions. Staff engineers are also frequently the ones establishing a team's review *norms* (what's blocking vs. optional, expected turnaround time, when synchronous discussion is warranted) rather than only participating within norms someone else set — and a team whose review culture silently rewards fast, low-scrutiny approvals over genuine substantive review will accumulate correctness and security risk in a way that's invisible until a real incident surfaces it.

## 14. Production Scenarios

- **[ADRs Asserting Decisions Without Citing Tested Evidence](../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md)** — a real, documented instance of review (in this case, of a design document rather than code) failing to catch an unsubstantiated claim before it became a costly, load-bearing assumption — the same review-quality-under-time-pressure failure mode this chapter's Section 5/13 discusses generally, applied to written technical decisions rather than code diffs.

## 15. Interview Questions

### Question 1 — How do you decide what feedback in a code review is blocking versus optional?

**Why interviewers ask it.** It's a direct test of the priority-by-impact and explicit-labeling principles (Section 3/4) — whether a candidate has an actual framework for this, or reviews reactively without a consistent standard.

**Expected answer.** Blocking feedback covers correctness issues, security concerns, and violations of established team/codebase conventions that would create real problems if merged as-is. Optional feedback covers matters of taste, minor style preferences, and "nice to have but not required" suggestions. Labeling each comment explicitly (a "blocking:" or "nit:" prefix, or an equivalent team convention) removes ambiguity for the author.

**Minimum acceptable answer.** Distinguishes "must fix" from "suggestion" conceptually, even without a named labeling convention.

**Strong Senior answer.** Names a specific, consistent labeling convention and can explain why the distinction matters for author trust and review-cycle speed (Section 5).

**Staff-level extension.** Connects this to team-level review-culture design (Section 13) — establishing this convention as a team norm, not just a personal practice, and monitoring whether the team's actual behavior (review turnaround time, approval rate without substantive comments) reflects a healthy balance between rigor and speed.

**Common mistakes.** Treating every comment as equally mandatory, forcing authors to either push back on minor points or accumulate unnecessary churn resolving them.

**Follow-up questions.** "What do you do when you and the author disagree about whether something is blocking?" (Section 9 — escalate to a synchronous conversation rather than an extended async back-and-forth, especially for genuine approach-level disagreements.)

### Question 2 — Why does keeping code changes small improve review quality, beyond just being faster to read?

**Why interviewers ask it.** It tests whether the size-quality relationship (Section 5) is understood as a real cognitive-load mechanism, or only as a vague "smaller is better" platitude.

**Expected answer.** A reviewer's ability to hold a change's full context and spot subtle issues degrades non-linearly as the change grows — attention and working memory are the actual bottleneck, not available time. A 500-line diff doesn't receive half the scrutiny of two 250-line diffs reviewed separately; it typically receives far less, because the reviewer's capacity to track everything relevant degrades faster than the size grows.

**Minimum acceptable answer.** States that smaller changes are easier to review, even without the specific cognitive-load mechanism.

**Strong Senior answer.** Articulates the non-linear degradation explicitly and can describe concrete techniques for splitting a naturally large change into smaller, independently reviewable pieces (e.g., separating a refactor from the feature change it enables).

**Staff-level extension.** Connects this to the compounding-latency argument (Section 5) as a second, related lever — both change size and review turnaround time affect overall team throughput, and a Staff engineer optimizing team velocity should track and address both, not just one.

**Common mistakes.** Assuming "smaller is always better" without limit — an artificially fragmented change that can't be understood in isolation (each piece depending on a not-yet-merged piece) trades one problem for a different one.

**Follow-up questions.** "How would you split a large, genuinely interdependent change into smaller reviewable pieces?" (Feature flags, or landing structural/refactoring changes separately from behavior changes — a real, common technique worth being able to describe concretely.)

## 16. Coding/Practice Exercises

- Review a real, recent pull request (your own team's, or an open-source project's) and explicitly categorize every comment left on it as blocking, nit, or question — compare your categorization against how the actual reviewer or author treated it.
- Write two versions of feedback for the same hypothetical issue (a naming choice you disagree with): one as an unlabeled, ambiguous comment, and one applying Section 4's why-not-just-what and explicit-labeling principles — compare which would actually be faster and less friction-inducing for the recipient to act on.

## 17. Debugging Exercises

**Symptom:** a team's average pull-request review turnaround time has crept from a few hours to several days over the past few months, without anyone deciding this was acceptable.

**Diagnose:** this is Section 5's compounding-latency mechanism made visible at the team level — check whether review requests are being distributed evenly (a small number of "designated" reviewers becoming a bottleneck is a common, specific cause) and whether change sizes have grown over the same period (Section 5's size-quality relationship also predicts slower reviews as changes grow, since a larger change takes longer to review carefully even at the same reviewer availability). The fix is rarely "review faster" as a directive — it's usually addressing the underlying cause directly: distributing review load more evenly, or actively encouraging smaller changes.

## 18. Design Exercises

**Design constraint:** design a code-review policy for a growing engineering team (currently 8 engineers, expected to double within a year) that must scale without either becoming a bottleneck on shipping velocity or degrading into rubber-stamp approvals.

Design the policy around this chapter's two core levers (Section 5/13) explicitly: a stated expectation for review turnaround time (e.g., first response within one business day), an explicit blocking/nit labeling convention (Section 4), and a guideline on change size (e.g., a soft target line-count, with an expectation that larger changes are split unless there's a specific, stated reason not to). State the real trade-off this design makes: some review rigor is deliberately sacrificed for velocity on small, low-risk changes (a "light touch" review tier), while high-risk changes (payment processing, security-sensitive code, public API changes) require an explicit second reviewer or a more thorough review standard — the calibrated-by-risk principle from Section 12, made into an actual, written team policy rather than left as individual judgment.

## 19. Further Reading

- [Google Engineering Practices — How to Do a Code Review](https://google.github.io/eng-practices/review/) — the widely-cited, authoritative reference this chapter's standards align with.
- [Architecture Decision Records and Technical Writing for Engineers](architecture-decision-records-and-technical-writing.md) — the written-artifact-review sibling to this chapter's code-review focus; the same feedback-quality principles apply to reviewing a design document as to reviewing a diff.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what code review is for and why feedback should be prioritized by impact | [Section 3](#3-foundation-l1) |
| L2 | Distinguish blocking from optional feedback explicitly, and write a review comment that explains why, not just what | [Interview Question 1](#question-1--how-do-you-decide-what-feedback-in-a-code-review-is-blocking-versus-optional) |
| L3 | Explain the non-linear relationship between change size and review quality, and the compounding effect of review latency on team throughput | [Section 7's real evidence](#7-examples), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real team-level review-turnaround regression to its underlying cause (Section 17), and design a scalable code-review policy calibrated by change risk (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
