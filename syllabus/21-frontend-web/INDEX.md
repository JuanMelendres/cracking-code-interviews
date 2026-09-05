---
title: "Frontend & Web (React/Next.js) — Domain Index"
document_type: syllabus-domain-index
domain: 21-frontend-web
status: 32 of 32 mapped chapters physically relocated (Phase 3, 2026-09-03); Foundation/Working-Knowledge already present (Beginner–Expert tiers by original design); L1–L4 equivalence formally mapped (Phase 5, 2026-09-05)
last_updated: 2026-09-05
---

# Frontend & Web (React/Next.js)

The additive, deliberately-separate React/Next.js track (31 `handbook/frontend/` chapters spanning Beginner→Expert by design since its 2026-08-12 Scope Addendum, plus 1 interview-craft entry from `interview-playbook/frontend/`). Kept structurally distinct from the Java backend track per `CLAUDE.md`'s explicit "additive, not merged" instruction.

> **Phase 3 update (2026-09-03).** All 31 `handbook/frontend/` chapters plus `interview-playbook/frontend/frontend-live-coding-and-debugging-protocol.md` have physically relocated here via `git mv`, preserving file history — 32 files total. `practice/frontend/` and `00-project/frontend-topic-register.md` stay at their current paths per the plan's Section 7.4 (`practice/` never relocates) and Section 7.3 (the register stays at `00-project/` as provenance), referenced from here rather than moved.
>
> **Phase 5 update (2026-09-05) — mastery equivalence mapped, not retrofitted.** This domain was explicitly exempted from the Level-1/Level-2 content-insertion retrofit applied to the other 16 syllabus domains this phase: per the transformation plan's own §3 ("the frontend domain is the exception that proves the rule"), `00-project/frontend-topic-register.md` already spans Beginner/Intermediate/Advanced/Expert tiers by original design (the 2026-08-12 Scope Addendum), with each individual chapter written at its own single, deliberate tier rather than needing Foundation/Working-Knowledge layers added underneath existing Senior/Staff-only content. What was genuinely missing was a *stated* equivalence between this domain's own four-tier system and the syllabus-wide L1–L4 mastery model, so the two systems could be compared and queried consistently. The equivalence applied here, per tier: **Beginner → L1, L2** (matches the register's own "what it is, why it exists" framing for BEG topics — read directly: F-101–F-104's foundations chapter teaches both what JSX/props/state are and how to use them correctly, e.g. the real, measured list-key bug); **Intermediate → L2, L3** (INT topics teach correct practitioner usage that already reaches into real footguns and performance nuance — e.g. `useContext`'s re-render cost at scale, stale-closure bugs in `useEffect`); **Advanced → L3, L4** (ADV topics are explicitly internals/production-depth by the register's own description — reconciliation/fiber "mirrors the JVM-internals treatment," testing is a "direct parallel to the backend `test-strategy-and-test-doubles.md` chapter," both Senior/Staff-equivalent framings); **Expert → L4** (the register's only EXP topic, F-214 full-stack integration, is explicitly framed as the topic serving systemic, cross-service architectural judgment — the register's own Staff-equivalent description). This is a front-matter and index-level mapping only (`topic_id` and `mastery_levels_covered` added to all 32 files' YAML front matter, `last_updated` bumped) — no chapter body content was added, removed, or reworded, since none of it was missing to begin with. The interview-craft entry (`frontend-live-coding-and-debugging-protocol.md`) has no register tier of its own (it is an application-skill document, not a knowledge topic, mirroring `syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md`'s own treatment) — mapped to `[L2, L3, L4]` based on its stated `target_levels: [mid, senior, staff]`, with no fabricated `topic_id`.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| F-101/F-104 | React Fundamentals: JSX, Components, Props, and State | Beginner tier → **L1, L2** | `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md` |
| F-105/F-106 | React Hooks: useEffect and useRef | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md` |
| F-107/F-108 | React Memoization and Context: useMemo, useCallback, useContext | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md` |
| F-109/F-110 | React useReducer and Custom Hooks | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md` |
| F-111 | React Component Patterns | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-component-patterns.md` |
| F-112 | React Reconciliation and the Fiber Architecture | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-reconciliation-and-fiber.md` |
| F-113 | Concurrent React: Transitions, Deferred Values, and Suspense for Data | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-concurrent-rendering.md` |
| F-114 | React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-forms.md` |
| F-115 | React Error Boundaries and Error Handling Strategy | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-error-boundaries.md` |
| F-116 | React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/react-accessibility.md` |
| F-117 | React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-performance.md` |
| F-118 | React Testing: RTL Philosophy, Mocking, and E2E with Playwright | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-testing.md` |
| F-119 | TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-typescript.md` |
| F-120 | React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/react-state-management.md` |
| F-201 | Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite | Beginner tier → **L1, L2** | `syllabus/21-frontend-web/nextjs-fundamentals.md` |
| F-202 | Next.js App Router Fundamentals: Nested Layouts and Route Groups | Beginner tier → **L1, L2** | `syllabus/21-frontend-web/nextjs-app-router-fundamentals.md` |
| F-203 | Server Components vs. Client Components: The Actual Boundary | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-server-vs-client-components.md` |
| F-204 | Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store' | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md` |
| F-205 | Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-rendering-strategies.md` |
| F-206 | Streaming & Suspense Boundaries in the App Router | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-streaming-and-suspense.md` |
| F-207 | Route Handlers: Building a Backend-for-Frontend Layer in Next.js | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-route-handlers.md` |
| F-208 | Proxy (formerly Middleware) & the Edge Runtime in Next.js 16 | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md` |
| F-209 | The Metadata API and SEO Fundamentals in Next.js | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md` |
| F-210 | Image and Font Optimization, and Core Web Vitals in Next.js | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md` |
| F-211 | Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized() | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-authentication-patterns.md` |
| F-212 | Server Actions and Mutations in Next.js: No API Layer, Real Progressive Enhancement | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md` |
| F-213 | Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-deployment-models.md` |
| F-214 | Full-Stack Integration: Next.js with a Separate Java/Spring Backend | Expert tier → **L4** | `syllabus/21-frontend-web/nextjs-fullstack-integration.md` |
| F-301 | Build Tooling: Vite vs. Next.js's Turbopack, What a Bundler Actually Does | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md` |
| F-302 | Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified | Intermediate tier → **L2, L3** | `syllabus/21-frontend-web/nextjs-styling-approaches.md` |
| F-303 | Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified | Advanced tier → **L3, L4** | `syllabus/21-frontend-web/nextjs-monorepo-layout.md` |
| — | Frontend Live-Coding & Debugging Protocol (interview-craft, from `interview-playbook/frontend/`) | Interview-application skill (no register tier) → **L2, L3, L4** | `syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
