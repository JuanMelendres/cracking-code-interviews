---
title: "Design Exercise — Security Review of a Multi-Tenant Expense-Approval Platform"
week: 17
document_type: study-pack-design-exercise
status: draft
last_reviewed: 2026-08-02
---

# Design Exercise — Security Review of a Multi-Tenant Expense-Approval Platform

**Format:** 45 minutes, whiteboard or written. Produce a security review covering the service below, applying all seven of this week's topics explicitly.

## The scenario

Your team owns a B2B SaaS expense-approval platform: employees submit expense reports, their manager approves or rejects them, and approved reports are paid out via a third-party payments API. It's a shared-schema (pool) multi-tenant system — one company's data lives alongside every other customer's in the same PostgreSQL database, distinguished by a `company_id` column. The platform is growing fast, onboarding several new enterprise customers this quarter, each with their own compliance team asking pointed security questions before signing.

## Design this

1. **Access control:** An expense report has an `id`, `submitter_id`, and `company_id`. What's your first question about the "fetch expense report by ID" endpoint, and why?
2. **Credential storage and transport:** The platform stores user passwords and calls the payments API over HTTPS. What specifically would you check about each?
3. **Approval authorization model:** "A manager may approve a report only for their own direct reports, and never their own submitted report." Your system currently uses a single `manager` role. Is that sufficient?
4. **A search feature:** Finance admins can search expense reports by submitter name. How would you build this safely?
5. **Tenant isolation:** Given the shared-schema/pool architecture, what's your biggest structural risk, and what would you add to reduce it?
6. **The payments API key:** This key authorizes real money movement. What's your position on how it's stored and how often it changes?
7. **The deployment itself:** The service runs in containers built from a shared internal base image. What would you want to know before an enterprise customer's security questionnaire asks about it?

Work through your answer before reading the reference solution below.

---

## Reference Solution

**1. Access control.** First question: does the fetch-by-ID handler check that the requester's `company_id` (and, for non-admin roles, that they're the submitter or their manager) matches the report's own `company_id`/`submitter_id` before returning it? This is the textbook IDOR shape from `01-owasp-top-10-for-backend-services.md` — the vulnerable and fixed versions of this exact handler differ by one comparison, and it's invisible to functional tests that only ever fetch a user's own reports. Given this is a multi-tenant system, a missing check here is not just one user seeing another's data — it's potentially one *company* seeing another's data, which is both a security and a contractual (data-isolation clause) failure.

**2. Credential storage and transport.** Passwords: confirm the hashing algorithm has a tunable, deliberately-expensive cost parameter (Argon2id or PBKDF2, per `02-applied-cryptography-hashing-signing-tls.md`) — not a fast general-purpose hash even if salted. Payments API calls: confirm TLS is actually enforced (not merely available) on every call path, using the platform's standard library defaults rather than a manually pinned, possibly-stale cipher-suite list — TLS 1.3's reduced negotiation surface means "use the current default" is the secure choice here, not a compromise.

**3. Approval authorization model.** No — a single `manager` role can only answer "does this user hold the manager role," not "is this specific report's submitter this specific manager's direct report" or "is this manager approving their own submission." Both are relationships between the specific subject and the specific resource, exactly the case RBAC structurally cannot express (`03-authn-authz-rbac-vs-abac.md`). The fix layers an ABAC-style check on the existing role: `hasRole("manager") AND report.submitter.managerId == currentUser.id AND report.submitterId != currentUser.id`.

**4. Search feature.** Never string-concatenate the search term into a SQL query, including inside a `LIKE '%...%'` clause — parameterize it, and explicitly escape `LIKE`'s own wildcard characters (`%`, `_`) as literal data rather than pattern syntax if the search term itself might contain them (`04-injection-input-validation-output-encoding.md`). Any result rendered back into an admin UI (submitter names, report descriptions) needs HTML output encoding at render time, independent of how the underlying query was built — the two defenses (parameterized query, output encoding) are separate and both required.

**5. Tenant isolation.** The biggest structural risk in a pool/shared-schema architecture is that `company_id` filtering depends on being correct in *every* query, in every code path — including future features, admin tools, and background jobs not yet written (`05-multi-tenancy-isolation-models.md`). Add PostgreSQL Row-Level Security on every tenant-scoped table, with a policy against a session-level `app.company_id` context variable set once per request — this converts isolation from "every query must remember" into "the database refuses by default," including for tooling that doesn't go through the primary application code path at all. Critically: audit every database role in use (application, any analytics/reporting tooling, migration scripts) for superuser or `BYPASSRLS` status, since RLS provides zero protection for an exempt role regardless of how correctly the policy itself is written.

**6. Payments API key.** This key is the platform's most sensitive secret — a compromise directly enables unauthorized money movement. Store it in a managed secrets system (KMS/Vault), never in application configuration files or environment variables checked into any repository. Implement key-version tagging (envelope-encryption-style, per `06-secrets-management-and-key-rotation.md`) even before rotation is required by the payments provider, specifically because retrofitting versioning onto an already-integrated, unversioned key-handling path later is a substantially larger project than building it in now — and rotate on a schedule at least as frequent as the payments provider's own recommendation, treating "no known compromise" as insufficient reason to skip rotation.

**7. The deployment itself.** Generate and continuously scan a real SBOM against the shared base image (`07-supply-chain-security-sbom-and-dependency-risk.md`) — an enterprise security questionnaire will very likely ask directly about known vulnerabilities in the deployed software stack, and "we haven't checked" is a materially worse answer than "here's our current SBOM and CVE triage status, including our remediation SLA by severity." Given the base image is shared internally, confirm whether its currency and patching is a centrally-owned responsibility (one team keeping it current, all services rebuilding against it) rather than something each service team is separately, inconsistently responsible for.

## Self-Check

- [ ] Identified the fetch-by-ID authorization gap as a company-level (not just user-level) risk, given the multi-tenant context
- [ ] Named the specific correct password-hashing category (Argon2id/PBKDF2) and the specific TLS posture (current library defaults, not a manually pinned legacy list)
- [ ] Recognized the approval rule as a relationship RBAC can't express, and proposed the specific ABAC-style condition, including the self-approval exclusion
- [ ] Named both defenses for the search feature (parameterized query AND output encoding) as separate, both-required steps
- [ ] Proposed RLS for tenant isolation and explicitly named the superuser/`BYPASSRLS` audit as a required companion step, not an afterthought
- [ ] Treated the payments API key as requiring both managed secrets storage and proactive (not purely reactive) rotation
- [ ] Connected the shared base image to SBOM scanning and centralized ownership, not a per-service afterthought
