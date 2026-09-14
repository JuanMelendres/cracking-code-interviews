---
title: "Interview Question Bank — 20-interview-preparation"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-14
related:
  - ../INDEX.md
  - 01-computer-science-foundations.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Interview Preparation Craft

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Self-referential domain — scope note.** This domain is where the compendium itself
lives, so a deliberate scoping decision was made before mining: the 15 numbered
`behavioral/` chapters (01–15) plus `behavioral/README.md` use the Behavioral Handbook
template (What the interviewer is assessing, weak/strong answer structure, Self-Review
Checklist) and have **no `## Interview Questions` section at all** — they teach how to
construct and deliver a behavioral answer, not a quiz-style Q&A set, so there is
genuinely nothing to mine from them in this compendium's format. The remaining 6
"interview craft" chapters (`behavioral/company-loop-structures-and-question-pattern-recognition.md`,
`coding/coding-interview-communication-protocol.md`, both `system-design/` chapters,
both `technical-answers/` chapters) use the standard canonical template and were mined
normally. `mock-interviews/` and `company-prep/` are reference-only per this domain's
own `INDEX.md` and were not mined (private/non-canonical, per the domain's Phase 3
migration notes).

**Honest count for this domain:** 6 chapters yielded 12 deep questions + 16 quick-fire
questions = **28 real questions**. No Junior Fundamentals chapter — this material
targets Senior/Staff interview delivery specifically.

---

## Behavioral Interview-Day Logistics: Loop Structures and Question-Pattern Recognition

### Q1 — An interviewer asks: "How would you handle a teammate who consistently pushes back on code review feedback?" Is this a STAR question?

**Canonical treatment:** [§ Interview Questions, Q1](../behavioral/company-loop-structures-and-question-pattern-recognition.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Reflexively reaches for a STAR story because the question sounds behavioral, without checking whether it's actually asking for a memory or a live judgment call — the common mistake this question targets.
- **Senior:** Correctly identifies the hypothetical shape and responds with live reasoning rather than a forced-fit story.
- **Staff:** Explicitly distinguishes the three question shapes unprompted and explains why misreading the shape is a distinct failure mode from telling a weak story.

### Q2 — You're on interviewer 4 of a 6-round loop and realize you're about to reuse a story you already told interviewer 2. What do you do?

**Canonical treatment:** [§ Interview Questions, Q2](../behavioral/company-loop-structures-and-question-pattern-recognition.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as purely a preparation-time problem (having enough stories) without a concrete plan for tracking usage live — the common mistake this question targets.
- **Senior:** Proposes tracking story usage across the loop as the actual prevention mechanism.
- **Staff:** Names the specific mechanism (a live grid or equivalent tracking) and explains why a deliberately-reemphasized repeat is a materially different, more defensible signal than an accidental identical one.

---

## Coding Interview Communication Protocol

### Q1 — Why does stating the invariant (phase 2) have to happen before writing code, not after?

**Canonical treatment:** [§ Interview Questions, Q1](../coding/coding-interview-communication-protocol.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that explaining before coding "looks more genuine," even without the falsifiability framing — the common mistake this question targets.
- **Senior:** Explains the indistinguishable-from-memorization mechanism specifically — reasoning stated after the fact can't be distinguished from a reverse-engineered justification.
- **Staff:** Generalizes the principle beyond coding interviews — stating a plan before executing it is what allows a team to catch a bad plan before time is sunk into it.

### Q2 — A bug is found during phase 5 (testing before declaring done) versus found by the interviewer after you say "I'm done." Is the code any different? Is the outcome?

**Canonical treatment:** [§ Interview Questions, Q2](../coding/coding-interview-communication-protocol.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as purely a scoring-optics question ("catching it yourself looks better") without naming the actual skill difference being evaluated — the common mistake this question targets.
- **Senior:** Names the specific skill difference — verification discipline, not coding correctness, is what phase 5 is evaluating.
- **Staff:** Connects this to code review and production practice, and models the discipline visibly precisely because junior engineers learn the norm by watching it.

---

## System Design Narration and Whiteboard Discipline

### Q1 — Walk through how you'd draw a system design diagram for a problem you know well, narrating as you go, without being told to.

**Canonical treatment:** [§ Interview Questions, Q1](../system-design/system-design-narration-and-whiteboard-discipline.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Draws in silence and narrates in a batch afterward, with notation introduced inconsistently — the common mistake this question targets.
- **Senior:** Precedes or accompanies every box with a stated reason, and applies a consistent notation throughout without needing correction.
- **Staff:** Proactively signposts phase transitions and trade-offs, and connects diagram-sequencing choices back to the estimation phase's stated bottleneck.

### Q2 — Mid-diagram, an interviewer asks a question that reveals a real gap in your current design. What do you do?

**Canonical treatment:** [§ Interview Questions, Q2](../system-design/system-design-narration-and-whiteboard-discipline.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Defends the original design without changing anything, or discards the diagram and starts over — the common mistake this question targets.
- **Senior:** Names the gap in the interviewer's own terms, modifies the diagram in place (an annotation or small addition, not a redraw), and explains why the fix addresses the raised scenario.
- **Staff:** Uses the moment to also state the trade-off the fix introduces, rather than presenting the fix as free.

---

## System Design Interview Delivery: Time-Boxing and Mid-Round Changes

### Q1 — An interviewer changes a requirement after your architecture phase is already drawn. What's your first move?

**Canonical treatment:** [§ Interview Questions, Q1](../system-design/time-boxing-and-mid-round-changes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a new component or check near where the requirement was mentioned without revisiting anything upstream — the common mistake this question targets.
- **Senior:** Correctly identifies which specific earlier-phase decision the new requirement invalidates and revises it explicitly.
- **Staff:** Frames the response in terms of what class of future requirement change would similarly ripple back to the same decision, generalizing rather than one-off patching.

### Q2 — You're 30 minutes into a 45-minute round and still in the data-model phase. What do you do?

**Canonical treatment:** [§ Interview Questions, Q2](../system-design/time-boxing-and-mid-round-changes.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Silently runs over on one phase and discovers with two minutes left that bottlenecks were never reached — the common mistake this question targets.
- **Senior:** Explicitly narrates the time trade-off and proposes a specific cut.
- **Staff:** Frames the decision using the same reasoning as any resource-allocation trade-off — protecting time for higher-weighted phases is itself a judgment call worth stating explicitly.

---

## The Technical Answer Framework — Nine Layers

### Q1 — Deliver a full nine-layer answer for a topic you know well, unprompted.

**Canonical treatment:** [§ Interview Questions, Q1](../technical-answers/technical-answer-framework.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Rambles past the L1 30-second budget, or gives a deep dive that repeats L2 more slowly instead of adding new material — the common mistake this question targets.
- **Senior:** Reaches layers 1–7 on request, each staying within its stated time budget.
- **Staff:** Offers layer 9 (Staff extension) without being asked specifically for it, and the follow-up chain survives at least five successive follow-ups without collapsing.

### Q2 — Why is silence during the whiteboard layer (L4) specifically called out as the most common point-loss?

**Canonical treatment:** [§ Interview Questions, Q2](../technical-answers/technical-answer-framework.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that silence "looks bad," even without the "indistinguishable from uncertainty" framing — the common mistake this question targets.
- **Senior:** Explains the mechanism — the interviewer's only signal during drawing is what's said aloud, so narrating each element converts a silent process into an observable one.
- **Staff:** Connects this to a broader principle — any part of a technical answer that happens "in your head" needs to be externalized in an interview setting, because unobserved competence doesn't score.

---

## Trade-off Narration and Architecture Decision Records

### Q1 — Deliver a technical decision using the four-beat structure, unprompted.

**Canonical treatment:** [§ Interview Questions, Q1](../technical-answers/trade-off-narration-and-adrs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Stops after beat 3 (the decision and why) without beat 4 (the cost) — the single most common failure this question targets.
- **Senior:** Delivers all four beats when asked directly.
- **Staff:** Produces all four beats unprompted, without the interviewer having to ask "and what did that cost you."

### Q2 — Why does beat 4 (cost) matter more than it seems?

**Canonical treatment:** [§ Interview Questions, Q2](../technical-answers/trade-off-narration-and-adrs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats beat 4 as an optional, polite addendum rather than the load-bearing part of the answer — the common mistake this question targets.
- **Senior:** Explains why an answer with no stated cost reads as either not having considered alternatives seriously, or as sales pitching rather than engineering reasoning.
- **Staff:** Produces a genuine example where cost, not benefit, was the deciding factor — a harder, more honest answer than "we chose the option with more benefits."

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What are the three distinct behavioral-question shapes, and what does each want? | [Behavioral Interview-Day Logistics](../behavioral/company-loop-structures-and-question-pattern-recognition.md#flashcards) |
| 2 | What concrete mechanism prevents accidentally repeating the same story across a multi-round loop? | [Behavioral Interview-Day Logistics](../behavioral/company-loop-structures-and-question-pattern-recognition.md#flashcards) |
| 3 | Name the six phases of the coding interview communication protocol, in order. | [Coding Interview Communication Protocol](../coding/coding-interview-communication-protocol.md#flashcards) |
| 4 | Why does stating the invariant (phase 2) have to happen before writing code? | [Coding Interview Communication Protocol](../coding/coding-interview-communication-protocol.md#flashcards) |
| 5 | A bug found during self-testing versus found by the interviewer after "I'm done" — is the code defect different? | [Coding Interview Communication Protocol](../coding/coding-interview-communication-protocol.md#flashcards) |
| 6 | Why is a technically excellent diagram drawn in silence, then explained afterward, considered unscorable? | [System Design Narration and Whiteboard Discipline](../system-design/system-design-narration-and-whiteboard-discipline.md#flashcards) |
| 7 | Name the two opposite failure modes when an interviewer's question reveals a design gap mid-round. | [System Design Narration and Whiteboard Discipline](../system-design/system-design-narration-and-whiteboard-discipline.md#flashcards) |
| 8 | What are the two live-delivery failures distinct from knowing the six-phase method itself? | [Time-Boxing and Mid-Round Changes](../system-design/time-boxing-and-mid-round-changes.md#flashcards) |
| 9 | What distinguishes a "bolted-on patch" response to a mid-round change from a "coherent revision"? | [Time-Boxing and Mid-Round Changes](../system-design/time-boxing-and-mid-round-changes.md#flashcards) |
| 10 | Which two phases of the six-phase method should consume more than half the round's time, and why? | [Time-Boxing and Mid-Round Changes](../system-design/time-boxing-and-mid-round-changes.md#flashcards) |
| 11 | Name the nine layers of the Technical Answer Framework, in order. | [Technical Answer Framework](../technical-answers/technical-answer-framework.md#flashcards) |
| 12 | What did the audit's "mean answer length ~110 characters" finding actually indicate? | [Technical Answer Framework](../technical-answers/technical-answer-framework.md#flashcards) |
| 13 | What is the single most common point-loss during the whiteboard layer (L4)? | [Technical Answer Framework](../technical-answers/technical-answer-framework.md#flashcards) |
| 14 | Name the four beats of trade-off narration, in order. | [Trade-off Narration and ADRs](../technical-answers/trade-off-narration-and-adrs.md#flashcards) |
| 15 | Which beat does the named interview feedback specifically target? | [Trade-off Narration and ADRs](../technical-answers/trade-off-narration-and-adrs.md#flashcards) |
| 16 | What's an ADR? | [Trade-off Narration and ADRs](../technical-answers/trade-off-narration-and-adrs.md#flashcards) |

---

## Related

- [`01-computer-science-foundations.md`](01-computer-science-foundations.md)
- [`19-leadership-staff.md`](19-leadership-staff.md)
- [`18-engineering-practices.md`](18-engineering-practices.md)
- [`17-architecture.md`](17-architecture.md)
- [`16-performance-jvm.md`](16-performance-jvm.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
