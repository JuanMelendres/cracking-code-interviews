---
title: "Junior → Mid, Week 7 — Ship Something"
document_type: study-pack
week: 7
track: junior-to-mid
status: draft
estimated_hours: 8
---

# Week 7 — Ship Something

## Weekly Outcome

By the end of this week you can build a small Spring Boot REST API from scratch — a controller, a service, a repository, correctly wired with constructor injection — design its endpoints using correct resource naming and HTTP status codes, and package it into a Docker image you build and run yourself.

## Why This Week Matters

This is the closing week of the pack: every prior week's material (OOP, collections, SQL, testing) comes together into one real, running application. [Spring MVC Fundamentals](../../../syllabus/05-spring/spring-mvc-fundamentals.md) and [REST API Fundamentals](../../../syllabus/07-api-design/rest-api-fundamentals.md) were both written this session specifically because the original repository jumped straight into Spring bean lifecycle and auto-configuration without ever teaching `@Controller`/`@Service`/`@Repository` first. Docker closes the loop by shipping what you just built.

## Prerequisites

Weeks 1–6 — in particular, comfortable writing a class with a constructor (Week 1) and a `SELECT`/`JOIN` (Week 5) before this week's controller-service-repository stack.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Spring MVC Fundamentals](../../../syllabus/05-spring/spring-mvc-fundamentals.md) (T-2203) — read in full, reproduce the Task API demo, including the real `-parameters` bug and its fix |
| Wed | [Spring Framework vs. Spring Boot](../../../syllabus/05-spring/spring-framework-vs-spring-boot.md) (T-506/T-501) |
| Thu | [REST API Fundamentals](../../../syllabus/07-api-design/rest-api-fundamentals.md) (T-2205) — read in full, reproduce the Book API demo |
| Fri–Sat | [Docker and Containers Fundamentals](../../../syllabus/14-devops-containers/docker-and-containers-fundamentals.md) (T-2208) — build and run the demo image yourself |
| Sun | Review checklist below, then the full-pack retrospective |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Spring MVC Fundamentals (T-2203) | [`syllabus/05-spring/spring-mvc-fundamentals.md`](../../../syllabus/05-spring/spring-mvc-fundamentals.md) |
| 2 | Spring Framework vs. Spring Boot (T-506/T-501) | [`syllabus/05-spring/spring-framework-vs-spring-boot.md`](../../../syllabus/05-spring/spring-framework-vs-spring-boot.md) |
| 3 | REST API Fundamentals (T-2205) | [`syllabus/07-api-design/rest-api-fundamentals.md`](../../../syllabus/07-api-design/rest-api-fundamentals.md) |
| 4 | Docker and Containers Fundamentals (T-2208) | [`syllabus/14-devops-containers/docker-and-containers-fundamentals.md`](../../../syllabus/14-devops-containers/docker-and-containers-fundamentals.md) |

## Hands-On Exercises

- [`practice/java/spring-mvc-fundamentals/`](../../../practice/java/spring-mvc-fundamentals/) — a real Task API, including `curl-transcript-before-fix.txt` (a genuine 500 error from a missing `-parameters` compiler flag) and `curl-transcript.txt` (the same request after the real fix). Reproduce both — the bug is as instructive as the fix.
- [`practice/java/rest-api-fundamentals/`](../../../practice/java/rest-api-fundamentals/) — a real Book API with a full `curl-transcript.txt` covering create/read/update/delete and correct status codes.
- [`practice/docker-fundamentals/`](../../../practice/docker-fundamentals/) — build the real `Dockerfile` yourself against Docker Engine, and reproduce the network-isolation proof (`curl` from the host fails without `-p`, `docker exec ... curl localhost:8080` succeeds from inside the container).
- T-506/T-501's own practice material on auto-configuration.

## Interview Answer Drills

Answer, out loud: "why did removing `@PathVariable("id")`'s explicit name break the Task API?" (the real bug from T-2203) and "why is `PUT` idempotent but `POST` isn't?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week — the hands-on exercises above are the week's coding work.

## System Design Exercise

Design the endpoints (resource paths, HTTP verbs, status codes) for a small API of your own choosing (e.g., a to-do list, a bookmark manager) before building it — a lightweight first exposure to the design thinking Mid → Senior's system-design material builds on directly.

## Behavioral Exercise

None this week — see Week 1's note on where behavioral preparation lives in this repository.

## Mock Interview

Self-check: build one new endpoint (not from either demo) on top of the Task or Book API from scratch, then containerize it, timed at 60 minutes total.

## Review Checklist

- [ ] Reproduced both the Task API's real bug and its fix.
- [ ] Reproduced the Book API's full CRUD transcript with correct status codes.
- [ ] Built and ran the Docker demo yourself, confirmed the network-isolation behavior firsthand.

## Completion Criteria

- [ ] Built a small Spring Boot REST API from scratch (not copied) with at least one `GET` and one `POST` endpoint, correctly wired with constructor injection.
- [ ] Can state, unprompted, what `spring-boot-starter-web` auto-configures that plain Spring would require manual setup for.
- [ ] Built and ran a Docker image of your own small application, confirmed it runs correctly with a port mapped to the host.

## Retrospective

This is the pack's final retrospective: review every prior week's retrospective note and confirm each one is now resolved (no more equals/hashCode mistakes, binary-search bugs, or collection-choice errors) before moving on to [Mid → Senior](../../../syllabus/00-overview/learning-paths/mid-to-senior.md).

## Next Week

This is the last week of the Junior → Mid pack. Continue with [Mid → Senior](../../../syllabus/00-overview/learning-paths/mid-to-senior.md).
