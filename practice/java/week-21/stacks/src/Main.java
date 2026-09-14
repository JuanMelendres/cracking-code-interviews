import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 496
        Check.isTrue(Arrays.equals(new int[]{-1,3,-1},
            Problems.nextGreaterElement(new int[]{4,1,2}, new int[]{1,3,4,2})),
            "LC496 nextGreaterElement([4,1,2],[1,3,4,2]) = [-1,3,-1]");
        Check.isTrue(Arrays.equals(new int[]{3,-1},
            Problems.nextGreaterElement(new int[]{2,4}, new int[]{1,2,3,4})),
            "LC496 nextGreaterElement([2,4],[1,2,3,4]) = [3,-1]");

        // LC 84
        Check.eq(10, Problems.largestRectangleArea(new int[]{2,1,5,6,2,3}), "LC84 largestRectangleArea([2,1,5,6,2,3]) = 10");
        Check.eq(4, Problems.largestRectangleArea(new int[]{2,4}), "LC84 largestRectangleArea([2,4]) = 4");

        // LC 150
        Check.eq(9, Problems.evalRPN(new String[]{"2","1","+","3","*"}), "LC150 evalRPN([2,1,+,3,*]) = 9");
        Check.eq(6, Problems.evalRPN(new String[]{"4","13","5","/","+"}), "LC150 evalRPN([4,13,5,/,+]) = 6");

        // LC 232
        Problems.MyQueue q = new Problems.MyQueue();
        q.push(1);
        q.push(2);
        Check.eq(1, q.peek(), "LC232 peek() after push(1),push(2) = 1");
        Check.eq(1, q.pop(), "LC232 pop() = 1 (FIFO)");
        Check.isTrue(!q.empty(), "LC232 empty() -> false (2 remains)");
        Check.eq(2, q.pop(), "LC232 pop() = 2");
        Check.isTrue(q.empty(), "LC232 empty() -> true after draining");

        // LC 503
        Check.isTrue(Arrays.equals(new int[]{2,-1,2},
            Problems.nextGreaterElements(new int[]{1,2,1})),
            "LC503 nextGreaterElements([1,2,1]) circular = [2,-1,2]");
        Check.isTrue(Arrays.equals(new int[]{2,3,4,-1,4},
            Problems.nextGreaterElements(new int[]{1,2,3,4,3})),
            "LC503 nextGreaterElements([1,2,3,4,3]) circular = [2,3,4,-1,4] (last 3 wraps past 4 first)");

        Check.summary("Week 21 — Stacks (LC 496, 84, 150, 232, 503)");
    }
}
