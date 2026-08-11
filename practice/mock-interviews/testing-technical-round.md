---
title: "Mock Interview: Testing Technical Round (45 min)"
slug: testing-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Percentile vs mean latency diagnosis
  - Performance-testing process design
  - Live test-first coding discipline
  - Consumer-driven contract testing
  - Coverage vs. mutation/verification strength
  - JUnit 5 architecture
related:
  - ../../handbook/testing/performance-and-load-testing-methodology.md
  - ../../handbook/testing/writing-tests-live-in-an-interview.md
  - ../../handbook/testing/contract-testing-for-services.md
  - ../../handbook/testing/mutation-and-property-based-testing.md
  - ../../handbook/testing/junit5-architecture-and-advanced-features.md
source: ../../study-packs/week-18/08-week-18-mock-interview.md
official_references: []
---

# Mock Interview: Testing Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Includes one live-coding segment (Question 3). Elevated from `study-packs/week-18/08-week-18-mock-interview.md`.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Percentile vs mean latency | Q1 | [Percentiles, Tail Latency, and Coordinated Omission](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md) |
| Performance-testing process design | Q2 | [Performance and Load Testing Methodology](../../handbook/testing/performance-and-load-testing-methodology.md) |
| Live test-first coding discipline | Q3 (live-coding) | [Writing Tests Live in an Interview](../../handbook/testing/writing-tests-live-in-an-interview.md) |
| Consumer-driven contract testing | Q4 | [Contract Testing for Services](../../handbook/testing/contract-testing-for-services.md) |
| Coverage vs. verification strength | Q5 | [Mutation and Property-Based Testing](../../handbook/testing/mutation-and-property-based-testing.md) |
| JUnit 5 architecture | Q6 | [JUnit 5 Architecture and Advanced Features](../../handbook/testing/junit5-architecture-and-advanced-features.md) |
| Cross-topic synthesis | Q7 | All five of this week's testing topics |

## Interviewer Opening Script

*"This is a 45-minute Testing technical round, including one short live-coding exercise. I'll ask seven questions covering performance diagnosis, testing process design, contract testing, coverage vs. mutation testing, and JUnit 5 — for the live-coding question, I want to see genuine test-first discipline: write a failing test, confirm it fails for the right reason, then implement. Narrate as you go. Ready?"*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** A dashboard shows mean latency flat at 15ms for weeks, but complaints about slowness are increasing. What would you check first, and why?
2. **(6 min)** Your team's load-testing script hasn't run in six months, and no one can say why. What's the underlying process problem, and how would you fix it?
3. **(6 min, live-coding)** Implement, test-first, a function that returns the second-largest distinct value in an array. Narrate every step, including at least one deliberate red step.
4. **(6 min)** Your org relies on manually notifying downstream teams before any shared-API change. What would you propose, and what would the transition actually require?
5. **(6 min)** A module has 95% line coverage. Does that tell you the test suite would catch a real bug introduced into it? Why or why not?
6. **(6 min, whiteboard)** Sketch JUnit 5's three-module architecture and explain what the split specifically enables.
7. **(9 min)** Free-form: pick any two of this week's five topics and explain how they interact in a single real testing strategy (e.g., why a contract-testing failure and a live-coding-interview red step should both be read carefully for the *specific* reason they failed, not just that they failed).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Flat mean, rising complaints

**Ideal answer outline:** checks percentile latency (p95/p99), not mean — a flat mean is fully consistent with a real, growing tail-latency problem, since the mean is mathematically dominated by the bulk of fast requests regardless of tail severity.
**Common weak answers:** treating the flat mean as evidence against a real regression.
**Pass signal:** correctly identifies percentile latency as the metric to check and explains why the flat mean doesn't contradict the complaints.
**Borderline signal:** suspects "something's off" without naming percentiles specifically.
**Fail signal:** dismisses the complaints since the dashboard "looks fine."

### Question 2 — Performance testing silently lapsed

**Ideal answer outline:** performance testing produces no automatic failure signal when skipped, unlike a functional test suite — fix is structural: a required, automated gate or an explicitly owned, scheduled exercise.
**Common weak answers:** "just run it now" without addressing the structural gap.
**Pass signal:** identifies the lack of automatic failure signal as the root cause.
**Borderline signal:** proposes running it again without a process fix.
**Fail signal:** no diagnosis of why it lapsed at all.

### Question 3 — Live-coding: second-largest distinct value

**Ideal answer outline:** starts with the smallest meaningful case, confirms a red failure for the expected reason, writes a minimal implementation, adds cases for duplicates and a too-small array (narrating the edge-case decision explicitly).
**Common weak answers:** writing the full implementation first, retrofitting tests afterward.
**Pass signal:** genuine, narrated red-green-refactor loop with sensible test-case ordering.
**Borderline signal:** correct final code, but tests written only at the end.
**Fail signal:** no test-first discipline at all.

### Question 4 — Manual coordination doesn't scale

**Ideal answer outline:** propose consumer-driven contract testing — each downstream team defines a contract from real usage, verified automatically against the provider's real implementation in its own pipeline.
**Common weak answers:** proposing it as a purely provider-side tooling change with no acknowledgment of consumer-side maintenance responsibility.
**Pass signal:** correctly proposes contract testing and names the real ownership shift required.
**Borderline signal:** names "some kind of automated check" without the consumer-driven ownership detail.
**Fail signal:** proposes only "better communication" with no testing mechanism.

### Question 5 — Coverage vs. verification strength

**Ideal answer outline:** coverage measures execution, not verification strength — a weak assertion can execute a buggy line without catching the bug; mutation testing directly measures whether the suite would catch a real defect.
**Common weak answers:** treating high coverage as strong evidence of quality.
**Pass signal:** correctly explains the execution-vs-verification distinction.
**Borderline signal:** senses coverage "isn't everything" without explaining why.
**Fail signal:** treats coverage percentage as sufficient on its own.

### Question 6 — Whiteboard: JUnit 5 architecture

**Ideal answer outline:** draws Platform (bottom, framework-agnostic), Jupiter and Vintage (side by side on top), explains Vintage's role in enabling incremental migration rather than a big-bang rewrite.
**Pass signal:** correctly draws and narrates the three-module split and its migration payoff.
**Borderline signal:** names the three modules but can't explain what the split practically enables.
**Fail signal:** describes JUnit 5 as "just new annotations" with no architectural understanding.

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., contract-test failures and live-coding red steps both need their exact failure reason read carefully rather than reflexively acted on — a shared discipline of evidence-first investigation over assumption) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold as the [Java Core Technical Round](java-core-technical-round.md):

| Score | Meaning |
|---|---|
| 1 | No coherent answer, or a factually wrong one |
| 2 | Names the right topic but no working mechanism |
| 3 | Correct mechanism, Senior-level bar met |
| 4 | Correct mechanism plus one Staff-level extension |
| 5 | Correct mechanism, Staff-level extension, and a real/plausible production connection |

**Pass threshold for this mock:** average score ≥ 3.5 across all seven questions, with no individual score below 2.

## Debrief Guide

Walk the candidate through their own scores question by question, starting with the lowest. Question 3 (live-coding) deserves its own discussion independent of whether the final code was correct: did the candidate write a genuinely failing test before implementing, confirm the failure reason, and narrate throughout — these process signals matter as much as correctness for this specific question. Questions 1 and 5 share an evidence-quality theme (a metric that looks fine isn't the same as a metric that's actually measuring the right thing) — if both scored low, the gap may be broader than either topic individually.

## Remediation Recommendations

- Any score ≤ 2 on Q1 → re-read [Percentiles, Tail Latency, and Coordinated Omission](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md).
- Any score ≤ 2 on Q2 → re-read [Performance and Load Testing Methodology](../../handbook/testing/performance-and-load-testing-methodology.md)'s process-ownership material.
- Any score ≤ 2 on Q3 → re-read [Writing Tests Live in an Interview](../../handbook/testing/writing-tests-live-in-an-interview.md) and redo the kata under a timer.
- Any score ≤ 2 on Q4 → re-read [Contract Testing for Services](../../handbook/testing/contract-testing-for-services.md), specifically the consumer-driven ownership model.
- Any score ≤ 2 on Q5 → re-read [Mutation and Property-Based Testing](../../handbook/testing/mutation-and-property-based-testing.md).
- Any score ≤ 2 on Q6 → re-read [JUnit 5 Architecture and Advanced Features](../../handbook/testing/junit5-architecture-and-advanced-features.md).
- Below the 3.5 pass threshold overall → retake this mock in full after remediation.
