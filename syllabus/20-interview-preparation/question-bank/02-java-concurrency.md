---
title: "Interview Question Bank — 02-java/concurrency"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../02-java/concurrency/INDEX.md
  - 02-java-collections.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Java Concurrency

Part of the `02-java` compendium. See
[`02-java-collections.md`](02-java-collections.md) for the tier-explanation format
and sourcing discipline, and `00-project/interview-question-bank-plan.md` for the
full 22-domain plan.

**Honest count for this subdomain:** 15 chapters yielded 29 deep questions + 41
quick-fire questions = **70 real questions**. This subdomain has no dedicated Junior
Fundamentals chapter (concurrency is inherently a Mid+ topic in this repository's own
target-level design), so unlike `collections/`, there's no separate leveled-Junior
question set to fold in — several individual questions below still have real, honest
Junior/Mid framings where the underlying misconception is one a less experienced
engineer would genuinely have.

---

## Java Memory Model and `volatile`

### Q1 — Why does double-checked locking break without `volatile`?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/java-memory-model-and-volatile.md#interview-questions)

**What's expected:**
- **Junior:** Knows "you need volatile for thread safety," without the mechanism.
- **Mid:** States that `volatile` is needed for correctness, even without the precise reordering mechanism.
- **Senior:** Without the happens-before edge `volatile` provides, a reader thread can observe a non-null reference to the singleton field before the constructor's writes are visible — a partially-constructed object.
- **Staff:** Distinguishes this from a pure caching problem (compiler/JIT reordering, not stale CPU cache), and can name the alternative fix (a static holder class, relying on class-initialization happens-before instead).

### Q2 — Is `volatile int count; count++;` from multiple threads safe?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/java-memory-model-and-volatile.md#interview-questions)

**What's expected:**
- **Junior:** Answers "yes" — the single most common misconception this question targets.
- **Mid:** States that `count++` is unsafe even with `volatile`, even without the precise read-modify-write reasoning.
- **Senior:** No — `volatile` guarantees visibility of each individual read and write, but `count++` is read-modify-write, three separate operations; another thread can interleave between them. Names `AtomicInteger` or `synchronized` as the fix.
- **Staff:** Connects this directly to the companion chapter's (Deadlock and Race Conditions) measured race-condition data as the concrete, measured version of this exact gap.

---

## Atomics, CAS, and the ABA Problem

### Q1 — What is the ABA problem, and why doesn't `compareAndSet` alone prevent it?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/atomics-cas-and-the-aba-problem.md#interview-questions)

**What's expected:**
- **Junior:** Not typically asked — presupposes understanding CAS first.
- **Mid:** States that ABA involves a value changing and changing back, even without full mechanism.
- **Senior:** CAS only checks whether the current value equals the expected value at the instant it runs; if a value changed A→B→A before the CAS, it succeeds despite the intervening change, which can corrupt structures relying on "nothing changed" rather than "looks the same."
- **Staff:** Generalizes ABA to optimistic-concurrency schemes beyond `AtomicReference` — database version columns, distributed conditional writes.

### Q2 — Your lock-free structure has a rare, unreproducible corruption bug. What do you suspect?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/atomics-cas-and-the-aba-problem.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names ABA as a plausible cause, even without a reproduction strategy.
- **Senior:** Suspects ABA if the structure reuses object/node identity and is built on plain `AtomicReference` CAS; proposes reproducing it deterministically via manual interleaving rather than relying on flaky live timing.
- **Staff:** Proposes the systemic fix — review every lock-free structure with reusable node identity for ABA vulnerability, not just the one that broke.

---

## CompletableFuture and Async Composition

### Q1 — What thread does this callback run on?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/completablefuture-and-async-composition.md#interview-questions)

**What's expected:**
- **Junior:** "A background thread," without the attach-timing distinction.
- **Mid:** Knows `*Async` variants exist and dispatch to an executor, even without the precise attach-timing rule for non-`Async` methods.
- **Senior:** A non-`Async` callback runs on the completing thread if attached before completion, or inline on the attaching thread if attached after completion; `*Async` always dispatches to an executor regardless.
- **Staff:** Connects the choice of executor (default common pool vs. custom) to broader resource-isolation concerns across the application.

### Q2 — You fire off a `CompletableFuture` and never call `get()` on it. What happens if it throws?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/completablefuture-and-async-composition.md#interview-questions)

**What's expected:**
- **Junior:** Assumes it surfaces somewhere automatically — the common mistake this question targets.
- **Mid:** States that the exception is "lost" or "swallowed," even without the precise mechanism.
- **Senior:** The exception is stored inside the future and never surfaced — no stack trace, no log line — unless something calls `join()`, `get()`, `exceptionally()`, or `handle()`.
- **Staff:** Generalizes to the broader principle that any decoupled, fire-and-forget operation needs an explicit failure-observability plan, not just `CompletableFuture` specifically.

---

## Deadlock, Race Conditions, and Thread Diagnostics

### Q1 — Two threads deadlock in production. Walk me through diagnosing it live.

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#interview-questions)

**What's expected:**
- **Junior:** Describes deadlock in the abstract (dining philosophers), without a tool.
- **Mid:** Names thread dumps as a diagnostic tool, even without the precise `ThreadMXBean` mechanism.
- **Senior:** Attach with `jstack` (internally uses `ThreadMXBean.findDeadlockedThreads()`), identify the `BLOCKED` threads and which locks each wants/holds, reconstruct the acquisition-order bug from that.
- **Staff:** Proposes a structural prevention (global lock ordering convention, or eliminating the need for multiple locks via a different design) rather than only reactive detection.

### Q2 — Your metrics counter is undercounting under load. Why, and how do you fix it — show the numbers.

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#interview-questions)

**What's expected:**
- **Junior:** "That shouldn't really happen much" — the common mistake this question targets.
- **Mid:** States that `count++` is unsafe under concurrency, even without measured numbers.
- **Senior:** `count++` isn't atomic; concurrent threads lose updates via interleaved read-modify-write. Names `AtomicLong`/`AtomicInteger` as the fix.
- **Staff:** Knows `LongAdder` trades single-value read consistency for higher-throughput writes under heavy contention — the right choice specifically for write-heavy, read-rarely counters like metrics.

---

## Executors and Thread Pool Sizing

### Q1 — Size this pool. Show the arithmetic.

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/executors-and-thread-pool-sizing.md#interview-questions)

**What's expected:**
- **Junior:** Picks a round number ("8, because that sounds reasonable") — the common mistake this question targets.
- **Mid:** Distinguishes CPU-bound from IO-bound sizing conceptually, even without the precise Little's Law formula.
- **Senior:** Applies Little's Law with a stated request rate and average service time, distinguishing CPU-bound (near `N_cores`) from IO-bound (scales with wait ratio) sizing.
- **Staff:** Proposes separating CPU-bound and IO-bound work into different pools entirely, sized independently.

### Q2 — Queue is unbounded and memory is climbing. Why?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/executors-and-thread-pool-sizing.md#interview-questions)

**What's expected:**
- **Junior:** Assumes the fixed thread count itself caps memory usage — the common mistake this question targets.
- **Mid:** States that the default queue has no limit, even without the precise mechanism.
- **Senior:** The default `newFixedThreadPool`/`newSingleThreadExecutor` queue is an unbounded `LinkedBlockingQueue`; under sustained overload, tasks accumulate without limit. Proposes a bounded queue with an explicit rejection policy.
- **Staff:** Connects the fix to a broader system design point — a queue backing up is a signal the system is overloaded, and the correct response is backpressure/shedding, not "make the queue absorb more."

---

## Foreign Function and Memory API

### Q1 — What two problems does the Foreign Function & Memory API solve?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/foreign-function-and-memory-api.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Not typically asked — this is an Expert-tier, rare-frequency topic in this repository's own register.
- **Senior:** Names both problems it solves: safe off-heap memory access (replacing `Unsafe`/direct `ByteBuffer`s) and calling native code without JNI glue code, plus at least one core abstraction (`MemorySegment`, `Arena`, or `Linker`).
- **Staff:** Correctly states FFM's real version status (finalized JDK 22, preview in JDK 21) and calibrates the depth of the answer to the topic's actual rarity.

---

## ForkJoinPool and Work-Stealing

### Q1 — Explain work-stealing: why do workers steal from the *opposite* end of a peer's deque?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/forkjoinpool-and-work-stealing.md#interview-questions)

**What's expected:**
- **Junior:** "Idle threads grab work from busy ones," with no mechanism.
- **Mid:** Knows idle workers steal work from busy peers, even without the same-end-vs-opposite-end mechanism.
- **Senior:** Each worker treats its own deque as a LIFO stack for cheap, uncontended access to its own cache-hot subtask. An idle worker steals from the opposite (FIFO) end of a peer's deque — the peer's oldest task, minimizing contention and reducing re-stealing.
- **Staff:** Connects this design choice to the broader principle of minimizing shared-resource contention in concurrent data structure design generally.

### Q2 — Does `.parallelStream()` share a pool with `CompletableFuture`'s default `*Async` calls? What about `StructuredTaskScope`?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/forkjoinpool-and-work-stealing.md#interview-questions)

**What's expected:**
- **Junior:** Guesses either "all share" or "none share" — the trap this two-part question is built around.
- **Mid:** Correctly answers the first half (parallel streams and `CompletableFuture` share a pool), even if unsure about structured concurrency.
- **Senior:** Correctly distinguishes both halves — parallel streams and `CompletableFuture`'s unqualified `*Async` calls genuinely share `ForkJoinPool.commonPool()`; `StructuredTaskScope`'s virtual threads run on a separate, dedicated `ForkJoinPool` instance.
- **Staff:** Generalizes to "which shared, implicit resources does this feature actually touch, verified rather than assumed?" — surface-level similarity is not proof of shared resource contention.

---

## MethodHandle and `invokedynamic`

### Q1 — Why are lambda expressions considered more efficient than anonymous inner classes, mechanically?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/methodhandle-and-invoke.md#interview-questions)

**What's expected:**
- **Junior:** "Lambdas don't generate any class at all" — an overclaim the question is designed to catch.
- **Mid:** States that lambdas don't generate an extra class the way anonymous inner classes do, even without the `invokedynamic`/`LambdaMetafactory` mechanism detail.
- **Senior:** A lambda compiles to a single `invokedynamic` instruction, bootstrapped once via `LambdaMetafactory.metafactory` into a cached `CallSite` wrapping a `MethodHandle`. An anonymous inner class is a real, separate compiled class.
- **Staff:** Connects this to the broader platform story: `invokedynamic` was built for JVM-hosted dynamic languages before being reused for lambdas.

### Q2 — What's the actual difference between `invoke()` and `invokeExact()`, and when would `WrongMethodTypeException` be thrown?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/methodhandle-and-invoke.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Not typically asked — presupposes `MethodHandle` familiarity.
- **Senior:** `invokeExact()` requires the call site's types to exactly match the handle's declared `MethodType`, with no implicit conversion; `invoke()` performs that conversion (`asType`) automatically. Correctly names `WrongMethodTypeException`.
- **Staff:** Explains why `invokeExact` exists at all despite `invoke`'s convenience — it avoids the (small but real) `asType` adaptation cost, valuable for statically-known, performance-sensitive call sites.

---

## ReentrantLock, ReadWriteLock, and StampedLock

### Q1 — Your read-heavy code is bottlenecked on a lock. What do you check, and what do you change?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#interview-questions)

**What's expected:**
- **Junior:** Jumps straight to "add more threads/servers" — the common mistake this question targets.
- **Mid:** Names `ReadWriteLock` as a fix for read-heavy lock contention, even without the diagnostic steps.
- **Senior:** Checks thread dumps for threads `BLOCKED` on the same monitor during a read-heavy pattern; replaces the plain lock with `ReadWriteLock` since readers don't mutate state, so concurrent reading is inherently safe.
- **Staff:** Considers `StampedLock`'s optimistic path as a further step if `ReadWriteLock` alone doesn't fully resolve the bottleneck, and explains the additional discipline it requires.

### Q2 — What happens if you skip `validate()` after a `StampedLock` optimistic read?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `StampedLock` "handles" correctness automatically — the common mistake this question targets.
- **Senior:** Without `validate()`, the reader has no way to know whether a writer committed a change since the stamp was issued — the read may be silently stale/torn, with no signal anything went wrong.
- **Staff:** Connects this to the general risk of optimistic-concurrency patterns anywhere in a system: the validation step is not an optional performance nicety, it is the correctness boundary.

---

## Scoped Values and ThreadLocal Migration

### Q1 — What's the actual bug risk with `ThreadLocal` in a thread-pool-based system?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/scoped-values-and-threadlocal-migration.md#interview-questions)

**What's expected:**
- **Junior:** Assumes `ThreadLocal` is automatically cleaned up when a task finishes — the common mistake this question targets.
- **Mid:** States that forgetting to call `remove()` can cause stale data, even without the thread-pool-reuse mechanism specifically.
- **Senior:** A `ThreadLocal` set on a pooled thread and never explicitly removed remains genuinely visible to the next, unrelated task that reuses that same physical thread — a real, measured leak. Proposes guaranteed `remove()` or migration to `ScopedValue`.
- **Staff:** Generalizes to the broader principle of state lifecycle tied to a physical resource versus a logical unit of work.

### Q2 — Does a `ThreadLocal` set on the main thread show up inside a thread pool's worker threads automatically?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/scoped-values-and-threadlocal-migration.md#interview-questions)

**What's expected:**
- **Junior:** Assumes automatic propagation to any child thread — the common mistake this question targets.
- **Mid:** States that `ThreadLocal` values don't automatically appear on other threads, even without naming `InheritableThreadLocal` or `ScopedValue`.
- **Senior:** No — a plain `ThreadLocal` is genuinely per-thread and does not propagate automatically. `InheritableThreadLocal` copies the value once at child-thread creation, still carrying the same reuse-leak risk. `ScopedValue` is the real mechanism for propagation into structured-concurrency subtasks.
- **Staff:** Connects this to the broader structured-concurrency design philosophy — subtask context should propagate predictably from the scope that spawned it.

---

## Structured Concurrency

### Q1 — What's the actual difference between fanning out with `CompletableFuture` and with `StructuredTaskScope`?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/structured-concurrency.md#interview-questions)

**What's expected:**
- **Junior:** "They're basically the same, just different syntax" — the common mistake this question targets.
- **Mid:** States that `StructuredTaskScope` provides better cancellation behavior, even without precise mechanism.
- **Senior:** `CompletableFuture` subtasks run independently to completion regardless of a sibling's failure; `StructuredTaskScope.ShutdownOnFailure` automatically interrupts sibling subtasks the moment one fails, and the scope cannot exit until every subtask has terminated.
- **Staff:** Generalizes to the broader lifetime-coupling design question applicable beyond this specific API.

### Q2 — Is `StructuredTaskScope` safe to use in production today?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/structured-concurrency.md#interview-questions)

**What's expected:**
- **Junior:** "Yes, it's in the JDK docs so it's fine" — the common mistake this question targets.
- **Mid:** Knows it's a preview feature, even without the exact JEP number or version history.
- **Senior:** As of JDK 21, it's a preview API (JEP 453, second preview) requiring `--enable-preview`, with its API surface not guaranteed stable until finalized.
- **Staff:** Frames the adoption decision as a real cost/benefit trade-off against the measured orphaned-task cost of not adopting it, rather than a simple yes/no.

---

## Synchronizers — CountDownLatch, CyclicBarrier, Semaphore

### Q1 — How would you implement an application readiness check waiting for several independent background tasks?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#interview-questions)

**What's expected:**
- **Junior:** Proposes a polling loop checking a shared counter without synchronization — the common mistake this question targets.
- **Mid:** Proposes some coordination mechanism that correctly waits for multiple tasks, even if not `CountDownLatch` by name.
- **Senior:** Use a `CountDownLatch` initialized to the number of tasks; each calls `countDown()` on completion; the check calls `await()` or checks `getCount() == 0`.
- **Staff:** Connects this to the real production failure mode (traffic routed to a not-actually-ready instance) and proposes a bounded `await(timeout, unit)` so a stuck task produces a diagnosable timeout rather than an indefinite hang.

### Q2 — What's the difference between `CountDownLatch` and `CyclicBarrier`, beyond one being reusable?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#interview-questions)

**What's expected:**
- **Junior:** Names only "one is reusable, one isn't."
- **Mid:** Same, without the signaling-role difference underneath it.
- **Senior:** `CountDownLatch` is signaled by `countDown()` calls from *any* number of threads, independent of how many `await()` it — signaling and waiting are decoupled. `CyclicBarrier` requires each of the same fixed number of parties to call `await()` itself.
- **Staff:** Identifies the barrier-action distinction (`CyclicBarrier` supports a once-per-round action on the last-arriving thread) unprompted and connects it to a real use case (rolling up per-phase results in multi-phase parallel computation).

---

## ThreadLocal-Mediated Classloader Leaks

### Q1 — Why can one leaked `ThreadLocal` value keep an entire classloader alive?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/threadlocal-mediated-classloader-leaks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes only the directly-leaked object itself is affected — the common mistake this question targets.
- **Senior:** Every object strongly references its own `Class`, and every `Class` strongly references its defining `ClassLoader` — a single leaked instance keeps that entire classloader, and everything it loaded, reachable.
- **Staff:** Connects this to the real, diagnosable Metaspace-growth-after-redeploy symptom and proposes both disciplinary and structural fixes.

### Q2 — Why does this leak show up as Metaspace growth specifically, rather than ordinary heap growth?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/threadlocal-mediated-classloader-leaks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates this with an ordinary heap-based object leak — the common mistake this question targets.
- **Senior:** Class metadata lives in Metaspace, not the regular heap; the leaked classloader keeps its loaded classes' metadata reachable, so metadata accumulates in Metaspace across repeated redeploys.
- **Staff:** Discusses why this specific symptom shape (Metaspace growth tracking redeploy count) is a strong diagnostic signal pointing directly at a classloader leak, rather than a generic leak.

---

## VarHandles and Unsafe

### Q1 — Why did `sun.misc.Unsafe` become widely used despite never being a supported API?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/varhandles-and-unsafe.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `Unsafe` was simply "faster" — the common mistake this question targets.
- **Senior:** No public Java API provided fine-grained, low-level memory access that high-performance libraries genuinely needed — `Unsafe` was the only thing that offered it.
- **Staff:** Connects this to `VarHandle`'s introduction as the JDK team's deliberate response — a public, safe replacement — and the module system's ongoing effort to push code off `Unsafe` entirely.

### Q2 — What does `VarHandle` provide that neither `AtomicInteger` nor `sun.misc.Unsafe` does?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/varhandles-and-unsafe.md#interview-questions)

**What's expected:**
- **Junior/Mid:** "It's a newer/safer API," without naming the access-mode granularity.
- **Senior:** A real, granular family of access modes (plain, opaque, acquire/release, volatile), each with an explicit, named ordering guarantee, chosen per call — `AtomicXxx` only offers full volatile-strength semantics; `Unsafe` offers no sanctioned ordering API at all.
- **Staff:** Discusses when this granularity is worth its added complexity versus when the safer default (`AtomicXxx`/`volatile`) is the better engineering choice.

---

## Virtual Threads

### Q1 — What actually changes for IO-bound workloads under virtual threads?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/concurrency/virtual-threads.md#interview-questions)

**What's expected:**
- **Junior:** "Virtual threads make everything faster" — the common mistake this question targets.
- **Mid:** States that virtual threads help with concurrency for blocking work, even without the precise mechanism.
- **Senior:** The achievable concurrency for blocking-IO-heavy work is no longer capped by platform-thread memory cost — many virtual threads can be blocked simultaneously because unmounted ones don't occupy a carrier.
- **Staff:** Names pinning unprompted as the regression case, and can state roughly how severe it is from having seen or reasoned through a real measurement.

### Q2 — Why is pooling virtual threads an anti-pattern?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/concurrency/virtual-threads.md#interview-questions)

**What's expected:**
- **Junior:** "Pool them like any other thread" — the common mistake this question targets.
- **Mid:** States that virtual threads shouldn't be pooled, even without full reasoning why.
- **Senior:** Virtual threads are designed to be cheap and disposable, created per-task; pooling reintroduces the platform-thread-style "limited resource, must be reused" mental model virtual threads exist to eliminate.
- **Staff:** Names an actual alternative for the underlying need (limiting concurrent load on a downstream system) — a semaphore or rate limiter bounding concurrent in-flight requests, decoupled from thread count entirely.

---

## Quick-fire questions (from this subdomain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Does a successful `compareAndSet` mean the value never changed? | [Atomics, CAS, ABA](../../02-java/concurrency/atomics-cas-and-the-aba-problem.md#flashcards) |
| 2 | How does `AtomicStampedReference` fix the ABA problem? | [Atomics, CAS, ABA](../../02-java/concurrency/atomics-cas-and-the-aba-problem.md#flashcards) |
| 3 | Is a CAS retry loop always faster than a `synchronized` block? | [Atomics, CAS, ABA](../../02-java/concurrency/atomics-cas-and-the-aba-problem.md#flashcards) |
| 4 | If you attach `thenApply` to an already-complete `CompletableFuture`, what thread runs it? | [CompletableFuture](../../02-java/concurrency/completablefuture-and-async-composition.md#flashcards) |
| 5 | What happens to an exception thrown inside a pipeline nothing ever calls `get()`/`join()` on? | [CompletableFuture](../../02-java/concurrency/completablefuture-and-async-composition.md#flashcards) |
| 6 | How do you accidentally turn two independent async calls into a sequential pipeline? | [CompletableFuture](../../02-java/concurrency/completablefuture-and-async-composition.md#flashcards) |
| 7 | What are the six real `Thread.State` values? | [Deadlock and Thread Diagnostics](../../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#flashcards) |
| 8 | How do you detect a deadlock in a live JVM? | [Deadlock and Thread Diagnostics](../../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#flashcards) |
| 9 | How much data can an unsynchronized `count++` lose under real concurrent load? | [Deadlock and Thread Diagnostics](../../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#flashcards) |
| 10 | What queue does `Executors.newFixedThreadPool()` use by default, and what's the consequence? | [Executors and Pool Sizing](../../02-java/concurrency/executors-and-thread-pool-sizing.md#flashcards) |
| 11 | How do you get real backpressure from a thread pool? | [Executors and Pool Sizing](../../02-java/concurrency/executors-and-thread-pool-sizing.md#flashcards) |
| 12 | How should CPU-bound vs. IO-bound pool sizing differ? | [Executors and Pool Sizing](../../02-java/concurrency/executors-and-thread-pool-sizing.md#flashcards) |
| 13 | What two older mechanisms does the Foreign Function & Memory API replace? | [Foreign Function & Memory API](../../02-java/concurrency/foreign-function-and-memory-api.md#flashcards) |
| 14 | Why does an idle worker steal from the opposite end of a peer's deque? | [ForkJoinPool and Work-Stealing](../../02-java/concurrency/forkjoinpool-and-work-stealing.md#flashcards) |
| 15 | Do parallel streams, `CompletableFuture`, and `StructuredTaskScope` all share the same pool? | [ForkJoinPool and Work-Stealing](../../02-java/concurrency/forkjoinpool-and-work-stealing.md#flashcards) |
| 16 | How would you verify work-stealing is actually happening? | [ForkJoinPool and Work-Stealing](../../02-java/concurrency/forkjoinpool-and-work-stealing.md#flashcards) |
| 17 | What does `volatile` actually guarantee? | [Java Memory Model](../../02-java/concurrency/java-memory-model-and-volatile.md#flashcards) |
| 18 | Does `volatile` make `count++` thread-safe? | [Java Memory Model](../../02-java/concurrency/java-memory-model-and-volatile.md#flashcards) |
| 19 | Why does double-checked locking need `volatile` on the singleton field? | [Java Memory Model](../../02-java/concurrency/java-memory-model-and-volatile.md#flashcards) |
| 20 | What's the real difference between `invoke()` and `invokeExact()`? | [MethodHandle and invokedynamic](../../02-java/concurrency/methodhandle-and-invoke.md#flashcards) |
| 21 | Mechanically, why doesn't a lambda produce a separate `.class` file? | [MethodHandle and invokedynamic](../../02-java/concurrency/methodhandle-and-invoke.md#flashcards) |
| 22 | Name three real capabilities `ReentrantLock` has that `synchronized` lacks. | [ReentrantLock, RWLock, StampedLock](../../02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#flashcards) |
| 23 | What can happen concurrently under a `ReentrantReadWriteLock`? | [ReentrantLock, RWLock, StampedLock](../../02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#flashcards) |
| 24 | What must you always do after `StampedLock.tryOptimisticRead()`? | [ReentrantLock, RWLock, StampedLock](../../02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md#flashcards) |
| 25 | Does `ScopedValue` have a `set()` method like `ThreadLocal`? | [Scoped Values and ThreadLocal Migration](../../02-java/concurrency/scoped-values-and-threadlocal-migration.md#flashcards) |
| 26 | What real bug can happen if `ThreadLocal.remove()` is forgotten in thread-pool code? | [Scoped Values and ThreadLocal Migration](../../02-java/concurrency/scoped-values-and-threadlocal-migration.md#flashcards) |
| 27 | Does a `ThreadLocal` set on a parent thread automatically appear on a spawned child? | [Scoped Values and ThreadLocal Migration](../../02-java/concurrency/scoped-values-and-threadlocal-migration.md#flashcards) |
| 28 | What does "structured" actually guarantee in structured concurrency? | [Structured Concurrency](../../02-java/concurrency/structured-concurrency.md#flashcards) |
| 29 | Does `CompletableFuture` automatically cancel sibling tasks when one fails? | [Structured Concurrency](../../02-java/concurrency/structured-concurrency.md#flashcards) |
| 30 | Is `StructuredTaskScope` a stable JDK 21 API? | [Structured Concurrency](../../02-java/concurrency/structured-concurrency.md#flashcards) |
| 31 | Can a `CountDownLatch` be reset and reused for a second round? | [Synchronizers](../../02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#flashcards) |
| 32 | Does a `Semaphore` require the same thread to `acquire()` and `release()`? | [Synchronizers](../../02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#flashcards) |
| 33 | How would you verify a `Semaphore` actually bounds concurrent access under real contention? | [Synchronizers](../../02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md#flashcards) |
| 34 | Under what condition does one leaked `ThreadLocal` value leak an entire classloader? | [ThreadLocal-Mediated Classloader Leaks](../../02-java/concurrency/threadlocal-mediated-classloader-leaks.md#flashcards) |
| 35 | What production symptom is the classic signature of this leak? | [ThreadLocal-Mediated Classloader Leaks](../../02-java/concurrency/threadlocal-mediated-classloader-leaks.md#flashcards) |
| 36 | What does `VarHandle` provide that `AtomicInteger` doesn't, for the same atomic-increment use case? | [VarHandles and Unsafe](../../02-java/concurrency/varhandles-and-unsafe.md#flashcards) |
| 37 | Can a `VarHandle` perform a volatile-strength read on a plain (non-`volatile`) field? | [VarHandles and Unsafe](../../02-java/concurrency/varhandles-and-unsafe.md#flashcards) |
| 38 | Why doesn't this chapter demonstrate a live reordering bug from a weaker access mode? | [VarHandles and Unsafe](../../02-java/concurrency/varhandles-and-unsafe.md#flashcards) |
| 39 | What does a virtual thread's carrier do when the virtual thread blocks on supported IO? | [Virtual Threads](../../02-java/concurrency/virtual-threads.md#flashcards) |
| 40 | What causes a virtual thread to pin its carrier? | [Virtual Threads](../../02-java/concurrency/virtual-threads.md#flashcards) |
| 41 | Why is pooling virtual threads considered an anti-pattern? | [Virtual Threads](../../02-java/concurrency/virtual-threads.md#flashcards) |

---

## Related

- [`02-java` question bank — collections`](02-java-collections.md)
- [`02-java` question bank — jvm-internals`](02-java-jvm-internals.md) (next)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
