---
title: "Typed API Contract Change Caught at Compile Time Instead of in Production"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md
source: syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md#production-scenarios
---

# Typed API Contract Change Caught at Compile Time Instead of in Production

## Context

A backend team changes a `/products/:id` endpoint's `price` field from a JSON number to a formatted string (`"19.99"` instead of `19.99`) as part of an internationalization effort, without updating every consumer. A frontend component calling `formatPrice(product.price)` is one of dozens of call sites touching that field.

## Symptoms (in a plain-JavaScript codebase)

Nothing about the call site looks wrong at review time; the bug surfaces only when a real user's browser executes that exact code path, producing a blank price or a crashed component, reported after the fact as a confusing, hard-to-reproduce customer complaint.

## Impact (as prevented)

In a TypeScript codebase where `product.price` is typed as `number` and the API client's response type is updated to reflect the real contract change, every affected call site — not just the ones a developer happened to think to check — fails to compile immediately, listed exhaustively by `tsc`, before the change is even merged.

## Initial Hypotheses

- Manually grepping for every call site referencing `product.price` would be sufficient to find all affected code — considered, but unreliable for a field this widely used across dozens of call sites, some possibly indirect.
- Runtime testing (QA) would catch every affected call site before shipping — a real possibility, but incomplete and dependent on QA happening to exercise every one of the dozens of paths.
- Updating the shared `Product` interface's `price` field type and letting the compiler enumerate every now-incompatible call site is exhaustive and immediate — correct, and the approach adopted.

## Evidence

Running `tsc` after updating the type definition produces an exhaustive list of every call site now failing to type-check, each one an exact, addressable location rather than a guess.

## Investigation Timeline

1. Backend team plans an API contract change to `price`'s JSON type.
2. Frontend team updates the shared `Product` interface (and the API client's response type) to reflect the new contract, ahead of the backend change reaching production.
3. `tsc` immediately lists every call site depending on the old type, before any of them reach a user.

## Root Cause

Not applicable in the incident sense — this is a demonstrated prevention, not a failure. The underlying condition being addressed is that a JSON-based, JavaScript-consumed API contract carries no compile-time enforcement on its own; TypeScript's type system is what supplies it on the consuming side.

## Immediate Mitigation

Not applicable — no incident occurred; the type update is itself the safeguard, applied before the backend change ships.

## Permanent Fix

Keep the shared type definitions for any API contract as the single source of truth consumers compile against, updated in lockstep with any real contract change, so `tsc` continues to enumerate every affected call site for any future field-type change.

## Alternatives Considered

Relying on runtime schema validation (e.g., a validation library checking the response shape at the network boundary) instead of static types — a real, complementary technique, but one that surfaces problems at runtime rather than at compile time, and doesn't enumerate every affected call site the way a type-level change does.

## Trade-offs

Keeping frontend type definitions in sync with the real backend contract requires active maintenance whenever the API changes — a real, ongoing cost, accepted in exchange for exhaustive, compile-time detection of every affected call site.

## Prevention

Model every API response consumed by the frontend with a real TypeScript type reflecting its actual field types, and treat any backend contract change as requiring the corresponding frontend type update in the same change, not as an afterthought discovered via a runtime bug report.

## Monitoring and Alerts

- CI running `tsc` as a hard gate on every pull request, so any future API-type mismatch is caught in the same way this one was.
- Coordination process (e.g., a shared changelog or contract-change notification) between backend and frontend teams for any endpoint field-type change, prompting the frontend type update proactively.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent contract change.

- **Situation:** a backend team planned to change an API field's JSON type in a way that would silently break dozens of frontend call sites in a plain-JavaScript codebase.
- **Task:** ensure every affected call site is found and fixed before the change ships, not discovered afterward via customer complaints.
- **Action:** updated the shared `Product` interface's type to reflect the real contract change, ahead of the backend deployment.
- **Result:** `tsc` immediately and exhaustively listed every now-incompatible call site, each fixed before merge, with zero runtime incidents.

## Staff-Level Discussion

The cost is paid once, at the type definition; the value compounds across every call site the type actually reaches. The organizational lesson is that static types convert an entire class of "did we remember to check everywhere" risk into a mechanical, exhaustive compiler check — for any API contract with dozens of consumers, this scales in a way manual auditing or partial QA coverage structurally cannot.

## Related Handbook Chapters

- [TypeScript Fundamentals: Types, Interfaces, and Generics](../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) — the canonical typed-API-contract enforcement behind this prevention.
