---
title: "Week 7 Mock — Spring Technical (45 min)"
week: 7
last_reviewed: 2026-07-29
---

# Week 7 Mock — Spring Technical

**Format:** 45 minutes, Spring-focused technical round.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)

---

## Part A — Candidate script

1. **(6 min)** Walk the full bean lifecycle order, unprompted, then explain where a `@Transactional` proxy is actually created in that sequence.
2. **(8 min)** "Explain why `@Transactional` on an `@Async` method behaves unexpectedly." Full answer with the real demo's numbers.
3. **(8 min)** Trace a request through your security filter chain — use `06-security-chain-trace-deliverable.md`'s worked example or your own.
4. **(8 min)** "Explain JWT revocation honestly." Full answer including both mitigations and their costs.
5. **(8 min)** "Why PKCE if you already have a client secret?"
6. **(7 min)** Deliver Story 9 or 10 using the four-beat structure.

## Part B — Interviewer script

1. On the lifecycle question, ask specifically: "Which callback does the transactional proxy get created in, and why does it have to be that one and not `@PostConstruct`?"
2. On `@Async`+`@Transactional`: interrupt if the candidate claims the transaction itself is broken — redirect to "is the transaction wrong, or is something else wrong?"
3. On the filter chain: ask for a specific rejection scenario, not just the happy path.
4. On JWT revocation: if the candidate says "yes, you can revoke it" without qualification, push: "how, exactly — walk me through what changes at verification time?"
5. Score using `study-packs/week-01/10-week-1-evaluation-rubric.md`'s Technical Depth and Production Judgment dimensions.
