package demo;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A catch-all advice that standardises the error response and logs nothing.
 * Written in good faith to stop stack traces leaking to clients; the side
 * effect is that it also stops them reaching the log.
 */
@RestControllerAdvice
class UnloggedAdvice {

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> handle(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }
}
