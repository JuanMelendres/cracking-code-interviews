---
title: "Cheat Sheet: Applied Cryptography — Hashing, Signing, TLS"
slug: applied-cryptography-hashing-signing-tls
document_type: cheat-sheet
domain: security
topic_id: T-1303
canonical: ../handbook/security/applied-cryptography-hashing-signing-tls.md
last_updated: 2026-08-05
---

# Applied Cryptography — Hashing, Signing, TLS

**Canonical chapter:** [`syllabus/12-security/applied-cryptography-hashing-signing-tls.md`](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)

## Core Mental Model

Keep three separate mental boxes — conflating them is the single most common mistake in this domain. **Hashing** answers "does this input match a value I already committed to, without me ever storing the input" (integrity/verification of a secret, one-way). **Signing** answers "did the claimed sender produce this exact content, verifiable by anyone with the public key" (authenticity + integrity, asymmetric). **TLS** answers "can two parties who've never met establish a private, tamper-evident channel right now" (transport confidentiality + integrity, session-based, itself built from signing and symmetric encryption). A password hash is not a signature. A signature is not encryption. TLS protects data in transit only — hashing and signing protect data at rest and provenance, which TLS never touches once the connection ends.

## Essential Definitions

- **Password-hashing functions** (PBKDF2, bcrypt, scrypt, Argon2) — deliberately expensive one-way functions with a tunable cost parameter, so offline brute-force is computationally expensive even with the stolen hash.
- **Digital signatures** (RSA, ECDSA/EdDSA) — asymmetric key pair; anyone with the public key verifies specific content was signed by the private-key holder and hasn't changed since.
- **TLS** (current: 1.3, RFC 8446) — negotiates a shared symmetric session key using asymmetric crypto only for the handshake, then fast symmetric encryption for the actual data.
- **Salt vs. slow hash** — a salt defeats *rainbow-table precomputation*; a slow, tunable-cost function defeats *brute-force speed*. Two different problems — neither substitutes for the other.

## Decision Table

| Primitive | Answers | Correct tools | Wrong tool trap |
|---|---|---|---|
| Password hashing | Does this match a stored secret? | Argon2id (OWASP default), PBKDF2 (FIPS contexts), bcrypt, scrypt | Plain SHA-256/MD5, even salted |
| Signing | Did the claimed party produce this exact content? | ECDSA, EdDSA (new implementations), RSA (legacy/compliance) | Assuming signed = encrypted/confidential |
| TLS | Can two parties establish a private channel now? | TLS 1.3, current library defaults | Custom "secure channel" implementations |

**Trade-offs:** higher password-hashing cost parameters increase brute-force resistance but directly increase login latency and CPU cost at scale — tune against measured wall-clock time on real hardware, never an arbitrary high value. Asymmetric operations (signing, TLS handshake) are far more expensive per-op than symmetric encryption, which is why TLS uses them only once per connection, not per message.

## Key Numbers (real, executed — 3 separate demos)

PBKDF2 cost scaling (`PasswordHashingCostDemo.java`, each iteration count in its own fresh JVM):

```
iterations=1        time=  31ms  <- fixed JVM-startup cost baseline
iterations=100000    time=  86ms  <- ~55ms marginal
iterations=600000    time= 128ms  <- ~97ms marginal (JIT warmup affects the ratio — see chapter note)
```

EC signature tamper detection (`SignatureTamperDemo.java`, `SHA256withECDSA`):

```
verify(original message, same signature)                    = true
verify(tampered message '100.00' -> '900.00', same signature) = false
```

Real self-signed TLS 1.3 handshake (`openssl s_client`/`s_server`, OpenSSL 3.x):

```
Protocol version: TLSv1.3
Ciphersuite: TLS_AES_256_GCM_SHA384          <- fixed modern set, no weak-suite negotiation possible
Negotiated TLS1.3 group: X25519MLKEM768      <- hybrid post-quantum key exchange, already the default
Verification error: self-signed certificate  <- expected/correct for a non-CA-issued cert
```

## Common Pitfalls

- Using a fast general-purpose hash (SHA-256, MD5) for password storage instead of a purpose-built cost-tunable function — even salted, this only defeats rainbow tables, not GPU brute-force speed.
- Confusing "signed" with "encrypted" — a signed payload is fully readable; it only proves authenticity and integrity.
- Assuming HTTPS/TLS alone protects data at rest — TLS protects data only in transit, for the connection's duration.
- Reusing the same key across unrelated purposes (signing and encryption both) — couples independent security properties and complicates rotation.

## Interview Answer Skeleton

**30-sec:** Hashing, signing, TLS solve three different problems. Password hashing is deliberately slow (opposite of every other perf goal in this handbook). Signing proves authenticity/integrity, never confidentiality. TLS is a transport-only, session-based private channel using asymmetric crypto for the handshake and symmetric for the data.

**2-min:** Add why each exists (general-purpose hashing is too fast for passwords; content needs provenance independent of transport; two untrusted parties need confidentiality with no pre-shared secret) + the salted-fast-hash trade-off (defeats rainbow tables, not brute-force speed) + the real measured evidence (PBKDF2 cost scaling, EC tamper detection, TLS 1.3 handshake).

**Whiteboard:** Three separate boxes — "Hashing" (one-way, no arrow back), "Signing" (private key → sign → signature + readable content; public key → verify), "TLS" (Client/Server connected by a channel: handshake icon using asymmetric crypto, transitioning to a padlock for the symmetric session). Emphasize these are independent tools, not a pipeline — though TLS's own handshake uses signing internally.

**Staff-level framing:** reason about cryptographic-control *enforcement* as a systems problem, not just an algorithm-choice problem — "we sign our requests" is a claim requiring verification of every consuming code path, not a self-evidently true statement. Treat key-rotation and cost-parameter-upgrade paths as part of initial design, not an afterthought.

## Production Warning Signs

- A security review finds salted SHA-256 for passwords — the salt is correct (defeats rainbow tables) but does nothing about brute-force speed; migrate to Argon2id (or PBKDF2 for FIPS), re-hashing opportunistically at next login since old hashes can't be upgraded without the plaintext.
- A signed request envelope isn't actually verified server-side on every code path (e.g., a debug flag or "internal caller" exception bypasses verification) — a signature that exists but isn't checked provides zero protection; "we sign our requests" needs confirmation, not assumption.
- **Prevention:** never roll a custom cryptographic scheme — even competent engineers reliably introduce subtle flaws that adversarially-reviewed standards have already addressed.

## Related

- `syllabus/12-security/secrets-management-and-key-rotation.md`
- `syllabus/12-security/oauth2-oidc-and-jwt.md`
- `production-cookbook/salted-fast-hash-passwords-surviving-rainbow-tables-not-gpu-cracking.md`
