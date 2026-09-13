---
title: "Cheat Sheet: gRPC API Design"
slug: grpc-api-design
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-918
canonical: ../syllabus/07-api-design/grpc-api-design.md
last_updated: 2026-09-12
---

# gRPC API Design

**Canonical chapter:** [`syllabus/07-api-design/grpc-api-design.md`](../syllabus/07-api-design/grpc-api-design.md)

## Core Mental Model

gRPC trades REST's flexible, independently-interpreted request/response format for a compiler-enforced contract and a native streaming model, at the cost of human-readability and direct browser access. Client and server are generated from the same `.proto` file by the same compiler — no separate parsing step for either side to disagree on.

## Essential Definitions

- **`.proto` contract** — the single source both client and server code are generated from.
- **Unary / server streaming / client streaming / bidirectional streaming** — the four real call shapes, determined by `stream` keyword placement on request/response.
- **Deadline** — a required, explicit timeout on every outbound call; its absence is a real cascading-failure risk, not a minor inefficiency.

## Decision Table

| Situation | What to reach for |
|---|---|
| Internal service-to-service call, request/response | Unary |
| Server needs to push many results over time for one request | Server streaming |
| Client needs to send many messages before getting one result | Client streaming |
| Both sides need to send/receive independently on one connection | Bidirectional streaming |
| Any outbound gRPC call | Always set an explicit deadline |
| Public/browser-facing API | REST (or gRPC-Web if gRPC is required) |

## Common Pitfalls

- Describing gRPC's strictness benefit vaguely ("it's stricter") without naming the shared-codegen mechanism that actually causes it.
- Confusing client streaming with bidirectional streaming — client streaming still returns exactly one response.
- Treating a missing deadline as a minor inefficiency rather than a real cascading-failure risk (a slow downstream call holding the caller's thread indefinitely, exhausting the thread pool under concurrent load).
- Reaching for gRPC on a public/browser-facing API without accounting for the proxy layer (gRPC-Web) it needs there.

## Interview Answer Skeleton

**30-sec:** gRPC compiles a single `.proto` contract into real client and server code over HTTP/2 with Protocol Buffers — compiler-enforced type safety and native streaming REST lacks. Trade-off: not human-readable, needs a proxy for browser access.

**2-min:** Add: all four call shapes are real, native protocol features (demonstrated with a real 3-message server stream and a real 3-message client stream); gRPC errors are real exceptions carrying a protocol-level status code, not a parsed response field.

**Staff-level framing:** Every internal gRPC call needs an explicit deadline — a real, measured production scenario shows a missing deadline turning one slow downstream call into a thread-pool-exhaustion cascade affecting unrelated requests, not just a slow individual call.

## Related

- syllabus/07-api-design/graphql-api-design.md
- syllabus/07-api-design/rest-api-fundamentals.md
- syllabus/07-api-design/api-design.md
