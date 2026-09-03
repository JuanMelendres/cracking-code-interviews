---
title: "Cheat Sheet: Enums, EnumMap, and EnumSet"
slug: enums-enummap-and-enumset
document_type: cheat-sheet
domain: java-core
topic_id: T-111
canonical: ../handbook/java-core/enums-enummap-and-enumset.md
last_updated: 2026-09-02
---

# Enums, EnumMap, and EnumSet

**Canonical chapter:** [`syllabus/02-java/language-core/enums-enummap-and-enumset.md`](../syllabus/02-java/language-core/enums-enummap-and-enumset.md)

## Core Mental Model

An enum constant is a real, JVM-enforced singleton object, and the entire enum type is a class the compiler generates, extending `java.lang.Enum` and refusing reflective construction by dedicated design — not merely a private-constructor convention.

## Essential Definitions

- **Enum constant identity** — every reference (`Color.RED`, `values()[0]`, `valueOf("RED")`) resolves to the exact same object; reflective construction throws a dedicated `IllegalArgumentException`.
- **Constant-specific method body** — `PLUS { ... }` genuinely generates an anonymous subclass per constant (`Operation$1`, `$2`, ...), not `switch` sugar; `getDeclaringClass()` still returns the enum type.
- **`EnumMap`** — array-backed, indexed by `ordinal()`, no hashing; iterates in declaration order.
- **`EnumSet`** — bitset-backed (a `long`/`long[]`, one bit per constant).
- **`Enum.ordinal()`** — declaration position; silently shifts if a constant is inserted/reordered.

## Decision Table

| Question | Answer |
|---|---|
| Are keys/elements a single, fixed enum type? | `EnumMap`/`EnumSet` — guaranteed ordering, lower memory footprint |
| Does this value ever cross a persistence/serialization boundary? | Never use `ordinal()` — use `name()` or an explicit stable code |
| Does each constant need genuinely different behavior for one method? | Constant-specific method bodies — compiler-enforced completeness beats an external `switch` |
| Will this enum gain new constants over time? | Plan persistence/ordinal-dependent logic around that from the start |

## Key Numbers

- `EnumMap` vs `HashMap` put/get: measured only ~1.08–1.14x faster (honest, modest — not dramatic).
- `ordinal()` reorder demo: inserting one constant mid-declaration silently shifted every later ordinal; a value persisted as `ordinal=2` (REJECTED under V1) resolved to APPROVED under V2 — zero exception.

## Common Pitfalls

- Persisting `Enum.ordinal()` directly — silent corruption risk on any future reordering.
- Assuming `EnumMap`/`EnumSet` are dramatically faster than `HashMap`/`HashSet` without measuring — real advantage is ordering/memory, not raw throughput.
- Writing a `switch` on `ordinal()` instead of the constant or a constant-specific method body.
- Assuming reflection can construct additional enum instances the way it can for an ordinary class.

## Interview Answer Skeleton

**30-sec:** Enum constants are real, JVM-enforced singletons — reflection cannot construct new instances (dedicated `IllegalArgumentException`). Constant-specific bodies are real anonymous subclasses. `EnumMap`/`EnumSet` guarantee natural order with a modest (~1.1x), not dramatic, throughput edge. `Enum.ordinal()` is dangerous to persist — reordering silently corrupts old values.

**2-min:** Add the measured proof: reflective construction attempt throws `Cannot reflectively create enum objects`; `Operation$1`/`$2`/`$3` are genuinely distinct runtime classes; the ordinal-reorder demo shows a persisted value silently resolving to the wrong constant with zero exception.

**Whiteboard:** Declare → persist ordinal → reorder → ordinals shift → old persisted value resolves to the wrong constant, no exception. Beside it, draw `name()`/`valueOf()` as the stable, safe alternative.

**Staff-level framing:** Any position-derived value (array indices as external IDs, list order as priority, declaration order as a stable code) is fragile to reordering, and the failure is almost always silent — prefer identity-based, explicitly-assigned representations for anything crossing a persistence boundary.

## Production Warning Signs

- A status field silently changes apparent meaning after a routine enum update — trace the enum's declaration-order history against when reporting discrepancies began.
- Fix: migrate to `name()`-based persistence or an explicit, deliberately-assigned stable code.

## Related

- `syllabus/02-java/language-core/annotations-and-annotation-processing.md`
- `syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md`
