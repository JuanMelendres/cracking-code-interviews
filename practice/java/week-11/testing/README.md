# Week 11 Java — Test Pyramid: Unit (Mockito) + Integration (real Postgres) — runnable verification

Two real test suites, run via JUnit 5's console launcher (no Maven/Gradle needed).

## Setup

```bash
cd practice/java/week-11/testing
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
```

## 1. Unit test — mocked dependency — `PaymentServiceUnitTest.java`

No Docker needed.

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher execute --select-class PaymentServiceUnitTest --details=tree
```

**Real observed output (last run):**

```
├─ JUnit Jupiter ✔
│  └─ PaymentServiceUnitTest ✔
│     ├─ exhaustsRetriesAndReturnsFalseOnPermanentFailure() ✔
│     ├─ succeedsOnThirdAttemptAfterTwoFailures() ✔
│     └─ succeedsImmediatelyWithNoRetriesNeeded() ✔

Test run finished after 460 ms
[3 tests successful, 0 tests failed]
```

## 2. Integration test — real Postgres — `OrderRepositoryIntegrationTest.java`

```bash
docker run --rm -d --name week11-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week11 -p 5434:5432 postgres:16
sleep 4
docker exec -e PGPASSWORD=postgres week11-pg psql -U postgres -d week11 \
  -c "CREATE TABLE test_orders (id BIGSERIAL PRIMARY KEY, customer_id TEXT NOT NULL, amount_cents BIGINT NOT NULL);"

java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher execute --select-class OrderRepositoryIntegrationTest --details=tree
```

**Real observed output (last run):**

```
├─ JUnit Jupiter ✔
│  └─ OrderRepositoryIntegrationTest ✔
│     └─ insertedOrderIsReallyPersistedAndReadableBack() ✔

Test run finished after 154 ms
[1 tests successful, 0 tests failed]
```

**Note:** this uses direct JDBC + a plain `docker run`, not the Testcontainers library itself — see `study-packs/week-11/02-integration-testing-against-real-dependencies.md` §4 for why, stated explicitly.

## Teardown

```bash
docker rm -f week11-pg
```
