---
title: "Topic Specification"
document_type: syllabus-overview
status: extracted from the approved Syllabus Transformation Plan — Phase 1 scaffolding
last_updated: 2026-09-03
source: 00-project/syllabus-transformation-plan.md
---

> **Provenance note.** This file's content is extracted verbatim from `00-project/syllabus-transformation-plan.md` (approved 2026-09-03), not newly authored. The plan document remains the canonical source of record for any future dispute about intent; this file exists so the content lives at its designated `syllabus/` home per Phase 1 scaffolding.

# Topic Specification

## 4. Topic Specification

### 4.1 Relationship to the existing Canonical Chapter Template

`CLAUDE.md` already defines a Canonical Chapter Template (44 sections, reproduced in full at `CLAUDE.md:1299`). The Topic Specification below is **that template, restructured around explicit mastery levels** rather than an implicit "assume Senior/Staff" depth, plus a small number of additions the audit showed were missing (explicit Prerequisites as their own section rather than only front matter; an explicit Mastery Checklist; explicit Debugging and Design exercise categories rather than one undifferentiated "Practice Exercises"). Every existing chapter section maps onto exactly one part of this spec — nothing is orphaned.

### 4.2 The standard sections (in order)

| # | Section | Existing equivalent | Notes |
|---|---|---|---|
| 1 | **Why this matters** | "Why This Matters in Interviews" | Broadened: why it matters for building systems, not only for interviews |
| 2 | **Prerequisites** | Front-matter `prerequisites:` list | Promoted to a visible section with one-line reasons, not just links |
| 3 | **Foundation (L1)** | *Mostly absent — the core gap, see §2.4* | What it is, why it exists, when you'd reach for it, in plain language |
| 4 | **Core concepts (L2)** | "Core Concepts" | The practical vocabulary and comparisons a working engineer needs |
| 5 | **How it works internally (L3)** | "Internal Implementation," "Execution Flow" | Existing Senior-depth content mostly lands here unchanged |
| 6 | **Practical usage** | Scattered across "Java Examples," "Comparisons" | Runnable code, drawn from `practice/` |
| 7 | **Examples** | "Java Examples" | |
| 8 | **Common mistakes** | "Common Mistakes" | |
| 9 | **Edge cases** | *Often folded into "Common Mistakes" today* | Split out where it exists |
| 10 | **Performance implications** | "Performance Implications" | |
| 11 | **Trade-offs** | "Trade-offs," "Decision Framework" | |
| 12 | **Senior-level considerations (L3)** | "Senior-Level Expectations," "Concurrency/Security/Memory Implications" | |
| 13 | **Staff/system-level considerations (L4)** | "Staff-Level Discussion" | |
| 14 | **Production scenarios** | "Production Scenarios" | Linked from `production-cookbook/`, not duplicated |
| 15 | **Interview questions** | "Interview Questions," "Interview Answer Framework" | Unchanged; this is already strong |
| 16 | **Coding/practice exercises** | "Practice Exercises" | Linked to `practice/` |
| 17 | **Debugging exercises** | *New, split out of Practice Exercises* | Given production-cookbook's incident-diagnosis format is already this exact exercise type, most topics can source this directly |
| 18 | **Design exercises** | *New, split out; overlaps `architecture-atlas/` for system-scale topics* | |
| 19 | **Further reading** | "Additional Reading," "Official References" | |
| 20 | **Mastery checklist** | *New — see §5* | Verifiable per-level criteria, not "read this chapter" |

A topic may omit a section with a one-line justification in an HTML comment (`<!-- Edge cases: N/A — this is a design methodology, not an API -->`), matching the existing rule that a section may be dropped when it doesn't apply.

### 4.3 Front matter (extends the existing schema)

```yaml
---
title: <Topic Title>
slug: <kebab-case-slug>
document_type: syllabus-topic
domain: <e.g. 02-java/collections>
topic_id: <stable ID, e.g. T-201 — reused from the Master Topic Register where one exists>
status: draft | reviewed | canonical
version: 1.0
last_updated: YYYY-MM-DD
mastery_levels_covered: [L1, L2, L3, L4]   # which levels this document currently has written
prerequisites:
  - <topic_id or relative link>
related:
  - <topic_id or relative link>
practice: <relative link into practice/>
cheat_sheet: <relative link into cheat-sheets/>
flashcards: <relative link into flashcards/>
production_scenarios:
  - <relative link(s) into production-cookbook/>
interview_paths: [interview-emergency-sprint, senior-to-staff]   # which learning paths reference this topic
source_history:
  - <original path this content was migrated from, for traceability — required whenever content moves>
official_references: []
---
```

The `source_history` field is the mechanism that satisfies the "conserva el historial y la trazabilidad" requirement at the document level, independent of and in addition to `git mv` preserving file history at the VCS level (§9.4).

---
