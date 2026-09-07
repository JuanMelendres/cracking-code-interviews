---
title: "Flashcards: How a Computer Executes a Program"
slug: how-a-computer-executes-a-program
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2002"
canonical: ../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md
last_updated: 2026-09-07
---

# Flashcards: How a Computer Executes a Program

**Canonical chapter:** [`syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md`](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: The fetch-decode-execute cycle

**Prompt:**
What are the three steps of the loop a CPU repeats, forever, to execute a program?

**Answer:**
Fetch (read the next instruction from the address the program counter points at), decode (figure out what the instruction means), execute (actually perform it — arithmetic, a memory read/write, or changing the program counter, which is what an `if`, a loop, or a function call ultimately compiles down to).

**Why it matters:**
Everything else — objects, methods, `for` loops, `HashMap`s — is a story built on top of this loop; it's the layer every "it's O(1), just a memory access" or "the stack overflowed" claim ultimately rests on.

**Common trap:**
Treating "the computer runs the program" as too abstract to need an actual mechanism, rather than seeing it as this specific, tiny, repeating loop.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: Bytecode is not machine code

**Prompt:**
Does `javac` compile Java source directly into the CPU's native machine code?

**Answer:**
No. `javac` compiles Java source into JVM bytecode — a platform-independent, stack-machine instruction set (e.g., `iload_0`, `iadd`, `ireturn`) that no physical CPU can execute directly. Turning that bytecode into real, CPU-specific machine code is the JVM's job at run time (via an interpreter, or the JIT compiler once a method is hot), not `javac`'s job at build time.

**Why it matters:**
It's the mechanical basis for "write once, run anywhere" — the same `.class` file runs on any machine with a JVM, because the JVM, not `javac`, produces the hardware-specific instructions.

**Common trap:**
Saying "Java compiles to machine code" flatly, with no bytecode step at all — or the opposite over-correction, assuming the JVM is a pure interpreter with no compilation step.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: Interpreter then JIT — why Java's first seconds run slower

**Prompt:**
Why can a Java method run measurably slower in its first several calls than in later calls, with no code change?

**Answer:**
The JVM starts every method interpreted — translating and executing bytecode one instruction at a time — and only promotes a method to compiled native machine code, via the JIT compiler, once its invocation count crosses a threshold marking it "hot." Early calls pay the interpretation cost; later calls run the compiled version directly.

**Why it matters:**
It's the concrete, non-mysterious explanation for "Java's first few seconds are sometimes slower than its next few minutes" — it isn't slower Java, it's bytecode still being interpreted.

**Common trap:**
Assuming the JVM picks interpretation or compilation once, globally, rather than promoting individual hot methods as evidence accumulates that compiling them is worth it.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: Stack depth does not scale linearly with `-Xss`

**Prompt:**
In the chapter's own measurement (OpenJDK 21.0.12), a parameterless recursive method survived 2,333 nested calls before `StackOverflowError` at `-Xss256k`. Roughly how many nested calls did it survive at the `2048k` platform default — and is that exactly 8x more (matching the 8x larger stack)?

**Answer:**
32,949 nested calls — roughly 14x more depth for only an 8x larger stack, not a clean linear scaling. (At `8m`, it reached 145,996 — about 63x, for a 32x larger stack.)

**Why it matters:**
Shows a fixed per-thread overhead (guard pages the JVM reserves so `StackOverflowError` handling itself has somewhere safe to run) eats a proportionally larger share of a small stack than a large one — a real, physical cost, not a measurement artifact.

**Common trap:**
Assuming a bigger `-Xss` scales usable recursion depth exactly linearly with the size increase.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: The JVM does not optimize tail recursion

**Prompt:**
If a Java method is written in a tail-recursive style (its recursive call is the very last thing it does), does the JVM reuse the current stack frame instead of pushing a new one?

**Answer:**
No. Unlike some other language runtimes (e.g., Scheme, some functional languages) that guarantee tail-call optimization, the JVM always pushes a new stack frame for a tail-recursive call — a Java method written to look tail-call-safe can still overflow.

**Why it matters:**
It's exactly why an "isolate frame count from frame size" recursive demo reliably overflows rather than running forever, even though the same code shape wouldn't in a language that optimizes tail calls.

**Common trap:**
Assuming tail-call-shaped recursion is automatically converted into a loop by the JVM.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)

## Card: `StackOverflowError` is an `Error`, not an `Exception`

**Prompt:**
Is `StackOverflowError` a checked `Exception` a program is expected to recover from in the ordinary sense?

**Answer:**
No — it's an `Error`, deliberately signaling that the JVM itself may now be in a compromised state, not a recoverable business-logic problem. Catching it is reasonable for measurement or a last-resort top-level handler, but not as ordinary control flow.

**Why it matters:**
Shapes how it should — and shouldn't — be handled in real code: it's a signal something is structurally wrong (usually unbounded recursion), not a condition to branch on routinely.

**Common trap:**
Treating `StackOverflowError` like a checked exception meant to be caught and handled as part of normal program logic.

**Related:**
[syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md](../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md)
