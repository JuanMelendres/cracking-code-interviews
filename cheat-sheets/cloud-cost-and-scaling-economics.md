---
title: "Cheat Sheet: Cloud Cost and Scaling Economics"
slug: cloud-cost-and-scaling-economics
document_type: cheat-sheet
domain: cloud
topic_id: T-1007
canonical: ../handbook/cloud/cloud-cost-and-scaling-economics.md
last_updated: 2026-08-05
---

# Cloud Cost and Scaling Economics

**Canonical chapter:** [`handbook/cloud/cloud-cost-and-scaling-economics.md`](../handbook/cloud/cloud-cost-and-scaling-economics.md)

## Core Mental Model

Every cloud cost decision is a bet on how predictable your future usage is, priced accordingly. On-demand costs the most per unit for zero commitment. Reserved costs less per unit in exchange for committing to pay whether you use it or not. Spot costs the least but can be reclaimed with little notice. The right choice isn't "whichever is cheapest per unit" — it's whichever matches how confidently you can predict the workload paying for it.

## Essential Definitions

- **On-demand** — charges per unit with no commitment; maximum flexibility, highest per-unit price.
- **Reserved/committed-use** — lower per-unit rate for committing to a usage volume over a term (often 1–3 years); the discount rewards the provider's demand predictability.
- **Spot/preemptible** — spare capacity at a steep discount; the provider can reclaim it with little or no notice.
- **Steady baseline vs. peak** — the genuinely flat, always-present portion of demand (what a reservation should target) versus the highest short-duration demand (what should scale on-demand/autoscaled instead).

## Decision Table

| Demand shape | Right approach |
|---|---|
| Genuinely flat, 24/7 steady | Reserve near the steady level; autoscaling adds complexity with little savings |
| Clear peak/trough pattern | Reserve only the confirmed steady baseline; autoscale the variable portion on-demand |
| Can tolerate sudden reclamation | Spot capacity for the reclaimable-tolerant portion |
| Uncertain/rapidly-changing usage | On-demand, accepting the higher per-unit price for flexibility |

**Trade-offs:** a reservation's discount only pays off on capacity actually used — sizing it to peak instead of the steady baseline can cost *more* than correctly autoscaling on-demand. Autoscaling's savings come specifically from the peak/trough gap, not from autoscaling being inherently cheaper — a genuinely flat workload gets no benefit from it.

## Key Numbers (real, illustrative arithmetic — method is the transferable skill, not the specific prices)

Reserving the *correct* steady baseline (20 instances, 24/7, at $0.10 on-demand vs. $0.06 reserved):

```
On-demand:  20 x $0.10 x 24 x 365 = $17,520/year
Reserved:   20 x $0.06 x 24 x 365 = $10,512/year   <- $7,008/year saved (40%)
```

Over-provisioning for peak (peak=20 instances/4hr, trough=6 instances/20hr) vs. correctly autoscaling:

```
Static, peak 24/7:        20 x $0.10 x 24 x 365 = $17,520/year
Correctly autoscaled:     (20x4 + 6x20) x $0.10 x 365 = $7,300/year
Annual waste from static peak-provisioning: $10,220/year (58%)
```

Reserving the *wrong* number (peak instead of steady baseline):

```
Reserve 20 (=peak) at $0.06/hr, 24/7: $10,512/year (committed regardless of use)
vs. correctly-autoscaled on-demand:    $7,300/year
Reserving peak costs MORE by $3,212/year -- sized for the wrong baseline
```

## Common Pitfalls

- Sizing a reservation to peak demand instead of the genuinely steady baseline.
- Assuming autoscaling always saves money regardless of whether demand actually varies.
- Choosing spot purely by discount percentage without verifying the workload tolerates sudden reclamation.
- Treating a reservation as a "set once" decision instead of revisiting it at each renewal against current usage data.

## Interview Answer Skeleton

**30-sec:** Cloud pricing trades flexibility for discount: on-demand (no commitment, highest price), reserved (term commitment, meaningful discount), spot (reclaimable, steepest discount). The right choice depends on demand predictability, not per-unit price alone — reserving for peak instead of the genuine steady baseline can increase total spend, a real computable mistake.

**2-min:** Add why the model exists (providers reward demand predictability) + the real worked arithmetic (reserving the steady baseline saves 40%; reserving peak instead of correctly autoscaling costs $3,212/year *more*) + the trade-off (autoscaling only saves money because of the peak/trough gap — a flat workload gets nothing from it).

**Whiteboard:** Spectrum on-demand → reserved → spot, per-unit price decreasing left to right, commitment/reclamation-risk increasing left to right. Below it, a demand curve with a clear peak and trough — shade "reserve this much" as only the trough-and-below steady baseline, leaving the peak-hours gap for on-demand/autoscaled capacity.

**Staff-level framing:** the Staff-level move isn't knowing current prices — it's applying the calculation method to whatever numbers are given, and catching the peak-vs-baseline sizing mistake before it's made rather than discovering it in a finance review months later. Treat a cost proposal with the same rigor as an architecture decision: state the demand-shape assumption, show the arithmetic, identify what would have to be true for it to hold.

## Production Warning Signs

- A finance review shows total compute spend *increased* after a cost-reduction reservation purchase — check whether the reservation was sized to peak instead of the confirmed steady baseline; the committed rate applies to unused capacity most of the day.
- Autoscaling is proposed for a workload with genuinely flat, 24/7-steady demand — expect little to no real savings; the fleet would be sized near-identically either way, and the complexity buys nothing.
- **Prevention:** size any reservation only to a confirmed, historically-observed steady baseline (never peak), and revalidate at every renewal point against current usage data, not the data available at commitment time.

## Related

- `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md`
- `handbook/system-design/system-design-method-and-estimation.md`
