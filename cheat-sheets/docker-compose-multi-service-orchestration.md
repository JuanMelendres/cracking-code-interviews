---
title: "Cheat Sheet: Docker Compose Multi-Service Orchestration"
slug: docker-compose-multi-service-orchestration
document_type: cheat-sheet
domain: 14-devops-containers
topic_id: T-2428
canonical: ../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md
last_updated: 2026-09-23
---

# Docker Compose: Multi-Service Orchestration

**Canonical chapter:** [`syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md`](../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md)

## Core Mental Model

A restaurant's doors unlocking (container started) is not the same event as the kitchen being ready to cook (application ready) — `depends_on` alone only confirms the doors unlocked.

## Essential Definitions

- **Service-name networking** — every service in a `docker-compose.yml` is reachable by its own name, via real DNS on Compose's private network — never an IP address or `localhost`.
- **`depends_on` (list form)** — waits only for the named service's container to *start*.
- **`depends_on` (map form + `condition: service_healthy`)** — waits for the named service's own `healthcheck` to *pass*, a genuine readiness signal.

## Decision Table

| Situation | Correct approach |
|---|---|
| Dependency has real, non-instant startup work (a database running `initdb`) | Add a `healthcheck` + `condition: service_healthy` |
| Dependency is ready the instant its process starts (a static file server, most caches) | Plain `depends_on` list form is enough — a health check here is unneeded ceremony |
| One service needs to reach another | Use the other service's own name as the hostname — never an IP or `localhost` |

## Common Pitfalls

- Assuming `depends_on`'s plain list form means "wait until ready" — it only waits for the container to start.
- "Fixing" a startup race with a `sleep` or retry loop instead of a real `healthcheck`.
- Not testing against a genuinely fresh volume (`docker compose down -v`) — a warm, already-initialized volume can mask this exact bug.

## Interview Answer Skeleton

**30-sec:** Compose gives every service real DNS resolution by name; `depends_on`'s plain form only waits for a dependency's container to start, not for the application inside it to be ready — a real, common gotcha for databases, fixed with a `healthcheck` plus `condition: service_healthy`.

**2-min:** Add: real demo proof — a fresh-volume Postgres dependency fails a startup connection under plain `depends_on` (`Connection refused`) and connects on the first attempt once a health check is added.

**Staff-level framing:** The same "started" vs. "ready" distinction reappears, with higher stakes, in Kubernetes' own readiness-probe mechanism — this is a recurring, cross-tool orchestration lesson, not a Compose-specific quirk.

## Related

- syllabus/14-devops-containers/docker-and-containers-fundamentals.md
- syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md
