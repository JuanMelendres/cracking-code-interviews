---
title: "ADR-002: Use Streaming Replication, Not Log-Shipping, for Cross-Region DR"
status: accepted
date: 2026-08-25
deciders: [platform-architecture-group]
---

# ADR-002: Use Streaming Replication, Not Log-Shipping, for Cross-Region DR

> **Representative scenario.** A worked example, not a record of a real company
> decision. Every number cited is real, measured evidence from this repository's own
> [Multi-Region, Failover, and Disaster Recovery](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md)
> chapter and its [practice code](../../sql/multi-region-failover-and-dr/README.md).

## Status

Accepted

## Context

The order-processing service's primary region needs a disaster-recovery region. The
business has stated an RPO target of "a few seconds at most" for order data —
losing more than that many seconds of confirmed orders during a region loss is not
acceptable — and an RTO target of "a few minutes."

## Decision Drivers

- Business-approved RPO: at most a few seconds of data loss.
- Business-approved RTO: at most a few minutes of downtime.
- Real, measured evidence for both candidate patterns already exists in this
  repository's own DR chapter, rather than needing to be estimated from vendor
  documentation alone.

## Considered Options

### Option A: WAL-archiving (log-shipping) standby

**Pros:**
- Lowest standing infrastructure cost — no continuously-running second full node.

**Cons:**
- Real, measured RPO in this repository's own test: with `archive_timeout=3`
  configured, a real 10-second write window produced **10 out of 10 rows genuinely
  unrecoverable**, because no WAL segment closed and archived during that window at
  all. The configured timeout did not match observed behavior. This real result is
  worse than the business's stated RPO target, not merely more expensive to prove.

### Option B: Warm standby via streaming replication

**Pros:**
- Real, measured RPO in this repository's own test: **0 rows lost** destroying a
  primary mid-burst of 2,437 real committed writes.
- Real, measured RTO: **0.98 seconds** from region loss to the promoted standby's
  first accepted write — well inside the business's few-minutes target, with margin
  for real detection and DNS-propagation time on top.

**Cons:**
- Real, standing infrastructure cost — a continuously-running, full-sized standby
  node in the DR region.

## Decision

We will adopt Option B — a warm standby via streaming replication — because Option A
was directly, empirically tested against the same business RPO target and failed it,
not merely estimated to be riskier. The cost of a continuously-running standby is
accepted as the real price of a verified RPO/RTO number instead of a hoped-for one.

## Consequences

**Positive:**
- RPO and RTO targets are backed by real, executed evidence specific to this exact
  configuration, not a vendor's general claim about the technology.
- Promotion is fast enough that DNS/traffic-manager propagation delay, not database
  promotion itself, becomes the dominant real cost in an actual failover.

**Negative:**
- A full second node runs continuously in the DR region, a real, ongoing cost that
  Option A would have avoided.
- The failover procedure must include real, unconditional fencing of the old primary
  before promotion — per this repository's own [real, reproduced split-brain finding](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md#production-scenarios)
  — or this decision's real RPO advantage is undermined by a different real risk.

**Follow-up:**
- Schedule a real DR game day before this goes live, verifying the measured RTO holds
  under the actual production network path, not just the local test environment this
  ADR's evidence came from.
- Confirm the failover runbook includes an explicit, automated fencing step — not a
  manual, judgment-call one — before sign-off.

## Related

- [Multi-Region, Failover, and Disaster Recovery](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) — the canonical chapter and all cited evidence.
- [DR practice code and real measurements](../../sql/multi-region-failover-and-dr/README.md)
