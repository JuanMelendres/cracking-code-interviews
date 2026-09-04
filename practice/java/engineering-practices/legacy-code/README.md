# Legacy Code — Real, Executed Evidence

Evidence base for [Working with Legacy Code](../../../../syllabus/18-engineering-practices/working-with-legacy-code.md) (T-1803). Demonstrates characterization testing on a small, deliberately untested "legacy" class.

```bash
javac -d out src/LegacyOrderPricer.java src/Explore.java src/CharacterizationTest.java
java -cp out Explore              # step 1: probe actual behavior
java -cp out CharacterizationTest # step 2: pin it down as a real test
```

## Step 1 — probe, don't assume

`Explore.java`'s real output across a spread of inputs, captured before any test existed:

```
price(qty=9, unitPrice=19.99) = 179.91
price(qty=10, unitPrice=19.99) = 179.91
price(qty=11, unitPrice=19.99) = 197.9
```

**A real, surprising finding, not injected for effect**: at this unit price, `qty=9` and `qty=10` produce the *identical* price — the 10% bulk discount at the threshold exactly cancels the cost of the tenth unit, making it effectively free. Nobody documented this; it fell out of running the actual code against real inputs.

## Step 2 — pin it down

`CharacterizationTest.java` turns every one of these real, observed values into an assertion — including the discount-cliff finding, explicitly labeled as *current behavior being locked in*, not a bug being endorsed:

```
  PASS  no discount below threshold -> 179.91
  PASS  discount applies at threshold -> 179.91
  PASS  discount applies above threshold -> 197.9
  PASS  qty=9 and qty=10 produce the identical price at this unit price (real discount-cliff behavior) -> 179.91
  PASS  small unit price, no discount -> 0.09
  PASS  small unit price, discount applies -> 0.09
  PASS  single unit, no discount possible -> 1000.0
All characterization assertions passed -- current behavior is now pinned.
```

This is the entire point of a characterization test: it says nothing about whether the discount-cliff behavior is *correct* — only that it's *real*, and that any future refactor changing it must do so as a deliberate, visible decision, not an accident.
