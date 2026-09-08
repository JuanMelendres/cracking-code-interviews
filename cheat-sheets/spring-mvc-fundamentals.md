---
title: "Cheat Sheet: Spring MVC Fundamentals"
slug: spring-mvc-fundamentals
document_type: cheat-sheet
domain: 05-spring
topic_id: T-2203
canonical: ../syllabus/05-spring/spring-mvc-fundamentals.md
last_updated: 2026-09-07
---

# Spring MVC Fundamentals

**Canonical chapter:** [`syllabus/05-spring/spring-mvc-fundamentals.md`](../syllabus/05-spring/spring-mvc-fundamentals.md)

## Core Mental Model

A class declares what it needs (usually as a constructor parameter); Spring builds the object graph and hands the dependencies in. The class never writes `new` for another bean — it only states the shape it needs.

## Essential Definitions

- **Bean** — an object Spring manages, created via a stereotype annotation (`@Component`, `@Service`, `@Repository`, `@Controller`/`@RestController`).
- **Constructor-based dependency injection** — a class with exactly one constructor gets it wired automatically (Spring Framework 4.3+), no `@Autowired` needed.
- **`@RestController`** — a class whose methods write their return value directly to the HTTP response body as JSON, no view template.
- **`@PathVariable` / `@RequestParam` / `@RequestBody`** — bind a URL segment, a query parameter, and the whole request body (deserialized by Jackson) to method parameters, respectively.

## Decision Table

| Layer | Responsibility | Annotation |
|---|---|---|
| Controller | Reads the request, calls the service, returns a value — no business logic | `@RestController` |
| Service | Business logic — never mentions HTTP | `@Service` |
| Repository | Data access | `@Repository` |

## Common Pitfalls

- **`@PathVariable Long id` with no explicit name, compiled without `-parameters`** — a real, genuine `500` (`IllegalArgumentException: Name for argument of type [java.lang.Long] not specified...`). Fix: `@PathVariable("id") Long id`, always.
- Putting business logic directly in the controller — untestable without a running HTTP stack.
- Using field `@Autowired` on every dependency instead of a constructor — hides how many dependencies a class has, and can't be constructed with plain `new` in a test.
- Removing a `@RequestBody` target class's no-argument constructor — breaks every endpoint accepting that body, with a non-obvious error.

## Interview Answer Skeleton

**30-sec:** Spring's core idea is dependency injection — a class declares what it needs, usually via its constructor, and Spring builds and wires the whole object graph. `@RestController` → `@Service` → `@Repository` is the conventional three-layer shape, with the controller as the only layer that knows HTTP exists.

**2-min:** Add the request-flow trace: `DispatcherServlet` matches the path/method to a controller method, `@PathVariable`/`@RequestBody` bind the request data, the controller calls the service, the service calls the repository, and the return value is serialized to JSON by a message converter. Mention constructor injection's real advantage: all dependencies visible in one place, testable with plain `new`.

**Whiteboard:** Draw the three boxes (Controller → Service → Repository) with arrows showing constructor injection resolving bottom-up (repository built first, then service, then controller) — then draw an incoming HTTP request arrow hitting only the controller box.

**Staff-level framing:** The three-layer shape is a convention, not a compiler-enforced law — nothing stops a database call inside a controller method. Real leverage is a consistently-enforced layering convention (review or an architecture test), the same argument [Clean and Hexagonal Architecture](../syllabus/17-architecture/clean-hexagonal-architecture.md) makes at a larger scale.

## Related

- syllabus/05-spring/auto-configuration-and-bean-lifecycle.md
- syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md
- syllabus/07-api-design/rest-api-fundamentals.md
