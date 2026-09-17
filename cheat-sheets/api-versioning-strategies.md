---
title: "Cheat Sheet: API Versioning Strategies"
slug: api-versioning-strategies
document_type: cheat-sheet
domain: 07-api-design
topic_id: T-919
canonical: ../syllabus/07-api-design/api-versioning-strategies.md
last_updated: 2026-09-16
---

# API Versioning Strategies

**Canonical chapter:** [`syllabus/07-api-design/api-versioning-strategies.md`](../syllabus/07-api-design/api-versioning-strategies.md)

## Core Mental Model

Every versioning strategy answers "which version does this request want?" using a different part of the
HTTP request as the signal: the URL (path or query string) or a header (custom, or the standard `Accept`).
They differ most in what happens when that signal is missing or too generic — and that failure mode, not
the happy path, is what interviews actually probe.

## Essential Definitions

- **URI path versioning** — `/api/v1/...` vs. `/api/v2/...`, two separate route registrations.
- **Query parameter versioning** — `/api/users/1?version=1`.
- **Header versioning** — same URL, a custom header (`Api-Version: 1`) picks the handler.
- **Media-type/content-negotiation versioning** — same URL, the standard `Accept` header (`application/vnd.api.v1+json`) picks the handler.

## Decision Table

| Situation | Lean toward | Real evidence |
|---|---|---|
| Public API, third-party consumers | URI path | Version visible in every log line, unambiguous |
| Internal API, you control every client | Header or media-type | Keeps the URL/resource identity clean |
| Manual testing / quick internal tools | Query parameter | Typeable directly in a browser bar |
| Worried about silent wrong-version bugs | URI path | The only strategy where a missing/wrong version is loud |

## Common Pitfalls

- Assuming header-based versioning has a default version when the header is missing — verified: it's a real `404`, not a default.
- Assuming an ambiguous `Accept: */*` gets rejected — verified: it silently resolves to whichever version-specific handler is matched first, not a `406`.
- Versioning the whole API for one resource's breaking change.
- Treating a version bump as a one-time event instead of an ongoing deprecation/sunset commitment.

## Interview Answer Skeleton

**30-sec:** Four strategies — URI path, query parameter, custom header, media-type/content negotiation.
URI path is most visible/debuggable; the others keep URLs clean at the cost of a harder-to-diagnose
missing-signal failure.

**2-min:** Add: header versioning 404s with no explanation when the header's absent; media-type
versioning silently resolves a generic `Accept` to the first-matched version — verified directly against
real Spring MVC dispatch, not assumed.

**Staff-level framing:** a company with many APIs each versioning ad hoc creates real onboarding and
operational cost — a platform-wide consistent convention (often enforced at a shared API gateway) is the
Staff-level answer, not "pick whichever the team likes."

## Related

- syllabus/07-api-design/api-design.md
- syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
- syllabus/07-api-design/rest-api-fundamentals.md
