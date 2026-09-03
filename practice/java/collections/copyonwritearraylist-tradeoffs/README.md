# CopyOnWriteArrayList and copy-on-write trade-offs (T-206) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md`](../../../../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md)
(T-206). Two independent demos: the real cost side (O(n) write cost, measured) and the real
benefit side (lock-free concurrent reads, measured).

Snapshot-isolation/iterator behavior is already covered with real evidence in
[`fail-fast-vs-weakly-consistent-iterators.md`](../../../../syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md)
(T-208) and is not re-demonstrated here — this chapter's own evidence focuses on the write-cost
and read-throughput trade-off specifically.

## Setup and run

```bash
cd practice/java/collections/copyonwritearraylist-tradeoffs
mkdir -p out
javac -d out src/*.java
java -cp out WriteCostScalingDemo
java -cp out ConcurrentReadThroughputDemo
```

No special flags needed.

## Real observed output (last run)

### `WriteCostScalingDemo` — real, measured O(n) write cost vs. `ArrayList`'s real O(1) amortized cost

```
== CopyOnWriteArrayList: per-write cost as list size grows ==
size	time for 100 add() calls (ms)	avg per write (microseconds)
1000	0ms	0.37
10000	0ms	1.78
100000	1ms	14.50
500000	8ms	82.33

== ArrayList (synchronized externally, single-threaded here): per-write cost as list size grows ==
size	time for 100 add() calls (ms)	avg per write (microseconds)
1000	0ms	0.07
10000	0ms	0.03
100000	0ms	0.03
500000	0ms	0.11
```

`CopyOnWriteArrayList.add()`'s real, measured average cost grows roughly proportionally with the
list's current size (0.37µs at 1,000 elements → 82.33µs at 500,000 elements, a real ~222x increase
for a ~500x size increase — consistent with real O(n) full-array-copy behavior). `ArrayList.add()`'s
real, measured average cost stays essentially flat (0.03–0.11µs) across the same size range,
consistent with its real O(1) amortized behavior. This is the direct, measured cost side of
`CopyOnWriteArrayList`'s trade-off: every single write copies the entire backing array, regardless
of how small the actual mutation is.

### `ConcurrentReadThroughputDemo` — real, measured lock-free-read benefit

```
CopyOnWriteArrayList (lock-free reads): 13ms (sink=7992000000, prevents dead-code elimination)
Collections.synchronizedList (every read takes the intrinsic lock): 577ms (sink=7992000000, prevents dead-code elimination)

== Real measured total wall-clock time, 8 threads x 2000000 reads each, ZERO writers ==
CopyOnWriteArrayList:        13ms
Collections.synchronizedList: 577ms
Real measured ratio: 44.38x
```

8 real reader threads, 2,000,000 `get()` calls each, zero writers. `CopyOnWriteArrayList`'s reads
take no lock at all — they read a plain, immutable array reference — and the real measured total
time (13ms) reflects that. `Collections.synchronizedList()` serializes every single read through
one shared intrinsic lock, even with zero writers ever contending for it, and measured a real
42–44x slower total time across repeated runs. This is the direct, measured benefit side of
`CopyOnWriteArrayList`'s trade-off: for a read-heavy, write-rare workload, the O(n) write cost
measured above is paid rarely, while every read benefits from genuinely lock-free access.
