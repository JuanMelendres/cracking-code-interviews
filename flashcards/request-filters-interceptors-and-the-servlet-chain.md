---
title: "Flashcards: Request Filters, Interceptors, and the Servlet Chain"
slug: request-filters-interceptors-and-the-servlet-chain
document_type: flashcard-deck
domain: 05-spring
topic_id: T-2441
canonical: ../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md
last_updated: 2026-09-29
---

# Flashcards: Request Filters, Interceptors, and the Servlet Chain

**Canonical chapter:** [`syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md`](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)

## Card: The one structural fact everything follows from

**Prompt:**
In one sentence, what is the structural difference between a Servlet `Filter` and a Spring `HandlerInterceptor`?

**Answer:**
A filter wraps the entire `DispatcherServlet` from outside; an interceptor runs inside it, after Spring has resolved which handler applies. Every practical consequence — what each can see, which exceptions your advice reaches, which requests each covers — derives from that nesting.

**Why it matters:**
Candidates who memorize "servlet-level vs Spring-level" cannot derive a single consequence. Candidates who hold the nesting can derive all of them on the spot.

**Common trap:**
Stopping at the level labels and treating them as the whole answer.

**Related:**
[Request Filters, Interceptors, and the Servlet Chain](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)

## Card: Can `@ControllerAdvice` handle a filter's exception?

**Prompt:**
A filter throws. Does `@RestControllerAdvice` handle it, and what does the client receive?

**Answer:**
No. The advice is registered as a `HandlerExceptionResolver` *inside* the `DispatcherServlet`, and the filter sits outside it — the servlet is never entered. Measured directly: the client received Tomcat's own body, `{"timestamp":...,"status":500,"error":"Internal Server Error","path":"/boom-filter"}`, versus the application's `{"error":"..."}` for the identical failure thrown from a controller.

**Why it matters:**
These are different JSON *shapes*, not just different status codes. An API silently has two error contracts, and only the controller-level one is typically covered by contract tests.

**Common trap:**
Assuming "global exception handling" means global. It is scoped to one layer.

**Related:**
[Bean Validation and Global Exception Handling](../syllabus/05-spring/bean-validation-and-global-exception-handling.md)

## Card: Why `afterCompletion`'s `ex` is almost always null

**Prompt:**
Your interceptor logs failures with `if (ex != null)` in `afterCompletion`, but logs nothing in production despite real 500s. Why?

**Answer:**
`ex` is the exception still *unresolved* at that point, not whatever the handler threw. `DispatcherServlet.processDispatchResult` runs the `HandlerExceptionResolver` chain — including the one backing `@ControllerAdvice` — and a resolved exception is never passed to `triggerAfterCompletion`. Measured: the controller threw, the advice mapped it to a 422, and both interceptors still recorded `ex=null, status=422`.

**Why it matters:**
The better a service handles its errors, the blinder this monitoring becomes. Derive the failure signal from the response status instead.

**Common trap:**
"Fixing" it by moving the logic to `postHandle`, which does not run on the error path at all — strictly worse.

**Related:**
[Request Filters, Interceptors, and the Servlet Chain](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)

## Card: When does `postHandle` not run?

**Prompt:**
When is an interceptor's `postHandle` skipped, and what does that break?

**Answer:**
Whenever the handler throws — measured, even when `@ControllerAdvice` successfully handles the exception — and whenever an earlier interceptor short-circuits. Anything placed there is lost on exactly the paths that matter. The classic breakage is `ThreadLocal`/MDC cleanup: never cleared, so the value leaks onto the next request served by that pooled thread.

**Why it matters:**
A leaked correlation ID is worse than a missing one — it attributes one user's failure to another user's request.

**Common trap:**
Treating `preHandle`/`postHandle` as a symmetric acquire/release pair. The symmetric pair is `preHandle`/`afterCompletion`.

**Related:**
[Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: What `preHandle` returning `false` actually skips

**Prompt:**
An interceptor's `preHandle` returns `false`. Which callbacks still run, and who writes the response?

**Answer:**
The handler does not run and no `postHandle` runs. `afterCompletion` runs only on interceptors *earlier* in the chain whose `preHandle` already returned `true` — the rejecting interceptor's own `afterCompletion` does **not** run, because `applyPreHandle` records how far it got in `interceptorIndex` and `triggerAfterCompletion` unwinds only that far. Both filters complete normally. And Spring writes no response: return `false` without setting one and the client gets an empty `200`.

**Why it matters:**
An interceptor that acquires something in `preHandle` and releases it in its own `afterCompletion` has no cleanup path on the rejection branch.

**Common trap:**
Assuming Spring produces a sensible default rejection response.

**Related:**
[Request Filters, Interceptors, and the Servlet Chain](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)

## Card: Filter or interceptor for a correlation ID?

**Prompt:**
A correlation ID must appear on every log line for every request. Filter or interceptor?

**Answer:**
Filter. It must cover requests with no resolved handler — 404s and static resources — which an interceptor never sees at all, plus requests short-circuited by an earlier interceptor. A filter's `finally` block runs for literally every request that enters the application.

**Why it matters:**
The interceptor version fails in exactly the cases anyone investigates: errors, rejections, and 404s.

**Common trap:**
Setting MDC in `preHandle` and clearing it in `postHandle` — missing on error paths and leaking across requests.

**Related:**
[Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: Does a filter's `finally` see the real response status?

**Prompt:**
A filter logs `response.getStatus()` in its `finally` block. A request fails with a container-generated 500. What does the log record?

**Answer:**
`200`. Measured directly: the outermost filter recorded `status=200, response committed=false` for a request the client received as `500`. Tomcat sets the error status in `StandardHostValve`/`ErrorReportValve`, which sit outside the entire application filter chain — so at that moment the status is still its default.

**Why it matters:**
Access logs built this way under-report server errors, and the dashboard looks healthiest during the incidents it exists to reveal.

**Common trap:**
"Put the access log in a filter, it sees everything." A filter sees every request, but not every request's true outcome. Record the throwable in a `catch`, or use the container's own access-log valve.

**Related:**
[Request Filters, Interceptors, and the Servlet Chain](../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md)
