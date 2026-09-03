---
title: "Registry-Blocked Schema Change That Would Have Broken Every Consumer on Deploy"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md
  - ../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md
source: handbook/kafka/schema-registry-and-compatibility-evolution.md#production-scenarios
---

# Registry-Blocked Schema Change That Would Have Broken Every Consumer on Deploy

## Context

A team wants to add a required `shippingAddress` field to an `Order` event schema, on a topic running under the default BACKWARD compatibility mode.

## Symptoms

The schema registration attempt is rejected before it ever reaches production, rather than shipping and failing later.

## Impact

Had the change shipped anyway, every consumer instance would have failed the moment it deployed and encountered a single pre-existing record on the topic — a fleet-wide deserialization failure, not an isolated one.

## Initial Hypotheses

None needed — the team's intent was simply to add the field; the registry's rejection is what surfaced the problem rather than a hypothesis being tested.

## Evidence

[`registry-demo.sh`](../../practice/java/kafka/schema-registry-and-compatibility-evolution/README.md) attempted exactly this registration against a real, running Schema Registry. Result: **HTTP 409**, with a structured error naming the exact problem — `READER_FIELD_MISSING_DEFAULT_VALUE`, `'shippingAddress' ... has no default value and is missing in the old schema`. [`AvroSchemaResolutionDemo.java`](../../practice/java/kafka/schema-registry-and-compatibility-evolution/README.md) demonstrates directly what would have happened had this shipped anyway: decoding real, existing v1-schema bytes with a reader schema requiring `shippingAddress` genuinely throws `AvroTypeException: ... missing required field shippingAddress` — a real deserialization failure that would have hit every consumer instance the moment it deployed and encountered a single pre-existing record.

## Investigation Timeline

1. Team attempts to register a new `Order` schema version adding a required `shippingAddress` field with no default value.
2. Schema Registry rejects the registration with HTTP 409 and a structured `READER_FIELD_MISSING_DEFAULT_VALUE` error, before any deployment occurs.
3. To confirm the rejection was correctly protective rather than overly conservative, the would-have-shipped scenario is reproduced directly: decoding existing v1-schema records with the new reader schema.
4. That reproduction throws `AvroTypeException: ... missing required field shippingAddress`, confirming the registry's rejection prevented a real, fleet-wide deserialization failure.
5. The same change pattern re-attempted with a real default value (`currency` defaulting to `"USD"`) and confirmed to succeed (HTTP 200) in the same test run, establishing the correct fix.

## Root Cause

The new reader schema demanded data that the already-written records on the topic could never supply, with no default to substitute — a required field with no default breaks BACKWARD compatibility by construction.

## Immediate Mitigation

Add the same field with a real default value (the `currency` field, defaulting to `"USD"`, is the same shape of change, and the registry accepted it in the same test run).

## Permanent Fix

Establish a rule — often enforced directly by CI calling the registry's compatibility-check endpoint before merge — that no schema change ships without first passing a real compatibility check against the target subject, not a human review of the diff alone.

## Alternatives Considered

None recorded as rejected — adding a default value is presented as the direct, sufficient fix for this specific field-addition case.

## Trade-offs

Requiring a default for every added field is a real constraint on schema design — it forces every new field to have a sensible fallback meaning, which is not always natural (a `shippingAddress` defaulting to an empty string is not really a meaningful "no address," just a value that satisfies the type system).

## Prevention

Treat "does this new field have a default" as a mandatory question in every schema-change code review, before it ever reaches the registry.

## Monitoring and Alerts

- Wire the Schema Registry's compatibility-check endpoint into CI as a hard merge gate, so an incompatible schema change fails the build with the same `READER_FIELD_MISSING_DEFAULT_VALUE`-style error this incident surfaced, rather than depending on someone running the check manually before registering.
- Alert on any HTTP 409 from the registry occurring outside of CI (i.e., a manual or ad hoc registration attempt against a production subject), since that indicates someone bypassed the intended compatibility-check-before-merge workflow.
- Track consumer-side deserialization error rates (`AvroTypeException` and equivalents) as a standing metric per topic — this is the failure mode the registry exists to prevent, and a nonzero rate is a signal the compatibility gate was bypassed or misconfigured somewhere in the pipeline.

## Interview Story

This maps directly to "walk me through a schema-evolution compatibility check" arriving as a near-miss caught before production. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a team attempted to add a required field with no default to an event schema on a topic under BACKWARD compatibility.
- **Task:** determine whether the change was safe before it shipped.
- **Action:** the Schema Registry itself rejected the registration with a structured error; confirmed independently, by decoding real existing records with the new schema, that shipping the change would have caused a fleet-wide consumer failure.
- **Result:** re-added the field with a real default value, which the registry accepted, and established a CI-enforced rule that no schema change merges without passing a real compatibility check first.

## Staff-Level Discussion

The organizational value of this near-miss is that the registry converted what would have been a fleet-wide production incident — every consumer instance failing on deserialization the moment it encountered one pre-existing record — into a rejected API call with a structured, actionable error message, entirely before deployment. That only works, though, if the compatibility check runs somewhere it cannot be skipped; a registry that exists but is checked manually, at an engineer's discretion, before merging is one missed step away from the same incident happening anyway. The trade-off worth surfacing at the Staff level is that "every added field needs a default" is a real, sometimes awkward constraint on schema design — a `shippingAddress` field defaulting to an empty string is not a meaningful business default, it's a type-system placeholder — so a mature schema-governance policy should distinguish between fields where a default is genuinely meaningful and cases where the safer answer is a new, additive event type rather than forcing an artificial default onto an existing one.

## Related Handbook Chapters

- [Schema Registry and Compatibility Evolution](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md) — canonical mechanics of BACKWARD/FORWARD compatibility checking this incident reproduces.
- [Delivery Semantics and Exactly-Once](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md) — the broader consumer-reliability context schema compatibility failures threaten.
