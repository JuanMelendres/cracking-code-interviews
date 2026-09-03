---
title: "Cheat Sheet: DDD Strategic Design — Bounded Contexts and Context Mapping"
slug: ddd-strategic-bounded-contexts-and-context-mapping
document_type: cheat-sheet
domain: architecture
topic_id: T-902
canonical: ../handbook/architecture/ddd-strategic-bounded-contexts-and-context-mapping.md
last_updated: 2026-09-02
---

# DDD Strategic Design — Bounded Contexts and Context Mapping

**Canonical chapter:** [`syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md`](../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)

## Core Mental Model

A bounded context is the boundary within which a specific model, and a specific vocabulary, is unambiguous and internally consistent. Outside that boundary, the same word can — and often should — mean something else entirely. The mistake nearly every team makes once is assuming a shared word implies a shared model. Strategic DDD exists because forcing every team's "Order" to be one universal class produces a class no team can safely change, since every change risks breaking someone else's unrelated meaning of the same word.

## Essential Definitions

- **Bounded context** — an explicit boundary (usually aligned with a service, module, or team) inside which a domain model and its ubiquitous language apply consistently.
- **Ubiquitous language** — the vocabulary used identically by domain experts, code, and conversation *within one bounded context*; the same word meaning something different elsewhere is expected, not a bug.
- **Context mapping** — the practice of explicitly naming the relationship between two bounded contexts that must exchange information, because that relationship determines how change in one propagates (or doesn't) to the other.
- **Shared Kernel** — two contexts deliberately share a small, jointly-owned piece of model; changes require both teams' agreement.
- **Customer/Supplier** — an upstream/downstream relationship where the downstream has real, negotiated influence over the upstream's roadmap.
- **Conformist** — the downstream accepts the upstream's model as-is, with no translation and no negotiating power.
- **Anti-Corruption Layer (ACL)** — the downstream builds a translation layer converting the upstream model into its own, so upstream internal changes never propagate past that one layer.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Two teams already collaborate tightly and can commit to joint review of shared changes | Shared Kernel |
| Downstream team has real influence over the upstream team's priorities | Customer/Supplier |
| Upstream context is a third party or a team you have no leverage over | Conformist (if cost acceptable) or Anti-Corruption Layer |
| Downstream context needs to evolve independently of upstream churn | Anti-Corruption Layer |
| Two "same-named" concepts are actually different models under real inspection | Separate bounded contexts, not one shared model |

**Relationship comparison:**

| Relationship | Translation cost | Downstream influence over upstream | Isolation from upstream churn |
|---|---|---|---|
| Shared Kernel | None (shared code) | Joint ownership | None — a shared change affects both by design |
| Customer/Supplier | Low to moderate | Real, negotiated | Moderate |
| Conformist | None | None | None — proven as this chapter's real failure mode |
| Anti-Corruption Layer | Real, ongoing (the translator) | None | High — proven directly |

## Key Numbers (real, executed `javac` compile evidence)

- After Sales renames `customerName` to `buyerName`: the ACL-consuming `AclFulfillmentService.java` compiles unchanged (exit code 0). The Conformist-consuming `ConformistFulfillmentService.java` (byte-identical to its pre-rename version, verified via real `diff`) fails: `error: cannot find symbol ... method getCustomerName() ... location: variable order of type SalesOrder` (exit code 1).
- Both consumer files were confirmed byte-identical between the before/after trees — the only difference in outcome was which context-mapping relationship the consumer used.

## Common Pitfalls

- Treating "DDD" as synonymous with its tactical patterns (aggregates, entities, value objects) and having no answer when a strategic question arrives.
- Assuming a shared word ("Order," "Customer") implies the two contexts should share one model, rather than recognizing different meanings across contexts as the expected, healthy case.
- Choosing Conformist by default because it's cheapest to build initially, without weighing the real coupling cost.
- Answering "two teams disagree on what X means" as a communication problem to resolve by consensus, rather than a modelling question resolved by drawing a boundary.

## Interview Answer Skeleton

**30-sec:** A bounded context is where a specific model and vocabulary apply consistently; outside it, the same word can mean something else. Context mapping names the relationship between two contexts that must exchange data — Shared Kernel, Customer/Supplier, Conformist, or Anti-Corruption Layer — and that choice determines whether one context's internal changes break the other.

**2-min:** Add the real compile proof: after an upstream rename, the Conformist consumer failed to compile (`cannot find symbol`) while the byte-identical-before-rename ACL consumer compiled unchanged, because only its translator absorbed the change.

**Whiteboard:** Draw two circles, "Sales" and "Fulfillment," each with its own "Order" box with different fields. Conformist version: a direct arrow from Fulfillment's box straight into Sales's box. ACL version: a diamond labeled "Translator" sitting on the line between them, arrow from Fulfillment stopping at the diamond, separate arrow from the diamond into Sales's box. Say explicitly: "everything on Fulfillment's side of that diamond is protected from anything on Sales's side."

**Staff-level framing:** Connect context-mapping decisions to team topology and Conway's Law explicitly — a context boundary decision is simultaneously a technical and organizational proposal. Discuss retrofitting bounded contexts onto a legacy system that grew without them, including using a temporary ACL during a migration window, later replaced by genuine service separation.

## Production Warning Signs

- Adding a single new field to a shared entity requires a coordinated cross-team deployment — a sign that genuinely different bounded contexts (each with its own concerns) have been collapsed into one Conformist-style shared model with no boundary drawn.
- A team refactoring their own model discovers a downstream team's build breaking — silent Conformist coupling discovered at the worst possible time.
- An Anti-Corruption Layer whose translator output still exposes the upstream's exact field names, types, or invariants — a false sense of isolation while the coupling persists one layer down.

## Related

- `syllabus/17-architecture/ddd-tactical-design-aggregates.md`
- `syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md`
- `syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md`
- `syllabus/17-architecture/strangler-fig-and-migration-patterns.md`
