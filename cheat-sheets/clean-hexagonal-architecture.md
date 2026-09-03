---
title: "Cheat Sheet: Clean and Hexagonal Architecture"
slug: clean-hexagonal-architecture
document_type: cheat-sheet
domain: architecture
topic_id: T-901
canonical: ../handbook/architecture/clean-hexagonal-architecture.md
last_updated: 2026-08-04
---

# Clean and Hexagonal Architecture

**Canonical chapter:** [`syllabus/17-architecture/clean-hexagonal-architecture.md`](../syllabus/17-architecture/clean-hexagonal-architecture.md)

## Core Mental Model

Draw an arrow from every dependency in your codebase. If any arrow from your business logic points outward — toward a database, a framework, an SDK — you don't have this pattern, no matter what your folders are named. The entire pattern is one rule: dependencies point inward, always. Ports are how the domain states what it needs without depending on who provides it; adapters are the "who."

## Essential Definitions

- **Hexagonal architecture** (Alistair Cockburn, 2005, "Ports and Adapters") — the domain (business logic) has no compile-time dependency on anything outside it: not the database, not the web framework, not the message broker, not the cloud SDK.
- **Port** — an interface owned by the domain, stating what it needs or offers (e.g., `OrderRepository`, `PaymentGateway`, or `PlaceOrderUseCase`).
- **Adapter** — a concrete implementation of a port, living in infrastructure: a Postgres repository, a Stripe client, a REST controller.
- **Primary (driving) ports** — interfaces the outside world calls *into* the domain (e.g., `PlaceOrderUseCase.execute(command)`). **Secondary (driven) ports** — interfaces the domain calls *out through* (e.g., `OrderRepository.save(order)`).
- **Dependency Rule** (Robert Martin's Clean Architecture, 2012) — source code dependencies point only inward; nothing in an inner ring knows anything about an outer ring. Onion Architecture (Jeffrey Palermo, 2008) is a third name for the same shape.

## Decision Table

| Benefit | Cost |
|---|---|
| Domain testable with plain unit tests, no framework bootstrap | Extra interfaces and (often) mapping code for every port |
| Infrastructure swap touches only adapters | More files, more indirection for a newcomer to navigate |
| Enforces a single dependency direction, catchable in code review or with `ArchUnit` | Temptation to leak framework types through ports if not disciplined |
| Domain logic reads as business rules, not persistence mechanics | Overkill for a small CRUD service with no meaningful domain logic |

| Signal | Lean toward hexagonal | Lean toward simpler layering |
|---|---|---|
| Business-rule density | High — real domain logic to protect | Low — mostly CRUD + validation |
| Expected system lifetime | Years, multiple infra generations likely | Short-lived, prototype, or throwaway |
| Team size / parallel work | Multiple teams need a stable contract | Single small team, low coordination cost |
| Testing requirement | Fast, framework-free unit tests required | Integration tests are acceptable |

## Key Numbers

This is a compile-time/organizational pattern, not a runtime one — the chapter explicitly states no measured latency figures apply ("an extra interface dispatch is not measurable next to I/O"). The concrete evidence is organizational: an ORM migration estimated at **two weeks** took **over three months**, touching a large fraction of the codebase, because the domain wasn't actually clean.

## Common Pitfalls

- Believing hexagonal architecture is a folder layout — `domain/`, `application/`, `infrastructure/` packages with zero enforcement of the dependency rule is theater; a domain class can still `import javax.persistence.Entity` inside those folders
- Anemic use cases that just forward to the repository — if every "use case" is `repository.save(mapper.toEntity(dto))`, there is no domain logic being protected
- Claiming the mapping-code cost is negligible without having actually estimated it

## Interview Answer Skeleton

**30-sec:** Hexagonal/Clean/Onion architecture is one rule under three names: dependencies point inward, always. The domain defines ports; infrastructure provides adapters implementing them. The payoff is a testable, infrastructure-swappable domain; the cost is mapping code and indirection — and the honest answer to "use this everywhere?" is no, with a stated criterion.

**2-min:** Add why it exists (Cockburn's testability motivation) + primary/secondary ports + the mapping-cost trade-off + the two-weeks-vs-three-months ORM migration example.

**Whiteboard:** Draw the hexagon (driving adapters left, ports as connectors, driven adapters right), all arrows inward, then draw one wrong-direction arrow to illustrate the layered-architecture inversion it prevents.

**Staff-level framing:** hexagonal boundaries are team boundaries as much as code boundaries — a well-drawn port is a contract two teams can develop against in parallel without a shared merge conflict. Scope it per bounded context, starting with the highest-change-rate module, not the whole system at once — the indirection has a real onboarding and code-review cost that has to be weighed against the swap-cost benefit.

## Production Warning Signs

- **Real incident pattern:** a "quick" ORM migration (Hibernate→JDBC, estimated two weeks) takes over three months because a dependency-direction audit — run for the first time *during* the migration, via a tool like ArchUnit — finds dozens of domain classes directly importing `javax.persistence` annotations and, in several cases, calling `EntityManager` methods directly from classes labeled "domain services."
- The folder structure existed; the dependency rule had eroded silently over time with no automated check catching it. Prevention: verify hexagonal claims with an automated ArchUnit-style check from the start, not code review alone.

## Related

- `syllabus/17-architecture/ddd-tactical-design-aggregates.md`
- [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md)
