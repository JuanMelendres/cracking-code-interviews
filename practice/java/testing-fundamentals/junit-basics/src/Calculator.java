// The class under test -- deliberately small, so the tests themselves
// (CalculatorTest.java) are the actual teaching content of this chapter.
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return numerator / denominator;
    }

    public boolean isEven(int n) {
        return n % 2 == 0;
    }
}
