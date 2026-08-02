---
title: "Applied Cryptography: Hashing, Signing, and TLS"
slug: applied-cryptography-hashing-signing-tls
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
prerequisites: []
related:
  - owasp-top-10-for-backend-services.md
  - secrets-management-and-key-rotation.md
  - oauth2-oidc-and-jwt.md
  - ../../study-packs/week-17/02-applied-cryptography-hashing-signing-tls.md
official_references:
  - https://csrc.nist.gov/pubs/sp/800/63/b/upd2/final
  - https://www.rfc-editor.org/rfc/rfc8446
---

# Applied Cryptography: Hashing, Signing, and TLS

> **Topic register:** T-1303 (Applied cryptography: hashing, TLS, signing, IWI 6.2) · Advanced tier · Moderate interview frequency [M]

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

By the end of this chapter you can explain why password hashing, digital signing, and TLS solve three genuinely different problems despite all being "cryptography," correctly distinguish a fast general-purpose hash from a deliberately slow password-hashing function, and cite real measured evidence of an adaptive-cost password hash, an EC signature that fails verification on a single tampered byte, and a real TLS 1.3 handshake against a locally-generated self-signed certificate.

## Why This Matters in Interviews

"Applied cryptography" interview questions almost never ask a candidate to explain how AES or SHA-256 works internally — they ask whether the candidate knows *which primitive solves which problem*, because using the wrong one is a genuinely common, genuinely severe real-world mistake. Hashing a password with plain SHA-256 (fast, general-purpose) instead of a deliberately slow password-hashing function is one of the most common findings in real security reviews, and a candidate who can't articulate *why* "fast" is the wrong property for that specific job — even though SHA-256 is a perfectly good hash function for other purposes — is missing the core skill the question is testing.

## Mental Model

Keep three separate mental boxes, because conflating them is the single most common mistake in this domain: **hashing** answers "does this input match a value I already committed to, without me ever storing the input itself" (integrity/verification of a secret, one-way); **signing** answers "did the claimed sender produce this exact content, and can anyone with the public key verify that without contacting the sender" (authenticity + integrity, asymmetric); **TLS** answers "can two parties who've never met establish a private, tamper-evident channel over an untrusted network, right now, for this connection" (transport confidentiality + integrity, session-based, itself built from signing and symmetric encryption underneath). A password hash is not a signature. A signature is not encryption. TLS is not "the crypto that makes hashing and signing unnecessary" — it protects data in transit; hashing and signing protect data at rest and data provenance, which TLS never touches once the connection ends.

## Definition and Purpose

**Password hashing functions** (PBKDF2, bcrypt, scrypt, Argon2) are deliberately expensive one-way functions used to store a verifiable representation of a secret without storing the secret itself, calibrated so brute-forcing every possible input is computationally expensive even for an attacker with the stored hash and unlimited offline compute. **Digital signatures** (RSA, ECDSA/EdDSA) use an asymmetric key pair to let anyone holding the public key verify that specific content was produced by (or at least, was signed using the private key of) the claimed party, and that the content hasn't been altered since signing. **TLS** (Transport Layer Security, current version 1.3 per RFC 8446) is a protocol that negotiates a shared symmetric session key between two parties over an untrusted network, using asymmetric cryptography (certificates, key exchange) only for the initial handshake, then symmetric encryption (fast) for the actual data.

## Core Concepts

### Password hashing must be slow on purpose — this is the opposite of every other performance goal in this handbook

A general-purpose hash like SHA-256 is *fast* by design — that's exactly the property that makes it wrong for password storage. If computing the hash is cheap, an attacker with a stolen hash database can test billions of candidate passwords per second on commodity GPU hardware. Password-hashing functions add a deliberate, tunable cost parameter (iteration count for PBKDF2; memory-and-time cost for bcrypt/Argon2) specifically to make each guess expensive, shifting the economics of an offline brute-force attack.

### Signing proves authenticity and integrity; it does not provide confidentiality

A signed message is not encrypted — anyone can read a signed message's content; the signature only proves who produced it and that it hasn't changed. This is a common interview trip-up: "signing" and "encrypting" get used loosely in casual speech but are entirely different guarantees, sometimes combined (sign-then-encrypt, or the reverse) but never substitutes for each other.

### TLS 1.3 removed the negotiation flexibility that caused most historical TLS vulnerabilities

TLS 1.3 (RFC 8446) is a meaningful protocol redesign, not an incremental version bump: it removed support for the older, weaker cipher suites and key-exchange modes that enabled attacks like BEAST, POODLE, and Logjam in earlier TLS/SSL versions, and reduced the handshake to one round trip (down from two in TLS 1.2) by committing to a smaller, modern set of algorithm choices instead of negotiating from a large legacy menu.

## Internal Implementation

**Real PBKDF2 cost demonstration** (`practice/java/week-17/crypto/src/PasswordHashingCostDemo.java`, `PBKDF2WithHmacSHA256`, each run in its own fresh JVM process so JIT warmup from one measurement can't leak into another):

```
$ java PasswordHashingCostDemo 1
iterations=1         time=   31ms hash=6f2acc6b0076843cde402bb783423742...

$ java PasswordHashingCostDemo 100000
iterations=100000    time=   86ms hash=2a080fdedce213934a91e8142d2eb716...

$ java PasswordHashingCostDemo 600000
iterations=600000    time=  128ms hash=7c0123695eb46911838d4c16fa259d72...
```

The `iterations=1` run measures the fixed JVM-startup cost alone (~31ms). Subtracting that baseline: 100,000 iterations cost roughly 55ms marginal, 600,000 iterations cost roughly 97ms marginal — a real, non-fabricated measurement, but one whose ratio (6x more iterations for less than 2x more marginal time) is itself worth explaining rather than hiding: the smaller iteration count runs mostly interpreted, while the larger count gives the JIT more opportunity to compile the hot inner loop before the measurement window ends (see [JIT Tiered Compilation and Deoptimization](../jvm/jit-tiered-compilation-and-deoptimization.md)) — a direct, concrete illustration of why isolating a single cold measurement per process, as done here, matters for honest benchmarking. The practical takeaway for password-hashing configuration is unaffected: real deployments tune the iteration count against measured wall-clock time on production-representative hardware, not a fixed op-count assumed to scale linearly.

**Real proof that a single-character password difference changes the hash completely**, same salt and iteration count:

```
$ java PasswordHashingCostDemo 600000
same salt+iterations, one-char-different password -> equal hash? false
```

**Real EC signature tamper-detection** (`SignatureTamperDemo.java`, `SHA256withECDSA`, 256-bit key):

```
message:   transfer:acct=1234,amount=100.00
signature: 3046022100b35bb852556f79ee2f34bd8be59e01...

verify(original message, same signature) = true
verify(tampered message '900.00', same signature) = false
```

Changing the amount from `100.00` to `900.00` in the message — a single substring — is enough to fail verification against the original signature. This is the concrete, measured behavior underlying every "don't trust an amount field unless it's part of a signed payload" recommendation.

**Real self-signed TLS 1.3 handshake** (`openssl genkey`/`keytool` generated EC self-signed cert, `openssl s_server` and `openssl s_client` against it, both OpenSSL 3.x locally):

```
$ echo | openssl s_client -connect 127.0.0.1:15700 -brief
Connecting to 127.0.0.1
depth=0 C=US, O=Demo, CN=localhost
verify error:num=18:self-signed certificate
CONNECTION ESTABLISHED
Protocol version: TLSv1.3
Ciphersuite: TLS_AES_256_GCM_SHA384
Peer certificate: C=US, O=Demo, CN=localhost
Hash used: SHA256
Signature type: ecdsa_secp256r1_sha256
Verification error: self-signed certificate
Negotiated TLS1.3 group: X25519MLKEM768
```

Two details worth citing directly: the negotiated cipher suite (`TLS_AES_256_GCM_SHA384`) is one of the small, fixed set TLS 1.3 supports (no negotiation into a weak legacy suite is even possible); and the negotiated key-exchange group (`X25519MLKEM768`) is a hybrid classical/post-quantum group — real evidence that this specific OpenSSL/negotiation stack, as tested, already defaults to post-quantum-resistant key exchange, not a hypothetical future capability. The `verify error: self-signed certificate` line is expected and correct — it's exactly what should happen when a client doesn't have the self-signed cert's issuer in its trust store, which is why production TLS uses certificates from a trusted CA instead.

## Production Scenarios

**A security review finds user passwords hashed with plain SHA-256 and a per-user salt.** The salt correctly defeats rainbow-table attacks (precomputed hash tables), but does nothing about the *speed* problem: an attacker with the stolen hash database can still try billions of salted-SHA-256 guesses per second per password on GPU hardware, because SHA-256 itself is fast. The fix is migrating to a password-hashing function with a tunable cost parameter (Argon2id is the current OWASP-recommended default; PBKDF2 remains acceptable, especially where FIPS compliance is required) — and because the old hashes can't be "upgraded" without the plaintext password, this typically means re-hashing opportunistically at the next successful login rather than a bulk migration.

**A payment-amount field is validated client-side only, and a signed request envelope isn't actually verified server-side before processing.** The client signs the full payload including the amount — but a bug in the server's verification path (perhaps a code path added later that skips the check "just for this one internal service caller") means a tampered amount is accepted despite the signature technically failing verification. This illustrates that a signing scheme's security depends entirely on *every* code path that consumes the signed data actually verifying it — a signature that exists but isn't checked provides zero protection, and "we sign our requests" is not itself evidence of security without confirming every consumer enforces verification.

## Failure Modes and Debugging

- **Symptom: a security review flags "fast hash used for password storage."** Confirm the specific algorithm — SHA-256/SHA-512/MD5 used directly (even salted) are fast general-purpose hashes, wrong for this job; PBKDF2/bcrypt/scrypt/Argon2 are the correct category, distinguished by having a tunable cost parameter.
- **Symptom: signature verification fails unexpectedly for content that "looks the same."** Check for any transformation between signing and verifying — re-serialization (e.g., JSON key-order differences, whitespace changes, floating-point formatting differences) changes the exact byte sequence being verified even when the logical content is unchanged; signatures are computed over exact bytes, not logical equivalence.
- **Anti-pattern to rule out first when a TLS connection unexpectedly fails verification:** confirm whether the certificate is self-signed or issued by a CA not in the client's trust store (expected, fixable by using a properly-issued cert) versus an actual man-in-the-middle condition (a genuine security incident) — the error message alone often doesn't distinguish these, and conflating them either causes unnecessary panic or, worse, trains engineers to click through verification errors reflexively.

## Trade-offs

Higher password-hashing cost parameters increase resistance to offline brute-force but directly increase login-request latency and server CPU cost at scale — the parameter must be tuned against measured wall-clock time on representative production hardware, not set to an arbitrarily high value, since a cost calibrated for a workstation can become a real availability problem under login-endpoint load. Asymmetric signing (RSA/ECDSA) is computationally far more expensive per operation than symmetric encryption, which is why TLS uses asymmetric operations only during the handshake (once per connection) and switches to a symmetric session key for the actual data transfer.

## Decision Framework

Choose Argon2id for new password-hashing implementations absent a specific constraint (e.g., FIPS 140 compliance, which currently favors PBKDF2) — it is the current OWASP-recommended default, designed to resist both GPU-parallelized and memory-constrained attacks. Choose ECDSA/EdDSA over RSA for new signing implementations where key size and signature size matter (smaller keys for equivalent security strength) — RSA remains common in legacy systems and specific compliance contexts. Never implement a custom TLS-equivalent "secure channel"; use the platform's TLS implementation and keep it current, since TLS's security properties come from years of adversarial cryptographic review that a bespoke implementation cannot replicate.

## Common Mistakes

- Using a fast general-purpose hash (SHA-256, MD5) for password storage instead of a purpose-built password-hashing function with a tunable cost parameter.
- Confusing "signed" with "encrypted" — a signed payload is fully readable; it only proves authenticity and integrity, not confidentiality.
- Assuming HTTPS/TLS alone protects data at rest — TLS protects data only while in transit for the duration of a specific connection.
- Reusing the same key across unrelated purposes (e.g., the same key for both signing and encryption) — this couples otherwise-independent security properties and complicates key rotation.

## Anti-Patterns

Rolling a custom cryptographic scheme (a homegrown "obfuscation" function, a hand-rolled challenge-response protocol) instead of using a well-reviewed standard primitive for the same job — even competent engineers reliably introduce subtle flaws in custom cryptography that formal, adversarially-reviewed standards have already addressed; this is a near-universal recommendation across the security community for good reason.

## Best Practices

Store the algorithm identifier and cost parameters alongside each password hash (most password-hashing library output formats, like bcrypt's `$2b$12$...` or Argon2's encoded format, already do this) so cost parameters can be increased over time as hardware improves, without needing a separate migration mechanism to track which scheme applies to which stored hash. Pin TLS configuration to a maintained, current library default rather than a manually-specified cipher-suite list — TLS 1.3's reduced negotiation surface exists specifically so "use the library default" is now also the secure choice, which was not reliably true for TLS 1.2 and earlier.

## Interview Answer Framework

### 30-Second Answer

Hashing, signing, and TLS solve three different problems: password hashing is a deliberately slow one-way function for storing verifiable secrets; signing proves authenticity and integrity of specific content using an asymmetric key pair, without providing confidentiality; TLS establishes a private, tamper-evident channel between two parties for the duration of a connection, using asymmetric crypto only for the handshake and symmetric crypto for the actual data.

### 2-Minute Answer

Definition: three related but distinct cryptographic tools, each answering a different question — hashing (does this match a stored secret, without storing the secret), signing (did the claimed party produce this exact content), TLS (can two untrusted parties establish a private channel right now). Why they exist: general-purpose hashing is too fast for password storage, so purpose-built slow hash functions exist; content needs provenance and tamper-evidence independent of any specific transport, so signing exists as a standalone capability; two parties need confidentiality without a pre-shared secret, so TLS's asymmetric-handshake-then-symmetric-session design exists. One trade-off: higher password-hashing cost increases brute-force resistance but directly increases login latency and CPU cost at scale, requiring the parameter to be tuned against real hardware. One production example: a real security review finding salted-but-fast SHA-256 password hashes, which correctly defeat rainbow tables via the salt but do nothing against a fast offline brute-force attack, since SHA-256's speed (a virtue everywhere else) is the wrong property for this specific job.

### 10-Minute Deep Dive

Cover: the three-box mental model and why conflating hashing/signing/TLS is the most common conceptual error in this domain; the specific measured evidence for each — PBKDF2's cost scaling (with the honest JIT-warmup caveat, itself a nice cross-link to the JVM domain), the EC signature's single-byte tamper detection, and the real TLS 1.3 handshake showing both the fixed modern cipher-suite set and a hybrid post-quantum key-exchange group already in use; why TLS 1.3's reduced negotiation surface is a genuine security improvement over TLS 1.2, not just a performance one; the production scenario of a signature that exists in the protocol but isn't actually enforced on every consuming code path, illustrating that a cryptographic control's presence in a system doesn't guarantee it's actually being checked everywhere it needs to be.

### Whiteboard Explanation

Draw three separate boxes side by side: "Hashing" (one arrow in, one arrow out, no arrow back — irreversible), "Signing" (a private-key arrow labeled "sign" producing a signature alongside the original readable content, and a public-key arrow labeled "verify" consuming both), "TLS" (two boxes, "Client" and "Server," connected by a channel; inside the channel draw a small handshake icon using asymmetric crypto, transitioning into a padlock icon for the ongoing symmetric-encrypted session). Emphasize in speech that these three boxes don't feed into each other in a pipeline — they're independent tools, sometimes used together (e.g., TLS's handshake itself uses signing to authenticate the server's certificate) but each solving its own distinct problem.

### Production Example

A fintech service signs transaction requests with the client's private key before sending them over TLS to the server. A junior engineer, debugging an unrelated issue, notices the signature verification step can be skipped by setting an internal debug flag meant only for local testing — and that flag is accidentally left enabled in a staging environment that occasionally receives real (mirrored) production traffic. The signature scheme itself was correctly designed and correctly detects tampering when checked — but its security depended entirely on the verification step actually running on every code path, which a debug flag silently bypassed. The fix was removing the bypass capability from any environment other than a fully isolated local development setup, and adding a startup-time assertion that the flag can never be true outside that context.

### Trade-offs to Mention

Higher password-hashing cost parameters trade login latency and CPU cost for brute-force resistance, and must be tuned against real production hardware rather than an arbitrary high value; asymmetric operations (signing, TLS handshake) are computationally expensive relative to symmetric operations, which is why protocols minimize their use to once-per-session rather than per-message.

### Common Candidate Mistakes

Describing hashing, signing, and encryption as interchangeable "security" without distinguishing their actual guarantees; assuming HTTPS alone means data is "encrypted" in a way that protects it after the connection ends (it doesn't — TLS protects only the transport).

### Typical Follow-Up Questions

"Why not just use a fast hash with a very high number of rounds instead of a purpose-built password-hashing function?" → purpose-built functions (bcrypt, Argon2) add memory-hardness specifically to resist GPU/ASIC parallelization, a property plain iterated-hashing doesn't provide even at a high round count. "Why does TLS 1.3 need fewer round trips than TLS 1.2?" → because it commits to a small, modern set of algorithm choices upfront rather than negotiating from a large menu, collapsing what used to be a negotiate-then-agree exchange into a single round trip.

### Senior-Level Expectations

Correctly distinguishes all three primitives' guarantees, names the specific algorithm families for each, and can explain why a fast hash is the wrong tool for password storage.

### Staff-Level Discussion

Reasons about cryptographic-control *enforcement* as a systems problem, not just an algorithm-choice problem — as in the production example, the algorithm was correct but a debug bypass undermined it in practice. Considers key-rotation and cost-parameter-upgrade paths as part of the initial design (per [Secrets Management and Key Rotation](secrets-management-and-key-rotation.md)) rather than an afterthought, and treats "we use TLS" or "we sign our requests" as claims requiring verification of every consuming code path, not self-evidently true statements about the system.

## Interview Questions

### Question 1

**A teammate proposes hashing passwords with SHA-256 plus a random salt per user. What's wrong with this, and what would you recommend instead?**

**Expected answer:** the salt correctly defeats precomputed rainbow-table attacks, but SHA-256 remains fast, so an attacker with the stolen hash database can still brute-force at high speed per password using GPU hardware. Recommend a purpose-built password-hashing function with a tunable, deliberately expensive cost parameter — Argon2id as the current OWASP-recommended default, or PBKDF2 where FIPS compliance is required.

**Common mistakes:** believing the salt alone is sufficient because it does correctly solve the (different) rainbow-table problem.

**Follow-up questions:** "How would you choose the cost parameter?" (tune against measured wall-clock time on production-representative hardware, balancing brute-force resistance against login-endpoint latency and CPU cost at real traffic volume.)

**Senior-level expectations:** correctly identifies the speed problem as distinct from the salt's actual (correct) purpose.

**Staff-level expectations:** proposes a concrete migration path for existing SHA-256-hashed passwords (opportunistic re-hash at next successful login, since the plaintext isn't otherwise recoverable) and a cost-parameter-tuning methodology.

### Question 2

**Your team signs API requests between two internal services. A security review asks: "how do you know the signature is actually being verified, not just present?" How would you answer that, concretely?**

**Expected answer:** the presence of a signature in a request proves nothing by itself — the receiving service's code must actually call signature verification on every code path that processes the request, reject on failure, and this needs to be confirmed (ideally via an automated test that submits a request with a deliberately invalid signature and asserts rejection) rather than assumed from the scheme's design alone.

**Common mistakes:** treating "we sign our requests" as itself sufficient evidence of security without describing how enforcement is verified.

**Follow-up questions:** "What's a realistic way this verification step gets silently bypassed in practice?" (a debug/testing flag, an internal-caller exception path, a newly-added code path that reuses request-parsing logic but skips the verification middleware.)

**Senior-level expectations:** correctly separates "signature exists in the protocol" from "verification is enforced in code."

**Staff-level expectations:** proposes a concrete test or monitoring mechanism to continuously verify enforcement, not just a one-time code review.

## Summary

Hashing, signing, and TLS answer three genuinely different questions and should never be treated as interchangeable "crypto." Password hashing must be deliberately slow (PBKDF2/bcrypt/Argon2), unlike every other performance goal in this handbook. Signing proves authenticity and integrity without confidentiality. TLS 1.3 provides transport-level confidentiality and integrity for the duration of a connection, using a deliberately reduced, modern algorithm set that removes most of the historical negotiation-based vulnerability surface. All three were demonstrated with real, measured evidence: adaptive-cost PBKDF2 hashing, EC signature tamper-detection failing on a single changed byte, and a real TLS 1.3 handshake showing both a fixed modern cipher suite and a hybrid post-quantum key-exchange group already negotiated by default.

## Key Takeaways

- Password hashing must be slow on purpose (PBKDF2/bcrypt/Argon2) — a fast general-purpose hash like SHA-256, even salted, is the wrong tool.
- Signing proves authenticity and integrity, never confidentiality — a signed payload is fully readable.
- TLS protects data only while in transit, for the duration of a specific connection — not data at rest, and not after the connection ends.
- TLS 1.3's reduced, fixed algorithm set is itself a security improvement, removing the negotiation-based attack surface that caused several TLS 1.2-era vulnerabilities.
- A cryptographic control's presence in a system's design doesn't guarantee it's enforced on every code path — enforcement needs its own verification (tests, monitoring), not just correct algorithm choice.

## Cheat Sheet

| Primitive | Answers | Correct tools | Wrong tool trap |
|---|---|---|---|
| Password hashing | Does this match a stored secret? | Argon2id, PBKDF2, bcrypt, scrypt | Plain SHA-256/MD5, even salted |
| Signing | Did the claimed party produce this exact content? | ECDSA, EdDSA, RSA | Assuming signed = encrypted/confidential |
| TLS | Can two parties establish a private channel now? | TLS 1.3, current library defaults | Custom "secure channel" implementations |

## Flashcards

**Q: Why is a fast hash like SHA-256 the wrong tool for password storage, even with a per-user salt?**
A: The salt defeats rainbow tables, but SHA-256's speed lets an attacker brute-force at high speed per password on GPU hardware — password hashing needs a deliberately expensive, tunable-cost function instead.

**Q: Does a digital signature provide confidentiality?**
A: No — it proves authenticity and integrity only; signed content remains fully readable.

**Q: Why did TLS 1.3 remove support for many legacy cipher suites and key-exchange modes?**
A: Those legacy options enabled negotiation-based attacks (like downgrade attacks); a smaller, fixed modern set removes that attack surface and also collapses the handshake to one round trip.

## Practice Exercises

1. Reproduce `PasswordHashingCostDemo.java` at your own iteration counts, each in a separate JVM invocation, and confirm the fixed-JVM-startup-cost baseline plus marginal-cost pattern described in this chapter.
2. Reproduce `SignatureTamperDemo.java` and change only the message's whitespace (not its semantic content) between signing and verifying — confirm verification still fails, illustrating that signatures are computed over exact bytes, not logical equivalence.

## Solutions

1. The `iterations=1` run isolates JVM startup cost; subtracting it from higher-iteration runs gives the marginal PBKDF2 cost, which should increase with iteration count though not perfectly linearly, due to JIT warmup effects across the measurement.
2. Verification fails even for a whitespace-only change, since `Signature.update()` consumes the exact byte sequence provided — this is why signed-payload systems must fix an exact, canonical serialization (whitespace, key ordering, number formatting) before signing, not just the "logical" content.

## Additional Reading

- [NIST SP 800-63B: Digital Identity Guidelines — Authentication and Lifecycle Management](https://csrc.nist.gov/pubs/sp/800/63/b/upd2/final)

## Official References

- [RFC 8446 — The Transport Layer Security (TLS) Protocol Version 1.3](https://www.rfc-editor.org/rfc/rfc8446)
