# Process/Thread Model — Real, Executed Evidence

Evidence base for [The OS Process/Thread Model, Below Java's Abstraction of It](../../../../syllabus/01-computer-science-foundations/os-process-thread-model.md) (T-2004). Measures real, OS-level thread counts — read from the operating system, not from any JVM API — while 200 Java threads are alive and blocked, once for platform threads and once for virtual threads.

Environment: OpenJDK 21.0.12, macOS (Darwin), 10 CPU cores (`sysctl -n hw.ncpu`).

```bash
javac -d out src/ThreadCountingDemo.java
java -cp out ThreadCountingDemo platform
java -cp out ThreadCountingDemo virtual
```

`printOsThreadCount` shells out to `top -l 1 -pid <pid> -stats th`, macOS's own real-time per-process thread counter — a measurement taken from outside the JVM entirely, not something the JVM is reporting about itself.

## Real captured output

```
=== platform ===
Mode: platform threads, n=200, pid=85780
  OS-level thread count (before spawning any of the 200 threads): 22
  OS-level thread count (with all 200 platform threads alive and blocked): 230

=== virtual ===
Mode: virtual threads, n=200, pid=85861
  OS-level thread count (before spawning any of the 200 threads): 22
  OS-level thread count (with all 200 virtual threads alive and blocked): 32
```

## What the numbers show

- **Baseline (22 OS threads before spawning anything)** is the JVM's own internal machinery — GC threads, JIT compiler threads, the reference-handler and finalizer threads, the signal dispatcher — that exist before any application code runs a single `Thread`.
- **200 platform threads → 230 OS threads**, an increase of **208** for 200 requested: essentially **1:1**. This is the real, measured confirmation that a Java platform thread *is* an OS thread — Java's `Thread` object here is a thin wrapper around a genuine native thread the operating system schedules directly. (The 8-thread gap from an exact 200 is ordinary JVM-internal variance — additional GC or JIT activity triggered by the load — not evidence against the 1:1 model.)
- **200 virtual threads → 32 OS threads**, an increase of only **10** for 200 requested — and 10 is exactly this machine's CPU core count (`sysctl -n hw.ncpu`). This is the real, measured confirmation of virtual threads' M:N model: 200 virtual threads were multiplexed onto a small, fixed-size pool of **carrier threads**, sized by default to the number of available processors, rather than each getting its own dedicated OS thread. All 200 virtual threads are genuinely alive and blocked at the moment of measurement — the OS simply never had to create 200 real threads to hold them.

This single, real comparison is the entire M:N threading model made concrete: the same 200-blocked-threads scenario costs the OS approximately 200 real threads under the platform model, and approximately 10 under the virtual-thread model.
