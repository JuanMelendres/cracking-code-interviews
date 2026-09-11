---
title: "Cheat Sheet: Incident Command — Roles and Real-Time Coordination"
slug: incident-command-roles-and-real-time-coordination
document_type: cheat-sheet
domain: 19-leadership-staff
topic_id: T-1906
canonical: ../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md
last_updated: 2026-09-11
---

# Incident Command: Roles and Real-Time Coordination

**Canonical chapter:** [`syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md`](../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md)

## Core Mental Model

Incident command is a real, named structure for coordinating a live, ongoing incident — not "whoever's around handles it." The single most important, most commonly violated rule: the Incident Commander coordinates; they do not personally debug. Per Google's SRE book, the operations team should be the only group modifying the system during an incident.

## Essential Definitions

- **Incident Commander (IC)** — holds overall responsibility for the incident's structure, delegates, does not debug.
- **Ops/Operations Lead** — the only person actually changing the system, under the IC's direction.
- **Communications** — the single point of contact for stakeholder status updates, freeing the IC/Ops lead from repeating "what's the status."
- **Planning** — longer-term bookkeeping (follow-up bugs, shift handoffs) so the IC isn't tracking that mid-incident.

## Decision Table

| Decision | Benefit | Cost |
|---|---|---|
| Activate full command structure for a SEV1 | Clear ownership, reduced diffusion of responsibility | Overhead inappropriate for a minor issue |
| IC delegates debugging instead of doing it | Both coordination and technical work get focused attention | The most knowledgeable person isn't the one directly driving the fix |
| Explicit, structured shift handoffs | No silent loss of context/ownership across a long incident | Real time cost to produce a handoff summary under pressure |

## Common Pitfalls

- The most senior engineer becomes IC by default and immediately starts debugging themselves — collapsing IC and Ops-lead roles.
- No one explicitly says "I'm IC" — everyone assumes someone else has it (diffusion of responsibility, the bystander effect).
- A shift handoff that's just "I'm heading off, good luck" instead of an explicit state transfer.
- Escalating every incident to full command structure regardless of severity, or never escalating even a genuine SEV1.

## Interview Answer Skeleton

**30-sec:** Incident command separates IC (coordinates, doesn't debug), Ops Lead (the only one changing the system), Communications, and Planning — the IC/Ops-lead collapse is the single most common, most damaging violation.

**2-min:** Add: not every incident needs all four roles — severity classification (SEV1–SEV4) determines activation. Diffusion of responsibility is the specific failure incident command prevents: without a named IC, capable engineers each assume someone else is coordinating.

**Staff-level framing:** IC authority is temporary and scoped to the incident, not a permanent hierarchy override — it can (and often should) go to someone other than the most senior person in the room.

## Related

- syllabus/19-leadership-staff/hiring-and-team-building.md
- syllabus/13-observability/incident-response-and-blameless-postmortems.md
