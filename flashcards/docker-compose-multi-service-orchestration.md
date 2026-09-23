---
title: "Flashcards: Docker Compose Multi-Service Orchestration"
slug: docker-compose-multi-service-orchestration
document_type: flashcard-deck
domain: 14-devops-containers
topic_id: T-2428
canonical: ../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md
last_updated: 2026-09-23
---

# Flashcards: Docker Compose Multi-Service Orchestration

**Canonical chapter:** [`syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md`](../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md)

## Card: What `depends_on` actually guarantees

**Prompt:**
Does `depends_on: [db]` guarantee `db` is ready to accept connections before the dependent service starts?

**Answer:**
No — it only guarantees `db`'s container has started. A real demo proved this directly: on a fresh Postgres volume, an `api` service's startup connection attempt failed with a real `Connection refused`, even though `docker compose`'s own log showed `db-1 Started` before `api-1 Starting`.

**Why it matters:**
This gap is real and reproducible on a fresh volume — a common, genuine source of "works on retry, fails on a fresh clone or CI run" bugs, not a theoretical edge case.

**Common trap:**
Assuming `depends_on` means "wait until ready" rather than "wait until started."

**Related:**
[Core Concepts](../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md#core-concepts)

## Card: The real fix for the depends_on gap

**Prompt:**
How do you make Compose genuinely wait for a dependency to be ready, not just started?

**Answer:**
Add a `healthcheck` to the dependency (`pg_isready` for Postgres) and use the map form of `depends_on` with `condition: service_healthy` on the dependent service. A real demo confirmed this: with the fix in place, `db-1` reached `Healthy` before `api-1` ever started, and the connection succeeded on the first attempt.

**Why it matters:**
This is the correct, orchestration-level fix — not an application-level `sleep` or retry loop working around a guarantee Compose was never actually making.

**Common trap:**
Reaching for a `sleep` or retry loop in the application instead of the health-check-based fix Compose already provides.

**Related:**
[Internal Implementation](../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md#internal-implementation)

## Card: Why a stale volume can hide this bug

**Prompt:**
Why might this exact `depends_on` race never show up on a developer's own machine, but reliably show up in CI or for a new team member?

**Answer:**
A developer's local Postgres volume is usually already initialized from a previous run, so `initdb` doesn't need to run again and the database becomes ready almost instantly — masking the race. A genuinely fresh checkout or CI runner starts from a truly clean volume every time, where `initdb`'s real initialization time reliably wins the race against the plain `depends_on` guarantee.

**Why it matters:**
Explains a real, confusing "works on my machine" pattern honestly, rather than treating it as user error.

**Common trap:**
Concluding the bug doesn't exist because it can't be reproduced on an already-initialized local volume.

**Related:**
[Production Scenarios](../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md#production-scenarios)
