---
title: "T-308 · JIT: Tiered Compilation, Inlining, and Deoptimization"
topic_id: T-308
domain: JVM
tier: Advanced
iwi: 5.45
prerequisites: [T-301]
unlocks: []
week: 16
last_reviewed: 2026-07-31
canonical: ../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md
---

# T-308 · JIT: Tiered Compilation, Inlining, and Deoptimization

**IWI 5.45 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [JIT: Tiered Compilation, Inlining, and Deoptimization](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md). This file is the Week 16 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-16/jit-compilation/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
4. [Trade-offs](#4-trade-offs)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. The concept

HotSpot executes bytecode through progressively more optimized tiers (interpreter → C1 → C2). Deoptimization is the mechanism for safely undoing a speculative optimization (e.g., a monomorphic call-site assumption) once runtime behavior violates it. → [Definition and Purpose](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#definition-and-purpose).

## 2. Why it exists

Compiling everything maximally upfront is too slow to be practical; tiered compilation defers C2's expensive aggressive optimization to methods proven hot, and deoptimization is the necessary safety valve for the speculative bets that make C2's biggest wins possible. → [Definition and Purpose](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#definition-and-purpose).

## 3. The measured evidence

Real warmup speedup: ~330 ns/op (`-Xint`, pure interpreter) vs. ~34 ns/op (default tiered) — a ~9.6x measured difference on identical code. Real deoptimization: a monomorphic call site's C2 compilation was marked "made not entrant" the instant a second type was introduced; the first affected call took ~2.62ms versus ~1.27ms for the identical call re-run after recompilation. → [Internal Implementation](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#internal-implementation) has the full trace.

## 4. Trade-offs

Speculative optimization trades common-case peak performance for a real, one-time recompilation cost whenever the speculated assumption is violated — measured at roughly 2x on the first affected call. → [Trade-offs](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#trade-offs).

## 5. Interview questions

1. Your service is measurably slower for the first minute after every deploy, then recovers. Explain why, and what would you do operationally.
2. A brief, unexplained latency spike correlates with a feature-flag rollout introducing a second implementation at a hot call site. No GC event, no deploy. What's happening?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#interview-questions).

## 6. Common mistakes

Saying "JIT warmup" without naming the tier mechanism; treating every "made not entrant" log line as a problem rather than routine housekeeping; not knowing deoptimization exists, misattributing a real deopt-caused spike to GC. → [Common Mistakes](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#common-mistakes).

## 7. Staff-level discussion

Connects deoptimization risk to rollout planning (pre-warm against the full expected type/branch profile before a gradual flag rollout reaches significant traffic) rather than defaulting to "probably GC" for unexplained anomalies. → [Staff-Level Discussion](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#interview-answer-framework).

## 8. Summary

Tiered compilation trades a small performance ceiling for fast time-to-reasonable-performance; deoptimization safely undoes violated speculative bets. Measured directly: ~9.6x tiered-vs-interpreted speedup; a real deopt costing ~2x on the first affected call after a type-profile change. → [Summary](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#practice-exercises) and [Solutions](../../handbook/jvm/jit-tiered-compilation-and-deoptimization.md#solutions). Reproducible demos: `practice/java/week-16/jit-compilation/`.

## 13. Additional Reading

- [Aleksey Shipilëv — JVM Anatomy Quarks: Deoptimization](https://shipilev.net/jvm/anatomy-quarks/2-deoptimization/)

## 14. Official References

- [Java HotSpot VM Performance Enhancements (Java 21)](https://docs.oracle.com/en/java/javase/21/vm/java-hotspot-virtual-machine-performance-enhancements.html)
