---
title: "Week 18 Mock — Testing Technical Round (45 min)"
week: 18
document_type: study-pack-mock
status: draft
last_reviewed: 2026-08-02
---

# Week 18 Mock — Testing Technical Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

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

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Weeks 13–17's mocks — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
