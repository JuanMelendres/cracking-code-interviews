---
title: "Flashcards: Production Troubleshooting Methodology"
slug: production-troubleshooting-methodology
document_type: flashcard-deck
domain: 13-observability
topic_id: T-2442
canonical: ../syllabus/13-observability/production-troubleshooting-methodology.md
last_updated: 2026-09-29
---

# Flashcards: Production Troubleshooting Methodology

**Canonical chapter:** [`syllabus/13-observability/production-troubleshooting-methodology.md`](../syllabus/13-observability/production-troubleshooting-methodology.md)

## Card: The three triage questions

**Prompt:**
Before reaching for any tool, what three questions narrow a production symptom the most?

**Answer:**
Is it everything or one endpoint (shared cause versus scoped cause)? Was it a step change or a gradual slope (discrete event versus something growing)? What changed around that time (deploy, config, flag, migration, traffic)?

**Why it matters:**
Each one roughly halves the candidate list, costs nothing, and needs no special tooling. Reaching for a profiler before answering them means profiling without knowing what you are looking for.

**Common trap:**
Answering a scenario question with a list of possible causes instead of an order of checks.

**Related:**
[Level 2 — Working Knowledge](../syllabus/13-observability/production-troubleshooting-methodology.md#level-2-working-knowledge)

## Card: What a flat throughput line means

**Prompt:**
Under rising load, throughput plateaus while latency climbs. What does that tell you, and what will not fix it?

**Answer:**
A bounded resource is the ceiling — a connection pool, a thread pool, a rate limit, or a downstream's own capacity. Measured: with a pool of 10, throughput flattened at roughly 7,600 req/s from concurrency 10 onward and was no better at 200. Optimising the code will not help, and neither will adding application instances behind the *same* bounded resource.

**Why it matters:**
It redirects the whole investigation away from the code, which is where most people look first.

**Common trap:**
Answering "scale horizontally" without identifying what is saturated — more instances behind one shared pool change nothing.

**Related:**
[Core Concepts](../syllabus/13-observability/production-troubleshooting-methodology.md#core-concepts)

## Card: Why the median can be blind

**Prompt:**
A system is badly saturated. What did its p50 and p99 actually do in the measured run?

**Answer:**
p50 stayed at **1 ms** at every concurrency level including 200, while p99 went from 1 ms to **152 ms**. The extra time is queueing, distributed unevenly, so it lands entirely on the tail.

**Why it matters:**
A dashboard showing average or median latency reports this system as perfectly healthy while one request in a hundred is 152 times slower. It is the concrete reason to alert on p99.

**Common trap:**
"Check the average latency" as a diagnostic step.

**Related:**
[Percentiles, Tail Latency, and Coordinated Omission](../syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md)

## Card: Why dev could not have caught it

**Prompt:**
Why is "we should have tested with more data" an incomplete explanation of a dev-versus-prod performance gap?

**Answer:**
Because the two implementations have different **curves**, not different constants. Measured: naive N+1 versus batched was 2,809 us versus 510 us at 10 rows (6x, both instant) and 1,283,121 us versus 507 us at 5,000 rows (**2531x**). The naive version is linear in row count; the batched one is flat. A dev dataset sits at the one point where they look alike, and a faster machine moves both curves down without closing the gap.

**Why it matters:**
It makes production-scale pre-production data an infrastructure decision with a measurable cost argument, not a matter of individual discipline.

**Common trap:**
Concluding "the production hardware is slower."

**Related:**
[Core Concepts](../syllabus/13-observability/production-troubleshooting-methodology.md#core-concepts)

## Card: The three causes of "nothing in the logs"

**Prompt:**
A Spring Boot app returns 500s with no log line. Name the three causes and what distinguishes them.

**Answer:**
Swallowed in a bare `catch` (evidence **gone** — needs a code change); logged at a level production does not emit (**recoverable by config alone**); or a catch-all `@ExceptionHandler` that standardises the response and logs nothing (needs a change to the advice). Raising the application package to `DEBUG` discriminates: verified directly, it recovered the wrong-level case and **only** that one.

**Why it matters:**
The three differ in whether the evidence can be recovered without a deploy — which is the first thing an on-call engineer needs to know.

**Common trap:**
Blaming the log shipper or the logging configuration before checking whether the requests appear in the access log at all.

**Related:**
[Core Concepts](../syllabus/13-observability/production-troubleshooting-methodology.md#core-concepts)

## Card: Which tool first, and why that order?

**Prompt:**
You suspect a memory leak. Why is a heap dump the wrong first tool, and what comes before it?

**Answer:**
A heap dump pauses the application and produces a file the size of the heap. `jcmd <pid> GC.class_histogram` is far cheaper and usually identifies the growing type on its own. The dump is only needed for the question a histogram cannot answer: what **retains** those objects. More generally — cheap, always-on signals (access logs, metrics, GC logs, traces) narrow the *where*; expensive, perturbing tools (heap dump, profiler) identify the *what*, once the where is narrow enough.

**Why it matters:**
Reaching for the expensive tool first is the most common way an investigation becomes a long one.

**Common trap:**
Also worth knowing: if heap looks flat but the container is OOM-killed, the growth is off-heap — `jcmd VM.native_memory summary`, not another heap dump.

**Related:**
[Memory Leak Diagnosis and Heap Dump Analysis](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)

## Card: When is "add a log line" the right answer?

**Prompt:**
In an interview, is "I'd add logging and wait for it to happen again" a weak answer?

**Answer:**
Not inherently. When the evidence to discriminate between your hypotheses does not exist, shipping a log line or a metric is a legitimate and often optimal next step — the alternative is guessing. It is weak only when it replaces narrowing: "add logging everywhere" is a non-answer, while "the access log will tell us whether these requests even reach this service, which eliminates two of my four hypotheses" is a diagnostic.

**Why it matters:**
Candidates often avoid saying it, believing they are expected to name a cause. Interviewers are listening for method, and making a system observable is method.

**Common trap:**
Proposing to add logging *before* checking whether the request appears in the access log at all.

**Related:**
[Best Practices](../syllabus/13-observability/production-troubleshooting-methodology.md#best-practices)

