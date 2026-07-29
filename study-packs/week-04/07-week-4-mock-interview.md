---
title: "Week 4 Mock Interview — Full System Design Round"
week: 4
last_reviewed: 2026-07-29
---

# Week 4 Mock Interview

**Format:** 45 minutes, full system design round. **Partner strongly preferred** — a self-mock cannot generate the unexpected follow-ups that make this round valuable.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)

---

## Part A — Candidate script

Design a **news feed** system (or a different unseen problem your partner supplies). Run the full six-phase method from `study-packs/week-03/03-system-design-method.md`, 45 minutes. Caching and fan-out are mandatory discussion points — if your design doesn't naturally reach them by minute 30, raise them yourself.

Score yourself with `study-packs/week-01/10-week-1-evaluation-rubric.md`'s System Design dimension after.

---

## Part B — Interviewer script

1. Do not prompt phase transitions — this measures whether the six phases are habitual.
2. If caching doesn't come up by minute ~20, ask: *"How would a user's feed avoid being recomputed from scratch on every single page load?"*
3. If fan-out isn't discussed, ask: *"A user with 10 million followers posts something. Walk me through what happens."* — listening for whether the candidate recognizes fan-out-on-write (precompute every follower's feed, expensive for celebrities) vs. fan-out-on-read (compute at read time, expensive for heavy readers) as a real trade-off, not just naming one approach.
4. During bottleneck analysis, if pagination comes up, ask: *"Would you use OFFSET or keyset pagination here, and why?"* — checking whether Week 4's own chapter content transfers to a new design context.
5. Score with `study-packs/week-01/10-week-1-evaluation-rubric.md` immediately after.
