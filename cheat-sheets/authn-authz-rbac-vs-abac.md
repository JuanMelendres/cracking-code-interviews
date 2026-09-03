---
title: "Cheat Sheet: AuthN vs AuthZ, RBAC vs ABAC"
slug: authn-authz-rbac-vs-abac
document_type: cheat-sheet
domain: security
topic_id: T-1302
canonical: ../handbook/security/authn-authz-rbac-vs-abac.md
last_updated: 2026-08-05
---

# AuthN vs AuthZ, RBAC vs ABAC

**Canonical chapter:** [`syllabus/12-security/authn-authz-rbac-vs-abac.md`](../syllabus/12-security/authn-authz-rbac-vs-abac.md)

## Core Mental Model

A building with a front desk and many internal doors. **Authentication** is the front desk checking your ID and issuing a badge — happens once per session, and everything downstream trusts the badge without re-checking. **Authorization** is every individual door deciding whether *this specific badge* may open *this specific door* *right now* — it can happen many times, at many doors, and each door may need context beyond "what badge type" (RBAC) — like which floor you're on, the time, or a relationship to the resource (ABAC).

## Essential Definitions

- **AuthN** — verifying a claimed identity (credentials against a trusted source). Answers "who."
- **AuthZ** — deciding whether an already-authenticated identity may perform a specific action on a specific resource. Answers "may they." Can recur at multiple layers (gateway, service, data row) per request.
- **RBAC** — grants permissions via static role membership, decided in advance, independent of request context.
- **ABAC** (NIST SP 800-162) — evaluates policy rules against subject/resource/action/environment attributes at request time.

## Decision Table

| Situation | Model |
|---|---|
| Permission depends only on "what kind of actor is this" | RBAC — simpler to build, audit, reason about |
| Rule is conditional on ownership, relationship, or environment (time, location) | ABAC (or a narrow attribute layer on top of existing RBAC) |
| "We need a new role for every combination of conditions" | Signal that ABAC, not more roles, is the right tool |
| 401 vs 403 | 401 = not authenticated, re-auth helps; 403 = authenticated but not authorized, re-auth won't help |

**Trade-offs:** RBAC is trivially auditable (read the table) but cannot express conditional/relationship rules without unmanageable role proliferation. ABAC expresses those rules directly but needs a policy engine, reliable attribute sourcing, and is harder to audit ("what can this user do" requires evaluating policy against hypothetical attributes).

## Key Numbers (real, executed — `RbacVsAbacDemo.java`)

Three users share the identical `engineer` role:

```
RBAC: alice/bob/carol all rbacAllow(deploy:approve) = true   <- cannot see 'own change' or 'wrong team'

ABAC @ 14:00 (business hours):
alice team=payments abacAllow(chg-42) = false   <- correctly denies: author of the change
bob   team=payments abacAllow(chg-42) = true    <- correctly allows: same team, not author
carol team=search    abacAllow(chg-42) = false   <- correctly denies: wrong team

ABAC: bob approving the SAME change @ 02:00 = false   <- only the time attribute changed
```

Identical user, identical change, identical role — only an environment attribute (time) differs — produces a different, correct decision. RBAC structurally cannot represent this.

## Common Pitfalls

- Saying "authentication is login, authorization is permissions" — textbook-correct but shallow; misses that AuthZ recurs at multiple layers with different available context each time.
- Treating a rule RBAC can't express as "add a narrower role" instead of recognizing the structural limitation.
- Confusing 401 with 403, misleading clients into retrying the wrong remediation.
- Assuming ABAC is strictly "better" without acknowledging its policy-engine and attribute-sourcing costs.

## Interview Answer Skeleton

**30-sec:** AuthN verifies who a request claims to be; AuthZ decides, separately and potentially multiple times per request, whether that identity may act. RBAC grants static role-based permissions; ABAC evaluates policy against subject/resource/action/environment attributes at request time, expressing conditional rules RBAC structurally cannot.

**2-min:** Add why ABAC exists (many real rules are conditional — ownership, team, time, relationship to resource history — in ways a static role table can't express without unmanageable role proliferation) + the trade-off (RBAC simpler to audit, ABAC needs a policy engine and reliable attributes) + the real demo evidence (identical role, three users, RBAC gives one wrong answer for all three; ABAC correctly differentiates using team/author/time attributes).

**Whiteboard:** Front desk labeled "AuthN: verify identity, issue badge (once)." Three doors downstream, each "AuthZ: may this badge open this door (checked separately)." Under one door, an RBAC lookup table "badge type → allowed doors." Under another, an ABAC rule box "badge type AND owns-this-room AND business-hours → allow," with the same badge type producing different outcomes by room/time.

**Staff-level framing:** "role explosion" (needing a narrower role for every condition combination) is the practical signal a system has outgrown RBAC for that rule. RBAC and ABAC are not mutually exclusive — coarse RBAC plus targeted ABAC rules is common in practice. Design AuthZ as a decision made deliberately at each layer with layer-appropriate context, never a single upstream gate assumed to cover everything downstream.

## Production Warning Signs

- A compliance audit flags a broad role (e.g., "support-agent can read all orders") as over-provisioned — RBAC's static grant is technically correct for the legitimate use case but grants far more than any single interaction needs; an ABAC policy scoped to "has an open ticket referencing this order" fixes it without breaking the team's workflow.
- A separation-of-duties incident (someone approved their own transaction) traces back to RBAC alone — "not the same person who did X" is a per-instance subject-resource relationship no static role can encode; this is not an RBAC bug, it's a category RBAC cannot express at all.
- **Prevention:** default to RBAC for coarse actor-type gating; the moment a requirement mentions ownership, a relationship, or an environment condition, route it to an attribute-based check instead of a new role.

## Related

- `syllabus/12-security/owasp-top-10-for-backend-services.md`
- `syllabus/12-security/oauth2-oidc-and-jwt.md`
- `syllabus/12-security/multi-tenancy-isolation-models.md`
