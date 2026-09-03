---
title: "Missing Environment Variable Passing Health Checks, Then Failing Every Request"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../syllabus/15-cloud/twelve-factor-config.md
source: handbook/system-design/twelve-factor-config.md#production-scenarios
---

# Missing Environment Variable Passing Health Checks, Then Failing Every Request

## Context

A required `PAYMENT_GATEWAY_URL` environment variable was present in the staging Kubernetes deployment manifest but accidentally omitted from the production one during a manifest refactor.

## Symptoms

The deployment's readiness probe passed, the service showed as healthy, and then every real request immediately failed with a generic `NullPointerException` somewhere inside the payment-processing path.

## Impact

Every real production request to the payment-processing path failed immediately after a deployment that appeared, by every automated health signal, to have succeeded.

## Initial Hypotheses

- A bug introduced in the release being deployed — this was the first hypothesis pursued.

## Evidence

The service "started" successfully because nothing checked for the `PAYMENT_GATEWAY_URL` value at startup, and the `NullPointerException` only appeared once code actually tried to use it, deep in the payment-processing path — giving no indication the real root cause was missing configuration.

## Investigation Timeline

1. **Deployment appeared healthy**: the readiness probe passed and the service reported as up.
2. **First real requests failed immediately** with a generic `NullPointerException` in the payment-processing path.
3. **Release-bug hypothesis pursued first**, on the assumption the deployed code itself contained a regression.
4. **Root cause traced to configuration**: `PAYMENT_GATEWAY_URL` was present in staging's manifest but missing from production's, omitted during a manifest refactor and never checked at startup.

## Root Cause

The health check verified that the process was running and responding, not that its configuration was complete. A required environment variable was silently omitted from the production manifest, and because nothing validated required configuration at startup, the omission surfaced only when code deep in the payment-processing path actually tried to use the missing value.

## Immediate Mitigation

Manually patched the production manifest with the missing variable.

## Permanent Fix

Added real startup validation requiring every config key the service actually uses to be checked and present before the health check can ever report ready.

## Alternatives Considered

Relying on a manifest-diff review step performed manually before every deploy. Not adopted as the primary fix because a manual review step is exactly the kind of check that had already failed once (during the refactor) and offers no structural guarantee against a future omission, unlike an automated startup check that fails fast on every deploy without relying on a reviewer noticing.

## Trade-offs

A slightly stricter startup can now fail on missing config the service happens not to need for every code path. This was accepted because a fast, obvious failure at deploy time is far cheaper than a confusing failure discovered by a customer in production.

## Prevention

Added a deployment-pipeline check diffing required config keys across environment manifests, to catch a future omission before it reaches production at all.

## Monitoring and Alerts

- Startup-validation failures treated as deploy-blocking, first-class alerts distinct from generic application errors, so a missing-config failure is immediately legible as a configuration problem rather than a runtime bug.
- A deployment-pipeline gate diffing required config keys across environment manifests before any production rollout proceeds.

## Interview Story

This maps to a "how did a healthy deployment fail every request" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a service passed its readiness probe and then failed every real request with a generic `NullPointerException` in the payment path.
- **Task:** find why "healthy" and "working" diverged.
- **Action:** ruled out a code regression in the release; traced the failure to a required environment variable present in staging but missing from the production manifest.
- **Result:** added startup validation that checks every required config key before the health check can report ready, and a pipeline gate diffing config keys across environments.

## Staff-Level Discussion

"Healthy" and "correctly configured" are not the same claim, and a readiness probe that only checks process liveness structurally cannot catch a missing-configuration defect — no amount of probe tuning fixes this, because the probe is answering a different question than "is this deployment correctly configured." This generalizes to any environment-specific configuration surface: the failure mode isn't a one-time mistake by one engineer during one refactor, it's a structural gap between what a manifest refactor can silently break and what any automated gate was checking for at the time. The fix that actually closes the gap is making the application itself the authority on its own required configuration — validating every key it will use at startup — rather than depending on infrastructure tooling or manual review to independently rediscover what the application needs.

## Related Handbook Chapters

- [The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation](../syllabus/15-cloud/twelve-factor-config.md) — canonical fail-fast startup validation model used here.
