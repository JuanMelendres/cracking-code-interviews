# 12-factor config, and fail-fast validation (T-1008) — runnable verification

Real, executed Java 21 output backing
[`handbook/system-design/twelve-factor-config.md`](../../../../handbook/system-design/twelve-factor-config.md)
(T-1008). No framework — a real, minimal config loader implementing the
12-factor app's config precedence (defaults < config file < environment
variables < command-line arguments), plus a real, direct comparison between
a service that validates required config at startup and one that doesn't.

## Files

- `AppConfig.java` — the real config loader, layering all four real sources
  in the correct precedence order.
- `config.properties` — the real config file both demos share; it never sets
  `database.url`, which `FailFastDemo` relies on deliberately.
- `PrecedenceDemo.java`, `FailFastDemo.java` — the two demos below.

## Run

```bash
cd practice/java/system-design/twelve-factor-config
mkdir -p out
javac -d out *.java
java -cp out PrecedenceDemo
APP_TIMEOUT_MS=5000 java -cp out PrecedenceDemo --timeout.ms=9000
java -cp out FailFastDemo
```

## Real observed output (last full run, Java 21)

### 1. `PrecedenceDemo` — real config precedence, one layer at a time

Defaults + file only:

```
timeout.ms      = 2000                       (from file:config.properties)
max.retries     = 3                          (from default)
service.name    = file-configured-service    (from file:config.properties)
```

Adding a real environment variable (`APP_TIMEOUT_MS=5000`):

```
timeout.ms      = 5000                       (from env:APP_TIMEOUT_MS)
```

Adding a real command-line argument on top (`--timeout.ms=9000`):

```
timeout.ms      = 9000                       (from cli:--timeout.ms=9000)
```

Each layer really overrides the one below it for the same key, and nothing
else — `max.retries` and `service.name` stay at whichever source last set
them, unaffected by an override targeting a different key. This is the
identical real precedence order Spring Boot's own `PropertySource` resolution
uses (`application.properties` < OS environment variables < command-line
arguments), reproduced here from scratch with no framework.

### 2. `FailFastDemo` — a confusing runtime failure vs. a clear startup failure

Both scenarios run against the identical, real config, deliberately missing
`database.url`:

```
=== BUGGY: no startup validation -- the app starts up "successfully" ===
Real failure, deep in business logic, minutes/hours after startup:
  NullPointerException: Cannot invoke "String.toUpperCase()" because "<local1>" is null

=== FIXED: real startup validation -- fails immediately, at boot, with a clear message ===
Real startup failure -- BEFORE the app ever accepts a single request:
  Missing required config key 'database.url' -- refusing to start. Set it via config file, an APP_DATABASE_URL environment variable, or --database.url=... on the command line.
```

The root cause is identical in both cases — a missing required config value
— but the real, observed failure experience is completely different. The
unvalidated version fails with a generic `NullPointerException` at whatever
point in business logic first touches the missing value, potentially minutes
or hours after a "successful" deploy, with no indication of which config key
is actually the problem. The validated version fails immediately, before the
service ever accepts a request, with a message naming the exact missing key
and every real way to supply it.

## Real discoveries made while building this pack

No bugs were hit while building this pack — both demos produced correct,
real output on the first run, including the exact `NullPointerException`
this demo was specifically designed to provoke honestly (a real missing
value, not a contrived exception).
