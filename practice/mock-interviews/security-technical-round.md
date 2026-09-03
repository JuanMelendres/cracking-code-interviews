---
title: "Mock Interview: Security Technical Round (45 min)"
slug: security-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - IDOR structural diagnosis and fix
  - Password hashing algorithm selection
  - RBAC-to-ABAC escalation for relationship-based rules
  - SQL injection mechanism
  - Row-Level Security's real scope and gaps
  - Envelope encryption and key rotation
related:
  - ../../syllabus/12-security/owasp-top-10-for-backend-services.md
  - ../../syllabus/12-security/applied-cryptography-hashing-signing-tls.md
  - ../../syllabus/12-security/authn-authz-rbac-vs-abac.md
  - ../../syllabus/12-security/injection-input-validation-output-encoding.md
  - ../../syllabus/12-security/multi-tenancy-isolation-models.md
  - ../../syllabus/12-security/secrets-management-and-key-rotation.md
  - ../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md
source: ../../study-packs/week-17/10-week-17-mock-interview.md
official_references: []
---

# Mock Interview: Security Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-17/10-week-17-mock-interview.md`.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| IDOR structural diagnosis | Q1 | [OWASP Top 10 for Backend Services](../../syllabus/12-security/owasp-top-10-for-backend-services.md) |
| Password hashing selection | Q2 | [Applied Cryptography: Hashing, Signing, and TLS](../../syllabus/12-security/applied-cryptography-hashing-signing-tls.md) |
| RBAC-to-ABAC escalation | Q3 | [AuthN vs AuthZ, RBAC vs ABAC](../../syllabus/12-security/authn-authz-rbac-vs-abac.md) |
| SQL injection mechanism | Q4 | [Injection, Input Validation, and Output Encoding](../../syllabus/12-security/injection-input-validation-output-encoding.md) |
| RLS's real scope | Q5 | [Multi-Tenancy Isolation Models](../../syllabus/12-security/multi-tenancy-isolation-models.md) |
| Envelope encryption / key rotation | Q6 | [Secrets Management and Key Rotation](../../syllabus/12-security/secrets-management-and-key-rotation.md) |
| Cross-topic synthesis | Q7 | All seven of this week's topics, including [Supply Chain Security, SBOM, and Dependency Risk](../../syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md) |

## Interviewer Opening Script

*"This is a 45-minute Security technical round. I'll walk through seven scenarios covering access control, cryptography, authorization models, injection, tenant isolation, and key management — most are diagnostic or code-review-style, one is a whiteboard sketch, and the last is free-form synthesis. Explain your reasoning as you go, and be specific about mechanisms — 'that's insecure' isn't enough, I want to know exactly why. Let's start."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** A code review finds a fetch-by-ID endpoint with no ownership check. Walk through why this passed functional testing and what you'd change structurally, not just in this one endpoint.
2. **(6 min)** A teammate proposes hashing passwords with salted SHA-256. Evaluate this and recommend an alternative.
3. **(6 min)** A new requirement: "a manager may approve a purchase only if the requester reports to them." Your system uses RBAC today. How do you implement this?
4. **(6 min)** Explain, mechanically, why the SQL string built from username `admin' --` grants access without a valid password.
5. **(6 min)** A team says "we've enabled Row-Level Security, so we're protected against cross-tenant leaks." What's your follow-up question?
6. **(6 min, whiteboard)** Sketch the envelope-encryption pattern that makes key rotation practical without breaking existing encrypted data.
7. **(9 min)** Free-form: pick any two of this week's seven topics and explain how they interact in a single real production system (e.g., an SSRF vulnerability that succeeds specifically because an internal service assumed any request reaching it was already authorized).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — IDOR

**Ideal answer outline:** IDOR is an absence, not a wrong line — the vulnerable and fixed handlers differ only by one ownership comparison, invisible to functional tests that only ever use the correct owner's credentials. Structural fix: make the ownership check a required part of every object-fetching endpoint's template, not a step to remember.
**Common weak answers:** treating this as a one-off bug fix for the specific endpoint found.
**Pass signal:** correctly explains why functional testing misses this and proposes a structural (not one-off) fix.
**Borderline signal:** fixes the specific endpoint but doesn't generalize to "every fetch-by-ID needs this."
**Fail signal:** can't explain why it passed testing in the first place.

### Question 2 — Password hashing

**Ideal answer outline:** salted SHA-256 defeats rainbow tables but remains fast, so brute-forcing at high speed per password is still feasible on GPU hardware. Recommend Argon2id (current OWASP default) or PBKDF2 where FIPS compliance is required — a deliberately slow, tunable-cost function.
**Common weak answers:** treating the salt alone as sufficient.
**Pass signal:** correctly identifies the speed problem as distinct from what the salt actually solves.
**Borderline signal:** knows "we need a better algorithm" without naming Argon2id/PBKDF2/bcrypt specifically.
**Fail signal:** accepts salted SHA-256 as adequate.

### Question 3 — RBAC can't express a relationship

**Ideal answer outline:** this is a relationship between the specific subject and resource, not a role property — no static role can encode it. Layer an ABAC-style check on top of the existing RBAC role: `hasRole("manager") AND requester.managerId == currentUser.id`.
**Common weak answers:** proposing a new, narrower role.
**Pass signal:** identifies this as a relationship RBAC can't express and proposes attribute-based logic.
**Borderline signal:** recognizes RBAC is insufficient but can't articulate the ABAC-style fix concretely.
**Fail signal:** proposes more roles as the solution.

### Question 4 — SQL injection mechanism

**Ideal answer outline:** `--` is SQL comment syntax; everything after it, including the password check, is discarded before the query is evaluated. The database executes only `... WHERE username = 'admin'`, matching the real row regardless of the supplied password.
**Common weak answers:** vague description ("it breaks the query") without the comment-syntax mechanism.
**Pass signal:** correctly and precisely explains the comment-truncation mechanism.
**Borderline signal:** knows it's an injection but can't explain why this specific payload works.
**Fail signal:** can't explain the mechanism at all.

### Question 5 — RLS follow-up

**Ideal answer outline:** asks which database role every code path (application, background jobs, analytics tools, admin scripts, migrations) actually connects as, and whether any has superuser or `BYPASSRLS` status — RLS provides zero protection for an exempt role regardless of policy correctness.
**Common weak answers:** accepting "RLS is enabled" as sufficient.
**Pass signal:** names the superuser/`BYPASSRLS` exemption as the specific gap to probe.
**Borderline signal:** senses something might be missing but can't name the specific exemption.
**Fail signal:** treats "RLS enabled" as proof of isolation.

### Question 6 — Whiteboard: envelope encryption

**Ideal answer outline:** draws a key ring with two key versions present simultaneously, two ciphertexts each tagged with the version that encrypted them, both resolving correctly; then draws the old key's removal breaking its tagged record's decryptability, showing why retirement must follow re-encryption.
**Pass signal:** correctly draws and narrates the version-tag mechanism and the retirement-ordering requirement.
**Borderline signal:** draws the key ring but can't explain why per-record tagging specifically is necessary.
**Fail signal:** describes rotation as "just swap the key."

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., a superuser-provisioned analytics tool bypassing RLS being structurally the same category of risk as an SSRF vulnerability bypassing an authorization boundary — both are a trusted-boundary assumption violated by an out-of-band access path) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold as the [Java Core Technical Round](java-core-technical-round.md):

| Score | Meaning |
|---|---|
| 1 | No coherent answer, or a factually wrong one |
| 2 | Names the right topic but no working mechanism |
| 3 | Correct mechanism, Senior-level bar met |
| 4 | Correct mechanism plus one Staff-level extension |
| 5 | Correct mechanism, Staff-level extension, and a real/plausible production connection |

**Pass threshold for this mock:** average score ≥ 3.5 across all seven questions, with no individual score below 2.

## Debrief Guide

Walk the candidate through their own scores question by question, starting with the lowest. Questions 1, 5, and (to a lesser extent) 3 share a structural theme worth naming: each asks the candidate to distinguish "a control exists" from "a control is actually enforced everywhere, for everyone, correctly." A candidate who accepts a surface-level claim ("RLS is enabled," "there's a role check") at face value across multiple questions has a genuine security-skepticism gap, not isolated knowledge gaps — this is exactly the instinct a Staff-level security review depends on.

## Remediation Recommendations

- Any score ≤ 2 on Q1 → re-read [OWASP Top 10 for Backend Services](../../syllabus/12-security/owasp-top-10-for-backend-services.md)'s IDOR material and compare the vulnerable/fixed handler pair line by line.
- Any score ≤ 2 on Q2 → re-read [Applied Cryptography: Hashing, Signing, and TLS](../../syllabus/12-security/applied-cryptography-hashing-signing-tls.md).
- Any score ≤ 2 on Q3 → re-read [AuthN vs AuthZ, RBAC vs ABAC](../../syllabus/12-security/authn-authz-rbac-vs-abac.md)'s role-explosion signal.
- Any score ≤ 2 on Q4 → re-read [Injection, Input Validation, and Output Encoding](../../syllabus/12-security/injection-input-validation-output-encoding.md).
- Any score ≤ 2 on Q5 → re-read [Multi-Tenancy Isolation Models](../../syllabus/12-security/multi-tenancy-isolation-models.md)'s RLS/`BYPASSRLS` material.
- Any score ≤ 2 on Q6 → re-read [Secrets Management and Key Rotation](../../syllabus/12-security/secrets-management-and-key-rotation.md)'s envelope-encryption walkthrough.
- Below the 3.5 pass threshold overall → retake this mock in full after remediation.
