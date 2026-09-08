---
title: "Frontend Mid → Senior, Week 6 — Testing Habits and Typing React Correctly"
document_type: study-pack
week: 6
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 6 — Testing Habits and Typing React Correctly

## Weekly Outcome

By the end of this week you can write a behavior-focused React Testing Library test (not an implementation-detail test), and type a generic component with a discriminated-union prop pattern correctly.

## Why This Week Matters

Direct parallel to the backend's own test-strategy chapter — testing habits at Senior depth. TypeScript-with-React now has a real prerequisite (Junior-to-Mid's TypeScript Fundamentals), so this week applies plain TypeScript to React-specific typing problems rather than teaching both at once.

## Prerequisites

[Frontend Junior → Mid](../../../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md)'s TypeScript Fundamentals topic.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [React Testing: RTL Philosophy, Mocking, and E2E with Playwright](../../../syllabus/21-frontend-web/react-testing.md) (F-118) |
| Thu–Sat | [TypeScript with React: Generic Components and Discriminated Unions](../../../syllabus/21-frontend-web/react-typescript.md) (F-119) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | React Testing (F-118) | [`syllabus/21-frontend-web/react-testing.md`](../../../syllabus/21-frontend-web/react-testing.md) |
| 2 | TypeScript with React (F-119) | [`syllabus/21-frontend-web/react-typescript.md`](../../../syllabus/21-frontend-web/react-typescript.md) |

## Hands-On Exercises

Real demos exist at [`practice/frontend/react-testing/`](../../../practice/frontend/react-testing/) (includes real E2E tests, per its `e2e/` directory) and [`practice/frontend/react-typescript/`](../../../practice/frontend/react-typescript/) (both predate this pack; not re-verified here).

## Interview Answer Drills

Answer, out loud: "why does RTL discourage querying by class name or test ID as a first choice?" and "how do you type a component prop that's a discriminated union of 3 variants correctly?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: write an RTL test for a form component (renders, user types, submits, assertion) using only accessible queries (`getByRole`, `getByLabelText`), cold, in under 20 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Reproduced both real demos, including at least one E2E test run.
- [ ] Can type a generic component with `<T>` correctly from a blank file.

## Completion Criteria

- [ ] Wrote a behavior-focused RTL test (not implementation-detail) for a component of your own.
- [ ] Correctly typed a discriminated-union prop pattern, unaided.

## Retrospective

Note whether your instinct was to test implementation details (state, internal function calls) before this week — RTL's philosophy is a real mindset shift, not just a new API.

## Next Week

[Week 7 — The State-Management Decision Framework, and Streaming the App Router](../week-07/README.md).
