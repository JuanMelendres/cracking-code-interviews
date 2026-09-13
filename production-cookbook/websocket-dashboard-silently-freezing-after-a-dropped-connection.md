---
title: "WebSocket Dashboard Silently Freezing After a Dropped Connection"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
source: syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md#production-scenarios
---

# WebSocket Dashboard Silently Freezing After a Dropped Connection

## Context

A real-time dashboard (order counts, live metrics, a chat panel) uses a raw `WebSocket` with no reconnect logic.

## Symptoms

Users report the dashboard freezes — no new data arrives — until they manually refresh the page. It happens sporadically, correlating loosely with backend deploys or brief network hiccups, not a hard crash.

## Impact

Users lose trust in "live" data without any visible error, since nothing crashes — the UI just stops updating silently.

## Initial Hypotheses

- A backend bug stopped emitting events — checked, server logs show it's still sending.
- A frontend rendering bug — checked, the console shows no errors at all.
- A deploy's brief connection drop (or a proxy/load-balancer idle timeout) closed the socket, and nothing reconnects it — correct.

## Evidence

Confirming the client's `WebSocket.readyState` is `3` (CLOSED) in the affected session, and confirming no `close`-event handler exists that does anything beyond logging.

## Investigation Timeline

1. Users report the dashboard freezing sporadically, loosely correlated with deploys or network hiccups.
2. Backend-bug and frontend-rendering-bug hypotheses ruled out via server logs and a clean console.
3. `WebSocket.readyState` checked directly in an affected session, confirming `CLOSED` with no active reconnect attempt.

## Root Cause

A `WebSocket` left alone after `close` simply stays closed — nothing else happens; the dashboard never implemented any reconnect logic to recover from a dropped connection.

## Immediate Mitigation

Ship a reconnect wrapper (bounded exponential backoff) as a hotfix; in the meantime, advise a manual refresh.

## Permanent Fix

Add the reconnect wrapper permanently, with a real, tested backoff schedule and a cap on retry attempts (or an escalating delay ceiling) to avoid a reconnect storm if the backend is genuinely down for a while — and expose a visible "reconnecting…" UI state rather than failing silently.

## Alternatives Considered

Switching to SSE instead of adding reconnect logic — rejected as a general fix, since it forecloses future bidirectional needs (chat replies, live cursor position) the dashboard may eventually need; appropriate only if the feature is genuinely one-way forever.

## Trade-offs

A reconnect wrapper is real, ongoing code to maintain (backoff tuning, connection-state UI, potential duplicate-message handling across reconnects) versus SSE's zero-code reconnection — but only SSE's one-way constraint is acceptable for this specific feature, which needs bidirectional communication.

## Prevention

Treat "what happens when this WebSocket drops" as a mandatory design question for any real-time feature, the same way error handling is mandatory for any network call — not an edge case to patch reactively.

## Monitoring and Alerts

- Client-side `WebSocket.readyState` tracking reported to monitoring, alerting on connections stuck in `CLOSED` beyond an expected reconnect window.
- A visible "reconnecting…" UI state itself doubles as a monitoring signal to users, replacing the previous silent-freeze failure mode.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a real-time dashboard silently stopped updating after brief network drops or deploys, with no visible error.
- **Task:** find the root cause of a "silent freeze" with a clean console and healthy backend logs.
- **Action:** checked `WebSocket.readyState` directly in an affected session, confirming the connection was closed with no reconnect logic to recover it.
- **Result:** added a reconnect wrapper with bounded exponential backoff and a visible reconnecting state, eliminating the silent-freeze symptom.

## Staff-Level Discussion

The failure mode here isn't exotic — it's the single most predictable thing about a raw `WebSocket`, and the fix (a reconnect wrapper) and its verification (checking `readyState` directly) are both concrete and specific, not a vague "add error handling." The organizational lesson is treating connection-drop recovery as a mandatory design requirement for any real-time feature from the very start, the same way network-error handling is mandatory for any ordinary HTTP call — not a resilience feature to retrofit after the first production incident.

## Related Handbook Chapters

- [WebSocket and Server-Sent Events for Real-Time UI](../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md) — the canonical reconnect-wrapper pattern behind this incident's fix.
