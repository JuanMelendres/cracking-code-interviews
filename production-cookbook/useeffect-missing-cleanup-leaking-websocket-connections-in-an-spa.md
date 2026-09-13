---
title: "useEffect Missing Cleanup Leaking WebSocket Connections in an SPA"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md
source: syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md#production-scenarios
---

# useEffect Missing Cleanup Leaking WebSocket Connections in an SPA

## Context

A "recently viewed items" widget subscribes to a live-price WebSocket feed in a `useEffect` with an empty dependency array, intending "connect once when this widget mounts."

## Symptoms

After enough navigation cycles in a long-lived SPA session, the browser's connection limit is hit and new subscriptions silently fail — reported as "prices stop updating after a while."

## Impact

A live-data feature silently degrades over the course of a session, with no error surfaced to the user or an obvious trigger to point at.

## Initial Hypotheses

- A backend feed issue — the initial investigation path, pursued for two days before being ruled out.
- A rate limit on the price feed itself — checked, the feed's own server-side connection accounting shows no rate-limiting behavior.
- The effect's setup function opens a WebSocket connection but never closes it, leaking one connection per widget mount — correct.

## Evidence

Opening DevTools' Network tab and counting orphaned WebSocket connections shows connections accumulating with every navigation to a page containing the widget, never decreasing.

## Investigation Timeline

1. Users report prices stop updating after extended use of the app.
2. Backend feed and rate-limit hypotheses investigated for two days as the initial, incorrect focus.
3. DevTools' Network tab inspected directly, revealing an accumulating count of orphaned WebSocket connections tied to widget mounts.

## Root Cause

The effect's cleanup function — which would call `.close()` on unmount — was never written, so every mount of the widget opens a new connection with no corresponding close, exhausting the browser's connection limit over a long-lived session.

## Immediate Mitigation

Advise affected users to refresh the page as a stopgap, resetting the connection count, while the fix ships.

## Permanent Fix

Add the missing cleanup function (`return () => socket.close();`) to the effect, ensuring every mount's connection is closed on the corresponding unmount.

## Alternatives Considered

Investigating the backend feed further before checking client-side connection counts — this was, in fact, the path taken first, and cost two days precisely because the symptom (stale data) didn't obviously point to a frontend resource leak; recorded here as the lesson, not a considered-and-rejected alternative.

## Trade-offs

None meaningful — the fix is a single line with no functional downside.

## Prevention

Treat every `useEffect` that opens a persistent resource (a WebSocket, a subscription, a timer) as requiring an explicit, reviewed cleanup function as a standing checklist item, not an optional addition.

## Monitoring and Alerts

- Client-side open-connection-count tracking (or a periodic DevTools-equivalent check in synthetic long-session testing) to catch this class of leak before users report stale data.
- A lint rule or code-review checklist flag for any `useEffect` opening a connection/subscription/timer with no corresponding cleanup return.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent leak.

- **Situation:** a live-price widget's data silently stopped updating after extended use, investigated for two days as a backend issue.
- **Task:** find the actual root cause once the backend was ruled out.
- **Action:** opened DevTools' Network tab and counted orphaned WebSocket connections, confirming a per-mount leak with no corresponding cleanup.
- **Result:** added the missing one-line cleanup function, closing the leak entirely.

## Staff-Level Discussion

The fix was one line; the cost was the two-day misdirected investigation, because the symptom (stale data) didn't obviously point to a frontend resource leak. The organizational lesson is that a `useEffect` opening any persistent resource without cleanup is a structurally invisible bug — it produces no error, no console warning, and a symptom (stale data, silent failure) that looks like almost anything else — so an explicit, standing cleanup-function checklist item is the only reliable defense, not vigilance during ad hoc code review.

## Related Handbook Chapters

- [React Hooks: useEffect and useRef](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md) — the canonical effect-cleanup pattern behind this incident's fix.
