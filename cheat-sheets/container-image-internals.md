---
title: "Cheat Sheet: Containers & Image Internals"
slug: container-image-internals
document_type: cheat-sheet
domain: cloud
topic_id: T-1001
canonical: ../handbook/cloud/container-image-internals.md
last_updated: 2026-09-01
---

# Containers & Image Internals

**Canonical chapter:** [`handbook/cloud/container-image-internals.md`](../handbook/cloud/container-image-internals.md)

## Core Mental Model

A container image is not a single file — it is a stack of read-only, content-addressed filesystem layers plus metadata (entrypoint, environment, exposed ports). A running container adds exactly one more layer: a thin, writable layer unique to that instance. Two containers from the same image share every read-only layer on disk and only diverge in their own writable layer — copy-on-write. A running container is *not* a lightweight VM: there is no hypervisor, no separate kernel. It is an ordinary Linux process placed inside **namespaces** (so it cannot *see* most of what the host can see) and constrained by **cgroups** (so it cannot *consume* more than allowed) — isolation implemented entirely with mechanisms the host kernel provides.

## Essential Definitions

- **Layer** — an immutable, content-addressed filesystem diff (SHA-256 digest); the build cache is keyed by instruction + input content + parent layer.
- **Multi-stage build** — more than one `FROM`, each a named stage; a later stage `COPY --from=build <path>` pulls forward only named artifacts, structurally discarding build tooling.
- **Namespaces** (PID, network, mount, UTS, IPC, user) — isolation: what a process can *see*.
- **cgroups** (cpu, memory, io, pids) — resource limits: what a process can *consume*, kernel-enforced.
- **overlayfs** — shared read-only image layers + one writable layer per container; a write triggers copy-up, never touching the shared layers.

## Decision Table

1. Does the final image need anything beyond one or a few named build artifacts? No → multi-stage build; there is essentially no case for shipping build tooling to production.
2. Does the build have a real dependency-resolution step separate from compiling? Yes → give it its own layer, ordered before source is copied in.
3. Does the runtime need a shell for legitimate operational reasons? Yes → minimal but shell-having base (`-alpine`/`-slim`); No, and security matters more → distroless.
4. Is the team equipped to debug a shell-less container (ephemeral debug containers, `kubectl debug`)? No → defer distroless until that tooling exists.

**Trade-offs:**

| Choice | Helps | Hurts |
|---|---|---|
| Single-stage build | Simpler, one `FROM` | Ships build tooling to production — measured 3.1x larger |
| Multi-stage build | Small, minimal runtime image | One more `COPY --from=` to get right |
| Splitting dependency resolution into its own layer | Fast rebuilds on source-only changes (2.4s vs. 21.5s) | Slightly slower cold build |
| Distroless final image | Smallest attack surface, no shell for an attacker | No shell for you either — needs ephemeral debug containers |

## Key Numbers (real, executed Docker 29.6.2, `overlayfs`)

```
Layered Dockerfile, source-only rebuild: 2.4 seconds (dependency layer CACHED)
Multi-stage Dockerfile (combined RUN), source-only rebuild: 21.5 seconds (full re-resolve)

Container `ps aux`: shows only its own single process at PID 1
Host at that instant: 537 processes running
`--memory=64m` container's /sys/fs/cgroup/memory.max: exactly 67108864
```

## Common Pitfalls

- Believing a container is "a lightweight VM" — it shares the host kernel; no virtualized hardware or second kernel boot.
- Collapsing dependency-resolution and source-compile into one `COPY . .` + one `RUN` "for simplicity," silently paying full dependency re-resolution on every source change.
- Assuming `docker build --no-cache` is the only way layers get invalidated — changing an *earlier* instruction's output invalidates every instruction after it regardless.
- Running `RUN rm -rf ...` at the end of a single-stage Dockerfile to "clean up" — does not shrink the image; deleted files' bytes still exist in the earlier layer.

## Interview Answer Skeleton

**30-sec:** An image is a stack of immutable, content-addressed layers plus run metadata; a container is a normal Linux process placed in its own namespaces (isolation) and constrained by a cgroup (resource limits) — not a lightweight VM.

**2-min:** Add the build cache's exact keying rule (instruction + input content + parent layer, top to bottom, one miss cascades forward), the measured 3.1x size reduction from multi-stage builds, and the measured PID-namespace/cgroup proofs (own PID 1 vs. host's 537 processes; `--memory=64m` becoming exactly `67108864`).

**Whiteboard:** Two stacks of three shared read-only layers (base OS, JRE, app.jar) each topped by a small private "writable layer" box, both stacks sitting on one "host kernel" box below — annotate "no separate kernel, no hypervisor; isolation from namespaces, limits from cgroups, both from this one kernel."

**Staff-level framing:** Base-image policy is a governance decision, not a per-service one — argue for a small, centrally-maintained set of approved base images with an explicit exception process, and speak to the migration cost/risk of retroactively changing that standard.

## Production Warning Signs

- A CI pipeline's Docker build step regressing from 90 seconds to 9 minutes after a routine dependency bump — a Dockerfile "simplification" merged the separate dependency-resolution and source-compile layers into one `COPY . .` + one `RUN`, collapsing two independent cache keys into one.
- An oversized image where `docker history` was never checked to see which specific instruction is responsible for the bulk of the size.
- An Alpine or distroless base swapped in without testing — musl-based Alpine has caused real, subtle native-library breakage for some JVM/native-dependency combinations.

## Related

- `handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md`
- `handbook/cloud/kubernetes-objects-scheduling-and-networking.md`
- `handbook/security/supply-chain-security-sbom-and-dependency-risk.md`
