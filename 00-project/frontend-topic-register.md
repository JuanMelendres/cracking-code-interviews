---
title: Frontend Topic Register — React & Next.js
document_type: project-register
status: draft
version: 1.0
last_updated: 2026-08-12
audience:
  - Full-Stack Developer (Junior through Staff)
---

# Frontend Topic Register — React & Next.js

## Purpose

Companion register to `00-project/knowledge-architecture-blueprint.md`, but for the frontend domain added per the Scope Addendum in `CLAUDE.md` (2026-08-12). Kept as a separate document rather than merged into the Java backend blueprint — the two domains have different depth targets (this one spans beginner through expert; the backend blueprint targets Senior/Staff almost exclusively) and are additive, not unified.

`Tier`: BEG Beginner · INT Intermediate · ADV Advanced · EXP Expert
`Gap`: 🔴 absent (true for every row below — this is a new domain, nothing exists yet)

## D-F1 · React Fundamentals

| ID | Topic | Tier | Notes |
|---|---|---|---|
| F-101 | JSX, elements vs components, the virtual DOM idea | BEG | What actually happens between JSX and a DOM update |
| F-102 | Props, component composition, children | BEG | |
| F-103 | State with `useState`, controlled re-renders | BEG | |
| F-104 | Events, conditional rendering, lists & `key` | BEG | The `key`-as-array-index pitfall belongs here |
| F-105 | `useEffect`: dependency arrays, cleanup, common footguns | INT | Stale closures, missing deps, effect-vs-event confusion |
| F-106 | `useRef`, DOM access, mutable values that don't trigger renders | INT | |
| F-107 | `useMemo` / `useCallback`: what they actually prevent, when they don't help | INT | A real interview favorite — most candidates overuse or misunderstand these |
| F-108 | `useContext` and the Context API | INT | Including its re-render cost at scale |
| F-109 | `useReducer`, when it beats `useState` | INT | |
| F-110 | Custom hooks: design, naming, composition rules | INT | |
| F-111 | Component patterns: composition vs. inheritance, render props, compound components, HOCs | ADV | Why React explicitly favors composition |
| F-112 | Reconciliation & the fiber architecture (how React actually diffs/schedules) | ADV | Internals-depth, mirrors the JVM-internals treatment on the backend side |
| F-113 | Concurrent React: transitions, `useDeferredValue`, `useTransition`, Suspense for data | ADV | |
| F-114 | Forms: controlled vs. uncontrolled, validation strategies, React Hook Form / Zod | INT | |
| F-115 | Error boundaries & error handling strategy | INT | |
| F-116 | Accessibility (a11y): semantic HTML, ARIA, keyboard navigation, focus management | INT | Frequently skipped, frequently interview-relevant for Staff-level frontend |
| F-117 | Performance: profiling with React DevTools, memoization strategy, virtualization for large lists, code-splitting with `React.lazy` | ADV | |
| F-118 | Testing: React Testing Library philosophy (behavior not implementation), Jest/Vitest, mocking, Playwright/Cypress for E2E | ADV | Direct parallel to the backend `test-strategy-and-test-doubles.md` chapter |
| F-119 | TypeScript with React: typing props/state/hooks, generic components, discriminated unions for variant props | ADV | |
| F-120 | State management landscape: Context vs. Redux Toolkit vs. Zustand vs. server state (React Query/TanStack Query) — decision framework, not just syntax | ADV | The "when do you actually need a global store" question |

## D-F2 · Next.js

| ID | Topic | Tier | Notes |
|---|---|---|---|
| F-201 | Next.js's role: file-based routing, why a meta-framework over plain React/Vite | BEG | |
| F-202 | App Router fundamentals: layouts, pages, nested routing, route groups | BEG | |
| F-203 | Server Components vs. Client Components: the actual boundary, `"use client"`, what runs where | INT | The single most-tested modern Next.js concept |
| F-204 | Data fetching in the App Router: `fetch` caching semantics, `revalidate`, `cache: 'no-store'` | INT | |
| F-205 | Rendering strategies: SSR, SSG, ISR — what each means mechanically and when to choose it | INT | Direct analogue to the backend's caching-strategies cheat sheet |
| F-206 | Streaming & Suspense boundaries in the App Router | ADV | |
| F-207 | Route Handlers (API routes): building a backend-for-frontend layer in Next.js itself | INT | Relevant to a Java-backend-plus-Next.js full-stack setup — where does logic live |
| F-208 | Middleware & the Edge runtime | ADV | |
| F-209 | Metadata API, SEO fundamentals for a React app | INT | |
| F-210 | Image/font optimization, Core Web Vitals | INT | |
| F-211 | Authentication patterns: Auth.js/NextAuth, JWT vs. session cookies, protecting Server Components and Route Handlers | ADV | |
| F-212 | Server Actions: mutations without a separate API layer, progressive enhancement | ADV | |
| F-213 | Deployment models: Vercel's platform-native features vs. self-hosting (Docker/Node server) — real trade-offs, not marketing | ADV | |
| F-214 | Full-stack integration with a separate backend (this repo's Java/Spring material): CORS, BFF pattern, where auth/session logic should live when the API is a separate service | EXP | The topic that most directly serves a full-stack Java+React developer specifically |

## D-F3 · Tooling & Ecosystem

| ID | Topic | Tier | Notes |
|---|---|---|---|
| F-301 | Build tooling: Vite vs. Next.js's own compiler/Turbopack, what a bundler actually does | INT | |
| F-302 | Styling approaches: CSS Modules, Tailwind, CSS-in-JS — trade-offs, not just preference | INT | |
| F-303 | Monorepo/full-stack repo layout: where frontend and backend code live together sanely | ADV | Relevant to how this repo itself is structured for a full-stack learner |

## How this register will be used

Mirrors the Java backend blueprint's discipline: each topic gets a canonical chapter in `handbook/frontend/` (or a merged chapter where two IDs are inseparable, exactly as `jpa-entity-lifecycle-and-the-n1-problem.md` grouped T-601/T-602), following the same canonical chapter template, Interview Answer Framework, and real-executed-evidence standard already established for the Java content — real React/Next.js apps actually built and run (via the in-app browser preview tooling), not described.

No chapters exist yet. This register exists so the ~37 topics above are reviewable before any content generation starts, same as the original Java blueprint was reviewed before Phase 4 began.
