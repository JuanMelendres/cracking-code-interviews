---
title: "Flashcards: Incident Command — Roles and Real-Time Coordination"
slug: incident-command-roles-and-real-time-coordination
document_type: flashcard-deck
domain: 19-leadership-staff
topic_id: T-1906
canonical: ../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md
last_updated: 2026-09-11
---

# Flashcards: Incident Command — Roles and Real-Time Coordination

**Canonical chapter:** [`syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md`](../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md)

## Card: The most commonly violated rule

**Prompt:**
What's the single most important, most commonly violated rule of incident command?

**Answer:**
The Incident Commander coordinates; they do not personally debug the system. Per Google's SRE book, the operations team should be the only group modifying the system during an incident.

**Why it matters:**
The specific anti-pattern this whole structure exists to prevent — a senior engineer defaulting into both roles at once.

**Common trap:**
The most senior/knowledgeable engineer present becomes IC and immediately starts debugging themselves, collapsing two jobs that can't both be done well simultaneously under pressure.

**Related:**
[Incident Command: Roles and Real-Time Coordination](../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md)

## Card: The failure mode incident command prevents

**Prompt:**
What specific failure does naming an explicit Incident Commander prevent?

**Answer:**
Diffusion of responsibility — without a named IC, capable engineers each individually assume someone else is coordinating (the same social phenomenon behind the bystander effect), leading to conflicting simultaneous fixes or no one owning the escalation decision.

**Why it matters:**
Explains *why* the structure works, not just what the roles are.

**Common trap:**
Assuming a group of skilled engineers will naturally self-organize without an explicitly named coordinator.

**Related:**
[Incident Command: Roles and Real-Time Coordination](../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md)

## Card: IC authority is scoped and temporary

**Prompt:**
Does the Incident Commander need to be the most senior person in the room?

**Answer:**
No — IC authority exists only for the incident's duration and only over how the response is organized, not as a general management override. Someone with strong coordination skill, not necessarily the most tenured engineer, should often take the role.

**Why it matters:**
Prevents the common mistake of reflexively handing IC to whoever has the most seniority regardless of coordination skill.

**Common trap:**
Assuming IC should always default to the most senior or most technically knowledgeable person present.

**Related:**
[Incident Command: Roles and Real-Time Coordination](../syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md)
