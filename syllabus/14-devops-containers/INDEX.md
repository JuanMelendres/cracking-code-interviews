---
title: "DevOps & Containers — Domain Index"
document_type: syllabus-domain-index
domain: 14-devops-containers
status: 4 of 4 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 5th chapter added 2026-09-08 (Docker and Containers Fundamentals, T-2208), a true Junior on-ramp per the repository's expanded Junior-to-Staff positioning
last_updated: 2026-09-08
---

# DevOps & Containers

Kubernetes objects/scheduling/networking, resource limits and probes, container image internals, and CI/CD pipeline design — the operational mechanics a backend engineer runs day-to-day. Split out of `cloud/` per Section 3.3.

> **Phase 3 update (2026-09-03).** This domain's full existing content (4 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 4 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (an overhead-projector-transparency analogy for image layers plus a curtain-and-budget analogy for namespaces/cgroups, a restaurant-shift-manager analogy for Deployments/ReplicaSets/Services, a storage-unit-and-loose-items analogy for `OutOfMemoryError` vs. OOM kill plus a manager-check-in analogy for probes, and a restaurant-new-menu-rollout analogy for rolling/blue-green/canary deployments). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`14-devops-containers` is now fully L1–L4 (4/4)** — the twelfth fully-retrofitted domain in the syllabus.
>
> **Junior Fundamentals gap closed (2026-09-08).** The Phase 5 retrofit above added intuition for existing Senior-level topics; [Containers & Image Internals](container-image-internals.md) — its title says "internals" — never taught what a container or an image actually *is* first, because this domain's original scope assumed that baseline already. Found during a repository-wide audit (see `syllabus/02-java/INDEX.md`'s matching note) after the user asked whether this repository genuinely served a low-to-high-seniority reader yet. [Docker and Containers Fundamentals](docker-and-containers-fundamentals.md) (T-2208, reserved range `T-2200`–`T-2299`) closes it, with a real image built and run against the actual Docker Engine (29.6.2) — including a genuine, observed proof of container network isolation (a container reachable via `docker exec` from inside its own network namespace, but completely unreachable from the host without an explicit `-p` port mapping).

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-2208 | Docker and Containers Fundamentals | L1, L2 — fully written, real executed demo (2026-09-08) | `syllabus/14-devops-containers/docker-and-containers-fundamentals.md` |
| T-1001 | Containers & Image Internals | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/container-image-internals.md` |
| T-1002 | Kubernetes Objects, Scheduling, and Networking | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md` |
| T-1003 | Kubernetes Resource Limits, Probes, and JVM Sizing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md` |
| T-1009 | CI/CD Pipeline Design and Deployment Strategies | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
