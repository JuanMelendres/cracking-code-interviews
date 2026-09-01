---
title: "Flashcards: Containers & Image Internals"
slug: container-image-internals
document_type: flashcard-deck
domain: cloud
topic_id: T-1001
canonical: ../handbook/cloud/container-image-internals.md
last_updated: 2026-09-01
---

# Flashcards: Containers & Image Internals

**Canonical chapter:** [`handbook/cloud/container-image-internals.md`](../handbook/cloud/container-image-internals.md)

## Card: Why deleting a file later doesn't shrink an image

**Prompt:**
Why does deleting a file in a later `RUN` instruction not shrink a single-stage image?

**Answer:**
Union filesystem layers are additive and immutable; the file's bytes remain in the earlier layer that created it, and the delete is only a whiteout marker in the new layer.

**Why it matters:**
Explains why "just delete it in a later step" doesn't actually shrink an image — a common size-optimization mistake engineers reach for first, instead of multi-stage builds.

**Common trap:**
Believing a later `RUN rm` instruction removes the file's bytes from the final image rather than merely hiding it behind a new layer.

**Related:**
[handbook/cloud/container-image-internals.md](../handbook/cloud/container-image-internals.md)

## Card: Namespaces versus cgroups

**Prompt:**
What are the two independent kernel mechanisms that together implement container isolation and limits?

**Answer:**
Namespaces (isolation — what a process can see) and cgroups (limits — what a process can consume).

**Why it matters:**
Separates two frequently conflated container primitives into their real, distinct concerns — the mechanism every Kubernetes and CI/CD conversation quietly assumes.

**Common trap:**
Treating "containers" as one monolithic isolation mechanism instead of two separate kernel features doing different jobs.

**Related:**
[handbook/cloud/container-image-internals.md](../handbook/cloud/container-image-internals.md)

## Card: Why splitting the dependency layer speeds up rebuilds

**Prompt:**
Why does splitting dependency resolution into its own Dockerfile layer speed up rebuilds?

**Answer:**
The build cache is keyed by instruction plus input content; if the dependency manifest (e.g., `pom.xml`) hasn't changed, that layer stays cached even when application source changes in a later, separately-cached layer.

**Why it matters:**
Explains the concrete mechanism behind a measured rebuild-speed difference, rather than treating Docker's build cache as a black box.

**Common trap:**
Ordering a Dockerfile's `COPY`/`RUN` steps by convenience instead of by change frequency, forcing an unnecessarily expensive cache invalidation on every source change.

**Related:**
[handbook/cloud/container-image-internals.md](../handbook/cloud/container-image-internals.md)
