package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

// Real Kubernetes readiness/liveness probes hit exactly these paths (see
// practice/k8s/week-15/deployment-with-probes-and-limits.yaml elsewhere in
// this repo). probes.enabled=true is required outside a real Kubernetes
// environment, where Boot would otherwise auto-detect and enable it itself.
@SpringBootTest(properties = "management.endpoint.health.probes.enabled=true")
@AutoConfigureMockMvc
public class ReadinessLivenessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void readinessProbeReflectsRealApplicationAvailabilityState() throws Exception {
        MvcResult beforeResult = mockMvc.perform(get("/actuator/health/readiness")).andReturn();
        String beforeBody = beforeResult.getResponse().getContentAsString();
        System.out.println("Real readiness before: " + beforeBody);
        assertTrue(beforeBody.contains("\"status\":\"UP\""));

        // The exact real signal Boot itself publishes internally when a
        // graceful-shutdown hook decides the app should stop receiving traffic.
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);

        MvcResult afterResult = mockMvc.perform(get("/actuator/health/readiness")).andReturn();
        String afterBody = afterResult.getResponse().getContentAsString();
        System.out.println("Real readiness after REFUSING_TRAFFIC: " + afterBody);
        assertTrue(afterBody.contains("\"status\":\"OUT_OF_SERVICE\""));
    }
}
