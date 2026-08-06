---
title: "Flashcards: Multi-Tenancy Isolation Models"
slug: multi-tenancy-isolation-models
document_type: flashcard-deck
domain: security
topic_id: T-1307
canonical: ../handbook/security/multi-tenancy-isolation-models.md
last_updated: 2026-08-06
---

# Flashcards: Multi-Tenancy Isolation Models

**Canonical chapter:** [`handbook/security/multi-tenancy-isolation-models.md`](../handbook/security/multi-tenancy-isolation-models.md)

## Card: The key weakness of application-level tenant_id filtering

**Prompt:**
What's the key weakness of application-level `tenant_id` filtering as the sole isolation mechanism?

**Answer:**
It requires every single query, in every code path, forever, to correctly apply the filter — one missed instance is a direct cross-tenant leak.

**Why it matters:**
The structural reason RLS is preferred over relying on application-code discipline alone.

**Common trap:**
Treating consistent application-level `tenant_id` filtering as sufficient isolation on its own.

**Related:**
[handbook/security/multi-tenancy-isolation-models.md](../handbook/security/multi-tenancy-isolation-models.md)

## Card: Does RLS guarantee isolation unconditionally

**Prompt:**
Does enabling Row-Level Security guarantee isolation unconditionally?

**Answer:**
No — database roles with superuser or `BYPASSRLS` status bypass RLS entirely; the guarantee depends on which role each code path actually uses to connect.

**Why it matters:**
The specific, easy-to-miss gap that makes an RLS-only claim of isolation incomplete without a role audit.

**Common trap:**
Assuming RLS provides isolation without auditing which database roles are exempt from it.

**Related:**
[handbook/security/multi-tenancy-isolation-models.md](../handbook/security/multi-tenancy-isolation-models.md)

## Card: What an RLS query returns with an unset tenant context

**Prompt:**
What does an RLS-protected query return when the tenant-context session variable is unset?

**Answer:**
Zero rows (fail-closed), not an error and not all rows — because `tenant_id = NULL` is never true in SQL's three-valued logic.

**Why it matters:**
Confirms RLS's default behavior fails safely rather than silently leaking data when the context is missing.

**Common trap:**
Assuming a missing tenant-context variable would either error loudly or return every tenant's rows.

**Related:**
[handbook/security/multi-tenancy-isolation-models.md](../handbook/security/multi-tenancy-isolation-models.md)
