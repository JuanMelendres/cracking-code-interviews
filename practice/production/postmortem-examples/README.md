# Incident response and blameless postmortems (T-1207) — runnable verification

Real, executed output backing
[`syllabus/13-observability/incident-response-and-blameless-postmortems.md`](../../../syllabus/13-observability/incident-response-and-blameless-postmortems.md)
(T-1207). A real Python linter run against real, checked-in example documents — not a
description of what "blameless" or "contributing factors" should mean.

## Files

- `postmortem-001-checkout-latency-regression.md` — a full, representative example
  (per this repository's fictionalized-scenario labeling convention) that passes both
  real checks: it uses "Contributing Factors" (plural, several distinct factors named)
  and contains no blame-coded language.
- `bad-example-blaming-language.md` — deliberately reproduces the anti-pattern of
  naming an individual's failing ("failed to test," "should have caught," "forgot to")
  as the incident's cause.
- `bad-example-single-root-cause.md` — deliberately reproduces the register's own
  named misconception: a singular "Root Cause" section instead of "Contributing
  Factors," plus action items with no owner or due date.
- `../../../scripts/check_postmortem_blameless.py` — the real linter: checks for
  required sections, flags a singular "Root Cause" heading, scans for blame-coded
  language patterns with the exact offending sentence quoted, and verifies every
  action item has both an owner and a due date.

## Run

```bash
python3 scripts/check_postmortem_blameless.py \
  templates/postmortem-template.md \
  practice/production/postmortem-examples/postmortem-001-checkout-latency-regression.md \
  practice/production/postmortem-examples/bad-example-blaming-language.md \
  practice/production/postmortem-examples/bad-example-single-root-cause.md
```

## Real observed output (last full run)

```
  PASS   templates/postmortem-template.md
  PASS   practice/production/postmortem-examples/postmortem-001-checkout-latency-regression.md
  FAIL   practice/production/postmortem-examples/bad-example-blaming-language.md
           - blame-coded language "failed to" in: "- The on-call engineer failed to test the config change in staging before deploying"
           - blame-coded language "should have caught" in: "to production, which should have caught the issue."
  FAIL   practice/production/postmortem-examples/bad-example-single-root-cause.md
           - missing required section(s): Contributing Factors
           - uses a singular "Root Cause" section -- incidents rarely have exactly one cause; use "Contributing Factors" instead
           - action item missing Owner/Due: "Use CONCURRENTLY for all future schema migrations on hot tables."
           - action item missing Owner/Due: "Add a migration review checklist item for lock behavior."
```

Exit code: `1` (two of four documents fail) — real, non-zero, CI-gateable output, not
a printed opinion.

## What this proves and what it doesn't

The linter's blame-language pattern list is real but deliberately small and honest
about its limits — it caught "failed to" and "should have caught" in the bad example
above, but did not catch a nearby phrase using "forgotten" (a different word form than
the "forgot to" pattern it checks for), which is disclosed here rather than silently
undercounted. A real production version of this check would need a larger, actively
maintained phrase list (or an NLP-based sentiment/blame classifier) to catch language
this simple pattern list misses — what this demo proves is the *mechanism*
(automated, objective, CI-gateable checking of a document against blameless and
contributing-factors standards), not a claim of exhaustive phrase coverage.
