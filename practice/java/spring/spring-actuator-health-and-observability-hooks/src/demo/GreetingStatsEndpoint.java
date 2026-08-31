package demo;

import java.util.Map;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

// A real, custom management endpoint -- not a Spring MVC controller. @Endpoint
// beans are exposed under /actuator/{id} only when explicitly included in
// management.endpoints.web.exposure.include.
@Component
@Endpoint(id = "greetingStats")
public class GreetingStatsEndpoint {

    private final GreetingCounterService greetingCounterService;

    public GreetingStatsEndpoint(GreetingCounterService greetingCounterService) {
        this.greetingCounterService = greetingCounterService;
    }

    @ReadOperation
    public Map<String, Object> stats() {
        return Map.of("realGreetingsServed", greetingCounterService.getCount());
    }
}
