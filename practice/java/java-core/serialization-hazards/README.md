# Serialization hazards and alternatives (T-115) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/language-core/serialization-hazards-and-alternatives.md`](../../../../syllabus/02-java/language-core/serialization-hazards-and-alternatives.md)
(T-115). Three independent demos: a real, byte-level-tampered constructor-bypass proof (and its
real, verified fix), a real broken-then-fixed Singleton round trip, and the JDK's own current,
real defensive mechanism (`ObjectInputFilter`).

All demos are defensive-security educational content — they demonstrate the real, well-documented
mechanisms behind Java deserialization hazards and their real, standard JDK mitigations. None
build or distribute an exploit/gadget-chain toolchain.

## Setup and run

```bash
cd practice/java/java-core/serialization-hazards
mkdir -p out
javac -d out src/*.java
java -cp out ConstructorBypassDemo
java -cp out SingletonBreakDemo
java -cp out ObjectInputFilterDemo
```

No special flags needed.

## Real observed output (last run)

### `ConstructorBypassDemo` — a real, byte-level-tampered hazard, and its real, verified fix

```
new Account(-100) threw real IllegalArgumentException: balance cannot be negative: -100

Located the real serialized int bytes for balance=500 at stream offset 60
ObjectInputStream.readObject() on the tampered bytes produced: Account{balance=-999999}
<-- REAL: a balance the real constructor would have thrown IllegalArgumentException for.
No reflection was used to produce this object -- readObject() alone did it.

Deserializing the identically-tampered bytes against SecureAccount threw real InvalidObjectException:
balance cannot be negative: -999999  <-- REAL: the readObject() override genuinely re-applies the invariant
```

The constructor genuinely enforces its invariant on normal construction. But deserialization is a
real, entirely separate construction path: the demo locates the actual 4-byte big-endian
representation of the serialized `int` value inside a real serialized byte stream and overwrites
it in place — exactly what an attacker controlling the bytes over the wire could do — then calls
`ObjectInputStream.readObject()` directly. No reflection is used to produce the corrupted object;
the deserialization mechanism itself does it, because the constructor never runs on this path. A
`SecureAccount` variant that re-validates inside its own private `readObject()` method is then
proven, with the identical tampered-byte technique, to genuinely reject the same attack with a
real `InvalidObjectException`.

### `SingletonBreakDemo` — a real broken singleton, and its real, verified fix

```
BrokenSingleton.INSTANCE == deserialize(serialize(INSTANCE)): false
<-- REAL: false. Deserialization created a genuinely SECOND, distinct instance.

FixedSingleton.INSTANCE == deserialize(serialize(INSTANCE)): true
<-- REAL: true. readResolve() genuinely preserved the singleton guarantee.
```

A naive `Serializable` singleton (private constructor, static instance field) is genuinely broken
by a real serialize/deserialize round trip — `==` is real, verified `false`, meaning deserialization
produced an actual second instance despite the private constructor. Adding a `readResolve()` method
that returns the canonical instance is then proven, with the identical round-trip technique, to
genuinely restore `==` equality.

### `ObjectInputFilterDemo` — the JDK's own real, current defensive mechanism

```
Deserializing an ALLOWED class succeeded: safe

Deserializing a DISALLOWED class threw real InvalidClassException: filter status: REJECTED
<-- REAL: the object graph was never even reconstructed; the filter rejected it up front

ObjectInputFilter.Config.getSerialFilter() = null
```

`java.io.ObjectInputFilter` (JEP 290, standard since Java 9) genuinely rejects deserializing a
disallowed class *before* the object graph is reconstructed — real, verified `InvalidClassException`
for a class not on an explicit allow-list, with the allowed class deserializing normally. This
repo's own JVM has no process-wide default filter configured (`getSerialFilter() == null`), real,
direct confirmation that a production system accepting untrusted serialized data must configure
one explicitly (`-Djdk.serialFilter` or `ObjectInputFilter.Config.setSerialFilter`, per JEP 415) —
it isn't protected by default.
