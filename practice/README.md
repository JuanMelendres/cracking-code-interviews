# Practice — Real, Executed Code

Every technical claim in `syllabus/` that says "real, executed" or "real, compiled" points
back to a file in here. Nothing in this directory is pseudocode or a hypothetical example —
each demo was actually compiled/run, and where a chapter quotes output, that output was
captured from one of these runs.

## What's here

| Directory | Contents |
|---|---|
| [`java/`](java/) | Runnable Java demos, organized by topic (collections, concurrency, JVM internals, Spring, Kafka, Hibernate/JPA, security, etc.) and by week for the original study packs |
| [`frontend/`](frontend/) | Runnable React/Next.js/TypeScript demos, one directory per topic |
| [`sql/`](sql/) | PostgreSQL lab scripts (index internals, replication, MVCC, sharding, CDC, and more) |
| [`git/`](git/) | Git internals demos (object model, merge vs. rebase, reflog/bisect) |
| [`architecture/`](architecture/) | ADR examples referenced from the Architecture domain |
| [`system-design/`](system-design/) | System-design exercise material |
| [`docker-fundamentals/`](docker-fundamentals/) | A real built-and-run Docker image walkthrough |
| [`k8s/`](k8s/) | Kubernetes exercise material |
| [`production/`](production/) | Postmortem-example source material |
| [`mock-interviews/`](mock-interviews/) | Real mock-interview session transcripts |

## How to use this

You generally don't start here — start at the `syllabus/` chapter, cheat sheet, or
study-pack week that references a specific file, then come here to actually run it
yourself. Each Java/frontend demo directory has its own `README.md` with the exact
commands to reproduce it (and a `fetch-deps.sh` script for anything needing a library
beyond the JDK or npm's own lockfile).

Prerequisites vary by demo: JDK 17+ (21 used throughout), Node.js, and Docker (for
anything needing a real Postgres or Kafka instance) cover nearly everything here.
