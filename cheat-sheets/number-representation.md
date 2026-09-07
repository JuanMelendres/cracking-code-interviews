---
title: "Cheat Sheet: Number Representation"
slug: number-representation
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2003
canonical: ../syllabus/01-computer-science-foundations/number-representation.md
last_updated: 2026-09-06
---

# Number Representation

**Canonical chapter:** [`syllabus/01-computer-science-foundations/number-representation.md`](../syllabus/01-computer-science-foundations/number-representation.md)

## Core Mental Model

A computer stores every number as a fixed-length sequence of bits — never "the number" in an abstract mathematical sense — and the same bit pattern means different numbers depending entirely on the encoding rule applied to it.

## Essential Definitions

- **Two's complement** — Java's integer encoding; to negate a value, invert every bit and add 1. This lets a CPU's adder circuit do subtraction using the exact same hardware as addition (`a - b` = `a + (-b)`), with no separate subtract circuit.
- **Fixed range, silent overflow** — every Java integer type has a hard range (`int`: -2,147,483,648 to 2,147,483,647); `+`, `-`, `*` do not check for overflow and wrap silently.
- **IEEE 754** — the standard `float` (32-bit, 23 mantissa bits) and `double` (64-bit, 52 mantissa bits) implement: sign bit, exponent, mantissa — binary scientific notation. The finite mantissa is why most decimal fractions (like `0.1`) can't be represented exactly, the same structural reason `1/3` has no finite decimal.
- **Narrowing conversion** — defined by the JLS as bit-truncation, not rounding or clamping: `(byte) 200` keeps the low 8 bits and discards the rest, landing on `-56`.
- **`BigDecimal`** — represents a number as an arbitrary-precision integer plus a scale (decimal-point position), not a binary fraction — sidesteps IEEE 754 imprecision entirely, but only when constructed from a `String`.

## Decision Table

| Type | Encoding | Gains | Risk |
|---|---|---|---|
| `int`/`long` | Two's complement, fixed width | Fast, hardware-native | Silent overflow at a fixed boundary, no built-in detection |
| `BigInteger` | Arbitrary precision | No overflow, ever | Memory/CPU overhead; not usable where a primitive is required |
| `float`/`double` | IEEE 754 | Fast, compact, hardware-native | Cannot represent most decimal fractions exactly; error accumulates |
| `BigDecimal` (from `String`) | Integer + scale | Exact decimal arithmetic | Much slower; no operator overloading (`add()`, not `+`) |

## Common Pitfalls

- Assuming `float`/`double` imprecision is a Java bug — it's IEEE 754, reproduced identically in Python, JavaScript, C, and C++.
- "Fixing" a floating-point precision issue by rounding the *display* rather than the *representation* — the underlying inexact value still drives any further arithmetic.
- Constructing `BigDecimal` from a `double` (`new BigDecimal(0.1)`) when the source value should have been exact from the start — it faithfully preserves the `double`'s existing imprecision rather than fixing it; parse from a `String` instead.
- Assuming a narrowing cast throws, clamps, or rounds — it silently truncates bits, so `(byte) userInput` is not range validation.
- `Math.abs(Integer.MIN_VALUE)` returns `Integer.MIN_VALUE` itself, still negative — negating it produces the identical bit pattern back, an overflow most engineers don't expect from `abs`.

## Interview Answer Skeleton

**30-sec:** `double`/`float` store IEEE 754 binary floating point, which can't represent most decimal fractions exactly — the same reason `1/3` has no finite decimal, just in base 2 instead of base 10. `0.1 + 0.2 == 0.3` is `false` because both operands are stored as the closest binary approximation, and adding those approximations doesn't land bit-for-bit on `0.3`'s own approximation. Use `BigDecimal` from a `String` when exactness matters, like money.

**2-min:** Add the two's-complement mechanism for integers (invert + add 1, giving subtraction for free from addition hardware), the silent-overflow behavior of `+`/`-`/`*` versus `Math.addExact` throwing `ArithmeticException`, and the narrowing-cast truncation rule (`(byte) 200` → `-56`, not a clamp).

**Staff-level framing:** A numeric representation choice made early gets baked into a schema, wire format, or public API and is expensive to change once real data depends on it — code correctly bounds-checked or precision-tested for one context (Ariane 4's velocity range, one uptime duration) can silently stop being safe in a new one (Ariane 5's faster velocity, 100+ hours of Patriot uptime) with no code change at all, and the failure often surfaces far from the actually-wrong line.

## Production Warning Signs

- Ariane 5 Flight 501 (1996): a 64-bit float horizontal-velocity value narrowed to a 16-bit signed integer overflowed — bounds-checking that was valid for Ariane 4's flight profile silently stopped holding for Ariane 5's genuinely faster velocity, cascading into a self-destruct and a $370 million loss.
- Patriot missile failure, Dhahran (1991): time tracked in tenths of a second in a 24-bit fixed-point register; `0.1` cannot be represented exactly in binary, and after ~100 hours of continuous operation the accumulated rounding error reached about a third of a second — enough to miss an intercept.
- Symptom: a financial reconciliation report is occasionally off by a fraction of a cent, only for specific amount combinations, only after many transactions accumulate. Diagnose: check whether amounts are summed as `double` rather than `BigDecimal`/scaled `long`; confirm by reproducing with the actual amounts and checking whether switching to `BigDecimal` (from `String`) eliminates the discrepancy.

## Real Measured Numbers

- Summing `0.1` ten million times (OpenJDK 21.0.12): `float` accumulates to `1,087,937.0` (error ~8.79% of the expected `1,000,000.0`); `double` accumulates to `999,999.9998389754` (error ~0.000016%) — roughly six orders of magnitude difference in accumulated error.
- `new BigDecimal(0.1)` reveals the true stored binary value: `0.1000000000000000055511151231257827021181583404541015625`; `new BigDecimal("0.1")` gives exactly `0.1`.

## Related

- syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md
