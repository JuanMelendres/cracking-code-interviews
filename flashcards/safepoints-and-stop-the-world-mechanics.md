---
title: "Flashcards: Safepoints and Stop-the-World Mechanics"
slug: safepoints-and-stop-the-world-mechanics
document_type: flashcard-deck
domain: jvm
topic_id: T-310
canonical: ../handbook/jvm/safepoints-and-stop-the-world-mechanics.md
last_updated: 2026-08-06
---

# Flashcards: Safepoints and Stop-the-World Mechanics

**Canonical chapter:** [`syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md`](../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md)

## Card: Is every stop-the-world pause a GC pause

**Prompt:**
Is every stop-the-world pause a GC pause?

**Answer:**
No — GC is the most common safepoint operation in practice, but thread dumps, deoptimization, class redefinition, and other operations use the identical safepoint mechanism.

**Why it matters:**
Explains why a real latency blip can appear with nothing in the GC log at that timestamp.

**Common trap:**
Assuming any unexplained stop-the-world pause must be GC-related and searching only GC logs for it.

**Related:**
[handbook/jvm/safepoints-and-stop-the-world-mechanics.md](../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md)

## Card: Reaching safepoint vs at safepoint cost

**Prompt:**
What's the difference between "reaching safepoint" and "at safepoint" cost?

**Answer:**
Reaching safepoint depends on what each thread is doing when the request comes in (thread-dependent); at safepoint is the actual requested operation's own execution cost (operation-dependent).

**Why it matters:**
Separates two genuinely different cost sources that both contribute to total observed pause time.

**Common trap:**
Treating total safepoint pause time as a single undifferentiated number rather than two distinct cost components.

**Related:**
[handbook/jvm/safepoints-and-stop-the-world-mechanics.md](../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md)

## Card: The measured cost gap between a deadlock check and a full GC

**Prompt:**
What real cost gap did this chapter measure between a deadlock check and a full GC's "at safepoint" phase?

**Answer:**
Roughly 1,500x — ~1 microsecond for `FindDeadlocks` versus ~1.59 milliseconds for `G1CollectFull`, from the same real run.

**Why it matters:**
A concrete, measured illustration of how widely "at safepoint" cost varies by operation type.

**Common trap:**
Assuming all safepoint operations carry roughly comparable execution cost.

**Related:**
[handbook/jvm/safepoints-and-stop-the-world-mechanics.md](../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md)
