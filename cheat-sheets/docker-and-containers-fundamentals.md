---
title: "Cheat Sheet: Docker and Containers Fundamentals"
slug: docker-and-containers-fundamentals
document_type: cheat-sheet
domain: 14-devops-containers
topic_id: T-2208
canonical: ../syllabus/14-devops-containers/docker-and-containers-fundamentals.md
last_updated: 2026-09-08
---

# Docker and Containers Fundamentals

**Canonical chapter:** [`syllabus/14-devops-containers/docker-and-containers-fundamentals.md`](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Core Mental Model

A container is an ordinary process, isolated via kernel namespaces/cgroups — not a separate machine. An image is a read-only template; a container is one running instance of it, same relationship as class and object.

## Essential Definitions

- **Image** — read-only build template (a `Dockerfile`'s output).
- **Container** — one running (or stopped) instance created from an image.
- **`docker build`** — reads a `Dockerfile`, produces an image.
- **`docker run`** — creates and starts a container from an image.
- **Port publishing (`-p host:container`)** — explicitly bridges a container's isolated network namespace to the host; `EXPOSE` alone is documentation, not a grant.

## Decision Table

| Situation | Fact |
|---|---|
| Container vs. VM | Container shares host kernel (namespaces/cgroups); VM runs its own full guest kernel |
| Port not reachable from host | Check for a missing `-p` mapping first — `EXPOSE` doesn't publish |
| Slow rebuilds | Check `Dockerfile` instruction order — put stable steps before volatile ones for layer-cache reuse |
| Container stopped | Its port mapping and any un-persisted filesystem changes go with it |

## Common Pitfalls

- Assuming `EXPOSE` makes a port reachable from the host — it doesn't; `-p` does.
- Confusing a container with a VM — container isolation is kernel-feature-based, not a separate OS.
- One broad `COPY` of an entire project directory, defeating layer caching on every source change.
- Forgetting a stopped container still exists until `rm`'d — `docker ps` (no `-a`) hides it.

## Interview Answer Skeleton

**30-sec:** A container is an isolated process on the host's own kernel (namespaces/cgroups), not a separate machine like a VM. An image is the read-only template; a container is a running instance of it. Ports must be explicitly published with `-p` to be reachable from the host.

**2-min:** Add the real proof pattern: a container without `-p` is genuinely running and reachable via `docker exec` from inside its own network namespace, but completely unreachable from the host — concrete evidence of the isolation boundary, not just a rule to memorize.

**Whiteboard:** Draw the host kernel as one big box, with two smaller isolated boxes (containers) inside it sharing that kernel — next to it, draw a VM as an entirely separate box with its own kernel stacked on top of virtualized hardware.

**Staff-level framing:** Containers' sub-second startup (vs. a VM's full-OS boot) is why they became the default unit of horizontally-scaled deployment — the underlying reason orchestration platforms like Kubernetes exist at all.

## Related

- syllabus/14-devops-containers/container-image-internals.md
- syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md
- syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md
