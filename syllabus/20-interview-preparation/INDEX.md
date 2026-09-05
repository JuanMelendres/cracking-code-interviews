---
title: "Interview Preparation — Domain Index"
document_type: syllabus-domain-index
domain: 20-interview-preparation
status: 19 of 19 mapped chapters physically relocated (Phase 3, 2026-09-03); mock-interviews/ and company-prep/ deliberately not relocated; L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Interview Preparation

The interview-application layer: behavioral (from `behavioral-handbook/`, relocated as-is), coding/system-design/technical-answers craft (from `interview-playbook/`), mock interviews (referenced from `practice/mock-interviews/`, not relocated), and one private company-specific prep guide (`company-prep/`, permanently excluded from any public/commercial build, per the Section 2.8 decision approved 2026-09-03).

> **Phase 3 update (2026-09-03).** All 15 `behavioral-handbook/` chapters plus 4 `interview-playbook/` entries (one each from `behavioral/`, `coding/`, `system-design/` ×2, `technical-answers/` ×2 — 5 files total, see table below) have physically relocated via `git mv`, preserving file history. `behavioral-handbook/` no longer exists as a directory, since every file it held has moved. `interview-playbook/` still exists — it now holds only `README.md` (rewritten to reflect the move) and the untouched, private `company-prep/` subdirectory.
>
> **Two things deliberately were not moved**, per the plan's own rules: `practice/mock-interviews/` (12 real mock-interview transcripts) stays at its current path per Section 7.4 (`practice/` never relocates) — see the `mock-interviews/` subsection below for the reference-only index. `interview-playbook/company-prep/nordstrom-senior-backend-remote.md` stays exactly where it is, per the plan's own Section 7.2 sample mapping ("private — flagged, not migrated by default") and the Section 14 open-question-3 decision (approved 2026-09-03): this is a permanent, tooling-enforced private category, not something this migration touches.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 21 chapters across `behavioral/` (16), `coding/` (1), `system-design/` (2), and `technical-answers/` (2) gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted immediately after "Why This Matters in Interviews" (or, for the four `playbook-technical-answer`-template entries, after "Why This Exists") per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject, using 21 distinct analogies (an ER intake form, a mechanic's labeled socket set, a photograph crop, a doctor's differential diagnosis, buying a real car vs. a dream car, a debate club's steelman drill, teaching a kid to ride a bike, a chef sending back a dish, a neighborhood fence-repair pitch, vascular bypass surgery, a landlord roof-repair pitch, a book editor catching a plot hole, a diplomat's toast, house-hunting with different informants, a used-car purchase, a touring comedian's setlist notebook, a driving instructor's commentary drive, a timed dinner party, a live city-map tour guide, a restaurant tasting menu, and a hiking-trail daylight constraint — no analogy repeated). Every chapter also gained `topic_id` (T-1501 through T-1515, T-1604, T-1419, T-1601, T-1505/T-916 — `time-boxing-and-mid-round-changes.md` has no dedicated T-code of its own, being an explicit companion entry to T-801/T-802, so `topic_id` was deliberately left unset there rather than fabricated) and `mastery_levels_covered: [L1, L2, L3, L4]` front matter. Verified via a Python pass: 1 H1 per file, valid YAML, exactly one Level 1/Level 2 heading pair each, and every relative link resolving on disk — zero broken links found across all 21 files. **`20-interview-preparation` is now fully L1–L4 (21/21)** — the sixteenth fully-retrofitted domain in the syllabus.

## Topics — behavioral/

| Title | Old path | New path |
|---|---|---|
| STAR Framework and Delivery Mechanics | `behavioral-handbook/01-star-framework-and-delivery.md` | `syllabus/20-interview-preparation/behavioral/01-star-framework-and-delivery.md` |
| Story Portfolio Design | `behavioral-handbook/02-story-portfolio-design.md` | `syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md` |
| Scope, Impact, and Influence Framing | `behavioral-handbook/03-scope-impact-and-influence-framing.md` | `syllabus/20-interview-preparation/behavioral/03-scope-impact-and-influence-framing.md` |
| Production Incident Narratives | `behavioral-handbook/04-production-incident-narratives.md` | `syllabus/20-interview-preparation/behavioral/04-production-incident-narratives.md` |
| Architecture Trade-off Narration | `behavioral-handbook/05-architecture-trade-off-narration.md` | `syllabus/20-interview-preparation/behavioral/05-architecture-trade-off-narration.md` |
| Conflict and Technical Disagreement | `behavioral-handbook/06-conflict-and-technical-disagreement.md` | `syllabus/20-interview-preparation/behavioral/06-conflict-and-technical-disagreement.md` |
| Mentoring and Developing Others | `behavioral-handbook/07-mentoring-and-developing-others.md` | `syllabus/20-interview-preparation/behavioral/07-mentoring-and-developing-others.md` |
| Failure and Learning Narratives | `behavioral-handbook/08-failure-and-learning-narratives.md` | `syllabus/20-interview-preparation/behavioral/08-failure-and-learning-narratives.md` |
| Cross-Team Influence Without Authority | `behavioral-handbook/09-cross-team-influence-without-authority.md` | `syllabus/20-interview-preparation/behavioral/09-cross-team-influence-without-authority.md` |
| Migrations and Large Technical Change | `behavioral-handbook/10-migrations-and-large-technical-change.md` | `syllabus/20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md` |
| Technical Debt Advocacy | `behavioral-handbook/11-technical-debt-advocacy.md` | `syllabus/20-interview-preparation/behavioral/11-technical-debt-advocacy.md` |
| Design Reviews and RFCs | `behavioral-handbook/12-design-reviews-and-rfcs.md` | `syllabus/20-interview-preparation/behavioral/12-design-reviews-and-rfcs.md` |
| Company-Specific Frameworks | `behavioral-handbook/13-company-specific-frameworks.md` | `syllabus/20-interview-preparation/behavioral/13-company-specific-frameworks.md` |
| Questions to Ask Your Interviewer | `behavioral-handbook/14-questions-to-ask-your-interviewer.md` | `syllabus/20-interview-preparation/behavioral/14-questions-to-ask-your-interviewer.md` |
| Offer Evaluation and Negotiation | `behavioral-handbook/15-offer-evaluation-and-negotiation.md` | `syllabus/20-interview-preparation/behavioral/15-offer-evaluation-and-negotiation.md` |
| Behavioral Interview-Day Logistics: Loop Structures and Question-Pattern Recognition (T-1604) | `interview-playbook/behavioral/company-loop-structures-and-question-pattern-recognition.md` | `syllabus/20-interview-preparation/behavioral/company-loop-structures-and-question-pattern-recognition.md` |

## Topics — coding/

| Title | Old path | New path |
|---|---|---|
| Coding Interview Communication Protocol (T-1419) | `interview-playbook/coding/coding-interview-communication-protocol.md` | `syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md` |

## Topics — system-design/

| Title | Old path | New path |
|---|---|---|
| System Design Interview Delivery: Time-Boxing and Mid-Round Changes | `interview-playbook/system-design/time-boxing-and-mid-round-changes.md` | `syllabus/20-interview-preparation/system-design/time-boxing-and-mid-round-changes.md` |
| System Design Narration and Whiteboard Discipline (T-1602) | `interview-playbook/system-design/system-design-narration-and-whiteboard-discipline.md` | `syllabus/20-interview-preparation/system-design/system-design-narration-and-whiteboard-discipline.md` |

## Topics — technical-answers/

| Title | Old path | New path |
|---|---|---|
| The Technical Answer Framework — Nine Layers (T-1601) | `interview-playbook/technical-answers/technical-answer-framework.md` | `syllabus/20-interview-preparation/technical-answers/technical-answer-framework.md` |
| Trade-off Narration and Architecture Decision Records (T-1505/T-916) | `interview-playbook/technical-answers/trade-off-narration-and-adrs.md` | `syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md` |

## mock-interviews/ (referenced, not relocated)

`practice/mock-interviews/` (12 real mock-interview transcripts plus its own README) stays at its current path per the plan's Section 7.4 — `practice/` never relocates, to avoid multiplying broken-link risk across a directory that's already flat and discoverable by filename. Referenced here, not duplicated:

- [`practice/mock-interviews/`](../../practice/mock-interviews/README.md)

## company-prep/ [PRIVATE — not relocated]

`interview-playbook/company-prep/nordstrom-senior-backend-remote.md` stays exactly where it is. See the note above.

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
