---
title: "Flashcards: ZGC and Shenandoah: Concurrent Collection"
slug: zgc-and-shenandoah-concurrent-collection
document_type: flashcard-deck
domain: jvm
topic_id: T-305
canonical: ../handbook/jvm/zgc-and-shenandoah-concurrent-collection.md
last_updated: 2026-08-06
---

# Flashcards: ZGC and Shenandoah: Concurrent Collection

**Canonical chapter:** [`syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md`](../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md)

## Card: The core architectural difference from G1

**Prompt:**
What's the core architectural difference between ZGC/Shenandoah and G1?

**Answer:**
Concurrent relocation — the expensive evacuation work runs alongside application threads via a reference-remapping mechanism, rather than during a stop-the-world pause.

**Why it matters:**
The precise mechanism behind ZGC/Shenandoah's dramatically lower pause times compared to G1.

**Common trap:**
Attributing ZGC/Shenandoah's benefit to "better tuning" rather than the specific concurrent-relocation architecture.

**Related:**
[handbook/jvm/zgc-and-shenandoah-concurrent-collection.md](../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md)

## Card: The real cost a concurrent collector can still impose

**Prompt:**
What real cost can a concurrent collector impose even when its individual GC pauses are excellent?

**Answer:**
Allocation stalls — application threads waiting for reclamation to catch up when the collector's background work can't keep pace with the allocation rate.

**Why it matters:**
Prevents treating a low-pause-time collector as a free lunch with no possible latency cost.

**Common trap:**
Assuming excellent measured pause times mean the collector has no remaining latency risk under any allocation rate.

**Related:**
[handbook/jvm/zgc-and-shenandoah-concurrent-collection.md](../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md)

## Card: Why migrating to ZGC/Shenandoah needs more heap headroom

**Prompt:**
Why does migrating to ZGC/Shenandoah typically require more heap headroom than a comparable G1 deployment?

**Answer:**
To give the concurrent reclamation work enough room to keep pace with the application's allocation rate and avoid allocation stalls — G1's evacuation-pause model has different headroom needs.

**Why it matters:**
The specific reason a straight collector swap with unchanged heap sizing risks trading GC pauses for allocation stalls.

**Common trap:**
Migrating to ZGC/Shenandoah while keeping the existing G1-era heap sizing unchanged.

**Related:**
[handbook/jvm/zgc-and-shenandoah-concurrent-collection.md](../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md)
