package demo;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingConfig {

    // Incremented by the @Bean factory method below, which only runs when Spring
    // actually builds a fresh ApplicationContext for this configuration -- never
    // when a cached context is reused for a later test class with identical config.
    public static final AtomicInteger CONTEXTS_CREATED = new AtomicInteger(0);

    @Bean
    public Object contextMarker() {
        CONTEXTS_CREATED.incrementAndGet();
        return new Object();
    }
}
