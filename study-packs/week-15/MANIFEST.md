---
title: "Week 15 Study Pack — Manifest"
week: 15
plan: B
last_reviewed: 2026-07-31
---

# Week 15 Study Pack — Manifest

**Topics:** T-1003, T-1002, T-1007, T-1009, T-1006 · **Plan:** B, Cloud & Infrastructure (Phase 4/5 — third new-domain week; Cloud & Infrastructure previously had zero study-pack coverage)
**Files:** 12 (+ this manifest) · **Total words:** 6,673 (real count, `wc -w` over all 12 files; checked 2026-07-31 for slimming — this week was already authored lean, post-dating the study-pack slimming convention, so only trivial prose tightening in `09`/README applied; 01–05 already summary+link, `06`/`07`/`08`/`10`/`resources.md` had no cuttable fat)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria, evidence-scoping note | 697 |
| 2 | `01-kubernetes-resource-limits-probes-and-jvm-sizing.md` | T-1003 — summary + link; full chapter canonical at `syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md` | 582 |
| 3 | `02-kubernetes-objects-scheduling-and-networking.md` | T-1002 — summary + link; full chapter canonical at `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md` | 504 |
| 4 | `03-cloud-cost-and-scaling-economics.md` | T-1007 — summary + link; full chapter canonical at `syllabus/15-cloud/cloud-cost-and-scaling-economics.md` | 504 |
| 5 | `04-cicd-pipeline-design-and-deployment-strategies.md` | T-1009 — summary + link; full chapter canonical at `syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md` | 527 |
| 6 | `05-aws-core-services-for-backend-engineers.md` | T-1006 — summary + link; full chapter canonical at `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` | 491 |
| 7 | `06-hands-on-lab.md` | 5 labs, all real and reproducible | 442 |
| 8 | `07-flashcards.md` | 15 cards | 1,072 |
| 9 | `08-week-15-mock-interview.md` | 45-min Cloud & Infrastructure technical round | 676 |
| 10 | `09-design-exercise-deployment-infrastructure.md` | Full infrastructure/deployment design, worked reference solution | 782 |
| 11 | `10-week-15-checklist.md` | Day-by-day checklist | 241 |
| 12 | `resources.md` | Sources classified PRIMARY/SECONDARY | 155 |

---

## Verification

| Item | Status |
|---|---|
| Docker + Java — Container-aware JVM heap sizing | **Executed.** Docker 29.6.2, `eclipse-temurin:21-jre`. Real `maxMemory()` measurements at `--memory=256m/512m/1g` (121/123/247 MiB), real `-XX:+PrintFlagsFinal` confirmation of the `MinRAMPercentage`/`MaxRAMPercentage` mechanism explaining the 256m outlier, real cgroup `memory.max` confirmation (268435456 bytes = exactly 256 MiB). Source: `practice/java/week-15/container-ergonomics/` |
| Docker + Java — OutOfMemoryError vs OOMKilled | **Executed.** Real `java.lang.OutOfMemoryError` (exit 1, full stack trace) for a 512m container with `-Xmx64m`; real OOMKilled (exit 137) for a 100m container with `-Xmx256m`, confirmed via `docker inspect --format '{{.State.OOMKilled}}'` returning `true`. Source: `practice/java/week-15/container-ergonomics/` |
| Kubernetes manifest | **Syntax-validated**, not applied against a live cluster (stated explicitly). `ruby -ryaml` confirms 3 documents (Deployment, Service, HorizontalPodAutoscaler) parse correctly. Source: `practice/k8s/week-15/deployment-with-probes-and-limits.yaml` |
| CI/CD pipeline | **Syntax-validated**, not executed against a live GitHub Actions runner (stated explicitly). `ruby -ryaml` confirms all 3 jobs parse correctly. Source: `practice/k8s/week-15/ci-cd-pipeline.yaml` |
| Cloud cost calculations | **Real arithmetic** against clearly-labeled illustrative unit prices (not live-scraped current AWS rates — stated explicitly, verify against published rates for any real decision). All figures independently re-checked. Source: `syllabus/15-cloud/cloud-cost-and-scaling-economics.md` §Internal Implementation |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

None. This is new-domain content (Cloud & Infrastructure had zero prior coverage), not a correction to existing material.

## Scope note

This week covers the 5 highest-weighted-IWI Cloud & Infrastructure topics (T-1003, T-1002, T-1007, T-1009, T-1006) out of 9 total in the D10 register (`00-project/knowledge-architecture-blueprint.md` §D10). The remaining 4 (T-1001 Containers & image internals, T-1004 Service mesh & sidecar trade-offs, T-1005 Infrastructure as Code fundamentals, T-1008 12-factor, config & secrets management) are deferred — stated explicitly, not silently dropped.

This week's evidence is scoped differently from prior weeks: no live Kubernetes cluster or AWS account was used. Real Docker containers (a genuine cgroup memory boundary, the same mechanism a Kubernetes pod resource limit ultimately configures) provide real execution evidence for T-1003. Kubernetes and GitHub Actions YAML manifests are syntax-validated via a real YAML parser, not applied against a live API server. Cloud cost figures use real arithmetic against clearly-labeled illustrative unit prices, not live-scraped current rates. Every one of these scoping choices is stated explicitly in the relevant chapter, per this repository's established integrity convention.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real Docker/Java runs, real `ruby -ryaml` validation, independently re-checked arithmetic). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
