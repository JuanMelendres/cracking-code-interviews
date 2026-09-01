---
title: "Flashcards: Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push"
slug: realtime-delivery-websocket-sse-and-long-polling
document_type: flashcard-deck
domain: system-design
topic_id: T-812
canonical: ../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md
last_updated: 2026-09-01
---

# Flashcards: Real-Time Delivery: WebSocket, SSE, Long-Polling, and Push

**Canonical chapter:** [`handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md`](../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md)

## Card: What's the real difference in request volume between short- and long-polling?

**Prompt:**
For the identical real event, how many requests did short-polling take to
detect it, versus long-polling?

**Answer:**
Short-polling took 8 real requests (polling every 200ms); long-polling took
1 real request (holding the connection open on a real `Object.wait()` until
the event genuinely existed) — measured directly for the same event and
comparable real detection latency.

**Why it matters:**
It's the concrete, measured justification for long-polling's real advantage:
fewer wasted requests, not less latency.

**Common trap:**
Assuming long-polling is primarily about reducing latency rather than
reducing wasted request count.

**Related:**
[handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md](../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md)

## Card: Is SSE actually streaming, or does it just look that way?

**Prompt:**
How can you prove an SSE endpoint is genuinely streaming events incrementally
rather than buffering them until the connection closes?

**Answer:**
Timestamp each event as the client receives it. Measured directly: 5 real
events arrived at ~38ms, ~440ms, ~845ms, ~1250ms, ~1656ms — spaced roughly
400ms apart, matching the server's real send interval, not all arriving
together at connection close.

**Why it matters:**
It's the real, decisive difference between genuine real-time push and a
slow, ordinary HTTP response that merely looks similar on paper.

**Common trap:**
Assuming any long-lived HTTP response is automatically "streaming" without
verifying the client actually receives data incrementally.

**Related:**
[handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md](../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md)

## Card: What makes WebSocket structurally different from SSE and long-polling?

**Prompt:**
What can a WebSocket server do that neither an SSE server nor a long-polling
server can?

**Answer:**
Send a message to the client at any time, completely unsolicited, with no
pending client request at all. Measured directly: a real WebSocket server
sent a real push message on its own 1-second timer, independent of any
client message, received and confirmed by the client.

**Why it matters:**
It's the real, decisive proof of WebSocket's core differentiator — genuine
two-way, either-side-initiates communication, not just a request that
eventually gets answered (long-polling) or one-way server push (SSE).

**Common trap:**
Believing SSE also supports "server push" in the same sense as WebSocket,
missing that SSE has no client-to-server channel at all.

**Related:**
[handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md](../handbook/system-design/realtime-delivery-websocket-sse-and-long-polling.md)
