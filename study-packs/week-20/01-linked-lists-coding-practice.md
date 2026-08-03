---
title: "Coding Practice — Linked Lists (T-1405)"
week: 20
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Linked Lists (T-1405)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 1/10 to 6/10 register problems.

---

## Problem 1 — LC 21 Merge Two Sorted Lists

**Pattern:** dummy-head splice, two pointers advancing whichever list has the smaller head.

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

**Retrospective:** the dummy head avoids special-casing "is this the first node" logic. Once one list is exhausted, the remainder of the other is already sorted and can be spliced on directly — no need to keep looping node by node. **Complexity:** O(n+m) time, O(1) extra space (splices existing nodes, doesn't allocate new ones).

## Problem 2 — LC 141 Linked List Cycle

**Pattern:** Floyd's slow/fast pointer (tortoise and hare).

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

**Retrospective:** if a cycle exists, the fast pointer (moving 2x speed) is guaranteed to eventually lap the slow pointer and meet it inside the cycle — it cannot "jump over" it, since the gap between them shrinks by exactly one node per iteration once both are inside the loop. **Complexity:** O(n) time, O(1) space — no hash set of visited nodes needed.

## Problem 3 — LC 19 Remove Nth Node From End

**Pattern:** two pointers with a fixed gap of `n`, single pass.

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

**Retrospective:** advancing `fast` by `n` first, then moving both pointers together, means when `fast` reaches the last node, `slow` is exactly one node before the target — the position needed to unlink it. The dummy head handles the edge case of removing the actual head node without a separate branch. **Complexity:** O(n) time, single pass, O(1) space.

## Problem 4 — LC 143 Reorder List

**Pattern:** find middle (slow/fast) → reverse second half → merge alternately.

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

**Retrospective:** this combines three techniques from problems 1-3 of this pattern group (slow/fast middle-finding, in-place reversal, merge-style splicing) into one problem — a good illustration of why pattern *combination* problems sit later in a progression than single-technique ones. **Complexity:** O(n) time, O(1) extra space (no auxiliary array/list, unlike a naive "dump to array and rebuild" approach).

## Problem 5 — LC 138 Copy List with Random Pointer

**Pattern:** hash map from original node → its clone, built in one pass, wired in a second.

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

**Retrospective:** the two-pass structure exists because a `random` pointer can point *forward* to a node not yet created during a single left-to-right pass — the map must be fully populated before any `random` pointer can be safely resolved. (A well-known O(1)-space alternative interleaves clone nodes directly into the original list rather than using a map — a good follow-up to bring up in an interview after the map-based solution.) **Complexity:** O(n) time, O(n) space for the map.

## Verification

```
$ cd practice/java/week-20/linked-lists/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
