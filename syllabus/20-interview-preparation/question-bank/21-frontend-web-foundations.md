---
title: "Interview Question Bank — 21-frontend-web-foundations"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-14
related:
  - ../../21-frontend-web/INDEX.md
  - 21-frontend-web-react.md
  - 21-frontend-web-nextjs.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Frontend Web: Foundations, Security, and Cross-Cutting Topics

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

`21-frontend-web` is the largest remaining domain (38 chapters) and, like `02-java`,
yields far more real questions than one file can hold at pilot depth — split into 3
files matching this domain's own natural topic groups: this file (JavaScript,
TypeScript, browser fundamentals, security, and cross-cutting practice — 7 chapters),
[`21-frontend-web-react.md`](21-frontend-web-react.md) (14 chapters), and
[`21-frontend-web-nextjs.md`](21-frontend-web-nextjs.md) (17 chapters).

**Honest count for this file:** 7 chapters yielded 15 deep questions + 23 quick-fire
questions = **38 real questions**. No Junior Fundamentals chapter in this specific
group, though the frontend domain overall spans the full Junior–Staff ladder per its
Scope Addendum in `CLAUDE.md`.

---

## How the Web Works: HTML, CSS, the DOM, and HTTP

### Q1 — What's the difference between the HTML you write and the DOM?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "HTML" and "the DOM" as interchangeable terms for the same thing — the common mistake this question targets.
- **Senior:** States precisely why the distinction matters practically (debugging with DevTools vs. View Source), not just reciting the definition — HTML is the original static text, the DOM is a live, in-memory tree JavaScript can mutate.
- **Staff:** Not the focus of this chapter's scope.

### Q2 — Why does `box-sizing: border-box` matter, and what's the default behavior without it?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Doesn't know this is even a distinction — assumes `width: 200px` always means the element is exactly 200px wide regardless of padding/border — the common mistake this question targets.
- **Senior:** Can state the exact math (declared width + padding + border, under the default `content-box`) that produces the "wider than expected" surprise.
- **Staff:** Not the focus of this chapter's scope.

### Q3 — Walk me through what happens, at a high level, between typing a URL and seeing a rendered page.

**Canonical treatment:** [§ Interview Questions, Q3](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Skips DNS or the TCP handshake entirely and starts the explanation at "the browser sends an HTTP request" — the common mistake this question targets.
- **Senior:** Names all the major stages unprompted, in the correct order (DNS, TCP/TLS, HTTP request/response, HTML parse, CSS cascade, paint), and can explain the render-blocking follow-up.
- **Staff:** Not the focus of this chapter's scope.

---

## JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony

### Q1 — What will this code print, and why? (`console.log`, `setTimeout`, `Promise.resolve().then()`, interleaved)

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers in source order, or treats both async mechanisms as equally deferred without distinguishing microtask vs. macrotask priority — the common mistake this question targets.
- **Senior:** States the microtask-before-macrotask rule unprompted, and correctly explains that this holds regardless of the macrotask's declared delay.
- **Staff:** Not the focus of this chapter's scope.

### Q2 — Why does this `setInterval`-inside-a-method code fail, and how would you fix it?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Says "it's a scope problem" without identifying that the specific issue is `this` binding — the common mistake this question targets.
- **Senior:** Names multiple valid fixes (arrow function, `.bind(timer)`, `const self = this`), not just one, and can explain why each works mechanically.
- **Staff:** Not the focus of this chapter's scope.

---

## TypeScript Fundamentals: Types, Interfaces, and Generics

### Q1 — What's the actual difference between `any` and `unknown`, and why would you ever prefer the less convenient one?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes them as roughly interchangeable "flexible" types — the common mistake this question targets.
- **Senior:** Gives a concrete example of code that compiles with `any` and correctly fails (until narrowed) with `unknown`.
- **Staff:** Frames banning `any` via lint rule as a team-scale convention decision, not a per-file judgment call.

### Q2 — Two interfaces `Dog` and `Robot`, declared completely independently, both have `name: string` and `bark(): void`. Is a `Robot` value assignable to a variable typed `Dog`?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers "no" by reasoning from nominal-typing languages (Java, C#) where this would be incompatible — the common mistake this question targets.
- **Senior:** States the structural-vs-nominal distinction unprompted (yes, it's assignable — TypeScript checks shape, not declared name or inheritance) and correctly reasons about the excess-property-checking follow-up.
- **Staff:** Not the focus of this chapter's scope.

---

## Frontend Security: XSS, CSRF, and Content Security Policy

### Q1 — A CSP with `script-src 'self'` is deployed, but a reflected-XSS payload using an `<img onerror>` attribute still executes. Why didn't CSP stop it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/frontend-security-xss-csrf-and-csp.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes any CSP header makes XSS impossible, rather than understanding its specific, real scope — the common mistake this question targets.
- **Senior:** Correctly names the event-handler-attribute vector as outside `script-src`'s specific coverage in some configurations.
- **Staff:** Reframes the real lesson: CSP is a valuable second layer, but escaping untrusted output remains the primary, structural fix.

### Q2 — Why doesn't HTML-escaping help against CSRF?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/frontend-security-xss-csrf-and-csp.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes "we escape our output" is a sufficient security answer covering CSRF as well as XSS — the common mistake this question targets.
- **Senior:** Correctly explains the automatic-cookie-attachment mechanism CSRF exploits, independent of anything being rendered as markup.
- **Staff:** Names both real standard defenses (CSRF tokens, `SameSite` cookies) and explains why using both is more robust than either alone.

---

## Micro-Frontends and Module Federation

### Q1 — Two remotes both depend on React 18, and neither configures `shared`. What happens, and what's the fix?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/micro-frontends-and-module-federation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes Module Federation automatically dedupes shared dependencies without any configuration — the common mistake this question targets; it does not.
- **Senior:** Names `shared` and `singleton: true` specifically and explains the version-resolution behavior (the highest version satisfying every build's `requiredVersion` is used).
- **Staff:** Discusses the organizational process needed to keep every team's `shared` configuration consistent over time as remotes are added or upgraded independently.

### Q2 — How would you actually verify, in a real system, that two micro-frontends can be deployed independently — not just that the architecture diagram says they can?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/micro-frontends-and-module-federation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "we use Module Federation" itself as sufficient proof, without ever actually testing the deploy-independence claim — the common mistake this question targets.
- **Senior:** Connects this directly to independent CI/CD pipelines per micro-frontend as the production-scale version of the deploy-one-without-the-other test.
- **Staff:** Notes this should be a standing, repeatable verification, not a one-time proof-of-concept, since a later shared-build-step regression could quietly reintroduce coupling.

---

## WebSocket and Server-Sent Events for Real-Time UI

### Q1 — Your WebSocket client reconnects immediately with no delay when the connection drops. What's wrong with that, and what would you change?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the browser or protocol already prevents a reconnect storm — the common mistake this question targets; it does not.
- **Senior:** Names exponential backoff and explains the retry-storm mechanism it prevents.
- **Staff:** Adds jitter to avoid many clients retrying in lockstep, and a maximum retry ceiling or user-visible "still reconnecting" state.

### Q2 — Could you build a WebSocket-based feature that also gets automatic reconnection without writing your own backoff logic?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Believes a specific browser or JavaScript runtime secretly reconnects WebSockets automatically under some circumstance — the common mistake this question targets; it does not, per RFC 6455's own scope.
- **Senior:** Correctly distinguishes "a library implements backoff for you" from "the protocol reconnects on its own" (which only `EventSource`/SSE genuinely does), and can name at least one such library.
- **Staff:** Notes that if the feature is genuinely one-way, this is a real signal to reconsider `EventSource` instead of adding a reconnection library on top of WebSocket.

---

## Frontend Live-Coding & Debugging Protocol

### Q1 — You're given a component with a suspected re-render bug. Walk through how you'd approach it, live.

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/frontend-live-coding-and-debugging-protocol.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to a proposed fix without confirming the bug is real and reproducible first — the common mistake this question targets.
- **Senior:** Explicitly reproduces the bug first, states a specific hypothesis before investigating further, and confirms both the bug and the fix using a real tool (DevTools, the Profiler) rather than assuming either from the code alone.
- **Staff:** Connects the same evidence standard to code review practice — expecting a teammate's performance-fix PR to include the same before/after confirmation.

### Q2 — Why does "build the whole component, then run it once" fail more often in a frontend round than the equivalent pattern in a backend coding round?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/frontend-live-coding-and-debugging-protocol.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as purely a time-management tip rather than connecting it to why isolating cause-and-effect is structurally harder once multiple UI bugs are visible at once — the common mistake this question targets.
- **Senior:** Explains that a UI's multiple simultaneous bugs are harder to isolate after the fact than a backend function's single wrong return value, and that incremental rendering avoids that compounding.
- **Staff:** Generalizes to real development practice — a large, unreviewed frontend PR risks the identical compounding problem at team scale, part of the case for small, incrementally-reviewable PRs.

---

## Quick-fire questions (from this file's chapters' Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What's the difference between the HTML a server sends and the DOM? | [How the Web Works](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#flashcards) |
| 2 | What does `box-sizing: border-box` change about how `width` is interpreted? | [How the Web Works](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#flashcards) |
| 3 | When do you reach for Flexbox vs. Grid? | [How the Web Works](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#flashcards) |
| 4 | Beyond style, what does using `<nav>`/`<main>`/`<article>` instead of `<div>` actually buy you? | [How the Web Works](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#flashcards) |
| 5 | Why does an HTTP response need a `Content-Length` header at all? | [How the Web Works](../../21-frontend-web/how-the-web-works-html-css-dom-and-http.md#flashcards) |
| 6 | Why does a regular function passed as a `setTimeout` callback inside an object method lose access to the object as `this`, while an arrow function doesn't? | [JavaScript Fundamentals](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#flashcards) |
| 7 | Given synchronous code, a `.then()` callback, and a `setTimeout(fn, 0)`, what actually prints first? | [JavaScript Fundamentals](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#flashcards) |
| 8 | What is a closure, precisely — not just "a function inside a function"? | [JavaScript Fundamentals](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#flashcards) |
| 9 | Why does `[] == false` evaluate to `true` in JavaScript? | [JavaScript Fundamentals](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#flashcards) |
| 10 | Does `const` prevent an object's properties from being changed? | [JavaScript Fundamentals](../../21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md#flashcards) |
| 11 | `formatPrice("19.99")` calls `.toFixed(2)` and crashes in plain JavaScript. What happens in TypeScript, and why? | [TypeScript Fundamentals](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#flashcards) |
| 12 | `Point` and `Coordinate` share a shape but no declared relationship. Is a `Coordinate` assignable to a `Point`-typed variable? | [TypeScript Fundamentals](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#flashcards) |
| 13 | Both `any` and `unknown` accept any value. What's the practical difference? | [TypeScript Fundamentals](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#flashcards) |
| 14 | Why is a discriminated union stronger than one interface with an optional field? | [TypeScript Fundamentals](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#flashcards) |
| 15 | `identity<T>(value: T): T` vs. `identity(value: any): any` — what's the real difference? | [TypeScript Fundamentals](../../21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#flashcards) |
| 16 | What's the real, mechanical reason an XSS payload executes in a browser? | [Frontend Security](../../21-frontend-web/frontend-security-xss-csrf-and-csp.md#flashcards) |
| 17 | Does a Content-Security-Policy header change what the server sends, or what the browser will execute? | [Frontend Security](../../21-frontend-web/frontend-security-xss-csrf-and-csp.md#flashcards) |
| 18 | What does a host's bundle actually contain for a module it imports from a remote? | [Micro-Frontends and Module Federation](../../21-frontend-web/micro-frontends-and-module-federation.md#flashcards) |
| 19 | What's the concrete test for whether two micro-frontends can actually deploy independently? | [Micro-Frontends and Module Federation](../../21-frontend-web/micro-frontends-and-module-federation.md#flashcards) |
| 20 | After a WebSocket closes and after an EventSource's connection drops, which one reconnects automatically, and why? | [WebSocket and SSE for Real-Time UI](../../21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md#flashcards) |
| 21 | Why does a WebSocket reconnect wrapper use increasing delays (backoff) instead of retrying immediately? | [WebSocket and SSE for Real-Time UI](../../21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md#flashcards) |
| 22 | Name the two distinct frontend live-coding round formats and each one's primary risk. | [Frontend Live-Coding & Debugging Protocol](../../21-frontend-web/frontend-live-coding-and-debugging-protocol.md#flashcards) |
| 23 | Which phase of the frontend-adapted protocol is the single highest-leverage, most frequently skipped phase? | [Frontend Live-Coding & Debugging Protocol](../../21-frontend-web/frontend-live-coding-and-debugging-protocol.md#flashcards) |

---

## Related

- [`21-frontend-web-react.md`](21-frontend-web-react.md)
- [`21-frontend-web-nextjs.md`](21-frontend-web-nextjs.md)
- [`20-interview-preparation.md`](20-interview-preparation.md)
- [`01-computer-science-foundations.md`](01-computer-science-foundations.md)
- [`19-leadership-staff.md`](19-leadership-staff.md)
- [`12-security.md`](12-security.md)
- [`08-testing.md`](08-testing.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
