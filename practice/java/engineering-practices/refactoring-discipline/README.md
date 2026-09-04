# Refactoring Discipline — Real, Executed Evidence

Evidence base for [Refactoring Discipline](../../../../syllabus/18-engineering-practices/refactoring-discipline.md) (T-1804). A real before/after Extract Method refactor, proven behavior-preserving by running both versions against identical inputs.

```bash
javac -d out src/*.java
java -cp out RefactoringParityTest
```

## Real captured output

```
  PASS  weight=0.5 region=domestic      express=false before=5.00 after=5.00
  PASS  weight=0.5 region=domestic      express=true  before=15.00 after=15.00
  PASS  weight=3.0 region=continental   express=false before=13.50 after=13.50
  PASS  weight=3.0 region=continental   express=true  before=38.50 after=38.50
  PASS  weight=20.0 region=international express=false before=45.00 after=45.00
  PASS  weight=20.0 region=international express=true  before=70.00 after=70.00
  PASS  weight=45.5 region=domestic      express=true  before=47.13 after=47.13
  PASS  weight=1.0 region=domestic      express=false before=5.00 after=5.00
  PASS  weight=5.0 region=continental   express=true  before=38.50 after=38.50
  PASS  weight=100.0 region=international express=false before=195.00 after=195.00
All 10 cases: before and after produce identical output.
```

## What this actually demonstrates

`ShippingCostBefore.java` is one long method mixing three unrelated concerns (weight-tier pricing, region multiplier, express surcharge) in nested conditionals. `ShippingCostAfter.java` reaches the identical output through three Extract Method refactors — no feature added, no bug fixed, purely a structure change.

`RefactoringParityTest.java` is the actual proof, not an assertion of good faith: it runs the exact same 10 cases through both versions and confirms every single one matches, byte-for-byte on the returned `double`. This is the real discipline a refactor requires — the test suite exists *before* the refactor starts, and passes *unmodified* after it. If the test needed editing to pass again, the change wasn't a pure refactor; it was a behavior change wearing a refactor's clothes.
