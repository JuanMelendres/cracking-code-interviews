---
title: "Backend Java Specialization Study Pack — Index"
document_type: study-pack-index
status: draft
last_updated: 2026-09-08
---

# Backend Java Specialization Study Pack

The scheduled, week-by-week version of [`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../syllabus/00-overview/learning-paths/backend-java-specialization.md) — that path is the *content* (five domains, taken in full, at every mastery level each domain has); this is the *schedule* built on top of it, the same relationship [`study-packs/junior-to-mid/`](../junior-to-mid/README.md) and [`study-packs/mid-to-senior/`](../mid-to-senior/README.md) have to their own learning paths.

## Audience and scope

An engineer who wants deep, comprehensive mastery of the primary Java backend stack specifically — Java itself, Spring, databases, messaging, and JVM/performance tuning — rather than the broader system-design/architecture/leadership material [Mid → Senior](../../syllabus/00-overview/learning-paths/mid-to-senior.md) and [Senior → Staff](../../syllabus/00-overview/learning-paths/senior-to-staff.md) cover, and not the frontend track at all. Unlike those two packs, this one does not stop at L3: it schedules **every topic in each of the five domains' own `INDEX.md`, at every mastery level that domain has** (all five are fully retrofitted to L1–L4), per the learning path's own stated scope ("full L1–L4 coverage across exactly five domains").

**No duplicated content.** This pack does not re-list every topic inside each domain — the learning path itself makes that same choice, deliberately, since that would duplicate each domain's own `INDEX.md` (the canonical, exhaustive list) rather than add scheduling value on top of it. Each week below names its subdomain's real topics as a reading list and links back to the domain's own `INDEX.md` as the exhaustive source, then links to real, already-existing `syllabus/` chapters and `practice/` demos — per this repository's no-duplication rule.

## Why this exists

The [Backend Java Specialization](../../syllabus/00-overview/learning-paths/backend-java-specialization.md) learning path (added 2026-09-05, per the syllabus transformation plan §6) sequences five domains by dependency order but, like the other learning paths before their own study packs existed, had no operational week-by-week layer of its own. This pack fills that gap the same way `study-packs/junior-to-mid/` and `study-packs/mid-to-senior/` did for their paths — turning "which domain first, and why" into a schedule with reading assignments, hands-on exercises, and review checkpoints.

## A note on the schedule's precision

The learning path states its own time budget as a range, not a single number: "~8 weeks, intensive (10+ hours/week), or 12–14 weeks part-time." This pack schedules **9 weeks** — one per subdomain-sized unit of work — which sits close to the intensive end for the lighter weeks (Spring, Performance & JVM Tuning) but realistically needs the part-time framing's extra runway for the heavier ones: Java's `language-core` (17 chapters) and `concurrency` (13 chapters) subdomains, and the Databases domain's 15 chapters split across two weeks, are each large enough that a reader going at a sustainable pace should expect to stretch that week to a week and a half rather than compress it to fit 7 days. Treat the 9-week structure below as a sequencing device, not a claim that every week fits in exactly 7 days at the stated hour count — the source path itself declines to give one number, and this schedule should not pretend to more precision than that.

## Weeks

| Week | Theme | Domain / Subdomain(s) | Topics | Estimated hours |
|---|---|---|---|---|
| [01](week-01/README.md) | Java language fundamentals through advanced core | Java — `language-core` | 17 | 12–15h |
| [02](week-02/README.md) | Collections, from usage to internals | Java — `collections` | 10 | 8–10h |
| [03](week-03/README.md) | Concurrency internals | Java — `concurrency` | 13 | 10–13h |
| [04](week-04/README.md) | JVM internals | Java — `jvm-internals` | 12 | 10–13h |
| [05](week-05/README.md) | Spring, end to end | Spring | 10 | 8–10h |
| [06](week-06/README.md) | Databases — JPA and Hibernate mechanics | Databases (part 1 of 2) | 7 | 8–10h |
| [07](week-07/README.md) | Databases — indexing, query planning, and operational concerns | Databases (part 2 of 2) | 8 | 8–10h |
| [08](week-08/README.md) | Messaging and event-driven systems | Messaging & Event-Driven Systems | 9 | 8–10h |
| [09](week-09/README.md) | Performance and JVM tuning | Performance & JVM Tuning | 3 | 5–7h |

**Total: ~77–104 hours across 9 weeks** — consistent with the learning path's own ~8-week intensive estimate (10+ h/week ≈ 80+ h) at the lower end, and its 12–14-week part-time estimate at the upper end once the heavier weeks above are given their natural extra runway.

## After this pack

[Senior → Staff](../../syllabus/00-overview/learning-paths/senior-to-staff.md) — the learning path's own stated next step once this path's L4 depth across the five backend domains is solid and the goal shifts to systemic and organizational judgment beyond the Java stack itself. [Mid → Senior](../../syllabus/00-overview/learning-paths/mid-to-senior.md) is the better fit instead if the goal is general Senior-level breadth (12 domains at L3) rather than Java-stack specialization depth — see that learning path's own "Related paths" section.
