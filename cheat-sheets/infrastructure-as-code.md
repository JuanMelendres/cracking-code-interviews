---
title: "Cheat Sheet: Infrastructure as Code"
slug: infrastructure-as-code
document_type: cheat-sheet
domain: 15-cloud
topic_id: T-2438
canonical: ../syllabus/15-cloud/infrastructure-as-code.md
last_updated: 2026-09-28
---

# Infrastructure as Code

**Canonical chapter:** [`syllabus/15-cloud/infrastructure-as-code.md`](../syllabus/15-cloud/infrastructure-as-code.md)

## Core Mental Model

Double-entry bookkeeping for infrastructure. **Configuration** = what you intend to own. **State** = your ledger. **Cloud account** = physical inventory. **Plan** = the reconciliation. **Drift** = the discrepancy.

## State Is the Crown Jewel

| Property | Consequence |
|---|---|
| Maps declarations to real resources | Lose it and the tool owns nothing |
| Holds secrets **in plaintext** | It is a credential store — encrypt, restrict |
| Shared by engineers + CI | Remote backend, never a laptop, never git |
| Written concurrently = corruption | Locking is mandatory |
| Versioned | How a bad apply is recovered |

## Plan: Three-Way Compare

configuration vs state vs reality → create / update / **replace** / **destroy**

Read it for `destroy` first. Replacement is a **provider** property: the same logical change is in-place on one resource type and destroy-and-recreate on another.

What a plan cannot tell you: the *behaviour* of the change; what happens between plan and apply (apply a saved plan); anything outside its own state.

## Drift: Cause → Response

| Cause | Response |
|---|---|
| Emergency console fix | Codify it — the fix was right, the record is missing |
| Unmanaged resource | Import, or document as out of scope |
| Two tools managing it | Fix ownership overlap |
| Provider-side auto-change | Ignore deliberately, with a comment |

A plan that always shows noise is a safety problem: reviewers stop reading, and the one saying `destroy` slips through.

## Guards That Actually Work

```hcl
lifecycle { prevent_destroy = true }   # on every stateful resource
```

Plus CI that fails on destroy actions against protected types, with a deliberate override. Beats "review more carefully," which has no visible failure mode.

## State Splitting

Production always separate; then split where **change frequency** differs sharply. Cost: cross-state references. One state for everything = one lock, one plan against the world, unbounded blast radius.

## Common Pitfalls

- State in git or an unencrypted bucket.
- No locking.
- Unpinned providers and modules → non-reproducible plans.
- Standing console write access to production → guaranteed drift.
- Secrets in configuration files instead of a secret manager.

## Interview Answer Skeleton

**30-sec:** Infrastructure defined in version control and applied by a tool. A state file maps declarations to real resources; a plan compares configuration, state, and reality. The two things that bite: state holds secrets and needs remote/encrypted/locked storage, and drift when reality changes outside the tool.

## Related

- [AWS Core Services for Backend Engineers](../syllabus/15-cloud/aws-core-services-for-backend-engineers.md)
- [CI/CD Pipeline Design and Deployment Strategies](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)
- [Branching Strategy, Versioning, and Release Management](../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md)
