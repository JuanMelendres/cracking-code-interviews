---
title: "Interview Question Bank — Plan"
document_type: project-planning-document
status: pilot in progress
version: 1.0
last_updated: 2026-09-13
depends_on:
  - syllabus-transformation-plan.md
---

# Interview Question Bank — Plan

## Origin

User request (2026-09-13): each of the 22 `syllabus/` domains needs its own
compendium of roughly 150 interview questions, organized by seniority tier
(Junior, Mid, Senior, Staff) with a correct explanation of what's expected at
each tier — not a flat question list. Questions that recur across domains
should be cross-linked or consolidated rather than duplicated per domain.

## What this is, and what it is not

This is a **rapid-review question compendium** — a high-volume, scannable
list a candidate uses to self-test broad coverage and calibrate what depth a
given seniority actually requires. It is explicitly **not** a replacement for
each canonical chapter's own `## Interview Questions` section, which stays
the authoritative, deep treatment (expected answer, minimum acceptable
answer, Senior/Staff extensions, common mistakes, follow-ups, evaluation
criteria) per `CLAUDE.md`'s Interview Question Standard. The compendium
entry for a question that already has that deep treatment **links to it**
rather than re-deriving it — canonical-ownership rule, no duplication.

## Sourcing discipline — no fabrication

Per `CLAUDE.md`'s prohibition on shallow, invented question lists, each
domain's compendium is built primarily by **mining this project's own
already-real, already-verified material** for that domain, not writing 150
new questions from a blank page:

1. Every canonical chapter's own `## Interview Questions` section (already
   deep, already real).
2. Every canonical chapter's own `## Flashcards` section (lighter-weight,
   already real).
3. That domain's mock-interview round(s) under `practice/mock-interviews/`,
   where one exists.
4. Only once those sources are exhausted, a small number of new questions
   may be added to round out seniority-tier coverage that's genuinely thin
   (see the honesty note below) — always grounded in the same chapter's real
   content, never invented independent of it.

**Honesty over quota.** Several `06-databases` chapters, for example, target
`senior`/`staff` only in their own front matter (`target_levels`) — real
Junior/Mid-level questions for those specific chapters may be genuinely
thin, and the pilot reports the real achieved count per domain rather than
padding to exactly 150 with low-value filler questions.

## Structure per domain file

`syllabus/20-interview-preparation/question-bank/<domain-slug>.md`, one file
per `syllabus/` domain. Within it, questions are grouped by the domain's own
chapters/topics, and each question shows:

- The question itself.
- A link to the canonical chapter section with its full, deep treatment.
- A short **"What's expected at each level"** block: Junior / Mid / Senior /
  Staff, describing the real, different shape and depth of a correct answer
  at each tier for *this specific question* — not a generic, repeated
  boilerplate paragraph.

## Cross-domain relation handling

Where a question's substance genuinely spans two domains (e.g., a
connection-pool-exhaustion question touching both `06-databases` and
`05-spring`), it is placed in the domain that owns the deepest canonical
treatment, and cross-linked from the other domain's compendium file rather
than duplicated — the same canonical-ownership discipline the rest of this
repository already follows for cheat sheets and flashcards.

## Status

- **Pilot (2026-09-13):** `06-databases` — chosen because it was just
  content-depth-audited this same session, so its real question inventory is
  freshest. See `syllabus/20-interview-preparation/question-bank/06-databases.md`.
- **Remaining 21 domains:** not started. Do not begin scaling to the rest
  until the pilot's format, depth, and honest-count approach are reviewed
  and approved.
