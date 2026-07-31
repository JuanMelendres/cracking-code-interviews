---
title: "Week 16 Mock — JVM Internals Technical Round (45 min)"
week: 16
document_type: study-pack-mock
status: draft
last_reviewed: 2026-07-31
---

# Week 16 Mock — JVM Internals Technical Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** GC pause times have grown noticeably but heap occupancy hasn't moved. Walk through your diagnostic process.
2. **(6 min)** A service's memory grows steadily over days and eventually OOMs. What do you check, in order?
3. **(6 min)** A process throws `StackOverflowError` on one endpoint, but heap and overall memory look completely normal. What's going on?
4. **(6 min)** You doubled a container's memory limit, but heap-related metrics barely moved. Explain why.
5. **(6 min)** A brief, unexplained latency spike correlates with a feature-flag rollout, no GC event, no deploy. Diagnose it.
6. **(6 min, whiteboard)** Sketch how G1's remembered sets and write barriers let it collect one region without scanning the whole heap.
7. **(9 min)** Free-form: pick any two of this week's five topics and explain how they interact in a single real production system (e.g., a metaspace leak from unbounded dynamic proxy generation interacting with container memory limits).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Pause time up, heap occupancy flat

**Ideal answer outline:** suspects write-barrier/RSet pressure from a hot, frequently-mutated cross-region structure; confirms via `-Xlog:gc+phases=debug`, checking `Merge Heap Roots` duration and `Dirty Cards`/`Scanned Cards` sums.
**Common weak answers:** jumping straight to "increase the heap" or "tune the pause-time goal" without diagnosing the cause first.
**Pass signal:** correctly names the write-pattern-driven mechanism and the specific log evidence.
**Borderline signal:** suspects "something about GC internals" without naming RSets/write barriers specifically.
**Fail signal:** proposes only sizing changes with no diagnostic step.

### Question 2 — Steady memory growth to OOM

**Ideal answer outline:** rules out a warming cache with spaced samples; uses `jmap -histo:live` to find the growing class; confirms with a targeted heap dump's GC-roots view; fixes by breaking the specific reference.
**Common weak answers:** proposing more heap as the fix.
**Pass signal:** correctly sequences histogram-then-dump and explains why heap alone doesn't fix it.
**Borderline signal:** names "memory leak" correctly but can't describe the diagnostic sequence.
**Fail signal:** "add more memory" as the complete answer.

### Question 3 — StackOverflowError, heap looks fine

**Ideal answer outline:** stack capacity is per-thread (`-Xss`) and independent of heap (`-Xmx`); checks whether the recursion depth is legitimately data-dependent and deep, then bounds the recursion or raises `-Xss` for the affected thread pool.
**Common weak answers:** proposing to increase heap size.
**Pass signal:** correctly identifies stack capacity as the issue and names the right flag.
**Borderline signal:** knows it's "not a heap thing" but can't name `-Xss` or explain the mechanism.
**Fail signal:** proposes a heap-size change.

### Question 4 — Doubled container memory, heap barely moved

**Ideal answer outline:** without an explicit `-Xmx`, the heap cap is `MaxRAMPercentage` (default 25%) of the container's memory limit, so it should scale proportionally — check for an explicit `-Xmx` overriding the default, or a different percentage flag.
**Common weak answers:** assuming the heap cap always tracks the container limit 1:1.
**Pass signal:** names the percentage-based default and the possibility of an overriding explicit `-Xmx`.
**Borderline signal:** knows something's tunable but can't name `MaxRAMPercentage`.
**Fail signal:** no explanation beyond "that's weird."

### Question 5 — Latency spike correlated with a flag rollout

**Ideal answer outline:** suspects deoptimization — a previously-monomorphic hot call site getting a second implementation, forcing a fallback and recompilation; confirms via `-XX:+PrintCompilation`'s "made not entrant" events correlated with the spike.
**Common weak answers:** assuming it must be GC-related without checking.
**Pass signal:** correctly names deoptimization and the confirming diagnostic.
**Borderline signal:** suspects "something about the JIT" without naming deoptimization specifically.
**Fail signal:** defaults to "probably GC" with no distinguishing evidence.

### Question 6 — Whiteboard: G1 remembered sets

**Ideal answer outline:** draws regions, a write barrier dirtying a card on a cross-region reference store, and the card being merged into the target region's RSet at the next pause — explicitly connects this to why one region can be collected without scanning the whole heap.
**Pass signal:** correctly draws and narrates both mechanisms and their relationship.
**Borderline signal:** draws regions correctly but can't explain the write-barrier/card-table mechanism.
**Fail signal:** can't connect the diagram to "why is this safe."

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., a metaspace leak from unbounded proxy generation combined with a container's memory limit masking the real cause because heap metrics look fine) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Weeks 13–15's mocks — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
