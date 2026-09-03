---
title: "Cheat Sheet: System Design Method and Estimation"
slug: system-design-method-and-estimation
document_type: cheat-sheet
domain: system-design
topic_id: T-801
canonical: ../handbook/system-design/system-design-method-and-estimation.md
last_updated: 2026-08-03
---

# System Design Method and Estimation

**Canonical chapter:** [`syllabus/11-system-design/system-design-method-and-estimation.md`](../syllabus/11-system-design/system-design-method-and-estimation.md)

## Core Mental Model

Every box in a system design must trace back to a number stated earlier — "we need a cache" is not architecture; "reads are 50,000/s and the DB tops out around 8,000/s reads, so a cache is required" is. The six phases exist to guarantee the numbers (estimation) come *before* the boxes (architecture). Reversing that order — jumping straight to components — is the single most common failure pattern below Staff level.

## Essential Definitions

- **Clarify** — scope, core action (read/write-heavy), what's explicitly out of scope.
- **Estimate** — QPS/storage numbers with every assumption stated, so decisions are falsifiable.
- **API phase** — core endpoints; doubles as a second clarification pass.
- **Data phase** — data model and storage-technology fit, driven by the estimate.
- **Architecture phase** — every box justified against the estimate.
- **Bottlenecks phase** — ≥3 named failure modes + mitigations; most-skipped phase under time pressure.
- **Peak-to-average ratio** — the single most consequential estimation assumption; architecture is sized to peak, not average.

## Decision Table

| Phase | Time budget | Purpose |
|---|---|---|
| 1. Clarify | 2–3 min | Scope + explicit non-scope |
| 2. Estimate | 3–5 min | QPS/storage, assumptions stated |
| 3. API | 2–3 min | Client-facing contract |
| 4. Data | 3–5 min | Model + storage choice, driven by Phase 2 |
| 5. Architecture | 10–15 min | Boxes, each justified against Phase 2 |
| 6. Bottlenecks | 5–10 min | ≥3 failure modes + mitigations — don't skip |

## Key Numbers (worked example: 10M DAU)

```
Writes: 10M × 5 actions/day = 50M/day → avg 580/s
Peak (3x consumer ratio): ~1,740 writes/s
Reads (10:1 ratio): ~17,400 reads/s
Storage: 50M × 500B/day ≈ 25GB/day → ~9.1TB/yr → ~27.3TB/yr with 3x replication
```
B2B/business-hours systems: peak-to-average ~1.5–2x, not 3x.

## Common Pitfalls

- Jumping to components before establishing scale (#1 failure below Staff level)
- An estimate with no stated assumptions — unfalsifiable, unreviewable
- Running out of time before Phase 6 (bottlenecks) — a scored gap, not bad luck
- Treating the six phases as a rigid script rather than expecting Phase 6→5 iteration

## Interview Answer Skeleton

**30-sec:** Clarify → Estimate → API → Data → Architecture (justified by the estimate) → Bottlenecks. Estimation precedes architecture so components are justified by numbers, not reflex.

**2-min:** State the six phases + why (prevents jumping to components, prevents one phase eating all the time) + architecture traces to Phase 2 numbers + trade-off (feels slower, every decision becomes defensible) + notification-service production example.

**Whiteboard:** Draw the six-box flowchart left→right with a dotted "iterate" arrow from Bottlenecks back to Architecture; narrate each box's time budget while drawing.

**Follow-ups to expect:** "Why estimate before designing?" / "Peak-to-average is actually 5x, not 3x — how does the architecture change?" / "What's the first thing you'd cut with a 6-week deadline?"

## Production Warning Signs

- Design doc jumps from problem statement straight to architecture diagram, no capacity-estimation section
- Consumer/partition counts set to "reasonable-sounding" round numbers with no traceable justification
- **Real incident pattern:** notification service skipped Phase 2 entirely → guess-sized capacity → fell over during a promotional campaign, queue backed up for hours, notifications arrived too late to matter. Fix wasn't broad over-provisioning (expensive, still untraceable) — it was making capacity estimation a required, gating section of the design-doc template.

## Related

- [Distributed Systems Failure Modes](distributed-systems-failure-modes.md)
- [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md)
- `syllabus/07-api-design/api-design.md`
