# Java — Practice

Real, compiled/executed Java demos backing the `02-java`, `05-spring`, `06-databases`, `08-testing`, `09-messaging-event-driven`, `11-system-design`, `17-architecture`, `18-engineering-practices`, `19-leadership-staff`, and `22-ai-llm-engineering` syllabus domains, plus the original Interview Emergency Sprint study-pack material.

## How this directory is organized

Two layouts coexist, both real and both still in use:

- **`week-NN/`** — the original Interview Emergency Sprint material (`study-packs/week-01` through `week-25`), organized by the week it was built for rather than by topic.
- **Topic-named directories** (`collections/`, `concurrency/`, `jvm/`, `spring/`, `kafka/`, `hibernate-jpa/`, `architecture/`, `system-design/`, `cs-foundations/`, `engineering-practices/`, `oop-fundamentals/`, and one directory per newer gap-audit chapter, e.g. `solid-principles/`, `ood-interview-problems/`, `rest-api-fundamentals/`, `graphql-api-design/`, `grpc-api-design/`, `spring-data-jpa-repositories/`, `bean-validation-and-exception-handling/`) — one per canonical chapter, added as each chapter was written.

You generally don't need to browse this directory to find a demo — every syllabus chapter that says "real, executed" links directly to its own subdirectory here in its own `practice:` front-matter field and its Production Scenarios/Java Examples sections. The topic-grouping directories (`collections/`, `concurrency/`, `jvm/`, `spring/`, `kafka/`, etc.) and every `week-NN/` are containers, not demos themselves — the individual demo one level (sometimes two) deeper is where the real `README.md` (with the exact commands to reproduce it) and `fetch-deps.sh` (for anything needing a library beyond the JDK) actually live.
