# Java Platform Module System (JPMS) — Real Demo

Backs [`syllabus/02-java/language-core/java-platform-module-system.md`](../../../../syllabus/02-java/language-core/java-platform-module-system.md) (T-116).

Pure JDK, no dependencies. Two independent real module graphs (`src-a`,
`src-b`), each compiled and run with real `javac`/`java` module-path
tooling — no simplification of the module resolution or strong-encapsulation
rules.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).
The script is self-contained: it temporarily edits `src-a`'s module
descriptor and `src-b`'s `Main.java` to reproduce each "before" state, then
restores both files — the checked-in source always reflects the final,
correct state.

## What it proves

- **Strong encapsulation blocks reflection by default, even with `exports`.**
  `com.example.modA` exports `com.example.modA` but does not open it.
  `com.example.modB`'s `ReflectiveProbe` can compile against `Secret`
  (export covers normal compile-time/link-time access) but a real
  `Field.setAccessible(true)` call at runtime throws a real
  `InaccessibleObjectException` — `exports` and `opens` are genuinely
  different grants, not two names for the same thing.
- **Adding a qualified `opens ... to ...` fixes exactly that, and only
  that.** The identical reflective call succeeds once `modA`'s
  `module-info.java` adds `opens com.example.modA to com.example.modB;` —
  a real, minimal, module-scoped grant instead of the classpath's
  all-or-nothing reflective access.
- **`ServiceLoader` resolves a provider module the consumer never
  `requires`.** `com.example.svc.app` declares only `uses
  com.example.svc.api.Greeter;` — no `requires com.example.svc.provider;`
  anywhere in its module descriptor — yet `ServiceLoader.load(Greeter.class)`
  finds and instantiates `com.example.svc.provider.internal.EnglishGreeter`
  at runtime. This is real JPMS service binding: the module system resolves
  a provider module into the graph specifically because a resolved module
  declares `uses` for a service type the provider `provides`, independent
  of any `requires` edge.
- **That same provider package is still genuinely inaccessible for a
  normal `import`.** Directly importing
  `com.example.svc.provider.internal.EnglishGreeter` from the app module
  fails at compile time with a real, verbatim javac error — the module
  system's special-cased service-provider loading does not create a
  general encapsulation hole; `ServiceLoader` can reach the class, but
  ordinary compiled code still cannot.
