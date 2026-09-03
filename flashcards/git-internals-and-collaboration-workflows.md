---
title: "Flashcards: Git Internals and Collaboration Workflows"
slug: git-internals-and-collaboration-workflows
document_type: flashcard-deck
domain: cloud
topic_id: "—"
canonical: ../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md
last_updated: 2026-09-02
---

# Flashcards: Git Internals and Collaboration Workflows

**Canonical chapter:** [`syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md`](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md)

## Card: Merge vs. rebase commit identity

**Prompt:**
Does `git rebase` change the hash of the commits it replays?

**Answer:**
Yes — each replayed commit is a genuinely new object (same diff, different parent, different hash). Merge leaves original commits unchanged.

**Why it matters:**
This is why rebasing a branch others have already pulled breaks their local history.

**Common trap:**
Assuming rebase just "reorders" the same commits in place.

**Related:**
[syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md)

## Card: Reflog recovery window

**Prompt:**
Why does `git reflog` recover work after a `git reset --hard`?

**Answer:**
`reset` only moves the branch pointer; the commit/tree/blob objects stay in `.git/objects` until an actual garbage collection prunes genuinely unreachable ones (default ~30-90 day expiry).

**Why it matters:**
Turns "I think I lost my work" into a routine, mechanical recovery step.

**Common trap:**
Believing a hard reset is instantly and permanently destructive.

**Related:**
[syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md)
