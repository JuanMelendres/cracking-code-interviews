---
title: "Flashcards: CompletableFuture and Async Composition"
slug: completablefuture-and-async-composition
document_type: flashcard-deck
domain: concurrency
topic_id: T-407
canonical: ../handbook/concurrency/completablefuture-and-async-composition.md
last_updated: 2026-09-02
---

# Flashcards: CompletableFuture and Async Composition

**Canonical chapter:** [`syllabus/02-java/concurrency/completablefuture-and-async-composition.md`](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md)

## Card: Attach-timing threading rule

**Prompt:**
If you attach `thenApply` to a `CompletableFuture` that's already complete, what thread runs the callback?

**Answer:**
The thread that attached it — synchronously, inline, no dispatch at all.

**Why it matters:**
The least-expected of the three real threading behaviors, and the one most candidates get wrong.

**Common trap:**
Assuming `thenApply` always behaves like `thenApplyAsync` in terms of thread dispatch.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md#internal-implementation)

## Card: Fire-and-forget exceptions

**Prompt:**
What happens to an exception thrown inside a `CompletableFuture` pipeline that nothing ever calls `get()`/`join()` on?

**Answer:**
It's stored in the future and silently discarded — no stack trace, no log line, no signal at all.

**Why it matters:**
A real, undetectable-by-default failure mode in production code.

**Common trap:**
Believing an uncaught exception "surfaces somewhere" automatically.

**Related:**
[Production Scenarios](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md#production-scenarios)

## Card: Accidental serialization

**Prompt:**
How do you accidentally turn two independent async calls into a sequential ~2x-slower pipeline?

**Answer:**
Call `get()`/`join()` on the first future before submitting the second.

**Why it matters:**
Measured directly at 614ms sequential vs. 313ms concurrent for two 300ms calls.

**Common trap:**
Believing code "uses `CompletableFuture`" is sufficient for it to be concurrent.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/completablefuture-and-async-composition.md#internal-implementation)
