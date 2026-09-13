---
title: "API Key Shipped to the Browser From a Misplaced use client Directive"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-server-vs-client-components.md
source: syllabus/21-frontend-web/nextjs-server-vs-client-components.md#production-scenarios
---

# API Key Shipped to the Browser From a Misplaced use client Directive

## Context

A developer needs a component to call an internal API using a server-side API key, and, under time pressure, adds `"use client"` to the file — perhaps because a sibling component in the same folder needed it, and it seemed "consistent" to match.

## Symptoms

Code review passes normally — the diff looks like ordinary data-fetching code, and nobody manually inspects the built JS bundle.

## Impact

The API key's code is genuinely bundled and shipped to every visitor's browser, extractable by anyone who opens dev tools — a real, active credential-exposure incident, not a hypothetical one.

## Initial Hypotheses

- The API key is only referenced server-side elsewhere and this is a false alarm — checked directly by grepping the actual built output, not assumed from reading the source.
- Code review would have caught a real exposure — checked, and disproven: the diff looked identical to a correct Server Component minus one directive, and passed review cleanly.
- The `"use client"` directive on this specific file causes the component (and its API-key reference) to be bundled and shipped to the browser — correct.

## Evidence

Grepping the real `.next/static` output for the API key string finds it present in the client-shippable bundle.

## Investigation Timeline

1. A component intended to call an internal API server-side is marked `"use client"` under time pressure.
2. Code review passes, since the diff resembles ordinary, correct data-fetching code.
3. A targeted grep of the built `.next/static` output for the API key string confirms it is present in client-shippable code.

## Root Cause

Adding `"use client"` to a component that references a server-side secret bundles that secret into the client JavaScript, and this specific failure mode is invisible to a normal code-review skim, since the diff looks like ordinary correct code minus one directive.

## Immediate Mitigation

Rotate the exposed API key immediately, treating it as compromised regardless of how briefly or how few requests saw it, and remove or fix the `"use client"` directive.

## Permanent Fix

Remove `"use client"` from the component (or split the component so the server-only, key-referencing part remains a Server Component), and add a repeatable, automated grep-based check for the specific key string against `.next/static` output.

## Alternatives Considered

Relying on more thorough manual code review to catch this in the future — rejected as insufficient, since this exact incident demonstrates the diff looks identical to correct code to a human reviewer; only a targeted, automated check against the actual built output reliably catches it.

## Trade-offs

None meaningful — the grep-based CI check costs a small amount of pipeline time and catches an entire class of secret-leak mistake that code review structurally cannot.

## Prevention

Run this chapter's own grep-based verification (exact command, exact expected result: zero matches) in CI for every known-sensitive string, as a repeatable, automatable proof that specific known-sensitive strings never appear in client-shippable output.

## Monitoring and Alerts

- A CI step grepping `.next/static` build output for every known secret/API-key string, failing the build on any match.
- Automated secret-rotation runbooks triggered immediately whenever such a CI check does fire, minimizing exposure window for any future occurrence.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** an API key was accidentally shipped to the browser via a misplaced `"use client"` directive, undetected by code review.
- **Task:** confirm the actual exposure and close it, given the diff itself looked entirely correct.
- **Action:** grepped the real built output directly for the key string, confirming it was present in client-shippable code, then rotated the key and fixed the directive.
- **Result:** closed the exposure and added an automated, repeatable CI check for this exact failure mode going forward.

## Staff-Level Discussion

This chapter's grep-based verification is directly the kind of check a team should run in CI for exactly this failure mode — not a one-off audit, but a repeatable, automatable proof that specific known-sensitive strings never appear in client-shippable output, catching a mistake that a visual code review is likely to miss entirely, because the diff looks identical to a correct Server Component minus one directive. The organizational lesson is that some classes of mistake are structurally invisible to human review and need a mechanical, automated check instead — relying on reviewer vigilance alone for this class of bug is a standing risk.

## Related Handbook Chapters

- [Next.js Server vs. Client Components](../syllabus/21-frontend-web/nextjs-server-vs-client-components.md) — the canonical grep-based bundle-verification method behind this incident's fix.
