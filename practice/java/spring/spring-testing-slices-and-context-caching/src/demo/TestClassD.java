package demo;

import org.springframework.test.context.ContextConfiguration;

// Same config a fourth time, run after TestClassC dirtied the cache -- should
// force a real, fresh ApplicationContext rebuild instead of a cache hit.
@ContextConfiguration(classes = CountingConfig.class)
class TestClassD {
}
