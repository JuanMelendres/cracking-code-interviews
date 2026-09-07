---
title: "Flashcards: Linked Lists and In-Place Manipulation"
slug: linked-lists-and-in-place-manipulation
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2104"
canonical: ../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md
last_updated: 2026-09-07
---

# Flashcards: Linked Lists and In-Place Manipulation

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md`](../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md)

## Card: What a dummy head is for

**Prompt:**
What problem does a "dummy head" node solve in linked-list manipulation?

**Answer:**
It's a placeholder node inserted before the real head, used purely to avoid writing separate logic for "what if I'm modifying the very first node" — removing an entire category of edge-case bugs by making the first real node's predecessor always exist.

**Why it matters:**
Reach for it whenever a list operation might modify or remove the actual head node — Merge Two Sorted Lists and Remove Nth Node From End both rely on it.

**Common trap:**
Forgetting a dummy head and special-casing head-modification logic separately, doubling the code paths that need to be correct.

**Related:**
[syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md](../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md)

## Card: Floyd's cycle-detection gap-closing argument

**Prompt:**
Why is Floyd's tortoise-and-hare technique guaranteed to detect a cycle, if one exists, within one full lap?

**Answer:**
Once both pointers have entered the cycle, the fast pointer gains on the slow pointer by exactly one node per iteration around a fixed-length cycle. A gap shrinking by exactly one each step cannot "jump over" the other pointer, so it must hit zero (a meeting) within at most one full lap.

**Why it matters:**
Achieves cycle detection in O(1) space, versus a `HashSet`-based approach's O(n) space — an explicit space-for-simplicity trade-off worth naming in an interview.

**Common trap:**
Defaulting straight to a `HashSet` of visited nodes without considering the O(1)-space alternative.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)

## Card: The fixed-gap invariant

**Prompt:**
In Remove Nth Node From End (LC 19), why does advancing the fast pointer `n` steps before moving both pointers together work, without ever counting the list's total length?

**Answer:**
Advancing `fast` by `n` steps first establishes an invariant — the gap between the two pointers stays exactly `n` nodes for the rest of the traversal. When `fast` reaches the last node, `slow` must be sitting exactly `n` nodes behind it, which is precisely one node before the target to remove.

**Why it matters:**
Found in a single pass, with no separate length-counting pass needed.

**Common trap:**
Not handling `n` equal to the list's total length (removing the actual head) — exactly what the dummy head handles without a separate branch.

**Related:**
[syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md](../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md)

## Card: The lost-reference reversal bug

**Prompt:**
An in-place linked-list reversal produces a list that appears to contain only the last node or two — the rest have "disappeared." What's the bug?

**Answer:**
The reversal loop overwrote a node's `next` pointer before saving a reference to what it used to point to, permanently losing access to the rest of the original chain — exactly why the reversal technique tracks three references (`prev`, `cur`, `next`), not two.

**Why it matters:**
The single most common linked-list reversal bug, and diagnosable purely from this symptom.

**Common trap:**
Reassigning `cur.next = prev` before saving `next` into a temporary variable.

**Related:**
[syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md](../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md)

## Card: Why Copy List with Random Pointer needs two passes

**Prompt:**
Why does Copy List with Random Pointer (LC 138) need two passes instead of one?

**Answer:**
A `random` pointer can point forward to a node not yet created during a single left-to-right pass. The clone map must be fully populated first (pass one), then every `next`/`random` pointer wired using that completed map (pass two), guaranteeing every lookup succeeds because every clone already exists.

**Why it matters:**
Generalizes beyond linked lists — any single-pass algorithm processing a structure with forward references needs either a pre-pass or a data structure that can hold "pending" references until targets exist.

**Common trap:**
Attempting a single-pass clone without a plan for unresolved forward references, then patching around the resulting `null` bugs.

**Related:**
[syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md](../syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md)
