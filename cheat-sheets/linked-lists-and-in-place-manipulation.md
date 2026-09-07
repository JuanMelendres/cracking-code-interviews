---
title: "Cheat Sheet: Linked Lists and In-Place Manipulation"
slug: linked-lists-and-in-place-manipulation
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2104
canonical: ../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md
last_updated: 2026-09-06
---

# Linked Lists and In-Place Manipulation

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md`](../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md)

## Core Mental Model

Linked-list problems are less about a specific algorithm and more about careful, bug-free pointer bookkeeping: reversing links without losing a reference to the rest of the list, finding a middle or cycle in one pass without extra memory, and knowing the small set of recurring techniques (dummy heads, slow/fast pointers, in-place reversal). Getting the pointer order wrong by one step is the single most common way to lose credit here, even with the right overall approach.

## Essential Definitions

- **Dummy head** — a placeholder node inserted before the real head, removing the need for separate "what if I'm modifying the first node" logic.
- **Slow/fast pointers (Floyd's tortoise and hare)** — one pointer moves one step, another two steps, through the same list; answers cycle detection (do they meet?) and finding the middle (where's slow when fast hits the end?).
- **Fixed-gap two pointers** — advance one pointer `n` steps ahead, then move both together; when the lead reaches the end, the trailing pointer is exactly `n` nodes from the end, found in one pass.
- **In-place reversal** — walk once, redirecting each `next` pointer backward, using three tracked references (`prev`, `cur`, `next`) to avoid losing the rest of the list.
- **Hash-map-clone technique** — needed whenever a structure has pointers that can reference forward to a node not yet created in a single pass (Copy List with Random Pointer).

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| List might have its head modified or removed | Dummy head |
| "Find the middle" or "detect a cycle" | Slow/fast pointers |
| "Remove/find the Nth node from the end" | Fixed-gap two pointers |
| Need to flip link direction | In-place reversal (`prev`/`cur`/`next`) |
| Structure has forward-referencing pointers (e.g., `random`) | Hash-map clone (two passes) |

**Complexity:** Merge Two Sorted Lists O(n+m)/O(1); Linked List Cycle O(n)/O(1); Remove Nth Node From End O(n)/O(1); Reorder List O(n)/O(1); Copy List with Random Pointer O(n)/O(n) (map-based).

## Common Pitfalls

- Losing a reference to the rest of the list during in-place reversal by overwriting a node's `next` pointer before saving it — exactly why the reversal technique tracks three references, not two.
- Forgetting a dummy head and special-casing head-modification logic separately, doubling the code paths that need to be correct.
- Attempting a single-pass clone of a structure with forward-referencing pointers — structurally impossible without either two passes or an interleaving trick.

## Interview Answer Skeleton

**30-sec:** Linked-list problems are pointer-bookkeeping problems: the recurring toolkit is a dummy head (removes head-modification special-casing), slow/fast pointers (cycle/middle detection in O(1) space), a fixed gap (Nth-from-end in one pass), and careful three-reference in-place reversal.

**2-min:** For cycle detection, walk through Floyd's gap-closing argument: once both pointers are inside a cycle, the fast pointer gains one node per iteration, so the gap must hit zero within one lap — O(1) space, versus an O(n)-space hash-set alternative for the same question. Note the explicit trade-off: hash-set cycle detection is easier to reason about but costs O(n) memory; Floyd's technique achieves the same result with O(1) space by exploiting the specific geometry of two pointers at different fixed speeds.

**Whiteboard:** Draw the list as boxes with arrows; for reversal, show `prev`, `cur`, `next` at each step and the arrow flipping backward. For cycle detection, draw the loop and mark slow/fast positions converging.

**Staff-level framing:** The core discipline here — bookkeeping through mutable, pointer-based state without losing references or corrupting a shared structure mid-mutation — transfers directly to production code working with mutable linked/graph-like structures, such as an LRU cache's internal doubly-linked list, though at production scale this compounds with genuine concurrency concerns a single-threaded interview problem never has to address.

## Production Warning Signs

- An in-place linked-list reversal function, applied to a list with more than a few nodes, produces a list that appears to only contain the last one or two original nodes — the rest seem to have "disappeared."
- Diagnose: check whether the reversal loop saves `next` (the node after `cur`) into a temporary variable *before* reassigning `cur.next = prev` — if that save happens after the reassignment, or not at all, the rest of the list becomes unreachable the instant the first `next` pointer is overwritten, explaining the "only the last node or two survive" symptom.

## Related

- syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
- syllabus/02-java/collections/arraylist-and-linkedlist-internals.md
