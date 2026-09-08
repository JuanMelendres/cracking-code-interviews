---
title: "Junior → Mid, Week 8 — Manifest"
week: 8
track: junior-to-mid
last_reviewed: 2026-09-08
---

# Week 8 — Manifest

**Topics:** T-2203 (Spring MVC Fundamentals), T-506/T-501 (Spring Framework vs. Spring Boot), T-2205 (REST API Fundamentals), T-2208 (Docker and Containers Fundamentals). **Track:** Junior → Mid, Week 8 of 8 (final week).
**Files:** 1 (+ this manifest) — no chapter content duplicated, per this repository's no-duplication rule.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercises, review checklist |

## Verification

| Demo | Location | Status |
|---|---|---|
| Task API (`TaskApplication`, `Task`, `TaskRepository`, `TaskService`, `TaskController`) | `practice/java/spring-mvc-fundamentals/` | Real, compiled, executed — includes a genuine `500` error transcript (`curl-transcript-before-fix.txt`) caused by a missing `-parameters` compiler flag, and the real fixed transcript (`curl-transcript.txt`), both confirmed when T-2203 was written this session (2026-09-08) |
| Book API (`Book`, `BookApplication`, `BookRepository`, `BookController`) | `practice/java/rest-api-fundamentals/` | Real, compiled, executed — full CRUD `curl-transcript.txt`, confirmed when T-2205 was written this session (2026-09-08) |
| Docker demo (`Dockerfile`, `Server.java`) | `practice/docker-fundamentals/` | Real, built and run against Docker Engine 29.6.2 — network-isolation proof captured in `docker-transcript.txt`, confirmed when T-2208 was written this session (2026-09-08) |
| T-506/T-501's practice material | linked from the chapter itself | Predates this session; not re-verified here |

## Scope note

Closing week of the 7-week pack — deliberately the heaviest hands-on week, integrating Spring, REST design, and Docker into one working, shippable artifact. The real 500-error bug in the Task API demo is kept intentionally, not smoothed over, since debugging a real Spring configuration failure is itself part of what this week teaches.

## Integrity note

All three practice demos' real transcripts (Task API's before/after fix, Book API's CRUD transcript, Docker's network-isolation proof) were verified directly against captured evidence when their respective chapters were written this session; T-506/T-501 predates this session and is cited by chapter link only.
