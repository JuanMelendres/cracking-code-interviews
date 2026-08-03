public class Main {
    public static void main(String[] args) {
        // LC 21
        Problems.ListNode m1 = Problems.fromArray(new int[]{1, 2, 4});
        Problems.ListNode m2 = Problems.fromArray(new int[]{1, 3, 4});
        Check.eq("[1, 1, 2, 3, 4, 4]",
                java.util.Arrays.toString(Problems.toArray(Problems.mergeTwoLists(m1, m2))),
                "LC21 mergeTwoLists([1,2,4],[1,3,4])");

        // LC 141
        Problems.ListNode c1 = new Problems.ListNode(3);
        Problems.ListNode c2 = new Problems.ListNode(2);
        Problems.ListNode c3 = new Problems.ListNode(0);
        Problems.ListNode c4 = new Problems.ListNode(-4);
        c1.next = c2; c2.next = c3; c3.next = c4; c4.next = c2; // cycle back into c2
        Check.isTrue(Problems.hasCycle(c1), "LC141 hasCycle(3->2->0->-4->2) = true");
        Check.isTrue(!Problems.hasCycle(Problems.fromArray(new int[]{1, 2, 3})), "LC141 hasCycle([1,2,3]) = false");

        // LC 19
        Problems.ListNode r1 = Problems.fromArray(new int[]{1, 2, 3, 4, 5});
        Check.eq("[1, 2, 3, 5]",
                java.util.Arrays.toString(Problems.toArray(Problems.removeNthFromEnd(r1, 2))),
                "LC19 removeNthFromEnd([1,2,3,4,5], 2)");

        // LC 143
        Problems.ListNode ro1 = Problems.fromArray(new int[]{1, 2, 3, 4});
        Problems.reorderList(ro1);
        Check.eq("[1, 4, 2, 3]", java.util.Arrays.toString(Problems.toArray(ro1)), "LC143 reorderList([1,2,3,4])");

        Problems.ListNode ro2 = Problems.fromArray(new int[]{1, 2, 3, 4, 5});
        Problems.reorderList(ro2);
        Check.eq("[1, 5, 2, 4, 3]", java.util.Arrays.toString(Problems.toArray(ro2)), "LC143 reorderList([1,2,3,4,5])");

        // LC 138
        Problems.RandomListNode a = new Problems.RandomListNode(7);
        Problems.RandomListNode b = new Problems.RandomListNode(13);
        Problems.RandomListNode c = new Problems.RandomListNode(11);
        a.next = b; b.next = c;
        a.random = null; b.random = a; c.random = b;
        Problems.RandomListNode copyHead = Problems.copyRandomList(a);
        Check.isTrue(copyHead != a, "LC138 copyRandomList produces a distinct head node (deep copy, not same reference)");
        Check.eq(7, copyHead.val, "LC138 copy head val preserved");
        Check.eq(13, copyHead.next.val, "LC138 copy next val preserved");
        Check.isTrue(copyHead.next.random == copyHead, "LC138 copy's random pointers rewired to the CLONE graph, not the original");

        Check.summary("Week 20 — Linked Lists (LC 21, 141, 19, 143, 138)");
    }
}
