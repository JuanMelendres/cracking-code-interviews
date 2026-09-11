---
title: "Flashcards: WebSocket and Server-Sent Events for Real-Time UI"
slug: websocket-and-server-sent-events-for-realtime-ui
document_type: flashcard-deck
domain: 21-frontend-web
topic_id: F-402
canonical: ../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
last_updated: 2026-09-11
---

# Flashcards: WebSocket and Server-Sent Events for Real-Time UI

**Canonical chapter:** [`syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md`](../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)

## Card: Who reconnects a dropped connection

**Prompt:**
After a WebSocket closes and after an EventSource's connection drops, which one reconnects automatically, and why?

**Answer:**
`EventSource` does, automatically — the WHATWG HTML spec defines a real reconnection algorithm as part of the API itself. A plain `WebSocket` does not — RFC 6455 defines open/close mechanics only, with reconnection left entirely to application code. This chapter's own demo proves both halves directly: a real closed WebSocket stays closed; a real EventSource reconnects three times with zero client-side retry code.

**Why it matters:**
This is a protocol-level fact, not a library quirk — it directly decides how much reconnection code a given real-time feature actually needs to write.

**Common trap:**
Assuming any "real-time" browser API reconnects automatically, or assuming none of them do.

**Related:**
[WebSocket and Server-Sent Events for Real-Time UI](../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)

## Card: Why backoff, not immediate retry

**Prompt:**
Why does a WebSocket reconnect wrapper use increasing delays (backoff) instead of retrying immediately?

**Answer:**
An immediate, unlimited retry loop means every disconnected client hits the server again the instant it drops — if the server was down or restarting, all of them arriving back at once (a "reconnect storm") can overwhelm it right as it recovers. Backoff spaces reconnect attempts out over increasing delays, verified in this chapter's own demo with a real, measured 300ms/600ms/1200ms schedule.

**Why it matters:**
It's the standard, expected answer to "what's wrong with naive reconnect logic" in a real-time-feature interview.

**Common trap:**
Proposing a fixed retry delay instead of an increasing one, which still risks synchronized retries across many clients.

**Related:**
[WebSocket and Server-Sent Events for Real-Time UI](../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)
