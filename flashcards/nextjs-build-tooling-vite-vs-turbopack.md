---
title: "Flashcards: Build Tooling: Vite vs. Next.js's Turbopack, What a Bundler Actually Does"
slug: nextjs-build-tooling-vite-vs-turbopack
document_type: flashcard-deck
domain: frontend
topic_id: F-301
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md
last_updated: 2026-09-07
---

# Flashcards: Build Tooling: Vite vs. Next.js's Turbopack, What a Bundler Actually Does

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md`](../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md)

## Card: Does Vite bundle application code in development?

**Prompt:**
Does Vite's dev server bundle your own application's source files together during development?

**Answer:**
No — verified with a real network trace. Every source file (`main.jsx`, `App.jsx`, a shared module) showed up as its own separate HTTP request, resolved by the browser's native `import`. Vite DOES pre-bundle third-party `node_modules` dependencies (via esbuild) and DOES genuinely bundle for the production build.

**Why it matters:**
This is the real, precise, scoped version of "Vite doesn't bundle" — an absolute reading of that claim is wrong.

**Common trap:**
Treating "no dev-mode app-code bundling" as "no bundling at all, ever."

**Related:**
[Build Tooling: Vite vs. Next.js's Turbopack, What a Bundler Actually Does](../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md)

## Card: Is a raw "ready in Xms" number a fair way to compare Vite and Turbopack's speed?

**Prompt:**
If Vite reports "ready in 400ms" for one app and Turbopack reports "Ready in 271ms" for a different app, is that a fair speed comparison?

**Answer:**
No — verified directly. The two real apps had very different sizes (3 files vs. 31 routes), and neither number reflects full-app compilation, since both tools compile lazily on first request. The fair, real comparison is the actual request pattern for an equivalent page load, not the startup timestamp.

**Why it matters:**
A bare startup-time comparison across differently-sized apps is a common, real source of misleading tooling conclusions.

**Common trap:**
Comparing marketing or documentation-quoted numbers without controlling for app size or measuring the same thing.

**Related:**
[Build Tooling: Vite vs. Next.js's Turbopack, What a Bundler Actually Does](../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md)
