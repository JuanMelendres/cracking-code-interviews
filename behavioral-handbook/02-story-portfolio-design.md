---
title: "Story Portfolio Design"
slug: story-portfolio-design
document_type: behavioral-handbook-chapter
domain: behavioral
status: draft
version: 1.0
last_updated: 2026-08-03
difficulty:
  - intermediate
target_levels:
  - senior
  - staff
prerequisites:
  - behavioral-handbook/01-star-framework-and-delivery.md
related:
  - behavioral-handbook/03-scope-impact-and-influence-framing.md
official_references: []
---

# Story Portfolio Design

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Why This Matters in Interviews](#why-this-matters-in-interviews)
- [Mental Model: A Portfolio, Not a Question List](#mental-model-a-portfolio-not-a-question-list)
- [The Competency Matrix](#the-competency-matrix)
- [Cross-Mapping to This Program's Study Packs](#cross-mapping-to-this-programs-study-packs)
- [Building the Portfolio: Sequencing](#building-the-portfolio-sequencing)
- [One Story, Multiple Competencies](#one-story-multiple-competencies)
- [Coverage Gaps in This Repository, Named Honestly](#coverage-gaps-in-this-repository-named-honestly)
- [Common Mistakes](#common-mistakes)
- [Self-Review Checklist](#self-review-checklist)
- [Summary](#summary)
- [Related](#related)

## Learning Objectives

After this chapter, you can look at any behavioral question and immediately know which prepared story to reach for, you understand why 10-14 stories is the right target size (not 3-4, not 30), and you can identify which of your own competency slots are still empty before walking into a real interview loop, not during it.

## Why This Matters in Interviews

An unprepared candidate has 2-3 stories they're comfortable telling, and they reach for the same one regardless of which question is actually asked — a candidate asked about conflict who answers with their best architecture story (reframed, awkwardly, as a conflict story) is a common and easily-spotted failure pattern. A prepared candidate has a full competency matrix, each slot filled, each story available in the three length variants from [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — and reaches for the *right* story because the question maps cleanly onto a specific, pre-identified slot.

## Mental Model: A Portfolio, Not a Question List

The wrong mental model is "memorize answers to the 50 most common behavioral questions." Real interviewers ask an effectively unbounded set of question phrasings, and memorized answers to specific phrasings collapse the moment a question doesn't match the memorized script exactly.

The right mental model: build a portfolio of 10-14 real stories, each one mapped to a *competency* (not a question phrasing). A competency — "a production incident I diagnosed and resolved," "a time I disagreed with a technical decision" — can be answered by dozens of differently-worded questions ("tell me about a time something broke in production," "walk me through a difficult debugging experience," "describe handling an outage"). One well-prepared story, told at the right scope and length, answers all of them.

## The Competency Matrix

Fifteen distinct competencies recur across Senior and Staff behavioral loops. The first two are mechanics (covered in their own chapters, not story slots); the remaining thirteen are the actual story-portfolio target.

| ID | Competency | Story slot? |
|---|---|---|
| T-1501 | STAR structure and delivery mechanics | Mechanics, not a slot — see [chapter 1](01-star-framework-and-delivery.md) |
| T-1502 | Story portfolio design (this chapter) | Mechanics, not a slot |
| T-1503 | Scope, impact, and influence framing | A *lens* applied to other stories, not its own slot — see [chapter 3](03-scope-impact-and-influence-framing.md) |
| T-1504 | Production incident diagnosed and resolved | Slot — see [Production Incident Narratives](04-production-incident-narratives.md) |
| T-1505 | Architecture or technical trade-off decision | Slot — see [Architecture Trade-off Narration](05-architecture-trade-off-narration.md) |
| T-1506 | Conflict or technical disagreement | Slot — see [Conflict and Technical Disagreement](06-conflict-and-technical-disagreement.md) |
| T-1507 | Mentoring another engineer | Slot — see [Mentoring and Developing Others](07-mentoring-and-developing-others.md) |
| T-1508 | A failure owned, with a real lesson | Slot — see [Failure and Learning Narratives](08-failure-and-learning-narratives.md) |
| T-1509 | Cross-team influence without direct authority | Slot — see [Cross-Team Influence Without Authority](09-cross-team-influence-without-authority.md) |
| T-1510 | A migration or large technical change led | Slot — see [Migrations and Large Technical Change](10-migrations-and-large-technical-change.md) |
| T-1511 | Technical debt advocacy — arguing for unglamorous work | Slot — see [Technical Debt Advocacy](11-technical-debt-advocacy.md) |
| T-1512 | A design review or RFC you drove or shaped | Slot — see [Design Reviews and RFCs](12-design-reviews-and-rfcs.md) |
| — | Scaling or performance work under real constraints | Slot — no dedicated chapter yet; the closest existing framework is [Production Incident Narratives](04-production-incident-narratives.md), though a scaling story is often proactive rather than incident-driven |
| — | Navigating genuine ambiguity or incomplete information | Slot — no dedicated chapter yet |
| — | Project or initiative recovery after it went off track | Slot — no dedicated chapter yet |

That's thirteen story-worthy competency slots — within the 10-14 target range. Two additional register topics exist alongside these but are *not* story slots: T-1513 (company-specific leadership-principle framing, e.g. Amazon's LPs — a lens for adapting existing stories to a specific company's rubric, not new material) and T-1514/T-1515 (questions to ask the interviewer, and offer negotiation — genuinely different content types, covered separately, not part of the story portfolio itself).

## Cross-Mapping to This Program's Study Packs

This program's Weeks 1-12 already reference thirteen numbered story slots by topic, introduced incrementally as the weeks progress (Stories 1-2 in Week 1, Story 3 in Week 2, and so on through Story 13 by Week 10). The table below maps this repository's existing numbering onto the canonical competency list above, so cross-references in existing mock-interview files (`study-packs/week-*/`) remain interpretable against this chapter.

| Repo story # | Introduced | Maps to competency |
|---|---|---|
| 1 | Week 1 | Architecture or technical trade-off decision (T-1505) |
| 2 | Week 1 | Conflict or technical disagreement (T-1506) |
| 3 | Week 2 | Production incident diagnosed and resolved (T-1504) |
| 4 | Week 2 | Technical debt advocacy (T-1511) |
| 5 | Week 3 | Mentoring another engineer (T-1507) |
| 6 | Week 3 | A failure owned, with a real lesson (T-1508) |
| 7 | Week 4 | Cross-team influence without direct authority (T-1509) |
| 8 | Week 4 | A migration or large technical change led (T-1510) |
| 9 | Week 7 | A design review or RFC you drove or shaped (T-1512) |
| 10 | Week 7 | Technical debt advocacy, second instance — see note below |
| 11 | Week 8 | Scaling or performance work under real constraints |
| 12 | Week 11 | Navigating genuine ambiguity or incomplete information |
| 13 | Week 10 (referenced, topic unresolved) | Project or initiative recovery — the best-fit remaining competency, but not confirmed against original source material |

**Note on Story 10:** the pre-existing Week 7 material labels this slot "technical debt advocacy," the same competency as Story 4. This may be intentional (two *different* real events that both demonstrate the same competency are legitimate and valuable — interviewers sometimes ask for a second example of the same competency type), or it may be an unresolved gap where the intended competency was never finalized. Either interpretation is honest; this chapter doesn't invent a resolution. If filling this slot, favor treating it as "project or initiative recovery" or another currently-thin competency rather than a third technical-debt story, simply for portfolio balance.

## Building the Portfolio: Sequencing

Don't try to write all thirteen stories at once. The sequencing already embedded in this program's study packs reflects a deliberate principle worth stating explicitly: **build 2 stories in depth first (Week 1), then add one new story roughly every 1-2 weeks, interleaved with technical study** — because writing all thirteen in one sitting produces shallow, interchangeable stories, while spacing the work lets the [story inventory](../study-packs/week-01/05-star-story-workbook.md) (a 20+ item brain-dump done once, up front) supply better raw material to each individual story as it's written.

## One Story, Multiple Competencies

A single real event often demonstrates more than one competency — a production incident you diagnosed (T-1504) might also have involved disagreeing with a teammate about the root cause (T-1506) and later mentoring someone through a similar diagnosis (T-1507). This is normal and doesn't mean the event only needs one slot; it means the *same underlying event* can be told with a different emphasis depending on which competency the question is actually probing. Prepare the emphasis-shift, not three entirely separate stories, when this overlap happens — trying to force every competency slot to map to a completely distinct life event produces strained, thin material for the less-common competencies.

## Coverage Gaps in This Repository, Named Honestly

As of this chapter's writing, only Stories 1 and 2 have a dedicated fill-in worksheet in this repository (`study-packs/week-01/05-star-story-workbook.md`). Stories 3-13 are referenced by topic in later weeks' mock-interview and checklist files, but no dedicated worksheet file exists for them yet — they're assumed to have been drafted by the reader off-repository, following the same S/T/A/R table structure as Stories 1 and 2. Building matching worksheets for Stories 3-13, using the same table format, is a reasonable and currently-open next step for this deliverable — flagged here rather than silently worked around, per this project's standing rule against fabricating story content that doesn't exist.

## Common Mistakes

- Building only 3-4 stories and stretching each one to cover multiple unrelated competencies under interview pressure — a story about mentoring, forced to also answer a conflict question, reads as evasive.
- Building all 10-14 stories in a single sitting rather than spacing the work, producing shallow, interchangeable material.
- Treating the competency list as a checklist to memorize question phrasings against, rather than a set of real events to have ready — see [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) for why memorized-script delivery reads worse than genuine recall.
- Leaving one or two competencies completely empty and hoping the interview loop doesn't ask about them — loops specifically probe for coverage gaps by asking multiple differently-worded questions if an early answer seems evasive or thin.

## Self-Review Checklist

- [ ] All 13 competency slots have at least a one-line real-event candidate identified (even if not fully written yet)
- [ ] No single story is being stretched to cover more than 2-3 competencies
- [ ] The portfolio was built incrementally, not in a single sitting
- [ ] Each completed story exists in all three length variants (see [chapter 1](01-star-framework-and-delivery.md))
- [ ] For any competency slot still empty, that gap is known and named, not discovered live in an interview

## Summary

A behavioral portfolio is 10-14 real stories mapped to competencies, not memorized answers to specific question phrasings — the same well-prepared story about a production incident answers a dozen differently-worded questions asking for the same underlying competency. This program's existing study packs reference thirteen numbered story slots across Weeks 1-12; this chapter maps that informal numbering onto a clean fifteen-topic canonical taxonomy (thirteen story slots plus two mechanics-only topics: scope framing and company-specific rubric adaptation). Build the portfolio incrementally, not all at once, and be honest about which slots remain genuinely empty rather than discovering the gap live in an interview.

## Related

- [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — the structure each individual story is built on.
- [Scope, Impact, and Influence Framing](03-scope-impact-and-influence-framing.md) — the technique for reframing an existing story at Staff scope, applied *across* the whole portfolio, not just one slot.
- `study-packs/week-01/05-star-story-workbook.md` — the only currently-populated worksheet (Stories 1-2); the template to replicate for Stories 3-13.
