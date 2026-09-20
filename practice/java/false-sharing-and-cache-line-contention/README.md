# False Sharing and Cache-Line Contention — Real, Executed Demo

Backs [False Sharing and Cache-Line Contention](../../../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md).
Real OpenJDK 21.0.12, Apple M4 (arm64, 4 performance + 6 efficiency cores), macOS.

## Setup and run

```bash
javac -d out src/FalseSharingDemo.java
java -cp out FalseSharingDemo
```

Real captured output: [`output-transcript.txt`](output-transcript.txt).

## What it proves

Four threads, each atomically incrementing its **own** counter 200 million
times — no logical data sharing between them at all. Two layouts:

- **Unpadded**: all 4 counters packed into 4 consecutive slots of a
  primitive `long[]` — all inside a single 64-byte CPU cache line.
- **Padded**: each real counter spaced 8 `long`s (64 bytes) apart in a
  larger array — provably on separate cache lines (proof in
  [`FalseSharingDemo.java`](src/FalseSharingDemo.java)'s own doc comment).

Real, measured, reproducible result (two independent runs):

```
Unpadded (all 4 counters share a cache line): ~5,900ms
Padded (each counter its own cache line):     ~370ms
Slowdown from false sharing:                  ~16x
```

**A real ~16x slowdown from data the four threads never actually shared.**
The mechanism: every atomic update to any one counter invalidates every
other core's cached copy of that entire 64-byte cache line (CPU cache
coherence operates at cache-line granularity, not per-variable), forcing
real cross-core coherence traffic and CAS retries for the *other* three
counters living on the same line — even though no thread ever reads or
writes another thread's counter.

## A note on the array-of-objects trap

An earlier version of this demo used an array of `AtomicLong` **objects**
or an ArrayList-style array of primitives — the array padded correctly at
the array-slot level. This did not reproduce the effect, because an array
of object *references* doesn't control where the referenced objects
themselves land on the heap; only a primitive array's own elements are
laid out inline and contiguous. Fixed by using a plain `long[]` with
`VarHandle.getAndAdd` for atomic updates, where array-index spacing
directly and reliably controls physical memory layout.
