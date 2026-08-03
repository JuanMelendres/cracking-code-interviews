---
title: "Cheat Sheet: Java Memory Model and volatile"
slug: java-memory-model-and-volatile
document_type: cheat-sheet
domain: concurrency
topic_id: T-401
canonical: ../handbook/concurrency/java-memory-model-and-volatile.md
last_updated: 2026-08-03
---

# Java Memory Model and volatile

**Canonical chapter:** [`handbook/concurrency/java-memory-model-and-volatile.md`](../handbook/concurrency/java-memory-model-and-volatile.md)

## Core Mental Model

`volatile` is a promise about *order*, not about *where a value lives*. The intuitive "forces a read from RAM instead of a cache" framing is exactly backwards — CPU cache-coherence protocols already keep caches consistent automatically. The real problem is that the *compiler/JIT* is allowed to prove a loop never modifies a variable and stop re-reading it — valid in a single-threaded model, silently wrong once another thread does the modifying. `volatile` tells the compiler not to apply that optimization and establishes a happens-before edge between the write and any subsequent read.

## Essential Definitions

- **Java Memory Model (JMM)** — a spec for what values a thread is *guaranteed* to observe when it reads memory another thread wrote.
- **happens-before** — a partial order (not global/total) — if A happens-before B, every write A made is guaranteed visible to B; only actions connected by a specific rule are ordered.
- **volatile** — establishes happens-before between a write on one thread and a read on another; guarantees visibility of each individual read/write, **not** atomicity of a sequence.
- **visibility** vs **atomicity** — visibility: is a write guaranteed observable by another thread? Atomicity: does a sequence (`count++` = read-modify-write) execute as one indivisible step? `volatile` gives you the first, never the second.
- **Five happens-before rules** — program order; monitor lock rule (unlock happens-before subsequent lock of the same monitor); volatile variable rule; thread start/join rule; final field rule (safe publication of immutable objects).

## Decision Table

| Need | Mechanism |
|---|---|
| Single flag/reference visibility across threads | `volatile` |
| Atomic compound operations (increment, compare-and-set) | `AtomicInteger`/`AtomicReference` or `synchronized` |
| Safe publication of an immutable object | `final` fields, properly constructed |
| Mutual exclusion + visibility together | `synchronized` |

**Trade-offs:**

| Mechanism | Benefit | Cost |
|---|---|---|
| Plain field | Cheapest access | No visibility guarantee across threads at all |
| `volatile` | Visibility + ordering, no lock contention | Does NOT make compound operations atomic |
| `synchronized` | Visibility + mutual exclusion + atomicity | Lock contention; deadlock risk with multiple locks |
| `final` field | Safe publication, zero runtime sync cost | Only applies at construction |

## Key Numbers (real, executed — 3 separate runs, same result each time)

Two threads, plain `boolean` flag vs. `volatile boolean` flag, main sets the flag after the worker spins 1.5s (long enough for JIT to compile/optimize the loop):

```
non-volatile flag: worker STILL RUNNING 5002ms after the flag was set -- update never observed
volatile flag:      worker stopped 0ms after the flag was set, having run 5,848,056,485 iterations
```

Root cause: JIT hoists the loop-invariant read (`while (!stop) i++;` → `if (!stop) { while (true) i++; }`), keeping the value in a register instead of re-reading main memory — **not** a CPU cache-coherence problem.

## Common Pitfalls

- Describing `volatile` as being about CPU/hardware caching rather than compiler-visible ordering guarantees
- Believing `volatile` makes multi-step operations atomic
- Treating happens-before as a total order across all threads rather than a partial order
- Double-checked locking without `volatile` (or the static-holder alternative)

## Interview Answer Skeleton

**30-sec:** `volatile` = happens-before edge between a write and subsequent reads of that field; an ordering guarantee, not caching. Doesn't make `count++` atomic — needs `AtomicInteger` or `synchronized`.

**2-min:** Add why it exists (compilers/CPUs can reorder/cache writes invisibly to a single thread) + the classic loop-hoisting bug + the measured non-volatile flag never observed across three runs.

**Whiteboard:** Draw Thread 1 write → "happens-before edge" arrow → Thread 2 read, with a "compiler" box between the write and the field annotated: "without volatile, the compiler may prove this read is loop-invariant and never re-check the field at all."

**Staff-level framing:** the JMM is why "it worked on my machine" is a legitimate, dangerous failure mode — an unsynchronized pattern may work under a given JIT tier/CPU/optimization level and fail silently after a JVM upgrade or longer runtime. "No observed bug in testing" is weak evidence for concurrent code.

## Production Warning Signs

- A background config-refresh thread's update to a plain (non-volatile) flag stops being picked up after a routine JVM minor-version upgrade — while other parts of the system update normally
- "It worked in testing" is explicitly **not** evidence of correctness for this bug class — the failure depends on JIT tier and warm-up duration, not wall-clock testing time
- **Prevention:** any field written by one thread and read by another in a polling loop, with no other synchronization, should be flagged in code review — "is it `volatile`?"

## Related

- `handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md`
