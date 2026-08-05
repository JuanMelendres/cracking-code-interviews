---
title: "Broken equals/hashCode Contract Letting Duplicates Through"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/java-core/equals-hashcode-and-comparable-contracts.md
source: handbook/java-core/equals-hashcode-and-comparable-contracts.md#production-scenarios
---

# Broken equals/hashCode Contract Letting Duplicates Through

## Context

A batch job deduplicates incoming customer records by inserting them into a `HashSet<CustomerRecord>` and checking size before and after. A refactor added a `lastModified` timestamp field and regenerated `equals()` — but not `hashCode()` — via an IDE action that only updated one method.

## Symptoms

The pipeline stops deduplicating after the refactor. Downstream reports start showing the same customer multiple times.

## Impact

Duplicate customer records propagate into billing and reporting systems, requiring a manual data-cleanup pass and eroding trust in the pipeline's dedup guarantee.

## Initial Hypotheses

- The incoming data itself contains more duplicates than expected — checked and ruled out; source data volume and duplicate rate are unchanged.
- A downstream system re-introduces duplicates — checked and ruled out; the `HashSet` size before the downstream write already shows no deduplication happening.
- `equals()` and `hashCode()` are no longer consistent after the refactor — correct.

## Evidence

Two `CustomerRecord` instances with identical business-key fields but different `lastModified` timestamps are confirmed `equals()`-equal — the refactored `equals()` correctly ignores `lastModified` — but their `hashCode()`, unmodified and still including the old field set from before the IDE regeneration ran, differs, because the IDE action only regenerated `equals()` and left the pre-existing `hashCode()` untouched.

## Investigation Timeline

1. **Dedup rate dropped to zero** immediately following the field-addition refactor, with duplicate customer reports appearing downstream.
2. **Input-volume and downstream-reintroduction hypotheses ruled out**, confirming the `HashSet` itself was failing to deduplicate before any downstream write occurred.
3. **`equals()` behavior verified directly**: two records differing only in `lastModified` are correctly `equals()`-equal per the refactored method.
4. **`hashCode()` compared against `equals()`**, finding it still includes the pre-refactor field set and therefore differs for records the refactored `equals()` treats as identical.

## Root Cause

The refactor broke the `equals()`/`hashCode()` contract: two records that are `equals()`-equal now have different hash codes, so the `HashSet` looks in the wrong bucket and never recognizes the duplicate — silently, with no exception.

## Immediate Mitigation

Run a one-off cleanup pass on already-propagated duplicate records in the affected downstream systems.

## Permanent Fix

Regenerate both `equals()` and `hashCode()` together from the same field list, and add a unit test that specifically asserts the contract — two objects that are `equals()`-equal must have equal `hashCode()` — for every value class used as a `HashSet`/`HashMap` key.

## Alternatives Considered

Switching to a `LinkedHashSet` or a manual duplicate-detection loop. Rejected as treating the symptom — the actual bug is the broken contract, and any hash-based structure will have the same problem until it's fixed.

## Trade-offs

None — fixing the contract has no downside; it's strictly a bug fix.

## Prevention

Treat `equals()` and `hashCode()` as one atomic unit that must always be regenerated together, and add an automated contract test to any value class used as a hash key, to catch a future accidental divergence before it reaches production.

## Monitoring and Alerts

- The `HashSet` size-before/size-after check already in use should be paired with a periodic sanity assertion — a small sample of known-duplicate synthetic records run through the pipeline — verifying the dedup mechanism actually deduplicates, not just that it ran without error; a silently broken contract produces no error to alert on otherwise.
- The `equals()`/`hashCode()` contract test (the Permanent Fix above) run in CI for every value class used as a hash key, converting this entire bug class into a build-time failure rather than a downstream data-quality discovery.

## Interview Story

This maps directly to a "silent data-quality bug from a broken hash contract" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a deduplication pipeline stopped deduplicating immediately after an apparently unrelated field-addition refactor, with no exception anywhere.
- **Task:** find the cause without any error message to start from.
- **Action:** rule out input-volume changes and downstream re-introduction; verify `equals()` behavior directly against known duplicate pairs; compare it against `hashCode()`, finding the IDE-regeneration only updated one of the paired methods.
- **Result:** regenerated both methods together and added an automated contract test, closing the specific bug and the entire class of future regeneration mistakes.

## Staff-Level Discussion

The `equals()`/`hashCode()` contract is a case where tooling (an IDE's "regenerate equals and hashCode" action, split into two separately invokable actions) makes it easy to break an invariant that was designed to be indivisible — nothing in the language enforces that the two methods stay consistent, and nothing in ordinary code review catches it either, since both methods individually look correct in isolation. The bug produces no exception and no obviously wrong output at the point of failure; it manifests only as a downstream data-quality symptom, which is exactly the profile that makes it expensive to trace back to its source. A Staff engineer's response to encountering this bug class once should be systemic: an automated contract test for every hash-keyed value class, not a one-off fix plus a reminder to "be careful next time" — the tooling will make the same mistake possible again for the next field added to the next class.

## Related Handbook Chapters

- [equals/hashCode and Comparable Contracts](../handbook/java-core/equals-hashcode-and-comparable-contracts.md) — canonical contract mechanics and `BrokenEqualsHashCodeDemo` used here.
- [HashMap Internals](../handbook/collections/hashmap-internals.md) — the bucket-lookup mechanism this broken contract silently defeats.
