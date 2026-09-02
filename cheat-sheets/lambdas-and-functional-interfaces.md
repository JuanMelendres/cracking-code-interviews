---
title: "Cheat Sheet: Lambdas and Functional Interfaces"
slug: lambdas-and-functional-interfaces
document_type: cheat-sheet
domain: java-core
topic_id: T-108
canonical: ../handbook/java-core/lambdas-and-functional-interfaces.md
last_updated: 2026-09-02
---

# Lambdas and Functional Interfaces

**Canonical chapter:** [`handbook/java-core/lambdas-and-functional-interfaces.md`](../handbook/java-core/lambdas-and-functional-interfaces.md)

## Core Mental Model

A lambda is not an object literal — it's a deferred call to a factory the JVM builds at runtime, the first time that call site executes. Unlike an anonymous class (a full, separate `.class` file at compile time), a lambda leaves only a private method plus an `invokedynamic` instruction; `LambdaMetafactory` synthesizes the implementation lazily via `MethodHandle`s.

## Essential Definitions

- **Functional interface (SAM)** — exactly one abstract method; `default`/`static` methods don't count toward the SAM requirement.
- **Effectively final capture** — a lambda captures a local variable's *value* at creation time, not a live reference; reassignment after capture is a compile error.
- **Method reference** — pure syntactic sugar over one of four lambda shapes (static, bound instance, unbound instance, constructor); identical runtime mechanism.

## Decision Table

| Question | Answer |
|---|---|
| Target needs more than one method's behavior or extra mutable state? | Use an anonymous/named class — a lambda is fundamentally single-method |
| An existing method already matches the target signature exactly? | Prefer a method reference over a lambda |
| Lambda needs to mutate an enclosing local? | Can't for a local — box it (`AtomicInteger`, a holder class) |
| Interface meant to be implemented via lambda by callers? | Annotate `@FunctionalInterface` — compiler catches a future accidental second abstract method |

## Key Numbers

- Lambda compiles to zero extra `.class` files; anonymous class produces a real, separate `AnonymousExample$1.class`.
- `invokedynamic`'s bootstrap is `LambdaMetafactory.metafactory`, generating the implementation once, on first call-site execution, then cached.

## Common Pitfalls

- Believing a lambda "captures the variable" like a mutable closure — Java captures the *value*, hence the effectively-final rule.
- Assuming `default`/`static` interface methods block `@FunctionalInterface` — they don't.
- Treating a method reference as a different runtime mechanism from its equivalent lambda — it isn't.
- Writing an anonymous class out of habit for a simple single-method implementation.

## Interview Answer Skeleton

**30-sec:** A lambda compiles to a private method plus an `invokedynamic` call site — no extra `.class` file — with `LambdaMetafactory` generating the implementation lazily at runtime. Captured locals must be effectively final because capture is by value, not reference. Method references are pure sugar over specific lambda shapes.

**2-min:** Add the real evidence: `javac` produces zero extra class files for the lambda vs. a real `$1.class` for the anonymous class; the real compiler error `local variables referenced from a lambda expression must be final or effectively final`; two real fixes (`AtomicInteger` boxing, or a static field with no restriction at all).

**Whiteboard:** Compile time → private method + `invokedynamic` call site; first runtime execution → `LambdaMetafactory` generates the implementation once; subsequent calls reuse the cached `CallSite`. Contrast with the anonymous-class path: full separate class, ordinary `new` + `invokespecial` every time.

**Staff-level framing:** Deferring class generation to runtime (`invokedynamic`/`MethodHandle`s) trades a small first-use cost for avoiding upfront class-loading/`.class`-file bloat — the same pattern underlies `invokedynamic`-based String concatenation (JEP 280) and parts of records/pattern-matching; relevant to container cold-start and CDS discussions.

## Common Interview Traps

- A time-pressured hotfix blocked by the effectively-final compiler error during an incident — recognizing it as the compiler correctly preventing a real capture-semantics bug (not an obstruction) and fixing it in seconds via boxing.

## Related

- `handbook/java-core/streams-and-collectors.md`
- `handbook/java-core/reflection-and-dynamic-proxies.md`
- `handbook/java-core/optional-and-null-strategy.md`
