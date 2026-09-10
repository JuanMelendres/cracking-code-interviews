---
title: "Spring MVC Fundamentals"
slug: spring-mvc-fundamentals
document_type: syllabus-topic
domain: 05-spring
topic_id: T-2203
status: canonical
version: 1.0
last_updated: 2026-09-07
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md
related:
  - auto-configuration-and-bean-lifecycle.md
  - spring-bean-scopes-and-proxy-modes.md
  - spring-framework-vs-spring-boot.md
  - bean-validation-and-global-exception-handling.md
  - ../07-api-design/api-design.md
practice: ../../practice/java/spring-mvc-fundamentals/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://docs.spring.io/spring-framework/reference/web/webmvc.html
  - https://docs.spring.io/spring-boot/reference/web/servlet.html
---

# Spring MVC Fundamentals

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

Every other chapter in `05-spring` assumes you already know what `@Autowired` does and can read a `@RestController` at a glance — reasonable for the Senior/Staff-only version of this repository, not reasonable once it explicitly covers Junior through Staff. This chapter is that missing floor, the same role [Java OOP Fundamentals](../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) plays for `02-java`. Nearly every Java backend job posting says "Spring Boot experience required," and nearly every technical screen opens with some version of "walk me through how a request flows through your controller" — a question this chapter answers directly, and one every advanced chapter in this domain (bean lifecycle, proxy mechanics, security filter chains) silently assumes you can already answer.

## 2. Prerequisites

[Java OOP Fundamentals: Classes, Objects, and Interfaces](../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) — specifically the constructor and interface material; Spring's own dependency injection (Section 4) is built directly on ordinary Java constructors, not a special language feature.

## 3. Foundation (L1)

**Dependency injection** is Spring's central idea, and it is simpler than it sounds: instead of a class creating the objects it depends on (`new TaskRepository()`), it declares what it needs — usually as a constructor parameter — and a container builds the object graph and hands the dependencies in. The class stops being responsible for knowing how to construct its own dependencies; it only states what it needs.

Spring calls the objects it manages **beans**. A class becomes a bean by being annotated with a **stereotype annotation** — `@Component` is the generic form, and `@Service`, `@Repository`, and `@Controller`/`@RestController` are the same mechanism under three more specific, self-documenting names for three conventional layers: business logic, data access, and web request handling, respectively. At startup, Spring scans for these annotations, constructs one instance of each, and wires them together based on what each one's constructor asks for.

A **`@RestController`** is a class whose methods handle incoming HTTP requests and write their return value directly into the HTTP response body (as JSON, by default) — no HTML page, no template. `@GetMapping("/tasks")` on a method means "run this method when an HTTP `GET` request arrives at `/tasks`." `@PostMapping`, `@PutMapping`, and `@DeleteMapping` are the same idea for `POST`, `PUT`, and `DELETE`.

## 4. Core Concepts (L2)

The conventional three-layer shape this chapter's demo uses, and the reason it exists:

- **Controller** (`@RestController`) — the only layer that knows HTTP exists. It reads the request (path variables, query parameters, the request body) and returns a value; it should contain no real business logic.
- **Service** (`@Service`) — the business logic. [`TaskService`](../../practice/java/spring-mvc-fundamentals/src/demo/TaskService.java) never mentions HTTP at all — it is just as usable from a test, a CLI tool, or a background job as from a web request, which is the actual point of keeping it separate from the controller.
- **Repository** (`@Repository`) — data access. This chapter's demo uses a plain in-memory list on purpose, so the pattern (controller depends on service depends on repository) stays visible without a real database's own concerns mixed in.

**Constructor-based dependency injection**, the only kind this chapter uses, works like this: a class declares one constructor listing what it needs as parameters, and — since Spring Framework 4.3 — a class with exactly one constructor does not even need an `@Autowired` annotation; Spring uses that single constructor automatically. [`TaskController`](../../practice/java/spring-mvc-fundamentals/src/demo/TaskController.java)'s constructor asks for a `TaskService`; [`TaskService`](../../practice/java/spring-mvc-fundamentals/src/demo/TaskService.java)'s constructor asks for a `TaskRepository`. Spring builds the `TaskRepository` first, then the `TaskService` (handing in the repository), then the `TaskController` (handing in the service) — resolving the whole chain without any class in it ever writing `new` for another bean.

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TaskController<br/>(@RestController)
    participant Service as TaskService<br/>(@Service)
    participant Repo as TaskRepository<br/>(@Repository)

    Client->>Controller: GET /tasks/{id}
    Controller->>Service: getTask(id)
    Service->>Repo: findById(id)
    Repo-->>Service: Task
    Service-->>Controller: Task
    Controller-->>Client: 200 OK, JSON body
```

Startup wiring runs in the opposite, dependency-first order: Spring builds `TaskRepository` first (it needs nothing), then `TaskService` (handing in the repository), then `TaskController` (handing in the service) — no class in the chain ever writes `new` for another bean.

Three annotations bind pieces of the incoming HTTP request directly to method parameters: `@PathVariable` binds a segment of the URL path (`/tasks/{id}` → the method parameter matching `id`), `@RequestParam` binds a query-string parameter (`?status=done`), and `@RequestBody` binds the entire request body, deserialized from JSON into a Java object by Jackson (already on the classpath).

## 5. How It Works Internally (L3)

At startup, `@SpringBootApplication` triggers **component scanning**: Spring walks the application's packages looking for classes annotated `@Component` (or one of its stereotype specializations), and constructs the **application context** — the actual container holding every bean instance. Dependency injection happens as this container is built: Spring inspects each bean class's constructor, sees what types it asks for, and either supplies an already-built bean of that type or builds it first if it doesn't exist yet — recursively, until the whole graph (repository → service → controller, in this chapter's demo) is resolved. If two beans depended on each other's constructors directly, this resolution would have no valid starting point — a genuine circular-dependency failure Spring detects and reports at startup, not silently.

For the web layer specifically, Spring Boot starts an **embedded servlet container** (Tomcat, by default) as part of the same application process — no separate application-server installation or deployment step. Every incoming HTTP request passes through Spring's own `DispatcherServlet`, which matches the request's path and method against every `@GetMapping`/`@PostMapping`/etc. across every controller bean, invokes the matching method, and converts its return value to the HTTP response using a registered `HttpMessageConverter` (Jackson, for JSON, in this chapter's demo).

`@PathVariable`'s binding-by-name (Section 4) depends on the method parameter's actual name being available at runtime — and by default, compiled Java bytecode does **not** retain parameter names unless compiled with the `-parameters` flag. Section 7's demo hit this directly: `@PathVariable Long id` (no explicit name) failed with a genuine `500` and the exact message `Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection` the first time it was run — not a hypothetical, the actual failure this repository's own build produced. The fix, `@PathVariable("id") Long id`, states the binding explicitly rather than depending on a build flag every future compilation of the project would also need to remember.

## 6. Practical Usage

Keep the controller thin — if a method is doing anything beyond reading the request and calling the service layer, that logic almost always belongs in the service instead, testable without spinning up a whole HTTP stack. Default to constructor injection over field `@Autowired` — it makes a class's dependencies visible in one place (the constructor signature), makes the class straightforward to construct directly in a unit test with plain `new`, and makes a class with too many dependencies visibly awkward to construct rather than silently accumulating unlimited `@Autowired` fields. Name `@PathVariable`/`@RequestParam` bindings explicitly, per Section 5's real failure, rather than relying on the `-parameters` compiler flag being present in every build.

## 7. Examples

All output below is real, from a live Spring Boot 3.5.16 app running on embedded Tomcat, `localhost:8080` — [`practice/java/spring-mvc-fundamentals/`](../../practice/java/spring-mvc-fundamentals/), full transcript in `curl-transcript.txt`.

**Empty state, `GET /tasks`:**
```
$ curl -s http://localhost:8080/tasks
[]
```

**Creating a resource, `POST /tasks`** — the request body is real JSON, and the response is the real object the service/repository layer produced, `id` assigned by the repository:
```
$ curl -s -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"Write chapter"}'
{"id":1,"title":"Write chapter","done":false}
```

**Listing again after two creations, `GET /tasks`:**
```
$ curl -s http://localhost:8080/tasks
[{"id":1,"title":"Write chapter","done":false},{"id":2,"title":"Run the demo","done":false}]
```

**A real path-variable bug, hit while building this exact demo** — the original controller wrote `@PathVariable Long id` with no explicit name; `GET /tasks/1` failed with a genuine `500`:
```
$ curl -s -w "\nHTTP status: %{http_code}\n" http://localhost:8080/tasks/1
{"timestamp":"...","status":500,"error":"Internal Server Error","path":"/tasks/1"}
HTTP status: 500
```
The real cause, from the application's own log: `IllegalArgumentException: Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag.` The fix — naming the binding explicitly, `@PathVariable("id") Long id` — resolved it:
```
$ curl -s -w "\nHTTP status: %{http_code}\n" http://localhost:8080/tasks/1
{"id":1,"title":"Write chapter","done":false}
HTTP status: 200
$ curl -s -w "\nHTTP status: %{http_code}\n" http://localhost:8080/tasks/999
HTTP status: 404
```
`GET /tasks/999` (an id that was never created) correctly returns an empty body and a real `404`, produced by `ResponseEntity.notFound().build()` in [`TaskController`](../../practice/java/spring-mvc-fundamentals/src/demo/TaskController.java) — see the full, uncut before/after transcripts in `curl-transcript-before-fix.txt` and `curl-transcript.txt`.

## 8. Common Mistakes

- **Writing `@PathVariable Long id` (or `@RequestParam`) without an explicit name and without the `-parameters` compiler flag** — Section 5/7's real, hit-while-building-this-chapter bug. Always name the binding explicitly; it costs nothing and depends on no build configuration.
- **Putting business logic directly in the controller** — makes the logic untestable without a running HTTP stack, and impossible to reuse from anywhere that isn't a web request.
- **Using field `@Autowired` on every dependency instead of a constructor** — hides how many dependencies a class actually has (there's no single place listing them), and makes the class impossible to construct with plain `new` in a unit test.
- **Forgetting that `@RequestBody` deserialization needs a no-argument constructor on the target class** — [`Task`](../../practice/java/spring-mvc-fundamentals/src/demo/Task.java)'s own empty constructor exists specifically because Jackson needs it; removing it (leaving only the all-args constructor) breaks every `POST`/`PUT` endpoint that accepts a `Task` body, with an error that doesn't obviously point at "the constructor" as the cause.

## 9. Edge Cases

- A **`GET` request to a URL with no matching `@GetMapping`** returns Spring Boot's default `404` error response, structurally identical in shape to Section 7's `ResponseEntity.notFound().build()` result, but produced by an entirely different mechanism (no controller method ran at all, versus a controller method explicitly choosing to return 404) — both look the same from the client's side, which is worth knowing when debugging "why is this returning 404" during development.
- **Two `@RestController` methods mapped to the same path and HTTP method** is a startup-time ambiguity error, not a runtime "first one wins" — Spring refuses to start rather than silently picking one.
- **A constructor-injected dependency that Spring cannot find a bean for** (e.g., an interface with zero or more than one implementing bean, and no qualifier to disambiguate) fails at startup with a clear "no qualifying bean" message — a real, common Mid-level debugging scenario `05-spring`'s other chapters explore further.

## 10. Performance Implications

Component scanning and dependency-graph resolution happen once, at application startup — not per request — so this chapter's mechanics add no per-request overhead of their own; the request-handling cost model (matching a path, invoking a method, serializing a response) is dominated by whatever the service/repository layers actually do, not by the DI mechanism that wired them together. [Spring Auto-Configuration and Bean Lifecycle](auto-configuration-and-bean-lifecycle.md) covers what actually happens during that one-time startup cost in depth.

## 11. Trade-offs

| Concern | Constructor injection | Field `@Autowired` injection |
|---|---|---|
| Visibility of dependencies | All listed in one place — the constructor signature | Scattered across the class body, no single list |
| Testability | Constructible with plain `new` in a unit test, no Spring context needed | Requires either a Spring test context or reflection-based mocking to set the fields |
| Immutability | Dependencies can be stored in `final` fields | Fields injected after construction cannot be `final` |
| Circular dependency detection | Fails fast and clearly at startup (Section 5) | Can sometimes be worked around with a proxy, hiding a design problem rather than surfacing it |

## 12. Senior-Level Considerations (L3)

A Senior engineer treats a controller class with ten constructor parameters as a real design signal, not just a long line to scroll past — Section 6's "constructor injection makes too many dependencies visibly awkward" property is only useful if the awkwardness is actually acted on, usually by asking whether that controller (or the service behind it) is doing too many unrelated things and should be split. The same review instinct applies to Section 9's "no qualifying bean" failures: the fix is rarely just adding a qualifier annotation; it's worth asking first whether having two implementations of the same interface active as beans simultaneously was actually the intended design.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the three-layer shape in this chapter (controller/service/repository) is a convention, not a law enforced by the compiler — nothing stops a team from putting a database call directly in a controller method. The actual leverage a Staff engineer has here is the same as [Clean and Hexagonal Architecture](../17-architecture/clean-hexagonal-architecture.md)'s own argument at a larger scale: a consistently-enforced layering convention (via code review, or an automated architecture test) is what makes it possible for any engineer to predict where a given piece of logic lives without reading the whole codebase, and what makes a future change (swapping the in-memory repository for a real database, in this chapter's own demo) touch only one layer instead of being smeared across every controller method that happened to call the database directly.

## 14. Production Scenarios

No existing `production-cookbook/` entry has an MVC-fundamentals-specific root cause — the closest adjacent entries are proxy- and transaction-scale, not basic-request-routing-scale.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by business logic embedded directly in a controller method — untestable without a full HTTP stack, and duplicated across two controllers that both needed the same logic — would be a natural, non-duplicative addition connecting this chapter's Section 8 warning to a genuine production incident.

## 15. Interview Questions

**Q1 (Junior): "What does `@Autowired` do?"**
Expected answer: it tells Spring to inject a dependency — Section 3/4's core idea — though the strongest answers note that on a single constructor (Spring Framework 4.3+), the annotation isn't even required.

**Q2 (Junior/Mid): "Walk me through what happens when a GET request hits your `/tasks/{id}` endpoint."**
Expected answer: Section 5's real flow — `DispatcherServlet` matches the path and method to `TaskController.getTask`, the `{id}` path segment binds to the `id` parameter, the controller calls into the service, the service calls the repository, and the return value is serialized to JSON by a message converter.

**Q3 (Mid): "Why prefer constructor injection over field `@Autowired`?"**
Expected answer: Section 11's trade-off table — visibility of the full dependency list, testability with plain `new`, and the ability to use `final` fields; the strongest answers add that an awkwardly-long constructor is a useful, visible design smell that field injection hides.

**Q4 (Mid/Senior): "You add a second implementation of an interface your controller already depends on, and the app fails to start. Why, and how do you fix it?"**
Expected answer: Section 9's "no qualifying bean" ambiguity — Spring can no longer determine which implementation to inject; fixed with `@Qualifier`, marking one implementation `@Primary`, or (per Section 12) reconsidering whether both implementations should really be active beans simultaneously.

**Q5 (Senior/Staff): "Your team's controllers have started accumulating direct database calls instead of going through the service layer. How do you address it?"**
Expected answer: Section 13's framing — this is a convention-erosion problem, not a one-off bug; the fix is process/tooling (code review discipline, or an automated architecture test enforcing the layering) rather than a single code change, because the same shortcut will keep recurring under time pressure otherwise.

## 16. Coding/Practice Exercises

1. Add a `PUT /tasks/{id}` endpoint that updates an existing task's `title` and `done` fields, returning `404` (matching the existing `GET /tasks/{id}` pattern) if the id doesn't exist.
2. Add a `@RequestParam(required = false) Boolean done` to `GET /tasks`, filtering the returned list to only tasks matching that `done` value when the parameter is present, and returning the full list when it's absent.
3. Deliberately remove `Task`'s no-argument constructor (Section 8's warning) and reproduce the real failure `POST /tasks` now produces; then restore it and confirm the endpoint works again.

## 17. Debugging Exercises

Given `@PathVariable Long id` (no explicit name), and a controller compiled without the `-parameters` flag, predict what happens when a request hits that endpoint, then verify against Section 7's own real transcript.

The answer is a genuine `500 Internal Server Error`, not a `400 Bad Request` or a silently-null `id` — Spring cannot even attempt to bind the path variable without knowing what name to bind it under, so the failure happens before the controller method body ever runs. A candidate guessing "it would just receive `null`" is assuming a more forgiving failure mode than what Spring actually does here; the exact, real message is `Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection` — worth recognizing on sight, since it's a genuinely common first-project Spring MVC error.

## 18. Design Exercises

Design the controller/service/repository layering for a simple comment system: `POST /posts/{postId}/comments` (create a comment on a post), `GET /posts/{postId}/comments` (list them). State explicitly which layer is responsible for verifying the post actually exists before allowing a comment to be created against it, and why that check does not belong in the controller.

## 19. Further Reading

- [Spring Auto-Configuration and Bean Lifecycle](auto-configuration-and-bean-lifecycle.md) — the full startup-time mechanism this chapter's Section 5 only summarizes.
- [Spring Bean Scopes and Proxy Modes](spring-bean-scopes-and-proxy-modes.md) — what happens once a bean needs more than one instance, or a scope other than the application-wide default this chapter's demo uses.
- [API Design](../07-api-design/api-design.md) — how to design the URLs and status codes this chapter's controller uses well, at a larger scale.

## 20. Mastery Checklist

- [ ] Can explain dependency injection in one sentence, with a concrete constructor example.
- [ ] Can name the controller/service/repository layering and state what belongs in each.
- [ ] Can trace a request through `DispatcherServlet` to a controller method to a JSON response, in the right order.
- [ ] Can explain why the Section 7/17 `@PathVariable` bug happens, and state the fix.
- [ ] Can justify constructor injection over field `@Autowired` with more than one concrete reason.
- [ ] Can diagnose a "no qualifying bean" startup failure and name at least two ways to resolve it.
