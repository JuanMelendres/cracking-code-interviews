---
title: "Flashcards: Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer"
slug: networking-basics
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2005"
canonical: ../syllabus/01-computer-science-foundations/networking-basics.md
last_updated: 2026-09-07
---

# Flashcards: Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer

**Canonical chapter:** [`syllabus/01-computer-science-foundations/networking-basics.md`](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: TCP turns an unreliable network into a reliable byte stream

**Prompt:**
What guarantee does TCP provide on top of an inherently unreliable physical network?

**Answer:**
TCP makes the connection feel like a reliable, ordered, byte-by-byte stream: bytes written on one end arrive on the other end in the same order they were written, or the connection reports a failure — nothing arrives silently corrupted, duplicated, or out of order.

**Why it matters:**
Every one of Java's `Socket` reads and writes operates on top of exactly this guarantee — it's the foundation HTTP, and everything built on HTTP, silently relies on.

**Common trap:**
Assuming TCP delivers discrete messages the way a UDP datagram or a Kafka record does, rather than an undifferentiated byte stream.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: The three-way handshake, by name

**Prompt:**
Name the three steps of the TCP handshake, and state what has to happen before either side can send application data.

**Answer:**
`SYN` (the connecting side sends a synchronize packet), `SYN-ACK` (the listening side acknowledges and synchronizes its own side), `ACK` (the connecting side's final acknowledgment). Only after this three-step exchange completes is the connection considered open and ready for actual data.

**Why it matters:**
This handshake costs at least one full network round-trip, which is exactly why opening a fresh connection per request is more expensive than reusing an already-open one (`keep-alive`).

**Common trap:**
Describing the handshake as pure formality or overhead with no functional purpose, rather than the actual mechanism by which both sides agree the connection is real and synchronized.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: HTTP/1.1 is plain text, not a binary format

**Prompt:**
Is an HTTP/1.1 request or response a special binary wire format?

**Answer:**
No — it's genuinely plain ASCII text sent over a TCP connection: a request is a method and path line, then headers, then optionally a body; a response is a status line, then headers, then optionally a body, all `\r\n`-terminated. (HTTP/2 and HTTP/3 do introduce binary framing, but the request/response semantics remain conceptually the same text-derived model.)

**Why it matters:**
It demystifies what a framework like Spring MVC is actually producing underneath `@RestController` — the same shape any hand-written socket program would produce.

**Common trap:**
Assuming HTTP is a binary protocol, or some format meaningfully different from plain text, for the still-dominant HTTP/1.1 case.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: TCP has no message boundaries — HTTP invents its own

**Prompt:**
Since TCP delivers an undifferentiated byte stream with no built-in message boundaries, how does an HTTP client know where a response ends?

**Answer:**
HTTP states its own length explicitly: either a `Content-Length` header giving an exact byte count up front, or `Transfer-Encoding: chunked` for a body whose length isn't known until it's fully generated. Without one of these, a client reading from the stream has no way to know where one message ends and the next begins.

**Why it matters:**
It's the direct diagnostic lens for "the client is waiting for more bytes that are never coming" — a `Content-Length` mismatch is a framing bug, not a mysterious hang.

**Common trap:**
Assuming "one read from the socket equals one complete message," which eventually breaks under load or with larger payloads.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: A TCP connection is identified by a 4-tuple

**Prompt:**
How can a single server, listening on one port (say, `8080`), simultaneously serve thousands of different clients?

**Answer:**
Each TCP connection is uniquely identified by a 4-tuple: (source IP, source port, destination IP, destination port). Every client connects from a different source IP and/or source port, so the OS can demultiplex incoming packets to the correct individual connection even though they're all headed to the same destination port.

**Why it matters:**
It's the concrete mechanism behind "how does one port handle many clients" — a question that otherwise sounds like a contradiction.

**Common trap:**
Assuming a listening port can only handle one connection at a time, or that all connections to the same port are somehow the same connection.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)

## Card: `keep-alive` amortizes handshake cost but isn't free

**Prompt:**
What does HTTP/1.1's `keep-alive` default actually trade off, compared to opening a new connection per request?

**Answer:**
It reuses one TCP connection across multiple sequential requests, avoiding the handshake round-trip cost on every request. The cost in exchange: a kept-alive connection sits idle between requests while still consuming a real socket and its send/receive buffers on both ends, and a server must decide how long to keep an idle connection open before reclaiming it.

**Why it matters:**
Connection-pool configuration knobs (`maxIdleConnections`, idle timeouts) are a direct expression of this trade-off, not arbitrary settings — every open connection is a real, finite OS resource.

**Common trap:**
Treating "open a fresh connection per request" as free, or treating `keep-alive` as a pure win with no resource cost of its own.

**Related:**
[syllabus/01-computer-science-foundations/networking-basics.md](../syllabus/01-computer-science-foundations/networking-basics.md)
