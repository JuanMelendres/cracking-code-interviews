---
title: "Week 2 Mock Interview"
week: 2
last_reviewed: 2026-07-29
---

# Week 2 Mock Interview

**Format:** 30 minutes, self-recorded or with a partner. Must include one `EXPLAIN` walkthrough aloud. Two hard-separated parts.

---

## Part A — Candidate script

1. **(3 min)** Present an unfamiliar `EXPLAIN ANALYZE` output (pick one of the three from `01-query-planning-and-explain.md` you haven't looked at in the last hour) and diagnose it aloud, cold.
2. **(4 min)** "Model many-to-many between Order and Product. Now it needs quantity — what changes?" Full answer including the price-history trigger if it comes up naturally.
3. **(4 min)** "What is an aggregate boundary, and why is it a transaction boundary?" Then one follow-up.
4. **(4 min)** "Choose between PostgreSQL and DynamoDB for [a workload the interviewer/you specify]. Defend it, then argue the opposite."
5. **(8 min)** LC 739 (Daily Temperatures), narrated per all six phases. Include stating, unprompted, why a values-only stack can't work.
6. **(7 min)** Deliver Story 3 (production incident) using the four-beat trade-off structure where relevant, then answer one follow-up.

Score yourself with `study-packs/week-01/10-week-1-evaluation-rubric.md` (general dimensions still apply) before reading Part B.

---

## Part B — Interviewer script

1. Pick one of the three `01-…` scenarios the candidate hasn't recently reviewed. Ask them to read and diagnose it live, without the chapter open.
2. After the many-to-many answer: *"What if the relationship has no extra column today — is there still a case for the explicit entity?"* — this is the price-history trigger; if it doesn't come up, that's the specific gap to flag.
3. After the aggregate answer: *"Two transactions need to update two different aggregates together. Now what?"* — listening for a named mechanism (saga/outbox), not just "eventual consistency" as an unexplained buzzword.
4. On the storage question: interrupt the "defend it" half partway with *"what specific access pattern would flip your answer?"* — a candidate who can't name one hasn't actually reasoned from the access-pattern method.
5. During LC 739: if the candidate starts writing a values-based stack, let them go for 60 seconds before asking *"how will you know which day that value came from?"* — this is the exact question that surfaces the structural impossibility, not just a code bug.
6. After Story 3: ask for the specific decision criterion (beat 3) if it wasn't explicit, and the cost (beat 4) if it wasn't stated.
