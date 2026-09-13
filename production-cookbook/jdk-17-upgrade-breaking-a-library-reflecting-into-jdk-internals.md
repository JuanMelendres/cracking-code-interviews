---
title: "JDK 17 Upgrade Breaking a Library Reflecting Into JDK Internals"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/language-core/java-platform-module-system.md
source: syllabus/02-java/language-core/java-platform-module-system.md#production-scenarios
---

# JDK 17 Upgrade Breaking a Library Reflecting Into JDK Internals

## Context

A service upgrades its JDK from 8 to 17. A dependency (an older ORM, a serialization library, an internal-tooling agent) reflectively accesses a JDK internal class or field that worked without complaint on Java 8.

## Symptoms

The service starts failing at startup, or throwing `InaccessibleObjectException` at runtime, with no code change on the team's own side.

## Impact

The service fails to start, or a specific feature path that depended on the reflective access breaks.

## Initial Hypotheses

- A real regression in the team's own code — checked, the failure traces into JDK-internal reflective access inside the dependency, not application code.
- A JVM configuration problem — checked, the JVM starts fine for everything not depending on that specific reflective path.
- Strong encapsulation (JEP 396's `--illegal-access` default flip in Java 16, then JEP 403 making the flag inert in Java 17) now actually enforces module boundaries the dependency was silently violating for years — correct.

## Evidence

`jdeps --jdk-internals` run against the dependency's JAR identifies exactly which internal JDK packages it reaches into reflectively.

## Investigation Timeline

1. Startup failure or `InaccessibleObjectException` observed immediately after the JDK 8 → 17 upgrade.
2. Team's own code and JVM configuration ruled out as the cause.
3. `jdeps --jdk-internals` run against the dependency, identifying the specific internal packages accessed.
4. Findings cross-checked against JEP 396/403's documented enforcement timeline.

## Root Cause

Strong encapsulation, enforced in two steps (JEP 396 in Java 16, JEP 403 in Java 17), now blocks a reflective access pattern the dependency relied on that was previously silently permitted.

## Immediate Mitigation

Add the specific `--add-opens <module>/<package>=ALL-UNNAMED` (or scoped to the specific consuming module) flags `jdeps` identified as needed, as a documented, temporary JVM startup flag.

## Permanent Fix

Upgrade the dependency to a version that has removed its reliance on JDK-internal reflection (most major libraries did this specifically for Java 17+ compatibility) rather than carrying `--add-opens` flags indefinitely.

## Alternatives Considered

Staying on an older JDK LTS release — rejected as a long-term plan, since it only delays an upgrade that strong encapsulation will eventually force regardless, and forgoes real JDK improvements (performance, security patches) in the meantime.

## Trade-offs

`--add-opens` flags are a real, working mitigation but are a form of technical debt — each one is a documented exception to a safety mechanism the JDK now defaults to, and needs to be tracked and eventually removed as dependencies are upgraded.

## Prevention

Run `jdeps --jdk-internals` against new dependencies before adopting them, and treat any reliance on JDK-internal reflection as a real, trackable risk rather than an invisible one.

## Monitoring and Alerts

- A CI step running `jdeps --jdk-internals` against the full dependency set on every JDK version bump, failing the build (or at least warning loudly) rather than discovering the break at deploy time.
- A tracked inventory of any `--add-opens` flags in use, each tied to a specific dependency-upgrade ticket for removal.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent upgrade break.

- **Situation:** a routine JDK 8 → 17 upgrade broke a dependency with no application-code change.
- **Task:** diagnose and unblock the upgrade without reverting it.
- **Action:** ran `jdeps --jdk-internals` against the dependency to identify the exact internal packages accessed, applied targeted `--add-opens` flags as a bridge, and tracked a dependency upgrade to remove them.
- **Result:** unblocked the JDK upgrade immediately, with a tracked path to removing the temporary flags.

## Staff-Level Discussion

Strong encapsulation didn't create a new class of bug — it turned a previously silent, working-by-accident reliance on unstable internals into a real, visible failure, specifically so it could be found and fixed rather than discovered later at a worse time. The organizational lesson is to make `jdeps --jdk-internals` a standing pre-adoption check for new dependencies, not a reactive tool reached for only after an upgrade breaks.

## Related Handbook Chapters

- [Java Platform Module System (JPMS)](../syllabus/02-java/language-core/java-platform-module-system.md) — the canonical strong-encapsulation and `jdeps` mechanics behind this incident.
