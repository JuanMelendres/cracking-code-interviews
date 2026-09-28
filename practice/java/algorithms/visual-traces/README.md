# Visual Traces for the Algorithms Chapters — Real Executed Output

Backs the step-by-step visual explanations added to nine chapters in
[`syllabus/03-data-structures-algorithms/`](../../../../syllabus/03-data-structures-algorithms/).

Every table printed in those chapters' diagrams is real executed output from this
one program, not a hand-drawn illustration. If a chapter shows a pointer walk, a DP
grid, or a stack evolution, the numbers came from here and can be re-verified by
re-running it.

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/VisualTraces.java
java -cp out VisualTraces
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it traces

| Chapter | Problem | What the trace shows |
|---|---|---|
| `arrays-two-pointers-and-sliding-window.md` | LC 11 Container With Most Water | Every pointer position, width, area, and which side moved and why — answer 49 in 8 steps |
| `binary-search-and-search-on-answer.md` | LC 1011 Ship Packages | The search space `[10, 55]` collapsing over 5 probes, with `daysNeeded(mid)` and the feasibility verdict at each — answer 15 |
| `bit-manipulation.md` | LC 136 Single Number | The running XOR in 4-bit lanes, showing each duplicate cancelling itself bit by bit — answer 4 |
| `dynamic-programming.md` | LC 72 Edit Distance | The full 6x4 DP table for `"horse" -> "ros"`, every cell computed — answer 3 |
| `greedy-and-the-exchange-argument.md` | LC 45 Jump Game II | `farthest` versus `currentEnd` per index, with the exact moments a jump is committed — answer 2 |
| `hashing-patterns-and-frequency-maps.md` | LC 560 Subarray Sum Equals K | The prefix-sum map after every element, plus what each step looked up and found — answer 6 |
| `intervals-merging-and-sweep-line.md` | LC 253 Meeting Rooms II | Sorted start/end events with the running and peak active-room count — answer 2 |
| `sorting-algorithms.md` | Merge sort | The real split and merge order, indented by recursion depth |
| `stacks-and-monotonic-stack.md` | LC 84 Largest Rectangle | Stack contents after every index, with each pop's `height x width = area` — answer 10 |

The tenth chapter given a diagram in the same change,
`coding-interview-pattern-recognition-methodology.md`, gets a Mermaid decision tree
rather than a trace — it teaches a routing decision, not an algorithm, so there is
nothing to execute.
