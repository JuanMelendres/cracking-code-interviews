---
title: "Design Exercise — Test Strategy for a Checkout Service"
week: 18
document_type: study-pack-design-exercise
status: draft
last_reviewed: 2026-08-02
---

# Design Exercise — Test Strategy for a Checkout Service

**Format:** 45 minutes, whiteboard or written. Produce a full test strategy for the service below, applying all five of this week's topics explicitly.

## The scenario

Your team owns a checkout service: it validates a cart, calls an external payments provider, calls an internal inventory service to reserve stock, and returns an order confirmation. It's consumed by a web frontend, a mobile app, and an internal admin tool. The service has a unit-test suite with 88% line coverage that the team is reasonably confident in, but a recent incident revealed a boundary bug (an order at exactly the maximum allowed cart size was silently rejected when it should have been accepted) that no existing test caught. Leadership has asked for a documented test strategy before the next major feature (a new "buy now, pay later" payment option) ships.

## Design this

1. **Coverage vs. confidence:** The team cites "88% coverage" as evidence of good testing. How do you respond, and what would you actually check?
2. **The boundary-bug incident:** What technique would you introduce specifically to catch this class of bug going forward, and how would you scope its use (not everywhere, given its cost)?
3. **Inventory-service integration:** The inventory service is owned by a different team. How do you verify checkout stays compatible with it as both evolve independently?
4. **Performance readiness for the new payment option:** The new "buy now, pay later" flow calls an additional external provider. What testing would you run before launch, and what would trigger re-running it later?
5. **A live-coding interview for a new hire:** You're asked to design a 30-minute live-coding round for a Senior candidate joining this team. What would you have them build, test-first, and what would you specifically be watching for?

Work through your answer before reading the reference solution below.

---

## Reference Solution

**1. Coverage vs. confidence.** 88% line coverage tells you which code *executed* during test runs, not whether the assertions were strong enough to actually *verify* correct behavior (`05-mutation-and-property-based-testing.md`) — exactly what the boundary-bug incident demonstrates directly: the boundary-adjacent code very likely executed during existing tests, but no assertion actually checked the exact boundary value. The right response isn't dismissing coverage as useless, but treating it as necessary-not-sufficient, and proposing a targeted mutation-testing run on checkout's core validation logic specifically to measure whether the existing assertions would actually catch a class of defect similar to the one that just shipped.

**2. Catching the boundary-bug class going forward.** Two complementary techniques: mutation testing (scoped specifically to the cart-validation and boundary-condition-heavy modules, given its real computational cost — not applied blanket across the whole codebase) to directly measure whether existing assertions would catch a real boundary-shifted defect; and, where a clean invariant exists (e.g., "a cart at or under the maximum size is always accepted, one over is always rejected"), a property-based test sweeping cart sizes across a range including the exact boundary, rather than relying on a single hand-picked boundary example that itself could still miss an off-by-one in a different direction.

**3. Inventory-service compatibility.** Consumer-driven contract testing (`03-contract-testing-for-services.md`): checkout, as the consumer, defines a contract describing exactly which inventory-service fields and status codes it actually depends on (e.g., a specific "insufficient stock" error code checkout branches on). The inventory team runs contract verification against their real implementation in their own CI pipeline — catching a breaking change (a renamed field, a removed status code) before it ships, without requiring checkout's full application to be running, and without reintroducing the manual cross-team coordination this pattern is meant to replace.

**4. Performance readiness for the new payment flow.** Run a load test (`01-performance-and-load-testing-methodology.md`) against the new flow specifically, with a traffic *shape* that resembles realistic buy-now-pay-later adoption (not just raw checkout volume — this flow likely has a different latency profile given the additional external provider call), reporting against a percentile threshold, not a mean-latency threshold. Given the new flow adds a genuinely new external dependency, also consider a stress test ahead of launch specifically to understand failure behavior if that new provider is slow or unavailable (does checkout degrade gracefully, e.g., falling back to other payment options, or does it hang/fail the whole request). Trigger for re-running: any future change to the payment-provider integration itself, plus a standing quarterly re-validation given how traffic patterns evolve over time.

**5. Live-coding round design.** Have the candidate implement, test-first, a small self-contained piece of checkout-adjacent logic (e.g., "given a list of cart line items, compute the total with a quantity-based discount tier") — small enough to complete in 30 minutes with a genuine, narrated red-green-refactor loop (`02-writing-tests-live-in-an-interview.md`), not an already-solved algorithm. Watch specifically for: whether test cases are chosen in a deliberate smallest-to-largest order and narrated; whether each red step is confirmed to fail for the *expected* reason before moving on; and, if the candidate runs short on time, whether they communicate an explicit scope-reduction plan rather than silently rushing or silently dropping the test-first discipline — this last signal is the one most directly transferable to how the candidate would actually behave on this team under a real production deadline.

## Self-Check

- [ ] Treated 88% coverage as necessary-but-not-sufficient, and connected the boundary-bug incident directly to what coverage cannot measure
- [ ] Proposed mutation testing scoped to specific high-risk modules, not a blanket full-codebase requirement, and named its real cost as the reason for that scoping
- [ ] Proposed consumer-driven (not provider-driven) contract testing for the inventory-service integration, with checkout as the contract owner
- [ ] Designed the new-payment-flow performance testing around traffic shape and percentile thresholds specifically, plus a stress-test angle for the new external dependency's failure behavior
- [ ] Designed the live-coding round around a small, genuinely narratable kata and named specific, observable signals (test-case ordering, red-reason confirmation, explicit time-management communication) rather than just "watch if they write good tests"
