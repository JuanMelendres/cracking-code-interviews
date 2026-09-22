---
title: "Interactive System Design Canvas"
slug: interactive-system-design-canvas
document_type: interactive-practice-tool
domain: architecture-atlas
status: canonical
version: 1.0
last_updated: 2026-09-22
topic_id: —
related:
  - ../syllabus/11-system-design/system-design-method-and-estimation.md
  - README.md
---

# Interactive System Design Canvas

**Not a full Architecture Atlas entry** — this page doesn't follow the Architecture Atlas Standard's 15-element template (problem statement, capacity assumptions, data model, and so on). It's a practice tool: a real, in-browser canvas for rehearsing the drawing half of a system design interview — placing components, connecting them, moving things around as the design changes — without needing an actual whiteboard.

## Why this exists

[System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md)'s Whiteboard Explanation section is written to be read, not drawn along with. Every worked example in this Atlas ships a static Mermaid diagram — accurate, but fixed; there was no way to actually place boxes, second-guess a connection, and redraw, short of physical paper. This canvas fills that specific gap: drag components onto a surface, connect them, rename anything, delete what turns out to be wrong, export the result as a PNG to keep.

It's deliberately low-fidelity — eight generic component types, straight-line connections, no auto-layout. The point isn't a polished diagram; it's rehearsing the physical act of building a design up piece by piece under time pressure, the same motion a real whiteboard or shared-screen interview requires.

## How to use it

1. Click a component button to drop it onto the canvas.
2. Drag any component to reposition it.
3. Double-click a component to rename it — reuse the eight generic types for whatever system you're practicing.
4. Click **Connect mode**, then click two components in turn to draw a connection between them. Click **Connect mode** again to turn it off.
5. Click a component's **×** to delete it (and any connections attached to it).
6. Click **Export PNG** to download the current layout as an image.
7. **Clear** wipes the canvas. Everything else is saved automatically in this browser as you go, so reloading the page keeps your layout — nothing is sent anywhere; it never leaves your device.

<div class="design-canvas" id="cci-design-canvas" data-cci-design-canvas markdown="0">
  <div class="design-canvas__toolbar">
    <div class="design-canvas__palette">
      <button type="button" data-add="Client">+ Client</button>
      <button type="button" data-add="Load Balancer">+ Load Balancer</button>
      <button type="button" data-add="API Gateway">+ API Gateway</button>
      <button type="button" data-add="Service">+ Service</button>
      <button type="button" data-add="Database">+ Database</button>
      <button type="button" data-add="Cache">+ Cache</button>
      <button type="button" data-add="Message Queue">+ Message Queue</button>
      <button type="button" data-add="CDN">+ CDN</button>
    </div>
    <div class="design-canvas__actions">
      <button type="button" id="cci-canvas-connect" aria-pressed="false">Connect mode: off</button>
      <button type="button" id="cci-canvas-export">Export PNG</button>
      <button type="button" id="cci-canvas-clear">Clear</button>
    </div>
  </div>
  <div class="design-canvas__surface" id="cci-canvas-surface">
    <svg class="design-canvas__lines" id="cci-canvas-lines"></svg>
  </div>
  <p class="design-canvas__hint">Drag boxes to arrange them. Double-click a box to rename it, click its &times; to delete it. Click "Connect mode," then click two boxes in turn to draw a connection between them. Your layout saves automatically in this browser.</p>
</div>

## What this isn't

It doesn't check your design, suggest components, or grade anything — [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md) and the worked entries throughout this Atlas own that judgment. It also doesn't replace practicing on an actual whiteboard or in a real [mock interview](../practice/mock-interviews/README.md) — a real interview loop expects narration while drawing, which this tool doesn't simulate. Treat it as a rehearsal aid for the drawing motion itself, not a substitute for either the content judgment or the live-communication practice.

## Related

- [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md) — the six-phase method this canvas helps rehearse the drawing half of.
- [Architecture Atlas — Index](README.md) — the full set of worked system-design reference entries this tool doesn't replace.
