---
title: "Reflected XSS From a Bypassed Default-Escaping Path"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md
source: syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md#production-scenarios
---

# Reflected XSS From a Bypassed Default-Escaping Path

## Context

A search page displays "Showing results for: `<query>`" using the query parameter directly.

## Symptoms

A security review (or a real bug bounty report) finds that the display executes injected script when the query parameter contains specific markup.

## Impact

An attacker can craft a URL that, once clicked by a victim, executes arbitrary JavaScript in that victim's authenticated session — session hijacking, credential theft, or unauthorized actions performed as the victim.

## Initial Hypotheses

- A framework-level bug — checked, the underlying template engine correctly escapes by default; this specific line bypassed it.
- A browser-specific quirk — checked, reproduces identically across browsers, since it's a real HTML-parsing behavior, not an implementation quirk.
- The query value was inserted into the page without going through the framework's default escaping path — correct.

## Evidence

Locating the exact rendering call shows a "raw HTML" or `dangerouslySetInnerHTML`-style escape hatch, used for a legitimate reason elsewhere, reused here by habit.

## Investigation Timeline

1. Security review or bug-bounty report flags injected-script execution on the search results display.
2. Framework-bug and browser-quirk hypotheses ruled out.
3. Exact rendering call located, showing a raw-HTML escape hatch bypassing default escaping.

## Root Cause

The presence or absence of escaping before insertion is the entire, single variable controlling whether identical input executes as script or displays as text — this line bypassed it.

## Immediate Mitigation

Restore default escaping for this specific output, or, if raw HTML insertion is genuinely required, route it through a real, dedicated HTML-sanitization library rather than raw string concatenation.

## Permanent Fix

Audit the codebase for other uses of the same "raw HTML" escape hatch, and add a CSP header (`script-src 'self'`, no `'unsafe-inline'`) as an independent second layer that can block a class of exploitation even if a future escaping bug slips through.

## Alternatives Considered

Relying on CSP alone, without fixing the escaping bug — rejected: CSP would only block some exploitation techniques (inline script) and provides no protection against payloads that don't require inline script execution at all.

## Trade-offs

Defense-in-depth (both escaping and CSP) costs real, upfront configuration effort for the CSP policy, which must be kept in sync with the application's actual, legitimate script sources — worthwhile given that either layer alone leaves a real gap the other closes.

## Prevention

Default to framework-provided escaping everywhere; treat any "raw HTML insertion" code path as a reviewed, justified exception, not a convenience; add CSP as a real, independent second layer, not a substitute for the first.

## Monitoring and Alerts

- A static-analysis/lint rule flagging any use of a raw-HTML-insertion API (`dangerouslySetInnerHTML` or equivalent) for mandatory security review.
- CSP violation reporting (`report-uri`/`report-to`) wired to alerting, so an attempted inline-script exploitation is visible even if it's blocked.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent finding.

- **Situation:** a search page was found reflecting unescaped user input as executable script.
- **Task:** find the exact point escaping was bypassed and close the gap without breaking the legitimate feature that needed raw HTML elsewhere.
- **Action:** located the specific rendering call, restored default escaping, and added a CSP header as a second, independent layer.
- **Result:** closed the specific vulnerability and added a defense-in-depth layer against future escaping mistakes.

## Staff-Level Discussion

A single escaping bug in one output path is a real, common, findable-in-review mistake — the concrete fix (restore escaping) and the concrete second layer (CSP) are both real, specific, demonstrable defenses, not a vague "be careful with user input." The organizational lesson is that any raw-HTML escape hatch, once introduced for one legitimate reason, tends to get reused by habit elsewhere without the same justification — a lint rule catches this class of drift structurally, rather than relying on every future reviewer to notice.

## Related Handbook Chapters

- [Frontend Security: XSS, CSRF, and CSP](../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) — the canonical escaping/CSP defense-in-depth pattern behind this incident's fix.
