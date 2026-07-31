---
title: "Week 11 Mock — Behavioral, Full 6-Question Set (45 min)"
week: 11
last_reviewed: 2026-07-31
---

# Week 11 Mock — Behavioral, Full 6-Question Set

**Format:** 45 minutes, per `00-project/learning-roadmap.md` §4 Week 11. This week's instruction is explicit: **retrofit this week's observability vocabulary (percentiles, RED, error budgets) into Stories 3, 7, and 11** rather than write new stories — no new story is introduced this week. This mock draws 6 questions from the full 12-story bank built across Weeks 1–9, with the three retrofit targets included.

## Table of Contents

1. [Part A — Candidate script](#part-a--candidate-script)
2. [Part B — Interviewer script](#part-b--interviewer-script)
3. [Retrofit checklist](#retrofit-checklist)

---

## Part A — Candidate script

Each answer: four-beat structure (situation, task/decision, action, result — with the result quantified wherever this week's vocabulary applies), 5-7 minutes including follow-ups.

1. **Story 1 — Architecture decision.** A design choice you made, alternatives considered, why you chose it, what it cost.
2. **Story 3 — Production incident.** **Must be retrofitted**: state the incident's impact in error-budget terms (per `05-performance-methodology-and-slo-error-budgets.md`'s own worked example: "the incident consumed roughly X% of that month's error budget in Y minutes") rather than only a qualitative severity description.
3. **Story 7 — Cross-team influence.** **Must be retrofitted where genuinely applicable**: if the influence involved a technical case for change, was it grounded in a specific metric (a RED signal, a percentile, an error rate) rather than a vague "it was slow" claim?
4. **Story 11 — Scaling/performance.** **Must be retrofitted**: name the specific percentile the scaling decision was justified against (per `03-percentiles-tail-latency-and-coordinated-omission.md`), not an average — and state explicitly if the original measurement was closed-loop (and therefore possibly understated).
5. **Story 6 — A failure you owned.** Owned without deflection, with concrete behavioral change afterward.
6. **Story 12 — Ambiguity/incomplete information.** How you moved forward productively without complete information.

## Part B — Interviewer script

1. On Story 1: probe for a genuine alternative that was seriously considered and rejected, not a strawman.
2. On Story 3 (retrofit target): if the candidate describes severity only qualitatively ("it was pretty bad"), push: "quantify that — what fraction of your error budget did it consume, and over what window?" The specific gap this week's material exists to close.
3. On Story 7 (retrofit target): if the case for change was originally vague, ask directly: "what number would have made that argument land faster?"
4. On Story 11 (retrofit target): if the candidate cites an average rather than a percentile, redirect: "what did the tail look like, specifically — and how was it measured?" (testing for awareness of `03`'s coordinated-omission gap without leading the witness).
5. On Story 6: if the ending is too neat (no real cost, no real behavior change), push: "what would you do differently if the exact same situation happened again tomorrow?"
6. On Story 12: probe for a case where the ambiguity was genuine (not merely "I hadn't read the docs yet") and the candidate's judgment call under uncertainty is the actual content being evaluated.
7. Score using `study-packs/week-01/10-week-1-evaluation-rubric.md`'s Behavioral dimension, with an explicit sub-note on whether Stories 3/7/11 were successfully retrofitted with this week's vocabulary or still read as they did before Week 11.

## Retrofit checklist

Per this week's own explicit instruction — confirm all three before considering this mock complete:

- [ ] Story 3 (production incident) states impact in error-budget terms, with a specific percentage and time window
- [ ] Story 7 (cross-team influence) grounds its technical case in a specific metric, where genuinely applicable to that story's content
- [ ] Story 11 (scaling/performance) cites a specific percentile, not an average, and notes the measurement methodology (closed- vs. open-loop) if relevant
