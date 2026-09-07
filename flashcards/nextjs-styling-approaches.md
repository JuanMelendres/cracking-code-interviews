---
title: "Flashcards: Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified"
slug: nextjs-styling-approaches
document_type: flashcard-deck
domain: frontend
topic_id: F-302
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-styling-approaches.md
last_updated: 2026-09-07
---

# Flashcards: Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-styling-approaches.md`](../syllabus/21-frontend-web/nextjs-styling-approaches.md)

## Card: Does Tailwind's JIT engine understand whether a class name is actually applied to an element?

**Prompt:**
Does Tailwind's real class-generation mechanism know the difference between a utility class actually used in a `className` and the same string appearing elsewhere in a file (a comment, a string)?

**Answer:**
No — verified with two real, reproduced tests. A utility class mentioned ONLY in a code comment, with no corresponding `className` usage anywhere, was genuinely generated into the production CSS. More consequentially, the default scan reaches the WHOLE project, not just source code — this app's own `README.md`, documenting the finding in prose, leaked its own mentioned class names into the real build until an explicit `source(none)` + `@source` fix was applied.

**Why it matters:**
A real, non-obvious source of unexpectedly large Tailwind bundles — searching only `className` usages, or only source code, when auditing bundle size will miss this class of cause.

**Common trap:**
Assuming Tailwind's purge mechanism works like JavaScript dead-code elimination.

**Related:**
[Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified](../syllabus/21-frontend-web/nextjs-styling-approaches.md)

## Card: Can you inspect styled-components' generated CSS via `<style>.textContent`?

**Prompt:**
For styled-components (v6), does reading a `<style>` element's `.textContent` reveal the CSS rules it has generated?

**Answer:**
No — verified directly. A real test found `.textContent` empty even though genuine, distinct CSS rules existed. styled-components inserts rules via the CSSOM's `insertRule` API directly; `document.styleSheets[n].cssRules` is what actually reveals them.

**Why it matters:**
A real, easy-to-miss trap when debugging or writing tooling that tries to inspect CSS-in-JS output programmatically.

**Common trap:**
Assuming every style-injection mechanism populates `<style>.textContent`.

**Related:**
[Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS, Verified](../syllabus/21-frontend-web/nextjs-styling-approaches.md)
