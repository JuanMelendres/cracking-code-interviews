---
title: "Cheat Sheet: How a Computer Executes a Program"
slug: how-a-computer-executes-a-program
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2002
canonical: ../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md
last_updated: 2026-09-06
---

# How a Computer Executes a Program

**Canonical chapter:** [`syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md`](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Core Mental Model

A program is, underneath every abstraction, a list of tiny instructions in memory that a CPU works through one at a time via the fetch-decode-execute cycle; a "variable" is just a memory location or register a specific instruction reads or writes.

## Essential Definitions

- **Fetch-decode-execute cycle** — fetch the next instruction at the program counter's address, decode what it means, execute it, repeat.
- **Machine code** — the raw, final instruction set a specific CPU family (x86-64, ARM64) executes directly; no notion of classes or methods, only addresses, registers, and a fixed op set.
- **JVM bytecode** — the platform-independent, stack-machine instruction set `javac` produces (`iload_0`, `iadd`, `ireturn`); no physical CPU executes it directly.
- **Interpreter vs. compiler** — an interpreter translates and executes bytecode one instruction at a time, every time; a compiler (the JIT) translates a chunk once into native machine code so later runs skip translation entirely.
- **Call stack / stack frame** — calling a function pushes a frame (return address, locals, operand-stack references) onto a per-thread memory region; returning pops it — LIFO, because the most recently entered, not-yet-returned call is always the next to finish.
- **Register** — a tiny, fixed-count storage slot built into the CPU itself; reading/writing one is far faster than RAM because there's no bus to cross.

## Decision Table

| Concept | What produces it | Executed by |
|---|---|---|
| Machine code | `javac` never produces this directly | Real CPU, directly |
| JVM bytecode | `javac` | Interpreter, then JIT once hot |
| Cold method | First several invocations | Interpreter (translates every time) |
| Hot method | Invocation count crosses a threshold | JIT-compiled native code (specific to the exact CPU running now) |

## Common Pitfalls

- Treating "the stack" and "the heap" as two arbitrary buckets rather than regions with fundamentally different lifetime/access patterns — stack frames are strictly LIFO and auto-reclaimed on return; heap objects can outlive their creating call and need a GC.
- Assuming bytecode and machine code are the same thing, or that `javac` "compiles to native code" — `javac` only ever produces bytecode; turning it into machine code is the JIT's runtime decision, not `javac`'s build-time one.
- Assuming a bigger `-Xss` scales stack depth exactly linearly — it doesn't; a fixed per-thread guard-page overhead eats a larger fraction of a small stack than a large one.
- Assuming tail-call-shaped recursion gets optimized into a loop by the JVM — it doesn't, unlike some other language runtimes; a new frame is pushed every time.

## Interview Answer Skeleton

**30-sec:** `javac` compiles Java to platform-independent JVM bytecode, not machine code. The JVM interprets bytecode at first, then the JIT compiles hot methods into real, CPU-specific machine code once invocation counts cross a threshold — trading a warm-up cost for "compile once, run anywhere" plus run-time specialization an ahead-of-time compiler could never do.

**2-min:** Add the `javap -c` evidence (`iload_0`, `iadd`, `ireturn` — a stack machine, nothing an x86-64/ARM64 decoder recognizes directly), the call-stack mechanism (why LIFO is the right structure — the most recent unfinished call always finishes next), and the measured `-Xss` result: stack depth doesn't scale linearly with size because of a fixed guard-page overhead.

**Staff-level framing:** Before adding more of a resource (stack size, thread pool size, memory) to fix a symptom, identify which specific physical resource is actually exhausted — adding more of the wrong one doesn't just fail to help, it adds overhead (extra memory reserved, extra threads to schedule) without adding the capacity that's actually constrained.

## Production Warning Signs

- `StackOverflowError` misdiagnosed as a heap sizing problem: an `Error` type most engineers associate with memory exhaustion in general gets treated as a heap issue, when the real cause is unusually deep recursion hitting a fixed, separate per-thread stack reservation. Fix: recognize the stack and heap as independently exhaustible regions; raising `-Xheap` never helps a stack overflow.
- Doubling the connection pool made latency worse under CPU saturation: more threads added to an already CPU-bound system just adds context-switching overhead, since a finite number of physical cores can only execute a finite number of instruction streams at once. Fix: identify whether the system is actually CPU-bound before adding more of a resource that competes for the same cores.
- Symptom: `StackOverflowError` starts appearing on one endpoint right after it began accepting arbitrarily nested user-supplied JSON. Diagnose by confirming heap/process memory look normal (rules out heap exhaustion) and reproducing with a deliberately deep test payload; fix by bounding accepted nesting depth at input validation, not by raising `-Xss`.

## Real Measured Numbers

- `StackOverflowError` nested-call counts (OpenJDK 21.0.12): `256k` → 2,333 calls; `2048k` (default) → 32,949 calls (~1.77× a naive linear prediction); `8m` → 145,996 calls (~1.96× a naive linear prediction).

## Related

- syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md
- syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md
- syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md
