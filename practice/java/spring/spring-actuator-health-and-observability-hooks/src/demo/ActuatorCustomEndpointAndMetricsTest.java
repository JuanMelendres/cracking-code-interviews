package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "management.endpoints.web.exposure.include=health,greetingStats,metrics")
@AutoConfigureMockMvc
public class ActuatorCustomEndpointAndMetricsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GreetingCounterService greetingCounterService;

    @Test
    void customEndpointAndMetricsEndpointReflectRealCounterActivity() throws Exception {
        // Computed from a live "before" reading rather than assuming 0, so this
        // test stays correct regardless of what else ran earlier in a shared,
        // cached context.
        double before = greetingCounterService.getCount();

        greetingCounterService.greet("Ada");
        greetingCounterService.greet("Grace");

        double after = greetingCounterService.getCount();
        assertEquals(before + 2, after, 0.0001);

        MvcResult statsResult = mockMvc.perform(get("/actuator/greetingStats"))
                .andExpect(status().isOk())
                .andReturn();
        String statsBody = statsResult.getResponse().getContentAsString();
        System.out.println("Real /actuator/greetingStats body: " + statsBody);
        assertTrue(statsBody.contains("\"realGreetingsServed\":" + after));

        MvcResult metricsResult = mockMvc.perform(get("/actuator/metrics/greeting.requests"))
                .andExpect(status().isOk())
                .andReturn();
        String metricsBody = metricsResult.getResponse().getContentAsString();
        System.out.println("Real /actuator/metrics/greeting.requests body: " + metricsBody);
        assertTrue(metricsBody.contains("\"name\":\"greeting.requests\""));
    }
}
