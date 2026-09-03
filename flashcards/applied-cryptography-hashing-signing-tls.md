---
title: "Flashcards: Applied Cryptography: Hashing, Signing, and TLS"
slug: applied-cryptography-hashing-signing-tls
document_type: flashcard-deck
domain: security
topic_id: T-1303
canonical: ../handbook/security/applied-cryptography-hashing-signing-tls.md
last_updated: 2026-08-06
---

# Flashcards: Applied Cryptography: Hashing, Signing, and TLS

**Canonical chapter:** [`syllabus/12-security/applied-cryptography-hashing-signing-tls.md`](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)

## Card: Why SHA-256 is wrong for password storage

**Prompt:**
Why is a fast hash like SHA-256 the wrong tool for password storage, even with a per-user salt?

**Answer:**
The salt defeats rainbow tables, but SHA-256's speed lets an attacker brute-force at high speed per password on GPU hardware — password hashing needs a deliberately expensive, tunable-cost function instead.

**Why it matters:**
The precise reason "salted SHA-256" is a common but insufficient answer to the password-storage question.

**Common trap:**
Believing a salt alone makes a fast general-purpose hash acceptable for password storage.

**Related:**
[handbook/security/applied-cryptography-hashing-signing-tls.md](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)

## Card: Does a digital signature provide confidentiality

**Prompt:**
Does a digital signature provide confidentiality?

**Answer:**
No — it proves authenticity and integrity only; signed content remains fully readable.

**Why it matters:**
A frequently conflated pair of guarantees — signing and encryption solve different problems.

**Common trap:**
Assuming signing a payload also protects its confidentiality.

**Related:**
[handbook/security/applied-cryptography-hashing-signing-tls.md](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)

## Card: Why TLS 1.3 removed legacy cipher suites

**Prompt:**
Why did TLS 1.3 remove support for many legacy cipher suites and key-exchange modes?

**Answer:**
Those legacy options enabled negotiation-based attacks (like downgrade attacks); a smaller, fixed modern set removes that attack surface and also collapses the handshake to one round trip.

**Why it matters:**
The reason "use the platform's current TLS defaults" is the secure choice, not a compromise.

**Common trap:**
Manually pinning an older, broader cipher-suite list under the assumption it's more compatible and equally safe.

**Related:**
[handbook/security/applied-cryptography-hashing-signing-tls.md](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)
