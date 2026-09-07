---
title: "Flashcards: Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified"
slug: nextjs-deployment-models
document_type: flashcard-deck
domain: frontend
topic_id: F-213
tier: Advanced
canonical: ../syllabus/21-frontend-web/nextjs-deployment-models.md
last_updated: 2026-09-07
---

# Flashcards: Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-deployment-models.md`](../syllabus/21-frontend-web/nextjs-deployment-models.md)

## Card: Does `output: "standalone"` produce a complete, ready-to-run deployment?

**Prompt:**
Does Next.js's `output: "standalone"` build produce a complete deployment artifact on its own?

**Answer:**
No — verified with a real, reproduced test. The standalone `server.js` renders HTML correctly but returns a real `404` for every static asset, since `public/` and `.next/static` are deliberately excluded and must be copied in manually.

**Why it matters:**
This is a real, easy-to-miss failure mode: the app LOOKS like it's working (correct HTML response) while everything else is silently broken.

**Common trap:**
Testing only the HTML response, not the static assets, when validating a standalone deployment.

**Related:**
[Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified](../syllabus/21-frontend-web/nextjs-deployment-models.md) [Server Actions and Mutations in Next.js: No API Layer, Real Progressive Enhancement](../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md)

## Card: Does every Server Action need a shared encryption key across self-hosted instances?

**Prompt:**
When self-hosting Next.js across multiple instances, does every Server Action require `NEXT_SERVER_ACTIONS_ENCRYPTION_KEY` to be set identically?

**Answer:**
No — verified with a real, decisive, contrasted test. A plain, top-level, `.bind()`-style Server Action rendered the SAME action id across two independent builds and worked cross-instance with zero shared-key configuration. Only a genuine inline closure (capturing an outer-scope variable) rendered a real, visibly different encrypted field, and per the framework's own docs, only that case requires the shared key.

**Why it matters:**
A blanket policy would add unnecessary key-management overhead for codebases that use only top-level, bound actions, like this app's own F-211/F-212 work.

**Common trap:**
Assuming "Server Actions" is a single, uniform category with uniform infrastructure requirements.

**Related:**
[Deployment Models in Next.js: Vercel-Native vs. Self-Hosting, Verified](../syllabus/21-frontend-web/nextjs-deployment-models.md) [Server Actions and Mutations in Next.js: No API Layer, Real Progressive Enhancement](../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md)
