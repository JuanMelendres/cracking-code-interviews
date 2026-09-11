---
title: Frontend Topic Register — React & Next.js
document_type: project-register
status: re-audited 2026-09-08 — all 37 originally-registered topics confirmed to have a real, fully-written chapter in `syllabus/21-frontend-web/` (cross-checked against its own `INDEX.md`, not assumed); Gap column below now reflects reality instead of the 2026-08-12 "nothing exists yet" placeholder. 3 new topics (F-001–F-003, a "D-F0 · Web & Language Fundamentals" section) added the same day to close a real floor gap — see `syllabus/00-overview/changelog.md`'s matching entry.
version: 1.1
last_updated: 2026-09-08
audience:
  - Full-Stack Developer (Junior through Staff)
---

# Frontend Topic Register — React & Next.js

## Purpose

Companion register to `00-project/knowledge-architecture-blueprint.md`, but for the frontend domain added per the Scope Addendum in `CLAUDE.md` (2026-08-12). Kept as a separate document rather than merged into the Java backend blueprint — the two domains have different depth targets (this one spans beginner through expert; the backend blueprint targets Senior/Staff almost exclusively) and are additive, not unified.

`Tier`: BEG Beginner · INT Intermediate · ADV Advanced · EXP Expert
`Gap`: 🔴 absent · 🟡 partial · 🟢 adequate (a real, fully-written chapter exists) — same symbol convention as `00-project/knowledge-architecture-blueprint.md`'s own legend

## D-F0 · Web & Language Fundamentals

**Added 2026-09-08.** Every topic below (D-F1 onward) was written assuming the reader already knows plain JavaScript, basic HTML/CSS/HTTP, and — for F-119 specifically — plain TypeScript. Nothing in the original 37-topic register actually taught any of that. This is the exact same "assumes the basics" pattern found and fixed on the Java backend side earlier this session (Java Syntax Fundamentals, Collections Usage Fundamentals, SQL Fundamentals, etc.) — found here during the 2026-09-08 re-audit and closed the same way: real, floor-level chapters with real executed evidence, inserted as true prerequisites.

| ID | Topic | Tier | Notes | Gap | Chapter |
|---|---|---|---|---|---|
| F-001 | How the Web Works: HTML, CSS, the DOM, and HTTP | BEG | The true starting point — semantic HTML, the box model, what the DOM actually is, the request/response cycle. Cross-links to the backend's own REST API Fundamentals (T-2205) for HTTP-verb depth rather than re-explaining it. | 🟢 | [`how-the-web-works-html-css-dom-and-http.md`](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) |
| F-002 | JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony | BEG | Variables/scope, functions and `this`, arrays/objects/destructuring, closures, the event loop and Promises/`async`/`await` — the real prerequisite `react-fundamentals-jsx-components-props-and-state.md` never had. | 🟢 | [`javascript-fundamentals-variables-functions-and-asynchrony.md`](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) |
| F-003 | TypeScript Fundamentals: Types, Interfaces, and Generics | BEG/INT | Plain TypeScript before React-specific TypeScript — the real prerequisite F-119 (`react-typescript.md`) never had. | 🟢 | [`typescript-fundamentals-types-interfaces-and-generics.md`](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) |

## D-F1 · React Fundamentals

| ID | Topic | Tier | Notes | Gap | Chapter |
|---|---|---|---|---|---|
| F-101 | JSX, elements vs components, the virtual DOM idea | BEG | What actually happens between JSX and a DOM update | 🟢 | [`react-fundamentals-jsx-components-props-and-state.md`](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md) |
| F-102 | Props, component composition, children | BEG | | 🟢 | same as F-101 (grouped chapter) |
| F-103 | State with `useState`, controlled re-renders | BEG | | 🟢 | same as F-101 (grouped chapter) |
| F-104 | Events, conditional rendering, lists & `key` | BEG | The `key`-as-array-index pitfall belongs here | 🟢 | same as F-101 (grouped chapter) |
| F-105 | `useEffect`: dependency arrays, cleanup, common footguns | INT | Stale closures, missing deps, effect-vs-event confusion | 🟢 | [`react-hooks-useeffect-and-useref.md`](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md) |
| F-106 | `useRef`, DOM access, mutable values that don't trigger renders | INT | | 🟢 | same as F-105 (grouped chapter) |
| F-107 | `useMemo` / `useCallback`: what they actually prevent, when they don't help | INT | A real interview favorite — most candidates overuse or misunderstand these | 🟢 | [`react-usememo-usecallback-and-usecontext.md`](../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md) |
| F-108 | `useContext` and the Context API | INT | Including its re-render cost at scale | 🟢 | same as F-107 (grouped chapter) |
| F-109 | `useReducer`, when it beats `useState` | INT | | 🟢 | [`react-usereducer-and-custom-hooks.md`](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md) |
| F-110 | Custom hooks: design, naming, composition rules | INT | | 🟢 | same as F-109 (grouped chapter) |
| F-111 | Component patterns: composition vs. inheritance, render props, compound components, HOCs | ADV | Why React explicitly favors composition | 🟢 | [`react-component-patterns.md`](../syllabus/21-frontend-web/react-component-patterns.md) |
| F-112 | Reconciliation & the fiber architecture (how React actually diffs/schedules) | ADV | Internals-depth, mirrors the JVM-internals treatment on the backend side | 🟢 | [`react-reconciliation-and-fiber.md`](../syllabus/21-frontend-web/react-reconciliation-and-fiber.md) |
| F-113 | Concurrent React: transitions, `useDeferredValue`, `useTransition`, Suspense for data | ADV | | 🟢 | [`react-concurrent-rendering.md`](../syllabus/21-frontend-web/react-concurrent-rendering.md) |
| F-114 | Forms: controlled vs. uncontrolled, validation strategies, React Hook Form / Zod | INT | | 🟢 | [`react-forms.md`](../syllabus/21-frontend-web/react-forms.md) |
| F-115 | Error boundaries & error handling strategy | INT | | 🟢 | [`react-error-boundaries.md`](../syllabus/21-frontend-web/react-error-boundaries.md) |
| F-116 | Accessibility (a11y): semantic HTML, ARIA, keyboard navigation, focus management | INT | Frequently skipped, frequently interview-relevant for Staff-level frontend | 🟢 | [`react-accessibility.md`](../syllabus/21-frontend-web/react-accessibility.md) |
| F-117 | Performance: profiling with React DevTools, memoization strategy, virtualization for large lists, code-splitting with `React.lazy` | ADV | | 🟢 | [`react-performance.md`](../syllabus/21-frontend-web/react-performance.md) |
| F-118 | Testing: React Testing Library philosophy (behavior not implementation), Jest/Vitest, mocking, Playwright/Cypress for E2E | ADV | Direct parallel to the backend `test-strategy-and-test-doubles.md` chapter | 🟢 | [`react-testing.md`](../syllabus/21-frontend-web/react-testing.md) |
| F-119 | TypeScript with React: typing props/state/hooks, generic components, discriminated unions for variant props | ADV | Now has a real prerequisite: F-003 above | 🟢 | [`react-typescript.md`](../syllabus/21-frontend-web/react-typescript.md) |
| F-120 | State management landscape: Context vs. Redux Toolkit vs. Zustand vs. server state (React Query/TanStack Query) — decision framework, not just syntax | ADV | The "when do you actually need a global store" question | 🟢 | [`react-state-management.md`](../syllabus/21-frontend-web/react-state-management.md) |

## D-F2 · Next.js

| ID | Topic | Tier | Notes | Gap | Chapter |
|---|---|---|---|---|---|
| F-201 | Next.js's role: file-based routing, why a meta-framework over plain React/Vite | BEG | | 🟢 | [`nextjs-fundamentals.md`](../syllabus/21-frontend-web/nextjs-fundamentals.md) |
| F-202 | App Router fundamentals: layouts, pages, nested routing, route groups | BEG | | 🟢 | [`nextjs-app-router-fundamentals.md`](../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md) |
| F-203 | Server Components vs. Client Components: the actual boundary, `"use client"`, what runs where | INT | The single most-tested modern Next.js concept | 🟢 | [`nextjs-server-vs-client-components.md`](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md) |
| F-204 | Data fetching in the App Router: `fetch` caching semantics, `revalidate`, `cache: 'no-store'` | INT | | 🟢 | [`nextjs-data-fetching-and-caching.md`](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md) |
| F-205 | Rendering strategies: SSR, SSG, ISR — what each means mechanically and when to choose it | INT | Direct analogue to the backend's caching-strategies cheat sheet | 🟢 | [`nextjs-rendering-strategies.md`](../syllabus/21-frontend-web/nextjs-rendering-strategies.md) |
| F-206 | Streaming & Suspense boundaries in the App Router | ADV | | 🟢 | [`nextjs-streaming-and-suspense.md`](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md) |
| F-207 | Route Handlers (API routes): building a backend-for-frontend layer in Next.js itself | INT | Relevant to a Java-backend-plus-Next.js full-stack setup — where does logic live | 🟢 | [`nextjs-route-handlers.md`](../syllabus/21-frontend-web/nextjs-route-handlers.md) |
| F-208 | Middleware & the Edge runtime | ADV | | 🟢 | [`nextjs-proxy-and-edge-runtime.md`](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) |
| F-209 | Metadata API, SEO fundamentals for a React app | INT | | 🟢 | [`nextjs-metadata-api-and-seo.md`](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) |
| F-210 | Image/font optimization, Core Web Vitals | INT | | 🟢 | [`nextjs-image-font-optimization-and-web-vitals.md`](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) |
| F-211 | Authentication patterns: Auth.js/NextAuth, JWT vs. session cookies, protecting Server Components and Route Handlers | ADV | | 🟢 | [`nextjs-authentication-patterns.md`](../syllabus/21-frontend-web/nextjs-authentication-patterns.md) |
| F-212 | Server Actions: mutations without a separate API layer, progressive enhancement | ADV | | 🟢 | [`nextjs-server-actions-and-mutations.md`](../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md) |
| F-213 | Deployment models: Vercel's platform-native features vs. self-hosting (Docker/Node server) — real trade-offs, not marketing | ADV | | 🟢 | [`nextjs-deployment-models.md`](../syllabus/21-frontend-web/nextjs-deployment-models.md) |
| F-214 | Full-stack integration with a separate backend (this repo's Java/Spring material): CORS, BFF pattern, where auth/session logic should live when the API is a separate service | EXP | The topic that most directly serves a full-stack Java+React developer specifically | 🟢 | [`nextjs-fullstack-integration.md`](../syllabus/21-frontend-web/nextjs-fullstack-integration.md) |

## D-F3 · Tooling & Ecosystem

| ID | Topic | Tier | Notes | Gap | Chapter |
|---|---|---|---|---|---|
| F-301 | Build tooling: Vite vs. Next.js's own compiler/Turbopack, what a bundler actually does | INT | | 🟢 | [`nextjs-build-tooling-vite-vs-turbopack.md`](../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md) |
| F-302 | Styling approaches: CSS Modules, Tailwind, CSS-in-JS — trade-offs, not just preference | INT | | 🟢 | [`nextjs-styling-approaches.md`](../syllabus/21-frontend-web/nextjs-styling-approaches.md) |
| F-303 | Monorepo/full-stack repo layout: where frontend and backend code live together sanely | ADV | Relevant to how this repo itself is structured for a full-stack learner | 🟢 | [`nextjs-monorepo-layout.md`](../syllabus/21-frontend-web/nextjs-monorepo-layout.md) |

## D-F4 · Advanced Frontend Architecture & Security

**Added 2026-09-11.** Found during a full repository-wide gap audit: this domain's 40 originally-registered topics (D-F0–D-F3) had zero dedicated coverage of frontend security, despite the backend's own `12-security` domain covering the equivalent server-side ground (authN/authZ, injection, CORS). Opened as a new tier rather than folded into an existing one, since it targets a genuinely different concern (what a frontend engineer controls at the rendering/browser layer) than any existing D-F0–D-F3 tier. WebSocket/real-time and micro-frontends were also flagged as absent from this domain during the same audit and remain open, un-closed sub-items of this tier for a future pass — not yet given topic IDs.

| ID | Topic | Tier | Notes | Gap | Chapter |
|---|---|---|---|---|---|
| F-401 | Frontend Security: XSS, CSRF, and Content Security Policy | ADV | Real, browser-executed evidence (Playwright/Chromium, not jsdom): a real reflected-XSS payload firing, HTML-escaping neutralizing it, and a real CSP header genuinely blocking inline script execution with the browser's own captured console violation. | 🟢 | [`frontend-security-xss-csrf-and-csp.md`](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) |

## Interview-craft (no register tier)

| Topic | Notes | Gap | Chapter |
|---|---|---|---|
| Frontend Live-Coding & Debugging Protocol | Application-skill document, not a knowledge topic — mirrors `syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md`'s own treatment. Originated in `interview-playbook/frontend/`, relocated Phase 3 (2026-09-03). | 🟢 | [`frontend-live-coding-and-debugging-protocol.md`](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md) |

## How this register is used today

**Re-audited 2026-09-08.** All 40 topics now listed (the original 37 plus F-001–F-003) have a real, fully-written chapter in `syllabus/21-frontend-web/` — cross-checked directly against that domain's own `INDEX.md` and, for the three new topics, against real executed evidence (Node.js scripts, `tsc` compiler output, a real HTTP server and `curl` transcript) built the same day. This register remains the provenance record of the domain's original planning; `syllabus/21-frontend-web/INDEX.md` is the current, authoritative index of what actually exists — when the two ever disagree, trust the INDEX.

No topic here is a placeholder or an aspirational stub. If a future audit finds a real new gap, extend this register the same way F-001–F-003 were added: a new row, a real chapter with real executed evidence, then an update here — not a promise for later.
