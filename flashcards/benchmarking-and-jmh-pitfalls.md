---
title: "Flashcards: Benchmarking & JMH Pitfalls"
slug: benchmarking-and-jmh-pitfalls
document_type: flashcard-deck
domain: jvm
topic_id: T-1203
canonical: ../handbook/jvm/benchmarking-and-jmh-pitfalls.md
last_updated: 2026-09-01
---

# Flashcards: Benchmarking & JMH Pitfalls

**Canonical chapter:** [`handbook/jvm/benchmarking-and-jmh-pitfalls.md`](../handbook/jvm/benchmarking-and-jmh-pitfalls.md)

## Card: Why discarding a computed value fakes a fast benchmark

**Prompt:**
Why does discarding a computed value in a JMH benchmark produce a fictitiously fast result?

**Answer:**
The JIT can prove the discarded computation has no observable effect on the program and eliminate it entirely (dead-code elimination) — the benchmark ends up measuring an empty method body, not the intended computation.

**Why it matters:**
Explains the actual root cause behind a classic JMH pitfall — a benchmark can report an impossibly fast number without erroring, so knowing the mechanism is what lets you recognize a suspicious result.

**Common trap:**
Writing a benchmark method that computes a value but never uses it, unaware the JIT is allowed to delete the entire computation.

**Related:**
[handbook/jvm/benchmarking-and-jmh-pitfalls.md](../handbook/jvm/benchmarking-and-jmh-pitfalls.md)

## Card: The actual fix for dead-code elimination

**Prompt:**
What is the actual mechanism that prevents dead-code elimination in a correctly written JMH benchmark?

**Answer:**
Returning the computed value (which JMH automatically passes to an internal `Blackhole`) or explicitly calling `Blackhole.consume()` — either gives the value an observable use the JIT cannot optimize away.

**Why it matters:**
Gives the concrete, actionable fix rather than a vague "add a blackhole" instruction — return the value or call `Blackhole.consume()` explicitly.

**Common trap:**
Assuming any benchmark that "runs without errors" is measuring real work, rather than checking that the computed value has an observable use.

**Related:**
[handbook/jvm/benchmarking-and-jmh-pitfalls.md](../handbook/jvm/benchmarking-and-jmh-pitfalls.md)

## Card: Why a documented pitfall might not reproduce on your JVM

**Prompt:**
Why might a documented benchmarking pitfall (like constant folding) fail to reproduce on your JVM even though it's real?

**Answer:**
Its manifestation depends on the specific operation, the JVM version, and what the JIT can actually prove about the surrounding loop shape — a pitfall being real and well-documented doesn't guarantee it manifests identically on every JVM/configuration.

**Why it matters:**
A direct, honestly-reported finding from this chapter's own demo — grounds the general lesson that empirical verification beats trusting documented pitfalls unconditionally.

**Common trap:**
Citing a well-known benchmarking pitfall as universally reproducible rather than verifying it against the specific JVM/version/operation in front of you.

**Related:**
[handbook/jvm/benchmarking-and-jmh-pitfalls.md](../handbook/jvm/benchmarking-and-jmh-pitfalls.md)
