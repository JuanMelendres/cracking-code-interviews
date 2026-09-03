---
title: "Cheat Sheet: OWASP Top 10 for Backend Services"
slug: owasp-top-10-for-backend-services
document_type: cheat-sheet
domain: security
topic_id: T-1301
canonical: ../handbook/security/owasp-top-10-for-backend-services.md
last_updated: 2026-08-05
---

# OWASP Top 10 for Backend Services

**Canonical chapter:** [`syllabus/12-security/owasp-top-10-for-backend-services.md`](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Core Mental Model

Treat the OWASP Top 10 not as ten independent bugs to memorize but as three recurring failure shapes: **(1)** a trust boundary was crossed without a check (broken access control, SSRF, insecure design), **(2)** untrusted data was treated as code or as an unconditionally trusted target (injection, deserialization, some SSRF), and **(3)** a security control existed but was misconfigured, outdated, or silently absent (misconfiguration, vulnerable components, cryptographic failures, auth failures, logging failures). Most real incidents are combinations — an SSRF (shape 2) that succeeds *because* an internal service assumed any request reaching it was already authorized (shape 1).

## Essential Definitions

- **OWASP Top 10** — a periodically updated (~every 3–4 years; 2021 is current) ranked list of the most critical web-application security risk *categories*, from vulnerability data and practitioner survey. A prioritization tool, not an exhaustive checklist.
- **IDOR (A01)** — Insecure Direct Object Reference: fetching an object by ID with no ownership check. An *absence*, not a visibly wrong line.
- **A04, Insecure Design** — a category about a *missing* control, not a broken one; a design-review finding, not a code-review finding.
- **SSRF (A10)** — server-side request forgery: the server fetches a URL an attacker influenced, potentially reaching internal/cloud-metadata targets. Newest addition (2021), tied to cloud metadata endpoints turning "fetched a URL" into credential theft.

## Decision Table

| Category | One-line risk | Primary defense | Deep-dive |
|---|---|---|---|
| A01 Broken Access Control | Object-level authorization check missing | Explicit ownership/permission check on every fetch | This chapter + `authn-authz-rbac-vs-abac.md` |
| A02 Cryptographic Failures | Weak/absent crypto for data at rest or in transit | Modern algorithms, correct key handling | `applied-cryptography-hashing-signing-tls.md` |
| A03 Injection | Untrusted data parsed as code/syntax | Parameterized queries, output encoding | `injection-input-validation-output-encoding.md` |
| A04 Insecure Design | Control never designed in | Threat modeling before implementation | This chapter |
| A05 Security Misconfiguration | Insecure default left enabled | Explicit prod-vs-dev config review | This chapter |
| A06 Vulnerable/Outdated Components | Known-vulnerable dependency in use | SBOM + dependency scanning | `supply-chain-security-sbom-and-dependency-risk.md` |
| A07 Identification/Auth Failures | Weak auth flow or session handling | Standard OAuth2/OIDC/JWT patterns | `oauth2-oidc-and-jwt.md` |
| A08 Software/Data Integrity Failures | Unsigned/unverified code or data | Signing, verified pipelines | `applied-cryptography-hashing-signing-tls.md` |
| A09 Logging/Monitoring Failures | Attack undetected due to insufficient logging | Security-event logging as first-class category | This chapter |
| A10 SSRF | Server-side fetch reaches unintended target | Allowlist on **resolved** destination | This chapter |

## Key Numbers (real, executed — `IdorDemo.java`, `SsrfDemo.java`)

IDOR — vulnerable and fixed handlers share the exact same data-access line; the entire vulnerability is one missing comparison:

```
VULNERABLE: bob requests alice's invoice 101 -> Result: Invoice[...] <-- leaked
FIXED:      bob requests alice's invoice 101 -> Blocked: not the owner
FIXED:      alice requests her own invoice   -> Result: Invoice[...] <-- allowed
```

SSRF — a "URL preview" service fetching an internal metadata-style endpoint:

```
VULNERABLE preview, attacker-supplied internal URL -> Leaked: AKIA-DEMO-... SecretAccessKey=...
FIXED preview, same internal URL -> Blocked: target host:port not in allowlist
```

The fix is a strict **allowlist** checked against the *resolved* target — not a denylist of known-bad hosts, and not a string-pattern check (bypassable via redirects, DNS rebinding, alternate IP representations of loopback).

## Common Pitfalls

- Reciting the ten category names without a concrete code-level example for the top few.
- Treating "we have a WAF" as covering A03 — a valuable additional layer, not a substitute for parameterized queries and output encoding at the source.
- Missing that A10 (SSRF) applies to *any* server-side URL fetch — webhooks, PDF generators, image proxies are all SSRF-shaped, not just an obvious "URL parameter" feature.
- Assuming IDOR requires a scanner to find — routinely found by manually changing an ID in a request and observing whether authorization is enforced.

## Interview Answer Skeleton

**30-sec:** The Top 10 is a prioritization/scoping tool for security review, not an exhaustive checklist — real risk includes business-logic flaws the list doesn't name. It's used as a routing question: name a category, then produce a concrete code-level example and the layer the fix belongs to (input boundary, authz layer, output boundary, dependency pipeline).

**2-min:** Add why it exists (focus limited review time on highest-prevalence/impact categories) + how it works (each category is a *shape*, not a specific bug) + the IDOR example (works perfectly on the happy-path test, only fails when a different user's ID is substituted — invisible to functional testing that only tests correct credentials).

**Whiteboard:** Three columns — "Trust boundary crossed" (A01/A10), "Data treated as code/target" (A03/A08), "Control missing or broken" (A02/A05/A06/A07/A09). Circle A04 outside all three, labeled "design-level absence, not implementation defect."

**Staff-level framing:** treat the Top 10 as scope, not completion criteria. A04 and A09 findings typically indicate a process gap (no threat modeling; no security-event logging standard), not a single fixable bug — propose the process change alongside the immediate fix.

## Production Warning Signs

- Verbose stack traces (internal class names, file paths, SQL fragments) in production error responses — A05, a framework's dev error page left enabled; fix is configuration, requiring someone to have explicitly verified prod config differs from dev defaults.
- A credential-stuffing attack against login runs undetected for weeks — A09; failed-auth attempts weren't logged with enough context (source IP, username, timestamp) to distinguish a mistyped password from an automated attack — a missing decision, not a missing feature.
- **Prevention:** default every object-fetching endpoint to requiring an explicit authorization check as part of its implementation template, and every server-side URL-fetch feature to allowlist validation as shared middleware — structural requirements, not add-ons to remember.

## Related

- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
- `syllabus/12-security/injection-input-validation-output-encoding.md`
- `production-cookbook/credential-stuffing-undetected-from-missing-security-event-logging.md`
