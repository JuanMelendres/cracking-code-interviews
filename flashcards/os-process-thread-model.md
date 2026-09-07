---
title: "Flashcards: The OS Process/Thread Model, Below Java's Abstraction of It"
slug: os-process-thread-model
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2004"
canonical: ../syllabus/01-computer-science-foundations/os-process-thread-model.md
last_updated: 2026-09-07
---

# Flashcards: The OS Process/Thread Model, Below Java's Abstraction of It

**Canonical chapter:** [`syllabus/01-computer-science-foundations/os-process-thread-model.md`](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: Process vs. thread — what's isolated, what's shared

**Prompt:**
What does a process get that a thread inside it does not, and what do all threads inside the same process share?

**Answer:**
A process gets its own private virtual memory space, isolated from every other process. Threads within the same process deliberately share that process's memory (the heap, static fields, loaded classes) — what each thread keeps privately is only its own call stack and its own saved CPU register state (including its own program counter).

**Why it matters:**
Shared memory between threads is exactly what makes shared-memory concurrency, and every one of its classic hazards (races, visibility bugs), possible in the first place.

**Common trap:**
Describing a thread as "a lightweight process" without stating specifically what's shared (memory) versus private (stack, registers).

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: What a context switch actually costs

**Prompt:**
What does the operating system actually do during a context switch, and why is it not free?

**Answer:**
It saves one thread's complete CPU state (registers, program counter) and loads another thread's saved state in its place. This takes real CPU cycles, and it typically evicts the outgoing thread's data from the CPU's fast cache — the next thread to run there has to reload it from slower main memory.

**Why it matters:**
It's the concrete, physical reason "just add more threads" has a real ceiling: past a certain number of runnable threads competing for a fixed number of cores, the system spends a growing share of time context-switching rather than doing application work.

**Common trap:**
Treating "add more threads" as a free way to get more concurrent progress, regardless of how many cores are actually available.

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: 1:1 platform threads vs. M:N virtual threads

**Prompt:**
What is the structural difference between how a Java platform thread and a Java virtual thread map onto real OS threads?

**Answer:**
A platform thread is a genuine native OS thread, created 1:1 via the same OS call any native program would use, with its own dedicated OS stack. A virtual thread is a lightweight JVM object that the JVM's own scheduler mounts onto a real OS "carrier" thread only while running or about to block, and unmounts the moment it blocks on something the JVM can intercept — an M:N model, many virtual threads multiplexed onto a much smaller pool of carriers.

**Why it matters:**
It's the direct, derivable reason virtual threads scale to far more concurrent blocked tasks than platform threads, rather than an unexplained fact to memorize.

**Common trap:**
Describing virtual threads as "not real threads" rather than the more precise mechanism — they are real `Thread` objects with real, if usually brief, execution mounted on a real OS thread.

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: Measured OS thread cost — 200 platform vs. 200 virtual threads

**Prompt:**
In the chapter's real measurement (10-core machine), spawning and blocking 200 platform threads created roughly how many new OS threads, versus 200 blocked virtual threads?

**Answer:**
200 platform threads created about 208 new OS threads (~1:1, confirming the model directly). 200 virtual threads created only about 10 new OS threads — exactly this machine's CPU core count, the default size of the JVM's virtual-thread carrier pool.

**Why it matters:**
It makes the M:N threading model concrete and falsifiable rather than asserted: the identical "200 concurrently blocked tasks" scenario has a dramatically different real OS-level cost depending only on which `Thread` flavor is used.

**Common trap:**
Treating "virtual threads are lightweight" as a memorized buzzword rather than a measurable, falsifiable claim about actual OS thread counts.

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: Virtual thread pinning

**Prompt:**
What operation prevents the JVM from unmounting a virtual thread from its carrier thread while it's blocked, defeating the M:N model for that blocking window?

**Answer:**
Entering a `synchronized` block (or calling into native code) and then blocking inside it. The virtual thread stays pinned to its carrier for the entire blocking window instead of freeing that carrier to run other virtual threads.

**Why it matters:**
It's a real, documented migration hazard: code written assuming platform-thread semantics (like locking around a blocking call) can silently degrade rather than improve after switching to virtual threads.

**Common trap:**
Assuming virtual threads make all blocking calls cheap unconditionally, without checking for `synchronized` blocks around blocking operations in the hot path.

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)

## Card: Virtual memory is what actually enforces process isolation

**Prompt:**
Why can't one process's bug corrupt a different process's memory, even though both processes might believe they're using the same memory address (e.g., `0x1000`)?

**Answer:**
Each process gets its own virtual address space, translated by the CPU's memory management unit into real physical addresses using a per-process mapping the OS controls. Two processes can both reference virtual address `0x1000` and genuinely be looking at entirely different physical memory — the hardware itself refuses to let one process's virtual addresses resolve into another's physical memory.

**Why it matters:**
Process isolation is enforced by hardware-backed virtual memory, not merely a software convention or agreement between processes.

**Common trap:**
Treating process memory isolation as a matter of the OS "being careful," rather than a hardware-enforced guarantee via address translation.

**Related:**
[syllabus/01-computer-science-foundations/os-process-thread-model.md](../syllabus/01-computer-science-foundations/os-process-thread-model.md)
