---
title: "Checkout p99 latency regression during a promotional campaign"
document_type: postmortem
status: final
incident_date: 2026-08-10
severity: SEV-2
---

# Postmortem: Checkout p99 Latency Regression During a Promotional Campaign

## Summary

Checkout p99 latency rose from 200ms to 4.1 seconds over roughly 40 minutes during a
promotional campaign, causing an estimated 3% of checkout attempts to time out
client-side before completing.

## Impact

Approximately 1,800 checkout attempts timed out over the affected window; an
estimated $12,000 in abandoned-cart revenue based on historical conversion rates for
timed-out sessions.

## Timeline

All times UTC.

- 14:02 — Promotional campaign email sent, driving a 6x traffic increase to checkout.
- 14:18 — p99 latency alert fires at 1.5s threshold.
- 14:20 — On-call engineer acknowledges, begins triage.
- 14:25 — Database connection pool utilization confirmed at 100%; mitigation decision made to scale the pool immediately rather than continue root-cause diagnosis.
- 14:28 — Connection pool size increased via a hot-reloadable config flag; p99 begins recovering within 3 minutes.
- 14:31 — p99 back under 300ms; incident declared mitigated.
- 15:40 — Diagnosis identifies the underlying connection-pool sizing formula as based on average, not peak, expected concurrency.

## Detection

Detected by an automated p99 latency alert 16 minutes after the regression began.
Detection latency of 16 minutes was within the team's SLO for this alert, but slower
than ideal given the promotional campaign's known traffic pattern was foreseeable in
advance.

## Mitigation

Increasing the database connection pool size via a hot-reloadable configuration flag
restored acceptable latency within 3 minutes of the change. This was a mitigation,
not a permanent fix: it consumed additional database connection headroom that would
not have been available had a second, unrelated traffic spike occurred simultaneously.

## Contributing Factors

- The connection pool's sizing formula was based on average expected concurrency
  rather than the documented peak concurrency for promotional campaigns.
- No load test had been run simulating the specific traffic multiplier a promotional
  email produces, so the pool exhaustion point was not known in advance.
- The alert threshold (1.5s) allowed nearly a minute of severe degradation before
  firing, because it required three consecutive breaching data points to avoid noise.
- The promotional campaign's expected traffic multiplier was known to the marketing
  team but was not communicated to the on-call engineering rotation ahead of the send.

## Blameless Analysis

The connection pool sizing formula and the alert threshold are the systems this
incident points to, not any individual's judgment at the time each was configured —
both were reasonable choices under the assumptions available when they were set, and
neither assumption had been revisited since. The lack of pre-send communication
between marketing and engineering is a process gap, not a failure of any specific
person to remember to send an email.

## Action Items

- [ ] Recompute the connection pool sizing formula against documented peak, not average, concurrency — Owner: platform-team — Due: 2026-08-24
- [ ] Add a promotional-campaign traffic simulation to the standard load-test suite — Owner: sre-team — Due: 2026-09-07
- [ ] Establish a standing notification from marketing to on-call engineering before any campaign expected to exceed 3x baseline traffic — Owner: marketing-eng-liaison — Due: 2026-08-31

## Lessons Learned

Capacity-sizing formulas based on averages silently under-provision for foreseeable,
scheduled traffic multipliers; any sizing decision tied to "average" load should be
revisited whenever a known peak-traffic event (a campaign, a product launch) is
planned, not left to be discovered live.
