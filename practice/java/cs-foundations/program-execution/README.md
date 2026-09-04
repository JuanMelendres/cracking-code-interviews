# Program Execution — Real, Executed Evidence

Evidence base for [How a Computer Executes a Program](../../../../syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md) (T-2002). Two small, real, compiled-and-run demos: one showing the call stack's fixed-size, per-thread nature directly; one showing what `javac` actually emits (JVM bytecode, not machine code) via `javap -c`.

Environment: OpenJDK 21.0.12 (Homebrew build), macOS, `21.0.12 2026-07-21`.

## 1. Call stack depth vs. `-Xss`

`src/CallStackDepthDemo.java` recurses with no parameters and no local variables beyond frame overhead, counting calls until `StackOverflowError`, isolating "how many frames fit" from "how big is each frame."

```bash
javac -d out src/CallStackDepthDemo.java src/BytecodeDisassemblyDemo.java
java -cp out CallStackDepthDemo               # default -Xss
java -Xss256k -cp out CallStackDepthDemo
java -Xss8m -cp out CallStackDepthDemo
```

Real captured output:

| `-Xss` | Nested calls before `StackOverflowError` |
|---|---|
| `256k` | 2,333 |
| default (`2048k` — confirmed via `-XX:+PrintFlagsFinal -version \| grep ThreadStackSize`) | 32,949 |
| `8m` | 145,996 |

**Honest reading of the numbers.** This is *not* a clean linear relationship with stack size, and that's itself the finding, not a flaw in the measurement. Naively, `8m` is 32× larger than `256k`, so a linear model predicts `2,333 × 32 = 74,656` — the real result, `145,996`, is roughly double that. Going from `256k` to the `2048k` default (8×) predicts `18,664`; the real result is `32,949`, again well above the linear prediction. Larger stacks yield *more* depth per kilobyte than smaller ones, which is consistent with the stack having a **fixed per-thread overhead independent of its total size** — guard pages and a reserved zone the JVM keeps so it can still run the `StackOverflowError` handling code *after* the overflow is detected, without itself overflowing. That fixed cost is a larger fraction of a small stack than a large one, so small stacks look disproportionately worse per kilobyte than large ones. Going from `2048k` to `8m` (4×) is closer to linear (`131,796` predicted vs. `145,996` actual) — consistent with the fixed overhead mattering less as the stack itself gets bigger.

## 2. What `javac` actually produces

`src/BytecodeDisassemblyDemo.java` defines one trivial method. `javap -c` disassembles the compiled class file:

```bash
javap -c -p -classpath out BytecodeDisassemblyDemo
```

Real captured output:

```
  static int add(int, int);
    Code:
       0: iload_0
       1: iload_1
       2: iadd
       3: ireturn

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: iconst_2
       4: iconst_3
       5: invokestatic  #13                 // Method add:(II)I
       8: invokevirtual #19                 // Method java/io/PrintStream.println:(I)V
      11: return
```

This is **JVM bytecode** — a stack-machine instruction set `javac` emits — not the machine code the CPU itself executes. `iload_0`/`iload_1` push the two int arguments onto an operand stack; `iadd` pops both, pushes their sum; `ireturn` returns it. None of this is x86-64 or ARM64. The chapter's Section 5 walks through what happens to these exact instructions next: interpreted one at a time at first, then compiled to real native machine instructions by the JIT compiler once `add` is called often enough to be judged "hot."
