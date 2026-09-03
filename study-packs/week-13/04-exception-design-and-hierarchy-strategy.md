---
title: "T-105 · Exception Design and Hierarchy Strategy"
topic_id: T-105
domain: JavaCore
tier: Core
iwi: 5.50
prerequisites: []
unlocks: []
week: 13
last_reviewed: 2026-07-30
canonical: ../../handbook/java-core/exception-design-and-hierarchy-strategy.md
---

# T-105 · Exception Design and Hierarchy Strategy

**IWI 5.50 · Core tier · High interview frequency**

**Canonical chapter:** [Exception Design and Hierarchy Strategy](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md). This file is the Week 13 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-13/exception-design/src/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Swallowed vs. chained cause, measured](#3-swallowed-vs-chained-cause-measured)
4. [Suppressed exceptions vs. manual finally, measured](#4-suppressed-exceptions-vs-manual-finally-measured)
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

Exception design is choosing a hierarchy (checked vs. unchecked) and a wrapping discipline that preserves enough information for whoever eventually catches the failure to actually understand what happened. → [Definition and Purpose](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#definition-and-purpose).

## 2. Why it exists

A low-level exception isn't always meaningful several layers up, but wrapping it must preserve the original as the cause, or the information needed to debug the failure is gone the moment it's wrapped. → [Definition and Purpose](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#definition-and-purpose).

## 3. Swallowed vs. chained cause, measured

Measured: wrapping an `IOException` in a message-only custom exception leaves `getCause() == null` — the original failure and its stack trace are gone. Using the cause-accepting constructor preserves it, visible via `getCause()` and the full `Caused by:` chain. → [Internal Implementation](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#internal-implementation) has the full trace.

## 4. Suppressed exceptions vs. manual finally, measured

Measured: try-with-resources, with both the body and `close()` throwing, propagates the body's exception as primary and attaches the `close()` failure via `addSuppressed()` — both preserved. A manual `finally` block whose `close()` also throws silently replaces the original exception entirely, with nothing to recover it. → [Internal Implementation](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#internal-implementation) has the full trace.

## 5. Trade-offs

Checked exceptions force callers to acknowledge a failure category at the cost of boilerplate; chaining the cause has no real cost and is strictly better than not chaining. → [Trade-offs](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#trade-offs).

## 6. Interview questions

1. Your on-call alert shows a generic exception with no detail. What's the first thing you check in the code?
2. What happens if both the try block and close() throw, with and without try-with-resources?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#interview-questions).

## 7. Common mistakes

Constructing a wrapped exception without chaining the cause; using a manual `try`/`finally` for an `AutoCloseable` resource instead of try-with-resources. → [Common Mistakes](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#common-mistakes).

## 8. Staff-level discussion

A good exception design never has to choose between two pieces of information — it should always be possible to know both what ultimately propagated and what else happened along the way. → [Staff-Level Discussion](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#interview-answer-framework).

## 9. Summary

Wrapping without chaining the cause destroys diagnostic information permanently, measured directly. Try-with-resources preserves both a body exception and a close() failure; a manual finally block silently replaces the original entirely. → [Summary](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#practice-exercises) and [Solutions](../../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#solutions). Reproducible demos: `practice/java/week-13/exception-design/src/`.

## 14. Additional Reading

- Joshua Bloch, *Effective Java*, Item 73 and Item 77

## 15. Official References

- [java.lang.Throwable (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Throwable.html)
- [The Java Tutorials — try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
