---
title: "KMS Outage Exposing a Missing Key-Version Fallback"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/12-security/secrets-management-and-key-rotation.md
source: handbook/security/secrets-management-and-key-rotation.md#production-scenarios
---

# KMS Outage Exposing a Missing Key-Version Fallback

## Context

A service signs and verifies data using a key-management system (KMS). The implementation has no key-version tracking — it references only "the current signing key" with no versioning concept.

## Symptoms

A KMS outage makes the current signing key temporarily unavailable. The service has no way to fall back to a still-valid previous key.

## Impact

The entire signing and verification capability goes down as a single point of failure tied to one specific key's availability, rather than degrading gracefully.

## Initial Hypotheses

- The KMS outage itself is the entire problem, with no application-level remediation possible — the natural first read of the situation.
- The signing/verification implementation's lack of key versioning is the actual amplifying factor — correct, once the implementation is reviewed against how a properly versioned system would have behaved.

## Evidence

The implementation references only "the current signing key" with no version tag or cached-previous-key fallback path, meaning the moment that single key becomes unavailable, both signing and verification of anything depending on it stop entirely — there is no alternate key the code is capable of falling back to, regardless of whether a previous, still-valid key exists.

## Investigation Timeline

1. **Signing and verification capability went down entirely** during the KMS outage window.
2. **Outage itself confirmed as the trigger**, but its full-capability impact reviewed against what should have been a partial, gracefully-degraded failure.
3. **Implementation inspected**, finding no key-version tracking or fallback logic — only a single reference to "the current key."
4. **Gap identified**: a properly versioned implementation could have continued verifying against a still-cached previous key and failed over signing to a backup key, rather than losing the capability entirely.

## Root Cause

A naive implementation without key-version tracking has no way to fall back to a still-valid previous key during a KMS outage. A properly versioned system can continue verifying signatures produced under the previous key version while new signing temporarily fails over to a backup key or degrades gracefully, rather than the entire signing and verification capability going down as a single point of failure tied to one specific key's availability.

## Immediate Mitigation

Manually intervene to restore access to the specific key or expedite KMS recovery, since the application has no built-in fallback path to use in the interim.

## Permanent Fix

Introduce key-version tracking: a version tag alongside every signed artifact, a cached set of recently valid key versions for verification, and an explicit backup-key failover path for signing, so a single key's temporary unavailability degrades gracefully instead of taking down the entire capability.

## Alternatives Considered

Treating this as purely a KMS reliability problem and pushing for a higher-availability KMS tier alone. Rejected as insufficient in isolation — even a highly available KMS will have some non-zero outage probability, and the application-level single point of failure would still exist for that residual risk.

## Trade-offs

Key-version tracking adds real implementation complexity: version tags, a cache of recent keys, explicit failover logic. Accepted, since the alternative is a signing/verification outage every time the KMS has any availability issue, however rare.

## Prevention

Treat key-version tracking as a day-one architectural decision for any system relying on a signing or encryption key, not something added reactively after a KMS-availability incident demonstrates its absence.

## Monitoring and Alerts

- KMS availability tracked as a dependency health signal distinct from the application's own health, so a KMS-driven outage is immediately attributable rather than initially presenting as an unexplained application failure.
- Once key versioning is implemented, an alert on any verification falling back to a non-current key version, surfacing exactly when and how often the fallback path is actually exercised in production.

## Interview Story

This maps to a "your KMS went down, what happened to your service" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a KMS outage took down the service's entire signing and verification capability, rather than causing a partial, graceful degradation.
- **Task:** explain why a temporary key-management outage became a full capability outage.
- **Action:** review the implementation's key-handling logic directly; find that it references only a single current key with no version tracking or fallback path; contrast this against how a properly versioned system would have degraded gracefully instead.
- **Result:** introduced key-version tracking with a cached-previous-key verification fallback and a backup-key signing failover, converting a full outage risk into a graceful-degradation path for future KMS incidents.

## Staff-Level Discussion

This incident demonstrates a general principle about single points of failure that extends well beyond key management: any system with exactly one reference to a critical dependency — "the current key," "the primary database," "the active leader" — inherits that dependency's full unavailability as its own failure mode, with no architectural buffer, unless a fallback or degradation path is explicitly designed in. Key versioning is the specific mechanism here, but the underlying discipline is asking, for any critical single dependency reference in a system, "what happens to us during this dependency's outage, and is that an acceptable blast radius?" A Staff engineer reviewing security-critical infrastructure should treat "no fallback path for key unavailability" as a structural risk finding worth raising proactively, not something that should require an actual outage to surface.

## Related Handbook Chapters

- [Secrets Management and Key Rotation](../syllabus/12-security/secrets-management-and-key-rotation.md) — canonical key-versioning and rotation mechanics used here.
- [Applied Cryptography: Hashing, Signing, TLS](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md) — the signing mechanics this key-version fallback protects.
