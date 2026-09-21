package demo;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Real background work (ReportJobService's own thread pool, a real
 * 300ms Thread.sleep, not a stubbed flag), real repeated polling with
 * real elapsed wall-clock time -- nothing here is simulated.
 */
class AsyncLongRunningOperationTest {

    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(new AsyncLongRunningOperationController()).build();

    @Test
    void submitReturns202WithLocation_andRealBackgroundJobEventuallyCompletes() throws Exception {
        long t0 = System.nanoTime();

        MockHttpServletResponse submitResponse = mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"quarterly-sales\"}"))
                .andExpect(status().isAccepted())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse();

        String location = submitResponse.getHeader("Location");
        String submitBody = submitResponse.getContentAsString();
        System.out.println("POST /reports -> 202 Accepted, Location: " + location + ", body: " + submitBody);
        assertTrue(submitBody.contains("\"status\":\"PENDING\"") || submitBody.contains("\"status\":\"RUNNING\""));

        // Immediate poll -- the real background job has not had 300ms to finish yet.
        String immediateStatus = mockMvc.perform(get(location))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Retry-After", "1"))
                .andReturn().getResponse().getContentAsString();
        System.out.println("Immediate GET " + location + " -> 202 Accepted, Retry-After: 1, body: " + immediateStatus);
        assertFalse(immediateStatus.contains("\"status\":\"COMPLETED\""));

        // Hitting the result endpoint before completion -- real 425 Too Early (RFC 8470).
        String tooEarlyBody = mockMvc.perform(get(location + "/result"))
                .andExpect(status().is(425))
                .andReturn().getResponse().getContentAsString();
        System.out.println("Immediate GET " + location + "/result -> 425 Too Early (RFC 8470), body: " + tooEarlyBody);
        assertTrue(tooEarlyBody.contains("not ready yet"));

        // Real polling loop -- waits on the real background thread, no fixed sleep-then-assume.
        String finalStatusBody = null;
        String resultLocation = null;
        int pollCount = 0;
        for (int i = 0; i < 20; i++) {
            pollCount++;
            MockHttpServletResponse pollResponse = mockMvc.perform(get(location)).andReturn().getResponse();
            if (pollResponse.getStatus() == 303) {
                finalStatusBody = pollResponse.getContentAsString();
                resultLocation = pollResponse.getHeader("Location");
                break;
            }
            Thread.sleep(50);
        }
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

        assertNotNull(finalStatusBody, "job never completed within the poll budget");
        System.out.println("Poll #" + pollCount + " GET " + location + " -> 303 See Other, real elapsed=" + elapsedMs + "ms, body: " + finalStatusBody);
        assertTrue(elapsedMs >= 300, "real background work should take at least the real 300ms sleep");

        String resultBody = mockMvc.perform(get(resultLocation))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println("GET " + resultLocation + " -> 200 OK, real result body: " + resultBody);
        assertTrue(resultBody.contains("quarterly-sales"));
    }

    @Test
    void unknownJobId_returns404() throws Exception {
        mockMvc.perform(get("/reports/999999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/reports/999999/result")).andExpect(status().isNotFound());
        System.out.println("Unknown job id 999999 -> 404 on both status and result endpoints");
    }
}
