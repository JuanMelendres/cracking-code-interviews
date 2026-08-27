package demo;

import org.springframework.test.context.ContextConfiguration;

// Identical @ContextConfiguration to TestClassA -- same cache key, so the
// TestContext framework should reuse TestClassA's already-built context.
@ContextConfiguration(classes = CountingConfig.class)
class TestClassB {
}
