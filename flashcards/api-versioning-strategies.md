---
title: "Flashcards: API Versioning Strategies"
slug: api-versioning-strategies
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-919
canonical: ../syllabus/07-api-design/api-versioning-strategies.md
last_updated: 2026-09-16
---

# Flashcards: API Versioning Strategies

**Canonical chapter:** [`syllabus/07-api-design/api-versioning-strategies.md`](../syllabus/07-api-design/api-versioning-strategies.md)

## Card: What happens when a header-versioned endpoint gets no version header at all?

**Prompt:**
An endpoint uses `@GetMapping(headers = "Api-Version=1")` / `"Api-Version=2"` for its two versions. A
caller sends the request with no `Api-Version` header. What real HTTP status do they get?

**Answer:**
A real `404` — verified directly against real Spring MVC dispatch. The `headers` request condition has no
matching handler when the header is absent; there is no default-version fallback unless you build one
explicitly.

**Why it matters:**
This 404 is indistinguishable from "resource doesn't exist" in most logs, making a missing-header bug far
harder to diagnose than the equivalent URI-path mistake.

**Common trap:**
Assuming header-based versioning falls back to a sensible default version.

**Related:**
[API Versioning Strategies](../syllabus/07-api-design/api-versioning-strategies.md)

## Card: Does a generic `Accept: */*` fail on a media-type-versioned endpoint?

**Prompt:**
An endpoint uses `produces = "application/vnd.myapi.v1+json"` / `v2+json` for its two versions. A caller
sends `Accept: */*` (what curl sends by default). What happens?

**Answer:**
A real `200` — verified directly, not a `406`. Content negotiation resolves the generic `Accept` against
whichever version-specific handler it matches first, silently, with no signal to the caller that a
specific version was chosen for them.

**Why it matters:**
A caller who forgets to pin an exact `Accept` value gets a specific, possibly outdated, version
indefinitely with zero error.

**Common trap:**
Assuming an ambiguous `Accept` header gets rejected rather than silently resolved.

**Related:**
[API Versioning Strategies](../syllabus/07-api-design/api-versioning-strategies.md)

## Card: The four real API versioning strategies

**Prompt:**
Name the four real ways an API can be versioned, and what part of the HTTP request each one uses as the
signal.

**Answer:**
URI path (`/v1/`, `/v2/` — the URL itself); query parameter (`?version=1` — a request parameter); custom
header (`Api-Version: 1` — a bespoke header); media-type/content negotiation (`Accept:
application/vnd.api.v1+json` — HTTP's own existing negotiation mechanism).

**Why it matters:**
A candidate who names only URI-path versioning as "the" strategy signals shallow, list-memorized
knowledge rather than real API-design experience.

**Common trap:**
Treating URI-path versioning as the only real option.

**Related:**
[API Versioning Strategies](../syllabus/07-api-design/api-versioning-strategies.md)
