---
title: "Cheat Sheet: Foreign Function & Memory API"
slug: foreign-function-and-memory-api
document_type: cheat-sheet
domain: concurrency
topic_id: T-416
canonical: ../handbook/concurrency/foreign-function-and-memory-api.md
last_updated: 2026-09-01
---

# Foreign Function & Memory API

**Canonical chapter:** [`handbook/concurrency/foreign-function-and-memory-api.md`](../handbook/concurrency/foreign-function-and-memory-api.md)

*Expert tier, rare interview frequency, recognition-level only per this handbook's own register notes — this cheat sheet is intentionally brief.*

## Core Mental Model

Before FFM, calling native code from Java meant writing actual C glue code (JNI) — a separate compiled artifact and a real category of crash-the-JVM bugs. Off-heap memory access meant `sun.misc.Unsafe` (unsafe by name and nature) or `ByteBuffer.allocateDirect` (safe but limited). FFM lets Java code describe native memory layouts and native function signatures *directly*, entirely in Java, and lets the JVM generate the actual calling and memory-access code at runtime — no C compiler involved, with real safety checks neither JNI nor `Unsafe` provide.

## Essential Definitions

- **`MemorySegment`** — a reference to a contiguous region of memory, on-heap or off-heap, with real bounds checking and enforced lifetime.
- **`MemoryLayout`** — describes the structure (size, alignment, nested fields) of memory being accessed — the Java-side equivalent of a C struct.
- **`Arena`** — controls a segment's real lifetime: `ofConfined()` ties it to explicit `close()`; `ofAuto()` ties it to garbage collection.
- **`Linker`** — the real JNI replacement; builds a `MethodHandle` that performs a genuine native function call.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Interview asks "what is FFM for"? | Name both problems it solves: safe off-heap memory, native calls without JNI |
| Interview probes version status? | State it precisely: finalized JDK 22 (JEP 454); preview in JDK 21 (JEP 442) |
| Tempted to claim deep production fluency? | Don't — scope the answer to recognition-level per this topic's own register notes |

**Trade-offs:**

| Mechanism | Native calls without a build toolchain? | Off-heap safety checks? |
|---|---|---|
| JNI | No — requires C compilation | No |
| `sun.misc.Unsafe` | N/A (memory-only) | No |
| FFM API | Yes | Yes — real use-after-close exception |

## Key Numbers (real, executed JDK 21 output, third preview)

```
=== Real safety proof: using the segment AFTER its Arena has closed ===
Real exception thrown instead of a crash or silent garbage read: IllegalStateException: Already closed

Real native strlen() result: 66
Match: true (matches Java's String.length())
```

## Common Pitfalls

- Overstating fluency with an Expert-tier, recognition-level topic rather than honestly scoping what's known.
- Getting the version status wrong — finalized JDK 22 (JEP 454), not JDK 21 (still preview, JEP 442).
- Assuming FFM eliminates all risk from native interop — its safety guarantees apply to Java-side memory/lifetime management, not to what a native function itself does.

## Interview Answer Skeleton

**30-sec:** FFM replaces two historically painful mechanisms: JNI for native calls, and `Unsafe`/direct `ByteBuffer`s for off-heap memory — both from pure Java, with real safety checks neither old mechanism provided. Finalized in JDK 22; preview in JDK 21.

**2-min:** Add the real use-after-close exception proof and the real, zero-glue-code native `strlen` call — then explicitly flag this as Expert-tier, rare-frequency material with recognition-level familiarity, not deep production experience.

**Staff-level framing:** Calibrate the depth of the answer to the topic's actual rare interview frequency — demonstrating good judgment about what merits deep preparation versus recognition-level familiarity is itself part of the signal.

## Production Warning Signs

- Not directly demonstrated in this chapter — FFM is scoped as recognition-level, not a production-incident topic in this handbook.

## Related

- `handbook/concurrency/varhandles-and-unsafe.md`
- `handbook/jvm/native-memory-direct-buffers-and-off-heap.md`
