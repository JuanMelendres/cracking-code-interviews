---
title: "Java Version Features Timeline: Java 8 Through 25"
slug: java-version-features-timeline
document_type: syllabus-topic
domain: 02-java
topic_id: T-2211
status: draft
version: 1.2
last_updated: 2026-09-17
mastery_levels_covered: [L1, L2, L3]
prerequisites:
  - java-platform-basics-jvm-jdk-jre-and-primitive-types.md
  - java-syntax-fundamentals-variables-control-flow-and-methods.md
related:
  - lambdas-and-functional-interfaces.md
  - streams-and-collectors.md
  - optional-and-null-strategy.md
  - records-sealed-types-and-pattern-matching.md
  - ../concurrency/foreign-function-and-memory-api.md
  - ../concurrency/virtual-threads.md
  - ../concurrency/structured-concurrency.md
  - ../concurrency/scoped-values-and-threadlocal-migration.md
practice: ../../../practice/java/oop-fundamentals/java-version-features/
production_scenarios: []
interview_paths: [junior-to-mid, mid-to-senior, interview-emergency-sprint]
official_references:
  - https://openjdk.org/projects/jdk/
  - https://docs.oracle.com/en/java/javase/21/language/java-language-changes.html
  - https://openjdk.org/jeps/0
  - https://openjdk.org/projects/jdk/25/jeps-since-jdk-21
---

# Java Version Features Timeline: Java 8 Through 25

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

"What's new in Java 17?" and "have you used records / virtual threads / pattern matching?" are near-universal questions once a candidate claims any Java experience beyond a bootcamp — and this repository's own version-specific features were, before this chapter, scattered across a dozen deep-dive chapters (`lambdas-and-functional-interfaces.md`, `records-sealed-types-and-pattern-matching.md`, `virtual-threads.md`, and others) with no single place answering "which version introduced what, and is it actually safe to use in production yet." This chapter is that single reference — it does not re-teach any feature in depth (each one's own canonical chapter already does that, per this project's no-duplication rule); it gives the accurate timeline and links onward.

## 2. Prerequisites

[Java Platform Basics](java-platform-basics-jvm-jdk-jre-and-primitive-types.md), [Java Syntax Fundamentals](java-syntax-fundamentals-variables-control-flow-and-methods.md), and [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md).

## 3. Foundation (L1)

Java has shipped a new major release **every six months** since Java 10 (March 2018) — a deliberate change from the old multi-year release cadence, specifically so individual features could ship as soon as they were ready instead of waiting years for a bundled "big" release. Not every release is meant for production use, though: Oracle designates certain releases as **LTS (Long-Term Support)** — Java 8, 11, 17, 21, and 25 among them — which receive extended vendor support and are the releases most production codebases actually run. Non-LTS releases (9, 10, 12, 13, 14, 15, 16, 18, 19, 20, 22, 23, 24) exist mainly to preview and iterate on upcoming features before they land, finalized, in the next LTS.

A feature's status matters as much as its existence: **preview** features (marked with `--enable-preview` required to compile and run) are real, working, and specified, but not yet guaranteed stable across future releases — using one in production code is a deliberate, informed risk, not a default choice. A feature is **final** once it ships without the preview flag requirement, at which point its behavior and API are considered stable.

### The five LTS releases, one at a time, in plain words

**Java 8 (2014) — the functional-programming LTS.** Before Java 8, "pass a small piece of behavior as a value" meant writing a whole anonymous inner class — several lines of ceremony to express something as simple as "compare these two things" or "run this on each element." Java 8 added **lambda expressions**: a short, inline way to write that same small piece of behavior directly, as a value. On top of lambdas, Java 8 added the **Stream API** — a declarative way to say "take this collection, keep only the elements matching a condition, transform each one, then collect the results," instead of writing the equivalent loop by hand with a manual accumulator variable. It also added **`Optional`** — an explicit, typed container that says "this might have nothing in it," making the possibility of absence visible in a method's signature instead of a silent, undocumented `null` a caller has to already know to check for. Java 8 is still, by a wide margin, the single most widely deployed LTS release in the industry — extremely mature, and still a perfectly reasonable production default for a codebase that hasn't yet needed a newer LTS's specific features. *Real example: Section 7, Example 1.*

**Java 11 (2018) — the cleanup-and-packaging LTS.** Java 11 is deliberately smaller than Java 8 or Java 17 — because the new six-month cadence (which started with Java 9 and 10, both non-LTS) let features ship incrementally as they became ready, instead of accumulating for years behind one release the way everything before Java 9 had to. Java 11's own headline moves: the **`java.net.http` HTTP Client** (a real, modern, non-blocking-capable HTTP client finally built into the JDK itself, replacing the old, awkward `HttpURLConnection`) was finalized; `var` (introduced as local-variable type inference in Java 10) was extended to **lambda parameters**, mainly useful when a parameter needs an annotation that a bare, untyped lambda parameter can't carry; a handful of small but genuinely useful **`String` methods** were added (`isBlank()`, `strip()`, `lines()`) filling real, everyday gaps the class had carried since Java 1.0; and Java 11 also **removed** several modules that used to ship bundled with the JDK by default (JavaFX, CORBA, Java EE modules like JAXB) — a real, sometimes-surprising migration cost for code that had been relying on them being present without an explicit dependency. *Real example: Section 7, Example 2.*

**Java 17 (2021) — the sealed-types LTS.** Java 17's real headline is **sealed classes and interfaces**, finalized: a way to declare a type whose complete, closed set of permitted subtypes is known and enforced by the compiler at compile time (`sealed interface Shape permits Circle, Square {}`) — something Java simply couldn't express precisely before, only approximate with a `final` class hierarchy and hope. Java 17 also turned on **strong encapsulation of internal JDK APIs by default** (`sun.*` and similar internal packages are no longer reflectively accessible without an explicit module-system opt-in) — a real, sometimes-breaking change for old libraries that had been reaching into JDK internals. *Real example: Section 7, Example 4, and see [Records, Sealed Types, and Pattern Matching](records-sealed-types-and-pattern-matching.md) for the full depth on sealed types.*

**Java 21 (2023) — the virtual-threads LTS.** Java 21's real headline is **virtual threads**, finalized: lightweight, JVM-managed threads that let a thread-per-request or thread-per-task style of code scale to hundreds of thousands of concurrent tasks without exhausting OS thread resources, because many virtual threads share a small pool of real OS ("platform") threads underneath, unmounting whenever they block. Java 21 also finalized **pattern matching for `switch`** — including *exhaustive* matching over a sealed type, where the compiler can prove every case is covered and no `default` branch is even required — plus **record patterns** (destructuring a record directly in a pattern, e.g. `case Point(int x, int y) ->`) and **sequenced collections** (a real, unified `getFirst()`/`getLast()`/`reversed()` contract across `List`, `Deque`, and `LinkedHashSet`, finally giving them a shared, well-defined notion of "first" and "last"). *Real example: Section 7, Examples 4 and 5, and see [Virtual Threads](../concurrency/virtual-threads.md) for the full depth on how virtual threads actually schedule.*

**Java 25 (2025) — the structured-concurrency-context LTS.** Java 25's real headline, per the [official JDK 25 JEP list](https://openjdk.org/projects/jdk/25/jeps-since-jdk-21) (verified directly against OpenJDK's own project page, not inferred): **Scoped Values** (JEP 506) finalized — a safer, immutable alternative to `ThreadLocal` for sharing context (like a request ID) down a call stack, specifically designed to work correctly with virtual threads and structured concurrency, where a mutable `ThreadLocal` can leak or get copied incorrectly across thousands of short-lived virtual threads. Also finalized: **Flexible Constructor Bodies** (JEP 513) — statements are now allowed *before* an explicit `this(...)`/`super(...)` call in a constructor, as long as those statements don't touch the instance being constructed (e.g., validating constructor arguments before delegating); **Module Import Declarations** (JEP 511) — `import module java.base;` imports every package a module exports in one line, instead of one `import` per package; **Compact Source Files and Instance Main Methods** (JEP 512) — a real, simplified `void main()` entry point for small programs and scripts, no `public static`, no `String[] args`, no surrounding class required; and **Compact Object Headers** (JEP 519) — a real, opt-in JVM change shrinking every object's header from 96–128 bits down to 64 bits on 64-bit platforms, reducing heap footprint measurably for object-heavy workloads. **Structured Concurrency** (JEP 505) is explicitly *still in preview* as of Java 25 (its fifth preview round) — not finalized yet, despite being closely related to Scoped Values and frequently mentioned alongside it. *No local example here: this environment has OpenJDK 21 only, no JDK 25 installed, so unlike every other version above, this paragraph's claims are sourced directly from the official JEP index rather than a locally compiled demo — see Section 9 for this chapter's own standing accuracy discipline.*

## 4. Core Concepts (L2)

**A compact summary table of the five LTS releases** (Section 3 above is the deep, plain-words version of each row):

| Version | Released | Headline final features |
|---|---|---|
| **Java 8** | 2014 | Lambda expressions, the Stream API, `Optional`, default/static interface methods, the new `java.time` date/time API |
| **Java 11** | 2018 | `var` in lambda parameters, the `java.net.http` HTTP Client finalized, several `String` convenience methods, several bundled modules removed |
| **Java 17** | 2021 | Sealed classes/interfaces finalized, strong encapsulation of internal JDK APIs by default |
| **Java 21** | 2023 | Virtual threads finalized, exhaustive pattern matching for `switch` finalized, record patterns finalized, sequenced collections |
| **Java 25** | 2025 | Scoped values finalized (JEP 506), flexible constructor bodies finalized (JEP 513), module import declarations finalized (JEP 511), compact source files/instance main methods finalized (JEP 512), compact object headers finalized (JEP 519) — structured concurrency (JEP 505) still preview |

**The full release-by-release picture, including non-LTS releases** (each one's headline *final* feature — a non-LTS release's own preview-only features are covered by the LTS row where they finalize, not repeated here):

| Version | Released | Headline final features |
|---|---|---|
| 9 | 2017 | The Java Platform Module System (`module-info.java`, JPMS), `jshell` (the REPL), private interface methods |
| 10 | 2018 | Local-variable type inference (`var`) |
| 11 | 2018 | *(LTS — see table above)* |
| 12 | 2019 | Small `String` methods (`indent`, `transform`); switch expressions still preview |
| 13 | 2019 | Text blocks (`"""`) as preview; switch expressions preview continues |
| 14 | 2020 | Switch expressions finalized (JEP 361); records and pattern matching for `instanceof` both preview for the first time; helpful `NullPointerException` messages finalized |
| 15 | 2020 | Text blocks finalized (JEP 378); sealed classes preview begins |
| 16 | 2021 | Records finalized (JEP 395); pattern matching for `instanceof` finalized (JEP 394) |
| 17 | 2021 | *(LTS — see table above)* |
| 18 | 2022 | UTF-8 as the default `Charset` (JEP 400) — a real, silent-failure-avoiding portability fix; simple web server (`jwebserver`) |
| 19 | 2022 | Virtual threads preview begins (JEP 425); structured concurrency preview begins (JEP 428); record patterns preview begins |
| 20 | 2023 | Scoped values preview begins; virtual threads/structured concurrency second preview round |
| 21 | 2023 | *(LTS — see table above)* |
| 22 | 2024 | Unnamed variables and patterns (`_`) finalized; the Foreign Function & Memory API finalized (JEP 454) — see [Foreign Function and Memory API](../concurrency/foreign-function-and-memory-api.md) |
| 23 | 2024 | Primitive types in patterns/`instanceof`/`switch` preview; markdown documentation comments (JEP 467) |
| 24 | 2024 | Stream gatherers finalized (JEP 485); quantum-resistant cryptography algorithms (ML-KEM, ML-DSA) |
| 25 | 2025 | *(LTS — see table above; Section 9 accuracy caveat applies most strongly to this and the 22–24 rows above)* |

**Records** (finalized Java 16, JEP 395) and **pattern matching** (rolled out across several releases — `instanceof` in Java 16, `switch` in Java 21) are, together, Java's most significant syntax evolution since lambdas — they let code express "what shape is this data, and what do I do with each shape" directly, instead of manually writing constructors/`equals`/`hashCode`/`toString` and a chain of `instanceof`-and-cast checks.

**Virtual threads** (finalized Java 21, JEP 444) are lightweight, JVM-managed threads — you can create hundreds of thousands of them without exhausting OS resources, because many virtual threads share a small pool of real OS ("platform") threads, unmounting from the carrier thread whenever they block. This chapter's own demo runs 1,000 concurrent virtual threads in Section 7; a full production treatment of scheduling, pinning, and when NOT to use them is [Virtual Threads](../concurrency/virtual-threads.md)'s own job.

## 5. How It Works Internally (L3)

Records generate their `equals()`, `hashCode()`, `toString()`, and per-component accessor methods at **compile time**, from the record's declared components — there is no runtime reflection involved in ordinary use, and the generated `equals()`/`hashCode()` pair is always internally consistent by construction (satisfying exactly the contract [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) covers in depth). A record's "compact constructor" (a constructor with no parameter list, just validation/normalization logic) runs *before* field assignment, which is why this chapter's own demo can reject invalid coordinates before a `Point` is ever fully constructed.

Pattern matching for `switch` over a `sealed` hierarchy lets the compiler perform genuine **exhaustiveness checking**: because `Shape` in this chapter's demo `permits` only `Circle` and `Square`, the compiler can prove those two branches cover every possible case and does not require a `default` branch — a real compile-time guarantee, not a runtime convention, and one that breaks loudly (a real compile error) the moment someone adds a third permitted subtype without also updating every exhaustive `switch` over it.

## 6. Practical Usage

Default to whatever your team's actual production JDK is — usually the most recent LTS release, since that's what gets the longest vendor support window. Adopt a *preview* feature in production code only with an explicit, team-wide decision to accept the risk that its API could still change before finalization — never by accident, because `--enable-preview` is not something you'd add without noticing. Reach for records the moment you're about to write a class whose only job is holding a few related values with real equality semantics — this is exactly the shape records exist to replace. Reach for sealed types plus exhaustive `switch` pattern matching when you have a genuinely closed, small set of variants (a payment method, a shape, a parsed token type) and want the compiler to catch a missed case at compile time rather than a bug discovered in production.

## 7. Examples

Every claim below is verified against a real, compiled, executed program at [`practice/java/oop-fundamentals/java-version-features/`](../../../practice/java/oop-fundamentals/java-version-features/), run on OpenJDK 21.0.12 — 15/15 assertions pass.

```java
// Example 1 — Java 8 (final): a lambda expression, the Stream API's declarative
// filter/map/collect, and Optional's explicit "might be absent" container.
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
add.apply(3, 4); // 7 — a lambda expression IS a value, assignable and callable directly

List<String> names = List.of("Ada", "Bob", "Cy", "Diana", "Ed");
List<String> longNamesUpper = names.stream()
        .filter(n -> n.length() > 2)   // keep Ada, Bob, Diana — Cy and Ed are too short
        .map(String::toUpperCase)      // transform each survivor
        .collect(Collectors.toList()); // [ADA, BOB, DIANA] — declarative, no manual loop

Optional<String> maybeName = names.stream().filter(n -> n.startsWith("Z")).findFirst();
maybeName.orElse("no match"); // "no match" — the absence is explicit, typed, and handled
```

```java
// Example 2 — Java 11 (final): `var` in a lambda parameter (JEP 323, useful mainly
// when the parameter needs an annotation), and two small but real String methods.
BiFunction<Integer, Integer, Integer> multiply = (var a, var b) -> a * b;
multiply.apply(3, 4); // 12 — behaves identically to an untyped lambda parameter

"   \n  ".isBlank();           // true  — whitespace-only counts as blank
"first\nsecond\nthird".lines().count(); // 3 — a real line-splitting stream
```

```java
// Example 3 — Java 16 (final): records get real, compiler-generated equals/hashCode/accessors,
// plus a real compact constructor that can enforce validation.
record Point(int x, int y) {
    Point {
        if (x < 0 || y < 0) throw new IllegalArgumentException("coordinates must be non-negative");
    }
}

Point p1 = new Point(3, 4);
Point p2 = new Point(3, 4);
p1.equals(p2); // true — real, correct, component-based equality
p1.x();        // 3   — a real, compiler-generated accessor
```

```java
// Example 4 — Java 17 (final) sealed types + Java 21 (final) exhaustive switch pattern matching —
// no `default` branch, and the compiler PROVES this is exhaustive.
sealed interface Shape permits Circle, Square {}
record Circle(double radius) implements Shape {}
record Square(double side) implements Shape {}

static double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square s -> s.side() * s.side();
    };
}
```

```java
// Example 5 — Java 21 (final): 1,000 real virtual threads, completed concurrently.
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int t = 0; t < 1000; t++) {
        executor.submit(() -> completed.incrementAndGet());
    }
} // all 1,000 tasks awaited and completed before this block exits
```

## 8. Common Mistakes

- **Claiming a preview feature is "in Java X" without noting it required `--enable-preview`** — an interviewer who knows the real JEP history will immediately notice the imprecision, and it is exactly the kind of overclaim this chapter's own Section 9 accuracy caveat exists to prevent.
- **Assuming the newest feature is always the right choice** — records are excellent for simple data carriers, but a class with real behavioral invariants beyond validation still deserves a full class, not a record forced to fit.
- **Confusing "finalized in version X" with "safe to use in a codebase still targeting an older version"** — a feature finalized in Java 21 cannot be used at all if your project's `--release` or `pom.xml` source/target level is still 17.
- **Treating LTS as the only real releases** — non-LTS releases run in production too; LTS just means longer vendor support, not that intermediate releases are somehow unstable by default (individual *preview features* within them are the actual risk, not the release itself).

## 9. Edge Cases

**An accuracy note on Java 25, updated 2026-09-17:** the Java 25 paragraph in Section 3 and its JEP numbers (506, 513, 511, 512, 519, and 505's still-preview status) were verified directly against the [official JDK 25 JEP index](https://openjdk.org/projects/jdk/25/jeps-since-jdk-21) while writing this update — not inferred or guessed. What remains genuinely unverifiable in this environment: none of this chapter's other examples (Section 7) include a Java 25 code sample, because this environment has OpenJDK 21 installed, not JDK 25 — every other example in this chapter is real, compiled, and executed; the Java 25 paragraph alone is sourced from the official spec rather than a local compile. Per this repository's own Modern Java Version Policy (see `CLAUDE.md`), re-verify against the official OpenJDK JEP index (linked in Official References) before asserting Java 25+ feature status in an interview or relying on it in production code, since any release past the one this chapter last checked is the single most likely place for a Java-timeline reference to drift out of date.

Structured concurrency and scoped values are two features worth naming specifically as a caution: both went through multiple preview rounds across several releases before finalizing (scoped values reached final in Java 25; structured concurrency, per the same verified JDK 25 JEP index, was still in its *fifth* preview round as of Java 25, not yet final), and their exact API shape changed between some of those preview rounds — a detail that trips up anyone who learned an earlier preview's syntax and assumes it's still current.

## 10. Performance Implications

Virtual threads specifically change a real, measurable production constraint: a thread-per-request architecture that was previously limited by OS thread overhead (a few thousand platform threads before resource exhaustion becomes a real risk) can scale to hundreds of thousands of concurrent virtual threads instead, without rewriting the application to a reactive/async model — directly relevant to any Spring MVC application considering [Spring WebFlux and Reactive Programming](../../05-spring/spring-webflux-and-reactive-programming.md) purely for thread-scaling reasons rather than genuine backpressure needs.

## 11. Trade-offs

| Choice | When it helps | When it hurts |
|---|---|---|
| Adopt the latest LTS promptly | Longest support window, earliest access to new features | Migration cost, dependency compatibility lag |
| Stay on an older LTS | Stability, proven in production | Missing genuine productivity/performance gains (records, virtual threads) |
| Use a preview feature in production | Early access to a nearly-final feature | Risk of API changes before finalization |

## 12. Senior-Level Considerations (L3)

A Senior engineer evaluating a JDK upgrade should distinguish between *language* features (records, pattern matching, sealed types — purely compile-time, zero runtime JDK dependency once compiled) and *platform/runtime* features (virtual threads, generational garbage collectors) — the former can sometimes be backported via a build tool's `--release` flag against an older runtime, the latter genuinely require running on the newer JVM itself.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, JDK version strategy is an organizational risk-and-velocity decision: standardizing on the current LTS org-wide reduces the surface area of "which JDK does this service run on" incidents and lets the org capture real, measured gains (e.g., virtual threads' throughput improvement under high-concurrency I/O-bound workloads) uniformly, but requires a real, budgeted migration process — dependency compatibility testing, a rollback plan, and a defined timeline — not an ad hoc, service-by-service drift that eventually leaves some services years behind and effectively unsupported.

## 14. Production Scenarios

No dedicated `production-cookbook/` entry exists yet for a JDK-version-migration-specific incident.

> Planned reference: a dedicated `production-cookbook/` entry for a real incident caused by adopting a preview feature in production code, then having its API shape change in the next release during an otherwise-routine JDK upgrade, would be a natural, non-duplicative addition to that deliverable's own backlog.

## 15. Interview Questions

### Question 1: What are the major features introduced across Java 8, 11, 17, and 21?

**Expected answer:** Java 8 — lambdas, streams, `Optional`, default/static interface methods. Java 11 — `var` in lambdas, HTTP Client finalized, single-file launching. Java 17 — sealed classes finalized. Java 21 — virtual threads finalized, pattern matching for `switch` finalized, record patterns finalized.

**Common mistakes:** attributing records or pattern matching to Java 8 (a very common confusion, since both are now heavily used but are actually Java 16/21 features respectively); not knowing which releases are LTS.

**Follow-up questions:** "which of these were preview features before they finalized, and in which release did they first appear as preview?"

**Senior-level expectations:** correctly distinguishes preview from final status for at least 2–3 features.

**Staff-level expectations:** frames the answer around organizational JDK-upgrade strategy, not just a feature list.

### Question 2: What problem do virtual threads solve, and what do they NOT solve?

**Expected answer:** they solve the OS-thread-count ceiling for thread-per-request/task architectures under high concurrency, without requiring a rewrite to reactive/async code. They do NOT make CPU-bound work faster — virtual threads help I/O-bound, blocking-heavy workloads specifically, since the benefit comes from unmounting a blocked virtual thread from its carrier thread, freeing that carrier for other work.

**Minimum acceptable answer:** knows virtual threads are lightweight and JVM-managed, not OS threads.

**Strong Senior answer:** correctly states the I/O-bound-specific benefit and can name at least one real pitfall (e.g., thread pinning inside a `synchronized` block, which prevents unmounting).

**Staff-level extension:** discusses whether adopting virtual threads changes an organization's need for a separate reactive stack, and under what real conditions it does or doesn't.

**Common mistakes:** claiming virtual threads make CPU-bound code faster (they don't — CPU-bound work is still limited by real available cores).

**Likely follow-ups:** "what happens if a virtual thread executes a `synchronized` block?" (It can "pin" to its carrier thread for the block's duration, temporarily losing the lightweight-unmounting benefit — a real, documented caveat.)

**Evaluation criteria:** correctly scopes the benefit to I/O-bound concurrency, not general performance.

## 16. Coding/Practice Exercises

1. Write a record representing a `Money` value (amount, currency) with a compact constructor that rejects a negative amount, mirroring this chapter's `Point` validation pattern.
2. Write a sealed interface with three permitted record implementations and an exhaustive `switch` expression over it — then add a fourth permitted type and observe the real compile error every non-updated exhaustive `switch` now produces.

## 17. Debugging Exercises

Given a build failure citing a preview-feature compiler error (e.g., attempting to use a feature without `--enable-preview` on a JDK version where it's still preview), diagnose the real cause and the two possible fixes (add the flag, accept the risk; or upgrade to the release where it's final).

## 18. Design Exercises

Design a small "which JDK version should we target" decision document for a hypothetical team currently on Java 11, considering: what production features they'd gain (records, pattern matching, virtual threads), what migration risk they'd take on, and whether an intermediate step (Java 17 first, then 21 later) is more defensible than jumping directly — a real, Staff-level organizational trade-off, not a purely technical one.

## 19. Further Reading

- [Lambdas & Functional Interfaces](lambdas-and-functional-interfaces.md), [Streams & Collectors](streams-and-collectors.md), [Optional and Null Strategy](optional-and-null-strategy.md) — Java 8's own deep-dive chapters.
- [Records, Sealed Types, and Pattern Matching](records-sealed-types-and-pattern-matching.md) — the Java 14–21 language-evolution deep dive this chapter's timeline points to.
- [Virtual Threads](../concurrency/virtual-threads.md), [Structured Concurrency](../concurrency/structured-concurrency.md), [Scoped Values and ThreadLocal Migration](../concurrency/scoped-values-and-threadlocal-migration.md) — real depth on Java 21+'s concurrency features, both paths confirmed to exist on disk.

## 20. Mastery Checklist

- [ ] Can correctly attribute lambdas/streams/Optional to Java 8, records to Java 16, sealed classes to Java 17, and virtual threads/pattern-matching-for-switch to Java 21.
- [ ] Can explain the difference between a preview feature and a final feature.
- [ ] Can explain what problem virtual threads solve and what they do not (Question 2).
- [ ] Reproduced this chapter's real demo and confirmed the same 15/15 assertions pass.
- [ ] Can explain, in plain words, at least one headline feature from each of the five LTS releases (Section 3), not just the two or three most commonly cited ones.
- [ ] Knows to verify the most recent 1–2 releases' exact feature status before citing them confidently (Section 9).
