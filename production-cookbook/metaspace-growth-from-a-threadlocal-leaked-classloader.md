---
title: "Metaspace Growth From a ThreadLocal-Leaked Classloader"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/concurrency/threadlocal-mediated-classloader-leaks.md
source: handbook/concurrency/threadlocal-mediated-classloader-leaks.md#production-scenarios
---

# Metaspace Growth From a ThreadLocal-Leaked Classloader

## Context

A caching or context-propagation utility class, loaded by the application's own classloader, called `ThreadLocal.set(this)` on container-pooled worker threads and never called `remove()`.

## Symptoms

Metaspace utilization crept upward after each deployment of a new version of the same application, never fully returning to its pre-deploy baseline even well after the old version's traffic had drained away.

## Impact

The service required scheduled restarts to avoid running out of memory, an operational cost that recurred with every redeploy rather than resolving itself.

## Initial Hypotheses

- A genuine memory leak in application code, unrelated to deployment — this was the first hypothesis pursued.

## Evidence

Heap dumps taken after several redeploys showed multiple, distinct instances of the same class name, each loaded by a different classloader instance — one classloader per historical deployment, none of them actually the current one.

## Investigation Timeline

1. **Metaspace growth noticed** creeping upward after each redeploy, not returning to baseline even after old traffic drained.
2. **General application-leak hypothesis pursued first**, on the assumption the leak was unrelated to deployment activity.
3. **Heap dumps captured across several redeploys**, revealing multiple distinct classloader instances for the same class name.
4. **Retention mechanism traced**: each old classloader was being kept alive by a single leaked `ThreadLocal` entry on a container-managed worker thread that had survived every redeploy.

## Root Cause

A caching or context-propagation utility class called `ThreadLocal.set(this)` on container-pooled worker threads and never called `remove()`. Every redeploy created a brand-new classloader and a brand-new leaked instance on whichever pooled threads happened to handle a request during that deployment's lifetime, while the previous deployment's classloader — and everything it loaded — remained reachable through its own leaked entry, since the pooled threads themselves outlived any single deployment.

## Immediate Mitigation

Scheduled periodic full application server restarts — a real but crude stopgap.

## Permanent Fix

Added a `try/finally` around every `ThreadLocal.set()` call site guaranteeing `remove()` on the same thread.

## Alternatives Considered

Using a `WeakReference`-based ThreadLocal wrapper instead of guaranteed `remove()` calls. Not adopted as the primary fix because a weak reference only prevents the *value* from being strongly retained, while the leak's actual mechanism was the `ThreadLocal` entry itself keeping the classloader reachable — a guaranteed `remove()` closes the leak at its source rather than relying on garbage-collection timing.

## Trade-offs

Minor code churn was required to audit every `ThreadLocal` use site. This was accepted against the real, recurring cost of scheduled restarts.

## Prevention

Added a static-analysis rule flagging any `ThreadLocal.set()` call without a matching `remove()` in the same method's control flow.

## Monitoring and Alerts

- Metaspace utilization tracked as a first-class metric across redeploys specifically, since a leak of this shape shows a distinctive step-up-per-redeploy pattern rather than a smooth continuous growth curve.
- Heap-dump class-histogram checks for multiple classloader instances of the application's own classes as a standing diagnostic for this specific leak signature.

## Interview Story

This maps to a "why does Metaspace grow after every redeploy" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** Metaspace usage climbed after every redeploy of the same application, eventually requiring scheduled restarts.
- **Task:** find why memory wasn't being reclaimed across redeploys.
- **Action:** ruled out a generic application-level leak; heap dumps showed multiple classloader instances for the same class, traced to a leaked `ThreadLocal` entry on a container-pooled thread.
- **Result:** added guaranteed `remove()` calls via `try/finally` at every `ThreadLocal.set()` site, eliminating the classloader retention.

## Staff-Level Discussion

One leaked `ThreadLocal` value can leak an entire classloader — the symptom, Metaspace growth after redeploys, is a real, well-known signature of exactly this mechanism, not a generic memory leak, and recognizing the signature early avoids chasing the wrong hypothesis (a generic application-level leak) for far longer than necessary. The organizational risk is specific to long-lived, thread-pooled runtimes redeploying the same logical application repeatedly: any container-managed thread that outlives a deployment is a candidate leak vector for every `ThreadLocal` used anywhere in that deployment's code, and this is invisible to standard heap-usage monitoring, which tracks object count and size, not classloader identity. A static-analysis rule catching unmatched `set()`/`remove()` pairs is the durable fix because it prevents the leak's precondition from ever shipping, rather than depending on someone recognizing the Metaspace-growth signature after the fact.

## Related Handbook Chapters

- [ThreadLocal-Mediated Classloader Leaks](../handbook/concurrency/threadlocal-mediated-classloader-leaks.md) — canonical classloader-retention mechanism used here.
