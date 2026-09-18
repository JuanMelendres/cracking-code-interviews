# API Design — Core Concepts Batch — Real, Executed Demos

Backs [API Design](../../../syllabus/07-api-design/api-design.md) (T-803) — four
gaps this chapter previously lacked entirely: HATEOAS/Richardson Maturity Model,
RFC 9457 Problem Details, filtering/sorting query parameters, and bulk operations
with partial success. Real Spring MVC 6.1.14 request dispatch
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
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ApiDesignCoreConceptsTest
```

Real output ([full capture](test-run-output.txt)); all 6 tests pass.

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
