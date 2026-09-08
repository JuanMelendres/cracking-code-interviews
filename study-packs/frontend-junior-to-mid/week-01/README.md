---
title: "Frontend Junior → Mid, Week 1 — The Web, JavaScript, and TypeScript, From Zero"
document_type: study-pack
week: 1
track: frontend-junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 1 — The Web, JavaScript, and TypeScript, From Zero

## Weekly Outcome

By the end of this week you can explain the request/response cycle behind loading a web page, read and write semantic HTML and CSS box-model/flex/grid layout, write correct JavaScript using closures, `this`, the event loop, and `==`/`===`, and read and write typed TypeScript using interfaces and generics — all before any React.

## Why This Week Matters

Every later week in this pack, and almost every chapter in [`syllabus/21-frontend-web/`](../../../syllabus/21-frontend-web/), is written in JavaScript/JSX (mostly TypeScript) and silently assumes you can already read it. This week is the floor everything else stands on — [How the Web Works](../../../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) (F-001), [JavaScript Fundamentals](../../../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) (F-002), and [TypeScript Fundamentals](../../../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) (F-003) exist specifically because this domain's own "Beginner tier" never taught them before a 2026-09-08 audit found the gap.

## Prerequisites

None from this repository — but this pack assumes you have used a computer and a text editor before. If you have never written a line of code in any language, budget extra time for F-002's closures and event-loop sections; they are the densest material in the week.

## Schedule

| Day | Focus |
|---|---|
| Mon | [How the Web Works](../../../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) (F-001) — read in full, reproduce the served page and `curl` transcript |
| Tue–Wed | [JavaScript Fundamentals](../../../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) (F-002) — read in full, run all seven scripts yourself |
| Thu–Fri | [TypeScript Fundamentals](../../../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) (F-003) — read in full, reproduce the `tsc` error-then-fix pair |
| Sat | Practice exercises from all three chapters (their own Interview Questions / exercises sections) |
| Sun | Review checklist below, self-check against each chapter's own mastery expectations |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | How the Web Works: HTML, CSS, the DOM, and HTTP (F-001) | [`syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md`](../../../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) |
| 2 | JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony (F-002) | [`syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md`](../../../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md) |
| 3 | TypeScript Fundamentals: Types, Interfaces, and Generics (F-003) | [`syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md`](../../../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md) |

## Hands-On Exercises

Every demo below is real and was executed this same week (2026-09-08) — reproduce each one yourself rather than reading the captured output.

- [`practice/frontend/web-fundamentals/`](../../../practice/frontend/web-fundamentals/) — a real served static site (`index.html`, `styles.css`, `app.js`) and a real captured `curl -v` transcript (`curl-transcript.txt`) covering four requests, including a 404. Note: the transcript was captured over a Unix-domain socket rather than TCP because this sandbox blocks TCP loopback — the chapter's own "Real Verified Demos" section explains why the HTTP semantics are identical either way.
- [`practice/frontend/javascript-fundamentals/`](../../../practice/frontend/javascript-fundamentals/) — 7 real, executed Node.js v24.18.0 scripts (`variablesAndScope.js`, `thisBinding.js`, `closures.js`, `eventLoop.js`, `equality.js`, `arraysObjectsDestructuring.js`, `mathUtils.js`/`moduleDemo.js`), with the full real transcript captured in `output.txt`.
- [`practice/frontend/typescript-fundamentals/`](../../../practice/frontend/typescript-fundamentals/) — 7 real TypeScript 6.0.3 files checked with `tsc`, including a genuine error-then-fix pair: `tsc-output-before-fix.txt` (a real captured compiler error) and `tsc-output-after-fix.txt` (the same project compiling clean after the fix).

## Interview Answer Drills

For each of the three chapters' own Interview Questions sections, answer the Beginner-tier questions out loud, unprompted, before checking the chapter's own expected answer. Pay particular attention to F-002's closures/event-loop questions and F-003's `interface` vs. `type` question — these recur constantly in early-career frontend screens.

## Coding Problems

None dedicated this week — Week 1 is conceptual floor-building in plain JavaScript/TypeScript, not algorithmic patterns. Coding-pattern practice for this domain, where it exists, lives alongside the backend track's own DSA material; this pack's own coding focus starts once React and Next.js fundamentals are in place.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — this pack is technical-fundamentals-focused throughout; behavioral interview preparation lives in [`syllabus/20-interview-preparation/behavioral/`](../../../syllabus/20-interview-preparation/behavioral/) as its own, separate track.

## Mock Interview

Self-check only this week: pick 3 Interview Questions at random across the three chapters (not the ones you just reviewed) and answer them cold, out loud, timing yourself to under 90 seconds each.

## Review Checklist

- [ ] Can explain, unprompted, the difference between `==` and `===`, why the temporal dead zone produces a `ReferenceError`, and why a `var` loop variable captured in a closure prints `[3, 3, 3]` instead of `[0, 1, 2]`.
- [ ] Reproduced the `curl -v` transcript against the served page and can explain what `Content-Length` and `Content-Type` are actually for.
- [ ] Reproduced the `tsc` error-then-fix pair and can explain, in your own words, why `interface` and `type` overlap but are not identical.

## Completion Criteria

- [ ] All 7 JavaScript scripts run on your own machine and produce the same output shown in `output.txt`.
- [ ] `tsc` reports zero errors on the TypeScript project after your own reproduction of the fix.
- [ ] Can write a small `interface`, a generic function constrained with `extends`, and a discriminated union from a blank file, without copying from the chapter.

## Retrospective

Note which of each chapter's Common Mistakes sections you personally hit while reproducing the demos, and why — this is the most useful thing to review before Week 2 builds React directly on top of this material.

## Next Week

[Week 2 — The Actual Floor of React](../week-02/README.md).
