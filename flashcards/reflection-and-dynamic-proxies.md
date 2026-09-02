---
title: "Flashcards: Reflection and Dynamic Proxies"
slug: reflection-and-dynamic-proxies
document_type: flashcard-deck
domain: java-core
topic_id: T-113
canonical: ../handbook/java-core/reflection-and-dynamic-proxies.md
last_updated: 2026-09-02
---

# Flashcards: Reflection and Dynamic Proxies

**Canonical chapter:** [`handbook/java-core/reflection-and-dynamic-proxies.md`](../handbook/java-core/reflection-and-dynamic-proxies.md)

## Card: Reflection's real cost

**Prompt:**
Roughly how much slower is classic `Method.invoke()` than a direct method call?

**Answer:**
Real, measured ~18.7x slower across 200 million calls in this chapter's own benchmark, with `MethodHandle` measuring ~2.5-2.6x faster than classic reflection for the same operation.

**Why it matters:**
Turns "reflection is slow" from a vague claim into a defensible, measured number.

**Common trap:**
Treating reflection's cost as either negligible or prohibitive without measuring the actual hot path.

**Related:**
[Internal Implementation](../handbook/java-core/reflection-and-dynamic-proxies.md#internal-implementation)

## Card: The interface-only constraint

**Prompt:**
Can `java.lang.reflect.Proxy` create a proxy for a concrete class?

**Answer:**
No — verified directly via a real `IllegalArgumentException`. It can only proxy interfaces, which is the real reason Spring falls back to CGLIB/ByteBuddy for interface-less beans.

**Why it matters:**
A real, structural constraint, not a minor limitation to work around.

**Common trap:**
Assuming any object can be proxied with a plain JDK dynamic proxy.

**Related:**
[Internal Implementation](../handbook/java-core/reflection-and-dynamic-proxies.md#internal-implementation)

## Card: Self-invocation defeats the proxy

**Prompt:**
Why might a Spring `@Transactional` method silently not get its transaction applied?

**Answer:**
If called via `this.method()` from within the same bean, the call never crosses the proxy boundary — the proxy only intercepts external calls.

**Why it matters:**
A real, common, silent production bug directly traceable to the proxy mechanism.

**Common trap:**
Assuming `@Transactional` "just works" regardless of how the method is called.

**Related:**
[Production Scenarios](../handbook/java-core/reflection-and-dynamic-proxies.md#production-scenarios)
