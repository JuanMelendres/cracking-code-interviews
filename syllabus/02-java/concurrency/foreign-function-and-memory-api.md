---
title: "Foreign Function & Memory API"
slug: foreign-function-and-memory-api
document_type: handbook-chapter
domain: 02-java/concurrency
status: draft
version: 1.0
last_updated: 2026-09-03
source_history:
  - handbook/concurrency/foreign-function-and-memory-api.md
difficulty:
  - advanced
target_levels:
  - staff
estimated_reading_minutes: 20
topic_id: T-416
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - varhandles-and-unsafe.md
related:
  - varhandles-and-unsafe.md
  - ../jvm-internals/native-memory-direct-buffers-and-off-heap.md
  - ../../../practice/java/concurrency/foreign-function-and-memory-api/README.md
official_references:
  - https://openjdk.org/jeps/442
  - https://openjdk.org/jeps/454
---

# Foreign Function & Memory API

> **Topic register:** T-416 (Foreign Function & Memory API, IWI 3.4) · Expert tier · Rare interview frequency — **recognition-level only**, per this handbook's own register notes on T-414/415/416.
> **Provenance:** every result in this chapter's Java Examples section is
> real, executed JDK 21 output (third preview, [JEP 442](https://openjdk.org/jeps/442);
> finalized as [JEP 454](https://openjdk.org/jeps/454) in JDK 22, not
> available in this environment) — a real off-heap allocation, a real
> use-after-close safety exception, and a real, direct call into libc's
> `strlen` with zero JNI glue code. Reproducible source:
> [`practice/java/concurrency/foreign-function-and-memory-api/`](../../../practice/java/concurrency/foreign-function-and-memory-api/README.md).

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Java Examples](#java-examples)
9. [Trade-offs](#trade-offs)
10. [Comparisons](#comparisons)
11. [Common Mistakes](#common-mistakes)
12. [Interview Answer Framework](#interview-answer-framework)
13. [Interview Questions](#interview-questions)
14. [Summary](#summary)
15. [Key Takeaways](#key-takeaways)
16. [Cheat Sheet](#cheat-sheet)
17. [Flashcards](#flashcards)
18. [Practice Exercises](#practice-exercises)
19. [Solutions](#solutions)
20. [Additional Reading](#additional-reading)
21. [Official References](#official-references)

## Learning Objectives

After this chapter you should be able to:

- Name the two real problems the Foreign Function & Memory (FFM) API
  solves — safe off-heap memory access, and calling native code without
  JNI.
- Name its four core abstractions (`MemorySegment`, `MemoryLayout`, `Arena`,
  `Linker`) and what each is responsible for.
- Explain, at a recognition level, why FFM is safer than both
  `sun.misc.Unsafe` and hand-written JNI.
- Recognize FFM's real, current version status (finalized in JDK 22, a
  third preview in JDK 21) well enough not to overstate its availability.

## Why This Matters in Interviews

This is a rare-frequency, Expert-tier, recognition-level topic — most
interviews never reach it, and this handbook's own register notes say so
explicitly. When it does come up, it's almost always a scope-check: does the
candidate know FFM exists, roughly what problem it solves, and that it
replaces two historically painful things (JNI for native calls, `Unsafe`/
direct `ByteBuffer`s for off-heap memory) — not a request for deep,
practitioner-level fluency. Overclaiming depth here, or getting the
preview/finalized version status wrong, is a worse signal than honestly
saying "I know it exists and roughly what it replaces, but I haven't used it
in production."

## Level 1 — Foundation

**This is a recognition-level-only topic per this chapter's own scope** — most application engineers will never use it directly. The Foreign Function & Memory (FFM) API lets Java code call native (C-language) functions and access memory outside the JVM's normal heap directly, without writing separate C glue code (the older way of doing this, called JNI).

The everyday, practical level of knowledge needed: knowing this API exists and roughly what it replaces (JNI for calling native code, and `Unsafe`/direct `ByteBuffer`s for off-heap memory) is genuinely sufficient for the vast majority of working engineers and interviews on this topic.

## Level 2 — Working Knowledge

Given this chapter's own explicit "recognition-level only" scope, there isn't a deeper everyday working pattern to build beyond Level 1 — the practical, honest answer if this comes up is simply naming the two problems FFM solves (safe off-heap memory access, and calling native code without JNI) and its current version status (Section notes it precisely), rather than claiming hands-on fluency most engineers genuinely won't have.

## Mental Model

Before FFM, calling native code from Java meant writing actual C glue code
(JNI) — a real, separate compiled artifact, a real build-toolchain burden,
and a real category of crash-the-JVM bugs if the glue code got anything
wrong. Off-heap memory access meant either `sun.misc.Unsafe` (unsafe by
name and by nature) or `ByteBuffer.allocateDirect` (safe but limited).
FFM's real idea: let Java code describe native memory layouts and native
function signatures *directly*, entirely in Java, and let the JVM generate
the actual calling and memory-access code at runtime — no C compiler
involved, and with real safety checks (like the use-after-close exception
this chapter's own demo proves) that neither JNI nor `Unsafe` provide.

## Definition and Purpose

The **Foreign Function & Memory (FFM) API** (JEP 454, finalized in Java 22;
JEP 442, third preview in Java 21) is a pure-Java API for two related
problems: allocating and safely accessing memory outside the JVM heap
(replacing `sun.misc.Unsafe` and direct `ByteBuffer`s), and calling native
(C) library functions without writing JNI glue code (replacing JNI
entirely for most use cases). It exists because both of those older
mechanisms carry real, serious risk — JNI bugs can corrupt the JVM process
outright, and `Unsafe` provides no safety net at all — and because modern
use cases (calling into native libraries for performance-critical code,
GPU/AI workloads, systems programming) needed a safer, standard, pure-Java
path that didn't exist before.

## Core Concepts

- **`MemorySegment`** is a reference to a contiguous region of memory,
  on-heap or off-heap, with real bounds checking and a real, enforced
  lifetime.
- **`MemoryLayout`** describes the structure (size, alignment, nested
  fields) of memory being accessed — the Java-side equivalent of a C
  struct definition.
- **`Arena`** controls a `MemorySegment`'s real lifetime: `Arena.ofConfined()`
  ties it to explicit `close()` (proven directly in this chapter's demo to
  throw a real exception on any use afterward), while `Arena.ofAuto()` ties
  it to normal Java garbage collection.
- **`Linker`** is what actually replaces JNI: it builds a real
  `MethodHandle` that, when invoked, performs a genuine native function
  call — proven directly in this chapter's demo calling libc's `strlen`
  with zero hand-written glue code.

## Java Examples

The real, decisive off-heap safety result:

```
=== Real safety proof: using the segment AFTER its Arena has closed ===
Real exception thrown instead of a crash or silent garbage read: IllegalStateException: Already closed
```

The real, decisive native-call result — zero JNI glue code:

```
Java string:              "Hello from pure Java, calling real native libc code with zero JNI!"
Real Java String.length(): 66
Real native strlen() result: 66
Match: true
```

## Trade-offs

FFM: pure-Java native interop and off-heap access with real safety checks
JNI and `Unsafe` don't provide — at the cost of being a genuinely advanced,
still-recent API (finalized only in JDK 22) with a real learning curve
around memory layouts and lifetimes. JNI: the long-established, universally
available mechanism — at the cost of a separate native build toolchain and
a real, serious crash-the-JVM risk class FFM is specifically designed to
close.

## Comparisons

| Mechanism | Native calls without a build toolchain? | Off-heap safety checks? |
|---|---|---|
| JNI | No — requires C compilation | No |
| `sun.misc.Unsafe` | N/A (memory-only) | No |
| FFM API | Yes | Yes — proven directly with a real use-after-close exception |

## Common Mistakes

- Overstating fluency with an Expert-tier, recognition-level topic in an
  interview, rather than honestly scoping what's actually known.
- Getting FFM's version status wrong — it's finalized in JDK 22 (JEP 454),
  not JDK 21, where it remains a preview feature (JEP 442).
- Assuming FFM eliminates all risk from native interop — calling into
  genuinely unsafe native code can still corrupt memory; FFM's safety
  guarantees apply to the Java-side memory and lifetime management, not to
  what a native function itself does.

## Interview Answer Framework

### 30-Second Answer

The Foreign Function & Memory API replaces two historically painful
mechanisms: JNI for calling native code, and `Unsafe`/direct `ByteBuffer`s
for off-heap memory — both from pure Java, with real safety checks neither
old mechanism provided. It's finalized in JDK 22; a preview feature in
JDK 21.

### 2-Minute Answer

FFM solves two related problems: safely accessing off-heap memory, and
calling native library functions without writing JNI glue code. `MemorySegment`
is a real, bounds-checked reference to a memory region; `Arena` controls its
real lifetime — I've verified directly that using a segment after its
`Arena` closes throws a real exception instead of silently reading freed
memory. `Linker` is what replaces JNI: I've called libc's `strlen` directly
from Java, with zero hand-written glue code, and gotten back the exact
correct byte length. This is genuinely Expert-tier, rare-frequency
material — I know what problem it solves and its core shape, but I'd flag
that it's still a fairly new, finalized-in-JDK-22 API without deep
production experience behind it.

### Senior-Level Expectations

Name the two problems FFM solves and its four core abstractions without
prompting; know its correct version status.

### Staff-Level Discussion

Calibrate the depth of the answer to the topic's actual, rare interview
frequency — demonstrating good judgment about what merits deep preparation
versus recognition-level familiarity is itself part of the signal here.

## Interview Questions

### Question 1: What two problems does the Foreign Function & Memory API solve?

**Why interviewers ask it.** It's a scope check for a rare-frequency topic —
does the candidate know what FFM is for at all.

**Expected answer.** Safe off-heap memory access (replacing `Unsafe`/direct
`ByteBuffer`s) and calling native code without JNI glue code.

**Minimum acceptable answer.** Names one of the two problems.

**Strong Senior answer.** Names both, plus at least one core abstraction
(`MemorySegment`, `Arena`, or `Linker`).

**Staff-level extension.** Correctly states FFM's real version status
(finalized JDK 22, preview in JDK 21) and calibrates the depth of the
answer to the topic's actual rarity.

**Common mistakes.** Confusing FFM with `sun.misc.Unsafe` as the same
mechanism.

**Likely follow-ups.** "What does `Arena` actually control?"

**Evaluation criteria.** Both problems named (3), correct version status (2).

## Summary

The Foreign Function & Memory API replaces JNI (for native calls) and
`Unsafe`/direct `ByteBuffer`s (for off-heap memory) with a pure-Java,
safety-checked alternative. This chapter proves its two defining safety and
capability claims directly: a real use-after-close exception instead of a
silent, unsafe memory read, and a real native call into libc's `strlen`
with zero hand-written JNI glue code. It's Expert-tier and rarely asked
about in interviews — this handbook's own register notes say so
explicitly — so recognition-level fluency, correctly scoped, is the
appropriate depth target, not deep practitioner expertise.

## Key Takeaways

- FFM solves two real problems: safe off-heap memory, and native calls
  without JNI.
- `Arena` provides real, enforced lifetime control — proven directly with a
  real use-after-close exception.
- `Linker` genuinely replaces JNI — proven directly calling libc's `strlen`
  with zero glue code.
- FFM is finalized in JDK 22 (JEP 454); a preview feature in JDK 21
  (JEP 442) — get this right rather than overstating availability.
- This is a recognition-level topic by this handbook's own register notes —
  calibrated brevity is the correct interview response, not deep depth.

## Cheat Sheet

- **`MemorySegment`**: a real, bounds-checked reference to a memory region.
- **`Arena`**: controls a segment's real lifetime (`ofConfined`, `ofAuto`).
- **`Linker`**: builds a real `MethodHandle` for a native function call —
  the actual JNI replacement.
- **Version**: finalized JDK 22 (JEP 454); preview in JDK 21 (JEP 442).
- **Depth target**: recognition-level, per this handbook's own register
  notes — know what it is, not deep practitioner fluency.

## Flashcards

### Card: What does FFM replace?

**Prompt:**
What two older mechanisms does the Foreign Function & Memory API replace?

**Answer:**
JNI (for calling native code) and `sun.misc.Unsafe`/direct `ByteBuffer`s
(for off-heap memory) — both replaced with a pure-Java, safety-checked API.
Measured directly: a real native `strlen` call with zero JNI glue code, and
a real use-after-close exception instead of an unsafe memory read.

**Why it matters:**
It's the actual scope of what this Expert-tier, rare-frequency topic is
for.

**Common trap:**
Confusing FFM with `Unsafe` as interchangeable, rather than FFM being the
safer replacement for it.

**Related:**
[[foreign-function-and-memory-api]], [[varhandles-and-unsafe]]

## Practice Exercises

1. Extend `OffHeapMemoryDemo` to allocate a `MemoryLayout`-described
   struct-like region (multiple fields) instead of a single primitive, and
   read/write each field by its real offset.
2. Extend `NativeCallDemo` to call a different real libc function (e.g.,
   `getpid`, which takes no arguments) and verify the real returned value is
   a plausible process ID.

## Solutions

Exercise 1 requires `MemoryLayout.structLayout(...)` and computing real
field offsets via `layout.byteOffset(...)`; left as self-directed practice
as a genuinely different FFM feature (structured layouts) from this
chapter's own single-value demo. Exercise 2 is a direct variant of
`NativeCallDemo`'s existing `Linker.downcallHandle` pattern with a
no-argument `FunctionDescriptor`; left as self-directed practice since the
existing demo already isolates the exact pattern to adapt.

## Additional Reading

- [VarHandles, Unsafe, and Their Replacement](varhandles-and-unsafe.md)
  covers `sun.misc.Unsafe`'s history and its own, separate public
  replacement — read alongside this chapter for the full picture of what
  replaced `Unsafe`'s two historical use cases (atomic field access and
  off-heap memory).
- [Native Memory, Direct Buffers, and Off-Heap](../jvm-internals/native-memory-direct-buffers-and-off-heap.md)
  covers off-heap memory management more broadly, including
  `ByteBuffer`-based approaches this chapter's `MemorySegment` approach is
  a safer alternative to.

## Official References

- OpenJDK, [JEP 442: Foreign Function & Memory API (Third Preview)](https://openjdk.org/jeps/442)
- OpenJDK, [JEP 454: Foreign Function & Memory API](https://openjdk.org/jeps/454)
