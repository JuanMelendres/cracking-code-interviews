---
title: "Flashcards: Batch and Scheduled Job Design"
slug: batch-and-scheduled-job-design
document_type: flashcard-deck
domain: 11-system-design
topic_id: T-2443
canonical: ../syllabus/11-system-design/batch-and-scheduled-job-design.md
last_updated: 2026-09-29
---

# Flashcards: Batch and Scheduled Job Design

**Canonical chapter:** [`syllabus/11-system-design/batch-and-scheduled-job-design.md`](../syllabus/11-system-design/batch-and-scheduled-job-design.md)

## Card: What happens to a @Scheduled job on three replicas?

**Prompt:**
A `@Scheduled` nightly job is deployed on three instances. What happens?

**Answer:**
It runs three times, concurrently. Measured: three instances against 1,000 records produced **3,000** side effects, with no error and a success report from all three. Application-level schedulers fire on every instance that has the code.

**Why it matters:**
It is invisible until the second replica exists — it works in development and on a one-pod staging environment, and breaks the first time someone scales the deployment, a change nobody associates with a job.

**Common trap:**
Proposing `synchronized` or any in-process guard, which does nothing across instances.

**Related:**
[Core Concepts](../syllabus/11-system-design/batch-and-scheduled-job-design.md#core-concepts)

## Card: The job lock, in one statement

**Prompt:**
What is the minimum mechanism that makes a scheduled job run on exactly one instance, and what does it need besides the lock itself?

**Answer:**
A conditional `UPDATE`: `UPDATE job_lock SET held_by = ? WHERE name = ? AND held_by IS NULL`. It returns 1 for exactly one instance and 0 for the rest, atomic via the database's own row-level locking, with no check-then-act window. It also needs a **lease expiry** — a `locked_at` timestamp plus a maximum hold time.

**Why it matters:**
A lock without expiry converts duplicate execution into the job never running again, which is a worse failure because it is even quieter.

**Common trap:**
Reaching for a coordination service. A job that runs once a night does not need one; the database already in the system is sufficient.

**Related:**
[Core Concepts](../syllabus/11-system-design/batch-and-scheduled-job-design.md#core-concepts)

## Card: Why chunk size is not a performance setting

**Prompt:**
How should you choose a batch job's commit interval?

**Answer:**
From the acceptable rework after a crash, not from throughput. The work at risk in a crash is exactly one chunk, by construction. Measured over 10,000 rows: chunk 1 took 148 ms, chunk 10,000 took 16 ms — **9.25x** faster — with worst-case loss going from 0 rows to 9,999.

**Why it matters:**
Framing it as tuning leads to "the largest chunk that fits in memory," which maximises rework. The right question is how much repeated work is acceptable, usually answerable in seconds or minutes.

**Common trap:**
Ignoring the second cost — a chunk is a transaction, so on a busy database the biggest chunk is often not the fastest in practice.

**Related:**
[Core Concepts](../syllabus/11-system-design/batch-and-scheduled-job-design.md#core-concepts)

## Card: The one detail that makes a checkpoint work

**Prompt:**
Where must a batch job's checkpoint be written, and what breaks if it is written elsewhere?

**Answer:**
In the **same transaction** as the chunk it describes. Separate commits reintroduce a dual write: a crash between them leaves the checkpoint ahead of the work (records silently skipped on restart) or behind it (records reprocessed). A log file, an in-memory field, or a separate service all fail for the same reason.

**Why it matters:**
Measured, the payoff is concrete: a single-transaction job crashing at 60% committed **zero** and cost 1,599 units of work to commit 1,000; the chunked version committed 500, resumed at 501, and wasted exactly 99.

**Common trap:**
Believing the checkpoint makes the job safe. It makes it *restartable*; only idempotent per-record processing makes reprocessing safe.

**Related:**
[Core Concepts](../syllabus/11-system-design/batch-and-scheduled-job-design.md#core-concepts)

## Card: When is skipping a bad record safe?

**Prompt:**
A job hits one malformed record. Under what conditions is skipping it and continuing acceptable?

**Answer:**
Two, both required: the skip count is an **alerted metric**, and skipped records land somewhere they can be inspected and replayed. Measured contrast: fail-fast stopped at record 500 leaving 500 unprocessed and was loud; skip processed 999, reported success, and the dropped record existed only as a counter.

**Why it matters:**
Skip's failure mode looks like success. If nothing reads the counter, the failure is invisible permanently — and the same code reports success whether 1 or 400 records were skipped, unless the skip *rate* is what is alerted on.

**Common trap:**
Choosing skip because it "finishes," without treating it as the monitoring commitment it is.

**Related:**
[Core Concepts](../syllabus/11-system-design/batch-and-scheduled-job-design.md#core-concepts)
