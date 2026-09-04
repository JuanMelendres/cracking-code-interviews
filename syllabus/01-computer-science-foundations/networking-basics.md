---
title: "Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer"
slug: networking-basics
document_type: syllabus-topic
domain: 01-computer-science-foundations
topic_id: T-2005
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - os-process-thread-model.md
related:
  - os-process-thread-model.md
  - ../07-api-design/api-design.md
  - ../11-system-design/realtime-delivery-websocket-sse-and-long-polling.md
practice: ../../practice/java/cs-foundations/networking-basics/
production_scenarios:
  - ../../production-cookbook/connection-pool-exhaustion-from-an-http-call-in-a-transaction.md
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://www.rfc-editor.org/rfc/rfc9110
  - https://www.rfc-editor.org/rfc/rfc9293
---

# Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer

[The OS Process/Thread Model](os-process-thread-model.md) explains how one machine runs many concurrent things at once. This topic asks what happens when those "things" need to talk to a *different* machine — the wire-level mechanics [API Design](../07-api-design/api-design.md) and every Spring MVC controller sit on top of, but rarely have to name directly, because the framework has already handled them.

## 1. Why This Matters

`@RestController`, `RestTemplate`, `WebClient` — every one of these exists to let an engineer stop thinking about sockets, byte streams, and text-based request lines. That's a genuine, valuable abstraction, right up until something at the wire level breaks it: a connection pool exhausts, a request hangs past a timeout with no clear cause, a load balancer's health check behaves unexpectedly, or an interviewer asks "what actually happens when you type a URL into a browser and hit enter." An engineer who can only reason in terms of Spring's own annotations has no vocabulary for any of these; an engineer who knows what's underneath can debug the first three and answer the fourth from first principles rather than from memorized trivia.

## 2. Prerequisites

[The OS Process/Thread Model](os-process-thread-model.md) — specifically, that a socket is a real OS-managed resource, similar in spirit to a thread: something the operating system creates, tracks, and eventually reclaims, at a real cost.

## 3. Foundation (L1)

**Two computers on a network exchange raw bytes over a connection, and TCP (Transmission Control Protocol) is the layer that turns an unreliable underlying network into something that feels like a reliable, ordered, byte-by-byte stream** — the bytes you write on one end arrive on the other end in the same order you wrote them, or the connection reports a failure; nothing arrives silently corrupted, duplicated, or out of order, however messy the actual physical network in between happens to be. Every one of Java's `Socket` reads and writes operates on top of exactly this guarantee.

**Before either side can send anything, TCP requires a connection to be established — a "handshake"** — a brief up-front exchange whose entire purpose is for both sides to agree the connection is real and ready before any actual data flows. This handshake takes real, measurable time (at minimum, one full network round-trip), which is why "opening a new connection for every single request" is more expensive than reusing one already-open connection for several requests in a row — exactly the trade-off `HTTP/1.1`'s default `keep-alive` behavior (Section 5) is designed around.

**HTTP (Hypertext Transfer Protocol) is simply an agreed-upon text format sent over a TCP connection** — a request is a handful of plain-text lines (a method and a path, then headers, then optionally a body); a response is the same shape (a status line, then headers, then optionally a body). There is no special binary wire format most of the time (HTTP/1.1, still the most common version in ordinary backend work); it's genuinely just text, which the practice demo for this topic shows directly, byte for byte, with no HTTP library involved on either end.

## 4. Core Concepts (L2)

**The TCP three-way handshake, by name: `SYN`, `SYN-ACK`, `ACK`.** The connecting side sends a `SYN` ("synchronize") packet; the listening side replies with a `SYN-ACK` (acknowledging the first, and synchronizing its own side); the connecting side replies with a final `ACK`. Only after this three-step exchange completes is the connection considered open and ready for actual application data — this is the concrete mechanism behind "TCP handshake latency," a real, physical cost every new connection pays, independent of anything the application itself is doing.

**An IP address identifies a machine; a port number identifies a specific process (or specific listening socket) on that machine.** A single machine can run many independent network services simultaneously — a web server on port `8080`, a database on port `5432` — because the combination of IP address and port disambiguates which one a given packet is meant for. The practice demo's client and server end up on two genuinely different port numbers (one the server explicitly listens on, one the OS assigns the client automatically) — a real, concrete instance of exactly this identification mechanism.

**TCP delivers a byte stream with no built-in message boundaries — HTTP has to invent its own framing on top of it.** This is precisely why an HTTP response must state its own length: either a `Content-Length` header giving an exact byte count up front, or `Transfer-Encoding: chunked` for a body whose length isn't known until it's fully generated. Without one of these, a client reading from a TCP stream has no way to know where one HTTP message ends and (on a reused connection) the next one begins.

**HTTP request methods (`GET`, `POST`, `PUT`, `DELETE`, and the rest) and status codes (the `2xx`/`4xx`/`5xx` families) are conventions the *text itself* encodes — not something TCP or the network layer knows or enforces in any way.** TCP is entirely indifferent to whether the bytes flowing over it spell out an HTTP request, a completely different protocol, or nothing meaningful at all; HTTP's semantics live entirely in how the bytes are formatted and interpreted by both ends, by mutual agreement.

## 5. How It Works Internally (L3)

**Each TCP connection is uniquely identified by a 4-tuple: (source IP, source port, destination IP, destination port).** This is what allows a server on one single port (say, `8080`) to serve thousands of simultaneous clients: every client connects from a different source IP and/or source port, so the OS can demultiplex incoming packets to the correct individual connection even though they're all headed to the exact same destination port. The practice demo's server accepts exactly one connection and shows its remote address directly (`/127.0.0.1:54234`) — a live instance of one entry in that 4-tuple space.

**Every open TCP connection consumes a real, finite OS resource** — a socket, complete with kernel-managed send and receive buffers, and an entry in the OS's connection-tracking tables — which is precisely why connection pooling exists at every layer of a typical backend stack (a JDBC connection pool, an `HttpClient`'s connection pool, a browser's own per-host connection limit): reusing an already-open, already-handshaken connection avoids paying the handshake cost (Section 3) again, and avoids exhausting the OS's or the remote server's finite pool of concurrently open sockets.

**HTTP/1.1's `keep-alive` default reuses one TCP connection across multiple sequential requests**, explicitly to amortize the handshake cost across many requests instead of paying it once per request. This is a genuine trade-off, not a pure win: a kept-alive connection sits idle between requests, still consuming a socket and its buffers on both ends, and a server handling many clients must decide how long to keep an idle connection open before reclaiming it — exactly the kind of finite-resource accounting [The OS Process/Thread Model](os-process-thread-model.md) already introduced for threads, now applied to sockets instead.

## 6. Practical Usage

- **Reading a `curl -v` or a browser's network-tab timing breakdown as literally showing these phases** — DNS lookup, TCP handshake, TLS handshake (if HTTPS), time-to-first-byte, content download — each one a real, separately measurable phase of what this topic describes, not an opaque single number.
- **Recognizing `Connection: keep-alive` / `Connection: close` headers, and connection-pool configuration (`maxIdleConnections`, `maxTotal`) on an HTTP client, as directly implementing Section 5's reuse trade-off** — not arbitrary configuration knobs, but a direct expression of "how many handshakes am I willing to pay for, in exchange for how many idle sockets."
- **Treating `Content-Length` mismatches or missing terminators as a framing bug, not a mysterious "connection hung" symptom** — Section 4's core insight (TCP has no message boundaries; HTTP invents its own) is the direct diagnostic lens for "the client is waiting for more bytes that are never coming."

## 7. Examples

```
GET /status HTTP/1.1\r\n
Host: 127.0.0.1:54233\r\n
User-Agent: RawHttpDemo/1.0\r\n
Connection: close\r\n
\r\n
```

The complete, real HTTP request the practice demo's client sent — captured exactly as the server received it, with no HTTP library on either end, `\r\n` line endings and all.

```
HTTP/1.1 200 OK\r\n
Content-Type: application/json\r\n
Content-Length: 37\r\n
Connection: close\r\n
\r\n
{"message":"hello from a raw socket"}
```

The complete, real response, built by hand as a plain string and written directly to the socket's output stream — no `HttpServletResponse`, no Spring, no serialization library, and the wire format is identical in shape to what any of those would have produced.

## 8. Common Mistakes

- **Assuming HTTP is a binary protocol, or some format meaningfully different from "text."** HTTP/1.1 (still the default for most backend-to-backend and browser traffic) is plain ASCII text, exactly as the practice demo's captured bytes show — HTTP/2 and HTTP/3 do introduce binary framing, but the request/response *semantics* (methods, headers, status codes) remain conceptually the same text-derived model underneath.
- **Treating a TCP connection as if it delivers discrete messages**, the way, say, a UDP datagram or a Kafka record does. TCP is a byte *stream* with no inherent framing — code that reads from a raw socket and assumes "one read equals one complete message" (rather than relying on `Content-Length` or an equivalent explicit framing rule) will eventually receive a partial message, or two messages concatenated in one read, especially under load or with larger payloads than this demo's single small response.
- **Underestimating the real cost of the TCP (and, for HTTPS, TLS) handshake** by treating "open a fresh connection per request" as free — it's a full network round-trip (or more, for TLS) paid every single time, which is exactly why connection pooling and keep-alive exist at every layer of a real system.

## 9. Edge Cases

- **A connection can be "half-open"**: one side has sent a `FIN` (indicating "I'm done sending") but continues to read data the other side still sends — TCP connections are full-duplex, and closing one direction doesn't automatically close the other, a real source of subtle connection-lifecycle bugs in code that assumes closing is instantaneous and symmetric.
- **`Content-Length` and the actual number of bytes sent can disagree** if application code miscounts (a common bug: computing length from a `String`'s character count rather than its UTF-8 byte length once the string contains non-ASCII characters, since UTF-8 encodes many characters as more than one byte) — a real, concrete example of exactly the class of bug the practice demo's `.getBytes(StandardCharsets.UTF_8).length` call (rather than `.length()`) is written specifically to avoid.
- **A TCP handshake can fail or hang without a clean error** if a firewall silently drops packets instead of rejecting them, producing a symptom (a connection attempt that never completes, without a fast, explicit refusal) qualitatively different from "the destination is unreachable" — a real, common cause of a request appearing to hang forever with no immediately obvious cause in application logs.

## 10. Performance Implications

The practice demo's real, executed output shows the concrete evidence for two of this topic's core claims directly rather than by assertion:

- **Distinct local and remote ports for the same logical connection** — the server, listening on `127.0.0.1:54233`, saw the client connect from `/127.0.0.1:54234`; the client's own log line independently confirms the identical pair (`local port 54234 -> remote port 54233`) — real, matching evidence from both ends of the same real TCP connection, not a value either side merely asserted about itself.
- **The exact request and response bytes**, `\r\n`-terminated, with a blank line separating headers from body, and a `Content-Length: 37` header whose value genuinely matches the response body's real byte count — the practical, wire-level confirmation of Section 4's framing claim, checkable by counting the characters in `{"message":"hello from a raw socket"}` and confirming it's exactly 37 UTF-8 bytes.

Beyond what this specific demo measures, the general, real-world performance shape worth internalizing: a fresh TCP handshake costs at least one network round-trip (commonly single-digit to low tens of milliseconds even on a healthy connection, dominated by physical distance and network conditions, not CPU), and TLS adds at least one more round-trip on top of that for HTTPS — which is precisely why connection reuse (Section 5) has a real, measurable latency payoff at any nontrivial request volume, not just a theoretical one.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| A new TCP connection per request | Simple; no shared-state lifecycle to manage | Pays a full handshake round-trip (plus TLS, if HTTPS) on every single request |
| A pooled, reused connection (`keep-alive`) | Amortizes the handshake cost across many requests | An idle connection still holds a real socket and buffers on both ends; requires an eviction/timeout policy for connections that go stale |
| `Content-Length`-based framing | Simple; the receiver knows exactly how many bytes to expect | The full body length must be known before the first byte is sent — awkward for genuinely streamed or dynamically generated content |
| Chunked transfer encoding | Body length can be unknown up front; supports streaming | More parsing complexity on the receiving end; not universally supported by every intermediary in exactly the same way |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is diagnosing a networking symptom by locating which specific layer it belongs to, rather than treating "the request failed" as one undifferentiated category. A connection that hangs indefinitely with no error (Section 9's silent-firewall-drop case) points at the TCP layer; a connection that opens instantly but then the body never completes (a `Content-Length` mismatch, Section 9) points at the HTTP framing layer; a connection pool that's exhausted under load (Section 14) points at the resource-accounting layer described in Section 5. Naming the correct layer immediately narrows where to actually look — logs and metrics at the wrong layer (retrying application logic when the real problem is a stuck TCP handshake, for instance) waste real debugging time on a system that's actually healthy at that layer.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, connection-lifecycle decisions (pool sizing, keep-alive timeouts, how aggressively to reclaim idle connections) are exactly the kind of cross-cutting infrastructure choice that's invisible when correct and expensive when wrong at scale — [Connection Pool Exhaustion from an HTTP Call in a Transaction](../../production-cookbook/connection-pool-exhaustion-from-an-http-call-in-a-transaction.md) is a real instance of this: a slow outbound HTTP call held inside a database transaction ties up a database connection for the HTTP call's entire duration, and under load, this pattern silently exhausts the database connection pool — a resource entirely separate from, but starved by, the outbound HTTP connection's own lifecycle. The Staff-level pattern is recognizing that **every connection pool in a system (database, outbound HTTP, message broker) is a finite shared resource whose sizing assumptions can be violated by a code path in a completely different part of the system** — exactly the same "scaling assumption baked in early, invisible until violated" pattern [Algorithmic Complexity](algorithmic-complexity-and-big-o-from-first-principles.md)'s own Staff-level section (Section 13 there) names for complexity classes, now applied to network resources instead of CPU cycles.

## 14. Production Scenarios

- **[Connection Pool Exhaustion from an HTTP Call in a Transaction](../../production-cookbook/connection-pool-exhaustion-from-an-http-call-in-a-transaction.md)** — a slow, blocking outbound HTTP call (Section 3's handshake and round-trip cost, compounded by whatever latency the remote service itself adds) held open inside a database transaction, starving an entirely separate connection pool (the database's) under load — exactly Section 13's cross-resource scaling-assumption failure.

## 15. Interview Questions

### Question 1 — Walk me through what happens, at the network level, between a client sending an HTTP request and receiving a response.

**Why interviewers ask it.** It's a broad, standard check for whether a candidate's mental model of "making an API call" includes the actual wire-level mechanics, or stops entirely at "the framework sends it."

**Expected answer.** DNS resolves the target hostname to an IP address (if not already cached). A TCP connection is established via the three-way handshake (`SYN`, `SYN-ACK`, `ACK`) — or an existing pooled/kept-alive connection is reused, skipping this step. For HTTPS, a TLS handshake follows, negotiating encryption. The client then writes the HTTP request as plain text (a request line, headers, optionally a body) onto that connection. The server reads it, processes it, and writes back an HTTP response in the same text-based shape, with its length indicated by `Content-Length` or chunked encoding. Depending on `Connection` headers, the TCP connection either closes or stays open for reuse.

**Minimum acceptable answer.** Names DNS, a TCP connection, and an HTTP request/response, even without full handshake detail.

**Strong Senior answer.** Explicitly separates connection *establishment* cost from request *processing* cost, and names connection reuse (keep-alive/pooling) as the standard mitigation for the former — grounded in a real understanding of why the handshake costs a round-trip, not just that it exists.

**Staff-level extension.** Connects this to a real, cross-system resource-accounting failure (Section 13's production scenario) — that connection pools at every layer are finite, shared resources whose exhaustion in one code path can be caused by a seemingly unrelated code path elsewhere in the system.

**Common mistakes.** Skipping straight to "the controller method runs," treating everything below the framework's own entry point as invisible or irrelevant.

**Follow-up questions.** "What's the difference in cost between a fresh connection and a reused one?" (At least one full round-trip for the handshake, plus another for TLS if HTTPS — Section 10.) "What happens if the server never sends `Content-Length` and never closes the connection?" (The client hangs waiting for more bytes that never arrive or never get an unambiguous end signal — Section 9's framing-ambiguity edge case.)

### Question 2 — Why does TCP need a three-way handshake instead of just sending data immediately?

**Why interviewers ask it.** It probes whether a candidate understands TCP's reliability guarantee is actively constructed (via this handshake and ongoing acknowledgment), not a passive property of "the network."

**Expected answer.** The handshake lets both sides confirm the other is reachable and ready, and lets both sides agree on initial sequence numbers used to track which bytes have been sent and acknowledged for the rest of the connection's life. Without it, a side could start sending data into a connection the other end isn't actually prepared to receive, or the two sides could disagree about where the byte stream "starts," breaking the ordered-delivery guarantee Section 3 describes.

**Minimum acceptable answer.** Knows the handshake exists to set up the connection before data flows, even without naming sequence numbers specifically.

**Strong Senior answer.** Connects the handshake's cost directly to a real design decision — e.g., why keep-alive and connection pooling exist specifically to avoid paying this cost repeatedly (Section 5).

**Staff-level extension.** Names a real scenario where handshake cost at scale becomes an actual system constraint — for instance, a service making a fresh outbound HTTP call per request to a downstream dependency under high request volume, where handshake and TLS-negotiation overhead becomes a measurable fraction of total latency, motivating a connection-pooled client instead.

**Common mistakes.** Describing the handshake as pure formality or overhead with no functional purpose, rather than the actual mechanism by which both sides agree the connection is real and synchronized.

**Follow-up questions.** "What would happen without it — could two computers still communicate?" (UDP is the real-world answer: no handshake, no ordering guarantee, no reliable delivery — a genuinely different trade-off, appropriate for different use cases like real-time media, not for most HTTP APIs.) "Does HTTP/2 or HTTP/3 still use this same handshake?" (HTTP/2 still runs over TCP, so yes; HTTP/3 runs over QUIC, built on UDP, with its own different connection-establishment mechanism — a real, current exception worth knowing exists, if not necessarily its full mechanics.)

## 16. Coding/Practice Exercises

- Run [`RawHttpDemo.java`](../../practice/java/cs-foundations/networking-basics/src/RawHttpDemo.java) yourself, then modify the server to deliberately send a `Content-Length` value smaller than the real body it sends — observe from the client's side what happens to the extra, unaccounted-for bytes, and connect the result back to Section 9's framing-ambiguity edge case.
- Extend the demo to keep the connection open (`Connection: keep-alive`, and don't close the socket after one request/response) and send two requests in sequence over the same TCP connection — confirm both work without a second handshake, and inspect whether the two requests' local port numbers are identical (they should be — same connection, same 4-tuple).
- Use `curl -v` against any real HTTP endpoint you have access to and match its verbose output's phases (`* Trying ...`, `* Connected to ...`, `> GET ...`, `< HTTP/1.1 200 ...`) against this topic's own Section 3/5 vocabulary — identify exactly which line corresponds to the TCP handshake completing versus the HTTP request being sent.

## 17. Debugging Exercises

**Symptom:** a service that calls a third-party API occasionally hangs for exactly the configured socket-read timeout duration, with no error in the third-party service's own logs, and the issue seems to correlate with the service being under heavier-than-usual load.

**Diagnose:** distinguish two structurally different possible causes this topic's vocabulary now separates cleanly — (a) the outbound connection pool to the third-party API is exhausted, so the request waits for a connection to become free rather than actually being sent at all (Section 13's shared-finite-resource pattern), versus (b) the TCP connection and HTTP request genuinely reach the third party, but its response is slow or the response's framing is ambiguous, so the client waits at the socket-read level for bytes that are simply late, not missing (Section 9). Walk through how you'd tell these apart without guessing: check whether outbound connection-pool metrics (active vs. idle vs. max) show exhaustion at the time of the symptom — if they do, it's (a), and no network request was actually delayed, just queued waiting for a pooled connection; if the pool has headroom, capture the actual outbound request with connection-level tracing or a local proxy to see whether bytes are flowing slowly or not flowing at all, pointing at (b).

## 18. Design Exercises

**Design constraint:** a service makes outbound HTTP calls to five different downstream dependencies, each with different, independently variable latency and reliability characteristics, and a slow or failing dependency must never be allowed to exhaust resources needed by calls to the other four.

Design the outbound HTTP client configuration: state explicitly why each downstream dependency needs its **own**, independently sized connection pool (rather than one shared pool across all five) — directly derived from Section 5's "every connection pool is a finite, shared resource" principle, now applied across *multiple* pools rather than one — and name the specific failure this isolation prevents: one dependency's connections all becoming stuck (Section 9's framing-ambiguity or handshake-hang edge cases) starving out calls to the other four dependencies that would otherwise be healthy. Connect this directly to why [Connection Pool Exhaustion from an HTTP Call in a Transaction](../../production-cookbook/connection-pool-exhaustion-from-an-http-call-in-a-transaction.md)'s root cause — one slow call type consuming a shared, finite pool meant for unrelated work — is exactly the failure mode per-dependency pool isolation is designed to prevent.

## 19. Further Reading

- [RFC 9293 — Transmission Control Protocol (TCP)](https://www.rfc-editor.org/rfc/rfc9293) — the current, authoritative specification of the three-way handshake and TCP's reliable-byte-stream guarantee referenced in Sections 3–4.
- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110) — the current, authoritative specification of HTTP's request/response text format, methods, and status codes referenced throughout this chapter.
- [API Design](../07-api-design/api-design.md) — the layer directly above this topic: how these wire-level mechanics get shaped into an actual, well-designed HTTP API surface.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, that HTTP is text sent over a TCP connection, and that a TCP handshake happens before any data can flow | [Section 3](#3-foundation-l1) |
| L2 | Name the three-way handshake's steps, explain what an IP address and a port each identify, and explain why HTTP needs `Content-Length` or chunked encoding at all | [Interview Question 2](#question-2--why-does-tcp-need-a-three-way-handshake-instead-of-just-sending-data-immediately) |
| L3 | Explain the 4-tuple that uniquely identifies a TCP connection, and why connection pooling and keep-alive exist as a direct consequence of handshake cost and finite OS socket resources | [Section 10's real measurements](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real production symptom (Section 17) by correctly locating which network layer it belongs to, and design a system that isolates independent connection pools to prevent one dependency's failure from starving the others (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
