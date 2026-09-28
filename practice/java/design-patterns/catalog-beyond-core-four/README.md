# Design Patterns Catalog Beyond the Core Four — Real Demos

Backs [`syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md`](../../../../syllabus/04-software-design/design-patterns-catalog-beyond-the-core-four.md) (T-2432).

Companion to [`../applied-gof/`](../applied-gof/), which covers Strategy, Builder, Decorator, and Singleton.

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/CreationalDemo.java src/StructuralDemo.java src/BehavioralDemo.java
java -cp out CreationalDemo
java -cp out StructuralDemo
java -cp out BehavioralDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

**`CreationalDemo.java` — Factory Method, Abstract Factory**

- A factory method selecting between two payment providers, where the call site never names a concrete class, and an unknown key produces a real `IllegalArgumentException`.
- An abstract factory producing a *matched pair* (tax calculator + invoice formatter) per region, run for DE (19% VAT, German wording) and US (8.75% sales tax, dollar wording) — the point being that a mismatched pair is structurally unrepresentable.

**`StructuralDemo.java` — Adapter, Proxy, Facade**

- An adapter translating a legacy SDK's `Object[]` return into a typed `GeoLookup` result, confining the unpacking to one class.
- A **real JDK dynamic proxy** (`java.lang.reflect.Proxy`) adding retries to a deterministically flaky client: 2 failures then success on attempt 3, with the real runtime proxy class name (`$Proxy0`) printed, and the implementation containing no retry code. A second run with `maxAttempts=2` against 5 failures shows the last error genuinely propagating.
- A facade reducing a four-service checkout to one call, with the real computed receipt.

**`BehavioralDemo.java` — Observer, Command, Chain of Responsibility, Template Method**

- Observer happy path, then **two real failure modes**:
  - A throwing listener aborts dispatch, so a later subscriber never runs — registration order silently decides who survives an unrelated failure. The hardened version contains the failure and reports it.
  - **An unplanned, genuine finding:** a listener that unsubscribes itself during dispatch produced **no `ConcurrentModificationException` at all**. Removing the second-of-three element left the `ArrayList` iterator's cursor equal to the new size, so `hasNext()` returned `false` before the `modCount` check could fire — and the third listener was *silently skipped*. That is strictly worse than the exception most people expect, and it is the real, captured behavior on this JDK. The `CopyOnWriteArrayList` variant runs every listener with no exception.
- Command with a working undo stack, run down to empty.
- A four-handler chain showing that handler *order* is the design: an unauthenticated oversized request is rejected `401`, never `413`.
- Template Method with a `final` skeleton method: one subclass overrides the optional hook, neither can reorder or skip a step.
