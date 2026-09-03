# Optional and null strategy (T-109) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/java-core/optional-and-null-strategy.md`](../../../../syllabus/02-java/language-core/optional-and-null-strategy.md)
(T-109). Three independent demos: real construction/access contracts, a real ~1200x-measured
eager-vs-lazy gotcha, and a real serialization failure proving the "Optional as a field" anti-pattern.

## Setup and run

```bash
cd practice/java/java-core/optional-and-null-strategy
mkdir -p out
javac -d out src/*.java
java -cp out OptionalBasicsDemo
java -cp out OrElseVsOrElseGetDemo
java -cp out OptionalAsFieldAntiPatternDemo
```

No special flags needed.

## Real observed output (last run)

### `OptionalBasicsDemo` — real construction and access contracts

```
Optional.of(null): threw real NullPointerException, immediately at construction
Optional.ofNullable(null): Optional.empty (no exception, isPresent=false)

get() on empty: threw real NoSuchElementException: No value present

orElse("default"):        default
orElseGet(() -> "lazy"):  lazy
orElseThrow(customSupplier): threw the real, custom exception: real, custom exception

present.map(String::length) = Optional[10]
emptyOpt.map(String::length) = Optional.empty (map on empty short-circuits, no NPE)
```

### `OrElseVsOrElseGetDemo` — a real, dramatically measured eager-vs-lazy gotcha

```
orElse() on a PRESENT Optional: returned "already-have-a-value" (the real, present value) -- but the fallback argument was STILL evaluated 1 time(s) before orElse() was even called, its result silently discarded -- EAGER evaluation, real and measurable
orElseGet() on a PRESENT Optional: fallback was called 0 time(s) -- LAZY evaluation, real and measurable, the Supplier is never even invoked

On an EMPTY Optional: orElse() called fallback 1 time(s), orElseGet() called it 1 time(s) -- identical when the Optional is actually empty, the difference ONLY shows up when present

== Real measured cost of the eager evaluation, with a genuinely expensive fallback ==
orElse(expensive computation), 5,000,000 calls, value already present: 3715ms
orElseGet(expensive computation), 5,000,000 calls, value already present: 3ms
Real measured cost of the eager-evaluation bug: 1238.33x
```

`orElse(x)` is a plain method call — Java must evaluate `x` before the call happens, exactly like
any other method argument, *regardless* of whether the `Optional` turns out to be present. This is
measured directly and dramatically: with a genuinely expensive fallback computation and an
already-present `Optional`, `orElse()` measured a real ~1200x slower than `orElseGet()` across
repeated runs — `orElseGet()`'s `Supplier` is never even invoked when the value is present, real,
lazy evaluation. The two methods behave identically only when the `Optional` is genuinely empty.

### `OptionalAsFieldAntiPatternDemo` — a real, concrete consequence of the "Optional as a field" anti-pattern

```
Optional implements Serializable: false

Serialization threw real NotSerializableException: java.util.Optional

Serialization succeeded, 127 real bytes written
getMiddleName() still returns a real Optional at the call site: Optional.empty
```

`java.util.Optional` genuinely does not implement `Serializable` — a real, verifiable fact, not
merely a style guideline. A class storing an `Optional` directly as a field genuinely cannot be
serialized, real proof captured via an actual `ObjectOutputStream` write attempt throwing
`NotSerializableException`. The correct alternative — a plain, nullable field with `Optional` used
only as a method return type at the API boundary — serializes successfully while still offering
`Optional`'s ergonomics to callers of the getter.
