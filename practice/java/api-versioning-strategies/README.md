# API Versioning Strategies — Real, Executed Demos

Backs [API Versioning Strategies](../../../syllabus/07-api-design/api-versioning-strategies.md)
(T-919). Real Spring MVC 6.1.14 request dispatch (`MockMvcBuilders.standaloneSetup`,
the actual `HandlerMapping` resolving every request below -- nothing
stubbed), no Maven/Gradle, jars fetched directly from Maven Central.

## What this proves

The same real breaking change -- V1's single `name` field split into V2's
`firstName`/`lastName` -- served through three independent, real Spring
MVC versioning mechanisms from one controller, `VersionedUserController`.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Run

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.ApiVersioningStrategiesTest
```

Real output ([full capture](test-run-output.txt)):

```
URI path v1: {"id":1,"name":"Ada Lovelace"}
URI path v2: {"id":1,"firstName":"Ada","lastName":"Lovelace"}
Header v1: {"id":1,"name":"Ada Lovelace"}
Header v2: {"id":1,"firstName":"Ada","lastName":"Lovelace"}
Accept: */* (no explicit version) -> real response: {"id":1,"name":"Ada Lovelace"}
No Api-Version header -> real HTTP status: 404
Media-type v1 (Content-Type echoed back): {"id":1,"name":"Ada Lovelace"}
Media-type v2 (Content-Type echoed back): {"id":1,"firstName":"Ada","lastName":"Lovelace"}
```

## The three strategies, and two real gotchas found running them

1. **URI path versioning** (`/api/v1/users/{id}` vs `/api/v2/users/{id}`) -- two
   completely separate `@GetMapping`s. No surprises: the URL itself picks the
   handler.

2. **Header versioning** (same URI, `@GetMapping(headers = "Api-Version=1")`).
   **Real gotcha, verified directly:** a request with **no** `Api-Version`
   header at all gets a real `404`, not a silent fallback to either version --
   Spring's `headers` request condition simply has no matching handler when
   the header is absent. "We version by header" implicitly requires every
   caller to always send that header, forever, or they get a hard failure
   with no indication *why*.

3. **Media-type / content-negotiation versioning** (same URI,
   `@GetMapping(produces = "application/vnd.myapi.v1+json")`/`v2+json`).
   **Real gotcha, verified directly:** a generic `Accept: */*` -- what many
   real HTTP clients send by default, including curl with no `-H Accept`
   flag -- does **not** `406`. It silently resolves to whichever
   version-specific handler Spring's content negotiation finds first
   (`V1` here). A caller that forgets to pin an exact `Accept` value gets
   the old version indefinitely, with no error telling them so.

Both gotchas point at the same underlying lesson: URI path versioning fails
loudly and unambiguously (a typo'd version number 404s immediately, in an
obvious way); header and media-type versioning can fail *silently* -- either
a hard 404 with a non-obvious cause, or a silent downgrade to an unintended
version -- unless the API explicitly documents and enforces what happens
when the version signal is missing or generic.
