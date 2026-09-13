---
title: "Image quality Prop Silently Clamped to an Unconfigured Value"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md
source: syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md#production-scenarios
---

# Image quality Prop Silently Clamped to an Unconfigured Value

## Context

A developer, wanting a slightly sharper hero image, sets `quality={95}` on an `<Image>` component, in an app whose `next.config.js` only allowlists the default `[75]`.

## Symptoms

The image looks unchanged after the "improvement" ships; nobody notices anything is wrong because there's no error, no warning, no visual regression — just a missed intent.

## Impact

A shipped, intended visual improvement silently didn't happen, with no signal to anyone that it failed.

## Initial Hypotheses

- The image asset itself wasn't updated — checked, the correct, higher-resolution source asset was in place.
- The component didn't rebuild/redeploy correctly — checked, the deploy completed successfully and the component code was live.
- The `quality` prop value isn't allowlisted in `next.config.js`, so the framework silently clamps it to the nearest configured value — correct.

## Evidence

Inspecting the actual rendered `<img>` tag's `src`/`srcset` shows `q=75`, not `q=95` — the component silently clamped the request to the nearest configured value.

## Investigation Timeline

1. A shipped image-quality improvement appears to have no visible effect.
2. Asset-update and deploy-failure hypotheses ruled out.
3. Rendered `<img>` tag inspected directly, revealing the actual served quality value.

## Root Cause

`quality` values aren't validated against a project's actual `next.config.js` at the point a developer writes the JSX — there's no build-time or edit-time feedback that a given value isn't allowed.

## Immediate Mitigation

None needed operationally — no user-facing harm occurred, only a missed intended improvement.

## Permanent Fix

Add the desired quality value to `images.qualities` in `next.config.js`, and, as a team practice, treat any `quality` prop value as needing an explicit corresponding `next.config.js` entry, verified by inspecting the real rendered output rather than assuming the prop "just works."

## Alternatives Considered

Relying on code review alone to catch a mismatched `quality` value — rejected as unreliable, since the mismatch produces no visible signal in the diff or in casual review; only inspecting the actual rendered output reveals it.

## Trade-offs

None meaningful — adding the value to the allowlist is a one-line config change with no downside.

## Prevention

Treat any new `quality` prop value as needing verification against `next.config.js`'s `images.qualities` allowlist before considering the change complete, since neither the framework nor a visual diff will flag the mismatch.

## Monitoring and Alerts

- An automated check (e.g., a build-time script grepping `quality` prop usages against the configured allowlist) flagging any value not present in `images.qualities`.
- A code-review checklist item for any `<Image quality={...}>` change, requiring the corresponding config entry be shown in the same diff.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent finding.

- **Situation:** a developer's intended image-quality improvement shipped with no visible effect and no error.
- **Task:** find why a change that should have worked silently didn't.
- **Action:** inspected the actual rendered `<img>` tag's `src` attribute, revealing the framework had clamped the requested quality to the nearest allowlisted value.
- **Result:** added the value to `next.config.js`'s allowlist and established a verification practice against silently clamped values going forward.

## Staff-Level Discussion

The organizational lesson is that a silent no-op is a particularly dangerous failure mode precisely because nothing signals it happened — no error, no warning, no visual regression a casual glance would catch. Any framework behavior that can silently clamp or ignore a value rather than erroring deserves an explicit verification step (inspecting real output, not just trusting the prop), since code review and casual QA structurally cannot catch this class of gap.

## Related Handbook Chapters

- [Next.js Image, Font Optimization, and Web Vitals](../syllabus/21-frontend-web/nextjs-image-font-optimization-and-web-vitals.md) — the canonical `images.qualities` allowlist mechanics behind this incident.
