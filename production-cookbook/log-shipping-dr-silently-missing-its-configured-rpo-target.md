---
title: "Log-Shipping DR Silently Missing Its Configured RPO Target"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/system-design/multi-region-failover-and-disaster-recovery.md
  - ../handbook/architecture/architecture-decision-records.md
source: handbook/system-design/multi-region-failover-and-disaster-recovery.md#production-scenarios
---

# Log-Shipping DR Silently Missing Its Configured RPO Target

## Context

A team must pick a disaster-recovery pattern for a PostgreSQL-backed service, given a business RPO target of "a few seconds," and must justify the infrastructure cost of whatever they choose between a hot standby (streaming replication) and backup-restore / log-shipping (WAL archiving).

## Symptoms

The two candidate DR patterns, tested identically, produce dramatically different real RPOs for the same underlying data and workload — one meets the business target, the other does not, despite its own configuration claiming it should.

## Impact

Had the log-shipping pattern been chosen on the strength of its configured `archive_timeout` value alone, a real disaster would have resulted in far greater data loss than the business RPO target allowed — discovered only during the actual disaster rather than beforehand.

## Initial Hypotheses

None needed — the test was a deliberate, controlled comparison of two DR patterns against the stated RPO target, not a diagnosis of an unexpected failure.

## Evidence

**Warm standby (streaming replication).** [`rpo-demo.sh`](../../practice/sql/multi-region-failover-and-dr/README.md) fired a 150,000-statement burst at a real primary and destroyed the primary's container **and volume** mid-burst, 0.4 seconds in — a real, irreversible loss, not a graceful shutdown. Result: **2,437 rows committed, 0 lost.** Measured RTO: **0.98 seconds** from destruction to the promoted standby accepting its first write.

**Backup-restore / log-shipping (WAL archiving).** [`rpo-archive-demo.sh`](../../practice/sql/multi-region-failover-and-dr/README.md) configured a real primary with `archive_mode=on`, `archive_timeout=3`, wrote ten individually-timestamped rows over ten real seconds, then destroyed the primary. Result: **only the startup WAL segment was ever archived — 10 out of 10 rows genuinely unrecoverable**, because no second segment closed and shipped in the entire ten-second window despite the 3-second `archive_timeout` configuration.

## Investigation Timeline

1. Business RPO target of "a few seconds" established for a PostgreSQL-backed service, with a requirement to justify whichever DR pattern's infrastructure cost is chosen.
2. Warm-standby pattern tested first: a 150,000-statement burst fired at a real primary, which is then destroyed (container and volume) 0.4 seconds into the burst.
3. Warm-standby result measured directly: 2,437 rows committed, 0 lost; RTO of 0.98 seconds to the promoted standby's first accepted write.
4. Log-shipping pattern tested identically in structure: a primary configured with `archive_timeout=3`, ten timestamped rows written over ten seconds, then destroyed.
5. Log-shipping result measured directly: only the startup WAL segment had ever archived, and all 10 rows were unrecoverable — despite the 3-second configured archive timeout implying they should have been captured.
6. Diagnosis reached by comparing the two mechanisms structurally: continuous streaming versus periodic archiving, with the archiver's real-world timing not matching its configured target under this write pattern.

## Root Cause

The two patterns produced dramatically different real RPOs for the identical underlying data — not because one number was fabricated, but because a continuously-streaming standby and a periodically-archived one are structurally different mechanisms, and the archiver's real-world timing did not match its configured target.

## Immediate Mitigation

For an RPO target of "a few seconds," the warm-standby pattern is the only one of the two that empirically met it in this real test; log-shipping alone did not.

## Permanent Fix

If cost still rules out a continuously-running standby, the log-shipping RPO must be *independently verified*, the same way this test did — by actually destroying a primary and checking what survived — not trusted from `archive_timeout`'s configured value.

## Alternatives Considered

Log-shipping / backup-restore was the actual alternative under consideration to the warm standby, and was rejected specifically because it failed the stated RPO target when independently tested, not on cost grounds alone.

## Trade-offs

The warm standby costs real, continuous infrastructure spend for a second running node; log-shipping is dramatically cheaper but, as measured, can silently miss its own configured RPO target under light write load.

## Prevention

Treat a DR pattern's RPO and RTO as claims to be tested, on a real schedule (a "DR game day"), not values to be read off a configuration file and trusted.

## Monitoring and Alerts

- Alert on WAL segment archive frequency falling below what `archive_timeout` implies it should be — this incident's actual failure mode was silent (no error, just a segment that never closed and shipped), so a direct metric on archive cadence is necessary to catch it before a real disaster does.
- Schedule recurring "DR game day" exercises (the Prevention step) as a calendared, non-optional practice, tracking measured RPO/RTO results over time to catch drift as write volume, `archive_timeout`, or infrastructure changes over the service's life — a one-time measurement can go stale.
- Track actual replication lag on the warm standby continuously, in addition to the one-time destructive test, since the destructive test validates the mechanism but ongoing lag monitoring is what confirms the RPO guarantee continues to hold under real, changing production write patterns.

## Interview Story

This maps directly to "how would you choose and validate a disaster-recovery pattern" backed by a real, destructive, measured comparison. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a team needed to choose a DR pattern for a PostgreSQL service against a business RPO target of "a few seconds" and justify the infrastructure cost.
- **Task:** provide a defensible answer backed by real evidence rather than a configuration file's stated intent.
- **Action:** destroyed a real primary mid-write-burst under both a warm-standby and a log-shipping configuration, measuring actual data loss and recovery time for each.
- **Result:** found the warm standby met the RPO target (0 rows lost) while log-shipping silently missed it entirely (10 of 10 rows lost) despite its `archive_timeout` configuration implying otherwise, and recommended the warm standby with the cost trade-off stated explicitly.

## Staff-Level Discussion

The most consequential fact in this scenario is not which pattern won, but that a plausible, correctly-configured setting (`archive_timeout=3`) produced a result (zero of ten rows recoverable) that directly contradicted what an engineer reading that configuration value alone would reasonably expect. That gap between configured intent and real, tested behavior is exactly the kind of risk that survives unnoticed until the day a disaster actually happens — at which point it is discovered under maximum pressure and with no chance to reverse the decision. The organizational discipline this scenario argues for — a recurring, calendared DR game day that actually destroys a primary and measures what survives — is a genuine cost (engineering time, risk of the exercise itself going wrong) that a Staff engineer must defend against a business tendency to treat DR configuration as "set once and trust," precisely because the alternative is discovering the gap during a real incident instead of a scheduled one.

## Related Handbook Chapters

- [Multi-Region Failover and Disaster Recovery](../handbook/system-design/multi-region-failover-and-disaster-recovery.md) — canonical comparison of warm-standby and log-shipping DR patterns and the `rpo-demo.sh` / `rpo-archive-demo.sh` measurements this incident reproduces.
- [Architecture Decision Records](../handbook/architecture/architecture-decision-records.md) — the documentation practice of grounding a DR pattern choice in tested evidence rather than a general argument.
