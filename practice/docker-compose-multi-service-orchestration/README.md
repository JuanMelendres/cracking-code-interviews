# Docker Compose: Multi-Service Orchestration — Real, Executed Demo

Backs [Docker Compose: Multi-Service Orchestration](../../syllabus/14-devops-containers/docker-compose-multi-service-orchestration.md) (T-2428). Two real Compose stacks, run with actual Docker Compose (v5.3.1) against a genuinely fresh Postgres volume each time — no simulation.

## Reproduce

```bash
javac -d api/src api/src/ApiServer.java

# Scenario 1: BROKEN -- depends_on with no healthcheck condition
docker compose down -v --remove-orphans   # guarantee a fresh volume
docker compose up -d --build
docker compose logs api                   # real "Connection refused" on the startup check
curl -s http://localhost:8081/db-check    # succeeds once retried, after Postgres finishes initializing
docker compose down -v --remove-orphans

# Scenario 2: FIXED -- healthcheck + condition: service_healthy
docker compose -f docker-compose.fixed.yml up -d --build
docker compose -f docker-compose.fixed.yml logs api   # connects on the first attempt
docker compose -f docker-compose.fixed.yml down -v --remove-orphans
```

`output-transcript.txt` reproduces both real runs end to end. The point of this demo: `depends_on: [db]` (list form) only waits for the **container process** to start — on a genuinely fresh Postgres volume, `initdb` takes real, observable time, and `api`'s own startup-time connection attempt loses that race (`FAILED to connect to db:5432 after 10ms -- Connection refused`), even though `docker compose`'s own log shows `db-1 Started` before `api-1 Starting`. Adding a real `healthcheck` (`pg_isready`) to `db` and `condition: service_healthy` to `api`'s `depends_on` entry makes Compose genuinely wait for Postgres to accept connections — visible directly in the second run's log as `db-1 Waiting` → `db-1 Healthy` before `api-1` ever starts, and `api`'s own startup check connects on the first attempt every time.

Re-run twice on a fresh volume each time (`docker compose down -v` removes it) — the broken scenario's race reproduced identically both times, not a one-off fluke.

A second, real thing this demo proves: `api` reaches Postgres at the hostname `db` — never an IP address, never `localhost` — because Compose's default network gives every service real DNS resolution by service name alone.
