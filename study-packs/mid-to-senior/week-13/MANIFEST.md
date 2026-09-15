---
title: "Mid → Senior, Week 13 — Manifest"
week: 13
track: mid-to-senior
last_reviewed: 2026-09-15
---

# Week 13 — Manifest

**Domain:** Security (deep dive, continuing Week 9). **Priority topics:** CSRF, CORS, and Session Security; Enterprise SSO: SAML, Federated Identity, and Commercial IAM Integration. **Track:** Mid → Senior, Week 13 of 13 (final week).
**Files:** 1 (+ this manifest) — no chapter content duplicated.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercise, cookbook cross-references, review checklist |

## Verification

| Item | Location | Status |
|---|---|---|
| CSRF/CORS `practice/` demo | `practice/java/week-17/csrf-cors-session/` | Confirmed to exist at manifest write time (2026-09-15), matches the chapter's own cited path |
| Both cookbook cross-references | `production-cookbook/cors-mistaken-for-an-authentication-boundary-on-an-internal-endpoint.md`, `production-cookbook/custom-header-csrf-protection-reopened-by-a-legacy-form-endpoint.md` | Confirmed to exist at manifest write time |
| No cookbook entry for SSO/SAML | `production-cookbook/` | Confirmed absent via direct search, not assumed |

## Scope note

Both chapters were added to `12-security` after Week 9 was originally scheduled (CSRF/CORS on 2026-09-10, Enterprise SSO/SAML on 2026-09-14) and were never folded into any study pack despite the domain's own `INDEX.md` already listing them — this week closes that gap, added during a 2026-09-15 meta-documentation consistency pass across `syllabus/00-overview/` and `study-packs/`. Scheduled as a single week (not split) since both are real but bounded topics, comparable in scope to Week 9's own two-security-topic half.

## Integrity note

No production-cookbook entry cited for SSO/SAML — confirmed absent, not assumed, and stated honestly in the README rather than a placeholder, per this pack's own established convention (Week 12's equivalent note). No assertion counts cited beyond what each source chapter's own text already states.
