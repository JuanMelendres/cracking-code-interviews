---
title: "Cheat Sheet: Strings, Interning, Compact Strings, and Builders"
slug: strings-interning-compact-strings-and-builders
document_type: cheat-sheet
domain: java-core
topic_id: T-106
canonical: ../handbook/java-core/strings-interning-compact-strings-and-builders.md
last_updated: 2026-09-02
---

# Strings: Interning, Compact Strings, and Builders

**Canonical chapter:** [`syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md`](../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md)

## Core Mental Model

A string literal reuses a shared pooled object; `new String(...)` explicitly allocates a fresh one; the JVM stores each string's characters in the narrowest encoding that can represent them, silently, based on content alone. Pooling decides whether `==` succeeds; encoding width decides real memory footprint.

## Essential Definitions

- **String constant pool** — literals and compile-time-constant expressions automatically share a JVM-maintained pooled instance; `.intern()` lets a runtime string join it explicitly.
- **Compact Strings** (JEP 254, Java 9+) — an all-Latin-1 string stores one byte/char (`coder=LATIN1`); one non-Latin-1 character flips the *entire* string to two bytes/char (`coder=UTF16`).
- **`StringBuilder`/`StringBuffer`** — mutable buffers for incremental string construction; `StringBuffer` is the legacy, `synchronized` variant.

## Decision Table

| Question | Answer |
|---|---|
| Is this a literal or compile-time constant? | Automatically pooled — no action needed |
| Building a string incrementally, especially in a loop? | Always `StringBuilder` — never `+=` in a loop |
| Need the builder shared safely across threads? | Only then `StringBuffer` (or external synchronization) |
| Runtime string expected to have many duplicates, compared often? | Consider explicit `.intern()` — but measure; overuse bloats the pool |

## Key Numbers

- `String +=` in a loop (60,000 iterations): 100ms vs `StringBuilder.append`: 1ms — measured ~63-147x slower.
- `StringBuilder` vs `StringBuffer` (20,000,000 append+reset): 22ms vs 66ms — measured ~2.8-3.0x slower for `StringBuffer`, even single-threaded.
- Compact Strings: one non-Latin-1 character doubles backing byte array size (11 bytes → 22 bytes for an 11-char string) — real, exact 2.0x measured difference.

## Common Pitfalls

- Assuming `new String("literal")` is ever necessary — just an unnecessary duplicate of an already-pooled value.
- Using `String +=` inside a loop — real, measured quadratic cost.
- Defaulting to `StringBuffer` "to be safe" in single-threaded code — real, unnecessary synchronization cost.
- Assuming Compact Strings' optimization applies per-character — it's per-string, all-or-nothing.

## Interview Answer Skeleton

**30-sec:** Literals/compile-time constants pool automatically (`==` succeeds); `new String(...)`/runtime concatenation don't. Compact Strings store all-Latin-1 at 1 byte/char, falling back to 2 bytes for the whole string at the first non-Latin-1 character — measured exact 2x. `String +=` in a loop is genuinely quadratic (measured 63-147x slower than `StringBuilder`); `StringBuffer` is ~2.8-3x slower than `StringBuilder` even single-threaded.

**2-min:** Add the real, honest self-correction: an initial test character (`'ö'`) turned out to be within Latin-1's range, so the demo was corrected to `'λ'` (genuinely outside Latin-1) to reproduce the intended doubling.

**Whiteboard:** Pooling branch (literal/constant → shared pool vs new/runtime → distinct heap object) beside the encoding branch (all-Latin-1 → 1 byte/char vs even one non-Latin-1 char → 2 bytes/char for the whole string). Draw the quadratic-vs-linear concatenation-cost curve alongside — the 63-147x gap made visual.

**Staff-level framing:** Compact Strings' all-or-nothing rule generalizes: many real optimizations are content-dependent thresholds, not smooth curves — a single "wrong" element can silently flip an entire structure from its optimized path to its unoptimized one. Treat "what's the actual triggering condition, and how fragile is it to real-world data?" as a standing question for capacity planning.

## Production Warning Signs

- A high-throughput log-formatting hot path regresses after a "readability" refactor to `String +=` in a loop — profiling attributes disproportionate CPU to string allocation/array-copy. Fix: switch to `StringBuilder`.

## Related

- `syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md`
