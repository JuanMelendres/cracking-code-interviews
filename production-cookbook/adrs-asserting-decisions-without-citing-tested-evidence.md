---
title: "ADRs That Assert a Decision Without Citing Tested Evidence"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/17-architecture/architecture-decision-records.md
  - ../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md
  - ../syllabus/17-architecture/cqrs-read-write-separation.md
source: handbook/architecture/architecture-decision-records.md#production-scenarios
---

# ADRs That Assert a Decision Without Citing Tested Evidence

## Context

Architecture Decision Records are a documentation practice rather than a running system, so this entry elevates the source chapter's own "production scenarios" faithfully to that shape: it uses this repository's own three worked ADRs — [`adr-001-cqrs-for-order-reporting.md`](../../practice/architecture/adr-examples/adr-001-cqrs-for-order-reporting.md), [`adr-002-streaming-replication-for-dr.md`](../../practice/architecture/adr-examples/adr-002-streaming-replication-for-dr.md), and [`adr-003-backward-compatibility-for-orders-topic.md`](../../practice/architecture/adr-examples/adr-003-backward-compatibility-for-orders-topic.md) — as labeled, representative decisions whose value comes specifically from citing real, already-executed evidence from elsewhere rather than general argument.

## Symptoms

An ADR that states a conclusion ("CQRS is faster," "streaming replication is safer," "BACKWARD compatibility is the right default") in general terms is, on paper, indistinguishable from one whose author never actually tested the alternatives — nothing in the document itself would reveal the difference to a later reader or auditor.

## Impact

A decision record that cannot be distinguished from an untested assertion loses its core value: a future engineer revisiting the decision cannot tell whether the original choice was validated or merely asserted, and cannot easily judge whether changed circumstances would change the outcome.

## Initial Hypotheses

None applicable in the incident-diagnosis sense — this entry documents a governance practice and the standard it is measured against, not a specific failure being root-caused.

## Evidence

`adr-001-cqrs-for-order-reporting.md` considers three options for a degrading reporting query and rejects two of them (an index; a read replica) for the same real reason: neither changes the query's fundamental shape problem. The chosen option cites the [CQRS chapter's](../architecture/cqrs-read-write-separation.md) own real, measured 4.6–5.4x speedup as the specific evidence tipping the decision — not a general claim that "CQRS is faster."

`adr-002-streaming-replication-for-dr.md` considers log-shipping DR first — the cheaper option — and rejects it not on theoretical grounds but because the [multi-region DR chapter's](../system-design/multi-region-failover-and-disaster-recovery.md) own real test showed it losing all 10 of 10 rows in a real 10-second window, directly failing the stated RPO target. The ADR's Consequences section can honestly state the cost accepted (a continuously-running standby) because the alternative's cost (real, unacceptable data loss) was actually measured, not assumed.

`adr-003-backward-compatibility-for-orders-topic.md` picks Confluent's own default compatibility mode — but the ADR's real value is naming, explicitly in Consequences, the real constraint that default imposes (every new field needs a meaningful default from day one) rather than presenting the default as a free, zero-cost choice.

## Investigation Timeline

1. Reviewed `adr-001` for whether its chosen option (CQRS) is justified by a specific, cited number or by general reasoning — found it cites the CQRS chapter's own measured 4.6–5.4x speedup directly.
2. Reviewed `adr-001`'s rejected options (an index; a read replica) for whether the rejection reasons are specific — found both rejected for the same, stated structural reason (neither changes the query's shape problem), not a vague "wasn't good enough."
3. Reviewed `adr-002` for whether the cheaper, rejected option (log-shipping) was dismissed on theory or evidence — found it cites the multi-region DR chapter's own real destructive test result (10 of 10 rows lost) as the specific reason.
4. Confirmed `adr-002`'s Consequences section states the accepted cost (a continuously-running standby) honestly, made possible specifically because the alternative's cost was measured rather than assumed.
5. Reviewed `adr-003` for whether a "default" choice was treated as free — found the ADR explicitly names the real constraint the default imposes (every new field needs a meaningful default) in its Consequences section, rather than omitting it.

## Root Cause

An ADR template alone does not guarantee evidence-grounded reasoning; without an explicit norm requiring a decision's stated advantage or an alternative's stated risk to cite a real, checkable source, an ADR can read as well-structured while still being functionally equivalent to an untested opinion.

## Immediate Mitigation

None applicable — this entry documents an established, functioning practice (all three worked ADRs already cite real evidence) rather than a defect being corrected.

## Permanent Fix

Require every ADR's Decision and Consequences sections to cite a specific, checkable source — a measured benchmark, a destructive test result, an explicitly named default's documented behavior — for its central claims, following the pattern all three worked ADRs already demonstrate, rather than accepting general assertions as sufficient justification.

## Alternatives Considered

Accepting well-argued but uncited reasoning as sufficient for an ADR — implicitly rejected by the practice these three examples demonstrate, since each one specifically grounds its pivotal claim in a real, external, already-executed measurement rather than argument alone.

## Trade-offs

Requiring cited evidence for every ADR's central claim raises the bar for producing one — a decision genuinely cannot yet be evidence-grounded if no relevant measurement exists yet, which means either deferring the ADR until a targeted test is run, or explicitly flagging the decision as provisional pending evidence, rather than writing the same ADR anyway without the citation.

## Prevention

Treat "does this ADR's central claim cite a real, checkable source" as a standing review question for every architecture decision record, modeled directly on how `adr-001`, `adr-002`, and `adr-003` each ground their decisions in another chapter's real, measured evidence rather than restating a general argument.

## Monitoring and Alerts

- Add an ADR review checklist item — "does the Decision and Consequences section cite a specific measurement, test result, or documented default, rather than a general claim?" — enforced at PR/merge time for any new ADR, mirroring the pattern this entry documents across all three worked examples.
- Periodically audit the ADR archive for records whose central claims are not traceable to any cited source, flagging them for retroactive evidence-gathering or an explicit "provisional, not yet validated" label, rather than letting uncited ADRs accumulate silently alongside cited ones.
- Track how often an ADR's cited evidence is later revisited or invalidated by new measurements (e.g., a benchmark re-run after a dependency upgrade) as a signal of how current the decision archive actually is, rather than assuming an ADR's evidence stays valid indefinitely once written.

## Interview Story

This maps directly to "how do you make an architecture decision defensible, not just documented" — present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** an architecture-decision-record practice needed a way to distinguish a genuinely evidence-grounded decision from one that merely reads as well-argued.
- **Task:** establish and demonstrate a norm for what "grounded" actually means in an ADR.
- **Action:** examined three worked ADRs and confirmed each ties its pivotal claim to a specific, real, already-executed measurement elsewhere in the same body of work — a 4.6–5.4x CQRS speedup, a destructive DR test losing 10 of 10 rows, and an explicitly named cost of a schema-compatibility default — rather than a general assertion.
- **Result:** codified "cite a real, checkable source for the central claim" as the standing bar for any future ADR, making the decision archive auditable rather than merely readable.

## Staff-Level Discussion

The organizational risk an evidence-free ADR practice carries is subtle because it is invisible at write time: a well-written, well-structured ADR that asserts its conclusion in general terms looks identical, to a reviewer skimming it, to one that is genuinely grounded in a tested fact — the difference only matters later, when someone needs to know whether the decision would still hold under changed circumstances, or whether it can be defended to an auditor, a new hire, or a skeptical stakeholder challenging it years afterward. A Staff engineer's responsibility for architectural documentation is not just to write good ADRs personally but to establish the review norm that makes "assert without evidence" visibly incomplete to every reviewer, the way these three examples make explicit ties to real, checkable measurements the expected shape rather than an occasional nicety. This also has an honest cost worth naming: requiring cited evidence means some decisions genuinely cannot be finalized as confidently-justified ADRs until the relevant measurement exists, which argues for treating "run the targeted test" as part of the decision-making timeline itself, not an optional afterthought layered on top of a decision already made.

## Related Handbook Chapters

- [Architecture Decision Records](../syllabus/17-architecture/architecture-decision-records.md) — canonical ADR structure and the norm of citing real evidence this entry documents.
- [Multi-Region Failover and Disaster Recovery](../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) — source of the measured RPO evidence cited in `adr-002`.
- [CQRS: Read/Write Separation](../syllabus/17-architecture/cqrs-read-write-separation.md) — source of the measured speedup evidence cited in `adr-001`.
