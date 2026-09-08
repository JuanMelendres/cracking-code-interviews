---
title: "Study Packs — Index"
document_type: study-pack-index
status: active
last_updated: 2026-09-08
---

# Study Packs

This directory holds eight separate, non-overlapping programs across two domains (Java backend and React/Next.js frontend). All schedule the same canonical `syllabus/` content into a week-by-week (or, for one, a short daily-rotation) plan — none of them duplicate chapter content, per this repository's own no-duplication rule — but they exist for different readers with different timelines. **`week-01` through `week-25` (below), `junior-to-mid/week-01` through `week-08`, `mid-to-senior/week-01` through `week-10`, `senior-to-staff/week-01` through `week-08`, `backend-java-specialization/week-01` through `week-09`, `frontend-junior-to-mid/week-01` through `week-06`, and `frontend-mid-to-senior/week-01` through `week-10` all reuse the same "week-NN" numbering inside their own directory** — always go through this index or a learning path's own link rather than guessing from a bare "week-01" reference which program it belongs to.

## Which program is this?

| If you are... | Use... |
|---|---|
| New to backend development entirely (0–2 years, no prior Java/SQL/Spring) | [`junior-to-mid/`](junior-to-mid/README.md) |
| A working engineer (2–5 years) who ships features but needs Senior-level internals depth | [`mid-to-senior/`](mid-to-senior/README.md) |
| A Senior engineer building the systemic/organizational judgment a Staff loop tests | [`senior-to-staff/`](senior-to-staff/README.md) |
| Focused specifically on deep Java-stack mastery (Java, Spring, DB, messaging, JVM) rather than broader domain breadth | [`backend-java-specialization/`](backend-java-specialization/README.md) |
| Already Senior/Staff-level with depth in place, with an interview loop coming up soon — this is recall, not new learning | [`senior-interview-refresh/`](senior-interview-refresh/README.md) |
| New to frontend/web development entirely (0–2 years, no prior JS/TS/React) | [`frontend-junior-to-mid/`](frontend-junior-to-mid/README.md) |
| A working frontend/full-stack engineer who ships React/Next.js features but needs Senior-level internals depth | [`frontend-mid-to-senior/`](frontend-mid-to-senior/README.md) |
| On an urgent interview timeline and need the highest-impact material fast, regardless of current level | `week-01/` through `week-25/` (below) |

## Programs — Java backend

### [`junior-to-mid/`](junior-to-mid/README.md) — Junior → Mid, 8 weeks

Schedules [`syllabus/00-overview/learning-paths/junior-to-mid.md`](../syllabus/00-overview/learning-paths/junior-to-mid.md)'s 25-topic sequence: Java platform basics, syntax, modifiers, OOP, version-features timeline, CS foundations, collections, DSA patterns, SQL, testing, Spring MVC, REST APIs, and Docker. ~60 hours total. Built 2026-09-08 using the lean README+MANIFEST convention (no duplicated content).

### [`mid-to-senior/`](mid-to-senior/README.md) — Mid → Senior, 10 weeks

Schedules [`syllabus/00-overview/learning-paths/mid-to-senior.md`](../syllabus/00-overview/learning-paths/mid-to-senior.md)'s 12-domain sequence: concurrency, JVM internals, Spring internals, database internals, testing, Kafka, distributed systems, system design, security/observability, and delivery/architecture. ~76–96 hours total. Built 2026-09-08, same lean convention, each week cross-referenced against real, already-diagnosed `production-cookbook/` incidents.

### [`senior-to-staff/`](senior-to-staff/README.md) — Senior → Staff, 8 weeks

Schedules [`syllabus/00-overview/learning-paths/senior-to-staff.md`](../syllabus/00-overview/learning-paths/senior-to-staff.md)'s 16 named topics (2 per week): microservice decomposition, modular monoliths, CQRS, migration patterns, technical debt as an economic decision, ADRs, multi-region/DR, consistent hashing, storage trade-offs, cloud cost, capacity planning, and five Leadership & Staff topics (mentoring, cross-team influence, leading migrations, debt advocacy, design reviews). ~48–64 hours total. Built 2026-09-08; Weeks 6–8 each carry a real Behavioral Exercise tied to the path's own Story Portfolio Design requirement.

### [`backend-java-specialization/`](backend-java-specialization/README.md) — Backend Java Specialization, 9 weeks

Schedules [`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../syllabus/00-overview/learning-paths/backend-java-specialization.md)'s 5-domain, full-L1–L4 depth track: Java (language-core, collections, concurrency, jvm-internals — 4 weeks), Spring, Databases (2 weeks: JPA/Hibernate, then indexing/query planning/ops), Messaging & Event-Driven Systems, and Performance & JVM Tuning. ~77–104 hours total. Built 2026-09-08; for readers who want deep Java-stack mastery specifically rather than the broader domain breadth `mid-to-senior/` covers.

### [`senior-interview-refresh/`](senior-interview-refresh/README.md) — Senior Interview Refresh, 3–5 days

Not a multi-week program — a short, repeatable daily rotation (cheat-sheet sweep → flashcard pass → one explain-it-cold check per domain) for someone who already has the depth and has an interview coming up soon. Schedules [`syllabus/00-overview/learning-paths/senior-interview-refresh.md`](../syllabus/00-overview/learning-paths/senior-interview-refresh.md)'s own rotation against this repository's real `cheat-sheets/`/`flashcards/` contents. Built 2026-09-08; no `week-NN/` subdirectories, just `README.md` + `MANIFEST.md`.

### [`week-01/`](week-01/README.md) through [`week-25/`](week-25/README.md) — Interview Emergency Sprint

Schedules [`syllabus/00-overview/learning-paths/interview-emergency-sprint.md`](../syllabus/00-overview/learning-paths/interview-emergency-sprint.md)'s two plans: **Plan A** (Weeks 1–6, 20h/week) for an urgent, imminent-interview timeline, and **Plan B** (Weeks 7–25, continuing from Week 6's shared foundation) for broader depth across Spring, security, Kafka, concurrency/JVM, distributed data, resilience, and testing/observability. Predates the `syllabus/` migration and the level-specific paths above — this is the original, single-reader-profile program this repository started with, still the right choice when time pressure outweighs following the level-appropriate path.

## Programs — Frontend (React/Next.js)

### [`frontend-junior-to-mid/`](frontend-junior-to-mid/README.md) — Frontend Junior → Mid, 6 weeks

Schedules [`syllabus/00-overview/learning-paths/frontend-junior-to-mid.md`](../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md)'s 14-topic sequence: web/JavaScript/TypeScript fundamentals, core React (JSX, state, hooks), Next.js basics, build tooling, styling, and production-shaped UI (forms, error boundaries, accessibility). ~44–48 hours total. Built 2026-09-08, same lean convention as the backend packs.

### [`frontend-mid-to-senior/`](frontend-mid-to-senior/README.md) — Frontend Mid → Senior, 10 weeks

Schedules [`syllabus/00-overview/learning-paths/frontend-mid-to-senior.md`](../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md)'s 20-topic sequence: Next.js internals (Server/Client boundary, rendering strategies, streaming, the edge runtime), React internals (fiber, concurrent rendering, performance, testing), and a closing Expert-tier capstone (Full-Stack Integration with a Java/Spring backend) with a real System Design Exercise. ~62–78 hours total. Built 2026-09-08; the only pack in this repository whose closing week cites real, verified evidence from two separately-running processes (a Next.js app and a Spring Boot backend) proving a real CORS failure, its fix, and the BFF pattern's full credential chain.

## All backend and frontend learning paths now have an operational layer

As of 2026-09-08, every `syllabus/00-overview/learning-paths/*.md` file — all five Java backend paths and both frontend paths — has a matching program above. The pattern is consistent end to end across both domains, not partial.
