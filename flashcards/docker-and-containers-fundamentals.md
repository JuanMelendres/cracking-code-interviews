---
title: "Flashcards: Docker and Containers Fundamentals"
slug: docker-and-containers-fundamentals
document_type: flashcard-deck
domain: 14-devops-containers
topic_id: T-2208
canonical: ../syllabus/14-devops-containers/docker-and-containers-fundamentals.md
last_updated: 2026-09-08
---

# Flashcards: Docker and Containers Fundamentals

**Canonical chapter:** [`syllabus/14-devops-containers/docker-and-containers-fundamentals.md`](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Card: Container vs. virtual machine

**Prompt:**
What's the core mechanical difference between a container and a virtual machine?

**Answer:**
A container is an isolated process sharing the host's own kernel, using namespaces and cgroups for isolation. A VM runs its own complete guest operating system and kernel on virtualized hardware — a fundamentally heavier isolation boundary.

**Why it matters:**
The single most common "what is a container" interview question, and the real answer explains why containers start so much faster.

**Common trap:**
Describing a container as "a lightweight VM" — it isn't a VM at all; it's an isolated process on the same kernel.

**Related:**
[Docker and Containers Fundamentals](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Card: Image vs. container

**Prompt:**
What's the difference between a Docker image and a Docker container?

**Answer:**
An image is a read-only template. A container is one running (or stopped) instance created from that image — the same relationship as a class and an object.

**Why it matters:**
A basic but frequently-checked distinction, and the class/object analogy makes it stick.

**Common trap:**
Using "image" and "container" interchangeably.

**Related:**
[Docker and Containers Fundamentals](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Card: EXPOSE vs. -p

**Prompt:**
Does `EXPOSE 8080` in a Dockerfile make a container's port 8080 reachable from your host machine?

**Answer:**
No — `EXPOSE` is documentation only. Reaching the port from the host requires explicitly publishing it with `-p host_port:container_port` on `docker run`.

**Why it matters:**
A genuinely common source of "why can't I reach my container" confusion.

**Common trap:**
Assuming `EXPOSE` alone grants host access to the port.

**Related:**
[Docker and Containers Fundamentals](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Card: Proving container network isolation

**Prompt:**
A container is running without a `-p` mapping. Is it actually running and serving requests?

**Answer:**
Yes — provably so: `docker exec <container> curl localhost:8080` succeeds from inside the container's own network namespace, while the identical `curl` from the host fails with connection refused. The server never stopped; the host simply has no route to it.

**Why it matters:**
Concrete proof of the isolation boundary, not just an assertion — a strong answer demonstrates rather than just states this.

**Common trap:**
Assuming a container unreachable from the host must be broken or not running.

**Related:**
[Docker and Containers Fundamentals](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)

## Card: Dockerfile layer caching and instruction order

**Prompt:**
Why does the order of instructions in a Dockerfile affect build speed?

**Answer:**
Each instruction that changes the filesystem produces a cached layer. A change invalidates that layer and every layer after it. Putting stable instructions (like dependency installation) before volatile ones (like copying source code) maximizes cache reuse on rebuilds.

**Why it matters:**
A real, common, and easily-fixed cause of slow CI pipelines.

**Common trap:**
Copying the entire project directory in one early instruction, invalidating the cache on every source change.

**Related:**
[Docker and Containers Fundamentals](../syllabus/14-devops-containers/docker-and-containers-fundamentals.md)
