---
title: "Java Modifiers and Method Signatures: Access Control, static, final, and Abstract vs. Concrete Methods"
slug: java-modifiers-and-method-signatures
document_type: syllabus-topic
domain: 02-java
topic_id: T-2210
status: draft
version: 1.3
last_updated: 2026-09-30
mastery_levels_covered: [L1, L2, L3]
prerequisites:
  - java-platform-basics-jvm-jdk-jre-and-primitive-types.md
  - java-syntax-fundamentals-variables-control-flow-and-methods.md
related:
  - java-oop-fundamentals-classes-objects-and-interfaces.md
  - polymorphism-and-dynamic-dispatch.md
  - generics-erasure-and-pecs.md
  - nested-and-inner-classes.md
practice: ../../../practice/java/oop-fundamentals/modifiers-and-methods/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html
  - https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.4.3
---

# Java Modifiers and Method Signatures: Access Control, static, final, and Abstract vs. Concrete Methods

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

Access modifiers, `static`, `final`, and the abstract-vs-concrete method distinction are used in nearly every class this repository has ever shown — and, per a 2026-09-08 audit, never actually explained from zero anywhere in `02-java`. `private`/`protected`/`public` appears incidentally inside [Reflection and Dynamic Proxies](reflection-and-dynamic-proxies.md) (a Senior/Staff-depth chapter that assumes you already know the four-level access table), and `final` appears incidentally inside [Immutability and Defensive Copying](immutability-and-defensive-copying.md) and [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) — always as a tool already assumed known, never as its own floor topic. These are also among the most commonly asked Junior-level Java interview questions, precisely because they are easy to use correctly by imitation and hard to explain precisely without having studied them directly.

## 2. Prerequisites

[Java Platform Basics](java-platform-basics-jvm-jdk-jre-and-primitive-types.md) and [Java Syntax Fundamentals](java-syntax-fundamentals-variables-control-flow-and-methods.md) — this chapter assumes you can already read a class definition and a method.

## 3. Foundation (L1)

**Java has four access levels**, from most to least restrictive:

| Modifier | Same class | Same package | Subclass (different package) | Everywhere |
|---|---|---|---|---|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(no modifier — "package-private", commonly called* **Default (no keyword)** *)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

There is no keyword for "package-private" — it is simply what you get by writing no access modifier at all. This is the real default, and it is a deliberate design choice, not an oversight: a field or method with no modifier is visible throughout its own package but nowhere else, useful for internal collaboration between classes that live together without exposing that collaboration outside the package.

**`static`** marks a field or method as belonging to the *class itself*, not to any one instance — every instance shares the exact same static field, and a static method can be called without creating an object at all (`Math.max(3, 5)`, not `new Math().max(3, 5)`).

**`final`** means "cannot be changed further," applied to three different things with three different effects: a `final` variable can be assigned exactly once and never reassigned; a `final` method cannot be overridden by a subclass; a `final` class cannot be extended at all.

**`abstract`** marks a method with a signature but deliberately *no body* — no braces, just a semicolon after the parameter list (`abstract double area();`) — which forces every concrete (non-abstract) subclass to supply its own implementation. A **concrete method**, by contrast, has a real body and is immediately callable and inheritable as-is.

## 4. Core Concepts (L2)

**`private` is scoped to the top-level enclosing class, not the immediate class body** — a genuinely surprising rule the first time you encounter it: two *nested* classes inside the same outer class *can* read each other's `private` members directly, because the JLS's actual rule is "accessible anywhere within the body of the top-level class that encloses the declaration," not "accessible only within this exact class." This chapter's own practice code discovered this by accident while building a private-access demo (Section 7) — worth internalizing precisely because it contradicts the simplified mental model most people carry.

**A class can only be `abstract` if it has at least one unimplemented method, OR if it is deliberately declared abstract to prevent direct instantiation even though every method has a body** — both are valid; the first is by far the more common shape. An abstract class can still have real fields, real constructors (called only via `super()` from a subclass, since it can never be instantiated directly), and real concrete methods alongside its abstract ones.

**`static` and `final` combine to make a true constant**: `static final int MAX_RETRIES = 3;` is shared across every instance (static) and can never change after class initialization (final) — this is Java's actual mechanism for what other languages might call a named constant, and it is why interface fields (which are implicitly `public static final`, per [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md)'s own Section 8) behave the way they do.

**Overloading vs. overriding**, since both terms describe a subclass or class having "another version" of a method with the same name, and the two are routinely confused:

- **Overloading** is having *multiple methods with the same name but different parameter lists* in the *same class* (or a subclass adding a new signature). Which overload runs is decided by the compiler, once, from the argument types written at the call site.
- **Overriding** is a *subclass redefining an inherited instance method with the identical signature* (same name, same parameter types). Which override runs is decided by the JVM, at runtime, from the actual object's class — this is what makes polymorphism work.

| Aspect | Overloading | Overriding |
|---|---|---|
| Method signature | Must differ (parameter types/count/order) | Must be identical to the parent's |
| Where it happens | Same class, or a subclass adding a new variant | A subclass redefining an inherited method |
| Resolved by | Compiler, from the declared argument types (static/compile-time binding) | JVM, from the object's actual runtime class (dynamic/virtual dispatch) |
| Return type | Can differ freely | Must be the same, or a covariant subtype |
| Access modifier | No constraint relative to other overloads | Cannot be more restrictive than the overridden method |
| `private`/`static`/`final` methods | Can be overloaded normally | Cannot be overridden at all — see Section 9 (a subclass "redefining" one hides it instead) |
| Annotation | None (there's no `@Overload`) | `@Override` — always use it; it makes `javac` verify a real match exists |

**Varargs (`String... parts`) is a parameter, an array, and an overload-resolution tiebreaker all at once.** Writing `...` after a parameter's type lets a caller pass any number of arguments of that type, including none. At runtime the method receives a plain array — real, executed output from Section 7's demo: `joinAll("-", "a", "b", "c")` sees `String[] of length 3`, and `joinAll("-")` sees `String[] of length 0`, never `null`. Three rules follow, and all three are asked about:

- **A varargs parameter must be last**, and a method can have only one, because otherwise the compiler could not tell where one variable-length list ended and the next began.
- **An array can be passed directly** where varargs is expected, since that is what the parameter already is. This is where the surprises live (below).
- **Varargs loses every overload contest it can lose.** Java resolves an overloaded call in three phases, stopping at the first phase that finds an applicable method: phase 1 considers only subtyping and widening primitive conversion, phase 2 additionally allows boxing/unboxing, and phase 3 — only if the first two found nothing — allows varargs. Real, executed evidence from the same demo: with `pick(long)`, `pick(Integer)`, and `pick(int...)` all in scope, `pick(42)` selects `pick(long)`; remove the `long` overload and `only(42)` selects `only(Integer)`, not the varargs method. Varargs runs last, which is why adding a varargs overload to an existing API rarely changes what existing calls resolve to — and why a call that *does* fall through to varargs is often a sign the intended overload does not actually apply.

**Passing an array to a varargs parameter behaves differently for reference and primitive arrays**, which produces one of Java's most-cited real bugs. Because `String[]` *is* an `Object[]`, `describe(names)` against `describe(Object... items)` spreads the array into three separate arguments. Because `int[]` is *not* an `Object[]` (primitive arrays do not participate in that covariance), the identical-looking `describe(numbers)` passes the whole array as a single element. Measured directly:

```text
Arrays.asList(numbers).size() = 1  <- the classic bug this causes
Arrays.asList(names).size()   = 3
```

`Arrays.asList(int[])` returning a one-element `List<int[]>` is not a quirk of `Arrays`; it is this rule. Casting to `(Object)` forces the single-element reading deliberately when that is what you want.

**A covariant return type is a legal override that narrows the return.** Since Java 5, an override may declare a return type that is a subtype of the overridden method's: `Dog reproduce()` legally overrides `Animal reproduce()`. Real output: calling it through an `Animal` reference returns a `Dog`, and callers holding a `Dog` reference need no cast at all. This is what lets `clone()` in a well-written class return the concrete type rather than `Object` — see [Immutability and Defensive Copying](immutability-and-defensive-copying.md). Narrowing is allowed; widening is not, because existing callers assigned the old, narrower type.

**A `static` method in a subclass with the same signature hides rather than overrides.** The table above notes that `static` methods cannot be overridden; the consequence is that resolution is by the *compile-time* type. Real, executed contrast from the same demo, with `Base viewedAsBase = new Derived()`:

```text
Instance method through a Base reference: Derived.instanceGreet()   <- dynamic dispatch, runtime type wins
Static method resolved through Base:      Base.staticGreet()        <- static binding, compile-time type wins
```

`@Override` on a static method is a real compile error — `static methods cannot be annotated with @Override` — captured verbatim in the practice directory's transcript. The bytecode-level reason (`invokestatic` versus `invokevirtual`) belongs to [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md).

This chapter stops at the signature-and-modifier rules above; the actual dispatch mechanism — *why* overload resolution is a compile-time decision and override resolution is a runtime one, down to the `invokestatic` vs. `invokevirtual` bytecode difference — is [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md)'s own job, including the field-hiding and static-hiding gotchas that follow directly from this same static-vs-dynamic distinction.

## 5. How It Works Internally (L3)

Access control is enforced entirely at **compile time** by `javac` — there is no runtime access check for a normal field/method access (reflection can bypass it deliberately via `setAccessible(true)`, which is exactly why [Reflection and Dynamic Proxies](reflection-and-dynamic-proxies.md) treats that call as a real security-relevant decision, not a routine one). A `private` field access from outside its permitted scope is a real compilation failure, not a runtime exception — demonstrated concretely in Section 7.

`static` fields live in the class's own storage, allocated once when the class is loaded by the JVM's classloader, before any instance of that class is ever created — this is why `Counter.totalCreated` in Section 7's demo already has a defined value (`0`) even before the first `new Counter()` call. `final` local variables and fields are enforced by the compiler's definite-assignment analysis: it tracks, at compile time, every possible code path to guarantee a `final` variable is assigned exactly once before any read, which is also what makes a `final` local variable safely capturable by a lambda or anonymous inner class (the compiler can prove it will never change).

**Varargs is compile-time sugar over an array, and generics make that leaky.** javac rewrites `joinAll("-", "a", "b")` into `joinAll("-", new String[]{"a", "b"})`; the method's real descriptor takes an array. For a *generic* varargs parameter (`<T> T... items`) the compiler cannot create an array of the erased type parameter, so it creates an array of the erasure bound — usually `Object[]` — and warns about **heap pollution**: a variable whose declared type says one thing while the object it points at is genuinely something else. Measured directly in Section 7's demo, a `List<String>[]` returned from a generic varargs method reports its runtime type as `List[]`, an `Integer`-bearing list is stored into it through an `Object[]` alias with no `ArrayStoreException`, and the failure surfaces later as:

```text
Reading polluted[0].get(0) as String threw: ClassCastException
  message: class java.lang.Integer cannot be cast to class java.lang.String
```

on a line of source that contains no visible cast — the cast was inserted by the compiler at the read. `@SafeVarargs` suppresses the warning and is a promise, not a check: it is correct only when the method merely *reads* the array and never stores it, exposes it, or writes to it. Generics erasure itself is [Generics, Erasure, and PECS](generics-erasure-and-pecs.md)'s topic.

## 6. Practical Usage

Default to `private` for fields — expose behavior through methods, not raw field access, per the encapsulation principle [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md) already covers. Use package-private (no modifier) when two or three classes in the same package need to collaborate without exposing that collaboration to the rest of the codebase. Reach for `protected` specifically when you're designing a class *meant* to be subclassed and want subclasses (even in other packages) to have direct access to something. Mark a field `final` by default when it's set once at construction and never changes — this documents your intent and lets the compiler catch an accidental reassignment. Mark a class `final` when you have a specific reason to prevent subclassing (many immutable value classes do this deliberately, `String` among them).

## 7. Examples

Every claim below is verified against real, compiled, executed code at [`practice/java/oop-fundamentals/modifiers-and-methods/`](../../../practice/java/oop-fundamentals/modifiers-and-methods/), run on OpenJDK 21.0.12.

```java
// Real, measured: static state is SHARED, instance state is NOT.
static class Counter {
    static int totalCreated = 0; // one copy, shared by every Counter
    int instanceId;              // a separate copy per Counter

    Counter() {
        totalCreated++;
        instanceId = totalCreated;
    }
}
// After creating 3 Counters: c1.instanceId=1, c2.instanceId=2, c3.instanceId=3,
// but Counter.totalCreated == 3 for ALL of them — it's the same field.
```

Five real, deliberately broken programs, each producing a genuine `javac` error, captured verbatim in that directory's `compile-errors-transcript.txt`:

```
src/BrokenFinalReassignment.java:4: error: cannot assign a value to final variable x
src/BrokenFinalMethodOverride.java:10: error: greet() in Child cannot override greet() in Parent
  overridden method is final
src/BrokenFinalClassExtension.java:8: error: cannot inherit from final Sealed
src/BrokenAbstractInstantiation.java:7: error: Shape is abstract; cannot be instantiated
src/BrokenPrivateAccess.java:4: error: balance has private access in BankAccount
```

And the real, unplanned finding from Section 4 — `NestedPrivateAccessDemo.java` compiles and runs cleanly, printing `Account.balance accessed from a sibling nested class: 100.0`, even though `balance` is `private` — because both classes share the same top-level enclosing class.

Two further real, deliberately broken programs in the same directory, with their genuine `javac` output appended to `compile-errors-transcript.txt`:

```
src/BrokenVarargsAmbiguity.java:18: error: reference to handle is ambiguous
  both method handle(String,Object...) in BrokenVarargsAmbiguity and method handle(Object,String...) in BrokenVarargsAmbiguity match
src/BrokenStaticOverride.java:16: error: static methods cannot be annotated with @Override
```

The ambiguity error is the practical limit of varargs overloading: `handle("a", "b")` matches `handle(String, Object...)` and `handle(Object, String...)` equally well, neither is more specific than the other, and the compiler refuses rather than guessing. Two varargs overloads whose fixed-arity prefixes differ in this way are simply unusable together.

`src/MethodSignatureRulesDemo.java` covers the signature rules from Sections 4 and 5 — varargs arity, the three-phase overload resolution order, array-versus-varargs spreading, generic varargs heap pollution ending in a real `ClassCastException`, covariant return types, and static hiding versus instance overriding. Its full output, including the three real `-Xlint:all` warnings javac emits for the heap-pollution examples, is captured in `signature-rules-transcript.txt`.

## 8. Common Mistakes

- **Making every field `public` "to keep things simple"** — defeats encapsulation immediately; any external code can then set a field to an invalid state with no validation path.
- **Assuming `private` means "only this exact class"** — Section 4's nested-class finding shows the real rule is scoped to the top-level class, which can matter for inner-class-heavy designs.
- **Confusing a `final` class with an `abstract` class** — they are opposites in intent: `final` forbids all subclassing, `abstract` requires it (you can never instantiate an abstract class directly).
- **Expecting a varargs overload to win over a boxing one** — resolution reaches varargs only in its third and final phase, so `only(42)` picks `only(Integer)` over `only(int...)`, measured directly.
- **Passing an `int[]` where an `Object...` is expected and expecting it to spread** — it arrives as one element, because `int[]` is not an `Object[]`. This is exactly why `Arrays.asList(someIntArray).size()` is `1`.
- **Treating `@SafeVarargs` as a check rather than a promise** — it silences the warning without verifying anything; it is only correct when the method never stores, writes to, or exposes the varargs array.
- **Writing `@Override` on a `static` method** — a real compile error, because hiding a static method is not overriding it.
- **Forgetting that `static` methods cannot access instance (non-static) fields or call instance methods directly** — there is no implicit `this` in a static context, since a static method isn't tied to any particular instance.

## 9. Edge Cases

- A `private` constructor is a real, common pattern (utility classes with only static methods, or classes that only construct themselves internally via a static factory method) — it prevents `new` from outside the class entirely while still allowing internal instantiation.
- An `abstract` class can have zero abstract methods — a deliberate way to prevent direct instantiation of a class meant only as a shared base, even when every method already has a default body.
- `final` on a method parameter (`void process(final int id)`) prevents reassigning the parameter *variable* inside the method body — it says nothing about whether the value it points to (if it's a reference) is itself immutable.

## 10. Performance Implications

None of these modifiers have a meaningful runtime performance cost by themselves — access control is a compile-time-only check with zero runtime overhead, and `final` on a field can, in some cases, let the JIT compiler make additional optimizing assumptions (though this is an implementation detail, not a specified guarantee, and should never be the primary reason to reach for `final`).

## 11. Trade-offs

| Choice | When it helps | When it hurts |
|---|---|---|
| `private` field + public getter/setter | Encapsulation, validation on write | More boilerplate than a public field |
| Package-private | Internal collaboration without exposing it | Only works within one package |
| `final` class | Guarantees no unexpected subclass behavior | Forecloses legitimate future extension |
| `abstract` method | Forces every subclass to supply real behavior | Cannot provide a sensible default |

## 12. Senior-Level Considerations (L3)

A Senior engineer should recognize `abstract` classes and interfaces as two different tools for a similar goal, and know when each is the right one: an `abstract` class can hold shared state (fields) and a real constructor, which an interface cannot (interfaces contribute no instance state at all, per [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md)'s own diamond-problem discussion) — so reach for an abstract class when subclasses genuinely share implementation state, and an interface when you only need to guarantee a contract across otherwise-unrelated classes.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, access-modifier choices are an API-design and long-term-maintainability decision, not just a syntax choice: a `public` field or method becomes part of a library's contract with every caller the moment it ships — widening access later is always safe, narrowing it later is a breaking change. The disciplined default (start as restrictive as possible, widen only when a real caller needs it) is the same principle behind semantic versioning's treatment of API surface, and it is far more expensive to retrofit than to apply from the start.

## 14. Production Scenarios

No dedicated `production-cookbook/` entry exists yet for an access-modifier or final/abstract-specific incident.

> Planned reference: a dedicated `production-cookbook/` entry for a real incident caused by a field that should have been `private` being left `public` (or package-private and accessed from an unintended package after a refactor), allowing external code to mutate internal state and violate an invariant, would be a natural, non-duplicative addition to that deliverable's own backlog.

## 15. Interview Questions


### Interview Answer Framework

Pre-built delivery layers for this topic, in the shape [The Technical Answer Framework](../../20-interview-preparation/technical-answers/technical-answer-framework.md) describes. The 10-minute layer is an **outline, not a script** — expanding it into ten minutes of speech is work that happens out loud and in advance, per [Explaining Technical Concepts Under Pressure](../../20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md).

#### 30-Second Answer

Four access levels, most restrictive first: `private` is the same class only, package-private is the same package, `protected` adds subclasses, `public` is everywhere. Package-private is the real default — you get it by writing no keyword at all. Separately: `static` belongs to the class rather than any instance, and `final` means it cannot be reassigned, overridden, or extended depending on what it is applied to.

#### 2-Minute Answer

Open as above, then make the point that there is no `default` keyword — the absence of a modifier *is* a modifier, and it is deliberate: it lets classes in the same package collaborate without exposing anything publicly.

**Then `static` versus instance, concretely.** "A `static` field exists once for the whole class; every instance shares it. That is why a `static` counter counts across all objects, and why a `static` method cannot touch instance fields — there is no instance for it to touch."

**Then `final`, which means three different things by position.** On a variable: cannot be reassigned, though a `final` `List` can still have elements added. On a method: cannot be overridden. On a class: cannot be extended. The variable case is the one that gets misread as immutability.

Close with the method signature: name plus parameter types. Not the return type — which is exactly why you cannot overload on return type alone.

#### 10-Minute Deep Dive

Cover, in order: the four access levels and why package-private has no keyword (Section 3); `static` as class-level state, with the shared-counter demonstration; `final` in its three positions and the `final`-does-not-mean-immutable trap; what a method signature legally consists of, and the consequence for overloading; abstract classes and methods, and when an abstract class beats an interface; varargs and how they participate in overload resolution (Section 4, measured); and close with the Staff-level framing — access modifiers as an API-surface decision, since anything `public` is a promise you have to keep.

#### Whiteboard Explanation

Draw the four-level access table as concentric rings: `private` innermost, then package, then package-plus-subclasses, then `public` outermost. Mark the second ring "no keyword — the default". Most candidates recite four levels; drawing them as nested scopes shows you understand they are a containment hierarchy, not a list.
### Question 1: What are Java's access modifiers, and what does "package-private" actually mean?

**Expected answer:** `private`, package-private (no modifier), `protected`, `public`, in increasing order of visibility. Package-private is the real default when no modifier is written — visible within the same package only, invisible everywhere else, including subclasses in other packages.

**Common mistakes:** forgetting package-private exists as a real, distinct level (treating "no modifier" as equivalent to `public` or `private`).

**Follow-up questions:** "can a subclass in a different package access a package-private member of its superclass?" (No — package-private ignores the subclass relationship entirely; only `protected` or wider grants that.)

**Senior-level expectations:** connects the choice of access level to API-design discipline (Section 13).

**Staff-level expectations:** frames narrowing access later as a breaking change, widening as safe — a real semantic-versioning-adjacent principle.

### Question 2: What's the difference between an abstract method and a concrete method, and can an abstract class have both?

**Expected answer:** an abstract method has a signature but no body (ends in `;`, forces subclasses to implement it); a concrete method has a real body and is directly callable/inheritable. Yes — an abstract class routinely has both, and this chapter's own demo (`Shape`) does exactly that.

**Minimum acceptable answer:** knows an abstract class cannot be instantiated directly.

**Strong Senior answer:** correctly distinguishes when to use an abstract class (shared state + some default behavior) versus an interface (pure contract, no shared state).

**Staff-level extension:** discusses the trade-off of introducing an abstract base class into an existing hierarchy versus retrofitting an interface with default methods, and the compatibility implications of each for existing subclasses.

**Common mistakes:** claiming an abstract class can never have a constructor (it can — called via `super()` from a subclass); claiming an interface and an abstract class are interchangeable.

**Likely follow-ups:** "why can't you instantiate an abstract class even if every one of its methods has a body?" (Because `abstract` on the class itself is a separate, explicit declaration of intent, independent of whether any specific method is abstract.)

**Evaluation criteria:** correctly explains the body/no-body distinction, correctly states instantiation is forbidden, and can articulate at least one real reason to choose an abstract class over an interface.

### Question 3: What is varargs, and which overload wins when a varargs method and a boxing overload both apply?

**Expected answer:** `Type... name` lets a caller pass any number of arguments, including zero; at runtime the method receives an array (`String[] of length 0` for a no-argument call, never `null`). It must be the last parameter and there can be only one. Java resolves an overloaded call in three phases — phase 1 subtyping and widening primitive conversion, phase 2 additionally boxing/unboxing, phase 3 varargs — stopping at the first phase with an applicable method. So a boxing overload beats a varargs overload: with `only(Integer)` and `only(int...)` in scope, `only(42)` selects `only(Integer)`, verified by real, executed output.

**Minimum acceptable answer:** knows varargs accepts a variable number of arguments and is an array inside the method.

**Strong Senior answer:** states the three phases in order, notes that varargs losing every contest is what makes adding a varargs overload to an existing API relatively safe, and knows that two varargs overloads can be mutually ambiguous — `handle(String, Object...)` and `handle(Object, String...)` produce a real `reference to handle is ambiguous` compile error for `handle("a", "b")`.

**Staff-level extension:** treats varargs in a published API as a compatibility decision — changing a fixed-arity method to varargs is source-compatible but not binary-compatible, because the erased descriptor changes from `(String,String)` to `(String,String[])`, so callers compiled against the old signature break at link time until recompiled.

**Common mistakes:** believing a varargs parameter arrives as `null` when no arguments are passed; assuming varargs wins over boxing; trying to declare two varargs parameters.

**Likely follow-ups:** "what happens if you pass an `int[]` to an `Object...` parameter?" (It arrives as a single element, because `int[]` is not an `Object[]` — this is exactly why `Arrays.asList(someIntArray).size()` is `1` while `Arrays.asList(someStringArray).size()` is the array's length.) "How do you force an array to be treated as one argument?" (Cast it to `(Object)`.)

**Evaluation criteria:** correct phase ordering, correct empty-varargs behavior, and awareness that array-versus-varargs behaves differently for primitive and reference arrays.

### Question 4: What is heap pollution, and what does `@SafeVarargs` actually guarantee?

**Expected answer:** heap pollution is a variable whose declared parameterized type does not match the type of the object it actually references, which erasure makes possible. A generic varargs parameter (`<T> T... items`) is the common source: the compiler cannot create an array of the erased type parameter, so it creates one of the erasure bound. Real, measured: a method returning `T[]` from generic varargs produced a value declared `List<String>[]` whose runtime type printed as `List[]`; storing an `Integer`-bearing list into it through an `Object[]` alias raised no `ArrayStoreException`, and the failure surfaced later as a `ClassCastException` on a read with no visible cast in the source. `@SafeVarargs` only suppresses the warning — it verifies nothing. It is honest only when the method reads the array and never stores it, writes to it, or lets it escape.

**Minimum acceptable answer:** knows generic varargs produce an unchecked warning related to erasure.

**Strong Senior answer:** explains why the error surfaces far from its cause (the compiler-inserted cast at the read site), and states the concrete rule for when `@SafeVarargs` is a true claim.

**Staff-level extension:** frames blanket `@SafeVarargs` annotation as a review hazard — it is an assertion a reviewer must verify by reading the body, so a codebase that applies it reflexively has converted a compiler warning into an unchecked human process.

**Common mistakes:** treating `@SafeVarargs` as a compiler check; believing the `ArrayStoreException` mechanism protects generic arrays (it does not, because the runtime element type is the erasure).

**Likely follow-ups:** "why is `List<String>[]` not creatable directly?" (Generic array creation is forbidden precisely because the runtime store check cannot see the type argument.) "Can you annotate any method with `@SafeVarargs`?" (Only `static`, `final`, or `private` methods, plus constructors — an overridable method could break the promise in a subclass.)

**Evaluation criteria:** defines heap pollution correctly, connects the delayed `ClassCastException` to erasure, and states a usable rule for `@SafeVarargs`.

### Question 5: A subclass declares a `static` method with the same signature as its superclass. Which one runs, and why?

**Expected answer:** the one selected by the *compile-time* type of the reference, because static methods are hidden rather than overridden. Real, executed contrast with `Base viewedAsBase = new Derived()`: the instance method resolves to `Derived.instanceGreet()` by dynamic dispatch on the runtime type, while `Base.staticGreet()` resolves to the base version by static binding. `@Override` on a static method is a real compile error: `static methods cannot be annotated with @Override`.

**Minimum acceptable answer:** knows static methods are not polymorphic.

**Strong Senior answer:** names the bytecode reason — `invokestatic` resolves against the compile-time type, `invokevirtual` dispatches on the runtime type — and notes that calling a static method through an instance reference (`viewedAsBase.staticGreet()`) compiles but is misleading enough that most style guides forbid it.

**Staff-level extension:** treats static hiding as an API-design smell: if a subclass wants to vary a static method's behavior, the method is really an instance concern, and the class is fighting the language rather than expressing a hierarchy.

**Common mistakes:** predicting `Derived.staticGreet()` because the object is a `Derived`; assuming `@Override` works on statics.

**Likely follow-ups:** "does the same rule apply to fields?" (Yes — field access is also resolved by the compile-time type, which is field hiding.) "Does a covariant return type still count as an override?" (Yes — narrowing the return type is legal since Java 5 and is a genuine override, verified by real output.)

**Evaluation criteria:** correct answer with the compile-time-versus-runtime reason, and ideally the bytecode-level explanation.

## 16. Coding/Practice Exercises

1. Write a `static` counter class of your own (not copied from this chapter) that tracks how many times a specific method has been called across all instances.
2. Write an abstract `PaymentMethod` class with one abstract method (`process(double amount)`) and one concrete method (`describe()`), then implement two concrete subclasses.

## 17. Debugging Exercises

Given a class where a `static` field is unexpectedly shared across what the reader assumed were independent instances (a classic real bug: declaring a field `static` by mistake, or relying on it deliberately without realizing the implication), identify the fix and explain the actual behavior before and after.

## 18. Design Exercises

Design a small class hierarchy for a company's employee types (e.g., `Employee` abstract base, `Manager`/`Engineer` concrete subclasses) deciding explicitly, in writing, which fields should be `private`, which methods should be `abstract` vs. concrete, and whether any class or method should be `final` — and justify each choice against Section 6's practical-usage guidance.

## 19. Further Reading

- [Java OOP Fundamentals](java-oop-fundamentals-classes-objects-and-interfaces.md) — classes, interfaces, and the diamond problem this chapter's abstract-class material builds toward.
- [Reflection and Dynamic Proxies](reflection-and-dynamic-proxies.md) — how `setAccessible(true)` deliberately bypasses the compile-time access checks this chapter describes.
- [Immutability and Defensive Copying](immutability-and-defensive-copying.md) — `final` fields as one ingredient of real immutability, at Senior depth.
- [Generics, Erasure, and PECS](generics-erasure-and-pecs.md) — why a generic varargs parameter cannot produce a properly-typed array, and what heap pollution costs.
- [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md) — the `invokestatic` versus `invokevirtual` reason static methods hide while instance methods override.
- [Nested and Inner Classes](nested-and-inner-classes.md) — the full treatment of the nested-class access rule this chapter's Section 4 finding touches.

## 20. Mastery Checklist

- [ ] Can name all four access levels and correctly state their visibility rules from memory.
- [ ] Can explain the difference between `static` and instance state with a concrete example.
- [ ] Can explain all three effects of `final` (variable, method, class) without conflating them.
- [ ] Can explain the difference between an abstract method and a concrete method, and why an abstract class cannot be instantiated.
- [ ] Can state the three phases of overload resolution in order and explain why varargs always loses.
- [ ] Can predict, correctly, what `Arrays.asList(new int[]{1,2,3}).size()` returns and say why.
- [ ] Can explain heap pollution and state exactly when `@SafeVarargs` is honest.
- [ ] Can explain why a `static` method in a subclass hides rather than overrides, and what that changes at the call site.
- [ ] Reproduced this chapter's real demo and all five real compile errors, and can explain each one's root cause.
