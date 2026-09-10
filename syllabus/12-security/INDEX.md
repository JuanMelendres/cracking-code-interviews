---
title: "Security — Domain Index"
document_type: syllabus-domain-index
domain: 12-security
status: 9 of 9 chapters written — domain gap-audited and closed (2026-09-10), fully L1-L4
last_updated: 2026-09-10
---

# Security

OWASP Top 10, OAuth2/OIDC/JWT, secrets management, multi-tenancy isolation, supply-chain risk, and CSRF/CORS/session security. Existing `handbook/security/` (8 chapters) relocated here unchanged in content; a ninth chapter closed a real audited gap.

> **Phase 3 update (2026-09-03).** This domain's full existing content (8 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 8 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a home-burglary-risk-list analogy for the OWASP Top 10, an office-building-badge analogy for AuthN/AuthZ and RBAC/ABAC, a bouncer/notary/tamper-evident-envelope analogy for hashing/signing/TLS, an "instruction hidden in a note" analogy for injection, an apartment-building analogy for multi-tenancy isolation and Row-Level Security, a limited-pass-and-wax-seal analogy for OAuth2/OIDC/JWT, a storage-unit-key-generation analogy for key rotation, and a food-ingredient-label analogy for SBOMs). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. `12-security` was fully L1–L4 (8/8) at this point — the tenth fully-retrofitted domain in the syllabus.
>
> **Gap audit and closure (2026-09-10).** A repository-wide domain gap audit found this domain's 7 non-`oauth2` chapters missing their `## Diagrams` and `## Comparisons` sections (present in `oauth2-oidc-and-jwt.md`, absent from the other 7 despite the shared template) — closed by adding a real, template-consistent Diagrams section (a Mermaid diagram of the chapter's own already-established real evidence, never a new claim) and Comparisons section (a real comparison table, e.g. RBAC/ABAC/ReBAC, mTLS/JWT/API-key, RLS/app-filter/schema/silo) to each of the 7. The same audit found this domain, despite otherwise being comprehensive, had zero coverage of CSRF, CORS, or session security — three of the most-confused topics in web application security and explicitly named in `CLAUDE.md`'s own Security gap-analysis list. Closed with a ninth chapter, [CSRF, CORS, and Session Security](csrf-cors-and-session-security.md) (T-1308), backed by a real demo (`practice/java/week-17/csrf-cors-session/`) proving an identical forged cross-site request succeeds against a cookie-only endpoint and fails against a synchronizer-token-protected one, real CORS origin-allowlist header evidence (with an explicit, honest caveat about what a non-browser client can and can't demonstrate about browser-side enforcement), and a real session-fixation attack succeeding against a naive endpoint and being closed by session-ID regeneration on login. `applied-cryptography-hashing-signing-tls.md` also gained real mutual-TLS (mTLS) coverage in the same pass, including a real `openssl s_server`/`s_client` handshake demonstrating a connection rejected for missing a client certificate and accepted once one is presented — closing a second gap the same audit found (mTLS had zero coverage anywhere in the domain despite the chapter already covering one-way TLS in depth).

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1301 | OWASP Top 10 for Backend Services | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/owasp-top-10-for-backend-services.md` |
| T-1302 | AuthN vs AuthZ, RBAC vs ABAC | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/authn-authz-rbac-vs-abac.md` |
| T-1303 | Applied Cryptography: Hashing, Signing, and TLS (incl. mTLS) | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04; mTLS added 2026-09-10) | `syllabus/12-security/applied-cryptography-hashing-signing-tls.md` |
| T-1304 | Secrets Management and Key Rotation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/secrets-management-and-key-rotation.md` |
| T-1305 | Injection, Input Validation, and Output Encoding | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/injection-input-validation-output-encoding.md` |
| T-1306 | Supply Chain Security, SBOM, and Dependency Risk | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md` |
| T-1307 | Multi-Tenancy Isolation Models | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/multi-tenancy-isolation-models.md` |
| T-1308 | CSRF, CORS, and Session Security | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/12-security/csrf-cors-and-session-security.md` |
| T-512/T-513 | OAuth2, OIDC, and JWT | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/oauth2-oidc-and-jwt.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
