---
title: "Interview Question Bank — 07-api-design"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-28
related:
  - ../../07-api-design/INDEX.md
  - 05-spring.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — API Design

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 9 chapters yielded 17 deep questions + 9
already-leveled Junior/Mid questions (from the domain's one Junior Fundamentals
chapter, `rest-api-fundamentals.md`) + 20 quick-fire questions = **46 real
questions**. (Updated 2026-09-28: `http-caching-for-apis.md` is a new chapter
closing a real coverage gap — `Cache-Control` appeared once in the whole repository,
in a frontend chapter — contributing 3 questions.) (Updated 2026-09-27: `api-versioning-strategies.md`,
`openapi-and-contract-first-api-design.md`, and `webhook-design-and-delivery-guarantees.md`
had complete Interview Questions sections never indexed — a stale-index gap, not
a content gap. Added 6 questions + 8 quick-fire cards.)

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

## API Versioning Strategies (T-919)

### Q1 — Your team migrated from URI-path versioning to header-based versioning. What real failure mode should you expect from that migration, and why?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/api-versioning-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes there's a sensible default version returned automatically when the header is missing — the common mistake this question targets.
- **Senior:** Correctly predicts that any existing caller that never sends the new `Api-Version` header gets a real `404` from Spring's `headers` request condition having no matching handler — not a default version, not a descriptive error — and proposes an explicit default-version fallback or a transition period keeping both schemes live.
- **Staff:** Frames this as an organizational rollout risk (which teams/clients haven't migrated yet) rather than only a technical dispatch detail.

### Q2 — A caller hits your media-type-versioned endpoint with `Accept: */*`. What real HTTP status do they get, and why does that matter?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/api-versioning-strategies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes an ambiguous `Accept` header is rejected outright (a `406`) — the common mistake this question targets.
- **Senior:** Correctly predicts a real `200`, not a `406` — content negotiation resolves the generic `Accept` against whichever version-specific `produces` handler it matches first, silently — and can name the underlying reason (content negotiation is designed to always find something acceptable).
- **Staff:** Connects this to a broader API-contract-clarity principle: any mechanism that resolves ambiguity silently, rather than rejecting it, defers a real bug to whoever debugs the resulting confusion later.

---

## OpenAPI and Contract-First API Design (T-2414)

### Q1 — A hand-written Swagger doc and the real API you're documenting have drifted apart. How does that happen, and how would you prevent it?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/openapi-and-contract-first-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes "more frequent manual reviews" of the hand-written doc as the fix, rather than removing the manual step entirely — the common mistake this question targets.
- **Senior:** Recognizes that a hand-written spec is a second artifact with no mechanism forcing it to track the real code, and proposes annotation-driven generation (e.g., springdoc), describing the basic mechanism (reading `@GetMapping` and DTO fields).
- **Staff:** Frames this as an organizational tooling decision — consistent generation across every team's APIs, published to a shared catalog — rather than a single-API fix.

### Q2 — Your validation annotation (`@Min(1)`) and your API documentation both need to say "amount must be at least 1." How do you avoid maintaining that fact twice?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/openapi-and-contract-first-api-design.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `@Schema` annotations are the only way to express constraints in the spec, duplicating what `@Min`/`@NotBlank` already provide — the common mistake this question targets.
- **Senior:** Names the specific mechanism — `jakarta.validation` constraints like `@Min(1)` are read by the spec generator too, becoming a real `"minimum": 1` in the generated schema with zero OpenAPI-specific syntax added — with a concrete before/after example.
- **Staff:** Connects this to the broader principle of a single source of truth: the fewer places a fact is expressed, the fewer places it can silently disagree with reality.

---

## Webhook Design and Delivery Guarantees (T-2415)

### Q1 — How does a webhook receiver know a request really came from the claimed provider, and not an attacker who found the URL?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/webhook-design-and-delivery-guarantees.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats a hard-to-guess URL as sufficient security — the common mistake this question targets.
- **Senior:** Correctly names HMAC signature verification — the provider computes a hash of the payload using a shared secret and sends it as a header; the receiver recomputes it and compares using a constant-time comparison to avoid a timing side-channel — and knows it proves authenticity/integrity but not confidentiality.
- **Staff:** Discusses secret rotation and what happens to in-flight signed requests during a rotation.

### Q2 — Why can a webhook be delivered twice for the same logical event, and whose responsibility is it to handle that?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/webhook-design-and-delivery-guarantees.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a provider's "retry" mechanism implies duplicates are the provider's bug to fix, rather than an inherent property of at-least-once delivery — the common mistake this question targets.
- **Senior:** Explains that a provider can't always know for certain a delivery was processed (a response can be lost even after successful processing), so it retries rather than risk losing an event, and correctly places deduplication responsibility on the receiver, using the provider's delivery ID.
- **Staff:** Connects this to the general distributed-systems principle that exactly-once delivery is not achievable without idempotent processing on the receiving side.

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
| 13 | What real HTTP status does a header-versioned endpoint return when the version header is missing entirely? | [API Versioning Strategies](../../07-api-design/api-versioning-strategies.md#flashcards) |
| 14 | Does a generic `Accept: */*` fail on a media-type-versioned endpoint? | [API Versioning Strategies](../../07-api-design/api-versioning-strategies.md#flashcards) |
| 15 | Why can't a spec generated by a tool like springdoc silently drift from the API's real behavior? | [OpenAPI and Contract-First API Design](../../07-api-design/openapi-and-contract-first-api-design.md#flashcards) |
| 16 | What does `@Min(1)` become in a generated OpenAPI schema? | [OpenAPI and Contract-First API Design](../../07-api-design/openapi-and-contract-first-api-design.md#flashcards) |
| 17 | What real dependency gap did this chapter's lab find in springdoc before generation worked? | [OpenAPI and Contract-First API Design](../../07-api-design/openapi-and-contract-first-api-design.md#flashcards) |
| 18 | What does an HMAC webhook signature actually prove, and what does it NOT provide? | [Webhook Design and Delivery Guarantees](../../07-api-design/webhook-design-and-delivery-guarantees.md#flashcards) |
| 19 | Should a webhook sender retry a `401` the same way it retries a `503`? | [Webhook Design and Delivery Guarantees](../../07-api-design/webhook-design-and-delivery-guarantees.md#flashcards) |
| 20 | Why do real webhook providers guarantee "at least once" delivery instead of "exactly once"? | [Webhook Design and Delivery Guarantees](../../07-api-design/webhook-design-and-delivery-guarantees.md#flashcards) |

---

---

## HTTP Caching for APIs

### Q1 — What is the difference between `no-cache`, `no-store`, and `must-revalidate`?

**Canonical treatment:** [§ Interview Questions, Q1](../../07-api-design/http-caching-for-apis.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Knows `no-store` is the one that actually prevents storage.
- **Senior:** Defines all three precisely — `no-store` forbids storing anywhere; `no-cache` permits storing but requires revalidation before every reuse; `must-revalidate` applies only once a response is already stale, forbidding a cache from serving it without revalidating. Adds that `no-cache` plus an `ETag` is a deliberate combination (always current, body only on change) and that saying nothing at all is worse than any of them, because caches may apply heuristic freshness derived from `Last-Modified`.
- **Staff:** Frames the default as organizational — `private, no-store` everywhere with `public` as a reviewed per-route exception, because the cost of a wrong `public` is a confidentiality incident while the cost of a missing one is bandwidth.

### Q2 — We added ETags and request volume did not drop. Why?

**Canonical treatment:** [§ Interview Questions, Q2](../../07-api-design/http-caching-for-apis.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Knows a `304` still involves a request.
- **Senior:** Separates the two mechanisms — validation saves the body (measured: 50 bytes to 0), freshness saves the round trip — and identifies the server-side subtlety: a body-hash ETag renders before deciding to send a `304`, so only a version-derived ETag saves the work. Measured in the chapter's demo as 2 renders across 4 requests.
- **Staff:** Raises the mobile dimension — on a radio link the round trip, not the payload, is the expensive part — so the fix for a config endpoint is `max-age` plus `stale-while-revalidate`, with a separate uncached endpoint for the few values that must be immediate.

### Q3 — A CDN served one customer's data to another. The query filtering is correct. What happened?

**Canonical treatment:** [§ Interview Questions, Q3](../../07-api-design/http-caching-for-apis.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Identifies that a shared cache stored a user-specific response.
- **Senior:** Names `Cache-Control: public` as the specific override of the default rule that shared caches must not store responses to `Authorization`-carrying requests, plus the missing `Vary`. Notes the diagnostic tell — one origin request in the logs while many users saw the response — and explains why `Vary: Authorization` is a poor substitute for `private`/`no-store`.
- **Staff:** Moves the control to the edge: a default `private, no-store` at the gateway with `public` requiring a reviewed opt-in and a contract test, plus purge authority and purge latency as part of the incident plan.


## Related

- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
