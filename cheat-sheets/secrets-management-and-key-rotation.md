---
title: "Cheat Sheet: Secrets Management and Key Rotation"
slug: secrets-management-and-key-rotation
document_type: cheat-sheet
domain: security
topic_id: T-1304
canonical: ../handbook/security/secrets-management-and-key-rotation.md
last_updated: 2026-08-05
---

# Secrets Management and Key Rotation

**Canonical chapter:** [`syllabus/12-security/secrets-management-and-key-rotation.md`](../syllabus/12-security/secrets-management-and-key-rotation.md)

## Core Mental Model

Encryption keys are like a specific lock-and-key set for a storage unit facility — rotating the key isn't like changing your house key; you can't just cut a new key and expect it to open units already locked with the old one. **Envelope encryption** makes rotation practical: generate descendant keys per rotation period, tag every encrypted record with *which* key version locked it, and keep old keys available in a "key ring" until every record encrypted under them has been re-encrypted and verified — only then is the old key safely destroyed.

## Essential Definitions

- **Secrets management** — storing, distributing, and controlling access to credentials so they're never hardcoded or logged in plaintext.
- **Key rotation** — periodically replacing an encryption/signing key while retaining the ability to decrypt/verify data produced under the old key until it's deliberately retired.
- **Envelope encryption** — data encrypted under a versioned "data key," each record tagged with the key version used, so old and new keys coexist during rotation without a synchronous re-encryption of everything.

## Decision Table

| Step | What happens | Downtime |
|---|---|---|
| Generate new key version | Added to key ring, tagged with a new version number | None |
| Switch new writes to new version | New records encrypted under the new key | None |
| Old records remain decryptable | Old key stays in ring, still resolves old-tagged records | None |
| Re-encryption sweep | Background job re-encrypts old records, verifies | Rate-limited background work |
| Retire old key | Delete only after sweep is complete and verified | None, if sequenced correctly |

**Trade-offs:** more frequent rotation reduces exposure window/blast radius but increases operational overhead managing multiple key versions; retaining old keys indefinitely avoids that cost but leaves any historical compromise exploitable forever.

## Key Numbers (real, executed — `KeyRotationDemo.java`, AES-256-GCM)

```
key v1 active: encrypt a record -> keyVersion=1

rotate to key v2 (scheduled rotation, no downtime): currentVersion now = 2

decrypt BOTH old (v1) and new (v2) records after rotation:
  v1 record decrypts to: ssn:123-45-6789
  v2 record decrypts to: ssn:987-65-4321
```

Simulated premature retirement (v1 removed before a re-encryption sweep):

```
v1 record now fails: no key for version 1
(this is why rotation runbooks re-encrypt-and-verify BEFORE retiring the old key)
```

## Common Pitfalls

- Treating "we encrypted the data" as a completed, static task rather than an ongoing lifecycle requiring rotation.
- Rotating a key by simply replacing it with no versioning scheme, breaking decryption for every previously-encrypted record.
- Deleting an old key immediately upon "rotating" without confirming a re-encryption sweep actually completed.
- Treating rotation as purely reactive to a suspected compromise, rather than also proactive (limiting exposure volume, meeting compliance schedules).

## Interview Answer Skeleton

**30-sec:** Secrets management is controlling access to credentials outside source code; key rotation periodically replaces keys while retaining old-key decryption ability until deliberate retirement. Envelope encryption — tagging each record with the key version that produced it — is what makes rotation practical without breaking existing data.

**2-min:** Add why rotation is proactive as well as reactive (limits exposure volume, meets compliance schedules, bounds an undetected slow leak's blast radius) + the real measured evidence (zero-downtime rotation with simultaneous old/new decryption; a premature-retirement demo permanently breaking a v1-tagged record) + the trade-off (frequent rotation costs re-encryption overhead; infrequent rotation leaves an unbounded historical blast radius).

**Whiteboard:** A key ring holding v1 and v2 simultaneously. Two encrypted records, tagged "v1" and "v2," each with an arrow to its corresponding key — both simultaneously decryptable. Then v1 removed from the ring, and the v1-tagged record's arrow now points at nothing, labeled "permanently undecryptable."

**Staff-level framing:** key rotation is a system design decision with real retrofitting cost asymmetry — building versioning in from day one versus a multi-month migration project later. Treat key retirement as requiring an explicit, verified re-encryption sweep as a hard precondition, never an assumption.

## Production Warning Signs

- A compliance audit requires proof of annual key rotation and the team discovers all historical data is encrypted under one unversioned key — retrofitting rotation now requires a real migration: backfilling a version tag, standing up envelope-encryption logic, and a multi-week re-encryption sweep across the entire dataset.
- A KMS outage makes the current signing key temporarily unavailable — a system with no key-version tracking has no fallback to a still-valid previous key, turning a partial outage into total signing/verification failure.
- **Prevention:** tag every encrypted record with its key version as a first-class, non-optional part of the encryption scheme from the very first implementation — the single decision that determines whether a future rotation is routine or a multi-month migration.

## Related

- `syllabus/12-security/applied-cryptography-hashing-signing-tls.md`
- `syllabus/12-security/owasp-top-10-for-backend-services.md`
- `syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md`
