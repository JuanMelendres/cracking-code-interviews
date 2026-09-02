---
title: "Cheat Sheet: The Modular Monolith as a Deliberate Choice"
slug: modular-monolith-as-a-deliberate-choice
document_type: cheat-sheet
domain: architecture
topic_id: T-910
canonical: ../handbook/architecture/modular-monolith-as-a-deliberate-choice.md
last_updated: 2026-09-02
---

# The Modular Monolith as a Deliberate Choice

**Canonical chapter:** [`handbook/architecture/modular-monolith-as-a-deliberate-choice.md`](../handbook/architecture/modular-monolith-as-a-deliberate-choice.md)

## Core Mental Model

A module boundary that isn't mechanically enforced is a comment, not a boundary. Naming a package `internal` communicates intent to a human reader, but Java's own compiler treats a `public class` in `orders.internal` exactly the same as one in `orders.api` — nothing stops another module from importing it directly. The real, load-bearing distinction between a genuinely modular monolith and one that only looks modular in its package diagram is whether something automated — not a linter's opinion, not a reviewer's memory — actually fails a build the moment that boundary is crossed.

## Essential Definitions

- **Modular monolith** — a single deployable unit whose internal code is organized into modules with explicit, enforced boundaries and well-defined public contracts between them; retains one deployable's operational simplicity while adopting the internal discipline that would otherwise motivate splitting into services.
- **Package naming vs. architecture test** — a package named `orders.internal` communicates intent, but every class in it remains `public`; only an automated architecture test (e.g., ArchUnit) checking real compiled dependency edges actually enforces the boundary.
- **Module-level cycle** — a structurally distinct risk from a single boundary violation: A depends on B and B depends on A, meaning the two can no longer be reasoned about, tested, deployed, or extracted independently — effectively merging two "modules" into one.
- **The modular monolith as a stepping stone** — because boundaries are already real and enforced, extracting a module into its own service later (once decomposition criteria are met) is comparatively mechanical: only the transport changes from in-process to network.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Multiple, independently-scheduled sub-teams genuinely need independent deployment | Consider microservice extraction (see the decomposition chapter) |
| That condition is not yet met | Default to a modular monolith |
| A module boundary has been drawn | Enforce it with a real, automated check from day one, not after the first violation |
| A dependency runs "backward" (a module needing its dependent to do something) | Use an event or callback interface owned by the depended-upon module, not a direct call back — avoids the cycle |

**Deployment model comparison:**

| | Modular monolith | Unmodularized monolith | Microservices |
|---|---|---|---|
| Deployment complexity | Low — one artifact | Low — one artifact | High — many independently deployed services |
| Enforced internal boundaries | Yes, if mechanized | No | Enforced by the network itself, at real distributed-systems cost |
| Extraction cost later | Low — boundaries already proven | High — boundaries have to be discovered first | N/A — already extracted |

## Key Numbers (real, executed ArchUnit checks against real compiled bytecode)

- Boundary violation: `shippinglegacy.LegacyShippingService` directly imports `orders.internal.PricingEngine`, compiling and running with zero errors. A real ArchUnit rule against actual compiled classes caught it: `FAIL ... Constructor <shippinglegacy.LegacyShippingService.<init>()> calls constructor <orders.internal.PricingEngine.<init>()> ...` (3 violations reported). The identical rule against the clean `shipping` module passed in the same run.
- Module cycle: `shipping` legitimately depends on `orders.api`; separately, `orders.internal.OrderCreatedNotifier` was added to call `shipping` directly — completing a real cycle. ArchUnit's slice-cycle check reported: `FAIL Cycle detected: Slice orders -> Slice shipping -> Slice orders`, naming the specific dependency responsible for each direction.

## Common Pitfalls

- Believing a package named `internal` or `impl` is itself a boundary — the compiler enforces nothing about that name.
- Treating code review as a sufficient, standalone enforcement mechanism for module boundaries at any real team size or tenure.
- Checking for boundary violations but not cycles, or vice versa — these are genuinely distinct defects needing distinct checks.
- Describing "modular monolith" as a synonym for "monolith we haven't gotten around to splitting yet," rather than a deliberate, actively-maintained architecture.

## Interview Answer Skeleton

**30-sec:** A modular monolith is a single deployable unit with real, enforced internal module boundaries — capturing team-autonomy and clear-ownership benefits usually attributed to microservices without paying their distributed-systems cost. The boundaries only count if mechanically enforced — a naming convention alone is a comment, not a boundary.

**2-min:** Add the real evidence: a class importing `orders.internal.PricingEngine` directly compiled and ran cleanly until a real ArchUnit rule caught it with a precise violation report. Add the real cycle example: a plausible shortcut (a direct call replacing an event) created a real `orders → shipping → orders` cycle, invisible to a single-PR reviewer, caught immediately by a dedicated slice-cycle check.

**Whiteboard:** Draw two boxes, "orders" and "shipping," each with an inner "api" sub-box and an "internal" sub-box. Solid arrow from shipping into orders' "api" (correct). A second, dashed, crossed-out arrow from shipping directly into orders' "internal": "compiles fine — nothing stops this without a real check."

**Staff-level framing:** Name a concrete enforcement mechanism and describe what it actually checks at the bytecode/dependency level. Distinguish single-boundary violations from cycles as genuinely different defects. Frame the modular monolith as a real stepping stone toward extraction under the decomposition chapter's own criteria, and connect technical module boundaries to real organizational ownership — an unenforced or unmaintained boundary is equivalent, in practice, to no boundary at all.

## Production Warning Signs

- A code review misses a new class importing another module's `internal` package directly — nothing in the build failed because nothing was checking; add the architecture test to CI so this class of defect fails automatically.
- Two independently well-structured modules become difficult to reason about or test independently, with changes in one unpredictably requiring changes in the other — check for a module-level cycle first, since the defect often looks fine in each individual pull request.
- An architecture test suite that's quietly bypassed or disabled as soon as it becomes inconvenient — worse than no test at all, since it creates false confidence that boundaries are still enforced.
- A cycle discovered only when someone tries to extract a module into its own service — the expensive-to-discover-late failure mode that continuous cycle-checking is meant to prevent.

## Related

- `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md`
- `handbook/architecture/ddd-tactical-design-aggregates.md`
- `handbook/architecture/clean-hexagonal-architecture.md`
- `handbook/architecture/architecture-decision-records.md`
