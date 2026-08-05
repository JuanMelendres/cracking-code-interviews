---
title: "Salted Fast-Hash Passwords Surviving Rainbow Tables, Not GPU Cracking"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/security/applied-cryptography-hashing-signing-tls.md
source: handbook/security/applied-cryptography-hashing-signing-tls.md#production-scenarios
---

# Salted Fast-Hash Passwords Surviving Rainbow Tables, Not GPU Cracking

## Context

User passwords are hashed with plain SHA-256 and a per-user salt.

## Symptoms

A security review finds the password-hashing scheme uses SHA-256 with a per-user salt, and flags it as inadequate despite the salt being correctly implemented.

## Impact

If the hash database were ever stolen, an attacker could recover a large fraction of user passwords far faster than the team's threat model assumed, because the hashing scheme's actual resistance is much weaker than "we salt our hashes" suggests.

## Initial Hypotheses

- The salt implementation itself is flawed — checked and ruled out; the salt is correctly per-user and defeats rainbow-table (precomputed hash table) attacks exactly as intended.
- The underlying hash function's speed, not the salt, is the actual weakness — correct.

## Evidence

The salt correctly defeats rainbow-table attacks, but does nothing about the speed problem: an attacker with the stolen hash database can still try billions of salted-SHA-256 guesses per second per password on GPU hardware, because SHA-256 itself is fast.

## Investigation Timeline

1. **Security review conducted**, examining the password-hashing scheme's actual resistance to a stolen-database scenario.
2. **Salt correctness verified**, confirming it does defeat rainbow-table precomputation as designed.
3. **Hash function speed evaluated separately** from the salting scheme, since the two properties address different attack classes.
4. **Gap identified**: a fast hash function, even correctly salted, offers little resistance to brute-force guessing at GPU-accelerated scale.

## Root Cause

The salt correctly defeats rainbow-table attacks — precomputed hash tables — but does nothing about the speed problem: SHA-256 itself is a fast hash function, so an attacker with the stolen database can try billions of salted guesses per second per password on GPU hardware.

## Immediate Mitigation

None available without a migration — the exposure is inherent to the algorithm choice, not a runtime-configurable setting.

## Permanent Fix

Migrate to a password-hashing function with a tunable cost parameter — Argon2id is the current OWASP-recommended default; PBKDF2 remains acceptable, especially where FIPS compliance is required. Because the old hashes can't be upgraded without the plaintext password, this typically means re-hashing opportunistically at the next successful login rather than a bulk migration.

## Alternatives Considered

Adding a pepper (an application-wide secret combined with the salt) to the existing SHA-256 scheme instead of migrating algorithms. Rejected as insufficient — a pepper adds a constant-cost secret, but does not address the fundamental speed problem; a fast hash remains fast to brute-force even with a pepper, given the stolen database and pepper together (or the pepper alone, if it's ever also compromised).

## Trade-offs

A tunable-cost function like Argon2id is deliberately slower per hash than SHA-256, adding real, measurable CPU cost to every login. Accepted, since that added cost is precisely the mechanism that makes brute-force guessing at scale impractical for an attacker.

## Prevention

Any password-storage scheme should default to a purpose-built, tunable-cost password-hashing function from the start, never a general-purpose fast cryptographic hash function regardless of salting — salting and slow-hashing solve different problems and neither substitutes for the other.

## Monitoring and Alerts

- A standing security-review checklist item verifying password-hashing algorithm choice specifically, distinguished from checking only that hashing and salting are present — "we hash and salt passwords" is not sufficient evidence of adequate protection without confirming the specific algorithm's cost properties.
- Re-hash-on-login migration progress tracked as a metric, surfacing how much of the user base remains on the legacy fast-hash scheme at any point during the gradual migration.

## Interview Story

This maps to "you salt your password hashes, are you actually safe" — a direct test of whether a candidate conflates salting with slow hashing. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a security review flagged a correctly salted SHA-256 password-hashing scheme as inadequate.
- **Task:** explain why correct salting doesn't imply adequate protection.
- **Action:** verify the salt implementation is genuinely correct; separately evaluate the hash function's speed against a stolen-database, GPU-accelerated brute-force scenario; identify the fast hash function, not the salting, as the actual weakness.
- **Result:** migrated to Argon2id with opportunistic re-hashing at login, adding deliberate, tunable computational cost specifically to make brute-force guessing impractical at scale.

## Staff-Level Discussion

The critical distinction this incident teaches is that salting and slow hashing solve two different, independent attack classes: salting defeats precomputation (rainbow tables), while a tunable-cost function defeats brute-force speed, and a scheme can correctly implement one while remaining fully vulnerable through the other. "We salt our passwords" is a phrase that sounds complete but answers only half the actual threat model, which is exactly why it's a common and dangerous gap — teams that implement salting correctly often stop there, believing the problem is solved. A Staff engineer reviewing any credential-storage scheme should evaluate salting and hash-function cost as two separate, both-required properties, and should treat any use of a general-purpose fast cryptographic hash (SHA-256, MD5, SHA-1) for password storage as a finding regardless of salting correctness.

## Related Handbook Chapters

- [Applied Cryptography: Hashing, Signing, TLS](../handbook/security/applied-cryptography-hashing-signing-tls.md) — canonical password-hashing cost-parameter mechanics used here.
