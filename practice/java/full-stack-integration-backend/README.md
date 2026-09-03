# Full-stack integration backend (F-214)

A real, separate Spring Boot backend (its own JVM process, its own port) that [`practice/frontend/react-nextjs-fundamentals/`](../../frontend/react-nextjs-fundamentals/) integrates with, backing [`syllabus/21-frontend-web/nextjs-fullstack-integration.md`](../../../syllabus/21-frontend-web/nextjs-fullstack-integration.md) (F-214). Reuses the same jar-based, no-Maven-required setup as [`practice/java/spring-vs-spring-boot/embedded-server-and-autoconfig`](../spring-vs-spring-boot/embedded-server-and-autoconfig/).

## Run it

```bash
bash fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.BackendApplication   # listens on :8080
```

## Endpoints

- `GET /api/public/greeting` — no auth, CORS-allowlisted for `http://localhost:5198` only (see `CorsConfig.java`).
- `GET /api/internal/secret-data` — requires header `X-Internal-Api-Key: f-214-demo-internal-shared-secret` (matches `INTERNAL_API_KEY` in the Next.js app's `.env.local`). Not CORS-allowlisted at all — a browser cannot reach it directly, by design; only the Next.js app's own `app/api/backend-proxy/route.js` (a server-to-server call) is meant to.

## Captured evidence

### A real, exact browser CORS failure — before `CorsConfig.java` existed

Real browser console, a genuine `fetch()` from the Next.js app (`http://localhost:5198`) to this backend's `/api/public/greeting` with no CORS configuration present:

```
Access to fetch at 'http://localhost:8080/api/public/greeting' from origin 'http://localhost:5198'
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

`fetch()` itself threw `TypeError: Failed to fetch` — the browser blocks CORS failures before the caller's own code ever sees a response object.

### The real fix, retested

With `CorsConfig.java` added (`allowedOrigins("http://localhost:5198")`, scoped to `/api/public/**` only) and the backend restarted, the SAME real browser `fetch()` call succeeded: `200`, real JSON body.

### A real, subtle finding: the CORS header exists on the wire but isn't JS-readable

```
$ curl -s -i -H "Origin: http://localhost:5198" http://localhost:8080/api/public/greeting | grep -i access-control
Access-Control-Allow-Origin: http://localhost:5198
```

But the SAME successful browser `fetch()` call's own `res.headers.get('access-control-allow-origin')` returned `null` — `Access-Control-Allow-Origin` is not itself in the default CORS-safelisted set of response headers exposed to JavaScript (only `Content-Type` and a few others are, unless the server adds `Access-Control-Expose-Headers`). The header is real and does its job (permitting the response through), but application code cannot introspect it without extra server-side configuration.

### The real double-protection on `/api/internal/secret-data`

```
$ curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/internal/secret-data
403   -- no X-Internal-Api-Key

$ curl -s -H "X-Internal-Api-Key: f-214-demo-internal-shared-secret" http://localhost:8080/api/internal/secret-data
{"secret":"Only reachable with the real shared secret..."}   -- 200
```

A real, live browser `fetch()` directly to `/api/internal/secret-data` (no CORS allowlist at all for this path) failed with the SAME `TypeError: Failed to fetch` — the browser cannot even attempt a call that would fail on the secret-header check anyway. See the Next.js app's own README for the BFF-side evidence (`app/api/backend-proxy/route.js`) completing this chain.
