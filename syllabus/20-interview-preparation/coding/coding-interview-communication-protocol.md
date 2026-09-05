---
title: "Coding Interview Communication Protocol"
slug: coding-interview-communication-protocol
document_type: playbook-technical-answer
domain: 20-interview-preparation/coding
status: draft
version: 1.0
last_updated: 2026-09-04
source_history:
  - interview-playbook/coding/coding-interview-communication-protocol.md
topic_id: T-1419
mastery_levels_covered:
  - L1
  - L2
  - L3
  - L4
difficulty:
  - foundational
  - intermediate
target_levels:
  - senior
  - staff
estimated_reading_minutes: 15
prerequisites: []
related:
  - ../technical-answers/technical-answer-framework.md
  - ../../21-frontend-web/frontend-live-coding-and-debugging-protocol.md
  - ../../../study-packs/week-01/04-coding-interview-communication.md
official_references: []
---

# Coding Interview Communication Protocol

**Canonical location:** `interview-playbook/coding/`

> **Topic register:** T-1419 · Core tier · Near-Certain interview frequency [H] — runs every coding session, every week of a study programme, and every live coding interview

## Table of Contents

1. [Why This Exists](#why-this-exists)
2. [Level 1 — Foundation](#level-1--foundation)
3. [Level 2 — Working Knowledge](#level-2--working-knowledge)
4. [The Six-Phase Protocol](#the-six-phase-protocol)
5. [Illustrative Failure Patterns](#illustrative-failure-patterns)
6. [Common Mistakes](#common-mistakes)
7. [Staff-Level Discussion](#staff-level-discussion)
8. [Interview Questions](#interview-questions)
9. [Summary](#summary)
10. [Key Takeaways](#key-takeaways)
11. [Cheat Sheet](#cheat-sheet)
12. [Flashcards](#flashcards)
13. [Practice Exercises](#practice-exercises)

---

## Why This Exists

A correct solution delivered in silence scores lower than a correct solution narrated, because the interviewer is evaluating a process they can only see if you describe it: how you clarify ambiguity, how you choose an approach, how you reason about correctness before running anything. Silence forces the interviewer to guess whether you got lucky or reasoned your way there — and Staff-level loops explicitly weight the reasoning over the final answer. This protocol externalizes that reasoning into six checkpoints, run on every coding problem, in every session, so the narration is a rehearsed habit by interview day rather than something invented under pressure.

## Level 1 — Foundation

A driving instructor's "commentary drive" test has the student narrate every observation and decision out loud while driving — "checking mirror, slowing for the pedestrian, indicating right" — specifically so the instructor sitting silently in the passenger seat can evaluate the reasoning behind each action, not just whether the car arrived safely. A student who drives perfectly in total silence gives the instructor nothing to grade except the outcome; a student who narrates continuously gives the instructor the entire decision process to evaluate, mistake or not. The six-phase protocol is a commentary drive for coding interviews: the interviewer can only score what you say out loud, not what you silently got right.

## Level 2 — Working Knowledge

At this level, the working discipline is the same one a commentary-drive examiner is trained to listen for: reasoning stated before the action, not after. A student who brakes for a pedestrian and only explains "I saw a pedestrian" once safely stopped has given the examiner nothing to evaluate in the moment that mattered; a student narrating "checking mirror, pedestrian ahead, easing off the accelerator" before braking gives the examiner a real-time reasoning trail. Phase 2 of this protocol (stating the invariant before coding) works identically — reasoning given after the code exists is retroactive and unfalsifiable, indistinguishable from having memorized the route.

The working test for phase 5 (testing before declaring done) is the commentary-drive equivalent of checking your blind spot before changing lanes rather than after a near-miss reveals you should have: a self-caught issue during a declared check reads as competence; the identical issue caught only because someone else noticed reads as a real gap in habit, even though the underlying skill might be the same. Practice narrating every phase until it's as automatic as a trained driver's habitual mirror-check — not something invented under the pressure of the actual test.

## The Six-Phase Protocol

| Phase | What happens | What to say |
|---|---|---|
| 1. Clarify | Confirm input shape, constraints, edge cases, and what "correct" means before writing anything | "Can the array be empty? Are duplicates possible? Should I optimize for time or space if they trade off?" |
| 2. State the invariant | Name the approach and *why* it applies, before coding | "This is a sliding-window problem because we need a contiguous run and the condition is monotonic as the window grows." |
| 3. Complexity, upfront | State expected time/space before writing code, not after | "This should be O(n) time, O(k) space for the window." |
| 4. Narrate while coding | Say what each block does as you write it, not after | "Advancing the right pointer, updating the count map." |
| 5. Test before declaring done | Walk at least one example by hand, including an edge case | "Let me trace this on the empty-string case before I say I'm done." |
| 6. State complexity, again, matching the code | Confirm the actual complexity matches what was predicted in phase 3 | "This came out O(n) time, O(min(n, charset size)) space, matching what I said." |

The phases run in order on every problem, not selectively. Phase 6 exists specifically to catch a mismatch between the phase-3 prediction and what the code actually does — a discrepancy there is itself a valuable signal, caught by the candidate rather than the interviewer.

## Illustrative Failure Patterns

The following are illustrative, representative failure patterns — not transcripts of any real candidate — used to make each phase's failure mode concrete.

**Skipping phase 1 (clarify):** a candidate reads "Two Sum" and immediately starts coding a brute-force nested loop. Ninety seconds in, the interviewer asks "can you assume the array is sorted?" — a constraint that unlocks an O(n) two-pointer solution, available from the prompt, never asked about. Phase 1 exists specifically to surface this before committing to an approach.

**Skipping phase 2 (state the invariant):** a candidate writes a correct sliding-window solution in silence, then explains it after finishing. Asked to walk through the reasoning *before* the code was written, the candidate can only reconstruct it retroactively — from the interviewer's side, this is indistinguishable from pattern-matching to a memorized template. Stating the invariant before coding is what separates recognition from memorization in the interviewer's read of the situation.

**Skipping phase 5 (test before declaring done):** a candidate declares a solution complete; the interviewer asks what it returns for an empty input; the candidate traces it live, finds an off-by-one, and fixes it — visibly rattled. The bug itself isn't the problem — bugs happen — the problem is that the edge case wasn't traced *before* declaring completion. A self-caught bug during phase 5 reads as rigor; the same bug caught by the interviewer after "I'm done" reads as carelessness, even though the code defect is identical either way.

## Common Mistakes

- Treating narration as a summary given *after* solving, rather than a running commentary *during* solving
- Stating complexity once, at the start, and never checking the final code actually matches it — phase 6 exists precisely to catch this
- Going silent while typing during phase 4 — the interviewer cannot follow a train of thought they can't hear

## Staff-Level Discussion

At Staff scope, this protocol is not interview theater — it's the same discipline a Staff engineer applies in a real pairing session, a design review, or narrating a live production investigation: state assumptions before acting on them, name the approach and its rationale before committing engineering time to it, and verify before declaring done. A candidate who runs this protocol fluently is demonstrating the actual on-the-job communication skill the interview is a proxy for, not just performing a rehearsed script. The specific value of phase 6 (re-stating complexity against the actual code) generalizes directly: any claim made at the start of a piece of work ("this migration should take two weeks," "this fix should resolve the latency issue") deserves the same closing-the-loop verification against what actually happened.

## Interview Questions

### Question 1 — Why does stating the invariant (phase 2) have to happen before writing code, not after?

**Why interviewers ask it.** Tests whether the candidate understands narration as evidence of reasoning, not just a courtesy explanation.

**Expected answer.** Reasoning stated before the artifact exists is falsifiable in real time — the interviewer can push back, ask a clarifying question, or redirect before code is written. Reasoning stated after the fact is unfalsifiable and, from the interviewer's side, indistinguishable from having pattern-matched to a memorized solution and reverse-engineered a justification.

**Minimum acceptable answer.** States that explaining before coding looks more genuine, even without the falsifiability framing.

**Strong Senior answer.** Explains the indistinguishable-from-memorization mechanism specifically.

**Staff-level extension.** Generalizes the principle beyond coding interviews — stating a plan before executing it, in any collaborative technical work, is what allows a team to catch a bad plan before time is sunk into it, not after.

**Common mistakes.** Treating phase 2 as a nice-to-have politeness rather than the specific signal that separates genuine reasoning from retroactive justification.

**Likely follow-ups.** "What would you do if you stated an invariant in phase 2 and then discovered mid-coding that it was wrong?" (Say so out loud, immediately — silently patching around a wrong invariant is worse than the invariant being wrong in the first place.)

**Evaluation criteria (1–5).** 1: doesn't see a difference between before/after. 3: states before-coding looks better. 5: explains the falsifiability/memorization-indistinguishability mechanism and generalizes it.

**Related references.** [§ Illustrative Failure Patterns](#illustrative-failure-patterns).

---

### Question 2 — A bug is found during phase 5 (testing before declaring done) versus found by the interviewer after you say "I'm done." Is the code any different? Is the outcome?

**Why interviewers ask it.** Tests whether the candidate understands that this protocol is about demonstrated process, not just defect rate.

**Expected answer.** The code defect is identical either way — same bug, same fix. The outcome is different because self-caught defects during a declared verification step demonstrate rigor, while the same defect caught by someone else after a premature "done" demonstrates a verification gap, independent of raw coding skill.

**Minimum acceptable answer.** States that self-catching looks better without articulating why the underlying skill demonstrated is different.

**Strong Senior answer.** Names the specific skill difference: verification discipline, not coding correctness, is what phase 5 is evaluating.

**Staff-level extension.** Connects this to code review and production practice — the same principle underlies "test before you ship" broadly, and a Staff engineer models this discipline visibly precisely because junior engineers learn the norm by watching it, not by being told it.

**Common mistakes.** Treating this as purely a scoring-optics question ("catching it yourself looks better") without naming the actual skill difference being evaluated.

**Likely follow-ups.** "What's the minimum testing you'd do before declaring any solution done, even under severe time pressure?" (At least one edge case, chosen deliberately, not an arbitrary "let me just check.")

**Evaluation criteria (1–5).** 1: sees no difference beyond optics. 3: states self-catching looks better. 5: names verification discipline as the actual skill and generalizes to code review/production practice.

**Related references.** [§ The Six-Phase Protocol](#the-six-phase-protocol), phase 5.

## Summary

Six phases run on every coding problem: clarify constraints before coding, state the chosen approach and why before writing it, predict complexity before writing code, narrate while coding rather than after, test at least one case (including an edge case) before declaring done, and re-state complexity against what the code actually does. The protocol exists because an interviewer can only evaluate reasoning they can observe — silence forces a guess about whether a correct answer was reasoned or lucky.

## Key Takeaways

- Six phases, in order: Clarify, State the invariant, Complexity upfront, Narrate while coding, Test before declaring done, Re-state complexity against the actual code.
- Stating the invariant *before* coding is what separates demonstrated reasoning from retroactive, unfalsifiable justification.
- A self-caught edge-case bug during phase 5 reads as rigor; the identical bug caught by the interviewer after "I'm done" reads as carelessness — same defect, different signal.
- Phase 6 closes the loop on phase 3's prediction — a mismatch caught here is itself valuable evidence of self-checking.

## Cheat Sheet

| Phase | One-line prompt to yourself |
|---|---|
| 1. Clarify | "What haven't I confirmed about the input yet?" |
| 2. State the invariant | "Have I said why this approach applies, out loud, before coding?" |
| 3. Complexity upfront | "What's my predicted time/space, stated before I write anything?" |
| 4. Narrate while coding | "Am I describing this block as I write it, not after?" |
| 5. Test before declaring done | "Have I traced at least one edge case by hand?" |
| 6. Re-state complexity | "Does the actual code match what I predicted in phase 3?" |

## Flashcards

### Card: The six phases, in order

**Prompt:**
Name the six phases of the coding interview communication protocol, in order.

**Answer:**
Clarify, State the invariant, Complexity upfront, Narrate while coding, Test before declaring done, Re-state complexity against the actual code.

**Why it matters:**
The rehearsed structure that turns silent problem-solving into evaluable reasoning.

**Common trap:**
Explaining the approach only after the code is already written.

**Related:**
[The Six-Phase Protocol](#the-six-phase-protocol)

### Card: Why phase 2 must come before coding

**Prompt:**
Why does stating the invariant (phase 2) have to happen before writing code?

**Answer:**
Reasoning stated first is falsifiable in real time; reasoning stated after the fact is indistinguishable from a memorized solution with a reverse-engineered justification.

**Why it matters:**
This is the mechanism that separates genuine problem-solving from pattern-matching in the interviewer's assessment.

**Common trap:**
Coding first, explaining second — even when the explanation is completely accurate.

**Related:**
[Illustrative Failure Patterns](#illustrative-failure-patterns)

### Card: Same bug, different signal

**Prompt:**
A bug is found during phase 5's self-testing versus found by the interviewer after "I'm done." Is the code defect different?

**Answer:**
No — identical defect. The signal is different: self-caught reads as rigor, interviewer-caught reads as carelessness.

**Why it matters:**
Demonstrates that this protocol evaluates verification discipline, not just raw correctness.

**Common trap:**
Skipping phase 5 under time pressure, treating testing as optional once the code "looks right."

**Related:**
[Illustrative Failure Patterns](#illustrative-failure-patterns)

## Practice Exercises

1. Solve one coding problem narrating only phases 1, 3, and 6 (skip 2, 4, 5). Notice which parts of your reasoning became invisible.
2. Solve a different problem narrating all six phases aloud, recorded solo. Watch the recording specifically for phase 4 — are there silent gaps while typing?
3. Re-solve a problem you've already solved before, this time stating the invariant (phase 2) explicitly before writing any code. Compare how confidently you commit to the approach versus solving it without narrating first.
