package demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class GreetingCounterService {

    private final Counter counter;

    public GreetingCounterService(MeterRegistry registry) {
        this.counter = Counter.builder("greeting.requests")
                .description("Real count of greetings served")
                .register(registry);
    }

    public String greet(String name) {
        counter.increment();
        return "Hello, " + name;
    }

    public double getCount() {
        return counter.count();
    }
}
