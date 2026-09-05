---
title: "DevOps & Containers — Domain Index"
document_type: syllabus-domain-index
domain: 14-devops-containers
status: 4 of 4 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# DevOps & Containers

Kubernetes objects/scheduling/networking, resource limits and probes, container image internals, and CI/CD pipeline design — the operational mechanics a backend engineer runs day-to-day. Split out of `cloud/` per Section 3.3.

> **Phase 3 update (2026-09-03).** This domain's full existing content (4 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 4 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (an overhead-projector-transparency analogy for image layers plus a curtain-and-budget analogy for namespaces/cgroups, a restaurant-shift-manager analogy for Deployments/ReplicaSets/Services, a storage-unit-and-loose-items analogy for `OutOfMemoryError` vs. OOM kill plus a manager-check-in analogy for probes, and a restaurant-new-menu-rollout analogy for rolling/blue-green/canary deployments). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`14-devops-containers` is now fully L1–L4 (4/4)** — the twelfth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-1001 | Containers & Image Internals | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/container-image-internals.md` |
| T-1002 | Kubernetes Objects, Scheduling, and Networking | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md` |
| T-1003 | Kubernetes Resource Limits, Probes, and JVM Sizing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| T-1009 | CI/CD Pipeline Design and Deployment Strategies | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
