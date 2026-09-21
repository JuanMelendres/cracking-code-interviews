---
title: "Cloud — Domain Index"
document_type: syllabus-domain-index
domain: 15-cloud
status: 4 originally-mapped-plus-gap-audit chapters — 3 physically relocated (Phase 3, 2026-09-03), L1/L2 retrofit complete (Phase 5, 2026-09-04); 4th chapter added 2026-09-10 (Azure and GCP for Backend Engineers, T-2404 — gap found in a full 22-domain audit); 5th chapter added 2026-09-21 (Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling, T-2425 — gap audit)
last_updated: 2026-09-21
---

# Cloud

AWS core services (ECS/EKS/RDS/SQS/SNS/ALB/Auto Scaling), Azure and GCP equivalents, cloud cost economics, twelve-factor configuration, and serverless (Lambda) execution mechanics — the more architectural, less daily-operational half of what was `cloud/`.

> **Phase 3 update (2026-09-03).** This domain's full existing content (3 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 3 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject: a car-ownership-spectrum analogy for the compute spectrum plus a safe-deposit-box/external-hard-drive/vending-machine analogy for storage and database access models (AWS core services); a gym-membership analogy for on-demand/reserved/spot pricing (cloud cost economics); a recipe-card-vs-fridge-ingredients analogy for config-vs-code separation (twelve-factor config). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`15-cloud` is now fully L1–L4 (3/3)** — the thirteenth fully-retrofitted domain in the syllabus.
>
> **Gap found and closed: Azure and GCP, 2026-09-10.** A full repository-wide 22-domain gap audit found this domain named "AWS core services" in its own top-level description and covered AWS exclusively — a real single-provider gap against a program whose own target-company list (Microsoft, and any GCP-based or multi-cloud organization) requires cross-provider fluency, not AWS-only vocabulary. Closed with [Azure and GCP for Backend Engineers](azure-and-gcp-for-backend-engineers.md) (T-2404), which maps every AWS service the existing chapter teaches to its real Azure and GCP counterpart by underlying access-model/ownership trade-off rather than by name, verified against current official documentation via `WebFetch` before writing — including a real 2023 rename (Azure AD → Microsoft Entra ID) and a real GCP native-IaC transition (Deployment Manager → Infrastructure Manager), both confirmed live, not assumed from memory.
>
> **Gap found and closed: Serverless (Lambda) execution mechanics, 2026-09-21.** A further gap audit found `aws-core-services-for-backend-engineers.md` names Lambda's "cold-start latency" as a real trade-off five separate times without ever explaining the mechanism behind it. Closed with [Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling](serverless-lambda-execution-model-cold-starts-and-concurrency.md) (T-2425), backed by a real Java simulation (`practice/java/cloud/serverless-cold-starts-and-concurrency/`) using genuine JVM class-loading/object-construction work as a technically substantiated proxy for a real Lambda INIT phase (the dominant real contributor to cold-start latency for an actual JVM-based Lambda function): a real cold invocation measured at ~176ms versus ~0.009ms for a warm one, and a real 5-way concurrent burst against zero warm environments measured at ~236ms wall-clock versus ~0.08ms for a repeat burst against already-warm ones — real, direct proof that concurrency multiplies cold starts rather than merely delaying one.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1006 | AWS Core Services for Backend Engineers | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` |
| T-1007 | Cloud Cost and Scaling Economics | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/cloud-cost-and-scaling-economics.md` |
| T-1008 | The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/15-cloud/twelve-factor-config.md` |
| T-2404 | Azure and GCP for Backend Engineers | L1, L2, L3, L4 — fully written, verified against current official docs (2026-09-10) | `syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md` |
| T-2425 | Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling | L1, L2, L3, L4 — fully written, real demo (2026-09-21) | `syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
