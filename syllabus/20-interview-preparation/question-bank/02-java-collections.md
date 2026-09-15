---
title: "Interview Question Bank — 02-java/collections"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../02-java/collections/INDEX.md
  - 06-databases.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Java Collections

Part of the `02-java` compendium (per `00-project/interview-question-bank-plan.md`),
split by the domain's own real subdirectory structure (`collections/`,
`concurrency/`, `jvm-internals/`, `language-core/`) since `02-java` alone yields far
more real questions (234) than one file can hold at the same depth as the
`06-databases` pilot. See that file for the tier-explanation format and sourcing
discipline.

**Honest count for this subdomain:** 11 chapters yielded 20 deep questions + 5
already-leveled Junior/Mid questions (from the one Junior Fundamentals chapter) + 27
quick-fire questions = **52 real questions**.

---

## Java Collections Usage Fundamentals — List, Map, and Set

The domain's Junior Fundamentals chapter for collections — its own Interview
Questions section already tags each by seniority.

### Q1 — What's the difference between a List and a Set?

**Canonical treatment:** [§15](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md#15-interview-questions)

**What's expected:**
- **Junior:** A `List` is ordered and allows duplicates; a `Set` has no guaranteed order (for `HashSet`) and never allows duplicates. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form at these tiers.

### Q2 — When would you use a Map instead of a List?

**Canonical treatment:** [§15](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md#15-interview-questions)

**What's expected:**
- **Junior:** Names Map as "for key-value pairs," without a usage rationale.
- **Junior/Mid:** When you need to look something up by a key/name rather than a position, or when you're counting/grouping — ideally with a concrete example like a frequency count. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q3 — Why is checking membership with a HashSet faster than checking a List?

**Canonical treatment:** [§15](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md#15-interview-questions)

**What's expected:**
- **Junior:** "It's faster," without complexity classes.
- **Mid:** `HashSet.contains()` is ~O(1) on average; scanning a `List` for a value is O(n), naming the actual complexity classes. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q4 — What does `map.get(key)` return if the key isn't present, and what's the danger?

**Canonical treatment:** [§15](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md#15-interview-questions)

**What's expected:**
- **Junior:** Knows it returns something falsy/empty, without naming `null` precisely.
- **Mid:** `null` — the danger is a later `NullPointerException` if the caller assumes a non-null result; `getOrDefault` or a `containsKey` check avoids it. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q5 — A service scans a List with a linear `contains()` check on every request. Diagnosis and fix?

**Canonical treatment:** [§15](../../02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes something is slow without naming the fix.
- **Mid/Senior:** This degrades quietly as the list grows, invisible at small scale; the fix is switching to a `HashSet`/`HashMap` for O(1) average lookup. Target tier.
- **Staff:** Generalizes to defaulting to the correct collection type based on actual access pattern from the start, not after a production slowdown is noticed.

---

## ArrayList and LinkedList Internals

### Q1 — Your team switched a list to `LinkedList` for flexibility and a hot path got slower. Why?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/arraylist-and-linkedlist-internals.md#interview-questions)

**What's expected:**
- **Junior:** Recognizes the list type change as a plausible suspect.
- **Mid:** Suspects the list-type change as the cause, even without the precise complexity reasoning.
- **Senior:** If the hot path performs indexed reads, switching to `LinkedList` regresses that specific operation to O(n) from `ArrayList`'s O(1) — correctly states the complexity classes and connects the regression to the actual access pattern.
- **Staff:** Generalizes to the principle that a Big-O claim ("LinkedList has O(1) insertion") is scoped to specific conditions, and applying it past that scope produces exactly this kind of mistaken optimization.

### Q2 — Is `LinkedList.addFirst()` really O(1) regardless of size? What about `add(k, x)`?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/arraylist-and-linkedlist-internals.md#interview-questions)

**What's expected:**
- **Junior:** States `addFirst()` is fast, without the arbitrary-index distinction.
- **Mid:** States that `addFirst()`/`addLast()` are O(1), even without addressing the arbitrary-index case.
- **Senior:** Correctly distinguishes head/tail operations (genuinely O(1)) from arbitrary-index operations (O(n) overall, due to the traversal to find the position).
- **Staff:** Notes that this makes `LinkedList` and `ArrayList` closer in practice for arbitrary-index insertion than the naive claim suggests — `ArrayList`'s better cache locality can even make it competitive despite its O(n) shift.

---

## BlockingQueue Family and Producer-Consumer

### Q1 — Your ingestion service crashed with `OutOfMemoryError` during a downstream slowdown. First suspect?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/blockingqueue-family.md#interview-questions)

**What's expected:**
- **Junior:** Suspects "too much data," without naming a queue mechanism.
- **Mid:** Suspects a queue or buffer growing without bound, even without naming the specific mechanism.
- **Senior:** An unbounded internal queue absorbing a growing backlog as ingestion outpaced processing, with no backpressure to slow ingestion down instead.
- **Staff:** Generalizes to the anti-pattern of absorbing overload internally instead of surfacing backpressure, naming other instances (thread pool queues, caches, retry loops) of the same principle.

### Q2 — What's the actual difference between `SynchronousQueue` and a capacity-1 `ArrayBlockingQueue`?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/blockingqueue-family.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `SynchronousQueue` has no capacity, even without contrasting it precisely against capacity-1.
- **Senior:** `SynchronousQueue` has zero internal storage — a `put()` only succeeds once a `take()` is already waiting; a capacity-1 queue genuinely stores one element and can return from `put()` before any `take()`.
- **Staff:** Connects this to a real use case: `Executors.newCachedThreadPool()` uses a `SynchronousQueue` internally so a submitted task is either handed directly to an idle thread or triggers a new thread — never queued.

---

## Collection Selection Decision Matrix

### Q1 — Choose a collection for a service ingesting webhook events at a bursty rate, processed at a steady rate.

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/collection-selection-decision-matrix.md#interview-questions)

**What's expected:**
- **Junior:** Proposes "a queue," without bounded/timed refinements.
- **Mid:** Proposes some form of queue, even without the bounded-vs-unbounded and blocking-vs-timed distinctions.
- **Senior:** A bounded `BlockingQueue` sized to absorb a reasonable burst, and explains why bounding it matters (real backpressure).
- **Staff:** Adds the timed-`offer()` refinement at the ingestion boundary specifically, so an overwhelmed queue produces an explicit rejection rather than blocking a request thread indefinitely.

### Q2 — Your team always uses `ArrayList`/`HashMap` by default. When does that hurt?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/collection-selection-decision-matrix.md#interview-questions)

**What's expected:**
- **Junior:** Defends the defaults as "usually fine."
- **Mid:** Names at least one concrete scenario where the default is wrong.
- **Senior:** Names both scenarios — `ArrayList` hurts for frequent head/tail insertion (measured ~117x slower than `LinkedList`); `HashMap` hurts (correctness, not just performance) the moment it's accessed from more than one thread.
- **Staff:** Frames this as a general principle: any default is a bet on the typical access pattern being representative, and the bet fails exactly when a specific piece of code's actual pattern diverges from that typical case.

---

## ConcurrentHashMap Internals

### Q1 — Your metrics dashboard undercounts under peak load but matches at low load. Why?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/concurrenthashmap-internals.md#interview-questions)

**What's expected:**
- **Junior:** Suspects "something with threads," without the mechanism.
- **Mid:** Suspects a race condition in the counting logic, even without the precise mechanism.
- **Senior:** A `get()`-then-`put()` counter-increment pattern loses updates under concurrent access — more concurrent threads means more opportunities for two reads to observe the same stale value before either write commits. Proposes `merge()`/`compute()` as the fix.
- **Staff:** Explains why the bug is load-dependent specifically, and proposes a code-review rule flagging any `get()`-then-`put()` sequence on a shared map.

### Q2 — How does ConcurrentHashMap achieve thread safety without one lock for the whole map?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/concurrenthashmap-internals.md#interview-questions)

**What's expected:**
- **Junior:** Knows it's "thread-safe," without a mechanism.
- **Mid:** States that ConcurrentHashMap uses some form of finer-grained locking than a single whole-map lock.
- **Senior:** Fine-grained internal locking — since JDK 8, per-bucket synchronization combined with CAS operations for lock-free fast paths, rather than one lock guarding the entire map.
- **Staff:** Connects this to why ConcurrentHashMap scales better than a `Collections.synchronizedMap(new HashMap<>())` wrapper, which does use a single lock for every operation.

---

## CopyOnWriteArrayList and Copy-on-Write Trade-offs

### Q1 — What's the actual cost of a single write on a `CopyOnWriteArrayList` with a million elements?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#interview-questions)

**What's expected:**
- **Junior:** Knows writes are "expensive," without a mechanism.
- **Mid:** States that a write copies the whole array, even without a precise complexity or measured number.
- **Senior:** A real, full copy of the entire backing array, an O(n) operation proportional to the list's current size regardless of the change size — measured directly (~0.37µs at 1,000 elements to ~82µs at 500,000).
- **Staff:** Frames this as a general read/write asymmetry design pattern applicable beyond this one collection.

### Q2 — When would swapping `CopyOnWriteArrayList` for `synchronizedList` be a mistake?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `synchronizedList` is slower for reads, even without the precise mechanism.
- **Senior:** On a read-heavy, write-rare collection, `synchronizedList` forces every read to acquire a shared lock — a real cost `CopyOnWriteArrayList`'s lock-free reads never pay, even with zero writers.
- **Staff:** Generalizes to the principle that a trade-off's original justification can silently stop holding as a system evolves, requiring periodic re-validation.

---

## Fail-Fast vs. Weakly Consistent Iterators

### Q1 — Explain what actually causes `ConcurrentModificationException`.

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/fail-fast-vs-weakly-consistent-iterators.md#interview-questions)

**What's expected:**
- **Junior:** Knows modifying during iteration "isn't allowed."
- **Mid:** States that modifying a collection during iteration (other than via the iterator) throws the exception, even without the `modCount` mechanism.
- **Senior:** Each fail-fast collection maintains a `modCount`, incremented on structural modification; each iterator captures `expectedModCount` at creation and compares it on every `next()` call, throwing on mismatch. Knows `Iterator.remove()` avoids it.
- **Staff:** Explains the best-effort limitation with a concrete counterexample (the second-to-last-element quirk) and generalizes to "no exception ≠ proof of safety."

### Q2 — If `ConcurrentModificationException` wasn't thrown, was your loop safe?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/fail-fast-vs-weakly-consistent-iterators.md#interview-questions)

**What's expected:**
- **Junior:** Answers "yes" — the common misconception this question targets.
- **Mid:** States that fail-fast is "not guaranteed," even without a concrete counterexample.
- **Senior:** Produces or describes a concrete case (removing the second-to-last element of a list) where no exception is thrown despite a genuine structural modification during iteration.
- **Staff:** Generalizes to the principle that best-effort detection is weak evidence of correctness, and names the actual fix (synchronization or a structurally safe collection).

---

## HashMap Internals

### Q1 — Your HashMap-based cache's lookup latency is climbing even though entry count is stable. What do you check?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/hashmap-internals.md#interview-questions)

**What's expected:**
- **Junior:** Suspects "something is slow," without a specific check.
- **Mid:** Suspects the `hashCode()` implementation, even without a specific inspection method.
- **Senior:** Check the key type's `hashCode()` for distribution quality against actual production values, and inspect whether specific buckets are overloaded (treeified) — a stable entry count with climbing latency points at a distribution problem, not a capacity problem.
- **Staff:** Proposes reflectively (or via profiling) inspecting bucket contents for `TreeNode` occurrences as a direct diagnostic signal, plus a distribution test against production-representative data as prevention.

### Q2 — Why does treeification also require table capacity ≥ 64, not just bucket size ≥ 8?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/hashmap-internals.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes knowing treeification exists at all.
- **Mid:** States both numbers (8 and 64) correctly, even without the design reasoning.
- **Senior:** A single overloaded bucket in a small table is more likely a sizing problem than a genuine hash-collision problem; resizing the table first is usually the more effective fix.
- **Staff:** Connects this to the general principle of diagnosing sizing problems separately from distribution problems, applied as a diagnostic framework for any hash-based structure.

---

## PriorityQueue Internals

### Q1 — Does iterating a `PriorityQueue` give you elements in sorted order?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/priorityqueue-internals.md#interview-questions)

**What's expected:**
- **Junior:** Answers "yes" — the common misconception this question targets.
- **Mid:** States correctly that iteration is not sorted, even without explaining the heap-invariant mechanism.
- **Senior:** Only `poll()`/`peek()` are guaranteed ordered; iteration walks the backing array's storage order, which only guarantees each parent precedes its children, not sibling order or full sorted order.
- **Staff:** Generalizes to the broader pattern of a data structure exposing both a correct, guarantee-carrying access method and an incidental general-purpose one, treating verifying which one calling code uses as a standing review habit.

### Q2 — Is `PriorityQueue` thread-safe? What would you use instead for a multi-threaded producer/consumer queue ordered by priority?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/priorityqueue-internals.md#interview-questions)

**What's expected:**
- **Junior:** Assumes "queue" implies thread-safety — the common mistake this question targets.
- **Mid:** Recognizes it might not be thread-safe, without naming the replacement.
- **Senior:** No — unsynchronized reads/writes against its backing array and size field, exactly like `ArrayList`/`HashMap`. `PriorityBlockingQueue` is the correct replacement.
- **Staff:** Proactively flags that wrapping in `Collections.synchronizedCollection()` does not provide compound-operation atomicity for a check-then-act sequence like `peek()`-then-`poll()`.

---

## TreeMap, TreeSet, and the Navigable Hierarchy

### Q1 — Draw the Set collection hierarchy, including `NavigableSet`. Where does it fit?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/collections/treemap-treeset-and-navigable-hierarchy.md#interview-questions)

**What's expected:**
- **Junior:** Names `Set`, `TreeSet`, `HashSet` without the interface chain.
- **Mid:** Draws a roughly correct hierarchy, even if `NavigableSet` is misplaced.
- **Senior:** `Collection → Set → SortedSet → NavigableSet` is a chain of interfaces; `TreeSet` implements the whole chain. `HashSet`/`LinkedHashSet` implement `Set` directly, without sorted/navigable capability.
- **Staff:** Proposes verifying any such claim via reflection (`SomeClass.class.getInterfaces()`) rather than trusting a remembered diagram.

### Q2 — Does `TreeMap` guarantee O(log n) performance in the worst case, or only on average?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/collections/treemap-treeset-and-navigable-hierarchy.md#interview-questions)

**What's expected:**
- **Junior:** Guesses "average case, like HashMap" — the common mistake this question targets.
- **Mid:** States it's worst-case, without the rebalancing mechanism.
- **Senior:** Worst case, genuinely — the Red-Black tree's rebalancing (rotations and recoloring on insertion) prevents degeneration, unlike a naive BST fed sorted input.
- **Staff:** Connects the real, measured guarantee to when it actually matters — workloads with adversarial or already-sorted input, where a naive BST would be a real, measurable liability.

---

## Quick-fire questions (from this subdomain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | On OpenJDK 21, is `ArrayDeque`'s capacity always a power of two? | [ArrayDeque Internals](../../02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#flashcards) |
| 2 | Why is `java.util.Stack` slower than `ArrayDeque` for single-threaded stack usage? | [ArrayDeque Internals](../../02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#flashcards) |
| 3 | Can you store `null` in an `ArrayDeque`? | [ArrayDeque Internals](../../02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#flashcards) |
| 4 | By what factor does `ArrayList` grow its backing array when full? | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md#flashcards) |
| 5 | What is the time complexity of `get(index)` for `ArrayList` vs. `LinkedList`? | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md#flashcards) |
| 6 | Is `LinkedList`'s O(1) insertion guarantee true for any index? | [ArrayList and LinkedList Internals](../../02-java/collections/arraylist-and-linkedlist-internals.md#flashcards) |
| 7 | When does `BlockingQueue.put()` block? | [BlockingQueue Family](../../02-java/collections/blockingqueue-family.md#flashcards) |
| 8 | What is `SynchronousQueue`'s internal capacity? | [BlockingQueue Family](../../02-java/collections/blockingqueue-family.md#flashcards) |
| 9 | Why is an unbounded `BlockingQueue` a risky default in production? | [BlockingQueue Family](../../02-java/collections/blockingqueue-family.md#flashcards) |
| 10 | What three questions does every collection choice reduce to? | [Collection Selection Decision Matrix](../../02-java/collections/collection-selection-decision-matrix.md#flashcards) |
| 11 | When does defaulting to `ArrayList` actually hurt? | [Collection Selection Decision Matrix](../../02-java/collections/collection-selection-decision-matrix.md#flashcards) |
| 12 | When does defaulting to `HashMap` actually hurt? | [Collection Selection Decision Matrix](../../02-java/collections/collection-selection-decision-matrix.md#flashcards) |
| 13 | What happens to a plain HashMap under concurrent writes from multiple threads? | [ConcurrentHashMap Internals](../../02-java/collections/concurrenthashmap-internals.md#flashcards) |
| 14 | Is `get()` followed by `put()` atomic on a ConcurrentHashMap? | [ConcurrentHashMap Internals](../../02-java/collections/concurrenthashmap-internals.md#flashcards) |
| 15 | What's the correct way to atomically increment a counter in a ConcurrentHashMap? | [ConcurrentHashMap Internals](../../02-java/collections/concurrenthashmap-internals.md#flashcards) |
| 16 | Does `set(index, value)` on a `CopyOnWriteArrayList` copy the whole array, or just the changed slot? | [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../../02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#flashcards) |
| 17 | How much faster were `CopyOnWriteArrayList`'s reads than `synchronizedList()`'s, with zero writers? | [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../../02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#flashcards) |
| 18 | What workload shape makes `CopyOnWriteArrayList`'s trade-off favorable? | [CopyOnWriteArrayList and Copy-on-Write Trade-offs](../../02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#flashcards) |
| 19 | What does a fail-fast iterator actually check to decide whether to throw CME? | [Fail-Fast vs. Weakly Consistent Iterators](../../02-java/collections/fail-fast-vs-weakly-consistent-iterators.md#flashcards) |
| 20 | Does removing an element during a for-each loop always throw CME? | [Fail-Fast vs. Weakly Consistent Iterators](../../02-java/collections/fail-fast-vs-weakly-consistent-iterators.md#flashcards) |
| 21 | Do `CopyOnWriteArrayList` and `ConcurrentHashMap` give the same iteration guarantee under concurrent modification? | [Fail-Fast vs. Weakly Consistent Iterators](../../02-java/collections/fail-fast-vs-weakly-consistent-iterators.md#flashcards) |
| 22 | When does a `HashMap` resize? | [HashMap Internals](../../02-java/collections/hashmap-internals.md#flashcards) |
| 23 | When does a HashMap bucket treeify? | [HashMap Internals](../../02-java/collections/hashmap-internals.md#flashcards) |
| 24 | What happens to HashMap performance with a poor `hashCode()`? | [HashMap Internals](../../02-java/collections/hashmap-internals.md#flashcards) |
| 25 | Does iterating a `PriorityQueue` return elements in priority order? | [PriorityQueue Internals](../../02-java/collections/priorityqueue-internals.md#flashcards) |
| 26 | Is `java.util.PriorityQueue` thread-safe? | [PriorityQueue Internals](../../02-java/collections/priorityqueue-internals.md#flashcards) |
| 27 | How would you actually verify `poll()` is O(log n) rather than assuming it? | [PriorityQueue Internals](../../02-java/collections/priorityqueue-internals.md#flashcards) |

---

## Related

- [`02-java` question bank — concurrency`](02-java-concurrency.md) (next)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
