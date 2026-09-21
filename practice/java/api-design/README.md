# API Design — Core Concepts Batch — Real, Executed Demos

Backs [API Design](../../../syllabus/07-api-design/api-design.md) (T-803) — five
gaps this chapter previously lacked entirely: HATEOAS/Richardson Maturity Model,
RFC 9457 Problem Details, filtering/sorting query parameters, bulk operations
with partial success, and (added 2026-09-21) the async long-running-operation
pattern (202 Accepted + polling). Real Spring MVC 6.1.14 request dispatch
(`MockMvcBuilders.standaloneSetup` — the actual `HandlerMapping` and
`ExceptionHandlerExceptionResolver` resolving every request below, nothing
stubbed), no Maven/Gradle, jars fetched directly from Maven Central.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Run

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher \
    --select-class demo.ApiDesignCoreConceptsTest --select-class demo.AsyncLongRunningOperationTest
```

Real output ([full capture](test-run-output.txt)); all 8 tests pass.

## What each test proves

1. **HATEOAS — state-dependent `_links`, not asserted, computed.**
   `hateoas_pendingOrderExposesCancelAndShip` / `hateoas_deliveredOrderExposesReturnOnly`:
   the *same* `GET /orders/{id}` endpoint returns a `cancel` and `ship` link for a
   `PENDING` order, and a `return` link (no `cancel`, no `ship`) for a `DELIVERED`
   one — real JSON, real state-driven branching in
   [`OrderResource.java`](src/demo/OrderResource.java), not a hardcoded client-side
   rulebook of "what actions exist for this resource."

2. **Richardson Maturity Model — Level 0 vs. the resource-oriented shape above.**
   `level0RpcContrast_sameDataOneUriOneVerb`: a real `POST /rpc` endpoint with a
   single URI and a single verb, dispatching on an `"action"` field
   (`{"action":"getOrder","id":1}`) — the RPC-over-HTTP shape the Richardson
   Maturity Model calls Level 0 — returns byte-for-byte the same JSON as the real
   `GET /orders/1`. Same data, two real endpoints, letting the maturity-level
   difference be read directly off the request shape instead of asserted in prose.

3. **RFC 9457 Problem Details for HTTP APIs — a real `ProblemDetail`, not a
   description of one.** `problemDetails_notFound_returnsRfc9457Shape`: a real
   `OrderNotFoundException` is translated by a real Spring 6
   `@ExceptionHandler` returning `org.springframework.http.ProblemDetail`
   (built into Spring Framework 6, no extra dependency). Verified directly: a
   real `404` status, a real `application/problem+json` content type (Spring's
   `ExceptionHandlerExceptionResolver` sets it automatically because
   `ProblemDetail` implements `ErrorResponse`), and a body matching RFC 9457's
   `type`/`title`/`status`/`detail`/`instance` fields plus a custom `errorCode`
   extension property.

4. **Filtering and sorting query parameters.**
   `filteringAndSorting_realQueryParams`: a real `GET /orders?status=PENDING`
   returns only the two seeded `PENDING` orders (verified by ID, not just count);
   a real `GET /orders?sort=amount,desc` returns orders in real descending-amount
   order (verified by string position of the highest vs. lowest amount in the
   actual response body, not a separately-asserted list).

5. **Bulk operations with per-item partial success.**
   `bulkCreate_partialSuccessPerItem`: a real `POST /orders/bulk` with 3 items (2
   valid, 1 with a negative amount) returns a real HTTP `207` (Multi-Status) with
   a per-item result array — index 0 succeeded with a real generated ID, index 1
   failed with `"amount must be positive"`, index 2 succeeded — proving why bulk
   endpoints can't collapse to one all-or-nothing status code.

6. **Async long-running operations — 202 Accepted + polling, with a genuinely
   asynchronous background job, not a stubbed flag.** [`ReportJobService.java`](src/demo/ReportJobService.java)
   submits real work to a real `ExecutorService`, with a real `Thread.sleep(300)`
   standing in for genuine work — the calling thread never blocks on it.
   `submitReturns202WithLocation_andRealBackgroundJobEventuallyCompletes` proves
   the full real lifecycle: `POST /reports` returns `202 Accepted` with a real
   `Location` header immediately (the job hasn't finished — it's only just been
   submitted); an immediate `GET` on that `Location` returns `202` again with a
   real `Retry-After: 1` header, since the background thread hasn't had its real
   300ms yet; hitting `/result` directly at this point returns a real `425 Too
   Early` — a deliberate, pragmatic reuse of the status code (RFC 8470 itself
   scopes `425` to TLS early-data replay risk, not general "not ready yet"
   semantics — stated honestly here, not claimed as the RFC's intended use) —
   the client jumped ahead of the real 303 redirect the
   status endpoint is meant to hand out once actually done. A real polling loop
   (no fixed sleep-then-assume) polls every 50ms until the status endpoint
   returns `303 See Other` with a `Location` pointing at the result — measured
   directly at **real elapsed time ≥ 300ms** (asserted, not assumed), proving the
   test genuinely waited on real background work rather than a synchronous call
   dressed up with a 202 status code. Following that `Location` returns a real
   `200 OK` with the actual generated report body.
