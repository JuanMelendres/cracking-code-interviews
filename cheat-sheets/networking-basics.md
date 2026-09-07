---
title: "Cheat Sheet: Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer"
slug: networking-basics
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2005
canonical: ../syllabus/01-computer-science-foundations/networking-basics.md
last_updated: 2026-09-06
---

# Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer

**Canonical chapter:** [`syllabus/01-computer-science-foundations/networking-basics.md`](../syllabus/01-computer-science-foundations/networking-basics.md)

## Core Mental Model

TCP turns an unreliable network into a reliable, ordered byte stream; HTTP is simply an agreed-upon text format sent over that stream — `@RestController`/`RestTemplate`/`WebClient` exist purely to let engineers stop thinking about sockets and byte streams, until something at the wire level breaks that abstraction.

## Essential Definitions

- **TCP three-way handshake** — `SYN`, `SYN-ACK`, `ACK`; only after this completes is a connection open and ready for data — a real cost of at least one full network round-trip, paid before any application data flows.
- **IP address + port** — an IP identifies a machine, a port identifies a specific process/listening socket on it; the combination disambiguates which of many services on one machine a packet is meant for.
- **4-tuple** — (source IP, source port, destination IP, destination port) uniquely identifies a TCP connection, letting one server port serve thousands of simultaneous clients.
- **No message boundaries** — TCP delivers a byte stream with no built-in framing; HTTP invents its own via `Content-Length` (exact byte count up front) or `Transfer-Encoding: chunked` (length unknown until fully generated).
- **`keep-alive`** — HTTP/1.1's default reuse of one TCP connection across multiple requests, to amortize handshake cost — a genuine trade-off, since an idle kept-alive connection still consumes a socket and buffers on both ends.

## Decision Table

| Choice | Gains | Costs |
|---|---|---|
| New TCP connection per request | Simple, no shared-state lifecycle | Full handshake round-trip (plus TLS if HTTPS) every request |
| Pooled/reused connection (`keep-alive`) | Amortizes handshake cost across many requests | Idle connection still holds a real socket/buffers; needs an eviction policy |
| `Content-Length` framing | Simple; receiver knows exactly how many bytes to expect | Full body length must be known before the first byte is sent |
| Chunked transfer encoding | Body length can be unknown up front; supports streaming | More parsing complexity; not universally supported identically by every intermediary |

## Common Pitfalls

- Assuming HTTP is a binary protocol — HTTP/1.1 (still the default for most backend and browser traffic) is plain ASCII text; HTTP/2 and HTTP/3 add binary framing but keep the same text-derived semantic model.
- Treating a TCP connection as if it delivers discrete messages the way UDP or a Kafka record does — code that assumes "one read equals one complete message" will eventually get a partial message or two concatenated messages in one read.
- Underestimating the real cost of the TCP (and TLS) handshake — "open a fresh connection per request" pays a full round-trip every single time, which is exactly why pooling and keep-alive exist.
- `Content-Length` computed from a `String`'s character count rather than its UTF-8 byte length once the string has non-ASCII characters — a real, common framing bug.

## Interview Answer Skeleton

**30-sec:** DNS resolves the host, TCP opens via a three-way handshake (or reuses a pooled connection), TLS negotiates if HTTPS, then the client writes a plain-text HTTP request and the server writes back a plain-text response whose length is stated via `Content-Length` or chunked encoding.

**2-min:** Separate connection *establishment* cost (handshake, at least one round-trip; TLS adds another) from request *processing* cost, and name connection reuse (keep-alive/pooling) as the standard mitigation for the former — every connection pool in a system (database, outbound HTTP, message broker) is a finite shared resource.

**Staff-level framing:** Connection-lifecycle decisions (pool sizing, keep-alive timeouts, idle-connection reclamation) are cross-cutting infrastructure choices that are invisible when correct and expensive when wrong at scale — a slow outbound HTTP call held inside a database transaction starves a completely separate connection pool (the database's) under load, the same "scaling assumption baked in early, invisible until violated" pattern that shows up with complexity classes.

## Production Warning Signs

- Connection pool exhaustion from an HTTP call inside a transaction: a slow, blocking outbound HTTP call held open inside a database transaction ties up a database connection for the HTTP call's entire duration, silently exhausting the database's pool under load — a resource entirely separate from, but starved by, the outbound HTTP connection's own lifecycle.
- Symptom: a service calling a third-party API occasionally hangs for exactly the configured socket-read timeout, correlating with heavier load. Diagnose by checking outbound connection-pool metrics (active/idle/max) for exhaustion (request queued waiting for a pooled connection, nothing ever sent) versus capturing the actual outbound request with connection-level tracing to see whether bytes are flowing slowly or not at all (a genuinely slow or framing-ambiguous remote response).
- Design principle: each downstream dependency needs its own, independently sized connection pool rather than one shared pool across all of them — otherwise one dependency's stuck connections (framing ambiguity, handshake hangs) starve calls to otherwise-healthy dependencies.

## Real Measured Numbers

From the practice demo's raw-socket capture (no HTTP library on either end): the server listening on `127.0.0.1:54233` saw the client connect from `/127.0.0.1:54234`, matching the client's own logged local/remote port pair exactly. The response's `Content-Length: 37` header matched the real UTF-8 byte count of `{"message":"hello from a raw socket"}` exactly.

## Related

- syllabus/01-computer-science-foundations/os-process-thread-model.md
- syllabus/07-api-design/api-design.md
- syllabus/11-system-design/realtime-delivery-websocket-sse-and-long-polling.md
