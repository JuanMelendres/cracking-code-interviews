# Bean Validation and Global Exception Handling — Real, Executed Demo

Backs [Bean Validation and Global Exception Handling](../../../syllabus/05-spring/bean-validation-and-global-exception-handling.md) (T-518). A real Spring Boot 3.5.16 app (`demo.OrderApplication`), embedded Tomcat, running on `localhost:8081` — plain jars fetched from Maven Central, no Maven/Gradle install, same dependency set as `practice/java/spring-mvc-fundamentals` plus real Hibernate Validator (the Jakarta Bean Validation reference implementation).

Real Bean Validation (`@Valid`, `@NotBlank`/`@Email`/`@Positive`/`@NotNull`, and a **custom, class-level cross-field constraint** — `@ValidPayment`, enforcing "a credit-card order needs a real card number") wired to a real `@RestControllerAdvice` global exception handler with three distinct real behaviors: structured `400` for validation failures, a specific `404` for a domain-level "not found," and a safe, generic `500` for an unhandled exception — verified to actually mask a sensitive detail from the client while it's still fully logged server-side.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.OrderApplication
```

## Reproduce the transcript

With the app running in another terminal:

```bash
curl -s -X POST http://localhost:8081/orders -H "Content-Type: application/json" \
  -d '{"customerEmail":"alice@example.com","quantity":2,"paymentMethod":"CREDIT_CARD","cardNumber":"4111111111111111"}'
curl -s -X POST http://localhost:8081/orders -H "Content-Type: application/json" \
  -d '{"customerEmail":"not-an-email","quantity":-5,"paymentMethod":null}'
curl -s -X POST http://localhost:8081/orders -H "Content-Type: application/json" \
  -d '{"customerEmail":"carol@example.com","quantity":1,"paymentMethod":"CREDIT_CARD"}'
curl -s http://localhost:8081/orders/42
curl -s http://localhost:8081/orders/999
```

`curl-transcript.txt` is the full, real output. `app.log` is the real server-side log from the same run, including the full stack trace (with the sensitive connection string) for the `id=999` request — proving the client response and the server log genuinely differ.

## What it proves

1. **A valid request succeeds (201)** — for both a `CREDIT_CARD` order (with a card number) and a `PAYPAL` order (without one).
2. **Multiple simple field violations are reported together, in one structured response** — an invalid email, a negative quantity, and a missing payment method all appear in a single `400`'s `fieldErrors` map, not just the first one found.
3. **A real, custom, class-level `@ValidPayment` constraint enforces a cross-field business rule** `@NotNull`/`@Positive`/etc. alone cannot express — a `CREDIT_CARD` order missing its card number is rejected with the custom constraint's own message, filed under `_object` (a class-level, not field-level, violation).
4. **A specific domain exception (`OrderNotFoundException`) gets its own `404` handler**, distinct from both validation failures and generic errors.
5. **An unhandled exception carrying a real sensitive detail (a database connection string) is fully logged server-side** (`app.log`) **but the client receives only a generic, safe `"An unexpected error occurred"` message** — real, verified evidence the catch-all handler doesn't leak internals, not just a claim.
