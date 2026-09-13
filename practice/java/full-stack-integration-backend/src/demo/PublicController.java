package demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// A REST endpoint with NO CORS configuration -- deliberately, to produce a
// real, captured browser CORS failure first (this chapter's "before"
// evidence). CorsConfig.java (added and enabled for the "after" evidence)
// is what actually fixes this, by explicitly allowlisting the Next.js
// app's own origin.
@RestController
class PublicController {
    @GetMapping("/api/public/greeting")
    Map<String, String> greeting() {
        return Map.of("message", "Hello from the separate Spring backend (port 8080).");
    }
}
