---
title: "Learning Path: Frontend Mid → Senior/Staff"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-08
source: 00-project/frontend-topic-register.md
---

# Learning Path: Frontend Mid → Senior/Staff

**Audience:** a working frontend or full-stack engineer who can already ship a React/Next.js feature (i.e., [Frontend Junior → Mid](frontend-junior-to-mid.md) is solid) and needs Next.js internals depth, React performance/testing/TypeScript mastery, and the production judgment a Senior frontend loop tests.

**Goal:** reach Advanced tier throughout (this domain's own equivalent of L3, with the closing topic reaching Expert/L4 per `syllabus/21-frontend-web/INDEX.md`'s Phase 5 mastery-equivalence mapping).

**Time budget:** ~8–10 weeks, part-time (6–8 hours/week).

**Stops at:** Advanced tier for most topics, Expert for the closing topic (Full-Stack Integration) — the frontend domain's own Staff-equivalent signal, per the mastery-equivalence mapping.

This path is the frontend counterpart to [Mid → Senior](mid-to-senior.md) (the Java backend path) — additive, not merged, per `CLAUDE.md`'s Scope Addendum. A full-stack reader following both paths gets genuine dual-track Senior depth: JVM/Spring/database internals on one side, React fiber internals/Next.js rendering strategies/full-stack integration on the other.

## Sequence

| # | Topic | Tier | Why here |
|---|---|---|---|
| 1 | [Server Components vs. Client Components: The Actual Boundary](../../21-frontend-web/nextjs-server-vs-client-components.md) (F-203) | Intermediate/Advanced | The single most-tested modern Next.js concept — the direct next step once Junior-to-Mid's basic App Router usage is solid. |
| 2 | [Data Fetching in the App Router: Caching Semantics](../../21-frontend-web/nextjs-data-fetching-and-caching.md) (F-204) | Intermediate/Advanced | Builds directly on Topic 1's Server/Client boundary — where and how data actually gets fetched. |
| 3 | [Rendering Strategies: SSR, SSG, and ISR](../../21-frontend-web/nextjs-rendering-strategies.md) (F-205) | Intermediate/Advanced | The mechanical "when to choose which" decision every real Next.js app has to make — direct analogue to the backend's own caching-strategies chapter. |
| 4 | [Route Handlers: Building a Backend-for-Frontend Layer](../../21-frontend-web/nextjs-route-handlers.md) (F-207) | Intermediate/Advanced | Where logic lives in a full-stack setup — sets up Topic 20's closing full-stack-integration topic. |
| 5 | [The Metadata API and SEO Fundamentals](../../21-frontend-web/nextjs-metadata-api-and-seo.md) (F-209) | Intermediate | A frequently-underweighted production concern for a shipped app. |
| 6 | [Image and Font Optimization, and Core Web Vitals](../../21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) (F-210) | Intermediate | Real performance metrics a Senior frontend engineer is expected to own. |
| 7 | [React Component Patterns](../../21-frontend-web/react-component-patterns.md) (F-111) | Advanced | Composition vs. inheritance, render props, compound components, HOCs — the design-pattern layer above Junior-to-Mid's basic component usage. |
| 8 | [React Reconciliation and the Fiber Architecture](../../21-frontend-web/react-reconciliation-and-fiber.md) (F-112) | Advanced | Internals depth — mirrors the JVM-internals treatment on the backend side. The first genuinely "how does this work underneath" topic in this path. |
| 9 | [Concurrent React: Transitions, Deferred Values, and Suspense for Data](../../21-frontend-web/react-concurrent-rendering.md) (F-113) | Advanced | Builds directly on Topic 8's fiber model. |
| 10 | [React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting](../../21-frontend-web/react-performance.md) (F-117) | Advanced | Applying Topics 8–9's internals knowledge to a real, measured performance problem. |
| 11 | [React Testing: RTL Philosophy, Mocking, and E2E with Playwright](../../21-frontend-web/react-testing.md) (F-118) | Advanced | Direct parallel to the backend's own test-strategy chapter — testing habits at Senior depth. |
| 12 | [TypeScript with React: Generic Components and Discriminated Unions](../../21-frontend-web/react-typescript.md) (F-119) | Advanced | Now has a real prerequisite (Frontend Junior-to-Mid's Topic 3) — this chapter applies plain TypeScript to React-specific typing problems. |
| 13 | [React State Management Landscape](../../21-frontend-web/react-state-management.md) (F-120) | Advanced | The "when do you actually need a global store" decision framework — Context vs. Redux Toolkit vs. Zustand vs. server state. |
| 14 | [Streaming and Suspense Boundaries in the App Router](../../21-frontend-web/nextjs-streaming-and-suspense.md) (F-206) | Advanced | Builds on Topics 1–3's Server Component/data-fetching/rendering-strategy foundation. |
| 15 | [Proxy (formerly Middleware) and the Edge Runtime](../../21-frontend-web/nextjs-proxy-and-edge-runtime.md) (F-208) | Advanced | Where request-level logic runs at the edge — a genuinely Senior-level Next.js concern. |
| 16 | [Authentication Patterns: DAL, JWT Sessions, and unauthorized()](../../21-frontend-web/nextjs-authentication-patterns.md) (F-211) | Advanced | Cross-references the backend's own security domain directly for a full-stack reader. |
| 17 | [Server Actions and Mutations: No API Layer, Real Progressive Enhancement](../../21-frontend-web/nextjs-server-actions-and-mutations.md) (F-212) | Advanced | The modern alternative to Topic 4's Route Handlers for a pure Next.js mutation path. |
| 18 | [Deployment Models: Vercel-Native vs. Self-Hosting](../../21-frontend-web/nextjs-deployment-models.md) (F-213) | Advanced | Real operational trade-offs, not marketing — a direct parallel to the backend's own cloud-cost-and-scaling-economics chapter. |
| 19 | [Monorepo and Full-Stack Repo Layout](../../21-frontend-web/nextjs-monorepo-layout.md) (F-303) | Advanced | Where frontend and backend code actually live together sanely — directly relevant to how this repository itself is structured for a full-stack learner. |
| 20 | [Full-Stack Integration: Next.js with a Separate Java/Spring Backend](../../21-frontend-web/nextjs-fullstack-integration.md) (F-214) | **Expert** | The path's closing, Staff-equivalent topic — CORS, the BFF pattern, and where auth/session logic should live when the API is a separate service. The single chapter that most directly serves a full-stack Java+React developer, tying this entire path back to the Java backend domain. |

## Completion criteria

- Can defend each Advanced-tier topic's trade-offs to a skeptical peer, naming the specific condition that would change the answer (per each chapter's own Trade-offs/Decision Framework sections).
- Can explain, from memory, how a component's fiber gets scheduled, why a given re-render happened, and how to fix an unnecessary one — the internals fluency Topics 8–10 build.
- Has built or extended a real full-stack demo combining a Next.js frontend with a separate backend API (this repository's own Java/Spring material is the natural pairing), correctly placing CORS/BFF/auth-session logic per Topic 20's own Decision Framework.

## Related paths

- [Frontend Junior → Mid](frontend-junior-to-mid.md) — the direct prerequisite.
- [Mid → Senior](mid-to-senior.md) and [Senior → Staff](senior-to-staff.md) — the Java backend counterparts; a full-stack reader following both tracks reaches genuine dual-track Senior/Staff depth, not just breadth on one side.
