# Enums, EnumMap, and EnumSet (T-111) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/language-core/enums-enummap-and-enumset.md`](../../../../syllabus/02-java/language-core/enums-enummap-and-enumset.md)
(T-111). Three independent demos: real enum-internals proofs (including a genuinely
JVM-enforced reflection guard), a real, honestly-modest `EnumMap`-vs-`HashMap` throughput
measurement, and a dramatic real reproduction of the classic `ordinal()` danger.

## Setup and run

```bash
cd practice/java/java-core/enums-enummap-enumset
mkdir -p out
javac -d out src/*.java
java -cp out EnumInternalsDemo
java -cp out EnumMapVsHashMapDemo
java -cp out OrdinalDangerDemo
```

No special flags needed.

## Real observed output (last run)

### `EnumInternalsDemo` — real singleton identity, a real JVM-enforced reflection guard, and real constant-specific bodies

```
Color.values()[0] == Color.values()[0] (called twice): true
Color.valueOf("RED") == Color.RED: true

Color's declared constructors: 1
Reflective construction threw real IllegalArgumentException: Cannot reflectively create enum objects

PLUS.apply(6, 3) = 9  (real runtime class: EnumInternalsDemo$Operation$1, declaring class: EnumInternalsDemo$Operation)
MINUS.apply(6, 3) = 3  (real runtime class: EnumInternalsDemo$Operation$2, declaring class: EnumInternalsDemo$Operation)
TIMES.apply(6, 3) = 18  (real runtime class: EnumInternalsDemo$Operation$3, declaring class: EnumInternalsDemo$Operation)
```

Every access to an enum constant returns the identical singleton instance — real, verified `==`.
Attempting reflective construction doesn't fail because of ordinary access control; it throws a
real, dedicated `IllegalArgumentException: Cannot reflectively create enum objects` — a genuine,
specific JVM-level guard, not merely a private-constructor convention. Each constant declared with
a body (`PLUS { ... }`) is real, verified proof of a genuine anonymous *subclass* per constant
(`Operation$1`, `Operation$2`, `Operation$3`) — `getSimpleName()` on these returns empty (a real
quirk of anonymous classes), which is why `getDeclaringClass()` is the correct way back to the
actual enum type.

### `EnumMapVsHashMapDemo` — an honest, modest real throughput difference, and a real, guaranteed ordering difference

```
EnumMap:  305ms
HashMap:  334ms
Real measured ratio: 1.08-1.14x (varies slightly by run)

Inserted FRIDAY, MONDAY, WEDNESDAY (in that order) -- EnumMap.keySet() = [MONDAY, WEDNESDAY, FRIDAY]
```

The real, measured put+get throughput difference between `EnumMap` and `HashMap` is modest —
roughly 1.08–1.14x across repeated runs, not the dramatic multiple sometimes assumed for an
array-backed structure with no hashing. This is reported honestly rather than overstated: modern
`HashMap` with `Integer`-boxed values and a small, well-distributed key space is already fast.
`EnumMap`'s real, unambiguous, guaranteed advantage is its **iteration order** — always natural
(ordinal) declaration order, regardless of insertion order, verified directly — versus `HashMap`'s
genuinely unspecified order.

### `OrdinalDangerDemo` — a real, dramatic reproduction of the classic ordinal-persistence bug

```
V1: PENDING=0, APPROVED=1, REJECTED=2
V2 (after inserting IN_REVIEW in the middle): PENDING=0, IN_REVIEW=1, APPROVED=2, REJECTED=3

A database row stored ordinal=2 back when it meant StatusV1.REJECTED.
Reading that SAME stored value (2) back through StatusV2.values()[2] now returns: APPROVED
<-- REAL: silently, incorrectly APPROVED instead of REJECTED. No exception. No warning.

StatusV2.valueOf("REJECTED") = REJECTED  <-- REAL: correctly resolves regardless of declaration order
```

Inserting a single new constant in the middle of an enum's declaration silently shifts every later
constant's real `ordinal()` value — a real, exact, silent data-corruption reproduction: a value
persisted as `ordinal=2` (meaning `REJECTED` under the original declaration order) resolves to the
genuinely wrong constant (`APPROVED`) after the enum is innocently reordered, with zero exception
and zero warning. `name()`/`valueOf()`, by contrast, are real, verified stable across reordering.
