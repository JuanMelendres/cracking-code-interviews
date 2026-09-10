# Bytecode and Class File Fundamentals — Real Demo

Backs [`syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md`](../../../../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md) (T-2406).

Pure JDK (`javac`/`java`/`javap`), plus a tiny pure-Java byte-patcher
(`ClassBytePatcher.java`) used only to reproduce two real JVM failures
deterministically. Requires `xxd` for the raw hex dump (standard on
macOS and most Linux distributions).

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).
The script recompiles `SumLoop.class` fresh between the two corruption
scenarios, so the checked-in `src/` always reflects the correct, unpatched
source — only the temporary `out/` build directory is ever modified, and
it is removed at the end of the run.

## What it proves

- **A `.class` file's first four bytes are always `CA FE BA BE`, and the
  next four encode the class file version.** A real hex dump shows
  `cafe babe 0000 0041` — `0041` (65) is Java 21's real major version
  number, matching `javap`'s own `major version: 65` line.
- **`javap -v` reveals the real constant pool, method bytecode, and
  verification metadata a compiler produces for genuinely simple source.**
  A one-method class with a `for` loop and a field write disassembles to
  real, named instructions (`iconst_0`, `iload_3`, `if_icmpgt`, `iadd`,
  `iinc`, `goto`, `putfield`) and a real `StackMapTable` attribute — the
  same attribute the verifier consults, not merely documentation.
- **`UnsupportedClassVersionError` is a real, exact, version-number
  comparison, not a vague compatibility check.** Patching only the major
  version bytes upward and rerunning produces a real exception naming
  both the file's (patched) version and the exact maximum version this
  specific JVM supports.
- **The bytecode verifier performs real, type-aware stack analysis before
  a method ever executes.** Patching a single opcode byte (`iadd` →
  `iaload`, which expects an array reference where two `int`s were
  actually left on the stack) produces a real `VerifyError: Bad type on
  operand stack`, with the JVM's own real bytecode-offset location and
  stack-frame contents in the error — proof the verifier is a genuine,
  detailed type-checker, not a structural sanity check alone.
