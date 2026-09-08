---
title: "Study Packs — Index"
document_type: study-pack-index
status: active
last_updated: 2026-09-08
---

# Study Packs

This directory holds three separate, non-overlapping programs. All three schedule the same canonical `syllabus/` content into a week-by-week plan — none of them duplicate chapter content, per this repository's own no-duplication rule — but they exist for different readers with different timelines. **Both `week-01` through `week-25` (below) and `junior-to-mid/week-01` through `week-07` reuse the same "week-NN" numbering inside their own directory** — always go through this index or a learning path's own link rather than guessing from a bare "week-01" reference which program it belongs to.

## Which program is this?

| If you are... | Use... |
|---|---|
| New to backend development entirely (0–2 years, no prior Java/SQL/Spring) | [`junior-to-mid/`](junior-to-mid/README.md) |
| A working engineer (2–5 years) who ships features but needs Senior-level internals depth | [`mid-to-senior/`](mid-to-senior/README.md) |
| On an urgent interview timeline and need the highest-impact material fast, regardless of current level | `week-01/` through `week-25/` (below) |

## Programs

### [`junior-to-mid/`](junior-to-mid/README.md) — Junior → Mid, 7 weeks

Schedules [`syllabus/00-overview/learning-paths/junior-to-mid.md`](../syllabus/00-overview/learning-paths/junior-to-mid.md)'s 22-topic sequence: Java fundamentals, OOP, collections, DSA patterns, SQL, testing, Spring MVC, REST APIs, and Docker. ~48–56 hours total. Built 2026-09-08 using the lean README+MANIFEST convention (no duplicated content).

### [`mid-to-senior/`](mid-to-senior/README.md) — Mid → Senior, 10 weeks

Schedules [`syllabus/00-overview/learning-paths/mid-to-senior.md`](../syllabus/00-overview/learning-paths/mid-to-senior.md)'s 12-domain sequence: concurrency, JVM internals, Spring internals, database internals, testing, Kafka, distributed systems, system design, security/observability, and delivery/architecture. ~76–96 hours total. Built 2026-09-08, same lean convention, each week cross-referenced against real, already-diagnosed `production-cookbook/` incidents.

### [`week-01/`](week-01/README.md) through [`week-25/`](week-25/README.md) — Interview Emergency Sprint

Schedules [`syllabus/00-overview/learning-paths/interview-emergency-sprint.md`](../syllabus/00-overview/learning-paths/interview-emergency-sprint.md)'s two plans: **Plan A** (Weeks 1–6, 20h/week) for an urgent, imminent-interview timeline, and **Plan B** (Weeks 7–25, continuing from Week 6's shared foundation) for broader depth across Spring, security, Kafka, concurrency/JVM, distributed data, resilience, and testing/observability. Predates the `syllabus/` migration and the Junior-to-Mid/Mid-to-Senior split — this is the original, single-reader-profile program this repository started with, still the right choice when time pressure outweighs following the level-appropriate path.

## Continuation

[Senior → Staff](../syllabus/00-overview/learning-paths/senior-to-staff.md) is the next learning path after Mid → Senior; it does not yet have its own weekly study pack (see that path's own file for its topic sequence in the meantime).
