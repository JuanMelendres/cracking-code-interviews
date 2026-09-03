# Lambdas and functional interfaces (T-108) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`syllabus/02-java/language-core/lambdas-and-functional-interfaces.md`](../../../syllabus/02-java/language-core/lambdas-and-functional-interfaces.md)
(T-108). Five independent pieces of evidence: real bytecode disassembly, two real compiler
errors (and their real fixes), and a real method-reference-kinds demo.

## Setup and run

```bash
cd practice/java/lambdas-and-functional-interfaces
mkdir -p out
# The two "Broken" files are INTENTIONALLY non-compiling -- they demonstrate real compiler errors.
javac -d out src/LambdaExample.java src/AnonymousExample.java src/CapturingFixed.java src/FunctionalInterfaceContractFixed.java src/MethodReferenceKindsDemo.java
java -cp out CapturingFixed
java -cp out FunctionalInterfaceContractFixed
java -cp out MethodReferenceKindsDemo

# Real bytecode disassembly (no special flags needed):
javap -c -p out/LambdaExample.class
javap -c -p out/AnonymousExample.class
javap -v out/LambdaExample.class | grep -A3 BootstrapMethods

# The real compiler errors (expected to fail):
javac -d out src/CapturingBroken.java
javac -d out src/FunctionalInterfaceContractBroken.java
```

## Real observed output (last run)

### Lambda vs. anonymous class: a real, on-disk `.class` file count difference

```
$ javac -d out src/LambdaExample.java src/AnonymousExample.java
$ ls out/
AnonymousExample$1.class
AnonymousExample.class
LambdaExample.class
```

The anonymous inner class produces a real, separate `AnonymousExample$1.class` file at compile
time. The lambda produces **no extra class file at all** — `LambdaExample.class` alone. The
lambda's implementation doesn't exist as a class on disk until the JVM synthesizes it at runtime.

### Lambda vs. anonymous class: real bytecode disassembly

```
=== LambdaExample javap -c -p ===
  public static void main(java.lang.String[]);
    Code:
       0: invokedynamic #7,  0              // InvokeDynamic #0:run:()Ljava/lang/Runnable;
       5: astore_1
       6: aload_1
       7: invokeinterface #11,  1           // InterfaceMethod java/lang/Runnable.run:()V
      12: return

  private static void lambda$main$0();
    Code:
       0: getstatic     #15                 // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #21                 // String lambda running
       5: invokevirtual #23                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return

=== AnonymousExample javap -c -p ===
  public static void main(java.lang.String[]);
    Code:
       0: new           #7                  // class AnonymousExample$1
       3: dup
       4: invokespecial #9                  // Method AnonymousExample$1."<init>":()V
       7: astore_1
       8: aload_1
       9: invokeinterface #10,  1           // InterfaceMethod java/lang/Runnable.run:()V
      14: return
```

The lambda compiles to a real `invokedynamic` instruction pointing at a synthetic private method
(`lambda$main$0`) generated in the *same* class — the actual `Runnable` implementation is created
at runtime by a bootstrap method, not written out as a `.class` file by `javac`. The anonymous
class compiles to a completely ordinary `new` + `invokespecial` object construction of the real,
separately-compiled `AnonymousExample$1` class.

### The real bootstrap method: `LambdaMetafactory`

```
BootstrapMethods:
  0: #43 REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
```

The real, direct proof of *how* the lambda's class is actually produced: `invokedynamic`'s
bootstrap method is `java.lang.invoke.LambdaMetafactory.metafactory`, which generates the
`Runnable` implementation class at the call site's first execution, using `MethodHandle`s — not
at compile time.

### Effectively-final capture: a real compiler error, and two real fixes

```
$ javac -d out src/CapturingBroken.java
src/CapturingBroken.java:7: error: local variables referenced from a lambda expression must be final or effectively final
            count++; // ILLEGAL: captured local is not effectively final
            ^
src/CapturingBroken.java:8: error: local variables referenced from a lambda expression must be final or effectively final
            return count;
                   ^
2 errors
```

```
$ java -cp out CapturingFixed
Fix 1 (AtomicInteger box): 1, 2, 3
Fix 2 (static field mutation, no restriction at all): 3
Effectively-final (never reassigned) local, no error: captured-once-used-in-lambda
```

The restriction is real and specific: a captured *local variable* must never be reassigned after
capture (the lambda captures its **value**, copied at capture time, not a live reference to its
storage slot). Boxing the mutable state in an object (`AtomicInteger`) works because the captured
local — the *reference* to the box — never changes, only the object it points to. A **field**
(instance or static) has no such restriction at all, because the lambda reads it through `this`
(or the class) at call time, never snapshotting its value.

### Single-abstract-method (SAM) contract: a real compiler error, and the real fix

```
$ javac -d out src/FunctionalInterfaceContractBroken.java
src/FunctionalInterfaceContractBroken.java:3: error: Unexpected @FunctionalInterface annotation
    @FunctionalInterface
    ^
  TwoAbstractMethods is not a functional interface
    multiple non-overriding abstract methods found in interface TwoAbstractMethods
1 error
```

```
$ java -cp out FunctionalInterfaceContractFixed
lambda satisfies the single abstract method
default method -- does not count toward SAM
static method -- does not count toward SAM either
```

`@FunctionalInterface` is not merely documentation — `javac` really enforces the single-abstract-
method constraint and fails compilation on a second one. Default and static methods, real and
compiling, do **not** count toward that constraint at all.

### The four kinds of method references, real output

```
1. Static:            Integer::parseInt("42") = 42  (lambda equivalent: 42)
2. Bound instance:    greeting::length() = 11  (lambda equivalent: 11)
3. Unbound instance:  String::length("unbound") = 7  (lambda equivalent: 7)
4. Constructor:       StringBuilder::new("built") = "built"  (lambda equivalent: "built")
Unbound, two-arg:     "a".equals("a") via String::equals = true
Sorted via method reference (String::compareTo): [alpha, bravo, charlie]
```

Every method-reference kind produces the identical result to its explicit lambda equivalent —
real, side-by-side proof that a method reference is purely syntactic sugar for a specific lambda
shape, not a different runtime mechanism.
