---
title: "Frontend Junior → Mid, Week 6 — Production-Shaped UI"
document_type: study-pack
week: 6
track: frontend-junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 6 — Production-Shaped UI

## Weekly Outcome

By the end of this week you can build a validated form (controlled or uncontrolled, with a clear reason for the choice), handle a component failure gracefully with an error boundary, and build a page that is genuinely accessible — semantic HTML, ARIA where needed, keyboard navigation, and focus management.

## Why This Week Matters

This is the pack's closing week: three genuinely production-shaped UI problems, closing the Junior-to-Mid ladder. Accessibility is deliberately included here rather than deferred to the Senior pack — it is cheapest to build in from day one, and builds directly on Week 1's semantic-HTML foundation.

## Prerequisites

Weeks 1–5.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [React Forms: Controlled vs. Uncontrolled, Validation Strategy](../../../syllabus/21-frontend-web/react-forms.md) (F-114) |
| Wed | [React Error Boundaries and Error Handling Strategy](../../../syllabus/21-frontend-web/react-error-boundaries.md) (F-115) |
| Thu–Fri | [React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management](../../../syllabus/21-frontend-web/react-accessibility.md) (F-116) |
| Sat | Practice exercises from all three chapters |
| Sun | Review checklist below, self-check against every chapter's Mastery Checklist across the whole pack |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | React Forms (F-114) | [`syllabus/21-frontend-web/react-forms.md`](../../../syllabus/21-frontend-web/react-forms.md) |
| 2 | React Error Boundaries (F-115) | [`syllabus/21-frontend-web/react-error-boundaries.md`](../../../syllabus/21-frontend-web/react-error-boundaries.md) |
| 3 | React Accessibility (F-116) | [`syllabus/21-frontend-web/react-accessibility.md`](../../../syllabus/21-frontend-web/react-accessibility.md) |

## Hands-On Exercises

Each chapter cites its own real, verified demo — [`practice/frontend/react-forms/`](../../../practice/frontend/react-forms/), [`practice/frontend/react-error-handling/`](../../../practice/frontend/react-error-handling/), [`practice/frontend/react-accessibility/`](../../../practice/frontend/react-accessibility/) (all predate this pack; not re-verified here). Run each locally and reproduce at least one real failure/fix pair per chapter (a validation error, a caught component crash, a keyboard-navigation gap).

## Interview Answer Drills

Answer, out loud: "when do you choose an uncontrolled form over a controlled one?", "what does an error boundary NOT catch?", and "give a concrete example of a component that looks fine visually but fails a screen reader."

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: build a small form (2–3 fields, one async submit, one error boundary wrapping it, fully keyboard-navigable) from a blank file, timed at 40 minutes.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists.
- [ ] Reproduced a real failure/fix pair from each of the three practice demos.
- [ ] Completed the full-pack retrospective below.

## Completion Criteria

- [ ] Built the mock-interview form exercise above, unaided.
- [ ] Can state what an error boundary does and does not catch (e.g., event-handler errors, async errors) unprompted.
- [ ] Can identify at least 2 real accessibility violations in a given markup snippet.

## Retrospective

This is the pack's final retrospective: review every prior week's retrospective note (list-key intuition, hook difficulty, styling preference) and confirm each is now resolved before moving on to [Frontend Mid → Senior](../../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md).

## Next Week

This is the last week of the Frontend Junior → Mid pack. Continue with [Frontend Mid → Senior](../../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md).
