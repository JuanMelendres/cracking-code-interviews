---
title: "Flashcards: JIT: Tiered Compilation, Inlining, and Deoptimization"
slug: jit-tiered-compilation-and-deoptimization
document_type: flashcard-deck
domain: jvm
topic_id: T-308
canonical: ../handbook/jvm/jit-tiered-compilation-and-deoptimization.md
last_updated: 2026-08-06
---

# Flashcards: JIT: Tiered Compilation, Inlining, and Deoptimization

**Canonical chapter:** [`syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md`](../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md)

## Card: "Made not entrant" vs a true deoptimization

**Prompt:**
What's the difference between "made not entrant" and a true deoptimization?

**Answer:**
"Made not entrant" is routine — an older compiled version retired because a better-tier one exists. A true deoptimization is a runtime assumption (e.g., monomorphic dispatch) getting violated, forcing an in-flight bailout to the interpreter.

**Why it matters:**
Prevents treating every recompilation event in a log as a costly correctness bailout.

**Common trap:**
Reading any "not entrant" log line as evidence of an expensive deoptimization event.

**Related:**
[handbook/jvm/jit-tiered-compilation-and-deoptimization.md](../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md)

## Card: Why speculative optimization exists despite deoptimization risk

**Prompt:**
Why does speculative optimization exist if it can cause deoptimization?

**Answer:**
It's the source of C2's biggest wins (e.g., inlining a call site assumed monomorphic) — the trade is common-case peak performance for a real but occasional recompilation cost when the assumption is violated.

**Why it matters:**
The explicit trade-off reasoning behind a JIT design choice, not merely a downside to avoid.

**Common trap:**
Treating deoptimization risk as a pure defect rather than the cost side of a deliberate performance trade-off.

**Related:**
[handbook/jvm/jit-tiered-compilation-and-deoptimization.md](../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md)

## Card: Measured tiered-JIT speedup over interpretation

**Prompt:**
Measured directly, roughly how much steady-state speedup did tiered JIT compilation give over pure interpretation?

**Answer:**
~9.6x (330ns/op interpreted vs. ~34ns/op tiered-compiled, same workload).

**Why it matters:**
A concrete, measured number grounding "JIT compilation is faster" in an actual figure.

**Common trap:**
Citing JIT's speedup only qualitatively, with no measured comparison.

**Related:**
[handbook/jvm/jit-tiered-compilation-and-deoptimization.md](../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md)
