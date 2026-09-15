---
title: "Interview Question Bank — 17-architecture"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../17-architecture/INDEX.md
  - 16-performance-jvm.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Architecture

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 9 chapters yielded 28 deep questions + 28 quick-fire
questions = **56 real questions**. No Junior Fundamentals chapter exists in this
domain — architecture presupposes backend fundamentals already covered elsewhere.
`clean-hexagonal-architecture.md` genuinely has 10 deep questions (not a leveled
Junior/Mid template — the plain template with more Q&A pairs than usual).

---

## Architecture Decision Records

### Q1 — What's the single most common way an ADR fails to be useful, even when it's technically well-formatted?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/architecture-decision-records.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Focuses only on formatting/template compliance as if that were sufficient — the common mistake this question targets.
- **Senior:** Specifically names the missing-negative-consequences or justification-only-context failure mode.
- **Staff:** Connects this to the pattern's actual purpose (a durable record for a future disagreeing reader) and can correctly scope what a structural check can and can't catch.

### Q2 — A past ADR's decision no longer makes sense given how the system has grown. What do you do?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/architecture-decision-records.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Suggests the old ADR should be edited or deleted — the common mistake this question targets.
- **Senior:** Correctly states supersession — write a new ADR, mark the old one Superseded with a link, never edit in place.
- **Staff:** Explains why — the old ADR's value as a historical record of what was known and believed at the time is destroyed by editing it.

---

## Clean and Hexagonal Architecture

### Q1 — What problem does hexagonal architecture solve that layered architecture does not?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes folder structure instead of dependency direction, or conflates the two patterns as identical — the common mistake this question targets.
- **Senior:** States the dependency-direction inversion precisely and can point to a concrete leaky-layered example.
- **Staff:** Connects the inversion to a real organizational cost (team coupling, migration cost), not just the technical definition.

### Q2 — What exactly is a port, and what is an adapter? Give one of each.

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a roughly correct definition of both, even if imprecise.
- **Senior:** Correctly distinguishes primary (driving) vs. secondary (driven) ports when asked.
- **Staff:** Discusses interface segregation trade-offs — one broad repository port vs. several narrow, single-capability ports.

### Q3 — Where does the repository interface live, and why not next to its implementation?

**Canonical treatment:** [§ Interview Questions, Q3](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers "next to the implementation, like normal Java convention" — the common mistake this question targets.
- **Senior:** States correctly that the interface belongs in the domain package, since the domain owns the contract it depends on.
- **Staff:** Connects this to the Dependency Inversion Principle explicitly, by name.

### Q4 — Your domain model must not depend on JPA. What does that cost you, concretely?

**Canonical treatment:** [§ Interview Questions, Q4](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims there is no cost, or that it's negligible without having estimated it — the common mistake this question targets.
- **Senior:** Gives an honest, reasoned mapping-layer cost estimate.
- **Staff:** Discusses when the annotated-domain-object option is the better trade-off specifically to avoid this cost, and why that's not "cheating."

### Q5 — Would you use this on every project?

**Canonical treatment:** [§ Interview Questions, Q5](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers "yes, always" — the single most common failure on this topic, revealing memorization over understanding.
- **Senior:** Answers "no," with at least one concrete reason (business-rule density, expected lifetime, team size).
- **Staff:** Produces a specific counter-example and reasons about the cost/benefit in terms a business stakeholder would recognize.

### Q6 — You are replacing PostgreSQL with DynamoDB. Which files change, and which must not?

**Canonical treatment:** [§ Interview Questions, Q6](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes zero changes anywhere — the common mistake this question targets.
- **Senior:** Correctly scopes the blast radius to adapters; domain and use cases don't change.
- **Staff:** Acknowledges a sufficiently different storage model can force a port redesign — hexagonal architecture makes swaps cheaper and better-contained, not free.

### Q7 — Isn't this a lot of mapping code?

**Canonical treatment:** [§ Interview Questions, Q7](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Denies the cost exists — the common mistake this question targets.
- **Senior:** Acknowledges the cost with a genuine trade-off answer.
- **Staff:** Ties the answer back to the Q5 sizing criterion — the mapping cost is exactly what's weighed against the domain-protection benefit.

### Q8 — How do you handle transactions across the port boundary?

**Canonical treatment:** [§ Interview Questions, Q8](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Puts `@Transactional` on domain methods or the repository adapter instead of the orchestrating service — the common mistake this question targets.
- **Senior:** Places the transaction boundary correctly at the application-service level and explains why.
- **Staff:** Discusses what happens when two repositories belong to different bounded contexts — the honest answer is this is exactly where a single transaction may no longer be appropriate.

### Q9 — What about queries that don't fit the repository abstraction — a complex report, a dashboard aggregate?

**Canonical treatment:** [§ Interview Questions, Q9](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Forces every read through the same repository port regardless of shape, producing a bloated interface — the common mistake this question targets.
- **Senior:** Recognizes the tension and proposes some form of read-side shortcut.
- **Staff:** Names it as CQRS-lite explicitly and explains why it doesn't violate the dependency rule.

### Q10 — How would you introduce this into an existing, tangled codebase without a rewrite?

**Canonical treatment:** [§ Interview Questions, Q10](../../17-architecture/clean-hexagonal-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a big-bang rewrite — the common mistake this question targets.
- **Senior:** Proposes an incremental approach in general terms.
- **Staff:** Names the Strangler Fig pattern explicitly and gives a concrete prioritization criterion (change frequency, highest pain point).

---

## CQRS: Read/Write Separation

### Q1 — Walk me through how you'd add CQRS to a slow reporting query, and what would concern you about it.

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/cqrs-read-write-separation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the mechanism (events, projector, read model) roughly correctly, without naming the trade-off.
- **Senior:** Describes the mechanism correctly and names eventual consistency as a real trade-off when asked.
- **Staff:** Names the trade-off unprompted, proposes a lag-monitoring approach, and states a concrete staleness bound the business can accept.

### Q2 — A colleague says "we're already using CQRS because our read and write DTOs are different classes." Do you agree?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/cqrs-read-write-separation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Agrees it's CQRS — the common mistake this question targets.
- **Senior:** Disagrees and correctly names the missing piece — no asynchronous boundary, no separate model being kept eventually consistent.
- **Staff:** Names CQRS-lite explicitly as the accurate label, and explains why the term "CQRS" being applied loosely is a common, worth-correcting source of confusion.

---

## DDD Strategic Design — Bounded Contexts and Context Mapping

### Q1 — Two teams disagree on what "Order" means. Resolve it.

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a single unified `Order` class as the "clean" solution — the common mistake this question targets.
- **Senior:** Names bounded contexts and at least one concrete context-mapping pattern (ACL or Conformist) as the resolution mechanism.
- **Staff:** Discusses which relationship to choose based on the teams' actual working relationship and the organizational implication of that choice.

### Q2 — What's the real difference between Conformist and Anti-Corruption Layer?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that ACL "protects" the downstream context, without precisely naming the mechanism.
- **Senior:** Correctly explains the mechanism for both, plus a concrete example of what breaks under Conformist that wouldn't under ACL.
- **Staff:** Names when Conformist is actually the correct choice despite the coupling cost, showing the pattern isn't a strict downgrade.

---

## DDD Tactical Design — Aggregates

### Q1 — What is an aggregate boundary, and why is it a *transaction* boundary?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/ddd-tactical-design-aggregates.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes aggregates purely in terms of object composition ("things that belong together") — the common mistake this question targets.
- **Senior:** States the invariant-driven boundary correctly — the boundary around objects that must be consistent together, saved atomically in one transaction.
- **Staff:** Names a concrete cross-aggregate consistency mechanism (saga/outbox) when the follow-up hits, not just "eventual consistency" as a buzzword.

### Q2 — What is the aggregate sizing rule, precisely?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/ddd-tactical-design-aggregates.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Models `Customer` and all their `Order`s as one aggregate "because they're related" — the common mistake this question targets, which makes every order touch-lock the customer.
- **Senior:** States the sizing principle (as small as the true invariant requires) and can identify an over-sized example.
- **Staff:** Connects over-sizing to a concrete concurrency cost (lock contention, larger blast radius for conflicts).

---

## Microservice Decomposition and the Monolith Trade-off

### Q1 — Where exactly do you draw a service boundary, and why there rather than one table over?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a boundary "wherever the code looks messy" — the common mistake this question targets.
- **Senior:** Proposes a boundary using a consistency-driven test — the aggregate-boundary test, where strong single-transaction consistency is not required across it.
- **Staff:** Explicitly connects the service boundary to the DDD aggregate-boundary concept, naming it directly.

### Q2 — Two services need one transaction. Now what?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a distributed transaction (2PC) as the default fix — the common mistake this question targets.
- **Senior:** Proposes eventual consistency / a saga as the general direction.
- **Staff:** Explicitly questions whether the boundary itself was drawn correctly before proposing a saga as the fix.

### Q3 — When would you merge two services back together?

**Canonical treatment:** [§ Interview Questions, Q3](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats microservices as a one-way door that's never reconsidered — the common mistake this question targets.
- **Senior:** Names at least one merge-back signal explicitly (always co-deployed, mostly synchronous critical-path calls, operational cost exceeding benefit).
- **Staff:** Proposes a concrete detection method (deployment correlation) rather than a purely qualitative judgment.

### Q4 — You have four engineers. Does microservices still make sense? Defend it.

**Canonical treatment:** [§ Interview Questions, Q4](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers "yes, microservices are always better" reflexively — the common mistake this question targets.
- **Senior:** Answers no, with reasoning — the organizational benefit requires multiple independently-scheduled teams.
- **Staff:** Names the specific condition that would flip the answer (the team splitting into genuinely separate, independently-scheduled sub-teams).

---

## The Modular Monolith as a Deliberate Choice

### Q1 — Your monolith has a package named `orders.internal`. Does that actually stop another module from depending on it?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/modular-monolith-as-a-deliberate-choice.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Says "no" without being able to explain why precisely.
- **Senior:** Correctly explains that Java's own access modifiers don't express "internal to this module" across packages without extra tooling.
- **Staff:** Names a concrete real mechanism (an architecture test, e.g. ArchUnit) and can describe exactly what such a tool checks and what a real violation report looks like.

### Q2 — How would you decide when a module in your modular monolith is ready to become its own service?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/modular-monolith-as-a-deliberate-choice.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "it's a separate package" as sufficient justification for extraction readiness — the common mistake this question targets.
- **Senior:** Correctly cites the decomposition chapter's specific criteria — independently-scheduled sub-teams needing independent deployment.
- **Staff:** Connects this chapter's own enforcement evidence to the decision — a module whose boundary has been continuously enforced is a genuinely low-risk extraction candidate.

---

## Strangler Fig, Anti-Corruption Layer, and Migration Patterns

### Q1 — How do you extract this incrementally without a rewrite?

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/strangler-fig-and-migration-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Names "incremental migration" or "gradual rollout" without the specific facade/routing mechanism.
- **Senior:** Names Strangler Fig specifically and describes the facade routing mechanism concretely.
- **Staff:** Adds dual-write and its connection to rollback safety, and names why a rewrite is usually the wrong default (undocumented legacy behavior).

### Q2 — How do you roll back mid-migration?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/strangler-fig-and-migration-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that dual-write is somehow involved, without the precise mechanism.
- **Senior:** Explains that disabling dual-write silently ends the rollback option, with a concrete example of what's lost.
- **Staff:** Proposes planning a calendared rollback-safety window in advance, with monitoring on its remaining duration, rather than an ad hoc "once confident" decision.

---

## Technical Debt and Evolutionary Architecture

### Q1 — Sell this refactor to a skeptical PM.

**Canonical treatment:** [§ Interview Questions, Q1](../../17-architecture/technical-debt-and-evolutionary-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Leads with "the code is messy" or cites SOLID/DRY as if self-evidently persuasive to a non-engineer — the common mistake this question targets.
- **Senior:** Uses a concrete, plausible metric (e.g., "the last three features touching this component took 40% longer") connected directly to a business outcome.
- **Staff:** Proposes the incremental remediation plan alongside the ask, and names the fitness function that will prevent recurrence.

### Q2 — How do you prevent this kind of coupling from creeping back in?

**Canonical treatment:** [§ Interview Questions, Q2](../../17-architecture/technical-debt-and-evolutionary-architecture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes "more code review" or "better documentation" without an automated mechanism — the common mistake this question targets.
- **Senior:** Names fitness functions specifically and describes a concrete example (a coupling count, a dependency-direction rule).
- **Staff:** Addresses threshold ownership and the review process for changing it, so the fitness function remains real governance.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What are the four sections Michael Nygard's original ADR pattern requires? | [Architecture Decision Records](../../17-architecture/architecture-decision-records.md#flashcards) |
| 2 | When a past ADR's decision no longer holds, do you edit it or write a new one? | [Architecture Decision Records](../../17-architecture/architecture-decision-records.md#flashcards) |
| 3 | What's the real test for whether an ADR's Consequences section is any good? | [Architecture Decision Records](../../17-architecture/architecture-decision-records.md#flashcards) |
| 4 | What is a port? | [Clean and Hexagonal Architecture](../../17-architecture/clean-hexagonal-architecture.md#flashcards) |
| 5 | What is an adapter? | [Clean and Hexagonal Architecture](../../17-architecture/clean-hexagonal-architecture.md#flashcards) |
| 6 | When should you NOT use hexagonal architecture? | [Clean and Hexagonal Architecture](../../17-architecture/clean-hexagonal-architecture.md#flashcards) |
| 7 | What's the difference between Command-Query Separation and CQRS? | [CQRS: Read/Write Separation](../../17-architecture/cqrs-read-write-separation.md#flashcards) |
| 8 | What is the one thing every CQRS explanation must name as a real cost, not a footnote? | [CQRS: Read/Write Separation](../../17-architecture/cqrs-read-write-separation.md#flashcards) |
| 9 | Does CQRS require Event Sourcing? | [CQRS: Read/Write Separation](../../17-architecture/cqrs-read-write-separation.md#flashcards) |
| 10 | Two teams disagree on what "Order" means. What's the structural resolution? | [DDD Strategic Design](../../17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#flashcards) |
| 11 | What did this chapter's real compile prove about Conformist vs. ACL? | [DDD Strategic Design](../../17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#flashcards) |
| 12 | Why are bounded contexts the correct unit for microservice decomposition? | [DDD Strategic Design](../../17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md#flashcards) |
| 13 | What is an aggregate root? | [DDD Tactical Design — Aggregates](../../17-architecture/ddd-tactical-design-aggregates.md#flashcards) |
| 14 | What decides aggregate boundaries? | [DDD Tactical Design — Aggregates](../../17-architecture/ddd-tactical-design-aggregates.md#flashcards) |
| 15 | How many repositories does an aggregate get? | [DDD Tactical Design — Aggregates](../../17-architecture/ddd-tactical-design-aggregates.md#flashcards) |
| 16 | What's the actual test for where to draw a service boundary? | [Microservice Decomposition and the Monolith Trade-off](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#flashcards) |
| 17 | Two services need one transaction — what does that signal? | [Microservice Decomposition and the Monolith Trade-off](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#flashcards) |
| 18 | Name a concrete signal that two services should be merged back. | [Microservice Decomposition and the Monolith Trade-off](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#flashcards) |
| 19 | Should a 4-engineer team default to microservices? | [Microservice Decomposition and the Monolith Trade-off](../../17-architecture/microservice-decomposition-and-monolith-tradeoff.md#flashcards) |
| 20 | Does naming a package `internal` actually stop another module from depending on it in Java? | [The Modular Monolith as a Deliberate Choice](../../17-architecture/modular-monolith-as-a-deliberate-choice.md#flashcards) |
| 21 | Why do module-level cycles need a separate check from single-direction boundary violations? | [The Modular Monolith as a Deliberate Choice](../../17-architecture/modular-monolith-as-a-deliberate-choice.md#flashcards) |
| 22 | When is a module in a modular monolith actually ready to become its own service? | [The Modular Monolith as a Deliberate Choice](../../17-architecture/modular-monolith-as-a-deliberate-choice.md#flashcards) |
| 23 | Why can a "we have a rollback plan" migration still lose data on rollback? | [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../17-architecture/strangler-fig-and-migration-patterns.md#flashcards) |
| 24 | Why is "a rewrite would be faster" usually the wrong call for a legacy system? | [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../17-architecture/strangler-fig-and-migration-patterns.md#flashcards) |
| 25 | How does this chapter's use of Anti-Corruption Layer differ from its use in DDD? | [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../17-architecture/strangler-fig-and-migration-patterns.md#flashcards) |
| 26 | Why does "this code is messy" usually fail to persuade a stakeholder? | [Technical Debt and Evolutionary Architecture](../../17-architecture/technical-debt-and-evolutionary-architecture.md#flashcards) |
| 27 | Give a concrete, minimal example of a fitness function. | [Technical Debt and Evolutionary Architecture](../../17-architecture/technical-debt-and-evolutionary-architecture.md#flashcards) |
| 28 | Why doesn't a good architecture review at project kickoff prevent this kind of coupling? | [Technical Debt and Evolutionary Architecture](../../17-architecture/technical-debt-and-evolutionary-architecture.md#flashcards) |

---

## Related

- [`16-performance-jvm.md`](16-performance-jvm.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
