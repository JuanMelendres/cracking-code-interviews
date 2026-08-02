---
title: "T-1303 · Applied Cryptography: Hashing, Signing, and TLS"
topic_id: T-1303
domain: Security
tier: Advanced
iwi: 6.20
prerequisites: []
unlocks: [T-1304]
week: 17
last_reviewed: 2026-08-02
canonical: ../../handbook/security/applied-cryptography-hashing-signing-tls.md
---

# T-1303 · Applied Cryptography: Hashing, Signing, and TLS

**IWI 6.20 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [Applied Cryptography: Hashing, Signing, and TLS](../../handbook/security/applied-cryptography-hashing-signing-tls.md). This file is the Week 17 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-17/crypto/` — PBKDF2 timing (own JVM process per iteration count), EC signature tamper detection, and a real self-signed TLS 1.3 handshake via `openssl s_server`/`s_client`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
4. [Trade-offs](#4-trade-offs)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. The concept

Three separate boxes, not one: password hashing (deliberately slow, one-way, verifies a secret without storing it), signing (asymmetric, proves authenticity + integrity, never confidentiality), TLS (negotiates a private channel per connection, asymmetric handshake then symmetric session). → [Mental Model](../../handbook/security/applied-cryptography-hashing-signing-tls.md#mental-model).

## 2. Why it exists

A fast general-purpose hash is the wrong tool for password storage precisely because it's fast — password-hashing functions add a deliberate, tunable cost to make offline brute-forcing expensive. → [Core Concepts](../../handbook/security/applied-cryptography-hashing-signing-tls.md#core-concepts).

## 3. The measured evidence

Real PBKDF2: 1-iteration run isolates ~31ms JVM startup cost; 100,000 iterations ~86ms, 600,000 iterations ~128ms (with an honest JIT-warmup caveat on the non-linear scaling). Real EC signature: verification succeeds on the original message, fails the instant one substring changes (`100.00` → `900.00`). Real TLS 1.3 handshake: `TLS_AES_256_GCM_SHA384` cipher suite, `X25519MLKEM768` hybrid post-quantum key-exchange group, correctly flagged self-signed certificate. → [Internal Implementation](../../handbook/security/applied-cryptography-hashing-signing-tls.md#internal-implementation) has the full trace.

## 4. Trade-offs

Higher password-hashing cost increases brute-force resistance but directly increases login latency and CPU cost at scale — must be tuned against real hardware, not an arbitrary high value. → [Trade-offs](../../handbook/security/applied-cryptography-hashing-signing-tls.md#trade-offs).

## 5. Interview questions

1. A teammate proposes salted SHA-256 for password hashing. What's wrong, and what would you recommend instead?
2. How do you know a signature is actually being verified, not just present in the protocol?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/security/applied-cryptography-hashing-signing-tls.md#interview-questions).

## 6. Common mistakes

Using a fast general-purpose hash for password storage; confusing "signed" with "encrypted"; assuming HTTPS protects data at rest, not just in transit. → [Common Mistakes](../../handbook/security/applied-cryptography-hashing-signing-tls.md#common-mistakes).

## 7. Staff-level discussion

Treats cryptographic-control enforcement as a systems problem (a debug bypass can silently undermine a correctly-designed signature scheme), not just an algorithm-choice problem. → [Staff-Level Discussion](../../handbook/security/applied-cryptography-hashing-signing-tls.md#interview-answer-framework).

## 8. Summary

Hashing, signing, and TLS solve three different problems and are never substitutes for each other. Measured directly: adaptive PBKDF2 cost, single-byte signature tamper detection, and a real TLS 1.3 handshake already defaulting to a hybrid post-quantum key-exchange group. → [Summary](../../handbook/security/applied-cryptography-hashing-signing-tls.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/security/applied-cryptography-hashing-signing-tls.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/security/applied-cryptography-hashing-signing-tls.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/security/applied-cryptography-hashing-signing-tls.md#flashcards). Full week-level deck: `09-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/security/applied-cryptography-hashing-signing-tls.md#practice-exercises) and [Solutions](../../handbook/security/applied-cryptography-hashing-signing-tls.md#solutions). Reproducible demo: `practice/java/week-17/crypto/`.

## 13. Additional Reading

- [NIST SP 800-63B — Digital Identity Guidelines](https://csrc.nist.gov/pubs/sp/800/63/b/upd2/final)

## 14. Official References

- [RFC 8446 — TLS 1.3](https://www.rfc-editor.org/rfc/rfc8446)
