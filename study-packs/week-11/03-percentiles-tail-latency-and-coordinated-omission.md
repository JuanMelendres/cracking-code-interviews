---
title: "T-1204 · Latency: Percentiles, Tail Latency & Coordinated Omission"
topic_id: T-1204
domain: Performance
tier: Staff
iwi: 6.70
prerequisites: []
unlocks: []
week: 11
last_reviewed: 2026-07-29
---

# T-1204 · Latency: Percentiles, Tail Latency & Coordinated Omission

**IWI 6.70 · Staff tier**

**Verification note:** the percentile figures in §3 are real, computed output from `practice/java/week-11/percentiles/src/CoordinatedOmissionDemo.java` — 100,000 simulated requests through each of two measurement methodologies, same underlying service behavior, same random seed.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Coordinated omission, measured](#3-coordinated-omission-measured)
4. [Why average latency is close to useless](#4-why-average-latency-is-close-to-useless)
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

A percentile (p50, p99, p99.9) states the latency below which a given fraction of requests fall — p99 = 500ms means 99% of requests were faster than 500ms. **Coordinated omission** is a measurement bug in load-testing tools: a naive ("closed-loop") load generator that waits for each request to finish before sending the next one systematically fails to measure the true cost of a slowdown, because it sends fewer requests exactly when the service is struggling.

## 2. Why it exists

Percentiles exist because averages hide exactly the information that matters for user experience and SLOs — a service can have a fast average and a genuinely painful tail. Coordinated omission exists as a concept because measuring percentiles correctly is harder than it looks: the natural way to write a load generator (send the next request when the last one returns) is precisely the wrong way, and this mistake is common enough to have its own name.

## 3. Coordinated omission, measured

**Real output**, the identical underlying service (98% of requests take 10ms, 2% stall for 500ms), measured two ways:

```
== closed-loop (naive): send next request only after the previous completes ==
closed-loop: p50=10ms  p90=10ms  p99=500ms  p99.9=500ms  max=500ms

== open-loop (correct): requests are scheduled every 50ms regardless of how long the previous one took ==
open-loop: p50=10ms  p90=380ms  p99=830ms  p99.9=1370ms  max=2110ms
```

Same service, same 2% stall rate, same random seed (so the exact same sequence of stalls occurs in both runs) — **p90 goes from 10ms to 380ms, and p99 from 500ms to 830ms, purely from correcting the measurement methodology.** The closed-loop generator's numbers are not wrong about each *individual* request's service time — they're wrong about the OVERALL EXPERIENCE, because they never account for the requests that pile up waiting behind a stall. When a 500ms stall occurs under open-loop scheduling (new requests keep arriving every 50ms regardless), roughly 10 requests queue up behind it and each pays not just its own service time but also the WAIT for the requests ahead of it to clear — that queueing delay is real, it's what a real user would experience, and coordinated omission is precisely the failure to measure it.

## 4. Why average latency is close to useless

Consider this chapter's own numbers: the closed-loop average is dominated by the 98% of fast requests (mostly 10ms) with a small contribution from the 2% slow ones — an average around `0.98*10 + 0.02*500 = 19.8ms`. **A single "19.8ms average" number is compatible with wildly different user experiences** — it can't distinguish "every user waits close to 19.8ms" from "98% of users wait 10ms and 2% wait 500ms" (this chapter's actual scenario) from a hypothetical where 50% wait near-0ms and 50% wait ~40ms. Percentiles distinguish these; a single average cannot. This is why SLOs (`05-performance-methodology-and-slo-error-budgets.md`) are almost always defined against a percentile (p99, p99.9), never an average.

## 5. Trade-offs

| Measurement approach | What it tells you |
|---|---|
| Average latency | Almost nothing about user experience — compatible with many very different real distributions |
| p50 (median) | Typical experience, insensitive to tail outliers |
| p99 / p99.9 | The experience of your most-affected users — directly what an SLO should target |
| Closed-loop load testing | Systematically understates tail latency under any real slowdown — measured directly above |
| Open-loop load testing | Correctly captures queueing delay from a slowdown — the honest number, and the harder one to implement correctly |

## 6. Interview questions

### Q1. Your load test shows p99 = 200ms, but users report much worse in production. Why the gap?

- **Expected answer:** the load test is very likely closed-loop (coordinated omission) — it understates real tail latency because it doesn't account for requests queueing behind a slowdown. Real production traffic is open-loop (users don't wait for the last request to finish before browsing again) and reveals the true, worse tail.
- **Common mistakes:** assuming the load-testing tool or environment is simply "less realistic" without naming the specific measurement bug.
- **Follow-up questions:** "How do you fix the load test?"
- **Senior-level expectations:** names coordinated omission by name and its root cause.
- **Staff-level expectations:** proposes a concrete fix (an open-loop load generator, or a coordinated-omission-corrected percentile calculation applied after the fact to closed-loop data) and can explain, numerically, roughly how large the understatement typically is for a given stall rate and duration — grounded in having seen a real measured gap like this chapter's own 500ms → 830ms p99 shift.

### Q2. Justify a percentile-based SLO instead of an average-based one.

- **Expected answer:** an average can't distinguish "uniformly mediocre" from "mostly great, occasionally terrible" — two experiences with very different user impact and very different fixes — while a percentile (especially a high one, p99/p99.9) directly targets the tail experience that actually drives complaints and churn.
- **Common mistakes:** defending an average-based SLO as "simpler to reason about" without addressing what it hides.
- **Follow-up questions:** "Why not target p100 (the max) directly?"
- **Senior-level expectations:** correctly rejects average-based SLOs.
- **Staff-level expectations:** explains why p100/max is a poor SLO target specifically — it's dominated by rare, often environmental, unrepresentative outliers (a single GC pause, a network blip) and chasing it produces diminishing, expensive returns; p99 or p99.9 targets the tail that's actually representative of a real, recurring user experience.

## 7. Common mistakes

- Reporting average latency as if it characterizes user experience.
- Load-testing with a closed-loop generator and treating the resulting percentiles as accurate.
- Chasing p100/max as an SLO target rather than a high-but-representative percentile like p99 or p99.9.

## 8. Staff-level discussion

The 500ms → 830ms p99 shift measured in this chapter (purely from correcting HOW latency was measured, not from changing the service at all) is a concrete instance of a broader Staff-level principle: **the measurement methodology is itself part of the system under evaluation, and getting it subtly wrong produces confidently-wrong conclusions rather than obviously-wrong ones** — the closed-loop numbers looked completely reasonable (a clean p99=500ms, exactly the stall duration) while being systematically misleading. A Staff engineer treats "how exactly was this number measured" as a first-class question before trusting any performance claim, in the same way `04-logging-metrics-tracing-and-opentelemetry.md`'s tracing discipline treats "which specific span in the call chain" as the first question before trusting a latency complaint's root cause.

## 9. Summary

Percentiles reveal what averages hide — this chapter's own scenario (98% fast, 2% very slow) produces an average that's compatible with many different real experiences, while p99 pins down the tail precisely. Coordinated omission is a real, measured methodology bug: a closed-loop load generator measuring the exact same service reported p99=500ms while the correct open-loop methodology reported p99=830ms and p90=380ms (versus the closed-loop's p90=10ms) — the same underlying service, a materially different, and more honest, picture of user experience.

## 10. Key Takeaways

- Average latency cannot distinguish "uniformly mediocre" from "mostly great, occasionally terrible" — percentiles can.
- Closed-loop load testing systematically understates tail latency by failing to measure queueing delay behind a slowdown — measured directly, not theoretical.
- Open-loop load testing (fixed request schedule regardless of response time) reveals the true tail.
- SLOs should target a high-but-representative percentile (p99/p99.9), not an average or the raw max.

## 11. Cheat Sheet

| Question | Answer |
|---|---|
| Average or percentile for an SLO? | Percentile — average hides too much |
| p99 or max? | p99 (or p99.9) — max is dominated by unrepresentative outliers |
| Closed-loop or open-loop load testing? | Open-loop — closed-loop understates the tail via coordinated omission |

## 12. Flashcards

1. **Q: What is coordinated omission?** A: A load-testing measurement bug where a closed-loop generator (waits for each response before sending the next) sends fewer requests exactly when the service is slow, understating the true tail latency.
2. **Q: Why can't average latency characterize user experience?** A: It can't distinguish "uniformly mediocre" from "mostly fast, occasionally very slow" — very different experiences can produce the same average.
3. **Q: Why is p99.9 usually a better SLO target than max/p100?** A: Max is dominated by rare, often environmental outliers (a single GC pause); p99.9 targets a representative tail experience without chasing unrepresentative extremes.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-11/percentiles/src/CoordinatedOmissionDemo.java`.
2. Change `STALL_PROBABILITY` and `STALL_LATENCY_MS` and predict, before running, roughly how the closed-loop vs. open-loop p99 gap should change.
3. Given a service with a 15% stall rate (stalls costing 500ms, normal requests costing 10ms), calculate the minimum safe interval between open-loop requests to keep the queue stable, and explain what happens to the measured percentiles if the interval is set too aggressively below that.

## 14. Additional Reading

- [Gil Tene — "How NOT to Measure Latency" (talk)](https://www.infoq.com/presentations/latency-response-time/) — the original coordinated-omission talk

## 15. Official References

- [HdrHistogram documentation](http://hdrhistogram.org/) — the standard library for coordinated-omission-corrected percentile recording in production load-testing tools
