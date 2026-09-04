# Number Representation — Real, Executed Evidence

Evidence base for [Number Representation](../../../../syllabus/01-computer-science-foundations/number-representation.md) (T-2003). One demo, five sections, all real output from OpenJDK 21.0.12.

```bash
javac -d out src/NumberRepresentationDemo.java
java -cp out NumberRepresentationDemo
```

## 1. Two's complement bit patterns

```
             0 -> 00000000000000000000000000000000
             1 -> 00000000000000000000000000000001
            -1 -> 11111111111111111111111111111111
             2 -> 00000000000000000000000000000010
            -2 -> 11111111111111111111111111111110
    2147483647 -> 01111111111111111111111111111111
   -2147483648 -> 10000000000000000000000000000000
```

`-1` is all 32 bits set. `Integer.MIN_VALUE` is a single `1` bit followed by all zeros — the one negative number whose absolute value has no positive two's-complement representation (Section 9 covers exactly this edge case).

## 2. Integer overflow: silent wraparound

```
  Integer.MAX_VALUE       = 2147483647
  Integer.MAX_VALUE + 1   = -2147483648
  Integer.MAX_VALUE + 2   = -2147483647
  Integer.MIN_VALUE       = -2147483648
  Integer.MIN_VALUE - 1   = 2147483647
  Math.addExact(MAX_VALUE, 1) threw: integer overflow
```

Plain `+` never throws on overflow — it wraps around silently, by design (Section 4). `Math.addExact` is the one standard-library escape hatch that turns the same overflow into a real, catchable `ArithmeticException`.

## 3. Narrowing cast truncation

```
  (byte) 200  = -56   (200 does not fit in a signed 8-bit byte, range -128..127)
  (short) 65578 = 42   (low 16 bits kept, high bits silently dropped)
  (int) 4294967303 = 7   (low 32 bits kept)
```

A narrowing primitive cast keeps the low-order bits and discards the rest — no rounding, no clamping to the target type's range, no exception.

## 4. Floating point precision loss — and a real surprise this demo caught

```
  0.1 + 0.2            = 0.30000000000000004
  0.1 + 0.2 == 0.3 ?   false
  Exact difference      = 5.551115123125783E-17
  0.1 via printf %.20f  = 0.10000000000000000000   (misleading -- see below)
  0.1 via new BigDecimal(double) = 0.1000000000000000055511151231257827021181583404541015625   (the real, exact binary value)
```

The first draft of this demo assumed `System.out.printf("%.20f", 0.1)` would reveal `double`'s true stored value beyond its normal 17-significant-digit display. Running it showed otherwise: Java's `%f` formatter pads the *shortest round-trip decimal representation* (`"0.1"`) with zeros rather than expanding the actual binary fraction — it does **not** show the real stored value at all, and printing it that way would have been a fabricated claim. `new BigDecimal(double)` is the construct that genuinely does show the exact binary value, and it's the one used in the chapter. This is exactly the kind of thing this repository's real-evidence discipline exists to catch before it ships as a false claim.

## 5. Accumulated rounding error: `float` vs. `double`

```
  iterations            = 10000000
  expected (mathematical) = 1000000.0
  float  accumulated sum  = 1087937.0   (error: -87937.0)
  double accumulated sum  = 999999.9998389754   (error: 1.610246254131198E-4)
```

Summing `0.1` ten million times: `float`'s error is roughly **8.8%** of the expected total — large enough to be visibly wrong, not a rounding footnote. `double`'s error is about **0.000016%** — six orders of magnitude smaller, from having roughly twice the mantissa bits to absorb the same per-addition rounding error. Neither is exact; the gap between them is the real, measured argument for why `double` is Java's default floating-point type and `float` needs a specific reason (memory density, hardware constraints) to choose instead.
