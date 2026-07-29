# Week 1 Mock Interview

**Format:** 20 minutes, self-recorded or with a partner. Two hard-separated parts — read only your own part until the mock is complete. Part A only if this is your first attempt at this pack (per `README.md`).

---

## Part A — Candidate script

Do not read Part B before attempting this.

1. **(2 min)** "Explain hexagonal architecture" — cold, no notes. Aim for the 30s opening, then let the interviewer (or your recording) decide how far to push.
2. **(3 min)** Answer whatever follow-up comes. If self-recording, pull one question at random from `01-clean-hexagonal-architecture.md` §7.
3. **(2 min)** "How does a B+Tree index actually find a row?" — walk it from root to heap, out loud.
4. **(3 min)** "You added an index and the query got slower. Give two distinct mechanisms." Then whichever follow-up from `02-database-index-fundamentals.md` §9 comes next.
5. **(5 min)** LC 146 (LRU Cache), narrated per all six phases from `04-coding-interview-communication.md`. Time-boxed to 20 minutes total if run as a full round; for this mock, 5 minutes is enough to demonstrate the narration habit, not necessarily finish.
6. **(5 min)** Deliver Story 1 (architecture decision) in under 2 minutes, then answer one follow-up: "What would you have done if the alternative had won instead?"

Stop. Score yourself with `10-week-1-evaluation-rubric.md` before reading Part B.

---

## Part B — Interviewer script (read only after completing Part A)

**Question bank, in order:**

1. "Explain hexagonal architecture." *(Wait for the 30s opening. Do not interrupt before 45 seconds regardless of how it's going — this itself is information: does the candidate self-regulate length, or run past the failure mode named in `03-technical-answer-framework.md` layer 1?)*
2. Pick one, based on what the L2 answer didn't cover: *"Where do JPA entities live in this model?"* or *"Would you use this on every project?"* — the second is the Staff-differentiating question; a confident unconditional "yes" is a specific, scoreable weakness.
3. "How does a B+Tree index actually find a row? Walk it from root to heap."
4. "You added an index and the query got slower. Give two distinct mechanisms." *(Correct: write amplification, and stale statistics. A candidate who only names one gets a nudge: "what else, besides the query getting a worse plan?")*
5. Present LC 146. Interrupt once, deliberately, partway through with: *"What happens if I call put on a key that already exists, when the cache is already full?"* — this is the exact question that exposes the errata bug if the candidate has memorized the source material's buggy version rather than reasoned through it.
6. After Story 1: *"What would you have done if the alternative had won instead?"* — listening for whether the candidate can represent the rejected alternative's strongest form, per `05-star-story-workbook.md` §3.

**Evaluation notes for the interviewer role:** score using `10-week-1-evaluation-rubric.md` immediately after, before discussing — first impressions drift if scoring is delayed.
