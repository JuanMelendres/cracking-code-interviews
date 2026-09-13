package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// show-details=always is required to see the "components" breakdown at all --
// by default Boot's /actuator/health only reports the aggregate status.
@SpringBootTest(properties = "management.endpoint.health.show-details=always")
@AutoConfigureMockMvc
public class ActuatorHealthAggregationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DownstreamDependency downstreamDependency;

    @Test
    void healthIsUpAndShowsTheCustomIndicatorWhileDownstreamIsAvailable() throws Exception {
        MvcResult result = mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        System.out.println("Real /actuator/health body (downstream UP): " + body);
        assertTrue(body.contains("\"status\":\"UP\""), "expected overall UP, got: " + body);
        assertTrue(body.contains("\"downstream\""), "expected the custom indicator's component key, got: " + body);
    }

    @Test
    void healthFlipsToDownWhenTheRealIndicatorReportsDown() throws Exception {
        downstreamDependency.setAvailable(false);
        try {
            MvcResult result = mockMvc.perform(get("/actuator/health")).andReturn();
            String body = result.getResponse().getContentAsString();
            System.out.println("Real /actuator/health body after flipping downstream DOWN: " + body);
            assertTrue(body.contains("\"status\":\"DOWN\""), "expected overall DOWN, got: " + body);
        } finally {
            downstreamDependency.setAvailable(true); // restore for anything else sharing this cached context
        }
    }
}
