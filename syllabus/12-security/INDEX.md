---
title: "Security — Domain Index"
document_type: syllabus-domain-index
domain: 12-security
status: 8 of 8 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Security

OWASP Top 10, OAuth2/OIDC/JWT, secrets management, multi-tenancy isolation, and supply-chain risk. Existing `handbook/security/` (8 chapters) relocates here unchanged in content.

> **Phase 3 update (2026-09-03).** This domain's full existing content (8 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 8 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a home-burglary-risk-list analogy for the OWASP Top 10, an office-building-badge analogy for AuthN/AuthZ and RBAC/ABAC, a bouncer/notary/tamper-evident-envelope analogy for hashing/signing/TLS, an "instruction hidden in a note" analogy for injection, an apartment-building analogy for multi-tenancy isolation and Row-Level Security, a limited-pass-and-wax-seal analogy for OAuth2/OIDC/JWT, a storage-unit-key-generation analogy for key rotation, and a food-ingredient-label analogy for SBOMs). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`12-security` is now fully L1–L4 (8/8)** — the tenth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1301 | OWASP Top 10 for Backend Services | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/owasp-top-10-for-backend-services.md` |
| T-1302 | AuthN vs AuthZ, RBAC vs ABAC | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/authn-authz-rbac-vs-abac.md` |
| T-1303 | Applied Cryptography: Hashing, Signing, and TLS | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/applied-cryptography-hashing-signing-tls.md` |
| T-1304 | Secrets Management and Key Rotation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/secrets-management-and-key-rotation.md` |
| T-1305 | Injection, Input Validation, and Output Encoding | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/injection-input-validation-output-encoding.md` |
| T-1306 | Supply Chain Security, SBOM, and Dependency Risk | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/supply-chain-security-sbom-and-dependency-risk.md` |
| T-1307 | Multi-Tenancy Isolation Models | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/multi-tenancy-isolation-models.md` |
| T-512/T-513 | OAuth2, OIDC, and JWT | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/12-security/oauth2-oidc-and-jwt.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
