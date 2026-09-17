package demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Real Spring MVC dispatch, not a description of it -- three
 * independent versioning strategies, each backed by Spring's actual
 * {@code HandlerMapping} request-condition matching:
 * <ol>
 *   <li>URI path versioning ({@code /api/v1/...} vs {@code /api/v2/...})</li>
 *   <li>Header versioning (same path, {@code Api-Version} header)</li>
 *   <li>Media-type / content-negotiation versioning (same path, {@code Accept} header)</li>
 * </ol>
 * All three simulate the exact same real breaking change: V1's single
 * {@code name} field split into V2's {@code firstName}/{@code lastName}. */
@RestController
public class VersionedUserController {

    // --- Strategy 1: URI path versioning ---

    @GetMapping("/api/v1/users/{id}")
    public UserV1Response getUserPathV1(@PathVariable long id) {
        return new UserV1Response(id, "Ada Lovelace");
    }

    @GetMapping("/api/v2/users/{id}")
    public UserV2Response getUserPathV2(@PathVariable long id) {
        return new UserV2Response(id, "Ada", "Lovelace");
    }

    // --- Strategy 2: header versioning (same URI, Spring's real `headers` request condition) ---

    @GetMapping(value = "/api/header/users/{id}", headers = "Api-Version=1")
    public UserV1Response getUserHeaderV1(@PathVariable long id) {
        return new UserV1Response(id, "Ada Lovelace");
    }

    @GetMapping(value = "/api/header/users/{id}", headers = "Api-Version=2")
    public UserV2Response getUserHeaderV2(@PathVariable long id) {
        return new UserV2Response(id, "Ada", "Lovelace");
    }

    // --- Strategy 3: media-type / content-negotiation versioning (same URI, real `produces` condition) ---

    @GetMapping(value = "/api/media/users/{id}", produces = "application/vnd.myapi.v1+json")
    public UserV1Response getUserMediaV1(@PathVariable long id) {
        return new UserV1Response(id, "Ada Lovelace");
    }

    @GetMapping(value = "/api/media/users/{id}", produces = "application/vnd.myapi.v2+json")
    public UserV2Response getUserMediaV2(@PathVariable long id) {
        return new UserV2Response(id, "Ada", "Lovelace");
    }
}
