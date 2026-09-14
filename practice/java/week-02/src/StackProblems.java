import java.util.ArrayDeque;
import java.util.Deque;

final class StackProblems {

    // LC 20 — Valid Parentheses. Push opens; on a close, top must match.
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) return false;
            }
        }
        return stack.isEmpty();
    }

    // LC 739 — Daily Temperatures. Monotonic (decreasing) stack of INDICES, not values.
    // This is the corrected version — the audited Notion guide's version pushed values
    // and drew an indices-based diagram, contradicting its own code (defect #6).
    static int[] dailyTemperatures(int[] temps) {
        int n = temps.length;
        int[] result = new int[n];
        Deque<Integer> indexStack = new ArrayDeque<>(); // holds INDICES
        for (int i = 0; i < n; i++) {
            while (!indexStack.isEmpty() && temps[indexStack.peek()] < temps[i]) {
                int prevIndex = indexStack.pop();
                result[prevIndex] = i - prevIndex;
            }
            indexStack.push(i);
        }
        return result;
    }
}

// LC 155 — Min Stack. Two parallel stacks: values, and running minimum at each depth.
final class MinStack {
    private final Deque<Integer> values = new ArrayDeque<>();
    private final Deque<Integer> minima = new ArrayDeque<>();

    void push(int val) {
        values.push(val);
        minima.push(minima.isEmpty() ? val : Math.min(val, minima.peek()));
    }

    void pop() {
        values.pop();
        minima.pop();
    }

    int top() {
        return values.peek();
    }

    int getMin() {
        return minima.peek();
    }
}
