---
title: "Cheat Sheet: MethodHandle and java.lang.invoke"
slug: methodhandle-and-invoke
document_type: cheat-sheet
domain: 02-java/concurrency
topic_id: T-2405
canonical: ../syllabus/02-java/concurrency/methodhandle-and-invoke.md
last_updated: 2026-09-11
---

# MethodHandle and java.lang.invoke

**Canonical chapter:** [`syllabus/02-java/concurrency/methodhandle-and-invoke.md`](../syllabus/02-java/concurrency/methodhandle-and-invoke.md)

## Core Mental Model

A `MethodHandle` is a method reference with its call signature (`MethodType`) fixed at creation, letting repeated invocation through it act almost like a direct call. `invokedynamic` is the bytecode instruction that defers "which handle do I call here" to runtime, resolved once per call site and cached — exactly what a lambda compiles down to.

## Essential Definitions

- **`MethodType`** — a `MethodHandle`'s fixed call signature (parameter and return types).
- **`invokeExact()`** — strict, exact-`MethodType` invocation; throws `WrongMethodTypeException` on mismatch.
- **`invoke()`** — adapts the call automatically to match, no exact-type requirement.
- **`LambdaMetafactory`** — the real bootstrap method a compiled lambda's `invokedynamic` call site resolves through.

## Decision Table

| Need | API |
|---|---|
| Handle for a static method | `Lookup.findStatic(Class, String, MethodType)` |
| Handle for an instance method | `Lookup.findVirtual(Class, String, MethodType)` |
| Handle for a constructor | `Lookup.findConstructor(Class, MethodType)` |
| Strict, exact-type invocation | `handle.invokeExact(args...)` |
| Adaptive invocation | `handle.invoke(args...)` |
| Pre-supply a receiver | `handle.bindTo(receiver)` |
| Inspect a compiled lambda's real bytecode | `javap -c -p -v <ClassFile>.class` |

## Common Pitfalls

- Using `invokeExact()` when argument types aren't guaranteed to match exactly — a real `WrongMethodTypeException`, not a silent coercion.
- Assuming `bindTo()` mutates the original handle — it returns a genuinely new handle with a reduced `MethodType`.
- Assuming a lambda produces a synthetic `.class` file like an anonymous inner class does — it doesn't; `invokedynamic`/`LambdaMetafactory` produces zero extra class files.

## Interview Answer Skeleton

**30-sec:** `MethodHandle` is a typed, fast reference to executable code; `invokeExact()` requires exact type match, `invoke()` adapts. `invokedynamic` is the bytecode instruction lambdas compile to, resolved once per call site via `LambdaMetafactory`.

**2-min:** Add: a real `javap -v` disassembly proves a compiled lambda produces zero extra `.class` files, unlike the equivalent anonymous inner class, which produces a real, extra one — a concrete, verifiable performance/footprint distinction.

**Staff-level framing:** `MethodHandle`/`invokedynamic` is the mechanism JSR 292 introduced specifically to support dynamically-typed languages on the JVM efficiently — lambdas are one consumer of that infrastructure, not its original purpose.

## Related

- syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md
- syllabus/02-java/language-core/reflection-and-dynamic-proxies.md
