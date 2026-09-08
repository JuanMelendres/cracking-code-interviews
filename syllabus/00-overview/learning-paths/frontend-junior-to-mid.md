---
title: "Learning Path: Frontend Junior → Mid"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-08
source: 00-project/frontend-topic-register.md
---

# Learning Path: Frontend Junior → Mid

**Audience:** a new engineer (0–2 years, or a backend engineer picking up frontend for the first time) with no prior JavaScript, TypeScript, or web-fundamentals experience — someone who has never built a web page or a React component before.

**Goal:** reach Beginner-through-Intermediate tier (this domain's own equivalent of L1–L3, per `syllabus/21-frontend-web/INDEX.md`'s Phase 5 mastery-equivalence mapping) across true web/language fundamentals, core React, and a first working Next.js app.

**Time budget:** ~6–8 weeks, part-time (6–8 hours/week).

**Stops at:** Intermediate tier for every topic listed. Several topics below continue to Advanced/Expert in their own chapter — stop reading once the Intermediate sections are solid; Advanced/Expert is this path's own follow-on, [Frontend Mid → Senior](frontend-mid-to-senior.md).

This path is the frontend counterpart to [Junior → Mid](junior-to-mid.md) (the Java backend path) — additive, not merged, per `CLAUDE.md`'s Scope Addendum. A full-stack reader following both paths together gets a genuinely dual-track floor: Java/Spring/SQL on one side, JavaScript/TypeScript/React/Next.js on the other, meeting at [REST API Fundamentals](../../07-api-design/rest-api-fundamentals.md) and [Full-Stack Integration](../../21-frontend-web/nextjs-fullstack-integration.md) (this path's own closing topic on the follow-on path).

**Built 2026-09-08**, the same day a repository-wide audit found this domain — despite already being designed to span Beginner-through-Expert, per the 2026-08-12 Scope Addendum — had no operational learning-path sequencing of its own, unlike every backend domain. The audit also found the domain's own "Beginner tier" silently assumed JavaScript/TypeScript/web-fundamentals literacy it never taught; three new chapters (F-001–F-003) closed that gap the same day and are Topics 1–3 below.

## Sequence

| # | Topic | Tier | Stop at | Why here |
|---|---|---|---|---|
| 1 | [How the Web Works: HTML, CSS, the DOM, and HTTP](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md) (F-001) | Beginner | Beginner | The true starting point — semantic HTML, the box model, the DOM, and the request/response cycle, before any JavaScript or React. |
| 2 | [JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) (F-002) | Beginner | Beginner | Every later chapter is written in JavaScript/JSX and assumes this. Closures specifically are the direct prerequisite for understanding `useState`/`useEffect` next. |
| 3 | [TypeScript Fundamentals: Types, Interfaces, and Generics](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) (F-003) | Beginner/Intermediate | Intermediate | Placed early deliberately, even though core React can be learned in plain JS — most real-world React/Next.js code (including this repository's own demos) is TypeScript, and reading it fluently from Topic 4 onward is worth the investment now rather than retrofitting it later. |
| 4 | [React Fundamentals: JSX, Components, Props, and State](../../21-frontend-web/react-fundamentals-jsx-components-props-and-state.md) (F-101–F-104) | Beginner | Beginner | Components, props, `useState`, events, conditional rendering, lists and `key` — the actual floor of React itself. |
| 5 | [React Hooks: useEffect and useRef](../../21-frontend-web/react-hooks-useeffect-and-useref.md) (F-105/F-106) | Intermediate | Intermediate | Side effects and DOM access — the second hook every React app needs. |
| 6 | [React Memoization and Context: useMemo, useCallback, useContext](../../21-frontend-web/react-usememo-usecallback-and-usecontext.md) (F-107/F-108) | Intermediate | Intermediate | A real interview favorite; also the first exposure to React's re-render model, which Topic 9 (Next.js Server/Client boundary) builds on conceptually. |
| 7 | [React useReducer and Custom Hooks](../../21-frontend-web/react-usereducer-and-custom-hooks.md) (F-109/F-110) | Intermediate | Intermediate | When `useState` stops being enough, and how to package reusable logic. |
| 8 | [Next.js's Role: File-Based Routing and Why a Meta-Framework](../../21-frontend-web/nextjs-fundamentals.md) (F-201) | Beginner | Beginner | The transition from "a React app" to "a real, shippable web app" — why a meta-framework over plain React/Vite. |
| 9 | [Next.js App Router Fundamentals: Nested Layouts and Route Groups](../../21-frontend-web/nextjs-app-router-fundamentals.md) (F-202) | Beginner | Beginner | File-based routing mechanics — the first real Next.js app structure. |
| 10 | [Build Tooling: Vite vs. Next.js's Turbopack](../../21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md) (F-301) | Intermediate | Intermediate | What actually happens when you run `npm run dev` — demystifies the tooling every prior topic silently relied on. |
| 11 | [Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS](../../21-frontend-web/nextjs-styling-approaches.md) (F-302) | Intermediate | Intermediate | Real trade-offs between styling approaches, building directly on Topic 1's CSS fundamentals. |
| 12 | [React Forms: Controlled vs. Uncontrolled, Validation Strategy](../../21-frontend-web/react-forms.md) (F-114) | Intermediate | Intermediate | The first genuinely production-shaped UI problem — every real app has a form. |
| 13 | [React Error Boundaries and Error Handling Strategy](../../21-frontend-web/react-error-boundaries.md) (F-115) | Intermediate | Intermediate | What happens when Topic 12's form (or anything else) breaks in production. |
| 14 | [React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management](../../21-frontend-web/react-accessibility.md) (F-116) | Intermediate | Intermediate | Deliberately not deferred to the Senior path — accessibility is cheapest to build in from day one, and directly extends Topic 1's semantic-HTML foundation. |

## Completion criteria

- Can explain each topic above cold, at its stated stop tier, without notes (see each chapter's own Interview Questions section).
- Has reproduced the real executed evidence in Topics 1–3's own `practice/frontend/` demos (a real served page and `curl` transcript, real Node.js script output, real `tsc` compiler output including a genuine error-then-fix pair).
- Has built and run at least one real Next.js app combining a form, client-side state, and at least one accessible, semantic page — not just read the chapters describing each piece separately.

## Next

[Frontend Mid → Senior](frontend-mid-to-senior.md) — the direct continuation into Next.js internals, React performance/testing/TypeScript depth, and the full-stack-integration capstone.
