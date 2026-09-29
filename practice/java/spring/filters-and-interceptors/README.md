# Servlet Filters vs. Spring MVC Interceptors — Real, Executed Demo

Backs [Request Filters, Interceptors, and the Servlet Chain](../../../../syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md) (T-2441).

A real Spring Boot 3.5.16 app (`demo.ChainApplication`) on Spring Framework 6.2.19 and embedded Tomcat 10.1.55, running on `localhost:8080` under OpenJDK 21.0.12 — plain jars fetched from Maven Central, no Maven/Gradle install. Dependency list reused verbatim from `practice/java/spring-mvc-fundamentals`.

Two Servlet `Filter`s and two Spring MVC `HandlerInterceptor`s are registered deliberately, rather than one of each: a single layer cannot distinguish "filters run before interceptors" from "the chain unwinds in reverse," and both claims are ones the chapter makes.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.ChainApplication
```

## Reproduce the transcripts

With the app running in another terminal:

```bash
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/ok
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/boom-controller
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/boom-filter
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/blocked
```

`curl-transcript.txt` is the real client-side output; `trace-transcript.txt` is the real server-side ordering the app printed for those same four requests.

## What it proves

**`/ok` — the full order, measured.** Filters wrap the `DispatcherServlet` from outside; interceptors run inside it. All eleven stages run on one container thread (`http-nio-8080-exec-1`), and the chain unwinds in exact reverse: `outer-filter` → `inner-filter` → `first.preHandle` → `second.preHandle` → handler → `second.postHandle` → `first.postHandle` → `second.afterCompletion` → `first.afterCompletion` → `inner-filter` → `outer-filter`.

**The handler is visible to one layer and not the other.** Every filter step records `handler-known=NO`; every interceptor step records `handler=DemoController#ok`, because handler resolution happens inside the `DispatcherServlet`, between the two layers. This is the mechanical reason a per-controller-method concern belongs in an interceptor and a per-request concern belongs in a filter.

**`/boom-controller` — two findings, neither planned.** `postHandle` does not run at all when the handler throws, even though `@RestControllerAdvice` successfully converted the exception to a `422`. And `afterCompletion` receives `ex=null`, not the `DemoException` — its `ex` parameter is the *still-unresolved* exception, so anything a `HandlerExceptionResolver` already handled arrives as `null`. Code that logs failures from `afterCompletion`'s `ex` silently records nothing for every exception the application handles properly.

**`/boom-filter` — where `@RestControllerAdvice` stops.** The advice never runs; no `CAUGHT` line appears. The client gets Tomcat's own error body (`{"timestamp":...,"status":500,"error":"Internal Server Error","path":"/boom-filter"}`), a genuinely different JSON shape from the advice's `{"error":"..."}`. An API's error contract silently changes shape depending on which layer failed.

A second, unplanned finding in the same trace: the outer filter's `finally` block recorded `status=200, response committed=false` for a request the client received as `500`. Tomcat sets the error status in `StandardHostValve`/`ErrorReportValve`, *after* the whole filter chain has unwound — so a filter that logs response status in a `finally` block reports `200` for a failed request. The committed stack trace also shows Spring's own `CharacterEncodingFilter` and `OncePerRequestFilter` sitting in the chain between the two demo filters.

**`/blocked` — what `preHandle` returning `false` actually skips.** `second-interceptor` returned `false`. Its *own* `afterCompletion` never ran; only `first-interceptor`'s did — Spring calls `afterCompletion` only on interceptors whose `preHandle` already returned `true`. No `postHandle` ran, and the handler was never invoked. Both filters still completed normally, because the short-circuit happens entirely inside the `DispatcherServlet`.
