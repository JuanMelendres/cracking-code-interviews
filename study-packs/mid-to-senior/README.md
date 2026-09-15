---
title: "Mid → Senior Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-15
---

# Mid → Senior Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/mid-to-senior.md`](../../syllabus/00-overview/learning-paths/mid-to-senior.md) — that path is the *content* (15 domains, in sequence, each with named priority topics and a stated L3 stop point); this is the *schedule* built on top of it, the same relationship [`study-packs/junior-to-mid/`](../junior-to-mid/README.md) has to its own learning path.

## Audience and scope

A working engineer (roughly 2–5 years) who can ship features correctly but hasn't yet built internals depth, production-debugging instinct, and trade-off vocabulary for a Senior loop. This pack assumes [Junior → Mid](../junior-to-mid/README.md) is already solid — OOP, collections usage, SQL basics, a first Spring/REST app, and Docker basics are all assumed, not retaught. It stops at L3 (Senior — internals, performance reasoning, production debugging) for every domain; L4 systemic/organizational judgment is [Senior → Staff](../../syllabus/00-overview/learning-paths/senior-to-staff.md)'s job, not this pack's.

**No duplicated content.** Every week below links to its domain's real `syllabus/` `INDEX.md` and named priority-topic chapters, real `practice/` demos where they exist, and real, already-diagnosed [`production-cookbook/`](../../production-cookbook/README.md) entries — per this repository's own no-duplication rule and the learning path's own stated cross-reference principle (§"Pair every domain with real production debugging").

## Why this exists

`study-packs/week-01` through `week-25` are Plan A/B of the [Interview Emergency Sprint](../../syllabus/00-overview/learning-paths/interview-emergency-sprint.md) — a time-boxed, urgent-timeline program with its own sequencing logic, not the standard Mid → Senior progression. A reader following the Mid → Senior learning path had no operational, week-by-week layer of its own until now — this pack fills that gap the same way `study-packs/junior-to-mid/` did for the Junior → Mid path. See [`study-packs/README.md`](../README.md) for how all three programs relate.

## Weeks

| Week | Theme | Domain(s) | Estimated hours |
|---|---|---|---|
| [01](week-01/README.md) | Concurrency internals | Java — Concurrency | 8–10h |
| [02](week-02/README.md) | JVM internals | Java — JVM Internals | 8–10h |
| [03](week-03/README.md) | Spring internals | Spring | 6–8h |
| [04](week-04/README.md) | Database internals | Databases | 8–10h |
| [05](week-05/README.md) | Testing at Senior depth | Testing | 6–8h |
| [06](week-06/README.md) | Messaging and event-driven systems | Messaging & Event-Driven Systems | 8–10h |
| [07](week-07/README.md) | Distributed systems | Distributed Systems | 8–10h |
| [08](week-08/README.md) | System design | System Design | 8–10h |
| [09](week-09/README.md) | Security and observability | Security + Observability | 8–10h |
| [10](week-10/README.md) | Delivery and architecture | DevOps & Containers + Architecture | 8–10h |
| [11](week-11/README.md) | Software design and API design (advanced) | Software Design + API Design (Advanced) | 8–10h |
| [12](week-12/README.md) | AI/LLM engineering | AI/LLM Engineering | 9–10h |
| [13](week-13/README.md) | Security deep dive: CSRF/CORS and Enterprise SSO | Security (continued) | 8–9h |

**Total: ~101–125 hours across 13 weeks**, matching the learning path's own ~12–13-week, 6–10h/week estimate. Weeks 11 and 12 added 2026-09-12; Week 13 added 2026-09-15 (CSRF/CORS/session security and Enterprise SSO/SAML — both real `12-security` chapters that existed before this pack was ever built, but were never scheduled) — see the learning path's own updated note and `syllabus/00-overview/changelog.md`'s matching 2026-09-15 entry.

## After this pack

[Senior → Staff](../../syllabus/00-overview/learning-paths/senior-to-staff.md) — the direct continuation into L4 systemic and organizational judgment.
