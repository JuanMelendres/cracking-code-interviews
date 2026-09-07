---
title: "Cheat Sheet: Architecture Decision Records and Technical Writing for Engineers"
slug: architecture-decision-records-and-technical-writing
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1802
canonical: ../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md
last_updated: 2026-09-06
---

# Architecture Decision Records and Technical Writing for Engineers

**Canonical chapter:** [`syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md`](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Core Mental Model

An ADR's real value is realized at read time, not write time: a 15-minute investment when a decision is made can save hours of reconstruction effort for every future reader who needs to know *why* the system is shaped the way it is — often the original author's own future self.

## Essential Definitions

- **ADR (Architecture Decision Record)** — a short, dated document capturing one significant technical decision, its context, the alternatives considered, and its consequences, written at (or near) decision time, not reconstructed afterward (Michael Nygard's 2011 format).
- **Standard sections** — Status, Context, Decision, Consequences. This repository's own `templates/adr-template.md` follows this structure, extended with Decision Drivers, Considered Options, and a Related section.
- **Context** — describes the situation neutrally, without pre-supposing the answer; a Context that already reads as justification for the eventual decision was usually written backwards.
- **Considered Options** — must include genuinely-considered alternatives, not strawmen; a reviewer can often tell the difference from whether the alternative's own pros are stated honestly.
- **Consequences** — must include negative consequences, not only positive ones; the single most commonly skipped section, and its absence is the most reliable single signal of a rushed or retroactively-justified ADR.
- **Status lifecycle** — proposed, accepted, rejected, deprecated, or superseded by a specific later ADR. Superseding (rather than editing in place) preserves the historical record of why the original decision was made.

## Decision Table

| Situation | Response |
|---|---|
| Decision changes later | Write a new ADR that supersedes the old one; don't edit the original in place |
| Decision was genuinely obvious, no real alternative existed | Still write a short ADR; state directly why the alternative space was narrow |
| Decision made under time pressure/incomplete information | State this explicitly in Context rather than presenting it as fully informed |
| New information emerges without the decision itself changing | Add a small dated addendum or linked follow-up ADR, not a silent edit |
| Checking basic structural completeness | Run a mechanical checker (this repo's `scripts/check_adr_completeness.py`) — verifies section presence, not honesty |
| Checking genuine quality | Requires human review judgment (real alternatives, honest negative consequences) — no script can verify this |

## Common Pitfalls

- Skipping the Consequences section's negative half entirely — the single most reliable tell of an under-interrogated decision.
- Writing the ADR weeks after the decision, reconstructing reasoning from memory — risks smoothing genuine uncertainty into a falsely tidy, retroactive narrative.
- Editing an old ADR in place to reflect a new decision, rather than superseding it — destroys the historical record of why the original decision was made.
- Treating a structural-completeness script's pass as proof the ADR is good — it only confirms all four sections exist, not that any are honest or substantive.

## Interview Answer Skeleton

**30-sec:** An ADR is a short, dated document capturing a significant technical decision, its context, alternatives, and consequences, written near decision time so future readers know *why*, not just *what*. The required sections are Status, Context, Decision, and Consequences, and Consequences must include negative outcomes — every real decision trades something away.

**2-min:** The standard ADR format (Nygard, 2011) has four sections. Context must describe the situation neutrally, without presupposing the answer. Considered Options must list genuinely weighed alternatives, not strawmen. Consequences must include real negative consequences — its complete absence is the most reliable single signal of a rushed or retroactively-justified ADR. Status has a real lifecycle (proposed/accepted/rejected/deprecated/superseded); superseding an old ADR with a new one, rather than editing in place, preserves the historical record of why the original decision was made, which often remains relevant later. This repo's own `scripts/check_adr_completeness.py` mechanically verifies all four required headings exist — running it against `templates/adr-template.md` passes, and running it against a copy missing Status and Consequences correctly fails and names both missing sections. That check is a real, cheap floor, but it cannot verify that Consequences is actually honest, only that the heading is present.

**Staff-level framing:** Staff engineers are frequently the ones establishing or revising the ADR template and process an organization adopts, which means understanding *why* each required section exists — not just that it's required — is itself part of the Staff bar. Auditing an organization's existing ADR corpus for genuine decision quality (not just template compliance) — checking whether Consequences sections honestly name real costs, whether superseded ADRs are properly linked rather than silently abandoned — is high-leverage organizational work: a corpus of template-compliant-but-hollow ADRs is a false sense of documented reasoning that fails exactly when a high-stakes decision needs revisiting under pressure.

## Production Warning Signs

- A team's ADR corpus has grown to dozens of documents, but engineers still can't find the reasoning behind past decisions and end up re-deciding already-settled questions. Check whether ADRs are being superseded and cross-linked correctly — a stale, no-longer-current ADR with no pointer to the newer decision is a common cause — and whether ADRs are indexed or discoverable at all, since a well-written ADR nobody can find provides none of its intended value.
- A real, documented case in this repository's own production cookbook: an ADR asserted a decision without citing tested evidence, passing structural review while still failing the "actually honest" bar — the exact gap between structurally complete and genuinely substantive.

## Related

- syllabus/18-engineering-practices/code-review-standards-and-practice.md
- syllabus/20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md
