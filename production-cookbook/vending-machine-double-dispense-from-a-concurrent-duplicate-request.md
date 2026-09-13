---
title: "Vending Machine Double-Dispense From a Concurrent Duplicate Request"
document_type: production-cookbook-entry
domain: software-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/04-software-design/ood-interview-problems.md
source: syllabus/04-software-design/ood-interview-problems.md#production-scenarios
---

# Vending Machine Double-Dispense From a Concurrent Duplicate Request

## Context

A vending machine's control software uses a naive boolean-flag implementation (`isDispensing`) to track whether an item is currently being dispensed.

## Symptoms

A customer inserts money, the machine begins dispensing, and a network blip causes a duplicate "select" request to arrive while dispensing is still in progress.

## Impact

The machine double-dispenses — a real, direct financial loss per occurrence, and a state the naive implementation had no way to prevent.

## Initial Hypotheses

- A duplicate request from the client itself — confirmed as the trigger, but not sufficient explanation on its own, since a correctly-guarded state machine should reject the duplicate regardless of how many times it arrives.
- A hardware dispensing fault — checked, the mechanism dispensed exactly as commanded twice; it was not misbehaving.
- The software has no explicit, checkable "currently dispensing" state guarding a second dispense — correct.

## Evidence

The boolean-flag implementation allows `select()` to be processed a second time while `isDispensing` is true, because no code path actually checks that flag before acting on a new "select" request during dispensing.

## Investigation Timeline

1. Double-dispense reported following a network blip during checkout.
2. Client-duplicate-request and hardware-fault hypotheses examined.
3. Code path traced, showing no guard against a second `select()` while a dispense is in progress.

## Root Cause

The design relied on several independently-checked boolean flags rather than a single, explicit, checkable state — "currently dispensing" was an inference, not a guarded precondition, so a concurrent duplicate request could slip through.

## Immediate Mitigation

Add client-side idempotency (a request ID de-duplicated at the network layer) to reduce duplicate-request frequency while the state-machine fix ships.

## Permanent Fix

Redesign the control flow as an explicit state machine, where the `DISPENSING` state's own guard in `insertCoin()` (and an equivalent guard on `select()` for a concurrent second request) rejects the duplicate outright, because "currently dispensing" is a single, explicit, checkable state rather than an inference drawn from several boolean flags that could theoretically be in an invalid combination.

## Alternatives Considered

Adding more boolean flags to cover the specific gap found — rejected, since it treats the symptom of this one duplicate-request pattern without addressing that boolean-flag combinations can still reach an unintended state as the machine's logic grows.

## Trade-offs

A state-machine redesign is a larger upfront change than patching the specific gap — accepted, since it makes an entire class of "invalid combination of flags" bugs structurally impossible rather than fixing one instance of it.

## Prevention

Model any hardware- or transaction-controlling software with a small number of mutually-exclusive, explicit states rather than independently-toggled booleans, specifically wherever a "currently mid-operation" precondition needs to reject concurrent or duplicate requests.

## Monitoring and Alerts

- A dispense-count-vs-payment-count reconciliation check per transaction, catching any future double-dispense regardless of its specific trigger.
- Alerting on any `select()`/dispense request received while the machine's own state reports `DISPENSING`, even if successfully rejected, to track duplicate-request frequency from the network layer.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a vending machine double-dispensed following a network-level duplicate request during an active dispense.
- **Task:** find why the software didn't reject the duplicate, and fix the actual gap rather than just the specific trigger.
- **Action:** traced the control flow, found no explicit guard against a concurrent `select()` during dispensing, and replaced the boolean-flag design with an explicit state machine.
- **Result:** made the specific failure — and the broader class of invalid-flag-combination bugs — structurally impossible.

## Staff-Level Discussion

A boolean-flag design's real risk isn't any one bug — it's that the total number of valid and invalid flag combinations grows combinatorially as more flags are added, and nothing enforces that only the valid combinations are reachable. An explicit state machine bounds that risk permanently: illegal states become unrepresentable rather than merely unlikely, which is the same reasoning this program applies to distributed leader-election (majority quorum making split-brain structurally impossible) and other concurrency-shaped correctness problems.

## Related Handbook Chapters

- [Object-Oriented Design Interview Problems](../syllabus/04-software-design/ood-interview-problems.md) — the canonical vending-machine state-machine design behind this incident's fix.
