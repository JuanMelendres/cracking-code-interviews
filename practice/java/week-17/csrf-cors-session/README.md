# CSRF, CORS, and Session Security — Real Demo

Backs [`syllabus/12-security/csrf-cors-and-session-security.md`](../../../../syllabus/12-security/csrf-cors-and-session-security.md) (T-1308).

Pure JDK, no dependencies. Three real `com.sun.net.httpserver.HttpServer` instances,
driven by real `java.net.http.HttpClient` requests — no browser involved.

## Run it

```bash
mkdir -p out
javac -d out src/CsrfCorsSessionDemo.java
java -cp out CsrfCorsSessionDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

1. **CSRF** — an identical forged cross-site request (valid session cookie, no
   CSRF token) succeeds against a cookie-only endpoint and is rejected (403)
   against a synchronizer-token-protected one.
2. **CORS** — the server emits `Access-Control-Allow-Origin` only for an
   allowlisted `Origin` header. The demo's own printed output is explicit that
   this Java client still receives the response body either way — actual CORS
   *enforcement* (blocking a cross-origin script from reading the response)
   happens in the browser, not something a non-browser client like this one
   can demonstrate.
3. **Session fixation** — an attacker-chosen session identifier, presented to
   the server before login, becomes a genuinely authenticated session against
   a vulnerable endpoint, and never does against an endpoint that always
   mints a fresh identifier at authentication. The fixed endpoint's
   `Set-Cookie` header also carries `HttpOnly; Secure; SameSite=Strict`.
