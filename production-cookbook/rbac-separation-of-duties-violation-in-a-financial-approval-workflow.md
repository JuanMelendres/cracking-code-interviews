---
title: "RBAC Separation-of-Duties Violation in a Financial-Approval Workflow"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/12-security/authn-authz-rbac-vs-abac.md
source: handbook/security/authn-authz-rbac-vs-abac.md#production-scenarios
---

# RBAC Separation-of-Duties Violation in a Financial-Approval Workflow

## Context

A financial-approval workflow requires that the person approving a transaction never be the same person who initiated it — a separation-of-duties rule. The system enforces access exclusively via RBAC: anyone holding the "approver" role can approve any transaction.

## Symptoms

An incident review finds the separation-of-duties rule was violated: a transaction was both initiated and approved by the same individual.

## Impact

A compliance-critical control failed silently — no error, no rejected action — for a rule the organization believed was enforced.

## Initial Hypotheses

- A bug in the RBAC implementation — investigated directly, since the natural first assumption for an authorization failure is a flaw in the authorization system itself.
- The rule was never expressible under the authorization model actually in use — correct, once RBAC's own semantics are examined against what the rule requires.

## Evidence

Investigation shows the system used RBAC exclusively: anyone with the "approver" role could approve any transaction, including their own. Nothing in the role grant references the relationship between the specific approver and the specific transaction's initiator.

## Investigation Timeline

1. **Self-approval incident identified**, violating a rule the organization believed was structurally enforced.
2. **RBAC implementation reviewed for a bug**, the natural first hypothesis for any authorization-rule failure.
3. **RBAC's own model examined against the rule's actual requirement**: "not the same person who did X" is a relationship between a specific subject and a specific resource's history, not a property of the subject's role.
4. **Conclusion reached**: no implementation bug exists; the rule was structurally inexpressible under RBAC alone.

## Root Cause

This is not a bug in the RBAC implementation — it's a category of rule RBAC cannot express at all, since "not the same person who did X" is a relationship between the specific subject and the specific resource's history, not a property of the subject's role.

## Immediate Mitigation

Manually audit recent approvals for other instances of the same violation, and require manual secondary review of approvals until a structural fix ships.

## Permanent Fix

Introduce attribute-based logic — or an equivalent workflow-engine rule — alongside the existing role check: an ABAC policy or workflow condition expressing "the approver's identity must differ from the transaction's initiator identity," evaluated against the specific transaction rather than the approver's static role.

## Alternatives Considered

Attempting to express the rule through finer-grained roles (e.g., separate "approver-A" and "approver-B" roles). Rejected — role membership is still static and independent of any specific transaction's initiator, so this doesn't actually close the gap; it only adds role-management complexity without solving the underlying relational requirement.

## Trade-offs

Adding attribute-based logic alongside existing RBAC introduces a second authorization mechanism to reason about and maintain. Accepted, since RBAC alone is structurally incapable of expressing this compliance requirement, regardless of how carefully roles are designed.

## Prevention

Any compliance or business rule expressed as a relationship between a specific subject and a specific resource's history or state — not merely a subject-level property — should be identified during design review as requiring attribute-based logic from the start, not assumed expressible via roles.

## Monitoring and Alerts

- An automated post-hoc audit comparing every approval's approver identity against the corresponding transaction's initiator identity, alerting on any match — a direct, mechanical check for the exact violation this incident represents, independent of and complementary to the preventive ABAC fix.
- A design-review checklist item explicitly asking, for any new authorization rule, whether it can be fully expressed as "does this subject hold this role" or whether it requires referencing a specific resource's history or another specific subject — flagging the latter as an ABAC requirement before implementation begins.

## Interview Story

This maps to "RBAC vs. ABAC — what can RBAC not express" as a real compliance incident. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a separation-of-duties rule, believed to be enforced, was violated by a self-approval that no error or rejection caught.
- **Task:** determine whether this is an implementation bug or a modeling limitation.
- **Action:** rule out an RBAC implementation bug by confirming the system behaved exactly as its role grants specify; recognize that the rule requires comparing the approver against the specific transaction's initiator, a relationship RBAC's role-based model cannot express.
- **Result:** added attribute-based logic alongside the existing role check, expressing the relational rule explicitly rather than attempting to force it into a role structure.

## Staff-Level Discussion

The most valuable outcome of this incident is correctly naming it as a modeling-capability gap rather than a bug — RBAC did exactly what it was configured to do, and no amount of debugging the implementation would have found a "fix," because there was nothing wrong to fix within RBAC's own semantics. This distinction matters for how the organization responds: treating it as a bug leads to wasted debugging effort and false confidence after a code review finds "nothing wrong"; correctly identifying it as an authorization-model limitation leads directly to the actual fix. A Staff engineer designing or reviewing an authorization system should maintain a working list of rule types RBAC cannot express — separation of duties, ownership-based access, time-or-context-bound grants — and treat any compliance requirement matching one of these categories as a signal that ABAC or an equivalent mechanism is required from the start, not discoverable only after an audit finding.

## Related Handbook Chapters

- [AuthN/AuthZ: RBAC vs. ABAC](../syllabus/12-security/authn-authz-rbac-vs-abac.md) — canonical RBAC/ABAC expressiveness comparison used here.
