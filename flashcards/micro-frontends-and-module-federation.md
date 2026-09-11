---
title: "Flashcards: Micro-Frontends and Module Federation"
slug: micro-frontends-and-module-federation
document_type: flashcard-deck
domain: 21-frontend-web
topic_id: F-403
canonical: ../syllabus/21-frontend-web/micro-frontends-and-module-federation.md
last_updated: 2026-09-11
---

# Flashcards: Micro-Frontends and Module Federation

**Canonical chapter:** [`syllabus/21-frontend-web/micro-frontends-and-module-federation.md`](../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)

## Card: What Module Federation's host/remote boundary actually is

**Prompt:**
What does a host's bundle actually contain for a module it imports from a remote — the remote's code, or something else?

**Answer:**
Something else: only the address of where to fetch it at runtime (the remote's `remoteEntry.js` URL). This chapter's own demo proves this directly — the host's `bundle.js` is built once, and the browser's real, captured network requests show it fetching the remote's code live, at page load, from a genuinely separate server.

**Why it matters:**
This runtime boundary, not any config syntax, is what actually makes Module Federation solve the micro-frontend problem.

**Common trap:**
Assuming Module Federation is just a fancier code-splitting feature within one build, rather than a runtime composition boundary across genuinely separate builds.

**Related:**
[Micro-Frontends and Module Federation](../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)

## Card: Proving independent deployability

**Prompt:**
What's the concrete test for whether two micro-frontends can actually deploy independently, as opposed to just being organized into separate folders?

**Answer:**
Rebuild and redeploy one piece in isolation, deliberately never touching or rebuilding the other, and confirm the composed page's user-visible behavior still reflects the change. This chapter's own demo does exactly this — a real, separate `webpack` rebuild of only the remote changes the host page's rendered button text and color, with the host's own `bundle.js` never rebuilt or re-served.

**Why it matters:**
Independent deployability is the entire organizational point of the pattern — an architecture that can't pass this test hasn't actually achieved it, regardless of what tooling it uses.

**Common trap:**
Treating "we use Module Federation" as sufficient proof of independent deployability without ever testing it.

**Related:**
[Micro-Frontends and Module Federation](../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)
