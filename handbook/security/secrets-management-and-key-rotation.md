---
title: "Secrets Management and Key Rotation"
slug: secrets-management-and-key-rotation
document_type: handbook-chapter
domain: security
status: draft
version: 1.0
last_reviewed: 2026-08-02
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - applied-cryptography-hashing-signing-tls.md
related:
  - applied-cryptography-hashing-signing-tls.md
  - owasp-top-10-for-backend-services.md
  - supply-chain-security-sbom-and-dependency-risk.md
  - ../../study-packs/week-17/06-secrets-management-and-key-rotation.md
official_references:
  - https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final
---

# Secrets Management and Key Rotation

> **Topic register:** T-1304 (Secrets management & key rotation, IWI 5.5) · Advanced tier · Moderate interview frequency [M]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Failure Modes and Debugging](#failure-modes-and-debugging)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Anti-Patterns](#anti-patterns)
13. [Best Practices](#best-practices)
14. [Interview Answer Framework](#interview-answer-framework)
15. [Interview Questions](#interview-questions)
16. [Summary](#summary)
17. [Key Takeaways](#key-takeaways)
18. [Cheat Sheet](#cheat-sheet)
19. [Flashcards](#flashcards)
20. [Practice Exercises](#practice-exercises)
21. [Solutions](#solutions)
22. [Additional Reading](#additional-reading)
23. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can explain why encryption keys need to be rotated even when never compromised, describe the envelope-encryption pattern that makes rotation practical at scale, and cite a real Java demonstration of encrypting under one key version, rotating to a new version with zero downtime, and decrypting both old and new records correctly until the old key is deliberately retired.

## Why This Matters in Interviews

Key rotation questions separate candidates who understand encryption as a static "turn it on" checkbox from those who understand it as an operational lifecycle. The naive assumption — "we encrypted the data, we're done" — misses that keys need periodic rotation even absent any known compromise (limiting the blast radius of a future, not-yet-discovered compromise; meeting compliance rotation schedules; reducing the volume of data ever encrypted under a single key), and that rotating a key in a system with millions of already-encrypted records is a genuinely nontrivial operational problem if the system wasn't designed for it from the start. A candidate who can describe *why* naive rotation (just swap the key and hope) breaks existing data, and what pattern avoids that, demonstrates real operational maturity.

## Mental Model

Think of encryption keys like a specific lock-and-key set for a storage unit facility. Rotating the key isn't like changing your house key — you can't just cut a new key and expect it to open units already locked with the old one. **Envelope encryption** is the pattern that makes this practical: instead of using one master key directly on every record, generate the master key's *descendant* keys per rotation period, tag every encrypted record with *which* key version locked it, and keep the old keys available (in a "key ring") until every record encrypted under them has been deliberately re-encrypted and verified — only then is the old key safely destroyed. The tag is the crucial piece: without it, you'd have no way to know which key opens which unit once you've cut several generations of keys.

## Definition and Purpose

**Secrets management** is the practice of storing, distributing, and controlling access to sensitive credentials (API keys, database passwords, encryption keys, certificates) such that they're never hardcoded in source code, never logged in plaintext, and are accessible only to the specific services and operators that legitimately need them. **Key rotation** is the practice of periodically replacing an encryption or signing key with a new one, while maintaining the ability to decrypt or verify data produced under the old key until it's deliberately retired. **Envelope encryption** is the specific pattern that makes rotation operationally practical: data is encrypted under a versioned "data key," and each encrypted record is tagged with the key version used, so old and new keys can coexist during a rotation without requiring a synchronous, all-at-once re-encryption of every existing record.

## Core Concepts

### Keys need rotation even without a known compromise

Rotation isn't purely reactive to a suspected breach — it's a proactive risk-reduction practice for several independent reasons: it limits the *volume* of data ever protected by a single key (reducing the value of that key as an attack target, and the blast radius if it's ever compromised in the future without immediate detection); it satisfies compliance frameworks that mandate periodic rotation regardless of incident history (PCI DSS, for instance, has explicit key-rotation requirements); and it reduces the exposure window of a key that may be compromised silently, without ever producing detectable evidence — a slow key leak with no rotation policy has an unbounded blast radius purely as a function of time.

### Rotation without a versioning scheme breaks existing data

The naive approach — generate a new key, start using it, and forget the old one — breaks decryption for every record already encrypted under the previous key, since there's no way to know which key protected which record without an explicit tag. Any real rotation scheme requires tracking which key version encrypted which data, which is exactly what envelope encryption's per-record key-version tag provides.

### Retiring an old key requires re-encryption, not just deletion

A key can only be safely deleted once every record it ever protected has been re-encrypted under a newer key and that re-encryption verified — deleting a key while any record still depends on it for decryption is a permanent, unrecoverable data-loss event, functionally indistinguishable from having deleted the data itself.

## Internal Implementation

**Real AES-256-GCM envelope-encryption key-rotation demonstration** (`practice/java/week-17/secrets-rotation/src/KeyRotationDemo.java`) — each ciphertext is tagged with the key version that produced it:

```
=== key v1 active: encrypt a record ===
keyVersion=1 ciphertext=00fc07228096ae3610732ac1a1f721cc9fd62d49b07b7dd548e5387f8ea787

=== rotate to key v2 (scheduled rotation, no downtime) ===
currentVersion now = 2
new record encrypted under keyVersion=2

=== decrypt BOTH old (v1) and new (v2) records after rotation ===
v1 record decrypts to: ssn:123-45-6789
v2 record decrypts to: ssn:987-65-4321
```

Rotation is a single atomic change to which key version new writes use — no downtime, no synchronous re-encryption of existing data required, and both the pre-rotation and post-rotation records decrypt correctly simultaneously, because each ciphertext carries its own key-version tag and the key ring retains both keys.

**Real evidence of why retirement requires re-encryption first, not just deletion:**

```
=== simulate v1 key retirement (removed from ring after re-encryption sweep) ===
v1 record now fails: no key for version 1  (this is why rotation runbooks re-encrypt-and-verify BEFORE retiring the old key)
```

Removing key v1 from the ring — simulating its deletion after retirement — immediately and permanently breaks decryption for any record still tagged with version 1. In a real system, this step would only be safe after a background job has re-encrypted every v1-tagged record under a current key version and verified the re-encryption succeeded; deleting a key before that sweep completes is equivalent to permanently destroying whatever data still depends on it.

## Production Scenarios

**A compliance audit requires proof that encryption keys protecting customer PII are rotated at least annually, and the team discovers their current architecture encrypts all historical data under one key with no versioning at all.** Implementing rotation retroactively on a system not designed for it requires a real migration project: introducing a key-version tag to the existing schema (a backfill, since existing records have no such tag and are implicitly "version 0" or the original key), standing up envelope-encryption logic for all new writes, and running a background re-encryption sweep across the entire existing dataset before the original key can be considered rotated at all. This is meaningfully more expensive than designing rotation in from the start, which is the practical argument for treating key versioning as a day-one architectural decision even before any rotation is imminently required.

**A key-management system (KMS) outage makes the current signing key temporarily unavailable, and a naive implementation without key-version tracking has no way to fall back to a still-valid previous key.** A properly-versioned system can continue verifying signatures produced under the previous key version (still cached or retrievable) while new signing temporarily fails over to a backup key or degrades gracefully, rather than the entire signing/verification capability going down as a single point of failure tied to one specific key's availability.

## Failure Modes and Debugging

- **Symptom: decryption fails for older records after a key rotation.** Confirm the rotation implementation actually retains old key versions in an accessible key ring, rather than replacing the active key outright — a rotation that doesn't preserve old keys until re-encryption is complete is a data-loss bug, not a successful rotation.
- **Symptom: a re-encryption sweep after rotation is taking an unexpectedly long time or causing load problems.** This is expected at scale — re-encrypting every existing record under a new key is genuinely proportional to dataset size, and should be designed as a rate-limited, resumable background job rather than a synchronous operation, especially for a rotation policy applied to a large existing dataset for the first time.
- **Anti-pattern to rule out first when a key is reported "lost" or accidentally deleted:** confirm whether any records are actually still tagged with that key version and unrecoverable, versus the key having already completed its retirement sweep and being safe to have removed — the practical severity differs enormously between these two situations, and confusing them either causes unnecessary panic or, worse, treats a genuine data-loss event as a non-issue.

## Trade-offs

More frequent key rotation reduces the exposure window and blast radius of a potential future compromise, but increases the operational overhead of managing multiple concurrent key versions and the volume of re-encryption work needed as older versions are retired. Retaining old key versions indefinitely (never retiring them) avoids re-encryption work entirely, but means a compromise of any historical key remains exploitable against any data still tagged with that version, forever — the retirement step, while operationally costly, is what actually bounds a key's blast radius over time.

## Decision Framework

Design key versioning and envelope encryption into any system handling sensitive data from the start, even if rotation isn't immediately required by policy — retrofitting it onto an unversioned system, as the compliance-audit production scenario shows, is a substantially larger project than building it in from day one. Choose a rotation cadence driven by the more conservative of: an applicable compliance requirement's explicit schedule, and an internally-assessed acceptable exposure window for the specific data's sensitivity — not an arbitrary default cadence. Never delete a key without first confirming (via an automated check, not a manual assumption) that zero records remain tagged with that version, or that any remaining records have been deliberately, knowingly accepted as permanently unrecoverable.

## Common Mistakes

- Treating "we encrypted the data" as a completed, static task rather than an ongoing lifecycle requiring rotation.
- Rotating a key by simply replacing it, with no versioning scheme, breaking decryption for every previously-encrypted record.
- Deleting an old key immediately upon "rotating," without confirming a re-encryption sweep of all data still tagged with that version has actually completed.
- Treating key rotation as purely reactive to a suspected compromise, rather than also a proactive practice limiting exposure volume and satisfying compliance schedules regardless of incident history.

## Anti-Patterns

Hardcoding a single, unversioned encryption key directly in application configuration with no key-management system, no rotation mechanism, and no per-record version tracking — this is a common shortcut in early-stage systems that becomes a substantial migration project the first time rotation is actually required (by compliance, by a suspected compromise, or by routine policy), precisely because retrofitting versioning onto already-encrypted data at scale is far more expensive than building it in from the start.

## Best Practices

Use a managed key-management system (a cloud KMS, HashiCorp Vault, or equivalent) rather than implementing key storage and access control from scratch — these systems provide audited access, automated rotation scheduling, and integration with envelope-encryption patterns as a built-in capability rather than custom-built infrastructure. Tag every encrypted record with its key version as a first-class, non-optional part of the encryption scheme from the very first implementation, even before any rotation is planned — this is the single design decision that determines whether a future rotation is routine or a multi-month migration project.

## Interview Answer Framework

### 30-Second Answer

Secrets management is controlling access to sensitive credentials outside source code; key rotation is periodically replacing encryption/signing keys while retaining the ability to handle data produced under old keys until they're deliberately retired. Envelope encryption — tagging each encrypted record with the key version that produced it — is what makes rotation practical without breaking existing data or requiring synchronous, all-at-once re-encryption.

### 2-Minute Answer

Definition: rotation replaces a key periodically, both proactively (limiting exposure volume, meeting compliance schedules) and reactively (responding to a suspected compromise). Why envelope encryption specifically: naive rotation (just swap the key) breaks decryption for every record encrypted under the previous key, since there's no way to know which key protected which record without an explicit tag — envelope encryption's per-record key-version tag solves exactly this. How it works: new writes use the current key version; old records keep their original tag; both remain decryptable as long as the corresponding key stays in the key ring; retirement (removing the old key) is only safe after a re-encryption sweep confirms no data still depends on it. One trade-off: more frequent rotation reduces exposure window but increases re-encryption operational overhead as older versions retire. One production example: measured directly, rotating from key v1 to v2 required zero downtime and both old and new records decrypted correctly simultaneously — but deliberately removing v1 from the key ring (simulating retirement without a completed re-encryption sweep) immediately and permanently broke decryption for the v1-tagged record, demonstrating exactly why retirement must follow re-encryption, never precede or replace it.

### 10-Minute Deep Dive

Cover: why rotation is proactive as well as reactive (exposure-volume limiting, compliance schedules, unbounded blast radius of an undetected slow leak without rotation); the envelope-encryption pattern and its per-record key-version tag as the mechanism that makes rotation practical; the real measured demonstration of zero-downtime rotation with simultaneous old/new record decryption; the real measured demonstration of the retirement failure mode (removing a key before its re-encryption sweep completes is equivalent to permanent data loss for dependent records); the retrofitting cost asymmetry — designing versioning in from day one versus retrofitting it onto an already-encrypted, unversioned dataset at scale; the KMS-outage production scenario illustrating that key-version tracking also provides operational resilience (fallback to a still-valid previous key), not just a security property.

### Whiteboard Explanation

Draw a "key ring" holding two keys, labeled v1 and v2, both present simultaneously. Draw two encrypted records, one tagged "v1" and one tagged "v2," each with an arrow pointing to its corresponding key on the ring — showing both are simultaneously decryptable. Then draw v1 being removed from the ring, and redraw the v1-tagged record's arrow now pointing at nothing, labeled "permanently undecryptable," to make the retirement-before-re-encryption failure mode visually explicit.

### Production Example

A healthcare-data platform is required by a compliance audit to demonstrate annual key rotation for all PII-protecting encryption keys. The engineering team discovers their original implementation, built years earlier under time pressure, encrypts all data under a single hardcoded key with no version tracking at all. Implementing rotation now requires: introducing a key-version column across every affected table (backfilled as an implicit "version 0" for existing data), building envelope-encryption logic for all new writes, provisioning a proper KMS-backed key ring, and running a multi-week, rate-limited background job re-encrypting the entire historical dataset under a new versioned key before the original key can be considered safely rotatable at all — a project scoped in months, directly attributable to the original design not having built in key versioning from the start.

### Trade-offs to Mention

More frequent rotation reduces exposure window and blast radius but increases the operational cost of managing multiple key versions and re-encrypting data as old versions retire; retaining old keys indefinitely avoids that cost but leaves a permanently unbounded blast radius for any historical key compromise.

### Common Candidate Mistakes

Treating encryption as a one-time, static implementation task rather than an ongoing lifecycle; describing rotation as "just generate a new key" without addressing what happens to already-encrypted data.

### Typical Follow-Up Questions

"How would you handle a very large existing dataset that needs its first-ever key rotation, given it wasn't originally designed with versioning?" → introduce a version tag (implicit "version 0" for existing unversioned data), then run a rate-limited, resumable background re-encryption job, verified record-by-record, before considering the original key retirable — this is a real migration project, not an instant operation. "What's the operational risk of rotating too frequently?" → excessive re-encryption overhead and increased key-ring management complexity, without a proportional security benefit once the rotation cadence is already well within the compliance/risk-tolerance window — rotation cadence should be driven by an actual exposure-window assessment, not minimized or maximized reflexively.

### Senior-Level Expectations

Correctly explains why naive key replacement breaks existing data, and describes envelope encryption's per-record version tag as the fix.

### Staff-Level Discussion

Reasons about key rotation as a system design decision with real retrofitting cost asymmetry — building versioning in from day one versus a multi-month migration project later — and factors this into architectural decisions for any new system handling sensitive data, even before rotation is immediately required. Recognizes key-version tracking as providing operational resilience (graceful KMS-outage fallback) as a side benefit distinct from its primary security purpose, and treats key retirement as requiring an explicit, verified re-encryption sweep as a hard precondition, never an assumption.

## Interview Questions

### Question 1

**A team wants to rotate their encryption key today. Walk through what needs to happen for this to be safe, given the system already has millions of records encrypted under the current key.**

**Expected answer:** first confirm the system tags each record with the key version that encrypted it (envelope encryption) — if it doesn't, this needs to be introduced first, with existing data backfilled as an implicit prior version. New writes then switch to the new key version immediately (this part can be instantaneous, zero downtime). The old key stays in the key ring, still able to decrypt its tagged records. A background job then re-encrypts existing records under the new key, verified as it proceeds. Only after that sweep completes and is verified can the old key be safely retired/deleted.

**Common mistakes:** describing rotation as "generate a new key and start using it" without addressing what happens to existing data, or without describing the verification-before-retirement step.

**Follow-up questions:** "What if the system has no key-version tagging today at all?" (this is a larger migration — introduce the tag, backfill existing data as an implicit version, then proceed with the sweep — meaningfully more expensive than a system already designed for rotation.)

**Senior-level expectations:** correctly describes the envelope-encryption pattern and the re-encrypt-then-retire sequence.

**Staff-level expectations:** proactively raises the backfill/migration cost for a system not originally designed with versioning, and describes the sweep as a rate-limited, resumable background job rather than a synchronous operation.

### Question 2

**Why should keys be rotated periodically even if there's no evidence of any compromise?**

**Expected answer:** several independent reasons — it limits the *volume* of data ever protected by a single key, bounding the blast radius of a future, not-yet-discovered compromise; it satisfies compliance frameworks with explicit rotation schedules regardless of incident history; and it reduces the exposure window of a slow, undetected key leak, which without rotation has an unbounded blast radius purely as a function of time, since there's no mechanism limiting how much data a silently-compromised key continues to protect.

**Common mistakes:** treating rotation as purely a reactive response to a known or suspected compromise.

**Follow-up questions:** "How would you choose an appropriate rotation cadence?" (the more conservative of an applicable compliance requirement's explicit schedule and an internally-assessed acceptable exposure window for the data's actual sensitivity — not an arbitrary default.)

**Senior-level expectations:** names at least two of the proactive reasons distinct from reactive compromise-response.

**Staff-level expectations:** proposes a concrete cadence-selection methodology rather than a fixed, one-size-fits-all rotation interval.

## Summary

Secrets management controls access to sensitive credentials outside source code; key rotation is the ongoing lifecycle practice of periodically replacing keys, proactively (limiting exposure volume, meeting compliance schedules) as well as reactively. Naive rotation breaks existing encrypted data; envelope encryption's per-record key-version tag is the pattern that makes rotation practical — demonstrated directly with real AES-256-GCM Java code showing zero-downtime rotation with simultaneous old/new record decryption, and the corresponding failure mode of a key removed before its re-encryption sweep completes, which permanently breaks decryption for any record still depending on it. Designing key versioning in from day one, rather than retrofitting it onto an already-encrypted, unversioned system, is the single decision that determines whether a future rotation is routine or a substantial migration project.

## Key Takeaways

- Key rotation is a proactive as well as reactive practice — it limits exposure volume and meets compliance schedules even absent any known compromise.
- Naive key replacement breaks decryption for every record encrypted under the previous key — envelope encryption's per-record key-version tag is what makes rotation practical.
- Old keys must stay in the key ring until a re-encryption sweep of every record still tagged with that version is complete and verified — retiring a key before that sweep is a permanent, unrecoverable data-loss event.
- Designing key versioning in from the start is meaningfully cheaper than retrofitting it onto an already-encrypted, unversioned system at scale.
- Key-version tracking provides operational resilience (graceful fallback during a KMS outage) as a side benefit distinct from its primary security purpose.

## Cheat Sheet

| Step | What happens | Downtime |
|---|---|---|
| Generate new key version | New key added to key ring, tagged with a new version number | None |
| Switch new writes to new version | New records encrypted under the new key | None |
| Old records remain decryptable | Old key stays in ring, still resolves old-tagged records | None |
| Re-encryption sweep | Background job re-encrypts old records under the new key, verifies | Rate-limited background work |
| Retire old key | Delete only after sweep is complete and verified | None, if sequenced correctly |

## Flashcards

**Q: Why does naive key rotation (just replace the key) break existing encrypted data?**
A: There's no way to know which key protected which record without an explicit tag — envelope encryption's per-record key-version tag solves this.

**Q: What must happen before an old key can be safely deleted after rotation?**
A: A re-encryption sweep of every record still tagged with that key version must complete and be verified — deleting first is a permanent data-loss event for any remaining dependent records.

**Q: Name two reasons to rotate keys proactively, absent any known compromise.**
A: Limiting the volume of data protected by a single key (bounding future blast radius) and satisfying compliance-mandated rotation schedules.

## Practice Exercises

1. Reproduce `KeyRotationDemo.java` and extend it to a third key version (v3), confirming records encrypted under all three versions (v1, v2, v3) remain simultaneously decryptable as long as all three keys stay in the ring.
2. Implement a simulated "re-encryption sweep": write a method that takes a v1-tagged ciphertext, decrypts it under v1, and re-encrypts it under the current version — then confirm that after this sweep, v1 can be safely removed from the ring without breaking that specific record.

## Solutions

1. The `KEY_RING` map and `Ciphertext.keyVersion()` tag generalize directly to any number of versions — decryption always looks up the specific tagged version, independent of how many other versions exist simultaneously.
2. A correct sweep implementation calls `decrypt()` under the old version, then `encrypt()` under `currentVersion`, producing a new `Ciphertext` tagged with the current version — replacing the stored v1-tagged ciphertext with this new one means the record no longer depends on v1 at all, and v1 can then be removed from the ring without affecting it.

## Additional Reading

- [NIST SP 800-57 Part 1 Rev. 5 — Recommendation for Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)

## Official References

- [NIST SP 800-57 Part 1 Rev. 5 — Recommendation for Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)
