---
title: "Frontend Junior → Mid Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-08
---

# Frontend Junior → Mid Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/frontend-junior-to-mid.md`](../../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md) — that path is the *content* (14 topics, in dependency order, with a stated stop-at tier and a one-line reason for each); this is the *schedule* built on top of it, the same relationship [`study-packs/junior-to-mid/`](../junior-to-mid/README.md) has to the Java backend's own Junior → Mid path.

## Audience and scope

A new engineer (0–2 years, or a backend engineer picking up frontend for the first time) with no prior JavaScript, TypeScript, or web-fundamentals experience — someone who has never built a web page or a React component before, per the [Scope Addendum](../../CLAUDE.md) that established this domain as its own Beginner-through-Staff ladder rather than the backend domain's Senior/Staff-only baseline. This pack stops at Intermediate tier (this domain's own equivalent of L2) for every topic listed — Advanced/Expert depth is [Frontend Mid → Senior](../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md)'s job, not this pack's.

**No duplicated content.** Every week below links to its topics' real, canonical `syllabus/21-frontend-web/` chapters and their real `practice/frontend/` demos — this pack adds scheduling, sequencing rationale, and lightweight review checkpoints on top of material that already exists in full, per this repository's own no-duplication rule.

## Why this exists

The three Junior Fundamentals chapters (F-001–F-003, built 2026-09-08) closed a real gap: this domain's own "Beginner tier" silently assumed JavaScript/TypeScript/web-fundamentals literacy it never actually taught. The learning path sequenced those chapters correctly the same day they were written. This study pack is the natural next step — the same operational structure (schedule, hands-on exercises, review checklist, lightweight mock) the Java backend's [Junior → Mid](../junior-to-mid/README.md) pack already has, built for this domain specifically.

## Weeks

| Week | Theme | Topics | Estimated hours |
|---|---|---|---|
| [01](week-01/README.md) | The web, JavaScript, and TypeScript, from zero | How the Web Works (F-001), JavaScript Fundamentals (F-002), TypeScript Fundamentals (F-003) | 8h |
| [02](week-02/README.md) | The actual floor of React | React Fundamentals: JSX, Components, Props, and State (F-101–F-104) | 8h |
| [03](week-03/README.md) | The hooks every real app needs | React Hooks: useEffect and useRef (F-105/F-106), React Memoization and Context (F-107/F-108), React useReducer and Custom Hooks (F-109/F-110) | 8h |
| [04](week-04/README.md) | From "a React app" to a real, shippable one | Next.js's Role (F-201), Next.js App Router Fundamentals (F-202) | 6–8h |
| [05](week-05/README.md) | What actually happens when you run `npm run dev` | Build Tooling: Vite vs. Turbopack (F-301), Styling Approaches (F-302) | 6–8h |
| [06](week-06/README.md) | Production-shaped UI | React Forms (F-114), React Error Boundaries (F-115), React Accessibility (F-116) | 8h |

**Total: ~44–48 hours across 6 weeks**, within the learning path's own ~6–8 week, 6–8h/week estimate.

## After this pack

[Frontend Mid → Senior](../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md) — the direct continuation into Next.js internals, React performance/testing/TypeScript depth, and the full-stack-integration capstone. Its own study pack, if built, will live at `study-packs/frontend-mid-to-senior/`.
