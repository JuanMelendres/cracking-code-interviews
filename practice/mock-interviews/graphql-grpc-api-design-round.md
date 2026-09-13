---
title: "Mock Interview: GraphQL and gRPC API Design Round (45 min)"
slug: graphql-grpc-api-design-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-09-13
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - GraphQL N+1 diagnosis and DataLoader batching
  - GraphQL null-propagation and schema-nullability design
  - GraphQL's HTTP-200-on-error semantics
  - gRPC call-shape selection from a requirement
  - gRPC deadline-less-call cascading-failure risk
  - gRPC's shared-codegen contract guarantee
  - Production/technical story
related:
  - ../../syllabus/07-api-design/graphql-api-design.md
  - ../../syllabus/07-api-design/grpc-api-design.md
  - ../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md
  - ../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md
source: null
official_references: []
---

# Mock Interview: GraphQL and gRPC API Design Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

**Sourcing note.** Like the [AI/LLM Engineering Technical Round](ai-llm-engineering-technical-round.md), this round has no prior `study-packs/` mock file to elevate — both chapters were added 2026-09-09, after every study-pack program in this repository. Built fresh, but not invented: Questions 1, 2, 4, and 5 are each chapter's own already-written Interview Questions (with real expected answers grounded in each chapter's real, executed demo); Questions 3 and 6 are drawn from each chapter's own already-written `flashcards/` deck (itself elevated earlier this session from the same canonical chapter, never independently re-derived). This round's own contribution is the Mock Interview Standard structure — competencies, interviewer script, pass/borderline/fail signals, scoring, debrief, remediation — not new technical content.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| N+1 diagnosis and `DataLoader` batching | Q1 | [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md) |
| Null-propagation and schema-nullability design | Q2 | [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md) |
| HTTP-200-on-error semantics | Q3 | [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md) |
| Call-shape selection from a requirement | Q4 | [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md) |
| Deadline-less-call cascading-failure risk | Q5 | [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md) |
| Shared-codegen contract guarantee | Q6 | [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md) |
| Production/technical story | Q7 | [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) |

## Interviewer Opening Script

*"This is a 45-minute round on API design beyond REST — GraphQL's resolver model and gRPC's contract-first, streaming-capable model. Several of these questions have a plausible-sounding but incomplete answer, and a more precise, mechanism-level one — I want the precise version. Let's start with a slow GraphQL query."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(7 min)** "A GraphQL API's response times degrade badly on a specific nested query. Diagnose it."
2. **(6 min)** "A resolver throws partway through a query. What comes back to the client?"
3. **(5 min)** "What HTTP status code does a GraphQL API return when a resolver throws, and why does that surprise engineers coming from REST?"
4. **(6 min)** "Design the call shape for a service that needs to stream live order updates to a client. Which of gRPC's four shapes, and why?"
5. **(6 min)** "An internal gRPC call has no deadline set. What's the risk, concretely?"
6. **(6 min)** "Why does gRPC avoid the 'client and server interpret the contract independently' risk that REST + JSON has?"
7. **(9 min)** Deliver a production/technical story about a system you built or debugged, using the four-beat structure — an API-design story if you have one, any real technical story otherwise.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — GraphQL response times degrade on a nested query

**Ideal answer outline:** each field has its own independent resolver; a field resolved once per item on a list (e.g., `author` on every `book` in a list) causes one backend call per item unless explicitly batched — the N+1 problem. The fix is `DataLoader` batching: collecting the keys requested across one execution tick and issuing one batched backend call instead of N individual ones. If the candidate proposes caching as the fix, the correct push is: "does caching help the very first time this query runs, with nothing warm yet?"
**Common weak answers:** proposing caching as the primary fix, or blaming the database/network without inspecting the resolver's actual call pattern.
**Pass signal:** names N+1 and proposes `DataLoader` batching, either unprompted or promptly under the push.
**Borderline signal:** recognizes a per-item call pattern under the push but can't name the `DataLoader`/batching fix specifically.
**Fail signal:** proposes only caching or database tuning even after the push, with no recognition of the per-item resolver pattern.

### Question 2 — A resolver throws partway through a query

**Ideal answer outline:** it depends on the failing field's nullability in the schema: if nullable, only that field comes back null and the rest of the response ships normally; if non-null, the null bubbles up to the nearest nullable ancestor, which can null out an entire branch or the whole response. This is a real, deliberate schema-design decision about failure blast radius, not an implementation detail. If the candidate says "GraphQL always partially succeeds" as a flat rule, the correct push is: "what if the failing field itself is marked non-null in the schema?"
**Common weak answers:** stating partial success as an unconditional guarantee regardless of schema nullability.
**Pass signal:** correctly states the nullability-dependent bubbling behavior, either unprompted or promptly under the push.
**Borderline signal:** senses nullability matters under the push but can't describe the bubbling mechanism precisely.
**Fail signal:** insists on unconditional partial success even after the push.

### Question 3 — HTTP status code on a GraphQL error

**Ideal answer outline:** still `200` — GraphQL's error semantics live in the response body's `errors` array, not the HTTP status code, since a single query can partially succeed and partially fail in ways a single HTTP status can't represent. This surprises engineers used to REST's convention of mapping failure to a `4xx`/`5xx` status. If the candidate assumes a `5xx` on any resolver error, the correct push is: "if nine of ten requested fields succeeded, what status would represent that?"
**Common weak answers:** assuming a resolver error changes the HTTP status the way it would in a REST API.
**Pass signal:** correctly states `200` regardless of resolver errors and explains why (errors live in the body, not the status), either unprompted or promptly under the push.
**Borderline signal:** guesses `200` under the push without explaining why a single status can't represent partial success/failure.
**Fail signal:** insists on a `4xx`/`5xx` status even after the push.

### Question 4 — Streaming live order updates: which gRPC call shape

**Ideal answer outline:** server streaming (`rpc StreamOrderUpdates (OrderId) returns (stream OrderUpdate)`) — one request establishes interest, and the server pushes updates as they occur, with no need for the client to re-request. Client streaming would be wrong here since the client isn't sending an ongoing stream of its own; bidirectional would be unnecessary complexity for a one-directional feed. If the candidate proposes unary calls with client-side polling, the correct push is: "what does polling cost that native streaming doesn't?"
**Common weak answers:** proposing unary calls with client-side polling, missing the point of native streaming entirely.
**Pass signal:** correctly identifies server streaming and explains why client streaming/bidirectional would be the wrong shape, either unprompted or promptly under the push.
**Borderline signal:** recognizes streaming is needed under the push but picks the wrong shape or can't justify server streaming specifically.
**Fail signal:** proposes polling even after the push.

### Question 5 — Internal gRPC call with no deadline

**Ideal answer outline:** a slow downstream call can hold the caller's thread indefinitely; with enough concurrent slow calls, this exhausts the caller's own thread pool, turning a downstream slowdown into a full outage for the caller's *other*, entirely unrelated requests. If the candidate says "it's just best practice to set deadlines" with no mechanism, the correct push is: "concretely, what happens to the calling service if enough of these calls hang at once?"
**Common weak answers:** treating a missing deadline as a minor inefficiency rather than naming the specific cascading-failure mechanism.
**Pass signal:** names the thread-exhaustion cascade mechanism, either unprompted or promptly under the push.
**Borderline signal:** agrees it's a real risk under the push but can't describe the thread-exhaustion mechanism specifically.
**Fail signal:** maintains it's a minor style/best-practice issue even after the push.

### Question 6 — Why gRPC avoids independent contract interpretation

**Ideal answer outline:** both client and server are generated from the same `.proto` file by the same compiler — there's no separate, independent parsing/interpretation step on either side that could disagree about the contract's shape, unlike REST + JSON, where each side's understanding of the contract is effectively independent and can silently drift. If the candidate says "gRPC is just stricter" with no mechanism, the correct push is: "stricter how, specifically — what's actually different about how the two sides get their understanding of the contract?"
**Common weak answers:** describing gRPC as generically "stricter" or "more type-safe" without naming the shared-codegen mechanism specifically.
**Pass signal:** names the shared `.proto`-generated codegen as the specific mechanism, either unprompted or promptly under the push.
**Borderline signal:** agrees gRPC is more contract-safe under the push but can't name the shared-codegen mechanism specifically.
**Fail signal:** gives only a vague "stricter"/"more modern" answer even after the push.

### Question 7 — Production/technical story

**Ideal answer outline:** a four-beat, clearly structured story (situation, action, the specific decision criterion used, and the outcome/cost) about real technical work under real constraints.
**Common weak answers:** a story with no clear structure, or one that describes what changed without stating the specific reasoning behind the chosen approach.
**Pass signal:** clear four-beat structure with a specific decision criterion and outcome, scored per Technical Depth and Production Judgment.
**Borderline signal:** the story is coherent but the decision criterion has to be extracted through follow-up.
**Fail signal:** no clear structure, or no identifiable decision criterion even on request.

## Scoring Rubric

Score this round using the [shared six-dimension rubric](../../study-packs/week-01/10-week-1-evaluation-rubric.md)'s **Technical Depth** and **Production Judgment** dimensions specifically (1–5 scale, 3 = Mid, 4 = Senior, 5 = Staff) — the same two-dimension scope the [AI/LLM Engineering Technical Round](ai-llm-engineering-technical-round.md), [Kafka Messaging Technical Round](kafka-messaging-technical-round.md), and [Spring Technical Round](spring-technical-round.md) use.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 1 and 5 share the sharpest theme: both are real, structural risks (GraphQL's per-field resolver model; a gRPC call with no timeout) that are easy to dismiss as minor until the candidate is pushed to state the concrete mechanism — a candidate who reaches for a vague or generic fix on both, rather than naming `DataLoader` batching and the thread-exhaustion cascade specifically, likely has surface familiarity with these protocols rather than hands-on production experience. Questions 2 and 3 are both "common misconception" traps rooted in the same underlying fact (GraphQL's error model is genuinely different from REST's, not just "more flexible") — getting both right unprompted, especially the HTTP-status question, is a strong signal the candidate has actually inspected real GraphQL response traffic, not just read about the spec.

## Remediation Recommendations

- Weak Q1 → re-read [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md)'s N+1/`DataLoader` Internal Implementation, and [`graphql-n-plus-one-overloading-a-downstream-author-service.md`](../../production-cookbook/graphql-n-plus-one-overloading-a-downstream-author-service.md) for the real incident shape.
- Weak Q2 → re-read [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md), specifically its Core Concepts section on error handling and null propagation.
- Weak Q3 → re-read [GraphQL API Design](../../syllabus/07-api-design/graphql-api-design.md) and its own [flashcard deck](../../flashcards/graphql-api-design.md)'s HTTP-status card.
- Weak Q4 → re-read [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md), specifically its Core Concepts section on the four call shapes.
- Weak Q5 → re-read [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md)'s Production Scenario, [`deadline-less-grpc-call-cascading-into-thread-pool-exhaustion.md`](../../production-cookbook/deadline-less-grpc-call-cascading-into-thread-pool-exhaustion.md) for the real incident, and [Distributed Systems Failure Modes](../../syllabus/10-distributed-systems/distributed-systems-failure-modes.md) for the general cascading-failure pattern.
- Weak Q6 → re-read [gRPC API Design](../../syllabus/07-api-design/grpc-api-design.md) and its own [flashcard deck](../../flashcards/grpc-api-design.md)'s shared-codegen card.
- Weak Q7 → re-read [Production Incident Narratives](../../syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md) — the closest-fit chapter for a technical story, since this domain has no dedicated behavioral-handbook chapter of its own.
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
