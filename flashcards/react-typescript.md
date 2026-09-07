---
title: "Flashcards: TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions"
slug: react-typescript
document_type: flashcard-deck
domain: frontend
topic_id: F-119
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-typescript.md
last_updated: 2026-09-07
---

# Flashcards: TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions

**Canonical chapter:** [`syllabus/21-frontend-web/react-typescript.md`](../syllabus/21-frontend-web/react-typescript.md)

## Card: Why a discriminated union beats optional fields for variant props

**Prompt:**
A component's `onRetry` prop should be required only when `variant === 'error'`. Why is `onRetry?: () => void` on a single shared interface weaker than a discriminated union?

**Answer:**
An optional field compiles cleanly for EVERY variant, whether or not `onRetry` was actually provided — the compiler never enforces the real business rule. A discriminated union (`{ variant: 'error'; onRetry: () => void } | ...`) makes `onRetry` visible and required only on the `'error'` branch, so omitting it there is a real compile-time error.

**Why it matters:**
Verified directly: omitting `onRetry` from an `'error'`-variant `<Alert>` usage produced a real `Property 'onRetry' is missing in type ... but required in type ...` error naming the exact issue.

**Common trap:**
Treating "optional plus a runtime warning" as an acceptable substitute for a real, compiler-enforced constraint.

**Related:**
[TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions](../syllabus/21-frontend-web/react-typescript.md)

## Card: What exhaustiveness checking actually catches

**Prompt:**
A `useReducer`'s action type union gains a new member, but the reducer's `switch` isn't updated. What real compiler mechanism catches this, and why?

**Answer:**
A `default: return assertNever(action)` branch, where `assertNever(x: never)`. After every NAMED case in the switch is handled, TypeScript's control-flow narrowing types the remaining value as `never` at the `default` branch. A newly added, unhandled union member is still assignable to `action` there, and passing it to a parameter typed `never` is a real type error.

**Why it matters:**
Verified directly: adding a `{ type: 'double' }` member to `CounterAction` with no matching `case` produced a real `Argument of type '{ type: "double"; }' is not assignable to parameter of type 'never'` error.

**Common trap:**
Writing an exhaustive-looking `switch` with no `never`-typed guard at all — it compiles fine today, but gives zero signal when a future teammate adds a new case without updating it.

**Related:**
[TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions](../syllabus/21-frontend-web/react-typescript.md)
