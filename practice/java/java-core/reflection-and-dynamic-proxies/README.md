# Reflection and dynamic proxies (T-113) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/language-core/reflection-and-dynamic-proxies.md`](../../../../syllabus/02-java/language-core/reflection-and-dynamic-proxies.md)
(T-113). Three independent demos: a real measured cost comparison, a real dynamic-proxy
interception mechanism (the JDK AOP mechanism), and a real encapsulation-bypass demo that
surfaced an honest, unexpected nestmate-access finding along the way.

## Setup and run

```bash
cd practice/java/java-core/reflection-and-dynamic-proxies
mkdir -p out
javac -d out src/*.java
java -cp out ReflectionCostDemo
java -cp out DynamicProxyDemo
java -cp out EncapsulationBypassDemo
```

No special flags needed.

## Real observed output (last run)

### `ReflectionCostDemo` — real, correctness-verified cost comparison

```
Correctness: direct=49 reflection=49 methodHandle=49 (all match: true)

== Real measured wall-clock time, 200000000 calls ==
Direct call:        50ms
Method.invoke():     937ms (18.7x slower than direct)
MethodHandle.invoke(): 357ms (7.1x slower than direct)
```

All three call mechanisms are verified to produce the identical result before any timing claim.
Classic reflection (`Method.invoke()`) measured a real ~18.7x slower than a direct call across
200 million calls; `MethodHandle.invoke()` — the modern alternative built for `invokedynamic` —
measured a real ~7.1x slower than direct, but a real ~2.5-2.6x *faster* than classic reflection,
consistently across repeated runs.

### `DynamicProxyDemo` — real method interception, and a real interface-only constraint

```
proxy's real runtime class: $Proxy0
proxy instanceof UserService: true
proxy instanceof RealUserService: false
[proxy] before: findUser([42])
[proxy] after:  findUser returned "user-42" in 2010375ns
Caller received: user-42

== Real proof: java.lang.reflect.Proxy can ONLY proxy interfaces, not concrete classes ==
Proxying a concrete class threw real IllegalArgumentException: DynamicProxyDemo$RealUserService is not an interface
```

A real `InvocationHandler` intercepts every call to the proxy's interface methods, logging
before/after and delegating to the real object — exactly the mechanism behind Spring's
interface-based JDK AOP proxies. The proxy's real runtime class is a JVM-synthesized
`$Proxy0` class, genuinely `instanceof` the proxied interface but NOT the real implementation
class. Attempting to proxy a concrete class instead of an interface throws a real
`IllegalArgumentException` — the real, concrete reason frameworks like Spring fall back to
CGLIB/ByteBuddy (subclass-based proxying) for beans with no interface to proxy against.

### `EncapsulationBypassDemo` — real encapsulation bypass, and an honest, unexpected finding

```
Before reflection: BankAccount{accountId='ACC-001', balance=100.0}
Read balance without setAccessible: real IllegalAccessException, as expected
Real balance read via reflection: 100.0
After reflective mutation: BankAccount{accountId='ACC-001', balance=999999.0}
Real interest computed via reflective private-method call: 49999.950000000004

== Real field introspection (Jackson/Spring-style bean discovery) ==
field: balance type=double modifiers=private
field: accountId type=String modifiers=private final
```

`BankAccount` is deliberately a genuinely separate top-level class (not a nested class of the
demo) — an earlier draft used a nested `static class BankAccount` inside `EncapsulationBypassDemo`,
and reflective private-field access **succeeded even without calling `setAccessible(true)`**, an
unexpected real result. The cause: since Java 11 ([JEP 181](https://openjdk.org/jeps/181)), classes
compiled together as nested/inner classes of the same top-level class are **nestmates**, and
private access between nestmates is permitted by the JVM directly — reflection doesn't need to
bypass anything, because there was nothing to bypass. Moving `BankAccount` to its own top-level
file restored the expected, genuine encapsulation boundary: reflective access without
`setAccessible(true)` now really throws `IllegalAccessException`, and `setAccessible(true)` is
what genuinely bypasses it — the real mechanism this demo set out to show.
