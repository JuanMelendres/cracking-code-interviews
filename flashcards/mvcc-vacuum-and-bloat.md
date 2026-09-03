---
title: "Flashcards: MVCC, Vacuum, and Bloat"
slug: mvcc-vacuum-and-bloat
document_type: flashcard-deck
domain: databases
topic_id: T-612
canonical: ../handbook/databases/mvcc-vacuum-and-bloat.md
last_updated: 2026-09-02
---

# Flashcards: MVCC, Vacuum, and Bloat

**Canonical chapter:** [`syllabus/06-databases/mvcc-vacuum-and-bloat.md`](../syllabus/06-databases/mvcc-vacuum-and-bloat.md)

## Card: Does UPDATE modify a row in place?

**Prompt:**
Does a PostgreSQL UPDATE modify a row's existing tuple in place?

**Answer:**
No — it creates a brand new physical tuple version and marks the old one dead. Real,
measured proof: the row's `ctid` (physical location) and `xmin` (creating
transaction) both change on every UPDATE, and the old tuple remains physically
present, with a real non-zero `xmax`, until VACUUM reclaims it.

**Why it matters:**
This is the foundational MVCC fact everything else in this topic follows from.

**Common trap:**
Assuming UPDATE works like an in-place mutation, the way it might in a
non-MVCC system.

**Related:**
[handbook/databases/mvcc-vacuum-and-bloat.md](../syllabus/06-databases/mvcc-vacuum-and-bloat.md)

## Card: Why doesn't VACUUM shrink the table?

**Prompt:**
A table has bloated significantly. You run VACUUM. Why doesn't its file size
shrink?

**Answer:**
Plain VACUUM only marks dead tuple space reusable within the existing file for
future writes — it does not compact or return space to the OS. Only VACUUM FULL
(a full table rewrite) actually shrinks the file, at the cost of a full exclusive
lock for the duration.

**Why it matters:**
Measured directly: a table stayed at 10 MB after plain VACUUM despite its dead-tuple
count dropping to zero, and only shrank to its original 1776 kB after VACUUM FULL.

**Common trap:**
Expecting repeated plain VACUUM runs to eventually shrink an already-bloated table.

**Related:**
[handbook/databases/mvcc-vacuum-and-bloat.md](../syllabus/06-databases/mvcc-vacuum-and-bloat.md)

## Card: The unrelated-transaction bloat mechanism

**Prompt:**
How can a transaction that never queries table X still cause table X to bloat?

**Answer:**
Its open snapshot might still need to see a dead tuple version in ANY table — VACUUM
conservatively can't remove a dead tuple older than the oldest currently-open
snapshot in the entire database, regardless of which tables that snapshot's
transaction actually queries.

**Why it matters:**
This is a genuinely surprising, real production mechanism (see this chapter's BI-tool
production scenario) that separates real operational experience from textbook
knowledge.

**Common trap:**
Assuming vacuum-blocking only applies to tables a long transaction directly locks or
reads.

**Related:**
[handbook/databases/mvcc-vacuum-and-bloat.md](../syllabus/06-databases/mvcc-vacuum-and-bloat.md), [handbook/databases/isolation-levels-and-concurrency-anomalies.md](../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md)
