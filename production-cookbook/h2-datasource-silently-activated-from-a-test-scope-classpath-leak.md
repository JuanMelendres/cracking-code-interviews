---
title: "H2 DataSource Silently Activated from a Test-Scope Classpath Leak"
document_type: production-cookbook-entry
domain: spring
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/05-spring/spring-framework-vs-spring-boot.md
source: handbook/spring/spring-framework-vs-spring-boot.md#production-scenarios
---

# H2 DataSource Silently Activated from a Test-Scope Classpath Leak

## Context

A service that should always use its configured production PostgreSQL database is investigated during an incident. A build configuration change had accidentally widened a testing-support library's dependency scope from `test` to `compile`/`runtime`, and that library transitively pulls in the H2 in-memory database jar.

## Symptoms

During an incident investigation, the service is discovered to have briefly been serving reads from an empty in-memory H2 database after a deployment — data appeared to have vanished, then reappeared after a restart with the correct configuration reasserting itself.

## Impact

A brief window of serving incorrect (empty) data to real users, and a confusing, hard-to-reproduce incident that initially looked like data loss.

## Initial Hypotheses

- An actual database outage or data-loss event — checked, and ruled out: the production database's own logs show no interruption or data change.
- A caching layer serving stale empty results — checked, and ruled out: the caching layer was disabled in the affected window.
- An unintended auto-configuration activating a different `DataSource` — correct.

## Evidence

The affected deployment's dependency manifest shows the H2 database jar was present on the runtime classpath — added transitively by a testing-support library that was supposed to be scoped to `test` only, but a build configuration change accidentally widened its scope to `compile`/`runtime`. With H2 genuinely present on the classpath and no explicit `DataSource` bean overriding it in that specific deployment path, Spring Boot's `DataSourceAutoConfiguration` legitimately matched and configured an in-memory H2 `DataSource`, which then raced with (and briefly won over) the intended explicit production `DataSource` configuration during startup ordering.

## Investigation Timeline

1. **Incident reported** as apparent data loss — production reads briefly returned empty results before self-correcting after a restart.
2. **Production database's own logs reviewed** and confirmed no interruption or data change occurred at any point — ruling out an actual database outage.
3. **Caching layer reviewed** and confirmed disabled in the affected window — ruling out stale cached empty results as the cause.
4. **Dependency manifest for the affected deployment inspected**, revealing the H2 in-memory database jar present on the runtime classpath, contrary to expectation.
5. **Build configuration history reviewed**, tracing the H2 jar's presence to a recent change that widened a testing-support library's dependency scope from `test` to `compile`/`runtime`, making H2 a transitive runtime dependency.
6. **Auto-configuration conditions confirmed as the mechanism** — with H2 now genuinely on the classpath and no explicit `DataSource` bean winning the race during that specific startup path, `DataSourceAutoConfiguration`'s `@ConditionalOnClass` check legitimately matched and configured an in-memory H2 `DataSource`.

## Root Cause

Not an auto-configuration bug — auto-configuration worked exactly as designed, reacting correctly to a genuine (if accidental) classpath change. The real root cause was the dependency-scope leak that put H2 on the runtime classpath in the first place, combined with no explicit startup-time assertion confirming which `DataSource` was actually active.

## Immediate Mitigation

Roll back the build configuration change that widened the testing library's dependency scope, and redeploy.

## Permanent Fix

Add a build-time check asserting that no test-scoped-only dependencies (including transitive ones) are present on the production runtime classpath, and add an explicit `@ConditionalOnProperty`-gated or profile-gated assertion at startup that the intended `DataSource` bean (by name or type) is the one actually active, failing fast rather than silently accepting whichever `DataSource` auto-configuration happened to win.

## Alternatives Considered

Explicitly excluding `DataSourceAutoConfiguration` via `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)` and defining the `DataSource` entirely by hand — a more defensive posture some teams choose, but rejected here as a broad fix for a narrow, build-config-specific bug; the actual problem was the dependency scope leak, not that auto-configuration exists.

## Trade-offs

A startup-time assertion adds a small amount of extra application code in exchange for converting "this class of bug is silent" into "this class of bug fails loudly at startup, immediately, in every environment."

## Prevention

Treat any change to build-file dependency scopes as a review-worthy classpath change, precisely because Spring Boot's auto-configuration reacts directly and automatically to exactly that signal.

## Monitoring and Alerts

- Add a startup-time log line (or a structured health-check field) reporting the concrete class and identity of the active `DataSource` bean, so any deployment's actual runtime configuration is directly observable rather than assumed from the intended configuration files.
- Add a CI/build-time dependency-tree audit that fails the build if any dependency known to be test-scoped-only (H2, embedded test databases, mock servers) appears anywhere in the resolved `compile`/`runtime` classpath, catching the scope leak before a deployment rather than after an incident.
- Alert on any read returning an unexpectedly small or empty result set for a table known to hold substantial data, as a defense-in-depth signal independent of the specific classpath-leak cause — this kind of anomaly detection would have caught the incident's user-facing symptom immediately rather than only becoming clear during investigation.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service briefly appeared to have lost production data after a deployment, self-correcting after a restart, with no evidence of an actual database outage.
- **Task:** determine how data could appear to vanish and reappear with no interruption or change in the actual production database.
- **Action:** ruled out a real database outage and a stale cache, then inspected the deployment's dependency manifest and found the H2 in-memory database jar unexpectedly present on the runtime classpath, tracing it to a build configuration change that widened a test-only dependency's scope.
- **Result:** rolled back the scope change to resolve the immediate incident, then added a build-time dependency-scope audit and a startup-time assertion confirming the active `DataSource`'s identity, converting this entire bug class into an immediate, loud build- or startup-time failure.

## Staff-Level Discussion

The most important framing for this incident is that Spring Boot's auto-configuration is not the defect — it did precisely what it is documented and designed to do, reacting correctly to a genuine classpath signal. The actual defect lived one layer away, in build tooling that most engineers don't think of as a security- or correctness-relevant surface: a dependency-scope change is easy to review as "just moving a test library," without recognizing that it changes what Spring Boot's conditional auto-configuration sees and acts on at runtime. This is a strong argument for a general Staff-level principle: any mechanism that reacts automatically and silently to an environmental signal (a classpath, an environment variable, a feature flag's default) needs an explicit, loud assertion of its actual resolved state at startup, because "trust that the intended configuration was applied" is not a sufficient safeguard once an automatic mechanism sits between intent and outcome. Organizationally, this also argues for build-dependency-scope changes receiving the same review scrutiny as application code changes — the build file is effectively part of the application's runtime behavior surface for any framework, like Spring Boot, whose behavior is classpath-conditional, and treating it as "just configuration" understates its actual blast radius.

## Related Handbook Chapters

- [Spring Framework vs. Spring Boot](../syllabus/05-spring/spring-framework-vs-spring-boot.md) — canonical auto-configuration conditions-evaluation mechanics and the real conditions-evaluation report this incident's diagnosis relies on.
- [Connection Pooling and Sizing (HikariCP)](../syllabus/06-databases/connection-pooling-and-sizing.md) — related `DataSource`-adjacent configuration concerns relevant once the correct `DataSource` is confirmed active.
