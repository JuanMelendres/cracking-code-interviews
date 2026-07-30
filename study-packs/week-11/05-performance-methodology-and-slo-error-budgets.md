---
title: "T-1201 / T-1206 · Performance Methodology (USE/RED) & SLI/SLO/Error Budgets"
topic_id: T-1206
domain: Performance
tier: Staff
iwi: 6.90
prerequisites: [T-1204]
unlocks: []
week: 11
last_reviewed: 2026-07-29
---

# T-1201 / T-1206 · Performance Methodology (USE/RED) & SLI/SLO/Error Budgets

**IWI 6.90 (T-1205's neighbor) / 6.80 (T-1206) · Staff tier**

**Verification note:** the 30-day simulation in §4 is real, computed output from `practice/java/week-11/error-budget/src/ErrorBudgetDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [USE and RED, applied to artifacts already produced this program](#3-use-and-red-applied-to-artifacts-already-produced-this-program)
4. [A real error budget, burned by a real incident](#4-a-real-error-budget-burned-by-a-real-incident)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

**USE** (Utilization, Saturation, Errors) is a methodology for diagnosing a RESOURCE (a CPU, a disk, a connection pool) — for each resource, check how busy it is, how much work is queued waiting for it, and whether it's throwing errors. **RED** (Rate, Errors, Duration) is the equivalent methodology for a SERVICE (a request-handling endpoint) — requests per second, error rate, and latency distribution. An **SLI** (service level indicator) is a measured metric (e.g., success rate); an **SLO** (service level objective) is a target for that SLI (e.g., 99.9%); the **error budget** is what's left to spend before the SLO is breached.

## 2. Why it exists

These topics supply the vocabulary that makes a scaling or incident story credible in a behavioral interview — the roadmap's own explicit reason for scheduling this week last: a candidate who can say "we were at 60% of our error budget for the month and a single incident consumed 15% of it in 40 minutes" is speaking with a precision that "the system was having some problems" simply doesn't convey, and that precision comes directly from having real practice computing these numbers, not from having read the definitions.

## 3. USE and RED, applied to artifacts already produced this program

Rather than new demo code, USE and RED are best understood by re-reading artifacts already real and already captured earlier in this program through their lens — the roadmap's own framing that this week is a "vocabulary retrofit," not new material:

**USE, applied to Week 9's real GC log** (`study-packs/week-09/05-gc-fundamentals-and-log-analysis.md`): the heap itself is the resource. **Utilization** = occupied heap / max heap at any point (the log's `38M(64M)` — 59% utilized just before a collection). **Saturation** = how much work is queued waiting on this resource — a GC pause IS saturation made visible: the JVM had to stop all application threads because the heap couldn't absorb another allocation without reclaiming space first. **Errors** = an `OutOfMemoryError`, the resource's explicit failure signal. Reading that chapter's real log through the USE lens turns "the GC log shows some pauses" into a structured diagnosis: rising utilization (occupancy trend across GC(0)→GC(3)), a growing saturation signal (repeat sub-millisecond pauses, still cheap here but the trend is the point), zero errors (never hit `OutOfMemoryError` in that specific run).

**RED, applied to Week 8's real Kafka consumer-group demo** (`study-packs/week-08/03-consumer-groups-and-rebalancing.md`): **Rate** = messages consumed per second by a consumer group. **Errors** = failed message processing, or the delivery-semantics duplicates measured in that week's `04-delivery-semantics-and-exactly-once.md`. **Duration** = time from a message being produced to being fully processed — directly connects to consumer lag as an SLO, not just a metric (the exact point Week 8's blueprint excerpt named for `T-707`). A dashboard built around RED for that consumer group would show exactly the three numbers an on-call engineer actually needs to triage a lagging pipeline, rather than a wall of disconnected metrics.

## 4. A real error budget, burned by a real incident

**Real output**, a simulated 30 days of a service targeting a 99.9% SLO, 2,000,000 requests/day, including one real 40-minute incident on day 17:

```
SLO: 99.900% success rate over 30 days (60,000,000 total requests)
Error budget: 60,000 allowed failures over the period (0.1000% of traffic)

day 16:      446 failures, budget remaining after today:     53,143
day 17:    8,791 failures, budget remaining after today:     44,352  <-- incident day
day 18:      402 failures, budget remaining after today:     43,950
...
Actual success rate over the 30 days: 99.96492%  (SLO target: 99.900%)
Total failures: 21,050 of 60,000 allowed (35.1% of budget consumed)
RESULT: SLO met -- budget remaining, but see day 17's single-day burn rate.
```

**One 40-minute incident (day 17) consumed roughly 8,350 more failures than a typical day** (8,791 vs. a ~450 background rate) — by itself, close to 14% of the ENTIRE 30-DAY error budget, in under an hour. The SLO was still met overall (99.96% actual vs. 99.9% target, 35.1% of budget consumed across the whole month) — but stating ONLY the monthly aggregate would hide that a single incident nearly used up two weeks' worth of typical daily burn in one sitting. This is precisely the kind of number a credible incident story needs: not "we had an outage," but "the outage consumed roughly 14% of our monthly error budget in 40 minutes, which is why we treated it as a genuine incident rather than routine noise" — and it separates a genuine root-cause discussion (why did day 17 happen) from noise (the small day-to-day variance every other day shows).

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| Tighter SLO (e.g., 99.99%) | Better guaranteed user experience | Much smaller error budget — a single incident can breach it; expensive engineering investment to sustain |
| Looser SLO (e.g., 99.5%) | Cheaper to sustain, more room for incidents without breach | Worse guaranteed experience; may not meet real user/business expectations |
| Error budget tracked monthly (as demonstrated) | Smooths out day-to-day noise, focuses attention on genuine trend | Can hide a severe single-day incident inside an otherwise-fine monthly aggregate — worth tracking BOTH the aggregate and daily burn rate, as this chapter's demo output does |
| USE for resources, RED for services | Matches the right methodology to the right diagnostic target | Neither alone covers everything — a resource can be healthy by USE while the service built on it fails by RED (e.g., a bug, not a resource-saturation issue) |

## 6. Interview questions

### Q1. We're at 35% of our monthly error budget with two weeks left. Do we ship the risky migration this week?

- **Expected answer:** the aggregate number alone doesn't answer this — check the DAILY burn rate trend, not just the monthly total. If the 35% consumption is mostly from one incident (as in §4) rather than a steadily climbing background rate, there's real room; if it's climbing steadily, shipping something risky could tip into a breach.
- **Common mistakes:** answering purely from the aggregate percentage without asking about the underlying daily pattern.
- **Follow-up questions:** "The 35% is spread evenly across every day, no single incident. Does that change your answer?"
- **Senior-level expectations:** asks for the daily breakdown before answering.
- **Staff-level expectations:** frames the error budget as a genuine resource-allocation decision (this is literally what "error budget" means in SRE practice — the budget exists specifically to make "can we ship something risky" answerable with data) rather than a retrospective health score.

### Q2. Set the timeout — from what data? [full circle to Week 10's resilience chapter]

- **Expected answer:** from the service's own RED "Duration" distribution — specifically a high percentile (p99), per `03-percentiles-tail-latency-and-coordinated-omission.md` — not a round number.
- **Common mistakes:** treating this as a new, unrelated question rather than recognizing it's the exact same question Week 10's resilience chapter asked, now answerable with THIS week's vocabulary (RED's Duration metric, percentile selection) named precisely.
- **Follow-up questions:** "How does that number change if your load testing measured it via a closed-loop generator?"
- **Senior-level expectations:** connects RED's Duration signal to percentile selection.
- **Staff-level expectations:** explicitly notes that a closed-loop-measured Duration distribution would understate the real tail (per `03`'s coordinated omission finding), meaning a timeout set from that data could be miscalibrated — tying three separate weeks' material (resilience, percentiles, RED) into one coherent answer, exactly the retrofit this week's material exists to enable.

## 7. Common mistakes

- Reporting only the monthly/aggregate error-budget consumption without checking the daily burn-rate pattern underneath it.
- Treating USE and RED as interchangeable rather than matched to their targets (resources vs. services).
- Treating this week's vocabulary as new content rather than recognizing it retrofits precision onto stories and decisions from every earlier week.

## 8. Staff-level discussion

The genuinely Staff-level move with error budgets isn't computing them — it's USING them as an actual decision input, the way §6 Q1 frames it: an error budget's entire operational purpose is to make "how much risk can we afford to take this month" an answerable, data-driven question rather than a gut call, and a Staff engineer explicitly ties concrete decisions (ship the risky migration, or don't; run the maintenance window, or defer it) to the budget's current state and trend, not just report the number after the fact in a retrospective.

## 9. Summary

USE (Utilization/Saturation/Errors) diagnoses resources; RED (Rate/Errors/Duration) diagnoses services — both are lenses applicable directly to artifacts already produced earlier in this program (Week 9's GC log, Week 8's Kafka consumer group), not topics needing new demo code. A real 30-day error-budget simulation shows a single 40-minute incident consuming roughly 14% of the entire monthly budget — precisely the kind of number that turns a vague incident story into a credible, quantified one, and precisely why tracking the daily burn-rate trend matters as much as the monthly aggregate.

## 10. Key Takeaways

- USE applies to resources (utilization, saturation, error signals); RED applies to services (rate, errors, duration).
- An error budget's monthly aggregate can hide a severe single-day incident — track the daily trend too.
- SLOs are defined against percentiles (per `03`), never averages.
- This week's vocabulary retrofits precision onto every earlier week's incident/scaling material — practice applying it backward, not just forward.

## 11. Cheat Sheet

| Target | Methodology |
|---|---|
| A resource (CPU, disk, connection pool, heap) | USE: Utilization, Saturation, Errors |
| A service (an endpoint, a consumer group) | RED: Rate, Errors, Duration |
| "How much risk can we take this month?" | Error budget remaining + daily burn-rate trend, not just the monthly aggregate |

## 12. Flashcards

1. **Q: What does USE stand for, and what does it diagnose?** A: Utilization, Saturation, Errors — a methodology for diagnosing a RESOURCE (CPU, disk, heap, connection pool).
2. **Q: What does RED stand for, and what does it diagnose?** A: Rate, Errors, Duration — a methodology for diagnosing a SERVICE (a request-handling endpoint or consumer group).
3. **Q: Why can a monthly error-budget aggregate be misleading on its own?** A: It can hide a severe single-day incident inside an otherwise-fine month — a 40-minute incident consuming ~14% of the ENTIRE month's budget was measured directly in this chapter, invisible from the 35.1%-of-budget monthly total alone.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-11/error-budget/src/ErrorBudgetDemo.java`, then change the incident's duration/severity and recompute what fraction of the monthly budget it consumes.
2. Apply the USE methodology, in writing, to Week 10's `04-resilience-patterns.md` circuit-breaker demo — what's the "resource," and what do Utilization/Saturation/Errors correspond to for a thread pool specifically?
3. Draft the exact quantified sentence you'd use in a behavioral story about an incident, using this chapter's own numbers as a template ("a N-minute incident consumed X% of the month's error budget").

## 14. Additional Reading

- [Google SRE Book — Service Level Objectives](https://sre.google/sre-book/service-level-objectives/)
- [Brendan Gregg — The USE Method](https://www.brendangregg.com/usemethod.html)

## 15. Official References

- [Google SRE Workbook — Implementing SLOs](https://sre.google/workbook/implementing-slos/)
