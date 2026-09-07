---
title: "Flashcards: Algorithmic Complexity and Big-O, From First Principles"
slug: algorithmic-complexity-and-big-o-from-first-principles
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2001"
canonical: ../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
last_updated: 2026-09-07
---

# Flashcards: Algorithmic Complexity and Big-O, From First Principles

**Canonical chapter:** [`syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md`](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: Big-O names the growth shape, not the speed

**Prompt:**
If an operation genuinely takes `3n² + 50n + 200` steps, what is its Big-O, and why do the `50n` and `200` terms not matter?

**Answer:**
`O(n²)`. Big-O describes an upper bound on cost as `n` grows toward infinity, after dropping constant multipliers and lower-order terms — the `50n` and `200` become irrelevant once `n` is large enough, and the leading `3` changes how steep the curve is but not which shape it is.

**Why it matters:**
This is precisely why "Big-O" and "how many milliseconds will this take" are different questions — Big-O deliberately ignores the constant factor that a real CPU's cache and JIT compiler actually care about.

**Common trap:**
Treating Big-O as a literal speed ranking at every input size, rather than an asymptotic-growth-rate statement.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: Average case vs. worst case for `HashMap.get()`

**Prompt:**
Is it accurate to say "`HashMap` is `O(1)`" without qualification?

**Answer:**
No — `HashMap.get()` is `O(1)` on average, assuming a reasonably distributed `hashCode()`. Its worst case, when many keys collide into the same bucket, is `O(n)` (or `O(log n)` once a bucket treeifies). Both statements are true about the same method; which one matters depends on the question being asked.

**Why it matters:**
Collapsing average and worst case into one number is a named common mistake, and the gap between them is exactly what turns into a real incident when a poor `hashCode()` distribution violates the "reasonably distributed" assumption in production.

**Common trap:**
Saying "`HashMap` is `O(1)`" flatly, with no "average" qualifier.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: Amortized `O(1)` for `ArrayList.add()`

**Prompt:**
`ArrayList.add()` is described as "amortized O(1)." What does that mean, given that some individual calls trigger an `O(n)` resize-and-copy?

**Answer:**
Most calls are genuinely `O(1)` (there's spare capacity); occasionally a call triggers a full resize-and-copy, which is `O(n)` for that one call. Amortized analysis averages the resize cost across all the cheap calls between resizes, so the *average* cost per call is still `O(1)` — even though no individual call is guaranteed to be cheap.

**Why it matters:**
A latency-sensitive system (e.g., a p99.9 SLA) needs to account for the possibility that any single call could be the expensive one, not just the amortized average.

**Common trap:**
Assuming "amortized O(1)" means every individual call is cheap, rather than that the cost only averages out over many calls.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: String concatenation in a loop is `O(n²)`, not `O(n)`

**Prompt:**
Why is `result += s` inside a loop over `n` strings `O(n²)` rather than `O(n)`?

**Answer:**
Each `+=` allocates an entirely new `String` and copies every character accumulated so far into it. Summed across `n` iterations, that's `1 + 2 + 3 + ... + n` characters copied — `O(n²)`. `StringBuilder.append()` avoids this by growing an internal mutable buffer, restoring the `O(n)` the code probably intended.

**Why it matters:**
It's the canonical example of a single, visible loop that looks like `O(n)` per-iteration work but is secretly quadratic overall.

**Common trap:**
Assuming a single visible loop can't be quadratic because there's only one `for` keyword in the code.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: Real measured `O(n²)` scaling — exactly 4x per doubling

**Prompt:**
In the chapter's real timing measurement, an `O(n²)` all-pairs operation's time roughly quadrupled every time `n` doubled (1,000 → 2,000 → 4,000 → 8,000 → 16,000). Why is that exactly the predicted result?

**Answer:**
Because `(2n)² = 4n²` — doubling the input and squaring it produces exactly 4x the cost. It was also the cleanest result in the whole measurement table, precisely because that same explosive growth is what makes quadratic complexity operationally unworkable past a few tens of thousands of elements.

**Why it matters:**
It's real, falsifiable evidence — not just a formula — for what "quadratic" concretely costs as data grows, and why an algorithm's complexity class is a scaling assumption that can silently stop holding.

**Common trap:**
Treating Big-O classes as abstract labels rather than expecting (and being able to verify) a specific, predictable growth ratio.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)

## Card: A "better" Big-O class isn't always faster in practice

**Prompt:**
Is `O(n log n)` always faster than `O(n²)` in real, measured wall-clock time?

**Answer:**
No — only asymptotically, as `n → ∞`. For small enough `n`, an `O(n²)` algorithm with a smaller constant factor can be faster. This is real, not hypothetical: `Arrays.sort()` for primitive arrays switches to insertion sort — `O(n²)` — for small subarrays, precisely because it wins at that size.

**Why it matters:**
Choosing an algorithm purely by asymptotic class without checking the realistic size of `n` for the actual system is a decision made on incomplete information.

**Common trap:**
Treating Big-O as a total, context-free speed ordering ("O(n log n) is just better") with no regard to constant factors or realistic input size.

**Related:**
[syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md](../syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md)
