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
