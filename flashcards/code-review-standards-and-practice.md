---
title: "Flashcards: Code Review: Standards and Practice"
slug: code-review-standards-and-practice
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1801
canonical: ../syllabus/18-engineering-practices/code-review-standards-and-practice.md
last_updated: 2026-09-07
---

# Flashcards: Code Review: Standards and Practice

**Canonical chapter:** [`syllabus/18-engineering-practices/code-review-standards-and-practice.md`](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Priority by impact, not ease of spotting

**Prompt:**
Should a reviewer spend equal attention on a typo in a comment and a genuine race condition, since both are "issues"?

**Answer:**
No — feedback should be prioritized by actual impact, not by how easy it is to spot. A reviewer who spends attention on the easiest-to-find issues rather than the most consequential ones is optimizing for the wrong thing.

**Why it matters:**
A typo is trivially easy to notice; a race condition takes real effort to see. Reviews that reward "easy wins" systematically under-scrutinize the issues that actually matter.

**Common trap:**
Treating a long list of minor comments as evidence of a thorough review, while missing the one substantive correctness or security concern.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Blocking vs. nit labeling convention

**Prompt:**
How do disciplined review comments remove ambiguity about what an author must fix before merge?

**Answer:**
By explicitly labeling each comment — e.g. a "blocking:", "nit:", or "question:" prefix — instead of leaving the author to guess which of ten comments are mandatory and which are optional preferences.

**Why it matters:**
Ambiguous feedback forces a costly clarifying round-trip and can make an author either over-invest in minor points or under-invest in real ones.

**Common trap:**
Leaving all feedback unlabeled and uniformly worded, so a matter of taste reads with the same weight as a correctness bug.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Explain why, not just what

**Prompt:**
Why is "this will race under concurrent access because both threads read-then-write the same field without synchronization" a better review comment than "this looks wrong"?

**Answer:**
It states the actual mechanism, so it's immediately actionable and teaches something. "This looks wrong" forces the author to guess at the concern, wasting a round-trip of back-and-forth.

**Why it matters:**
Comments that explain the *why* transfer knowledge and let the author self-verify the fix, not just apply a patch to silence the reviewer.

**Common trap:**
Writing terse, verdict-only comments ("wrong", "bad name") that require a follow-up question just to understand the objection.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Change size and review quality degrade non-linearly

**Prompt:**
Does a 500-line diff get roughly half the scrutiny of two separately reviewed 250-line diffs?

**Answer:**
No — it typically gets far less than half. A reviewer's attention and working memory, not available time, are the actual bottleneck, and that capacity degrades faster than the diff's size grows.

**Why it matters:**
This is the concrete mechanism behind "keep changes small" — it isn't a style preference, it's a direct response to how human attention degrades with change size.

**Common trap:**
Assuming a large diff just takes proportionally longer to review carefully, rather than being reviewed shallowly regardless of time spent.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Review latency compounds across a team

**Prompt:**
If every review takes a day to get feedback on and a typical change needs two review rounds, what does that add to every single change — and why does it matter at team scale?

**Answer:**
Two days of calendar time per change. Multiplied across every engineer on a team, this becomes a substantial, if invisible, tax on overall throughput — making fast turnaround one of the highest-leverage, least-technical interventions a team can make.

**Why it matters:**
Slow review turnaround isn't just an individual annoyance; it's a systemic velocity cost that compounds silently.

**Common trap:**
Treating review latency as a personal scheduling problem rather than a team-wide throughput lever worth actively managing.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)

## Card: Self-review before requesting review

**Prompt:**
What cheap, disciplined habit catches a real fraction of issues before another person's review time is spent?

**Answer:**
Reviewing your own diff as if you were the reviewer — ideally after stepping away from it briefly — before requesting review from someone else.

**Why it matters:**
It's a low-cost habit with an outsized return: issues caught this way never consume a reviewer's attention at all.

**Common trap:**
Requesting review immediately after finishing a change, without ever re-reading the diff from an outside perspective.

**Related:**
[syllabus/18-engineering-practices/code-review-standards-and-practice.md](../syllabus/18-engineering-practices/code-review-standards-and-practice.md)
