---
title: "File Systems and Durable I/O: Page Cache, Buffering, and fsync"
slug: file-systems-and-durable-io
document_type: syllabus-topic
domain: 01-computer-science-foundations
topic_id: T-2440
status: canonical
version: 1.0
last_updated: 2026-09-28
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - memory-hierarchy-caches-ram-and-virtual-memory.md
  - os-process-thread-model.md
related:
  - ../02-java/language-core/java-file-io-and-nio2.md
  - ../06-databases/mvcc-vacuum-and-bloat.md
  - ../09-messaging-event-driven/kafka-architecture-fundamentals.md
  - ../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md
practice: ../../practice/java/cs-foundations/file-systems-and-durable-io/
production_scenarios: []
interview_paths: [mid-to-senior, senior-to-staff]
official_references:
  - https://man7.org/linux/man-pages/man2/fsync.2.html
  - https://man7.org/linux/man-pages/man2/write.2.html
  - https://www.postgresql.org/docs/current/wal-reliability.html
---

# File Systems and Durable I/O: Page Cache, Buffering, and fsync

[Memory Hierarchy](memory-hierarchy-caches-ram-and-virtual-memory.md) stops at RAM. This topic continues down one more tier, to the layer every database, message broker, and log file ultimately rests on — and to the single system call that decides whether a committed transaction survives a power cut.

> **Provenance.** Every number below is real, measured output from [`practice/java/cs-foundations/file-systems-and-durable-io/`](../../practice/java/cs-foundations/file-systems-and-durable-io/README.md) (OpenJDK 21.0.12, macOS, APFS on NVMe). It includes an honest negative result: the textbook sequential-versus-random gap did **not** reproduce on this hardware with a warm page cache, and the chapter says so rather than reprinting folklore.

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

"The D in ACID" is a sentence most candidates can say and few can explain mechanically. The mechanism is one system call, `fsync`, and its cost is not small: measured here, forcing durability on every one of 2,000 small appends took **7,704 ms** against **3 ms** with no syncing at all — roughly a 2,000x difference for the identical bytes.

That single measurement explains an enormous amount of production behavior: why databases batch commits, why a "slow disk" incident is usually a sync-rate problem rather than a bandwidth problem, why Kafka's throughput claims always come with a durability qualifier, and why moving a database to a filesystem or volume with different sync semantics can silently change what "committed" means.

It also explains a class of bug nobody looks for: code that writes a file, sees `write()` return successfully, and assumes the data is safe. It is not. It is in RAM.

## 2. Prerequisites

- [Memory Hierarchy: Caches, RAM, and Virtual Memory](memory-hierarchy-caches-ram-and-virtual-memory.md) — the tier above this one, and where the page cache lives.
- [OS Process and Thread Model](os-process-thread-model.md) — system calls and the user/kernel boundary.

## 3. Foundation (L1)

A **file system** is the operating system's answer to "how do I find and organize bytes on a storage device." A device stores fixed-size **blocks** (commonly 4 KB) and knows nothing about names or folders; the file system builds files, directories, and metadata on top of that.

Three ideas carry most of the weight:

- A **file** is a sequence of bytes plus metadata — size, permissions, timestamps. On Unix-like systems the metadata lives in an **inode**, and a directory entry is just a name pointing at one. This is why a "move" within one file system is instant (rename an entry) while a move *across* file systems is a copy and a delete.
- Reading or writing a file is a **system call** — `read`, `write` — which crosses from your program into the kernel. Crossing that boundary costs real time, so the *number* of calls matters as much as the bytes.
- The kernel keeps recently-used file contents in RAM, in the **page cache**. Reads may be served from there without touching the device at all, and writes normally land there first and are written out later.

That last point is the one to internalize early, because it is the source of both the speed and the danger: **`write()` returning successfully does not mean the data is on disk.** It means the data is in the kernel's page cache and the kernel will get to it. If the machine loses power first, the data is gone, and your program was told everything was fine.

Making it durable requires a second, explicit step: `fsync` (in Java, `FileChannel.force(...)` or `FileDescriptor.sync()`), which returns only once the device reports the data persisted.

## 4. Core Concepts (L2)

### Syscall count is a first-class cost

The same 64 MB read, with different buffer sizes — real, measured:

```text
  buffer     512 B ->     48 ms   (131072 read() calls)
  buffer    4096 B ->      9 ms   (16384 read() calls)
  buffer   65536 B ->      5 ms   (1024 read() calls)
  buffer 1048576 B ->      4 ms   (64 read() calls)
```

Identical bytes; a 12x difference. Nothing about the storage device changed — only how many times the program crossed into the kernel. This is exactly what a `BufferedInputStream` or a `BufferedWriter` exists to fix, and it is why "wrap it in a buffered stream" is not a stylistic preference. Returns flatten past a page or two, so the interesting range is 512 B → 64 KB, not 64 KB → 1 MB.

### The page cache makes "disk is slow" a misleading claim

The same 32 MB file read three times in a row:

```text
  first read         3.3 ms     9769.0 MB/s
  second read        2.6 ms    12309.5 MB/s
  third read         2.4 ms    13310.0 MB/s
```

Nothing changed but the cache state. Multi-GB/s "disk" throughput is RAM throughput with the device in the loop only the first time. The practical consequences: a benchmark that ignores cache state measures nothing useful, free RAM is not wasted RAM (the kernel is using it as your read cache), and a process restart is fast while a *machine* restart is slow, because only the latter empties the page cache.

### Durability costs orders of magnitude, and batching is the standard answer

2,000 appends of 256 bytes each:

```text
  no fsync at all                    3 ms
  fsync every record              7704 ms
  fsync every 100 records           91 ms
```

Per-record durability cost roughly **2,000x** the un-synced write. Batching every 100 records brought it to about 30x — still a real cost, but a survivable one.

That middle row is the shape of every database's commit path, and the third row is why **group commit** exists: hold a handful of transactions for a few milliseconds, sync them together, and acknowledge them all. Each individual transaction pays slightly more latency; the system pays dramatically less total sync cost, at *identical* durability. If you understand this trade, you understand why `synchronous_commit` and `commitInterval`-style settings exist and what turning them off actually buys and risks.

### An honest result about sequential versus random access

The textbook claim is that random reads are 10–100x slower than sequential. Measured here, 20,000 4 KB reads each way:

```text
  sequential       24.0 ms     3258.1 MB/s
  random           16.2 ms     4818.6 MB/s
  Ratio random/sequential for the identical 80000 KB: 0.68x
```

Indistinguishable — the random pass was marginally *faster*, which is noise. The file was written moments earlier and was resident in the page cache, and the backing store is NVMe, so neither pattern paid a seek. Dropping the page cache needs elevated privileges, so the demo deliberately does not assert a number it cannot measure.

The honest statement is therefore narrower than the folklore: the large random-access penalty is a **spinning-disk, cache-miss** phenomenon. Sequential access still matters everywhere for other reasons — readahead can prefetch it, it aligns better with erase-block-sized writes on SSDs, and it is friendlier to every prefetching layer above — which is why B-trees, log-structured storage, and Kafka's design still favor it. But quoting "random I/O is 100x slower" as a universal fact about modern hardware is repeating a benchmark nobody re-ran.

## 5. How It Works Internally (L3)

**The write path.** `write()` copies bytes from your buffer into kernel page-cache pages and marks them **dirty**, then returns. A kernel writeback thread flushes dirty pages to the device later, driven by time and dirty-page thresholds. `fsync(fd)` forces the dirty pages for that file — and, critically, waits for the device to acknowledge — before returning. `fdatasync` (Java: `force(false)`) skips metadata that does not affect readability, which is cheaper and is what the demo used.

**Why the device acknowledgement matters.** Drives have their own volatile write caches. A drive that acknowledges before persisting makes `fsync` return early and makes durability a lie under power loss. This is why PostgreSQL documents `wal_sync_method` and disk write-cache behavior explicitly, and why "we lost committed transactions" incidents on consumer-grade hardware are a real category rather than a myth.

**Error handling is the sharp edge.** If writeback fails, the error surfaces on a later `fsync`, not on the `write` that queued the data — and on some kernels, historically, a failed `fsync` cleared the error state so a *second* `fsync` would report success while the data was gone. Code that ignores the return value of `fsync`, or retries it and treats the retry's success as recovery, can report durability it does not have.

**Metadata versus data.** Appending to a file changes both content and metadata (size). A durable append therefore needs the file's data synced *and* the directory entry durable if the file is newly created — which is why careful implementations `fsync` the parent directory after creating a file. Skipping that step is a classic source of "the file exists but is empty after a crash."

**Memory-mapped files** (`mmap`) map file pages directly into the process address space, so reads and writes become memory accesses and the page cache *is* your buffer — no copy into user space. The durability question does not go away; it becomes `msync`. See [Memory-Mapped Files and Zero-Copy I/O](../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md).

## 6. Practical Usage

- Always buffer. Use `BufferedOutputStream`/`BufferedWriter`, or a `ByteBuffer` of at least a page, and prefer one large call to many small ones.
- Know the difference between `flush()` and `fsync`. `flush()` on a Java stream pushes *your* buffer into the kernel; it does **not** make anything durable. Only `force(...)`/`sync()` does.
- Sync deliberately, not reflexively. Sync at meaningful boundaries — a committed transaction, a batch of records — never per tiny write, unless you have measured that you can afford it.
- For a newly created file that must survive a crash: write, `force`, then sync the parent directory.
- Treat `fsync` failures as fatal for that write path, not as retryable noise.

## 7. Examples

Full runnable source at [`practice/java/cs-foundations/file-systems-and-durable-io/`](../../practice/java/cs-foundations/file-systems-and-durable-io/README.md).

```java
// The three durability strategies the demo measures.
try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
     FileChannel ch = file.getChannel()) {
    for (int i = 1; i <= count; i++) {
        ch.write(ByteBuffer.wrap(record));       // reaches the page cache, not the disk
        if (syncEvery > 0 && i % syncEvery == 0) {
            ch.force(false);                     // data only, not metadata -- the cheaper sync
        }
    }
    if (syncEvery > 0) {
        ch.force(false);                         // never leave the tail unsynced
    }
}
```

`force(false)` versus `force(true)` is the `fdatasync`/`fsync` distinction: the former omits metadata that does not affect the ability to read the data back, and is the right default for appending to a file whose size change the file system already records durably.

## 8. Common Mistakes

- **Believing a successful `write()` means durable.** It means "in the page cache."
- **Confusing `flush()` with `fsync`.** `flush()` moves bytes from your buffer to the kernel; durability needs `force(...)`.
- **Unbuffered small writes.** Measured 12x on reads; writes behave the same way.
- **Syncing per record by default**, paying ~2,000x for durability nobody asked for at that granularity.
- **Benchmarking with a warm page cache** and reporting the number as disk performance.
- **Ignoring `fsync`'s return value**, or retrying it and treating success as proof the data survived.
- **Forgetting the parent directory** after creating a file that must survive a crash.

## 9. Edge Cases

- **Drive write caches that lie.** `fsync` returns, the data is still volatile. A real category on consumer hardware and in some virtualized storage.
- **Network and container file systems** (NFS, overlayfs, some volume drivers) with different sync semantics from the local disk you tested on.
- **Sparse files**, where logical size far exceeds allocated blocks, so "disk usage" and "file size" legitimately disagree.
- **Appends beyond a page boundary**, which touch metadata and therefore behave differently under `fdatasync` than in-place overwrites.
- **Many small files** versus one large file: the per-file metadata cost dominates, which is why archive formats and log-structured designs exist.

## 10. Performance Implications

| Operation | Rough cost | What dominates |
|---|---|---|
| Read from page cache | RAM speed | Nothing — no device involved |
| `read()` syscall | Fixed per call | User/kernel crossing, hence buffer size |
| Buffered write | RAM speed | Copy into page cache |
| `fsync` | Device latency | Device acknowledgement — measured ~2,000x an un-synced write per record |
| Batched `fsync` (every 100) | Amortised | ~30x, from the same demo |

The ordering is the lesson: syscall count and sync frequency are the two knobs, and they differ by orders of magnitude in impact.

## 11. Trade-offs

- **Durability versus throughput.** Per-record sync is the safest and slowest; batching trades a few milliseconds of latency for a large throughput gain at identical durability; no sync at all is fast and loses data on power loss.
- **Large buffers versus memory and latency.** Bigger buffers mean fewer syscalls and more unflushed data at risk, plus more memory held.
- **Page cache reliance versus predictability.** Leaning on it gives excellent average performance and terrible worst-case behavior after a cold start.
- **`mmap` versus explicit I/O.** `mmap` removes a copy and complicates error handling and durability.

## 12. Senior-Level Considerations (L3)

Be able to locate the sync boundary in any storage system you operate: what exactly is forced to disk, and at what granularity. For PostgreSQL that is WAL plus `synchronous_commit`; for Kafka it is `flush.ms`/`flush.messages` plus replication, where durability is achieved primarily by *replicas* rather than by syncing every write; for an application writing its own files it is wherever you put `force(...)` — and if you cannot point at that line, the system's durability claim is unverified.

Measure the right thing. A storage complaint is usually a sync-rate or IOPS problem rather than a bandwidth problem, and the two have completely different fixes. Bandwidth graphs look healthy in exactly the incident where sync latency has doubled.

Distrust inherited performance folklore, including from this chapter's own subject area: the random-versus-sequential result here did not reproduce the textbook claim on modern hardware with a warm cache. Re-measure on the hardware you actually run on.

## 13. Staff/System-Level Considerations (L4)

The durability decision is a **product and risk decision**, not a configuration detail, and it belongs in writing. "How much data may we lose in a power failure, and how much latency are we willing to pay to lose less?" has an owner, and answering it implicitly — by leaving a default in place — is still answering it. The honest framing to bring to that conversation is the measured spread: per-record sync, batched sync, and no sync differ by three orders of magnitude in cost and by everything in consequence.

Second, durability is usually achieved at the *replication* layer in modern systems rather than at the disk layer, and conflating the two is a common and expensive mistake. Acknowledging a write on three replicas' page caches is a different guarantee from syncing it to one machine's disk — one survives a machine loss, the other survives a power cut to a single node. A design review should state which failure it is buying protection against, because a team that has bought neither usually believes it has bought both.

Third, storage semantics are a **portability hazard** across environments: the local SSD you benchmarked on, the network volume in staging, and the cloud block store in production have different sync latencies and different failure modes. A durability guarantee validated in one is not validated in the others, and the cheapest way to find out is a deliberate power-loss test in a controlled environment rather than a real incident.

## 14. Production Scenarios

### Scenario: an import job is 50x slower in production than on a laptop

**Symptoms.** A batch importer processes 100,000 records in under a minute locally and takes over an hour in production, on faster hardware. CPU is idle; disk bandwidth is well under capacity.

**Initial hypotheses.** Network latency to the database; a missing index; contention.

**Evidence collected.** The job writes an audit file per record and calls `flush()` plus `sync()` after each one. Locally the file system is a laptop NVMe with an aggressive write cache; production is a network-attached volume with substantially higher sync latency. Bandwidth graphs look fine because the volume of data is tiny — the cost is entirely per-sync round trips.

**Diagnosis.** Sync rate, not bandwidth. The same 2,000x-shaped cost the demo measures, multiplied by 100,000 records and a slower device.

**Immediate mitigation.** Batch the syncs — one sync per 1,000 records — which in the demo's equivalent case cut the cost by roughly 85x while preserving the property that a crash loses at most one batch.

**Permanent remediation.** Write the audit trail as an append-only log with an explicit, documented durability boundary, or move it to a system designed for the job. Record the "we may lose up to one batch on power loss" decision rather than leaving it implicit.

**Interview lesson.** "Slow disk" almost always deserves the follow-up "slow how — bandwidth, IOPS, or sync latency?", and those three have different fixes.

### Scenario: a file exists after a crash but is empty

**Symptoms.** After an unclean shutdown, a config file written by the application exists with zero bytes, and the service refuses to start.

**Diagnosis.** The write reached the page cache and the file's *metadata* (its creation) was persisted before its *content* was. Without an `fsync` on the file and a sync of the parent directory, the two are independent, and the crash landed between them.

**Remediation.** The standard durable-write recipe: write to a temporary file, `force(...)` it, rename over the target (rename is atomic within a file system), then sync the parent directory. The rename gives an all-or-nothing switch and the directory sync makes the switch itself durable.

**Interview lesson.** "Write, sync, rename, sync the directory" is a concrete, memorable answer to "how do you write a file so a crash cannot leave it half-written," and it demonstrates knowing that metadata and data are separately durable.

## 15. Interview Questions

### Question 1 — Does a successful `write()` mean the data is on disk? Explain the mechanism.

**Expected answer.** No. `write()` copies the bytes into the kernel's page cache, marks the pages dirty, and returns; a kernel writeback thread persists them later. Durability requires `fsync`/`fdatasync` (Java: `FileChannel.force(true|false)`), which returns only after the device acknowledges. Measured cost: 2,000 small appends took 3 ms un-synced and 7,704 ms with a sync per record.

**Minimum acceptable answer.** Knows `write` is buffered by the OS and that an explicit sync is needed.

**Strong Senior answer.** Distinguishes `flush()` (your buffer into the kernel) from `fsync` (kernel to device), notes `fdatasync` as the cheaper variant that omits inessential metadata, and mentions that drive write caches can make `fsync` return before data is truly persisted.

**Staff-level extension.** Frames the durability level as a written product decision with an owner, and separates disk-level durability from replication-level durability — acknowledging on three replicas' page caches survives a machine loss, syncing to one disk survives a power cut; a design should say which it is buying.

**Common mistakes.** Treating `flush()` as durability; assuming a successful `fsync` retry proves the data survived.

**Likely follow-ups.** "How would you make writing a file crash-safe?" (Write temp, force, rename, sync the parent directory.) "Why do databases batch commits?" (Group commit: amortise sync cost at identical durability.)

### Question 2 — A job that writes many small records is far slower in production than locally. How do you diagnose it?

**Expected answer.** Separate bandwidth, IOPS, and sync latency before anything else. Small-record workloads are usually dominated by syscall count and sync frequency, neither of which shows up on a bandwidth graph. Check whether the code syncs per record, and check whether the production file system is network-attached with higher sync latency than the local SSD it was tested on.

**Strong Senior answer.** Proposes batching syncs with an explicit statement of what is now at risk (at most one batch), and cites the measured shape: per-record sync ~2,000x an un-synced write, batched every 100 about 30x. Also checks buffer sizes, since unbuffered small I/O costs a real multiple — 12x measured on reads across 512 B to 1 MB buffers.

**Staff-level extension.** Notes that storage semantics are a portability hazard: a durability guarantee validated on a local SSD is not validated on a network volume or a cloud block store, and the cheap way to find out is a deliberate power-loss test rather than an incident.

**Common mistakes.** Concluding "the disk is slow" without distinguishing the three failure shapes; adding threads, which multiplies syncs rather than amortising them.

### Question 3 — Are random reads really 10–100x slower than sequential reads?

**Expected answer.** On spinning disks with a cache miss, yes — that penalty is seek time. On modern NVMe with the data in the page cache, no: measured here, 20,000 4 KB reads each way came out at a ratio of 0.68x, i.e. indistinguishable. The claim is a spinning-disk, cache-miss statement, not a universal fact.

**Strong Senior answer.** Explains why sequential access still matters anyway — readahead can prefetch it, it aligns with erase-block-sized writes on SSDs, and every prefetching layer above benefits — so B-trees, log-structured storage, and Kafka's design remain sequential-friendly for reasons that survive the hardware change. Notes honestly that the demo could not drop the page cache without elevated privileges and therefore does not claim a cold-cache number.

**Staff-level extension.** Generalises it: inherited performance folklore should be re-measured on the hardware actually in use, because storage characteristics have changed by orders of magnitude within the working lifetime of most of the rules people still quote.

**Common mistakes.** Reciting the 100x figure with no conditions; concluding from the measurement that access patterns no longer matter at all.

## 16. Coding/Practice Exercises

1. Run the demo and record your machine's numbers for all four sections. Compare the fsync ratio with the 2,000x measured here.
2. Modify the durability section to sync every 10, 100, and 1,000 records, and plot cost against batch size. Identify where the curve flattens.
3. Implement the crash-safe write recipe (temp file, force, rename, sync the parent directory) and explain what each step protects against.
4. Compare `force(true)` and `force(false)` on append-heavy and overwrite-heavy workloads, and explain any difference.

## 17. Debugging Exercises

1. A service reports healthy disk bandwidth and terrible write latency. List the measurements that distinguish a sync-rate problem from a bandwidth problem.
2. A benchmark shows 10 GB/s "disk" throughput. Determine what was actually measured and how to make it honest.
3. A file created before a crash exists but is empty. Explain the sequence that produced it and the fix.

## 18. Design Exercises

1. Design the durability policy for an audit log that must lose no more than one second of records on power loss. State the sync strategy, the batch size, and the latency cost.
2. You are asked to move a database from local NVMe to a network volume. List what you would validate about sync semantics before agreeing, and how.
3. Design a write path for many small records where durability matters but per-record sync is unaffordable. State exactly what is lost in a crash.

## 19. Further Reading

- [Memory Hierarchy: Caches, RAM, and Virtual Memory](memory-hierarchy-caches-ram-and-virtual-memory.md) — the tier above, including the page cache's home.
- [Java File I/O and NIO.2](../02-java/language-core/java-file-io-and-nio2.md) — the Java-level API and its own measured buffering effects.
- [Memory-Mapped Files and Zero-Copy I/O](../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md) — what changes when the page cache *is* your buffer.
- [MVCC, Vacuum, and Bloat](../06-databases/mvcc-vacuum-and-bloat.md) — a database's own use of this layer.
- [Kafka Architecture Fundamentals](../09-messaging-event-driven/kafka-architecture-fundamentals.md) — a log design built directly on sequential writes and the page cache.

## 20. Mastery Checklist

- [ ] Can explain what `write()` guarantees and what it does not.
- [ ] Can distinguish `flush()`, `fsync`, and `fdatasync` precisely.
- [ ] Can state the measured cost of per-record durability and explain group commit.
- [ ] Can explain why buffer size changes performance without changing bytes transferred.
- [ ] Can explain what the page cache does to a benchmark.
- [ ] Can state the crash-safe file-write recipe and what each step protects against.
- [ ] Can qualify the random-versus-sequential claim correctly instead of reciting it.
