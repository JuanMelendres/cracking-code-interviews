---
title: "Cheat Sheet: OpenAPI and Contract-First API Design"
slug: openapi-and-contract-first-api-design
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-2414
canonical: ../syllabus/07-api-design/openapi-and-contract-first-api-design.md
last_updated: 2026-09-18
---

# OpenAPI and Contract-First API Design

**Canonical chapter:** [`syllabus/07-api-design/openapi-and-contract-first-api-design.md`](../syllabus/07-api-design/openapi-and-contract-first-api-design.md)

## Core Mental Model

A generated spec cannot silently drift from the code, because it has no independent existence to drift
from — it's recomputed from the real, running app's real annotations on every request. A hand-written spec
is a second artifact a human must remember to update every time the code changes, and in practice, doesn't
get updated every time.

## Essential Definitions

- **OpenAPI** — a machine-readable JSON/YAML spec format describing an HTTP API's paths, parameters, and schemas.
- **springdoc-openapi** — generates a real OpenAPI 3 spec from a real Spring MVC app's annotations, served at `/v3/api-docs`.
- **Contract-first design** — treating the spec as the authoritative source of truth a client is built against.
- **`openapi-generator-cli`** — generates a real typed client in another language/framework from any OpenAPI spec.

## Decision Table

| Situation | What to reach for |
|---|---|
| New API consumed by another team/partner | Generate a spec from day one (springdoc for Spring MVC) |
| DTO already has `@NotBlank`/`@Min`/etc. | Let those drive the schema's `required`/`minimum` — don't duplicate by hand |
| Consuming team needs a typed client | `openapi-generator-cli` against the real generated spec |
| `/v3/api-docs` throws `NoClassDefFoundError` on a Hibernate-Validator class | Add a real Bean Validation provider (Hibernate Validator), not just `jakarta.validation-api` |

## Common Pitfalls

- Writing a Swagger doc by hand and treating it as permanently accurate with no synchronization mechanism.
- Duplicating constraint information by hand in `@Schema` instead of letting `@NotBlank`/`@Min` drive it.
- Assuming `jakarta.validation-api` alone is enough for springdoc's schema generation — verified: it isn't; a real provider is required.
- Generating a client once, then hand-editing it instead of regenerating when the spec changes.

## Interview Answer Skeleton

**30-sec:** A generated spec (springdoc) can't silently drift from the running API, because it's recomputed
from the code's own annotations on every request — unlike a hand-written spec, which routinely does drift.

**2-min:** Add: `jakarta.validation` constraints like `@Min(1)` are read a second time by the spec generator
and become real schema fields (`"minimum": 1`) — verified directly. `openapi-generator-cli` closes the loop,
generating a real typed client from that same spec.

**Staff-level framing:** at scale, the question isn't "generate vs. hand-write" for one API — it's whether
every team generates specs consistently into a shared, discoverable catalog with automated client-SDK
generation, versus each team inventing its own documentation convention.

## Related

- syllabus/07-api-design/api-design.md
- syllabus/07-api-design/grpc-api-design.md (compiler-enforced contract, for contrast)
- syllabus/07-api-design/api-versioning-strategies.md
