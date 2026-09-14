---
title: "Interview Question Bank — 21-frontend-web-nextjs"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-14
related:
  - ../../21-frontend-web/INDEX.md
  - 21-frontend-web-foundations.md
  - 21-frontend-web-react.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Frontend Web: Next.js

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline. Part of the 3-file `21-frontend-web` split —
see [`21-frontend-web-foundations.md`](21-frontend-web-foundations.md) for the sourcing
note and the domain's other two files.

**Honest count for this file:** 17 chapters yielded 34 deep questions + 34 quick-fire
questions = **68 real questions**. This domain's Next.js chapters are unusually
evidence-heavy — most questions are grounded in a real, captured test the chapter ran
(a `next build` output, a network trace, a live-tested behavior) rather than a
documentation claim alone, which is reflected directly in the tier breakdowns below.

---

## Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite

### Q1 — Explain exactly how a URL like `/blog/my-post` gets served in a Next.js App Router project.

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes this only as "Next.js has file-based routing" without being able to explain the actual mechanism — the common mistake this question targets.
- **Senior:** Explains the mechanism precisely (a `[slug]` dynamic-segment folder, the `params` object) and can describe how to verify it independently — the real `next build` route manifest.
- **Staff:** Connects this to the broader configuration-drift argument — eliminating a separate route registry structurally prevents a real class of bug.

### Q2 — A teammate says Next.js layouts are "just like a shared header component in React Router." What's actually different?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Asserts layouts and a shared header component are functionally identical without naming the specific persistence guarantee — the common mistake this question targets.
- **Senior:** Identifies the specific persistence guarantee as the real distinction (layout doesn't remount between sibling routes) and proposes a concrete verification method — a mount counter, not trusting the framework's claim on faith.
- **Staff:** Connects the persistence guarantee to a concrete UX/architecture consequence (state loss on navigation) that would matter for a real feature.

---

## Next.js App Router Fundamentals: Nested Layouts and Route Groups

### Q1 — Does `DashboardLayout` remount navigating between two routes under `/dashboard`? What about navigating to a route outside `/dashboard`?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-app-router-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes layout persistence is a global, unconditional guarantee ("layouts never remount") — the common mistake this question targets.
- **Senior:** Answers both parts correctly with the correct reasoning (shared ancestry, not just "it's a layout so it persists") and can describe a concrete verification method.
- **Staff:** Connects this to a broader architectural point about where layout boundaries should be placed as an app grows, and the state-loss risk of an incorrect assumption.

### Q2 — Why would a team use `app/(marketing)/pricing/page.js` instead of `app/marketing/pricing/page.js`?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-app-router-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the two as functionally equivalent, or differing only cosmetically — the common mistake this question targets.
- **Senior:** States the precise URL-inclusion-vs-exclusion difference unprompted — the parenthesized version is a route group excluded from the URL, used to share a layout without a URL prefix.
- **Staff:** Recognizes route groups as one of several tools for managing layout-boundary architecture as an app scales, not just a syntax trick.

---

## Rendering Strategies: SSR, SSG, and ISR

### Q1 — A dynamic route has 10,000 possible param values, but only the top 50 get meaningful traffic. Would you list all 10,000 in `generateStaticParams`?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-rendering-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes every dynamic param value must be explicitly listed or the route won't work for that value — the common mistake this question targets, missing the on-demand fallback.
- **Senior:** Proposes the partial-listing approach unprompted and can explain the real fallback mechanism — an unlisted value is generated on first request, then cached.
- **Staff:** Discusses the cost trade-off explicitly (build-time cost vs. first-request latency for rare values) as a deliberate engineering decision.

### Q2 — A page needs a personalized "recently viewed" widget alongside a large, mostly-static product description. How would you structure the rendering strategy?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-rendering-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Defaults the entire page to SSR because some part of it needs personalization — the common mistake this question targets.
- **Senior:** Proposes component-level isolation unprompted rather than a whole-page strategy choice — the personalized widget in its own Suspense boundary, the rest statically generated.
- **Staff:** Quantifies or articulates the real cost difference at scale, and frames this as a general pattern (isolate the dynamic slice, keep the rest static).

---

## Server Components vs. Client Components: The Actual Boundary

### Q1 — "Server Components are safe for secrets because the data never reaches the browser." Is that exactly right? How would you verify it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-server-vs-client-components.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Repeats "Server Components keep secrets safe" as an unconditional guarantee — the common mistake this question targets.
- **Senior:** Corrects the overly broad claim precisely (the component's code never reaches the browser; its rendered output can still expose the secret if it chooses to render it) and proposes a concrete verification method (grepping the real client bundle and prerendered HTML).
- **Staff:** Proposes this verification as a repeatable, automatable CI check rather than a one-off manual audit.

### Q2 — Why can't a Client Component be an `async function`? And is that always caught at build time?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-server-vs-client-components.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes every Server/Client Component restriction is caught identically (always build-time or always runtime) without verifying the specific enforcement point — the common mistake this question targets.
- **Senior:** Explains the underlying reason (implicit Promise return incompatible with the synchronous Client render path) and correctly flags that build-time-vs-runtime enforcement isn't something to assume.
- **Staff:** Connects this to a concrete CI/testing gap — build success alone is an insufficient correctness signal for this class of bug — and proposes a mitigation (a runtime smoke test).

---

## Data Fetching in the App Router: Caching Semantics, `revalidate`, and `cache: 'no-store'`

### Q1 — "We don't need `cache: 'no-store'` here because `fetch` is uncached by default anyway." How do you respond?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-data-fetching-and-caching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Accepts the "uncached by default" claim as settling the question — the common mistake this question targets.
- **Senior:** Corrects the overly broad claim precisely — fetch-layer default vs. route-level Full Route Cache — and proposes a concrete verification method (the build manifest's `○`/`ƒ` marker).
- **Staff:** Frames this as a class of bug worth a team-wide check (explicitly reviewing every route's caching behavior before shipping).

### Q2 — A `force-cache`-eligible page fetches data from an API route in the SAME Next.js deployment. What real problem might you hit?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-data-fetching-and-caching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `force-cache`/default fetches only ever execute at request time — the common mistake this question targets, missing that static-generation-eligible routes are evaluated during the build.
- **Senior:** Explains the build-time-execution mechanism precisely and identifies the same-deployment dependency as the root cause — a real chicken-and-egg connection-refused failure.
- **Staff:** Proposes a concrete architectural fix (an external data source, or a build-ordering step) rather than only a code-level workaround.

---

## Streaming & Suspense Boundaries in the App Router

### Q1 — A dashboard page has three independent widgets, one occasionally taking 5+ seconds. How would you structure the Suspense boundaries, and verify the fix worked?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-streaming-and-suspense.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes the correct boundary structure but can't describe a concrete verification method beyond "it looks right" — the common mistake this question targets.
- **Senior:** Proposes per-widget boundaries unprompted and names a concrete, direct verification method — a real chunk-timing trace, not a proxy like perceived speed.
- **Staff:** Connects this to a durable team convention — every independently-fetched section gets its own boundary by default.

### Q2 — Does Next.js block streaming entirely for bot/crawler requests, or something narrower?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-streaming-and-suspense.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Repeats the broad "bots get blocking responses" claim without having verified its actual scope — the common mistake this question targets.
- **Senior:** Correctly identifies that the claim needs scoping — the blocking is specific to `generateMetadata` resolution, not a blanket rule — and proposes a way to verify it for a specific page.
- **Staff:** Generalizes into a broader discipline: treating documented framework behaviors as claims to verify for the specific case at hand, not universal facts.

---

## Route Handlers: Building a Backend-for-Frontend Layer in Next.js

### Q1 — A teammate wants a Server Component to fetch its data by calling this same app's own `/api/widgets` Route Handler. What's your concern?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-route-handlers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "one clean API layer for everything" as an unconditional good, without naming the specific build-time failure this pattern causes — the common mistake this question targets.
- **Senior:** Names the specific build-time mechanism — a Server Component prerendered at build time can't reach a Route Handler served by a server that doesn't exist yet.
- **Staff:** Proposes the shared-module fix and generalizes the principle to when Route Handlers are genuinely the right internal seam.

### Q2 — You add `export const dynamic = 'force-static'` to a Route Handler's `GET`. A week after deploy, the data is stale. Why, and what would you check first?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-route-handlers.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `force-static` behaves like an in-memory cache with a TTL — the common mistake this question targets.
- **Senior:** Explains the build-time-freeze mechanism precisely and proposes a concrete check — whether `revalidateTag`/`revalidatePath` is actually wired up.
- **Staff:** Connects this to the same time-based-vs-on-demand invalidation trade-off from the data-fetching chapter, rather than treating it as a new problem.

---

## Server Actions and Mutations in Next.js

### Q1 — A delete button renders only on pages the user is authenticated to see. Is the delete Server Action itself protected?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-server-actions-and-mutations.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "the UI only shows this to authenticated users" as equivalent to "only authenticated users can trigger this" — the common mistake this question targets.
- **Senior:** States the render-vs-endpoint distinction — a Server Action compiles into a public POST endpoint the moment it exists — and proposes the concrete fix (an explicit, early in-action check).
- **Staff:** Argues for testing mutation endpoints against underlying data state, not response status, since a bypass can produce a response indistinguishable from a legitimate rejection.

### Q2 — You wrap a Server Action in a local function to drive `useOptimistic`. Does this change the form's behavior?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-server-actions-and-mutations.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes all forms wired to Server Actions get identical framework guarantees regardless of how the action reference reaches the `<form>` — the common mistake this question targets.
- **Senior:** States the concrete mechanism — a serializable bound reference works without JS; a closure has nothing serializable to encode, so React renders an inert placeholder.
- **Staff:** Frames this as a deliberate, measured trade a team makes per-form, not a universal Server Actions limitation.

---

## Authentication Patterns in Next.js: DAL, JWT Sessions, and unauthorized()

### Q1 — Your Proxy checks `request.cookies.has('session')` before allowing `/dashboard` access. Is this sufficient authentication?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-authentication-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a cookie's mere presence implies a valid session — the common mistake this question targets, conflating presence with cryptographic validity.
- **Senior:** States the real, concrete gap (presence vs. validity) and proposes the concrete fix — real signature verification (`jwtVerify`) in Proxy, matching the DAL's authoritative check.
- **Staff:** Frames this as a defense-in-depth principle, not a single bug fix — both layers should independently do real verification.

### Q2 — With `authInterrupts` enabled, some `unauthorized()` calls show a real 401 status, others show 200 with correct UI. Why?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-authentication-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the `authInterrupts` flag alone determines the returned status — the common mistake this question targets.
- **Senior:** States the streaming-boundary-dependent behavior precisely — whether the response had already begun streaming as a 200 before `unauthorized()` was called.
- **Staff:** Identifies a concrete real consequence (automated tooling relying on status codes) and proposes the correct architectural trade-off (a Proxy-based check) where it matters.

---

## The Metadata API and SEO Fundamentals in Next.js

### Q1 — Your team forgot to set `metadataBase`, and pages use relative OG image paths. What actually happens?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-metadata-api-and-seo.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Trusts "the docs say it'll error" as proof this is safely caught in CI, without testing it directly — the common mistake this question targets.
- **Senior:** States the real (warning, not error) behavior precisely, with the specific wrong hardcoded `localhost` URL baked into production HTML as evidence.
- **Staff:** Proposes a concrete CI mitigation (grep the build output for the warning string) and generalizes the lesson — verify documentation-prose claims against real build output.

### Q2 — Does a search engine bot experience your dynamically-rendered page's metadata the same way a real user's browser does?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-metadata-api-and-seo.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes streaming benefits everyone equally, missing that bots specifically get the slower, blocking path — the common mistake this question targets.
- **Senior:** States the bot-specific blocking behavior precisely, backed by real timing contrast — bots wait for `generateMetadata` to finish before any response headers arrive.
- **Staff:** Connects this to a concrete performance budget for `generateMetadata` specifically for crawlable, dynamically-rendered pages.

---

## Image and Font Optimization, and Core Web Vitals in Next.js

### Q1 — A teammate sets `quality={90}` on an `<Image>` but the served file looks the same. What's happening, and how would you confirm it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a disallowed prop value would produce a visible error or warning — the common mistake this question targets; the real behavior is silent, invisible clamping.
- **Senior:** States the exact clamping behavior and proposes the correct verification method — inspecting the real rendered `<img src>`/`srcset`, not trusting the prop value.
- **Staff:** Articulates why the component and the `/_next/image` endpoint behave differently (UX-oriented fallback vs. a real enforcement boundary).

### Q2 — Is `priority` still the right prop to use for a Next.js 16 app's LCP image?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes all deprecations in a given framework version behave consistently (loud vs. silent) — the common mistake this question targets.
- **Senior:** States the deprecation precisely and the real absence of any warning signal, having actually checked.
- **Staff:** Generalizes to a concrete upgrade-process recommendation — an explicit deprecation audit against the changelog, not reliance on tooling warnings.

---

## Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified

### Q1 — Your Tailwind bundle contains a utility class no one remembers using. How would you find out why, and is this necessarily a bug?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-styling-approaches.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes Tailwind's purge behaves like JavaScript dead-code elimination, semantically aware of actual usage — the common mistake this question targets; it's a textual scan, not semantic.
- **Senior:** States the real, textual-scanning mechanism precisely, with the concrete reproduction method (a mention in a comment or README leaking into the build).
- **Staff:** Frames this as a real, ongoing governance concern for large, many-contributor codebases, proposing the `@source` scoping fix.

### Q2 — A teammate says CSS-in-JS libraries inject a `<style>` tag readable via `style.textContent`. Is that accurate?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-styling-approaches.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes all CSS injection mechanisms behave identically via `textContent` — the common mistake this question targets.
- **Senior:** States the real, specific reason `textContent` fails for styled-components v6 (it uses the CSSOM's `insertRule` directly) and names the correct inspection method — `document.styleSheets[n].cssRules`.
- **Staff:** Generalizes the lesson — verify a tool's actual DOM/CSSOM behavior directly rather than assuming a simplified mental model applies uniformly.

---

## Build Tooling: Vite vs. Next.js's Turbopack

### Q1 — "Vite doesn't bundle anything, Turbopack does everything." Is that accurate?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "Vite doesn't bundle" as an absolute, unqualified claim rather than a dev-mode-specific one — the common mistake this question targets.
- **Senior:** States the precise, scoped version of the claim with real evidence behind each half — Vite pre-bundles dependencies and genuinely bundles in production; Turbopack does bundle in dev.
- **Staff:** Frames the real question as "which dev-mode architecture" rather than "which tool bundles," since both tools' production guarantees converge.

### Q2 — Vite reports "ready in 400ms" and your Turbopack app reports "Ready in 271ms." Does that mean Turbopack is faster?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Compares startup numbers across apps of very different sizes without controlling for that variable — the common mistake this question targets.
- **Senior:** Identifies the app-size confound precisely and proposes the correct, controlled comparison — the actual request pattern once a page is genuinely requested.
- **Staff:** Generalizes the lesson — benchmark claims should be verified against a team's own real, representative app before justifying a tooling decision.

---

## Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified

### Q1 — You containerize with `output: 'standalone'`. Pages render but look unstyled, with 404s for `/_next/static/...`. What's wrong?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-deployment-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `output: "standalone"` is a complete, ready-to-run deployment artifact — the common mistake this question targets; it deliberately excludes `public/` and `.next/static`.
- **Senior:** States the concrete missing directories and the concrete fix (copying `public/` and `.next/static` into the standalone directory).
- **Staff:** Frames this as a checklist item to codify in CI/Dockerfile review, since the failure mode (correct HTML, broken everything else) is easy to miss in a quick smoke test.

### Q2 — Self-hosting across multiple instances behind a load balancer — does every Server Action need `NEXT_SERVER_ACTIONS_ENCRYPTION_KEY` set identically?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-deployment-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Applies a blanket "all Server Actions need this" policy without checking which actions actually close over outer-scope values — the common mistake this question targets.
- **Senior:** States the closure-vs-bound-action distinction precisely, with real visible evidence (the encrypted hidden-field blob only appears for closure-capturing actions).
- **Staff:** Recommends setting the key as a default anyway (defense against future refactors) while understanding precisely why it isn't strictly required today.

---

## Proxy (formerly Middleware) & the Edge Runtime in Next.js 16

### Q1 — Upgrading from v15 to v16 — what real risk should you check for around `middleware.ts`?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-proxy-and-edge-runtime.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a rename this significant would produce a loud build error if missed — the common mistake this question targets; it fails silently instead.
- **Senior:** Names the specific real symptom (silent, not a loud error) and the concrete verification method — checking the real build summary for the `ƒ Proxy (Middleware)` line.
- **Staff:** Frames this as one instance of a broader migration discipline — checking a version's real build output for a framework's own stated changes.

### Q2 — A teammate wants to set the Edge runtime on `proxy.js` for lower latency. What do you tell them?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-proxy-and-edge-runtime.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the Edge runtime is uniformly "deprecated" the same way everywhere — the common mistake this question targets; Proxy has a hard build-breaking constraint, ordinary routes only a warning.
- **Senior:** States the hard-error-vs-warning distinction precisely, with the real captured error text.
- **Staff:** Reads this as a signal about the framework's architectural direction and redirects the underlying latency concern toward an actually-current solution.

---

## Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified

### Q1 — Two apps need to share a validation schema. A teammate suggests just copying the file into both. What's the real risk, and the actual fix?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-monorepo-layout.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as a process/communication problem ("we just need better code review") rather than a structural one — the common mistake this question targets.
- **Senior:** Proposes the shared-workspace-package fix with the concrete mechanism (a real symlink, verified with `readlink`, not a copy).
- **Staff:** Names the real exception case (genuinely divergent versions needed) and how workspace tooling's versioning handles that too.

### Q2 — Ten independent frontend apps, none using workspace tooling, each with its own `node_modules`. Is that automatically a mistake?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-monorepo-layout.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes any multi-app repo without shared tooling is automatically under-engineered — the common mistake this question targets.
- **Senior:** States the real, measured trade-off precisely — a real, quantified duplication cost, weighed deliberately against per-app independence.
- **Staff:** Identifies the actual decision-relevant variable (does code need to be shared and kept in sync, not raw disk space) and ties the recommendation to the repo's real audience.

---

## Full-Stack Integration: Next.js with a Separate Java/Spring Backend

### Q1 — A request to your API "works fine in curl but fails in the browser," and a teammate assumes the API is broken. What's actually going on?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/nextjs-fullstack-integration.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Debugs the server when the failure is purely a browser-side enforcement decision the server can't see — the common mistake this question targets.
- **Senior:** States the browser-enforcement mechanic precisely (CORS is enforced entirely client-side, curl has no concept of it) and proposes the correct, scoped fix — an explicit origin allowlist.
- **Staff:** Explicitly separates the CORS question from the authorization question, and can describe when routing through a BFF avoids the CORS question for that path entirely.

### Q2 — Integrating a Next.js frontend with a separate Java/Spring backend — where should session/auth logic live?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/nextjs-fullstack-integration.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Sends the browser's own frontend-specific session token directly to the separate backend, expecting it to validate that token's format — the common mistake this question targets, coupling the two services' auth implementations.
- **Senior:** States the BFF-centralization answer with the concrete credential-separation mechanism — Next.js's Route Handlers hold both the user session and a separate server-only credential to the backend.
- **Staff:** Articulates the coupling cost of the alternative and frames the BFF's extra hop as a deliberate, worthwhile trade, not an accident of architecture.

---

## Quick-fire questions (from this file's chapters' Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What specifically creates the `/about` route in a Next.js App Router project? | [Next.js's Role](../../21-frontend-web/nextjs-fundamentals.md#flashcards) |
| 2 | How would you actually verify that Next.js layouts don't remount when navigating between sibling pages? | [Next.js's Role](../../21-frontend-web/nextjs-fundamentals.md#flashcards) |
| 3 | Under what specific condition does a nested layout unmount vs. persist on navigation? | [App Router Fundamentals](../../21-frontend-web/nextjs-app-router-fundamentals.md#flashcards) |
| 4 | What does wrapping a folder name in parentheses do in the App Router? | [App Router Fundamentals](../../21-frontend-web/nextjs-app-router-fundamentals.md#flashcards) |
| 5 | A user requests a dynamic route param not listed in `generateStaticParams`. What happens? | [Rendering Strategies](../../21-frontend-web/nextjs-rendering-strategies.md#flashcards) |
| 6 | Does a real `next build` distinguish routes generated via `generateStaticParams` from ones static by default? | [Rendering Strategies](../../21-frontend-web/nextjs-rendering-strategies.md#flashcards) |
| 7 | "Server Components are safe for secrets because the data never reaches the browser" — is that exactly correct? | [Server vs. Client Components](../../21-frontend-web/nextjs-server-vs-client-components.md#flashcards) |
| 8 | Is a Client Component being `async function` always caught as a `next build` error? | [Server vs. Client Components](../../21-frontend-web/nextjs-server-vs-client-components.md#flashcards) |
| 9 | Does `fetch()` with no `cache` option guarantee the page will always serve fresh data? | [Data Fetching and Caching](../../21-frontend-web/nextjs-data-fetching-and-caching.md#flashcards) |
| 10 | What real failure can a same-deployment `force-cache` fetch cause, and why? | [Data Fetching and Caching](../../21-frontend-web/nextjs-data-fetching-and-caching.md#flashcards) |
| 11 | Three sibling Suspense boundaries wrap components with 300ms, 1200ms, and 2500ms delays. Roughly how long does the total response take? | [Streaming & Suspense](../../21-frontend-web/nextjs-streaming-and-suspense.md#flashcards) |
| 12 | Does Next.js block streaming entirely for bot/crawler requests, or something narrower? | [Streaming & Suspense](../../21-frontend-web/nextjs-streaming-and-suspense.md#flashcards) |
| 13 | Is a Route Handler's `GET` response cached by default in the App Router? | [Route Handlers](../../21-frontend-web/nextjs-route-handlers.md#flashcards) |
| 14 | Why is it risky for a Server Component to fetch data by calling that same app's own Route Handler? | [Route Handlers](../../21-frontend-web/nextjs-route-handlers.md#flashcards) |
| 15 | If a page redirects unauthenticated visitors before showing a form, is the Server Action it submits to also protected? | [Server Actions and Mutations](../../21-frontend-web/nextjs-server-actions-and-mutations.md#flashcards) |
| 16 | Does wrapping a Server Action reference in a local client function change how the form behaves without JavaScript? | [Server Actions and Mutations](../../21-frontend-web/nextjs-server-actions-and-mutations.md#flashcards) |
| 17 | Does a Proxy check like `!request.cookies.has('session')` alone stop a genuinely tampered session cookie? | [Authentication Patterns](../../21-frontend-web/nextjs-authentication-patterns.md#flashcards) |
| 18 | With `authInterrupts` enabled, does calling `unauthorized()` always produce a real HTTP 401 status? | [Authentication Patterns](../../21-frontend-web/nextjs-authentication-patterns.md#flashcards) |
| 19 | Documentation says a relative-URL metadata field without `metadataBase` "will cause a build error." Is that what happens? | [Metadata API and SEO](../../21-frontend-web/nextjs-metadata-api-and-seo.md#flashcards) |
| 20 | For a dynamic page with a slow `generateMetadata`, does a bot's request behave the same as a normal browser's? | [Metadata API and SEO](../../21-frontend-web/nextjs-metadata-api-and-seo.md#flashcards) |
| 21 | If a `quality` prop isn't in `images.qualities`'s allowlist, does the `<Image>` component error? | [Image/Font Optimization and Web Vitals](../../21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md#flashcards) |
| 22 | Next.js 16 deprecated `priority` in favor of `preload`. Does using the old prop produce a warning? | [Image/Font Optimization and Web Vitals](../../21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md#flashcards) |
| 23 | Does Tailwind's JIT engine know the difference between a class actually used in `className` and the same string in a comment? | [Styling Approaches](../../21-frontend-web/nextjs-styling-approaches.md#flashcards) |
| 24 | For styled-components (v6), does reading a `<style>` element's `.textContent` reveal its generated CSS rules? | [Styling Approaches](../../21-frontend-web/nextjs-styling-approaches.md#flashcards) |
| 25 | Does Vite's dev server bundle your own application's source files together during development? | [Build Tooling: Vite vs. Turbopack](../../21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md#flashcards) |
| 26 | Is a raw "ready in Xms" number a fair way to compare Vite and Turbopack's speed? | [Build Tooling: Vite vs. Turbopack](../../21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md#flashcards) |
| 27 | Does `output: "standalone"` produce a complete, ready-to-run deployment on its own? | [Deployment Models](../../21-frontend-web/nextjs-deployment-models.md#flashcards) |
| 28 | Does every Server Action need a shared encryption key across self-hosted instances? | [Deployment Models](../../21-frontend-web/nextjs-deployment-models.md#flashcards) |
| 29 | Is "Middleware" still the current file convention/terminology in Next.js 16? | [Proxy & the Edge Runtime](../../21-frontend-web/nextjs-proxy-and-edge-runtime.md#flashcards) |
| 30 | Can a `proxy.js` file in Next.js 16 opt into the Edge Runtime? | [Proxy & the Edge Runtime](../../21-frontend-web/nextjs-proxy-and-edge-runtime.md#flashcards) |
| 31 | Does a workspace monorepo copy a shared package's files into each consumer, or link to them? | [Monorepo and Full-Stack Layout](../../21-frontend-web/nextjs-monorepo-layout.md#flashcards) |
| 32 | If a repo has several independent apps and doesn't use workspace tooling, is that automatically under-engineered? | [Monorepo and Full-Stack Layout](../../21-frontend-web/nextjs-monorepo-layout.md#flashcards) |
| 33 | Does CORS block a `curl` request the way it blocks a browser's `fetch()`? | [Full-Stack Integration with Spring](../../21-frontend-web/nextjs-fullstack-integration.md#flashcards) |
| 34 | After a cross-origin `fetch()` succeeds, can the calling JavaScript read the response's `Access-Control-Allow-Origin` header value? | [Full-Stack Integration with Spring](../../21-frontend-web/nextjs-fullstack-integration.md#flashcards) |

---

## Related

- [`21-frontend-web-foundations.md`](21-frontend-web-foundations.md)
- [`21-frontend-web-react.md`](21-frontend-web-react.md)
- [`20-interview-preparation.md`](20-interview-preparation.md)
- [`15-cloud.md`](15-cloud.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
