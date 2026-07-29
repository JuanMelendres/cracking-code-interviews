---
title: "T-809 · Idempotency"
topic_id: T-809
domain: System Design
tier: Advanced
iwi: 8.09
prerequisites: [T-909]
unlocks: []
week: 5
last_reviewed: 2026-07-29
---

# T-809 · Idempotency

**IWI 8.09 · Advanced tier · The structural fix to Week 4's retry-ambiguity problem**

**Verification note:** the full mechanism in §3 is real, executed Java against real PostgreSQL 16 — genuine concurrent threads, a real unique-constraint race, and real TTL-based recovery. Source: `practice/java/week-05/idempotency/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The full mechanism, reproduced](#3-the-full-mechanism-reproduced)
4. [What the client does when it never receives the response](#4-what-the-client-does-when-it-never-receives-the-response)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

An operation is idempotent if performing it multiple times produces the same result — and, critically for distributed systems, the same *side effect* — as performing it once. A client-supplied **idempotency key** attached to a request lets the server recognize "I have already handled this exact logical request" and return the original result instead of re-executing the operation, even though the request arrived again (a retry) with no way for the client or server to know for certain whether the first attempt actually completed.

## 2. Why it exists

This directly answers Week 4's unresolved question (`02-distributed-failure-modes.md` §4): a network cannot distinguish "the request was lost," "it's still processing," and "it succeeded but the response was lost." Idempotency keys don't resolve that ambiguity for the client — they make the ambiguity *safe to retry through* by moving the resolution to the server, which has ground truth about whether the operation actually ran.

## 3. The full mechanism, reproduced

**Key:** a client-generated unique identifier for one logical operation (e.g., one checkout attempt), sent with every retry of that same logical operation.

**Storage:** a table keyed on that identifier, with a `UNIQUE` constraint doing the actual coordination work:

```sql
CREATE TABLE idempotency_keys (
  key TEXT PRIMARY KEY,
  status TEXT NOT NULL,   -- 'IN_PROGRESS' or 'COMPLETED'
  result TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);
```

**Concurrent-duplicate behavior — real, measured:** two threads submit the *same* idempotency key at (almost) the same instant.

```
Request A result: charged $50.00, confirmation #49940811261291
Request B result: charged $50.00, confirmation #49940811261291
Actual charges performed: 1 (must be exactly 1)
Both requests returned the same result: true
```

**How it works:** both attempt `INSERT INTO idempotency_keys (key, status) VALUES (?, 'IN_PROGRESS')`. The database's unique constraint guarantees exactly one insert succeeds — no application-level lock needed, since this is precisely what a primary key/unique constraint is for. The winner performs the actual charge and updates the row to `COMPLETED` with the result. The loser catches the unique-violation (`SQLState 23505`), polls the row briefly, and once it sees `COMPLETED`, returns the *same* stored result — never re-executing the charge.

**TTL — real, measured:** an `IN_PROGRESS` row from a request that crashed before completing must not block every future retry forever.

```
A stale IN_PROGRESS row (age 10s, TTL 5s) exists for key 'charge-key-crashed'.
New request with the same key result: charged $75.00, confirmation #49941065026041
RESULT: the stale IN_PROGRESS row did not block a fresh attempt -- TTL recovery worked.
```

A losing request that finds an `IN_PROGRESS` row older than the TTL treats the original attempt as presumed-dead, deletes the stale row, and retries its own insert — reclaiming the key rather than waiting forever for a process that will never finish.

## 4. What the client does when it never receives the response

The client cannot distinguish this from every other failure mode in Week 4's chapter — so it does the only safe thing: **retry with the same idempotency key.** This is the entire point of the mechanism — the client's correct behavior on ambiguous failure is unconditional retry-with-same-key, because the server-side mechanism guarantees that retry is safe regardless of whether the original request actually succeeded, is still in flight, or never arrived at all. Without an idempotency key, this same client behavior (retry on any doubt) is exactly the mechanism that risks a duplicate charge.

## 5. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| No idempotency mechanism | No extra storage or logic | Every ambiguous failure forces a choice between "risk a duplicate" and "risk never completing the operation" |
| Idempotency key + unique constraint | Retries become unconditionally safe | Requires storage for every recent key, a TTL policy, and every mutating endpoint accepting a key |
| Short TTL | Storage doesn't grow unbounded | Risks reusing a key too early if a legitimate operation somehow takes longer than the TTL |
| Long TTL | Very safe against premature reuse | Storage grows proportional to key volume × TTL duration |

## 6. Interview questions

### Q1. Make a payment endpoint idempotent. Full mechanism — key, storage, TTL, concurrent-duplicate behaviour.

- **Expected answer:** all four pieces from §3, ideally citing the real numbers (1 charge for 2 concurrent duplicates; TTL-based recovery from a crashed attempt).
- **Common mistakes:** describing only the key concept ("check if we've seen this before") without the storage mechanism (a unique constraint doing the actual coordination) or the TTL/crash-recovery case.
- **Follow-up questions:** "What if the first request is still genuinely in progress when the duplicate arrives — not crashed, just slow?"
- **Senior-level expectations:** describes key, storage, and basic duplicate detection.
- **Staff-level expectations:** all four pieces, including the TTL-based recovery from a crashed (not just slow) in-progress attempt, and can explain why the unique constraint — not application-level locking — is what makes the mechanism correct under real concurrency.

### Q2. What does the client do when it never receives the response?

- **Expected answer:** §4 — retry with the same idempotency key; this is safe specifically because of the server-side mechanism, not despite the ambiguity.
- **Common mistakes:** proposing the client should somehow determine whether the operation succeeded before retrying — in the general case it structurally cannot.
- **Follow-up questions:** "Why is unconditional retry safe here but not for a non-idempotent operation?"
- **Senior-level expectations:** states that retry-with-same-key is correct.
- **Staff-level expectations:** explains why this specifically resolves the ambiguity from Week 4's chapter — the client doesn't need to resolve it, the server does.

## 7. Common mistakes

- Implementing "idempotency" as a client-side check (e.g., disabling a submit button) rather than a server-side, storage-backed mechanism — this doesn't protect against a genuine network retry or a second independent client.
- Using application-level locking instead of the database's own unique constraint, introducing a race the constraint would have closed for free.
- No TTL at all, meaning a crashed in-progress attempt permanently blocks all future retries of that logical operation.

## 8. Staff-level discussion

Idempotency keys are one instance of a broader Staff-level pattern: **moving ambiguity resolution to the party with the most information.** The client cannot know if its request succeeded; the server can, because it's the one that would have executed the side effect. Every well-designed idempotent API is built on this same insight — shift the burden of resolving "did this already happen" to whichever side actually has (or can cheaply obtain) the ground truth, rather than asking the side with less information to guess correctly.

## 9. Summary

An idempotency key, backed by a unique-constraint-based storage mechanism, converts "I don't know if my request succeeded" from a dangerous ambiguity into a safe-to-retry-regardless guarantee. The full mechanism — key, storage via a database unique constraint, concurrent-duplicate handling, and TTL-based recovery from a crashed attempt — was reproduced in this chapter with real concurrent threads against real PostgreSQL: exactly 1 charge for 2 simultaneous duplicate requests, and correct recovery when a prior attempt is presumed dead.

## 10. Key Takeaways

- An idempotency key lets a client safely retry an ambiguous request without risking a duplicate side effect.
- The database's own unique constraint — not application-level locking — is what correctly coordinates concurrent duplicate attempts.
- A TTL on `IN_PROGRESS` entries prevents a crashed attempt from permanently blocking all future retries of that key.
- The correct client behavior on "never received a response" is unconditional retry with the same key — the mechanism, not the client, resolves the ambiguity.

## 11. Cheat Sheet

See §3's mechanism walkthrough and §5's trade-off table.

## 12. Flashcards

1. **Q: What does an idempotency key actually protect against?** A: A duplicate side effect (e.g., a double charge) from a client retrying a request it can't confirm succeeded.
2. **Q: What coordinates concurrent duplicate requests correctly?** A: The storage layer's own unique constraint — not application-level locking.
3. **Q: Why is a TTL necessary on the mechanism?** A: Without it, a crashed in-progress attempt permanently blocks every future retry of that key.
4. **Q: Correct client behavior when a response never arrives?** A: Retry, unconditionally, with the same idempotency key.

(Full week-level deck: `05-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce the demo yourself: `practice/java/week-05/idempotency/`.
2. Extend the demo to simulate a THIRD concurrent duplicate arriving while the first two are still racing, and confirm it also receives the correct single result.
3. Design the idempotency-key mechanism for a non-payment case (e.g., an "add item to cart" endpoint) — does the same TTL make sense, or does this operation's semantics call for a different value?

## 14. Additional Reading

- [Stripe API documentation — Idempotent requests](https://stripe.com/docs/api/idempotent_requests) — a widely-cited real-world implementation this chapter's mechanism follows the shape of

## 15. Official References

- No single RFC governs idempotency-key design; Stripe's documentation (above) functions as a de facto industry reference.
