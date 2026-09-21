---
title: "Flashcards: Distributed Locking and Fencing Tokens"
slug: distributed-locking-and-fencing-tokens
document_type: flashcard-deck
domain: 10-distributed-systems
topic_id: T-2422
canonical: ../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md
last_updated: 2026-09-21
---

# Flashcards: Distributed Locking and Fencing Tokens

**Canonical chapter:** [`syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md`](../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md)

## Card: Why a shorter lease TTL doesn't fix the stale-write bug

**Prompt:**
Does shortening a distributed lock's lease TTL fix the pause-then-stale-write bug?

**Answer:**
No — it only narrows the window. A paused process cannot observe its own pause regardless of how short the TTL is; any TTL has some real pause duration (a longer GC pause, a slower disk write) that exceeds it. Real fencing tokens at the resource layer are the only complete fix.

**Why it matters:**
A common, incomplete first answer to the core question this topic tests.

**Common trap:**
Treating lease-TTL tuning as a real fix rather than a narrower, still-real risk window.

**Related:**
[Core Concepts](../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md#core-concepts)

## Card: Real measured evidence — the bug and the fix

**Prompt:**
What did a real demo measure for a 300ms lease against a real 500ms pause, with and without fencing tokens?

**Answer:**
Without fencing tokens: the stale holder's write silently overwrote the legitimate new holder's write (`Final UnsafeStorage value: A-value`) — real, reproducible corruption. With fencing tokens: the stale holder's write was explicitly rejected (`token 1 <= last accepted token 2`), and the legitimate holder's write survived.

**Why it matters:**
Turns an abstract distributed-systems concern into a concrete, reproducible, measured result — reliably reproduces, not a rare race.

**Common trap:**
Treating this failure mode as a rare, hard-to-trigger race rather than one that reproduces at any pause longer than the lease.

**Related:**
[Internal Implementation](../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md#internal-implementation)

## Card: Why the fencing check must live at the resource, not the lock service

**Prompt:**
Why does the fencing-token check have to happen at the resource being written to, rather than inside the lock service itself?

**Answer:**
Because the lock service has no way to intercept the stale holder's actual write operation — by the time the paused client resumes and writes, it isn't talking to the lock service at all, only to the resource. Only the resource itself, checking the token on every write, can catch it.

**Why it matters:**
A precise, mechanistic answer, not just "fencing tokens fix it."

**Common trap:**
Proposing to add the fencing check to the lock service instead of the actual write path.

**Related:**
[Common Mistakes](../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md#common-mistakes)
