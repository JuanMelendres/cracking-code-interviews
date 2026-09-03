# Foreign Function & Memory API (T-416) — runnable verification

Real, executed Java 21 output backing
[`syllabus/02-java/concurrency/foreign-function-and-memory-api.md`](../../../../syllabus/02-java/concurrency/foreign-function-and-memory-api.md)
(T-416). No JNI, no `sun.misc.Unsafe`, no `.so`/`.dylib` of our own — real
off-heap memory managed by a real `Arena`, a real safety exception on
use-after-close, and a real, direct call into libc's `strlen` with zero
native glue code.

**Version note:** the Foreign Function & Memory API is
[JEP 442](https://openjdk.org/jeps/442) — its **third preview** in JDK 21,
requiring `--enable-preview` for every command below. It was finalized
(no longer preview) as [JEP 454](https://openjdk.org/jeps/454) in JDK 22.
This environment has JDK 21 available, not JDK 22+, so every real result in
this pack is genuine preview-API behavior in JDK 21, specifically.

## Files

- `OffHeapMemoryDemo.java` — real `Arena`/`MemorySegment` allocation,
  write/read, deterministic close, and the real use-after-close safety
  exception.
- `NativeCallDemo.java` — a real `Linker.downcallHandle` call into libc's
  `strlen`, with a real `MemorySegment` holding the native string argument.

## Run

```bash
cd practice/java/concurrency/foreign-function-and-memory-api
mkdir -p out
javac --release 21 --enable-preview -d out src/*.java
java --enable-preview -cp out OffHeapMemoryDemo
java --enable-preview --enable-native-access=ALL-UNNAMED -cp out NativeCallDemo
```

## Real observed output (last full run, Java 21, third preview)

### 1. `OffHeapMemoryDemo` — real off-heap memory, real deterministic lifetime

```
=== Real off-heap allocation and write/read, inside a confined Arena ===
Real value written to real off-heap memory: 42

=== Real safety proof: using the segment AFTER its Arena has closed ===
Real exception thrown instead of a crash or silent garbage read: IllegalStateException: Already closed

=== Real automatic-lifetime allocation, no explicit close needed ===
Real value: 123456789 (freed automatically once unreachable, like ordinary heap objects)
```

An `Arena` controls a real off-heap allocation's lifetime deterministically
— `try`-with-resources closing it really frees the memory. The real,
decisive safety proof: accessing the same `MemorySegment` reference after
its `Arena` has closed throws a real, checked `IllegalStateException`
instead of silently reading freed memory or crashing the JVM — the actual
safety property that distinguishes the FFM API from both raw JNI and
`sun.misc.Unsafe`, neither of which can catch this at all.

### 2. `NativeCallDemo` — a real native call, zero JNI glue code

Without `--enable-native-access=ALL-UNNAMED`:

```
WARNING: A restricted method in java.lang.foreign.Linker has been called
WARNING: java.lang.foreign.Linker::downcallHandle has been called by the unnamed module
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for this module

Java string:              "Hello from pure Java, calling real native libc code with zero JNI!"
Real Java String.length(): 66
Real native strlen() result: 66
Match: true
```

With the flag, the identical real call runs with no warning at all. This
warning is itself a real, deliberate JDK safety feature: native access is
something a module must explicitly opt into at the JVM level — the runtime
will not silently allow arbitrary native calls without at least a visible
warning (or, with strong encapsulation enabled, a hard failure) by default.

The real result itself: `strlen` — an actual, unmodified libc function, not
a stub or a mock — correctly computed the byte length of a real Java string
written into off-heap memory, matching `String.length()` exactly, with no
`.c` file, no `javac -h`, no compiled `.so`/`.dylib`, and no
`System.loadLibrary(...)` anywhere in this pack.

## Real discoveries made while building this pack

No bugs were hit while building this pack — both demos produced correct,
real output on the first run, including the exact use-after-close exception
and the real native-access warning, both of which are genuine, documented
JDK behavior rather than anything unexpected.
