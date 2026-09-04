---
title: "Linked Lists and In-Place Manipulation"
slug: linked-lists-and-in-place-manipulation
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2104
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - arrays-two-pointers-and-sliding-window.md
  - hashing-patterns-and-frequency-maps.md
  - ../02-java/collections/arraylist-and-linkedlist-internals.md
practice: ../../practice/java/week-20/linked-lists/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-20/01-linked-lists-coding-practice.md
---

# Linked Lists and In-Place Manipulation

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-20/01-linked-lists-coding-practice.md` — real, compiled, executed code (`practice/java/week-20/linked-lists/`), re-verified on OpenJDK 21.0.12 while writing this chapter (10/10 assertions passing).

This is Master Topic Register **T-1405** (IWI 5.0, very-high frequency). This chapter is about manipulating raw, interview-style `ListNode` structures directly — a genuinely different skill from using `java.util.LinkedList`, whose own internal doubly-linked structure and trade-offs against `ArrayList` are covered in [ArrayList and LinkedList Internals](../02-java/collections/arraylist-and-linkedlist-internals.md). Nothing here duplicates that chapter; this one is about pointer manipulation technique, not the built-in collection's API or performance characteristics.

## 1. Why This Matters

Linked-list problems are less about a specific algorithm and more about careful, bug-free pointer bookkeeping under interview pressure — reversing links without losing a reference to the rest of the list, finding a middle or cycle in one pass without extra memory, and knowing the small set of techniques (dummy heads, slow/fast pointers, reversal-in-place) that recur across almost every problem in this pattern. Getting the pointer order wrong by one step is the single most common way to lose interview credit here, even when the overall approach is correct.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — this chapter's techniques are valued specifically for achieving O(1) extra space where a naive approach (dump to an array, manipulate, rebuild) would cost O(n).

## 3. Foundation (L1)

**A linked list is a chain of nodes, each holding a value and a reference to the next node** — unlike an array, there's no way to jump directly to the Nth element; you must walk the chain from the head. This single structural fact is the source of almost every technique in this chapter: since you can't index directly, the standard array techniques (Section on [Arrays, Two Pointers, and Sliding Window](arrays-two-pointers-and-sliding-window.md)) don't directly apply, and linked-list-specific pointer techniques take their place.

**A "dummy head" is a placeholder node inserted before the real head**, used purely to avoid writing separate logic for "what if I'm modifying the very first node" — a small trick that removes an entire category of edge-case bugs by making the first real node's predecessor always exist.

## 4. Core Concepts (L2)

**The slow/fast pointer technique (Floyd's tortoise and hare)** moves one pointer one step at a time and another two steps at a time through the same list. This single technique answers multiple different questions depending on what's checked: whether the two pointers ever meet (cycle detection, Section 7 Problem 2), or where the slow pointer ends up when the fast pointer reaches the end (finding the middle node, used inside Problem 4).

**The fixed-gap two-pointer technique** (Section 7, Problem 3) advances one pointer `n` steps ahead of the other, then moves both together — when the lead pointer reaches the end, the trailing pointer is exactly `n` nodes from the end, found in a single pass with no need to first count the list's total length.

**In-place reversal** walks the list once, at each node redirecting its `next` pointer to point *backward* to the previous node instead of forward, using three tracked references (`prev`, `cur`, `next`) to avoid losing the rest of the list the instant a `next` pointer is overwritten. This is the single most-reused sub-technique across linked-list problems — Reorder List (Section 7, Problem 4) uses it as one step inside a larger three-step algorithm.

**The hash-map-clone technique** (Copy List with Random Pointer, Section 7, Problem 5) is what's needed whenever a structure has pointers that can reference *forward*, to a node not yet created in a single left-to-right pass — a plain single-pass copy can't resolve a `random` pointer to a node that doesn't exist yet, so the clone map must be fully built first, then wired in a second pass.

## 5. How It Works Internally (L3)

**Floyd's cycle-detection correctness argument**: if a cycle exists, once both pointers have entered it, the fast pointer gains on the slow pointer by exactly one node per iteration (it moves two steps to the slow pointer's one, inside a loop of fixed length). A pointer gaining by exactly one position per step around a fixed-length cycle cannot "jump over" the other pointer — the gap between them shrinks by exactly one each iteration, so it must hit zero (a meeting) within at most one full lap of the cycle. This is the same core cycle-detection idea as [Hashing's Happy Number problem](hashing-patterns-and-frequency-maps.md#5-how-it-works-internally-l3), which uses an O(n)-space hash set to detect a cycle in a numeric sequence instead — Floyd's technique achieves the identical result with O(1) space by exploiting the specific geometry of two pointers moving at different fixed speeds, a genuinely different trade-off worth naming explicitly when one technique is chosen over the other.

**The fixed-gap technique's correctness**: advancing the fast pointer by `n` steps *before* starting to move both pointers together establishes an invariant — the gap between the two pointers stays exactly `n` nodes for the rest of the traversal. When the fast pointer (now starting from a position `n` ahead) reaches the last node, the slow pointer must be sitting exactly `n` nodes behind it, which is precisely one node before the target to remove — found without ever needing a separate pass to count the list's total length first.

**Reorder List's three-technique composition** is instructive precisely because it's a combination, not a new technique: slow/fast pointers find the middle (Section 4), the second half is reversed in place (Section 4), and the two halves are then merged by alternating node-splicing — a structurally identical operation to Merge Two Sorted Lists (Section 7, Problem 1), just alternating unconditionally instead of comparing values. Recognizing a "new" problem as a known composition of already-mastered sub-techniques, rather than something requiring an entirely new idea, is exactly the skill combination-tier problems are designed to test.

## 6. Practical Usage

- **Reach for a dummy head whenever a list operation might modify or remove the actual head node** — Merge Two Sorted Lists and Remove Nth Node From End both use this to avoid a separate "is this the head" branch.
- **Reach for slow/fast pointers for any "find the middle" or "detect a cycle" requirement** — the two most common linked-list sub-problems that compose into larger ones (Section 5's Reorder List).
- **Reach for a hash-map clone pass whenever a structure has pointers that can reference forward, not yet created in a single traversal order** — the same underlying need that Copy List with Random Pointer exemplifies applies to cloning any graph-like structure with forward references, not just this one specific problem shape.

## 7. Examples

**Problem 1 — LC 21, Merge Two Sorted Lists.**

```java
static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) { tail.next = l1; l1 = l1.next; }
        else { tail.next = l2; l2 = l2.next; }
        tail = tail.next;
    }
    tail.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

**Retrospective:** the dummy head avoids special-casing "is this the first node" logic. Once one list is exhausted, the remainder of the other is already sorted and can be spliced on directly. **Complexity:** O(n+m) time, O(1) extra space (splices existing nodes).

**Problem 2 — LC 141, Linked List Cycle.**

```java
static boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

**Retrospective:** see Section 5's gap-closing correctness argument. **Complexity:** O(n) time, O(1) space — no hash set of visited nodes needed.

**Problem 3 — LC 19, Remove Nth Node From End.**

```java
static ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode fast = dummy, slow = dummy;
    for (int i = 0; i < n; i++) fast = fast.next;
    while (fast.next != null) { fast = fast.next; slow = slow.next; }
    slow.next = slow.next.next;
    return dummy.next;
}
```

**Retrospective:** see Section 5's fixed-gap invariant. The dummy head handles removing the actual head node without a separate branch. **Complexity:** O(n) time, single pass, O(1) space.

**Problem 4 — LC 143, Reorder List.**

```java
static void reorderList(ListNode head) {
    if (head == null || head.next == null) return;
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next; fast = fast.next.next;
    }
    ListNode secondHead = slow.next;
    slow.next = null;
    ListNode prev = null, cur = secondHead;
    while (cur != null) {
        ListNode next = cur.next;
        cur.next = prev;
        prev = cur;
        cur = next;
    }
    ListNode first = head, second = prev;
    while (second != null) {
        ListNode firstNext = first.next;
        ListNode secondNext = second.next;
        first.next = second;
        if (firstNext != null) second.next = firstNext;
        first = firstNext;
        second = secondNext;
    }
}
```

**Retrospective:** combines slow/fast middle-finding, in-place reversal, and merge-style splicing — a good illustration of why combination problems sit later in a progression than single-technique ones (Section 5). **Complexity:** O(n) time, O(1) extra space.

**Problem 5 — LC 138, Copy List with Random Pointer.**

```java
static RandomListNode copyRandomList(RandomListNode head) {
    if (head == null) return null;
    Map<RandomListNode, RandomListNode> clones = new HashMap<>();
    RandomListNode cur = head;
    while (cur != null) {
        clones.put(cur, new RandomListNode(cur.val));
        cur = cur.next;
    }
    cur = head;
    while (cur != null) {
        clones.get(cur).next = clones.get(cur.next);
        clones.get(cur).random = clones.get(cur.random);
        cur = cur.next;
    }
    return clones.get(head);
}
```

**Retrospective:** the two-pass structure exists because a `random` pointer can point forward to a node not yet created during a single pass — the map must be fully populated before any `random` pointer can be safely resolved. A well-known O(1)-space alternative interleaves clone nodes directly into the original list rather than using a map. **Complexity:** O(n) time, O(n) space for the map.

## 8. Common Mistakes

- **Losing a reference to the rest of the list during in-place reversal** by overwriting a node's `next` pointer before saving it — the exact reason the reversal technique (Section 4) tracks three references (`prev`, `cur`, `next`), not two.
- **Forgetting a dummy head and special-casing head-modification logic separately**, doubling the code paths that need to be correct and doubling the chance of a bug in one of them.
- **Attempting a single-pass clone of a structure with forward-referencing pointers** (Copy List with Random Pointer) — Section 4/5 explains directly why this is structurally impossible without either two passes or an interleaving trick, not just an inefficiency to optimize later.

## 9. Edge Cases

- **A list of length 0 or 1** — Reorder List's own guard clause (`if (head == null || head.next == null) return;`) exists specifically because the slow/fast middle-finding logic assumes at least two nodes to meaningfully split.
- **A cycle that begins partway through the list, not at the head** — Linked List Cycle's own verified test case (`3->2->0->-4->2`, a cycle back to a middle node) confirms Floyd's technique doesn't require the cycle to include the head.
- **`n` equal to the list's total length** (Remove Nth Node From End, removing the actual head) — this is exactly the case the dummy head (Section 6) exists to handle without a separate branch.

## 10. Performance Implications

Real, executed verification from `practice/java/week-20/linked-lists/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC21 mergeTwoLists([1,2,4],[1,3,4])
  PASS  LC141 hasCycle(3->2->0->-4->2) = true
  PASS  LC141 hasCycle([1,2,3]) = false
  PASS  LC19 removeNthFromEnd([1,2,3,4,5], 2)
  PASS  LC143 reorderList([1,2,3,4])
  PASS  LC143 reorderList([1,2,3,4,5])
  PASS  LC138 copyRandomList produces a distinct head node (deep copy, not same reference)
  PASS  LC138 copy head val preserved
  PASS  LC138 copy next val preserved
  PASS  LC138 copy's random pointers rewired to the CLONE graph, not the original
Week 20 — Linked Lists (LC 21, 141, 19, 143, 138): 10/10 assertions passed
```

Every problem here achieves O(1) extra space except Copy List with Random Pointer's O(n) map-based approach — the practical performance lesson is that linked-list problems are disproportionately about *space* optimization relative to array problems, precisely because a linked list's own structure (existing, mutable pointers) offers a natural place to store reversal or reordering state without any auxiliary array or list, if the pointer bookkeeping is done carefully.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Slow/fast pointers | O(1) space cycle/middle detection | Requires careful proof of the gap-closing argument (Section 5) to trust correctness on an unfamiliar variant |
| In-place reversal | O(1) extra space | Destroys the original list structure — unsuitable if the original order must be preserved elsewhere |
| Hash-map clone (forward-reference structures) | Simple, correct, handles arbitrary forward references | O(n) extra space; a follow-up O(1)-space interleaving technique exists but is more intricate |
| Dummy head | Removes head-modification special-casing entirely | One extra node allocated and discarded — negligible cost, but worth explaining if asked why it's there |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is defending a pointer-manipulation algorithm's correctness with the specific invariant it maintains (the fixed gap in Remove Nth Node From End, the closing gap in cycle detection) rather than "I've seen this pattern before." An interviewer who changes one detail of a familiar problem (a cycle that could start anywhere, a list where nodes might be revisited for a different reason) is testing whether the candidate's understanding is the actual invariant or a memorized code template that quietly breaks under the changed condition.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, this chapter's core discipline — bookkeeping through mutable, pointer-based state without losing references or corrupting a shared structure mid-mutation — transfers directly to real production code working with mutable linked or graph-like data structures (an in-memory LRU cache's internal doubly-linked list, a dependency graph being rewired during a migration). The same three-reference discipline that prevents losing the "rest of the list" during reversal (Section 8) is the same discipline that prevents corrupting a production data structure during an in-place mutation under concurrent access — though at production scale, this compounds with genuine concurrency concerns (multiple threads mutating shared pointers) that a single-threaded interview problem never has to address, connecting directly to [Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) for what goes wrong when that discipline is skipped under real concurrent mutation.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a linked-list-pointer-manipulation-specific root cause.

> Planned reference: a future `production-cookbook/` entry covering a real in-memory data-structure corruption bug (e.g., an LRU cache's internal linked list corrupted by an incomplete eviction under concurrent access) would be a natural, non-duplicative addition connecting this chapter's pointer-discipline lesson to a genuine concurrency-scale incident.

## 15. Interview Questions

### Question 1 — How would you detect whether a linked list contains a cycle, using O(1) extra space?

**Why interviewers ask it.** It's the canonical test of whether a candidate knows Floyd's tortoise-and-hare technique specifically, versus defaulting to an O(n)-space hash-set approach that works but misses the more elegant, more frequently-expected O(1) solution.

**Expected answer.** Two pointers, one advancing one step at a time, one advancing two steps at a time. If a cycle exists, the faster pointer will eventually equal the slower one; if the list is acyclic, the faster pointer reaches the end (`null`) first. O(n) time, O(1) space.

**Minimum acceptable answer.** Produces a correct cycle-detection solution, even if it's the O(n)-space hash-set version.

**Strong Senior answer.** Produces the O(1)-space Floyd's-cycle version directly, and can explain the gap-closing correctness argument (Section 5) when asked why it's guaranteed to work.

**Staff-level extension.** Extends to the follow-up almost every interviewer asks next: "can you find *where* the cycle begins, not just whether one exists" — correctly describes the second phase (reset one pointer to the head, advance both one step at a time; they meet exactly at the cycle's start), and can at least gesture at why that specific reset-and-restart step works (a real, non-obvious piece of number theory about the relationship between the list's pre-cycle length and the cycle's own length).

**Common mistakes.** Defaulting straight to a `HashSet` of visited nodes without considering the O(1)-space alternative, especially when the interviewer's phrasing ("without extra space") is a direct hint toward Floyd's technique.

**Follow-up questions.** "What if you needed to find the cycle's length, not just its start?" (Once the meeting point is found, continue advancing one pointer around the cycle, counting steps, until it returns to the same node.)

### Question 2 — Why does copying a linked list with an extra `random` pointer require two passes, when a plain linked-list copy only needs one?

**Why interviewers ask it.** It tests whether a candidate can identify *why* a single-pass approach structurally fails here, not just that it does — the forward-reference problem named directly in Section 4/5.

**Expected answer.** A `random` pointer can point to any node in the list, including one that comes *after* the current node in traversal order — meaning, during a single left-to-right pass, the clone for that target node might not exist yet when you'd need to wire the current clone's `random` pointer to it. Building the full original-node-to-clone map first (one pass), then wiring every `next` and `random` pointer using that completed map (a second pass), guarantees every lookup succeeds because every clone already exists by the time any pointer needs wiring.

**Minimum acceptable answer.** States that `random` can point forward, and that this is why two passes are needed, even without a precise mechanism for why a single pass structurally can't work.

**Strong Senior answer.** Can also describe the O(1)-space interleaving alternative (temporarily splicing each clone node directly after its original, using that adjacency to resolve `random` pointers without a map, then unsplicing) as a genuine, real follow-up technique.

**Staff-level extension.** Generalizes the underlying principle — any single-pass algorithm processing a structure with forward references needs either a pre-pass to establish targets first, or a data structure (like a map) that can hold "pending" references until their targets exist — a pattern that recurs well beyond linked lists, in areas like resolving forward-declared references during a compiler's symbol-table construction.

**Common mistakes.** Attempting to solve this in one pass without a clear plan for unresolved forward references, then patching around the resulting `null` bugs rather than recognizing the fundamental structural issue upfront.

**Follow-up questions.** "Could you solve this recursively instead?" (Yes, with memoization keyed by original node to avoid infinite recursion on any node with a `random` pointer forming a cycle back to an ancestor in the recursion — a real, additional subtlety worth naming.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-20/linked-lists/) yourself and confirm the same 10/10 assertions pass.
- Implement the O(1)-space interleaving alternative for Copy List with Random Pointer (Section 15's follow-up) from scratch, and compare its complexity to the hash-map version measured in Section 10.
- Implement finding the start of a cycle (not just detecting one exists) as a direct extension of `hasCycle` (Section 7, Problem 2) — the second-phase technique referenced in Interview Question 1's Staff-level extension.

## 17. Debugging Exercises

**Symptom:** an in-place linked-list reversal function, applied to a list with more than a few nodes, produces a list that appears to only contain the last one or two original nodes — the rest seem to have "disappeared."

**Diagnose:** this is almost always Section 8's first common mistake — overwriting a node's `next` pointer before saving a reference to what it used to point to, permanently losing access to the rest of the original chain. Confirm by checking whether the reversal loop saves `next` (the node after `cur`) into a temporary variable *before* reassigning `cur.next = prev` — if that save happens after the reassignment, or not at all, the rest of the list becomes unreachable the instant the first `next` pointer is overwritten, which explains exactly the "only the last node or two survive" symptom (whatever was already reachable via `prev` before the bug's effects compound).

## 18. Design Exercises

**Design constraint:** design the internal data structure for an LRU (Least Recently Used) cache that must support O(1) get and O(1) put, including O(1) eviction of the least-recently-used entry.

Design this using a doubly-linked list plus a hash map, and connect it directly to this chapter's own techniques: the hash map provides O(1) node lookup by key (the same lookup role a `HashMap` plays throughout [Hashing Patterns](hashing-patterns-and-frequency-maps.md)); the doubly-linked list maintains recency order, with O(1) "move this node to the front" and O(1) "remove the tail node" operations, both requiring exactly the careful three-reference pointer discipline (Section 4/8) this chapter's reversal and splicing techniques rely on. State explicitly why a *singly*-linked list would not support O(1) removal of an arbitrary interior node (you'd need to already have a reference to the *previous* node, which a singly-linked list's own node doesn't carry) — the concrete reason this specific design needs the doubly-linked variant.

## 19. Further Reading

- [ArrayList and LinkedList Internals](../02-java/collections/arraylist-and-linkedlist-internals.md) — `java.util.LinkedList`'s own internal structure and its performance trade-offs against `ArrayList`, a genuinely different subject from this chapter's raw pointer-manipulation techniques.
- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — the hash-map-based cycle-detection alternative referenced in Section 5, and the hash-map-clone technique this chapter's Problem 5 shares a structural kinship with.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, why a linked list can't be indexed directly the way an array can, and what a dummy head is for | [Section 3](#3-foundation-l1) |
| L2 | Name and apply the slow/fast pointer, fixed-gap, and in-place reversal techniques to a new, unfamiliar problem | [Interview Question 1](#question-1--how-would-you-detect-whether-a-linked-list-contains-a-cycle-using-o1-extra-space) |
| L3 | Derive the correctness argument for Floyd's cycle detection and the fixed-gap technique, and decompose a combination problem into its constituent sub-techniques | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real reversal/pointer-loss bug (Section 17) from its symptom alone, and design a real system component (an LRU cache, Section 18) using this chapter's pointer-discipline techniques deliberately | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
