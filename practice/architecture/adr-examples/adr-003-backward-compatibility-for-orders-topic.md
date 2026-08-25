---
title: "ADR-003: BACKWARD Compatibility as the Default for the Orders Topic Schema"
status: accepted
date: 2026-08-25
deciders: [platform-architecture-group]
---

# ADR-003: BACKWARD Compatibility as the Default for the Orders Topic Schema

> **Representative scenario.** A worked example, not a record of a real company
> decision. Every result cited is real, executed evidence from this repository's own
> [Schema Registry and Compatibility Evolution](../../../handbook/kafka/schema-registry-and-compatibility-evolution.md)
> chapter and its [practice code](../../java/kafka/schema-registry-and-compatibility-evolution/README.md).

## Status

Accepted

## Context

The `orders` Kafka topic's Avro schema needs a compatibility mode set before the
first consumer is onboarded. Multiple internal teams will consume this topic; all are
on the same internal deploy tooling and release cadence, and none currently has a
stated need to deploy ahead of the topic's own producer.

## Decision Drivers

- All known consumers deploy on the same internal pipeline as the producer, with
  consumers typically restarting to pick up new code *after* a producer change ships
  — the common deploy-order case.
- No external or third-party consumer is currently planned for this topic.
- Real, verified evidence exists in this repository for exactly how BACKWARD
  compatibility behaves for the two schema-change shapes the team expects to make
  most often: adding fields (for new order attributes) and removing fields (for
  deprecating old ones).

## Considered Options

### Option A: FORWARD compatibility

**Pros:**
- Would protect a currently-hypothetical external consumer that lags the producer's
  deploys.

**Cons:**
- Real, tested behavior in this repository's own evidence: under FORWARD, **removing
  a field without a default is rejected** (real HTTP 409) — this would block the
  team's most anticipated future change (deprecating old fields) unless every
  removable field is given a default from the moment it's introduced, a real,
  ongoing schema-design tax with no consumer currently requiring it.

### Option B: BACKWARD compatibility (the registry's default)

**Pros:**
- Real, tested behavior: **removing a field is accepted unconditionally** (real HTTP
  200) — matches the team's actual anticipated change pattern.
- Matches the actual, current deploy-order reality (consumers deploy after
  producers).

**Cons:**
- Real, tested behavior: **adding a field without a default is rejected** (real HTTP
  409) — every new field must be designed with a meaningful default from day one.
  This is a real constraint, not a free choice.

## Decision

We will adopt Option B — BACKWARD compatibility — because it was directly, empirically
verified to match the team's actual anticipated schema-change pattern (field removal
over time), and because no consumer currently justifies FORWARD's real cost (requiring
every future removed field to have carried a default from its introduction).

## Consequences

**Positive:**
- Field deprecation (the anticipated common case) is unconstrained.
- The team has real, tested, and now-documented evidence for what will and won't be
  accepted, rather than a rule recalled from memory at the moment a schema change is
  proposed.

**Negative:**
- Every new field addition must be designed with a real, meaningful default —
  enforced automatically by the registry (real HTTP 409 on violation), but still a
  real constraint on schema design the team must plan for, not discover mid-PR.

**Follow-up:**
- If an external or third-party consumer is ever onboarded to this topic, revisit
  this ADR — Option A's trade-off changes once that consumer's deploy-lag risk is
  real rather than hypothetical.
- Add the registry's compatibility-check endpoint to CI for this topic's schema
  directory, per the canonical chapter's own best-practice recommendation, before the
  first schema change ships.

## Related

- [Schema Registry and Compatibility Evolution](../../../handbook/kafka/schema-registry-and-compatibility-evolution.md) — the canonical chapter and all cited evidence.
- [Schema Registry practice code and real 2×2 compatibility matrix](../../java/kafka/schema-registry-and-compatibility-evolution/README.md)
