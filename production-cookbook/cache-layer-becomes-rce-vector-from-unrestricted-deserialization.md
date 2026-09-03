---
title: "Cache Layer Becomes an RCE Vector from Unrestricted Deserialization"
document_type: production-cookbook-entry
domain: java-core
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md
source: handbook/java-core/serialization-hazards-and-alternatives.md#production-scenarios
---

# Cache Layer Becomes an RCE Vector from Unrestricted Deserialization

## Context

A service caches objects using Java's built-in serialization, storing serialized bytes in a shared cache (Redis/Memcached) that's reachable, directly or indirectly, by a component accepting less-trusted input.

## Symptoms

A routine dependency upgrade introduces a transitive library on the classpath containing a class with dangerous side effects reachable during deserialization (a "gadget"). A security scan (or an actual incident) reveals the cache's deserialization path is now exploitable for remote code execution.

## Impact

A critical-severity vulnerability — potential remote code execution — introduced not by a bug in the service's own code, but by the mere presence of a dangerous class anywhere on the classpath, combined with unrestricted deserialization of less-trusted bytes.

## Initial Hypotheses

- A bug in the caching logic itself — checked, and ruled out: the caching code is straightforward and correct.
- The new library itself has a known, direct vulnerability — checked, and ruled out: the library's own code has no bugs; it's simply "gadget material" when combined with unrestricted deserialization.
- The deserialization path itself has no restriction on what classes it will reconstruct — correct.

## Evidence

The service's `ObjectInputStream` usage matches the baseline unrestricted-deserialization configuration exactly: `ObjectInputFilter.Config.getSerialFilter()` returns `null`, and no per-stream filter is set anywhere in the caching code — direct confirmation that any class reachable on the classpath can be deserialized, unrestricted.

## Investigation Timeline

1. **Security scan (or incident) flags the cache's deserialization path** as newly exploitable for remote code execution, coinciding with a routine dependency upgrade.
2. **Caching logic reviewed** and confirmed straightforward and correct — the bug is not in how the service reads or writes cache entries.
3. **New library's own code reviewed** and confirmed to contain no direct vulnerability of its own — its risk exists only in combination with unrestricted deserialization elsewhere in the process.
4. **`ObjectInputStream` configuration checked directly**, confirming `ObjectInputFilter.Config.getSerialFilter()` returns `null` and no per-stream allow-list filter is configured anywhere in the caching code.
5. **Root mechanism confirmed**: any class reachable on the classpath — including the newly-introduced transitive dependency — can be reconstructed from cached bytes with no validation gate, and the new dependency happens to supply a class whose side effects during reconstruction are dangerous.

## Root Cause

Unrestricted deserialization of less-trusted bytes, with no allow-list gate, combined with the mere presence of a dangerous class anywhere on the classpath (not necessarily used anywhere in the service's own code).

## Immediate Mitigation

Configure a process-wide `ObjectInputFilter` (via `-Djdk.serialFilter` or `ObjectInputFilter.Config.setSerialFilter`) restricting deserialization to an explicit allow-list of the service's own cached types, immediately closing the gadget-reachability window regardless of what else is on the classpath.

## Permanent Fix

Migrate the cache serialization format away from Java's built-in mechanism entirely — to JSON, Protocol Buffers, or another format with no equivalent "reconstruct arbitrary classpath classes from bytes" capability — removing the entire hazard class structurally rather than managing it via an allow-list that must be kept correct forever.

## Alternatives Considered

Removing the offending transitive dependency — a real, partial mitigation for this one incident, but doesn't address the structural risk that any future dependency could introduce another gadget; the permanent fix targets the actual root cause (the deserialization mechanism itself), not this one instance of it.

## Trade-offs

Migrating away from Java serialization requires real migration work (cache format versioning, a transition period) — accepted given the severity class (RCE) of the risk being closed structurally rather than merely mitigated.

## Prevention

Any service deserializing data from a source that isn't fully, statically trusted (a shared cache, a message queue, any network input) should be reviewed for `ObjectInputFilter` configuration at minimum, and ideally migrated away from Java's built-in serialization for that boundary entirely.

## Monitoring and Alerts

- Add a startup-time assertion (or a CI check against the running configuration) that `ObjectInputFilter.Config.getSerialFilter()` is non-null for any process performing Java-native deserialization of cache or queue data, alerting immediately if a deployment or configuration drift removes the filter.
- Add dependency-scanning tooling (SCA) rules specifically tuned to flag known gadget-chain-capable libraries (a well-documented, actively-maintained list exists in the security community) appearing anywhere on the classpath of a service that performs Java-native deserialization, not only libraries with their own direct CVEs.
- Alert on `InvalidClassException`/filter-rejection events from the configured `ObjectInputFilter` in production — while these are the filter working as intended, a spike in rejections can indicate either an active exploitation attempt or an unexpected legitimate class needing to be added to the allow-list, and both are worth immediate visibility.

## Interview Story

Present it as a representative scenario to adapt, not a claimed personal history.

- **Situation:** a service caching objects via Java's built-in serialization was flagged as newly exploitable for remote code execution after a routine, unrelated dependency upgrade.
- **Task:** determine how an upgrade with no direct vulnerability of its own turned an existing caching mechanism into a critical-severity risk.
- **Action:** ruled out a caching-logic bug and a direct vulnerability in the new library, then checked the deserialization configuration directly and confirmed no allow-list filter was in place — meaning any classpath class, including the newly-introduced one, could be reconstructed from cached bytes.
- **Result:** configured a process-wide `ObjectInputFilter` allow-list as an immediate mitigation, then led a migration of the cache format away from Java's built-in serialization entirely, removing the hazard class structurally rather than managing it indefinitely.

## Staff-Level Discussion

This incident is the clearest possible illustration of why Java deserialization RCE is treated as a structural, not incidental, risk: the vulnerability was introduced by a completely unrelated, otherwise-benign dependency upgrade, with zero changes to the service's own code and zero direct vulnerability in the new library itself. The attack surface is the deserialization mechanism's own design — "reconstruct any classpath-reachable class from untrusted bytes" — not any single class's behavior, which means the set of "dangerous" classes on a given classpath can change silently with every dependency bump, indefinitely, for the life of the service. A Staff engineer evaluating this risk should recognize that an allow-list filter is a real, valid mitigation but not a permanent solution — it requires being kept correct forever, against every future dependency change, by every future engineer who touches that classpath. The organizationally sound position is to treat Java's built-in serialization as disqualified by default for any boundary touching less-than-fully-trusted bytes (a cache, a queue, any network-adjacent boundary), and to make that a standing architectural constraint enforced by tooling (dependency scanning, a lint against `ObjectInputStream` usage without a filter) rather than a fact every new hire must independently learn.

## Related Handbook Chapters

- [Serialization Hazards and Alternatives](../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md) — canonical mechanics of the constructor-bypass hazard, singleton break, and `ObjectInputFilter` defense this incident traces back to.
- [Optional and Null Strategy](../syllabus/02-java/language-core/optional-and-null-strategy.md) — related discussion of `Serializable`'s structural constraints on class design.
