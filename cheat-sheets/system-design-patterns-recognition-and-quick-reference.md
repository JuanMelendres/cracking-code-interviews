---
title: "Cheat Sheet: System Design Patterns Recognition and Quick-Reference"
slug: system-design-patterns-recognition-and-quick-reference
document_type: cheat-sheet
domain: 11-system-design
topic_id: T-2427
canonical: ../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md
last_updated: 2026-09-22
---

# System Design Patterns: Recognition and Quick-Reference

**Canonical chapter:** [`syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md`](../syllabus/11-system-design/system-design-patterns-recognition-and-quick-reference.md)

## Core Mental Model

A mechanic diagnoses a car by sound before opening the hood — a specific symptom maps to a specific, named cause. A system-design prompt's plain-English requirement maps the same way to one of 8 named patterns; recognizing the mapping fast is the skill, not re-deriving each pattern from scratch.

## Essential Definitions

- **Recognition signal** — the specific phrase or constraint in a requirement ("read-heavy," "safe to retry," "protect from abuse") that points to a named pattern.
- **Pattern gallery** — one concrete example plus one compact diagram per pattern, distinct from that pattern's own canonical chapter's deeper treatment.
- **Overlapping signal** — a requirement that plausibly matches two patterns at once; resolved by the requirement's specific constraints, not the surface phrase.

## Decision Table

| Signal | Pattern | Canonical chapter |
|---|---|---|
| Read-heavy, same expensive query repeated | Caching | caching-strategies-and-invalidation.md |
| Multiple instances, route to a healthy one | Load balancing & service discovery | load-balancing-service-discovery-and-health-checking.md |
| Protect from abuse, fair usage per client | Rate limiting | rate-limiting-and-throttling-algorithms.md |
| Safe to retry, duplicate request | Idempotency | idempotency.md |
| Downstream can fail/slow down | Resilience (circuit breaker, retry, bulkhead) | resilience-patterns.md |
| Which database — transactions vs. flexible schema vs. graph | Storage selection | storage-selection-tradeoffs.md |
| Full-text search, rank by relevance | Search and indexing | search-and-indexing-systems.md |
| Live updates, push to client | Real-time delivery | realtime-delivery-websocket-sse-and-long-polling.md |

## Most-Confused Pairs

- **Caching vs. read replica** — cache when staleness is acceptable and the same value repeats; read replica when the reader needs real SQL query flexibility.
- **Rate limiting vs. circuit breaker** — rate limiting is proactive/caller-side (bounds outbound volume); circuit breaker is reactive (triggered by observed failures from the callee).
- **Idempotency vs. message dedup** — idempotency at a sync API/write boundary; dedup at an async broker/consumer boundary.

## Common Pitfalls

- Pattern-matching on a surface keyword without checking the requirement's actual constraints.
- Naming a pattern and stopping there instead of continuing into its real trade-offs in the canonical chapter.
- Missing that a requirement signals two patterns at once (see Most-Confused Pairs above).

## Interview Answer Skeleton

**30-sec:** System-design prompts rarely name a pattern directly — they describe a symptom. Recognizing which of 8 named patterns (caching, load balancing, rate limiting, idempotency, resilience, storage selection, search indexing, real-time delivery) a symptom points to, fast, is what frees up interview time for trade-offs instead of pattern discovery.

**2-min:** Add: most patterns are pairwise confusable on surface wording alone (caching vs. read replica, rate limiting vs. circuit breaker) — the deciding factor is always the requirement's specific constraints, not the phrase used.

**Staff-level framing:** A real production architecture layers several of these patterns together; the Staff-level judgment is sequencing and combining them correctly, not just naming each one in isolation.

## Related

- syllabus/11-system-design/system-design-method-and-estimation.md
- syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md
