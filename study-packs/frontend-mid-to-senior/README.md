---
title: "Frontend Mid → Senior Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-08
---

# Frontend Mid → Senior Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/frontend-mid-to-senior.md`](../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md) — that path is the *content* (20 topics, in sequence, each with a stated reason for its place in the order); this is the *schedule* built on top of it, the same relationship [`study-packs/mid-to-senior/`](../mid-to-senior/README.md) has to its own Java backend learning path.

## Audience and scope

A working frontend or full-stack engineer who can already ship a React/Next.js feature — [Frontend Junior → Mid](../../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md) is assumed solid, not retaught. This pack builds Next.js internals depth (rendering strategies, the App Router's caching model, the edge runtime), React internals (fiber, concurrent rendering, performance), and the production judgment a Senior frontend loop tests. It targets Advanced tier throughout, per the learning path's own stated goal, closing with an Expert-tier capstone in Week 10.

**No duplicated content.** Every week below links to its topics' real, canonical `syllabus/21-frontend-web/` chapters and, per this repository's own no-duplication rule, to the real `practice/frontend/` (and, for Week 10, `practice/java/`) demos each chapter's own "Real Verified Demos" section already cites — this pack adds scheduling, sequencing rationale, and lightweight review checkpoints on top of material that already exists in full.

## Why this exists

[Mid → Senior](../mid-to-senior/README.md) gave the Java backend domain an operational, week-by-week layer of its own. The frontend domain's [Frontend Mid → Senior](../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md) learning path had no scheduled program of its own until now — this pack fills that gap the same way `study-packs/mid-to-senior/` did for the backend path. See [`study-packs/README.md`](../README.md) for how the other programs in this repository relate (note: that index predates this pack and is out of scope for this task to update).

## Weeks

| Week | Theme | Topics | Estimated hours |
|---|---|---|---|
| [01](week-01/README.md) | The Server/Client boundary and how data actually gets fetched | Server Components vs. Client Components (F-203); Data Fetching in the App Router (F-204) | 6–8h |
| [02](week-02/README.md) | Choosing a rendering strategy, and where BFF logic lives | Rendering Strategies: SSR/SSG/ISR (F-205); Route Handlers (F-207) | 6–8h |
| [03](week-03/README.md) | Production concerns: discoverability and real performance metrics | Metadata API and SEO (F-209); Image/Font Optimization and Core Web Vitals (F-210) | 6–8h |
| [04](week-04/README.md) | Component design patterns, then what's actually happening underneath | React Component Patterns (F-111); React Reconciliation and the Fiber Architecture (F-112) | 6–8h |
| [05](week-05/README.md) | Concurrent rendering and applying internals to real performance problems | Concurrent React (F-113); React Performance (F-117) | 6–8h |
| [06](week-06/README.md) | Testing habits and typing React correctly | React Testing (F-118); TypeScript with React (F-119) | 6–8h |
| [07](week-07/README.md) | The state-management decision framework, and streaming the App Router | React State Management Landscape (F-120); Streaming and Suspense Boundaries (F-206) | 6–8h |
| [08](week-08/README.md) | Edge-level request logic and real authentication | Proxy/Middleware and the Edge Runtime (F-208); Authentication Patterns (F-211) | 6–8h |
| [09](week-09/README.md) | Mutations without an API layer, and real deployment trade-offs | Server Actions and Mutations (F-212); Deployment Models (F-213) | 6–8h |
| [10](week-10/README.md) | Where frontend and backend code live together, and the Expert-tier capstone | Monorepo and Full-Stack Repo Layout (F-303); Full-Stack Integration with a Java/Spring Backend (F-214) | 8–10h |

**Total: ~62–78 hours across 10 weeks**, matching the learning path's own ~8–10-week, 6–8h/week estimate (Week 10 runs slightly longer given F-214's Expert-tier capstone status).

Week 10 is deliberately the heaviest week: F-214 is this path's closing, Expert/Staff-equivalent topic, and it is the one chapter that most directly serves a full-stack Java+React developer — the reader this whole repository is built for. Unlike every other week in this pack, Week 10 carries a real System Design Exercise, for that reason specifically.

## After this pack

There is no further pack in the frontend ladder — Frontend Mid → Senior is the top of this domain's sequence (the closing topic already reaches Expert/L4-equivalent depth, per `syllabus/21-frontend-web/INDEX.md`'s Phase 5 mastery-equivalence mapping). Return to [`syllabus/00-overview/learning-paths/`](../../syllabus/00-overview/learning-paths/) for the full set of paths.

**For a full-stack reader:** pair this pack with the Java backend [`study-packs/senior-to-staff/`](../senior-to-staff/README.md) pack for genuine dual-track Senior/Staff depth — JVM/Spring/database internals and organizational judgment on one side, React fiber internals/Next.js rendering strategies/full-stack integration on the other, per `CLAUDE.md`'s Scope Addendum framing the two domains as additive, not merged.
