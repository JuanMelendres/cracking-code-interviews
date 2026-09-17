package demo;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Real Spring MVC dispatch (MockMvcBuilders.standaloneSetup -- the real
 * HandlerMapping resolves every request below, nothing is stubbed). */
class ApiVersioningStrategiesTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new VersionedUserController()).build();

    @Test
    void uriPathVersioning_realDifferentShapes() throws Exception {
        String v1 = mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String v2 = mockMvc.perform(get("/api/v2/users/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("URI path v1: " + v1);
        System.out.println("URI path v2: " + v2);

        assertTrue(v1.contains("\"name\":\"Ada Lovelace\""));
        assertTrue(v2.contains("\"firstName\":\"Ada\"") && v2.contains("\"lastName\":\"Lovelace\""));
    }

    @Test
    void headerVersioning_realDifferentShapes() throws Exception {
        String v1 = mockMvc.perform(get("/api/header/users/1").header("Api-Version", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String v2 = mockMvc.perform(get("/api/header/users/1").header("Api-Version", "2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Header v1: " + v1);
        System.out.println("Header v2: " + v2);

        assertTrue(v1.contains("\"name\":\"Ada Lovelace\""));
        assertTrue(v2.contains("\"firstName\":\"Ada\""));
    }

    @Test
    void headerVersioning_realGotcha_missingHeaderIs404NotADefaultVersion() throws Exception {
        // Real, easy-to-miss consequence of the `headers` request condition:
        // if the caller sends NO Api-Version header at all, Spring has no
        // matching handler for either V1 or V2 -- this is a real 404, not a
        // silent fallback to whichever version was registered first.
        int status = mockMvc.perform(get("/api/header/users/1"))
                .andReturn().getResponse().getStatus();

        System.out.println("No Api-Version header -> real HTTP status: " + status);
        assertTrue(status == 404, "missing version header must 404, not silently pick a default version");
    }

    @Test
    void mediaTypeVersioning_realContentNegotiation() throws Exception {
        String v1 = mockMvc.perform(get("/api/media/users/1").header("Accept", "application/vnd.myapi.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.myapi.v1+json"))
                .andReturn().getResponse().getContentAsString();
        String v2 = mockMvc.perform(get("/api/media/users/1").header("Accept", "application/vnd.myapi.v2+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.myapi.v2+json"))
                .andReturn().getResponse().getContentAsString();

        System.out.println("Media-type v1 (Content-Type echoed back): " + v1);
        System.out.println("Media-type v2 (Content-Type echoed back): " + v2);

        assertTrue(v1.contains("\"name\":\"Ada Lovelace\""));
        assertTrue(v2.contains("\"firstName\":\"Ada\""));
    }

    @Test
    void mediaTypeVersioning_realGotcha_genericAcceptStarStarPicksFirstRegisteredHandler() throws Exception {
        // Real, easy-to-miss consequence of relying on `produces` alone:
        // a generic `Accept: */*` (what curl sends by default, and what
        // many real HTTP clients send if the caller forgets to set Accept
        // at all) does NOT 406 -- Spring's real content negotiation
        // resolves it against the first-registered, most-specific
        // `produces` handler it finds, silently picking V1 rather than
        // rejecting the ambiguous request.
        String result = mockMvc.perform(get("/api/media/users/1").header("Accept", "*/*"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("Accept: */* (no explicit version) -> real response: " + result);
        assertTrue(result.contains("\"name\":\"Ada Lovelace\""),
                "a generic Accept header silently resolves to whichever version-specific handler Spring finds first");
    }
}
