---
title: "Flashcards: gRPC API Design"
slug: grpc-api-design
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-918
canonical: ../syllabus/07-api-design/grpc-api-design.md
last_updated: 2026-09-12
---

# Flashcards: gRPC API Design

**Canonical chapter:** [`syllabus/07-api-design/grpc-api-design.md`](../syllabus/07-api-design/grpc-api-design.md)

## Card: Why gRPC avoids independent interpretation

**Prompt:**
Why does gRPC avoid the "client and server interpret the contract independently" risk that REST + JSON has?

**Answer:**
Both client and server are generated from the same `.proto` file by the same compiler — there's no separate parsing/interpretation step for either side to disagree on.

**Why it matters:**
This is the mechanical reason gRPC is preferred for internal service boundaries.

**Common trap:**
Describing this benefit vaguely as "gRPC is stricter" without naming the shared-codegen mechanism.

**Related:**
[gRPC API Design](../syllabus/07-api-design/grpc-api-design.md)

## Card: The four call shapes, by stream keyword placement

**Prompt:**
How do you tell gRPC's four call shapes apart from a `.proto` file?

**Answer:**
No `stream` keyword on either side: unary. `stream` on the response only: server streaming. `stream` on the request only: client streaming. `stream` on both: bidirectional streaming.

**Why it matters:**
A precise, checkable rule instead of a vague description.

**Common trap:**
Confusing client streaming with bidirectional streaming — client streaming still returns exactly one response.

**Related:**
[gRPC API Design](../syllabus/07-api-design/grpc-api-design.md)

## Card: Why every gRPC call needs a deadline

**Prompt:**
What concretely goes wrong if an internal gRPC call has no deadline?

**Answer:**
A slow downstream call can hold the caller's thread indefinitely; enough concurrent slow calls exhaust the caller's thread pool, cascading into an outage for unrelated requests.

**Why it matters:**
Measured in this chapter's production scenario as a real cascading-failure mechanism, not a hypothetical.

**Common trap:**
Treating a missing deadline as a minor inefficiency rather than a cascading-failure risk.

**Related:**
[gRPC API Design](../syllabus/07-api-design/grpc-api-design.md)
