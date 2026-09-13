import java.util.*;

final class Problems {

    // ---- LC 496: Next Greater Element I ----
    static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreater = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int num : nums2) {
            while (!stack.isEmpty() && stack.peek() < num) {
                nextGreater.put(stack.pop(), num);
            }
            stack.push(num);
        }
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreater.getOrDefault(nums1[i], -1);
        }
        return result;
    }

    // ---- LC 84: Largest Rectangle in Histogram ----
    static int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>(); // indices, increasing height
        int maxArea = 0;
        int n = heights.length;
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] >= h) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    // ---- LC 150: Evaluate Reverse Polish Notation ----
    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        Set<String> ops = Set.of("+", "-", "*", "/");
        for (String token : tokens) {
            if (ops.contains(token)) {
                int b = stack.pop();
                int a = stack.pop();
                int result = switch (token) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> a / b;
                    default -> throw new IllegalStateException("unreachable");
                };
                stack.push(result);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    // ---- LC 232: Implement Queue using Stacks ----
    static class MyQueue {
        private final Deque<Integer> inStack = new ArrayDeque<>();
        private final Deque<Integer> outStack = new ArrayDeque<>();

        void push(int x) {
            inStack.push(x);
        }

        int pop() {
            transferIfNeeded();
            return outStack.pop();
        }

        int peek() {
            transferIfNeeded();
            return outStack.peek();
        }

        boolean empty() {
            return inStack.isEmpty() && outStack.isEmpty();
        }

        private void transferIfNeeded() {
            if (outStack.isEmpty()) {
                while (!inStack.isEmpty()) outStack.push(inStack.pop());
            }
        }
    }

    // ---- LC 503: Next Greater Element II (circular array) ----
    static int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>(); // indices
        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < num) {
                result[stack.pop()] = num;
            }
            if (i < n) stack.push(i);
        }
        return result;
    }
}
