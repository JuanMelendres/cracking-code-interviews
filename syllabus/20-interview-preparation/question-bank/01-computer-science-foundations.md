---
title: "Interview Question Bank — 01-computer-science-foundations"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../01-computer-science-foundations/INDEX.md
  - 03-data-structures-algorithms.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Computer Science Foundations

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 5 chapters yielded 10 deep questions = **10 real
questions**. All 5 chapters use the older numbered `## 15. Interview Questions`
template with **no Flashcards section at all**, so there is no quick-fire layer to
mine here — the same pattern as [`19-leadership-staff.md`](19-leadership-staff.md).
Despite being the domain's own "true fundamentals" starting point, the Interview
Questions sections themselves are not written as a separate leveled Junior/Mid
format; Junior/Mid below is honestly derived from each question's own "Minimum
acceptable answer" and "Common mistakes" fields.

---

## Algorithmic Complexity and Big-O, From First Principles

### Q1 — What's the Big-O of this code, and why?

**Canonical treatment:** [§15, Q1](../../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Answers `O(n)` and stops, without checking what `.contains()` costs inside the loop — the common mistake this question targets.
- **Senior:** Gets to `O(n × m)` unprompted and immediately proposes the fix — converting `otherList` to a `HashSet` before the loop, making the whole thing `O(n + m)`.
- **Staff:** Connects this to review discipline — this exact pattern is common and expensive enough to warrant a static-analysis rule or code-review checklist item, rather than relying on every reviewer catching it by inspection.

### Q2 — Is `O(n log n)` always faster than `O(n²)`?

**Canonical treatment:** [§15, Q2](../../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats Big-O as a total, context-free speed ordering ("O(n log n) is just better") — the common mistake this question targets.
- **Senior:** States "not always," names the specific real-world example (hybrid sort algorithms using insertion sort below a size threshold), and explains why insertion sort's constant factor is smaller at small sizes.
- **Staff:** Generalizes the principle — choosing a "better" algorithm purely by asymptotic class without checking the actual, realistic size of `n` for this specific system is a decision made on incomplete information.

---

## How a Computer Executes a Program

### Q1 — What's the difference between JVM bytecode and machine code?

**Canonical treatment:** [§15, Q1](../../01-computer-science-foundations/how-a-computer-executes-a-program.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Says "Java compiles to machine code" flatly, with no bytecode step at all — the common mistake this question targets.
- **Senior:** Names the interpreter-then-JIT progression specifically and can point to concrete evidence for it (early calls to a method run measurably slower than later ones).
- **Staff:** Connects this to why it's a deliberate design trade-off — giving up "compiles once to the fastest code" for "write once, run anywhere" plus JIT specialization using run-time-only information.

### Q2 — Why does a program crash with `StackOverflowError` instead of just running out of memory gradually, the way a memory leak does?

**Canonical treatment:** [§15, Q2](../../01-computer-science-foundations/how-a-computer-executes-a-program.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes `StackOverflowError` as "running out of memory" without distinguishing which memory region — the common mistake this question targets, conflating it with `OutOfMemoryError`.
- **Senior:** States that this is measurable and predictable, and can describe how to confirm it (run the recursive path under a debugger or with depth instrumentation).
- **Staff:** Names the resource-model trade-off — raising `-Xss` has a real cost multiplied across every thread in a large pool, so the correct fix for adversarially deep recursion is usually bounding or restructuring the recursion itself.

---

## Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer

### Q1 — Walk me through what happens, at the network level, between a client sending an HTTP request and receiving a response.

**Canonical treatment:** [§15, Q1](../../01-computer-science-foundations/networking-basics.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Skips straight to "the controller method runs," treating everything below the framework's own entry point as invisible or irrelevant — the common mistake this question targets.
- **Senior:** Explicitly separates connection establishment cost from request processing cost, and names connection reuse (keep-alive/pooling) as the standard mitigation for the former.
- **Staff:** Connects this to a real cross-system resource-accounting failure — connection pools at every layer are finite, shared resources whose exhaustion in one code path can be caused by a seemingly unrelated one.

### Q2 — Why does TCP need a three-way handshake instead of just sending data immediately?

**Canonical treatment:** [§15, Q2](../../01-computer-science-foundations/networking-basics.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the handshake as pure formality or overhead with no functional purpose — the common mistake this question targets.
- **Senior:** Connects the handshake's cost directly to a real design decision — why keep-alive and connection pooling exist specifically to avoid paying this cost repeatedly.
- **Staff:** Names a real scenario where handshake cost at scale becomes an actual system constraint — a fresh outbound HTTP call per request under high volume, motivating a connection-pooled client.

---

## Number Representation

### Q1 — Why does `0.1 + 0.2 == 0.3` evaluate to `false` in Java?

**Canonical treatment:** [§15, Q1](../../01-computer-science-foundations/number-representation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Attributes this to "Java's floating point being buggy" rather than recognizing it as inherent to IEEE 754 — the common mistake this question targets.
- **Senior:** Explains the binary-fraction mechanism directly and names the correct fix depending on domain — an epsilon-tolerant comparison generally, `BigDecimal` for money or exact-decimal requirements.
- **Staff:** Cites a concrete real-world consequence at scale (the Patriot missile case) and connects it to the general principle that a representation choice validated for one range or duration isn't automatically safe for a different one.

### Q2 — What happens when you add 1 to `Integer.MAX_VALUE` in Java?

**Canonical treatment:** [§15, Q2](../../01-computer-science-foundations/number-representation.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes Java automatically promotes `int` arithmetic to `long` on overflow, or that an exception is thrown by default — the common mistake this question targets; neither is true.
- **Senior:** States the exact resulting value (`Integer.MIN_VALUE`) and can explain the two's-complement bit-pattern mechanism producing it.
- **Staff:** Connects this to Ariane 5 as the canonical real-world stakes example, and names `Math.addExact`/`long`/`BigInteger` as concrete standard-library mitigations.

---

## The OS Process/Thread Model, Below Java's Abstraction of It

### Q1 — What's the difference between a process and a thread?

**Canonical treatment:** [§15, Q1](../../01-computer-science-foundations/os-process-thread-model.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes a thread as "a lightweight process" without explaining what specifically is shared (memory) versus private (stack, registers) — the common mistake this question targets.
- **Senior:** Explains why this matters practically — threads communicate cheaply through shared memory but need explicit synchronization; processes are naturally isolated but communicate more expensively.
- **Staff:** Extends this to a real architectural decision — why a system might run multiple single-threaded worker processes rather than one multi-threaded process, trading cheap communication for stronger fault isolation.

### Q2 — Why do virtual threads scale to far more concurrent tasks than platform threads?

**Canonical treatment:** [§15, Q2](../../01-computer-science-foundations/os-process-thread-model.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Describes virtual threads as "not real threads" without the more precise mechanism — the common mistake this question targets; they are real `Thread` objects with real, if usually brief, mounted execution.
- **Senior:** Names the mount/unmount mechanism specifically and can cite or reproduce a real measurement rather than repeating the claim unverified.
- **Staff:** Names the pinning edge case as the mechanism's real, documented limitation, and connects it to the organizational risk of a large-scale virtual-thread migration — not a free, unconditional win.

---

## Related

- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`06-databases.md`](06-databases.md)
- [`19-leadership-staff.md`](19-leadership-staff.md)
- [`18-engineering-practices.md`](18-engineering-practices.md)
- [`17-architecture.md`](17-architecture.md)
- [`16-performance-jvm.md`](16-performance-jvm.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
