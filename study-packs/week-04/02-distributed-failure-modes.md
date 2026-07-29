---
title: "T-909 · Distributed Systems Failure Modes"
topic_id: T-909
domain: System Design
tier: Staff-Level
iwi: 8.45
prerequisites: [T-801]
unlocks: [T-1504]
week: 4
last_reviewed: 2026-07-29
---

# T-909 · Distributed Systems Failure Modes

**IWI 8.45 · Staff-Level tier · 4th-ranked topic in the Mandatory Core**

**Verification note:** the retry-amplification and fencing-token demonstrations in this chapter are real, executed Java — genuine concurrent thread pools, real measured timing and call counts. Source: `practice/java/week-04/failure-modes/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Retries and amplification, measured](#3-retries-and-amplification-measured)
4. [Distinguishing "failed" from "succeeded slowly"](#4-distinguishing-failed-from-succeeded-slowly)
5. [Split-brain and fencing tokens, reproduced](#5-split-brain-and-fencing-tokens-reproduced)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

A distributed system's defining property is that a component can fail, or merely appear to fail from another component's point of view, without either side being able to distinguish the two cases with certainty. Most of the failure modes this chapter covers are consequences of that single fact: the network cannot tell you whether a request was lost, is merely slow, succeeded and the response was lost, or succeeded and is about to arrive.

## 2. Why it exists

A single-process program either completes a function call or the whole process crashes — there's no in-between state to reason about. Across a network, every one of those in-between states is not just possible but common: a request can be sent, processed successfully, and have its *response* lost, which is indistinguishable on the caller's side from the request itself having been lost. Every retry policy, timeout, and idempotency mechanism exists because of this fundamental ambiguity.

## 3. Retries and amplification, measured

**"You added retries and made the outage worse" — the precise mechanism, reproduced:**

A downstream service (capacity 4, degraded to 400ms/unit) receives 12 requests in a burst, client timeout 700ms. Critically, a client giving up does **not** cancel the work already submitted downstream — it keeps running and occupying a slot.

```
--- NO RETRY ---
Elapsed: 708ms, succeeded within SLA: 4/12, total work units submitted to downstream: 12

--- RETRY, NO BACKOFF (immediate resubmit) ---
Elapsed: 2114ms, succeeded within SLA: 4/12, total work units submitted to downstream: 28

--- RETRY, EXPONENTIAL BACKOFF + JITTER ---
Elapsed: 2606ms, succeeded within SLA: 12/12, total work units submitted to downstream: 24
```

**Reading this precisely:** retrying without backoff did not improve the outcome at all — still 4/12 succeeded — while submitting 2.3x the load and taking 3x longer wall-clock. Every retry adds to the queue behind the *original, still-running* attempt rather than replacing it; under a fixed downstream capacity, this is a pure tax with no offsetting benefit. Backoff succeeds (12/12) specifically because the delay between attempts gives the downstream's existing backlog time to drain before the next attempt arrives, and it does so with *less* total amplification (2.0x) than the no-backoff case, because far fewer retries are wasted firing into an already-saturated pool.

## 4. Distinguishing "failed" from "succeeded slowly"

This distinction matters because the correct response to each is different, and conflating them is precisely what causes retry amplification: **a request that "failed" (the server rejected it, or definitely never received it) is generally safe to retry immediately; a request that "succeeded slowly" (still processing, or completed with a lost response) is not** — retrying it while the original is still in flight is exactly the mechanism demonstrated in §3. In practice this distinction is why an idempotency key matters more than the retry policy itself: if every retried request carries the same idempotency key, the *server* can recognize a retry of an in-flight or already-completed operation and return the original result instead of executing the operation again — which converts "I don't know if that succeeded" into "it's safe to retry regardless," collapsing the ambiguity instead of requiring the client to resolve it.

## 5. Split-brain and fencing tokens, reproduced

**Two nodes both believe they are leader. How, and what breaks?**

Node A holds a lease (token 1) and pauses (GC pause, network partition) before its write reaches storage. Its lease expires during the pause; Node B acquires a new lease (token 2) and writes correctly. Node A then "wakes up," still unaware its lease expired, and writes its now-stale data.

**Without fencing tokens:**
```
Node B writes: ACCEPTED write with token 2 -> data is now "correct-data-from-node-B"
Node A writes: ACCEPTED write with token 1 -> data is now "stale-data-from-node-A"
Final data: "stale-data-from-node-A"  <-- CORRUPTED by the stale node
```

**With fencing tokens** (storage tracks the highest token it has ever accepted and rejects anything older):
```
Node B writes: ACCEPTED write with token 2 -> data is now "correct-data-from-node-B"
Node A writes: REJECTED write with token 1 (a newer token 2 has already written)
Final data: "correct-data-from-node-B"  <-- correct
```

**What breaks without the fix:** any storage system that accepts writes from whoever currently claims to be the leader, with no way to verify that claim's currency, is vulnerable to exactly this — the "leader" designation is a belief held by a node, not a fact verifiable by storage, unless storage is given a monotonically increasing token to check against.

## 6. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| No retries | Simple, no amplification risk | Any transient failure becomes a permanent one from the caller's perspective |
| Retry, no backoff | Fast recovery from truly transient blips | Amplifies load precisely when the downstream is already struggling, per §3 |
| Retry with exponential backoff + jitter | Recovers from real degradation without amplifying it | Slower worst-case latency for the retrying request itself |
| Idempotency keys | Converts "don't know if it succeeded" into "safe to retry regardless" | Requires every mutating endpoint to accept and honor a client-supplied key, and requires storing recent operation results server-side |
| Fencing tokens | Prevents a stale leader from corrupting shared state | Requires every write path to storage to check and enforce token ordering — a design decision, not a bolt-on fix |

## 7. Interview questions

### Q1. You added retries and made the outage worse. Explain the mechanism precisely.

- **Expected answer:** §3's mechanism — retries add to the queue behind still-running original attempts rather than replacing them, amplifying load on an already-degraded downstream with no offsetting success-rate benefit (demonstrated: 4/12 succeeded either way, but no-backoff cost 2.3x the load and 3x the time).
- **Common mistakes:** a vague "retries added more load" without the specific claim that the original request's work is *not cancelled* by the client giving up.
- **Follow-up questions:** "How does backoff fix this, precisely?"
- **Senior-level expectations:** correctly identifies that retries add rather than replace load.
- **Staff-level expectations:** cites the specific real numbers (or reproduces them) and explains why backoff reduces total amplification, not just total latency.

### Q2. How do you distinguish "the request failed" from "the request succeeded slowly," and why does it matter?

- **Expected answer:** §4 — the server-side response was lost, or is merely delayed, versus a genuine failure; it matters because retrying an in-flight or completed operation without idempotency protection can duplicate its effect.
- **Common mistakes:** treating all timeouts as equivalent to failures.
- **Follow-up questions:** "How does an idempotency key resolve this ambiguity?"
- **Senior-level expectations:** states the distinction and its consequence.
- **Staff-level expectations:** names idempotency keys as the structural fix, explaining specifically how they shift the burden of resolving the ambiguity from the client to the server.

### Q3. Two nodes both believe they are leader. How, and what breaks?

- **Expected answer:** §5's scenario — a paused node's lease expires without it knowing, a new leader is elected, and the stale node's eventual write, if accepted, corrupts shared state.
- **Common mistakes:** describing the scenario but not naming the fix (fencing tokens) when asked.
- **Follow-up questions:** "What's a fencing token, precisely, and where does the check happen?"
- **Senior-level expectations:** describes the split-brain scenario correctly.
- **Staff-level expectations:** names fencing tokens, states precisely where the check must live (the storage/resource layer, not the nodes themselves — the nodes are exactly the parties whose belief can't be trusted), and can point to the real reproduction's exact reject condition (`token < highestTokenSeen`).

## 8. Common mistakes

- Believing a timeout definitively means the request failed rather than merely being ambiguous.
- Adding retries as a default resilience measure without backoff, jitter, or an idempotency mechanism.
- Assuming leader election alone (without fencing) is sufficient to prevent split-brain corruption — election solves "who is elected," not "can a stale former leader still cause damage."

## 9. Staff-level discussion

Every mechanism in this chapter — backoff, idempotency keys, fencing tokens — is a structural answer to the same underlying fact: **a distributed system cannot get instantaneous, certain knowledge of another component's state.** A Staff-level engineer designing a new distributed component asks, for every request/response boundary, "what happens if this response is lost, delayed, or duplicated," and builds the answer into the protocol rather than trusting operational discipline (careful retry tuning, hoping leader transitions are clean) to paper over it after the fact.

## 10. Summary

Distributed failure modes stem from one fact: a network cannot distinguish "lost," "slow," and "succeeded but the response was lost." Retries without backoff amplify load onto an already-degraded system with no success-rate benefit — reproduced with real numbers (4/12 succeeded either way, at 2.3x the cost without backoff, 12/12 succeeded with it). Split-brain — two nodes both believing they're the leader — is real and corrupts shared state unless storage itself enforces a fencing token, since node belief about leadership cannot be trusted by the resource it's writing to.

## 11. Key Takeaways

- A timeout is ambiguous — "failed" and "succeeded slowly" are indistinguishable without more information.
- Retries add load on top of still-running original attempts; they do not replace that load.
- Idempotency keys convert response ambiguity into safe-to-retry-regardless, shifting the resolution to the server.
- Split-brain is prevented by fencing tokens enforced at the storage layer, not by better leader election alone.
- Every fix in this chapter is a structural protocol decision, not an operational tuning knob.

## 12. Cheat Sheet

See §6's trade-off table.

## 13. Flashcards

1. **Q: Why is a network timeout ambiguous?** A: It can't distinguish "request lost," "still processing," and "succeeded but the response was lost."
2. **Q: Precisely how do retries amplify an outage?** A: They add new work on top of the still-running original attempt rather than replacing it, multiplying load on an already-degraded system.
3. **Q: What structurally fixes the retry-safety problem?** A: Idempotency keys — the server recognizes a retried operation and returns the original result instead of re-executing it.
4. **Q: What structurally prevents split-brain corruption?** A: A fencing token, enforced at the storage/resource layer, rejecting any write carrying an older token than one already accepted.

(Full week-level deck: `05-flashcards.md`.)

## 14. Practice Exercises

1. Reproduce the retry-storm demo yourself: `practice/java/week-04/failure-modes/RetryStormDemo.java`. Tune the parameters (capacity, work time, timeout) and observe how the amplification factor changes.
2. Reproduce the fencing-token demo: `practice/java/week-04/failure-modes/FencingTokenDemo.java`. Modify it to allow a second stale node to attempt a write after the fencing fix, and confirm it's also rejected.
3. Design an idempotency-key mechanism for a payment endpoint: where is the key generated, how long is a result cached server-side, what happens on a key collision with different request parameters?

## 15. Additional Reading

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 8 "The Trouble with Distributed Systems" (the fencing-token example in this chapter follows Kleppmann's original)
- AWS Builders' Library — ["Timeouts, retries, and backoff with jitter"](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/)

## 16. Official References

- [Stripe API documentation — Idempotent requests](https://stripe.com/docs/api/idempotent_requests) — a widely-cited real-world idempotency-key implementation
