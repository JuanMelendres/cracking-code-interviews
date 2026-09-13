---
title: "Payment Processor OCP Violation Breaking a Neighboring Payment Method"
document_type: production-cookbook-entry
domain: software-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/04-software-design/solid-principles.md
source: syllabus/04-software-design/solid-principles.md#production-scenarios
---

# Payment Processor OCP Violation Breaking a Neighboring Payment Method

## Context

A payment-processing service's `PaymentProcessor` class grows a new `if (paymentMethod.equals("APPLE_PAY"))`-style branch every time a new payment method is added, with every method's logic living inside the same method.

## Symptoms

A bug fix for PayPal's branch accidentally breaks Apple Pay's neighboring branch in the same method during a later change.

## Impact

A change scoped to one payment method causes a real, customer-facing failure in a completely different, unrelated payment method.

## Initial Hypotheses

- A regression specific to the Apple Pay integration itself — checked, no Apple Pay-specific code changed in the release.
- A shared-dependency version bump — checked, no dependency changes in the release.
- The PayPal bug fix, landing in the same method as Apple Pay's branch, altered shared state or control flow the Apple Pay branch also depended on — correct.

## Evidence

The diff for the "PayPal-only" bug fix touches lines inside the same method body that Apple Pay's branch also executes through, despite the change being described and reviewed as PayPal-scoped.

## Investigation Timeline

1. Apple Pay failures reported immediately following a release described as a PayPal-only fix.
2. Apple-Pay-specific and dependency-version hypotheses ruled out.
3. The release diff reviewed line-by-line, showing the "PayPal fix" landed inside the same method Apple Pay's branch also runs through.

## Root Cause

The class must be edited — not extended — for every new payment method, and because all the branches live in one method, an unrelated change has a real chance of breaking a case it wasn't even touching — a live Open/Closed Principle violation.

## Immediate Mitigation

Roll back the release; re-ship the PayPal fix in isolation after confirming it no longer shares a method body with any other payment method's logic.

## Permanent Fix

Introduce a `PaymentMethod` interface with one implementation per payment method; `PaymentProcessor` iterates over registered implementations rather than branching on a string, and adding a new payment method becomes a new class, not an edit to `PaymentProcessor` or any of its existing, already-tested payment methods.

## Alternatives Considered

Adding more defensive tests around the shared method to catch cross-branch regressions earlier — rejected as treating the symptom; the structural coupling between unrelated payment methods remains regardless of test coverage, and every new branch added to the same method increases the risk surface further.

## Trade-offs

Introducing an interface-per-payment-method adds a small amount of upfront structure compared to a single branching method — accepted, since it eliminates an entire class of cross-payment-method regressions rather than just this one instance.

## Prevention

Treat "does adding a new case require editing an existing, already-shipped branch or method" as a standing design review question for any branching logic handling multiple independent variants (payment methods, notification channels, discount types).

## Monitoring and Alerts

- Per-payment-method success-rate dashboards, split out individually, so a regression in one method is visible immediately rather than averaged into an aggregate payment-success metric.
- A release-review checklist item flagging any change to a shared method also exercised by cases outside the change's stated scope.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a bug fix scoped to one payment method broke an unrelated payment method in the same release.
- **Task:** find why a "PayPal-only" fix affected Apple Pay at all.
- **Action:** reviewed the diff line-by-line and found both payment methods' logic shared the same method body; refactored to a `PaymentMethod` interface with one implementation per method.
- **Result:** eliminated the cross-method coupling, so future per-method changes can no longer break unrelated methods by construction.

## Staff-Level Discussion

This is a live Open/Closed Principle violation with a concrete, measurable cost: a change scoped to one variant broke an unrelated one, purely because of how the code was structured, not because of any inherent coupling between PayPal and Apple Pay as payment methods. The organizational lesson is that OCP violations are not abstract code-smell complaints — they materialize as exactly this kind of cross-cutting regression, and the fix (one class per variant) is what makes future changes to one variant provably unable to affect another.

## Related Handbook Chapters

- [SOLID Principles](../syllabus/04-software-design/solid-principles.md) — the canonical Open/Closed Principle violation and fix behind this incident.
