---
title: "Performance & JVM Tuning — Domain Index"
document_type: syllabus-domain-index
domain: 16-performance-jvm
status: 3 of 3 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Performance & JVM Tuning

"How do I make the JVM fast" — GC tuning, JMH benchmarking pitfalls, JFR profiling, and capacity planning. Consolidates chapters previously split across `jvm/` and `performance/`.

> **Phase 3 update (2026-09-03).** This domain's full existing content (3 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 3 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject: a traffic-helicopter-photographing-a-highway analogy for sampling profilers and flame-graph width (profiling); a timing-a-sprinter analogy for JIT warmup, dead-code elimination, and the `Blackhole` mechanism (benchmarking); a call-center-hold-time analogy for Little's Law and the saturation cliff (capacity planning). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`16-performance-jvm` is now fully L1–L4 (3/3)** — the fourteenth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1202 | Profiling: async-profiler, JFR, and Flame Graphs | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md` |
| T-1203 | Benchmarking & JMH Pitfalls | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md` |
| T-1208 | Capacity Planning & Headroom | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/16-performance-jvm/capacity-planning-and-headroom.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
