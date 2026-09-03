---
title: "Cheat Sheet: Next.js Styling Approaches"
slug: nextjs-styling-approaches
document_type: cheat-sheet
domain: frontend
topic_id: F-302
tier: Intermediate
canonical: ../handbook/frontend/nextjs-styling-approaches.md
last_updated: 2026-09-03
---

# Next.js Styling Approaches

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-styling-approaches.md`](../syllabus/21-frontend-web/nextjs-styling-approaches.md)

The register names the trap this chapter avoids: "trade-offs, not just preference." All three approaches built side by side in one real app and tested for actual, distinguishing mechanical behavior.

## Core Mental Model

Three real, different places the same problem — "which CSS rule applies to this element, safely" — gets solved. CSS Modules solves it at BUILD TIME by rewriting class names to file-scoped unique strings. Tailwind ALSO solves it at build time, but by scanning source files as plain TEXT for anything that looks like a utility class name — not semantic analysis. CSS-in-JS (styled-components) solves it at RUNTIME, generating class names and rules as the app actually renders, based on props.

## Essential Definitions

- **CSS Modules** — any `.module.css` file; every class name rewritten to a unique, hashed string at build time; zero runtime cost.
- **Tailwind** — emits utility classes on demand by textually scanning files for class-name-shaped strings; does not parse JSX or track actual `className` usage.
- **CSS-in-JS (styled-components)** — generates real class names and inserts CSS rules into the CSSOM via `insertRule()` at render time, based on props.
- **CSSOM vs. `textContent`** — styled-components v6 inserts rules directly into the CSSOM; `<style>.textContent` reads empty regardless of real rules existing.

## Decision Table

| Question | Choice |
|---|---|
| Bespoke, low-dynamism CSS with strong scoping, zero runtime cost? | CSS Modules |
| UI built mostly from spacing/color/layout primitives, comfortable auditing the scan mechanism? | Tailwind |
| Styles must respond to runtime data (props, theme, computed state)? | CSS-in-JS — the only one of the three with a native mechanism for this |
| Auditing an unexpectedly large Tailwind bundle? | Search ALL text in scanned files, not just `className` usages |

## Key Numbers (real, grepped build output and CSSOM inspection)

- Two files' identically-named `.title` class compiled to genuinely different real names: `_title_18b5g_1` and `_title_1uj7y_1`.
- A Tailwind utility mentioned ONLY in a code comment (zero `className` usage): genuinely generated into the production CSS.
- Tailwind's default scan reached the project's own `README.md`, leaking its documented class-name mentions into the build — confirmed by removing the file and watching both classes vanish; fixed with `source(none)` + explicit `@source`.
- A single `Badge` styled-component rendered with/without a prop produced two different real CSSOM rules: `background: seagreen` vs. `background: crimson`. `<style>.textContent` read empty throughout.

## Common Pitfalls

- Assuming Tailwind's purge/JIT performs semantic dead-code elimination like a JS bundler — it's textual pattern matching, sensitive to mere mentions in comments or strings.
- Trying to inspect styled-components output via `<style>.textContent` and concluding nothing was injected.
- Treating "which styling approach" as a preference question rather than a mechanism question.

## Interview Answer Skeleton

**30-sec:** CSS Modules scopes at build time with zero runtime cost — verified with two files' identical class names compiling to different strings. Tailwind scans source as plain text, not semantics — verified with a class merely mentioned in a comment getting generated into production CSS. CSS-in-JS generates styles at runtime based on props — verified via CSSOM inspection showing two different rules from one component definition.

**2-min:** Cover the real build-time scoping proof, the two genuinely unexpected Tailwind findings (comment-triggers-generation, and the project-wide default scan leaking README mentions), and the CSS-in-JS runtime mechanism verified via CSSOM (not `textContent`).

**Whiteboard:** Three columns. CSS Modules: two `.title` boxes, arrows to two different hashed names. Tailwind: a file with both a `className` and a comment containing the same string — same arrow from both into generated CSS, labeled "textual scan, can't tell the difference." CSS-in-JS: one component box forking into two real rule bodies depending on a prop, labeled "runtime, via CSSOM insertRule — NOT textContent."

**Staff-level framing:** The Tailwind comment-mention finding is a genuine, non-hypothetical governance concern at scale: a large codebase with many contributors and comment-heavy or documentation-rich files can accumulate unexpected, un-audited CSS purely from prose mentioning class names, with no corresponding UI ever using them.

## Production Warning Signs

- A Tailwind production bundle is larger than expected; grepping the built CSS turns up utility classes no developer remembers using.
- Searching the whole project's text (not just `className` usages, not just source code) for the surprising class name finds it in documentation prose.
- Fix: apply `source(none)` plus an explicit `@source` allowlist rather than relying on the broad, whole-project default.

## Related

- `syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md`
- `syllabus/21-frontend-web/nextjs-monorepo-layout.md`
