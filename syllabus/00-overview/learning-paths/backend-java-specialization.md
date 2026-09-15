---
title: "Learning Path: Backend Java Specialization"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-15
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Backend Java Specialization

**Audience:** an engineer who wants deep, comprehensive mastery of the primary Java backend track specifically — not the broader system-design/architecture/leadership material [Senior → Staff](senior-to-staff.md) covers, and not the frontend track at all.

**Goal:** full L1–L4 coverage across exactly five domains: Java, Spring, Databases, Messaging & Event-Driven Systems, and Performance & JVM Tuning — the primary Java backend stack this whole repository was originally built around.

**Time budget:** ~8 weeks, intensive (10+ hours/week), or 12–14 weeks part-time.

**Stops at:** L4 throughout — this path assumes the reader wants the full depth of every topic in scope, not a curated subset stopping early.

Each domain below is taken in full — every topic in the domain's own `INDEX.md`, at every mastery level it has (all five domains are fully retrofitted to L1–L4). This path sequences *which domain first* and groups each domain by its internal subdomain structure; it does not re-list individual topics, since that would duplicate each domain's own `INDEX.md` (the canonical, exhaustive list) rather than add sequencing value on top of it.

## Sequence

| # | Domain | Subdomains, in order | Topic count |
|---|---|---|---|
| 1 | [Java](../../02-java/INDEX.md) | `language-core` (22) → `collections` (11) → `concurrency` (15) → `jvm-internals` (13) | 61 |
| 2 | [Spring](../../05-spring/INDEX.md) | Framework/Boot fundamentals → transactions/security → reactive/caching/observability/testing | 12 |
| 3 | [Databases](../../06-databases/INDEX.md) | JPA/Hibernate mechanics → indexing/query planning → concurrency/replication/migration | 17 |
| 4 | [Messaging & Event-Driven Systems](../../09-messaging-event-driven/INDEX.md) | Kafka core mechanics → delivery/lag/schema → event sourcing/integration styles | 12 |
| 5 | [Performance & JVM Tuning](../../16-performance-jvm/INDEX.md) | Profiling → benchmarking → capacity planning | 3 |

*(Topic counts verified directly against each domain's file system on 2026-09-08, correcting stale counts — 30 Java, 13 Databases — carried since this path's original 2026-09-05 authoring, before the Junior Fundamentals and SQL Fundamentals additions grew both domains. Re-verified 2026-09-15 during a `syllabus/00-overview/` meta-documentation audit: Java had grown from 52 to 61 chapters (gap-audit additions through 2026-09-10), Spring from 10 to 12 (2026-09-10/11 gap-audit additions), Databases from 15 to 17 (2026-09-10 gap-audit additions), and Messaging & Event-Driven Systems from 9 to 12 (2026-09-11 gap-audit additions) — none of which had been folded into this table since its 2026-09-08 correction. See `syllabus/00-overview/changelog.md`'s matching 2026-09-15 entry.)*

**Why this order:** Java's own subdomain order (language fundamentals → collections → concurrency → JVM internals) is itself a dependency chain — collections depend on `equals`/`hashCode` from language-core, concurrency depends on the memory model concepts that also explain collection thread-safety, and JVM internals (GC, JIT) is the layer underneath everything above it. Spring follows Java directly since its transaction and bean-lifecycle mechanics assume the concurrency and reflection material just covered. Databases follows Spring because its ORM chapters (JPA entity lifecycle, N+1) directly build on Spring's own persistence-layer chapters. Messaging and Performance/JVM close the path — both are genuinely usable once the rest of the stack is solid, and Performance/JVM's profiling and capacity-planning material is easiest to internalize once there's a real, complete backend system's worth of prior material to apply it to.

## Completion criteria

- Every topic across all five domains passes its own chapter's L1–L4 Mastery Checklist (see the [Mastery Model](../mastery-model.md)).
- Can trace a single realistic request through the full stack from memory — a Spring controller, through a transactional service method, an indexed database query, and (for at least one worked example) a Kafka event published as a side effect — naming the specific chapter that covers each hop.
- Has built or extended at least one real demo from each domain's `practice/` companion code, not just read the chapter describing it.

## Weekly study pack

[`study-packs/backend-java-specialization/`](../../../study-packs/backend-java-specialization/) turns this sequence into a scheduled, 9-week program — reading assignments naming each subdomain's real topics, hands-on exercises against real `practice/` demos, and a lightweight mock-interview checkpoint per week — the same operational layer [`study-packs/junior-to-mid/`](../../../study-packs/junior-to-mid/) and [`study-packs/mid-to-senior/`](../../../study-packs/mid-to-senior/) provide for their own paths. This path's five domains are the *content*; the study pack is the *schedule*.

## Related paths

- [Mid → Senior](mid-to-senior.md) covers a broader set of domains at L3 only — a better fit if the goal is general Senior-level breadth rather than Java-stack specialization specifically.
- [Senior → Staff](senior-to-staff.md) is the natural follow-on once this path's L4 depth is solid and the goal shifts to systemic/organizational judgment beyond the Java stack itself.
