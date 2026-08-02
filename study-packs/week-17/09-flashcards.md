---
title: "Week 17 Flashcards — Security Domain Closure"
week: 17
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-08-02
---

# Week 17 Flashcards — Security Domain Closure

21 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** Is the OWASP Top 10 an exhaustive vulnerability checklist?
**Answer:** No — it's a prioritization tool from vulnerability data and practitioner survey; real risk can include business-logic flaws the list doesn't name.
**Why it matters:** Treating it as complete coverage misses a service's actual highest-risk exposure.
**Common trap:** "We checked all ten boxes" as a substitute for reasoning about the specific service.
**Related:** `01-owasp-top-10-for-backend-services.md`

## Card 2

**Prompt:** Why does IDOR routinely pass functional testing?
**Answer:** Functional tests almost always use the correct owner's credentials; the vulnerability only appears when a *different* user's object ID is supplied.
**Why it matters:** Explains why IDOR survives code review and QA so often.
**Common trap:** Assuming a feature that "passed testing" has no access-control gap.
**Related:** `01-owasp-top-10-for-backend-services.md`

## Card 3

**Prompt:** Why is a denylist insufficient as an SSRF defense?
**Answer:** Bypassable via alternate address representations, DNS rebinding, and redirects — an allowlist validated against the resolved destination is required.
**Why it matters:** A common "good enough" mitigation that isn't.
**Common trap:** Blocking known-bad string patterns like "localhost" or "169.254" and considering it solved.
**Related:** `01-owasp-top-10-for-backend-services.md`

## Card 4

**Prompt:** Why is a fast hash like SHA-256 the wrong tool for password storage, even salted?
**Answer:** The salt defeats rainbow tables, but SHA-256's speed lets an attacker brute-force at high speed per password on GPU hardware.
**Why it matters:** One of the most common real security-review findings.
**Common trap:** Believing "salted" alone means "safe."
**Related:** `02-applied-cryptography-hashing-signing-tls.md`

## Card 5

**Prompt:** Does a digital signature provide confidentiality?
**Answer:** No — it proves authenticity and integrity only; signed content remains fully readable.
**Why it matters:** A common vocabulary trip-up under interview pressure.
**Common trap:** Conflating "signed" with "encrypted."
**Related:** `02-applied-cryptography-hashing-signing-tls.md`

## Card 6

**Prompt:** Why did TLS 1.3 remove support for many legacy cipher suites?
**Answer:** Those options enabled negotiation-based downgrade attacks; a smaller fixed set removes that surface and collapses the handshake to one round trip.
**Why it matters:** TLS 1.3's simplicity is itself a security property, not just a speed improvement.
**Common trap:** Describing TLS 1.3 as "just faster TLS 1.2."
**Related:** `02-applied-cryptography-hashing-signing-tls.md`

## Card 7

**Prompt:** What does a 401 response mean versus a 403?
**Answer:** 401 = not authenticated (re-authenticate); 403 = authenticated but not authorized (re-authentication won't help).
**Why it matters:** Conflating them misleads clients into the wrong remediation.
**Common trap:** Treating both as interchangeable "access denied."
**Related:** `03-authn-authz-rbac-vs-abac.md`

## Card 8

**Prompt:** Why can't RBAC express "a manager may approve only their own direct report's request"?
**Answer:** It's a relationship between the specific subject and resource, not a static role property — no fixed role can encode a per-instance relationship.
**Why it matters:** The core reason ABAC exists as a distinct model.
**Common trap:** Proposing an ever-narrower role as the fix.
**Related:** `03-authn-authz-rbac-vs-abac.md`

## Card 9

**Prompt:** What's the practical signal a system has outgrown RBAC for a specific rule?
**Answer:** Needing a new, narrower role for every combination of conditions — "role explosion."
**Why it matters:** A concrete, recognizable trigger for reaching for ABAC.
**Common trap:** Adding roles indefinitely instead of recognizing the structural limit.
**Related:** `03-authn-authz-rbac-vs-abac.md`

## Card 10

**Prompt:** Why do prepared statements prevent SQL injection?
**Answer:** They send query structure and parameter values as separate protocol messages — the database never re-parses bound values as SQL syntax.
**Why it matters:** The actual mechanism, not "better escaping."
**Common trap:** Describing prepared statements as escaping quotes more thoroughly.
**Related:** `04-injection-input-validation-output-encoding.md`

## Card 11

**Prompt:** Is input validation alone sufficient to prevent injection?
**Answer:** No — it's a necessary early filter but not sufficient; output encoding or parameterization at the point of use is what actually matters.
**Why it matters:** A common false sense of security.
**Common trap:** "We validate the field" treated as a complete defense.
**Related:** `04-injection-input-validation-output-encoding.md`

## Card 12

**Prompt:** Why is a generic "sanitize this string" function weaker than context-specific output encoding?
**Answer:** Different rendering contexts (HTML body, attribute, JS, URL) have different special characters and encoding rules.
**Why it matters:** Explains why the same "sanitized" data can still be exploitable in a different context.
**Common trap:** Reusing HTML-body encoding for a JavaScript or URL context.
**Related:** `04-injection-input-validation-output-encoding.md`

## Card 13

**Prompt:** What's the key weakness of application-level `tenant_id` filtering as the sole isolation mechanism?
**Answer:** It requires every query, in every code path, forever, to correctly apply the filter — one missed instance is a direct cross-tenant leak.
**Why it matters:** Why database-enforced isolation (RLS) is a meaningfully stronger guarantee.
**Common trap:** Treating "we filter by tenant_id" as equivalent to "we have isolation."
**Related:** `05-multi-tenancy-isolation-models.md`

## Card 14

**Prompt:** Does enabling Row-Level Security guarantee isolation unconditionally?
**Answer:** No — database roles with superuser or `BYPASSRLS` status bypass RLS entirely.
**Why it matters:** The single most important caveat about RLS, verified directly in this week's demo.
**Common trap:** "RLS is enabled" treated as sufficient without auditing which roles are exempt.
**Related:** `05-multi-tenancy-isolation-models.md`

## Card 15

**Prompt:** What does an RLS-protected query return when the tenant-context session variable is unset?
**Answer:** Zero rows (fail-closed), not an error and not all rows — `tenant_id = NULL` is never true in SQL's three-valued logic.
**Why it matters:** A deliberate, valuable property, but one that can look like "no data" during debugging.
**Common trap:** Mistaking a missing-context bug for "there's genuinely no data."
**Related:** `05-multi-tenancy-isolation-models.md`

## Card 16

**Prompt:** Why does naive key rotation (just replace the key) break existing encrypted data?
**Answer:** There's no way to know which key protected which record without an explicit tag — envelope encryption's per-record key-version tag solves this.
**Why it matters:** The core mechanism that makes rotation operationally practical.
**Common trap:** Describing rotation as "generate a new key and start using it."
**Related:** `06-secrets-management-and-key-rotation.md`

## Card 17

**Prompt:** What must happen before an old key can be safely deleted after rotation?
**Answer:** A re-encryption sweep of every record still tagged with that version must complete and be verified.
**Why it matters:** Deleting first is a permanent, unrecoverable data-loss event, demonstrated directly this week.
**Common trap:** Treating "rotated" and "old key safe to delete" as the same moment.
**Related:** `06-secrets-management-and-key-rotation.md`

## Card 18

**Prompt:** Name two reasons to rotate keys proactively, absent any known compromise.
**Answer:** Limiting the volume of data protected by a single key, and satisfying compliance-mandated rotation schedules.
**Why it matters:** Rotation isn't purely reactive to a suspected breach.
**Common trap:** Treating rotation as something you only do after an incident.
**Related:** `06-secrets-management-and-key-rotation.md`

## Card 19

**Prompt:** Why is reviewing only a project's directly-declared dependencies insufficient for supply-chain risk assessment?
**Answer:** Most real vulnerabilities live in transitive dependencies or base-image-level packages, both invisible to a direct-dependency-only review.
**Why it matters:** The core reason SBOM depth matters.
**Common trap:** "We reviewed our `pom.xml`/`package.json`" treated as a complete review.
**Related:** `07-supply-chain-security-sbom-and-dependency-risk.md`

## Card 20

**Prompt:** What did the real docker scout scan against `eclipse-temurin:21-jre` find?
**Answer:** 213 packages in the SBOM; 13 vulnerabilities including one CRITICAL CVE in a transitive Go package unrelated to any application code.
**Why it matters:** Concrete proof that base-image inheritance is a real, unavoidable attack-surface source.
**Common trap:** Assuming a vulnerability in an unfamiliar package name means the scanner made a mistake.
**Related:** `07-supply-chain-security-sbom-and-dependency-risk.md`

## Card 21

**Prompt:** Why should base-image vulnerability remediation typically be owned centrally, not per-service?
**Answer:** Many services often share the same base image and therefore the identical vulnerability simultaneously — one coordinated fix beats dozens of duplicated efforts.
**Why it matters:** A recurring, cost-effective remediation pattern at organizational scale.
**Common trap:** Each service team independently rediscovering and fixing the same shared-base-image finding.
**Related:** `07-supply-chain-security-sbom-and-dependency-risk.md`
