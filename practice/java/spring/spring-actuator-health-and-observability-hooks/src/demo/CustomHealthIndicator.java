package demo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// Bean name minus the "HealthIndicator" suffix becomes the component key Boot
// exposes under /actuator/health's "components" -- this bean shows up as
// "downstream".
@Component("downstreamHealthIndicator")
public class CustomHealthIndicator implements HealthIndicator {

    private final DownstreamDependency downstream;

    public CustomHealthIndicator(DownstreamDependency downstream) {
        this.downstream = downstream;
    }

    @Override
    public Health health() {
        if (downstream.isAvailable()) {
            return Health.up().withDetail("latencyMs", 12).build();
        }
        return Health.down().withDetail("reason", "downstream dependency unreachable").build();
    }
}
