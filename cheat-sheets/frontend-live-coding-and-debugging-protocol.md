---
title: "Cheat Sheet: Frontend Live-Coding & Debugging Protocol"
slug: frontend-live-coding-and-debugging-protocol
document_type: cheat-sheet
domain: 21-frontend-web
topic_id: "N/A (playbook-technical-answer, not a numbered register topic — see chapter's own scope note)"
canonical: ../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md
last_updated: 2026-09-12
---

# Frontend Live-Coding & Debugging Protocol

**Canonical chapter:** [`syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md`](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md)

## Core Mental Model

Frontend rounds take two distinct shapes — build and debug — each with its own primary risk: over-engineering the component tree before running anything (build), or guessing a fix from reading code instead of reproducing the bug first (debug). The backend six-phase coding protocol's shape still applies, re-scoped to render behavior instead of algorithmic complexity.

## Essential Definitions

- **Build round** — construct a small UI feature live; primary risk is over-decomposing before anything runs.
- **Debug round** — find a bug in someone else's component; primary risk is guessing a fix without reproducing it first.
- **Phase 3 (expected render behavior)** — stating upfront which components should/shouldn't re-render, the mechanism that makes phase 6's confirmation meaningful rather than an afterthought.

## Decision Table

| Phase | One-line prompt to yourself |
|---|---|
| 1. Clarify | "Can I reproduce this in the browser before I read/write a single line?" |
| 2. State the plan | "Who owns this state, and why — before I write any code?" |
| 3. Expected render behavior | "Which components should re-render here, and which shouldn't?" |
| 4. Narrate while building | "Am I saying why this dependency/key/memo boundary is what it is?" |
| 5. Test in the real browser | "Did I actually click through this, or just read the JSX?" |
| 6. Confirm render behavior | "Did I verify this in DevTools, or just assume the fix worked?" |

## Common Pitfalls

- Skipping phase 1's reproduction step in a debug round and going straight to reading code — turns an evidence-based investigation into a guessing exercise.
- Never opening browser DevTools during a performance/re-render question, relying entirely on reasoning about code instead of observing real behavior.
- Treating phase 3 (stated expected render behavior) as optional — removes the exact mechanism that makes phase 6 a meaningful, self-caught confirmation.
- Applying a build-round mindset to a debug round (jumping to a fix before confirming the bug is real), or vice versa.

## Interview Answer Skeleton

**30-sec:** Frontend rounds are build or debug, each with a different opening move — build starts with a decomposition plan, debug starts with reproduction. The backend six-phase protocol still applies, re-scoped to render behavior.

**2-min:** Add: phase 5 (testing in the real browser) is the single most frequently skipped, most consequential phase — a plausible-looking component can still have a real, visible bug that only running it reveals. Phase 3's stated expected render behavior is what makes phase 6's confirmation meaningful, not just a formality.

**Staff-level framing:** The same "observed, not just plausible" evidence standard this protocol asks of a candidate live is the same standard a Staff engineer should expect from a teammate's performance-fix pull request — narration without browser verification is an anti-pattern in both settings, not just an interview one.

## Related

- syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md
- syllabus/21-frontend-web/react-performance.md
- syllabus/21-frontend-web/react-reconciliation-and-fiber.md
