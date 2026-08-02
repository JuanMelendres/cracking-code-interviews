---
title: "Week 17 Mock — Security Technical Round (45 min)"
week: 17
document_type: study-pack-mock
status: draft
last_reviewed: 2026-08-02
---

# Week 17 Mock — Security Technical Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

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

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Weeks 13–16's mocks — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
