---
title: "Mock Interview: Production Debugging Round (60 min)"
slug: production-debugging-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-09-29
target_levels:
  - mid
  - senior
  - staff
duration_minutes: 60
competencies:
  - Symptom-to-cause diagnostic method
  - Latency and throughput investigation
  - Memory leak diagnosis
  - Concurrency and locking failures
  - Data-access pathology (N+1)
  - Browser/API boundary failures
  - Scalability bottleneck identification
  - Accountability and failure narration
related:
  - ../../syllabus/13-observability/production-troubleshooting-methodology.md
  - ../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md
  - ../../syllabus/06-databases/optimistic-vs-pessimistic-locking.md
  - ../../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md
  - ../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
  - ../../syllabus/12-security/csrf-cors-and-session-security.md
  - ../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md
  - ../../syllabus/20-interview-preparation/behavioral/08-failure-and-learning-narratives.md
  - ../../production-cookbook/README.md
official_references: []
---

# Mock Interview: Production Debugging Round

**Target role:** Mid/Senior/Staff Backend Engineer · **Duration:** 60 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

This round is deliberately unlike the other technical rounds in this directory. Every other round asks about a *cause* — how G1 remembered sets work, what `@Transactional` propagation does. This one asks about *symptoms*, which is how these questions are actually posed, and the cause is precisely what the candidate does not yet know. The competency under test is diagnostic method, not recall.

The canonical treatment of the method itself is [Production Troubleshooting Methodology](../../syllabus/13-observability/production-troubleshooting-methodology.md); each question below also maps to the deep-dive chapter that owns its particular cause.

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
| Symptom-to-cause diagnostic method | Q1, Q2, Q4 | [Production Troubleshooting Methodology](../../syllabus/13-observability/production-troubleshooting-methodology.md) |
| Memory leak diagnosis | Q3 | [Memory Leak Diagnosis and Heap Dump Analysis](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md) |
| Lost update / concurrency control | Q5 | [Optimistic vs. Pessimistic Locking](../../syllabus/06-databases/optimistic-vs-pessimistic-locking.md) |
| Data-access pathology | Q6 | [JPA Entity Lifecycle and the N+1 Problem](../../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md) |
| Deadlock diagnosis and prevention | Q7 | [Deadlock, Race Conditions, and Thread Diagnostics](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) |
| Browser/API boundary | Q8 | [CSRF, CORS, and Session Security](../../syllabus/12-security/csrf-cors-and-session-security.md) |
| Scalability bottleneck identification | Q9 | [Capacity Planning and Headroom](../../syllabus/16-performance-jvm/capacity-planning-and-headroom.md) |
| Accountability and failure narration | Q10 | [Failure and Learning Narratives](../../syllabus/20-interview-preparation/behavioral/08-failure-and-learning-narratives.md) |

## Interviewer Opening Script

*"This is a 60-minute production debugging round. I'll give you ten scenarios. Nine are technical — I'll describe a symptom and ask what you'd do — and the last one is about your own experience. I'm not looking for you to guess the right cause on the first try. I want to hear your diagnostic sequence: what you'd check, in what order, and — this is the part I care most about — what each check would rule out. If you tell me you'd check something, I'll often say 'you checked, it looks normal' and ask what's next. That's not a trap; it's the actual job. Go ahead."*

## Candidate Section

Answer each aloud, unprompted, before reading the evaluator section. Record yourself. For every check you name, say what its result would eliminate — an answer that lists checks without stating what they discriminate scores at 3 at best.

1. **(6 min)** Your Java application runs perfectly in development but is extremely slow in production. How would you identify the actual root cause?
2. **(6 min)** Your Spring Boot application is returning 500 errors, but the logs don't show any obvious exception. Walk me through your troubleshooting approach.
3. **(6 min)** Your application consumes more and more memory and eventually throws `OutOfMemoryError`. How would you identify what is causing the leak?
4. **(6 min)** Your API was fine yesterday. Today response time went from 200 ms to 5 s. What do you check first, and how do you find the bottleneck?
5. **(6 min)** Two users update the same record at almost the same time, and one user's changes silently overwrite the other's. How would you handle this?
6. **(6 min)** Your Hibernate application makes hundreds of SQL queries for a single API request. How would you identify and fix it?
7. **(6 min)** Your production application starts throwing deadlock errors. How would you troubleshoot it and prevent recurrence?
8. **(5 min)** Your REST API works perfectly in Postman, but the frontend gets a CORS error. What is actually happening, and how do you fix it?
9. **(6 min)** Your application works fine with 100 users and falls over at 10,000. How do you find the bottleneck and make it scalable?
10. **(7 min)** Tell me about a real scenario where **you** introduced a bug or a failure in a Java application. What happened, what did you learn, and what did you change afterwards?

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Fast in dev, slow in production

**Ideal answer outline:** narrows before tooling — everything or one endpoint (shared versus scoped cause), step change or gradual slope, what changed. For latency specifically: is throughput flat under rising load (bounded resource, so optimising code will not help), and what do the *percentiles* say rather than the average. Rejects "production hardware is slower" as the default frame and names the two structural differences — **data volume** and **concurrency** — noting that both have effects a faster machine does not fix.

**Pass signal:** justifies each check by what it *eliminates*, not by what it might confirm. Bonus for the structural point that an N+1 and a batched implementation have different *curves*, so a dev dataset sits at the one point where they look identical (measured: 2.8 ms vs 0.5 ms at 10 rows, 1.28 s vs 0.5 ms at 5,000 — a 2531x gap).

**Common weak answers:** opening with "I'd attach a profiler." Listing twelve possible causes with no ordering. Proposing to reproduce in dev, failing, and concluding the report is wrong.

**Borderline signal:** a sensible order of checks, but no statement of what each rules out.

**Fail signal:** "the production server needs more resources."

### Question 2 — 500s with nothing in the logs

**Ideal answer outline:** the symptom has three distinguishable causes that differ in whether the evidence survives — **swallowed** in a bare `catch` (gone; needs a code change), **wrong level** (recoverable by config alone, often without a deploy), **unlogging catch-all `@ExceptionHandler`** (needs a change to the advice). Raising the application package to `DEBUG` is the discriminating step: it *partitions* rather than merely producing more output. Before that, reading the error body's **shape** is free — a container-default body (`timestamp`/`status`/`error`/`path`) means the failure happened in a filter, outside the `DispatcherServlet`, where `@ControllerAdvice` never runs.

**Pass signal:** names at least two causes and treats the level change as a discriminator. Bonus for checking the access log first to establish the requests are even arriving.

**Common weak answers:** blaming the log shipper. "I'd add more logging" without partitioning. Proposing to grep for empty `catch` blocks as the *first* step rather than after the level change came back silent.

**Borderline signal:** suspects a swallowed exception but treats it as the only possibility.

**Fail signal:** "check the logging configuration," full stop.

### Question 3 — Growing memory, eventual OOM

**Ideal answer outline:** distinguishes a leak from a warming cache using spaced samples before doing anything expensive. Then the cheap tool first — `jcmd <pid> GC.class_histogram` to find which type is growing — and only then a heap dump to find what **retains** it, since a histogram cannot answer that. Fixes by breaking the specific reference. Explicitly states that raising `-Xmx` is mitigation that buys time, not a fix.

**Pass signal:** correct histogram-then-dump sequencing with the reason for the ordering, plus the retention concept.

**Common weak answers:** "increase the heap." Taking a full heap dump as step one.

**Borderline signal:** names "memory leak" and heap dumps but cannot sequence the diagnosis or explain retention.

**Fail signal:** more memory as the complete answer.

**Follow-up if they pass:** "Heap looks flat but the container keeps getting OOM-killed." (Off-heap: direct buffers, metaspace, native libraries, thread stacks. `jcmd VM.native_memory summary`. See `production-cookbook/direct-buffer-oom-invisible-to-heap-monitoring.md`.)

### Question 4 — 200 ms to 5 s overnight

**Ideal answer outline:** same triage. "Yesterday it was fine" is a step change, so the highest-yield question is what changed — deploy, config, feature flag, data migration, traffic, or a dependency's own incident. Scope it: one endpoint or all. Then the two latency-specific checks: flat throughput (bounded resource) and percentiles rather than average.

**Pass signal:** asks what changed *before* proposing any tool, and scopes the blast radius.

**Common weak answers:** jumping to "it's the database" with no evidence. Checking average latency.

**Borderline signal:** right instincts but no explicit ordering.

**Fail signal:** proposes a restart as the first diagnostic step. (Worth naming: a restart frequently clears the symptom *and* destroys the evidence.)

### Question 5 — Lost update between two concurrent writers

**Ideal answer outline:** names it as a **lost update** from a read-modify-write race, not a generic "concurrency issue." Proposes optimistic locking (a version column, `@Version`; the second writer's update matches zero rows and fails loudly) as the default, and pessimistic locking (`SELECT ... FOR UPDATE`) when contention is genuinely high and the transaction is short. States the real trade-off: optimistic pushes the failure to the user as a retry, pessimistic holds a lock and risks queueing or deadlock.

**Pass signal:** names lost update precisely and gives the version-column mechanism, including that the *detection* is "zero rows updated."

**Common weak answers:** `synchronized` on the service method — which is wrong across multiple instances and is a genuinely revealing answer.

**Borderline signal:** proposes a transaction without explaining that a default isolation level does not prevent this.

**Fail signal:** "use a transaction," with no mechanism.

**Follow-up if they pass:** "Your service runs on six pods. Does your fix still work?" (Yes for a database-level version check; no for anything in-process.)

### Question 6 — Hundreds of queries for one request

**Ideal answer outline:** recognises the N+1 signature immediately. Identifies it by enabling SQL logging or reading the count from an APM/trace rather than by inspecting code. Fixes with a `JOIN FETCH`, an entity graph, or a batched second query — and knows that fetching two collections eagerly in one query produces a **Cartesian product** and can be worse than the N+1 it replaced.

**Pass signal:** names N+1, gives a concrete detection method and a correct fix, and knows at least one way the naive fix backfires.

**Common weak answers:** switching everything to `FetchType.EAGER`, which moves the problem and often worsens it.

**Borderline signal:** recognises N+1 but proposes only eager fetching.

**Fail signal:** does not recognise the pattern from "hundreds of queries for one request."

**Follow-up if they pass:** "It's fine in staging. Why?" (Row count. This connects directly back to Q1's measured 2531x curve.)

### Question 7 — Deadlocks in production

**Ideal answer outline:** takes a **thread dump** (`jcmd <pid> Thread.print`) — the JVM detects Java-monitor deadlocks and names the cycle explicitly, so this is not a manual read. For database deadlocks, reads the database's own deadlock log, which reports the two statements. Root cause is almost always **inconsistent lock ordering**; the prevention is a globally consistent acquisition order, plus shorter transactions and lock timeouts so a cycle fails fast rather than hanging.

**Pass signal:** names the thread dump and the explicit JVM deadlock report, and identifies lock ordering as the structural fix rather than as a patch.

**Common weak answers:** "add a retry." Restarting as the fix.

**Borderline signal:** knows lock ordering matters but cannot name a diagnostic.

**Fail signal:** no diagnostic and no structural prevention.

### Question 8 — Works in Postman, CORS error in the browser

**Ideal answer outline:** CORS is a **browser** policy, not a server one — Postman is not a browser and never enforces it, so "works in Postman" is expected and tells you nothing. The browser sends a preflight `OPTIONS` for non-simple requests and requires the response's `Access-Control-Allow-Origin` (and `-Headers`, `-Methods`, and `-Credentials` when cookies are involved) to permit the origin. Fix on the **server** with a proper CORS configuration.

**Pass signal:** identifies it as browser-enforced, mentions preflight, and fixes it server-side.

**Common weak answers:** "disable CORS" or `allowedOrigins("*")` together with credentials — which the specification forbids and browsers reject, and which is worth probing.

**Borderline signal:** knows the fix is a header but cannot explain preflight or why Postman differs.

**Fail signal:** treats CORS as an authentication or server-side security control. (It is neither — see `production-cookbook/cors-mistaken-for-an-authentication-boundary-on-an-internal-endpoint.md`.)

### Question 9 — Fine at 100 users, falls over at 10,000

**Ideal answer outline:** measures the shape rather than guessing. Runs increasing concurrency and watches throughput and percentiles together. **Flat throughput under rising load** means a bounded resource — connection pool, thread pool, rate limit, downstream capacity — and neither faster code nor more application instances behind that same resource will help. Measured: with a pool of 10, throughput flattened at roughly 7,600 req/s from concurrency 10 onward and was no better at 200, while p50 stayed at **1 ms** and p99 went to **152 ms**.

**Pass signal:** names the flat-throughput signature, reads percentiles rather than averages, and knows that scaling out behind a shared bounded resource changes nothing.

**Common weak answers:** "add more instances" or "add a cache" with no identification of what saturates.

**Borderline signal:** proposes load testing but cannot say what to look for in the results.

**Fail signal:** horizontal scaling as a reflex.

**Follow-up if they pass:** "You doubled the pool and latency got *worse*. Why?" (CPU saturation — more concurrent work on a saturated CPU adds context switching and queueing. See `production-cookbook/doubling-the-connection-pool-made-latency-worse-under-cpu-saturation.md`.)

### Question 10 — A failure you caused

**Ideal answer outline:** a real, specific, non-trivial failure, owned without either minimising it into a disguised strength or over-correcting into excessive self-blame. Concrete impact. A lesson that changed a *system* — a test, a check, a default, a runbook — rather than only a resolution to be more careful. The STAR-L structure from [Failure and Learning Narratives](../../syllabus/20-interview-preparation/behavioral/08-failure-and-learning-narratives.md).

**Pass signal:** specific and real; a systemic change resulted; the candidate can answer "what would you do differently" without repeating the lesson verbatim.

**Common weak answers:** a disguised strength ("I cared too much about quality"). A failure attributed entirely to someone else or to process. A trivial failure chosen to seem safe.

**Borderline signal:** genuine failure, but the lesson is "I'll be more careful" — a resolution, not a change.

**Fail signal:** cannot produce one, or blames others throughout.

**Note for the evaluator:** this question is scored on accountability and engineering maturity, not on the severity of the incident. A small failure with a real systemic fix outscores a large one with only a personal resolution.

## Scoring Rubric

Same 1–5 scale as the other rounds in this directory, with the criteria adapted to a diagnostic round:

| Score | Meaning |
|---|---|
| 1 | No coherent approach, or a factually wrong one |
| 2 | Names plausible causes, but no ordering and no diagnostic |
| 3 | A correct, ordered diagnostic sequence — Senior bar met |
| 4 | Sequence plus an explicit statement of what each check *eliminates* |
| 5 | All of the above, plus a Staff-level extension (prevention, platform default, or the trade-off the obvious fix carries) |

**Pass threshold for this mock:** average ≥ 3.5 across all ten questions, with no individual score below 2. Q10 is scored on the same scale but against the accountability criteria in its own section.

## Debrief Guide

Walk the candidate through their scores starting with the lowest. Two cross-cutting patterns matter more than any individual score, and should be named explicitly if they appear:

**Pattern A — tool-first reflex.** If the candidate opened Q1, Q2, Q3, or Q4 by naming a tool ("I'd attach a profiler," "I'd take a heap dump," "I'd check the logs") before narrowing the problem, that is one habit showing up four times, not four separate gaps. It is also the single highest-leverage thing to fix, because the three triage questions cost nothing and eliminate most of the candidate space.

**Pattern B — confirming rather than discriminating.** If the candidate's checks would each be consistent with most of their own hypotheses, they are looking for confirmation. The tell is an inability to answer "and if that came back normal, what would you have learned?" Push on it directly during the debrief.

Also worth noting if it appears: a candidate who scored well on Q5–Q8 (specific, known causes) and poorly on Q1, Q2, Q4, and Q9 (open-ended symptoms) has strong recall and weak method. That is a common and very fixable profile, and it is exactly the gap this round exists to expose.

## Remediation Recommendations

- Any score ≤ 2 on Q1, Q2, Q4, or Q9, or Pattern A/B in the debrief → work through [Production Troubleshooting Methodology](../../syllabus/13-observability/production-troubleshooting-methodology.md) in full, including running both demos in [`practice/java/observability/production-troubleshooting/`](../java/observability/production-troubleshooting/README.md) and predicting each result before running it.
- Any score ≤ 2 on Q3 → [Memory Leak Diagnosis and Heap Dump Analysis](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md), and redo the histogram-sampling exercise.
- Any score ≤ 2 on Q5 → [Optimistic vs. Pessimistic Locking](../../syllabus/06-databases/optimistic-vs-pessimistic-locking.md), plus `production-cookbook/lost-update-in-a-get-then-put-counter-increment.md`.
- Any score ≤ 2 on Q6 → [JPA Entity Lifecycle and the N+1 Problem](../../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md), including the Cartesian-product warning.
- Any score ≤ 2 on Q7 → [Deadlock, Race Conditions, and Thread Diagnostics](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md).
- Any score ≤ 2 on Q8 → [CSRF, CORS, and Session Security](../../syllabus/12-security/csrf-cors-and-session-security.md).
- Any score ≤ 2 on Q10 → [Failure and Learning Narratives](../../syllabus/20-interview-preparation/behavioral/08-failure-and-learning-narratives.md) and [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md); draft two candidate stories and check them against the self-review checklist.
- Below the 3.5 pass threshold overall → retake this mock in full after remediation. Re-reading alone does not fix a method gap; the demos' predict-then-verify loop is the part that does.
