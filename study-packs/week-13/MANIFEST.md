---
title: "Week 13 Study Pack — Manifest"
week: 13
plan: B
last_reviewed: 2026-07-31
---

# Week 13 Study Pack — Manifest

**Topics:** T-107, T-101, T-104, T-105, T-103 · **Plan:** B, Java Core (Phase 4/5 — first Java Core week; domain previously had zero study-pack coverage)
**Files:** 12 (+ this manifest) · **Total words:** 7,012 (real count, `wc -w` over all 12 files; checked 2026-07-31 for slimming — this week was already authored lean, post-dating the study-pack slimming convention, so only trivial prose tightening in `08`/`09`/README applied; 01–05 already summary+link, `06`/`07`/`10`/`resources.md` had no cuttable fat)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 570 |
| 2 | `01-streams-and-collectors.md` | T-107 — summary + link; full chapter canonical at `handbook/java-core/streams-and-collectors.md` | 545 |
| 3 | `02-equals-hashcode-and-comparable-contracts.md` | T-101 — summary + link; full chapter canonical at `handbook/java-core/equals-hashcode-and-comparable-contracts.md` | 575 |
| 4 | `03-generics-erasure-and-pecs.md` | T-104 — summary + link; full chapter canonical at `handbook/java-core/generics-erasure-and-pecs.md` | 616 |
| 5 | `04-exception-design-and-hierarchy-strategy.md` | T-105 — summary + link; full chapter canonical at `handbook/java-core/exception-design-and-hierarchy-strategy.md` | 587 |
| 6 | `05-immutability-and-defensive-copying.md` | T-103 — summary + link; full chapter canonical at `handbook/java-core/immutability-and-defensive-copying.md` | 545 |
| 7 | `06-java-coding-practice.md` | 4 problems, all compiled and run, 12/12 assertions pass | 404 |
| 8 | `07-flashcards.md` | 15 cards | 943 |
| 9 | `08-week-13-mock-interview.md` | 45-min Java Core technical round | 914 |
| 10 | `09-code-review-exercise.md` | 7 defects across 5 topics in one class, with worked solution | 776 |
| 11 | `10-week-13-checklist.md` | Day-by-day checklist | 280 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK | 257 |

---

## Verification

| Item | Status |
|---|---|
| Java — Streams and Collectors | **Executed.** OpenJDK 21.0.12. Real laziness/short-circuiting trace, real `toMap()` duplicate-key `IllegalStateException`, real `ArrayList` corruption under `parallel().forEach()` (100,000 expected, ~24,000 actual — exact count varies run to run), real warmed-up parallel-vs-sequential benchmark (20,000 warmup + 20,000 measured iterations: sequential 3,111 ns/iter vs. parallel 20,539 ns/iter). Source: `practice/java/week-13/streams-collectors/` |
| Java — Equality Contracts | **Executed.** Real `HashSet` failing to detect a duplicate when `hashCode()` isn't overridden (size 2 instead of 1), real `TreeSet` silently dropping a distinct product when `compareTo()` disagrees with `equals()`. Source: `practice/java/week-13/equality-contracts/` |
| Java — Generics Erasure and PECS | **Executed.** Real `getClass()` equality proving erasure, real `ClassCastException` at read time (not insert time) from an unchecked cast, real reflection-verified bridge method, real PECS application and compile-time-rejected violation. Source: `practice/java/week-13/generics-erasure/` |
| Java — Exception Design | **Executed.** Real `getCause() == null` for an unchained wrapper vs. a real full `Caused by:` chain for a chained one, real try-with-resources `getSuppressed()` behavior vs. real complete exception loss under a manual `finally` block. Source: `practice/java/week-13/exception-design/` |
| Java — Immutability | **Executed.** Real mutation of a "final-fields-only" class's state via both a constructor-stored live `Date` reference and a getter-returned live `List` reference; real `UnsupportedOperationException` from the fixed, `List.copyOf()`-based version. Source: `practice/java/week-13/immutability/` |
| Java — Coding practice | **Executed.** `12/12` assertions pass across 4 problems, each exercising this week's topics together rather than in isolation. Source: `practice/java/week-13/mixed-review/` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

None. This is new-domain content (Java Core had zero prior coverage), not a correction to existing material.

## Scope note

This week covers the 5 highest-weighted-IWI Java Core topics (T-107, T-101, T-104, T-105, T-103) out of 16 total in the D1 register (`00-project/knowledge-architecture-blueprint.md` §D1). The remaining 11 (T-102 Polymorphism, T-106 Strings, T-108 Lambdas, T-109 Optional, T-110 Records/sealed/pattern matching, T-111 Enums, T-112 Annotations, T-113 Reflection, T-114 ClassLoaders, T-115 Serialization, T-116 JPMS) are deferred — stated explicitly, not silently dropped — since they carry lower IWI and, in several cases (T-112, T-114, T-115, T-116), Rare/Occasional interview frequency per the blueprint's own Freq column.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
