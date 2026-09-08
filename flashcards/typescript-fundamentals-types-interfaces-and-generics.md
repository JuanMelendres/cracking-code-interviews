---
title: "Flashcards: TypeScript Fundamentals: Types, Interfaces, and Generics"
slug: typescript-fundamentals-types-interfaces-and-generics
document_type: flashcard-deck
domain: frontend
topic_id: F-003
tier: Beginner/Intermediate
canonical: ../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md
last_updated: 2026-09-08
---

# Flashcards: TypeScript Fundamentals: Types, Interfaces, and Generics

**Canonical chapter:** [`syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md`](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Card: Why TypeScript catches the same bug JavaScript misses

**Prompt:**
`formatPrice(amount)` calls `amount.toFixed(2)`. In plain JavaScript, calling `formatPrice("19.99")` crashes at runtime. What happens in TypeScript, and why?

**Answer:**
With `amount: number` annotated, `tsc` rejects `formatPrice("19.99")` at compile time with `Argument of type 'string' is not assignable to parameter of type 'number'` — caught at the exact call site, before the code ever runs, instead of crashing whenever that call happens to execute in production.

**Why it matters:**
Verified directly: the identical mistake produces a real captured runtime `TypeError` in the plain-JS version and a real captured compile-time rejection in the typed version.

**Common trap:**
Describing TypeScript as "safer" without being able to point to a specific bug class and a specific mechanism.

**Related:**
[TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Card: Structural typing in one example

**Prompt:**
`Point { x: number; y: number }` and `Coordinate { x: number; y: number }` are declared with no relationship to each other. Is a `Coordinate` value assignable to a `Point`-typed variable?

**Answer:**
Yes. TypeScript checks the VALUE's shape against the target type's requirements, not the declared type name or any inheritance relationship. Since both have exactly `x: number` and `y: number`, they're structurally compatible.

**Why it matters:**
This is the single biggest mental-model difference from nominally-typed languages like Java, where this would be a compile error without an explicit `implements`/`extends`.

**Common trap:**
Assuming two types need a declared relationship (inheritance, a shared interface) to be assignable to each other.

**Related:**
[TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Card: `any` vs `unknown`

**Prompt:**
Both `any` and `unknown` accept any value. What's the practical difference?

**Answer:**
`any` disables type checking entirely on everything derived from it — even a nonexistent method call compiles. `unknown` also accepts any value but forbids using it until you narrow its type first (`typeof`, `instanceof`, a custom check) — it keeps the compiler's safety net instead of discarding it.

**Why it matters:**
`unknown` is the safe default for genuinely unknown-shaped values (parsed JSON, third-party responses); `any` should be a rare, tracked exception, not a convenience default.

**Common trap:**
Treating `any` and `unknown` as interchangeable "flexible" types.

**Related:**
[TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Card: What a discriminated union enforces that an optional field doesn't

**Prompt:**
Why is `{ variant: 'error'; onRetry: () => void } | { variant: 'info' }` (a discriminated union) stronger than `{ variant: 'error' | 'info'; onRetry?: () => void }` (one interface with an optional field)?

**Answer:**
The optional-field version compiles cleanly whether or not `onRetry` was actually provided for an `'error'`-variant value — the compiler never enforces the real requirement. The discriminated union makes `onRetry` visible and required only on the `'error'` branch, so omitting it there is a real, compile-time error.

**Why it matters:**
This exact pattern is the mechanism [TypeScript with React](../syllabus/21-frontend-web/react-typescript.md) uses for variant component props — this chapter's `Shape` demo is the same idea with no React involved.

**Common trap:**
Reaching for a pile of optional fields with runtime `if`-checks instead of modeling the real constraint at the type level.

**Related:**
[TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)

## Card: What generics buy you over `any`

**Prompt:**
`function identity<T>(value: T): T` vs. `function identity(value: any): any` — both accept any argument. What's the real difference?

**Answer:**
The generic version resolves `T` to the SPECIFIC type passed at each call site and keeps checking it — calling `identity(42)` returns a value TypeScript still knows is `number`, so a later `.toUpperCase()` on the result is a real compile error. The `any` version returns `any` regardless of input, so the same mistake compiles silently.

**Why it matters:**
Generics are the tool for genuine type-safe reuse across multiple types; `any` looks similar but discards every guarantee.

**Common trap:**
Treating `<T>` and `any` as interchangeable ways to make a function "accept anything."

**Related:**
[TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md)
