package demo;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Four endpoints that all return HTTP 500. Three of them produce no useful
 * application log line at the default log level; the fourth is the control
 * case that does. The point of the demo is that the response is identical
 * enough from the outside that "we get 500s and the logs show nothing" is a
 * single symptom with several distinct causes.
 */
@RestController
class SilentFailureController {

    private static final Logger log = LoggerFactory.getLogger(SilentFailureController.class);

    /**
     * Cause 1: the exception is caught and discarded. The most common source of
     * this symptom, and the only one where the stack trace is genuinely gone
     * rather than merely unprinted.
     */
    @GetMapping("/silent/swallowed")
    ResponseEntity<Map<String, String>> swallowed() {
        try {
            throw new IllegalStateException("downstream inventory lookup failed for sku=A-1099");
        } catch (Exception ignored) {
            // No logging. The caller gets a 500 and the cause is unrecoverable
            // from this process -- it was never written anywhere.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "internal error"));
        }
    }

    /**
     * Cause 2: the exception IS logged, at a level the production configuration
     * does not emit. The information exists and is one config change away.
     */
    @GetMapping("/silent/wrong-level")
    ResponseEntity<Map<String, String>> wrongLevel() {
        Exception cause = new IllegalStateException("payment tokenisation rejected: expired merchant key");
        log.debug("payment step failed", cause);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }

    /**
     * Cause 3: the handler throws, and a catch-all @ExceptionHandler converts it
     * to a 500 without logging it. Extremely common: the advice was written to
     * standardise the response shape, and logging was assumed to happen
     * somewhere else.
     */
    @GetMapping("/silent/unlogged-advice")
    Map<String, String> unloggedAdvice() {
        throw new IllegalStateException("order total recalculation overflowed for cart=77421");
    }

    /** Control case: the same failure, logged at ERROR the way it should be. */
    @GetMapping("/visible/logged")
    ResponseEntity<Map<String, String>> visible() {
        Exception cause = new IllegalStateException("shipping quote provider returned malformed XML");
        log.error("shipping quote failed", cause);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }
}
