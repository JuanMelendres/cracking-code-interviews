package demo;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The centralized handler most Spring Boot codebases have. The demo exists to
 * show exactly where its reach ends: it is wired into the DispatcherServlet as
 * a HandlerExceptionResolver, so it can only see exceptions that surface inside
 * the DispatcherServlet. A filter that throws never gets here.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DemoException.class)
    ResponseEntity<Map<String, String>> handle(DemoException ex) {
        RequestTrace.record("@RestControllerAdvice handler",
                "CAUGHT " + ex.getClass().getSimpleName() + " -> mapping to 422");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> handleAny(Exception ex) {
        RequestTrace.record("@RestControllerAdvice catch-all",
                "CAUGHT " + ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", String.valueOf(ex.getMessage())));
    }
}
