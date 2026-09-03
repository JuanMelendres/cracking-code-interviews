---
title: "Blocking JDBC Call Starving the WebFlux Event Loop"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../syllabus/05-spring/spring-webflux-and-reactive-programming.md
source: handbook/spring/spring-webflux-and-reactive-programming.md#production-scenarios
---

# Blocking JDBC Call Starving the WebFlux Event Loop

## Context

A service was migrated from Spring MVC to WebFlux. One repository method had not yet been migrated to a reactive database driver and made a real, synchronous JDBC call wrapped in `Mono.fromCallable(...)`, without `.subscribeOn(Schedulers.boundedElastic())`.

## Symptoms

The migrated service showed dramatically worse p99 latency under production load than the old blocking version it replaced — the opposite of the expected outcome of the migration.

## Impact

The service performed worse than before the migration under real production load, undermining the stated purpose of the migration and degrading latency for every request, not only the ones touching the unmigrated repository method.

## Initial Hypotheses

- Insufficient Netty event-loop threads configured for the new runtime — this was the first hypothesis pursued.

## Evidence

One repository method made a real, synchronous JDBC call wrapped in `Mono.fromCallable(...)` but without `.subscribeOn(Schedulers.boundedElastic())`. This blocked one of WebFlux's small, fixed number of event-loop threads for the duration of each database call.

## Investigation Timeline

1. **Latency regression observed** after the WebFlux migration, worse than the blocking version it replaced.
2. **Thread-pool sizing investigated first**, on the assumption the event loop simply needed more threads.
3. **Reactive chains audited for blocking calls**, surfacing the unmigrated repository method's synchronous JDBC call with no `subscribeOn`.
4. **Mechanism confirmed**: because WebFlux deliberately uses far fewer threads than a thread-per-request servlet model, each blocked event-loop thread starved a disproportionately large number of other concurrent requests, not just the ones touching that repository method.

## Root Cause

Every request touching the unmigrated repository method blocked one of the event loop's small, fixed number of threads for the duration of the database call. Because WebFlux relies on a small thread pool servicing many concurrent requests non-blockingly, one blocking call reachable from that pool starves a disproportionately large number of unrelated concurrent requests — not just the one that made the call.

## Immediate Mitigation

Added `.subscribeOn(Schedulers.boundedElastic())` around the blocking call as a stopgap, moving it off the event loop onto a bounded pool intended for blocking work.

## Permanent Fix

Completed the migration to a reactive database driver (R2DBC) for that repository, removing the blocking call entirely.

## Alternatives Considered

Leaving the call on `boundedElastic()` permanently instead of completing the R2DBC migration. Rejected because the stopgap still pays scheduler-hopping overhead on every call and keeps the service on two different persistence models indefinitely, rather than finishing the migration it had already started.

## Trade-offs

The stopgap fix added scheduler-hopping overhead, accepted as strictly better than event-loop starvation while the full R2DBC migration was completed.

## Prevention

Added a static-analysis rule flagging any blocking-API call (JDBC drivers, `Thread.sleep`, blocking HTTP clients) reachable from a reactive method without an adjacent `subscribeOn`.

## Monitoring and Alerts

- Event-loop thread utilization and blocking-call detection (e.g., BlockHound in test and staging environments) as a standing check specifically for reactive services, since a blocking call's damage is disproportionate to its own frequency.
- p99 latency compared against the pre-migration baseline for a defined period after any reactive migration, rather than assuming the migration succeeded once it deploys without errors.

## Interview Story

This maps to a "why did our reactive migration make things worse" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a WebFlux migration produced worse p99 latency than the blocking service it replaced.
- **Task:** find why a reactive rewrite performed worse than the model it was meant to improve on.
- **Action:** ruled out event-loop thread-pool sizing; audited reactive chains for blocking calls and found one unmigrated JDBC call with no `subscribeOn`.
- **Result:** applied `subscribeOn(boundedElastic())` as an immediate fix, then completed the R2DBC migration to remove the blocking call entirely.

## Staff-Level Discussion

A partial reactive migration is often worse than no migration at all, because it introduces the reactive model's thread-starvation risk without yet removing the blocking calls that trigger it — the small, fixed thread pool that makes WebFlux efficient when every call is truly non-blocking is exactly what makes it fragile when even one call isn't. This has a direct migration-strategy consequence: a reactive migration should either be completed end-to-end for a given request path before it goes to production, or every remaining blocking call on that path must be explicitly and verifiably scheduled off the event loop — "we'll finish migrating the rest later" is not a safe intermediate state the way it would be for most incremental migrations, because the failure mode compounds across unrelated requests rather than staying contained to the unmigrated code.

## Related Handbook Chapters

- [Spring WebFlux and Reactive Programming](../syllabus/05-spring/spring-webflux-and-reactive-programming.md) — canonical event-loop model and blocking-call mechanism used here.
