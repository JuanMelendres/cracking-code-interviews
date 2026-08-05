---
title: "Cheat Sheet: Object Layout, Headers, and Compressed Oops"
slug: object-layout-headers-and-compressed-oops
document_type: cheat-sheet
domain: jvm
topic_id: T-302
canonical: ../handbook/jvm/object-layout-headers-and-compressed-oops.md
last_updated: 2026-08-05
---

# Object Layout, Headers, and Compressed Oops

**Canonical chapter:** [`handbook/jvm/object-layout-headers-and-compressed-oops.md`](../handbook/jvm/object-layout-headers-and-compressed-oops.md)

## Core Mental Model

Every Java object is, physically, a small fixed-size header followed by its instance fields, padded to an 8-byte alignment boundary — there's no such thing as an object that's "just its fields." Compressed oops is a clever trick for making reference fields cheaper: since Java objects are always aligned, the low bits of any address are always zero and don't need storing, letting a 32-bit compressed reference address up to 32GB of heap by implicitly multiplying by 8, instead of needing a full 64-bit pointer.

## Essential Definitions

- **Object header** — fixed-size metadata every object carries regardless of fields: on 64-bit HotSpot, typically 12 bytes (mark word + compressed class pointer) with compressed class pointers enabled, or 16 bytes without.
- **Compressed oops** — a default-enabled (heaps under ~32GB) HotSpot optimization representing references in 32 bits instead of 64, by exploiting alignment guarantees.
- **The addressability ceiling** — roughly 32GB heap; past it, 32 bits genuinely can't address the whole heap even with the alignment trick, and the JVM silently falls back to full 64-bit references.

## Decision Table

| Component | Compressed oops active (default) | Compressed oops off / above ~32GB |
|---|---|---|
| Object header | 12 bytes | 16 bytes |
| Reference field | 4 bytes | 8 bytes |
| Compressed oops ceiling | ~32GB heap | N/A |

**Trade-offs:** compressed oops provides a substantial, real memory-footprint reduction for a negligible per-dereference decode cost — essentially free under the ~32GB ceiling. Above it, the trade-off disappears entirely; every reference field's cost silently doubles.

## Key Numbers (real, executed — `CompressedOopsFootprintDemo.java`, 5 million nodes, identical object graph)

```
Compressed oops ON (default):  heap used 134 MB, ~28 bytes/node
Compressed oops OFF:            heap used 191 MB, ~40 bytes/node
```

A real ~42% memory-footprint increase purely from the pointer-representation flag, with no change to the actual data stored. The per-node gap (12 bytes) is consistent with the header staying fixed size while the single reference field's cost doubles from 4 to 8 bytes.

## Common Pitfalls

- Estimating an object's memory footprint by summing only declared field sizes, omitting the fixed header cost every object carries.
- Assuming a reference field always costs 8 bytes (or always 4) without checking whether compressed oops is actually active for the specific heap size.
- Treating "increase the heap past 32GB" as a purely additive capacity decision for a reference-heavy workload, missing the real structural cost of losing compressed oops.

## Interview Answer Skeleton

**30-sec:** Every Java object carries a fixed header (12-16 bytes) plus its declared fields, and reference fields cost 4 bytes with compressed oops active (the default, under ~32GB) or 8 bytes without — compressed oops exploits object-alignment guarantees to halve reference cost at negligible decode overhead.

**2-min:** Add why it exists (without it, every reference field on a 64-bit JVM costs twice as much, hurting memory footprint and cache pressure for reference-heavy data) + the real measured evidence (134MB vs. 191MB for an identical 5-million-node graph, ~42% difference) + the trade-off (past ~32GB, compressed oops isn't addressable-range-sufficient and the JVM silently falls back to 64-bit references).

**Whiteboard:** A small rectangle labeled "Header (12-16 bytes)" followed by boxes for each declared field, reference fields labeled "4 bytes (compressed)" or "8 bytes (uncompressed)." A second diagram: a linked structure of many such objects with a shaded portion showing cumulative header + reference overhead as a real fraction of total memory.

**Staff-level framing:** factor compressed oops' addressability ceiling into heap-scaling capacity decisions for reference-heavy workloads specifically — "how much heap" has a real, structural cost discontinuity around ~32GB, not a smoothly additive one. Default to measuring real per-object footprint rather than a theoretical field-size sum.

## Production Warning Signs

- A memory-footprint estimate for a reference-heavy data structure (built purely from summing declared field types) undershoots real production usage significantly — the gap is header and reference-field overhead the naive calculation omitted; for small objects this overhead can be the majority of the real footprint.
- A team scales heap past roughly 32GB and observes a counter-intuitive memory-footprint regression for the same logical dataset — confirm whether compressed oops is still active at the new heap size via `-XX:+PrintFlagsFinal`'s `UseCompressedOops` value; crossing the ceiling silently doubles every reference field's cost.
- **Prevention:** measure real per-object memory via a before/after `Runtime` delta or a dedicated object-sizing tool rather than a theoretical field-size sum; explicitly check the compressed-oops ceiling before any heap-size increase for a reference-heavy workload.

## Related

- `handbook/jvm/jvm-memory-layout-and-runtime-regions.md`
- `handbook/concurrency/java-memory-model-and-volatile.md`
