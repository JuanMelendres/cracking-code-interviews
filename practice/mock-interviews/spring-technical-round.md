---
title: "Mock Interview: Spring Technical Round (45 min)"
slug: spring-technical-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Bean lifecycle order and the exact proxy-creation hook
  - "@Async + @Transactional visibility gotcha"
  - Security filter chain tracing, including a rejection scenario
  - Honest JWT revocation
  - PKCE vs. client secret, distinct attack surfaces
  - Design review/RFC or technical debt advocacy story
related:
  - ../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md
  - ../../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md
  - ../../syllabus/05-spring/security-filter-chain.md
  - ../../syllabus/12-security/oauth2-oidc-and-jwt.md
  - ../../syllabus/20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md
  - ../../syllabus/20-interview-preparation/behavioral/11-technical-debt-advocacy.md
source: ../../study-packs/week-07/07-week-7-mock-interview.md
official_references: []
---

# Mock Interview: Spring Technical Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-07/07-week-7-mock-interview.md`. Like the Weeks 1/2/4 rounds, this source uses a `Part A — Candidate script` / `Part B — Interviewer script` structure with no inline per-question pass/fail signals; the Evaluator Section below constructs pass/borderline/fail signals grounded in the interviewer script's own cues and the real, measured content of the canonical `handbook/spring/` and `syllabus/12-security/oauth2-oidc-and-jwt.md` chapters (both carry provenance notes stating their demos are real, executed Spring Framework 6.1.14 / `javax.crypto` output, not invented).

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Bean lifecycle order + proxy-creation hook | Q1 | [Spring Auto-Configuration and Bean Lifecycle](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md) |
| `@Async` + `@Transactional` visibility gotcha | Q2 | [Spring Auto-Configuration and Bean Lifecycle](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md) |
| Security filter chain trace + rejection scenario | Q3 | [Spring Security Filter Chain](../../syllabus/05-spring/security-filter-chain.md) |
| Honest JWT revocation | Q4 | [OAuth2, OIDC, and JWT](../../syllabus/12-security/oauth2-oidc-and-jwt.md) |
| PKCE vs. client secret | Q5 | [OAuth2, OIDC, and JWT](../../syllabus/12-security/oauth2-oidc-and-jwt.md) |
| Design review/technical debt story | Q6 | [Design Reviews and RFCs](../../syllabus/20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md), [Technical Debt Advocacy](../../syllabus/20-interview-preparation/behavioral/11-technical-debt-advocacy.md) |

## Interviewer Opening Script

*"This is a 45-minute Spring-focused technical round. I'll ask about bean lifecycle, a well-known async/transactional gotcha, security filter tracing, and JWT/OAuth2 mechanics, then close with a story. I'm listening for precise mechanism, not just correct conclusions — 'it works' isn't enough, I want to know exactly why. Let's start with the bean lifecycle."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(6 min)** Walk the full bean lifecycle order, unprompted, then explain where a `@Transactional` proxy is actually created in that sequence.
2. **(8 min)** "Explain why `@Transactional` on an `@Async` method behaves unexpectedly." Give the full answer, with real numbers if you have them.
3. **(8 min)** Trace a request through a security filter chain, using a worked example.
4. **(8 min)** "Explain JWT revocation honestly." Give the full answer, including both mitigations and their costs.
5. **(8 min)** "Why PKCE if you already have a client secret?"
6. **(7 min)** Deliver a design-review/RFC story or a technical debt advocacy story using the four-beat structure.

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Bean lifecycle order + proxy-creation hook

**Ideal answer outline:** constructor → `BeanPostProcessor.postProcessBeforeInitialization` → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom init-method → `BeanPostProcessor.postProcessAfterInitialization`, at which point the bean is ready for use. The `@Transactional` proxy is created at the `postProcessBeforeInitialization` step — *before* `@PostConstruct` even runs — which is why the proxy, not the raw bean, is what gets injected everywhere else in the container. On the follow-up ("why that callback and not `@PostConstruct`?"): a `BeanPostProcessor` needs to run before any bean's own initialization to have a chance to wrap it; by the time `@PostConstruct` runs, wrapping would be too late for the proxy to intercept the bean's own init logic too.
**Common weak answers:** reciting an approximate order without stating specifically where the proxy is created; assuming the proxy is created lazily at first method call rather than during bean initialization.
**Pass signal:** states the full order correctly and identifies `postProcessBeforeInitialization` specifically as the proxy-creation hook, with the "why that one and not `@PostConstruct`" reasoning.
**Borderline signal:** states an approximately correct order but can't pinpoint the exact hook, or names the hook without explaining why it must run before `@PostConstruct`.
**Fail signal:** cannot state the lifecycle order at all, or believes the proxy is created some other way (e.g., at class-loading time, or lazily).

### Question 2 — `@Async` + `@Transactional`

**Ideal answer outline:** the transaction itself is correct — it starts and, on failure, correctly rolls back, on whatever thread the async executor runs the method on. The surprise is purely about *visibility*: a `void`-returning `@Async` method returns to its caller immediately, before the transactional work even executes, so any failure — including a correct rollback — happens invisibly on a background thread. If interrupted with "is the transaction wrong, or is something else wrong?": correctly redirects to visibility, not correctness.
**Common weak answers:** claiming the transaction itself is broken or "doesn't work" under `@Async`.
**Pass signal:** correctly separates transactional correctness from caller visibility, and (ideally) names the fix — returning `CompletableFuture<T>` so the caller can observe the outcome, or a global `AsyncUncaughtExceptionHandler` as a safety net.
**Borderline signal:** senses something's off but, when interrupted, still leans toward "the transaction is wrong" before self-correcting.
**Fail signal:** insists the transaction itself doesn't work, even after the interrupt.

### Question 3 — Security filter chain trace

**Ideal answer outline:** traces an ordered chain (e.g., CORS → CSRF → authentication → authorization → controller), explaining that any filter can short-circuit by returning a response directly instead of calling the next filter. On the specific-rejection-scenario follow-up: distinguishes a 401 (authentication failed — no valid principal established, chain stops at the authentication filter, controller never reached) from a 403 (authentication succeeded, authorization failed — principal known but not permitted, chain stops one gate later).
**Common weak answers:** describing only the happy path with no rejection scenario; conflating authentication and authorization into a single check.
**Pass signal:** traces the chain correctly, names both short-circuit points, and gives a specific, correct rejection scenario (not just the happy path) when asked.
**Borderline signal:** traces the happy path correctly but the rejection scenario is vague or conflates 401/403.
**Fail signal:** cannot trace the chain, or treats authentication and authorization as one combined step.

### Question 4 — JWT revocation, honestly

**Ideal answer outline:** a valid, non-expired JWT cannot be revoked, because verification is a pure computation over the token's own bytes and the verifier's key — it never consults external state, so "has this account been suspended" is a question the verification step structurally cannot answer. Two honest mitigations, neither free: short expiry + refresh tokens (bounds the exposure window, doesn't make the token revocable), or a server-side deny-list checked at verification time (solves revocability directly, but reintroduces the stateful lookup a JWT was chosen to avoid). If the candidate says "yes, you can revoke it" without qualification, the correct push is: "how, exactly — what changes at verification time?"
**Common weak answers:** claiming JWTs "can be revoked" with no named mechanism, implying it's free; or the opposite overconfident answer, "JWTs are completely stateless, full stop," with no mitigation offered.
**Pass signal:** correctly states JWTs can't be revoked without extra machinery, and — under the push — names at least one specific mitigation with its cost.
**Borderline signal:** correctly states JWTs can't simply be revoked but can't name a concrete mitigation even when pushed.
**Fail signal:** claims JWTs can just be revoked, with no stateful mechanism named, even after the push.

### Question 5 — Why PKCE if you already have a client secret?

**Ideal answer outline:** they protect different attack surfaces — a client secret protects the token-exchange endpoint (proving the client itself is who it claims to be when redeeming a code); PKCE protects the authorization code specifically from interception in transit (e.g., a malicious app registered for the same redirect URI on a mobile OS intercepting the code) — an attacker who intercepts the code still can't exchange it for a token without the original `code_verifier`, which never left the legitimate client.
**Common weak answers:** treating PKCE and a client secret as redundant or solving the same problem.
**Pass signal:** correctly names the code-interception attack surface PKCE protects, distinct from the token-exchange surface a client secret protects.
**Borderline signal:** senses they're different mechanisms but can't articulate which specific attack each stops.
**Fail signal:** claims PKCE and the client secret do the same thing.

### Question 6 — Design review/technical debt story

**Ideal answer outline:** a four-beat, clearly structured story (situation, action, the specific decision/argument made, and the outcome or cost) for either a design review/RFC the candidate drove or shaped, or a technical debt argument they advocated for.
**Common weak answers:** a story with no clear structure, or one that describes the review/debt item without stating the candidate's own specific contribution or argument.
**Pass signal:** clear four-beat structure with the candidate's specific role and reasoning explicit, scored per Technical Depth and Production Judgment (the two dimensions the source specifies for this round).
**Borderline signal:** the story is coherent but the candidate's specific argument or decision criterion has to be extracted through follow-up.
**Fail signal:** no clear structure, or the candidate's own role in the outcome is unclear.

## Scoring Rubric

Per the source mock's own instruction, score this round using the [shared six-dimension rubric](../../study-packs/week-01/10-week-1-evaluation-rubric.md)'s **Technical Depth** and **Production Judgment** dimensions specifically (1–5 scale, 3 = Mid, 4 = Senior, 5 = Staff) — the source names these two dimensions explicitly rather than all six, reflecting this round's technical-mechanism focus.

## Debrief Guide

Walk the candidate through their scores, starting with the weakest. Questions 2 and 4 share a theme worth naming directly: both ask the candidate to separate "this mechanism is broken" from "this mechanism works exactly as designed, and the surprise is a specific, nameable limitation" — a transaction that's correct but invisible, and a token that's valid but unrevocable. A candidate who reaches for "it's broken" on both is pattern-matching to a general discomfort with async/stateless mechanisms rather than reasoning from how each one actually works. Questions 3 and 5 share a different theme: both require naming which of two adjacent-looking mechanisms handles which specific concern (authentication vs. authorization; client secret vs. PKCE) — a candidate who conflates both pairs has a general "adjacent mechanisms aren't interchangeable" gap.

## Remediation Recommendations

- Weak Q1 → re-read [Spring Auto-Configuration and Bean Lifecycle](../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md), specifically its real, executed lifecycle trace.
- Weak Q2 → re-read the same chapter's `@Async`+`@Transactional` section and its real 12ms-return demo trace.
- Weak Q3 → re-read [Spring Security Filter Chain](../../syllabus/05-spring/security-filter-chain.md) and its real 401/403 traces.
- Weak Q4 or Q5 → re-read [OAuth2, OIDC, and JWT](../../syllabus/12-security/oauth2-oidc-and-jwt.md), specifically its JWT revocation and PKCE sections.
- Weak Q6 → re-read [Design Reviews and RFCs](../../syllabus/20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md) or [Technical Debt Advocacy](../../syllabus/20-interview-preparation/behavioral/11-technical-debt-advocacy.md), matching whichever story type was told.
- Any dimension scored below Senior (4) overall → retake this mock in full after remediation.
