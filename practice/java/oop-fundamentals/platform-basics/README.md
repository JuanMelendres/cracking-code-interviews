# Platform Basics Demo (T-2209)

Real, compiled, executed evidence backing [Java Platform Basics: JVM, JDK, JRE, and Primitive Types](../../../../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md).

## Files

- `src/PrimitiveTypesDemo.java` — all 8 primitive types with real ranges, real byte/int overflow wraparound, the real `0.1 + 0.2` floating-point surprise, real autoboxing/unboxing, the real Integer cache (`-128..127`) gotcha, and a real `NullPointerException` from auto-unboxing a `null` wrapper. 11/11 assertions pass.
- `primitive-types-output.txt` — the real captured output of running that demo.
- `jdk-tooling-transcript.txt` — real `java -version`/`javac -version` output plus a real listing of this machine's JDK `bin/` directory (confirming `javac`, `jlink`, `jshell`, `jar`, etc. are present — the concrete evidence that a JDK bundles the compiler and tools, not just the runtime) and its `jmods/` directory (69 real module files, the modern JPMS-era mechanism for building a custom minimal runtime with `jlink` — the practical replacement for the standalone JRE download Oracle stopped offering after Java 8/9).

## Reproduce it yourself

```bash
cd practice/java/oop-fundamentals/platform-basics
javac -d out src/PrimitiveTypesDemo.java
java -cp out PrimitiveTypesDemo
```

For the JDK tooling evidence, run `java -version`, `javac -version`, and inspect your own JDK installation's `bin/` directory the same way — the exact path depends on your OS and installation method (see `jdk-tooling-transcript.txt` for how this was found on macOS with a Homebrew-installed OpenJDK 21).
