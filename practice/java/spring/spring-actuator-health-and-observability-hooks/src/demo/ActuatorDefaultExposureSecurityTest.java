package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Default properties, no explicit management.endpoints.web.exposure.include --
// proves Boot's real, secure-by-default endpoint exposure posture directly.
@SpringBootTest
@AutoConfigureMockMvc
public class ActuatorDefaultExposureSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthIsExposedByDefault() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void beansEndpointIsNotExposedByDefault() throws Exception {
        mockMvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
    }

    @Test
    void customGreetingStatsEndpointIsNotExposedByDefault() throws Exception {
        mockMvc.perform(get("/actuator/greetingStats")).andExpect(status().isNotFound());
    }
}
