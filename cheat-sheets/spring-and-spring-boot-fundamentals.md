---
title: "Cheat Sheet: Spring and Spring Boot Fundamentals"
slug: spring-and-spring-boot-fundamentals
document_type: cheat-sheet
domain: 05-spring
topic_id: T-2213
canonical: ../syllabus/05-spring/spring-and-spring-boot-fundamentals.md
last_updated: 2026-09-26
---

# Spring and Spring Boot Fundamentals

**Canonical chapter:** [`syllabus/05-spring/spring-and-spring-boot-fundamentals.md`](../syllabus/05-spring/spring-and-spring-boot-fundamentals.md)

## Core Mental Model

A kitchen before and after hiring an expediter: before, every station wires itself directly to every other station it needs; after, each station just states what it needs and the expediter (the IoC container) builds and hands it in. IoC is the principle; Dependency Injection is Spring's specific mechanism for it.

## Essential Definitions

- **IoC (Inversion of Control)** — control of object creation and wiring is handed to a container instead of your own code calling `new`.
- **DI (Dependency Injection)** — the specific mechanism the container uses: a class declares what it needs, the container builds and injects it.
- **Bean** — any object the IoC container creates, wires, and manages the lifecycle of.
- **Bean scopes (all six)** — `singleton` (default, one shared instance), `prototype` (new instance per request from the container), `request`/`session`/`application`/`websocket` (web-context-scoped).

## Decision Table

| Question | Answer |
|---|---|
| Constructor vs. field injection? | Constructor — visible dependencies, `final`-able, testable with plain `new`; field injection hides dependencies and blocks plain-`new` testing |
| Multiple beans of the same type, no `@Qualifier`/`@Primary`? | `NoUniqueBeanDefinitionException` at startup — not a silent "first one found" |
| `@Qualifier` vs. `@Primary`? | An explicit `@Qualifier` at the injection point always wins over `@Primary`'s default |
| Which stereotype has real extra behavior? | Only `@Repository` — persistence exception translation. `@Service`/`@Controller` are documentation-only over `@Component` |
| `@RestController` = ? | `@Controller` + `@ResponseBody` — same composition as `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody` |
| `@SpringBootApplication` = ? | `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`, composed into one |

## Common Pitfalls

- Conflating IoC (the principle) with DI (Spring's mechanism for it) as if they were the same word.
- Naming only `singleton`/`prototype` when asked for "the bean scopes" — four more exist (`request`/`session`/`application`/`websocket`).
- Assuming `@Autowired` is always required — unnecessary on a class's sole constructor since Spring 4.3.
- Believing profiles only affect property values — `@Profile` also gates which *beans* get created at all.

## Interview Answer Skeleton

**30-sec:** IoC hands control of object creation to a container; DI is Spring's mechanism for it — a class declares what it needs, the container builds and injects it. A bean is any object the container manages this way, discovered via component scanning and a stereotype annotation.

**2-min:** Add the real, commonly-missed detail: injecting a prototype-scoped bean into a singleton doesn't give a fresh instance each time — Spring resolves it once, at the singleton's own creation, permanently (fix: `ObjectProvider<T>` or a scoped proxy).

**Staff-level framing:** These twenty basics stop being trivia at Staff scope and become one judgment — does the candidate understand Spring as ordinary Java constructs wired by a container, or as memorized annotations with no underlying model?

## Related

- syllabus/05-spring/spring-mvc-fundamentals.md
- syllabus/05-spring/auto-configuration-and-bean-lifecycle.md
- syllabus/05-spring/spring-framework-vs-spring-boot.md
- syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md
- syllabus/15-cloud/twelve-factor-config.md
