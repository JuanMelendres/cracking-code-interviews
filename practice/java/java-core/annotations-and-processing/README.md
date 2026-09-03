# Annotations and annotation processing (T-112) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/java-core/annotations-and-annotation-processing.md`](../../../../syllabus/02-java/language-core/annotations-and-annotation-processing.md)
(T-112). Three independent demos plus real `javap` bytecode disassembly: retention-policy
lifetimes, a real reflective mini-ORM built purely from annotations, and the `@Inherited`
interfaces-don't-count gotcha.

## Setup and run

```bash
cd practice/java/java-core/annotations-and-processing
mkdir -p out
javac -d out src/*.java
java -cp out RetentionPolicyDemo
java -cp out ReflectiveProcessingDemo
java -cp out InheritedGotchaDemo

# Real bytecode disassembly (no special flags needed):
javap -v -p 'out/RetentionPolicyDemo$Annotated.class' | grep -B1 -A3 "Annotations:"
javap -v -p 'out/RetentionPolicyDemo$Annotated.class' | grep -A6 "sourceOnlyMethod"
```

## Real observed output (last run)

### `RetentionPolicyDemo` — real, different lifetimes for the three retention policies

```
sourceOnlyMethod: getAnnotations().length = 0  <-- invisible via reflection at runtime
classOnlyMethod: getAnnotations().length = 0  <-- invisible via reflection at runtime
runtimeVisibleMethod: getAnnotations().length = 1  <-- REAL: @RetentionPolicyDemo.RuntimeVisible()
```

Reflection at runtime sees only the `RUNTIME`-retention annotation — both `SOURCE` and `CLASS`
retention are invisible via `getAnnotations()`, confirming the Javadoc's own claim directly rather
than assuming it.

### Real `javap` disassembly — proof of exactly *why* each retention behaves as observed

```
$ javap -v -p 'out/RetentionPolicyDemo$Annotated.class' | grep -B1 -A3 "Annotations:"
    RuntimeInvisibleAnnotations:
      0: #14()
        RetentionPolicyDemo$ClassOnly
    RuntimeVisibleAnnotations:
      0: #17()
        RetentionPolicyDemo$RuntimeVisible

$ javap -v -p 'out/RetentionPolicyDemo$Annotated.class' | grep -A6 "sourceOnlyMethod"
  void sourceOnlyMethod();
    descriptor: ()V
    flags: (0x0000)
    Code:
      stack=0, locals=1, args_size=1
         0: return
      LineNumberTable:
```

Real, direct proof of the mechanism behind each retention policy: `CLASS`-retention's annotation
is genuinely present in the `.class` file — in the `RuntimeInvisibleAnnotations` attribute, which
reflection never reads at runtime. `RUNTIME`-retention's annotation is in `RuntimeVisibleAnnotations`,
exactly matching the reflective result above. `sourceOnlyMethod`'s bytecode carries **no** annotation
attribute at all — `SOURCE` retention means `javac` discards the annotation entirely after
compilation; it never reaches the `.class` file in any form.

### `ReflectiveProcessingDemo` — a real, working mini-ORM built purely from annotations + reflection

```
field "id" -> real @Column("user_id") = 42
field "name" -> real @Column("full_name") = Ada Lovelace
field "internalCache": no @Column, real EXCLUDED from mapping

Generated SQL: INSERT INTO users (user_id, full_name) VALUES (?, ?)
Real bound values, in order: [42, Ada Lovelace]
```

A real, minimal demonstration of exactly how JPA's `@Column` or Jackson's `@JsonProperty` actually
work under the hood: reflection scans a class's declared fields, reads each `@Column` annotation's
`value()`, and dynamically builds real behavior (here, a generated SQL `INSERT` statement and its
bound values) purely from what it discovers at runtime — no code generation, no annotation
processor, just reflection combined with runtime-retained annotations.

### `InheritedGotchaDemo` — the real `@Inherited` limitation: superclasses only, never interfaces

```
SubClass.class.isAnnotationPresent(InheritedClassAnnotation.class) = true  <-- REAL: inherited from BaseClass via extends

ImplementingClass.class.isAnnotationPresent(InheritedMarker.class) = false  <-- REAL: false. @Inherited is documented to apply ONLY to superclasses, never interfaces

MarkedInterface.class.isAnnotationPresent(InheritedMarker.class) = true  (the interface itself has it -- ImplementingClass just doesn't inherit it)
```

`@Inherited` genuinely works when a class extends an annotated superclass — real, verified `true`.
But it genuinely does **not** propagate through interface implementation, even when the interface's
own annotation is itself marked `@Inherited` — real, verified `false`. The interface itself still
carries its own annotation (`true`), it simply doesn't propagate to implementing classes — a real,
easy-to-miss limitation stated in `@Inherited`'s own Javadoc and confirmed here directly.
