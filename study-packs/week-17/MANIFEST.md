---
title: "Week 17 Study Pack — Manifest"
week: 17
plan: B
last_reviewed: 2026-08-02
---

# Week 17 Study Pack — Manifest

**Topics:** T-1301, T-1303, T-1302, T-1305, T-1307, T-1304, T-1306 · **Plan:** B, Security Domain Closure (Phase 4/5 — closes Security from 0/7 to 7/7 register topics, the first domain in the entire register closed to 100% coverage — see `00-project/coverage-audit-2026-07-31.md`, which flagged D13 Security 0/7 as the single worst-covered domain, worse than JVM's 8% before Week 16)
**Files:** 14 (+ this manifest) · **Total words:** 9,347 (real count, `wc -w` over all 14 files)
**Canonical chapters:** 7 new `handbook/security/` chapters, 28,281 words total (real count, `wc -w`), written full-depth from the start — this week did not need a separate slimming pass.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 729 |
| 2 | `01-owasp-top-10-for-backend-services.md` | T-1301 — summary + link; full chapter canonical at `handbook/security/owasp-top-10-for-backend-services.md` | 604 |
| 3 | `02-applied-cryptography-hashing-signing-tls.md` | T-1303 — summary + link; full chapter canonical at `handbook/security/applied-cryptography-hashing-signing-tls.md` | 554 |
| 4 | `03-authn-authz-rbac-vs-abac.md` | T-1302 — summary + link; full chapter canonical at `handbook/security/authn-authz-rbac-vs-abac.md` | 577 |
| 5 | `04-injection-input-validation-output-encoding.md` | T-1305 — summary + link; full chapter canonical at `handbook/security/injection-input-validation-output-encoding.md` | 556 |
| 6 | `05-multi-tenancy-isolation-models.md` | T-1307 — summary + link; full chapter canonical at `handbook/security/multi-tenancy-isolation-models.md` | 526 |
| 7 | `06-secrets-management-and-key-rotation.md` | T-1304 — summary + link; full chapter canonical at `handbook/security/secrets-management-and-key-rotation.md` | 542 |
| 8 | `07-supply-chain-security-sbom-and-dependency-risk.md` | T-1306 — summary + link; full chapter canonical at `handbook/security/supply-chain-security-sbom-and-dependency-risk.md` | 567 |
| 9 | `08-hands-on-lab.md` | 7 labs, all real and reproducible | 768 |
| 10 | `09-flashcards.md` | 21 cards | 1,304 |
| 11 | `10-week-17-mock-interview.md` | 45-min Security technical round | 927 |
| 12 | `11-design-exercise-multi-tenant-expense-platform-security-review.md` | Full security review, multi-tenant expense platform, worked reference solution | 1,146 |
| 13 | `12-week-17-checklist.md` | Day-by-day checklist (9-day cycle) | 312 |
| 14 | `resources.md` | Sources classified PRIMARY/TOOL | 235 |

---

## Verification

| Item | Status |
|---|---|
| Java — OWASP Top 10 (IDOR, SSRF) | **Executed.** OpenJDK 21.0.12. Real IDOR: vulnerable handler returns alice's invoice to bob; fixed handler throws `SecurityException`, same underlying data access, differing by one ownership comparison. Real SSRF: vulnerable "URL preview" service leaks a fake internal metadata endpoint's credentials (`AKIA-DEMO-NOT-REAL ...`) via two local `HttpServer` instances standing in for public/internal targets; fixed version blocks it via allowlist validated against the resolved host:port. Source: `practice/java/week-17/owasp-top-10/` |
| Java — Applied cryptography | **Executed.** Real PBKDF2WithHmacSHA256 cost measurement, each iteration count in its own fresh JVM process: `iterations=1` isolates ~31ms JVM-startup baseline; `iterations=100000` ~86ms; `iterations=600000` ~128ms. Real `SHA256withECDSA` signature: `verify(original)=true`, `verify(tampered '900.00')=false`. Real self-signed TLS 1.3 handshake (OpenSSL 3.x `s_server`/`s_client` against a `keytool`-generated EC self-signed cert): negotiated `TLS_AES_256_GCM_SHA384` cipher suite and `X25519MLKEM768` hybrid post-quantum key-exchange group, correctly flagged `self-signed certificate`. Source: `practice/java/week-17/crypto/` |
| Java — RBAC vs ABAC | **Executed.** Real demo: three users sharing the identical RBAC role (`engineer`) all get `rbacAllow=true` (two false positives). The same three users under an ABAC evaluator considering ownership/team attributes get correct, differentiated answers; the same legitimate approver denied again when only the environment attribute (time, 02:00 vs. 14:00) changes. Source: `practice/java/week-17/authz-models/` |
| Java + PostgreSQL — Injection, output encoding | **Executed.** Live PostgreSQL 16 (`postgres:16-alpine`). Real SQL-injection auth bypass: username `admin' --` grants access via the vulnerable string-concatenated query (executed SQL captured directly); the `PreparedStatement` version correctly fails for the identical input, legitimate `alice`/`hash1` login still succeeds. Real stored-XSS: a `<script>` payload renders live in the vulnerable HTML concatenation (`contains <script>? true`); the output-encoded version renders only inert HTML entities (`contains <script>? false`). Source: `practice/java/week-17/injection/` |
| PostgreSQL — Multi-tenancy RLS | **Executed.** Real Row-Level Security policy on a live `orders` table. As non-superuser `app_user`: `tenant_a` context returns only its 2 rows, `tenant_b` context returns only its 1 row, no context set returns 0 rows (fail-closed). As the `postgres` superuser (`BYPASSRLS` by default), the identical query returns all 3 rows across both tenants unconditionally, no `SET` required — the central RLS caveat, verified directly. Source: `practice/java/week-17/multi-tenancy/rls-demo.sql` |
| Java — Secrets management, key rotation | **Executed.** Real AES-256-GCM envelope encryption. Key v1 encrypts a record; rotation to v2 is instantaneous; both v1- and v2-tagged records decrypt correctly simultaneously post-rotation. Removing v1 from the key ring (simulating retirement without a completed re-encryption sweep) immediately and permanently breaks decryption for the v1-tagged record (`no key for version 1`). Source: `practice/java/week-17/secrets-rotation/` |
| Docker Scout — Supply chain, SBOM | **Executed.** Docker 29.6.2, Docker Scout v1.24.0, against `eclipse-temurin:21-jre` (the base image already used in Week 16's container-ergonomics chapter). Real SBOM: 213 packages found (via image attestation), spanning `deb` and `golang` package ecosystems. Real CVE scan against that SBOM: 13 vulnerabilities across 3 packages (1 CRITICAL, 1 HIGH, 7 MEDIUM, 1 LOW, 3 UNSPECIFIED), including CVE-2026-39821 (CRITICAL, fixed in `golang.org/x/net@0.55.0`) in a transitive package bundled into the base image, unrelated to any application code. Source: `practice/java/week-17/supply-chain/README.md` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

None. This is new-domain content (Security had 0 of 7 register topics covered), not a correction to existing material.

## Scope note

This week covers all 7 of the 7 Security register topics (T-1301, T-1302, T-1303, T-1304, T-1305, T-1306, T-1307), closing the domain completely — unlike Week 16's JVM week, which covered 5 of 12 register topics by IWI descending order and explicitly deferred the remainder. Security's smaller register size (7 topics total, versus JVM's 12) made full closure practical in one week. Note per `00-project/coverage-audit-2026-07-31.md` §2: three JWT/OAuth2-adjacent topics (T-511 Spring Security filter chain, T-512 OAuth2/OIDC, T-513 JWT design) physically live in `handbook/spring/` and `handbook/security/oauth2-oidc-and-jwt.md` but are classified under D5 Spring in the blueprint's own register, not D13 Security — they are pre-existing (Week 7) and out of this week's scope, referenced from the new chapters as related material rather than duplicated.

## A note on real evidence and cleanup

Verification used a real, disposable PostgreSQL 16 Docker container (`security-pg`, stopped and removed after evidence capture) and real, disposable TLS artifacts (a `keytool`-generated PKCS12 keystore, exported PEM cert/key) deleted after capturing the handshake evidence — none of these are committed. `.gitignore` was updated this week to add `*.p12` and `*.jks` alongside the pre-existing `*.pem`/`*.key` exclusions, since this week's TLS demo was the first in this repository to generate a PKCS12 keystore. Every number cited in the canonical chapters and this manifest was captured directly from these real runs before cleanup, not estimated.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, a real PostgreSQL 16 Docker container, real OpenSSL 3.x TLS handshakes, real Docker Scout v1.24.0 invocations against a real container image). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
