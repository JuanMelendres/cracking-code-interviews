---
title: "Flashcards: ThreadLocal-Mediated Classloader Leaks"
slug: threadlocal-mediated-classloader-leaks
document_type: flashcard-deck
domain: concurrency
topic_id: T-413
canonical: ../handbook/concurrency/threadlocal-mediated-classloader-leaks.md
last_updated: 2026-09-01
---

# Flashcards: ThreadLocal-Mediated Classloader Leaks

**Canonical chapter:** [`syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md`](../syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md)

## Card: Why does one leaked ThreadLocal value sometimes leak an entire classloader?

**Prompt:**
A `ThreadLocal` leaks a single object instance. Under what condition does
this leak an entire classloader, not just that object?

**Answer:**
When the leaked object's class was loaded by an application-specific
classloader (not the system classloader). Every object strongly references
its own `Class`, and every `Class` strongly references its defining
`ClassLoader` — so the one leaked instance is enough to keep the whole
classloader, and everything it loaded, reachable. Measured directly with a
real `WeakReference` remaining non-null after a forced GC.

**Why it matters:**
It's the real, structural reason this specific leak class can be far more
severe than "one stale object."

**Common trap:**
Assuming a `ThreadLocal` leak's impact is always limited to the single
leaked object.

**Related:**
[handbook/concurrency/threadlocal-mediated-classloader-leaks.md](../syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md), [Scoped Values and ThreadLocal Migration](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md)

## Card: What's the real-world symptom of this specific leak?

**Prompt:**
What production symptom is the classic real-world signature of a
`ThreadLocal`-mediated classloader leak?

**Answer:**
Metaspace (not heap) usage growing measurably after each redeploy of the
same application, never fully returning to baseline — because each
redeploy's classloader, kept alive by a leaked `ThreadLocal` entry, never
actually gets collected.

**Why it matters:**
It's a specific, diagnosable signal pointing directly at this mechanism,
distinct from a generic heap-based object leak.

**Common trap:**
Diagnosing Metaspace growth after redeploys as an ordinary memory leak
without checking for multiple, stale `ClassLoader` instances.

**Related:**
[handbook/concurrency/threadlocal-mediated-classloader-leaks.md](../syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md)
