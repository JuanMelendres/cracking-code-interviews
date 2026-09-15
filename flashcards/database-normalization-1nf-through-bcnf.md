---
title: "Flashcards: Database Normalization — 1NF Through BCNF"
slug: database-normalization-1nf-through-bcnf
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2411
canonical: ../syllabus/06-databases/database-normalization-1nf-through-bcnf.md
last_updated: 2026-09-15
---

# Flashcards: Database Normalization — 1NF Through BCNF

**Canonical chapter:** [`syllabus/06-databases/database-normalization-1nf-through-bcnf.md`](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)

## Card: The anomaly-first framing

**Prompt:**
What single question does every normal form (1NF–BCNF) answer, underneath its formal definition?

**Answer:**
"Can this schema currently represent two contradictory facts about the same real-world thing?" Each form closes one specific way that's possible — an unqueryable repeating cell, a fact duplicated via a partial or transitive dependency, or a non-key column's rule going unenforced.

**Why it matters:**
Reciting formal definitions without this framing is the single most common way candidates lose points on normalization questions.

**Common trap:**
Memorizing "atomic values / no partial dependency / no transitive dependency" without being able to produce a concrete anomaly for any of them.

**Related:**
[Database Normalization](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)

## Card: 2NF and 3NF are one pattern, twice

**Prompt:**
What's the real structural difference between a 2NF violation and a 3NF violation?

**Answer:**
Both are a non-key column duplicated across rows because it doesn't depend on the whole, direct key — 2NF's version depends on only *part* of a composite key; 3NF's version depends *transitively*, through another non-key column.

**Why it matters:**
Interviewers who ask about both are testing whether a candidate sees the shared pattern, not two disconnected rules.

**Common trap:**
Memorizing them as unrelated definitions instead of the same underlying bug caught at two points.

**Related:**
[Database Normalization](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)

## Card: Why BCNF exists

**Prompt:**
Give a real example of a table that satisfies 3NF but still has an anomaly BCNF would prevent.

**Answer:**
`enrollments(student_id, course_id, instructor)` — genuinely 3NF, but `instructor → course_id` is a real functional dependency from a non-candidate-key column, so nothing stops the same instructor being recorded against two different courses. Verified directly: a real, captured violation, then a real constraint error after the BCNF fix.

**Why it matters:**
Shows 3NF has a real, specific blind spot BCNF was introduced to close.

**Common trap:**
Assuming a 3NF-satisfying schema has no remaining normalization-related anomalies possible.

**Related:**
[Database Normalization](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)

## Card: The real denormalization trade-off

**Prompt:**
This chapter measured a specific denormalization trade-off. What was the real read win, and what was the real write cost?

**Answer:**
~3.27× faster reads (84.152ms normalized join vs. 25.763ms denormalized), at the cost of a single customer rename touching 80 rows instead of 1.

**Why it matters:**
Grounds "denormalization is faster" in a real, honest number with a real, honest write-side cost attached.

**Common trap:**
Citing the read speedup without mentioning the write-amplification cost, or assuming denormalization is free performance.

**Related:**
[Database Normalization](../syllabus/06-databases/database-normalization-1nf-through-bcnf.md)
