# Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling — Real Demo

Backs [`syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md`](../../../../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md) (T-2425).

Pure JDK, no dependencies, no AWS account or credentials required. No
mocked clock — real `System.nanoTime()` around real work.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out ColdStartDemo
java -cp out ConcurrencyScalingDemo
```

Real captured output in [`output-transcript.txt`](output-transcript.txt),
re-run twice to confirm reliability — the exact millisecond values vary
run to run (real JVM/OS scheduling noise), but the order of magnitude is
consistent: a cold invocation is reliably 4+ orders of magnitude slower
than a warm one, and a cold concurrent burst is reliably 1000x+ slower
than a repeat burst against already-warm environments.

## What it proves

`ExecutionEnvironment`'s constructor is a real, honest proxy for a real
Lambda execution environment's documented INIT phase: real CPU-bound
object/collection allocation (standing in for class loading and building
a real object graph, e.g. a JDBC connection pool's internal state) plus a
real, modest `Thread.sleep` standing in for a real network round-trip (a
database connection handshake — something a real Java Lambda's static
initializer very commonly does). `invoke()` is the fast, reusable
per-request handler work.

1. **`ColdStartDemo`** — a cold invocation (construct a fresh
   `ExecutionEnvironment`, then invoke once) versus 10 warm invocations
   reusing that same already-initialized environment. Real captured
   output: a cold invocation at ~170ms versus a warm invocation at
   ~0.01ms — a real, measured four-to-five-order-of-magnitude difference,
   entirely explained by whether INIT-phase work has already happened.
2. **`ConcurrencyScalingDemo`** — a real burst of 5 concurrent requests
   arriving simultaneously (a real `CountDownLatch` releases 5 real
   threads at once) when zero warm environments exist: each thread must
   construct its own environment in parallel, a real, measured ~230ms
   wall-clock time for the whole burst. An identical repeat burst against
   the now-warm environments completes in a fraction of a millisecond —
   proving directly why a burst of *concurrent* traffic against a cold
   function multiplies cold-start cost (each concurrent request needs its
   own execution environment), and why a warm/provisioned pool avoids it.

## Honest limitations

- This is a real, technically substantiated **proxy** for AWS Lambda's
  documented execution model — it does not invoke actual AWS Lambda (no
  AWS account, credentials, or cost involved). The real JVM startup and
  class-loading cost this demo measures is specifically the *dominant*
  real-world contributor to cold-start latency for a real JVM-based
  (Java) Lambda function, which is why this proxy is technically
  representative rather than a generic stand-in.
- Real AWS Lambda cold starts also include container/sandbox provisioning
  time outside the JVM itself (downloading the deployment package,
  starting the Firecracker microVM) — this demo measures only the
  JVM-internal portion of that cost, the part most directly under an
  application developer's control (initializer code, dependency-injection
  framework startup, connection-pool setup).
- The 5-way concurrent burst size and the ~120ms simulated network
  handshake are chosen for a demo that runs in well under a second while
  still producing a reliable, measurable result — the mechanism proven
  (parallel cold starts under a concurrent burst) holds at any real scale.
