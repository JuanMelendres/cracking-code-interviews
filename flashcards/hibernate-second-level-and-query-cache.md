---
title: "Flashcards: Hibernate Second-Level and Query Cache"
slug: hibernate-second-level-and-query-cache
document_type: flashcard-deck
domain: databases
topic_id: T-603
canonical: ../handbook/databases/hibernate-second-level-and-query-cache.md
last_updated: 2026-09-01
---

# Flashcards: Hibernate Second-Level and Query Cache

**Canonical chapter:** [`syllabus/06-databases/hibernate-second-level-and-query-cache.md`](../syllabus/06-databases/hibernate-second-level-and-query-cache.md)

## Card: Does Hibernate protect its cache from its own native SQL updates?

**Prompt:**
If you run `session.createNativeQuery("UPDATE ...").executeUpdate()`, does
the second-level cache go stale?

**Answer:**
No — measured directly: this conservatively evicted the affected L2 region
automatically, showing up as a real cache miss (not a stale hit) on the
next load. Hibernate defends its cache against DML it executes through its
own API, including native SQL.

**Why it matters:**
It corrects a common, reasonable-sounding assumption ("native SQL bypasses
Hibernate, so it must bypass the cache too") with real, measured evidence.

**Common trap:**
Assuming "native" means "invisible to Hibernate" in every respect.

**Related:**
[handbook/databases/hibernate-second-level-and-query-cache.md](../syllabus/06-databases/hibernate-second-level-and-query-cache.md)

## Card: What write actually produces a stale second-level cache read?

**Prompt:**
What kind of write reproduces a genuine, silent stale-cache bug against
Hibernate's second-level cache?

**Answer:**
A write through a completely separate connection or process — one Hibernate
has zero visibility into. Measured directly: a real, independent
`java.sql.Connection` (via `DriverManager`, bypassing the Hibernate
`SessionFactory` entirely) updated a row directly, and a subsequent
Hibernate load returned a real, stale cache hit with the old value.

**Why it matters:**
It's the exact real-world shape of a multi-writer, multi-service
architecture bug, not a contrived edge case.

**Common trap:**
Assuming any SQL statement, run through any means, is equally invisible to
Hibernate's cache machinery.

**Related:**
[handbook/databases/hibernate-second-level-and-query-cache.md](../syllabus/06-databases/hibernate-second-level-and-query-cache.md), [Spring Cache Abstraction and Pitfalls](../syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md)

## Card: Why doesn't the query cache alone eliminate database hits?

**Prompt:**
You enable the query cache but not the entity cache. Does a query-cache hit
still touch the database?

**Answer:**
Very likely yes — the query cache stores only the matching entity IDs and a
timestamp, not the entity data itself. Without the entity cache enabled,
hydrating each ID's actual data still requires a real database lookup.

**Why it matters:**
It's a common, real misconfiguration that produces a smaller performance
benefit than expected, without any error or warning.

**Common trap:**
Assuming the query cache is a complete, standalone caching solution.

**Related:**
[handbook/databases/hibernate-second-level-and-query-cache.md](../syllabus/06-databases/hibernate-second-level-and-query-cache.md)
