---
title: "Cloud — Domain Index"
document_type: syllabus-domain-index
domain: 15-cloud
status: 3 of 3 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Cloud

AWS core services (ECS/EKS/RDS/SQS/SNS/ALB/Auto Scaling), cloud cost economics, and twelve-factor configuration — the more architectural, less daily-operational half of what was `cloud/`.

> **Phase 3 update (2026-09-03).** This domain's full existing content (3 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 3 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject: a car-ownership-spectrum analogy for the compute spectrum plus a safe-deposit-box/external-hard-drive/vending-machine analogy for storage and database access models (AWS core services); a gym-membership analogy for on-demand/reserved/spot pricing (cloud cost economics); a recipe-card-vs-fridge-ingredients analogy for config-vs-code separation (twelve-factor config). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`15-cloud` is now fully L1–L4 (3/3)** — the thirteenth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1006 | AWS Core Services for Backend Engineers | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` |
| T-1007 | Cloud Cost and Scaling Economics | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/cloud-cost-and-scaling-economics.md` |
| T-1008 | The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/twelve-factor-config.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
