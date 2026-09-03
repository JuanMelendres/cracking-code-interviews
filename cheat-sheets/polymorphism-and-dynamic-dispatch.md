---
title: "Cheat Sheet: Polymorphism and Dynamic Dispatch Mechanics"
slug: polymorphism-and-dynamic-dispatch
document_type: cheat-sheet
domain: java-core
topic_id: T-102
canonical: ../handbook/java-core/polymorphism-and-dynamic-dispatch.md
last_updated: 2026-09-02
---

# Polymorphism and Dynamic Dispatch Mechanics

**Canonical chapter:** [`syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md`](../syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md)

## Core Mental Model

Only instance method calls are looked up by "what is this object, right now" — everything else (fields, static methods, overload selection) is decided once, at compile time, by the declared type of the reference and never revisited.

## Essential Definitions

- **Overriding** — resolved by the object's runtime class via `invokevirtual` (walks the hierarchy).
- **Field/static hiding** — a subclass member with the same name as a superclass one does not override it; both coexist, resolved by the reference's *declared* type via `getfield`/`invokestatic`.
- **Overload resolution** — decided entirely at compile time by the declared types of the arguments, baked into the bytecode before runtime.
- **Private methods** — never virtually dispatched; resolved via `invokespecial`, invisible to override in the polymorphic sense.

## Decision Table

| Member kind | Dispatch | Bytecode |
|---|---|---|
| Instance method (not `private`) | Dynamic — object's actual runtime class | `invokevirtual` |
| `private` instance method | Static — not visible for override | `invokespecial` |
| `static` method | Static — resolved by declared type | `invokestatic` |
| Field (instance or static) | Static — resolved by declared type | `getfield`/`getstatic` |
| Overloaded method selection | Static — resolved by declared argument types | Baked in before runtime |

## Common Pitfalls

- Assuming a subclass field with the same name overrides the superclass field — it hides it; both fields coexist.
- Assuming a "static override" in a subclass dispatches dynamically — it hides, resolved by declared type.
- Calling an overridable instance method from a superclass constructor — the subclass isn't fully constructed yet.
- Believing overload resolution considers the runtime type of arguments rather than their declared type.

## Interview Answer Skeleton

**30-sec:** Only instance method calls are dynamically dispatched via `invokevirtual`, resolved by the object's actual runtime class. Fields, static methods, and overload selection are all resolved at compile time by the declared type — same-named subclass members hide rather than override.

**2-min:** Add the measured overload-vs-override contrast, the field-hiding demo (identical object, two references, two different field values), and the constructor-dispatch pitfall: a superclass constructor calling an overridden method observes subclass fields still at their default (`null`/`0`/`false`), since field initializers run after `super()` returns.

**Whiteboard:** "Member access" branches on "instance method, not private, not static?" — yes → `invokevirtual`/"looked up on the actual object"; no → `getfield`/`invokestatic`/`invokespecial`/"fixed by declared type." Circle the yes branch: "this is the only branch that's actually polymorphism."

**Staff-level framing:** This same dispatch rule recurs in proxy-based frameworks (Spring `@Transactional`, Hibernate lazy-loading proxies) — they only intercept calls that arrive via `invokevirtual`, which is precisely why they silently fail on `private`, `final`, or self-invoked (`this.method()`) calls. One mechanism, appearing twice.

## Production Warning Signs

- A shared validation base class silently stops enforcing rules for every subclass after a refactor moves rule-building from a constructor body into a field initializer — the base constructor's call to the overridden method now observes the not-yet-initialized field.
- Fix: never call a non-`final` instance method from a constructor; pass pre-built state through a constructor parameter or use an explicit post-construction `init()` step.

## Related

- `syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md`
- `interview-playbook/technical-answers/`
