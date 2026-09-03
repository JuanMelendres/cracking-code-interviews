---
title: "Cheat Sheet: Serialization Hazards and Alternatives"
slug: serialization-hazards-and-alternatives
document_type: cheat-sheet
domain: java-core
topic_id: T-115
canonical: ../handbook/java-core/serialization-hazards-and-alternatives.md
last_updated: 2026-09-02
---

# Serialization Hazards and Alternatives

**Canonical chapter:** [`syllabus/02-java/language-core/serialization-hazards-and-alternatives.md`](../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md)

## Core Mental Model

`ObjectInputStream.readObject()` is a second, completely separate way to construct an object — one that never runs the class's own constructor, and therefore never runs any validation that constructor enforces. Every hazard traces back to that one structural fact.

## Essential Definitions

- **Constructor bypass** — `readObject()` populates fields directly from bytes via `defaultReadObject()`, never calling the constructor.
- **`readResolve()`** — a private method letting a class substitute its own canonical instance for whatever `readObject()` produced, fixing Serializable singletons.
- **`ObjectInputFilter`** (JEP 290, standard since Java 9; JEP 415 in Java 17+) — an explicit allow-list rejecting disallowed classes with `InvalidClassException` before reconstruction.
- **Gadget-chain RCE** — deserializing untrusted bytes can reconstruct any classpath-reachable class; a class with dangerous side effects reachable during construction becomes exploitable.

## Decision Table

| Question | Answer |
|---|---|
| Does this boundary ever see less-trusted bytes (network, shared cache, message queue)? | Never use unrestricted Java serialization — configure `ObjectInputFilter` at minimum, prefer JSON/Protobuf |
| Does the `Serializable` class enforce an invariant in its constructor? | Re-verify explicitly in `readObject()`, or implement `readResolve()` for singletons |
| Purely trusted, internal-only, single-JVM transient state? | Java serialization may remain acceptable — still add `ObjectInputFilter` as defense-in-depth |
| Designing a new system from scratch? | Default to JSON/Protobuf — avoids the hazard class structurally |

## Common Pitfalls

- Assuming constructor validation applies during deserialization — it genuinely doesn't, verified with real byte-level tampering.
- Making a Singleton `Serializable` without `readResolve()` — silently breaks the singleton guarantee.
- Deserializing untrusted bytes with no `ObjectInputFilter` configured — real, unrestricted classpath-wide exposure.
- Treating "no known CVE in a library" as sufficient assurance — gadget-chain risk depends on the combination of unrestricted deserialization plus whatever's on the classpath, which changes with any dependency update.

## Interview Answer Skeleton

**30-sec:** `readObject()` reconstructs an object directly from bytes, bypassing the constructor entirely — verified with real byte-level stream tampering producing an object the constructor would have rejected. This breaks invariants and singletons (fixed via `readObject()` re-validation and `readResolve()`) and is the structural root of gadget-chain RCE. `ObjectInputFilter` is the JDK's real current defense; migrating to JSON/Protobuf removes the hazard entirely.

**2-min:** Add the real, byte-level tampering demo: locating and overwriting a serialized `int`'s bytes in place produced `Account{balance=-999999}` with zero reflection involved; a `SecureAccount` with re-validation in `readObject()` genuinely rejects the identical attack.

**Whiteboard:** Bytes → `readObject()` → reconstructed directly, constructor never runs → invariants silently violated. Second path: bytes → filtered `ObjectInputStream` → allow-list check → reject before reconstruction.

**Staff-level framing:** Any mechanism reconstructing program state directly from untrusted bytes, bypassing normal validated construction, is a structural RCE/injection risk regardless of technology — the same pattern recurs in unsafe pickle deserialization, YAML deserializers instantiating arbitrary types, template engines evaluating user-controlled expressions.

## Production Warning Signs

- A cache layer using Java serialization becomes an RCE vector after an unrelated library upgrade introduces a transitive "gadget" class — `ObjectInputFilter.Config.getSerialFilter()` returning `null` confirms no default filter is active.
- Fix: configure a process-wide `ObjectInputFilter` immediately; migrate the cache format to JSON/Protobuf permanently.

## Related

- `handbook/security/owasp-top-10-for-backend-services.md`
- `syllabus/02-java/language-core/optional-and-null-strategy.md`
