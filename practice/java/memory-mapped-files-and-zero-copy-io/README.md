# Memory-Mapped Files and Zero-Copy I/O — Real, Executed Demo

Backs [Memory-Mapped Files and Zero-Copy I/O](../../../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md).
Real OpenJDK 21.0.12, Apple M4 (arm64, APFS), macOS.

## Setup and run

```bash
javac -d out src/MemoryMappedFileDemo.java
java -Xmx1g -cp out MemoryMappedFileDemo
```

Real captured output: [`output-transcript.txt`](output-transcript.txt).

## What it proves

A real 512MB temp file (real, varying byte content — verified by matching
checksums between both read methods, not trivial all-zero data), read two
ways: ordinary `FileChannel` reads (each a real syscall into the kernel)
versus a real `MappedByteBuffer` (the file's pages mapped directly into
this JVM process's virtual address space — a read becomes an ordinary
memory access, with the OS page cache handling the actual disk I/O
transparently, on demand, per page).

**Sequential full-file scan, real measured result — close, honestly reported:**

```
FileChannel bulk reads:  171ms
MappedByteBuffer scan:   177ms
```

No dramatic win here, and that's the honest finding: `FileChannel`'s own
bulk `read()` already amortizes syscall overhead across a large buffer
(1MB per call in this demo), so for genuinely sequential access, the two
approaches cost about the same.

**Random access, 2,000,000 reads of 8 bytes each, real measured result — dramatic:**

```
FileChannel positional reads (1 syscall each): 839ms
MappedByteBuffer random access (0 syscalls):   30ms
Random-access speedup: ~28x
```

**A real, measured ~28x speedup for random access.** The mechanism: every
`FileChannel.read(buffer, offset)` call is a real syscall — a real
kernel-mode transition, for every single 8-byte read, 2 million times.
`MappedByteBuffer.getLong(offset)` is an ordinary memory read; the kernel
only gets involved on a genuine page fault (the first touch of a not-yet-
resident page), and with a 512MB file mostly already resident in the OS
page cache from the earlier sequential scan, most of these 2 million reads
never leave user space at all.

## Correctness

Both `FileChannel` and `MappedByteBuffer` paths compute a real checksum
(sequential) and a real sum (random access) over the identical file
content, and both are verified to match exactly — the ~28x speedup isn't
coming from reading less data or different data, only from how the reads
are performed.
