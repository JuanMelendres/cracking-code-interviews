---
title: "Week 7 Study Pack — Manifest"
week: 7
plan: B
last_reviewed: 2026-07-31
---

# Week 7 Study Pack — Manifest

**Topics:** T-506, T-501, T-511, T-512, T-513 · **Plan:** B (first week beyond Plan A's 6-week sprint)
**Files:** 11 (+ this manifest) · **Total words:** 5,496 (real count, `wc -w` over all 11 files; updated 2026-07-31 — files 04–09 and README trimmed of redundant prose and worked-example padding, no canonical-chapter link applicable since they're practice/mock/checklist content, not duplicated topic explanation)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 730 |
| 2 | `01-spring-auto-configuration-and-lifecycle.md` | T-506/501 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/spring/auto-configuration-and-bean-lifecycle.md` | 657 |
| 3 | `02-spring-security-filter-chain.md` | T-511 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/spring/security-filter-chain.md` | 502 |
| 4 | `03-oauth2-oidc-and-jwt.md` | T-512/513 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/security/oauth2-oidc-and-jwt.md` | 657 |
| 5 | `04-java-coding-practice.md` | LC 46 (errata #3 fix), 78, 39, 22, all compiled and run | 783 |
| 6 | `05-flashcards.md` | 14 cards | 387 |
| 7 | `06-security-chain-trace-deliverable.md` | Template + worked example | 478 |
| 8 | `07-week-7-mock-interview.md` | 45-min Spring technical round | 251 |
| 9 | `08-design-exercise-authentication-service.md` | Full six-phase design | 578 |
| 10 | `09-week-7-checklist.md` | Day-by-day checklist | 243 |
| 11 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 230 |

---

## Verification

| Item | Status |
|---|---|
| Java — backtracking | **Executed.** OpenJDK 21.0.12. `12/12` assertions pass, including the errata #3 reproduction (buggy `permute` finds **zero** permutations on duplicate-value input, not just fewer). Source: `practice/java/week-07/backtracking/` |
| Java — Spring internals | **Executed.** Spring Framework 6.1.14, plain jars, no Maven/Boot. Real observed bean-lifecycle callback order; real `@Async`+`@Transactional` behavior (12ms return, exception invisible to caller, correct rollback confirmed by row count). Source: `practice/java/week-07/spring-internals/` |
| Java — security | **Executed.** Real HMAC-SHA256 JWT sign/verify/tamper/expiry (`javax.crypto`, no external library); real 3-scenario filter-chain trace with genuine short-circuiting. Source: `practice/java/week-07/security/` |
| OAuth2/OIDC flow | **Conceptual, stated explicitly.** A faithful multi-party demo (authorization server, resource server, real redirect flow) was out of scope for this pack's time budget — said so directly in `03-oauth2-oidc-and-jwt.md` rather than presenting the flow as executed |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

| # | Defect (from `CHANGELOG.md`'s errata register) | Status |
|---|---|---|
| 3 | Backtracking `permute` uses `contains()`, wrong on duplicate inputs | **Fixed and verified here** — `04-java-coding-practice.md`, `practice/java/week-07/backtracking/src/PermuteBuggy.java` / `BacktrackingProblems.java`. Real result is stronger than the original description: the buggy version finds **zero** permutations on any duplicate-value input, not merely an undercount. |

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
