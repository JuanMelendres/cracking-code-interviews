package demo;

import org.springframework.test.context.ContextConfiguration;

// A marker class carrying real @ContextConfiguration metadata -- driven directly
// by TestContextManager in ContextCachingDemo, not executed by a JUnit runner.
@ContextConfiguration(classes = CountingConfig.class)
class TestClassA {
}
