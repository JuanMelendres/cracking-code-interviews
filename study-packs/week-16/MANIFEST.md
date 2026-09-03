---
title: "Week 16 Study Pack — Manifest"
week: 16
plan: B
last_reviewed: 2026-07-31
---

# Week 16 Study Pack — Manifest

**Topics:** T-304, T-307, T-301, T-312, T-308 · **Plan:** B, JVM Internals Depth (Phase 4/5 — second JVM week; closes the five next-highest-IWI gaps in a domain that had only 1 of 12 register topics covered before this week — see `00-project/coverage-audit-2026-07-31.md`)
**Files:** 12 (+ this manifest) · **Total words:** 7,560 (real count, `wc -w` over all 12 files)
**Canonical chapters:** 5 new `handbook/jvm/` chapters, 17,674 words total (real count, `wc -w`), written full-depth from the start — this week did not need a separate slimming pass.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 688 |
| 2 | `01-g1-remembered-sets-and-write-barriers.md` | T-304 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md` | 556 |
| 3 | `02-memory-leak-diagnosis-and-heap-dump-analysis.md` | T-307 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md` | 575 |
| 4 | `03-jvm-memory-layout-and-runtime-regions.md` | T-301 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md` | 554 |
| 5 | `04-jvm-flags-and-container-ergonomics.md` | T-312 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md` | 557 |
| 6 | `05-jit-tiered-compilation-and-deoptimization.md` | T-308 — summary + link; full chapter canonical at `syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md` | 575 |
| 7 | `06-hands-on-lab.md` | 6 labs, all real and reproducible | 671 |
| 8 | `07-flashcards.md` | 15 cards | 1,051 |
| 9 | `08-week-16-mock-interview.md` | 45-min JVM internals technical round | 846 |
| 10 | `09-design-exercise-jvm-sizing-and-diagnostics-playbook.md` | Full sizing/diagnostics playbook, worked reference solution | 1,015 |
| 11 | `10-week-16-checklist.md` | Day-by-day checklist | 249 |
| 12 | `resources.md` | Sources classified PRIMARY/SECONDARY/TOOL | 223 |

---

## Verification

| Item | Status |
|---|---|
| Java — G1 remembered sets | **Executed.** OpenJDK 21.0.12, `-Xlog:gc+phases=debug`. Real low-cross-region-write run: 4 pauses, Dirty Cards Sum 13. Real high-cross-region-write run (identical iteration order of magnitude): 32 pauses, Dirty Cards Sum 23,938 (~1,841x the low run) — measured, not asserted. Source: `practice/java/week-16/g1-remembered-sets/` |
| Java — Memory leak diagnosis | **Executed.** Real `jmap -histo:live` samples on a running leaky process: 32,701 then 67,167 live `Session` instances across two samples. Real fixed-version samples: 0 live `Session` instances at both sample points. Real `jcmd ... GC.heap_dump` produced a genuine 201,098,489-byte `.hprof` file, magic header verified (`JAVA PROFILE 1.0`) then deleted (not committed — `.hprof` added to `.gitignore`). Source: `practice/java/week-16/memory-leak-diagnosis/` |
| Java — Memory layout | **Executed.** Real `OutOfMemoryError: Metaspace` after 5,275 dynamically-generated classes (`-Xmx512m -XX:MaxMetaspaceSize=32m`), heap usage 18MB at OOM. Real `StackOverflowError` depth scaling: 1,479 / 19,988 / 413,005 at `-Xss` 256k / 1m / 8m, heap fixed at `-Xmx512m` throughout. Real NMT (`-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary`) output captured showing Java Heap / Class / Thread / Code / GC as separately-accounted regions. Source: `practice/java/week-16/memory-layout/` |
| Java + Docker — Container ergonomics | **Executed.** Docker 29.6.2, `eclipse-temurin:21-jre`. Real `-Xlog:gc+init` output: "CPUs: 10 total, 2 available" and "CPUs: 10 total, 6 available" for `--cpus=2`/`--cpus=6` respectively, same 10-core host. Real `MaxRAMPercentage` scaling on a fixed `--memory=1g` container: 247MB heap at the default 25.0, 742MB at 75.0. Source: `practice/java/week-16/container-ergonomics/` |
| Java — JIT tiered compilation and deoptimization | **Executed.** Real steady-state measurement: ~330 ns/op (`-Xint`) vs. ~34 ns/op (default tiered) vs. ~34.5 ns/op (`-XX:TieredStopAtLevel=1`, C1 only). Real `-XX:+PrintCompilation` output showing level 3 → level 4 tiering for a hot method. Real genuine deoptimization: `DeoptDemo::sumAreas`'s C2 (level 4) compilation marked "made not entrant" at the exact point a second concrete type was introduced at a previously-monomorphic call site; real measured cost: 2.62ms (first mixed-type call, deopt just occurred) vs. 1.27ms (identical call re-run after recompilation). Source: `practice/java/week-16/jit-compilation/` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

None. This is new-domain-depth content (JVM had only 1 of 12 register topics covered), not a correction to existing material.

## Scope note

This week covers 5 of the remaining 11 uncovered JVM register topics (T-304, T-307, T-301, T-312, T-308), selected by IWI descending among what wasn't already covered by Week 9's GC-fundamentals chapter (T-306). The remaining 6 (T-302 object layout/compressed oops, T-305 ZGC/Shenandoah, T-309 escape analysis, T-310 safepoints, T-311 native memory/direct buffers, and T-303's classic-generational-collector framing distinct from Week 9's G1-flavored treatment) are deferred — stated explicitly, not silently dropped, per `00-project/coverage-audit-2026-07-31.md`'s own next-actions list.

## A note on real evidence and file cleanup

Two artifacts produced during verification were deliberately not committed: a 201MB `.hprof` heap dump (documented in the memory-leak-diagnosis chapter with its real size and verified magic header, then deleted) and several `*.log`/`*.class` build/GC-log artifacts under each `practice/java/week-16/*/out/` directory (already covered by the repository's existing `.gitignore` rules for `*.log` and `*.class`; `*.hprof` was added to `.gitignore` this week for the same reason). Every number cited in the canonical chapters and this manifest was captured directly from these real runs before cleanup, not estimated.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real Docker 29.6.2 containers, real `jmap`/`jcmd` invocations against live processes). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
