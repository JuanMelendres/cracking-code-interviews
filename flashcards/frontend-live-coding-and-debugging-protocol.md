---
title: "Flashcards: Frontend Live-Coding & Debugging Protocol"
slug: frontend-live-coding-and-debugging-protocol
document_type: flashcard-deck
domain: 21-frontend-web
topic_id: "N/A (playbook-technical-answer, not a numbered register topic — see chapter's own scope note)"
canonical: ../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md
last_updated: 2026-09-12
---

# Flashcards: Frontend Live-Coding & Debugging Protocol

**Canonical chapter:** [`syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md`](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md)

## Card: The two frontend round formats

**Prompt:**
Name the two distinct frontend live-coding round formats and each one's primary risk.

**Answer:**
Build (risk: over-engineering the component tree before running anything) and Debug (risk: guessing a fix from reading code instead of reproducing the bug first).

**Why it matters:**
Each format needs a different opening move — a build round starts with a decomposition plan; a debug round starts with reproduction.

**Common trap:**
Applying a build-round mindset to a debug round, jumping to a fix before confirming the bug is real.

**Related:**
[Frontend Live-Coding & Debugging Protocol](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md)

## Card: The highest-leverage phase

**Prompt:**
Which phase of the frontend-adapted protocol is called out as the single highest-leverage, most frequently skipped phase?

**Answer:**
Phase 5 — testing in the real browser, not just by reading the code.

**Why it matters:**
A plausible-looking React component can still have a real, visible bug that only running it reveals.

**Common trap:**
Reasoning about correctness purely from the source and never opening the browser until the very end, or not at all.

**Related:**
[Frontend Live-Coding & Debugging Protocol](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md)

## Card: Why stating expected render behavior matters

**Prompt:**
Why does the protocol require stating expected render behavior (phase 3) *before* building, rather than just checking render behavior at the end?

**Answer:**
Phase 3's upfront statement is what makes phase 6's confirmation meaningful — without a stated prediction, "checking DevTools at the end" has nothing concrete to confirm or contradict, turning verification into an afterthought rather than a real, self-caught check.

**Why it matters:**
Mirrors the backend protocol's predict-then-confirm structure (complexity analysis before coding, verification after) in a frontend-specific shape.

**Common trap:**
Treating the expected-render-behavior statement as optional busywork rather than the mechanism that makes later verification meaningful.

**Related:**
[Frontend Live-Coding & Debugging Protocol](../syllabus/21-frontend-web/frontend-live-coding-and-debugging-protocol.md)
