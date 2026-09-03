---
title: "AuthN vs AuthZ, RBAC vs ABAC"
slug: authn-authz-rbac-vs-abac
document_type: handbook-chapter
domain: 12-security
status: draft
version: 1.0
last_reviewed: 2026-08-02
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites: []
related:
  - owasp-top-10-for-backend-services.md
  - oauth2-oidc-and-jwt.md
  - multi-tenancy-isolation-models.md
  - ../05-spring/security-filter-chain.md
  - ../../study-packs/week-17/03-authn-authz-rbac-vs-abac.md
official_references:
  - https://csrc.nist.gov/pubs/sp/800/162/final
source_history:
  - handbook/security/authn-authz-rbac-vs-abac.md
---

# AuthN vs AuthZ, RBAC vs ABAC

> **Topic register:** T-1302 (AuthN vs AuthZ, RBAC vs ABAC, IWI 6.0) · Core tier · High interview frequency [H]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Production Scenarios](#production-scenarios)
8. [Failure Modes and Debugging](#failure-modes-and-debugging)
9. [Trade-offs](#trade-offs)
10. [Decision Framework](#decision-framework)
11. [Common Mistakes](#common-mistakes)
12. [Anti-Patterns](#anti-patterns)
13. [Best Practices](#best-practices)
14. [Interview Answer Framework](#interview-answer-framework)
15. [Interview Questions](#interview-questions)
16. [Summary](#summary)
17. [Key Takeaways](#key-takeaways)
18. [Cheat Sheet](#cheat-sheet)
19. [Flashcards](#flashcards)
20. [Practice Exercises](#practice-exercises)
21. [Solutions](#solutions)
22. [Additional Reading](#additional-reading)
23. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can state the precise distinction between authentication and authorization without conflating them, explain why RBAC cannot express certain real access-control requirements that ABAC expresses directly, and cite a real Java demonstration where identical role assignments produce different, context-correct outcomes under an ABAC evaluator.

## Why This Matters in Interviews

"AuthN vs AuthZ" sounds like a vocabulary question, but interviewers use it to check whether a candidate reasons about *where* an access decision actually happens in a request's lifecycle and *what information* that decision needs. A candidate who says "authentication is login, authorization is permissions" gives a textbook-correct but shallow answer. A stronger answer explains that authentication produces an *identity* the rest of the system can trust, and authorization is a *separate decision*, made potentially many times per request across different layers (API gateway, service, database row), each of which may need different context to decide correctly — which is exactly the seam where RBAC's simplicity starts to break down and where ABAC or its relatives (ReBAC, policy-based authorization) become necessary.

## Mental Model

Picture a building with a front desk and many internal doors. **Authentication** is the front desk checking your ID and issuing you a badge — it happens once (per session), and everything downstream trusts the badge without re-checking your ID. **Authorization** is every individual door deciding whether *this specific badge* may open *this specific door* *right now* — it can happen many times, at many doors, and each door's decision may depend on more than just "what badge type do you have" (RBAC) — it might depend on which floor you're already on, what time it is, or whether you're escorting a visitor (ABAC's attribute-based context). RBAC gives every badge type a fixed door list decided in advance; ABAC evaluates a rule against the actual situation at the door, at the moment of the request.

## Definition and Purpose

**Authentication (AuthN)** is the process of verifying a claimed identity — confirming that a request is actually coming from who or what it claims to be, typically via credentials (password, certificate, token) validated against a trusted source. **Authorization (AuthZ)** is the separate, subsequent decision of whether an *already-authenticated* identity is permitted to perform a specific action on a specific resource. **RBAC (Role-Based Access Control)** grants permissions by assigning users to roles, and roles to fixed sets of permissions, decided in advance and independent of any specific request's context. **ABAC (Attribute-Based Access Control)**, per NIST SP 800-162, evaluates access decisions dynamically against attributes of the subject, the resource, the action, and the environment at request time, expressed as policy rules rather than static role-to-permission tables.

## Core Concepts

### AuthN answers "who," AuthZ answers "may they" — and AuthZ can run many times per request

A single incoming request might authenticate once (validate the bearer token at the edge) but authorize multiple times as it flows through a system: an API gateway checks whether the identity may call this endpoint at all; a service checks whether the identity may access this specific resource; a database row-level policy checks whether the identity may see this specific row (see [Multi-Tenancy Isolation Models](multi-tenancy-isolation-models.md)). Treating "authorization" as a single check at the front door, rather than a decision that recurs at each layer with potentially different available context, is a common source of authorization gaps — exactly the shape of the IDOR vulnerability covered in [OWASP Top 10](owasp-top-10-for-backend-services.md).

### RBAC's core limitation: a role is static, but many real rules are conditional on the specific request

RBAC answers "does this user's role include this permission" — a question entirely independent of *which* specific resource, *when*, or *under what circumstance*. Real access-control requirements are frequently conditional: "an engineer may approve a deploy for their own team" (depends on a relationship between the subject and the resource, not just the subject's role alone); "only during business hours" (depends on environment); "not if they authored the change themselves" (a separation-of-duties rule depending on a relationship between subject and resource history). RBAC has no attribute slots for any of this — the only lever is adding more, increasingly narrow roles (e.g., "payments-team-engineer," "search-team-engineer," ...), which scales combinatorially and still can't express "not the author," a per-instance relationship no static role can encode.

### ABAC trades RBAC's simplicity for expressiveness, at the cost of needing a policy engine and richer attribute data

ABAC policies are boolean functions over attributes gathered from the subject (role, team, clearance level), the resource (owner, sensitivity, team), the action (read, approve, delete), and the environment (time, location, request risk score). This directly expresses conditions RBAC cannot, but requires the system to actually have those attributes available and correct at decision time, and requires a policy evaluation mechanism more sophisticated than a lookup table — real complexity, not free expressiveness.

## Internal Implementation

**Real demonstration** (`practice/java/week-17/authz-models/src/RbacVsAbacDemo.java`) — an RBAC lookup answers only "does this role have this permission," with zero request context; an ABAC evaluator considers the specific change being approved and the current time:

```
=== RBAC: role has deploy:approve? (no context at all) ===
alice  role=engineer  rbacAllow(deploy:approve) = true
bob    role=engineer  rbacAllow(deploy:approve) = true
carol  role=engineer  rbacAllow(deploy:approve) = true
RBAC says yes for all three -- it cannot see 'own change' or 'wrong team'.

=== ABAC: same three users, same change, evaluated at 14:00 (business hours) ===
alice  team=payments abacAllow(chg-42) = false
bob    team=payments abacAllow(chg-42) = true
carol  team=search   abacAllow(chg-42) = false
```

All three users share the identical role (`engineer`), so RBAC produces the identical answer for all three — a false positive for alice (the change's own author) and carol (wrong team). The ABAC evaluator, given the same role plus the change's team and author attributes, correctly denies alice (self-approval) and carol (wrong team), and correctly allows bob (same team, not the author):

```
=== ABAC: bob approving the same change outside business hours (02:00) ===
bob    abacAllow(chg-42) @ 02:00 = false
```

The identical user, identical change, identical role — only the environment attribute (time) differs — produces a different, correct decision. This is the concrete evidence that ABAC's decision depends on request-time context that a static role-permission table structurally cannot represent.

## Production Scenarios

**A support team is granted a broad "support-agent" role so they can look up any customer's order for troubleshooting, and this later shows up as a data-access finding in a compliance audit.** RBAC's static grant ("support-agent role can read all orders") is technically correct for the legitimate use case but overprovisions access far beyond what any single support interaction actually needs — an ABAC policy expressing "may read an order if there is an open support ticket referencing that order ID, assigned to this agent" grants exactly the access needed for the actual task, and nothing more, directly addressing the audit finding without removing the team's ability to do their job.

**A financial-approval workflow requires that the person approving a transaction never be the same person who initiated it (separation of duties), and an incident review finds this rule was violated.** Investigation shows the system used RBAC exclusively — anyone with the "approver" role could approve any transaction, including their own. This is not a bug in the RBAC implementation; it's a category of rule RBAC cannot express at all, since "not the same person who did X" is a relationship between the specific subject and the specific resource's history, not a property of the subject's role. The remediation requires introducing attribute-based logic (or an equivalent workflow-engine rule) alongside the existing role check, not a "fix" to the RBAC model itself.

## Failure Modes and Debugging

- **Symptom: a security or compliance review finds a role grants broader access than any individual user of that role actually needs.** This is RBAC's structural over-provisioning tendency ("role explosion" avoidance leads teams to create broad roles) — check whether the actual requirement is conditional on request-time context (ownership, team, ticket reference) that RBAC cannot express, which would indicate an ABAC-shaped gap rather than a role-definition mistake.
- **Symptom: an access-control rule described in a requirements document ("not the author," "only during X," "only if Y relationship holds") has no clean RBAC implementation.** This is a direct signal the rule needs attribute-based evaluation — don't force it into an ever-narrower role taxonomy, which scales combinatorially and often still can't express the actual condition.
- **Anti-pattern to rule out first when debugging "why did authorization allow/deny this specific request":** confirm which layer made the decision (gateway, service, data layer) and what attributes were actually available to it at that point — a decision that's "correct" given the attributes it had access to can still produce the wrong overall outcome if a needed attribute (e.g., resource ownership) wasn't propagated to that layer.

## Trade-offs

RBAC is simple to reason about, audit, and implement — a role-to-permission table is trivially inspectable and requires no runtime policy-evaluation infrastructure — but cannot express conditional, relationship-dependent, or environment-dependent rules without either over-provisioning access or an unmanageable proliferation of narrow roles. ABAC expresses these rules directly and can grant access precisely matched to the actual requirement, but requires a policy-evaluation engine, reliable attribute sourcing at decision time, and is genuinely harder to audit ("what can this user actually do" requires evaluating policies against hypothetical attribute combinations, not reading a table).

## Decision Framework

Default to RBAC for coarse-grained access control where the permission genuinely depends only on "what kind of actor is this" (an admin dashboard, a read-only reporting role) — it's simpler to build, audit, and reason about, and simplicity itself is a security property (fewer things to get wrong). Reach for ABAC (or a narrower attribute-based extension layered on top of an existing RBAC system, which is common in practice — the two are not mutually exclusive) specifically when a real requirement is conditional on a relationship (ownership, team membership, ticket reference) or environment (time, location, risk score) that a static role cannot express — and treat "we need a new role for every combination of conditions" as the signal that ABAC, not more roles, is the right tool.

## Common Mistakes

- Using "authentication" and "authorization" interchangeably in casual conversation, then failing to distinguish them precisely under interview pressure — e.g., saying a 401 response means "wrong permissions" (it means unauthenticated; 403 means authenticated but unauthorized).
- Treating RBAC's inability to express a conditional rule as "we need a new, narrower role" rather than recognizing it as a structural RBAC limitation calling for attribute-based logic.
- Assuming ABAC is strictly "better" without acknowledging its real costs (policy engine complexity, attribute-sourcing reliability, audit difficulty) — the right choice depends on the actual access-control requirements, not a blanket preference.
- Forgetting that authorization can and often should happen at multiple layers (gateway, service, data) with different available context at each — treating it as a single check performed once.

## Anti-Patterns

Encoding conditional business rules (ownership, time-of-day, separation-of-duties) as an ever-growing set of increasingly specific role names (`payments-team-engineer-not-author`, `payments-team-engineer-business-hours`) instead of introducing attribute-based logic once the role taxonomy starts needing to encode relationships or context rather than just actor type — this pattern is a strong, recognizable signal that RBAC has been stretched past its natural fit.

## Best Practices

Keep the AuthN/AuthZ separation explicit in system design — authentication should produce a well-defined identity object (and nothing else) that every downstream authorization decision consumes as input, never conflating "is this token valid" with "may this identity do this action." Layer authorization checks deliberately at each boundary where a decision genuinely needs to be made (gateway for coarse routing-level access, service for resource-level access, data layer for row-level access per [Multi-Tenancy Isolation Models](multi-tenancy-isolation-models.md)) rather than relying on a single upstream check to cover every downstream concern.

## Interview Answer Framework

### 30-Second Answer

Authentication verifies who a request claims to be; authorization decides, separately and potentially multiple times per request, whether that already-verified identity may perform a specific action. RBAC grants permissions via static role membership; ABAC evaluates policy rules against subject, resource, action, and environment attributes at request time, expressing conditional rules (ownership, time, relationships) that RBAC structurally cannot.

### 2-Minute Answer

Definition: AuthN answers "who," AuthZ answers "may they" — two separate decisions, not one. RBAC assigns fixed permission sets to roles; ABAC evaluates dynamic policy rules against real-time attributes. Why ABAC exists: many real access rules are conditional (ownership, team, time, relationship to the resource's history) in ways a static role-permission table cannot express without an unmanageable proliferation of narrow roles. One trade-off: RBAC is simpler to audit and reason about; ABAC is more expressive but requires a policy engine and reliable attribute sourcing. One production example: three users with the identical RBAC role produce identical (and partly wrong) authorization answers under RBAC, but correct, differentiated answers under ABAC once resource-ownership and team-membership attributes are considered — measured directly with a small Java demo.

### 10-Minute Deep Dive

Cover: the precise AuthN/AuthZ distinction and why authorization is a decision that recurs at multiple layers, not a single gate; RBAC's core limitation (no attribute slots for conditional rules) illustrated by the real demo where identical roles produce identical-but-wrong answers; ABAC's model (subject/resource/action/environment attributes evaluated as policy) illustrated by the same demo producing correct, differentiated answers with the same role but different context; the "role explosion" anti-pattern as the practical signal that a system has outgrown RBAC for a specific rule; the separation-of-duties production scenario as a concrete example of a rule RBAC cannot express at all, not just awkwardly; the trade-off that ABAC's expressiveness comes with real audit and attribute-sourcing costs, so the decision isn't "ABAC is strictly better."

### Whiteboard Explanation

Draw a front desk labeled "AuthN: verify identity, issue badge (once)." Draw three doors downstream, each labeled "AuthZ: may this badge open this door (checked separately, per door)." Under the first door, draw a simple table (RBAC): "badge type -> allowed doors." Under a second door, draw a small rule-evaluation box (ABAC): "badge type AND owns-this-room AND during-business-hours -> allow," with three example badges evaluated against it to show the same badge type producing different outcomes based on which room and what time.

### Production Example

An engineering organization initially implements deploy-approval permissions with a single `engineer` role, granting broad approve rights. A post-incident review after a self-approved risky deploy identifies the missing separation-of-duties control. The team's first instinct is a narrower role (`engineer-not-author`), which is immediately recognized as unimplementable as a static role (authorship is per-change, not a property of the person). The actual fix introduces an attribute-based check layered on top of the existing RBAC role: `hasRole("engineer") AND currentUser != change.author AND currentUser.team == change.team`, evaluated at approval time rather than encoded as a role.

### Trade-offs to Mention

RBAC trades expressiveness for simplicity and auditability; ABAC trades simplicity for the ability to express conditional, relationship- and context-dependent rules directly, at the cost of needing a policy-evaluation mechanism and reliable real-time attribute data.

### Common Candidate Mistakes

Conflating AuthN and AuthZ under pressure (e.g., misdescribing 401 vs. 403); proposing "add more roles" as the fix for a rule RBAC structurally cannot express.

### Typical Follow-Up Questions

"Can RBAC and ABAC coexist in the same system?" → Yes, and this is common in practice — RBAC for coarse-grained actor-type gating, ABAC (or a narrower attribute-based rule layer) for the specific conditional rules that need it, rather than an all-or-nothing choice. "What's a practical risk specific to ABAC that RBAC doesn't have?" → Attribute-sourcing reliability — if the environment or resource attributes a policy depends on can be manipulated or are stale/incorrect at decision time, the authorization decision is only as correct as its input attributes, a larger trusted-input surface than RBAC's simple role lookup.

### Senior-Level Expectations

Correctly and precisely distinguishes AuthN from AuthZ, and can name at least one concrete rule RBAC cannot express while ABAC can.

### Staff-Level Discussion

Recognizes "role explosion" as the practical signal a system has outgrown RBAC for a specific rule, and can articulate the real operational costs ABAC introduces (policy engine, attribute-sourcing reliability, audit complexity) rather than treating it as a strictly superior default. Designs authorization as a decision made deliberately at each system layer with layer-appropriate available context, rather than a single upstream gate assumed to cover all downstream concerns.

## Interview Questions

### Question 1

**A system currently uses RBAC. A new requirement states: "a manager may approve a purchase request only if the requester reports to them." How would you implement this?**

**Expected answer:** this is a relationship between the specific subject (manager) and the specific resource (requester's manager chain), not a property of the manager's role alone — no RBAC role can express it without one role per manager-report pair, which doesn't scale. The correct approach evaluates the relationship as an attribute-based check at approval time: `hasRole("manager") AND requestApprovalPolicy(manager, request.requester)`.

**Common mistakes:** proposing new, narrower roles as the fix.

**Follow-up questions:** "Where would this check run — gateway, service, or data layer — and why?" (service layer, since it requires resource-specific relationship data not typically available at the gateway.)

**Senior-level expectations:** correctly identifies this as an attribute/relationship-based rule RBAC cannot express.

**Staff-level expectations:** proposes where in the request lifecycle the check should run and what attributes/data it needs available, including how the requester's manager relationship data itself should be sourced reliably.

### Question 2

**What's the practical difference between a 401 and a 403 HTTP response, and why does the distinction matter beyond just picking the "correct" status code?**

**Expected answer:** 401 means the request wasn't authenticated (or authentication failed/expired) — the correct client response is to re-authenticate. 403 means the request was authenticated but the identity isn't authorized for the requested action — re-authenticating won't help; the client needs a different identity or permission grant. Conflating them misleads clients into retrying the wrong remediation (e.g., prompting a re-login for what is actually a permissions problem).

**Common mistakes:** treating the two as interchangeable "access denied" responses.

**Follow-up questions:** "Should a 403 response reveal that the resource exists but is forbidden, versus a 404 that hides its existence entirely?" (Depends on the resource's sensitivity — for a resource whose mere existence is itself sensitive information, returning 404 instead of 403 avoids leaking that existence to an unauthorized caller.)

**Senior-level expectations:** correctly distinguishes the two codes and their client-facing implications.

**Staff-level expectations:** raises the 403-vs-404 information-disclosure consideration unprompted.

## Summary

Authentication and authorization are separate decisions — AuthN verifies identity once per session; AuthZ decides permission, potentially many times per request across different layers. RBAC grants static, role-based permissions, simple to audit but unable to express conditional rules depending on ownership, relationships, or environment. ABAC evaluates policy against real-time subject/resource/action/environment attributes, expressing those conditional rules directly at the cost of policy-engine complexity and attribute-sourcing reliability. A real Java demonstration showed three users with an identical RBAC role producing identical (and two incorrect) authorization outcomes under RBAC, but correct, differentiated outcomes under ABAC once ownership, team, and time attributes were considered.

## Key Takeaways

- AuthN answers "who"; AuthZ answers "may they" — and AuthZ is a decision that can and should recur at multiple system layers, not a single gate.
- RBAC cannot express conditional rules dependent on ownership, relationships, or environment — only "increasingly narrow roles," which scales poorly and still can't express some rules (like "not the author") at all.
- ABAC evaluates policy against subject/resource/action/environment attributes at request time, directly expressing rules RBAC cannot.
- "We need a new role for every combination of conditions" is the practical signal a system has outgrown RBAC for that specific rule.
- RBAC and ABAC are not mutually exclusive — coarse-grained RBAC combined with targeted ABAC rules is a common, practical architecture.

## Cheat Sheet

| | AuthN | AuthZ |
|---|---|---|
| Question | Who is this? | May they do this? |
| Frequency | Once per session (typically) | Potentially many times per request, per layer |
| Failure status | 401 | 403 |

| | RBAC | ABAC |
|---|---|---|
| Basis | Static role membership | Real-time subject/resource/action/environment attributes |
| Expresses conditional rules? | No (only via role proliferation) | Yes, directly |
| Audit complexity | Low (read the table) | Higher (evaluate policy against hypothetical attributes) |
| Infrastructure needed | Simple lookup | Policy-evaluation engine |

## Flashcards

**Q: What does a 401 response mean versus a 403?**
A: 401 = not authenticated (re-authenticate); 403 = authenticated but not authorized (re-authentication won't help).

**Q: Why can't RBAC express "a manager may approve only their own direct report's request"?**
A: It's a relationship between the specific subject and the specific resource, not a static property of the subject's role — no fixed role can encode a per-instance relationship.

**Q: What's the practical signal that a system has outgrown RBAC for a specific rule?**
A: Needing a new, increasingly narrow role for every combination of conditions — this is "role explosion," and it indicates an attribute-based rule is needed instead.

## Practice Exercises

1. Reproduce `RbacVsAbacDemo.java` and add a fourth condition to the ABAC policy: the approver's clearance level must be at least the change's risk tier. Confirm the RBAC side of the demo remains unable to express this at all.
2. Extend the demo with a `ReBAC`-style ("relationship-based") twist: allow approval if the approver's team has previously approved at least one prior change authored by the same author (a trust-relationship condition) — this illustrates a third model (relationship-based access control) beyond RBAC and ABAC.

## Solutions

1. Add a `Set<String> clearanceOk` check gated on `approver.clearanceLevel() >= change.riskTier()` inside `abacAllow`; RBAC's `ROLE_PERMISSIONS` map has no attribute slot for a numeric clearance level at all, confirming the structural limitation directly.
2. This requires tracking approval history as additional resource-side state consulted during the policy evaluation — a good illustration that ABAC's "environment/resource attributes" can include derived, historical data, not just static fields on the current request.

## Additional Reading

- [NIST SP 800-162 — Guide to Attribute Based Access Control (ABAC) Definition and Considerations](https://csrc.nist.gov/pubs/sp/800/162/final)

## Official References

- [NIST SP 800-162 — Guide to Attribute Based Access Control (ABAC)](https://csrc.nist.gov/pubs/sp/800/162/final)
