---
title: "Flashcards: Performance and Load Testing Methodology"
slug: performance-and-load-testing-methodology
document_type: flashcard-deck
domain: testing
topic_id: T-1106
canonical: ../handbook/testing/performance-and-load-testing-methodology.md
last_updated: 2026-08-06
---

# Flashcards: Performance and Load Testing Methodology

**Canonical chapter:** [`handbook/testing/performance-and-load-testing-methodology.md`](../handbook/testing/performance-and-load-testing-methodology.md)

## Card: Why a load test can't substitute for a soak test

**Prompt:**
Why can't a load test substitute for a soak test?

**Answer:**
A load test's duration is typically too short to surface a problem that only manifests through time-accumulation (a leak, an unbounded cache) — regardless of how much traffic it generates in that window.

**Why it matters:**
The precise reason two distinct test types exist rather than one "performance test" catching everything.

**Common trap:**
Treating a passed load test as evidence the service is also safe to run for extended periods.

**Related:**
[handbook/testing/performance-and-load-testing-methodology.md](../handbook/testing/performance-and-load-testing-methodology.md)

## Card: What determines whether a load test catches real issues

**Prompt:**
What determines whether a load test actually catches real production-representative issues besides traffic volume?

**Answer:**
Traffic shape — request mix, cache-hit pattern, data-access distribution. Uniform synthetic traffic can pass cleanly while missing the specific conditions that cause real tail-latency behavior.

**Why it matters:**
Distinguishes a load test that generates a genuine confidence signal from one that only exercises raw throughput.

**Common trap:**
Designing a load test around total requests-per-second alone, without matching the real traffic's shape.

**Related:**
[handbook/testing/performance-and-load-testing-methodology.md](../handbook/testing/performance-and-load-testing-methodology.md)

## Card: Why performance testing silently lapses

**Prompt:**
Why does performance testing have a demonstrated tendency to silently lapse, unlike a functional test suite?

**Answer:**
It typically produces no automatic failure signal when skipped — without an explicit owner and defined trigger, it depends on individual initiative rather than an automated gate.

**Why it matters:**
Explains why a performance-testing practice needs deliberate ownership and a defined trigger, not just good intentions.

**Common trap:**
Assuming a performance-testing practice will persist on its own once established, with no explicit owner or re-trigger condition.

**Related:**
[handbook/testing/performance-and-load-testing-methodology.md](../handbook/testing/performance-and-load-testing-methodology.md)
