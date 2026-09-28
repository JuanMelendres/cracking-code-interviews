---
title: "Flashcards: File Systems and Durable I/O"
slug: file-systems-and-durable-io
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: T-2440
canonical: ../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md
last_updated: 2026-09-28
---

# Flashcards: File Systems and Durable I/O

**Canonical chapter:** [`syllabus/01-computer-science-foundations/file-systems-and-durable-io.md`](../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md)

## Card: What a successful write() guarantees

**Prompt:**
`write()` returned successfully. Is the data on disk?

**Answer:**
No. The bytes are in the kernel's page cache, marked dirty, and a writeback thread will persist them later. Only `fsync` (Java: `FileChannel.force(true)`) waits for the device to acknowledge. `flush()` on a Java stream is a third, weaker thing — it moves bytes from *your* buffer into the kernel and makes nothing durable.

**Why it matters:**
A power loss between `write` and writeback loses data the program was told was fine — this is the mechanism behind "the D in ACID."

**Common trap:**
Treating `flush()` as durability.

**Related:**
[File Systems and Durable I/O](../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md)

## Card: What durability actually costs

**Prompt:**
How expensive is `fsync`, and what do databases do about it?

**Answer:**
Measured on 2,000 appends of 256 bytes: 3 ms with no syncing, **7,704 ms** syncing per record (~2,000x), and 91 ms syncing every 100 records (~30x). Batching is group commit: hold several transactions briefly, sync them together, acknowledge them all — a little more latency per transaction, dramatically less total sync cost, at identical durability.

**Why it matters:**
It explains `synchronous_commit`-style settings, commit-latency floors, and why "slow disk" is usually a sync-rate problem rather than a bandwidth one.

**Common trap:**
Syncing per record by default in application code that writes many small records.

**Related:**
[MVCC, Vacuum, and Bloat](../syllabus/06-databases/mvcc-vacuum-and-bloat.md)

## Card: Why buffer size changes performance

**Prompt:**
Reading the same 64 MB with a 512 B buffer versus a 64 KB buffer — why does it differ, and by how much?

**Answer:**
Measured: 48 ms with 512 B (131,072 `read()` calls) versus 5 ms with 64 KB (1,024 calls) — about 10x for identical bytes. Each `read()` crosses the user/kernel boundary, so syscall *count* is the cost, not the data volume. Returns flatten past a page or two.

**Why it matters:**
It is the concrete reason `BufferedInputStream` exists, and why "wrap it in a buffered stream" is not stylistic advice.

**Common trap:**
Assuming a bigger buffer always helps — 64 KB to 1 MB bought only 1 ms in the same run.

**Related:**
[Java File I/O and NIO.2](../syllabus/02-java/language-core/java-file-io-and-nio2.md)

## Card: Is random I/O really 100x slower?

**Prompt:**
The textbook says random reads are 10–100x slower than sequential. Is that true today?

**Answer:**
Conditionally. Measured with 20,000 4 KB reads each way on NVMe with a warm page cache, the ratio was **0.68x** — indistinguishable, random marginally faster. The large penalty is a *spinning-disk, cache-miss* phenomenon. Sequential access still matters for readahead, erase-block alignment on SSDs, and every prefetching layer — which is why B-trees, log-structured storage, and Kafka still favour it.

**Why it matters:**
Quoting the figure unconditionally is repeating a benchmark nobody re-ran on current hardware.

**Common trap:**
Concluding the opposite error — that access patterns no longer matter at all.

**Related:**
[Kafka Architecture Fundamentals](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md)

## Card: Writing a file so a crash cannot leave it half-written

**Prompt:**
What is the crash-safe recipe for writing a file, and what does each step protect against?

**Answer:**
Write to a temporary file; `force(...)` it so the *content* is durable; `rename` over the target, which is atomic within a file system so readers see the old or new file and never a partial one; then `fsync` the parent **directory**, which makes the rename itself durable. Skipping the last step is how you get a file that exists and is empty after a crash — the metadata persisted and the data did not.

**Why it matters:**
Data and metadata are separately durable; assuming otherwise produces a failure that only appears after an unclean shutdown.

**Common trap:**
Writing in place and calling `force` — a crash mid-write leaves a genuinely half-written file.

**Related:**
[File Systems and Durable I/O](../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md)
