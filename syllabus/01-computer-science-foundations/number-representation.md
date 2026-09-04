---
title: "Number Representation"
slug: number-representation
document_type: syllabus-topic
domain: 01-computer-science-foundations
topic_id: T-2003
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - how-a-computer-executes-a-program.md
related:
  - how-a-computer-executes-a-program.md
practice: ../../practice/java/cs-foundations/number-representation/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references:
  - https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.2
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html#addExact(int,int)
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html
---

# Number Representation

[How a Computer Executes a Program](how-a-computer-executes-a-program.md) treats a "variable" as a location in memory or a register a specific instruction reads and writes. This topic answers the question that leaves open: *what, exactly, is stored there* when that variable is a number? The answer is not "the number" in any abstract mathematical sense — it's a fixed number of bits, interpreted according to one of a small number of encoding schemes, each with its own specific, learnable failure modes.

## 1. Why This Matters

Every one of Java's numeric types has a fixed size and a specific encoding, and both facts leak into observable behavior: `int` arithmetic overflows silently instead of throwing, `float` and `double` cannot represent most decimal fractions exactly, and a narrowing cast truncates rather than rounding or failing loudly. An engineer who doesn't know this reads `0.1 + 0.2 != 0.3` as a bug in Java; an engineer who does reads it as the direct, predictable consequence of binary floating point, and knows exactly which type (`BigDecimal`, scaled integers) to reach for when exactness actually matters — a decision with real financial and correctness consequences in any system that touches money, and a question asked constantly, in some form, across technical interviews at every level.

## 2. Prerequisites

[How a Computer Executes a Program](how-a-computer-executes-a-program.md) — specifically, that a variable is ultimately bits in memory or a register, not an abstract mathematical value. This topic is the direct continuation of that idea, applied specifically to numbers.

## 3. Foundation (L1)

**A computer stores every number as a fixed-length sequence of bits — 0s and 1s — and the same sequence of bits can mean different numbers depending on how it's interpreted.** There is no way to look at `01000001` sitting in memory and know what number it represents without also knowing the encoding rule being applied to it: as an 8-bit unsigned integer, it's 65; interpreted as a signed integer, still 65 (the top bit is 0, meaning positive); as an ASCII character, it's the letter `A`. The bits never change — only the agreed-upon rule for reading them does.

**For whole numbers, the encoding almost universally used today is called two's complement.** Its central, memorable property: to represent a negative number, you don't just flip a "sign bit" and store the positive value next to it (that's a different, older, less common scheme called sign-and-magnitude) — you invert every bit of the positive value and add 1. This sounds arbitrary until you see the payoff in Section 4: it makes addition and subtraction work identically for positive and negative numbers, using the exact same hardware circuit, with no special-case logic needed anywhere.

**For numbers with a fractional part, computers use a scheme called floating point**, modeled loosely on scientific notation (`1.23 × 10^4`) but in binary. The key, unavoidable consequence, worth internalizing at the Foundation level before any code: **most decimal fractions — including ordinary numbers like `0.1` — cannot be represented exactly in binary floating point**, for the same structural reason that `1/3` cannot be written exactly as a finite decimal. This isn't a Java bug, a rounding mistake, or something a "better" floating-point implementation would fix — it's an inherent property of representing base-10 fractions in base 2, true in every language and every runtime that uses this encoding (which is nearly all of them).

## 4. Core Concepts (L2)

**Two's complement's payoff: the CPU's adder circuit does subtraction using the exact same hardware as addition**, with no separate "subtract" circuit needed. `a - b` is computed as `a + (-b)`, and `-b` in two's complement is just "invert the bits of `b` and add 1" — a cheap, uniform operation. This is why every one of Java's integer types (`byte`, `short`, `int`, `long`) uses two's complement: it isn't a language design choice so much as an inheritance from how virtually all modern CPU arithmetic units are actually built.

**Every Java integer type has a fixed number of bits, and therefore a fixed, hard range** — `int` is 32 bits (`-2,147,483,648` to `2,147,483,647`), `long` is 64 bits, `byte` is 8, `short` is 16. There is no arbitrary-precision integer built into the language's primitive types (`java.math.BigInteger` exists specifically because primitives can't do this) — every primitive integer computation can overflow, and Java's `+`, `-`, and `*` operators do not check for it. Section 5 covers exactly what happens when they don't.

**IEEE 754 is the specific floating-point standard `float` (32-bit) and `double` (64-bit) implement**, encoding a number as a sign bit, an exponent, and a mantissa (the significant digits) — genuinely scientific notation in binary. The mantissa has a fixed, finite number of bits (23 for `float`, 52 for `double`), which is the direct cause of the imprecision named in Section 3: a value like `0.1` requires an infinitely repeating binary fraction to represent exactly (analogous to how `1/3` repeats forever in decimal), and the mantissa has to truncate it somewhere. `double`'s roughly 52 mantissa bits versus `float`'s 23 is why `double`'s truncation error is many orders of magnitude smaller — not zero, but smaller, exactly as the practice demo's accumulated-sum measurement shows directly.

**A narrowing conversion (assigning or casting a wider numeric type into a narrower one) is defined by the Java Language Specification as bit-truncation, not rounding or clamping.** `(byte) 200` doesn't fail, doesn't clamp to `127` (the max a `byte` can hold), and doesn't round — it keeps `200`'s low 8 bits and discards the rest, which happens to land on a negative number (`-56`) because the discarded high bit changes what the remaining bits mean under two's complement.

## 5. How It Works Internally (L3)

**Two's complement, mechanically: to negate a value, invert every bit, then add 1.** Take `2` (`00000010` in 8 bits): inverting gives `11111101`; adding 1 gives `11111110`, which is `-2`. Check it the other direction: adding `2` (`00000010`) and `-2` (`11111110`) using ordinary binary addition, ignoring any carry-out past the 8th bit, gives exactly `00000000` — zero, as expected — with no special-case subtraction logic anywhere in that computation. This exact mechanism is what the practice demo's bit-pattern output shows directly: `-1` is all 1 bits, and `Integer.MIN_VALUE` is a lone leading `1` followed entirely by zeros — the one value in two's complement whose bit pattern has no positive counterpart, because negating it (invert, add 1) produces the exact same bit pattern back, which is precisely why `Math.abs(Integer.MIN_VALUE)` returns `Integer.MIN_VALUE` itself, still negative, rather than the mathematically correct positive value (Section 9).

**IEEE 754 addition is not simple binary addition the way integer addition is.** Adding two floating-point numbers requires first aligning their exponents (shifting the smaller-magnitude number's mantissa right until both numbers share the same exponent), then adding the aligned mantissas, then re-normalizing and rounding the result back into the fixed mantissa width. That alignment-and-rounding step is exactly where precision is lost, and it happens on *every single floating-point addition*, not just ones that happen to look imprecise — the practice demo's float-accumulation measurement (summing `0.1f` ten million times, landing at `1,087,937` instead of `1,000,000`, roughly 8.8% off) is that per-operation rounding error compounding across ten million additions, made visible at scale rather than lost in one addition's tiny, individually-invisible error.

**`java.math.BigDecimal` exists specifically to sidestep this entire category of error, by representing a number as an arbitrary-precision integer plus a scale (the position of the decimal point), not as a binary fraction at all.** This is why `new BigDecimal(0.1)` (constructing from a `double`) reveals the ugly, exact binary value the `double` actually holds — `0.1000000000000000055511151231257827021181583404541015625`, captured directly in the practice demo's output — while `new BigDecimal("0.1")` (constructing from the *string* `"0.1"`, parsed digit-by-digit) gives the exact decimal value `0.1` with no error at all. The difference between those two constructors is a genuinely common, real source of confusion: the string constructor sidesteps binary floating point entirely; the `double` constructor inherits whatever imprecision the `double` already had before `BigDecimal` ever saw it.

## 6. Practical Usage

- **Never compare `float` or `double` values with `==` when the values came from arithmetic**, for exactly the reason Section 3/5 describe — use a small epsilon tolerance (`Math.abs(a - b) < 0.0001`) or, for money and other exact-decimal domains, avoid binary floating point entirely.
- **Use `BigDecimal`, constructed from a `String` (not a `double`), for any value where exactness matters** — money is the canonical case, but any domain with a legal or contractual requirement for exact decimal arithmetic applies equally.
- **Use `Math.addExact`, `Math.multiplyExact`, and their siblings** wherever silent integer overflow would be a correctness bug rather than an intentional wraparound (like a hash-code computation, where wraparound is expected and harmless) — they throw `ArithmeticException` instead of wrapping silently, exactly as the practice demo shows.

## 7. Examples

```java
// Two's complement bit pattern (why -1 is "all bits set")
System.out.println(Integer.toBinaryString(-1));
// 11111111111111111111111111111111

// Silent overflow -- no exception, no warning
int max = Integer.MAX_VALUE;
System.out.println(max + 1); // -2147483648

// The checked alternative
Math.addExact(Integer.MAX_VALUE, 1); // throws ArithmeticException

// Narrowing cast truncates, does not clamp or round
byte b = (byte) 200; // -56

// The classic floating-point surprise
System.out.println(0.1 + 0.2 == 0.3); // false

// The right way to get an exact 0.1
BigDecimal exact = new BigDecimal("0.1");          // exactly 0.1
BigDecimal inexact = new BigDecimal(0.1);           // 0.1000000000000000055511151231257827021181583404541015625
```

Real, executed output for every line above is captured in [`practice/java/cs-foundations/number-representation/`](../../practice/java/cs-foundations/number-representation/) — see Section 10.

## 8. Common Mistakes

- **Assuming `float`/`double` imprecision is a Java-specific bug.** It's IEEE 754, used by nearly every mainstream language — the same `0.1 + 0.2 != 0.3` surprise reproduces identically in Python, JavaScript, C, and C++.
- **"Fixing" a floating-point precision bug by rounding the *display* rather than changing the *representation*.** Formatting `0.30000000000000004` as `"0.30"` for display hides the symptom without addressing the actual risk: the underlying inexact value is still what gets used in any further arithmetic (comparisons, accumulation, persistence) downstream.
- **Constructing `BigDecimal` from a `double` when the source value should have been exact from the start.** `new BigDecimal(0.1)` doesn't "fix" `0.1`'s imprecision — it faithfully preserves whatever imprecise binary value the `double` already held before `BigDecimal` was ever involved. The fix is avoiding the `double` step entirely: parse from a `String`, or from `BigDecimal.valueOf(double)` (which round-trips through `Double.toString` first, giving the "expected" decimal rather than the raw binary value — a real, useful difference from the plain constructor, but still not a substitute for never having gone through a `double` in the first place if exactness matters upstream).
- **Assuming a narrowing cast will throw, clamp, or round.** It silently truncates bits, as Section 4 and the practice demo's output both show directly — code that relies on a cast to validate a range (`(byte) userInput`) is not doing validation at all.

## 9. Edge Cases

- **`Integer.MIN_VALUE` has no positive two's-complement counterpart.** `Math.abs(Integer.MIN_VALUE)` returns `Integer.MIN_VALUE` itself — still negative — because negating it (invert all bits, add 1) produces the identical bit pattern back, an overflow in its own right that most engineers never expect from calling `Math.abs`.
- **`NaN` (Not a Number) is a real, specific bit pattern IEEE 754 defines**, produced by operations like `0.0 / 0.0`, and it has the singular, surprising property that `NaN == NaN` is `false` — the only IEEE 754 value that is never equal to itself, by the standard's own definition. `Double.isNaN(x)`, not `x == Double.NaN`, is the correct way to test for it.
- **`+0.0` and `-0.0` are distinct bit patterns that compare equal with `==`** but are distinguishable through other means (`Double.compare`, `1.0 / 0.0` vs. `1.0 / -0.0`, which produce `Infinity` and `-Infinity` respectively) — a genuinely obscure edge case, rarely load-bearing, but a real one IEEE 754 defines deliberately.

## 10. Performance Implications

Real, executed output from `practice/java/cs-foundations/number-representation/` (OpenJDK 21.0.12):

Summing `0.1` ten million times (`iterations = 10,000,000`, expected mathematical total `1,000,000.0`):

| Type | Accumulated result | Error | Error as % of expected |
|---|---|---|---|
| `float` | `1,087,937.0` | `-87,937.0` | ~8.79% |
| `double` | `999,999.9998389754` | `0.0001610246` | ~0.000016% |

This isn't a performance-speed measurement — both types are fast — it's a **precision** measurement, and the magnitude of the gap (roughly six orders of magnitude between `float`'s and `double`'s accumulated error) is the concrete, quantified argument for why `double` is Java's default floating-point type, and why choosing `float` needs an actual reason (halving memory footprint at scale, matching a specific external format) rather than being a default.

The demo also caught a real methodological mistake before it shipped: an initial attempt to reveal `0.1`'s exact stored binary value via `System.out.printf("%.20f", 0.1)` produced `"0.10000000000000000000"` — misleadingly implying `0.1` is stored exactly. It isn't; `printf`'s `%f` formatter pads the *shortest round-trip decimal string* with zeros rather than expanding the true binary fraction. `new BigDecimal(0.1)` is what actually reveals the real stored value (`0.1000000000000000055511151231257827021181583404541015625`), and is the version that ended up in Section 7's examples.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| `int`/`long` (fixed-width two's complement) | Fast, hardware-native arithmetic | Silent overflow at a fixed range boundary; no built-in overflow detection unless `Math.*Exact` is used explicitly |
| `BigInteger` (arbitrary precision) | No overflow, ever, at any magnitude | Significant memory and CPU overhead per operation versus a primitive; not usable where a primitive is required (array indices, most APIs) |
| `float`/`double` (IEEE 754) | Fast, hardware-native, compact; adequate for the overwhelming majority of scientific/graphics/engineering use cases | Cannot represent most decimal fractions exactly; accumulated error grows with the number of operations |
| `BigDecimal` (arbitrary-precision decimal) | Exact decimal arithmetic — the correct choice for money | Much slower than a primitive `double`; more verbose API (no operator overloading — `add()`, `multiply()`, not `+`, `*`) |

## 12. Senior-Level Considerations (L3)

The Senior-level judgment call is choosing the right numeric type for a domain's actual correctness requirements, not defaulting to `double` for anything with a decimal point. Money, quantities with a contractual or legal precision requirement, and any value that will be compared for exact equality after arithmetic are the concrete signal for `BigDecimal` (constructed from a `String`, per Section 8); ordinary scientific, statistical, or graphics computation, where small relative error is expected and tolerated, is exactly what `double` is designed for and performs well at. Equally important: recognizing that `Math.addExact` and friends exist and reaching for them at any integer boundary where silent overflow would be a real correctness bug rather than intentional (deliberately wrapping) behavior — a code-review-level habit, not a rare specialist technique.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, a numeric-representation choice made early in a system's life is a decision that gets baked into a schema, a wire format, or a public API — and becomes expensive to change once real data and real integrations depend on it. Two real, well-documented historical incidents make the stakes concrete rather than abstract:

**Ariane 5 Flight 501 (1996)** failed 37 seconds after launch when a 64-bit floating-point value representing the rocket's horizontal velocity was converted to a 16-bit signed integer, and the value exceeded what 16 bits can hold — the exact narrowing-conversion failure mode Section 4 and Section 8 describe, at the scale of a real, uninsured $370 million payload. The conversion code had been correctly bounds-checked for the Ariane 4's flight profile; Ariane 5's genuinely faster horizontal velocity exceeded that old, silently-inherited assumption, and the resulting overflow (specifically, an unhandled exception the overflow triggered) cascaded into a self-destruct.

**The Patriot missile failure at Dhahran (1991)** stemmed from a floating-point *representation* problem, not an overflow: the system tracked time in tenths of a second using a 24-bit fixed-point register, and `0.1` — exactly like this topic's own `0.1 + 0.2 != 0.3` demonstration — cannot be represented exactly in binary. After roughly 100 hours of continuous operation, the accumulated rounding error reached about a third of a second, enough of a targeting-calculation error that an incoming Scud missile was not intercepted.

The Staff-level lesson from both: **a numeric representation decision is a scaling and reuse-across-context assumption**, exactly analogous to [Algorithmic Complexity](algorithmic-complexity-and-big-o-from-first-principles.md)'s Staff-level point about complexity classes (Section 13 there) — code correctly bounds-checked or precision-tested for one context can silently stop being safe in a new one (new hardware, longer uptime, a wider input range) without any code change at all, and the failure often surfaces far from the line that's actually wrong.

## 14. Production Scenarios

This topic references two real, publicly documented historical engineering failures rather than a fictionalized `production-cookbook/` incident, since no existing entry in this repository's cookbook covers a numeric-representation root cause, and these two cases are authoritative, well-investigated, and precisely on-topic:

- **Ariane 5 Flight 501 (1996)** — a 64-bit-float-to-16-bit-integer narrowing conversion overflow, Section 13.
- **Patriot missile failure, Dhahran (1991)** — accumulated fixed-point rounding error from a value (`0.1` seconds) with no exact binary representation, Section 13.

> Planned reference: a future, original `production-cookbook/` entry covering a numeric-representation incident (e.g., a monetary rounding discrepancy from `double`-based price arithmetic) would be a natural, non-duplicative addition to that deliverable's own backlog, separate from this chapter.

## 15. Interview Questions

### Question 1 — Why does `0.1 + 0.2 == 0.3` evaluate to `false` in Java?

**Why interviewers ask it.** It's a fast, concrete check for whether a candidate actually understands binary floating point, versus having only memorized "floating point is imprecise" as an unexplained fact.

**Expected answer.** `double` (and `float`) store numbers in IEEE 754 binary floating point, which — like decimal's inability to write `1/3` as a finite decimal — cannot represent most decimal fractions, including `0.1` and `0.2`, exactly. Both are stored as the closest representable binary approximation; adding those two approximations produces a result (`0.30000000000000004`) that is close to, but not bit-for-bit identical to, the closest binary approximation of `0.3`.

**Minimum acceptable answer.** Knows floating point is imprecise and that comparing with `==` is risky, even without explaining the binary-fraction mechanism.

**Strong Senior answer.** Explains the binary-fraction mechanism directly (the decimal-to-binary analogy in Section 3), and names the correct fix depending on domain: an epsilon-tolerant comparison for general floating-point work, `BigDecimal` (from a `String`) for money or any exact-decimal requirement.

**Staff-level extension.** Cites a concrete, real-world consequence of getting this wrong at scale (the Patriot missile case, Section 13) and connects it to the general principle: a representation choice validated for one range of inputs or one duration of operation is not automatically safe for a different range or a longer runtime.

**Common mistakes.** Attributing this to "Java's floating point being buggy" rather than recognizing it as inherent to IEEE 754, reproduced identically in nearly every mainstream language.

**Follow-up questions.** "How would you compare two `double` values for practical equality?" (An epsilon-tolerant comparison, sized to the domain's actual precision needs.) "Why doesn't `BigDecimal` have this problem?" (It represents numbers as an arbitrary-precision integer plus a decimal scale, not a binary fraction — Section 5.)

### Question 2 — What happens when you add 1 to `Integer.MAX_VALUE` in Java?

**Why interviewers ask it.** It tests whether a candidate knows Java integers have a fixed size and wrap silently on overflow, rather than assuming a runtime exception or an automatic promotion to a bigger type (both of which happen in some other languages or dynamically-typed contexts).

**Expected answer.** It silently wraps around to `Integer.MIN_VALUE` (`-2,147,483,648`), because `int` is a fixed 32-bit two's-complement value with no automatic widening and no built-in overflow check — `+` does ordinary two's-complement addition and simply doesn't detect that the mathematically correct result no longer fits.

**Minimum acceptable answer.** Knows it doesn't throw and doesn't stay `2,147,483,648` — some kind of wraparound happens, even if the exact resulting value isn't immediately recalled.

**Strong Senior answer.** States the exact resulting value and can explain *why* using the two's-complement bit-pattern mechanism (Section 5) — adding 1 to `01111111...1` flips every bit to `1000...0`, which is `Integer.MIN_VALUE` under this encoding.

**Staff-level extension.** Connects this to Ariane 5 (Section 13) as the canonical real-world stakes example, and names `Math.addExact`/`long`/`BigInteger` as the concrete, standard-library mitigations available depending on whether the fix needed is "detect it," "give it more headroom," or "remove the ceiling entirely."

**Common mistakes.** Assuming Java automatically promotes `int` arithmetic to `long` on overflow (it doesn't — the programmer must explicitly choose a wider type or a checked operation) or assuming an exception is thrown by default (it isn't, unless `Math.addExact` or an equivalent checked API is used explicitly).

**Follow-up questions.** "Does the same thing happen with `long`?" (Yes — same two's-complement mechanism, just at 64 bits instead of 32, so the range is far larger but still finite.) "How would you detect this before it happens, in code you control?" (`Math.addExact`, or pre-checking `a > Integer.MAX_VALUE - b` before adding.)

## 16. Coding/Practice Exercises

- Run [`NumberRepresentationDemo.java`](../../practice/java/cs-foundations/number-representation/src/NumberRepresentationDemo.java) yourself and, before reading each section's output, predict what `(short) 65578` and `(int) 4294967303` will print, then check your prediction against Section 4's truncation rule.
- Write a method that adds two `int` values and returns a `boolean` indicating whether the addition would overflow, without using `Math.addExact` — derive the overflow condition from the two operands' and the result's sign bits (a classic, genuinely instructive two's-complement exercise).
- Reproduce the `float`-vs-`double` accumulated-error measurement (Section 10) at a few different iteration counts (100, 10,000, 10,000,000) and confirm the error grows with the number of operations, not with the iteration count alone — checking whether the growth looks linear or something else.

## 17. Debugging Exercises

**Symptom:** a financial reconciliation report occasionally shows a total that's off by a fraction of a cent from the sum of its individual line items, only for specific combinations of amounts, and only after many transactions have accumulated.

**Diagnose:** check whether the amounts are stored and summed as `double` rather than `BigDecimal` or a scaled integer (cents as a `long`) — this exact symptom shape (small, seemingly random discrepancies that grow with the number of operations) is the direct, real signature of binary floating-point representation error (Section 3, Section 10) rather than a logic bug in the summation code itself. Confirm by reproducing the discrepancy with the actual amounts involved in a small, isolated test, and check whether switching the same computation to `BigDecimal` (constructed from `String` representations of the same amounts) eliminates it — if it does, that's a direct confirmation of the root cause, not a coincidence.

## 18. Design Exercises

**Design constraint:** you're designing the data model for a payments system that must never lose or gain a fraction of a cent across any sequence of additions, subtractions, or currency conversions, no matter how many operations accumulate over the system's lifetime.

Design the representation choice for monetary amounts end to end: what type stores an amount (and why `double` is disqualified by Section 3/10's own evidence), whether to store amounts as a scaled integer (e.g., cents as a `long`) or as `BigDecimal`, and what each choice costs in exchange (Section 11) — specifically, what happens at a currency-conversion step where a genuinely irrational-in-decimal exchange rate is involved, and where, if anywhere, *some* rounding becomes unavoidable and needs an explicit, deliberate policy (round-half-up, banker's rounding) rather than whatever a language's default happens to do.

## 19. Further Reading

- *IEEE Standard for Floating-Point Arithmetic (IEEE 754)* — the formal standard `float` and `double` implement.
- *The Java Language Specification, SE 21*, [§4.2 — Primitive Types and Values](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.2) — the authoritative definition of Java's integer and floating-point types, their ranges, and conversion rules.
- [`Math.addExact`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html#addExact(int,int)) and its siblings — official documentation for the checked-arithmetic methods referenced in Sections 6, 12, and 15.
- [`java.math.BigDecimal`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html) — official documentation, including the documented difference between its `double` and `String` constructors referenced in Section 8.
- Ariane 5 Flight 501 Inquiry Board Report (1996) and the U.S. GAO report on the Patriot missile failure at Dhahran (1992) — the primary investigation reports behind Section 13's two historical incidents.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, why `0.1 + 0.2 != 0.3` in Java, and state that fixed-width integers have a hard maximum value | [Section 3](#3-foundation-l1) |
| L2 | Name Java's integer encoding (two's complement) and floating-point encoding (IEEE 754), and explain why a narrowing cast truncates rather than rounds or clamps | [Interview Question 2](#question-2--what-happens-when-you-add-1-to-integermax_value-in-java) |
| L3 | Derive a two's-complement negation by hand (invert and add 1) and explain, mechanistically, why IEEE 754 addition requires exponent alignment and re-rounding on every operation | [Section 10's real measurements](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Connect a numeric-representation choice to a real, historical, high-stakes failure (Section 13), and design a monetary data model that avoids the entire failure class rather than patching individual symptoms (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
