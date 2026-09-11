---
title: "Cheat Sheet: WebSocket and Server-Sent Events for Real-Time UI"
slug: websocket-and-server-sent-events-for-realtime-ui
document_type: cheat-sheet
domain: 21-frontend-web
topic_id: F-402
canonical: ../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
last_updated: 2026-09-11
---

# WebSocket and Server-Sent Events for Real-Time UI

**Canonical chapter:** [`syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md`](../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)

## Core Mental Model

`EventSource` (SSE) reconnects automatically — the WHATWG HTML spec defines reconnection as part of the API itself. A plain `WebSocket` does not — RFC 6455 defines open/close mechanics only, leaving reconnection entirely to application code. Which one you need depends on whether the client must ever send data back over the same channel.

## Essential Definitions

- **WebSocket** — full-duplex, client can send; no built-in reconnection (RFC 6455).
- **`EventSource`/SSE** — server-to-client only; free, spec-guaranteed automatic reconnection (WHATWG HTML).
- **Exponential backoff** — increasing reconnect delays, avoiding a "reconnect storm" when many clients drop at once.

## Decision Table

| Need | Choice |
|---|---|
| Client must send messages back over the real-time channel | WebSocket, with a real, tested reconnect wrapper |
| Strictly server-to-client push (notifications, live metrics) | `EventSource`/SSE — free, spec-guaranteed reconnection |
| Tune how fast SSE reconnects | Server sends a `retry: <ms>` field in the event stream |
| Avoid a reconnect storm on a WebSocket client | Exponential backoff (ideally with jitter), not immediate/fixed-delay retry |
| Verify reconnect logic actually works | Force a real close server-side, measure real elapsed time to next successful connection |

## Common Pitfalls

- Assuming any "real-time" browser API reconnects automatically, or assuming none of them do — the answer differs by API and is spec-defined, not incidental.
- Writing a WebSocket reconnect loop with immediate or fixed-delay retry — risks a reconnect storm overwhelming a server right as it recovers.
- Choosing WebSocket when the feature is strictly server-to-client — SSE gets free reconnection at lower complexity.

## Interview Answer Skeleton

**30-sec:** `EventSource`/SSE reconnects automatically per spec; plain WebSocket does not — reconnection is entirely application code. Choose WebSocket only when the client must send data back.

**2-min:** Add: a real demo proves a closed WebSocket stays closed with zero reconnection, while a real `EventSource` reconnects three times with zero client-side retry code; a real, measured exponential-backoff wrapper hits a 300/600/1200ms schedule almost exactly.

**Staff-level framing:** Reconnection ownership (client-written vs. spec-guaranteed) is a real architectural cost difference between the two APIs — not a minor implementation detail — and should drive the WebSocket-vs-SSE choice as much as the send-direction requirement does.

## Related

- syllabus/21-frontend-web/micro-frontends-and-module-federation.md
