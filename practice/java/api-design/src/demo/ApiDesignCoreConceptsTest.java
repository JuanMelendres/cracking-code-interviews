package demo;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real Spring MVC dispatch (MockMvcBuilders.standaloneSetup) -- the
 * real HandlerMapping, real ExceptionHandlerExceptionResolver (for the
 * ProblemDetail conversion), and real Jackson serialization resolve
 * every request below. Nothing stubbed. */
class ApiDesignCoreConceptsTest {

    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(new ApiDesignCoreConceptsController()).build();

    @Test
    void hateoas_pendingOrderExposesCancelAndShip() throws Exception {
        String body = mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("PENDING order _links: " + body);

        assertTrue(body.contains("\"cancel\""));
        assertTrue(body.contains("\"ship\""));
        assertFalse(body.contains("\"return\""));
    }

    @Test
    void hateoas_deliveredOrderExposesReturnOnly() throws Exception {
        String body = mockMvc.perform(get("/orders/2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("DELIVERED order _links: " + body);

        assertTrue(body.contains("\"return\""));
        assertFalse(body.contains("\"cancel\""));
        assertFalse(body.contains("\"ship\""));
    }

    @Test
    void level0RpcContrast_sameDataOneUriOneVerb() throws Exception {
        String rpcBody = mockMvc.perform(post("/rpc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"getOrder\",\"id\":1}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String restBody = mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Level-0 RPC (POST /rpc, action field): " + rpcBody);
        System.out.println("Resource-oriented (GET /orders/1):     " + restBody);

        assertEquals(restBody, rpcBody);
    }

    @Test
    void problemDetails_notFound_returnsRfc9457Shape() throws Exception {
        String body = mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn().getResponse().getContentAsString();

        System.out.println("RFC 9457 Problem Details body: " + body);

        assertTrue(body.contains("\"type\":\"https://api.example.com/errors/order-not-found\""));
        assertTrue(body.contains("\"title\":\"Order Not Found\""));
        assertTrue(body.contains("\"status\":404"));
        assertTrue(body.contains("\"instance\":\"/orders/999\""));
        assertTrue(body.contains("\"errorCode\":\"ORDER_NOT_FOUND\""));
    }

    @Test
    void filteringAndSorting_realQueryParams() throws Exception {
        String pendingOnly = mockMvc.perform(get("/orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String sortedByAmountDesc = mockMvc.perform(get("/orders").param("sort", "amount,desc"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Filtered (status=PENDING): " + pendingOnly);
        System.out.println("Sorted (sort=amount,desc): " + sortedByAmountDesc);

        assertTrue(pendingOnly.contains("\"id\":1") && pendingOnly.contains("\"id\":3"));
        assertFalse(pendingOnly.contains("\"id\":2"));

        int posOf200 = sortedByAmountDesc.indexOf("\"amount\":200.0");
        int posOf50 = sortedByAmountDesc.indexOf("\"amount\":50.0");
        assertTrue(posOf200 >= 0 && posOf50 >= 0 && posOf200 < posOf50);
    }

    @Test
    void bulkCreate_partialSuccessPerItem() throws Exception {
        String requestBody = "[{\"amount\":10.0},{\"amount\":-5.0},{\"amount\":25.0}]";

        String responseBody = mockMvc.perform(post("/orders/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(207))
                .andReturn().getResponse().getContentAsString();

        System.out.println("Bulk create (2 valid, 1 invalid) -> real HTTP 207: " + responseBody);

        assertTrue(responseBody.contains("\"index\":0") && responseBody.contains("\"success\":true"));
        assertTrue(responseBody.contains("\"index\":1") && responseBody.contains("\"success\":false"));
        assertTrue(responseBody.contains("amount must be positive"));
        assertTrue(responseBody.contains("\"index\":2"));
    }
}
