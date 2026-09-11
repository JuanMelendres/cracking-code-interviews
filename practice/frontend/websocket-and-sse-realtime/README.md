# WebSocket vs. Server-Sent Events (SSE) — Real Reconnection Demo

Real, executed output backing
[`syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md`](../../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md)
(F-402). A real Node `ws` WebSocket server plus a real, headless Chromium
browser (via Playwright) using the browser's own native `WebSocket` and
`EventSource` APIs — not a description of the reconnection behavior, and
not a Node-only client that would miss what the browser engine itself
actually does.

## Run it

```bash
npm install
npx playwright install chromium
npm run start
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **A native browser `WebSocket` does not reconnect on its own after the
  server closes it.** Scenario 1: connect, echo round trip, server sends
  a real close frame (`code=4001`, `reason=demo-server-initiated-close`).
  1.5 real seconds later, the server's own connection counter is still
  `1` — no new connection ever arrived, because nothing tells a plain
  `WebSocket` to reconnect. That has to be application code.
- **A real, measured exponential-backoff reconnect wrapper.** Scenario 2
  writes exactly that missing application code: on each close, wait a
  real delay (300ms, 600ms, 1200ms), then reconnect. The transcript
  reports the *actual* elapsed milliseconds between close and reconnect
  for all three attempts (301ms, 601ms, 1202ms against a 300/600/1200ms
  schedule) — measured, not asserted — and the server's connection
  counter reaching `5` (1 initial + 1 from scenario 1 + 3 reconnects here)
  confirms each reconnect is a genuinely new connection, not the old one
  revived.
- **A native browser `EventSource` (SSE) reconnects on its own, with zero
  reconnect code.** Scenario 3 registers only `open` and `message`
  listeners — no `error` handler, no retry logic at all. The server
  deliberately ends the HTTP response after 3 ticks (simulating a dropped
  connection). Over 2.2 real seconds, the transcript shows **3 separate
  `open` events** and the server's own connection counter reaching `3` —
  each one a genuine new HTTP request the browser made by itself, per the
  WHATWG HTML spec's EventSource reconnection algorithm. The server's
  `retry: 300` field (a real field in the event-stream wire format, not a
  header) is what shortens the browser's default ~3000ms reconnection
  delay for this demo.

## Why this contrast matters

The same underlying question — "the connection dropped, now what?" — has
two genuinely different real answers depending on which API a frontend
engineer picks: `WebSocket` requires the developer to notice the drop and
re-establish it (this repo's own demo shows exactly how much code that
takes, and how to measure that it's working); `EventSource` was
specifically designed for the read-only server-to-client streaming case
and handles that reconnection natively, at the cost of only supporting
one-way communication. Neither is "better" in the abstract — the decision
is scoped in the corresponding syllabus chapter's own Decision Framework.
