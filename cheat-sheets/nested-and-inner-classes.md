---
title: "Cheat Sheet: Nested and Inner Classes"
slug: nested-and-inner-classes
document_type: cheat-sheet
domain: 02-java/language-core
topic_id: T-2431
canonical: ../syllabus/02-java/language-core/nested-and-inner-classes.md
last_updated: 2026-09-28
---

# Nested and Inner Classes

**Canonical chapter:** [`syllabus/02-java/language-core/nested-and-inner-classes.md`](../syllabus/02-java/language-core/nested-and-inner-classes.md)

## Core Mental Model

A static nested class is a file in a folder — complete on its own. An inner class is a sticky note attached to a page — you cannot keep the note without keeping the whole page.

## Essential Definitions

- **Static nested** — `static class X {}` inside another class. No enclosing instance. `new X()`.
- **Inner** — non-static nested class. Bound to an enclosing instance. `outer.new X()`.
- **Local** — named class declared inside a method body. Emitted as `Outer$1Name`.
- **Anonymous** — unnamed, declared and instantiated in one expression. Emitted as `Outer$1`.
- **`this$0`** — the compiler-generated `final synthetic` field holding the enclosing instance.
- **`Outer.this`** — how an inner class reaches an enclosing field shadowed by its own.

## Decision Table

| Question | Answer |
|---|---|
| Needs enclosing instance state? | No → `static` nested (the default) |
| Stored in a listener list, cache, scheduler, or queue? | `static` nested, even if reading outer state is convenient |
| Will it be serialized by a graph walk? | `static` nested, unconditionally |
| One method, one abstract method to implement? | lambda |
| One method, needs state or several methods? | anonymous (inline once) or local (needs a name) |
| Part of the public API, e.g. a `Builder`? | `static` nested, so callers write `Outer.Builder` |

## Measured Facts

| Case | Emitted field | Collected after GC? |
|---|---|---|
| Inner class reading an enclosing field | `LeakyService this$0` | No — 8 MB retained |
| Inner class never touching the outer instance | none (javac 21 elides it) | Yes |
| Static nested class | none | Yes |

Serializing a `Serializable` inner class whose outer is not serializable: `NotSerializableException: NestedClassesDemo` — the message names the **outer** class.

## Common Pitfalls

- Reading `static` on a nested class as "shared state." It means only "no enclosing instance."
- Writing `new Outer.Inner()` for a non-static inner class — the syntax is `outer.new Inner()`.
- Claiming the outer reference always exists. On JDK 21 it is elided when unused — design as if it were there, but do not assert it as fact.
- A small experiment showing "no leak" because a `final` field initialized to a literal was constant-folded, removing the outer access.
- Making a `Builder` an inner class, forcing callers to have an instance of the thing they are building.

## Interview Answer Skeleton

**30-sec:** `static` nested is a normal class scoped inside another; an inner class carries a hidden `final synthetic this$0` to an enclosing instance. That field is why it reads outer state unqualified, why it keeps the outer object reachable, and why it breaks serialization. Default to `static`.

**Follow-up ready:** `Outer.this` disambiguates shadowing; the leak is GC root → registry → callback → `this$0` → outer; the fix is `static` nested plus an explicit constructor.

## Production Warning Signs

- Even old-gen growth matching a job or request cadence, with heap dumps dominated by tiny callback objects.
- `NotSerializableException` naming a class the failing code never references.
- Listener registries that only ever grow.

## Related

- [Lambdas and Functional Interfaces](../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md)
- [Serialization Hazards and Alternatives](../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md)
- [Memory Leak Diagnosis and Heap Dump Analysis](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)
