---
title: "Flashcards: Test Strategy, the Pyramid, and Test Doubles"
slug: test-strategy-and-test-doubles
document_type: flashcard-deck
domain: testing
topic_id: T-1103
canonical: ../syllabus/08-testing/test-strategy-and-test-doubles.md
last_updated: 2026-08-06
---

# Flashcards: Test Strategy, the Pyramid, and Test Doubles

**Canonical chapter:** [`syllabus/08-testing/test-strategy-and-test-doubles.md`](../syllabus/08-testing/test-strategy-and-test-doubles.md)

## Card: What a mock proves beyond a return value

**Prompt:**
What does `verify(gateway, times(3))` prove that `assertTrue(result)` alone cannot?

**Answer:**
That the retry logic called the dependency the exact expected number of times with the exact arguments — the interaction, not just the outcome.

**Why it matters:**
Catches wasted-retry and missing-retry bugs a return-value-only assertion would miss identically.

**Common trap:**
Treating a passing boolean assertion as proof the interaction logic itself is correct.

**Related:**
[Internal Implementation](../syllabus/08-testing/test-strategy-and-test-doubles.md#internal-implementation)

## Card: Why mocking the database in a repository test is wrong

**Prompt:**
What's wrong with mocking the database in a repository test?

**Answer:**
It only verifies the test's own assumptions about what the database does — it never checks real SQL correctness.

**Why it matters:**
A common, false-confidence-producing mistake that a 100%-passing suite can hide.

**Common trap:**
Believing a fully-mocked, fully-passing repository suite is sufficient coverage of the boundary.

**Related:**
[Production Scenarios](../syllabus/08-testing/test-strategy-and-test-doubles.md#production-scenarios)

## Card: What coverage percentage actually measures

**Prompt:**
What does coverage percentage actually measure?

**Answer:**
Execution (lines/branches run at least once) — nothing about assertion quality. A diagnostic tool, not a quality target.

**Why it matters:**
Prevents treating a coverage number as proof of test quality.

**Common trap:**
Setting a coverage percentage as a release gate without checking assertion quality.

**Related:**
[Core Concepts](../syllabus/08-testing/test-strategy-and-test-doubles.md#core-concepts)

## Card: `@Mock` vs `@Spy` — what actually runs?

**Prompt:**
You call `write("hello")` on a `mock(AuditLog.class)` and on a `spy(new AuditLog())`. What does each return, and what happens to the real object's state?

**Answer:**
The mock returns `null` and no real code runs — and `realCallCount()` also returns `0`, because that accessor is mocked too, not because nothing happened. The spy returns the real `"WROTE:hello"` and `realCallCount()` is genuinely `1`. Opposite defaults: a mock stubs everything, a spy stubs nothing until you say so.

**Why it matters:**
Tests that check state through a mock's own accessor are asserting on stub defaults, not on behavior.

**Common trap:**
Describing a spy as "a mock that records calls." Both record calls; `verify(...)` is identical on each.

**Related:**
[Core Concepts](../syllabus/08-testing/test-strategy-and-test-doubles.md#core-concepts)

## Card: Why `when(spy.x())` is a bug

**Prompt:**
Why is `when(spy.write("hello")).thenReturn("STUBBED")` dangerous, and what do you write instead?

**Answer:**
Mockito must evaluate the argument to `when(...)`, which means really calling `write("hello")` on the spy. Measured: that single stubbing line left `realCallCount() == 1` and `written() == [hello]` before the test had done anything. If the real method throws or hits a database, the stubbing line is what fails. Use `doReturn("STUBBED").when(log).write("hello")` — measured at `realCallCount() == 0` with no side effect.

**Why it matters:**
This is the entire reason Mockito ships a second stubbing syntax.

**Common trap:**
Assuming `when()` is purely declarative. It is an ordinary Java expression, and its argument is evaluated like any other.

**Related:**
[Core Concepts](../syllabus/08-testing/test-strategy-and-test-doubles.md#core-concepts)

## Card: Does a stub apply to a spy's internal self-call?

**Prompt:**
A real method on a Mockito spy internally calls `this.otherMethod()`, which you stubbed. Does the stub apply?

**Answer:**
Yes — measured. `writeTwice("x")`, which calls `write(...)` twice internally, returned `"STUBBED|STUBBED"` with `realCallCount() == 0`. A Mockito spy is a proxy subclass whose real method bodies execute with `this` bound to the proxy, so internal calls are intercepted.

**Why it matters:**
This is the **opposite** of Spring's `@Transactional` self-invocation behavior, where an internal `this.method()` call bypasses the proxy entirely. "It's a proxy" predicts neither answer.

**Common trap:**
Generalizing the Spring self-invocation rule to every proxy.

**Related:**
[Transactional Proxy Mechanics and Propagation](../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md)
