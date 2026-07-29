# T-901 · Clean / Hexagonal Architecture

**IWI 7.25 · Advanced tier · Prerequisite for:** T-903 (aggregates), T-912 (technology replacement boundaries), most of Chapter 07

---

## 1. The concept

Hexagonal architecture (Alistair Cockburn, 2005, "Ports and Adapters") organizes a system around one rule: **the domain — your business logic — has no compile-time dependency on anything outside it.** Not the database, not the web framework, not the message broker, not the cloud SDK.

The domain defines **ports**: interfaces stating what it needs (`OrderRepository`, `PaymentGateway`) or what it offers (`PlaceOrderUseCase`). Everything outside the domain — a Postgres repository, a Stripe client, a REST controller — is an **adapter** that implements or calls a port. Dependencies point in exactly one direction: inward, toward the domain.

```mermaid
graph TD
    subgraph "Driving side (things that call your app)"
        REST[REST Controller]
        CLI[CLI Command]
        Test1[Test]
    end
    subgraph "Domain (the hexagon)"
        UseCase[PlaceOrderUseCase]
        Domain[Order, Money, Customer]
        Port1[["OrderRepository (port)"]]
        Port2[["PaymentGateway (port)"]]
    end
    subgraph "Driven side (things your app calls)"
        PG[(Postgres Adapter)]
        Stripe[Stripe Adapter]
    end

    REST --> UseCase
    CLI --> UseCase
    Test1 --> UseCase
    UseCase --> Domain
    UseCase --> Port1
    UseCase --> Port2
    Port1 -.implemented by.-> PG
    Port2 -.implemented by.-> Stripe
```

Clean Architecture (Robert Martin, 2012) is the same idea generalized into concentric rings — Entities, Use Cases, Interface Adapters, Frameworks & Drivers — with the **Dependency Rule**: source code dependencies point only inward, and nothing in an inner ring knows anything about an outer ring. Onion Architecture (Jeffrey Palermo, 2008) is a third name for the same shape. Whichever term an interviewer uses, the mechanism being tested is identical: **inversion of the dependency between domain and infrastructure.**

## 2. Why it exists

Layered architecture (`Controller → Service → Repository → Database`) looks similar but inverts the actual dependency: the "domain" logic living in the service layer typically imports JPA entities, transaction annotations, and framework types directly. The dependency points *outward*, toward infrastructure, even though the diagram is drawn as if it points down.

This matters concretely at the moment infrastructure changes: swapping ORMs, moving from REST to gRPC, or extracting a bounded context into its own service. In a layered system with a leaky service layer, that change touches every class that imported the old framework type — often the majority of the codebase. In a hexagonal system, it touches only the adapters implementing the affected port; the domain and use cases don't move.

The **historical motivation** was testability: Cockburn was trying to solve the specific problem of tests that could only run against a live database or a live UI, making TDD impractical for anything beyond trivial logic. A domain with no infrastructure dependency can be unit-tested with plain object construction — no `@SpringBootTest`, no Testcontainers, no mocks of framework classes.

## 3. How it works internally

**Primary (driving) ports** are interfaces the outside world calls *into* the domain — `PlaceOrderUseCase.execute(command)`. **Secondary (driven) ports** are interfaces the domain calls *out through* — `OrderRepository.save(order)`. The distinction matters because primary ports are typically one-per-use-case (small, task-shaped) while secondary ports are typically one-per-resource (broader, CRUD-shaped or narrower, depending on interface segregation choices).

Where do JPA entities live? Three defensible answers, in increasing order of purity:
1. **Separate persistence models + mappers** — a `Order` domain class and a distinct `OrderEntity` JPA class, converted at the repository adapter boundary. Maximum purity, real mapping-code cost.
2. **Annotated domain objects, pragmatic compromise** — the domain class carries `@Entity`/`@Id` annotations directly. The domain "knows about" JPA as a dependency, but no framework *behavior* leaks in (no lazy-loading proxies escaping the repository, no transaction management in domain code). Common in practice; defensible if stated as a deliberate trade-off.
3. **Domain depends on ORM types directly in its methods** (e.g., returns `Optional<OrderEntity>` from a domain-facing method) — this is the leak the pattern exists to prevent, not a valid option.

Transactions belong at the **application-service level** — the layer that orchestrates a use case, sitting just inside the primary port. The domain itself does not know transactions exist; it operates on already-loaded aggregates and returns new state.

## 4. Trade-offs

| Benefit | Cost |
|---|---|
| Domain testable with plain unit tests, no framework bootstrap | Extra interfaces and (often) mapping code for every port |
| Infrastructure swap touches only adapters | More files, more indirection to navigate for a newcomer |
| Enforces a single direction of dependency, catchable in code review or with `ArchUnit` | Temptation to leak framework types through ports if not disciplined |
| Domain logic reads as business rules, not persistence mechanics | Overkill for a small CRUD service with no meaningful domain logic |

**When NOT to use it:** a thin CRUD service that is, in substance, a typed wrapper over a single table with no business rules beyond validation. Applying hexagonal architecture unconditionally to that service adds indirection with no corresponding payoff — there is no domain logic to protect from infrastructure, so the domain/infrastructure boundary is drawn around nothing. A Staff-level answer says this explicitly; it is one of the most differentiating things a candidate can volunteer (see §7, follow-up 5).

## 5. Performance, memory, and concurrency implications

Hexagonal architecture is a compile-time/organizational pattern, not a runtime one — by itself it adds no meaningful latency (an extra interface dispatch is not measurable next to I/O). The indirect cost is at the mapping boundary: converting between a persistence model and a domain model on every read/write is allocation and CPU work that is easy to make expensive by mapping eagerly and completely when only a projection is needed. In a hot read path, mapping the entire aggregate graph to serve a summary view is the actual, measurable cost — not the architecture. This is why option 2 above (annotated domain objects) is sometimes the correct trade-off for a hot path: it removes the mapping cost at the price of purity.

## 6. Production example (template — fill from your own system)

> On **[a production service you've worked on]**, swapping **[persistence technology / external provider]** for **[replacement]** touched **[N] adapter classes and zero domain classes** — roughly **[X days]** instead of the **[Y weeks]** the initial estimate assumed, because the estimate was made before anyone confirmed the domain layer was actually clean.

Fill this from real experience before your first mock — a fabricated number collapses on the first follow-up ("what made the estimate wrong initially?").

## 7. Interview questions, with follow-ups

**Q1. What problem does hexagonal architecture solve that layered architecture does not?**
*Expected Senior answer:* names the *direction* of the dependency, not just "separation of concerns" — layered architecture also claims separation, so the differentiator has to be the inversion.
*Follow-up:* "Draw me a layered architecture where the service layer imports JPA types directly. Which layer is actually depending on which?"

**Q2. What exactly is a port, and what is an adapter? Give one of each.**
*Follow-up:* "Is a port a class or an interface? Why does that matter?" *(It must be an interface owned by the domain — if infrastructure owns the interface, the dependency direction inverts again.)*

**Q3. Where does the repository interface live, and why not next to its implementation?**
*Follow-up:* "What Java package would `OrderRepository` sit in versus `PostgresOrderRepository`?"

**Q4. Your domain model must not depend on JPA. What does that cost you, concretely?**
*Expected:* names the mapping layer and its maintenance cost honestly, doesn't oversell purity as free.

**Q5. Would you use this on every project?**
*This is the Staff-differentiating question.* Expected: **no**, with a concrete criterion (business-rule density, expected system lifetime, team size) — not "yes, always" and not "it depends" without the criterion.

**Q6. You are replacing PostgreSQL with DynamoDB. Which files change, and which must not?**
*Follow-up:* "What if the DynamoDB single-table design doesn't map cleanly onto your existing aggregate boundaries — what breaks first, the port or the domain model?"

**Q7. Isn't this a lot of mapping code?**
*Expected:* yes, and states when that cost is and isn't worth paying (§4/§8 anti-pattern).

**Q8. How do you handle transactions across the port boundary?**
*Expected:* application-service level; domain has no awareness transactions exist.

**Q9. What about queries that don't fit the repository abstraction — a complex report, a dashboard aggregate?**
*Expected, Staff-level:* a CQRS-lite read model that bypasses the domain/repository entirely for reads, stated as a deliberate, scoped exception rather than a crack in the architecture.

**Q10. How would you introduce this into an existing, tangled codebase without a rewrite?**
*Expected, Staff-level:* incrementally, starting at a single seam (usually the highest-change-rate module), using the Strangler Fig pattern — extract one use case behind a port, prove the pattern, expand.

## 8. Common mistakes and anti-patterns

- **Believing hexagonal architecture is a folder layout.** `domain/`, `application/`, `infrastructure/` packages with zero enforcement of the dependency rule is theater — a domain class can still `import javax.persistence.Entity` inside those folders. The rule is about **dependency direction**, verifiable with a tool like ArchUnit, not naming convention.
- **Anemic use cases that just forward to the repository** — if every "use case" is `repository.save(mapper.toEntity(dto))`, there is no domain logic being protected and the pattern is providing zero value (see §4, when not to use it).
- **Leaking a framework exception through a port** — e.g. a repository interface method that can throw `org.hibernate.LazyInitializationException`. The port's contract is now coupled to the adapter's implementation detail.
- **One port per method** rather than one port per cohesive capability — produces dozens of single-method interfaces and defeats the readability the pattern is meant to provide.

## 9. Staff-level discussion

At Staff scope, hexagonal boundaries are **team boundaries** as much as code boundaries — a well-drawn port is a contract two teams can develop against in parallel without a shared merge conflict. The migration cost of retrofitting the pattern into a legacy system is itself a planning artifact: it should be scoped per bounded context (start with the highest-change-rate module, not the whole system at once), and the "would I actually do this" judgment from Q5 becomes an organizational cost/benefit call, not just a technical one — indirection has a real cost in onboarding time and code-review overhead that has to be weighed against the swap-cost benefit for a system that may never actually swap its database.

## 10. Decision criteria (cheat sheet)

| Signal | Lean toward hexagonal | Lean toward simpler layering |
|---|---|---|
| Business-rule density | High — real domain logic to protect | Low — mostly CRUD + validation |
| Expected system lifetime | Years, multiple infra generations likely | Short-lived, prototype, or throwaway |
| Team size / parallel work | Multiple teams need a stable contract | Single small team, low coordination cost |
| Testing requirement | Fast, framework-free unit tests required | Integration tests are acceptable |

## 11. Exercises

1. Take one real aggregate from a system you know. Identify: is its "domain" logic actually free of framework imports today? List every framework type it touches.
2. Design the `OrderRepository` port for an order-placement use case. Write the interface only — no implementation.
3. Given a class `OrderService` that calls `entityManager.persist()` directly inside a method named `placeOrder`, rewrite it behind a port, and name exactly which file becomes the adapter.
4. Argue the anti-case: describe a real or plausible system where introducing this pattern would be a mistake, and say why.

*(Solutions are not provided here by design — Exercise 1 in particular only has value worked against your own system. Use `06-domain-purity-exercise.md` as the guided version of exercises 1–3.)*

## 12. References

- Alistair Cockburn, ["Hexagonal Architecture"](https://alistair.cockburn.us/hexagonal-architecture/) (original article, 2005)
- Robert C. Martin, *Clean Architecture*, Ch. 22 "The Clean Architecture", Ch. 23 "Presenters and Humble Objects"
- Jeffrey Palermo, "The Onion Architecture" (blog series, 2008)
- Vaughn Vernon, *Implementing Domain-Driven Design* — Ch. 4, "Architecture," for how hexagonal composes with DDD's bounded contexts
