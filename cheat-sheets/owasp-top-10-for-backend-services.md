---
title: "Cheat Sheet: OWASP Top 10 for Backend Services"
slug: owasp-top-10-for-backend-services
document_type: cheat-sheet
domain: security
topic_id: T-1301
canonical: ../syllabus/12-security/owasp-top-10-for-backend-services.md
last_updated: 2026-09-14
---

# OWASP Top 10 for Backend Services

**Canonical chapter:** [`syllabus/12-security/owasp-top-10-for-backend-services.md`](../syllabus/12-security/owasp-top-10-for-backend-services.md)

## Core Mental Model

Treat the OWASP Top 10 not as ten independent bugs to memorize but as three recurring failure shapes: **(1)** a trust boundary was crossed without a check (broken access control, SSRF, insecure design), **(2)** untrusted data was treated as code or as an unconditionally trusted target (injection, deserialization, some SSRF), and **(3)** a security control existed but was misconfigured, outdated, or silently absent (misconfiguration, vulnerable components, cryptographic failures, auth failures, logging failures). Most real incidents are combinations — an SSRF (shape 2) that succeeds *because* an internal service assumed any request reaching it was already authorized (shape 1).

## Essential Definitions

- **OWASP Top 10** — a periodically updated (~every 3–4 years) ranked list of the most critical web-application security risk *categories*, from vulnerability data and practitioner survey. A prioritization tool, not an exhaustive checklist. **The 2025 edition is current**, superseding 2021 — most category numbers changed.
- **IDOR (A01)** — Insecure Direct Object Reference: fetching an object by ID with no ownership check. An *absence*, not a visibly wrong line.
- **A06:2025 (was A04:2021), Insecure Design** — a category about a *missing* control, not a broken one; a design-review finding, not a code-review finding.
- **SSRF — no longer its own category as of 2025.** Folded into Broken Access Control (A01); was standalone A10 in 2021. The server fetches a URL an attacker influenced, potentially reaching internal/cloud-metadata targets.
- **A10:2025, Mishandling of Exceptional Conditions** — new in 2025, no 2021 equivalent. Covers error paths that fail open instead of closed (e.g., a fraud check defaulting to "approved" on an uncaught exception).

## Decision Table (2025 numbering)

| Category | One-line risk | Primary defense | Deep-dive |
|---|---|---|---|
| A01 Broken Access Control (incl. SSRF) | Object-level authorization check missing, or server-side fetch reaches unintended target | Explicit ownership check; allowlist on **resolved** destination | This chapter + `authn-authz-rbac-vs-abac.md` |
| A02 Security Misconfiguration | Insecure default left enabled | Explicit prod-vs-dev config review | This chapter |
| A03 Software Supply Chain Failures | Vulnerable dependency, or compromised build/distribution step | SBOM + dependency scanning + verified pipelines | `supply-chain-security-sbom-and-dependency-risk.md` |
| A04 Cryptographic Failures | Weak/absent crypto for data at rest or in transit | Modern algorithms, correct key handling | `applied-cryptography-hashing-signing-tls.md` |
| A05 Injection | Untrusted data parsed as code/syntax | Parameterized queries, output encoding | `injection-input-validation-output-encoding.md` |
| A06 Insecure Design | Control never designed in | Threat modeling before implementation | This chapter |
| A07 Authentication Failures | Weak auth flow or session handling | Standard OAuth2/OIDC/JWT patterns | `oauth2-oidc-and-jwt.md` |
| A08 Software/Data Integrity Failures | Unsigned/unverified code or data | Signing, verified pipelines | `applied-cryptography-hashing-signing-tls.md` |
| A09 Logging & Alerting Failures | Attack undetected due to insufficient logging/alerting | Security-event logging as first-class category | This chapter |
| A10 Mishandling of Exceptional Conditions | Error path fails open instead of closed | Explicit fail-closed default on security-relevant exceptions | This chapter |

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
- Treating "we have a WAF" as covering Injection (A05:2025) — a valuable additional layer, not a substitute for parameterized queries and output encoding at the source.
- Missing that SSRF applies to *any* server-side URL fetch — webhooks, PDF generators, image proxies are all SSRF-shaped, not just an obvious "URL parameter" feature; as of 2025 it's filed under A01, not its own category.
- Assuming IDOR requires a scanner to find — routinely found by manually changing an ID in a request and observing whether authorization is enforced.
- Citing 2021 category numbers (A02 Crypto, A03 Injection, A06 Vulnerable Components, A10 SSRF) as current — most of the list was renumbered in 2025.

## Interview Answer Skeleton

**30-sec:** The Top 10 is a prioritization/scoping tool for security review, not an exhaustive checklist — real risk includes business-logic flaws the list doesn't name. It's used as a routing question: name a category, then produce a concrete code-level example and the layer the fix belongs to (input boundary, authz layer, output boundary, dependency pipeline).

**2-min:** Add why it exists (focus limited review time on highest-prevalence/impact categories) + how it works (each category is a *shape*, not a specific bug) + the IDOR example (works perfectly on the happy-path test, only fails when a different user's ID is substituted — invisible to functional testing that only tests correct credentials).

**Whiteboard:** Three columns — "Trust boundary crossed" (A01, incl. SSRF), "Data treated as code/target" (A05/A08), "Control missing, broken, or fails open" (A02/A03/A04/A07/A09/A10). Circle A06 outside all three, labeled "design-level absence, not implementation defect."

**Staff-level framing:** treat the Top 10 as scope, not completion criteria. A06 and A09 findings typically indicate a process gap (no threat modeling; no security-event logging standard), not a single fixable bug — propose the process change alongside the immediate fix.

## Production Warning Signs

- Verbose stack traces (internal class names, file paths, SQL fragments) in production error responses — A02, a framework's dev error page left enabled; fix is configuration, requiring someone to have explicitly verified prod config differs from dev defaults.
- A credential-stuffing attack against login runs undetected for weeks — A09; failed-auth attempts weren't logged with enough context (source IP, username, timestamp) to distinguish a mistyped password from an automated attack — a missing decision, not a missing feature.
- A fraud/authorization check silently defaults to "approved" when an upstream dependency times out — A10 (new in 2025), failing open instead of closed on an exceptional condition.
- **Prevention:** default every object-fetching endpoint to requiring an explicit authorization check as part of its implementation template, every server-side URL-fetch feature to allowlist validation as shared middleware, and every security-relevant exception path to fail closed — structural requirements, not add-ons to remember.

## Related

- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
- `syllabus/12-security/injection-input-validation-output-encoding.md`
- `production-cookbook/credential-stuffing-undetected-from-missing-security-event-logging.md`
