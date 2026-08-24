---
title: "Structured Concurrency"
slug: structured-concurrency
document_type: handbook-chapter
domain: concurrency
status: draft
version: 1.0
last_updated: 2026-08-24
difficulty:
  - advanced
target_levels:
  - senior
  - staff
estimated_reading_minutes: 26
prerequisites:
  - virtual-threads.md
  - executors-and-thread-pool-sizing.md
related:
  - completablefuture-and-async-composition.md
  - forkjoinpool-and-work-stealing.md
  - ../../practice/java/concurrency/structured-concurrency/README.md
official_references:
  - https://openjdk.org/jeps/453
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html
---

# Structured Concurrency

> **Topic register:** T-411 · IWI 5.2 · Advanced tier · Moderate interview frequency [M]
> **Version status:** `StructuredTaskScope` is a **preview API in JDK 21** ([JEP 453](https://openjdk.org/jeps/453),
> second preview — `--enable-preview` required to compile and run). It continued evolving through further preview
> rounds in later JDK releases before finalization; this chapter targets the exact JDK 21 preview API surface
> verified in this repository's own JDK (OpenJDK 21.0.12) — do not assume a later JDK's finalized API is
> byte-for-byte identical without checking.
> **Provenance:** all three traces in this chapter are real, executed output from
> [`practice/java/concurrency/structured-concurrency/`](../../practice/java/concurrency/structured-concurrency/README.md).

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Diagrams](#diagrams)
8. [Production Scenarios](#production-scenarios)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Anti-Patterns](#anti-patterns)
13. [Best Practices](#best-practices)
14. [Interview Answer Framework](#interview-answer-framework)
15. [Interview Questions](#interview-questions)
16. [Summary](#summary)
17. [Key Takeaways](#key-takeaways)
18. [Cheat Sheet](#cheat-sheet)
19. [Flashcards](#flashcards)
20. [Practice Exercises](#practice-exercises)
21. [Solutions](#solutions)
22. [Additional Reading](#additional-reading)
23. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can:

- Explain what "structured" means in structured concurrency — thread lifetime bound to a lexical scope — and why that closes a real gap `CompletableFuture` and raw `ExecutorService` leave open.
- Demonstrate, with a real measured timing, that `StructuredTaskScope.ShutdownOnFailure` automatically cancels sibling subtasks when one fails.
- Reproduce, with real measured timing, the orphaned-task problem that structured concurrency exists specifically to prevent.
- State the correct, current version status of `StructuredTaskScope` (preview in JDK 21) and what that implies for production use.

## Why This Matters in Interviews

Structured concurrency is Advanced tier and Moderate frequency because it's newer and less universally used than `CompletableFuture`, but interviewers who do ask about it are specifically probing whether a candidate understands *why* it exists — not just its API. The real differentiator this chapter measures directly: `CompletableFuture` composition looks concurrent and correct, but leaves a genuine, measurable resource leak on the table (an orphaned sibling task) that structured concurrency's automatic cancellation propagation closes by construction, not by discipline.

## Mental Model

**A structured concurrent operation has exactly one entry and exactly one exit — like a method call, but for a tree of threads instead of a tree of stack frames.** Just as a method cannot return while a sub-call it made is still running, a `StructuredTaskScope` cannot exit its `try`-block while any subtask it forked is still alive: `close()` (called implicitly by try-with-resources) waits for every forked subtask to finish, and if one fails, the scope proactively interrupts the others rather than passively waiting them out. This is the exact discipline `CompletableFuture` composition doesn't enforce — nothing stops a `CompletableFuture` pipeline from outliving the code that created it.

## Definition and Purpose

**Structured concurrency** ([JEP 453](https://openjdk.org/jeps/453)) is a programming model where a set of concurrent subtasks forked within a lexical scope are guaranteed to complete — successfully, by cancellation, or by failure — before that scope exits, mirroring the same "no orphaned work" guarantee ordinary single-threaded code gets for free from the call stack. `StructuredTaskScope` is the JDK's API for this: subtasks are `fork()`ed inside a `try`-with-resources block, `join()` waits for them, and the scope's *policy* (`ShutdownOnFailure`, `ShutdownOnSuccess`, or a custom policy) decides what happens when one subtask fails or succeeds while others are still running. It exists because `CompletableFuture` and raw `ExecutorService` submission both decouple a spawned task's lifetime from the code that spawned it — nothing automatically cancels sibling work when one branch of a fan-out fails, which is a real, measurable resource leak this chapter reproduces directly.

## Core Concepts

### Fork, join, and a shutdown policy

`scope.fork(callable)` starts a subtask on a new virtual thread and returns a `Subtask` handle; `scope.join()` blocks until every forked subtask reaches a terminal state (or the scope is shut down). `ShutdownOnFailure` shuts the scope down — interrupting every other running subtask — the moment any one subtask throws; `throwIfFailed()` then re-raises that failure to the caller. `ShutdownOnSuccess` is the inverse: shuts down (cancelling the rest) the moment any one subtask succeeds, useful for "first result wins" racing patterns.

### Automatic cancellation propagation is the actual point

The core guarantee this chapter measures directly: when one subtask fails under `ShutdownOnFailure`, every sibling subtask still running is really, actively interrupted — not merely left to finish on its own schedule. This is fundamentally different from a `CompletableFuture` pipeline, where a sibling `supplyAsync` call has no automatic relationship to another one failing; it keeps running to completion regardless, a real orphaned task consuming real thread/resource time for no purpose once the overall operation has already failed.

### "Structured" means the scope cannot outlive its subtasks, by construction

`StructuredTaskScope.close()` (called by try-with-resources on scope exit) does not return until every forked subtask has completed — successfully, by exception, or by cancellation. This is enforced by the API itself, not by developer discipline: there is no way to fork a subtask and walk away from the scope while it's still running, which is exactly the gap that makes orphaned tasks possible with `CompletableFuture`.

## Internal Implementation

**Two real, concurrent subtasks, joined together:**

```
user=user-42, orders=orders-[7,8,9]
Real elapsed: 264ms -- both ~200ms and ~250ms calls ran CONCURRENTLY, not sequentially (sequential would be ~450ms)
```

Two subtasks (~200ms and ~250ms of real work) forked into a `ShutdownOnFailure` scope, joined together. The real elapsed time tracks the *slower* subtask, not their sum — direct proof of real concurrent execution, not sequential.

**Real, measured automatic cancellation on sibling failure:**

```
Real elapsed: 116ms (the long task's FULL budget was 5000ms)
Long-running sibling was really interrupted (flag check or Thread.sleep() throwing): true
scope.throwIfFailed() correctly surfaced the real failure: java.lang.IllegalStateException: simulated fast failure
```

One subtask fails after ~100ms. Its sibling has a real 5-second budget. The scope's real elapsed time is ~116ms — the sibling really was interrupted almost immediately, its own `Thread.sleep()` call throwing `InterruptedException` rather than running anywhere near its full budget. This is real, measured cancellation propagation, not a documentation claim taken on faith.

**The real problem this exists to solve, measured directly with the unstructured equivalent:**

```
Caller observes the failure at +109ms and "moves on" -- but the sibling task is STILL RUNNING in the background right now, uncancelled.
isDone() on the sibling immediately after 'moving on': false
  [background] orphaned task FINALLY finished at +2009ms
Total real wall time until the orphaned task actually finished: 2011ms
```

The identical shape built with plain `CompletableFuture` instead. The caller genuinely moves on at ~109ms, but the sibling — verified real via `isDone() == false` at that exact moment — is still running, uncancelled, only finishing at the real ~2011ms mark. This is the real, measured cost `StructuredTaskScope`'s automatic cancellation propagation exists to eliminate.

## Diagrams

```mermaid
sequenceDiagram
    participant Caller
    participant Scope as StructuredTaskScope
    participant A as Subtask A (fails ~100ms)
    participant B as Subtask B (5s budget)

    Caller->>Scope: fork(A), fork(B)
    A-->>Scope: fails at ~100ms
    Scope->>B: interrupt() -- automatic, real
    B-->>Scope: terminates early (~116ms total)
    Scope-->>Caller: join() returns; throwIfFailed() surfaces A's exception

    Note over Caller,B: Contrast -- plain CompletableFuture: nothing interrupts B; it really runs its full 5s regardless of A's failure
```

## Production Scenarios

### Scenario: a fan-out API call leaves orphaned downstream requests after one branch times out

**Symptoms.** A service fans out to three downstream services concurrently via `CompletableFuture.supplyAsync()` to assemble a response. When one downstream call times out and the handler returns an error to the client, monitoring shows the other two downstream calls' connections and threads remaining active for their *full* configured timeout, well after the client has already received an error response.

**Impact.** Real, wasted downstream load and held connections/threads for calls whose results the caller will never use, compounding under any real-world timeout-heavy incident (exactly when the downstream services are already struggling and can least afford wasted extra load).

**Initial hypotheses.** A connection-pool leak in the HTTP client itself (checked — connections are correctly returned to the pool once each call actually completes); a bug in the error-handling path (checked — the error handling correctly returns to the client); the fan-out has no mechanism to cancel siblings once the overall response has already failed (correct).

**Evidence.** Reproducing the exact shape of `UnstructuredLeakDemo` against the real handler confirms it: each `supplyAsync` branch runs to its own full completion or timeout independently, with zero relationship to any sibling branch's outcome.

**Diagnosis.** The real orphaned-task mechanism this chapter measures directly: `CompletableFuture` composition has no built-in concept of "the sibling branches should stop because the overall operation already failed" — each branch's lifetime is fully decoupled from the others'.

**Immediate mitigation.** Reduce each downstream call's individual timeout so orphaned calls at least resolve faster, shrinking (not eliminating) the wasted-load window.

**Permanent remediation.** Migrate the fan-out to `StructuredTaskScope.ShutdownOnFailure` (once past preview, or accepted as a preview feature for this JVM version in agreement with the team) so a failing branch automatically interrupts the others — the exact real cancellation measured in this chapter — eliminating the wasted downstream load structurally rather than merely shrinking its window.

**Alternatives considered.** Manually tracking and cancelling sibling `CompletableFuture`s on any branch's failure — a real, workable alternative, but requires disciplined, correct bookkeeping at every fan-out call site; `StructuredTaskScope` provides the same guarantee structurally, by construction, at every call site automatically.

**Trade-offs.** Adopting a preview API requires `--enable-preview` at both compile and runtime, and an explicit organizational decision about running preview features in production — a real, non-trivial adoption cost weighed against the real, measured resource-leak cost of not adopting it.

**Prevention.** Any concurrent fan-out where one branch's failure should logically stop the others should be reviewed for this exact leak — "does anything actually cancel my siblings when this fails?" is the right question, and `CompletableFuture` alone never answers yes without explicit extra code.

**Interview lesson.** This is Interview Question 2 (§ Interview Questions) — "what's the actual difference between fanning out with `CompletableFuture` and with `StructuredTaskScope`?" — arriving as a real, measured production resource-waste pattern rather than an abstract API comparison.

## Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| `StructuredTaskScope` | Real, automatic cancellation of sibling subtasks on failure; scope cannot leak running work past its own exit | Preview API in JDK 21 — requires `--enable-preview`; API surface not yet finalized (may differ across JDK versions) |
| Plain `CompletableFuture` fan-out | Stable, widely available, no preview flag | No automatic cancellation propagation — a real, measured resource leak on sibling failure unless manually implemented |
| Manual `CompletableFuture` cancellation bookkeeping | Achieves a similar guarantee without a preview API | Requires correct, disciplined code at every fan-out call site — easy to get wrong or forget |

## Decision Framework

1. **Does a concurrent fan-out have subtasks whose failure should logically stop the others?** If yes, `StructuredTaskScope.ShutdownOnFailure` gives that guarantee structurally; `CompletableFuture` does not, without extra manual work.
2. **Is this a "first result wins" racing pattern** (e.g., query two replicas, use whichever answers first)? `StructuredTaskScope.ShutdownOnSuccess` is built for exactly this shape.
3. **Is running a preview API in this JVM version acceptable for this codebase/team?** If not yet, `CompletableFuture` remains the stable choice — but explicitly track the orphaned-task cost this chapter measures as a known, accepted trade-off, not an invisible one.
4. **Does the fan-out's subtasks' lifetimes genuinely need to be independent of each other** (e.g., logging/metrics side-effects that should complete regardless)? Then the coupling `StructuredTaskScope` enforces may be the wrong fit — not every concurrent operation should have coupled lifetimes.

## Common Mistakes

- Assuming `CompletableFuture` composition automatically cancels sibling tasks on one branch's failure — it does not, measured directly as a real, sizeable resource leak.
- Using `StructuredTaskScope` in production without an explicit, deliberate decision about preview-API risk (API surface changes, `--enable-preview` requirement).
- Treating "structured" as merely a stylistic/API-shape preference rather than the real, enforced lifetime-coupling guarantee it provides.
- Forgetting that `join()` alone does not re-raise a failure — `throwIfFailed()` (or checking `exception()`) is required to actually surface it to the caller.

## Anti-Patterns

- **Fanning out via bare `CompletableFuture.supplyAsync()` calls with no cancellation relationship between them**, when the operation's actual semantics require "if one fails, the others are pointless."
- **Adopting a preview API in production without team-wide agreement and an explicit upgrade plan**, treating "it compiles with `--enable-preview`" as sufficient justification alone.
- **Coupling genuinely independent subtasks' lifetimes** under a structured scope when their independence was actually intentional (e.g., a best-effort metrics call that shouldn't be cancelled just because the main result failed).

## Best Practices

- Reach for `StructuredTaskScope` (where its preview status is acceptable) whenever a fan-out's subtasks have a genuine "if one fails, cancel the rest" relationship — let the API enforce it structurally rather than hand-rolling cancellation bookkeeping.
- Always call `throwIfFailed()` (or inspect `exception()`) after `join()` under `ShutdownOnFailure` — `join()` alone does not propagate a subtask's failure.
- Explicitly track and communicate preview-API adoption decisions (JDK version pinned, `--enable-preview` requirement) rather than letting them become an implicit, undocumented dependency.
- When independence between subtasks is intentional, don't force them into one structured scope merely for API convenience — plain concurrent execution (or separate scopes) may be the more correct fit.

## Interview Answer Framework

### 30-Second Answer

Structured concurrency (`StructuredTaskScope`, JEP 453, preview in JDK 21) binds a set of concurrent subtasks' lifetimes to a single lexical scope — the scope cannot exit while any subtask is still running, and `ShutdownOnFailure` automatically interrupts sibling subtasks the moment one fails. `CompletableFuture` doesn't provide this: a sibling task keeps running to its full completion regardless of another branch's failure, a real, measurable resource leak this fixes structurally.

### 2-Minute Answer

Definition: structured concurrency ties concurrent subtask lifetimes to a lexical scope, the same way a method call ties sub-call lifetimes to the call stack. Why it exists: `CompletableFuture`/raw executor submission decouples a task's lifetime from its creator, leaving orphaned work possible on partial failure. How it works: `fork()` subtasks inside a try-with-resources `StructuredTaskScope`; `join()` waits for all; a policy (`ShutdownOnFailure`/`ShutdownOnSuccess`) decides what happens on one subtask's early failure/success, interrupting the rest. One important trade-off: it's a JDK 21 preview API, requiring `--enable-preview` and an explicit adoption decision. Production example: a real, measured ~2-second orphaned downstream call versus ~100ms with structured cancellation, for the identical fan-out shape.

### 10-Minute Deep Dive

Cover, in order: the mental model — thread lifetime bound to lexical scope, the same guarantee the call stack gives single-threaded code (mental model); the real, measured concurrent fork/join baseline (internals, real evidence); the real, measured automatic cancellation on sibling failure — ~116ms versus a 5-second budget (internals, real evidence); the real, measured orphaned-task cost of the unstructured `CompletableFuture` equivalent — ~2 real seconds versus ~100ms (internals, real evidence); the decision framework for when coupling subtask lifetimes is actually correct versus when independence is intentional (decision framework); and close with the production scenario — a real fan-out API leaving orphaned downstream calls after one branch times out, quantified with this chapter's exact measurement technique.

### Whiteboard Explanation

Draw the [§ Diagrams](#diagrams) sequence diagram: subtask A fails at ~100ms; the scope interrupts subtask B; the whole operation completes at ~116ms instead of B's full 5-second budget. Beside it, draw the `CompletableFuture` contrast: A fails, nothing interrupts B, B runs to its full ~2-second completion regardless. The side-by-side timing gap is the entire argument, made visual.

### Production Example

The fan-out orphaned-call scenario in [§ Production Scenarios](#production-scenarios): downstream calls kept running their full timeout after an unrelated sibling branch failed and the client had already received an error response — fixed by migrating the fan-out to `StructuredTaskScope.ShutdownOnFailure`'s real, automatic cancellation.

### Trade-offs to Mention

State unprompted: `StructuredTaskScope` is a real preview API in JDK 21, not yet finalized — adoption is a deliberate decision, not a drop-in swap; the orphaned-task cost of `CompletableFuture` fan-out is real and measurable, not a hypothetical concern; not every concurrent operation should have coupled subtask lifetimes — genuine independence is sometimes the correct design.

### Common Candidate Mistakes

Assuming `CompletableFuture.allOf()`/fan-out patterns already cancel siblings on failure; treating "structured" as purely stylistic rather than an enforced lifetime guarantee; forgetting `throwIfFailed()` is required to actually surface a subtask's exception after `join()`.

### Typical Follow-Up Questions

1. "What's the actual difference between fanning out with `CompletableFuture` and with `StructuredTaskScope`?"
2. "Is `StructuredTaskScope` safe to use in production today?"
3. "What happens if you call `join()` but never call `throwIfFailed()`?"

### Senior-Level Expectations

Correctly explains the lifetime-coupling guarantee and the automatic-cancellation-on-failure behavior; knows it's a preview feature and states the version implication.

### Staff-Level Discussion

Structured concurrency generalizes a principle broader than this one JDK API: any time work is spawned whose lifetime should logically be coupled to the outcome of a larger operation, decoupling that lifetime (as `CompletableFuture`/raw thread submission does by default) creates a real resource-leak surface that grows invisibly with scale — the same pattern shows up in unbounded background job submission, fire-and-forget message publishing without a corresponding cancellation/expiry mechanism, and orphaned distributed-trace spans. A Staff-level engineer evaluates any concurrent fan-out pattern by asking "what happens to the other branches when one branch fails, and is that the actual desired behavior?" — treating lifetime coupling as a design decision to make explicitly, whether or not `StructuredTaskScope` itself is the chosen mechanism, and factoring the real adoption cost of a preview API (version pinning, team alignment, migration risk once finalized) into that decision rather than either dismissing it as "not production-ready" or adopting it uncritically.

## Interview Questions

### Question 1 — What's the actual difference between fanning out with `CompletableFuture` and with `StructuredTaskScope`?

**Why interviewers ask it.** Tests whether the candidate understands the real, measurable behavioral gap (cancellation propagation) rather than treating the two as stylistically different but functionally equivalent.

**Expected answer.** `CompletableFuture` subtasks run independently to completion regardless of a sibling's failure; `StructuredTaskScope.ShutdownOnFailure` automatically interrupts sibling subtasks the moment one fails, and the scope cannot exit until every subtask has actually terminated.

**Minimum acceptable answer.** States that `StructuredTaskScope` provides better cancellation behavior, even without precise mechanism.

**Strong Senior answer.** Explains the automatic-interruption behavior precisely and names the real cost of its absence (orphaned tasks).

**Staff-level extension.** Generalizes to the broader lifetime-coupling design question applicable beyond this specific API.

**Common mistakes.** Assuming `CompletableFuture.allOf()` already provides cancellation-on-failure semantics — it doesn't; it only waits for all futures regardless of individual outcomes.

**Likely follow-ups.** "Is this safe to use in production today?"

**Evaluation criteria (1–5).** 1: "they're basically the same, just different syntax." 3: correctly identifies the cancellation-propagation difference. 5: correct difference plus the orphaned-task cost quantified and the lifetime-coupling generalization.

**Related references.** [§ Core Concepts](#core-concepts), [§ Internal Implementation](#internal-implementation).

---

### Question 2 — Is `StructuredTaskScope` safe to use in production today?

**Why interviewers ask it.** Tests whether the candidate tracks real API maturity/version status rather than assuming any JDK API is automatically production-stable.

**Expected answer.** As of JDK 21, `StructuredTaskScope` is a preview API (JEP 453, second preview) — it requires `--enable-preview` at compile and run time, and its API surface is not guaranteed stable across JDK versions until finalized; production adoption is a deliberate, explicit team decision, not a default choice.

**Minimum acceptable answer.** Knows it's a preview feature, even without the exact JEP number or version history.

**Strong Senior answer.** States the `--enable-preview` requirement and the API-instability risk explicitly.

**Staff-level extension.** Frames the adoption decision as a real cost/benefit trade-off against the measured orphaned-task cost of not adopting it, rather than a simple yes/no.

**Common mistakes.** Assuming any feature documented in the official JDK Javadoc is automatically stable and production-ready.

**Likely follow-ups.** "How would you decide whether to adopt it for your team?"

**Evaluation criteria (1–5).** 1: "yes, it's in the JDK docs so it's fine." 3: correctly identifies it as a preview feature. 5: correct preview status plus a reasoned adoption framework.

**Related references.** [§ Version status](#structured-concurrency) (front-matter callout); [§ Trade-offs](#trade-offs).

## Summary

Structured concurrency ties concurrent subtask lifetimes to a lexical scope, the same guarantee single-threaded code gets for free from the call stack — measured directly as real, automatic cancellation of a sibling subtask (~116ms elapsed against a 5-second budget) the moment another subtask fails under `ShutdownOnFailure`. The identical fan-out shape built with plain `CompletableFuture` instead leaves the sibling running to its real, full completion (~2 real seconds) with zero automatic relationship to the failure — the real, measured resource-leak cost structured concurrency exists to eliminate. `StructuredTaskScope` remains a preview API in JDK 21 (JEP 453), requiring an explicit adoption decision.

## Key Takeaways

- Structured concurrency binds subtask lifetime to a lexical scope — the scope cannot exit while any forked subtask is still running.
- `ShutdownOnFailure` really, measurably interrupts sibling subtasks on one subtask's failure — not a documentation claim, verified directly.
- Plain `CompletableFuture` fan-out has no such automatic cancellation — a real, measured orphaned-task resource leak on partial failure.
- `StructuredTaskScope` is a JDK 21 preview API (JEP 453) — production adoption requires `--enable-preview` and an explicit team decision.

## Cheat Sheet

| Symptom | Likely cause | Fix |
|---|---|---|
| Downstream calls/threads stay active after an unrelated fan-out branch already failed and returned to the client | `CompletableFuture` fan-out with no cancellation relationship between branches | Migrate to `StructuredTaskScope.ShutdownOnFailure` (with an explicit preview-API adoption decision) |
| `join()` returns but a subtask's exception is never observed | Forgot `throwIfFailed()` | Always call `throwIfFailed()` (or inspect `exception()`) after `join()` under `ShutdownOnFailure` |
| Need "first result wins" among several redundant calls | Racing pattern | `StructuredTaskScope.ShutdownOnSuccess` |

## Flashcards

### Card: What "structured" actually guarantees

**Prompt:**
What does "structured" mean in structured concurrency?

**Answer:**
Subtask lifetime is bound to a lexical scope — the scope cannot exit while any forked subtask is still running, and a failure policy can automatically cancel siblings.

**Why it matters:**
The real, enforced guarantee `CompletableFuture` doesn't provide.

**Common trap:**
Treating it as a stylistic API difference rather than an enforced lifetime guarantee.

**Related:**
[Core Concepts](#core-concepts)

### Card: The orphaned-task cost

**Prompt:**
Does `CompletableFuture` automatically cancel sibling tasks when one fails?

**Answer:**
No — measured directly: a sibling ran its full real ~2-second duration despite an unrelated branch failing at ~100ms.

**Why it matters:**
The real, quantified problem structured concurrency exists to solve.

**Common trap:**
Assuming `CompletableFuture.allOf()` provides cancellation-on-failure semantics.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: Version status

**Prompt:**
Is `StructuredTaskScope` a stable JDK 21 API?

**Answer:**
No — it's a preview API (JEP 453, second preview in JDK 21), requiring `--enable-preview`.

**Why it matters:**
Production adoption is a deliberate decision, not a default.

**Common trap:**
Assuming any documented JDK API is automatically production-stable.

**Related:**
[Version status callout](#structured-concurrency)

## Practice Exercises

1. Reproduce every trace yourself: [`practice/java/concurrency/structured-concurrency/`](../../practice/java/concurrency/structured-concurrency/README.md).
2. Modify `BasicForkJoinDemo` to use `ShutdownOnSuccess` instead, forking two subtasks with different delays, and confirm the scope returns as soon as the *faster* one completes rather than waiting for both.
3. In `FailFastCancellationDemo`, remove the `try`/`catch` around the sibling's loop (so `InterruptedException` propagates uncaught) and confirm the measured elapsed time and cancellation behavior are unchanged — explain why.

## Solutions

**Exercise 1.** Expected output matches this chapter's measured traces in structure (exact millisecond values will vary run to run, but the qualitative pattern — concurrent execution, real automatic cancellation, real orphaned-task cost — will not).

**Exercise 2.** `ShutdownOnSuccess` shuts the scope down (cancelling the still-running sibling) the moment any one subtask succeeds — with two differently-delayed subtasks, the scope's `join()` returns close to the *faster* subtask's delay, and `result()` (its equivalent of `throwIfFailed()`/`get()`) returns that faster subtask's value.

**Exercise 3.** The behavior is unchanged because the subtask's `Callable` is permitted to throw `InterruptedException` directly (declared by `Callable.call()`) — `StructuredTaskScope` treats an interrupted, exception-throwing subtask as a failed subtask either way; the `try`/`catch` in the original code exists only to set the diagnostic flag for this chapter's own evidence capture, not to change the real cancellation behavior itself.

## Additional Reading

- [Virtual Threads](virtual-threads.md) — `StructuredTaskScope` forks each subtask onto its own virtual thread, making this chapter's real measurements cheap even under many concurrent subtasks.
- [ForkJoinPool and Work-Stealing](forkjoinpool-and-work-stealing.md) — real, direct proof that virtual threads' carrier scheduler is a separate `ForkJoinPool` instance from `commonPool()`, not the pool shared by parallel streams and `CompletableFuture`.

## Official References

- [JEP 453: Structured Concurrency (Second Preview)](https://openjdk.org/jeps/453)
- [StructuredTaskScope (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html)
