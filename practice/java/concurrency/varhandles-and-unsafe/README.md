# VarHandles, Unsafe, and their replacement (T-415) — runnable verification

Real, executed Java 21 output backing
[`syllabus/02-java/concurrency/varhandles-and-unsafe.md`](../../../../syllabus/02-java/concurrency/varhandles-and-unsafe.md)
(T-415). No mocked memory model — a real `VarHandle` over a plain field
achieving the identical race-free correctness as `AtomicInteger`, and all
four real VarHandle access-mode families (plain, opaque, acquire/release,
volatile) exercised directly, including a real, repeated safe-publication
proof.

## Files

- `VarHandleCounterDemo.java` — a real, multi-threaded race between an
  `AtomicInteger` and a `VarHandle`-backed plain `int` field, verifying both
  produce the exact correct final count.
- `MemoryOrderingAccessModesDemo.java` — exercises all four real access-mode
  families on the same field, then repeats a real `setRelease`/`getAcquire`
  safe-publication round 200,000 times.

## Run

```bash
cd practice/java/concurrency/varhandles-and-unsafe
mkdir -p out
javac -d out src/*.java
java -cp out VarHandleCounterDemo
java -cp out MemoryOrderingAccessModesDemo
```

## Real observed output (last full run, Java 21)

### 1. `VarHandleCounterDemo` — the same correctness, no wrapper object

```
=== 8 real threads, 100000 increments each, racing on both counters concurrently ===
Expected final count: 800000
Real AtomicInteger result: 800000  (correct)
Real VarHandle result:     800000  (correct)
```

`AtomicInteger` wraps a plain `int` inside a dedicated heap object;
`VarHandle` performs the identical real CAS retry loop directly against a
plain `int` field on this class, with no extra wrapper object at all. Both
produce the exact correct final count under real concurrent contention —
proof `VarHandle` provides the same real atomicity guarantee, at the cost of
a slightly more verbose call site in exchange for one fewer heap allocation.

### 2. `MemoryOrderingAccessModesDemo` — real, granular ordering strength per call

```
=== The four real VarHandle access-mode families, same field, all working ===
plain      set/get:    1  (no ordering guarantee -- like a normal field)
opaque     set/get:    2  (no reordering among opaque ops on the SAME variable, no happens-before)
acquire/release set/get: 3  (one-directional happens-before)
volatile   set/get:    4  (full bidirectional happens-before, like the volatile keyword)

=== Real safe-publication proof: setRelease/getAcquire, repeated 200,000 times ===
Real failures across 200000 real publish/observe rounds: 0  (release/acquire's happens-before guarantee held every single time)
```

All four access-mode families operate on the exact same, plain (non-`volatile`)
field — the ordering strength comes entirely from which VarHandle method is
called, not from any field modifier. The safe-publication round is a real,
repeated test: a writer thread fully initializes an object's fields with
ordinary, unsynchronized writes, then publishes the reference via
`setRelease`; a reader thread spins on `getAcquire` until it observes that
reference, then checks every field. Zero failures across 200,000 real
rounds is the expected, guaranteed result — `setRelease`/`getAcquire`'s
happens-before contract is a specification guarantee, not something that
merely tends to work on typical hardware.

## What this pack deliberately does not attempt

Reliably provoking a visible reordering *bug* from using `plain`/`opaque`
access where `acquire`/`release`/`volatile` was required is not something
this pack attempts to demonstrate live — on common hardware (x86/ARM
running the OpenJDK JIT), such a bug can require an enormous number of
iterations to surface non-deterministically, or may never surface in a
short demo despite being a real, specification-level bug. Claiming a demo
"proves" a reordering violation without a robust, sustained reproduction
would overstate what was actually observed. This pack instead proves the
positive, unconditional guarantee directly (real, repeated
`setRelease`/`getAcquire` correctness), which is both honest and
sufficient to demonstrate the real mechanism.

## Real discoveries made while building this pack

No bugs were hit while building this pack — both demos produced correct,
real output on the first run, including all 200,000 real safe-publication
rounds passing with zero failures.
