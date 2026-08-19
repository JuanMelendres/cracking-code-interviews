# Atomics, CAS, and the ABA problem (T-405) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/concurrency/atomics-cas-and-the-aba-problem.md`](../../../../handbook/concurrency/atomics-cas-and-the-aba-problem.md)
(T-405). Three independent demos: real measured CAS-vs-lock throughput, a deterministic
real reproduction of the ABA problem, and its real fix.

## Setup and run

```bash
cd practice/java/concurrency/atomics-cas-and-aba
mkdir -p out
javac -d out src/*.java
java -cp out CasVsSynchronizedDemo
java -cp out AbaProblemDemo
java -cp out AbaFixWithStampedReferenceDemo
```

No special JVM flags needed.

## Real observed output (last run)

### `CasVsSynchronizedDemo` — real measured throughput, 8 threads x 500,000 increments each

```
CAS counter: expected=4000000 actual=4000000 (correct, no lost updates)
synchronized counter: expected=4000000 actual=4000000 (correct, no lost updates)

== Real measured wall-clock time, 8 threads x 500000 increments ==
AtomicInteger (CAS retry loop): 70ms
synchronized counter:           154ms
```

Both approaches are correct — no lost updates under real contention from 8 real threads. The
CAS retry loop (`AtomicInteger.incrementAndGet()`) measured roughly 2x faster than the
`synchronized`-guarded counter at this contention level, the real cost difference between a
lock-free retry loop and OS-mediated lock acquisition/release on every single increment.

### `AbaProblemDemo` — a real, deterministic reproduction of ABA corruption

```
Initial stack (top to bottom): [A, B, C]
Thread 1 read oldTop=A, capturedNext=B -- then is preempted before its CAS runs
Thread 2 popped: A -- stack now [B, C]
Thread 2 popped: B -- stack now [C]
Thread 2 pushed A back (same object) -- stack now [A, C]

Thread 1's CAS(expected=A, new=B) succeeded: true -- top IS reference-equal to A, so plain AtomicReference CAS cannot tell anything happened in between
Stack after Thread 1's CAS: [B, C]  <-- CORRUPTED: B is resurrected at the top (it was already popped), and Thread 2's real push of A is lost
```

No real thread race is needed to reproduce ABA — it's a logical blind spot in reference-identity
CAS, reproduced here by manually interleaving two "threads'" operations on a single thread in a
fixed, deterministic order. Thread 1 captures the top (`A`) and its successor (`B`) before being
"preempted." Thread 2 fully runs: pops `A`, pops `B`, then pushes `A` back — the *same* object.
When Thread 1 resumes, its CAS only checks reference identity of `top`, which is still `A` — so it
succeeds, setting `top` to the already-popped `B`. The real, printed result is a corrupted stack:
`B` is resurrected at the top even though it was legitimately removed, and Thread 2's real push of
`A` is silently lost.

### `AbaFixWithStampedReferenceDemo` — the identical interleaving, with the real fix applied

```
Initial stack (top to bottom): [A, B, C]
Thread 1 read oldTop=A, stamp=3, capturedNext=B -- then is preempted before its CAS runs
Thread 2 popped: A -- stack now [B, C]
Thread 2 popped: B -- stack now [C]
Thread 2 pushed A back (same object) -- stack now [A, C], real current stamp=6 (bumped 3 times: pop, pop, push)

Thread 1's CAS(expected=A, new=B, expectedStamp=3) succeeded: false -- reference IS still == A, but the stamp moved from 3 to 6, so the stamped CAS correctly detects the interleaving and rejects it
Stack after Thread 1's (rejected) CAS: [A, C]  <-- CORRECT: unchanged from what Thread 2 legitimately produced; Thread 1 must retry its pop from scratch
```

The exact same interleaving, but the stack's top is now an `AtomicStampedReference<Node>` whose
integer stamp is bumped on every mutation (`push`, `pop`). Thread 1's stale CAS is real, correctly
rejected — `false` — even though the reference is still identity-equal to `A`, because the stamp
moved from 3 to 6 while Thread 1 was "preempted." This is the standard, real JDK fix for ABA:
compare a monotonically-changing stamp alongside the reference, not the reference alone.
