---
title: "Performance & JVM Tuning — Domain Index"
document_type: syllabus-domain-index
domain: 16-performance-jvm
status: 3 of 3 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 4th chapter added 2026-09-18 (JVM Startup Performance: CDS, AppCDS, and GraalVM Native Image, T-2416), closing a gap-audit finding zero coverage of JVM startup/cold-start cost anywhere in the domain; 5th-7th chapters added 2026-09-20 (False Sharing and Cache-Line Contention T-2417, Vector API and SIMD Performance T-2418, Memory-Mapped Files and Zero-Copy I/O T-2419), closing a follow-up gap audit
last_updated: 2026-09-20
---

# Performance & JVM Tuning

"How do I make the JVM fast" — GC tuning, JMH benchmarking pitfalls, JFR profiling, capacity planning, JVM/native-image startup performance, mechanical sympathy (false sharing, SIMD), and zero-copy I/O. Consolidates chapters previously split across `jvm/` and `performance/`.

> **Phase 3 update (2026-09-03).** This domain's full existing content (3 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 3 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject: a traffic-helicopter-photographing-a-highway analogy for sampling profilers and flame-graph width (profiling); a timing-a-sprinter analogy for JIT warmup, dead-code elimination, and the `Blackhole` mechanism (benchmarking); a call-center-hold-time analogy for Little's Law and the saturation cliff (capacity planning). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`16-performance-jvm` is now fully L1–L4 (3/3)** — the fourteenth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1202 | Profiling: async-profiler, JFR, and Flame Graphs | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md` |
| T-1203 | Benchmarking & JMH Pitfalls | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md` |
| T-1208 | Capacity Planning & Headroom | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/capacity-planning-and-headroom.md` |
| T-2416 | JVM Startup Performance: CDS, AppCDS, and GraalVM Native Image | L1, L2, L3, L4 — fully written, real AppCDS + GraalVM native-image demo (2026-09-18) | `syllabus/16-performance-jvm/jvm-startup-performance-cds-and-native-image.md` |
| T-2417 | False Sharing and Cache-Line Contention | L1, L2, L3, L4 — fully written, real ~16x measured demo (2026-09-20) | `syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md` |
| T-2418 | Vector API and SIMD Performance | L1, L2, L3, L4 — fully written, real Vector API demo (2026-09-20) | `syllabus/16-performance-jvm/vector-api-and-simd-performance.md` |
| T-2419 | Memory-Mapped Files and Zero-Copy I/O | L1, L2, L3, L4 — fully written, real ~28x measured demo (2026-09-20) | `syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
