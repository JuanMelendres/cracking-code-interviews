---
title: "Cheat Sheet: Comparator: Composition and Pitfalls"
slug: comparator-composition-and-pitfalls
document_type: cheat-sheet
domain: 02-java
topic_id: T-2413
canonical: ../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md
last_updated: 2026-09-17
---

# Comparator: Composition and Pitfalls

**Canonical chapter:** [`syllabus/02-java/language-core/comparator-composition-and-pitfalls.md`](../syllabus/02-java/language-core/comparator-composition-and-pitfalls.md)

## Core Mental Model

`Comparator` is `Comparable`'s swappable escape hatch: `Comparable` bakes one permanent ordering into a class; a `Comparator` is a separate, external object that can express as many orderings of the same type as needed, composed declaratively instead of hand-written.

## Essential Definitions

- **`Comparator.comparing(keyExtractor)`** — builds a comparator from a key-extracting method reference or lambda.
- **`.thenComparing(nextKeyExtractor)`** — appends a tie-breaker, consulted only when every earlier comparator in the chain returned zero.
- **`.reversed()`** — negates a finished comparator's result; does not rewrite the comparison logic.
- **`Comparator.comparingInt`/`comparingLong`/`comparingDouble`** — overflow-safe numeric-key comparators, backed by `Integer.compare()`/`Long.compare()`/`Double.compare()`.
- **`Comparator.nullsFirst()`/`nullsLast()`** — wraps an inner comparator to handle `null` keys explicitly, instead of throwing.

## Decision Table

| Situation | Use |
|---|---|
| One universal natural ordering for the whole class | `Comparable`, kept consistent with `equals()` |
| Different orderings needed in different contexts | External `Comparator`, built via `comparing`/`thenComparing` |
| Numeric sort key | `comparingInt`/`comparingLong`/`comparingDouble` — never subtraction |
| Sort key can be `null` | `Comparator.nullsFirst(...)`/`nullsLast(...)` |
| Multi-field sort | One `comparing().thenComparing()...` chain, priority order |

## Common Pitfalls

- `(a, b) -> a.getX() - b.getX()` for a numeric field — genuinely overflows at extreme values (`Integer.MIN_VALUE - 1` wraps to `Integer.MAX_VALUE`), verified directly; not just a style nit.
- Plain `Comparator.comparing(keyExtractor)` on data with a `null` key — throws a real `NullPointerException`.
- Assuming `.thenComparing()` sorts independently rather than only breaking ties left by the prior comparator.
- Sorting twice sequentially to fake a multi-field order instead of composing one chain.

## Interview Answer Skeleton

**30-sec:** `Comparator.comparing().thenComparing()` composes multi-field sorts declaratively; `.reversed()` flips a finished comparator. A subtraction-based numeric comparator is a real, reproduced overflow bug — `comparingInt` fixes it structurally. `nullsFirst()`/`nullsLast()` fixes a real `NullPointerException` on nullable keys.

**2-min:** Add: `List.sort()`/`Collections.sort()` are guaranteed stable, verified directly — equal-key elements keep their original relative order, which is exactly what makes `thenComparing()` chains reliable. Choose `Comparable` for one universal ordering, external `Comparator` for context-specific ones.

**Staff-level framing:** The subtraction-comparator bug is a specific instance of a broader pattern — an API shortcut correct for every value seen in development but with a real, silent failure at a boundary condition real production data eventually reaches. Prefer standard-library composition over hand-written comparison arithmetic.

## Related

- syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md
- syllabus/02-java/language-core/lambdas-and-functional-interfaces.md
- syllabus/02-java/collections/priorityqueue-internals.md
