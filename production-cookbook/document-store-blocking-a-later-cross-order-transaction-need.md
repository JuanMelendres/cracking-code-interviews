---
title: "Document Store Blocking a Later Cross-Order Transaction Need"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/system-design/storage-selection-tradeoffs.md
source: handbook/system-design/storage-selection-tradeoffs.md#production-scenarios
---

# Document Store Blocking a Later Cross-Order Transaction Need

## Context

A catalog service was built on a document store, chosen for its flexible per-category product schema.

## Symptoms

A year later, a finance reporting feature needs to atomically reconcile inventory counts across a batch of orders touching multiple products, and the document store's weaker cross-document transaction support makes this reliably impossible without significant application-level workaround code.

## Impact

A feature that would be a straightforward multi-row transaction in a relational store instead requires bespoke, error-prone application-level coordination, and has already produced at least one reconciliation discrepancy in production.

## Initial Hypotheses

- A bug in the application-level reconciliation logic — checked and ruled out; the logic is correct given the constraints, but the constraints themselves are the problem.
- The document store's transaction feature being misconfigured — checked and ruled out; it's configured correctly, but its guarantees are narrower than what a relational multi-row transaction provides.
- A fundamental mismatch between the storage technology's transactional model and the new feature's requirement — correct.

## Evidence

The document store's transaction API supports atomicity within a single document or a narrowly scoped set, not the broader, ad-hoc multi-product-and-order transaction the reporting feature needs.

## Investigation Timeline

1. **Reconciliation discrepancy discovered in production**, traced to the new finance reporting feature's atomicity requirements.
2. **Application logic reviewed and confirmed correct**, given the constraints it was written against.
3. **Document store's transaction configuration reviewed**, confirming it was configured correctly but structurally narrower in scope than the feature needs.
4. **Root cause identified**: a fundamental mismatch between the storage technology's transactional model and the new access pattern, not a configuration or logic defect.

## Root Cause

The original storage choice was correct for the catalog's per-category schema flexibility need at the time, but the team didn't anticipate a future access pattern — cross-order, cross-product atomic reconciliation — that the chosen technology structurally can't support well. The access-pattern method should have been re-applied when the new feature was scoped, not assumed to still hold.

## Immediate Mitigation

Build a manual reconciliation batch job with idempotent retries to paper over the missing atomicity guarantee for the reporting feature's specific need.

## Permanent Fix

Introduce a relational store specifically for the reporting/reconciliation domain — a polyglot-persistence decision — fed by change-data-capture from the document store, rather than trying to force the document store to provide a transactional guarantee it isn't designed for.

## Alternatives Considered

Migrating the entire catalog off the document store to relational. Rejected — the catalog's original access pattern (flexible per-category schema, no cross-document transactions) is still well served by the document store; only the new reporting feature has a different requirement.

## Trade-offs

Adding a second storage technology — the relational reconciliation store — is exactly the polyglot-persistence cost this program names generally: additional backup, monitoring, and on-call surface area. Accepted here because the access-pattern mismatch for the new feature is real and significant, not a minor inconvenience.

## Prevention

Re-run the access-pattern method whenever a new feature's requirements are scoped against an existing storage choice, rather than assuming the original decision still holds for every future need.

## Monitoring and Alerts

- No runtime monitoring signal directly prevents this class of gap — it's a design-time review question. The structural fix is a required access-pattern review step in feature-scoping for any new capability touching an existing storage layer, explicitly asking whether the new access pattern fits the chosen technology's transactional and query model.
- Once the CDC-fed relational store is in place, replication lag between the document store and the reconciliation store tracked as a standing metric, since the reporting feature's correctness now depends on it.

## Interview Story

This maps to "polyglot persistence — when is it actually justified" arriving as a real, measurable incident. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a document store, correctly chosen for a catalog's schema-flexibility needs, couldn't support a new cross-order atomic reconciliation feature a year later.
- **Task:** determine whether this reflects a bug, a misconfiguration, or a genuine technology mismatch.
- **Action:** rule out application logic and transaction misconfiguration directly; identify the document store's narrower cross-document transaction scope as a structural, not fixable, limitation for this specific access pattern.
- **Result:** introduced a purpose-built relational store for the reconciliation domain, fed by CDC, rather than forcing the document store to provide a guarantee it was never designed for or reversing the still-correct original catalog decision.

## Staff-Level Discussion

This incident is nearly a mirror of the earlier DynamoDB-migration case, and that repetition is itself the lesson worth naming: storage-selection mismatches for a later, unanticipated access pattern are not a rare, one-off failure mode — they're a predictable, recurring consequence of the access-pattern method being applied once, at initial design time, and never revisited as new features are scoped. The original decision was correct and remains correct for its original scope; the mistake was treating a storage decision as permanently settled rather than as a claim that needs re-evaluating against every materially new access pattern a future feature introduces. A Staff engineer should treat "does our current storage layer support this new feature's access pattern" as a standard question at every feature-scoping stage that touches existing storage, not something discovered only once a reconciliation discrepancy has already reached production.

## Related Handbook Chapters

- [Storage Selection Trade-offs](../handbook/system-design/storage-selection-tradeoffs.md) — canonical access-pattern method and polyglot-persistence trade-off used here.
