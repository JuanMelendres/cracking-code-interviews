---
title: "Cheat Sheet: Micro-Frontends and Module Federation"
slug: micro-frontends-and-module-federation
document_type: cheat-sheet
domain: 21-frontend-web
topic_id: F-403
canonical: ../syllabus/21-frontend-web/micro-frontends-and-module-federation.md
last_updated: 2026-09-11
---

# Micro-Frontends and Module Federation

**Canonical chapter:** [`syllabus/21-frontend-web/micro-frontends-and-module-federation.md`](../syllabus/21-frontend-web/micro-frontends-and-module-federation.md)

## Core Mental Model

A host's bundle doesn't contain a remote module's code — only the address of where to fetch it at runtime (the remote's `remoteEntry.js` URL). This runtime composition boundary across genuinely separate builds — not any config syntax — is what actually makes Module Federation solve the micro-frontend problem.

## Essential Definitions

- **`exposes`** — declares which of a build's modules other builds may consume at runtime.
- **`remotes`** — declares which other builds' exposed modules this build consumes.
- **`shared`** — avoids every remote shipping its own copy of a library; `singleton: true` for framework runtimes (React, etc.).

## Decision Table

| Need | Mechanism |
|---|---|
| Expose a module for other builds to consume at runtime | `ModuleFederationPlugin`'s `exposes` |
| Consume another build's exposed module at runtime | `ModuleFederationPlugin`'s `remotes` |
| Avoid every remote shipping its own copy of a shared library | `ModuleFederationPlugin`'s `shared`, `singleton: true` for framework runtimes |
| Avoid "shared module not available for eager consumption" | Wrap the remote import behind a dynamic `import()` (an async boundary) |
| Verify deploy independence is real, not assumed | Rebuild one piece only, confirm the other's artifact untouched, confirm the composed result still changed |

## Common Pitfalls

- Assuming Module Federation is a fancier code-splitting feature within one build, rather than a runtime composition boundary across genuinely separate builds.
- Treating "we use Module Federation" as proof of independent deployability without ever testing it (rebuild one piece in isolation and verify).
- Forgetting the async-boundary requirement for shared eager consumption, causing a real, common build-time error.

## Interview Answer Skeleton

**30-sec:** Module Federation lets a build (`exposes`) publish modules that another build (`remotes`) fetches at runtime, by address, not bundled at build time — the runtime boundary is what enables independent deployability.

**2-min:** Add: a real demo captures the browser's actual network requests proving the host fetches the remote's code live from a genuinely separate server, and a real, separate rebuild of only the remote changes the composed page's behavior with the host's own bundle never rebuilt — the concrete test for independent deployability.

**Staff-level framing:** Independent deployability is the entire organizational point of micro-frontends — an architecture that can't pass the "rebuild one piece in isolation" test hasn't actually achieved it, regardless of tooling used.

## Related

- syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
