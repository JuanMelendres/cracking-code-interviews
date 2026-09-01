---
title: "Cheat Sheet: Real-Time Delivery — WebSocket, SSE, Long-Polling, and Push"
slug: realtime-delivery-websocket-sse-and-long-polling
document_type: cheat-sheet
domain: system-design
topic_id: T-812
canonical: ../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md
last_updated: 2026-09-01
---

# Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push

**Canonical chapter:** [`handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md`](../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md)

## Core Mental Model

Four mechanisms sit on two real axes: is the connection held open, and can the server speak without being asked? Short-polling holds nothing open and the server never speaks first. Long-polling holds one request open, but the server still only ever *answers* that one open question, once. SSE holds a connection open and the server can push freely down it — but only one direction. WebSocket holds a connection open and *either side* can send at any time, unprompted. Push notifications are a different axis entirely: no connection from your server to the client at all — a third-party service (APNs/FCM) maintains its own connection to the device.

## Essential Definitions

- **Short-polling** — repeated ordinary HTTP requests on a fixed interval, immediate response every time regardless of change.
- **Long-polling** — a request the server intentionally holds open until new data exists or a timeout elapses.
- **SSE (Server-Sent Events)** — standardized one-way streaming over HTTP (`Content-Type: text/event-stream`); server pushes, client never re-requests.
- **WebSocket** — a distinct protocol (RFC 6455 upgrade from HTTP) providing persistent, full-duplex communication with no request/response pairing.
- **Push notifications (APNs/FCM)** — delivered via a platform service, reaching a device even when the app isn't running or has no active session.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Does the client need to send data as often as it receives it? | WebSocket — the only mechanism with genuine two-way push |
| Is the pattern strictly server-to-client, standard HTTP infra required? | SSE |
| Are updates infrequent enough a persistent connection isn't justified? | Long-polling |
| Does the client need updates even when the app isn't running? | Platform push notifications (APNs/FCM), not a connection-based mechanism |

**Trade-offs:**

| Mechanism | Connection held open? | Direction | Real measured cost signal |
|---|---|---|---|
| Short-polling | No | Client asks, server answers | 8 real requests for 1 real event |
| Long-polling | Yes, per pending request | Client asks, delayed answer | 1 real request for the same event |
| SSE | Yes | Server → client only | 5 real events, ~400ms apart, confirmed incremental |
| WebSocket | Yes | Both directions, either side initiates | 1 real unsolicited server push, no client request |

## Key Numbers (real, executed JDK server/client demos)

Polling comparison for the identical real event:

```
Short polling: Real requests made: 8, elapsed: 1483ms
Long polling:  Real requests made: 1, elapsed: 1306ms
```

SSE incrementality (real, spaced-apart arrival timestamps):

```
+38ms  data: real-event-1
+440ms  data: real-event-2
+845ms  data: real-event-3
```

WebSocket unsolicited push proof:

```
=== Waiting for a real, UNSOLICITED server-initiated push (no client request) ===
[client] received: unsolicited-real-time-push-from-server
Real unsolicited push received within 3s: true
```

## Common Pitfalls

- Defaulting to WebSocket for a feature that's genuinely one-way, adding unnecessary implementation complexity SSE would have avoided.
- Shipping a short-polling feature without considering real request-volume cost at scale.
- Assuming SSE supports client-to-server messaging over the same connection — strictly one-way by design.
- Confusing "push notifications" (platform-delivered via APNs/FCM) with "server push" (WebSocket/SSE over a live connection) — different problems.

## Interview Answer Skeleton

**30-sec:** Short-polling repeatedly asks and always gets an answer; long-polling holds the request open, cutting wasted requests dramatically. SSE is one-way, real-time push over plain HTTP. WebSocket is the only one with genuine two-way, either-side-initiates communication. Push notifications are structurally different — platform-delivered, working even when the app isn't running.

**2-min:** Add the measured request-count difference (8 vs. 1 for the identical event), the SSE incrementality proof (timestamps matching real send timing, not buffered), and the real from-scratch RFC 6455 handshake proving a genuine unsolicited server push.

**Whiteboard:** 2x2 grid — "connection held open?" (no/yes) × "who can speak first?" (client only/either side). Short-polling: no/client-only. Long-polling: yes/client-only. SSE: yes/server-can-push. WebSocket: yes/either-side. Push notifications sit entirely outside the grid, via a separate "platform push service" box.

**Staff-level framing:** Frame the choice as a real capacity-planning decision (connection count and server-side resource cost at target scale), and discuss pairing connection-based delivery with push notifications for messages a user must not miss.

## Production Warning Signs

- A live-price ticker built on short-polling triggering unrelated rate-limit alerts across the platform — replace with SSE, pushing updates only when data actually changes (this chapter's own measured fix: ~2/sec per user down to near-zero when stable).
- A WebSocket connection silently failing behind certain proxies/load balancers — the handshake succeeds but drops shortly after; SSE is a useful fallback since it needs no special intermediary support.
- An SSE connection appearing to receive nothing until it closes — check whether the server explicitly flushes after each event; an unflushed buffer defeats streaming.

## Related

- `handbook/system-design/api-gateway-bff-and-edge-concerns.md`
- `handbook/spring/spring-webflux-and-reactive-programming.md`
- `handbook/system-design/load-balancing-service-discovery-and-health-checking.md`
