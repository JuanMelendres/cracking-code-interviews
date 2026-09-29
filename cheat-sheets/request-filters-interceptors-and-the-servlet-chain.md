---
title: "Cheat Sheet: Request Filters, Interceptors, and the Servlet Chain"
slug: request-filters-interceptors-and-the-servlet-chain
document_type: cheat-sheet
domain: 05-spring
topic_id: T-2441
canonical: ../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md
last_updated: 2026-09-29
---

# Request Filters, Interceptors, and the Servlet Chain

**Canonical chapter:** [`syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md`](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)

## Core Mental Model

Two nested boxes. A **filter** wraps the whole `DispatcherServlet` from outside. An **interceptor** runs inside it, after Spring has resolved which controller method handles the request. Every practical difference follows from that nesting — including which exceptions `@ControllerAdvice` can reach.

```text
Tomcat -> Filter chain -> DispatcherServlet -> Interceptor chain -> @GetMapping method
                                  ^
                          @ControllerAdvice lives HERE
```

## Measured Order (one request, one thread)

```text
 1. Filter[outer]  BEFORE       6. Interceptor[second] postHandle
 2. Filter[inner]  BEFORE       7. Interceptor[first]  postHandle
 3. Interceptor[first]  preHandle   8. Interceptor[second] afterCompletion
 4. Interceptor[second] preHandle   9. Interceptor[first]  afterCompletion
 5. Controller                     10. Filter[inner] AFTER
                                   11. Filter[outer] AFTER
```

Forward on the way in, **reverse** on the way out, on both layers.

## Decision Rule

| The concern is about... | Use |
|---|---|
| The HTTP request (correlation ID, access log, body wrapping) | **Filter** |
| The controller method (its annotations, its identity) | **Interceptor** |
| Authentication / authorization / CSRF / CORS | **Spring Security filter chain** |

Filters cover 404s and static resources; interceptors do not. Interceptors can read the `HandlerMethod`; filters cannot.

## The Four Surprises (all measured)

| What you'd assume | What actually happens |
|---|---|
| `@ControllerAdvice` catches everything | A filter's exception bypasses it — client gets the container's error body, a **different JSON shape** |
| `postHandle` always runs | **Skipped entirely** when the handler throws — MDC cleanup there leaks onto the next pooled request |
| `afterCompletion(ex)` gives you the exception | `ex == null` for anything `@ControllerAdvice` already resolved. Key off the **response status** |
| A rejecting interceptor's `afterCompletion` runs | It does **not** — Spring unwinds only to `interceptorIndex` |

Plus one more: a filter's `finally` block reads `response.getStatus()` as **200 for a container-generated 500**, because Tomcat sets the error status outside the whole filter chain. Access logs built that way under-report 500s.

## Two Error Shapes, Same Service

```text
controller throws  ->  {"error":"..."}                                   HTTP 422
filter throws      ->  {"timestamp":...,"status":500,"error":"Internal
                        Server Error","path":"/boom-filter"}             HTTP 500
```

Test the filter-level failure too, or the second schema ships untested.

## `preHandle` Returning `false`

- Handler: **not run**. `postHandle`: **not run**.
- `afterCompletion`: only on interceptors **earlier** in the chain.
- Both filters: still complete normally.
- **You own the response.** Return `false` without writing one and the client gets an empty `200`.

## Registration

```java
@Bean
FilterRegistrationBean<MyFilter> myFilter() {
    var r = new FilterRegistrationBean<>(new MyFilter());
    r.addUrlPatterns("/*");                      // Servlet URL patterns
    r.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return r;
}

@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new MyInterceptor())
            .addPathPatterns("/api/**")          // Spring path patterns -- different syntax
            .excludePathPatterns("/api/health");
}
```

Prefer `OncePerRequestFilter` over raw `Filter`: it survives internal `FORWARD`/`ERROR` dispatches without running twice. A bare `@Component` filter is auto-registered across **all** URLs.

## Common Pitfalls

- Cleanup in `postHandle` instead of `afterCompletion` or a filter `finally`.
- Trusting `afterCompletion`'s `ex` as a failure signal.
- Expecting `@ControllerAdvice` to cover filter failures.
- Reading the request body in a filter without wrapping it — the stream is read-once and the controller gets nothing.
- Copying a path pattern between `addUrlPatterns` and `addPathPatterns`.

## Interview Answer Skeleton

1. Filter wraps the servlet; interceptor runs inside it, after handler resolution.
2. Therefore: filter sees every request but no handler; interceptor sees the `HandlerMethod` but not 404s.
3. Therefore: `@ControllerAdvice` reaches interceptor exceptions, not filter exceptions.
4. Decision rule: about the request → filter; about the method → interceptor.
5. Volunteer the error-contract consequence: two schemas, one tested.

## Production Warning Signs

- Correlation IDs missing from exactly the error logs.
- The same ID appearing on two unrelated requests (MDC leaked via `postHandle`).
- An access-log 5xx rate that stays flat during an incident.
- A client crashing on an error body the backend team cannot reproduce.

## Related

- [Spring MVC Fundamentals](../syllabus/05-spring/spring-mvc-fundamentals.md)
- [Security Filter Chain](../syllabus/05-spring/security-filter-chain.md)
- [Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)
- [Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)
