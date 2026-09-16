---
title: "Legacy Canonical Chapter Template (superseded)"
document_type: reference-template
status: superseded — kept for historical mapping only
---

# Legacy Canonical Chapter Template

> **Superseded.** This 44-section template predates `syllabus/00-overview/topic-specification.md` (the 20-section spec, approved 2026-09-03), which restructures every section below around explicit mastery levels (L1–L4) instead of an implicit Senior/Staff-only depth. **Use `syllabus/00-overview/topic-specification.md` for all new chapters.** This file is kept only because that spec's own §4.1 mapping table references it by name ("CLAUDE.md's Canonical Chapter Template") — deleting it would orphan that citation.

```markdown
---
title: <Chapter Title>
slug: <kebab-case-slug>
document_type: handbook-chapter
domain: <domain>
status: draft
version: 1.0
last_updated: YYYY-MM-DD
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - <relative link>
related:
  - <relative link>
official_references:
  - <URL>
---

# <Chapter Title>

## Table of Contents
## Learning Objectives
## Why This Matters in Interviews
## Mental Model
## Definition and Purpose
## Historical Context
## Core Concepts
## Internal Implementation
## Execution Flow
## Diagrams
## Java Examples
## Production Scenarios
## Failure Modes and Debugging
## Trade-offs
## Performance Implications
## Memory Implications
## Concurrency Implications
## Security Implications
## Decision Framework
## Comparisons
## Common Mistakes
## Anti-Patterns
## Best Practices

## Interview Answer Framework
### 30-Second Answer
### 2-Minute Answer
### 10-Minute Deep Dive
### Whiteboard Explanation
### Production Example
### Trade-offs to Mention
### Common Candidate Mistakes
### Typical Follow-Up Questions
### Senior-Level Expectations
### Staff-Level Discussion

## Interview Questions
### Question 1
**Expected answer**
**Common mistakes**
**Follow-up questions**
**Senior-level expectations**
**Staff-level expectations**

## Summary
## Key Takeaways
## Cheat Sheet
## Flashcards
## Practice Exercises
## Solutions
## Additional Reading
## Official References
```

Not every implication section requires equal length — include only material that is technically relevant, but do not omit relevant concerns.
