# java.util.concurrent Synchronizers — Real Demo

Backs [`syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md`](../../../../syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md) (T-417).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/SynchronizersDemo.java
java -cp out SynchronizersDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **`CountDownLatch` blocks until every signal arrives** — three workers with
  staggered real delays (100ms/250ms/400ms); `await()` measurably unblocks
  only after ~400ms, not after the first or fastest worker.
- **`CountDownLatch` is one-shot** — a latch already at zero returns from
  `await()` immediately, every time; there is no reset method.
- **`CyclicBarrier` is reusable** — the same instance's barrier action fires
  twice across two real, independently-run rounds, each requiring all three
  parties to arrive again.
- **`Semaphore` really bounds concurrency** — 10 tasks compete for 3 permits;
  an `AtomicInteger` tracks the real concurrent-holder count at every
  acquire/release, and the measured maximum never exceeds 3.
