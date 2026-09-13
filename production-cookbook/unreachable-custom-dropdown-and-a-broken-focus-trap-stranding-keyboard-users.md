---
title: "Unreachable Custom Dropdown and a Broken Focus Trap Stranding Keyboard Users"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-accessibility.md
source: syllabus/21-frontend-web/react-accessibility.md#production-scenarios
---

# Unreachable Custom Dropdown and a Broken Focus Trap Stranding Keyboard Users

## Context

A settings page has a custom dropdown widget built from `div`s with `onClick` handlers and no `tabIndex`/keyboard support, plus a different, separate custom widget with a hand-rolled focus trap.

## Symptoms

A support ticket reports a user "gets stuck" on the settings page and can't reach the Save button.

## Impact

Every keyboard-only and screen-reader user hits this identically — not a rare edge case, but a systemic barrier for an entire user segment.

## Initial Hypotheses

- A rare browser-specific bug affecting one user — the initial, low-priority framing, disproven immediately by direct keyboard-only reproduction.
- A single, isolated widget issue — checked, and found to actually be two separate, compounding problems, not one.
- The custom dropdown is entirely unreachable via Tab, and a nearby custom widget's hand-rolled focus trap never releases focus once entered — correct.

## Evidence

A real keyboard-only test, using `document.activeElement` verification, shows the custom dropdown is entirely unreachable via Tab, and confirms the nearby widget's focus trap genuinely strands keyboard users inside it with no release path.

## Investigation Timeline

1. Support ticket reports a user unable to reach a page's Save button.
2. Initial framing as a rare, low-priority bug reconsidered once a direct keyboard-only test is performed.
3. `document.activeElement`-based verification confirms two separate real issues: an unreachable dropdown and a genuinely broken focus trap.

## Root Cause

The dropdown was built from non-semantic `div`s with no keyboard support at all; the separate widget's focus trap was implemented by hand without verifying its release path, leaving keyboard users with no way to exit it.

## Immediate Mitigation

Provide an alternate, keyboard-accessible path to the Save action (or the settings the dropdown controls) as a stopgap while both widgets are fixed.

## Permanent Fix

Fix the dropdown with a semantic-HTML-first approach (a real `<button>`/`<select>` base, or full ARIA plus keyboard-handler parity if a fully custom widget is unavoidable); fix the broken trap using the same three-behaviors-independently-verified discipline (confirm entry, trap boundaries, and an actual escape/release path, all separately verified).

## Alternatives Considered

Patching only the reported symptom (the specific stuck state) without a broader keyboard-only audit of the page — rejected, since the dropdown's total unreachability was a separate, unreported issue that a narrow fix would have left in place.

## Trade-offs

Retrofitting accessibility onto already-shipped custom widgets is real, non-trivial work — the cheaper long-term fix is defaulting to native elements and well-tested patterns from the start, rather than treating accessibility as a later audit-and-patch phase.

## Prevention

Verify every custom interactive widget with a real keyboard-only test (not just automated tooling) before shipping, and default to native semantic elements unless a fully custom widget is genuinely necessary.

## Monitoring and Alerts

- A required keyboard-only manual test pass (Tab-only navigation through the full flow) as part of the pre-release checklist for any new interactive widget.
- An automated accessibility linter catching missing `tabIndex`/keyboard handlers on custom interactive elements, as an early, if incomplete, signal ahead of manual verification.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent bug.

- **Situation:** a support ticket reporting one user "stuck" on a page turned out to reveal two separate accessibility failures affecting an entire user segment.
- **Task:** determine the true scope of the problem rather than treating it as a rare, isolated bug.
- **Action:** performed a direct keyboard-only test, confirming both an entirely unreachable dropdown and a genuinely broken focus trap.
- **Result:** fixed both issues using semantic-HTML-first and independently-verified trap-release patterns, and added mandatory keyboard-only testing to the release checklist.

## Staff-Level Discussion

A single user's bug report reframed as a low-priority edge case turned out to affect every keyboard-only and screen-reader user identically — the organizational lesson is that accessibility bug reports deserve the same "does this affect an entire user segment" scrutiny as any other systemic bug, not a default assumption of rarity. Defaulting to native elements and well-tested patterns from the start is the durable fix; retrofitting accessibility later is real, avoidable, non-trivial cost.

## Related Handbook Chapters

- [React Accessibility](../syllabus/21-frontend-web/react-accessibility.md) — the canonical keyboard-only verification method behind this incident's fix.
