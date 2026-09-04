---
title: "Architecture Decision Records and Technical Writing for Engineers"
slug: architecture-decision-records-and-technical-writing
document_type: syllabus-topic
domain: 18-engineering-practices
topic_id: T-1802
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - code-review-standards-and-practice.md
related:
  - code-review-standards-and-practice.md
  - ../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md
practice: ../../templates/
production_scenarios:
  - ../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md
interview_paths: [senior-to-staff]
official_references:
  - https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions
  - https://adr.github.io/
source_history: []
---

# Architecture Decision Records and Technical Writing for Engineers

This is **T-1802** in `18-engineering-practices`. [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) already covers the *interview-answer* form of this skill — narrating a technical decision verbally in an interview, with ADRs mentioned as "the written form of the same skill" in one paragraph. This chapter is the canonical, general engineering-practice reference that paragraph points to: how to actually write, review, and maintain ADRs and technical documents on the job, independent of any interview context.

## 1. Why This Matters

A technical decision made without a written record survives only as long as the people who made it remember it — and even then, the reasoning ("why not the other option") is usually the first thing forgotten, while the decision itself lingers in code long after. An ADR exists specifically to answer, months or years later, "why is it built this way," for a reader who wasn't in the room — and a codebase's ADR history is often the single most valuable piece of institutional memory a team has, cheaper to write once than to reconstruct from git blame and stale memory later.

## 2. Prerequisites

[Code Review: Standards and Practice](code-review-standards-and-practice.md) — ADRs and design documents get reviewed using the same feedback-quality principles as code, applied to written reasoning instead of a diff.

## 3. Foundation (L1)

**An Architecture Decision Record (ADR) is a short, dated document capturing one significant technical decision, the context that led to it, the alternatives considered, and its consequences — written at (or near) the time the decision was made, not reconstructed afterward.** The format (per Michael Nygard's original 2011 proposal, still the dominant convention) is deliberately lightweight: a handful of short sections, not a comprehensive design document.

**The standard sections are Status, Context, Decision, and Consequences** — this repository's own `templates/adr-template.md` follows exactly this structure, extended with a couple of practical additions (Decision Drivers, Considered Options, a Related section for cross-linking).

## 4. Core Concepts (L2)

**Context describes the situation neutrally, without pre-supposing the answer** — a reader six months later needs to understand *why this was even a question*, not just what was decided. A Context section that already reads as a justification for the eventual decision has usually been written backwards, after the fact, rather than at decision time.

**Considered Options must include genuinely-considered alternatives, not strawmen** — an ADR listing one obviously-inferior "alternative" alongside the chosen option isn't documenting a real decision process, it's performing one. A reviewer (Section 5) can often tell the difference from whether the alternative's own pros are stated honestly.

**Consequences must include negative consequences, not only positive ones** — every real decision trades something away, and an ADR that lists no costs has not been honestly interrogated. This is the single most commonly skipped section, and its absence is the most reliable single signal of a rushed or retroactively-justified ADR.

**Status has a real lifecycle**: proposed, accepted, rejected, deprecated, or superseded by a specific later ADR — an ADR is not a permanent, unchangeable record of truth, but a dated snapshot of the best decision given what was known and true at the time. Superseding an old ADR with a new one (rather than editing the old one in place) preserves the historical record of *why* the original decision was made, which often remains relevant even after it's no longer the current approach.

## 5. How It Works Internally (L3)

**An ADR's real value is realized at read time, not write time** — the reasoning it captures is nearly always cheaper to write once, while it's fresh, than to reconstruct later from stale memory, scattered chat history, or git archaeology. The core argument for writing ADRs at all is this asymmetry: a 15-minute investment when a decision is made can save hours of reconstruction effort for every future reader who needs to understand *why* the system is shaped the way it is — and that reader is often the original author's own future self, having genuinely forgotten the reasoning within a surprisingly short time.

**Real, mechanical review of an ADR's structural completeness is possible and cheap** — this repository's own `scripts/check_adr_completeness.py` checks that an ADR markdown file contains all four required section headings (`Status`, `Context`, `Decision`, `Consequences`), exiting non-zero and naming exactly which are missing if not. Running it against the repository's own template confirms it passes; running it against a deliberately incomplete ADR (missing `Status` and `Consequences`) correctly fails and names both missing sections — a real, mechanical floor for completeness, though it cannot check for the *quality* of what's inside each section (a Consequences section containing only positive consequences passes this specific check while still failing Section 4's honesty requirement).

**The gap between "structurally complete" and "actually honest" is exactly where review (Section 4's second and third points) does the work a script cannot** — checking for genuinely-considered alternatives and honestly-stated negative consequences requires a human reader applying judgment, not a heading-presence check.

## 6. Practical Usage

- **Write the ADR close to the decision, not weeks later** — Section 5's asymmetry argument only holds if the reasoning is still fresh; a retroactively-written ADR risks smoothing over genuine uncertainty that existed at decision time into a falsely tidy narrative.
- **Run a structural completeness check (or manually verify all four required sections exist) before treating an ADR as done** — a cheap, mechanical floor that catches an entire class of incomplete records.
- **Explicitly review an ADR's Consequences section for whether it lists any negative consequences at all** — its complete absence is the single fastest tell of an ADR that hasn't been honestly interrogated (Section 4).

## 7. Examples

Real, executed verification of this repository's own ADR-completeness checker, run against both a complete and a deliberately incomplete ADR:

```
$ python3 scripts/check_adr_completeness.py templates/adr-template.md
  PASS   templates/adr-template.md

$ python3 scripts/check_adr_completeness.py /tmp/incomplete-adr.md
  FAIL   /tmp/incomplete-adr.md: missing Status, Consequences
```

The incomplete ADR used for this test had real `Context` and `Decision` sections but no `Status` or `Consequences` — exactly the kind of decision record that states *what* was decided without stating *whether it's still current* or *what it costs*, the two omissions this chapter's Section 4 flags as most damaging.

```markdown
## Status
Proposed | Accepted | Rejected | Deprecated | Superseded by [ADR-NNN](./adr-NNN-slug.md)

## Consequences
**Positive:** <what gets easier or safer>
**Negative:** <what gets harder, more expensive, or riskier>
**Follow-up:** <a concrete action this decision obligates someone to take>
```

The exact section shape from this repository's own `templates/adr-template.md`, showing the Status lifecycle (Section 4) and the required-negative-consequences structure (Section 4) directly in the template itself.

## 8. Common Mistakes

- **Skipping the Consequences section's negative half entirely** — Section 4/7's own real evidence names this as the single most reliable tell of an under-interrogated decision.
- **Writing the ADR weeks after the decision, reconstructing reasoning from memory** — risks smoothing over genuine, real uncertainty that existed at decision time into a falsely tidy, retroactive narrative (Section 6).
- **Editing an old ADR in place to reflect a new decision**, rather than writing a new ADR that supersedes it — destroys the historical record of why the original decision was made, which often remains genuinely relevant.
- **Treating a script's structural-completeness pass as proof the ADR is good**, when it only confirms all four sections exist, not that any of them are honest or substantive (Section 5).

## 9. Edge Cases

- **A decision that was genuinely obvious, with no real alternative considered** — still worth a short ADR, since "why wasn't X considered" is a question a future reader might reasonably ask; the Context section can state directly why the space of alternatives was narrow.
- **A decision made under significant time pressure or incomplete information** — Section 4's Context section should state this explicitly rather than presenting the decision as though it were made with full information; this is itself valuable information for a future reader evaluating whether the decision should be revisited.
- **An ADR that needs updating because new information emerged**, without the decision itself having changed — a small dated addendum or a linked follow-up ADR is more honest than silently editing the original.

## 10. Performance Implications

This chapter's real, executed evidence (Section 7) demonstrates a genuine, mechanical quality gate: a completeness checker that runs in well under a second against any ADR file, catching a real class of incompleteness (missing required sections) before a reviewer's time is spent on a document that's structurally incomplete. The actual "performance" argument for ADRs generally (Section 5) is about total organizational time spent reconstructing lost context — a real, if hard-to-directly-measure, cost that a small, consistent upfront writing investment demonstrably reduces.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Writing an ADR for every significant decision | Complete institutional memory; cheap for future readers | Real, ongoing writing time; risk of "ADR fatigue" if applied to genuinely minor decisions too |
| Writing ADRs only for the most consequential decisions | Lower ongoing overhead | Risk of under-documenting a decision that seemed minor at the time but became consequential later |
| Superseding an old ADR with a new one | Preserves historical reasoning | More files to maintain than editing in place |
| A lightweight, script-checkable template | Fast to write, mechanically verifiable for completeness | Cannot verify genuine honesty/quality — only structural presence |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is writing an ADR's Context and Considered Options sections with genuine intellectual honesty — presenting the alternatives as they were actually weighed, including ones that were reasonable but ultimately rejected, rather than constructing a narrative that makes the chosen option look obviously correct in hindsight. A Senior engineer reviewing someone else's ADR should specifically probe the Consequences section for missing negatives (Section 4/8), the same discipline [Code Review's](code-review-standards-and-practice.md) own priority-by-impact principle applies to a code diff.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, Staff engineers are frequently the ones establishing or revising the ADR template and process a team or organization adopts — which means understanding *why* each required section exists (not just that it's required) is itself part of the Staff-level bar, exactly as [Trade-off Narration and ADRs'](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) own Staff-Level Discussion states for the verbal-narration form of this skill. A Staff engineer auditing an organization's existing ADR corpus for genuine decision quality (not just template compliance) — checking whether Consequences sections honestly name real costs, whether superseded ADRs are properly linked rather than silently abandoned — is doing real, high-leverage organizational work: a healthy ADR corpus is a compounding asset, and a corpus of template-compliant-but-hollow ADRs is a false sense of documented reasoning that will fail exactly when a real, high-stakes decision needs to be revisited under pressure.

## 14. Production Scenarios

- **[ADRs Asserting Decisions Without Citing Tested Evidence](../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md)** — a real, documented instance of an ADR passing structural review while still asserting a decision without the tested evidence to back it, exactly the gap between "structurally complete" and "actually honest" this chapter's Section 5 names directly.

## 15. Interview Questions

### Question 1 — What are the required sections of an ADR, and why does "Consequences" need to include negative consequences specifically?

**Why interviewers ask it.** It checks whether a candidate has actually written or reviewed real ADRs, versus knowing the format exists only abstractly — the negative-consequences requirement specifically is the detail that separates genuine practice from surface familiarity.

**Expected answer.** Status, Context, Decision, and Consequences are the standard required sections (Michael Nygard's original ADR format). Consequences must include negative consequences because every real decision trades something away — an ADR listing only positive consequences hasn't honestly interrogated the decision, and is a strong, reliable signal of either a rushed write-up or a retroactive justification rather than a genuine decision record.

**Minimum acceptable answer.** Names the four required sections, even without articulating why negative consequences specifically matter.

**Strong Senior answer.** States the negative-consequences point unprompted as the single most reliable quality signal, and can describe what a genuinely honest Consequences section looks like versus a hollow one.

**Staff-level extension.** Connects this to organizational ADR-corpus health (Section 13) — auditing a team or organization's existing ADRs specifically for this pattern as a real, high-leverage quality-improvement activity.

**Common mistakes.** Naming the sections correctly but being unable to explain why any specific one matters, revealing template-level rather than practice-level familiarity.

**Follow-up questions.** "How would you mechanically check whether an ADR meets the basic structural bar?" (This repository's own `scripts/check_adr_completeness.py`, Section 5/7 — a real, concrete example of a lightweight structural check, explicitly distinguished from a quality check.)

### Question 2 — Why should you supersede an old ADR with a new one rather than editing the original in place when a decision changes?

**Why interviewers ask it.** It tests whether the Status-lifecycle concept (Section 4) is understood as a deliberate design choice, not an arbitrary convention.

**Expected answer.** Editing the original ADR in place destroys the historical record of why the *original* decision was made — information that often remains genuinely relevant even after the decision itself has changed (understanding what was true at the time helps evaluate whether a similar future decision under similar constraints should go the same way). Marking the old ADR "Superseded by [new ADR]" and writing a new one preserves both the original reasoning and the reason for the change, as two distinct, dated records.

**Minimum acceptable answer.** States that superseding preserves history, even without articulating specifically why that history remains valuable.

**Strong Senior answer.** Gives a concrete example of when the old reasoning remains useful even after being superseded (e.g., a constraint that was true then and might become true again later, making the old ADR's reasoning directly reusable).

**Staff-level extension.** Connects this to the broader "ADR corpus as compounding institutional memory" argument (Section 13) — a superseded-but-preserved ADR chain is itself a valuable artifact showing how a system's architecture evolved and why, useful for onboarding and for evaluating whether a past decision's original constraints still hold.

**Common mistakes.** Treating an ADR as a living document to be kept perpetually up to date, rather than a dated snapshot — this conflates an ADR with ordinary reference documentation, which serves a genuinely different purpose.

**Follow-up questions.** "What if the original decision turns out to have been wrong from the start, not just outdated?" (Still worth an honest supersession rather than deletion — the record that a wrong decision was made, and why it seemed reasonable at the time, is itself valuable for avoiding the same mistake again.)

## 16. Coding/Practice Exercises

- Run `python3 scripts/check_adr_completeness.py templates/adr-template.md` yourself and confirm it passes; then delete the `## Status` heading from a copy and confirm the script correctly fails, naming exactly that missing section.
- Write a real ADR for a genuine technical decision you've made recently (or a hypothetical one for a familiar system), using `templates/adr-template.md` as the starting structure — deliberately check your own Consequences section for whether it includes a real negative consequence before considering it done.
- Find (or construct) an example ADR that passes a structural completeness check but would fail Section 4's honesty requirements (e.g., a Consequences section with only positive entries) — identify exactly which review judgment call a script cannot make.

## 17. Debugging Exercises

**Symptom:** a team's ADR corpus has grown to dozens of documents, but engineers report they still can't find the reasoning behind past decisions when they need it, and frequently end up re-deciding questions that were already settled.

**Diagnose:** check whether ADRs are being superseded correctly and cross-linked (Section 4/9) — a common cause of this exact symptom is old, outdated ADRs never being marked superseded, so a search for "why did we choose X" surfaces a stale, no-longer-current ADR with no indication that a newer decision exists, rather than being redirected to it. Separately, check whether ADRs are indexed or discoverable at all (a `templates/`-adjacent index, or consistent naming/location) — a genuinely well-written ADR that nobody can find provides none of its intended value.

## 18. Design Exercises

**Design constraint:** design a lightweight process for a 15-person engineering team to adopt ADRs for the first time, given the team currently has no written record of past architectural decisions and is skeptical that "more documentation" will actually get maintained.

Design the process around this chapter's own real, cheap enforcement mechanism (Section 5/7): require an ADR only for decisions above a stated threshold of consequence (e.g., anything affecting more than one service, anything hard to reverse), use the existing lightweight template (`templates/adr-template.md`) rather than inventing a new, heavier one, and wire the structural completeness check (`scripts/check_adr_completeness.py`) into the team's existing pull-request or review process so an incomplete ADR is caught mechanically, not left to reviewer diligence alone. State explicitly what this design deliberately does *not* attempt to automate — verifying genuinely honest Consequences and genuinely-considered alternatives (Section 5's "structurally complete vs. actually honest" gap) — and name this as an explicit review responsibility a human reviewer must own, not something the tooling can substitute for.

## 19. Further Reading

- [Documenting Architecture Decisions (Michael Nygard, 2011)](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) — the original proposal this chapter's format and this repository's own template are both built on.
- [adr.github.io](https://adr.github.io/) — a broader, community-maintained reference on ADR tooling and practice conventions.
- [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) — the interview-answer application of this same skill, verbally narrating a trade-off using the same four-beat structure this chapter's written form follows.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Name an ADR's four required sections and explain what each captures | [Section 3](#3-foundation-l1) |
| L2 | Write an ADR for a real decision that honestly states genuine alternatives and real negative consequences | [Interview Question 1](#question-1--what-are-the-required-sections-of-an-adr-and-why-does-consequences-need-to-include-negative-consequences-specifically) |
| L3 | Explain the asymmetry argument for writing ADRs at decision time, and the gap between mechanical structural completeness and genuine honesty | [Section 7's real evidence](#7-examples), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real ADR-corpus discoverability failure (Section 17), and design a lightweight, mechanically-enforced ADR adoption process for a skeptical team (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
