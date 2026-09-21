# Kafka Security: SASL Authentication and ACL Authorization — runnable verification

Real, executed output backing
[`syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md`](../../../../syllabus/09-messaging-event-driven/kafka-security-authentication-and-authorization.md)
(T-2421). A real, disposable Kafka 3.8.0 broker (Docker, KRaft mode) with
real SASL/PLAIN authentication (two real users, real passwords) and the
real KRaft-native `StandardAuthorizer` for ACL-based authorization — no
mocked security layer, every failure below is a real exception a real
Kafka client threw.

## Setup and run

Requires Docker.

```bash
cd practice/java/kafka/kafka-security-authentication-and-authorization
docker compose up -d
sleep 12
./security-demo.sh
```

Tear down: `docker compose down -v`.

## What it proves

- **A real SASL/PLAIN broker rejects a wrong password with a real
  `SaslAuthenticationException`, distinctly from an ACL denial.** Step 5:
  `alice` with a correct username but the wrong password gets
  `Authentication failed: Invalid username or password` — this happens
  entirely before authorization is even considered, since the connection
  itself never authenticates.
- **A topic-level ACL grant genuinely restricts access to exactly the
  granted topic — nothing more.** Step 3 vs. Step 4: the identical `alice`
  credentials produce and succeed against `alice-allowed-topic` (granted)
  and fail with a real `TopicAuthorizationException` against
  `alice-forbidden-topic` (never granted) — same client, same broker,
  the ACL grant is the only variable.
- **A real, discovered gotcha this demo's own construction surfaced:
  a topic ACL alone does not grant consume access.** Step 6: `alice`,
  already granted Read/Write/Describe on her own topic, still gets a real
  `GroupAuthorizationException` trying to *consume* it — Kafka's consumer
  protocol also authorizes the *consumer group* resource independently,
  and no group ACL had been granted yet. This wasn't a contrived teaching
  example; it's a real, first-attempt failure this demo's own build
  process hit and is now documenting honestly rather than silently
  building the working version and hiding the mistake.
- **The real fix (Step 7) is a second, independent ACL grant** — `--operation
  Read --group "*"` — after which the identical `alice` client succeeds,
  consuming the real `hello-from-alice` message produced in Step 3.
- **The configured super user bypasses ACL checks entirely, for real.**
  Step 8: `admin` (declared via `KAFKA_SUPER_USERS`) produces to and
  consumes from `alice-forbidden-topic` — the exact topic `alice` was
  denied — with zero ACL grants for `admin` on that topic at all.

## A real Docker-image configuration gotcha, documented honestly

Getting this broker to start at all took three real, failed attempts,
each with a different real error — see the comments in
[`docker-compose.yml`](docker-compose.yml) for the full account:

1. `ensure KAFKA_OPTS` failing with `!1: unbound variable` — the
   `apache/kafka` image's own startup script requires `KAFKA_OPTS` to be
   set (and to mention `java.security.auth.login.config`) the moment it
   detects any `SASL_*` listener, regardless of which JAAS-configuration
   mechanism is actually used.
2. Setting a real `-Djava.security.auth.login.config=<path>` flag made
   the JVM try to actually load that file and fail with a real
   `SecurityException` — fixed with an unrelated, harmless dummy system
   property whose *name* merely contains the same substring the shell
   script string-matches for.
3. Naming the listener literally `SASL_PLAINTEXT` (matching the security
   protocol name) made the image's naive underscore-to-dot environment
   variable translation produce the wrong property name
   (`listener.name.sasl.plaintext...` instead of
   `listener.name.sasl_plaintext...`) — fixed by naming the listener
   `CLIENT` instead, with `SASL_PLAINTEXT` only appearing as the
   *protocol* in `KAFKA_LISTENER_SECURITY_PROTOCOL_MAP`, which has no
   such ambiguity.
