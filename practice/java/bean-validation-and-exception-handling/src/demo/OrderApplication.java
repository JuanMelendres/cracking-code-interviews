package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// T-518: real Bean Validation (@Valid, jakarta.validation constraints, a
// custom class-level cross-field constraint) plus a real global exception
// handler (@RestControllerAdvice) -- no Maven/Gradle, plain jars.
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,
            "--server.port=8081",
            "--logging.level.root=WARN"
        );
    }
}
