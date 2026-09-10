# MethodHandle and java.lang.invoke — Real Demo

Backs [`syllabus/02-java/concurrency/methodhandle-and-invoke.md`](../../../../syllabus/02-java/concurrency/methodhandle-and-invoke.md) (T-2405).

Pure JDK, no dependencies. Real `javac`/`java`/`javap` output — no
simplification of `MethodHandle` lookup, invocation, or combinator
semantics, and a real bytecode disassembly proving the `invokedynamic`
mechanism behind lambda expressions.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **`findStatic`/`findConstructor`/`findVirtual` obtain real, typed
  `MethodHandle`s**, and `bindTo()` produces a real new handle with the
  receiver removed from its `MethodType` — printed directly:
  `(Counter,int)int` before `bindTo`, `(int)int` after.
- **`invokeExact()` enforces the handle's exact `MethodType`; `invoke()`
  adapts automatically.** Calling `invokeExact` with a return type that
  doesn't exactly match throws a real `WrongMethodTypeException`; the
  identical mismatched call through `invoke()` succeeds via automatic
  `asType` conversion.
- **Combinators build new handles from existing ones without touching
  original source.** `MethodHandles.filterReturnValue` pipes one handle's
  result through another; `MethodHandles.dropArguments` adapts a handle to
  accept and ignore extra leading arguments — both proven with a real,
  printed resulting `MethodType` and a real invocation result.
- **A lambda expression compiles to a real `invokedynamic` instruction
  bootstrapped by `LambdaMetafactory.metafactory`, not a synthetic
  class.** `javap -v` on the compiled lambda shows the real bootstrap
  method entry referencing `LambdaMetafactory` and a real, synthesized
  `lambda$main$0` method handle — and the compiled output contains no
  `LambdaBytecode$1.class`. The identical behavior written as an anonymous
  inner class instead produces a real `AnonClassBytecode$1.class` at
  compile time — the concrete, verifiable difference behind "lambdas don't
  generate a class per call site the way anonymous inner classes do."
