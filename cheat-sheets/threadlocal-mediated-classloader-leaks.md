---
title: "Cheat Sheet: ThreadLocal-Mediated Classloader Leaks"
slug: threadlocal-mediated-classloader-leaks
document_type: cheat-sheet
domain: concurrency
topic_id: T-413
canonical: ../handbook/concurrency/threadlocal-mediated-classloader-leaks.md
last_updated: 2026-09-01
---

# ThreadLocal-Mediated Classloader Leaks

**Canonical chapter:** [`syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md`](../syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md)

## Core Mental Model

A classloader is not just a loading mechanism — it's part of a class's *identity*, and every instance of that class carries a permanent, structural reference back to it. A classloader is the root of its own small object graph: everything it loaded, plus every instance of everything it loaded, plus everything reachable from those instances, all trace back to it. Normally, when an application is undeployed, that whole graph — classloader included — becomes garbage in one clean sweep. But if even one object from that graph is still reachable from *outside* it — say, sitting in a `ThreadLocalMap` entry on a thread that outlives the deploy — the entire graph stays reachable too.

## Essential Definitions

- **Classloader leak** — a classloader (and every class/instance it defined) remains reachable from a GC root after the application should have been fully undeployed, because some external reference into its object graph was never released.
- **The chain** — pooled thread → `ThreadLocalMap` entry → leaked value → its `Class` → its defining `ClassLoader`.
- **`ThreadLocalMap`'s value reference is strong, not weak** — only the `ThreadLocal` key is held weakly; only an explicit `remove()` breaks the chain.
- **Structurally distinct, more severe consequence** than the general `ThreadLocal`-in-a-thread-pool leak: that one leaks a single object; this one leaks an entire classloader and everything it loaded.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Long-lived pooled threads + dynamically loaded/unloaded classes (servlet container, plugin system)? | Audit every `ThreadLocal.set()` call site for a guaranteed matching `remove()` |
| Is the stored value an instance of a class loaded by an application-specific (not system) classloader? | Treat as a real classloader-leak risk, not just a stale-value risk |
| Is `ScopedValue` (JDK 21+ preview) available and does the use case fit? | Prefer it — structurally immune to this leak class |
| Is Metaspace usage growing measurably after repeated redeploys? | Heap-dump and trace GC roots for stale `ClassLoader` instances |

**Trade-offs:**

| Leak severity | What's kept alive | Root cause |
|---|---|---|
| Plain `ThreadLocal` leak (system-classloader value) | One stale object | Missing `remove()` on a pooled thread |
| Classloader-mediated leak (this chapter) | An entire classloader + every class it loaded | Missing `remove()` for a value loaded by an app-specific classloader |
| `ScopedValue` (no leak possible) | Nothing beyond the binding's call duration | N/A — no persistent state to forget |

## Key Numbers (real, executed leak-and-fix demo with a real isolated ClassLoader)

```
=== "Deploying" a webapp via IsolatedClassLoader ===
Real ThreadLocal.set() called on the pooled thread -- no remove() anywhere.

=== BUGGY: forcing GC -- is the classloader actually collected? ===
Real classloader still reachable after GC: true (leaked)

=== FIXED: calling ThreadLocal.remove() on the SAME pooled thread ===
Real classloader still reachable after GC: false (now correctly collected)
```

## Common Pitfalls

- Treating every `ThreadLocal` leak as "just one stale object," missing that severity depends entirely on whose classloader the leaked value's class belongs to.
- Diagnosing Metaspace growth after redeploys as a generic memory leak without checking for multiple, stale `ClassLoader` instances in a heap dump.
- Assuming a single `ThreadLocal.remove()` fix resolves every leak, without checking for other call sites or other mechanisms (a static field, a listener) holding the same graph reachable.

## Interview Answer Skeleton

**30-sec:** A leaked `ThreadLocal` value on a long-lived pooled thread doesn't just leak that one object — if the value's class was loaded by an application-specific classloader, it keeps the entire classloader, every class it loaded, and every instance of those classes alive too. This is the real, classic cause of "Metaspace grows after every redeploy."

**2-min:** Add the strong-value/weak-key `ThreadLocalMap` asymmetry, and the measured `WeakReference` proof: the classloader remained reachable after a forced GC while the leak was present, and was genuinely collected once `ThreadLocal.remove()` was called on that same pooled thread.

**Whiteboard:** A long horizontal line labeled "pooled thread — lives for the whole process." Below it, a chain of four boxes with solid (strong) arrows: `ThreadLocalMap Entry` → instance → `Class` → `ClassLoader`. Circle the whole chain, label it "all reachable because of ONE entry" — draw an X through the first arrow (the `remove()` fix) to show erasing just that link frees the entire chain.

**Staff-level framing:** Connect this mechanism to a diagnosable production symptom (Metaspace growth specifically, not generic heap growth), and propose both a disciplinary fix (`try/finally` + static analysis) and a structural one (`ScopedValue` migration).

## Production Warning Signs

- Metaspace usage growing measurably after every redeploy, never fully returning to baseline — heap dumps showing multiple, distinct `ClassLoader` instances for the same application (one per historical deployment).
- A heap dump shows a `Class` object whose loader is a defunct-looking classloader instance — trace the GC root path directly; typically leads through `Thread` → `ThreadLocalMap` → `Entry` → value.
- The general `ThreadLocal.remove()` fix applied but the leak persists — check for more than one leaked entry, or a different mechanism (a static field) independently holding a reference.

## Related

- `syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md`
- `syllabus/02-java/concurrency/varhandles-and-unsafe.md`
- `syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md`
