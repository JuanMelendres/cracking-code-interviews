---
title: "Frontend Mid → Senior, Week 10 — Manifest"
week: 10
track: frontend-mid-to-senior
last_reviewed: 2026-09-08
---

# Week 10 — Manifest

**Topics:** F-303 (Monorepo and Full-Stack Repo Layout), F-214 (Full-Stack Integration with a Java/Spring Backend). **Track:** Frontend Mid → Senior, Week 10 of 10 (final week).
**Files:** 1 (+ this manifest) — no chapter content duplicated.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercises, full System Design Exercise, full-pack retrospective, review checklist |

## Verification

| Item | Location | Status |
|---|---|---|
| Monorepo layout demo (real symlink + measured `du -sh` costs) | `practice/frontend/monorepo-layout-demo/` | Predates this pack's construction; specific claims (symlink path, MB figures: 435/39/60/55) verified by directly reading `syllabus/21-frontend-web/nextjs-monorepo-layout.md`'s own body text during this manifest's construction (2026-09-08), not re-executed independently |
| Full-stack integration demo (real CORS failure/fix, real BFF chain) | `practice/frontend/react-nextjs-fundamentals/` (port 5198) + `practice/java/full-stack-integration-backend/` (port 8080) | Predates this pack's construction; specific file names (`PublicController.java`, `CorsConfig.java`, `InternalController.java`, `app/api/backend-proxy/route.js`) verified by directly reading `syllabus/21-frontend-web/nextjs-fullstack-integration.md`'s own body text during this manifest's construction (2026-09-08), not re-executed independently |

## Scope note

Closing week of the 10-week pack — the only week with a full System Design Exercise, since F-214 is this domain's Expert/Staff-equivalent capstone and the chapter most directly serving a full-stack Java+React developer.

## Integrity note

Unlike most other weeks in this pack (which cite pre-existing demos by link only, without inspecting them), this manifest's specific file names and measured figures were confirmed by reading both chapters' own body text directly — not re-executing the demos, but not guessing the specifics either.
