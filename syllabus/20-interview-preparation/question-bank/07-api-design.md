---
title: "Interview Question Bank — 07-api-design"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../07-api-design/INDEX.md
  - 05-spring.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — API Design

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 5 chapters yielded 8 deep questions + 9
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `rest-api-fundamentals.md`) + 12 quick-fire questions = **29 real
questions**.

---

## REST API Fundamentals

Junior Fundamentals chapter — its Interview Questions already tag each by seniority.

### Q1 — What's the difference between PUT and POST?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior:** `POST` creates a new resource (not idempotent); `PUT` replaces an existing resource's state at a known location (idempotent). Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form; a weak Junior answer says "PUT updates, POST creates" without the idempotency distinction interviewers actually probe.

### Q2 — What status code should a successful DELETE return, and why no body?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** `204 No Content` — the operation succeeded, and there's deliberately nothing left to describe since the resource no longer exists. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q3 — Why does a well-designed API return 201 instead of 200 from a creation endpoint, and what should come with it?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid:** `201` communicates "a new resource now exists" specifically; convention includes a `Location` header pointing at the new resource's real URL. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q4 — Is DELETE idempotent? What happens if you call it twice on the same resource?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** Conventionally idempotent in end-state terms, but the response to a second call is a genuinely debatable design choice (`404` vs. `204`) — the strongest answers name both and state a reasoned preference. Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

### Q5 — An API returns 404 from some endpoints and 204 from others for repeated deletes, with no stated reason. What do you do?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Senior/Staff:** Flag it as an unstated-inconsistency issue, not a bug in either endpoint; push for a documented, organization-wide convention. Target tier.
- **Junior/Mid:** Not typically asked in this exact form.

### Q6 — What's the real difference between 400 and 422, and why does it matter?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid:** `400` means the body itself couldn't be parsed, before any application code runs; `422` means it parsed but violates a semantic/business rule — a client reacts differently to each. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q7 — What's the difference between 401 and 403?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid:** `401` means identity isn't established at all (authentication failure); `403` means identity is established but not permitted for this action (authorization failure). Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q8 — How would you implement a cache-friendly GET endpoint that avoids re-sending an unchanged resource's full body every time?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** Conditional GET via `ETag`/`If-None-Match` — a resent matching `ETag` gets an empty-bodied `304 Not Modified`; Spring provides this via `ShallowEtagHeaderFilter`. Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

### Q9 — You need an endpoint that always points at "whichever resource is currently newest." What status code should the redirect use, and why?

**Canonical treatment:** [§15](../../07-api-design/rest-api-fundamentals.md#15-interview-questions)

**What's expected:**
- **Mid:** `302 Found`, not `301 Moved Permanently` — the target genuinely changes over time, and `301`/`308` tell clients to cache the redirect permanently. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

---

## API Design (Advanced — Pagination, Idempotency, Versioning)

### Q1 — Design pagination for a 500M-row endpoint. Why not `OFFSET`?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `OFFSET` gets slower at depth, without the precise mechanism.
- **Senior:** Correctly explains why `OFFSET` degrades (the database walks and discards every skipped row) and proposes keyset pagination (seeking directly via an index condition).
- **Staff:** Names the honest trade-off (no arbitrary page-jump) and proposes a hybrid approach for UIs that need it.

### Q2 — What makes an API idempotent, and why does it matter for retries?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates "idempotent" with "read-only" — the common mistake this question targets; a `PUT` is idempotent and can still be a write.
- **Senior:** States the definition correctly and connects it to safe retries — a client unsure whether a request succeeded can safely retry an idempotent operation.
- **Staff:** Explicitly ties this to the idempotency-key mechanism for making `POST` safe under retry.

---

## API Gateway, BFF, and Edge Concerns

### Q1 — How is an API gateway different from a load balancer?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/api-gateway-bff-and-edge-concerns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "gateway" and "load balancer" as interchangeable terms — the common mistake this question targets.
- **Senior:** Names both the routing-target distinction (identical instances of one service vs. routing to different services) and at least one concrete centralized concern (auth, rate limiting, logging).
- **Staff:** Discusses the shared-gateway-as-platform-dependency governance question — who owns it, what's the blast radius of an incident there.

### Q2 — When would you introduce a BFF instead of just adding more endpoints to a shared gateway?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/api-gateway-bff-and-edge-concerns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Introduces a BFF preemptively for every client type without a real divergence in needs — the common mistake this question targets.
- **Senior:** Explains the concurrent-fan-out mechanism and its real round-trip reduction — a BFF aggregates and reshapes calls to multiple backends into exactly what one client type needs.
- **Staff:** Discusses the trade-off of introducing a BFF too early (unnecessary maintenance burden) versus too late (a real, measured production latency problem).

---

## GraphQL API Design

### Q1 — A GraphQL API's response times degrade badly on a specific nested query. Diagnose it.

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/graphql-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes caching as the primary fix, or blames the database/network without checking the resolver's call pattern — the common mistake this question targets.
- **Senior:** Correctly names the N+1 mechanism (one backend call per list item) and proposes `DataLoader` batching with the right explanation (deferred, batched dispatch within one execution tick).
- **Staff:** Frames this as a recurring, structural risk for every new nested field, and proposes making batched resolvers the enforced default.

### Q2 — A resolver throws partway through a query. What comes back to the client?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/graphql-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes GraphQL always returns partial data regardless of schema, or that an error changes the HTTP status code — the common mistake this question targets.
- **Senior:** Correctly states the nullability-dependent bubbling behavior — a nullable field goes null alone; a non-null field's failure bubbles to the nearest nullable ancestor.
- **Staff:** Connects this to deliberate schema design — nullability should be a considered decision about failure blast radius, not an implementation detail.

---

## gRPC API Design

### Q1 — Design the `.proto` contract for a service streaming live order updates to a client. Which call shape, and why?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/grpc-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes unary calls with client-side polling, missing the point of native streaming — the common mistake this question targets.
- **Senior:** Correctly identifies server streaming (`rpc StreamOrderUpdates (OrderId) returns (stream OrderUpdate)`) and explains why client streaming or bidirectional would be wrong here.
- **Staff:** Discusses what happens if the client disconnects mid-stream, and how the server should detect and clean up that half-open call.

### Q2 — An internal gRPC call has no deadline set. What's the risk, concretely?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/grpc-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats a missing deadline as a minor inefficiency — the common mistake this question targets.
- **Senior:** Correctly explains the thread-exhaustion cascade mechanism — a slow downstream call holds the caller's thread indefinitely, and enough concurrent slow calls exhaust the caller's own pool.
- **Staff:** Proposes enforcing deadline propagation as a platform-level convention (shared client library or lint rule) rather than a per-call judgment call.

---

## Quick-fire questions (from this domain's Flashcards)

`rest-api-fundamentals.md` has no Flashcards section (it uses the leveled Q1-Q9
format above instead).

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Why does `OFFSET` pagination get slower with depth? | [API Design](../../07-api-design/api-design.md#flashcards) |
| 2 | What does keyset pagination give up in exchange for flat cost at any depth? | [API Design](../../07-api-design/api-design.md#flashcards) |
| 3 | Is `PUT` idempotent? Is `POST`? | [API Design](../../07-api-design/api-design.md#flashcards) |
| 4 | What's the real, structural difference between an API gateway and a load balancer? | [API Gateway, BFF, Edge Concerns](../../07-api-design/api-gateway-bff-and-edge-concerns.md#flashcards) |
| 5 | If a gateway rejects a request for a missing API key, does the backend ever see it? | [API Gateway, BFF, Edge Concerns](../../07-api-design/api-gateway-bff-and-edge-concerns.md#flashcards) |
| 6 | Why is calling a BFF endpoint once faster than a client calling the same two backends itself? | [API Gateway, BFF, Edge Concerns](../../07-api-design/api-gateway-bff-and-edge-concerns.md#flashcards) |
| 7 | Why does a naive GraphQL resolver cause an N+1 problem? | [GraphQL API Design](../../07-api-design/graphql-api-design.md#flashcards) |
| 8 | What determines whether a resolver failure nulls one field or the entire response? | [GraphQL API Design](../../07-api-design/graphql-api-design.md#flashcards) |
| 9 | What HTTP status code does a GraphQL API return when a resolver throws? | [GraphQL API Design](../../07-api-design/graphql-api-design.md#flashcards) |
| 10 | Why does gRPC avoid the "client and server interpret the contract independently" risk REST + JSON has? | [gRPC API Design](../../07-api-design/grpc-api-design.md#flashcards) |
| 11 | How do you tell gRPC's four call shapes apart from a `.proto` file? | [gRPC API Design](../../07-api-design/grpc-api-design.md#flashcards) |
| 12 | What concretely goes wrong if an internal gRPC call has no deadline? | [gRPC API Design](../../07-api-design/grpc-api-design.md#flashcards) |

---

## Related

- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
