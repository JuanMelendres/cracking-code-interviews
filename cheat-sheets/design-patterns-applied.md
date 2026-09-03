---
title: "Cheat Sheet: Design Patterns Applied"
slug: design-patterns-applied
document_type: cheat-sheet
domain: architecture
topic_id: T-914
canonical: ../syllabus/04-software-design/design-patterns-applied.md
last_updated: 2026-09-02
---

# Design Patterns Applied (GoF in Production)

**Canonical chapter:** [`syllabus/04-software-design/design-patterns-applied.md`](../syllabus/04-software-design/design-patterns-applied.md)

## Core Mental Model

Every pattern in this chapter exists to answer one specific, recurring "what varies, and how do I isolate it?" question. Strategy isolates which algorithm runs. Builder isolates how a complex object gets assembled from what it ultimately is. Decorator isolates which optional behaviors are layered on, avoiding a combinatorial subclass explosion. Singleton isolates how many instances exist, at the cost of global mutable state and testability. None are "best practices" to sprinkle everywhere — each answers a specific kind of variation, and using one where that variation doesn't exist just adds indirection for nothing.

## Essential Definitions

- **Strategy** — a family of interchangeable algorithms behind one common interface; the client never branches on which concrete algorithm it holds.
- **Builder** — separates construction of an object with many optional fields (and cross-field invariants) into its own fluent object, avoiding telescoping constructors and half-initialized mutable state.
- **Decorator** — attaches additional behavior by wrapping an object in another implementing the same interface, rather than subclassing; `N` independent behaviors need `N` decorator classes instead of up to `2^N` subclasses.
- **Singleton** — guarantees exactly one instance, reachable from a single global access point; the most controversial GoF pattern due to global mutable state and (in a naive implementation) a real thread-safety defect.
- **Recognition-tier patterns** — Observer, Factory Method, Adapter, Template Method: covered by real production example, not full from-scratch implementation.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Behavior varies by "which algorithm runs," with genuinely multiple real implementations | Strategy |
| Object has several optional fields or invariants spanning multiple fields | Builder |
| Several independent optional behaviors need to combine in different ways | Decorator |
| Something genuinely needs exactly one instance, globally | A DI framework's singleton-scoped bean; if unavoidable, an enum-based Singleton |

**What each pattern does NOT solve:**

| Pattern | Solves | Does NOT solve |
|---|---|---|
| Strategy | Swapping which algorithm runs, no branching in the client | Construction complexity (Builder) or behavior layering (Decorator) |
| Builder | Complex, validated, immutable construction | Runtime behavior variation after the object exists (Strategy) |
| Decorator | Composing optional behaviors without a subclass explosion | Guaranteeing a single instance (Singleton) |
| Singleton | Exactly one instance, globally reachable | Thread-safety automatically — a naive implementation is a real, measured bug |

## Key Numbers (real, executed Java 21 on OpenJDK 21.0.12)

- Strategy: same client call `checkout.total(100)` returns 100.0, 90.0, or 85.0 purely based on injected strategy — zero branching inside `Checkout`.
- Decorator: Email + Slack + SMS composed with zero new classes; a fourth composition (SMS + Email, skipping Slack) also needs zero new classes — `new SmsDecorator(new EmailNotifier())`.
- Singleton race (30 threads, `CountDownLatch`-released simultaneously): naive lazy Singleton — 29 distinct instance ids observed, 30 total objects actually constructed (confirmed: more than one instance under real concurrent first access). Enum-based Singleton, identical race — exactly 1 instance constructed every time.

## Common Pitfalls

- Reaching for a pattern because it sounds sophisticated, on a problem with no real variation to isolate.
- Implementing a lazy Singleton with a plain `if (instance == null)` check and no synchronization, assuming single-threaded intuition applies to concurrent first access.
- Using inheritance to combine optional behaviors, discovering the subclass count grows combinatorially as more behaviors are added.
- Treating a `final` field initialized once in a constructor as automatically thread-safe for publication of the object itself — construction and safe publication are related but distinct concerns.

## Interview Answer Skeleton

**30-sec:** Strategy isolates which algorithm runs behind a common interface; Builder isolates complex, validated construction; Decorator layers optional behavior at runtime instead of a subclass explosion; Singleton guarantees exactly one instance, but a naive implementation is not thread-safe — measured directly, an unsynchronized lazy Singleton constructs multiple instances under real concurrent first access.

**2-min:** Add the measured 30-threads-30-objects result for the naive Singleton versus exactly 1 for the enum-based version, and the N-vs-2^N framing for Decorator versus subclassing.

**Whiteboard:** Draw the Decorator sequence: Client → SmsDecorator → SlackDecorator → EmailNotifier, each layer calling `super.send()` before adding its own behavior on the way back out. Separately, sketch two timelines for Singleton: both threads calling `getInstance()`, both reading `instance == null` as true before either finishes constructing — annotate the gap between "check" and "act" as "this is where the race lives."

**Staff-level framing:** The Staff-level signal isn't knowing more pattern names — it's judgment about when *not* to use one. Flag a Strategy with one real implementation, or a Builder on a two-field record, as unnecessary indirection just as readily as a missing pattern handling genuine variation with ad hoc conditionals. Connect the Singleton thread-safety pitfall to the broader discipline: shared mutable state reachable from multiple threads needs an explicit correctness argument, not an assumption that "it probably only runs once."

## Production Warning Signs

- A database connection pool wrapped in a naive lazy Singleton briefly gets constructed twice under a deployment's cold-start traffic burst — metrics show two distinct pool objects constructed within milliseconds, each opening its own full complement of connections, spiking the database's open-connection count beyond the configured limit.
- Fix: replace the hand-rolled lazy Singleton with an enum-based Singleton or, more idiomatically, a framework-managed singleton-scoped bean (e.g., Spring's default bean scope).
- Treat any hand-rolled lazy Singleton in a codebase that already uses a DI framework as a design-review flag by default — the framework almost certainly already solves this correctly.

## Related

- `syllabus/17-architecture/clean-hexagonal-architecture.md`
- `syllabus/02-java/concurrency/java-memory-model-and-volatile.md`
- `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md`
- `syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md`
