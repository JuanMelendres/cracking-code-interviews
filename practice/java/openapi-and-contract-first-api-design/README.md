# OpenAPI and Contract-First API Design — Real, Executed Demo

Backs [OpenAPI and Contract-First API Design](../../../syllabus/07-api-design/openapi-and-contract-first-api-design.md)
(T-2414). Real Spring Boot 3.5.16 app, real embedded Tomcat, real
springdoc-openapi 2.9.1 generating a real OpenAPI 3.1 spec from nothing but
annotations on a real controller — no hand-written YAML/JSON spec anywhere
in this pack. Then a real `openapi-generator-cli` 7.25.0 run generates a
real Java client from that spec. No Maven/Gradle for the app itself; every
jar (including springdoc's full transitive dependency chain, resolved by
hand from each artifact's own POM) fetched directly from Maven Central.

## Setup and run

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.OpenApiDemoApplication --server.port=8099
```

In another terminal, once it's up:

```bash
curl -s http://localhost:8099/v3/api-docs | python3 -m json.tool > generated-openapi-spec.json
java -jar tools/openapi-generator-cli.jar generate \
  -i generated-openapi-spec.json -g java -o generated-client \
  --additional-properties=library=native,artifactId=order-api-client
```

Real captured output: [`demo-output.txt`](demo-output.txt) (boot log excerpt,
real HTTP responses, the real generated schema), the full real spec
([`generated-openapi-spec.json`](generated-openapi-spec.json)), the full real
codegen run log ([`openapi-generator-run-output.txt`](openapi-generator-run-output.txt)),
and the real generated client
([`generated-client/`](generated-client/) — trimmed to the load-bearing
files; the generator also scaffolds a full Maven/Gradle project, `.github`
workflow, docs, etc., not included here).

## What this proves

1. **The spec is generated from the code, not hand-maintained.**
   [`CreateOrderRequest.java`](src/demo/CreateOrderRequest.java) has no
   OpenAPI-specific documentation format at all — just `@Schema` (springdoc's
   annotation for description/example) and two ordinary `jakarta.validation`
   constraints, `@NotBlank` and `@Min(1)`. The real, running app's
   `/v3/api-docs` reflects them directly: `"minLength": 1` and
   `"required": ["customerName"]` from `@NotBlank`, `"minimum": 1` from
   `@Min(1)` — verified in the real captured spec, not asserted. This is the
   actual mechanism behind "the docs can't drift from the code" — there's no
   separate document to forget to update.

2. **A real, surprising dependency finding.** Running this demo the first
   time without Hibernate Validator on the classpath produced a real
   `NoClassDefFoundError: org/hibernate/validator/constraints/Range` from
   `/v3/api-docs` — springdoc-openapi-starter-common has a direct compile-time
   reference to a Hibernate-Validator-specific annotation class inside its
   constraint-reading code path (`SchemaUtils.hasValidationConstraints`), not
   a properly guarded optional check. Spring Boot's own auto-configuration
   degrades gracefully with only a `WARN` when no Bean Validation provider is
   present; springdoc's schema generation does not. Fixed by adding
   `hibernate-validator` (and its own real transitive chain — `jboss-logging`,
   `classmate`, `expressly`, `jakarta.el-api`) to `fetch-deps.sh`.

3. **Real codegen from that same real spec.** `openapi-generator-cli` 7.25.0
   (the real, self-contained CLI jar — not a description of what it does)
   generates a real `OrderControllerApi.createOrder(CreateOrderRequest)`
   method whose signature matches the controller's actual `@PostMapping`
   exactly, and a real `CreateOrderRequest` client model where
   `customerName` is marked `@Nonnull` with `required = true` — the same
   constraint, now enforced on the *consuming* side, propagated automatically
   from the same annotations in step 1. A client written against this
   generated code cannot silently drift from the real API shape, because it
   was never hand-written against a separate description of it.
