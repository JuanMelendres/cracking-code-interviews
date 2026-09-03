# ClassLoaders and class initialization (T-114) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/java-core/classloaders-and-class-initialization.md`](../../../../syllabus/02-java/language-core/classloaders-and-class-initialization.md)
(T-114). Three independent demos: the real classloader hierarchy and delegation model,
the classic "same class, two loaders" identity gotcha with a real `ClassCastException`,
and the real JLS "active use" initialization-trigger rules.

## Setup and run

```bash
cd practice/java/java-core/classloaders-and-class-initialization
mkdir -p out
javac -d out src/*.java
java -cp out HierarchyAndDelegationDemo
java -cp out SameClassTwoLoadersDemo
java -cp out InitializationTriggersDemo
```

No special flags needed.

## Real observed output (last run)

### `HierarchyAndDelegationDemo` — real hierarchy and parent-first delegation

```
String.class.getClassLoader():        null  <-- REAL null: bootstrap classloader is native code, not a Java object
This class's getClassLoader():         jdk.internal.loader.ClassLoaders$AppClassLoader@2c854dc5

  depth 0: jdk.internal.loader.ClassLoaders$AppClassLoader@2c854dc5
  depth 1: jdk.internal.loader.ClassLoaders$PlatformClassLoader@3764951d
  depth 2: null (bootstrap -- the real root of every delegation chain)

Class.forName("java.lang.String", true, appLoader) == String.class: true  <-- real proof of parent-first delegation
```

The real, current (JDK 9+) three-tier hierarchy — application → platform → bootstrap — walked
directly via `getParent()`. `String.class.getClassLoader()` really is `null`: the bootstrap
classloader is implemented in native code, not represented as a real `ClassLoader` Java object.
Explicitly asking the application classloader to load `java.lang.String` still returns the
identical `Class` object bootstrap already loaded — real, direct proof of parent-first delegation.

### `SameClassTwoLoadersDemo` — the classic classloader identity gotcha, with a real `ClassCastException`

```
Widget.class == isolatedWidgetClass: false  <-- REAL: two genuinely distinct Class objects for the identical class name/bytecode
isolatedWidget instanceof Widget: false  <-- REAL: false, despite being the 'same' class by name and source

Real ClassCastException: class Widget cannot be cast to class Widget (Widget is in unnamed module of loader SameClassTwoLoadersDemo$IsolatedClassLoader @1d44bcfa; Widget is in unnamed module of loader 'app')

Calling label() via reflection on the isolated instance still works fine: real-widget
```

The identical `Widget.class` bytecode is loaded twice — once normally by the app classloader,
once by a custom `IsolatedClassLoader` that defines its own copy instead of delegating. The result
is two real, genuinely distinct `Class` objects: `==` is `false`, `instanceof` is `false`, and
attempting to cast one to the other throws a real `ClassCastException` — with the JVM's own,
genuinely confusing real error message: `"class Widget cannot be cast to class Widget"`,
disambiguated only by each `Widget`'s defining loader. A class's real identity is the pair
`(fully-qualified name, defining ClassLoader)`, not the name alone.

### `InitializationTriggersDemo` — real JLS "active use" initialization triggers

```
== Trigger 1: merely referencing a class in a type declaration -- does NOT initialize ==
  (nothing printed)

== Trigger 2: Class.forName() with initialize=false -- does NOT initialize ==
  (nothing printed)

== Trigger 3: accessing a compile-time CONSTANT static final field -- does NOT initialize ==
  (nothing printed)

== Trigger 4: accessing a NON-constant static field -- DOES initialize, real output below ==
  [HasNonConstantStatic] static initializer RAN

== Trigger 5: constructing an instance -- DOES initialize (if not already) ==
  [HasCompileTimeConstant] static initializer RAN
```

Every predicted trigger/non-trigger from the JLS's "active use" rules was verified directly by
watching for real static-initializer output at each step, not assumed. Referencing a class as a
type, calling `Class.forName(..., initialize=false, ...)`, and reading a genuine compile-time
constant (`javac`-inlined at every call site) all produced zero initializer output. Reading a
non-constant static field and constructing an instance both really triggered initialization —
and, notably, `HasCompileTimeConstant`'s initializer did NOT run when its constant field was read
earlier, only later when it was actually constructed — real, direct proof that a compile-time
constant reference never counts as an active use, even for the exact same class.
