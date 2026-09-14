package demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// This endpoint represents the "auth/session logic lives at the BFF, not
// here" half of F-214's topic: this backend trusts ONE thing -- a shared,
// server-to-server secret header -- and knows nothing about the browser's
// own Next.js session cookie (F-211's httpOnly JWT), which never reaches
// this service at all. The Next.js Route Handler
// (app/api/backend-proxy/route.js) is the only caller expected to hold
// this secret; a browser calling this endpoint directly, even with valid
// CORS headers, would still be rejected without it.
@RestController
class InternalController {
    private static final String EXPECTED_KEY = "f-214-demo-internal-shared-secret";

    @GetMapping("/api/internal/secret-data")
    ResponseEntity<Map<String, String>> secretData(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey) {
        if (!EXPECTED_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Missing or invalid X-Internal-Api-Key"));
        }
        return ResponseEntity.ok(Map.of(
            "secret", "Only reachable with the real shared secret -- this backend never sees the browser's own Next.js session cookie."
        ));
    }
}
