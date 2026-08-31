# Hibernate second-level and query cache (T-603) — runnable verification

Real, executed Java 21 output backing
[`handbook/databases/hibernate-second-level-and-query-cache.md`](../../../../handbook/databases/hibernate-second-level-and-query-cache.md)
(T-603). Real Hibernate ORM 6.6.55.Final, a real second-level cache backed by
Hibernate's own JCache region factory over a real Ehcache 3 provider, and a
real in-memory H2 database. Extends
[`practice/java/hibernate-jpa/entity-lifecycle-and-n1/`](../entity-lifecycle-and-n1/)'s
established setup rather than duplicating it — that pack already covers the
first-level cache (the persistence context) in depth; this pack covers the
genuinely separate second-level and query caches.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -cp "out:lib/*" SecondLevelCacheDemo
java -cp "out:lib/*" QueryCacheDemo
java -cp "out:lib/*" StaleCacheAfterDirectSqlDemo
```

## Demo 1 — `SecondLevelCacheDemo`: a shared, cross-session cache

Real output:

```
=== First load, session A: real DB hit expected ===
Real L2 cache misses: 1 (expect 1)
Real L2 cache hits: 0 (expect 0)
Real L2 cache puts: 1 (expect 1)

=== Second load, session B (a DIFFERENT session): real L2 cache hit expected, no new SQL ===
Real L2 cache misses: 1 (expect still 1)
Real L2 cache hits: 1 (expect 1 -- served from L2, no SQL for session B)
```

The first-level cache (the persistence context) is per-session — it can't
explain this result, since session B is a brand-new session with an empty
persistence context. The real hit on session B's load is genuinely served by
the second-level cache, which is shared *across* sessions — proven directly
via Hibernate's own `Statistics` API, not inferred from timing.

## Demo 2 — `QueryCacheDemo`: caching a query's result set

Real output:

```
=== First run, session A: real query cache miss + real DB query ===
Real query cache misses: 1 (expect 1)
Real query cache hits: 0 (expect 0)

=== Second run, session B: real query cache hit -- no SQL re-issued for the query itself ===
Real query cache misses: 1 (expect still 1)
Real query cache hits: 1 (expect 1)
```

`.setCacheable(true)` on an HQL query caches its result — really just a list
of matching entity IDs plus a timestamp, not the full entity data. A cache
hit here still needs the actual `Product` rows hydrated from the second-level
cache (already proven working in Demo 1) or the database — the query cache
and the entity cache are two real, separate mechanisms working together, not
one cache doing both jobs.

## Demo 3 — `StaleCacheAfterDirectSqlDemo`: a real, hard-to-reproduce stale-cache bug

The first version of this demo used
`session.createNativeQuery("UPDATE product SET stock = 5 ...")` to simulate a
write "bypassing Hibernate" — and got real, correct data back (stock = 5)
every time, not the expected stale 100. Investigating with the real
`Statistics` API revealed why: that update showed up as a real cache **miss**
on the next load, not a hit — meaning Hibernate had actually evicted the
cache entry itself. **Real, honest correction:** Hibernate conservatively
invalidates the affected L2 cache region for any DML it executes through its
own APIs, including native SQL run via `session.createNativeQuery(...)`,
specifically to prevent this exact bug. A native query issued *through
Hibernate* is not a faithful reproduction of "Hibernate has no idea this
changed."

The real gotcha only appears when a write happens through a channel Hibernate
has **zero visibility into at all** — this demo now opens a real, completely
separate `java.sql.Connection` via `DriverManager.getConnection(...)`,
bypassing Hibernate's `SessionFactory`, connection pool, and query API
entirely (standing in for a different microservice, or a DBA running SQL
directly against the database):

```
=== A write through a REAL, completely separate JDBC connection --
    not through Hibernate's SessionFactory, connection pool, or query API at all ===
Real row updated via a real, independent JDBC connection -- Hibernate's L2 cache was never told.

=== A NEW Hibernate session loads the same entity ===
Real loaded stock: 100 (real row value is 5; stale L2 value would be 100)
Real L2 cache hits: 1, misses: 0 (a hit with stale=100 proves the bug; a miss means the cache genuinely had no stale data to serve)
```

This time the real `Statistics` API confirms a genuine **hit** — the stale
`100` is being served from the second-level cache, while the real row in the
database has already been updated to `5`. This is the real, decisive proof of
the eviction gotcha, and the earlier "failed" attempt is left documented
above rather than silently smoothed over, since it's itself a real, valuable
lesson about which writes Hibernate can and can't defend the cache against on
its own.

## Real discoveries made while building this pack

One real, substantive discovery, described in full above: **Hibernate
defends its own second-level cache against DML it executes itself — even raw
native SQL — but has no defense at all against a write through a completely
separate connection or process.** This matters production-wise: a
microservice architecture where multiple services (or a batch job, or a
DBA) write to the same tables outside the Hibernate-owning service's own
`SessionFactory` is exactly the real-world shape of this bug, and Hibernate's
own conservative self-protection does nothing to prevent it.
