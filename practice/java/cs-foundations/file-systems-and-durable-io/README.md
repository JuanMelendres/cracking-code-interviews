# File Systems and Durable I/O — Real Measurements

Backs [`syllabus/01-computer-science-foundations/file-systems-and-durable-io.md`](../../../../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md) (T-2440).

Pure JDK, no dependencies. Numbers are machine-specific (macOS, APFS on NVMe, 10 cores); the **orders of magnitude** are the lesson, and every number is reproducible by re-running the file.

## Run it

```bash
mkdir -p out
javac -d out src/FileSystemIoDemo.java
java -cp out FileSystemIoDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it measures

**Durability is the headline.** 2,000 appends of 256 bytes:

| Strategy | Time |
|---|---|
| No `fsync` at all | 3 ms |
| `fsync` every record | **7,704 ms** |
| `fsync` every 100 records | 91 ms |

Durability per record cost roughly **2,000x** the un-synced write, and batching amortised it back down to ~30x — which is precisely what a database's group commit does, trading a little latency for a large throughput gain at the *same* durability guarantee.

**Buffer size, same bytes read (64 MB):**

| Buffer | Time | `read()` calls |
|---|---|---|
| 512 B | 48 ms | 131,072 |
| 4 KB | 9 ms | 16,384 |
| 64 KB | 5 ms | 1,024 |
| 1 MB | 4 ms | 64 |

Identical bytes; the difference is syscall count crossing the user/kernel boundary. This is what a `BufferedInputStream` is for.

**Page cache**, same 32 MB file read three times: 3.3 ms → 2.6 ms → 2.4 ms. "Disk is slow" is really "a cache *miss* is slow," which is why a benchmark that ignores cache state measures nothing.

## An honest negative result

The sequential-versus-random comparison **did not reproduce** the textbook penalty: 20,000 4 KB reads each way came out at a ratio of **0.68x** (random marginally *faster*, i.e. indistinguishable within noise). The file was written moments earlier and is resident in the page cache, and the backing store is NVMe, so neither pattern pays a seek.

The classic 10–100x random-read penalty is a **spinning-disk, cache-miss** phenomenon. Dropping the page cache requires elevated privileges, so this demo deliberately does not assert a number it cannot measure rather than reprinting folklore. What does still hold everywhere: sequential access is friendlier to readahead, to erase-block-sized writes, and to any layer that prefetches.
