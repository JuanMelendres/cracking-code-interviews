# Vector Clocks and Quorum-Based Replication — Real Demo

Backs [`syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md`](../../../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md) (T-2407).

Pure JDK, no dependencies. No live network or database — a real, hand-rolled
vector clock implementation and a real, exhaustive combinatorial proof of
Dynamo's quorum-overlap rule, not a simplification of either.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Two independent, concurrent writes produce vector clocks that are
  genuinely incomparable — a real, programmatically detected conflict.**
  Replica A writing twice (`{A=2}`) and replica B writing once without
  having seen A's second write (`{A=1, B=1}`) compare as `CONCURRENT`:
  neither clock's counters dominate the other's on every node. This is
  the real mechanism behind Dynamo's "the application must resolve this,
  we can't silently pick a winner" conflict signal — not a heuristic or
  a timestamp guess.
- **A causal write — one replica reading another's value before writing
  — produces a vector clock that genuinely dominates, with zero
  ambiguity.** Replica B reading A's `{A=2}` state and then writing
  produces `{A=2, B=1}`, which compares as `DOMINATES` against `{A=2}` —
  correctly and automatically recognized as a real supersession, not a
  conflict, with no application-level merge needed.
- **Dynamo's `W + R > N` quorum rule is exhaustively true, not just
  usually true.** For `N=5`, every one of the 100 possible
  (write-quorum, read-quorum) pairs overlaps when `W=3, R=3` (`W+R=6>N`)
  — a real, complete enumeration, not a sample. For `W=3, R=2`
  (`W+R=5=N`, the boundary case a common misconception treats as safe),
  a real counterexample is found and printed: write quorum `{3,4,5}` and
  read quorum `{1,2}` share no replica at all, a genuine stale read.
  The same holds, more obviously, for `W=2, R=2` and `W=1, R=1`.
