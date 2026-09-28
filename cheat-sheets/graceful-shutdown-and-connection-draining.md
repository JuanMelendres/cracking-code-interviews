---
title: "Cheat Sheet: Graceful Shutdown and Connection Draining"
slug: graceful-shutdown-and-connection-draining
document_type: cheat-sheet
domain: 14-devops-containers
topic_id: T-2434
canonical: ../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md
last_updated: 2026-09-28
---

# Graceful Shutdown and Connection Draining

**Canonical chapter:** [`syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md`](../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md)

## Core Mental Model

Take the "open" sign down first, keep serving everyone already inside, then lock the door. Locking first traps the customers and slams the door on people mid-step.

## The Three Phases, In Order

1. **Flip readiness to NOT ready** — and keep serving. Wait at least one probe period.
2. **Close the listener, drain in-flight** — bounded timeout.
3. **Shut down worker pools** — bounded, then exit.

Reversing 1 and 2 is the bug this whole topic is about.

## Why the Order Matters

Endpoint removal is **eventually consistent and concurrent with `SIGTERM`**. Your process learns it is dying before the routing layer does, so for 1–2 seconds new requests are still arriving at a pod that has been told to stop.

## Measured

| Case | Client saw |
|---|---|
| No handling | `curl: (52) Empty reply from server`, HTTP status **000**, killed at 1.38s of a 3s request |
| Three-phase hook | HTTP **200** at 3.00s; process exited 2006 ms after `SIGTERM`, `completed=1 rejected=0` |

Status `000` means no HTTP response at all — a client retry policy keyed on status codes has nothing to key on.

## Kubernetes Facts

- `terminationGracePeriodSeconds` (default 30) is a **hard ceiling** on `preStop` + drain + cleanup.
- `preStop` runs **before** `SIGTERM` and spends part of that budget.
- `SIGKILL` cannot be caught. JVM shutdown hooks do not run on `SIGKILL`, a crash, or `Runtime.halt()`.
- Keep **liveness** healthy during the drain; only **readiness** flips, or the container gets restarted mid-shutdown.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| 502 burst on every deploy | No readiness flip; routing still sending to a closed socket | `preStop` sleep now; three-phase hook properly |
| Requests die mid-flight | No drain | `server.stop(timeout)` + bounded `awaitTermination` |
| `SIGKILL` lands during drain | Timeouts exceed the grace period | Shrink timeouts or raise the grace period, with arithmetic |
| Container restarts while draining | Liveness probe failing | Keep liveness `200` until exit |
| Duplicate charges after deploy | Retry of a non-idempotent op | Idempotency keys — shutdown only reduces frequency |

## Non-HTTP Work

- **Consumers:** stop polling, finish the batch, **commit offsets**, `close(timeout)` to leave the group promptly. No load balancer to leave.
- **Schedulers:** do not start a new run; finish or safely abandon the current one.
- **Streams (WS/SSE/gRPC):** send an application-level close so clients reconnect deliberately.

## Common Pitfalls

- Closing the socket first.
- Unbounded `awaitTermination` / `join()` in a hook.
- Drain timeout shorter than p99 — silently abandons the expensive requests.
- Spring Boot's `server.shutdown=graceful` covers phases 2 and 3 only, **not** the readiness flip.
- Dropping the connection instead of returning `503` during the drain.

## Interview Answer Skeleton

**30-sec:** On `SIGTERM`, stop taking new work, finish what you accepted, exit before `SIGKILL`. Readiness flips **first**, while still serving, because endpoint removal is eventually consistent. Without that, every deploy drops requests.

## Related

- [Kubernetes Resource Limits, Probes, and JVM Sizing](../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md)
- [Idempotency](../syllabus/11-system-design/idempotency.md)
- [Consumer Groups and Rebalancing](../syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md)
