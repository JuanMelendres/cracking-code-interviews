---
title: "How a Computer Executes a Program"
slug: how-a-computer-executes-a-program
document_type: syllabus-topic
domain: 01-computer-science-foundations
topic_id: T-2002
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites: []
related:
  - ../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md
  - ../02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md
  - ../02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md
practice: ../../practice/java/cs-foundations/program-execution/
production_scenarios:
  - ../../production-cookbook/stackoverflowerror-misdiagnosed-as-a-heap-sizing-problem.md
  - ../../production-cookbook/doubling-the-connection-pool-made-latency-worse-under-cpu-saturation.md
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-6.html
  - https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5.2
---

# How a Computer Executes a Program

[JVM Memory Layout and Runtime Regions](../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md) names the heap, metaspace, and per-thread stacks as regions the JVM Specification defines, and shows each is independently exhaustible with real, measured evidence. This topic sits one layer below that one: not what the JVM's runtime areas are, but *why a stack is a stack at all* — what a CPU actually does, instruction by instruction, and how a `.java` file's text becomes registers changing and memory being read and written, with the JVM as one particular, if unusually elaborate, layer in that chain rather than the foundation of it.

## 1. Why This Matters

Every "it's O(1), just a memory access" claim in [Algorithmic Complexity](algorithmic-complexity-and-big-o-from-first-principles.md), every "the stack overflowed" incident, every "this got slow because it wasn't JIT-compiled yet" explanation is a claim about what happens *below* the code you write. An engineer who has only ever reasoned about Java source can explain *what* a program does; an engineer who can also reason about the CPU underneath can explain *why it costs what it costs*, and can tell the difference between "logically correct, just slow because of what the hardware is doing" and "actually a bug" — a distinction that shows up constantly in performance interviews and even more constantly in real production debugging, where a profiler hands you a flame graph full of native frames you're expected to make sense of.

## 2. Prerequisites

None, by design — this is one of this domain's five foundational, prerequisite-free topics (alongside [Algorithmic Complexity](algorithmic-complexity-and-big-o-from-first-principles.md)). It does, however, assume nothing from Java specifically; where Java's own execution model differs from the generic picture, that's called out explicitly in Section 5.

## 3. Foundation (L1)

**A computer executes one instruction at a time, in a loop, forever, until the power turns off or the program tells it to stop.** That loop has a name — the **fetch-decode-execute cycle** — and it is genuinely that simple at its core:

1. **Fetch** — read the next instruction from memory, at the address a special register (the **program counter**, or instruction pointer) is currently pointing at.
2. **Decode** — figure out what that instruction actually means: "add these two numbers," "store this value at that address," "jump to a different instruction if this condition is true."
3. **Execute** — actually do it: perform the arithmetic, move the data, or change the program counter to point somewhere else (which is what an `if` or a loop or a function call ultimately compiles down to).
4. **Repeat**, with the program counter now pointing at the next instruction — which might be the very next one in memory, or somewhere completely different, if step 3 was a jump.

**A "program" is, underneath every abstraction layered on top of it, a list of these tiny instructions in memory**, and a "variable" is, underneath its name and its type, a location in memory or a slot in one of the CPU's own tiny, extremely fast storage slots (**registers**) that a specific instruction reads from or writes to. Everything else — objects, methods, `for` loops, `HashMap`s — is a story humans tell so they don't have to think in these terms directly. The story is genuinely useful; it is also, at some point in a career, worth being able to see through.

## 4. Core Concepts (L2)

**Machine code is the actual, final list of instructions a specific CPU family can execute** — a sequence of raw bytes, specific to an instruction set architecture (x86-64, ARM64, and so on), with no notion of "classes" or "methods," only addresses, registers, and a fixed, small set of operations (load, store, add, compare, jump, call, return). Nothing runs on real hardware except machine code; every other layer discussed below exists to produce it, or to imitate what it would do.

**Java does not compile straight to machine code.** `javac` compiles Java source into **JVM bytecode** — a different, platform-independent instruction set, defined by the *Java Virtual Machine Specification*, that no physical CPU can execute directly. The `practice/` demo for this topic disassembles a two-line method with `javap -c` and shows exactly this: instructions like `iload_0`, `iadd`, and `ireturn` that describe a *stack machine* (values are pushed onto and popped off an operand stack, not moved between named registers) — nothing a real CPU's instruction decoder would recognize. Bytecode's whole purpose is portability: the same `.class` file runs on any machine with a JVM for its specific CPU, because it's the JVM's job, not `javac`'s, to turn that bytecode into real instructions for whatever hardware it's running on.

**Two different strategies exist for that last step, and the JVM uses both.** An **interpreter** reads bytecode instructions one at a time and directly carries out the fetch-decode-execute cycle *for the bytecode*, translating and executing each instruction as it goes — simple to build, immediately correct, but repeats that translation work every single time the same instruction runs. A **compiler** instead translates a whole chunk of bytecode into real machine code *once*, up front (or, for the JVM specifically, the first several times a method runs — see Section 5), so that every subsequent execution runs the fast, native version directly with no translation overhead at all. This exact interpreter-then-compiler strategy, applied specifically to the JVM's own JIT compiler, is what [JIT Tiered Compilation and Deoptimization](../02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md) covers as its whole subject.

**The call stack is the concrete mechanism behind every function call**, in any language, on any hardware — not a Java-specific concept. Calling a function pushes a **stack frame** onto a per-thread region of memory: the address to return to once the function finishes, the function's local variables, and (in the JVM's case) references the operand stack needs. Returning pops that frame back off. This is exactly what [JVM Memory Layout and Runtime Regions](../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md) names as the per-thread stack region; this topic's contribution is the layer below that naming — *why* a stack (last-in-first-out) is the right structure for this at all: a function returns to wherever it was called from, and the most recently entered, not-yet-returned call is always the next one to finish, which is precisely what a stack, and only a stack, gives you for free.

## 5. How It Works Internally (L3)

**The JVM does not pick interpretation or compilation once, globally — it starts every method interpreted, then promotes individual hot methods to compiled machine code as evidence accumulates that the investment is worth it.** This is exactly the subject [JIT Tiered Compilation and Deoptimization](../02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md) covers in depth: a method is interpreted at first, its invocation count tracked, and once it crosses a threshold, the JIT compiler produces native machine code for it — specific to the exact CPU the JVM is running on right now, something `javac`, running ahead of time on a build machine, could never have produced, since it doesn't know what hardware the program will eventually run on. This is the concrete answer to "why is Java's first few seconds sometimes slower than its next few minutes": it isn't slower Java, it's bytecode still being interpreted, one instruction translation at a time, because the JIT hasn't judged it worth compiling yet.

**A CPU register is not memory in the RAM sense — it's a tiny, fixed number of storage slots built directly into the processor itself**, and reading or writing one is dramatically faster than reading or writing RAM, precisely because there's no bus to cross to reach it. A compiler's (whether `javac`'s bytecode-generation pass, conceptually, or, more concretely, the JIT compiler's actual native-code generation) most consequential low-level decisions are about **register allocation**: which of a method's handful of frequently used values get to live in one of the CPU's very few registers versus being repeatedly loaded from and stored back to the stack frame in RAM. This single decision is a large fraction of why hand-written assembly can occasionally still beat compiled code for extremely hot, tiny loops — a human can sometimes make a better register-allocation call for one specific loop than a general-purpose compiler heuristic can.

**The call stack's fixed size is not a JVM design choice layered on top of a limitless underlying resource — it's a real, physical, per-thread memory reservation the operating system makes when the thread is created**, and it cannot grow once the thread exists. The `practice/` demo measures this directly: a recursive method with no parameters and no local variables beyond frame overhead, run under three different `-Xss` values, hits `StackOverflowError` after a different, real, measured number of nested calls each time — `2,333` at `256k`, `32,949` at the platform default (`2048k`, confirmed via `-XX:+PrintFlagsFinal`), `145,996` at `8m`. Section 10 covers what those specific numbers do, and don't, show cleanly.

## 6. Practical Usage

Two habits make this layer visible during ordinary Java work, rather than staying permanently theoretical:

1. **Reading a stack trace as a literal snapshot of the call stack**, top to bottom, at the exact moment an exception was constructed — each line is one stack frame, in the order calls actually nested, which is why the *deepest* call (where the error was thrown) is always printed first and `main` last.
2. **Running `javap -c` on a compiled class whenever "what does this code actually do" stops being answerable by reading the Java source alone** — for instance, confirming whether a particular expression really does what it looks like it does, or checking whether a compiler optimization you're relying on (like `String` constant folding) actually happened, by looking for its evidence directly in the bytecode instead of guessing.

## 7. Examples

```bash
# What javac actually produces for a two-argument add method --
# a stack-machine bytecode, not the CPU's own machine code.
$ javap -c -p -classpath out BytecodeDisassemblyDemo

  static int add(int, int);
    Code:
       0: iload_0
       1: iload_1
       2: iadd
       3: ireturn
```

`iload_0` and `iload_1` push the method's two `int` parameters onto the operand stack; `iadd` pops both and pushes their sum; `ireturn` returns the top of the stack to the caller. Four bytecode instructions for `return a + b;` — none of them anything an x86-64 or ARM64 core executes directly; all of them exactly what the interpreter (or, once hot, the JIT compiler translating this same bytecode into real machine code) actually processes.

```java
// Isolates "how many stack frames fit" from "how big is each frame" --
// no parameters, no locals beyond the implicit frame overhead itself.
private static void recurse() {
    depth++;
    recurse();
}
```

## 8. Common Mistakes

- **Treating "the stack" and "the heap" as two arbitrary buckets memory gets divided into**, rather than as two regions with fundamentally different lifetime and access patterns: stack frames are strictly last-in-first-out and reclaimed automatically the instant a call returns, while heap objects can outlive the call that created them and need a garbage collector, precisely *because* nothing as simple as "pop the frame" tells the runtime when they're no longer needed.
- **Assuming bytecode and machine code are the same thing, or that `javac` "compiles to native code."** `javac` only ever produces JVM bytecode; whether that bytecode is ever turned into real machine code, and when, is entirely the JIT compiler's decision at run time, not `javac`'s at build time — see [JIT Tiered Compilation and Deoptimization](../02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md) for exactly how that decision gets made.
- **Assuming a bigger `-Xss` scales stack depth exactly linearly.** Section 10's real measurements show it doesn't, cleanly, at small stack sizes — a fixed per-thread overhead (guard pages the JVM reserves so it can still run `StackOverflowError`-handling code after detecting the overflow, without itself overflowing) eats a larger fraction of a small stack than a large one.

## 9. Edge Cases

- **Tail-call-shaped recursion is not automatically converted into a loop by the JVM**, unlike some other language runtimes (Scheme, some functional languages) that guarantee tail-call optimization. A Java method written in a way that *looks* like it could reuse the current stack frame instead of pushing a new one still pushes a new one — this is precisely why the "isolate frame count from frame size" demo in this topic's `practice/` directory reliably overflows rather than running forever.
- **A `StackOverflowError` is an `Error`, not an `Exception`**, deliberately signaling "the JVM itself may now be in a compromised state" rather than "your business logic hit a recoverable problem" — catching it (as the demo does, to report a number) is reasonable for measurement or a last-resort top-level handler, but not as ordinary control flow the way a checked exception might be.
- **Native (JNI) stack frames and interpreter/JIT-compiled Java frames can be interleaved on the same physical thread stack**, which is part of why a thread dump occasionally shows frames that don't look like ordinary Java call chains — the underlying OS thread stack doesn't distinguish "whose" frame it's holding.

## 10. Performance Implications

Real, measured results from `practice/java/cs-foundations/program-execution/` on OpenJDK 21.0.12:

| `-Xss` | Nested calls before `StackOverflowError` | Naive linear prediction from `256k` | Actual vs. prediction |
|---|---|---|---|
| `256k` | 2,333 | — (baseline) | — |
| `2048k` (platform default) | 32,949 | 18,664 (8× baseline) | ~1.77× higher than predicted |
| `8m` | 145,996 | 74,656 (32× baseline) | ~1.96× higher than predicted |

**What this actually shows:** stack depth does *not* scale linearly with `-Xss`, and larger stacks consistently yield more usable depth per kilobyte than smaller ones. That's consistent with a **fixed per-thread overhead** (the guard-page region the JVM reserves so `StackOverflowError` handling itself has somewhere safe to run) that consumes a proportionally larger share of a small stack than a large one — a real, physical cost, not a measurement artifact, though this demo alone can't fully separate that explanation from JIT-recompilation effects that might also change this specific recursive method's frame size partway through a long run. Both the `README.md` in the `practice/` directory and this section say that plainly rather than overclaiming a single clean cause.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Bytecode + JVM (Java's actual model) | One compiled artifact runs anywhere a JVM exists; JIT can specialize for the exact CPU actually running the code | Startup cost while methods are still interpreted; an extra translation layer versus ahead-of-time native compilation |
| Ahead-of-time compilation straight to machine code (e.g., C, or GraalVM native image) | No interpreter warm-up; often faster, more predictable startup | Loses "compile once, run anywhere"; loses the JIT's ability to specialize using information only available at run time |
| Larger `-Xss` per thread | Deeper safe recursion before `StackOverflowError` | More reserved memory per thread — multiplies across every thread in a large thread pool, competing with heap and metaspace for the same finite process memory |
| Interpreting a method forever, never JIT-compiling it | No compilation pause, ever, for that method | Leaves real, measurable performance on the table for any method actually called often enough to matter |

## 12. Senior-Level Considerations (L3)

A Senior engineer reading a flame graph or a thread dump treats **native frames and unfamiliar-looking stack entries as informative, not as noise to scroll past.** A profiler surfacing time spent inside JIT compilation, garbage collection, or a native method call is telling you something concrete about where cycles are actually going — dismissing it as "internal JVM stuff, not my code" is exactly the failure mode that hides real, fixable problems (a hot method that never got compiled because it's called through too many different call sites for the JIT's inlining heuristics to specialize well, for instance). The same discipline applies to `-Xss` sizing: it's a real trade-off against total process memory once multiplied across a large thread pool, not a knob to bump reflexively the moment a `StackOverflowError` appears — see Section 13 for exactly when bumping it is the wrong fix.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, this topic's real leverage is recognizing when a symptom that *looks* like an application bug is actually a resource-model mismatch between what the code assumes and what the hardware or OS actually provides underneath. [StackOverflowError Misdiagnosed as a Heap Sizing Problem](../../production-cookbook/stackoverflowerror-misdiagnosed-as-a-heap-sizing-problem.md) is exactly this: an `Error` type most engineers' first instinct associates with memory exhaustion in general gets misdiagnosed as a heap problem, when the actual cause — unusually deep recursion hitting a fixed, per-thread stack reservation, a completely separate memory region from the heap — requires understanding that "the JVM ran out of memory" is not one undifferentiated fact but several distinct, independently exhaustible resources (Section 8's first common mistake, at incident scale).

[Doubling the Connection Pool Made Latency Worse Under CPU Saturation](../../production-cookbook/doubling-the-connection-pool-made-latency-worse-under-cpu-saturation.md) is the same class of mistake one layer further out: more threads does not mean more concurrent progress once a system is already CPU-bound, because a finite number of physical cores can only be actually *executing* instructions (Section 3's fetch-decode-execute loop) for a finite number of threads at once — every additional thread beyond that just adds context-switching overhead, competing for the same cores rather than adding new ones. The Staff-level pattern in both incidents is identical: **before adding more of a resource (stack size, thread pool size, memory), identify which specific physical resource is actually exhausted** — because adding more of the wrong one doesn't just fail to help, it can make the real bottleneck worse by adding overhead (extra threads to schedule, extra memory to reserve) without adding any of the capacity that's actually constrained.

## 14. Production Scenarios

- **[StackOverflowError Misdiagnosed as a Heap Sizing Problem](../../production-cookbook/stackoverflowerror-misdiagnosed-as-a-heap-sizing-problem.md)** — a per-thread call-stack exhaustion (Section 5, Section 10) mistaken for heap exhaustion, because both surface as JVM memory-related errors to an engineer who hasn't internalized that the stack and the heap are separate, independently exhaustible regions.
- **[Doubling the Connection Pool Made Latency Worse Under CPU Saturation](../../production-cookbook/doubling-the-connection-pool-made-latency-worse-under-cpu-saturation.md)** — more threads added to a system that was already CPU-bound (Section 13), where the fetch-decode-execute cycle's fundamental constraint (a finite number of cores can only execute a finite number of instruction streams at once) was the real, physical ceiling no amount of connection-pool tuning could raise.

## 15. Interview Questions

### Question 1 — What's the difference between JVM bytecode and machine code?

**Why interviewers ask it.** It's a fast, direct check for whether a candidate's mental model of "compiling Java" is accurate, or stops at "it turns into something the computer runs" without the actual intermediate step.

**Expected answer.** `javac` compiles Java source into JVM bytecode — a platform-independent, stack-machine instruction set defined by the JVM Specification. No physical CPU executes bytecode directly; the JVM either interprets it one instruction at a time or, once a method is hot, the JIT compiler translates it into real, CPU-specific machine code. Machine code is architecture-specific (x86-64, ARM64); bytecode is not.

**Minimum acceptable answer.** Knows bytecode and machine code are different things, even if hazy on exactly which component (interpreter vs. JIT) produces machine code from bytecode.

**Strong Senior answer.** Names the interpreter-then-JIT progression specifically, and can point to a concrete piece of real evidence for it — e.g., "the first several calls to a method run measurably slower than later calls because it's still being interpreted," or has actually run `javap -c` on real code before.

**Staff-level extension.** Connects this to *why* it's a deliberate design trade-off (Section 11): giving up "compiles once to the fastest possible code" in exchange for "the exact same artifact runs on any hardware with a JVM, and the JIT can specialize using information — like which branches actually get taken at run time — that an ahead-of-time compiler on a build machine could never have."

**Common mistakes.** Saying "Java compiles to machine code" flatly, with no bytecode step at all; or the opposite over-correction, treating the JVM as a pure interpreter with no compilation step, missing the JIT entirely.

**Follow-up questions.** "If bytecode is platform-independent, what part of the JVM isn't?" (The native JVM binary itself — the interpreter and JIT compiler are platform-specific programs; the bytecode they consume is not.) "Why doesn't `javac` just compile straight to machine code and skip this step?" (It would lose "write once, run anywhere," and lose the JIT's ability to use run-time-only information for optimization — Section 11.)

### Question 2 — Why does a program crash with `StackOverflowError` instead of just running out of memory gradually, the way a memory leak does?

**Why interviewers ask it.** It probes whether a candidate understands the stack as a fixed-size, per-thread reservation, distinct from the heap's more gradual, GC-managed exhaustion — a distinction Section 8 names as a common mistake and Section 14's first scenario shows as a real, misdiagnosed incident.

**Expected answer.** Each thread's call stack is a fixed-size block of memory reserved when the thread is created (sized by `-Xss`), not a resizable region like the heap. Every nested call pushes a new frame; once the reserved block is full, there's no more room for another frame, and the JVM throws `StackOverflowError` immediately at that exact call — a hard, sudden boundary, not a gradual slowdown the way heap pressure typically presents.

**Minimum acceptable answer.** Knows the stack has a fixed size that heap memory doesn't, even without precise `-Xss` terminology.

**Strong Senior answer.** Can state that this is measurable and predictable — deeper recursion with smaller stack frames survives longer before overflowing than shallow recursion with large frames (many local variables), and can describe how they'd actually confirm this (run the recursive path under a debugger or with instrumentation counting depth, exactly what this topic's `practice/` demo does directly).

**Staff-level extension.** Names the resource-model trade-off from Section 13: raising `-Xss` to accommodate deeper recursion has a real cost multiplied across every thread in a large pool, so the correct fix for adversarially deep recursion (the scenario in the linked production incident) is usually bounding or restructuring the recursion itself, not indefinitely raising the stack size limit.

**Common mistakes.** Describing `StackOverflowError` as "running out of memory" without distinguishing which memory region — conflating it with heap exhaustion (`OutOfMemoryError`), which is exactly the real, documented misdiagnosis in Section 14's first scenario.

**Follow-up questions.** "Would increasing `-Xheap` (heap size) help this error?" (No — it's the wrong resource entirely; only `-Xss`, or fixing the recursion, addresses a stack overflow.) "Is `StackOverflowError` always a bug?" (Usually, yes, for ordinary application code — but it's a designed, expected outcome for anything deliberately probing stack limits, like this topic's own measurement demo.)

## 16. Coding/Practice Exercises

- Run [`CallStackDepthDemo.java`](../../practice/java/cs-foundations/program-execution/src/CallStackDepthDemo.java) yourself at three different `-Xss` values and reproduce Section 10's table — confirm on your own machine whether the "larger stacks yield more depth per kilobyte" pattern holds, since your absolute numbers will differ from the ones measured here.
- Modify the recursive method to add a handful of local `long` variables before recursing, predict (before running) whether the overflow depth at a fixed `-Xss` will go up or down relative to the zero-local-variable version, then measure and check.
- Compile a small class with a couple of different method bodies (a loop, an `if`/`else`, a method call) and run `javap -c` on each — identify which bytecode instructions correspond to each Java-level construct.

## 17. Debugging Exercises

**Symptom:** a service that has run in production for months without incident suddenly starts throwing `StackOverflowError` on one specific endpoint, immediately after a new feature shipped that accepts user-supplied, arbitrarily nested JSON as part of its request body.

**Diagnose:** walk through why this points at recursive JSON parsing depth rather than a heap or memory-leak problem — check whether heap and overall process memory look normal at the time of the error (they should, per Section 10 and Section 14's first scenario, since a stack overflow and a heap exhaustion are independent failure modes with different symptoms); confirm the mechanism by constructing a test request with deliberately deep nesting and reproducing the error directly, rather than guessing; and state the two legitimate fixes this diagnosis actually supports — bounding the accepted nesting depth at the input-validation layer (the real fix, since arbitrarily deep untrusted input is the actual defect), versus raising `-Xss` (a mitigation that raises the depth needed to trigger the same problem again, not a fix for the underlying unbounded-input issue).

## 18. Design Exercises

**Design constraint:** you're building a service that must safely process user-supplied, arbitrarily deep recursive data structures (nested JSON, nested comment threads, a file-system tree of unknown depth) without a `StackOverflowError` taking down the request thread, regardless of how deeply an adversarial or simply very large input happens to be nested.

Design the parsing/traversal approach so its stack usage does **not** grow with input nesting depth — name the specific technique (an explicit, heap-allocated stack data structure the algorithm manages itself, replacing the call stack the JVM would otherwise grow one frame at a time) and explain precisely why this converts an unbounded *stack* growth problem (fixed-size, Section 10) into a bounded-only-by-available-heap problem (a very different, and much larger, resource ceiling, per [JVM Memory Layout and Runtime Regions](../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)). State what you'd still want to cap explicitly (a maximum nesting depth accepted at all, regardless of which resource holds it) and why removing the *stack* limit doesn't mean removing the need for *any* limit.

## 19. Further Reading

- *The Java Virtual Machine Specification, SE 21*, [Chapter 6 — The `javac` Compiler](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-6.html) and [§2.5.2 — Java Virtual Machine Stacks](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5.2) — the official, authoritative definitions of bytecode and the per-thread stack this chapter builds on.
- Patterson and Hennessy, *Computer Organization and Design* — the standard, comprehensive reference for the fetch-decode-execute cycle, registers, and instruction set architecture at a depth well beyond what any single interview needs, for anyone who wants the full picture below this topic's foundation.
- [JIT Tiered Compilation and Deoptimization](../02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md) — the canonical, in-depth treatment of exactly how and when bytecode becomes real machine code, referenced throughout Sections 4–5 above.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Describe the fetch-decode-execute cycle in plain language and state, correctly, that a program is ultimately a list of tiny instructions a CPU works through one at a time | [Section 3](#3-foundation-l1) |
| L2 | Explain the difference between JVM bytecode and real machine code, and why the JVM needs both an interpreter and a JIT compiler rather than just one or the other | [Interview Question 1](#question-1--whats-the-difference-between-jvm-bytecode-and-machine-code) |
| L3 | Explain why the call stack has a fixed size per thread, predict how changing `-Xss` should affect maximum recursion depth, and correctly reason about why the real, measured relationship isn't perfectly linear | [Section 10's real measurements](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a production symptom (Section 17) as a specific, named resource exhaustion (stack, not heap; CPU cores, not connection pool) rather than reaching for "just add more capacity," and design a system that avoids a whole failure class by choosing a different resource entirely (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
