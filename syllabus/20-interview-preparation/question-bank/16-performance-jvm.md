---
title: "Interview Question Bank — 16-performance-jvm"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../16-performance-jvm/INDEX.md
  - 02-java-jvm-internals.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Performance and JVM

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 3 chapters yielded 6 deep questions + 9 quick-fire
questions = **15 real questions**. No Junior Fundamentals chapter exists in this
domain — this is a genuinely small domain (only 3 chapters), a companion to
[`02-java-jvm-internals.md`](02-java-jvm-internals.md) targeting Senior/Staff
production-performance work specifically. Two of three chapters use a plain
`**Q:** ... **A:** ...` Flashcards format instead of the `### Card:` template.

---

## Benchmarking & JMH Pitfalls

### Q1 — A teammate shows you a benchmark claiming Optimization A is 40% faster than the current code. What do you check before believing it?

**Canonical treatment:** [§ Interview Questions, Q1](../../16-performance-jvm/benchmarking-and-jmh-pitfalls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Says "I'd run it again to see if I get the same number" without naming any specific methodological check — the common mistake this question targets.
- **Senior:** Names dead-code elimination and constant folding specifically, and explains how each could produce a fictitious 40% number.
- **Staff:** Proposes a standing team practice (linking performance claims to real, reviewable benchmark code) rather than re-litigating this one benchmark in isolation.

### Q2 — Why does JMH require forking a new JVM process for each benchmark by default, and when would you turn that off?

**Canonical treatment:** [§ Interview Questions, Q2](../../16-performance-jvm/benchmarking-and-jmh-pitfalls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States "it's just JMH's default setting" with no explanation of why — the common mistake this question targets.
- **Senior:** Explains the specific contamination risk (shared JIT profile/compilation state across benchmarks in one process) and names the real trade-off of disabling it.
- **Staff:** Connects this to the general principle that benchmark isolation and production-representativeness are in tension with iteration speed, and that the right trade-off differs between exploratory development and a number cited in a decision document.

---

## Capacity Planning & Headroom

### Q1 — A service's throughput dashboard looks healthy, but users are reporting slow page loads. How do you investigate?

**Canonical treatment:** [§ Interview Questions, Q1](../../16-performance-jvm/capacity-planning-and-headroom.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to "scale up" without first confirming the diagnosis via latency percentiles and utilization — the common mistake this question targets.
- **Senior:** Explicitly names the throughput/latency divergence and checks utilization on the specific bottleneck resource before recommending a fix.
- **Staff:** Proposes a standing load-testing practice and a specific headroom target going forward, not just a one-time fix for this incident.

### Q2 — How would you decide how much headroom to provision for a new service before it launches?

**Canonical treatment:** [§ Interview Questions, Q2](../../16-performance-jvm/capacity-planning-and-headroom.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a guessed instance count with no stated ceiling or utilization target behind it — the common mistake this question targets.
- **Senior:** Explicitly separates "finding the ceiling" from "choosing the safety margin," and gives a concrete utilization number with a stated reason for it.
- **Staff:** Discusses re-evaluation cadence and the organizational cost of getting the target wrong in either direction — over-provisioning as invisible waste, under-provisioning as a launch-day incident risk.

---

## Profiling: async-profiler, JFR, and Flame Graphs

### Q1 — Walk me through how you'd find a CPU hotspot in a production service.

**Canonical treatment:** [§ Interview Questions, Q1](../../16-performance-jvm/profiling-jfr-and-flame-graphs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names a profiling tool without describing the sampling mechanism or how to interpret results.
- **Senior:** Correctly names JFR (or async-profiler) and explains reading a flame graph by frame width, rather than starting from a code-inspection-based assumption.
- **Staff:** Connects this to a real or realistic story where the profile contradicted the initial code-review-based hypothesis.

### Q2 — What's the difference between JFR and async-profiler?

**Canonical treatment:** [§ Interview Questions, Q2](../../16-performance-jvm/profiling-jfr-and-flame-graphs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names both tools without a clear differentiator — the common mistake this question targets.
- **Senior:** States the built-in-vs-agent distinction and the native-frame visibility difference — JFR ships with the JDK at low overhead; async-profiler is a separate native agent with typically lower overhead and better native-frame visibility.
- **Staff:** Gives a concrete scenario where each tool's specific strength would be the deciding factor.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Why does discarding a computed value in a JMH benchmark produce a fictitiously fast result? | [Benchmarking & JMH Pitfalls](../../16-performance-jvm/benchmarking-and-jmh-pitfalls.md#flashcards) |
| 2 | What is the actual mechanism that prevents dead-code elimination in a correctly written JMH benchmark? | [Benchmarking & JMH Pitfalls](../../16-performance-jvm/benchmarking-and-jmh-pitfalls.md#flashcards) |
| 3 | Why might a documented benchmarking pitfall (like constant folding) fail to reproduce on your JVM even though it's real? | [Benchmarking & JMH Pitfalls](../../16-performance-jvm/benchmarking-and-jmh-pitfalls.md#flashcards) |
| 4 | What does Little's Law state, and what makes it broadly useful for capacity planning? | [Capacity Planning & Headroom](../../16-performance-jvm/capacity-planning-and-headroom.md#flashcards) |
| 5 | Why can a throughput dashboard look "healthy" during a real capacity incident? | [Capacity Planning & Headroom](../../16-performance-jvm/capacity-planning-and-headroom.md#flashcards) |
| 6 | Why is utilization-to-latency non-linear near saturation? | [Capacity Planning & Headroom](../../16-performance-jvm/capacity-planning-and-headroom.md#flashcards) |
| 7 | In a flame graph, does a taller stack or a wider frame indicate a hotspot? | [Profiling: async-profiler, JFR, Flame Graphs](../../16-performance-jvm/profiling-jfr-and-flame-graphs.md#flashcards) |
| 8 | What did this chapter's own real profiling run find, and why does it matter? | [Profiling: async-profiler, JFR, Flame Graphs](../../16-performance-jvm/profiling-jfr-and-flame-graphs.md#flashcards) |
| 9 | When would you reach for async-profiler instead of JFR? | [Profiling: async-profiler, JFR, Flame Graphs](../../16-performance-jvm/profiling-jfr-and-flame-graphs.md#flashcards) |

---

## Related

- [`02-java-jvm-internals.md`](02-java-jvm-internals.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
