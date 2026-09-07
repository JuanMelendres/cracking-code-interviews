---
title: "Flashcards: Architecture Decision Records and Technical Writing for Engineers"
slug: architecture-decision-records-and-technical-writing
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1802
canonical: ../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md
last_updated: 2026-09-07
---

# Flashcards: Architecture Decision Records and Technical Writing for Engineers

**Canonical chapter:** [`syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md`](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: ADR required sections

**Prompt:**
What are the four standard sections of an Architecture Decision Record, per Michael Nygard's original format?

**Answer:**
Status, Context, Decision, and Consequences. This repository's own `templates/adr-template.md` follows this structure, extended with Decision Drivers, Considered Options, and a Related section.

**Why it matters:**
These four sections are the structural floor a mechanical completeness check can verify before any human review of quality even begins.

**Common trap:**
Naming the sections correctly but being unable to explain why any specific one matters — template-level rather than practice-level familiarity.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: Consequences must include negatives

**Prompt:**
Why is a Consequences section listing only positive outcomes a red flag?

**Answer:**
Every real decision trades something away. Skipping negative consequences is the single most commonly skipped part of an ADR, and its absence is the most reliable single signal of a rushed or retroactively-justified ADR.

**Why it matters:**
It's the fastest, most reliable quality tell a reviewer can check — distinguishing a genuinely interrogated decision from a hollow, template-compliant one.

**Common trap:**
Treating a structurally complete ADR (all four headings present) as proof the decision was honestly evaluated — a completeness check cannot verify honesty, only presence.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: Supersede, don't edit in place

**Prompt:**
When a past architectural decision changes, should you edit the old ADR to reflect the new decision?

**Answer:**
No — mark the old ADR "Superseded by [new ADR]" and write a new one. Editing in place destroys the historical record of why the *original* decision was made, which often remains relevant even after it's no longer current.

**Why it matters:**
An ADR is a dated snapshot of the best decision given what was known at the time, not a living document to be kept perpetually up to date.

**Common trap:**
Conflating an ADR with ordinary reference documentation that should always reflect the current state.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: Mechanical completeness check, real evidence

**Prompt:**
What did running this repository's `scripts/check_adr_completeness.py` against a deliberately incomplete ADR (missing `Status` and `Consequences`) actually produce?

**Answer:**
`FAIL /tmp/incomplete-adr.md: missing Status, Consequences` — it correctly failed and named exactly the two missing required sections, while passing cleanly against the repository's own `templates/adr-template.md`.

**Why it matters:**
It's a real, cheap, mechanical floor for completeness — but it cannot check the *quality* of what's inside each section (a Consequences section with only positive entries still passes this check).

**Common trap:**
Assuming a passing completeness script means the ADR is good, not just structurally present.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: The asymmetry argument for writing ADRs promptly

**Prompt:**
Why should an ADR be written close to the decision, not reconstructed weeks later?

**Answer:**
An ADR's reasoning is nearly always cheaper to write once, while fresh, than to reconstruct later from stale memory, scattered chat history, or git archaeology. A retroactively-written ADR also risks smoothing over genuine uncertainty into a falsely tidy narrative.

**Why it matters:**
A 15-minute investment at decision time can save hours of reconstruction effort for every future reader — often the original author's own future self.

**Common trap:**
Deferring the ADR until "there's time to write it properly," by which point the real reasoning has already faded.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)

## Card: Considered Options must be genuine, not strawmen

**Prompt:**
What's wrong with an ADR that lists one obviously-inferior "alternative" next to the chosen option?

**Answer:**
It isn't documenting a real decision process, it's performing one. A reviewer can often tell the difference by checking whether the alternative's own pros are stated honestly.

**Why it matters:**
Genuinely-considered alternatives are what let a future reader trust that the decision was actually weighed, not rubber-stamped after the fact.

**Common trap:**
Padding the Considered Options section with a token weak alternative purely to make the format look complete.

**Related:**
[syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md](../syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md)
