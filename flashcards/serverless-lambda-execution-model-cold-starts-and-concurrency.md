---
title: "Flashcards: Serverless Cold Starts"
slug: serverless-lambda-execution-model-cold-starts-and-concurrency
document_type: flashcard-deck
domain: 15-cloud
topic_id: T-2425
canonical: ../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md
last_updated: 2026-09-21
---

# Flashcards: Serverless Compute Cold Starts

**Canonical chapter:** [`syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md`](../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md)

## Card: What a cold start actually is

**Prompt:**
What actually happens during a Lambda cold start, mechanically?

**Answer:**
A fresh execution environment completes the INIT phase — loading the runtime and code, running static/global initializers, sometimes opening a database connection — before the INVOKE phase (the handler function) can run for the first time. Every subsequent invocation on that same warm environment skips INIT entirely.

**Why it matters:**
"Lambda has cold starts" is a memorized fact; explaining the INIT/INVOKE mechanism is the actual signal of hands-on understanding.

**Common trap:**
Describing cold start vaguely as "Lambda being slow sometimes" instead of naming the specific one-time, per-environment cost.

**Related:**
[Definition and Purpose](../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md#definition-and-purpose)

## Card: Why concurrency multiplies cold starts

**Prompt:**
Does a burst of 100 concurrent requests against a cold function produce one cold start followed by 99 fast requests?

**Answer:**
No. Every concurrent request beyond the number of currently-warm environments triggers its own, independent cold start, run in parallel with the others — up to 100 simultaneous cold starts, each paying the full INIT cost.

**Why it matters:**
The most common misconception about cold starts — that they're a one-time, not a per-concurrent-request, cost.

**Common trap:**
Assuming cold start only affects the first request after a traffic lull.

**Related:**
[Core Concepts](../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md#core-concepts)

## Card: Real measured evidence — cold vs. warm

**Prompt:**
What did a real Java simulation measure for cold vs. warm Lambda invocations?

**Answer:**
A cold invocation (fresh execution environment construction + first invoke) measured ~176ms, versus ~0.009ms for a warm invocation reusing that environment — a real 20,000x-plus difference. A 5-way concurrent burst against zero warm environments measured ~236ms wall-clock, versus ~0.08ms for a repeat burst against already-warm ones.

**Why it matters:**
Turns the abstract cold-start concept into concrete, reproducible, measured evidence, using real JVM class-loading work as a technically substantiated proxy (the dominant real contributor to cold-start cost for an actual JVM-based Lambda function).

**Common trap:**
Treating cold-start cost as a rough estimate rather than something with a real, measurable, orders-of-magnitude structure.

**Related:**
[Internal Implementation](../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md#internal-implementation)
