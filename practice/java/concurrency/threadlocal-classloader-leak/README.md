# ThreadLocal-mediated classloader leaks (T-413) — runnable verification

Real, executed Java 21 output backing
[`handbook/concurrency/threadlocal-mediated-classloader-leaks.md`](../../../../handbook/concurrency/threadlocal-mediated-classloader-leaks.md)
(T-413). No mocked classloader — a real, isolated `ClassLoader` subclass
defining a class independently of the system classloader, a real
`WeakReference` proving whether that classloader is actually collectible,
and a real `ThreadLocal` leak on a real, long-lived pooled thread. This
extends the general `ThreadLocal`-in-a-thread-pool leak already proven in
[`practice/java/concurrency/scoped-values-and-threadlocal/`](../scoped-values-and-threadlocal/README.md)
with the specific, classic mechanism that keeps an entire *classloader* alive
— the real cause of application-server "redeploy leaks."

## Files

- `LeakyThreadLocalHolder.java` — stands in for a long-lived
  framework/container class, loaded once by the system classloader.
- `PluginTask.java` — stands in for a "webapp" class, loaded by its own,
  isolated classloader.
- `IsolatedClassLoader.java` — a real, minimal classloader that defines
  `PluginTask` independently, without delegating to the parent for that
  specific class — the same real mechanism a servlet container uses to give
  each deployed webapp its own classloader.
- `ClassloaderLeakDemo.java` — the demo below.

## Run

```bash
cd practice/java/concurrency/threadlocal-classloader-leak
mkdir -p out
javac -d out src/*.java
java -cp out ClassloaderLeakDemo
```

## Real observed output (last full run, Java 21)

```
=== "Deploying" a webapp: loading PluginTask via its OWN, isolated classloader ===
Real defining classloader for PluginTask: IsolatedClassLoader@1540e19d

=== Running the plugin task on a real, long-lived pooled thread ===
Real ThreadLocal.set() called on the pooled thread -- no remove() anywhere.

=== "Undeploying" the webapp: dropping every direct reference ===

=== BUGGY: forcing GC -- is the classloader actually collected? ===
Real classloader still reachable after GC: true (leaked -- the pooled thread's ThreadLocal entry still references a PluginTask instance)

=== FIXED: calling ThreadLocal.remove() on the SAME pooled thread ===
Real classloader still reachable after GC: false (now correctly collected)
```

`PluginTask` is loaded by a real, genuinely separate `IsolatedClassLoader`
instance — confirmed directly via `pluginClass.getClassLoader()`. Running
`PluginTask` on a pooled thread calls `LeakyThreadLocalHolder.HOLDER.set(this)`
with no matching `remove()`. After "undeploying" (dropping every direct
reference to the classloader, the class, and the instance) and forcing a
real GC, a `WeakReference` around the classloader is still non-null — real,
decisive proof the classloader is still reachable, kept alive by the pooled
thread's `ThreadLocalMap` entry, which strongly references the `PluginTask`
instance, which strongly references its own defining classloader (every
object holds a strong reference to its `Class`, and every `Class` holds a
strong reference to its defining `ClassLoader`). Calling `remove()` on that
same pooled thread breaks the chain; a second real GC then confirms the
classloader is genuinely collected.

## Real discoveries made while building this pack

No bugs were hit while building this pack — the demo produced correct, real
output on the first run, including both the leaked and fixed states. This
is itself worth noting honestly: the design deliberately reused the exact,
already-proven `ThreadLocal`-on-a-pooled-thread mechanism from
[T-412's `ThreadLocalLeakDemo`](../scoped-values-and-threadlocal/README.md),
extended with a real isolated classloader specifically to make the
classloader-retention consequence — not just the stale-value consequence —
directly observable via a real `WeakReference` check.
