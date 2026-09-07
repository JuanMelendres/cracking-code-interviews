---
title: "Cheat Sheet: Algorithmic Complexity and Big-O, From First Principles"
slug: algorithmic-complexity-and-big-o-from-first-principles
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2001
canonical: ../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
last_updated: 2026-09-06
---

# Algorithmic Complexity and Big-O, From First Principles

**Canonical chapter:** [`syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md`](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Core Mental Model

Big-O names the growth *shape* of an operation's cost as input size grows, not its speed in milliseconds — it deliberately discards the constant factor (specific CPU, cache behavior) to describe only how cost changes as `n` doubles.

## Essential Definitions

- **Upper bound, dropping constants and lower-order terms** — `3n² + 50n + 200` is still `O(n²)`; the `50n` and `200` vanish as `n` grows, and the `3` changes steepness, not shape.
- **Average case vs. worst case** — different, both true statements about the same method. `HashMap.get()` is `O(1)` average, `O(n)` worst case (many keys colliding into one bucket) or `O(log n)` once a bucket treeifies.
- **Amortized complexity** — a third category: `ArrayList.add()` is "amortized O(1)" because most calls are O(1) but an occasional resize-and-copy call is O(n); averaged across all calls between resizes, the average is O(1) even though no single call is guaranteed cheap.
- **Recurrence relation** — how recursive complexity is derived, not loop counting. Merge sort: `T(n) = 2·T(n/2) + O(n)` → `O(n log n)`.
- **Space complexity** — same logic applied to memory; a hash set trades `O(n)` extra memory for `O(1)` average lookup, versus a sorted array's `O(log n)` lookup with no extra memory.

## Decision Table

| Complexity | Name | Typical code shape |
|---|---|---|
| `O(1)` | Constant | Array index access, `HashMap` lookup on average, arithmetic |
| `O(log n)` | Logarithmic | Binary search, balanced-tree operations (`TreeMap`/`TreeSet`) |
| `O(n)` | Linear | A single loop over the input once |
| `O(n log n)` | Linearithmic | Comparison-based sorting (`Arrays.sort`, merge sort) |
| `O(n²)` | Quadratic | A loop nested inside a loop, both bounded by `n` |
| `O(2^n)` | Exponential | Naive (non-memoized) recursion exploring every subset |

## Reading Complexity Out of Code (heuristic, not proof)

1. Check what's *inside* the loop, not just how many loops there are — a single loop calling a method with hidden `O(n)` cost is `O(n²)` overall.
2. Check whether the inner bound actually scales with `n`, or is a fixed constant — a loop over `n` containing a loop over a fixed `10` is `O(10n)` = `O(n)`, not `O(n²)`.
3. Know the complexity of library methods called inside a loop — `ArrayList.contains(x)` is `O(n)`; calling it inside a loop over the same list silently reintroduces `O(n²)`.

## Common Pitfalls

- Treating `String` concatenation in a loop (`result += s`) as `O(n)` when it's `O(n²)` — each `+=` allocates a new `String` and copies everything accumulated so far; `StringBuilder.append()` restores `O(n)`.
- Assuming a nested loop is automatically `O(n²)` without checking whether the inner bound scales with `n` — or the reverse, assuming a single visible loop can't be quadratic when it calls something with hidden `O(n)` cost.
- Collapsing average case and worst case into one number — saying "`HashMap` is `O(1)`" without "average."
- Assuming a better Big-O class is always faster — for small `n`, an `O(n²)` algorithm with a smaller constant factor can beat `O(n log n)`; `Arrays.sort()` for primitives switches to insertion sort for small subarrays for exactly this reason.

## Interview Answer Skeleton

**30-sec:** Big-O describes how an operation's cost grows as input size grows, ignoring the constant factor — `O(1)` doesn't scale with input, `O(log n)` grows by one step per doubling, `O(n)` grows proportionally, `O(n²)` quadruples when input doubles.

**2-min:** Add the mechanical loop-counting heuristic (count loop/recursion depth tied to `n`), the average-vs-worst-case distinction using `HashMap.get()`, and one concrete recurrence derivation: merge sort's `T(n) = 2·T(n/2) + O(n)` giving `O(n log n)`.

**Whiteboard:** Draw the four analogies in order of cost — dictionary-by-page-number (`O(1)`), phone-book-halving (`O(log n)`), read-every-page (`O(n)`), everyone-shakes-everyone's-hand (`O(n²)`) — then say "Big-O names which of these shapes an operation follows, not how fast the specific machine runs it."

**Staff-level framing:** An algorithm's complexity class is a scaling assumption baked into code, often implicitly, and scaling assumptions that were true when the code was written silently stop being true as a system grows — usually invisibly, until they aren't. The right team-wide response is a standard code-review question ("what happens to this path at 100× today's data volume"), not relying on one senior engineer to catch every hidden quadratic pattern by inspection.

## Production Warning Signs

- Offset pagination degrading an admin tool as a table grows: `OFFSET n` pagination costs `O(n)` per page requested at any page depth — invisible at launch (small table, shallow pages), a real incident once the table grew and users paged deep.
- HashMap bucket overload from a poor `hashCode()` distribution: the gap between average-case `O(1)` and worst-case `O(n)` stopped being theoretical once real key data violated the "reasonably distributed `hashCode()`" assumption.
- Symptom: a report that ran in under 2 seconds now takes 3+ minutes after data grew ~50× with no code change. Diagnose by checking whether the growth ratio matches `O(n)` (~50×, i.e. ~100s) or `O(n²)` (~2,500×, i.e. ~83min) — profile for a nested-loop-over-the-same-collection pattern or reproduce against synthetic data sizes to see which curve the growth actually follows.

## Real Measured Numbers

OpenJDK 21.0.12, min of 5 timed runs after 3 warmups:

- `O(1)` and `O(log n)` are indistinguishable from measurement noise across five orders of magnitude of `n` (roughly 0.0003–0.03 ms throughout).
- `O(n)`: a 10,000× growth in `n` (1,000 → 10,000,000) produced a ~464× growth in time (0.0049 ms → 2.2723 ms) — same shape as the model, not exactly proportional at every point due to cache locality and JIT warmup.
- `O(n²)`: time increases almost exactly 4× every time `n` doubles (1,000 → 2,000 → 4,000 → 8,000 → 16,000 elements) — the cleanest result in the whole measurement set, exactly matching `(2n)² = 4n²`.

## Related

- syllabus/02-java/collections/hashmap-internals.md
- syllabus/02-java/collections/arraylist-and-linkedlist-internals.md
- syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md
- syllabus/16-performance-jvm/capacity-planning-and-headroom.md
