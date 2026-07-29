---
title: "Week 3 Checkpoint Mock — 60 Minutes Combined"
week: 3
checkpoint: true
last_reviewed: 2026-07-29
---

# Week 3 Checkpoint Mock

**Format:** 60 minutes — 30 technical, 30 design. Partner strongly preferred over self-recording for this one specifically, since a checkpoint's value depends on genuine, unrehearsed follow-ups. Two hard-separated parts.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)

---

## Part A — Candidate script

**Technical half (30 min):**

1. **(5 min)** "Method A calls a `@Transactional` method B in the same class. What happens, why, and what are your fixes?" Full answer, all nine layers if time allows.
2. **(5 min)** One of the 6 Spring transaction demos, explained and re-derived from memory (don't re-read the chapter first).
3. **(8 min)** "Explain write skew with a concrete example." This is the discriminating question — deliver it in full, including why REPEATABLE READ misses it and why SERIALIZABLE catches it.
4. **(7 min)** LC 98 (Validate BST), narrated, including stating the local-check trap unprompted before writing any code.
5. **(5 min)** Deliver Story 5 or 6 using the four-beat trade-off structure where relevant.

**Design half (30 min):**

6. **(25 min)** Full six-phase design of a system given by your partner/interviewer (not the ride-hailing example you've already practiced — an unseen problem is the point).
7. **(5 min)** Self- or partner-assessment against `07-week-3-checkpoint-rubric.md`.

---

## Part B — Interviewer script

**Technical half:**

1. Ask Q1. If the candidate only names one fix, ask "what's a second way?"
2. During the demo re-derivation, interrupt once with: *"What would you observe differently if you ran this against PostgreSQL instead of H2?"* — listening specifically for the `readOnly` enforcement distinction (§6 of `01-transactions-and-propagation.md`) even though this question is about a different demo, to test whether the candidate generalizes "driver-dependent behavior" as a concept rather than memorizing per-demo facts.
3. On write skew: if the candidate's example conflates write skew with a lost update, ask *"is this the same kind of anomaly as two transactions both incrementing a counter?"* — the answer must be no, with the distinction stated.
4. On LC 98: if the candidate writes a local-only check first, let them run it against the trap example (`Main.java`'s `trapBst`) before pointing out the failure — self-discovery here is more valuable than a warning.
5. After the story: ask for the specific decision criterion if it wasn't explicit.

**Design half:**

6. Present an unseen problem (suggestions: a parking garage reservation system, a collaborative document editor's conflict resolution, a flash-sale inventory system). Do not help with phase discipline — this checkpoint measures whether the six phases have become habitual without prompting.
7. Score using `07-week-3-checkpoint-rubric.md` immediately, before discussing.

**Checkpoint decision:** if 4 of 6 dimensions on the rubric fail, tell the candidate directly and point them to `README.md`'s guidance: stop adding new topics, spend Week 4 consolidating Weeks 1–3, and repeat this checkpoint before proceeding.
