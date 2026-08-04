---
title: "Design Exercise — Authentication Service"
week: 7
last_reviewed: 2026-07-31
---

# Design Exercise — Authentication Service

**45 minutes, timed, full six-phase method.** Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Authentication Service](../../architecture-atlas/authentication-service.md). This file is the Week 7 study-pack entry point; the full worked exercise is now canonical there.

## Table of Contents

1. [Phase 1 — Clarify](#phase-1--clarify)
2. [Phase 2 — Estimate](#phase-2--estimate)
3. [Phase 3 — API](#phase-3--api)
4. [Phase 4 — Data](#phase-4--data)
5. [Phase 5 — Architecture](#phase-5--architecture)
6. [Phase 6 — Bottlenecks](#phase-6--bottlenecks)
7. [Exit check](#exit-check)

---

## Phase 1 — Clarify

Issue/validate tokens, support logout — social login and MFA internals explicitly out of scope. Issuance is low-volume; validation is extremely high-volume. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/authentication-service.md#problem-statement).

## Phase 2 — Estimate

2M logins/day → ~69 peak issuance QPS vs. ~50,000 peak validation QPS — a ~700x asymmetry that drives the whole design. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/authentication-service.md#capacity-assumptions).

## Phase 3 — API

Validation is not a network call downstream services make per-request. Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/authentication-service.md#apis).

## Phase 4 — Data

Users/credentials relational; refresh tokens revocable; access tokens (JWTs) not stored at all. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/authentication-service.md#data-model).

## Phase 5 — Architecture

Local JWT signature verification, no call back to the Auth Service, justified by the 700x asymmetry. Full diagram: canonical entry [§ Architecture Diagram](../../architecture-atlas/authentication-service.md#architecture-diagram).

## Phase 6 — Bottlenecks

Refresh-token store scale, the revocation gap, and key rotation — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/authentication-service.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] The issuance-vs-validation asymmetry stated explicitly in Phase 2 and traced through to the local-verification architecture decision in Phase 5
- [ ] The revocation gap named as a bottleneck, connecting explicitly to `03-oauth2-oidc-and-jwt.md`
- [ ] Key rotation named as a real operational bottleneck, not glossed over
