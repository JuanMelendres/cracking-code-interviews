---
title: "Reserved-Capacity Purchase Increasing Cloud Spend"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/15-cloud/cloud-cost-and-scaling-economics.md
source: handbook/cloud/cloud-cost-and-scaling-economics.md#production-scenarios
---

# Reserved-Capacity Purchase Increasing Cloud Spend

## Context

A team, aiming to reduce cloud costs, purchases a 1-year reserved capacity commitment sized to match their fleet's peak instance count.

## Symptoms

Six months later, a finance review shows total compute spend is higher than the same period the previous year, before the reservation.

## Impact

A cost-reduction initiative measurably increases spend instead, undermining confidence in future cost-optimization proposals from the same team.

## Initial Hypotheses

- The reserved rate itself was mis-negotiated or priced incorrectly — checked and ruled out; the per-unit reserved rate is correctly lower than on-demand, exactly as quoted.
- Overall traffic or usage grew significantly in the period — checked and ruled out; usage patterns are essentially unchanged from the prior year.
- The reservation was sized for peak demand rather than the workload's actual steady baseline, and the trough-hours capacity is now being paid for at the committed rate whether it's used or not — correct.

## Evidence

Utilization data shows the fleet's actual demand follows a business-hours peak and a much lower overnight/weekend trough, but the reservation commits to paying for peak-level capacity around the clock, while the workload only genuinely needs that level roughly 4 of every 24 hours.

## Investigation Timeline

1. **Higher-than-expected spend flagged** during a routine finance review, six months after the reservation purchase.
2. **Reserved-rate and usage-growth hypotheses ruled out**, confirming the per-unit rate was correct and usage patterns were essentially flat year over year.
3. **Utilization data examined against the reservation's committed volume**, revealing a business-hours peak/trough pattern the reservation doesn't account for.
4. **Arithmetic reproduced**: reserving the peak count, rather than the confirmed steady baseline, produces a real, computable spend increase versus the correctly scoped alternative.

## Root Cause

The reservation commits to paying for peak-level capacity around the clock, but the workload only genuinely needs that level roughly 4 of every 24 hours. Reserving the peak count, rather than reserving only the confirmed steady baseline and autoscaling the variable portion on-demand, produces a real, computable spend increase — the reservation discount doesn't matter if it's applied to capacity that goes unused most of the day.

## Immediate Mitigation

None available mid-term without a financial penalty for early termination — reserved commitments are typically binding for their full term.

## Permanent Fix

At the reservation's renewal point, right-size the committed volume to the workload's actual confirmed steady baseline, verified from a full year of utilization data, not an assumption, and let the variable, peak-hours portion scale on autoscaled on-demand or spot capacity instead.

## Alternatives Considered

Purchasing a smaller reservation immediately and eating the early-termination cost on the oversized one. Evaluated case by case depending on the specific penalty terms, but not a universal answer; often the mathematically correct move is simply waiting out the term while ensuring the next reservation decision uses actual baseline data.

## Trade-offs

Correctly right-sizing a future reservation to the steady baseline, rather than peak, means accepting on-demand or spot pricing — higher per-unit cost — for the variable portion. Accepted, since the total blended cost is lower than over-committing the reservation to cover a peak that doesn't hold most of the day.

## Prevention

Any reservation-sizing decision should be based on a confirmed, historically observed steady baseline — the trough, or a genuinely flat portion of demand — never on peak demand, unless the workload is verified to be genuinely flat around the clock.

## Monitoring and Alerts

- A standing utilization-vs-committed-capacity dashboard reviewed before any future reservation purchase or renewal, surfacing the actual baseline/peak split directly rather than relying on an assumption at purchase time.
- A post-purchase spend-trend check at a fixed interval, for example 90 days, after any reservation commitment, comparing actual spend trajectory against the pre-purchase projection — this would have surfaced the increase well before the six-month finance review did.

## Interview Story

This maps to a "cost optimization that increased cost, why" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a reserved-capacity purchase intended to cut cloud spend instead increased total spend over the following six months.
- **Task:** explain how a correctly priced discount produced a net increase.
- **Action:** rule out mispricing and usage growth using direct billing and utilization data; compare the reservation's committed volume against the workload's actual peak/trough demand pattern; identify that the reservation was sized to peak rather than steady baseline.
- **Result:** committed to right-sizing the next reservation to the confirmed steady baseline at renewal, with the variable peak-hours portion left to autoscaled on-demand or spot capacity.

## Staff-Level Discussion

The core error here is a subtle one: a "discount" is only a savings if it's applied to capacity that would otherwise have been paid for anyway at the higher rate — reserving capacity that sits unused for 20 of 24 hours doesn't just fail to save money on that unused portion, it actively adds a new, fixed cost the team wasn't paying before, since on-demand billing would have charged nothing for those idle hours. This is a recurring category of cost-optimization mistake: initiatives that look correct through the lens of "lower per-unit rate" can still increase total spend if the sizing decision doesn't account for actual utilization shape. A Staff engineer evaluating any cost-optimization proposal involving a capacity commitment should insist on the utilization-shape data (peak vs. baseline, not just an aggregate average) before approving the sizing, since the arithmetic — as this chapter demonstrates directly — can go the wrong way even when every individual number involved is technically correct.

## Related Handbook Chapters

- [Cloud Cost and Scaling Economics](../syllabus/15-cloud/cloud-cost-and-scaling-economics.md) — canonical reservation-sizing arithmetic used here.
