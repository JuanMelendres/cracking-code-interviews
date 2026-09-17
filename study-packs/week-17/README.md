---
title: "Week 17 — Security Domain Closure"
document_type: study-pack
week: 17
status: draft
estimated_hours: 16
---

# Week 17 — Security Domain Closure

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real code all seven Security register topics: OWASP Top 10 for backend services, applied cryptography (hashing/signing/TLS), AuthN vs AuthZ and RBAC vs ABAC, injection and output encoding, multi-tenancy isolation models, secrets management and key rotation, and supply-chain security/SBOM.

## Why This Week Matters

Security was the single worst-covered domain in the register — 0 of 7 topics had a canonical chapter before this week, per the repository's own coverage audit (`00-project/coverage-audit-2026-07-31.md`), the lowest coverage percentage of any domain including JVM's 8% before Week 16. Unlike JVM (12 register topics, partially closed over one week), Security's 7 topics are small enough to close completely in a single week. These topics recur directly in Senior/Staff interviews whenever a candidate is asked to review a design for access-control gaps, explain why a hashing scheme is wrong, or reason about what a shared container base image actually ships.

## Prerequisites

None from prior weeks are strictly required, though `syllabus/05-spring/security-filter-chain.md` and `syllabus/12-security/oauth2-oidc-and-jwt.md` (Week 7) provide useful context for the AuthN/AuthZ chapter, and `syllabus/06-databases/table-partitioning-and-sharding-strategies.md` for the multi-tenancy chapter.

## Schedule

See `12-week-17-checklist.md` for the day-by-day breakdown (a 9-day cycle, given seven topics rather than five).

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | [`README.md`](README.md) | This file |
| 2 | [`01-owasp-top-10-for-backend-services.md`](01-owasp-top-10-for-backend-services.md) | T-1301 — summary + link; full chapter canonical at `syllabus/12-security/owasp-top-10-for-backend-services.md` |
| 3 | [`02-applied-cryptography-hashing-signing-tls.md`](02-applied-cryptography-hashing-signing-tls.md) | T-1303 — summary + link; full chapter canonical at `syllabus/12-security/applied-cryptography-hashing-signing-tls.md` |
| 4 | [`03-authn-authz-rbac-vs-abac.md`](03-authn-authz-rbac-vs-abac.md) | T-1302 — summary + link; full chapter canonical at `syllabus/12-security/authn-authz-rbac-vs-abac.md` |
| 5 | [`04-injection-input-validation-output-encoding.md`](04-injection-input-validation-output-encoding.md) | T-1305 — summary + link; full chapter canonical at `syllabus/12-security/injection-input-validation-output-encoding.md` |
| 6 | [`05-multi-tenancy-isolation-models.md`](05-multi-tenancy-isolation-models.md) | T-1307 — summary + link; full chapter canonical at `syllabus/12-security/multi-tenancy-isolation-models.md` |
| 7 | [`06-secrets-management-and-key-rotation.md`](06-secrets-management-and-key-rotation.md) | T-1304 — summary + link; full chapter canonical at `syllabus/12-security/secrets-management-and-key-rotation.md` |
| 8 | [`07-supply-chain-security-sbom-and-dependency-risk.md`](07-supply-chain-security-sbom-and-dependency-risk.md) | T-1306 — summary + link; full chapter canonical at `syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md` |
| 9 | [`08-hands-on-lab.md`](08-hands-on-lab.md) | 7 labs reproducing this week's real demonstrations |
| 10 | [`09-flashcards.md`](09-flashcards.md) | 21 cards |
| 11 | [`10-week-17-mock-interview.md`](10-week-17-mock-interview.md) | 45-min Security technical round |
| 12 | [`11-design-exercise-multi-tenant-expense-platform-security-review.md`](11-design-exercise-multi-tenant-expense-platform-security-review.md) | Full security review for a multi-tenant B2B expense-approval platform |
| 13 | [`12-week-17-checklist.md`](12-week-17-checklist.md) | Day-by-day checklist |
| 14 | [`resources.md`](resources.md) | Sources classified PRIMARY/TOOL |
| 15 | [`13-csrf-cors-and-session-security.md`](13-csrf-cors-and-session-security.md) | T-1308, added 2026-09-17 — see Addendum below |
| 16 | [`14-enterprise-sso-saml-and-federated-identity.md`](14-enterprise-sso-saml-and-federated-identity.md) | T-1309, added 2026-09-17 — see Addendum below |

## Hands-On Exercises

Complete all 7 labs in `08-hands-on-lab.md` — a real IDOR/SSRF pair, real password-hashing cost and signature tamper detection, a real RBAC-vs-ABAC comparison, a real live SQL-injection bypass and stored-XSS demonstration, a real PostgreSQL Row-Level Security multi-tenancy demonstration, a real key-rotation demonstration, and a real SBOM/CVE scan.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

None this week in the usual LeetCode sense — security topics are review-and-reasoning-shaped, not algorithm-shaped. See `08-hands-on-lab.md` for this week's hands-on equivalent.

## System Design Exercise

`11-design-exercise-multi-tenant-expense-platform-security-review.md` — produce a full security review for a multi-tenant B2B expense-approval platform, applying all seven of this week's topics.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`10-week-17-mock-interview.md` — 45-minute Security technical round, candidate/evaluator sections hard-separated.

## Review Checklist

See `12-week-17-checklist.md`.

## Completion Criteria

- [ ] All seven canonical chapters read in full
- [ ] All seven labs in `08-hands-on-lab.md` reproduced with matching results
- [ ] Design exercise completed independently before checking the reference solution
- [ ] Mock interview average score ≥ 3.5
- [ ] Addendum: both `13-csrf-cors-and-session-security.md` and `14-enterprise-sso-saml-and-federated-identity.md` read, and the CSRF/session-fixation lab in `13-csrf-cors-and-session-security.md` reproduced (added 2026-09-17)

## Retrospective

Note which of the seven topics needs a second pass, and whether the design exercise revealed a gap not caught by the individual chapter labs.

## Addendum (2026-09-17): two topics added after this week originally closed

`12-security` grew from 7 to 10 canonical chapters after this week's original sprint shipped (2026-08-02) — two later, separate gap-fill sessions added CSRF/CORS/Session Security (2026-09-10) and Enterprise SSO/SAML/Federated Identity (2026-09-14). Neither was folded back into this week's required reading at the time, a real scheduling gap flagged and now closed:

- [`13-csrf-cors-and-session-security.md`](13-csrf-cors-and-session-security.md) — T-1308, includes a real, reproducible hands-on lab (`practice/java/week-17/csrf-cors-session/`, which already existed, unwired, since 2026-09-10).
- [`14-enterprise-sso-saml-and-federated-identity.md`](14-enterprise-sso-saml-and-federated-identity.md) — T-1309, reading-only this week; its own canonical chapter is explicit that a faithful end-to-end demo would require standing up a real SAML Identity Provider, out of scope for this project's practice budget.

Everything above this section (Weekly Outcome, Why This Week Matters, the numbered `01`–`12` reading list, hands-on labs 1–7, the flashcard deck, mock interview, and design exercise) is left exactly as originally written — an accurate record of the 2026-08-02 "Security Domain Closure" sprint when the domain had exactly 7 topics, not retroactively rewritten to claim it always covered 9. A learner working through this week today should read all 9 topics; the completion criteria below now include the two added ones.

## Next Week

Security coverage is now at 7/7 register topics (was 0/7 before this week), per `00-project/coverage-audit-2026-07-31.md` — the first domain in the entire register closed to 100%. JVM remains at 6/12 (6 topics deferred: T-302, T-305, T-309, T-310, T-311, T-303's classic-generational framing). Next steps per the audit: continue JVM depth, start Testing depth (2/8), or move to Phase 6 complementary deliverables (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook — none of which have started yet).
