---
title: "Interview Question Bank — 12-security"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../12-security/INDEX.md
  - 11-system-design.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Security

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 9 chapters yielded 18 deep questions + 28 quick-fire
questions = **46 real questions**. No Junior Fundamentals chapter exists in this
domain. Eight of nine chapters use a plain-bold-question Flashcards format
(`**Q: ...** / A: ...`) rather than the `### Card:` template; only
`oauth2-oidc-and-jwt.md` uses the standard template. Eight of nine deep-question
chapters also lack an explicit "Minimum acceptable answer" tier — Junior/Mid below is
honestly derived from each question's own "Common mistakes" field (the mistake a
Junior/Mid candidate actually makes), matching the same derivation method used for
[`03-data-structures-algorithms.md`](03-data-structures-algorithms.md).

---

## Applied Cryptography: Hashing, Signing, and TLS

### Q1 — A teammate proposes hashing passwords with SHA-256 plus a random salt per user. What's wrong with this, and what would you recommend instead?

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/applied-cryptography-hashing-signing-tls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Believes the salt alone is sufficient, since it does correctly solve the (different) rainbow-table problem — the common mistake this question targets.
- **Senior:** Correctly identifies the speed problem as distinct from the salt's actual purpose — SHA-256 remains fast, so a stolen hash database can still be brute-forced at high speed per password on GPU hardware.
- **Staff:** Proposes a concrete migration path for existing SHA-256-hashed passwords (opportunistic re-hash at next successful login) and a cost-parameter-tuning methodology for the replacement (Argon2id or PBKDF2).

### Q2 — A security review asks: "how do you know the signature is actually being verified, not just present?" How would you answer that, concretely?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/applied-cryptography-hashing-signing-tls.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "we sign our requests" as itself sufficient evidence of security, without describing how enforcement is verified — the common mistake this question targets.
- **Senior:** Correctly separates "signature exists in the protocol" from "verification is enforced in code" on every code path that processes the request.
- **Staff:** Proposes a concrete test or monitoring mechanism to continuously verify enforcement, not just a one-time code review.

---

## AuthN vs AuthZ, RBAC vs ABAC

### Q1 — A system currently uses RBAC. A new requirement states: "a manager may approve a purchase request only if the requester reports to them." How would you implement this?

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/authn-authz-rbac-vs-abac.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes new, narrower roles as the fix — the common mistake this question targets.
- **Senior:** Correctly identifies this as an attribute/relationship-based rule RBAC cannot express, since no role can encode a per-instance relationship.
- **Staff:** Proposes where in the request lifecycle the check should run (service layer, not gateway) and what attributes/data it needs, including how the manager-report relationship should be sourced reliably.

### Q2 — What's the practical difference between a 401 and a 403 HTTP response, and why does the distinction matter beyond picking the "correct" status code?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/authn-authz-rbac-vs-abac.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the two as interchangeable "access denied" responses — the common mistake this question targets.
- **Senior:** Correctly distinguishes the two codes and their client-facing implications — 401 means re-authenticate, 403 means a different identity or permission is needed.
- **Staff:** Raises the 403-vs-404 information-disclosure consideration unprompted, for resources whose mere existence is itself sensitive.

---

## CSRF, CORS, and Session Security

### Q1 — A teammate says: "Our API has a strict CORS policy... so we're protected against CSRF." Evaluate this claim.

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/csrf-cors-and-session-security.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Accepts the CORS-as-CSRF-protection claim without probing whether the attack actually requires reading a response — the common mistake this question targets.
- **Senior:** Correctly identifies that CORS and CSRF govern different parts of the request lifecycle — CORS governs whether a response can be *read*, not whether a cookie-authenticated request gets *sent* at all.
- **Staff:** Proposes the concrete, correct defense (a synchronizer token, applied consistently across every state-changing endpoint) rather than just identifying the misconception.

### Q2 — Walk through what specifically goes wrong in a session-fixation attack, and what one server-side rule closes it completely.

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/csrf-cors-and-session-security.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes a stronger or longer session identifier as the fix — the common mistake this question targets, irrelevant since the attacker supplies the identifier themselves.
- **Senior:** Correctly identifies session-ID regeneration at authentication as the fix, and explains why identifier strength is irrelevant to this specific attack.
- **Staff:** Generalizes the fix to every authentication-reaching code path in a real system (password reset, SSO callbacks, "remember me" exchanges), not just the obvious primary login form.

---

## Injection, Input Validation, and Output Encoding

### Q1 — A junior engineer says: "We're safe from SQL injection because we validate that the username field only contains alphanumeric characters." Evaluate this claim.

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/injection-input-validation-output-encoding.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Accepts the validation-only claim as sufficient without probing whether every other field and query is equally protected — the common mistake this question targets.
- **Senior:** Correctly identifies that field-level validation is necessary-adjacent but not sufficient, since it depends on every field in every query being equally strict.
- **Staff:** Proposes parameterization as the actual guarantee and explains why it doesn't depend on the specific character content of the input at all, unlike validation.

### Q2 — Explain, mechanically, why `SELECT * FROM users WHERE username = 'admin' --' AND password_hash = 'anything'` grants access without a valid password.

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/injection-input-validation-output-encoding.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the attack vaguely ("it breaks out of the string") without identifying the specific comment-syntax mechanism — the common mistake this question targets.
- **Senior:** Correctly explains the comment-syntax mechanism precisely — `--` is PostgreSQL's line-comment syntax, so the password-check clause is discarded before evaluation.
- **Staff:** Generalizes the underlying principle (string-literal closure plus syntax truncation) beyond the specific `--` sequence shown, to other databases' comment syntaxes.

---

## Multi-Tenancy Isolation Models

### Q1 — A team says: "We've enabled Row-Level Security on our multi-tenant tables, so we're protected against cross-tenant data leaks." What follow-up question would you ask, and why?

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/multi-tenancy-isolation-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Accepts "RLS is enabled" as sufficient without probing which roles are exempt from it — the common mistake this question targets.
- **Senior:** Correctly identifies the superuser/`BYPASSRLS` exemption as the specific gap to probe — RLS provides zero protection for any exempt connection, regardless of how correctly the policy is defined.
- **Staff:** Proposes a structural, ongoing audit process (provisioning policy requiring justification for any `BYPASSRLS` grant) rather than a one-time check.

### Q2 — Why might a company migrate its largest enterprise customers from a shared pool model to dedicated silo infrastructure, even though the pool model with RLS provides real, measured isolation?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/multi-tenancy-isolation-models.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats strong logical isolation (RLS) as equivalent to the physical isolation some compliance requirements specifically demand — the common mistake this question targets.
- **Senior:** Correctly distinguishes logical isolation from physical/infrastructural isolation — a shared database's outage or engine vulnerability still affects every tenant sharing it.
- **Staff:** Proposes the bridge/hybrid model (silo isolation only for the specific tenants that require it) as the pragmatic middle ground rather than an all-or-nothing platform-wide choice.

---

## OAuth2, OIDC, and JWT

### Q1 — Explain JWT revocation honestly.

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/oauth2-oidc-and-jwt.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims JWTs "can just be revoked," implying revocation is free — the common mistake this question targets.
- **Senior:** Correctly states JWTs can't be revoked without extra machinery — a valid, non-expired JWT requires a stateful deny-list check, which undermines the statelessness that motivated using a JWT.
- **Staff:** Names both mitigations (short expiry + refresh tokens, or accepting the deny-list cost) and is explicit that a deny-list reintroduces the exact stateful-lookup cost the token format was chosen to avoid.

### Q2 — Why PKCE if you already have a client secret?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/oauth2-oidc-and-jwt.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats PKCE and a client secret as redundant mechanisms — the common mistake this question targets.
- **Senior:** States that PKCE protects the authorization code from interception specifically, a different attack surface than the client secret's token-exchange impersonation protection.
- **Staff:** Notes that public clients (mobile, SPA) generally can't hold a secret securely at all, making PKCE not just complementary but often the *only* real protection available.

---

## OWASP Top 10 for Backend Services

### Q1 — Walk me through how you'd find an IDOR vulnerability in a code review, given that the vulnerable and fixed code differ by only one check.

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/owasp-top-10-for-backend-services.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes IDOR only in terms of automated scanning rather than the manual review question that actually catches it — the common mistake this question targets.
- **Senior:** Correctly identifies the review question to ask ("what stops a different authenticated user from supplying a different object ID here") and why standard functional testing misses it.
- **Staff:** Proposes a structural fix (a shared authorization-check utility or framework-level enforcement) rather than relying on every reviewer remembering to ask the question every time.

### Q2 — A teammate proposes defending against SSRF by blocking any URL containing "169.254" or "localhost." Is this sufficient?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/owasp-top-10-for-backend-services.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats denylist string-matching as "good enough" without considering resolution-time bypasses — the common mistake this question targets.
- **Senior:** Correctly identifies the denylist as insufficient and names at least one bypass category (alternate IP representations, DNS rebinding, redirects).
- **Staff:** Proposes the full allowlist-plus-resolved-address-plus-redirect-handling defense and can explain why each individual piece is necessary.

---

## Secrets Management and Key Rotation

### Q1 — A team wants to rotate their encryption key today. Walk through what needs to happen for this to be safe, given millions of records already encrypted under the current key.

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/secrets-management-and-key-rotation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes rotation as "generate a new key and start using it" without addressing existing data or a verification-before-retirement step — the common mistake this question targets.
- **Senior:** Correctly describes the envelope-encryption pattern and the re-encrypt-then-retire sequence — new writes switch immediately, a background sweep re-encrypts existing records, and only then is the old key retired.
- **Staff:** Proactively raises the backfill/migration cost for a system not originally designed with key-version tagging, and describes the sweep as a rate-limited, resumable background job.

### Q2 — Why should keys be rotated periodically even if there's no evidence of any compromise?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/secrets-management-and-key-rotation.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats rotation as purely a reactive response to a known or suspected compromise — the common mistake this question targets.
- **Senior:** Names at least two proactive reasons — bounding blast radius, satisfying compliance schedules, reducing the exposure window of a slow, undetected leak.
- **Staff:** Proposes a concrete cadence-selection methodology (the more conservative of compliance requirements and an internally-assessed exposure window) rather than an arbitrary fixed interval.

---

## Supply Chain Security, SBOM, and Dependency Risk

### Q1 — A scanner reports a critical CVE in a package no one on the team has ever written or knowingly imported. How is this possible, and what would you do?

**Canonical treatment:** [§ Interview Questions, Q1](../../12-security/supply-chain-security-sbom-and-dependency-risk.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the scanner made an error, or searches application code for a dependency that doesn't exist there — the common mistake this question targets.
- **Senior:** Correctly identifies the base-image origin of the finding rather than assuming application-code fault — an SBOM surfaces exactly this kind of transitive, bundled dependency.
- **Staff:** Proposes the centralized base-image-ownership remediation model for a shared image across the organization's services.

### Q2 — Your team generates an SBOM once a quarter as part of a compliance audit. Is this sufficient?

**Canonical treatment:** [§ Interview Questions, Q2](../../12-security/supply-chain-security-sbom-and-dependency-risk.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats quarterly generation as adequate because it satisfies the audit requirement that originally motivated it — the common mistake this question targets.
- **Senior:** Correctly identifies quarterly generation as insufficient for continuous risk visibility, since new CVEs are disclosed continuously against already-deployed software.
- **Staff:** Distinguishes build-time scanning from continuous scanning of already-deployed software and explains why both are needed.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Why is a fast hash like SHA-256 the wrong tool for password storage, even with a per-user salt? | [Applied Cryptography](../../12-security/applied-cryptography-hashing-signing-tls.md#flashcards) |
| 2 | Does a digital signature provide confidentiality? | [Applied Cryptography](../../12-security/applied-cryptography-hashing-signing-tls.md#flashcards) |
| 3 | Why did TLS 1.3 remove support for many legacy cipher suites and key-exchange modes? | [Applied Cryptography](../../12-security/applied-cryptography-hashing-signing-tls.md#flashcards) |
| 4 | What does a 401 response mean versus a 403? | [AuthN vs AuthZ, RBAC vs ABAC](../../12-security/authn-authz-rbac-vs-abac.md#flashcards) |
| 5 | Why can't RBAC express "a manager may approve only their own direct report's request"? | [AuthN vs AuthZ, RBAC vs ABAC](../../12-security/authn-authz-rbac-vs-abac.md#flashcards) |
| 6 | What's the practical signal that a system has outgrown RBAC for a specific rule? | [AuthN vs AuthZ, RBAC vs ABAC](../../12-security/authn-authz-rbac-vs-abac.md#flashcards) |
| 7 | Does a strict CORS policy protect against CSRF? | [CSRF, CORS, and Session Security](../../12-security/csrf-cors-and-session-security.md#flashcards) |
| 8 | What's the one rule that completely closes session fixation? | [CSRF, CORS, and Session Security](../../12-security/csrf-cors-and-session-security.md#flashcards) |
| 9 | Why doesn't a stronger, more random session identifier help against fixation? | [CSRF, CORS, and Session Security](../../12-security/csrf-cors-and-session-security.md#flashcards) |
| 10 | Why do prepared statements prevent SQL injection? | [Injection, Input Validation, Output Encoding](../../12-security/injection-input-validation-output-encoding.md#flashcards) |
| 11 | Is input validation alone sufficient to prevent injection? | [Injection, Input Validation, Output Encoding](../../12-security/injection-input-validation-output-encoding.md#flashcards) |
| 12 | Why is a generic "sanitize this string" function a weaker model than context-specific output encoding? | [Injection, Input Validation, Output Encoding](../../12-security/injection-input-validation-output-encoding.md#flashcards) |
| 13 | What's the key weakness of application-level `tenant_id` filtering as the sole isolation mechanism? | [Multi-Tenancy Isolation Models](../../12-security/multi-tenancy-isolation-models.md#flashcards) |
| 14 | Does enabling Row-Level Security guarantee isolation unconditionally? | [Multi-Tenancy Isolation Models](../../12-security/multi-tenancy-isolation-models.md#flashcards) |
| 15 | What does an RLS-protected query return when the tenant-context session variable is unset? | [Multi-Tenancy Isolation Models](../../12-security/multi-tenancy-isolation-models.md#flashcards) |
| 16 | OAuth2 vs. OIDC, in one line each? | [OAuth2, OIDC, and JWT](../../12-security/oauth2-oidc-and-jwt.md#flashcards) |
| 17 | Why PKCE if you already have a client secret? | [OAuth2, OIDC, and JWT](../../12-security/oauth2-oidc-and-jwt.md#flashcards) |
| 18 | Can a valid, non-expired JWT be revoked? | [OAuth2, OIDC, and JWT](../../12-security/oauth2-oidc-and-jwt.md#flashcards) |
| 19 | Two honest JWT-revocation mitigations? | [OAuth2, OIDC, and JWT](../../12-security/oauth2-oidc-and-jwt.md#flashcards) |
| 20 | Is the OWASP Top 10 an exhaustive vulnerability checklist? | [OWASP Top 10 for Backend Services](../../12-security/owasp-top-10-for-backend-services.md#flashcards) |
| 21 | Why does IDOR routinely pass functional testing? | [OWASP Top 10 for Backend Services](../../12-security/owasp-top-10-for-backend-services.md#flashcards) |
| 22 | Why is a denylist insufficient as an SSRF defense? | [OWASP Top 10 for Backend Services](../../12-security/owasp-top-10-for-backend-services.md#flashcards) |
| 23 | Why does naive key rotation (just replace the key) break existing encrypted data? | [Secrets Management and Key Rotation](../../12-security/secrets-management-and-key-rotation.md#flashcards) |
| 24 | What must happen before an old key can be safely deleted after rotation? | [Secrets Management and Key Rotation](../../12-security/secrets-management-and-key-rotation.md#flashcards) |
| 25 | Name two reasons to rotate keys proactively, absent any known compromise. | [Secrets Management and Key Rotation](../../12-security/secrets-management-and-key-rotation.md#flashcards) |
| 26 | Why is reviewing only a project's directly-declared dependencies insufficient for supply-chain risk assessment? | [Supply Chain Security, SBOM, Dependency Risk](../../12-security/supply-chain-security-sbom-and-dependency-risk.md#flashcards) |
| 27 | What did the real docker scout scan against `eclipse-temurin:21-jre` find? | [Supply Chain Security, SBOM, Dependency Risk](../../12-security/supply-chain-security-sbom-and-dependency-risk.md#flashcards) |
| 28 | Why should base-image vulnerability remediation typically be owned centrally rather than per-service? | [Supply Chain Security, SBOM, Dependency Risk](../../12-security/supply-chain-security-sbom-and-dependency-risk.md#flashcards) |

---

## Related

- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
