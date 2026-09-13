import java.util.HashMap;
import java.util.Map;

public class Problems {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    static ListNode fromArray(int[] vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
        while (head != null) { out.add(head.val); head = head.next; }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    // LC 21 — Merge Two Sorted Lists. Dummy-head splice, O(n+m) time, O(1) extra space.
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

    // LC 141 — Linked List Cycle. Floyd's slow/fast pointer, O(n) time, O(1) space.
    static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    // LC 19 — Remove Nth Node From End. Two pointers, gap of n, single pass, O(n) time.
    static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i < n; i++) fast = fast.next;
        while (fast.next != null) { fast = fast.next; slow = slow.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // LC 143 — Reorder List. Find middle (slow/fast), reverse second half, merge alternately.
    // O(n) time, O(1) extra space (in-place pointer surgery, no auxiliary list/array).
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

    // LC 138 — Copy List with Random Pointer. Hash-map interleave: original node -> its clone.
    // O(n) time, O(n) space (a second pass rewires next/random using the map).
    static class RandomListNode {
        int val;
        RandomListNode next, random;
        RandomListNode(int val) { this.val = val; }
    }

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
}
