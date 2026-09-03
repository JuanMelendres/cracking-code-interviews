---
title: "Cheat Sheet: Incident Response and Blameless Postmortems"
slug: incident-response-and-blameless-postmortems
document_type: cheat-sheet
domain: performance
topic_id: T-1207
canonical: ../handbook/performance/incident-response-and-blameless-postmortems.md
last_updated: 2026-09-02
---

# Incident Response and Blameless Postmortems

**Canonical chapter:** [`syllabus/13-observability/incident-response-and-blameless-postmortems.md`](../syllabus/13-observability/incident-response-and-blameless-postmortems.md)

## Core Mental Model

An incident has two, genuinely different jobs happening at once, and conflating them is the single most common process mistake: stopping the bleeding (mitigation) and understanding what happened (diagnosis). They can proceed in parallel with enough people, but when forced to choose, mitigation wins — every minute of continued user-facing impact has a real, compounding cost, while diagnosis can almost always continue productively after the impact has stopped. A postmortem, afterward, resists the very human urge to compress a messy, multi-factor failure into one satisfying story with a single villain or a single cause.

## Essential Definitions

- **Incident response** — the real-time process of detecting, triaging, and mitigating an active service disruption.
- **Mitigation** — any action that stops or reduces user-facing impact, whether or not it addresses the underlying cause (a rollback, a feature flag, a manual failover).
- **Diagnosis** — the process of understanding why the incident happened, which may extend well past the point of mitigation.
- **Blameless postmortem** — a structured, written analysis identifying contributing factors and durable fixes without attributing the incident to an individual's personal failing.
- **Contributing factors, not root cause** — the register's central, testable distinction: real incidents typically have several genuinely independent contributing factors, any one of which, changed alone, would have prevented or shortened the incident.
- **Detection latency** — how long an incident ran before anyone noticed; a real, separate, actionable metric from how long it took to mitigate once noticed.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| An action is available that would stop or reduce user-facing impact right now | Take it before continuing diagnosis |
| The available mitigation itself would be risky or hard to reverse (a schema change) | Weigh that risk explicitly — mitigate-first is a default, not an absolute rule |
| A postmortem draft names an individual or team as the cause | Rewrite as a systems/process statement before it's reviewed |
| An incident has more than one plausible independent contributing factor | List all of them under "Contributing Factors," don't collapse to one |
| An action item lacks an owner or a due date | Not a real commitment yet — assign both before closing the postmortem |

**Approach comparison:**

| Approach | Speed to stop impact | Investigation depth |
|---|---|---|
| Diagnose fully before acting | Slow — impact continues throughout | Can be thorough |
| Mitigate first, diagnose after | Fast | Can still be equally thorough, just sequenced later |
| Single "Root Cause" postmortem | N/A | Shallow — stops at first identified cause |
| "Contributing Factors" postmortem | N/A | Deeper by construction |

## Key Numbers (real, executed Python linter output against real checked-in documents)

- `PASS templates/postmortem-template.md`; `PASS ...postmortem-001-checkout-latency-regression.md` (four genuinely independent contributing factors: a sizing-formula assumption, a missing load test, a slow alert threshold, a communication gap).
- `FAIL ...bad-example-blaming-language.md` — flagged: blame-coded language `"failed to"` in `"The on-call engineer failed to test the config change in staging before deploying"`; `"should have caught"` in `"which should have caught the issue."`
- `FAIL ...bad-example-single-root-cause.md` — flagged: missing required section `Contributing Factors`; uses a singular `Root Cause` section; two action items flagged as missing Owner/Due.

## Common Pitfalls

- Diagnosing fully before taking any mitigating action, extending user-facing impact unnecessarily.
- Believing a postmortem is blameless simply because it doesn't name a person, while still using blame-coded language ("the team failed to," "this should have been caught") that carries the same effect under different phrasing.
- Collapsing a multi-factor incident into a single "Root Cause" — the register's own named misconception.
- Writing action items with no owner or due date, effectively guaranteeing they never get done.

## Interview Answer Skeleton

**30-sec:** Mitigate before you fully diagnose — user impact compounds every minute, and diagnosis can usually continue after mitigation. A blameless postmortem lists contributing factors, plural, not a single root cause, and never uses blame-coded language, checkable mechanically rather than left to a reviewer's judgment.

**2-min:** Add the real linter evidence: a passing document with four independent contributing factors, versus two deliberately-flawed documents — one flagged for blame-coded language quoted verbatim, one flagged for a singular "Root Cause" heading and action items missing owner/due date.

**Whiteboard:** Draw a timeline with "incident starts" on the left, "impact stops" and "full understanding" as two separate points to its right. Mitigate-first path: "impact stops" lands close to the start, diagnosis continues in parallel past it. Diagnose-first path: "impact stops" pushed far right, after diagnosis completes. Shade the gap between the two "impact stops" points as the concrete cost of choosing diagnosis first.

**Staff-level framing:** Discuss blameless culture as a leadership commitment with real organizational teeth (leaders must visibly not punish people named in honest write-ups), not a writing convention. Connect action-item ownership to the same governance discipline required for fitness-function thresholds. Name the real exception to mitigate-first: an irreversible or high-risk mitigation genuinely warrants more diagnosis first.

## Production Warning Signs

- A postmortem names an individual ("the on-call engineer failed to run the checklist") — the engineer disengages from the review, and reviewing prior postmortems shows the same framing correlates with measurably shallower "contributing factors" analysis, because blame gives an investigation a place to stop early.
- Fix: rewrite as a systems statement ("the deploy pipeline had no enforced gate requiring the checklist be run") — leads directly to a concrete, systemic action item.
- A postmortem's timeline shows a long gap between detection and any mitigating action, filled entirely with diagnostic steps — diagnosing before mitigating, extending user-facing impact.
- The same category of incident (e.g., connection pool exhaustion) recurs under different proximate triggers because only the most recent trigger was ever documented under a singular "Root Cause," never the shared underlying contributing factors.

## Related

- `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md`
- `syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md`
- `syllabus/17-architecture/architecture-decision-records.md`
- `syllabus/11-system-design/resilience-patterns.md`
