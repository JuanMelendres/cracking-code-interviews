---
title: "Same Field-Removal Diff Passing Under BACKWARD and Failing Under FORWARD Compatibility"
document_type: production-cookbook-entry
domain: kafka
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md
  - ../syllabus/17-architecture/cqrs-read-write-separation.md
source: handbook/kafka/schema-registry-and-compatibility-evolution.md#production-scenarios
---

# Same Field-Removal Diff Passing Under BACKWARD and Failing Under FORWARD Compatibility

## Context

The same demo run that surfaced the missing-default incident also tested removing a default-less `amount` field from the `Order` schema, checked against both BACKWARD and FORWARD compatibility modes on isolated subjects.

## Symptoms

The identical schema diff — removing `amount` — produces opposite registry outcomes depending on which compatibility mode the subject is configured under, with no other variable changed.

## Impact

A team relying on intuition about what "removing a field" means for compatibility, without checking the topic's actual configured mode, risks either an unnecessary rejection (assuming FORWARD's rule under a BACKWARD topic) or an unsafe assumption of safety (assuming BACKWARD's rule under a FORWARD topic).

## Initial Hypotheses

None needed — this was a deliberate test of the same diff under both modes, not a diagnosis of an unexpected failure.

## Evidence

Under BACKWARD: real **HTTP 200** — accepted. Under FORWARD: real **HTTP 409** — rejected, with the registry's error naming the same field from the *opposite* direction (`'amount' ... in the old schema has no default value and is missing in the new schema`).

## Investigation Timeline

1. `amount` field (no default value) removed from the `Order` schema, registered against a subject configured under BACKWARD compatibility — result: HTTP 200, accepted.
2. The identical removal diff registered against an isolated subject configured under FORWARD compatibility — result: HTTP 409, rejected.
3. The FORWARD-mode error message inspected, naming `amount` from the opposite direction of the earlier missing-default error — the old schema now cannot be satisfied by new data.
4. Diagnosis reached by comparing which schema plays the "reader" role under each mode: under BACKWARD the new (reader) schema simply stops asking for `amount`; under FORWARD the old schema is still the reader and still expects a value FORWARD-mode's new data no longer provides.

## Root Cause

Under BACKWARD, the new (reader) schema simply stops asking for `amount` — old data having it is irrelevant. Under FORWARD, the *old* schema is the reader, and it still expects `amount` on every record it reads — new data no longer providing it, with no default to fall back on, breaks that old reader.

## Immediate Mitigation

None needed — this is a controlled comparison test, not a live incident; the FORWARD-mode rejection is the registry behaving correctly.

## Permanent Fix

None separate from the general schema-governance rule established in the companion missing-default incident: verify compatibility against the registry directly, per subject, rather than assuming a diff's safety generalizes across compatibility modes.

## Alternatives Considered

None recorded — this scenario is a diagnostic comparison, not a remediation.

## Trade-offs

None distinct from the general trade-off of choosing a compatibility mode: BACKWARD favors safely evolving consumers independently of producers (new readers, old data); FORWARD favors safely evolving producers independently of consumers (new data, old readers) — the same field-removal diff is safe under exactly one of those guarantees, not both.

## Prevention

Treat compatibility-mode semantics as mode-specific, not diff-specific — the same textual change to a schema can be simultaneously safe and unsafe depending on which compatibility promise the topic has made, so any schema-change review must confirm the target subject's actual configured mode before evaluating whether a given diff is acceptable.

## Monitoring and Alerts

- Have CI's compatibility-check gate (shared with the companion missing-default-field entry) surface which compatibility mode the target subject is configured under directly in its output, so a reviewer sees the mode-specific reasoning (not just pass/fail) for every schema-change pull request.
- Alert on any topic's compatibility mode being changed after creation — a mode change silently redefines which historical diffs would have been considered safe, and should be a reviewed, deliberate action rather than a configuration change that passes unnoticed.
- Track, per topic, whether producers or consumers are expected to deploy first as a documented fact alongside the compatibility mode — this incident's diagnosis ("which side deploys first") is the practical question the mode encodes, and making it explicit prevents the same field-removal ambiguity from being re-litigated per change.

## Interview Story

This maps directly to "what's the difference between BACKWARD and FORWARD compatibility" arriving as a real, evidence-backed side-by-side comparison. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** the same field-removal schema diff needed to be evaluated for safety on a topic, but the team wasn't certain whether "removing a field" was safe in general.
- **Task:** determine whether the diff's safety depends on something other than the diff itself.
- **Action:** registered the identical removal against isolated BACKWARD and FORWARD subjects and observed opposite outcomes, then traced the difference to which schema (old or new) plays the reader role under each mode.
- **Result:** confirmed that safety is a property of the compatibility mode and deployment order, not the diff in isolation, and used the registry's own real errors — naming the same field from opposite directions — as the concrete evidence for that distinction.

## Staff-Level Discussion

The core Staff-level insight this comparison makes concrete is that compatibility mode is really a statement about deployment order — BACKWARD says "consumers may deploy the new schema before producers finish writing old data," FORWARD says the reverse — and a schema diff's safety is inseparable from that statement. Teams that reason about schema changes purely at the diff level ("we're just removing an unused field, that should always be fine") will eventually hit exactly this asymmetry, and the cost of getting it wrong is identical to the missing-default incident: a fleet-wide deserialization failure, just triggered from the opposite direction. The organizational answer is to make the compatibility mode and the expected deploy order a visible, documented property of the topic itself — not tribal knowledge held by whoever originally configured it — so that a schema-change reviewer evaluates every diff against the actual rule in force, rather than a generic mental model of "field removal is usually safe."

## Related Handbook Chapters

- [Schema Registry and Compatibility Evolution](../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md) — canonical mechanics of BACKWARD/FORWARD compatibility and the reader/writer schema distinction this comparison demonstrates.
- [CQRS: Read/Write Separation](../syllabus/17-architecture/cqrs-read-write-separation.md) — a related instance of producer/consumer (write/read) evolution needing explicit compatibility reasoning.
