---
title: "Flashcards: Spring WebFlux and Reactive Programming"
slug: spring-webflux-and-reactive-programming
document_type: flashcard-deck
domain: spring
topic_id: T-509
canonical: ../handbook/spring/spring-webflux-and-reactive-programming.md
last_updated: 2026-09-01
---

# Flashcards: Spring WebFlux and Reactive Programming

**Canonical chapter:** [`handbook/spring/spring-webflux-and-reactive-programming.md`](../handbook/spring/spring-webflux-and-reactive-programming.md)

## Card: What's the real difference between a cold and a hot publisher?

**Prompt:**
Two subscribers subscribe to the same `Flux`. For a cold publisher, how many
times does the source's side effect run? For a hot publisher?

**Answer:**
Cold: once per subscriber, independently — measured directly at 2 real
executions for 2 subscribers. Hot: once, total, shared across all
subscribers via one underlying subscription — measured directly at 1 real
execution for 2 subscribers (via `ConnectableFlux`).

**Why it matters:**
Choosing the wrong one silently either duplicates expensive work (cold
reused as if hot) or misses per-subscriber customization (hot used where
independent execution was needed).

**Common trap:**
Assuming all `Flux`/`Mono` are cold by default without checking — most factory
methods (`Flux.just`, `Flux.range`, `Flux.defer`) are cold; `ConnectableFlux`
and `Sinks`-backed sources are hot.

**Related:**
[handbook/spring/spring-webflux-and-reactive-programming.md](../handbook/spring/spring-webflux-and-reactive-programming.md)

## Card: Does publishOn always leave the source on the original thread?

**Prompt:**
Does `publishOn` guarantee the upstream source never runs on the scheduler it
specifies?

**Answer:**
No — a real, honest discovery: for a `Fuseable` source (like
`Mono.fromSupplier`), `publishOn` can negotiate operator fusion and pull data
via its own worker thread instead of the source pushing it, meaning the
source itself runs there too. Measured directly: without `.hide()`, the source
ran on `boundedElastic-1`; with `.hide()` (disabling fusion), it correctly
stayed on `main`.

**Why it matters:**
It's a real caveat many explanations of `subscribeOn`/`publishOn` skip
entirely — knowing it signals genuine internals understanding.

**Common trap:**
Assuming the textbook "publishOn only affects downstream" rule holds
universally regardless of source type.

**Related:**
[handbook/spring/spring-webflux-and-reactive-programming.md](../handbook/spring/spring-webflux-and-reactive-programming.md)

## Card: Why is a partial reactive migration sometimes worse than no migration at all?

**Prompt:**
A service migrates from blocking Spring MVC to WebFlux, but one repository
method still makes a real blocking JDBC call inside the reactive chain. Why
can this perform *worse* than the original blocking version?

**Answer:**
WebFlux runs on far fewer threads than a thread-per-request servlet model;
one blocking call occupies one of those few threads for its full duration,
starving a disproportionately large number of other concurrent requests
sharing that same small pool. Measured directly: a real ~3x slowdown from
leaving blocking calls unoffloaded versus using
`subscribeOn(Schedulers.boundedElastic())`.

**Why it matters:**
It's a real, structural risk of incremental reactive adoption, not a
hypothetical — every call in the path must be genuinely non-blocking.

**Common trap:**
Assuming a WebFlux migration is always net-positive regardless of whether
every dependency has also been migrated to non-blocking equivalents.

**Related:**
[handbook/spring/spring-webflux-and-reactive-programming.md](../handbook/spring/spring-webflux-and-reactive-programming.md)
