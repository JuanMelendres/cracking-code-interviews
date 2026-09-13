---
title: "Flashcards: GraphQL API Design"
slug: graphql-api-design
document_type: flashcard-deck
domain: 07-api-design
topic_id: T-917
canonical: ../syllabus/07-api-design/graphql-api-design.md
last_updated: 2026-09-12
---

# Flashcards: GraphQL API Design

**Canonical chapter:** [`syllabus/07-api-design/graphql-api-design.md`](../syllabus/07-api-design/graphql-api-design.md)

## Card: Why N+1 happens in GraphQL

**Prompt:**
Why does a naive GraphQL resolver cause an N+1 problem?

**Answer:**
Each field has its own independent resolver; a field on every item in a list runs once per item unless explicitly batched.

**Why it matters:**
Measured directly in this chapter: 3 backend calls for 3 books, naive; 1 call, batched.

**Common trap:**
Assuming GraphQL handles this automatically without `DataLoader` or an equivalent.

**Related:**
[GraphQL API Design](../syllabus/07-api-design/graphql-api-design.md)

## Card: What decides partial vs. total failure

**Prompt:**
What determines whether a resolver failure nulls one field or the entire response?

**Answer:**
The failing field's nullability in the schema — non-null bubbles the null up to the nearest nullable ancestor.

**Why it matters:**
The precise mechanism behind the oversimplified claim "GraphQL always partially succeeds."

**Common trap:**
Assuming partial success is guaranteed regardless of schema design.

**Related:**
[GraphQL API Design](../syllabus/07-api-design/graphql-api-design.md)

## Card: GraphQL errors and HTTP status

**Prompt:**
What HTTP status code does a GraphQL API return when a resolver throws?

**Answer:**
Still `200` — error semantics live in the response body's `errors` array, not the status code.

**Why it matters:**
A common source of confusion for engineers moving from REST.

**Common trap:**
Expecting a `4xx`/`5xx` status the way a REST API would return one.

**Related:**
[GraphQL API Design](../syllabus/07-api-design/graphql-api-design.md)
