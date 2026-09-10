# SOLID Principles — Real Demo

Backs [`syllabus/04-software-design/solid-principles.md`](../../../syllabus/04-software-design/solid-principles.md) (T-1701).

Pure JDK, no dependencies. One violation and one fix per principle, with real,
observable evidence of the difference — never just an assertion that the fix
is "better design."

## Run it

```bash
mkdir -p out
javac -d out src/SolidPrinciplesDemo.java
java -cp out SolidPrinciplesDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **SRP** — reflection over each class's own declared methods shows one
  class carrying three unrelated concerns versus three classes carrying one each.
- **OCP** — a new `Triangle` shape is added and correctly included in a total-area
  calculation without a single line of `AreaCalculatorFixed` changing (verifiable
  directly from the file: zero `Triangle` references exist above `AreaCalculatorFixed`'s
  own definition).
- **LSP** — `SquareLsp extends Rectangle` breaks a real invariant test
  (`setWidth(5); setHeight(4)` should give area 20; `SquareLsp` gives 16).
- **ISP** — a fat interface forces a real `UnsupportedOperationException`;
  the fixed version has no `eat()` method to call at all.
- **DIP** — the identical `OrderServiceFixed` class is run twice with two
  different injected repository implementations, with zero changes to its source.
