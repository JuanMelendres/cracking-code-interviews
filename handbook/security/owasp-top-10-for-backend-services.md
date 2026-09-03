---
title: "OWASP Top 10 for Backend Services"
slug: owasp-top-10-for-backend-services
document_type: handbook-chapter
domain: security
status: draft
version: 1.0
last_reviewed: 2026-08-02
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites: []
related:
  - authn-authz-rbac-vs-abac.md
  - applied-cryptography-hashing-signing-tls.md
  - injection-input-validation-output-encoding.md
  - secrets-management-and-key-rotation.md
  - supply-chain-security-sbom-and-dependency-risk.md
  - multi-tenancy-isolation-models.md
  - ../../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md
  - oauth2-oidc-and-jwt.md
  - ../spring/security-filter-chain.md
  - ../../study-packs/week-17/01-owasp-top-10-for-backend-services.md
official_references:
  - https://owasp.org/Top10/
---

# OWASP Top 10 for Backend Services

> **Topic register:** T-1301 (OWASP Top 10 for backend services, IWI 6.35) · Core tier · High interview frequency [H]

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

By the end of this chapter you can name all ten OWASP Top 10 (2021) categories, explain which ones this handbook covers as their own deep-dive chapter versus which are covered only here, and reproduce two real Java demonstrations — an Insecure Direct Object Reference (IDOR, A01) and a Server-Side Request Forgery (SSRF, A10) — showing the exact code-level difference between a vulnerable handler and a fixed one.

## Why This Matters in Interviews

The OWASP Top 10 question rarely means "recite the list." Interviewers use it as a routing question: they want to see whether a candidate can take a category name and immediately produce a concrete, code-level example of how it manifests in a typical backend service, then explain the fix in terms of *where* the defense belongs (input boundary, authorization layer, output boundary, dependency pipeline). A candidate who says "SQL injection is bad, use prepared statements" gets partial credit; a candidate who says "injection is any case where untrusted data crosses into an interpreter's syntax rather than staying data — SQL is the classic case, but so is a shell command built from user input, or an LDAP filter, or a Server-Side Request Forgery where a URL itself is the interpreter's *target* rather than its filter" demonstrates the transferable mental model interviewers are actually screening for.

## Mental Model

Treat the OWASP Top 10 not as ten independent bugs to memorize but as three recurring failure shapes, each showing up in multiple categories: **(1) a trust boundary was crossed without a check** (broken access control, SSRF, insecure design), **(2) untrusted data was treated as code or as an unconditionally-trusted target** (injection, deserialization, some SSRF), and **(3) a security control existed but was misconfigured, outdated, or silently absent** (security misconfiguration, vulnerable/outdated components, cryptographic failures, identification/authentication failures, logging/monitoring failures). Most real incidents are combinations — an SSRF (shape 2) that succeeds *because* an internal service assumed any request reaching it was already authorized (shape 1).

## Definition and Purpose

The **OWASP Top 10** is a periodically-updated (roughly every 3–4 years; 2021 is the current published edition as of this writing) ranked list of the most critical web-application security risk *categories*, maintained by the Open Web Application Security Project from a combination of large-scale vulnerability-data contributions and an industry practitioner survey. It exists as a prioritization tool, not an exhaustive checklist — its purpose is to focus limited security review time on the categories most likely to matter for a typical backend service, in rough order of prevalence and impact.

## Core Concepts

### The 2021 list, and where each category lives in this handbook

| # | Category | Deep-dive location |
|---|---|---|
| A01 | Broken Access Control | This chapter (IDOR demo below); [AuthN/AuthZ, RBAC vs ABAC](authn-authz-rbac-vs-abac.md) for the authorization-model layer |
| A02 | Cryptographic Failures | [Applied Cryptography](applied-cryptography-hashing-signing-tls.md) |
| A03 | Injection | [Injection, Input Validation, Output Encoding](injection-input-validation-output-encoding.md) |
| A04 | Insecure Design | This chapter — a design-level, not implementation-level, category (see below) |
| A05 | Security Misconfiguration | This chapter — cross-cutting; see Production Scenarios |
| A06 | Vulnerable and Outdated Components | [Supply Chain Security, SBOM, and Dependency Risk](supply-chain-security-sbom-and-dependency-risk.md) |
| A07 | Identification and Authentication Failures | [OAuth2, OIDC, and JWT](oauth2-oidc-and-jwt.md); [AuthN/AuthZ](authn-authz-rbac-vs-abac.md) |
| A08 | Software and Data Integrity Failures | [Applied Cryptography](applied-cryptography-hashing-signing-tls.md) (signing); [Supply Chain Security](supply-chain-security-sbom-and-dependency-risk.md) (pipeline integrity) |
| A09 | Security Logging and Monitoring Failures | This chapter — cross-cutting; see Production Scenarios |
| A10 | Server-Side Request Forgery (SSRF) | This chapter (SSRF demo below) |

This chapter is deliberately the *survey* chapter: it owns the categories that don't already have a natural home elsewhere in the register (A01's IDOR angle, A04, A05, A09, A10) and routes everything else to its canonical chapter — per this repository's ownership model, the full explanation lives in exactly one place.

### A04, Insecure Design, is a category about missing controls, not broken ones

Every other category in the list describes an implementation defect in a control that exists. A04 is different: it describes the *absence* of a needed control from the design itself — no amount of careful coding fixes a design that never considered, say, rate-limiting a password-reset endpoint, or that trusted a client-supplied price field. This is why A04 is frequently a Staff-level interview thread: it's a design-review finding, not a code-review finding, and catching it requires threat-modeling before implementation, not testing after.

### A10, SSRF, is the newest addition and the one most tied to cloud infrastructure

SSRF entered the Top 10 for the first time in the 2021 edition specifically because cloud metadata endpoints (a well-known example pattern being a link-local address serving instance credentials) turned a previously low-impact bug class ("the server fetched a URL I gave it") into a credential-theft primitive. Any backend feature that fetches a user-influenced URL server-side — webhooks, URL previews, PDF-from-URL generators, image proxies — is a candidate.

## Internal Implementation

**Real IDOR demonstration** (`practice/java/week-17/owasp-top-10/src/IdorDemo.java`) — a vulnerable handler fetches an object by ID with no ownership check; the fixed handler enforces it:

```
=== VULNERABLE handler: bob requests alice's invoice 101 ===
Result: Invoice[id=101, ownerUserId=alice, amountUsd=4200.0]  <-- bob just read alice's $4,200 invoice

=== FIXED handler: bob requests alice's invoice 101 ===
Blocked: requester 'bob' is not the owner of invoice 101

=== FIXED handler: alice requests her own invoice 101 ===
Result: Invoice[id=101, ownerUserId=alice, amountUsd=4200.0]  <-- legitimate owner, allowed
```

The vulnerable and fixed handlers share the exact same in-memory data access line (`DB.get(invoiceId)`) — the entire vulnerability is the *absence* of one ownership comparison after the fetch, which is precisely why IDOR is so easy to introduce (the "happy path" code works perfectly) and so easy to miss in review (there's no obviously-wrong line, only a missing one).

**Real SSRF demonstration** (`practice/java/week-17/owasp-top-10/src/SsrfDemo.java`) — two local HTTP servers stand in for a legitimate public target and an internal metadata-style endpoint; a "URL preview" service fetches whatever URL it's given:

```
=== VULNERABLE preview service: legitimate request ===
<binary image bytes>

=== VULNERABLE preview service: attacker-supplied internal URL ===
Leaked: AKIA-DEMO-NOT-REAL SecretAccessKey=demo-secret-value-not-real

=== FIXED preview service: same attacker-supplied internal URL ===
Blocked: target host:port not in allowlist: 127.0.0.1:15601
```

The fixed version's defense is a strict **allowlist** of permitted destination hosts, checked against the *resolved* target after parsing the URL — not a denylist of "known-bad" hosts, and not a check on the URL string's syntax alone. Denylists for SSRF are notoriously bypassable (redirects, DNS rebinding, alternate IP representations of loopback addresses); an allowlist of legitimate external destinations is the only defense that doesn't require anticipating every attacker encoding trick.

## Production Scenarios

**A05, Security Misconfiguration — a service exposes verbose stack traces in production error responses.** This is one of the most common real-world A05 findings: a framework's default development error page (full stack trace, sometimes including internal class names, file paths, or SQL fragments) is left enabled after deployment. The fix is configuration, not code — disable detailed error pages outside a development profile — but it requires someone to have explicitly verified production configuration differs from development defaults, which is exactly the kind of check that's easy to skip when "it works" is the only acceptance criterion being tested.

**A09, Security Logging and Monitoring Failures — a credential-stuffing attack against a login endpoint runs undetected for weeks.** The application logs successful logins and generic errors, but never logs failed-authentication attempts with enough context (source IP, username attempted, timestamp) to distinguish "a user mistyped their password twice" from "an automated tool is trying 50,000 username/password pairs against this endpoint." The absence isn't a missing feature so much as a missing decision: security-relevant events (auth failures, authorization denials, privilege escalations) need to be logged as a first-class category, separately reviewable from general application logs, with alerting thresholds tuned to the traffic pattern of an actual attack rather than normal usage noise.

## Failure Modes and Debugging

- **Symptom: an endpoint that "worked in testing" leaks another user's data in production.** Check first for a missing object-level authorization check (IDOR) — this is the single most common real-world A01 finding, and it passes functional testing trivially because functional tests almost always test with the "correct" owner's credentials.
- **Symptom: a server-side URL-fetching feature is abused to reach an unexpected internal address.** Confirm whether the fetch target is validated against an allowlist *after* DNS resolution, not just against the URL string — a denylist-based or string-pattern-based check is bypassable via redirects or alternate address representations.
- **Anti-pattern to rule out first when triaging "how did this vulnerability get through code review":** checking whether the vulnerability was even reviewable from the diff alone — IDOR and insecure-design issues are frequently invisible from a code diff because the defect is an *absence*, not a presence, and requires reviewing the feature's authorization model, not just its new lines.

## Trade-offs

Treating the OWASP Top 10 as a compliance checklist ("we checked all ten boxes") is fast but shallow — it produces coverage of the *named* categories without necessarily catching a service's actual highest-risk exposure, which might be a business-logic flaw the list doesn't name at all (the Top 10 covers common technical categories, not every possible flaw). Treating it as the three recurring failure shapes described in this chapter's Mental Model is slower to apply per-review but transfers to vulnerabilities the list doesn't explicitly name.

## Decision Framework

Use the Top 10 as a starting checklist for a security review's *scope*, not its *completion criteria* — for each category, ask "does this service have a feature shaped like this risk" (does it fetch user-influenced URLs server-side? does it deserialize untrusted input? does it expose object IDs that another user could guess or enumerate?) rather than treating "no known CVE in this category" as sufficient. Escalate straight to a design-level review (A04's territory) rather than a code-level fix whenever the finding is "this feature has no control for X" rather than "this feature's control for X has a bug."

## Common Mistakes

- Reciting the ten category names without being able to produce a concrete code-level example for at least the top few.
- Treating "we use a web application firewall" as covering A03 (Injection) — a WAF is a valuable additional layer, not a substitute for parameterized queries and proper output encoding at the source.
- Missing that A10 (SSRF) applies to *any* server-side URL fetch, not just an obvious "URL parameter" feature — webhooks, PDF generators, and image proxies are all SSRF-shaped features that don't look like it at first glance.
- Assuming IDOR requires a security scanner to find — it's routinely found by manually changing an ID in a request and observing whether authorization is actually enforced.

## Anti-Patterns

Relying on "security through obscurity" object identifiers (e.g., long random-looking IDs) as a *substitute* for an object-level authorization check, rather than as defense-in-depth alongside one — an unguessable ID still leaks if a URL is shared, logged, cached, or referenced by another vulnerability (like the SSRF or logging failures described above), and the underlying access-control gap remains exploitable by anyone who does obtain a valid ID through any of those paths.

## Best Practices

Default every object-fetching endpoint to requiring an explicit authorization check as part of its implementation template, rather than treating the check as an add-on to remember — this converts A01 from "a mistake a developer might make" into "a step the framework or code review structurally requires." For any server-side URL-fetching feature, default to an allowlist-based validation of the resolved destination, applied consistently as shared middleware or a shared utility rather than reimplemented ad hoc per feature.

## Interview Answer Framework

### 30-Second Answer

The OWASP Top 10 is a ranked list of the most critical web-application security risk categories, updated periodically by OWASP from vulnerability data and practitioner surveys. It's used as a prioritization and scoping tool for security review, not an exhaustive vulnerability checklist — real backend risk includes business-logic flaws the list doesn't explicitly name.

### 2-Minute Answer

Definition: ten ranked categories of web-application risk, currently the 2021 edition. Why it exists: to focus limited security-review time on the highest-prevalence, highest-impact risk categories rather than an unbounded search space. How it works: each category names a *shape* of failure (broken access control, injection, cryptographic failure, etc.) rather than a specific bug, and a real service is reviewed against "does this feature have this shape of risk" for each category. One trade-off: treating it as a compliance checklist gives coverage of the named categories without necessarily catching a service's actual highest risk, which might be an unnamed business-logic flaw. One production example: an IDOR (A01) where a fetch-by-ID handler works perfectly for its "happy path" test (the correct owner requesting their own object) and only fails when a *different* user's ID is substituted — invisible to functional testing that only ever tests with correct credentials.

### 10-Minute Deep Dive

Cover: the three recurring failure shapes (trust-boundary-crossed-without-check, untrusted-data-treated-as-code-or-target, control-present-but-misconfigured); a walk through the ten 2021 categories and which this handbook covers as its own deep-dive versus which live only here; the real IDOR demonstration showing the vulnerability is a missing comparison, not a wrong one; the real SSRF demonstration showing why allowlists (not denylists) are the correct defense shape; A04 (Insecure Design) as a design-review finding distinct from every other implementation-level category; A09 (logging/monitoring failures) as an often-overlooked category that determines whether an incident is caught in minutes or discovered weeks later.

### Whiteboard Explanation

Draw three columns labeled "Trust boundary crossed," "Data treated as code/target," and "Control missing or broken." Under each, list the OWASP categories that fit (A01/A10 under the first; A03/A08 under the second; A02/A05/A06/A07/A09 under the third, noting several controls-related categories can appear in more than one column depending on the specific incident). Circle A04 outside all three columns, labeled "design-level absence, not implementation-level defect," to show why it gets a different review process.

### Production Example

A URL-preview feature (paste a link, see a thumbnail) is added to an internal chat tool. It works correctly in testing against public URLs. Months later, a routine security review notices the feature will fetch *any* URL server-side, including ones targeting the service's own internal network — a textbook A10/SSRF exposure that had nothing to do with a coding bug in the feature itself, only with the feature's design never having considered that "fetch this URL" is a request the server, not the user, actually executes.

### Trade-offs to Mention

The Top 10 is a prioritization tool calibrated to common web-application risk; it is not calibrated to a specific service's actual highest-risk exposure, which may be a business-logic flaw or a category the list doesn't name at all.

### Common Candidate Mistakes

Reciting category names without a concrete example; conflating "we have a WAF/scanner" with "this category is covered."

### Typical Follow-Up Questions

"Which category would a leaked API key in a public GitHub repo fall under?" → A02 (Cryptographic Failures, if the key itself was mishandled) or A05 (Security Misconfiguration), depending on how it was exposed — a good follow-up answer distinguishes the two. "How would an allowlist-based SSRF defense need to change if the service also needs to support user-supplied *internal* URLs for a legitimate reason?" → the allowlist would need to explicitly include those internal destinations rather than blocking all internal addresses categorically, which is a genuinely harder design problem than a blanket internal/external split.

### Senior-Level Expectations

Names several categories with a correct, concrete code-level example each, and correctly distinguishes categories that are implementation defects from A04 (a design-level absence).

### Staff-Level Discussion

Treats the Top 10 as a starting scope for a review process, not the review's completion criteria; can reason about a service's business-logic-specific risks that the list doesn't name; recognizes that A04 and A09 findings typically indicate a process gap (no threat modeling; no security-event logging standard) rather than a single fixable bug, and proposes the process change alongside the immediate fix.

## Interview Questions

### Question 1

**Walk me through how you'd find an IDOR vulnerability in a code review, given that the vulnerable and fixed code differ by only one check.**

**Expected answer:** IDOR is rarely visible from a diff alone if the diff only shows the new feature's "happy path" — the reviewer needs to explicitly ask "what stops a different, authenticated user from supplying a different object ID here" for every object-fetching endpoint, since the vulnerability is an absence, not a suspicious-looking line.

**Common mistakes:** describing IDOR only in terms of automated scanning rather than the manual review question that actually catches it.

**Follow-up questions:** "Would functional tests catch this?" (No, not unless a test specifically supplies a different user's ID — which functional tests, by default, don't.)

**Senior-level expectations:** correctly identifies the review question to ask and why standard functional testing misses it.

**Staff-level expectations:** proposes a structural fix (e.g., a shared authorization-check utility or framework-level enforcement) rather than relying on every reviewer remembering to ask the question every time.

### Question 2

**A teammate proposes defending against SSRF by blocking any URL containing the string "169.254" or "localhost." Is this sufficient?**

**Expected answer:** no — this is a denylist of known-bad string patterns, which is bypassable via alternate IP representations (decimal, octal, IPv6-mapped forms), DNS rebinding (a hostname that resolves to an internal address at request time despite passing a string check earlier), or redirects from an initially-allowed URL to a blocked one. The correct defense is an allowlist of permitted destination hosts, validated against the *resolved* address, not the URL string.

**Common mistakes:** treating denylist string-matching as "good enough" without considering resolution-time bypasses.

**Follow-up questions:** "What about redirects — does validating the initial URL's host cover that case?" (No — the fetch needs to either disable redirect-following or re-validate the destination after each redirect hop.)

**Senior-level expectations:** correctly identifies the denylist as insufficient and names at least one bypass category.

**Staff-level expectations:** proposes the full allowlist-plus-resolved-address-plus-redirect-handling defense and can explain why each individual piece is necessary.

## Summary

The OWASP Top 10 (2021) is ten ranked categories of web-application security risk, useful as a review-scoping tool rather than an exhaustive checklist. This chapter is the survey entry point for the domain: it fully covers the categories without a natural deep-dive home elsewhere (A01's IDOR angle, A04, A05, A09, A10) with real, working Java demonstrations for IDOR and SSRF, and routes the remaining categories to their dedicated chapters in this handbook.

## Key Takeaways

- The Top 10 is a prioritization tool from vulnerability data and practitioner survey, not an exhaustive vulnerability list.
- Most real incidents fit one of three shapes: trust boundary crossed without a check, untrusted data treated as code or an unconditionally-trusted target, or a control present but misconfigured/outdated/absent from logging.
- IDOR (A01) is an *absence* of an ownership check, not a visibly-wrong line — this is why it survives functional testing and code review so often.
- SSRF (A10) defenses must be allowlist-based and validated against the resolved address, not a denylist of known-bad string patterns.
- A04 (Insecure Design) and A09 (Logging/Monitoring Failures) typically indicate a missing process, not a single fixable code defect.

## Cheat Sheet

| Category | One-line risk | Primary defense | Deep-dive |
|---|---|---|---|
| A01 Broken Access Control | Object-level authorization check missing | Explicit ownership/permission check on every fetch | This chapter + [AuthN/AuthZ](authn-authz-rbac-vs-abac.md) |
| A02 Cryptographic Failures | Weak/absent crypto for data at rest or in transit | Modern algorithms, correct key handling | [Applied Cryptography](applied-cryptography-hashing-signing-tls.md) |
| A03 Injection | Untrusted data parsed as code/syntax | Parameterized queries, output encoding | [Injection](injection-input-validation-output-encoding.md) |
| A04 Insecure Design | Control never designed in | Threat modeling before implementation | This chapter |
| A05 Security Misconfiguration | Insecure default left enabled | Explicit prod-vs-dev config review | This chapter |
| A06 Vulnerable/Outdated Components | Known-vulnerable dependency in use | SBOM + dependency scanning | [Supply Chain](supply-chain-security-sbom-and-dependency-risk.md) |
| A07 Identification/Authentication Failures | Weak auth flow or session handling | Standard OAuth2/OIDC/JWT patterns | [OAuth2/OIDC/JWT](oauth2-oidc-and-jwt.md) |
| A08 Software/Data Integrity Failures | Unsigned/unverified code or data | Signing, verified pipelines | [Applied Cryptography](applied-cryptography-hashing-signing-tls.md) |
| A09 Logging/Monitoring Failures | Attack undetected due to insufficient logging | Security-event logging as first-class category | This chapter |
| A10 SSRF | Server-side fetch reaches unintended target | Allowlist on resolved destination | This chapter |

## Flashcards

**Q: Is the OWASP Top 10 an exhaustive vulnerability checklist?**
A: No — it's a prioritization tool covering the most common/impactful categories; real risk can include business-logic flaws the list doesn't name.

**Q: Why does IDOR routinely pass functional testing?**
A: Because functional tests almost always test with the correct owner's credentials; the vulnerability only appears when a *different* user's object ID is supplied, which standard happy-path tests don't do.

**Q: Why is a denylist insufficient as an SSRF defense?**
A: It's bypassable via alternate address representations, DNS rebinding, and redirects — an allowlist validated against the resolved destination is required instead.

## Practice Exercises

1. Reproduce `IdorDemo.java` and modify it to add a third user role ("support-agent") that should be allowed to read any invoice for support purposes — implement this as an explicit rule, not by removing the ownership check.
2. Reproduce `SsrfDemo.java` and add a redirect step from the allowed public URL to the internal one; confirm whether the fixed version's defense still holds (it does, since it never validates the initial URL, only ever fetches after `HttpClient` follows a redirect and needs an additional redirect-aware check to be fully robust — a good exercise in seeing the limits of a single-hop allowlist check).

## Solutions

1. The correct implementation adds an explicit `requesterRole.equals("support-agent")` OR-condition alongside the ownership check — never a change that weakens or removes the ownership check itself for other roles.
2. `HttpClient.newHttpClient()`'s default redirect policy is `NEVER`, so the demo as written does not follow redirects automatically — but a production HTTP client configured with automatic redirect-following would need the allowlist check re-applied after each redirect hop, not just on the initial URL, to stay robust.

## Additional Reading

- [OWASP Top 10:2021](https://owasp.org/Top10/)
- [Serialization Hazards and Alternatives](../../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md) — the real, Java-specific mechanics (with real, byte-level-tampered reproductions) behind this chapter's "untrusted data treated as code" deserialization risk shape.

## Official References

- [OWASP Top 10](https://owasp.org/Top10/)
