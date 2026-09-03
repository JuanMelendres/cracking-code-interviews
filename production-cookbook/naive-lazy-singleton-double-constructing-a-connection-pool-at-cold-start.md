---
title: "Naive Lazy Singleton Double-Constructing a Connection Pool Under Cold-Start Load"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/architecture/design-patterns-applied.md
  - ../handbook/concurrency/java-memory-model-and-volatile.md
source: handbook/architecture/design-patterns-applied.md#production-scenarios
---

# Naive Lazy Singleton Double-Constructing a Connection Pool Under Cold-Start Load

## Context

A service's database connection pool is wrapped in a naive lazy Singleton (`if (pool == null) pool = new ConnectionPool(config);`).

## Symptoms

Under normal traffic it behaves correctly, but during a cold-start burst — many requests arriving concurrently right after a deployment, before any request has yet triggered pool construction — the database briefly reports far more open connections than the configured pool size should ever allow, and a handful of early requests fail with connection-related errors before things stabilize.

## Impact

A brief but real burst of connection-related failures immediately after every deployment, discovered via error-rate dashboards and initially misdiagnosed as a database capacity problem.

## Initial Hypotheses

- The database itself is undersized for the traffic burst — checked and ruled out; steady-state connection count afterward is well within the configured pool limit.
- A configuration mismatch between the pool's configured size and the database's actual max-connections setting — checked and ruled out; configuration values match exactly.
- The Singleton's lazy initialization races under concurrent first access — correct.

## Evidence

Connection pool metrics, timestamped precisely, show two distinct `ConnectionPool` objects were constructed within milliseconds of the deployment's first requests arriving — each independently opening its own full complement of connections to the database — before the code's own local reference settled on just one of them, silently leaking the other pool's connections until they eventually timed out.

## Investigation Timeline

1. Brief burst of connection-related request failures observed immediately following every deployment, via error-rate dashboards.
2. Database undersizing hypothesis checked against steady-state connection counts — ruled out; steady-state is well within the configured limit.
3. Pool-size-versus-database-max-connections configuration mismatch checked — ruled out; values match exactly.
4. Connection pool metrics inspected with precise timestamps around the deployment window, revealing two distinct `ConnectionPool` objects constructed within milliseconds of each other.
5. Root cause traced to the Singleton's `if (pool == null)` check racing under concurrent first access during the cold-start burst, with one pool's connections silently leaked until they timed out.

## Root Cause

The `NaiveLazySingleton` race: several of the first requests after deployment all observed `pool == null` concurrently and each constructed its own `ConnectionPool`, briefly doubling (or worse, depending on request concurrency) the number of open database connections until the race resolved and the extra pool's connections aged out.

## Immediate Mitigation

Add a startup-time "warm-up" call that forces the Singleton to initialize during application startup, before any real traffic arrives, closing the race window during the specific cold-start moment when it was actually being hit.

## Permanent Fix

Replace the hand-rolled lazy Singleton with either an enum-based Singleton (a measured, thread-safe fix) or, more idiomatically in a Spring-based codebase, a framework-managed singleton-scoped bean — Spring's own default bean scope already provides exactly the "exactly one instance, constructed once, thread-safely" guarantee this code was hand-rolling incorrectly.

## Alternatives Considered

Wrapping the existing `if (pool == null)` check in a `synchronized` block — a valid fix, but rejected in favor of the enum or DI-managed approaches, since both remove the need to reason about synchronization correctness by hand at all going forward.

## Trade-offs

None significant for the DI-managed fix — Spring already owns the application's dependency graph, so letting the framework manage the singleton lifecycle removes both this bug class and the testability cost of a hand-rolled global.

## Prevention

Treat any hand-rolled lazy Singleton in a codebase that already uses a DI framework as a design-review flag by default — the framework almost certainly already solves this problem correctly.

## Monitoring and Alerts

- Alert on open database connection count exceeding the configured pool size by any margin, specifically during the deployment window — this is the exact, measurable symptom of the race and would page before connection-related request failures accumulate.
- Instrument `ConnectionPool` construction itself with a counter metric; more than one construction event within a short window after a deployment is a direct signal of the race condition, independent of whether it has yet caused a visible connection-count spike.
- Add an automated startup-time check (or a code-review lint rule) flagging any hand-rolled `if (x == null) x = new ...()` Singleton pattern in a codebase already using a DI framework, converting the Prevention section's design-review heuristic into an automatically-enforced one.

## Interview Story

This maps directly to a "why is the naive lazy Singleton not thread-safe" question, arriving as a real, timestamped production incident. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a service briefly reported far more open database connections than its configured pool size allowed, immediately after every deployment, with a handful of early requests failing.
- **Task:** find the cause, having already ruled out database undersizing and a configuration mismatch.
- **Action:** inspected precisely timestamped connection-pool metrics and found two distinct `ConnectionPool` objects constructed within milliseconds of each other during the cold-start burst, tracing it to a race in the hand-rolled lazy Singleton's null check.
- **Result:** added a startup warm-up call as an immediate fix, then replaced the Singleton with a framework-managed Spring bean, removing the need to reason about the synchronization correctness of a hand-rolled pattern at all.

## Staff-Level Discussion

This incident's real lesson is less about the specific Singleton bug and more about why the bug survived unnoticed under normal traffic: a race that only manifests during a narrow, low-frequency window (concurrent first access, specifically right after deployment) is exactly the kind of defect that passes ordinary testing and code review, because most test traffic and most manual QA doesn't reproduce a genuine cold-start burst. A Staff engineer reviewing a codebase that already has a DI framework in place should treat any hand-rolled lifecycle-management code — a Singleton, a manual cache, a custom object pool — as a standing question: "why isn't the framework already doing this correctly?" The DI-managed fix's real value isn't just correctness, it's that it removes an entire category of future thread-safety bugs from being possible in this specific spot, rather than requiring every future engineer touching this code to re-verify synchronization correctness by hand.

## Related Handbook Chapters

- [Design Patterns Applied](../handbook/architecture/design-patterns-applied.md) — canonical Singleton pattern analysis and the `NaiveLazySingleton` race this incident reproduces.
- [Java Memory Model and volatile](../handbook/concurrency/java-memory-model-and-volatile.md) — the underlying visibility and atomicity concerns a hand-rolled lazy Singleton must get right and a framework-managed bean sidesteps entirely.
