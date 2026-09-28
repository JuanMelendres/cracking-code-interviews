# Graceful Shutdown and Connection Draining — Real Demo

Backs [`syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md`](../../../../syllabus/14-devops-containers/graceful-shutdown-and-connection-draining.md) (T-2434).

Two servers that differ **only** in how they react to `SIGTERM`. Both serve `/work`, which takes ~3 seconds. `run-demo.sh` starts each one, fires a request, delivers `SIGTERM` one second into that three-second request, and records exactly what the client saw.

Pure JDK (`com.sun.net.httpserver`), no dependencies.

## Run it

```bash
./run-demo.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

**Abrupt (no shutdown hook):**

```text
[client] curl: (52) Empty reply from server
[client] /work HTTP status: 000, total 1.378310s
```

The in-flight request is destroyed mid-flight. The client gets no status code at all — not a 500, not a 503 — which means a client-side retry policy keyed on status codes has nothing to key on, and any non-idempotent operation is now in an unknown state.

**Graceful (readiness flip, then bounded drain):**

```text
[server] SIGTERM received
[server] readiness flipped to NOT ready; in-flight requests: 1
[server] no longer accepting new connections; draining up to 10s
[server] request finished
[client] /work HTTP status: 200, total 3.004853s
[server] worker pool drained cleanly: true
[server] shutdown complete in 2006ms; completed=1 rejected=0
```

The same request completes normally with a 200, and the process exits 2006 ms after `SIGTERM` — the remaining 2 seconds of real work plus the readiness-propagation pause, not an arbitrary sleep.

The ordering is the whole lesson, and it is why the hook does three separate things rather than one: stop declaring readiness **first** (so the load balancer stops sending new work while the socket is still open), then stop accepting connections and drain in-flight work under a bounded timeout, then shut the worker pool down and wait — also bounded. A hook that closes the socket first still drops the requests already routed to it.
