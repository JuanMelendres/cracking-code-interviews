---
title: "Flashcards: Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified"
slug: nextjs-monorepo-layout
document_type: flashcard-deck
domain: frontend
topic_id: F-303
tier: Advanced
canonical: ../syllabus/21-frontend-web/nextjs-monorepo-layout.md
last_updated: 2026-09-07
---

# Flashcards: Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-monorepo-layout.md`](../syllabus/21-frontend-web/nextjs-monorepo-layout.md)

## Card: Does a workspace monorepo copy a shared package's files into each consumer, or link to them?

**Prompt:**
When a workspace monorepo installs a local package as another package's dependency, does it copy the files or link to them?

**Answer:**
Links — verified directly. `npm install` produced a real symlink (`ls -la` showed `lrwxr-xr-x`, `readlink` resolved to the actual source directory), and a real live-edit test confirmed it: editing the shared package's source and re-running two consumers (no reinstall, no rebuild) showed the change in both instantly.

**Why it matters:**
This is the exact mechanism behind "monorepos make code sharing easy" — there's no version to bump and nothing to publish, so there's no window for drift.

**Common trap:**
Assuming workspace packages are copied like a normal npm install from the registry.

**Related:**
[Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified](../syllabus/21-frontend-web/nextjs-monorepo-layout.md)

## Card: Is skipping workspace tooling in a multi-app repo automatically a mistake?

**Prompt:**
If a repo has several independent apps and doesn't use any workspace/monorepo tooling, is that automatically under-engineered?

**Answer:**
Not automatically — verified directly using this repository's own real structure as the example. Real, measured duplication exists (four of this repo's own apps each fully duplicate `react@19.2.8`), but the trade is deliberate: every app stays fully independent and copyable in isolation, which matters for a repo whose real audience works through one chapter's app at a time.

**Why it matters:**
The right structure depends on whether code genuinely needs to stay in sync across apps, not on disk space alone.

**Common trap:**
Assuming any multi-app repo without shared tooling is automatically a mistake, without checking what property the repo's own real structure is actually optimizing for.

**Related:**
[Monorepo and Full-Stack Repo Layout: Where Code Actually Lives, Verified](../syllabus/21-frontend-web/nextjs-monorepo-layout.md)
