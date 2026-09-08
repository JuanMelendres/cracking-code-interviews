---
title: "Cheat Sheet: Java Version Features Timeline"
slug: java-version-features-timeline
document_type: cheat-sheet
domain: 02-java
topic_id: T-2211
canonical: ../syllabus/02-java/language-core/java-version-features-timeline.md
last_updated: 2026-09-08
---

# Java Version Features Timeline

**Canonical chapter:** [`syllabus/02-java/language-core/java-version-features-timeline.md`](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Core Mental Model

Java ships a new release every 6 months since Java 10; LTS releases (8, 11, 17, 21, 25) get extended support and run most production code. A feature is "preview" (needs `--enable-preview`, may still change) before it's "final" (stable, no flag needed).

## Essential Definitions

- **LTS** — Long-Term Support release: 8, 11, 17, 21, 25.
- **Preview feature** — real and working, but not yet API-stable; requires `--enable-preview`.
- **Record** — a compact, immutable data carrier with compiler-generated `equals`/`hashCode`/`toString`/accessors (final, Java 16).
- **Sealed type** — a class/interface with an explicit, closed `permits` list of subtypes (final, Java 17).
- **Virtual thread** — a lightweight, JVM-managed thread; many share a small pool of OS threads (final, Java 21).

## Decision Table

| Version | Headline final features |
|---|---|
| Java 8 (2014) | Lambdas, Streams, `Optional`, default/static interface methods |
| Java 11 (2018) | `var` in lambdas, HTTP Client finalized, single-file source launch |
| Java 17 (2021) | Sealed classes/interfaces finalized |
| Java 21 (2023) | Virtual threads, pattern matching for `switch`, record patterns — all finalized |
| Java 25 (2025) | Scoped values, flexible constructor bodies, module import declarations — finalized (verify exact status against the current JEP index) |

## Common Pitfalls

- Attributing records or pattern matching to Java 8 — they're Java 16/21 respectively.
- Claiming a preview feature is "in" a version without noting `--enable-preview` was required.
- Assuming virtual threads make CPU-bound code faster — they help I/O-bound concurrency specifically, not raw compute.
- Treating non-LTS releases as unstable overall — only their preview features carry that risk, not the release itself.

## Interview Answer Skeleton

**30-sec:** Java 8 = lambdas/streams/Optional. Java 17 = sealed classes. Java 21 = virtual threads + exhaustive switch pattern matching + record patterns, all finalized. New release every 6 months; LTS = 8/11/17/21/25.

**2-min:** Add the preview-vs-final distinction and one real example (structured concurrency going through multiple preview rounds before finalizing).

**Whiteboard:** Draw a timeline with the 5 LTS releases as dots, one headline feature under each.

**Staff-level framing:** JDK-version strategy is an organizational risk/velocity trade-off — standardizing on current LTS reduces "which JDK does this run on" incidents but needs a real, budgeted migration plan.

## Related

- syllabus/02-java/language-core/lambdas-and-functional-interfaces.md
- syllabus/02-java/language-core/records-sealed-types-and-pattern-matching.md
- syllabus/02-java/concurrency/virtual-threads.md
