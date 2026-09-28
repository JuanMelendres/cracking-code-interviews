# HTTP Caching for APIs: ETag, Conditional Requests, and Cache-Control — Real Demo

Backs [`syllabus/07-api-design/http-caching-for-apis.md`](../../../../syllabus/07-api-design/http-caching-for-apis.md) (T-2435).

A real `com.sun.net.httpserver` server and a real `java.net.http.HttpClient`, exchanging conditional requests. Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/HttpCachingDemo.java
java -cp out HttpCachingDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **A strong `ETag` derived from the body**, returned on both `200` and `304` — omitting the directives on a `304` is a real bug, because the client then falls back to heuristic freshness.
- **A real `304 Not Modified`** in response to a matching `If-None-Match`, with a genuinely empty body (`sendResponseHeaders(304, -1)`).
- **The ETag changing when the resource changes** — the same `If-None-Match` now produces a `200` with a new validator, which is the whole correctness argument for validators over timestamps.
- **Measured savings:** 50-byte body versus a 0-byte `304`, and 2 server-side renders for 4 requests — the `304`s skipped serialization entirely.
- **The limit of revalidation:** both `304`s still cost a full round trip. `Cache-Control: max-age` is what removes the round trip; a validator only removes the body. That distinction is the single most common gap in an interview answer on this topic.
