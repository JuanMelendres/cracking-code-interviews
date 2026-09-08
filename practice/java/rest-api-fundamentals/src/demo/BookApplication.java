package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// T-2205: a real, minimal REST API demonstrating resource naming, correct
// HTTP verb-to-operation mapping, and correct status codes -- separate from
// T-2203's own Spring MVC Fundamentals demo (a different, focused set of
// claims: this one is about REST conventions, not Spring's DI mechanism).
@SpringBootApplication
public class BookApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class,
            "--server.port=8081",
            "--logging.level.root=WARN"
        );
    }
}
