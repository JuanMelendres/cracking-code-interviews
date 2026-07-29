# Week 7 Java — Security — runnable verification

Two real demos, pure JDK — no external dependencies (JWT uses `javax.crypto` directly; the filter chain is a plain reproduction of the `Filter`/`FilterChain` pattern Spring Security's real chain is built on).

## Reproduce

```bash
cd practice/java/week-07/security
mkdir -p out
javac -d out src/*.java
java -cp out JwtDemo
java -cp out SecurityFilterChainDemo
```

## 1. `JwtDemo.java` — real HMAC-SHA256 sign/verify

**Real output (last run):**

```
=== 1. Issue a token, verify it succeeds ===
Verification: VALID

=== 2. Tamper with the payload, verify it's rejected ===
Verification: INVALID (signature mismatch -- token was tampered with)

=== 3. An expired token is rejected, even with a correct signature ===
Verification: INVALID (expired)

=== 4. Why a valid, non-expired JWT cannot be revoked ===
Verification: VALID  <-- still VALID; nothing about deleting the user changes this token's bytes
```

All four verification outcomes are real cryptographic results (`Mac.getInstance("HmacSHA256")`), not simulated strings — the tampered token has a genuinely different payload but the same, now-mismatching, signature.

## 2. `SecurityFilterChainDemo.java` — real chain-of-responsibility trace

**Real output (last run), three scenarios:**

```
=== Scenario 1: valid token, non-admin path ===
  CorsFilter -> CsrfFilter -> AuthenticationFilter (valid) -> AuthorizationFilter -> CONTROLLER

=== Scenario 2: no Authorization header -- short-circuits at authentication ===
  CorsFilter -> CsrfFilter -> AuthenticationFilter: NO credentials -- SHORT-CIRCUITING, 401
  (CONTROLLER line never appears)

=== Scenario 3: valid token, wrong role for /admin -- short-circuits at authorization ===
  ... -> AuthenticationFilter (valid) -> AuthorizationFilter: lacks required role -- SHORT-CIRCUITING, 403
  (authenticated successfully, but never reached the controller)
```
