---
title: "T-1505/T-916 · Trade-off Narration and ADRs"
topic_id: T-1505/T-916
domain: Interview Craft
tier: Advanced
iwi: 8.10
prerequisites: []
unlocks: []
week: 2
last_reviewed: 2026-07-29
---

# T-1505 / T-916 · Trade-off Narration and Architecture Decision Records

**IWI 8.10 · Advanced tier · Runs every week from here on**

## Table of Contents

1. [Why this exists](#1-why-this-exists)
2. [The four-beat structure](#2-the-four-beat-structure)
3. [Worked example](#3-worked-example)
4. [ADRs — the written form of the same skill](#4-adrs--the-written-form-of-the-same-skill)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. Why this exists

The named interview feedback specifically included "communicating why a decision was selected" and "explaining alternatives and trade-offs." This is the highest-IWI single item in the feedback block (8.10) precisely because it's not about any one technical topic — it's the structural skill underneath every technical answer this entire programme produces. Every chapter from here forward ends with trade-offs; this chapter is where that gets turned into something rehearsed and deliverable under pressure, rather than assembled ad hoc each time.

## 2. The four-beat structure

| Beat | Content | Common failure if skipped |
|---|---|---|
| **1. Context** | The actual constraint that forced a decision — not a generic problem statement | Sounds like a textbook answer, not a real situation |
| **2. Options** | At least two genuinely considered alternatives, including their honest strengths | Presenting a strawman alternative that was never seriously considered |
| **3. Decision criterion** | The *specific reason* this option won — not just "it seemed best" | The most commonly skipped beat — an answer that stops at "we chose X" without saying why X beat Y |
| **4. What it cost** | The real trade-off accepted by choosing this option | **The beat the named feedback was specifically about** — listing only benefits, never costs |

## 3. Worked example

**Situation:** choosing between a relational database and a document store for a new service (using `04-storage-selection-tradeoffs.md`'s method as the technical input).

**Beat 1 — Context:** "We were building a catalog service that needed to support ad-hoc filtering across many product attributes, and the product schema varied significantly by category."

**Beat 2 — Options:** "We considered a document store, since the schema-per-category variation is exactly what it's good at, and a relational model with an `EAV` (entity-attribute-value) pattern to handle the variable attributes within Postgres."

**Beat 3 — Decision criterion:** "We chose the relational option specifically because the catalog needed to participate in the same transaction as inventory and pricing updates — losing that transactional guarantee to gain schema flexibility was the wrong trade for this specific service, even though the document store was a better technical fit for the attribute-variation problem in isolation."

**Beat 4 — What it cost:** "The cost is that the EAV-style attribute table is genuinely more awkward to query than a document store would have been — every attribute filter is an extra join, and we've had to build a small query-builder abstraction to keep the awkwardness out of application code."

**Why this is a complete answer:** every beat is present, beat 3 names the specific deciding factor (not "it seemed better overall"), and beat 4 is an honest, non-trivial cost — not a token concession.

## 4. ADRs — the written form of the same skill

An **Architecture Decision Record** is the four-beat structure, written down and dated, so a real decision and its reasoning survive past the meeting where it was made. Standard sections: **Context** (beat 1), **Options Considered** (beat 2), **Decision** (beat 3, including the criterion), **Consequences** (beat 4, both positive and negative). The full template and a worked example are in `10-adr-exercise.md` — this week's `ADR-001.md` deliverable is that template filled from a real decision.

## 5. Interview questions

### Q1. Deliver a technical decision using the four-beat structure, unprompted.

- **Expected answer:** all four beats present, in order, without being told the structure exists.
- **Common mistakes:** stopping after beat 3 (the decision and why) without beat 4 (the cost) — this is the single most common failure and the exact one the named feedback was about.
- **Follow-up questions:** "What would have to change for the alternative to become the right choice?"
- **Senior-level expectations:** all four beats present when asked directly.
- **Staff-level expectations:** produces all four beats *unprompted*, without the interviewer having to ask "and what did that cost you."

### Q2. Why does beat 4 (cost) matter more than it seems?

- **Expected answer:** an answer with no stated cost reads as either not having considered alternatives seriously, or as sales pitching rather than engineering reasoning — both are exactly what a Staff interview is trying to screen for.
- **Common mistakes:** treating beat 4 as an optional, polite addendum rather than the load-bearing part of the answer.
- **Follow-up questions:** "Have you ever chosen an option specifically because its cost was more acceptable than the alternative's benefit was valuable? Give an example."
- **Senior-level expectations:** explains why beat 4 matters.
- **Staff-level expectations:** produces a genuine example where cost, not benefit, was the deciding factor — this is a harder, more honest answer than the more common "we chose the option with more benefits."

## 6. Common mistakes

- Presenting only one "alternative" that was obviously never a real contender (a strawman), instead of the genuinely-considered second option.
- Skipping beat 4 entirely — the exact failure mode the named interview feedback identified.
- An ADR that reads as a justification written *after* the decision, rather than a record of the reasoning *at the time* — interviewers and reviewers can usually tell the difference from whether the "Options Considered" section has any real substance.

## 7. Staff-level discussion

At Staff scope, the four-beat structure is also the shape of a design review conversation with other engineers, not just an interview answer — a design doc or ADR that's missing beat 4 (cost) reads as either naive or as trying to sell a decision rather than document it honestly, and experienced reviewers will specifically probe for the missing cost. Staff engineers are frequently the ones *writing* the ADR template a team adopts, not just filling it in — which means understanding *why* each beat exists (not just that it exists) is itself part of the Staff-level bar.

## 8. Summary

Every technical trade-off answer should hit four beats: the real context that forced a decision, the genuinely-considered alternatives, the specific criterion that decided it, and — most commonly skipped, most valuable — what it actually cost. An ADR is this same structure, written down and dated, so the reasoning survives past the meeting where the decision was made.

## 9. Key Takeaways

- Four beats: Context → Options → Decision criterion → What it cost.
- Beat 4 (cost) is the one the named feedback was specifically about, and the one most commonly skipped.
- A real alternative, not a strawman, is required in beat 2.
- An ADR is the written, permanent form of the same four beats.

## 10. Cheat Sheet

| Beat | One-line prompt to yourself |
|---|---|
| 1. Context | "What specific constraint forced a choice here?" |
| 2. Options | "What's the strongest form of the alternative I didn't choose?" |
| 3. Decision criterion | "What one factor actually decided this?" |
| 4. What it cost | "What did I give up by choosing this?" |

## 11. Flashcards

1. **Q: Name the four beats, in order.** A: Context, Options, Decision criterion, What it cost.
2. **Q: Which beat does the named interview feedback specifically target?** A: Beat 4 — what it cost.
3. **Q: What's an ADR?** A: The four-beat structure, written down and dated — Context / Options Considered / Decision / Consequences.

(Full week-level deck: `08-flashcards.md`.)

## 12. Practice Exercises

1. Take one technical decision from your own experience. Write out all four beats explicitly, then check: is beat 2's alternative a real one, and does beat 4 name a genuine cost?
2. Redeliver `01-clean-hexagonal-architecture.md`'s Q5 answer ("would you use this on every project") using the explicit four-beat structure.

## 13. Additional Reading

- Michael Nygard, ["Documenting Architecture Decisions"](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) — the original ADR format this chapter's template follows

## 14. Official References

- [adr.github.io](https://adr.github.io/) — ADR format examples and tooling
