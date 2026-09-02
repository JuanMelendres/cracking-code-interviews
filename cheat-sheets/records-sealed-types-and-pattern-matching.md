---
title: "Cheat Sheet: Records, Sealed Types, and Pattern Matching"
slug: records-sealed-types-and-pattern-matching
document_type: cheat-sheet
domain: java-core
topic_id: T-110
canonical: ../handbook/java-core/records-sealed-types-and-pattern-matching.md
last_updated: 2026-09-02
---

# Records, Sealed Types, and Pattern Matching

**Canonical chapter:** [`handbook/java-core/records-sealed-types-and-pattern-matching.md`](../handbook/java-core/records-sealed-types-and-pattern-matching.md)

## Core Mental Model

A record trades extensibility and boilerplate for a compiler-enforced, transparent, immutable data carrier; a sealed type trades open extensibility for a compiler-provable closed set of subtypes; pattern matching is what lets code consume both without an `instanceof`-and-cast ceremony.

## Essential Definitions

- **Record** (JDK 16, JEP 395) — implicitly `final`, extends `java.lang.Record`, cannot extend another class; generates fields, canonical constructor, `comp()`-style accessors, `equals`/`hashCode`/`toString`.
- **Compact constructor** — validates/normalizes arguments before the implicit field assignments; cannot skip assigning a component.
- **Sealed class/interface** (JDK 17, JEP 409) — `sealed ... permits A, B, C`; every permitted subtype must be `final`, `sealed`, or `non-sealed`.
- **Pattern matching for `switch`** (JDK 21, JEP 441/440) — type patterns, record deconstruction, `when` guards, explicit `case null`; exhaustiveness checked against sealed hierarchies.

## Decision Table

| Question | Answer |
|---|---|
| Type is a pure data holder with value semantics? | Record |
| Full set of "kinds" is fixed, owned by you, and you want missed-case compile errors? | Sealed hierarchy (often sealed interface + record implementations) |
| About to write `instanceof X x` + cast, or a chain of them? | Pattern matching (`instanceof` pattern or switch pattern for 3+ variants) |
| Adding a new variant should mean a new virtual method override, not touching every switch? | Polymorphism problem, not pattern-matching — prefer an abstract method |

## Key Numbers

- Java version timeline: `instanceof` patterns and records final in JDK 16; sealed types final in JDK 17; switch patterns/record patterns/`case null` final in JDK 21.
- Measured: `p1.hashCode() == p2.hashCode()` for equal records is `true`, but the independently-derived `Objects.hash()` value does NOT match — the exact formula is unspecified.

## Common Pitfalls

- Assuming a record's `hashCode()` equals `Objects.hash(components)` — it doesn't have to, and measurably doesn't.
- Assuming `sealed` alone gives runtime safety — it only constrains what can extend the type; pattern matching/exhaustive switch is what gives compile-time completeness.
- Writing `case Integer s when ...` against a primitive `int` selector — doesn't compile; type patterns require a reference-type selector.
- Believing records can extend a class — they can only implement interfaces.

## Interview Answer Skeleton

**30-sec:** A record is a compiler-generated, immutable data carrier; a sealed type restricts which classes can extend it to an explicit, compiler-known list; pattern matching lets code test-and-destructure both without manual casts, and combined with a sealed hierarchy lets the compiler prove a switch handles every case.

**2-min:** Mention the non-obvious trade-off (record `hashCode()` unspecified formula) and the real, captured compile error from an incomplete sealed switch: `the switch expression does not cover all possible input values`.

**Whiteboard:** Sealed interface `Shape` at top, three record boxes hanging off it (`Circle`, `Rectangle`, `Triangle`) with component lists inline. Switch box below with three arrows, "no default — compiler proves these 3 are ALL of them." Cross out one arrow: "COMPILE ERROR."

**Staff-level framing:** Choosing `sealed` for a domain-event hierarchy is a bet that the variant set is genuinely closed and owned by one team — a coordination point when the domain boundary is real, a liability when it isn't; a sealed hierarchy + exhaustive switch is a smaller, compiler-verified Visitor-pattern substitute.

## Production Warning Signs

- A payment-event hierarchy modeled `sealed` catches a missed case at build time, not in production: adding `PartiallyRefunded` forces every consumer's switch to be updated explicitly, instead of three of five silently ignoring it until a support ticket surfaces the gap.

## Related

- `handbook/architecture/design-patterns-applied.md`
- `handbook/java-core/polymorphism-and-dynamic-dispatch.md`
