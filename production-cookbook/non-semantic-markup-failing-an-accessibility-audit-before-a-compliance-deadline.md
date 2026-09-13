---
title: "Non-Semantic Markup Failing an Accessibility Audit Before a Compliance Deadline"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md
source: syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md#production-scenarios
---

# Non-Semantic Markup Failing an Accessibility Audit Before a Compliance Deadline

## Context

A team builds a landing page entirely out of `<div>`s with CSS classes doing all visual differentiation — a `<div class="page-title">` instead of `<h1>`, a `<div class="nav-links">` instead of `<nav>`.

## Symptoms

Visually, sighted QA testers see no problem: fonts, colors, and spacing all look correct. A scheduled accessibility audit, required ahead of a public-sector contract, runs a screen reader against the page and fails outright.

## Impact

No navigable landmark structure exists at all, so a screen-reader user has no way to jump to the main content or skip repeated navigation; heading-based screen-reader navigation returns nothing, since no real `<h1>`–`<h6>` elements exist anywhere on the page.

## Initial Hypotheses

- A screen-reader compatibility bug in a third-party component — checked, the page uses no such components; the markup itself is the issue.
- An audit tooling misconfiguration — checked, the same result reproduces with multiple screen readers.
- The markup uses generic containers instead of semantically correct elements — correct.

## Evidence

Inspecting the DOM shows zero landmark or heading elements anywhere, despite every element visually resembling a title, a heading, or a navigation region.

## Investigation Timeline

1. Accessibility audit fails outright ahead of a compliance deadline.
2. Screen-reader-bug and tooling-misconfiguration hypotheses ruled out.
3. DOM inspected directly, confirming zero semantic landmark/heading elements exist.

## Root Cause

"Looks right" and "is semantically correct" are different, unrelated properties of the same markup — every visual review that ran before the audit checked only the former.

## Immediate Mitigation

None needed beyond the fix itself — no user-facing regression occurred; the gap was invisible until the audit specifically checked for it.

## Permanent Fix

Swap the generic containers for the semantically correct elements they were already visually styled to look like — real headings, a real `<nav>` — touching no CSS.

## Alternatives Considered

Adding ARIA roles on top of the existing `<div>`-based structure instead of using native elements — considered, but native elements were simpler and required no ongoing ARIA-attribute maintenance for behavior browsers and screen readers already provide by default.

## Trade-offs

None meaningful — the fix took an afternoon and changed no visual output at all.

## Prevention

Include a screen-reader or automated accessibility-audit pass (not only visual QA) in the standard pre-launch checklist for any public-facing page, specifically because visual review structurally cannot catch this class of gap.

## Monitoring and Alerts

- An automated accessibility linter (e.g., checking for heading hierarchy and landmark presence) wired into CI, catching this class of regression before a scheduled audit ever needs to.
- A periodic, not just pre-launch, accessibility audit cadence for any page in active development.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent audit finding.

- **Situation:** a page passed every visual QA check but failed an accessibility audit outright ahead of a compliance deadline.
- **Task:** find why a visually correct page had zero accessibility support.
- **Action:** inspected the DOM directly, confirming no semantic landmarks or headings existed anywhere despite the correct visual appearance.
- **Result:** swapped generic containers for semantically correct elements, passing the audit with no visual change and an afternoon's work.

## Staff-Level Discussion

The organizational lesson is that "looks correct" and "is correct" are genuinely separate properties for markup, and no amount of visual QA rigor will ever catch a semantic gap — only a check that specifically exercises the semantic layer (a screen reader, an automated accessibility linter) can. Building that check into the standard pre-launch process, not treating it as a separate compliance-only gate, is what prevents this exact deadline-adjacent scramble from recurring.

## Related Handbook Chapters

- [How the Web Works: HTML, CSS, DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) — the canonical semantic-HTML mechanics behind this incident's fix.
