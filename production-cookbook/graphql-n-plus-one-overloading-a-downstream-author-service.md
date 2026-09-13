---
title: "GraphQL N+1 Overloading a Downstream Author Service"
document_type: production-cookbook-entry
domain: api-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/07-api-design/graphql-api-design.md
source: syllabus/07-api-design/graphql-api-design.md#production-scenarios
---

# GraphQL N+1 Overloading a Downstream Author Service

## Context

A mobile team ships a new "library" screen that queries `{ books { title author { name } } }` for a scrollable list.

## Symptoms

Shortly after rollout, the author service's request rate and p99 latency both spike, and its on-call is paged for a service that "nothing changed" on its own side.

## Impact

Elevated latency and error rate on the author service, cascading into slower responses for every other client of that service, not just the new screen.

## Initial Hypotheses

- A deploy to the author service itself — checked, no recent deploy.
- A traffic spike from an unrelated client — checked, the request pattern's timing lines up exactly with the new screen's rollout.
- The GraphQL layer resolving `author` per-book rather than batched — correct.

## Evidence

Request logs on the author service show request volume scaling linearly with the number of books rendered per screen load — the N+1 pattern.

## Investigation Timeline

1. Author-service on-call paged for a spike in request rate and p99 latency.
2. Own-deploy and unrelated-traffic hypotheses ruled out via deploy history and timing correlation.
3. Request logs show volume scaling linearly with books-per-screen-load, confirming per-book resolution.

## Root Cause

The `author` field's resolver was written as a direct, per-object call to the author service with no batching — the default, easiest way to write a GraphQL resolver.

## Immediate Mitigation

Add a response-time budget / rate limit in front of the author service to protect it while a proper fix ships, accepting degraded (but bounded) latency on the library screen in the interim.

## Permanent Fix

Replace the direct-call resolver with a `DataLoader`-batched one, collapsing the per-screen-load call count from `O(books per page)` to `O(1)`.

## Alternatives Considered

Caching author lookups — rejected as a first fix, since it reduces load only for repeated lookups of the same author and does nothing for the first page load, whereas batching fixes the root cause for every load.

## Trade-offs

`DataLoader` batching requires resolvers to be written to defer and batch rather than call out directly — a small amount of upfront resolver-design discipline, in exchange for avoiding O(N) backend calls on every list-shaped query.

## Prevention

Any resolver for a field reachable from a list should default to a `DataLoader`-batched implementation, not a direct call — treat the direct-call pattern as the anti-pattern, not the default.

## Monitoring and Alerts

- Downstream-service request-rate dashboards correlated against upstream GraphQL query shapes, so a new client-side query can be traced back to its resolver-level cause quickly.
- A resolver-level lint or review check flagging any list-reachable field resolver that performs a direct call instead of using a registered `DataLoader`.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a new mobile screen's list query overloaded an unrelated downstream service that hadn't deployed anything itself.
- **Task:** find why a client-side-only change caused a backend service incident.
- **Action:** correlated the timing with the new screen's rollout, then confirmed via request-log volume that the `author` field was being resolved once per book with no batching.
- **Result:** replaced the direct-call resolver with a `DataLoader`-batched one, collapsing the call count to O(1) per screen load.

## Staff-Level Discussion

The N+1 problem is not a theoretical concern raised only in interviews — it reproduces at real production scale the moment a list-shaped query reaches a nested field, and it can page an on-call engineer for a service team that made no change of its own. The organizational lesson is that GraphQL's flexibility shifts fan-out risk onto whichever service backs a list-reachable field — a schema-wide convention (default to `DataLoader`, treat direct calls as the exception) is the only durable defense, since any individual resolver written the "easy way" can independently reproduce this incident.

## Related Handbook Chapters

- [GraphQL API Design](../syllabus/07-api-design/graphql-api-design.md) — the canonical N+1/`DataLoader` batching measurement behind this incident's fix.
