---
title: "Index-as-Key Silently Reattaching Notes to the Wrong Task After a Delete"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md
source: syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md#production-scenarios
---

# Index-as-Key Silently Reattaching Notes to the Wrong Task After a Delete

## Context

A task list has each row's inline "notes" field as an uncontrolled input, and the list is rendered with `key={index}` because it was the first thing that made the console warning disappear during development.

## Symptoms

Whenever a user deletes a task from the middle of the list, the notes typed into rows below the deleted one silently reattach to the wrong tasks.

## Impact

A real, reported data-integrity bug once real users started reordering and deleting freely — invisible in casual testing, since testers rarely type into a row and then delete a different row above it in the same session.

## Initial Hypotheses

- A backend data-corruption issue — the initial framing from the bug report, since the symptom looked like data associated with the wrong record.
- A state-management bug elsewhere in the app — checked, no other state logic touches these notes fields.
- `key={index}` causes React to reuse DOM nodes (and their uncontrolled input state) by position rather than by task identity after a deletion shifts indices — correct.

## Evidence

Reproducing the exact interaction (type into a row, delete a different row above it) shows the uncontrolled input's DOM node — and its typed value — staying at the same list position rather than following its original task, confirming the reconciliation is keyed by index, not identity.

## Investigation Timeline

1. Bug reported and initially investigated as a backend data-corruption issue.
2. Backend and unrelated-state hypotheses ruled out via review.
3. Reproduced the specific interaction order (type, then delete above), confirming the notes field's uncontrolled state stayed at the list position rather than following the task.

## Root Cause

`key={index}` was chosen only to silence a console warning during development, not to correctly identify each row — after a deletion shifts every subsequent row's index, React reuses each position's existing DOM node (and its uncontrolled input value) for a now-different task.

## Immediate Mitigation

None needed operationally beyond the fix itself — the bug is deterministic and directly traceable once reproduced, with no ongoing data loss beyond already-affected records needing manual review.

## Permanent Fix

Switch to a stable `key={task.id}`, so React correctly associates each row's DOM node and its uncontrolled input state with the actual task it belongs to, regardless of list position changes.

## Alternatives Considered

Converting the notes field to controlled state tied to the task object itself — a real, valid alternative that would also fix the bug, but a larger change than necessary when the actual defect was specifically the key choice, not the controlled-vs-uncontrolled decision.

## Trade-offs

None meaningful — switching to a stable ID-based key is a one-line change with no functional downside.

## Prevention

Treat `key={index}` as a specific, standing code-review flag for any list whose items can be reordered, inserted, or deleted, especially when list items hold their own local (including uncontrolled) state.

## Monitoring and Alerts

- A lint rule flagging `key={index}` usage on any list rendering, requiring an explicit justification comment if genuinely intentional (e.g., a list guaranteed never to reorder).
- QA test scenarios specifically covering "type into a row, then delete a different row" interaction orders for any editable list, since casual testing structurally misses this exact sequence.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent bug.

- **Situation:** a task list's inline notes silently reattached to the wrong task whenever a user deleted a row above one they'd edited.
- **Task:** find the root cause of what was initially reported as a backend data-corruption bug.
- **Action:** reproduced the exact interaction order and traced it to `key={index}` causing React to reuse DOM nodes by position rather than task identity.
- **Result:** switched to a stable `key={task.id}`, a one-line fix, and added a lint rule to catch future `key={index}` usage on reorderable lists.

## Staff-Level Discussion

`key={index}` was chosen because it was the first thing that made the console warning disappear during development — not because it correctly identified each row — and the resulting bug reproduced only under a specific interaction order, looking like a backend issue rather than a frontend rendering one. The organizational lesson is that silencing a warning is not the same as understanding what it was warning about; a list-rendering key choice needs to be justified by actual item identity, not by whichever value happens to make React stop complaining.

## Related Handbook Chapters

- [React Fundamentals: JSX, Components, Props, and State](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md) — the canonical key-based reconciliation mechanics behind this incident's fix.
