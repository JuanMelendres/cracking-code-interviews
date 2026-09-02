---
title: "Cheat Sheet: Schema Registry and Compatibility Evolution"
slug: schema-registry-and-compatibility-evolution
document_type: cheat-sheet
domain: kafka
topic_id: T-708
canonical: ../handbook/kafka/schema-registry-and-compatibility-evolution.md
last_updated: 2026-09-02
---

# Schema Registry and Compatibility Evolution

**Canonical chapter:** [`handbook/kafka/schema-registry-and-compatibility-evolution.md`](../handbook/kafka/schema-registry-and-compatibility-evolution.md)

## Core Mental Model

A compatibility mode is really just a promise about who deploys first. BACKWARD promises "new readers can handle old data" — the promise needed when consumers deploy after producers, the overwhelmingly common case. FORWARD promises "old readers can handle new data" — needed when producers deploy first and some consumers lag. Once framed as "which side deploys first, and does the other side's existing code survive," the field-add/field-remove rules fall out directly from asking whether the reader schema has everything it needs to resolve the data.

## Essential Definitions

- **Schema Registry** — a centralized service storing and versioning schemas, enforcing a compatibility mode per subject; moves an incompatible-schema failure from a runtime deserialization crash to a rejected registration.
- **BACKWARD** (Confluent's default) — new schema can read data written with the immediately previous schema; adding a field requires a default, removing a field is always allowed.
- **FORWARD** — the previous schema can read data written with the new schema; adding a field is always allowed, removing a field requires the *old* schema to have had a default.
- **FULL** — both BACKWARD and FORWARD simultaneously; every add/remove needs a default.
- **NONE** — no compatibility checking; only appropriate for a tightly-coupled, always-co-deployed producer/consumer pair.
- **Schema resolution** — at decode time, an Avro deserializer walks the writer's fields against the reader's, filling any reader field absent from the writer's data with its default, or failing if none exists.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Consumers typically deploy after producers (the common case) | BACKWARD (Confluent's default) |
| Producers must be free to deploy ahead of some lagging consumers (e.g., an external/third-party consumer) | FORWARD |
| Neither side's deploy order can be guaranteed | FULL — accept every add/remove needing a default |
| A single producer/consumer, always co-deployed | NONE is tolerable, never as a general default |

**Compatibility rules:**

| | BACKWARD | FORWARD | FULL |
|---|---|---|---|
| Protects | New consumers reading old data | Old consumers reading new data | Both directions |
| Add a field | Only with a default | Always allowed | Only with a default |
| Remove a field | Always allowed | Only if the old schema had a default | Only if a default exists |

## Key Numbers (real, executed against a live Confluent Schema Registry + Kafka broker)

- Adding a required `shippingAddress` field with no default under BACKWARD: real HTTP 409 — `READER_FIELD_MISSING_DEFAULT_VALUE`, naming the exact field.
- The same field added with a default (`currency` defaulting to `"USD"`): real HTTP 200, accepted.
- Removing the default-less `amount` field: real HTTP 200 under BACKWARD (accepted); real HTTP 409 under FORWARD (rejected, same field, opposite direction of error).
- Decoding real v1-schema bytes with a reader schema requiring `shippingAddress`: real `AvroTypeException: ... missing required field shippingAddress`.

## Common Pitfalls

- Guessing that removing a field is the riskier operation under BACKWARD compatibility — real evidence shows the opposite: removal is always safe, add-without-default is rejected.
- Treating "the registry approved my schema" as proof every currently-deployed consumer can handle it — the registry doesn't know which schema version each running instance was actually compiled against.
- Picking FORWARD or FULL by habit rather than reasoning from the actual deploy-order question.
- Skipping a default on a new field "to backfill later," instead of recognizing the default *is* the backfill mechanism for already-written records.

## Interview Answer Skeleton

**30-sec:** Schema Registry enforces a compatibility mode so an incompatible change is rejected at registration time, before it reaches a topic and breaks a consumer. BACKWARD (the common default) means new code can read old data — removing fields is safe, but adding one requires a default since old data won't have it.

**2-min:** Add the real 2×2 evidence: under BACKWARD, remove-`amount` returned HTTP 200 while add-`shippingAddress`-without-default returned HTTP 409; under FORWARD, the same remove returned HTTP 409. Cite the real `AvroTypeException` that would have hit every consumer had the add-without-default change shipped anyway.

**Whiteboard:** Draw two parallel timelines — "Producer deploys" then "Consumer deploys," gap labeled "old data / new consumer, BACKWARD protects this." Second timeline reversed, gap labeled "new data / old consumer, FORWARD protects this."

**Staff-level framing:** Reason from the deploy-order mental model to predict the outcome of an unfamiliar schema change rather than recalling a memorized rule. Name CI-time enforcement as necessary (call the registry's compatibility-check endpoint before merge, not just at publish time), and explain precisely what a passing check does and does not guarantee about every currently-deployed consumer.

## Production Warning Signs

- A schema change adds a required field with no default under BACKWARD compatibility — caught as a real HTTP 409 at registration, or (if bypassed) crash-loops every consumer on `AvroTypeException` the moment it reads a pre-existing record.
- Compatibility mode set to `NONE` "to avoid friction" — removes the exact safety net that caught the add-without-default defect in this chapter's own evidence.
- Schema changes reviewed only as a code diff, never run against the registry's real compatibility-check endpoint in CI.

## Related

- `handbook/kafka/producer-semantics-and-partition-keys.md`
- `handbook/kafka/delivery-semantics-and-exactly-once.md`
- `handbook/architecture/cqrs-read-write-separation.md`
- `handbook/architecture/event-sourcing-and-its-real-costs.md`
