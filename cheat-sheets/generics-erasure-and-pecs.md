---
title: "Cheat Sheet: Generics — Erasure, Variance, and PECS"
slug: generics-erasure-and-pecs
document_type: cheat-sheet
domain: java-core
topic_id: T-104
canonical: ../handbook/java-core/generics-erasure-and-pecs.md
last_updated: 2026-08-05
---

# Generics: Erasure, Variance, and PECS

**Canonical chapter:** [`handbook/java-core/generics-erasure-and-pecs.md`](../handbook/java-core/generics-erasure-and-pecs.md)

## Core Mental Model

Generics are a compiler-only illusion — at runtime there is exactly one `List` class, and the type parameter is gone. Everything else follows from that: a type-safety violation that bypasses the compiler (an unchecked cast) doesn't fail at the point it happens; it fails later, at the point something tries to use the value as its declared type. PECS is a separate compile-time reasoning tool: "if you only read from it, `extends`; if you only write to it, `super`" — because the compiler can't otherwise prove a write into a wildcarded type is safe.

## Essential Definitions

- **Type erasure** — the compiler removes generic type parameters after compile time, replacing them with their bound (or `Object`); `List<String>`, `List<Integer>`, and raw `List` are the identical class at runtime.
- **PECS** ("Producer Extends, Consumer Super") — a parameter only read from should be `? extends T`; a parameter only written to should be `? super T`.
- **Bridge method** — a synthetic method the compiler generates so a generic interface implementation still satisfies the interface's erased signature (e.g., `set(Object)` delegating to `set(String)`).
- **Heap pollution** — an incompatible value entering a generically-typed structure via an unchecked cast, undetected until read.

## Decision Table

| Question | Answer |
|---|---|
| Method only reads `T` values out of a parameter? | `List<? extends T>` — producer |
| Method only writes `T` values into a parameter? | `List<? super T>` — consumer |
| Method both reads and writes as exactly `T`? | Plain `List<T>`, no wildcard |
| Unchecked cast unavoidable at a library boundary? | Add explicit runtime validation immediately at that boundary |

**Trade-offs:** a fully generic API enforces type safety throughout but can't bridge to raw-typed legacy APIs without adaptation; an unchecked cast lets that bridging happen but defers any type violation to a later, harder-to-trace failure unless validated immediately at the cast site.

## Key Numbers (real, executed)

```
strings.getClass() == integers.getClass(): true   <- List<String> and List<Integer> are the SAME runtime class
```

```
Integer 42 inserted into a List<String> via unchecked cast -- NO error at insertion
strings.get(0) -> ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String
```

```
real method:   Wrapper.set(java.lang.String)
BRIDGE METHOD: Wrapper.set(java.lang.Object)     <- compiler-generated, proven via reflection
```

```
readOnlyView (List<? extends Number>): .get(0) = 1 (safe); .add(99) -- DOES NOT COMPILE
```

## Common Pitfalls

- Assuming generic type information survives to runtime (`instanceof List<String>` doesn't compile — meaningless post-erasure).
- Using an unchecked cast with no immediate runtime validation, letting a type violation surface far from its actual cause.
- Confusing which side of PECS (`extends` vs. `super`) applies to reading vs. writing.
- Treating erasure as a minor implementation detail rather than the reason bridge methods, no `new T[]`, and no `instanceof List<String>` all exist.

## Interview Answer Skeleton

**30-sec:** Generics are erased after compile time — `List<String>` and `List<Integer>` are the identical runtime class, measured directly. A defeated generic (via an unchecked cast) fails at read time, not insert time. PECS: `? extends T` for read-only parameters, `? super T` for write-only — writing to a `? extends T` is rejected at compile time because the compiler can't prove it's safe.

**2-min:** Add why erasure exists (type safety with full binary compatibility to pre-generics code, at the cost of no runtime type info) + the real evidence (a `ClassCastException` at `get()`, not at the unchecked cast; a compiler-generated bridge method proven via reflection) + the production cost (an unchecked cast bridging a raw-typed library can let a `ClassCastException` surface months later, far from its real cause).

**Whiteboard:** `List<String>` and `List<Integer>` both compiling down to the same erased `List` class. Below it: unchecked cast → `add(42)` succeeds silently → `get(0)` later throws — annotate the gap as "this is why the stack trace often points at the wrong place."

**Staff-level framing:** the gap between where a violation is introduced (an unchecked cast) and where it's detected (a later `get()` relying on the declared type) is a specific instance of a general principle — type systems only protect you where they're actually consulted, and any bypass (unchecked cast, raw type, deserialization) becomes a place a bug can travel arbitrarily far from its cause. Treat every `@SuppressWarnings("unchecked")` as flagged, requiring an adjacent runtime check.

## Production Warning Signs

- A `ClassCastException` appears deep in unrelated business logic at a simple `.get(i)` call — trace backward for any unchecked cast bridging a raw-typed dependency; the real cause is often in a completely different file.
- Code compiles cleanly around `@SuppressWarnings("unchecked")` with no adjacent validation — a latent heap-pollution bug waiting for the exact input the library returns an unexpected type for.
- **Prevention:** validate actual runtime types immediately at any unchecked-cast boundary bridging a raw-typed API, converting an eventual far-away failure into an immediate, traceable one.

## Related

- `handbook/java-core/streams-and-collectors.md`
