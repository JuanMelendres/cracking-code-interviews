---
title: "<Incident Title>"
document_type: postmortem
status: draft
incident_date: YYYY-MM-DD
severity: <SEV-1 | SEV-2 | SEV-3>
---

# Postmortem: <Incident Title>

## Summary

One or two sentences: what happened, what was the user-facing impact, how long did
it last.

## Impact

Concretely quantified: users affected, requests failed, revenue or SLO impact,
duration.

## Timeline

All times in a single, stated timezone. Include detection time and every
significant escalation, mitigation, or diagnostic step, in order.

- HH:MM — event
- HH:MM — event

## Detection

How was this incident detected — an alert, a customer report, a routine check?
State the detection latency (time between the incident starting and it being
noticed) explicitly; a long detection latency is itself an actionable finding.

## Mitigation

What stopped the user-facing impact, and when. Mitigation is not the same as a
permanent fix — state clearly if this was a rollback, a feature flag, a manual
intervention, or another stop-gap, and what risk (if any) it deliberately traded
away in exchange for speed.

## Contributing Factors

Incidents are very rarely caused by exactly one thing. List every factor that
contributed to this incident occurring, being undetected for as long as it was, or
taking as long as it did to mitigate — technical, process, and organizational
factors are all in scope. Do not collapse this into a single "root cause": name
each factor that, had it alone been different, would have prevented or shortened
the incident.

- Factor 1:
- Factor 2:

## Blameless Analysis

This section names what went wrong with systems and processes, never with people.
Rewrite any sentence that names or blames an individual, a team, or frames an event
as a personal failing (see this pack's own linter,
`scripts/check_postmortem_blameless.py`, for the specific language patterns this
template's author-facing checklist flags).

## Action Items

Every action item has an explicit owner and a due date — an action item with
neither is not a commitment, it's a wish.

- [ ] Action description — Owner: <name or team> — Due: YYYY-MM-DD
- [ ] Action description — Owner: <name or team> — Due: YYYY-MM-DD

## Lessons Learned

What does this incident teach beyond its specific fix — a pattern to watch for
elsewhere, a monitoring gap that likely exists in other systems too, a process gap
worth closing generally.
