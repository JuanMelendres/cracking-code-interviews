---
title: "Java — Domain Index"
document_type: syllabus-domain-index
domain: 02-java
status: 49 of 49 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit in progress (Phase 5) — 24 of 49 done (collections + language-core subdomains complete, 2026-09-04)
last_updated: 2026-09-04
---

# Java

The primary specialization and deepest track: language core, collections, JVM internals, and concurrency. All 49 chapters have physically relocated here across four subdomains, each originally written at Senior/Staff depth (L3/L4) only — Foundation (L1) and Working-Knowledge (L2) layers are additive retrofit work tracked per Section 7.6, now underway. (`benchmarking-and-jmh-pitfalls.md`, previously in `handbook/jvm/`, belongs to `16-performance-jvm` instead and was not moved here — it awaits that domain's own Phase 3 turn.)

> **Phase 3 update (2026-09-03).** This domain's full existing content has physically relocated via `git mv` from `handbook/{java-core,collections,jvm,concurrency}/`, preserving file history. 235 files across the rest of the repository (`cheat-sheets/`, `flashcards/`, `production-cookbook/`, other `handbook/` chapters, `study-packs/`, `architecture-atlas/`, `practice/`) had their cross-references to these 49 chapters updated to the new paths; a full repository-wide link-resolution pass confirms zero references broken by this move. See the repository-root `CHANGELOG.md` for the full account, including a discovered class of pre-existing, unrelated broken links (in `behavioral-handbook/`, `AGENTS.md`/`CLAUDE.md`, and `production-cookbook/`) that predate this migration and were not introduced by it.
>
> **Phase 5 update (2026-09-04).** The `02-java/collections` subdomain (T-201–T-209, 9 chapters) and the `02-java/language-core` subdomain (T-101–T-115, 15 chapters) have both now received the plan's own additive L1/L2 retrofit (§2.4): each chapter gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between the existing "Why This Matters in Interviews" and "Mental Model" sections, per the plan's own stated placement. No existing sentence was deleted, reworded, or moved — every retrofit was a pure insertion, verified by diff. Each chapter also gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]` front-matter fields (additive; existing fields untouched). `jvm-internals` and `concurrency` (25 chapters) still carry L3/L4 only and are next in this retrofit, subdomain by subdomain.

## Topics

| Topic ID | Title | New subdomain | Mastery levels covered today | Current location |
|---|---|---|---|---|
| T-201 | HashMap Internals | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/hashmap-internals.md` |
| T-202 | ArrayList and LinkedList Internals | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/arraylist-and-linkedlist-internals.md` |
| T-203 | TreeMap/TreeSet & the Navigable Hierarchy | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md` |
| T-204 | ArrayDeque Internals and the Legacy Stack/Vector Problem | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md` |
| T-205 | ConcurrentHashMap Internals | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/concurrenthashmap-internals.md` |
| T-206 | CopyOnWriteArrayList and Copy-on-Write Trade-offs | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md` |
| T-207 | BlockingQueue Family and Producer-Consumer | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/blockingqueue-family.md` |
| T-208 | Fail-Fast vs. Weakly-Consistent Iterators | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md` |
| T-209 | Collection Selection Decision Matrix | `02-java/collections` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/collections/collection-selection-decision-matrix.md` |
| T-401/T-402 | Java Memory Model and volatile | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/java-memory-model-and-volatile.md` |
| T-404 | ReentrantLock, ReadWriteLock, and StampedLock | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md` |
| T-405 | Atomics, CAS, and the ABA Problem | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md` |
| T-406 | Executors and Thread Pool Sizing | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md` |
| T-407 | CompletableFuture and Async Composition | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/completablefuture-and-async-composition.md` |
| T-408 | ForkJoinPool and Work-Stealing | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md` |
| T-409 | Deadlock, Race Conditions, and Thread Diagnostics | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md` |
| T-410 | Virtual Threads (Project Loom) | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/virtual-threads.md` |
| T-411 | Structured Concurrency | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/structured-concurrency.md` |
| T-412 | Scoped Values and ThreadLocal Migration | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md` |
| T-413 | ThreadLocal-Mediated Classloader Leaks | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md` |
| T-415 | VarHandles, Unsafe, and Their Replacement | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/varhandles-and-unsafe.md` |
| T-416/T-414 | Foreign Function & Memory API | `02-java/concurrency` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/concurrency/foreign-function-and-memory-api.md` |
| T-302 | Object Layout, Headers, and Compressed Oops | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md` |
| T-303 | GC Roots, Reachability, and Reference Strength | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md` |
| T-303/T-306/T-306/T-303 | GC Fundamentals and Log Analysis | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md` |
| T-305 | ZGC and Shenandoah: Concurrent Collection | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md` |
| T-309 | Escape Analysis and Scalar Replacement | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md` |
| T-310 | Safepoints and Stop-the-World Mechanics | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md` |
| T-311 | Native Memory, Direct Buffers, and Off-Heap | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md` |
| — | G1 Internals: Remembered Sets and Write Barriers | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md` |
| — | JIT: Tiered Compilation, Inlining, and Deoptimization | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md` |
| — | JVM Flags and Container Ergonomics | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md` |
| — | JVM Memory Layout and Runtime Regions | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md` |
| — | Memory Leak Diagnosis and Heap Dump Analysis | `02-java/jvm-internals` | L3, L4 (existing Senior/Staff-depth content; L1/L2 Foundation/Working-Knowledge layers pending Phase 5) | `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md` |
| T-101 | equals(), hashCode(), and Comparable Contracts | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md` |
| T-102 | Polymorphism and Dynamic Dispatch Mechanics | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/polymorphism-and-dynamic-dispatch.md` |
| T-103 | Immutability and Defensive Copying | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/immutability-and-defensive-copying.md` |
| T-104 | Generics: Erasure, Variance, and PECS | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/generics-erasure-and-pecs.md` |
| T-105 | Exception Design and Hierarchy Strategy | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md` |
| T-106 | Strings: Interning, Compact Strings, and Builders | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md` |
| T-107 | Streams and Collectors | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/streams-and-collectors.md` |
| T-108 | Lambdas and Functional Interfaces | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/lambdas-and-functional-interfaces.md` |
| T-109 | Optional and Null Strategy | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/optional-and-null-strategy.md` |
| T-110 | Records, Sealed Types, and Pattern Matching | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/records-sealed-types-and-pattern-matching.md` |
| T-111 | Enums, EnumMap, and EnumSet | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/enums-enummap-and-enumset.md` |
| T-112 | Annotations and Annotation Processing | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/annotations-and-annotation-processing.md` |
| T-113 | Reflection and Dynamic Proxies | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/reflection-and-dynamic-proxies.md` |
| T-114 | ClassLoaders and Class Initialization | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/classloaders-and-class-initialization.md` |
| T-115 | Serialization Hazards and Alternatives | `02-java/language-core` | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/02-java/language-core/serialization-hazards-and-alternatives.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
