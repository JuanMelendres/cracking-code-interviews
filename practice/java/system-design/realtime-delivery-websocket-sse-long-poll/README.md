# Real-time delivery: WebSocket, SSE, and long-polling (T-812) — runnable verification

Real, executed Java 21 output backing
[`syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md`](../../../../syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md)
(T-812). No framework, no library — a real long-polling server holding a
request open on a real monitor wait, a real Server-Sent Events endpoint
streaming over chunked transfer encoding, and a real, minimal RFC 6455
WebSocket server implemented directly over `java.net.ServerSocket`, tested
against the JDK's own real, built-in `java.net.http.WebSocket` client.
Follows the same plain-JDK, no-dependency pattern established in
[`practice/java/system-design/load-balancing-and-health-checking/`](../load-balancing-and-health-checking/README.md).

## Files

- `ShortPollServer.java`, `LongPollServer.java` — real HTTP endpoints
  demonstrating both polling strategies; `PollingComparisonDemo.java` measures
  the real request-count difference between them for the identical event.
- `SseServer.java` — a real `text/event-stream` endpoint using chunked
  transfer encoding to stream events over real time;
  `SseClientDemo.java` consumes it and timestamps each event as it actually
  arrives.
- `WebSocketServer.java` — a real, minimal RFC 6455 WebSocket server: a real
  handshake (`Sec-WebSocket-Accept` computed via SHA-1 + Base64), real frame
  parsing/writing, scoped to small text messages for legibility.
  `WebSocketClientDemo.java` drives it using `java.net.http.WebSocket`, the
  JDK's own real, built-in WebSocket client.

## Run

```bash
cd practice/java/system-design/realtime-delivery-websocket-sse-long-poll
mkdir -p out
javac -d out *.java
java -cp out PollingComparisonDemo
java -cp out SseClientDemo
java -cp out WebSocketClientDemo
```

## Real observed output (last full run, Java 21)

### 1. `PollingComparisonDemo` — the real request-count cost of each strategy

```
=== Short polling: client polls every 200ms until it observes the real event ===
Observed: real-event-fired
Real requests made: 8, elapsed: 1483ms

=== Long polling: client makes ONE request that blocks until the real event arrives ===
Observed: real-event-fired
Real requests made: 1, elapsed: 1306ms

Real comparison for the SAME event, arriving at the same real ~1300ms: short polling took 8 real requests; long polling took 1 real request.
```

Both scenarios wait for the identical real event, published by a background
thread after the same real ~1300ms delay. Short polling makes a real HTTP
request every 200ms regardless of whether there's anything new, paying for 8
real round trips to detect one real event. Long polling holds a single real
connection open (blocked on a real `Object.wait()`, not a busy loop) until
the event genuinely exists, needing exactly 1 real request for the same
detection latency.

### 2. `SseClientDemo` — real, incremental streaming, not buffer-then-send

```
=== Real SSE events, timestamped as they actually arrive ===
+38ms  data: real-event-1
+440ms  data: real-event-2
+845ms  data: real-event-3
+1250ms  data: real-event-4
+1656ms  data: real-event-5
```

The real, ~400ms-apart timestamps are the actual proof point: if the server
were buffering all 5 events and sending them at connection close, every line
would print at nearly the same elapsed time. Instead, each real chunk arrives
and is processed by the client as the server actually writes and flushes it —
genuine one-way, real-time server push over a single, long-lived HTTP
connection.

### 3. `WebSocketClientDemo` — a real, full-duplex connection, both directions proven

```
=== Real WebSocket handshake complete -- client sending a real message ===
[client] received: echo: hello from client
Real echo received within 3s: true

=== Waiting for a real, UNSOLICITED server-initiated push (no client request) ===
[client] received: unsolicited-real-time-push-from-server
Real unsolicited push received within 3s: true
```

The echo proves client-to-server messaging works through a real, from-scratch
RFC 6455 handshake and frame parser. The unsolicited push — a message the
client never asked for, sent by the server on its own 1-second timer — is the
real, decisive proof of WebSocket's core differentiator from SSE and
long-polling: genuine two-way, server-can-initiate-anytime communication over
one persistent connection, not just a request that eventually gets answered.

## Real discoveries made while building this pack

One real bug was hit and fixed while building `WebSocketServer.java`: the
first version used `Executors.newSingleThreadExecutor().submit(...)` per
accepted connection and never shut the executor down. `ExecutorService`
thread pools use non-daemon threads by default, so every accepted connection
left behind a live, non-daemon thread pool — the demo's own real output was
completely correct (the echo and the unsolicited push both worked), but the
JVM process never exited afterward, since at least one non-daemon thread was
always still alive. The fix: replace the per-connection executor with a
plain `Thread` explicitly marked `setDaemon(true)` — a daemon thread has no
executor-shutdown lifecycle to forget, and the JVM exits cleanly once all
real demo work finishes. This is a real, easy-to-miss thread-lifecycle gotcha
specific to hand-rolling connection handling directly over raw sockets,
rather than relying on a framework's connection-pool management.
