---
title: "Java OOP Fundamentals: Classes, Objects, and Interfaces"
slug: java-oop-fundamentals-classes-objects-and-interfaces
document_type: syllabus-topic
domain: 02-java
topic_id: T-2201
status: draft
version: 1.0
last_updated: 2026-09-07
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - java-syntax-fundamentals-variables-control-flow-and-methods.md
related:
  - java-syntax-fundamentals-variables-control-flow-and-methods.md
  - polymorphism-and-dynamic-dispatch.md
  - equals-hashcode-and-comparable-contracts.md
  - immutability-and-defensive-copying.md
  - ../../04-software-design/design-patterns-applied.md
practice: ../../../practice/java/oop-fundamentals/classes-and-objects/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://docs.oracle.com/javase/tutorial/java/concepts/index.html
  - https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html
  - https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html
---

# Java OOP Fundamentals: Classes, Objects, and Interfaces

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

Every other Java topic in this syllabus assumes you can already read a class definition, tell a field from a method, and know why `private` exists. That assumption was correct for the original Senior/Staff-only version of this repository, but it stopped being correct the day this project decided to cover Junior through Staff — the same way `01-computer-science-foundations` and `03-data-structures-algorithms` already do. This chapter is the missing floor under `02-java`: it is the one chapter in this domain that teaches object-oriented programming itself, not a specific advanced mechanic of it. Interviewers rarely ask "what is a class" directly past the Junior level, but they constantly ask questions that silently assume you can answer it: "walk me through your class design," "why did you use an interface here instead of a class," "what would you change about this hierarchy." Candidates who learned OOP by imitation rather than by understanding the actual rules stall exactly there.

## 2. Prerequisites

[Java Syntax Fundamentals: Variables, Control Flow, and Methods](java-syntax-fundamentals-variables-control-flow-and-methods.md) — added 2026-09-08 as an even more foundational chapter once it became clear this one already assumed you could read an `if` statement and a `for` loop inside a method body. If you have never written a class before *and* are comfortable with basic Java syntax already, you can start directly here.

## 3. Foundation (L1)

Think of a **class** as a blueprint, and an **object** as one specific thing built from that blueprint. A blueprint for a house is not a house — you cannot live in a blueprint, and a single blueprint can produce many actual houses, each standing on its own lot with its own furniture. `class House { int rooms; }` is the blueprint. `new House()` builds one actual house — one **instance** — in memory, with its own copy of `rooms`. Build a second one, `new House()` again, and you have a second house with its own `rooms` value, completely independent of the first. This is the single most important fact to internalize before anything else: a class is written once; objects built from it exist many times, each with their own state.

A class bundles two things together: **fields** (the data an object carries — `rooms`, `address`, `ownerName`) and **methods** (the behavior it can perform — `paintWalls()`, `addRoom()`). This bundling is the entire idea of object-oriented programming in one sentence: keep data and the operations that make sense on that data in the same place, instead of scattering raw data through a program and hoping every piece of code that touches it remembers all the rules.

A **constructor** is a special method, matching the class's own name, that runs exactly once when `new` builds an object — its job is to make sure the object starts in a valid state. `new House(3)` should hand you a house that already has 3 rooms recorded, not an empty shell you have to remember to fill in correctly yourself afterward.

Now the four ideas usually called "the pillars of OOP" — not as a list to memorize, but as four answers to four different, concrete problems:

- **Encapsulation** — hide a class's own data behind methods, so the class can guarantee its own rules no matter who's calling it. Problem it solves: "how do I stop other code from putting my object into an invalid state?"
- **Abstraction** — expose *what* something does without exposing *how*. Problem it solves: "how do I let calling code depend on a stable contract, so the implementation underneath can change freely?"
- **Inheritance** — let one class reuse and specialize another class's fields and methods. Problem it solves: "how do I avoid rewriting the same fields and methods for every closely related type?"
- **Polymorphism** — let the same method call run different code depending on the actual object it's called on. Problem it solves: "how do I write one piece of code that works correctly against a whole family of related types, without an `if/else` chain checking which one it is?" This one has its own full chapter — [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md) — because the actual JVM mechanism behind it (`invokevirtual`) is deep enough, and differentiating enough at Senior level, to deserve dedicated treatment. This chapter uses polymorphism as a tool in Section 7's examples without re-deriving how it works underneath.

## 4. Core Concepts (L2)

**Interfaces vs. abstract classes** is the single most common "which tool do I reach for" confusion at this level, and it has a precise answer, not a matter of taste:

An **abstract class** can hold real fields, a real constructor, and concrete (fully implemented) methods alongside abstract (unimplemented) ones — it is a partial implementation, meant to be completed by a subclass. A class can `extend` only **one** class, abstract or not — Java has single inheritance for classes, on purpose (Section 5 explains the actual reason).

An **interface** declares a contract — method signatures any implementing class promises to fulfill — with no instance fields and, in modern Java, only `default` or `static` methods carrying actual code, never constructor logic. A class can `implement` **as many interfaces as it wants**, with no conflict, because implementing an interface never contributes state or constructor behavior that needs resolving.

The decision rule this produces: reach for an abstract class when you have real, shared *implementation* to hand down (fields, a constructor, working method bodies) to a genuinely related family of types. Reach for an interface when you only need to promise a *capability* — `Comparable`, `Runnable`, `Serializable` — that unrelated classes might all want to advertise, without inheriting anything from each other.

**Composition** — a class holding a reference to another class as a field, rather than extending it — is often the better tool than inheritance even when an "is-a" relationship looks plausible on paper. The classic failure mode ("the fragile base class problem," and the specific class-explosion variant demonstrated in Section 7's third demo) is that inheritance can only vary along one hierarchy at a time; a second independent way a type needs to vary forces either a combinatorial explosion of subclasses or contorting the hierarchy to fit. Composition — "has-a" instead of "is-a" — sidesteps this because each independent behavior lives in its own small class or interface, assembled together at construction time rather than baked into a fixed inheritance chain. This is *not* a claim that inheritance is wrong; it is a claim that inheritance is the right tool specifically for genuine, stable "is-a" relationships (a `Circle` really is a `Shape`, permanently, with no second axis of variation competing for the same hierarchy), and composition is the right tool for everything else.

## 5. How It Works Internally (L3)

Every object created with `new` lives on the heap. A variable holding that object — `House h = new House(3);` — does not hold the object itself; it holds a **reference** (conceptually, an address) pointing at it. `House h2 = h;` copies the reference, not the object — `h` and `h2` now point at the exact same object in memory, so a change made through `h2` is visible through `h` too. This is why Section 8's most common bug (accidentally sharing a mutable object between two places that each assume they own it privately) is possible at all: two references, one object.

Fields declared in a class are laid out as part of every object built from it — a `House` with an `int rooms` field means every single `House` object carries its own 4 bytes for `rooms`, at the same offset within the object, regardless of how many `House` objects exist. Methods are not duplicated per object this way — there is exactly one copy of a method's compiled bytecode regardless of how many objects call it; calling `h.paintWalls()` and `h2.paintWalls()` both jump into the same compiled method, just supplied with a different object reference (`this`) each time.

Why can a class `extend` only one other class, but `implement` many interfaces? Multiple inheritance of actual field state creates a genuine, unsolvable ambiguity the JVM has no consistent way to resolve on its own: if `class C extends A, B` and both `A` and `B` declare a field or method named `x`, which `x` does `C` inherit — a problem languages with multiple class inheritance (like C++) solve with extra rules (virtual inheritance) that Java's designers deliberately chose not to take on. Interfaces sidestep the problem entirely because, until default methods arrived in Java 8, they contributed no state and no method bodies to inherit a conflict from; default methods can still collide today, but Java resolves that specific, narrower case with an explicit rule (a class implementing two interfaces with clashing default methods must override the method itself, rather than the JVM guessing).

## 6. Practical Usage

Model a domain concept as a class when it has both data and behavior that belong together — an `Order` that can `addItem()` and `calculateTotal()`, not just a bag of fields some other code manipulates from outside. Default to making fields `private` and exposing only the methods a caller actually needs; add a public getter or setter only when something outside the class genuinely needs that access, not automatically for every field. Prefer composition for anything that is not a stable, permanent "is-a" relationship — when in doubt, a `has-a` field is easier to change later than an inheritance hierarchy is to un-tangle.

## 7. Examples

All three demos below are real, compiled, and executed on OpenJDK 21.0.12 — [`practice/java/oop-fundamentals/classes-and-objects/`](../../../practice/java/oop-fundamentals/classes-and-objects/), 19/19 assertions passing.

**Encapsulation, the bug it prevents, made concrete** — [`EncapsulationDemo.java`](../../../practice/java/oop-fundamentals/classes-and-objects/src/EncapsulationDemo.java): a `LeakyAccount` with a public `balance` field can be set directly to `-500.0` from outside the class — nothing stops it, because there is nothing to stop it with. The fixed `BankAccount` makes the identical mistake a compile error: `balance` is `private`, so the only way to change it is through `deposit()`/`withdraw()`, both of which reject an invalid amount before it ever reaches the field. 7/7 assertions pass, including both the accepted valid operations and the rejected invalid ones.

**Abstract class vs. interface, same problem, both tools shown side by side** — [`AbstractionAndInheritanceDemo.java`](../../../practice/java/oop-fundamentals/classes-and-objects/src/AbstractionAndInheritanceDemo.java): an abstract `Shape` class supplies a real, inherited `getName()` method and forces every subclass to supply its own `area()`; a `Movable` interface supplies a capability (`move()`, plus a `default` `describe()`) that has nothing to do with being a shape. `MovableCircle` extends `Circle` (one class) and implements `Movable` (one interface) simultaneously, proving the practical difference in Section 4's decision rule directly rather than asserting it. 6/6 assertions pass.

**Composition avoiding a real subclass explosion** — [`CompositionOverInheritanceDemo.java`](../../../practice/java/oop-fundamentals/classes-and-objects/src/CompositionOverInheritanceDemo.java): the inheritance version needs one subclass per engine type (`GasCar`, `ElectricCar`); adding a second independent variation (transmission type) would need one subclass per *combination* (`GasManualCar`, `GasAutomaticCar`, `ElectricManualCar`, `ElectricAutomaticCar` — four classes for two axes of two options each). The composition version expresses all four combinations from just four small classes (`GasEngine`, `ElectricEngine`, `ManualTransmission`, `AutomaticTransmission`) assembled through one `ComposedCar` constructor, with zero classes added as combinations grow. 6/6 assertions pass.

## 8. Common Mistakes

- **Exposing every field with a public getter and setter by default** — this undoes encapsulation almost entirely; a setter for every field is functionally equivalent to a public field, just with extra syntax, unless the setter actually validates something.
- **Sharing a mutable object between two places that each assume private ownership** — a direct consequence of Section 5's reference-sharing mechanics: assigning a reference doesn't copy the object, so a caller storing an object you handed it can mutate the *same* object you're still holding.
- **Reaching for inheritance because an "is-a" name sounds plausible, without checking for a second independent axis of variation** — the exact failure mode Section 7's third demo makes concrete.
- **Believing an interface can hold instance state** — it cannot; a `static final` constant on an interface is a compile-time-shared constant, not per-implementor state, a distinction that surprises candidates who reach for an interface expecting field-like behavior.
- **Forgetting that a constructor is not automatically inherited** — a subclass must explicitly call `super(...)` (implicitly, if the no-arg case fits, or explicitly otherwise) to run the parent's own constructor logic; skipping this is a compile error the moment the parent has no no-argument constructor of its own.

## 9. Edge Cases

- A class with **no explicit constructor** gets an implicit no-argument one supplied by the compiler — but the moment you write *any* constructor yourself, that implicit one disappears, and callers relying on `new House()` with no arguments will get a compile error if you only ever wrote `House(int rooms)`.
- An **interface's `static` method** belongs to the interface itself, not to any implementing class, and is called as `InterfaceName.method()`, never through an instance reference — a genuinely different rule from a class's own static methods, which (while discouraged, per [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md)'s own field/static-hiding coverage) can be called through an instance reference too.
- **Two interfaces with a colliding `default` method**, both implemented by the same class, is a compile error unless that class explicitly overrides the method itself — Java refuses to silently pick one interface's version over the other's.

## 10. Performance Implications

Encapsulation and abstraction, as used in this chapter, cost nothing at runtime beyond an ordinary method call — a validated `deposit()` method is not measurably slower than direct field access for any realistic workload; the JIT compiler routinely inlines small, simple accessor and validation methods entirely, per the mechanics [Escape Analysis and Scalar Replacement](../jvm-internals/escape-analysis-and-scalar-replacement.md) and general JIT inlining cover in depth. Choosing composition over a deep inheritance hierarchy has a small, usually-irrelevant indirection cost (one extra reference hop to reach the composed object's method) that is never the deciding factor in this choice — the deciding factor is always maintainability, per Section 11.

## 11. Trade-offs

| Concern | Inheritance | Composition |
|---|---|---|
| Best for | A genuine, stable, single "is-a" relationship | Anything with more than one independent axis of variation, or where the relationship might change |
| Flexibility | Fixed at compile time; a subclass cannot change its parent at runtime | The composed object can be swapped for a different implementation of the same interface |
| Coupling | Tight — a subclass depends on its parent's internal implementation details, not just its public contract | Loose — a class depends only on the interface of what it holds, not that interface's implementation |
| Failure mode when misused | Class explosion (Section 7) or a hierarchy contorted to fit an unrelated variation | Slightly more boilerplate (an extra interface, an extra constructor parameter) for genuinely simple, single-axis cases |

## 12. Senior-Level Considerations (L3)

A Senior engineer is expected to justify a class design choice, not just produce one that compiles. When reviewing a colleague's class hierarchy, the concrete question to ask is Section 4's decision rule applied backward: "is there a second way any of these subclasses might need to vary, independently of the axis this hierarchy already models?" — if yes, that hierarchy is a composition candidate before it grows its next subclass, not after. Senior-level code review should flag a getter/setter pair added "just in case" the same way it flags an unused import: a small, cheap signal that the class's actual invariants were never decided.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the pillars in this chapter stop being about a single class and become an organizational lever. A codebase where every class defaults to public fields and setters has no enforced invariants anywhere — every bug class Section 5's reference-sharing mechanics can produce becomes a live risk at every call site, multiplied by however many engineers touch that code without knowing its unstated rules. A Staff engineer's real leverage here is standards work, not code: getting a team to default to `private` fields and validated construction, or flagging a growing inheritance hierarchy during a design review before it becomes the four-subclass (or forty-subclass) problem Section 7 demonstrates in miniature, prevents the same mistake from being independently re-made by every engineer who touches that part of the system. This connects directly to [Clean and Hexagonal Architecture](../../17-architecture/clean-hexagonal-architecture.md)'s own dependency-direction rule at a larger scale: encapsulation and abstraction, done well inside one class, are the same discipline that architecture applies across module boundaries.

## 14. Production Scenarios

No existing `production-cookbook/` entry has an OOP-fundamentals-specific root cause — the closest adjacent entries are architecture-scale (module boundary violations), not single-class design.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by two components unintentionally sharing a mutable object across an assumed ownership boundary (the exact class of bug Section 5's reference-sharing mechanics makes possible) would be a natural, non-duplicative addition connecting this chapter's foundational mechanics to a genuine production incident.

## 15. Interview Questions

**Q1 (Junior): "What's the difference between a class and an object?"**
Expected answer: a class is a blueprint/template; an object is a specific instance built from it via `new`, with its own copy of the class's fields. Common mistake: describing them as synonyms, or being unable to give a concrete example.

**Q2 (Junior/Mid): "When would you use an interface instead of an abstract class?"**
Expected answer: Section 4's decision rule — abstract class when there's real shared implementation and fields to hand down within a genuinely related family; interface when you only need to promise a capability, especially across otherwise-unrelated classes, or when the implementing class needs to extend something else too (single inheritance forces the choice).

**Q3 (Mid): "Why can a Java class only extend one class but implement many interfaces?"**
Expected answer: Section 5's diamond-ambiguity explanation — multiple inheritance of actual field/method state creates unresolvable conflicts the JVM has no consistent rule for; interfaces avoid this because they historically contributed no state, and Java resolves the narrower default-method collision case with an explicit forced-override rule instead.

**Q4 (Mid/Senior): "Give a concrete example of favoring composition over inheritance, and explain why."**
Expected answer: something structurally like Section 7's engine/transmission demo — a second independent axis of variation that would otherwise force a multiplying number of subclasses. A strong answer states the actual mechanism (independent axes multiply under inheritance, add linearly under composition), not just "composition is more flexible" as an unexplained slogan.

**Q5 (Senior/Staff): "You're reviewing a PR that adds a fourth level to an inheritance hierarchy. What do you look for?"**
Expected answer: Section 12's reviewer question (is there a second independent axis of variation this hierarchy is being contorted to fit), plus Section 13's framing — that catching this in review is cheaper than the same mistake being re-made across a codebase, and that the fix (introduce composition at the point the second axis appears) is far cheaper before the fourth subclass than after the twentieth.

## 16. Coding/Practice Exercises

1. Extend `EncapsulationDemo`'s `BankAccount` with a `transfer(BankAccount other, double amount)` method that withdraws from `this` and deposits into `other`, but only if the withdrawal would succeed — leaving neither account changed if it wouldn't. Write a new assertion proving a failed transfer leaves both balances untouched.
2. Add a `Square` class to `AbstractionAndInheritanceDemo` that extends `Rectangle` rather than `Shape` directly (a square genuinely is a rectangle with equal sides). Add an assertion that a `Square` correctly participates in the same `List<Shape>` total-area loop as the existing shapes.
3. Add a third independent axis to `CompositionOverInheritanceDemo` (e.g., drivetrain: `FWD`/`AWD`) as its own interface + two implementations, and construct one `ComposedCar` combining all three axes, proving the "no new Car subclass" property still holds with three axes, not just two.

## 17. Debugging Exercises

Given this code, predict the output before running it, then verify:

```java
class Counter {
    int count;
    void increment() { count++; }
}

Counter a = new Counter();
Counter b = a;
a.increment();
b.increment();
System.out.println(a.count);
```

The answer is `2`, not `1` — `b = a` copies the *reference*, not the object (Section 5), so `a.increment()` and `b.increment()` both mutate the exact same `Counter` object. A candidate expecting `1` is modeling `Counter` as if assignment copied the object, the way a primitive `int` assignment does — the single most common source of confusion at this level, and worth deliberately breaking on first before it causes a real bug.

## 18. Design Exercises

Design a small class hierarchy (or composition-based alternative — decide which, and justify it using Section 4's rule) for a notification system that needs to send `Email`, `SMS`, and `PushNotification` messages, where a new channel (e.g., Slack) might be added later, and some channels (but not all) support a "priority" flag that changes delivery behavior. State explicitly which parts you modeled with inheritance, which with composition, and why the "priority flag on only some channels" requirement pushed your decision one way or the other.

## 19. Further Reading

- [Polymorphism and Dynamic Dispatch Mechanics](polymorphism-and-dynamic-dispatch.md) — the JVM mechanism (`invokevirtual`) behind the polymorphism this chapter uses but does not re-derive.
- [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) — the next chapter once class basics are solid; covers the contracts every well-designed class needs to honor.
- [Design Patterns Applied](../../04-software-design/design-patterns-applied.md) — composition-over-inheritance, applied at the scale of named, reusable design patterns (Strategy, Decorator, and others build directly on this chapter's Section 4 rule).

## 20. Mastery Checklist

- [ ] Can explain the class/object distinction with a concrete example, not just definitions.
- [ ] Can state, and justify with a real example, when to use an interface versus an abstract class.
- [ ] Can explain why Java classes have single inheritance but multiple interface implementation, citing the actual diamond-ambiguity reason.
- [ ] Can identify a class hierarchy that should be composition instead, by spotting a second independent axis of variation.
- [ ] Can predict the output of the Section 17 reference-sharing debugging exercise correctly, and explain why.
- [ ] Can connect this chapter's Section 4 decision rule to at least one named design pattern from `syllabus/04-software-design/`.
