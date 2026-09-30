# Java Concurrency Fundamentals — Real, Executed Demo

Backs [Java Concurrency Fundamentals](../../../../syllabus/02-java/concurrency/java-concurrency-fundamentals-threads-races-and-synchronization.md) (T-2214).

Pure JDK, no dependencies. OpenJDK 21.0.12, 10 cores.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out ConcurrencyFundamentalsDemo
```

Full output in `output-transcript.txt`. Sections 3, 4, and 6 produce different numbers every run — for 3 and 4 that *is* the finding. Section 5 was identical across every run.

## What it proves

### 1–2. Starting a thread, and `start()` versus `run()`

Three ways to create one — subclassing `Thread`, passing a `Runnable`, passing a lambda — all producing a real thread with its own name. Then the distinction that catches people:

```text
calling d.run()   ->     ran on: main
calling d.start() ->     ran on: Thread-1
```

`run()` is an ordinary method call on the current thread. Only `start()` creates one. Calling `run()` by mistake produces code that is entirely sequential and looks concurrent.

### 3. Finishing order is not starting order

Six threads released simultaneously by a start gate, each doing a small varying amount of work. Four consecutive runs:

```text
3 0 1 4 2 5
3 4 0 1 2 5
0 3 1 4 2 5
0 3 4 1 5 2
```

Four different orders. **An earlier version of this demo started the threads one at a time and printed `0 1 2 3 4 5` on every run** — each trivial task finished before the next thread was even created, so nothing interleaved. That version would have "demonstrated" non-determinism while actually showing determinism, so the demo was changed to use a start gate rather than the claim being softened.

### 4. The race condition, and why `volatile` does not fix it

8 threads × 100,000 increments = 800,000 expected:

```text
plain int, counter++                 229,765 /   800,000   lost 570,235  <- UPDATES LOST
volatile int, counter++              177,394 /   800,000   lost 622,606  <- STILL LOST
synchronized block                   800,000 /   800,000   lost       0  correct
AtomicInteger.incrementAndGet        800,000 /   800,000   lost       0  correct
```

Roughly **70–85% of the increments are lost** without synchronization. Across six runs the plain counter landed between 132,587 and 229,765, and the volatile counter between 135,457 and 195,568 — the two overlap, and the volatile counter came out *higher* than the plain one in two of the six runs. The honest statement is that **`volatile` does not help**, not that it is worse.

The mechanism: `counter++` is three operations — read, add, write back. `volatile` guarantees each read sees the latest value; it does nothing to stop another thread interleaving between the read and the write. That gap is the lost update.

This is the single most common Java concurrency misconception, and the two numbers on adjacent lines settle it without argument.

### 5. What `volatile` *does* solve

A reader thread spins until a flag flips; another thread sets it 200 ms later.

```text
plain boolean flag : reader exited 2,794 ms after the write  <- NEVER SAW IT (hit the 3s cap)
volatile flag      : reader exited 0 ms after the write
(spins recorded by the plain reader: 323,522,386)
```

The non-volatile reader **never observed the write at all** — it spun 323 million times and only exited because the demo caps it at three seconds. Reproduced identically on every run. The JIT is entitled to hoist a non-volatile read out of the loop, and here it did.

So the pair of demos gives the whole rule: `volatile` fixes **visibility** and not **atomicity**. Section 4 is the atomicity half; section 5 is the visibility half.

### 6. The correct options are not equally cheap

8 threads × 200,000 contended increments, after warmup:

```text
synchronized block            :    63 ms  (result 1,600,000)
AtomicInteger                 :    50 ms  (result 1,600,000)
```

Both correct, with a modest gap. The README states the decision rule rather than the timing, because the timing is the less useful half: `AtomicInteger` is a single compare-and-swap on **one** variable, while `synchronized` takes a lock and can protect **several fields together**. A lock guarding two related fields is not replaceable by two atomics, whatever the benchmark says.
