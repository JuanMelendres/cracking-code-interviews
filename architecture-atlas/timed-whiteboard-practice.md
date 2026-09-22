---
title: "Timed Whiteboard Practice"
slug: timed-whiteboard-practice
document_type: interactive-practice-tool
domain: architecture-atlas
status: canonical
version: 1.0
last_updated: 2026-09-22
topic_id: —
related:
  - ../syllabus/11-system-design/system-design-method-and-estimation.md
  - interactive-system-design-canvas.md
  - README.md
---

# Timed Whiteboard Practice

**Not a full Architecture Atlas entry** — same as the [Interactive System Design Canvas](interactive-system-design-canvas.md), this doesn't follow the Architecture Atlas Standard's 15-element template. It's a practice tool: a real countdown timer that walks through [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md)'s six phases in order, using that chapter's own stated per-phase minute ranges — for rehearsing the time-pressure half of a design round, not just the content.

## Why this exists

The canonical chapter states each phase's time budget in its cheat sheet (Clarify 2–3 min, Estimate 3–5 min, API 2–3 min, Data 3–5 min, Architecture 10–15 min, Bottlenecks 5–10 min) and names running out of time before Bottlenecks as a scored gap, not a neutral inconvenience — but reading a time budget and actually feeling it tick by under a real countdown are different kinds of practice. This tool is the second: a real timer that advances through the six phases automatically, beeping and flashing at each transition, so the discipline of "reserve time for Phase 6" gets rehearsed against a real clock instead of stated in the abstract.

Use it alongside the [Interactive System Design Canvas](interactive-system-design-canvas.md) — draw components on the canvas while this timer runs, for a closer approximation of the actual drawing-under-time-pressure experience than either tool gives alone.

## How to use it

1. Pick a preset — **25-min drill** loads the chapter's own lower-bound minutes per phase (2/3/2/3/10/5); **41-min full run** loads the upper-bound minutes (3/5/3/5/15/10) — or edit any phase's minutes directly.
2. Click **Start**. The clock counts down for the current phase; the bar above it shows all six phases, with the active one highlighted and completed ones marked done.
3. When a phase's time runs out, the timer beeps and automatically advances to the next phase — no need to watch the clock and switch manually.
4. **Pause** stops the countdown without losing your place; **Start** resumes it. **Reset** returns to Phase 1 with the currently configured minutes.
5. After Bottlenecks' time runs out, the session shows complete. Nothing is scored or recorded — the goal is rehearsing the six phases against a real, ticking clock, not tracking history across sessions.

<div class="whiteboard-timer" id="cci-whiteboard-timer" data-cci-whiteboard-timer markdown="0">
  <div class="whiteboard-timer__presets">
    <button type="button" data-preset="25">25-min drill (lower-bound pace)</button>
    <button type="button" data-preset="41">41-min full run (upper-bound pace)</button>
  </div>
  <table class="whiteboard-timer__phases">
    <thead>
      <tr><th style="text-align:left">Phase</th><th style="text-align:left">Minutes</th></tr>
    </thead>
    <tbody>
      <tr><td>1. Clarify</td><td><input type="number" min="0.1" step="0.5" value="2" data-phase-minutes="0"></td></tr>
      <tr><td>2. Estimate</td><td><input type="number" min="0.1" step="0.5" value="3" data-phase-minutes="1"></td></tr>
      <tr><td>3. API</td><td><input type="number" min="0.1" step="0.5" value="2" data-phase-minutes="2"></td></tr>
      <tr><td>4. Data</td><td><input type="number" min="0.1" step="0.5" value="3" data-phase-minutes="3"></td></tr>
      <tr><td>5. Architecture</td><td><input type="number" min="0.1" step="0.5" value="10" data-phase-minutes="4"></td></tr>
      <tr><td>6. Bottlenecks</td><td><input type="number" min="0.1" step="0.5" value="5" data-phase-minutes="5"></td></tr>
    </tbody>
  </table>
  <div class="whiteboard-timer__display">
    <div class="whiteboard-timer__phase-label" id="cci-timer-phase-label">Ready &mdash; Phase 1: Clarify</div>
    <div class="whiteboard-timer__clock" id="cci-timer-clock">02:00</div>
    <div class="whiteboard-timer__progress" id="cci-timer-progress"></div>
  </div>
  <div class="whiteboard-timer__controls">
    <button type="button" id="cci-timer-start">Start</button>
    <button type="button" id="cci-timer-pause" disabled>Pause</button>
    <button type="button" id="cci-timer-reset">Reset</button>
  </div>
  <p class="whiteboard-timer__hint">Minutes can be edited before starting. A short beep plays at each phase transition &mdash; keep sound on, or watch the progress bar above the clock.</p>
</div>

## What this isn't

It doesn't check whether a design is actually good, and it doesn't replace a real, narrated mock interview — see [Mock Interview Standard](../practice/mock-interviews/README.md) content for that. It also isn't a stopwatch for an arbitrary task; the six phases and their default minutes are fixed to match [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md)'s own method exactly, so the phase names and default minutes aren't meant to be repurposed for something else.

## Related

- [System Design Method and Estimation](../syllabus/11-system-design/system-design-method-and-estimation.md) — the six-phase method and per-phase minute ranges this timer rehearses directly.
- [Interactive System Design Canvas](interactive-system-design-canvas.md) — pairs with this timer for a fuller rehearsal of drawing under time pressure.
- [Architecture Atlas — Index](README.md) — the full set of worked system-design reference entries this tool doesn't replace.
