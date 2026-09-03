---
title: "Week 19 Study Pack — Manifest"
week: 19
plan: B
last_reviewed: 2026-08-02
---

# Week 19 Study Pack — Manifest

**Topics:** T-303, T-305, T-310, T-302, T-311, T-309 · **Plan:** B, JVM Domain Full Closure (Phase 4/5 — closes JVM from 6/12 to 12/12 register topics, the third domain in the entire register closed to full coverage, after Security in Week 17 and Testing in Week 18 — see `00-project/coverage-audit-2026-07-31.md`)
**Files:** 13 (+ this manifest) · **Total words:** 7,869 (real count, `wc -w` over all 13 files)
**Canonical chapters:** 6 new `handbook/jvm/` chapters, 23,697 words total (real count, `wc -w`), written full-depth from the start — this week did not need a separate slimming pass.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 756 |
| 2 | `01-gc-roots-reachability-and-reference-strength.md` | T-303 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md` | 516 |
| 3 | `02-zgc-and-shenandoah-concurrent-collection.md` | T-305 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md` | 503 |
| 4 | `03-safepoints-and-stop-the-world-mechanics.md` | T-310 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md` | 507 |
| 5 | `04-object-layout-headers-and-compressed-oops.md` | T-302 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md` | 461 |
| 6 | `05-native-memory-direct-buffers-and-off-heap.md` | T-311 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md` | 493 |
| 7 | `06-escape-analysis-and-scalar-replacement.md` | T-309 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md` | 512 |
| 8 | `07-hands-on-lab.md` | 6 labs, all real and reproducible | 609 |
| 9 | `08-flashcards.md` | 18 cards | 1,220 |
| 10 | `09-week-19-mock-interview.md` | 45-min JVM internals technical round | 849 |
| 11 | `10-design-exercise-jvm-tuning-for-a-market-data-service.md` | Full JVM tuning playbook, worked reference solution | 1,016 |
| 12 | `11-week-19-checklist.md` | Day-by-day checklist | 270 |
| 13 | `resources.md` | Sources classified PRIMARY/INTERNAL/TOOL | 157 |

---

## Verification

| Item | Status |
|---|---|
| Java — Reference-strength hierarchy | **Executed.** OpenJDK 21.0.12. Real strong/weak/soft/phantom reference demo: strong survives `System.gc()`; weak cleared immediately once unreferenced; soft survives the identical operation under no memory pressure; phantom `get()` always `null`, enqueued to a `ReferenceQueue` only after collection. Source: `practice/java/week-19/gc-roots-reachability/` |
| Java — ZGC vs. G1 pause times | **Executed.** Identical allocation-churn workload, 256MB heap. G1: 393 pauses, max 0.748ms, 0 stalls, 28.9M allocations completed. ZGC: real `-Xlog:safepoint` evidence, `XMarkStart`/`XMarkEnd`/`XRelocateStart` "At safepoint" range 1,125-40,250ns, 705 total safepoint ops, 218 real "Allocation Stall" events, 22.5M allocations completed (~22% fewer than G1). Shenandoah confirmed real, working sub-ms pause (`Pause Final Update Refs 0.010ms`) on the same environment. Source: `practice/java/week-19/zgc-vs-g1/` |
| Java — Safepoint operation types | **Executed.** Real single run, `-Xlog:safepoint`, an allocation-light workload with no natural GC pressure (confirmed via 2.16-second gap before the first externally-triggered safepoint). `PrintThreads` (`jcmd Thread.print`): 84,083ns at safepoint. `FindDeadlocks` (same jcmd call): 1,083ns. `G1CollectFull` (`jcmd GC.run`): 1,587,416ns — a real ~1,500x cost range across three distinct, real safepoint operations from one run. Source: `practice/java/week-19/safepoints/` |
| Java — Compressed oops memory footprint | **Executed.** 5,000,000 identical `Node` objects (1 reference field, 1 `long` field). `-XX:+UseCompressedOops` (default): 134MB total, ~28 bytes/node. `-XX:-UseCompressedOops`: 191MB total, ~40 bytes/node — a real, measured ~42% footprint difference from the flag alone, no data change. Source: `practice/java/week-19/object-layout/` |
| Java — Direct buffers and NMT | **Executed.** `-Xmx32m -XX:MaxDirectMemorySize=256m`: successfully allocated 256MB direct memory (8x the heap limit) before a real, distinct `OutOfMemoryError: Direct buffer memory` at exactly the configured limit (`Cannot reserve 8388608 bytes... limit: 268435456`). Real NMT evidence (`-Xmx64m`, 10×10MB direct buffers, `jcmd VM.native_memory summary`): `Java Heap` reserved=65536KB (exactly matching `-Xmx64m`), `Other` reserved=102400KB with `#10` mallocs (exactly matching the 10 real buffers) — confirmed entirely separate accounting. Source: `practice/java/week-19/native-memory/` |
| Java — Escape analysis and scalar replacement | **Executed.** Identical 600,000,000-iteration hot loop creating small, provably non-escaping `Point` objects. Default (escape analysis enabled): **0** GC pauses. `-XX:-DoEscapeAnalysis`: **362** real GC pauses. Same source code, same JVM, same iteration count — only the optimization flag differs. Source: `practice/java/week-19/escape-analysis/` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

**Coverage-audit correction (continuing the pattern from Weeks 18/19's earlier discoveries):** `gc-fundamentals-and-log-analysis.md`'s own "Topic register: T-303 / T-306" line credits T-303 coverage, but its actual content (young/mixed/full collection mechanics, GC-log reading, humongous allocations) is G1-implementation-centric — it does not treat GC roots, formal reachability, the reference-strength hierarchy, or the generational hypothesis as topics in their own right. This week's T-303 chapter was scoped explicitly to cover only that missing ground, cross-linked to (not duplicating) the existing chapter, rather than crediting T-303 as already fully closed the way T-1103 (Testing) was credited in Week 18.

## Scope note

This week covers the remaining 6 of the 12 JVM register topics (T-302, T-303, T-305, T-309, T-310, T-311), completing the domain Week 16 started (which covered T-301, T-304, T-306, T-307, T-308, T-312). JVM is now the third domain in the register closed to full coverage, after Security (7/7, Week 17) and Testing (8/8, Week 18).

## A note on real evidence and cleanup

All demos ran real Docker-free, dependency-free Java (OpenJDK 21.0.12, whose Homebrew build includes ZGC and Shenandoah out of the box). No external containers or services were needed this week. All build artifacts (`.class` files, `.log` files) remain under `practice/java/week-19/*/out/`, already covered by the repository's existing `.gitignore` rules. Every number cited in the canonical chapters and this manifest was captured directly from these real runs, not estimated.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, including ZGC/Shenandoah collector flags, `-Xlog:safepoint`/`-Xlog:gc` logging, and real `jcmd VM.native_memory summary` invocations). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
