---
title: "Cheat Sheet: TypeScript Fundamentals (Types, Interfaces, and Generics)"
slug: typescript-fundamentals-types-interfaces-and-generics
document_type: cheat-sheet
domain: frontend
topic_id: F-003
tier: Beginner/Intermediate
canonical: ../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md
last_updated: 2026-09-08
---

# TypeScript Fundamentals (Types, Interfaces, and Generics)

**Canonical chapter:** [`syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md`](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Core Mental Model

TypeScript adds nothing to what code does at runtime — every type annotation disappears at compile time. Its entire value is checking that values are used consistently with their declared or inferred shape, and reporting a mismatch immediately, at the exact call site, instead of letting it surface later as a runtime crash. Types are checked structurally (by shape), not nominally (by declared name) — this is the single biggest mental-model difference from Java-style typing.

## Essential Definitions

- **Structural typing** — TypeScript checks whether a value HAS the shape a target type requires, not whether it was declared under that type's name; two unrelated interfaces with identical fields are mutually assignable.
- **`any`** — disables type checking entirely for that value and everything derived from it. **`unknown`** — accepts anything but forbids use until narrowed (`typeof`, `instanceof`). Prefer `unknown`.
- **`interface`** — object/class shapes, mergeable across declarations. **`type` alias** — any type at all (unions, tuples, primitives), not mergeable.
- **Union (`A | B`)** — value is one of these; only operations valid on every member are allowed until narrowed. **Intersection (`A & B`)** — value must satisfy every member at once.
- **Discriminated union** — a union of object types sharing one literal-typed field (the discriminant); switching on that field narrows the whole object per branch.
- **Generics (`<T>`)** — one definition, `T` resolved per call site, every use of `T` in that call checked for consistency.
- **`readonly`** — compile-time only; not equivalent to `Object.freeze()`.

## Decision Table

| Situation | Choice |
|---|---|
| A value's shape is used in more than one place | Name it with `interface`/`type`, don't rely on inference alone |
| A component/function's required fields genuinely change by variant | Discriminated union — never a pile of optional fields |
| Same logic needed across several unrelated types | Generic (`<T>`) — never duplicate per type or widen to `any` |
| A value's shape is genuinely unknown until runtime (parsed JSON, `catch` param) | `unknown`, then narrow — never `any` |
| Aliasing a union, tuple, or primitive | `type`, not `interface` (interfaces can't express these) |

## Common Pitfalls

- Reaching for `any` the moment a type is inconvenient, silently reintroducing every bug class TypeScript exists to prevent.
- Assuming two types must share inheritance to be assignable — structural typing requires only a matching shape.
- Modeling a variant-specific requirement as an optional field instead of a discriminated union — it compiles whether or not the field was provided.
- Confusing `readonly` with runtime immutability — it's a compile-time-only check; assigning through a differently-typed alias to the same object still mutates it.
- Sprinkling `as SomeType` assertions to silence an error instead of fixing the real mismatch — hides the bug from the compiler instead of removing it.

## Interview Answer Skeleton

**30-sec:** TypeScript is a static type system checked at compile time, on top of JavaScript — every annotation disappears at runtime. Its whole value is catching shape mismatches early: a function called with the wrong-shaped argument fails to compile at the exact call site instead of crashing whenever that path finally executes in production.

**2-min:** Add the mental model (earlier, cheaper bug detection, not different runtime behavior), the real crash-vs-compile-error contrast (`formatPrice` called with a string: a real runtime `TypeError` in JS, a real captured `tsc` rejection in TS), structural typing (`Point`/`Coordinate` interchangeable despite no declared relationship), and discriminated unions turning a variant-specific requirement into a real, checked constraint instead of an optional field nobody's forced to fill in.

**Whiteboard:** Two boxes with identical fields, different names (`Point`/`Coordinate`), an arrow between them landing on a checkmark — "shapes match, names don't matter." Beside it, a discriminated union as three variant boxes with a switch statement's arrow narrowing into exactly one box per branch.

**Senior-level framing:** Names a specific bug class prevented (not just "it's safer"), and can state precisely what `any` silently allows that `unknown` would catch.

## Common Interview Traps

- Describing TypeScript as "JavaScript with type annotations" without naming a specific bug class it prevents.
- Answering a structural-typing question by reasoning from a nominally-typed language (Java, C#) — assuming two unrelated interfaces with the same fields are incompatible.
- Treating `any` and `unknown` as interchangeable "flexible" types.
- Proposing `onRetry?: () => void` (optional field) as equivalent to a discriminated union for a variant-specific requirement.

## Related

- `syllabus/21-frontend-web/react-typescript.md` — the direct next chapter; applies these same mechanics (discriminated unions, generics) to typed React component props.
- `00-project/frontend-topic-register.md`
