---
title: "Mock Interview: Frontend Security, Real-Time UI, and Micro-Frontends Round (45 min)"
slug: frontend-security-realtime-and-microfrontends-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-09-13
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - CSP's real, specific scope vs. XSS in general
  - CSRF as a mechanically distinct problem from XSS
  - WebSocket reconnect-storm risk and exponential backoff
  - WebSocket vs. EventSource automatic-reconnection distinction
  - Module Federation shared/singleton dependency configuration
  - Proving independent deployability, not assuming it
  - Production/technical story
related:
  - ../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md
  - ../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
  - ../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md
  - ../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md
source: null
official_references: []
---

# Mock Interview: Frontend Security, Real-Time UI, and Micro-Frontends Round

**Target role:** Senior/Staff Frontend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

**Sourcing note.** Like the [AI/LLM Engineering](ai-llm-engineering-technical-round.md) and [GraphQL/gRPC](graphql-grpc-api-design-round.md) rounds, this one has no prior `study-packs/` mock file to elevate — F-401, F-402, and F-403 are the newest `21-frontend-web` chapters (added 2026-09-11), after every frontend study-pack program. Built fresh, but not invented: all 6 technical questions are each chapter's own already-written Interview Questions (2 per chapter), each already carrying a real expected answer grounded in that chapter's own real, browser-executed demo (Playwright/Chromium — not a simulated DOM). This round's own contribution is the Mock Interview Standard structure, not new technical content. This closes the mock-interview freshness gap in full — the third and last of the three domains flagged this session.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| CSP's real, specific scope vs. XSS in general | Q1 | [Frontend Security: XSS, CSRF, and CSP](../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) |
| CSRF as a mechanically distinct problem from XSS | Q2 | [Frontend Security: XSS, CSRF, and CSP](../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) |
| WebSocket reconnect-storm risk and exponential backoff | Q3 | [WebSocket and Server-Sent Events for Real-Time UI](../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md) |
| WebSocket vs. EventSource automatic-reconnection distinction | Q4 | [WebSocket and Server-Sent Events for Real-Time UI](../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md) |
| Module Federation `shared`/`singleton` configuration | Q5 | [Micro-Frontends and Module Federation](../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md) |
| Proving independent deployability | Q6 | [Micro-Frontends and Module Federation](../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md) |
| Production/technical story | Q7 | [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) |

## Interviewer Opening Script

*"This is a 45-minute round on three cross-cutting frontend concerns: security, real-time UI, and multi-team architecture. Several of these questions have a plausible-sounding but incomplete answer, and a more precise, mechanism-level one — I want the precise version. Let's start with a CSP header that didn't do what someone expected."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** "A CSP with `script-src 'self'` is deployed, but a reflected-XSS payload using an `<img onerror>` attribute still executes. Why didn't CSP stop it?"
2. **(6 min)** "Why doesn't HTML-escaping help against CSRF?"
3. **(6 min)** "Your WebSocket client reconnects immediately with no delay when the connection drops. What's wrong with that, and what would you change?"
4. **(6 min)** "Could you build a WebSocket-based feature that also gets automatic reconnection without writing your own backoff logic?"
5. **(6 min)** "Two remotes both depend on React 18, and neither configures `shared`. What happens, and what's the fix?"
6. **(6 min)** "How would you actually verify, in a real system, that two micro-frontends can be deployed independently — not just that the architecture diagram says they can?"
7. **(9 min)** Deliver a production/technical story about a system you built or debugged, using the four-beat structure — a frontend story if you have one, any real technical story otherwise.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — CSP didn't stop an `<img onerror>` XSS payload

**Ideal answer outline:** `script-src 'self'` restricts which script *sources* may execute and blocks inline `<script>` tags specifically — but an `onerror`/`onload` event-handler attribute on a non-script element like `<img>` is a real, separate execution vector some CSP configurations don't fully close unless the policy also explicitly disallows inline event handlers. The real, primary fix is preventing the injection from becoming markup at all (escaping); CSP is a valuable second layer, not a universal XSS blocker. If the candidate assumes any CSP header makes XSS impossible, the correct push is: "if escaping already handles this, what does CSP actually add?"
**Common weak answers:** assuming any CSP header makes XSS structurally impossible, rather than understanding its specific, real scope.
**Pass signal:** names the event-handler-attribute vector as outside `script-src`'s coverage in some configurations, and reframes escaping as the primary fix, either unprompted or promptly under the push.
**Borderline signal:** agrees CSP has limited scope under the push, but can't name the specific attribute-handler vector.
**Fail signal:** insists CSP alone should have blocked this even after the push.

### Question 2 — Why HTML-escaping doesn't help against CSRF

**Ideal answer outline:** CSRF doesn't involve injecting or rendering anything at all — it exploits the browser's own default behavior of automatically attaching a user's cookies to any request to a site they're authenticated with, regardless of which page triggered the request. Escaping controls what gets parsed as markup; it has no bearing on which requests a browser sends or which cookies it attaches. CSRF needs its own defense: a token the attacker's page can't obtain, or a `SameSite` cookie attribute. If the candidate says "we escape our output" as a complete security answer, the correct push is: "does escaping change which requests the victim's browser sends?"
**Common weak answers:** treating "we escape our output" as a sufficient security answer covering CSRF as well as XSS.
**Pass signal:** correctly explains the automatic-cookie-attachment mechanism CSRF exploits, either unprompted or promptly under the push.
**Borderline signal:** agrees CSRF is different from XSS under the push, but can't name the cookie-attachment mechanism specifically.
**Fail signal:** conflates CSRF with XSS even after the push.

### Question 3 — WebSocket client reconnects immediately, no delay

**Ideal answer outline:** if the server is down or restarting, every client reconnecting instantly and repeatedly can itself overwhelm the server the moment it comes back — a self-inflicted retry storm. The fix is exponential backoff (increasing delay between attempts, often with a cap and jitter). If the candidate sees no problem with immediate retry, the correct push is: "what happens when the server that just went down comes back, and every disconnected client tries to reconnect in the same instant?"
**Common weak answers:** assuming the browser or protocol already prevents this — it does not; reconnection is entirely application-owned for a raw `WebSocket`.
**Pass signal:** names exponential backoff and explains the retry-storm mechanism it prevents, either unprompted or promptly under the push.
**Borderline signal:** agrees rapid reconnection is risky under the push, but can't name backoff specifically.
**Fail signal:** sees no problem with immediate, unlimited retries even after the push.

### Question 4 — Getting automatic reconnection without writing backoff logic

**Ideal answer outline:** not with a plain `WebSocket` — the reconnection algorithm is genuinely part of the `EventSource`/SSE specification, not something a library can retrofit onto the WebSocket protocol itself. A library (e.g., Socket.IO) can implement backoff-based reconnection for you on top of WebSocket, but that's the same application-level work done by hand — the library ships it pre-written, it doesn't make the protocol itself reconnect on its own the way `EventSource` genuinely does. If the candidate believes some runtime secretly auto-reconnects WebSockets, the correct push is: "is that behavior specified anywhere, or is something else actually doing it?"
**Common weak answers:** believing a specific browser or JS runtime secretly reconnects WebSockets automatically under some circumstance.
**Pass signal:** correctly distinguishes "a library implements backoff for you" from "the protocol reconnects on its own," either unprompted or promptly under the push.
**Borderline signal:** agrees WebSocket itself doesn't auto-reconnect under the push, but can't articulate the library-vs-protocol distinction.
**Fail signal:** insists WebSocket reconnects on its own even after the push.

### Question 5 — Two remotes both depend on React 18, neither configures `shared`

**Ideal answer outline:** each remote bundles its own complete, separate copy of React, and the user's browser downloads React multiple times across the composed page — real wasted bytes, and potentially multiple independent React instances that can behave incorrectly if they need to share state or context. The fix is declaring `react`/`react-dom` under `shared` with `singleton: true` in every build's `ModuleFederationPlugin` configuration. If the candidate assumes Module Federation dedupes shared dependencies automatically, the correct push is: "is that automatic, or does something have to declare it?"
**Common weak answers:** assuming Module Federation automatically dedupes shared dependencies with no configuration required.
**Pass signal:** names `shared` and `singleton: true` specifically, either unprompted or promptly under the push.
**Borderline signal:** agrees duplication is the risk under the push, but can't name the specific configuration fix.
**Fail signal:** insists deduplication happens automatically even after the push.

### Question 6 — Proving independent deployability, not assuming it

**Ideal answer outline:** deploy a change to one micro-frontend's build in isolation, deliberately without touching or redeploying the other, and confirm the composed page's user-visible behavior actually reflects the change — a real rebuild-one-without-the-other test. In a real CI/CD pipeline, this maps to each micro-frontend having its own independent pipeline that can run end-to-end without triggering or depending on the other's. If the candidate treats "we use Module Federation" as sufficient proof on its own, the correct push is: "have you actually tested that, or is that an assumption based on the tooling?"
**Common weak answers:** treating "we use Module Federation" itself as sufficient proof, without ever actually testing the deploy-independence claim.
**Pass signal:** describes the rebuild-one-without-the-other test concretely, either unprompted or promptly under the push.
**Borderline signal:** agrees this needs testing under the push, but can't describe a concrete test.
**Fail signal:** maintains the architecture alone is sufficient proof even after the push.

### Question 7 — Production/technical story

**Ideal answer outline:** a four-beat, clearly structured story (situation, action, the specific decision criterion used, and the outcome/cost) about real technical work under real constraints.
**Common weak answers:** a story with no clear structure, or one that describes what changed without stating the specific reasoning behind the chosen approach.
**Pass signal:** clear four-beat structure with a specific decision criterion and outcome, scored per Technical Depth and Production Judgment.
**Borderline signal:** the story is coherent but the decision criterion has to be extracted through follow-up.
**Fail signal:** no clear structure, or no identifiable decision criterion even on request.

## Scoring Rubric

Score this round using the [shared six-dimension rubric](../../study-packs/week-01/10-week-1-evaluation-rubric.md)'s **Technical Depth** and **Production Judgment** dimensions specifically (1–5 scale, 3 = Mid, 4 = Senior, 5 = Staff) — the same two-dimension scope the [AI/LLM Engineering](ai-llm-engineering-technical-round.md) and [GraphQL and gRPC](graphql-grpc-api-design-round.md) rounds use.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 1 and 2 share the sharpest theme: both probe whether the candidate treats "we have security headers/escaping" as one undifferentiated defense, when CSP, escaping, and CSRF tokens each close a genuinely different, specific gap — a candidate who can't separate them is reciting a security checklist rather than reasoning about mechanisms. Questions 3 and 4 both hinge on knowing exactly what a spec (WebSocket's RFC 6455) does and does not provide versus what application or library code has to supply — a candidate confident that "the platform handles it" on either question has a gap worth flagging directly. Questions 5 and 6 share a "configuration vs. assumption" theme: Module Federation doesn't automatically dedupe dependencies (Q5) and doesn't automatically guarantee independent deployability (Q6) just because it's the tool in use — both require an explicit, verified step, not an assumed byproduct of adoption.

## Remediation Recommendations

- Weak Q1 → re-read [Frontend Security: XSS, CSRF, and CSP](../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)'s Core Concepts and Internal Implementation, and [`reflected-xss-from-a-bypassed-default-escaping-path.md`](../../production-cookbook/reflected-xss-from-a-bypassed-default-escaping-path.md) for the real incident shape.
- Weak Q2 → re-read [Frontend Security: XSS, CSRF, and CSP](../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)'s Definition and Purpose and Core Concepts sections.
- Weak Q3 → re-read [WebSocket and Server-Sent Events for Real-Time UI](../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)'s Core Concepts and Internal Implementation, and [`websocket-dashboard-silently-freezing-after-a-dropped-connection.md`](../../production-cookbook/websocket-dashboard-silently-freezing-after-a-dropped-connection.md) for the real incident.
- Weak Q4 → re-read [WebSocket and Server-Sent Events for Real-Time UI](../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)'s Definition and Purpose and Decision Framework sections.
- Weak Q5 → re-read [Micro-Frontends and Module Federation](../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)'s Core Concepts and Trade-offs sections.
- Weak Q6 → re-read [Micro-Frontends and Module Federation](../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)'s Internal Implementation, and [`two-teams-colliding-on-release-schedules-for-one-shared-frontend-bundle.md`](../../production-cookbook/two-teams-colliding-on-release-schedules-for-one-shared-frontend-bundle.md) for the real incident this pattern fixes.
- Weak Q7 → re-read [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) — the closest-fit chapter for a technical story, since this cluster of chapters has no dedicated behavioral-handbook chapter of its own.
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
