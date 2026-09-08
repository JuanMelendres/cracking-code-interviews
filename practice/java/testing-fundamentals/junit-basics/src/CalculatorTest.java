import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

// A real JUnit 5 test class -- every method below is a genuine test, run for
// real by the junit-platform-console-standalone launcher (see README.md),
// not a description of what a test would look like.
class CalculatorTest {

    private Calculator calculator;

    // @BeforeEach runs before EVERY @Test method in this class, each time
    // producing a brand-new Calculator -- this is what makes tests
    // independent of each other and of the order they happen to run in.
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    void addsTwoPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    void addsNegativeNumbers() {
        assertEquals(-1, calculator.add(2, -3));
    }

    @Test
    void dividesTwoNumbers() {
        assertEquals(4, calculator.divide(8, 2));
    }

    @Test
    void divisionByZeroThrowsArithmeticException() {
        // assertThrows: the ONLY correct way to test that an exception is
        // thrown -- it runs the lambda, catches the expected exception type,
        // and fails the test if no exception (or the wrong type) was thrown.
        ArithmeticException thrown = assertThrows(
                ArithmeticException.class,
                () -> calculator.divide(10, 0)
        );
        assertEquals("Cannot divide by zero", thrown.getMessage());
    }

    // @ParameterizedTest + @ValueSource: the SAME test logic run once per
    // value, instead of writing four nearly-identical @Test methods.
    @ParameterizedTest
    @ValueSource(ints = {2, 4, 100, 0, -6})
    void isEvenReturnsTrueForEvenNumbers(int n) {
        assertTrue(calculator.isEven(n));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 99, -7})
    void isEvenReturnsFalseForOddNumbers(int n) {
        assertFalse(calculator.isEven(n));
    }

    // @CsvSource: parameterized test with multiple values per run, unpacked
    // directly into the method's parameters.
    @ParameterizedTest
    @CsvSource({
        "2, 3, 5",
        "0, 0, 0",
        "-1, 1, 0",
        "100, 200, 300"
    })
    void addProducesExpectedSum(int a, int b, int expectedSum) {
        assertEquals(expectedSum, calculator.add(a, b));
    }
}
