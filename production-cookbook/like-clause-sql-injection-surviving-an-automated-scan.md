---
title: "LIKE-Clause SQL Injection Surviving an Automated Scan"
document_type: production-cookbook-entry
domain: security
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/12-security/injection-input-validation-output-encoding.md
source: handbook/security/injection-input-validation-output-encoding.md#production-scenarios
---

# LIKE-Clause SQL Injection Surviving an Automated Scan

## Context

A search feature builds a query by string-concatenating a user-supplied search term directly into a SQL `LIKE` clause. The feature previously passed a security review's automated SQL-injection scanner cleanly.

## Symptoms

A manual review, conducted separately from the automated scan, finds the search feature is genuinely vulnerable to SQL injection, despite having passed the automated scanner.

## Impact

A real, exploitable vulnerability existed in production, undetected by the security process that was supposed to catch exactly this class of defect, for the entire period between the scan and the manual review.

## Initial Hypotheses

- The automated scanner missed the vulnerability due to a configuration gap — investigated directly against the scanner's actual test coverage.
- The vulnerability requires an attack pattern specific to how the search term is embedded, one the scanner's standard test patterns don't happen to exercise — correct.

## Evidence

The scanner only tests common attack patterns against common field names, and the vulnerability here requires an attack pattern specific to how the search term is embedded — inside a `LIKE '%...%'` wildcard context, requiring `%` escaping in addition to quote-breaking — which the scanner's generic patterns don't specifically target.

## Investigation Timeline

1. **Manual review conducted independently of the automated scan**, as part of a separate, more thorough security pass.
2. **Vulnerability confirmed manually**, contradicting the earlier clean automated-scan result.
3. **Scanner's test coverage reviewed**, finding it tests common attack patterns against common field names, not the specific `LIKE`-wildcard-context pattern this vulnerability requires.
4. **Conclusion reached**: the scanner's incompleteness, not a scanner bug or misconfiguration, explains the gap.

## Root Cause

Automated scanning is a useful but incomplete defense — the scanner only tests common attack patterns against common field names, and this vulnerability required an attack pattern specific to how the search term is embedded, one the scanner never happened to test.

## Immediate Mitigation

Manually verify and patch the specific vulnerable query path identified by the manual review.

## Permanent Fix

Parameterize the query, including correctly escaping `LIKE` wildcard characters as data rather than pattern syntax — the underlying architectural fix, which closes the entire vulnerability class regardless of which specific attack pattern a scanner does or doesn't happen to test.

## Alternatives Considered

Adding the specific `LIKE`-wildcard attack pattern to the automated scanner's test suite as a point fix. Accepted as a complementary improvement, but explicitly not treated as sufficient alone — the architectural fix (parameterization) closes the vulnerability class structurally, while a scanner-pattern addition only closes the one specific variant that happened to be found.

## Trade-offs

None — parameterized queries have no meaningful downside versus string concatenation; the fix is strictly an improvement.

## Prevention

Treat automated scanning as one layer of defense, not a sufficient one — string-concatenated SQL construction should be flagged in code review regardless of scanner results, and the architectural fix (parameterization) should be the default for any new query, not a response reserved for scanner findings.

## Monitoring and Alerts

- A static-analysis or lint rule flagging any string concatenation into a SQL query string directly, independent of and in addition to dynamic scanner results — this catches the underlying pattern class rather than relying on any specific scanner's test coverage.
- Manual security review scheduled as a standing, recurring practice for query-construction code paths, not treated as redundant with automated scanning, given the demonstrated gap between what a scanner tests and what an actual attacker might try.

## Interview Story

This maps to "your SQL injection scan passed, is the code actually safe" — a direct test of whether a candidate treats automated scanning as sufficient. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a search feature passed an automated SQL-injection scan but was found genuinely vulnerable during a manual review.
- **Task:** explain the gap between a clean automated result and a real vulnerability.
- **Action:** review the scanner's actual test coverage against the specific attack pattern the vulnerability requires; recognize the scanner tests common patterns against common field names, not every possible embedding context.
- **Result:** parameterized the query with correct `LIKE`-wildcard escaping, closing the entire vulnerability class rather than only the one variant the manual review happened to find.

## Staff-Level Discussion

The most important lesson here is epistemic, not technical: "the scanner passed" is evidence of the absence of the specific vulnerability patterns the scanner tests for, not evidence of the absence of vulnerabilities in general — a distinction easy to blur in practice when a clean scan result is treated as a sign-off. This generalizes to any automated quality or security gate: a tool's coverage is always bounded by what its authors anticipated, and any code pattern outside that anticipated space is invisible to it regardless of how thorough the tool otherwise is. A Staff engineer should push for architectural fixes (parameterized queries, output encoding at a small number of centralized utilities) that close entire vulnerability classes structurally, rather than treating scanner-driven, pattern-by-pattern remediation as sufficient — the fix should not depend on a scanner happening to test the right pattern.

## Related Handbook Chapters

- [Injection, Input Validation, and Output Encoding](../syllabus/12-security/injection-input-validation-output-encoding.md) — canonical parameterization and `LIKE`-wildcard-escaping mechanics used here.
- [OWASP Top 10 for Backend Services](../syllabus/12-security/owasp-top-10-for-backend-services.md) — the broader injection risk category (A03) this incident is an instance of.
