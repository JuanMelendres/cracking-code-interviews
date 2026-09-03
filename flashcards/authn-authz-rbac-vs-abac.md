---
title: "Flashcards: AuthN vs AuthZ, RBAC vs ABAC"
slug: authn-authz-rbac-vs-abac
document_type: flashcard-deck
domain: security
topic_id: T-1302
canonical: ../handbook/security/authn-authz-rbac-vs-abac.md
last_updated: 2026-08-06
---

# Flashcards: AuthN vs AuthZ, RBAC vs ABAC

**Canonical chapter:** [`syllabus/12-security/authn-authz-rbac-vs-abac.md`](../syllabus/12-security/authn-authz-rbac-vs-abac.md)

## Card: 401 vs 403

**Prompt:**
What does a 401 response mean versus a 403?

**Answer:**
401 = not authenticated (re-authenticate); 403 = authenticated but not authorized (re-authentication won't help).

**Why it matters:**
The precise, testable distinction between two sequential security gates.

**Common trap:**
Treating both as generic "access denied" responses with no distinction.

**Related:**
[handbook/security/authn-authz-rbac-vs-abac.md](../syllabus/12-security/authn-authz-rbac-vs-abac.md)

## Card: Why RBAC can't express a per-instance relationship

**Prompt:**
Why can't RBAC express "a manager may approve only their own direct report's request"?

**Answer:**
It's a relationship between the specific subject and the specific resource, not a static property of the subject's role — no fixed role can encode a per-instance relationship.

**Why it matters:**
The structural reason RBAC has a hard ceiling that ABAC exists to raise.

**Common trap:**
Trying to express a subject-resource relationship by inventing an ever-narrower role instead of an attribute-based condition.

**Related:**
[handbook/security/authn-authz-rbac-vs-abac.md](../syllabus/12-security/authn-authz-rbac-vs-abac.md)

## Card: The signal a system has outgrown RBAC

**Prompt:**
What's the practical signal that a system has outgrown RBAC for a specific rule?

**Answer:**
Needing a new, increasingly narrow role for every combination of conditions — this is "role explosion," and it indicates an attribute-based rule is needed instead.

**Why it matters:**
A concrete, observable warning sign rather than an abstract design principle.

**Common trap:**
Continuing to add ever-more-specific roles instead of recognizing role explosion as the signal to switch models.

**Related:**
[handbook/security/authn-authz-rbac-vs-abac.md](../syllabus/12-security/authn-authz-rbac-vs-abac.md)
