package demo;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

// Same config again, but @DirtiesContext marks the cached context for eviction
// after this class finishes -- it still reuses the cache WHILE running, but
// poisons it for whichever class runs next with the same configuration.
@ContextConfiguration(classes = CountingConfig.class)
@DirtiesContext
class TestClassC {
}
