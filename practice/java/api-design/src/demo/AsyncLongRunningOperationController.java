package demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The async long-running-operation pattern: 202 Accepted + Location, a
 * status-polling endpoint, and a separate result endpoint once done --
 * real evidence, closing a gap rest-api-fundamentals.md's own text
 * explicitly flagged as conceptual-only (no genuine async operation
 * existed anywhere in this domain to demonstrate it honestly).
 */
@RestController
public class AsyncLongRunningOperationController {

    private final ReportJobService jobService = new ReportJobService();

    // --- Kick off real background work; return immediately, never blocking on it ---

    @PostMapping("/reports")
    public ResponseEntity<Map<String, Object>> submitReport(@RequestBody Map<String, String> request) {
        ReportJob job = jobService.submit(request.get("name"));
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .location(URI.create("/reports/" + job.id))
                .body(statusBody(job));
    }

    // --- Poll for status: 202 + Retry-After while pending, 303 -> result once done ---

    @GetMapping("/reports/{id}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable long id) {
        ReportJob job = jobService.get(id);
        if (job == null) return ResponseEntity.notFound().build();

        if (job.status == ReportJob.Status.COMPLETED) {
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/reports/" + id + "/result"))
                    .body(statusBody(job));
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header(HttpHeaders.RETRY_AFTER, "1")
                .body(statusBody(job));
    }

    // --- The actual result, only meaningful once the job is done ---

    @GetMapping("/reports/{id}/result")
    public ResponseEntity<Map<String, Object>> getResult(@PathVariable long id) {
        ReportJob job = jobService.get(id);
        if (job == null) return ResponseEntity.notFound().build();

        if (job.status != ReportJob.Status.COMPLETED) {
            // 425 Too Early -- a deliberate, pragmatic reuse of this status code.
            // RFC 8470 itself scopes 425 to TLS early-data replay risk, not general
            // "not ready yet" semantics; used here anyway for a distinctive,
            // programmatically-branchable status rather than an overloaded 404/409.
            return ResponseEntity.status(425).body(Map.of("error", "report not ready yet, status=" + job.status));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", job.id);
        body.put("result", job.result);
        body.put("completedAt", job.completedAt.toString());
        return ResponseEntity.ok(body);
    }

    private Map<String, Object> statusBody(ReportJob job) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", job.id);
        body.put("status", job.status.name());
        body.put("submittedAt", job.submittedAt.toString());
        return body;
    }
}
