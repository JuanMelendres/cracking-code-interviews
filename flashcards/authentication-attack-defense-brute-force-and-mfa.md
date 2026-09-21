---
title: "Flashcards: Authentication Attack Defense"
slug: authentication-attack-defense-brute-force-and-mfa
document_type: flashcard-deck
domain: 12-security
topic_id: T-1310
canonical: ../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md
last_updated: 2026-09-21
---

# Flashcards: Authentication Attack Defense

**Canonical chapter:** [`syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md`](../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md)

## Card: Why account lockout doesn't stop credential stuffing

**Prompt:**
Your service has account lockout after 5 failed attempts. Does that stop a credential-stuffing attack?

**Answer:**
No — credential stuffing uses an already-*correct* password stolen from an unrelated breach, so the attacker's login request succeeds on the first try and generates zero failed attempts. Lockout only counts failures, so it structurally cannot detect or stop this attack. Only a second factor (MFA) closes this gap.

**Why it matters:**
The single most important distinction in this topic — a candidate who conflates brute force and credential stuffing gives an incomplete answer.

**Common trap:**
Treating "we have account lockout" as complete authentication attack defense.

**Related:**
[Core Concepts](../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md#core-concepts)

## Card: Account lockout's own denial-of-service trade-off

**Prompt:**
What real demo evidence shows account lockout has a genuine cost, not just a benefit?

**Answer:**
A real demo (`LockingLoginService`, 5 attempts/10s window/3s lockout) shows: while an attacker's failed guesses lock out `alice`'s account, `alice` herself, logging in with her correct real password, is rejected too (`rejected, still locked for 2998ms`) — because lockout state is keyed by the *attempted* username, not the attacker's source. An attacker can deliberately lock out a known target with no intent to succeed.

**Why it matters:**
Shows lockout is a real trade-off, not a free win — a Staff-level framing point.

**Common trap:**
Presenting account lockout as a strictly positive control with no downside.

**Related:**
[Internal Implementation](../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md#internal-implementation)

## Card: TOTP is deterministic, not random

**Prompt:**
Is a TOTP-based MFA code random? What did a real implementation prove?

**Answer:**
No — TOTP is a deterministic function of a shared secret (set once at enrollment) and the current 30-second time window: `code = HOTP(secret, floor(unixTime / 30))`, per RFC 6238/RFC 4226. A from-scratch Java implementation was verified against all 5 of RFC 6238 Appendix B's own official test vectors, all passing (e.g. `T=59 -> 94287082`).

**Why it matters:**
A common misconception; understanding it correctly explains why enrollment (the one moment the secret transits) is the security-critical step, not authentication itself.

**Common trap:**
Describing MFA codes as randomly generated or server-pushed rather than independently computed from a shared secret.

**Related:**
[Internal Implementation](../syllabus/12-security/authentication-attack-defense-brute-force-and-mfa.md#internal-implementation)
