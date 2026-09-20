---
title: "Cheat Sheet: Vector API and SIMD Performance"
slug: vector-api-and-simd-performance
document_type: cheat-sheet
domain: 16-performance-jvm
topic_id: T-2418
canonical: ../syllabus/16-performance-jvm/vector-api-and-simd-performance.md
last_updated: 2026-09-20
---

# Vector API and SIMD Performance

**Canonical chapter:** [`syllabus/16-performance-jvm/vector-api-and-simd-performance.md`](../syllabus/16-performance-jvm/vector-api-and-simd-performance.md)

## Core Mental Model

The real question isn't "scalar vs. SIMD" — it's "does the JIT's automatic vectorization already cover this
loop, or does this workload need something the auto-vectorizer can't safely do?" HotSpot's SuperWord
optimization already vectorizes many simple, regular loops on its own.

## Essential Definitions

- **SIMD** — one CPU instruction processes multiple data lanes at once, using wide vector registers.
- **Vector API (`jdk.incubator.vector`)** — Java's explicit mechanism for requesting SIMD; still incubating as of JDK 21 (JEP 460, 6th round).
- **SuperWord** — HotSpot's own automatic loop-vectorization optimization (`-XX:+UseSuperWord`, on by default).

## Decision Table

| Situation | What to reach for |
|---|---|
| Simple, regular array loop (no complex branches, unit stride) | Trust JIT auto-vectorization; measure before adding Vector API complexity |
| Irregular access, cross-lane ops, complex control flow | Evaluate the Vector API directly |
| Comparing scalar vs. Vector API correctness | Use `Math.fma()` on the scalar side to match the Vector API's single-rounding `fma()` |
| Deciding whether a loop needs explicit vectorization | Measure with and without `-XX:-UseSuperWord` first |

## Key Numbers (real, executed — OpenJDK 21.0.12, Apple M4, 128-bit NEON)

```
Element-wise FMA, 1M floats, 2000 passes:
  Default settings (SuperWord ON):  Vector API ~0.98x vs. scalar -- essential parity
  -XX:-UseSuperWord (true scalar):  Vector API ~2.2x vs. scalar -- real, isolated advantage
```

## Common Pitfalls

- Assuming the Vector API is the only path to SIMD — HotSpot already auto-vectorizes many simple loops.
- Adopting the Vector API without first measuring whether auto-vectorization already covers the loop.
- Treating a bit-level output mismatch between `fma()` and `a*b+a` as a bug rather than a real, expected single-vs-double-rounding difference.

## Interview Answer Skeleton

**30-sec:** HotSpot already auto-vectorizes many simple loops via SuperWord. Measured directly: the Vector
API shows essential parity (0.98x) under default settings, and a real ~2.2x advantage only once
auto-vectorization is disabled to isolate it.

**2-min:** Add the mechanism (SuperWord vs. explicit Vector API) + both real measured numbers + the FMA
rounding correctness detail.

**Staff-level framing:** weigh the Vector API's real, measured benefit against its incubating-module status
(real API-stability risk across JDK releases) and added complexity before production adoption.

## Production Warning Signs

- A team adopts the Vector API expecting a big win and measures almost none.
- **Diagnosis:** the loop was likely already auto-vectorized by HotSpot. Verify with `-XX:-UseSuperWord`.

## Related

- `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md`
- `syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md`
