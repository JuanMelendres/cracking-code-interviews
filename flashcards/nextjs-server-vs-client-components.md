---
title: "Flashcards: Server Components vs. Client Components: The Actual Boundary"
slug: nextjs-server-vs-client-components
document_type: flashcard-deck
domain: frontend
topic_id: F-203
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-server-vs-client-components.md
last_updated: 2026-09-07
---

# Flashcards: Server Components vs. Client Components: The Actual Boundary

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-server-vs-client-components.md`](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md)

## Card: The precise Server Component safety claim

**Prompt:**
"Server Components are safe for secrets because the data never reaches the browser" — is this exactly correct? What's the precise claim?

**Answer:**
Not exactly. The precise claim: the Server Component's CODE never reaches the browser. Whether the secret's VALUE reaches the browser depends entirely on what the component renders — if it renders the raw secret, that value appears in the actual HTML sent to the browser.

**Why it matters:**
Verified directly: a secret string appeared zero times across every file in a real client bundle (`.next/static`), but exactly once in the real prerendered HTML for the page that rendered it.

**Common trap:**
Treating "Server Component" as an unconditional secrecy guarantee rather than a code-execution-location guarantee.

**Related:**
[Server Components vs. Client Components: The Actual Boundary](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md)

## Card: Why an async Client Component's error timing is a real, checked finding, not an assumption

**Prompt:**
Is a Client Component being `async function` always caught as a `next build` error?

**Answer:**
No — verify per version. In a real, live-tested Next.js 16.3.1 app, `next build` succeeded with an async Client Component; the restriction ("Only Server Components can be async at the moment") only surfaced as a runtime console error and dev-overlay crash once the component was actually rendered in a browser.

**Why it matters:**
This was discovered live, contradicting an initial pre-cutoff expectation of a build-time error — a concrete example of why current framework behavior should be verified directly rather than assumed from possibly-outdated knowledge, and why a `next build`-only CI check wouldn't catch this specific class of bug.

**Common trap:**
Assuming all Server/Client Component restrictions share the same enforcement timing (all build-time or all runtime).

**Related:**
[Server Components vs. Client Components: The Actual Boundary](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md)
