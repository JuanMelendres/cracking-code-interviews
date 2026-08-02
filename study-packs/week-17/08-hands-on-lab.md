---
title: "Hands-On Lab — Week 17 (Security Domain Closure)"
week: 17
document_type: study-pack-lab
status: draft
last_reviewed: 2026-08-02
---

# Hands-On Lab — Week 17 (Security Domain Closure)

Seven labs, one per topic. Labs 4 and 5 need a local PostgreSQL container; the rest are pure Java or Docker CLI.

**Verification note:** all commands below are real and were executed on OpenJDK 21.0.12, PostgreSQL 16 (`postgres:16-alpine`), OpenSSL 3.x, and Docker Scout v1.24.0.

## Lab 1 — IDOR and SSRF (T-1301)

```bash
cd practice/java/week-17/owasp-top-10/src
javac -d ../out IdorDemo.java SsrfDemo.java
java -cp ../out IdorDemo
java -cp ../out SsrfDemo
```

Expected: `IdorDemo` shows bob reading alice's invoice under the vulnerable handler and being blocked under the fixed one. `SsrfDemo` shows the vulnerable preview service leaking a fake internal metadata endpoint's contents, and the fixed version blocking it via allowlist.

## Lab 2 — Password hashing cost and signature tamper detection (T-1303)

```bash
cd practice/java/week-17/crypto/src
javac -d ../out PasswordHashingCostDemo.java SignatureTamperDemo.java
for n in 1 100000 600000; do java -cp ../out PasswordHashingCostDemo $n; done
java -cp ../out SignatureTamperDemo
```

Expected: hashing time increasing with iteration count (subtract the `n=1` baseline for the marginal cost); signature verification returning `true` for the original message and `false` the instant one character changes.

## Lab 3 — RBAC vs. ABAC (T-1302)

```bash
cd practice/java/week-17/authz-models/src
javac -d ../out RbacVsAbacDemo.java
java -cp ../out RbacVsAbacDemo
```

Expected: RBAC returning identical `true` for three users sharing one role; ABAC returning different, correct answers for the same three users once ownership/team/time attributes are considered.

## Lab 4 — SQL injection and output encoding (T-1305)

```bash
docker run -d --name security-pg -e POSTGRES_PASSWORD=demo -e POSTGRES_DB=appdb -p 15432:5432 postgres:16-alpine
docker exec -i security-pg psql -U postgres -d appdb -c "
  CREATE TABLE users (id serial primary key, username text, password_hash text, is_admin boolean default false);
  INSERT INTO users (username, password_hash, is_admin) VALUES ('alice','hash1',false),('admin','hash2',true);"

cd practice/java/week-17/injection/src
PG_JAR=~/.m2/repository/org/postgresql/postgresql/42.7.13/postgresql-42.7.13.jar
javac -d ../out -cp "$PG_JAR" SqlInjectionDemo.java OutputEncodingDemo.java
java -cp "../out:$PG_JAR" SqlInjectionDemo
java -cp ../out OutputEncodingDemo
```

Expected: the vulnerable login succeeding for username `admin' --` with any password; the `PreparedStatement` version correctly failing. The vulnerable HTML render containing a live `<script>` tag; the encoded render containing only inert entities.

## Lab 5 — Row-Level Security multi-tenancy (T-1307)

```bash
docker exec -i security-pg psql -U postgres -d appdb < practice/java/week-17/multi-tenancy/rls-demo.sql
docker exec -i security-pg psql -U app_user -d appdb -c "SET app.tenant_id = 'tenant_a'; SELECT * FROM orders;"
docker exec -i security-pg psql -U app_user -d appdb -c "SET app.tenant_id = 'tenant_b'; SELECT * FROM orders;"
docker exec -i security-pg psql -U app_user -d appdb -c "SELECT * FROM orders;"
docker exec -i security-pg psql -U postgres -d appdb -c "SELECT * FROM orders;"
```

Expected: each tenant sees only its own rows; no tenant context set returns zero rows; the superuser role returns every tenant's rows regardless of context — the RLS bypass caveat, demonstrated directly.

## Lab 6 — Key rotation with envelope encryption (T-1304)

```bash
cd practice/java/week-17/secrets-rotation/src
javac -d ../out KeyRotationDemo.java
java -cp ../out KeyRotationDemo
```

Expected: v1 and v2 records both decrypting correctly after rotation; the v1 record failing to decrypt once v1 is removed from the key ring, demonstrating why retirement must follow a completed re-encryption sweep.

## Lab 7 — Real SBOM and CVE scan (T-1306)

```bash
docker scout sbom eclipse-temurin:21-jre --format list | head -20
docker scout cves eclipse-temurin:21-jre
```

Expected: an SBOM listing on the order of 200+ packages; a CVE scan reporting a nonzero vulnerability count, including at least one finding in a package unrelated to any application code (exact counts drift over time as the image and CVE database update — that drift is the point).

## Self-Check

- [ ] All seven labs reproduced with your own matching (not necessarily identical) real output
- [ ] Can explain, for Lab 1, why IDOR passes functional testing that only ever uses the correct owner's credentials
- [ ] Can explain, for Lab 2, why the `n=1` run isolates JVM startup cost from the actual PBKDF2 marginal cost
- [ ] Can explain, for Lab 3, the specific request-context attribute that flips ABAC's answer for the same user and role
- [ ] Can explain, for Lab 4, why `--` specifically defeats the vulnerable query's password check
- [ ] Can explain, for Lab 5, why the superuser result differs from `app_user`'s despite an identical query
- [ ] Can explain, for Lab 6, why deleting v1 before a re-encryption sweep is equivalent to permanent data loss
- [ ] Can explain, for Lab 7, why a critical CVE can appear in a package no application code ever chose directly
