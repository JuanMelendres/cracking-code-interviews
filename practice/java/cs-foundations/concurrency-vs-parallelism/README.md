# Concurrency vs. Parallelism — Real Measurements

Backs the Concurrency-versus-Parallelism material added to [`syllabus/01-computer-science-foundations/os-process-thread-model.md`](../../../../syllabus/01-computer-science-foundations/os-process-thread-model.md) (T-104).

Pure JDK, no dependencies. Machine: macOS, OpenJDK 21.0.12, `availableProcessors() = 10`.

## Run it

```bash
mkdir -p out
javac -d out src/ConcurrencyVsParallelismDemo.java
java -cp out ConcurrencyVsParallelismDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it measures

**CPU-bound (40 tasks, no I/O at all):**

| Threads | Time |
|---|---|
| 1 | 610 ms |
| 2 | 307 ms |
| 4 | 180 ms |
| 10 (one per core) | 90 ms |
| 40 | 84 ms — **more threads, no better** |

Speedup tracks the core count and then flattens. This is **parallelism**, and it is bounded by hardware.

**I/O-bound (200 tasks, each waiting 50 ms):**

| Threads | Time |
|---|---|
| 1 | 10,656 ms |
| 10 (one per core) | 1,072 ms |
| 50 | 221 ms |
| 200 (one per task) | 71 ms |
| Virtual threads (one per task) | **64 ms** |

The identical thread-count increase that bought nothing for CPU work bought a **150x** improvement here, because a waiting thread consumes no CPU. This is **concurrency**: overlapping the waiting, so total time approaches the latency of one call rather than their sum.

**Amdahl's law:** a serial phase followed by 40 parallel tasks ran 642 ms on one thread and 128 ms on ten — a **5.02x** speedup with 10 cores, not 10x, because the serial phase is unchanged no matter how many cores exist. That ceiling is why "just add threads" stops working earlier than people expect.

**Concurrency with zero parallelism:** two tasks interleaved by a single thread, both in progress at once, neither running simultaneously — what a single-threaded event loop does.
