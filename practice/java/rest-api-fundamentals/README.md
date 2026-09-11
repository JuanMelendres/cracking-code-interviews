# REST API Fundamentals — Real, Executed Demo

Backs [REST API Fundamentals](../../../syllabus/07-api-design/rest-api-fundamentals.md) (T-2205). A real Spring Boot 3.5.16 app (`demo.BookApplication`), embedded Tomcat, `localhost:8081` — same plain-jar setup as `practice/java/spring-mvc-fundamentals` (T-2203), a separate demo focused specifically on REST conventions rather than Spring's DI mechanism.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.BookApplication
```

## Reproduce the transcript

`curl-transcript.txt` is the complete, real, unedited output of the last run, matching the chapter's Section 7:

- `POST /books` twice with the identical body — real proof that POST is **not** idempotent (two distinct resources, `id:1` and `id:2`, both created).
- The real `201 Created` status and `Location` header on both POSTs.
- `PUT /books/1` twice with the identical body — real proof that PUT **is** idempotent (same `200`, same resulting state both times).
- `PUT /books/999` (a non-existent id) — real `404`.
- `DELETE /books/2` then `DELETE /books/2` again — real `204` then real `404`, and `GET /books/2` afterward confirming it's genuinely gone.

## Status codes beyond the original five (`curl-transcript-status-codes.txt`)

Added to close a real gap: the chapter originally covered only `200`/`201`/`204`/`404`/`500`. Real, executed evidence for six more codes a backend interview commonly expects, using the identical `BookController`:

1. **`400 Bad Request`** — a genuinely malformed JSON body (`{"title": "Broken JSON"` with no closing brace). Spring's own message-conversion layer rejects it before the controller method ever runs — no code of ours produces this response.
2. **`405 Method Not Allowed`** — `PATCH /books/1`, a verb with no `@PatchMapping`. Spring returns a real `Allow` header listing the verbs that *are* mapped on that path (`PUT, DELETE, GET`), automatically, with zero controller code written for this case.
3. **`422 Unprocessable Entity`** — `POST /books` with a blank title: syntactically valid JSON, semantically invalid content (a real, added `createBook` check). The real, practical distinction from `400`: this JSON parsed fine; it failed a business rule.
4. **`409 Conflict`** — `POST /books` twice with the identical `isbn` (a new, optional field): a genuine data conflict on a real-world business key, deliberately distinct from the existing title-duplication proof (title duplication is a *feature* of `POST`'s non-idempotency; isbn duplication is a *conflict* — the two are not the same shape).
5. **`304 Not Modified`** — a real conditional `GET`: capture `GET /books/1`'s `ETag` (`ShallowEtagHeaderFilter`, Spring's own built-in filter — zero hand-rolled hashing code), re-request with `If-None-Match: <that ETag>`, get a real, empty-bodied `304` back. Then `PUT` a real change and re-request with the *same, now-stale* `If-None-Match` value — a real `200` with a genuinely new `ETag`.

**A real bug this pack's own 304 demo caught before shipping:** `replaceBook` originally rebuilt the stored `Book` with a 3-argument constructor that never carried `isbn` through — so a `PUT` request that explicitly included `isbn` would silently drop it, even though PUT's entire contract is "replace with exactly what I sent." Fixed to use the 4-argument constructor; `curl-transcript-status-codes.txt`'s step 8 shows the corrected, real output with `isbn` correctly preserved after the `PUT`.

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.BookApplication
# then run the curl sequence in curl-transcript-status-codes.txt
```
