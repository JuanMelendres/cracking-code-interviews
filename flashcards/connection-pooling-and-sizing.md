---
title: "Flashcards: Connection Pooling and Sizing (HikariCP)"
slug: connection-pooling-and-sizing
document_type: flashcard-deck
domain: databases
topic_id: T-607
canonical: ../handbook/databases/connection-pooling-and-sizing.md
last_updated: 2026-09-02
---

# Flashcards: Connection Pooling and Sizing (HikariCP)

**Canonical chapter:** [`syllabus/06-databases/connection-pooling-and-sizing.md`](../syllabus/06-databases/connection-pooling-and-sizing.md)

## Card: Does a bigger pool always help?

**Prompt:**
Does increasing `maximumPoolSize` always improve throughput under contention?

**Answer:**
No — measured directly: a pool sized to exactly match a CPU-capped database's real
capacity (size 2) outperformed a pool 8x larger (size 16) by more than 2x on
identical work. Beyond real database capacity, more connections create contention
for the same finite resource, not usable concurrency.

**Why it matters:**
This is the register's own named misconception, and the single most common wrong
instinct when responding to pool-exhaustion errors.

**Common trap:**
Reflexively increasing pool size as the first response to any connection timeout,
without checking database utilization first.

**Related:**
[handbook/databases/connection-pooling-and-sizing.md](../syllabus/06-databases/connection-pooling-and-sizing.md)

## Card: HikariCP's real leak-detection minimum

**Prompt:**
What happens if you set HikariCP's `leakDetectionThreshold` to 1000ms?

**Answer:**
Nothing detectable — HikariCP silently disables leak detection entirely below a
real, enforced 2000ms minimum, logging its own WARN explaining why. This is a real
detail discovered by actually configuring it, not something obvious from the
setting's name alone.

**Why it matters:**
A team that sets an aggressive threshold expecting fast detection may have silently
disabled the feature entirely without realizing it.

**Common trap:**
Assuming any positive value for `leakDetectionThreshold` takes effect as configured.

**Related:**
[handbook/databases/connection-pooling-and-sizing.md](../syllabus/06-databases/connection-pooling-and-sizing.md)

## Card: What exception does pool exhaustion actually throw?

**Prompt:**
What real exception does HikariCP throw when a caller waits past
`connectionTimeout` for a connection?

**Answer:**
A real `SQLTransientConnectionException`, with the pool's exact state
(`total`, `active`, `idle`, `waiting`) embedded directly in the message — real,
actionable diagnostic information, not a generic timeout.

**Why it matters:**
Reading that embedded state is what distinguishes "the pool is exhausted" from "the
database itself is unreachable," a distinction easy to lose if only a generic
timeout message is logged upstream.

**Common trap:**
Treating any database-related timeout as evidence the database is down, without
checking whether it's actually a pool-level exhaustion signal.

**Related:**
[handbook/databases/connection-pooling-and-sizing.md](../syllabus/06-databases/connection-pooling-and-sizing.md)
