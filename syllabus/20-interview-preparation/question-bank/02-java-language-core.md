---
title: "Interview Question Bank — 02-java/language-core"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../02-java/language-core/INDEX.md
  - 02-java-jvm-internals.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Java Language Core

Part of the `02-java` compendium — the last of its 4 subdomain files. See
[`02-java-collections.md`](02-java-collections.md) for the tier-explanation format
and sourcing discipline.

**Honest count for this subdomain:** 22 chapters yielded 38 deep questions + 10
already-leveled Junior/Mid questions (from the two Junior Fundamentals chapters,
`java-oop-fundamentals...` and `java-syntax-fundamentals...`) + 48 quick-fire
questions = **96 real questions**.

**02-java domain total across all 4 subdomains:** 52 (collections) + 70 (concurrency)
+ 31 (jvm-internals) + 96 (language-core) = **249 real questions** — the largest of
the 22 domains this initiative covers, consistent with `02-java` being the biggest
domain in the syllabus (61 chapters).

---

## Java OOP Fundamentals — Classes, Objects, and Interfaces

Junior Fundamentals chapter — its Interview Questions already tag each by seniority.

### Q1 — What's the difference between a class and an object?

**Canonical treatment:** [§15](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md#15-interview-questions)

**What's expected:**
- **Junior:** A class is a blueprint/template; an object is a specific instance built from it via `new`, with its own copy of the class's fields. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form at these tiers.

### Q2 — When would you use an interface instead of an abstract class?

**Canonical treatment:** [§15](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Abstract class when there's real shared implementation and fields to hand down within a genuinely related family; interface when you only need to promise a capability, or when the implementing class needs to extend something else too. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q3 — Why can a Java class only extend one class but implement many interfaces?

**Canonical treatment:** [§15](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md#15-interview-questions)

**What's expected:**
- **Mid:** Multiple inheritance of actual field/method state creates unresolvable conflicts the JVM has no consistent rule for; interfaces avoid this since they historically contributed no state. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q4 — Give a concrete example of favoring composition over inheritance, and explain why.

**Canonical treatment:** [§15](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** A second independent axis of variation that would otherwise force a multiplying number of subclasses — states the actual mechanism (independent axes multiply under inheritance, add linearly under composition), not just "composition is more flexible." Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

### Q5 — You're reviewing a PR that adds a fourth level to an inheritance hierarchy. What do you look for?

**Canonical treatment:** [§15](../../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md#15-interview-questions)

**What's expected:**
- **Senior/Staff:** Whether there's a second independent axis of variation this hierarchy is being contorted to fit, plus that catching this in review is cheaper than the same mistake being re-made across a codebase. Target tier.
- **Junior/Mid:** Not typically asked in this exact form.

---

## Java Syntax Fundamentals — Variables, Control Flow, and Methods

Junior Fundamentals chapter — its Interview Questions already tag each by seniority.

### Q1 — What does `7 / 2` evaluate to in Java, and why?

**Canonical treatment:** [§15](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md#15-interview-questions)

**What's expected:**
- **Junior:** `3` — integer division truncates the remainder; producing `3.5` requires casting at least one operand to a floating-point type first. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form.

### Q2 — What's the difference between a `for` loop and a `while` loop, and when would you use each?

**Canonical treatment:** [§15](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md#15-interview-questions)

**What's expected:**
- **Junior:** `for` when the number of iterations is known in advance; `while` when it depends on a condition being met, unknown in advance. Target tier.
- **Mid/Senior/Staff:** Not typically asked in this exact form.

### Q3 — What happens if you access `array[10]` on an 8-element array?

**Canonical treatment:** [§15](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** A real `ArrayIndexOutOfBoundsException`, thrown at runtime — Java does not check array bounds at compile time. Target tier.
- **Senior/Staff:** Not typically asked in this exact form.

### Q4 — Explain the difference between `&&` and `||`, with an example of getting it backwards.

**Canonical treatment:** [§15](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md#15-interview-questions)

**What's expected:**
- **Mid:** `&&` requires both conditions true; `||` requires at least one, with a concrete example of the bug from swapping them. Target tier.
- **Junior/Senior/Staff:** Not typically asked in this exact form.

### Q5 — Why can't you resize a Java array after creating it?

**Canonical treatment:** [§15](../../02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md#15-interview-questions)

**What's expected:**
- **Mid/Senior:** An array's fixed length is part of how it's laid out in memory at creation; growing a collection dynamically requires a different structure (`ArrayList`) built on top of arrays internally. Target tier.
- **Junior/Staff:** Not typically asked in this exact form.

---

## Java Platform Basics — JVM, JDK, JRE, and Primitive Types

### Q1 — What's the difference between JVM, JDK, and JRE?

**Canonical treatment:** [§15](../../02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md#15-interview-questions)

**What's expected:**
- **Junior:** JVM executes bytecode; JDK = JVM + compiler + dev tools; JRE (historically) = JVM + standard library only, no compiler.
- **Mid:** Same, plus knows Oracle stopped distributing a standalone JRE after Java 8/9.
- **Senior:** Connects this to `jlink`/JPMS and container image size.
- **Staff:** Frames it as an organizational operability-vs-image-size trade-off, not a single correct answer.

### Q2 — What are Java's primitive types, and why does `Integer a = 127; Integer b = 127; a == b` return `true` while `128` returns `false`?

**Canonical treatment:** [§15](../../02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md#15-interview-questions)

**What's expected:**
- **Junior:** Names the eight primitives, without the caching mechanism.
- **Mid:** Knows to use `.equals()` for wrapper value comparison, even without the exact cache-range number.
- **Senior:** `Integer.valueOf` caches boxed values from -128 to 127; explains the autoboxing mechanism and states this is a real, common production bug shape.
- **Staff:** Connects it to a broader principle — comparing any wrapper/boxed type by reference instead of value is a class of bug (applies to `Long`, `Short`, `Character` too).

---

## Java Modifiers and Method Signatures

### Q1 — What are Java's access modifiers, and what does "package-private" actually mean?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/java-modifiers-and-method-signatures.md#15-interview-questions)

**What's expected:**
- **Junior:** Names `private`, `protected`, `public`, and knows package-private is a real, distinct default level.
- **Mid:** States the increasing-visibility order precisely.
- **Senior:** Connects the choice of access level to API-design discipline.
- **Staff:** Frames narrowing access later as a breaking change, widening as safe — a semantic-versioning-adjacent principle.

### Q2 — What's the difference between an abstract method and a concrete method? Can an abstract class have both?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/java-modifiers-and-method-signatures.md#15-interview-questions)

**What's expected:**
- **Junior:** An abstract method has a signature but no body; a concrete method has a real body. Knows an abstract class cannot be instantiated directly.
- **Mid:** Correctly states an abstract class can have both, with an example.
- **Senior:** Correctly distinguishes when to use an abstract class (shared state + default behavior) versus an interface (pure contract).
- **Staff:** Discusses the trade-off of introducing an abstract base class into an existing hierarchy versus retrofitting an interface with default methods.

---

## Java Version Features Timeline

### Q1 — What are the major features introduced across Java 8, 11, 17, and 21?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/java-version-features-timeline.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Names a few features per release, even with some version confusion (a very common mistake — attributing records or pattern matching to Java 8).
- **Senior:** Correctly distinguishes preview from final status for at least 2-3 features.
- **Staff:** Frames the answer around organizational JDK-upgrade strategy, not just a feature list.

### Q2 — What problem do virtual threads solve, and what do they NOT solve?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/java-version-features-timeline.md#15-interview-questions)

**What's expected:**
- **Junior:** Claims virtual threads make CPU-bound code faster — the common mistake this question targets.
- **Mid:** Knows virtual threads are lightweight and JVM-managed, not OS threads.
- **Senior:** Correctly states the I/O-bound-specific benefit and names at least one real pitfall (thread pinning inside `synchronized`).
- **Staff:** Discusses whether adopting virtual threads changes an organization's need for a separate reactive stack.

---

## Annotations and Annotation Processing

### Q1 — What happens if you forget `@Retention(RUNTIME)` on a custom annotation your framework code reads via reflection?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/annotations-and-annotation-processing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes custom annotations are visible to reflection by default — the common mistake this question targets.
- **Senior:** Without explicit `@Retention(RUNTIME)`, the annotation defaults to `CLASS` retention — present in bytecode but invisible to reflection at runtime, with no exception.
- **Staff:** Proposes a systemic fix — fail-fast checks in the reflection-scanning code, and/or a lint rule requiring explicit retention.

### Q2 — Does `@Inherited` work if the annotated type is an interface rather than a class?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/annotations-and-annotation-processing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `@Inherited` behaves like general Java inheritance — the common mistake this question targets.
- **Senior:** No — `@Inherited` only propagates a class-level annotation from a superclass to a subclass via `extends`, never through interface implementation.
- **Staff:** Connects this to the broader pattern of "inheritance" meaning genuinely different things across contexts, and the value of verifying rather than assuming each one's real scope.

---

## Classloaders and Class Initialization

### Q1 — Does merely referencing `SomeClass.class` run its static initializer?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/classloaders-and-class-initialization.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes any reference to a class triggers its static block — the common mistake this question targets.
- **Senior:** No — initialization happens at genuine active use (constructing an instance, calling a static method, reading/writing a non-constant static field), not mere referencing.
- **Staff:** Connects this to real production reasoning about when static side effects actually happen, and why that timing can surprise engineers.

### Q2 — Why might the exact same class throw `ClassCastException` against itself?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/classloaders-and-class-initialization.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes this must be a JVM bug — the common mistake this question targets.
- **Senior:** A class's real identity is (fully-qualified name, defining ClassLoader) — if loaded by two different classloaders, the JVM treats them as genuinely distinct types.
- **Staff:** Generalizes to the broader principle that isolation mechanisms can produce structurally-identical-but-incompatible objects across their boundaries.

---

## Enums, EnumMap, and EnumSet

### Q1 — Why is persisting `Enum.ordinal()` dangerous?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/enums-enummap-and-enumset.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `ordinal()` can change if the enum is edited, even without the precise silent-corruption mechanism.
- **Senior:** `ordinal()` reflects declaration order, which silently shifts whenever a new constant is inserted or reordered — a persisted value resolves to a wrong constant with zero exception.
- **Staff:** Generalizes to the broader principle that position-derived values are fragile to reordering, applicable well beyond this feature.

### Q2 — Can reflection be used to create a second instance of an enum constant?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/enums-enummap-and-enumset.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes enums are just a convention-based singleton — the common mistake this question targets.
- **Senior:** No — a real, dedicated `IllegalArgumentException` is thrown, a JVM-level guard, unlike a hand-written singleton which reflection can defeat.
- **Staff:** Connects this directly to Joshua Bloch's recommendation to use a single-constant enum as the preferred Singleton implementation.

---

## `equals()`, `hashCode()`, and `Comparable` Contracts

### Q1 — Your `HashSet` isn't deduplicating records that look identical. What do you check first?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md#interview-questions)

**What's expected:**
- **Junior:** Assumes the issue is in the deduplication logic itself — the common mistake this question targets.
- **Mid:** Suggests checking `equals()`/`hashCode()`, even without full reasoning.
- **Senior:** Checks whether `equals()` and `hashCode()` are both overridden and consistent with each other.
- **Staff:** Proposes an automated contract test to catch this class of bug going forward.

### Q2 — Why would a `TreeSet` silently drop an element that isn't actually a duplicate?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `TreeSet` also consults `equals()` as a fallback, the way `HashSet` does — the common mistake this question targets.
- **Senior:** `TreeSet`/`TreeMap` use `compareTo() == 0` as their sole notion of "the same element," ignoring `equals()` entirely.
- **Staff:** Explains why this is legal Java (the `Comparable` contract only recommends, not requires, consistency with `equals()`) and states the design rule.

---

## Exception Design and Hierarchy Strategy

### Q1 — Your on-call alert shows a generic exception with no detail. What's the first thing you check?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/exception-design-and-hierarchy-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Suggests checking for a missing cause, even without full reasoning.
- **Senior:** Checks whether the exception's construction site passed the caught exception as the cause — a missing cause is the classic signature of this bug.
- **Staff:** Proposes a static-analysis or code-review rule to catch this class of bug systematically.

### Q2 — What happens if both the try block and `close()` throw, with and without try-with-resources?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/exception-design-and-hierarchy-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that try-with-resources preserves both exceptions somehow.
- **Senior:** With try-with-resources, the body's exception propagates as primary and `close()`'s is attached via `addSuppressed()`; without it, the `finally` block's exception silently replaces the original.
- **Staff:** Explains precisely why the manual `finally` case is strictly worse and connects this to why try-with-resources was introduced.

---

## Generics, Erasure, and PECS

### Q1 — Why does the `ClassCastException` show up at `get()` instead of at the unchecked cast itself?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/generics-erasure-and-pecs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the JVM does some generic type checking at runtime — the common mistake this question targets.
- **Senior:** Generics checks are compile-time only; once an unchecked cast bypasses that check, the failure only occurs later, at read time.
- **Staff:** Generalizes to the principle that any bypass of the type system can let a bug travel arbitrarily far from its actual cause before surfacing.

### Q2 — Why can't you write to a `List<? extends Number>`?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/generics-erasure-and-pecs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `? extends T` is "read-only" without the underlying reasoning.
- **Senior:** The compiler cannot prove what specific subtype the wildcard actually represents at runtime, so it rejects the write to prevent an unverifiable type-safety violation.
- **Staff:** Contrasts this with `? super T`, explaining why writes are safe there but reads only reliably yield `Object`.

---

## Immutability and Defensive Copying

### Q1 — Your class has only final fields and no setters. Is it immutable? How would you check?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/immutability-and-defensive-copying.md#interview-questions)

**What's expected:**
- **Junior:** Assumes final-fields-only is sufficient — the common mistake this question targets.
- **Mid:** States that final fields aren't sufficient, even without the precise two-boundary check.
- **Senior:** Checks every constructor for storing a mutable-typed argument directly, and every getter for returning a live reference to a mutable field.
- **Staff:** Proposes defensive copying (or immutable views) at both boundaries as the systematic fix, connecting it to thread-safety without synchronization.

### Q2 — What's the difference between defensively copying into a new `ArrayList` versus using `List.copyOf()`?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/immutability-and-defensive-copying.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the two approaches as equivalent — the common mistake this question targets.
- **Senior:** A new `ArrayList` copy is independent but still mutable; `List.copyOf()` produces an unmodifiable view that throws on any mutation attempt.
- **Staff:** Connects this to the general principle that a structural guarantee is stronger and more debuggable than a convention.

---

## Java Platform Module System (JPMS)

### Q1 — A dependency broke after a Java 17 upgrade with `InaccessibleObjectException`. Walk me through diagnosis and fix.

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/java-platform-module-system.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the fix is reverting the JDK upgrade — the common mistake this question targets.
- **Senior:** Runs `jdeps --jdk-internals` to identify what the dependency reflects into; applies the narrowest `--add-opens` as immediate mitigation; upgrades the dependency permanently.
- **Staff:** Frames `--add-opens` flags as trackable technical debt and connects the root cause to JEP 396/403's two-step enforcement change.

### Q2 — How does `ServiceLoader` find an implementation in a package never exported or opened — doesn't that break encapsulation?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/java-platform-module-system.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `ServiceLoader` access implies the package is effectively exported for any purpose — the common mistake this question targets.
- **Senior:** Module resolution adds a provider module to the graph when `uses`/`provides` match, independent of `requires` — a deliberate, narrow exception scoped to `ServiceLoader`'s path.
- **Staff:** Connects this to the general design principle that JPMS's grants are narrowly scoped and additive by default.

---

## Java Time API

### Q1 — What's the practical difference between `Period` and `Duration`, and why does it matter?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/java-time-api.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that one is "calendar-based" and one is "time-based," without a concrete example.
- **Senior:** Gives the DST transition as a concrete example where the two produce genuinely different, both-correct results.
- **Staff:** Connects this to a real production failure mode (a calendar-based business concept implemented with the wrong, fixed-time type).

### Q2 — Why is `SimpleDateFormat` not thread-safe, and what would you use instead?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/java-time-api.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the issue only produces exceptions — the common mistake this question targets.
- **Senior:** `SimpleDateFormat` holds mutable internal state its `parse()`/`format()` read and write without synchronization; `DateTimeFormatter` is immutable and safe to share.
- **Staff:** Connects this to the broader "legacy mutable types shared across threads" pattern and proposes it as a standing code-review flag.

---

## Lambdas and Functional Interfaces

### Q1 — Why can't a lambda capture a mutable local variable?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/lambdas-and-functional-interfaces.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that captured locals must be effectively final, even without the underlying value-capture reasoning.
- **Senior:** A lambda captures a local variable's value at creation time, not a live reference; the JLS requires effectively-final locals so the captured copy can never silently diverge.
- **Staff:** Contrasts this with instance/static field capture (no restriction) and connects it to why the restriction is specifically about local variables.

### Q2 — What actually happens the first time a lambda expression executes?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/lambdas-and-functional-interfaces.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Knows lambdas involve `invokedynamic`, even without the full metafactory mechanism.
- **Senior:** The bootstrap method `LambdaMetafactory.metafactory` runs once and generates the implementation class; the resulting `CallSite` is cached for subsequent calls.
- **Staff:** Connects this to the broader "defer class generation to runtime" pattern used elsewhere in the JVM.

---

## `Optional` and Null Strategy

### Q1 — What's the actual difference between `orElse()` and `orElseGet()`?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/optional-and-null-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that `orElseGet` takes a `Supplier` and is "lazier," even without the precise mechanism.
- **Senior:** `orElse(x)` evaluates `x` eagerly, unconditionally; `orElseGet(supplier)` only invokes the supplier lazily when empty — a real, measurable difference for non-trivial fallbacks.
- **Staff:** Generalizes to the broader eager-vs-lazy API design pattern recurring elsewhere in the JDK.

### Q2 — Why shouldn't you use `Optional` as a field type?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/optional-and-null-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that it's "not recommended," even without a concrete consequence.
- **Senior:** `Optional` doesn't implement `Serializable`, so a class storing one as a field genuinely cannot be serialized via standard Java serialization.
- **Staff:** Generalizes to the broader principle that a narrowly-designed type accumulates real structural costs when used outside its intended role.

---

## Polymorphism and Dynamic Dispatch

### Q1 — A subclass redeclares a field with the same name as a superclass field. What happens reading it through each reference type?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/polymorphism-and-dynamic-dispatch.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the subclass's field "overrides" the superclass's, the way a method would — the common mistake this question targets.
- **Senior:** Both fields coexist; field access is resolved by the reference's declared type at compile time, not the object's runtime type.
- **Staff:** Connects this to the underlying `getfield` vs. `invokevirtual` bytecode distinction.

### Q2 — A superclass constructor calls a method that a subclass overrides. What could go wrong?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/polymorphism-and-dynamic-dispatch.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Doesn't recognize this as a real, recurring bug pattern.
- **Senior:** The override runs during the superclass constructor's execution, before subclass field initializers have run — the override can observe subclass fields at their default value.
- **Staff:** Proposes a concrete fix and states why marking the method `final` is only a partial fix.

---

## Records, Sealed Types, and Pattern Matching

### Q1 — What's the actual difference between a record and a normal class with the same fields and generated `equals`/`hashCode`/`toString`?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/records-sealed-types-and-pattern-matching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Says they're functionally identical — the common mistake this question targets.
- **Senior:** States the accessor naming (`x()`, not `getX()`) and single-inheritance-spent points unprompted.
- **Staff:** Connects to when NOT to use a record (needs mutability, identity semantics, or lazy fields) and what the team gives up architecturally.

### Q2 — Why does an exhaustive switch over a sealed type not need a `default` branch, and what happens if you add one anyway?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/records-sealed-types-and-pattern-matching.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Believes `default` is always required by the language — the common mistake this question targets.
- **Senior:** The compiler can enumerate the sealed type's `permits` list and verify every value is covered; adding `default` still compiles but means a future new subtype silently falls into `default`.
- **Staff:** Discusses the `default`-as-safety-net trade-off explicitly as a design decision about future extensibility versus current safety.

---

## Reflection and Dynamic Proxies

### Q1 — How much slower is reflection than a direct call, roughly?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/reflection-and-dynamic-proxies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats reflection as either always negligible or always prohibitively slow — the common mistake this question targets.
- **Senior:** Real, measured order-of-magnitude slower for classic `Method.invoke()` (~18.7x measured), meaningfully improved by `MethodHandle`.
- **Staff:** Frames the decision as workload-dependent — significant on a hot path, negligible for one-time framework bootstrapping.

### Q2 — Why might `@Transactional` silently not apply to a method call?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/reflection-and-dynamic-proxies.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `@Transactional` failures are always a configuration problem — the common mistake this question targets.
- **Senior:** If called via self-invocation (`this.method()`), the call never passes through the bean's proxy, so the transactional behavior is silently never applied.
- **Staff:** Generalizes to the broader principle that any wrapper/interception-layer feature only intercepts calls that cross the wrapper's boundary.

---

## Serialization Hazards and Alternatives

### Q1 — Does the constructor run during deserialization?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/serialization-hazards-and-alternatives.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes Java "must" run the constructor somehow — the common mistake this question targets.
- **Senior:** No — `ObjectInputStream.readObject()` reconstructs state directly from the byte stream, never calling the constructor; any invariant enforced only there is genuinely absent.
- **Staff:** Connects this to the broader gadget-chain RCE risk class and the general principle of untrusted-bytes-driving-construction being dangerous.

### Q2 — Why is Java's built-in serialization considered a real security risk, beyond constructor bypass?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/serialization-hazards-and-alternatives.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as purely academic — the common mistake this question targets.
- **Senior:** Deserialization can reconstruct any class reachable on the classpath from untrusted bytes; a chain of dangerous side effects becomes arbitrary code execution — a real, historically exploited class.
- **Staff:** Generalizes to the broader "untrusted bytes driving arbitrary construction" pattern across serialization technologies and languages.

---

## Streams and Collectors

### Q1 — Your `parallel()` change made things slower. Why might that happen?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/streams-and-collectors.md#interview-questions)

**What's expected:**
- **Junior:** Assumes `parallel()` always improves performance — the common mistake this question targets.
- **Mid:** States that parallel streams have overhead, even without the fork/join specifics.
- **Senior:** Fork/join task-splitting and thread-handoff overhead can exceed the savings for small collections or cheap per-element work.
- **Staff:** Names the shared-common-pool contention risk across unrelated concurrent parallel streams in the same process.

### Q2 — How would you accumulate results safely from a parallel stream instead of a shared list?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/streams-and-collectors.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Reaches for a manually-synchronized shared list — the common mistake this question targets.
- **Senior:** Uses a proper `Collector` (`Collectors.toList()`, `toMap()` with a merge function, or a custom `Collector.of(...)`).
- **Staff:** Explains why: collectors have a combiner step specifically designed to merge per-thread partial results correctly.

---

## Strings — Interning, Compact Strings, and Builders

### Q1 — Why is `String +=` in a loop considered an anti-pattern?

**Canonical treatment:** [§ Interview Questions, Q1](../../02-java/language-core/strings-interning-compact-strings-and-builders.md#interview-questions)

**What's expected:**
- **Junior:** "It's just slower," with no mechanism.
- **Mid:** States that `+=` in a loop is "slow" or "creates lots of objects," without the precise quadratic mechanism.
- **Senior:** `String` is immutable, so `+=` allocates and copies on every iteration — genuinely quadratic total cost, measured directly as 63-147x slower than `StringBuilder`.
- **Staff:** Connects this to the broader principle of immutable-type "mutation" always allocating, and when that's acceptable versus when it compounds badly.

### Q2 — Does adding a single accented character to an otherwise-English string affect its real memory footprint?

**Canonical treatment:** [§ Interview Questions, Q2](../../02-java/language-core/strings-interning-compact-strings-and-builders.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the memory cost scales smoothly with the number of "wide" characters — the common mistake this question targets.
- **Senior:** Yes, genuinely — a Latin-1-representable string uses one byte per character; one non-Latin-1 character switches the *entire* string to two bytes per character.
- **Staff:** Generalizes to the broader pattern of content-dependent optimization thresholds and their fragility to real-world data variation.

---

## Quick-fire questions (from this subdomain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | What retention policy does an annotation get if you don't specify `@Retention` at all? | [Annotations](../../02-java/language-core/annotations-and-annotation-processing.md#flashcards) |
| 2 | What's the actual mechanism behind JPA's `@Column` or Jackson's `@JsonProperty`? | [Annotations](../../02-java/language-core/annotations-and-annotation-processing.md#flashcards) |
| 3 | Does `@Inherited` propagate an annotation from an interface to an implementing class? | [Annotations](../../02-java/language-core/annotations-and-annotation-processing.md#flashcards) |
| 4 | What determines a class's real identity in the JVM — just its fully-qualified name? | [Classloaders](../../02-java/language-core/classloaders-and-class-initialization.md#flashcards) |
| 5 | Does referencing `SomeClass.class` trigger its static initializer? | [Classloaders](../../02-java/language-core/classloaders-and-class-initialization.md#flashcards) |
| 6 | You see "class X cannot be cast to class X" — what's your first diagnostic step? | [Classloaders](../../02-java/language-core/classloaders-and-class-initialization.md#flashcards) |
| 7 | Can reflection create a second instance of an enum constant? | [Enums, EnumMap, EnumSet](../../02-java/language-core/enums-enummap-and-enumset.md#flashcards) |
| 8 | What happens if you persist `Enum.ordinal()` and later insert a new constant mid-declaration? | [Enums, EnumMap, EnumSet](../../02-java/language-core/enums-enummap-and-enumset.md#flashcards) |
| 9 | Is `EnumMap` dramatically faster than `HashMap` for enum keys? | [Enums, EnumMap, EnumSet](../../02-java/language-core/enums-enummap-and-enumset.md#flashcards) |
| 10 | What is the equals/hashCode contract, precisely? | [equals/hashCode/Comparable](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md#flashcards) |
| 11 | What happens if you override `equals()` but not `hashCode()`? | [equals/hashCode/Comparable](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md#flashcards) |
| 12 | What does `TreeSet` use to decide two elements are "the same"? | [equals/hashCode/Comparable](../../02-java/language-core/equals-hashcode-and-comparable-contracts.md#flashcards) |
| 13 | What does chaining the cause when wrapping an exception actually preserve? | [Exception Design](../../02-java/language-core/exception-design-and-hierarchy-strategy.md#flashcards) |
| 14 | What happens when both a try-with-resources body and `close()` throw? | [Exception Design](../../02-java/language-core/exception-design-and-hierarchy-strategy.md#flashcards) |
| 15 | Why is a manual `finally`-block `close()` that also throws strictly worse than try-with-resources? | [Exception Design](../../02-java/language-core/exception-design-and-hierarchy-strategy.md#flashcards) |
| 16 | What does type erasure actually remove, and when? | [Generics, Erasure, PECS](../../02-java/language-core/generics-erasure-and-pecs.md#flashcards) |
| 17 | When does a defeated generic (via unchecked cast) actually fail? | [Generics, Erasure, PECS](../../02-java/language-core/generics-erasure-and-pecs.md#flashcards) |
| 18 | State PECS. | [Generics, Erasure, PECS](../../02-java/language-core/generics-erasure-and-pecs.md#flashcards) |
| 19 | Do `final` fields alone make a class immutable? | [Immutability](../../02-java/language-core/immutability-and-defensive-copying.md#flashcards) |
| 20 | What are the two places a supposedly-immutable class can leak mutability? | [Immutability](../../02-java/language-core/immutability-and-defensive-copying.md#flashcards) |
| 21 | Why is `List.copyOf()` stronger than copying into a new `ArrayList`? | [Immutability](../../02-java/language-core/immutability-and-defensive-copying.md#flashcards) |
| 22 | What's the real difference between `exports` and `opens`, and does either imply the other? | [JPMS](../../02-java/language-core/java-platform-module-system.md#flashcards) |
| 23 | Does a module using `ServiceLoader.load()` need a `requires` edge to the provider module? | [JPMS](../../02-java/language-core/java-platform-module-system.md#flashcards) |
| 24 | Why did Java 9-era modularization not break most apps immediately, but a Java 16+ upgrade often does? | [JPMS](../../02-java/language-core/java-platform-module-system.md#flashcards) |
| 25 | What's the real, measurable difference between `Period.ofDays(1)` and `Duration.ofDays(1)`? | [Java Time API](../../02-java/language-core/java-time-api.md#flashcards) |
| 26 | Is `SimpleDateFormat` safe to share as a single cached instance across threads? | [Java Time API](../../02-java/language-core/java-time-api.md#flashcards) |
| 27 | What's the difference between `LocalDateTime` and `ZonedDateTime`, given the same date and time? | [Java Time API](../../02-java/language-core/java-time-api.md#flashcards) |
| 28 | Why must a local variable captured by a lambda be effectively final? | [Lambdas](../../02-java/language-core/lambdas-and-functional-interfaces.md#flashcards) |
| 29 | Does compiling a lambda produce an extra `.class` file, like an anonymous class does? | [Lambdas](../../02-java/language-core/lambdas-and-functional-interfaces.md#flashcards) |
| 30 | Do `default` and `static` interface methods count toward the single-abstract-method requirement? | [Lambdas](../../02-java/language-core/lambdas-and-functional-interfaces.md#flashcards) |
| 31 | Is `orElse(expensiveCall())` evaluated only when the `Optional` is empty? | [Optional and Null Strategy](../../02-java/language-core/optional-and-null-strategy.md#flashcards) |
| 32 | What real, concrete problem does storing `Optional` as a field cause, beyond style? | [Optional and Null Strategy](../../02-java/language-core/optional-and-null-strategy.md#flashcards) |
| 33 | What happens if you call `Optional.of(null)`? | [Optional and Null Strategy](../../02-java/language-core/optional-and-null-strategy.md#flashcards) |
| 34 | Which kinds of member access are dynamically dispatched in Java? | [Polymorphism](../../02-java/language-core/polymorphism-and-dynamic-dispatch.md#flashcards) |
| 35 | If a subclass declares a field with the same name as a superclass field, does it override the field? | [Polymorphism](../../02-java/language-core/polymorphism-and-dynamic-dispatch.md#flashcards) |
| 36 | What can go wrong if a superclass constructor calls a method the subclass overrides? | [Polymorphism](../../02-java/language-core/polymorphism-and-dynamic-dispatch.md#flashcards) |
| 37 | Roughly how much slower is classic `Method.invoke()` than a direct method call? | [Reflection and Dynamic Proxies](../../02-java/language-core/reflection-and-dynamic-proxies.md#flashcards) |
| 38 | Can `java.lang.reflect.Proxy` create a proxy for a concrete class? | [Reflection and Dynamic Proxies](../../02-java/language-core/reflection-and-dynamic-proxies.md#flashcards) |
| 39 | Why might a Spring `@Transactional` method silently not get its transaction applied? | [Reflection and Dynamic Proxies](../../02-java/language-core/reflection-and-dynamic-proxies.md#flashcards) |
| 40 | Does `ObjectInputStream.readObject()` call the class's constructor? | [Serialization Hazards](../../02-java/language-core/serialization-hazards-and-alternatives.md#flashcards) |
| 41 | How do you keep a Singleton's `==` identity intact across serialization? | [Serialization Hazards](../../02-java/language-core/serialization-hazards-and-alternatives.md#flashcards) |
| 42 | What's the JDK's own current, standard mechanism for restricting what a deserialization stream can reconstruct? | [Serialization Hazards](../../02-java/language-core/serialization-hazards-and-alternatives.md#flashcards) |
| 43 | When does a stream pipeline actually execute? | [Streams and Collectors](../../02-java/language-core/streams-and-collectors.md#flashcards) |
| 44 | Why does `Collectors.toMap()` throw on duplicate keys by default? | [Streams and Collectors](../../02-java/language-core/streams-and-collectors.md#flashcards) |
| 45 | Does `parallel()` make a stream's writes to shared state thread-safe? | [Streams and Collectors](../../02-java/language-core/streams-and-collectors.md#flashcards) |
| 46 | Does `new String("hello") == "hello"` evaluate to `true`? | [Strings, Interning, Compact Strings](../../02-java/language-core/strings-interning-compact-strings-and-builders.md#flashcards) |
| 47 | If a mostly-English string has ONE non-Latin-1 character, does only that character cost extra memory? | [Strings, Interning, Compact Strings](../../02-java/language-core/strings-interning-compact-strings-and-builders.md#flashcards) |
| 48 | How much slower is `String +=` in a loop than `StringBuilder.append()`, roughly? | [Strings, Interning, Compact Strings](../../02-java/language-core/strings-interning-compact-strings-and-builders.md#flashcards) |

---

## Related

- [`02-java` question bank — collections`](02-java-collections.md)
- [`02-java` question bank — concurrency`](02-java-concurrency.md)
- [`02-java` question bank — jvm-internals`](02-java-jvm-internals.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
