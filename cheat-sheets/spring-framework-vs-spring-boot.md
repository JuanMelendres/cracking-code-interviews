---
title: "Cheat Sheet: Spring Framework vs. Spring Boot"
slug: spring-framework-vs-spring-boot
document_type: cheat-sheet
domain: spring
topic_id: T-506 / T-501
canonical: ../handbook/spring/spring-framework-vs-spring-boot.md
last_updated: 2026-09-02
---

# Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server

**Canonical chapter:** [`syllabus/05-spring/spring-framework-vs-spring-boot.md`](../syllabus/05-spring/spring-framework-vs-spring-boot.md)

## Core Mental Model

Spring Framework is the programming model; Spring Boot is an opinionated assembler that reads your classpath and decides, condition by condition, which pieces of that model to wire up for you. Nothing Boot does is a new mechanism — `@Configuration`, `@Bean`, `@Conditional` are all plain Spring Framework tools, pre-applied at scale.

## Essential Definitions

- **Starter** — a curated, version-aligned dependency bundle (e.g., `spring-boot-starter-web`); contains almost no code, just changes the classpath.
- **Auto-configuration** — ordinary conditional `@Configuration` classes shipped in `spring-boot-autoconfigure`, activating based on the classpath (`@ConditionalOnClass`) and what the application hasn't already defined (`@ConditionalOnMissingBean`).
- **Embedded server** — Tomcat (default) is a library dependency started by the application's own `main()`, inside the application's own JVM process — the application owns the server, not vice versa.

## Decision Table

| Question | Answer |
|---|---|
| Want to override an auto-configured bean? | Define your own bean of the matching type — `@ConditionalOnMissingBean` yields to it, no explicit disable needed |
| Debugging why an auto-configuration did/didn't apply? | Run with `--debug` and read the real conditions-evaluation report |
| Choosing embedded vs traditional WAR deployment? | Default to embedded unless a specific org requirement needs a shared, centrally-managed app server |
| A dependency change is test-only vs runtime-affecting? | Scope test-only dependencies strictly to `test` — a scope leak is exactly the signal auto-config conditions react to |

## Key Numbers

- A full embedded-Tomcat Spring Boot app served a genuine HTTP 200 from a single `java -cp` command, no external server.
- Real conditions-evaluation report for a web-only classpath: 77 positive matches, 168 negative matches across 245 evaluated auto-configuration classes, each with a specific, logged reason (e.g., `DataSourceAutoConfiguration` did not match — required class `EmbeddedDatabaseType` absent).

## Common Pitfalls

- Describing Spring Boot as "a different framework from Spring" rather than a layer built on top of it.
- Assuming a starter directly configures something, rather than changing the classpath that auto-configuration's conditions react to.
- Assuming disabling unwanted auto-configuration requires explicit exclusion, when defining a competing bean is usually sufficient.
- Placing a `@SpringBootApplication` class in the default package — a real, reproduced `NoClassDefFoundError` from over-broad component scanning.

## Interview Answer Skeleton

**30-sec:** Spring Framework is the core programming model; Spring Boot adds starters (classpath-changing dependency bundles), auto-configuration (conditional `@Configuration` classes reacting to the classpath), and an embedded, application-owned server. Nothing Boot does is a new mechanism.

**2-min:** Add the real conditions-evaluation report numbers (77 positive / 168 negative matches, each with a specific reason) and the real default-package pitfall (`NoClassDefFoundError: io/r2dbc/spi/ValidationDepth`) hit during this chapter's own construction, fixed by placing the class in a real package.

**Whiteboard:** Classpath feeding into a `@Conditional` decision point, branching into "applied" (class present, no competing bean) vs two "skipped" branches (class absent; application's own bean already present). Annotate: "every branch is logged individually in the real report — nothing here is a black box."

**Staff-level framing:** A system reacting correctly to its actual observed state (the classpath) can still produce a surprising outcome when that state changes unintentionally — the mechanism isn't broken, the input changed. Debugging "why did this configuration activate" starts from "what changed about the observed state," not "what's broken."

## Production Warning Signs

- A test-only H2 database dependency accidentally scoped to `compile`/`runtime` silently activates `DataSourceAutoConfiguration` in production, briefly serving empty reads — the mechanism worked exactly as designed, reacting to an accidental classpath signal. Fix: build-time dependency-scope auditing, plus a startup assertion that the intended `DataSource` is actually active.

## Related

- `syllabus/05-spring/auto-configuration-and-bean-lifecycle.md`
- `syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md`
- `syllabus/05-spring/security-filter-chain.md`
