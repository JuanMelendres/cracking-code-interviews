import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// Real, compiling, executing demonstration of five JUnit 5 architecture
// features in one class: @ParameterizedTest (data-driven, one test method,
// many inputs), @TestFactory (dynamic tests generated at runtime, not fixed
// at compile time), a custom Extension (BeforeEachCallback, cross-cutting
// setup without inheritance), @Nested (grouping related tests, sharing
// context), and @Tag (selective execution -- "fast" vs "slow" test suites).
public class AdvancedFeaturesDemoTest {

    // --- Custom extension: times each test method, no inheritance needed ---
    static class TimingExtension implements BeforeEachCallback, AfterEachCallback {
        private long start;
        @Override public void beforeEach(ExtensionContext ctx) { start = System.nanoTime(); }
        @Override public void afterEach(ExtensionContext ctx) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.println("  [TimingExtension] " + ctx.getDisplayName() + " took " + ms + "ms");
        }
    }

    @Nested
    @ExtendWith(TimingExtension.class)
    @DisplayName("Parameterized: isPrime()")
    class ParameterizedPrimeTests {

        static boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
            return true;
        }

        @ParameterizedTest
        @CsvSource({"2,true", "4,false", "17,true", "1,false", "97,true"})
        @Tag("fast")
        void checksPrimality(int n, boolean expected) {
            assertEquals(expected, isPrime(n));
        }
    }

    @Nested
    @DisplayName("Dynamic tests: generated at runtime from a data source")
    class DynamicFactoryTests {
        @TestFactory
        @Tag("fast")
        Stream<DynamicTest> generatedFromWordList() {
            return Stream.of("racecar", "level", "hello", "noon")
                    .map(word -> dynamicTest("isPalindrome(\"" + word + "\")", () -> {
                        String reversed = new StringBuilder(word).reverse().toString();
                        boolean expected = !word.equals("hello");
                        assertEquals(expected, word.equals(reversed));
                    }));
        }
    }

    @Nested
    @Tag("slow")
    @DisplayName("Tag-filterable: a deliberately slower group")
    class SlowGroup {
        @Test
        void simulatesSlowIntegrationStyleCheck() throws InterruptedException {
            Thread.sleep(50);
            assertTrue(true);
        }
    }
}
