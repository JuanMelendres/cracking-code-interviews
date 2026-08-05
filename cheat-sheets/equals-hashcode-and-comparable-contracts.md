---
title: "Cheat Sheet: equals(), hashCode(), and Comparable Contracts"
slug: equals-hashcode-and-comparable-contracts
document_type: cheat-sheet
domain: java-core
topic_id: T-101
canonical: ../handbook/java-core/equals-hashcode-and-comparable-contracts.md
last_updated: 2026-08-05
---

# equals(), hashCode(), and Comparable Contracts

**Canonical chapter:** [`handbook/java-core/equals-hashcode-and-comparable-contracts.md`](../handbook/java-core/equals-hashcode-and-comparable-contracts.md)

## Core Mental Model

`equals()` answers "are these the same value," `hashCode()` answers "which bucket should this value live in," and every hash-based collection assumes those two answers never disagree. Break that assumption — override one without the other — and the collection doesn't error, it just quietly looks in the wrong bucket forever. `TreeSet`/`TreeMap` have a separate, stricter assumption: they use `compareTo()` *exclusively* for both ordering and duplicate detection, ignoring `equals()` entirely.

## Essential Definitions

- **The contract** — if two objects are `equals()`, they must have the same `hashCode()`. The reverse isn't required (unequal objects may share a hash code, resolved via `equals()` within the bucket).
- **`Comparable<T>`** — defines `compareTo()` for natural ordering; the Javadoc *recommends* but does not *require* consistency with `equals()`.
- **`TreeSet`/`TreeMap` duplicate detection** — uses `compareTo() == 0` exclusively as "the same element," with no reference to `equals()` at all.

## Decision Table

| Situation | Rule |
|---|---|
| Overriding `equals()` | Override `hashCode()` in the same change, from the same fields — never one without the other |
| Implementing `Comparable` for a class stored in `TreeSet`/`TreeMap` | Derive `compareTo()` from the same fields as `equals()`, or use it won't detect true duplicates correctly |
| Need a sort order narrower than equality (e.g., sort by price only) | Use an external `Comparator`, never an inconsistent `Comparable`, if instances are stored in a `TreeSet`/`TreeMap` |
| Value class used as a `HashSet`/`HashMap` key | Add an explicit automated contract test: equal objects → equal hash codes |

**Trade-offs:** overriding both together costs discipline on every field change but keeps hash-based collections correct; a `Comparable` inconsistent with `equals()` gives simpler ordering logic for a specific need but must never back a `TreeSet`/`TreeMap` where distinctness matters.

## Key Numbers (real, executed — `BrokenEqualsHashCodeDemo.java`, `ComparableInconsistentWithEqualsDemo.java`)

```
== equals() overridden, hashCode() NOT overridden ==
b1.equals(b2) = true                    <- equal by value
b1.hashCode() = 1554874502
b2.hashCode() = 1846274136               <- different, identity hash, contract broken
HashSet.contains(b2) = false             <- looked in the wrong bucket
HashSet size after adding both = 2       <- expected 1

== Fixed: both overridden consistently ==
HashSet size after adding both = 1       <- correctly deduplicated
```

```
== Two genuinely different products, same price ==
widget.equals(gadget) = false            <- different names, NOT equal
widget.compareTo(gadget) = 0             <- same price, compareTo says EQUAL

TreeSet.add(widget); .add(gadget) returned: false
catalog.size() = 1                       <- gadget SILENTLY DROPPED
```

## Common Pitfalls

- Overriding `equals()` without `hashCode()`, including via a partial IDE regeneration that only touches one method.
- Implementing `Comparable` inconsistently with `equals()` and then storing instances in a `TreeSet`/`TreeMap` where distinctness matters.
- Assuming a broken contract produces a visible error rather than a silent collection malfunction.
- Mutating a field used in `hashCode()` after the object is already in a hash-based collection.

## Interview Answer Skeleton

**30-sec:** `equals()` and `hashCode()` must agree — equal objects must share a hash code — or hash-based collections silently fail to detect duplicates, measured directly. `TreeSet`/`TreeMap` use `compareTo()` exclusively, ignoring `equals()` entirely, so an inconsistent `Comparable` can silently drop a genuinely distinct element.

**2-min:** Add why the contract exists (hash-based collections need a reliable way to locate a value already seen) + the real measured evidence (a `HashSet` failing to detect a duplicate after `hashCode()` wasn't updated to match a refactored `equals()`; a `TreeSet` silently dropping a distinct product because `compareTo()` returned 0) + the fix (regenerate both together, add an automated contract test for any hash-key class).

**Whiteboard:** `HashSet.add(x)` → `hashCode()` picks a bucket → diamond "any element in that bucket `equals` x?" → branch to "wrong bucket, treated as new" (broken contract) vs. "correctly recognized." Annotate the wrong-bucket branch: no exception, just a wrong answer.

**Staff-level framing:** this is one of the clearest examples in the language of a contract violation that fails silently — no exception, no crash, just quietly wrong behavior surfacing only through a downstream symptom. Treat every value class used as a collection key as needing an automated contract test, since this failure mode specifically survives code review (the code compiles and "looks fine" until the exact input triggers a collision or mismatch).

## Production Warning Signs

- A `HashSet`-based deduplication pipeline stops catching duplicates right after a refactor added a field and regenerated `equals()` via an IDE action — check whether `hashCode()` was regenerated in the same action; a partial regeneration is the single most common real-world cause.
- Records vanish from or duplicate inside a `TreeSet`/`TreeMap` even though `equals()` says they're distinct — check whether `compareTo()` is derived from fewer fields than `equals()` (e.g., sorting by price alone).
- **Prevention:** treat `equals()`/`hashCode()` as one atomic unit regenerated together on every field change, and add an automated equals/hashCode contract test to every class used as a hash key.

## Related

- `handbook/collections/hashmap-internals.md`
- `handbook/java-core/immutability-and-defensive-copying.md`
